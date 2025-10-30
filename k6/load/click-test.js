import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Trend } from 'k6/metrics';
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";
import { textSummary } from "https://jslib.k6.io/k6-summary/0.0.1/index.js";

// 커스텀 메트릭 정의
const clickSuccessCounter = new Counter('click_success_count');
const clickDuration = new Trend('click_duration');

// 테스트 설정
export const options = {
  duration: '5m',  // 5분 동안 실행
  vus: 1,          // 단일 가상 유저
  thresholds: {
    'http_req_duration': ['p(90)<500', 'p(95)<1000'], // P90, P95 임계값
  },
};

// 쿠키에서 accessToken 파싱 함수
function extractAccessToken(setCookieHeader) {
  if (!setCookieHeader) {
    return null;
  }

  // Set-Cookie 헤더가 배열인 경우 처리
  const cookies = Array.isArray(setCookieHeader) ? setCookieHeader : [setCookieHeader];

  for (const cookie of cookies) {
    if (cookie.includes('accessToken=')) {
      const match = cookie.match(/accessToken=([^;]+)/);
      if (match) {
        return match[1];
      }
    }
  }
  return null;
}

export function setup() {
  // Windows Docker에서는 host.docker.internal 사용
  const baseUrl = 'http://host.docker.internal:8080';

  console.log('=== 테스트 셋업 시작 ===');

  // 1. 회원가입 시도
  console.log('1. 회원가입 시도...');
  const registerPayload = JSON.stringify({
    email: 'test@test.com',
    password: 'password1234'
  });

  const registerRes = http.post(`${baseUrl}/api/v1/auth/register`, registerPayload, {
    headers: {'Content-Type': 'application/json'},
  });

  if (registerRes.status === 200 || registerRes.status === 201) {
    console.log('✓ 회원가입 성공');
  } else if (registerRes.status === 400) {
    console.log('⚠ 회원가입 실패 (이미 존재하는 계정 - 무시)');
  } else {
    console.log(`✗ 회원가입 실패: ${registerRes.status} - ${registerRes.body}`);
  }

  // 2. 로그인 및 accessToken 획득
  console.log('2. 로그인 시도...');
  const loginPayload = JSON.stringify({
    email: 'test@test.com',
    password: 'password1234'
  });

  const loginRes = http.post(`${baseUrl}/api/v1/auth/login`, loginPayload, {
    headers: {'Content-Type': 'application/json'},
  });

  check(loginRes, {
    '로그인 성공': (r) => r.status === 200 || r.status === 201,
  });

  if (loginRes.status !== 200 && loginRes.status !== 201) {
    console.error(`✗ 로그인 실패: ${loginRes.status} - ${loginRes.body}`);
    throw new Error('로그인 실패');
  }

  // Set-Cookie 헤더에서 accessToken 추출
  const setCookieHeader = loginRes.headers['Set-Cookie'];
  const accessToken = extractAccessToken(setCookieHeader);

  if (!accessToken) {
    console.error('✗ accessToken을 찾을 수 없습니다');
    console.error('Set-Cookie 헤더:', setCookieHeader);
    throw new Error('accessToken 추출 실패');
  }

  console.log('✓ 로그인 성공 및 accessToken 획득');

  // 3. URL 1개 생성
  console.log('3. URL 생성 시도...');
  const urlPayload = JSON.stringify({
    originalUrl: 'http://host.docker.internal:8080/test/redirect-target/1'
  });

  const createUrlRes = http.post(`${baseUrl}/api/v1/url/shorten`, urlPayload, {
    headers: {
      'Content-Type': 'application/json',
      'Cookie': `accessToken=${accessToken}`
    },
  });

  if (createUrlRes.status !== 200 && createUrlRes.status !== 201) {
    console.error(`✗ URL 생성 실패: ${createUrlRes.status} - ${createUrlRes.body}`);
    throw new Error('URL 생성 실패');
  }

  // 응답에서 short_key 추출
  let shortKey = null;
  try {
    const responseBody = JSON.parse(createUrlRes.body);
    shortKey = responseBody.short_key || responseBody.shortKey || responseBody.data?.short_key || responseBody.data?.shortKey;
    
    if (!shortKey) {
      console.error('응답 본문:', createUrlRes.body);
      throw new Error('short_key를 찾을 수 없습니다');
    }
    
    console.log(`✓ URL 생성 성공 - short_key: ${shortKey}`);
  } catch (e) {
    console.error(`✗ 응답 파싱 실패: ${e.message}`);
    console.error('응답 본문:', createUrlRes.body);
    throw new Error('short_key 추출 실패');
  }

  console.log('=== 테스트 셋업 완료 ===\n');

  return {
    baseUrl: baseUrl,
    shortKey: shortKey
  };
}

export default function (data) {
  const {baseUrl, shortKey} = data;

  // 4. URL 클릭 (리다이렉트)
  const startTime = new Date();

  const clickRes = http.get(`${baseUrl}/${shortKey}`, {
    redirects: 5,  // 리다이렉트 최대 5회까지 따라감
    tags: { name: 'URLClick' },
  });

  const endTime = new Date();
  const duration = endTime - startTime;

  // 응답 검증
  const success = check(clickRes, {
    'URL 클릭 성공': (r) => r.status === 200 || r.status === 302 || r.status === 301,
    '리다이렉트 성공': (r) => r.url.includes('/test/redirect-target/1') || r.status === 200,
  });

  if (success) {
    clickSuccessCounter.add(1);
    clickDuration.add(duration);
  } else {
    console.error(`URL 클릭 실패: ${clickRes.status} - ${clickRes.url}`);
  }
}

export function handleSummary(data) {
  const clickSuccessCount = data.metrics.click_success_count?.values?.count || 0;
  const avgDuration = data.metrics.http_req_duration?.values?.avg || 0;
  const p90Duration = data.metrics.http_req_duration?.values['p(90)'] || 0;
  const p95Duration = data.metrics.http_req_duration?.values['p(95)'] || 0;
  const minDuration = data.metrics.http_req_duration?.values?.min || 0;
  const maxDuration = data.metrics.http_req_duration?.values?.max || 0;
  const totalReqs = data.metrics.http_reqs?.values?.count || 0;
  const testDuration = data.state.testRunDurationMs / 1000;
  const rps = totalReqs / testDuration;

  console.log('\n');
  console.log('='.repeat(60));
  console.log('URL 클릭 부하 테스트 결과 리포트');
  console.log('='.repeat(60));
  console.log(`테스트 기간: ${testDuration.toFixed(1)}초`);
  console.log(`클릭 성공 횟수: ${clickSuccessCount}개`);
  console.log(`총 요청 수: ${totalReqs}개`);
  console.log(`초당 요청 수: ${rps.toFixed(2)} req/s`);
  console.log(`평균 응답 시간: ${avgDuration.toFixed(2)}ms`);
  console.log(`최소 응답 시간: ${minDuration.toFixed(2)}ms`);
  console.log(`최대 응답 시간: ${maxDuration.toFixed(2)}ms`);
  console.log(`P(90) 응답 시간: ${p90Duration.toFixed(2)}ms`);
  console.log(`P(95) 응답 시간: ${p95Duration.toFixed(2)}ms`);
  console.log('='.repeat(60));
  console.log('\n');

  const timestamp = new Date().toISOString().replace(/[:.]/g, '-').split('.')[0];
  
  return {
    [`/output/click-test_${timestamp}.html`]: htmlReport(data),
    [`/output/click-test_${timestamp}.json`]: JSON.stringify(data, null, 2),
    'stdout': textSummary(data, { indent: " ", enableColors: true }),
  };
}
