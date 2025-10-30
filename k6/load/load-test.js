import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Trend } from 'k6/metrics';
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";
import { textSummary } from "https://jslib.k6.io/k6-summary/0.0.1/index.js";

// 커스텀 메트릭 정의
const urlCreationCounter = new Counter('url_creation_count');
const urlCreationTrend = new Trend('url_creation_duration');

// 테스트 설정
export const options = {
  duration: '5m',  // 5분 동안 실행
  vus: 1,          // 단일 가상 유저
  thresholds: {
    'http_req_duration': ['p(90)<500', 'p(95)<1000'], // P90, P95 임계값
  },
};

// 고유한 랜덤 문자열 생성 함수
function generateUniqueId() {
  const timestamp = Date.now();
  const random = Math.random().toString(36).substring(2, 15);
  return `${timestamp}-${random}`;
}

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
  console.log('=== 테스트 셋업 완료 ===\n');

  return {
    baseUrl: baseUrl,
    accessToken: accessToken
  };
}

export default function (data) {
  const {baseUrl, accessToken} = data;

  // 3. URL 생성 (캐싱 방지를 위한 고유 randomDummy)
  const randomDummy = generateUniqueId();
  const urlPayload = JSON.stringify({
    originalUrl: `http://host.docker.internal:8080/test/redirect-target/${randomDummy}`
  });

  const startTime = new Date();

  const createUrlRes = http.post(`${baseUrl}/api/v1/url/shorten`, urlPayload, {
    headers: {
      'Content-Type': 'application/json',
      'Cookie': `accessToken=${accessToken}`
    },
  });

  const endTime = new Date();
  const duration = endTime - startTime;

  // 응답 검증
  const success = check(createUrlRes, {
    'URL 생성 성공': (r) => r.status === 200 || r.status === 201,
    '응답 본문 존재': (r) => r.body && r.body.length > 0,
  });

  if (success) {
    urlCreationCounter.add(1);
    urlCreationTrend.add(duration);
  } else {
    console.error(`URL 생성 실패: ${createUrlRes.status} - ${createUrlRes.body}`);
  }
}

export function handleSummary(data) {
  const urlCreationCount = data.metrics.url_creation_count?.values?.count || 0;
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
  console.log('부하 테스트 결과 리포트');
  console.log('='.repeat(60));
  console.log(`테스트 기간: ${testDuration.toFixed(1)}초`);
  console.log(`생성된 URL 수: ${urlCreationCount}개`);
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
    [`/output/summary_${timestamp}.html`]: htmlReport(data),
    [`/output/summary_${timestamp}.json`]: JSON.stringify(data, null, 2),
    'stdout': textSummary(data, { indent: " ", enableColors: true }),
  };
}
