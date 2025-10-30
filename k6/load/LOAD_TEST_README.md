# K6 부하 테스트 가이드

## 사전 준비

### 1. K6 설치

#### macOS
```bash
brew install k6
```

#### Linux
```bash
sudo gpg -k
sudo gpg --no-default-keyring --keyring /usr/share/keyrings/k6-archive-keyring.gpg --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
echo "deb [signed-by=/usr/share/keyrings/k6-archive-keyring.gpg] https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
sudo apt-get update
sudo apt-get install k6
```

#### Windows
```powershell
choco install k6
```

또는 [공식 다운로드 페이지](https://k6.io/docs/getting-started/installation/)에서 설치

### 2. 애플리케이션 실행

테스트 전에 Spring Boot 애플리케이션이 실행 중이어야 합니다:

```bash
./gradlew bootRun
```

또는 Docker Compose를 사용하는 경우:

```bash
docker-compose up
```

## 테스트 실행

### 기본 실행
```bash
k6 run load-test.js
```

### 상세 출력
```bash
k6 run --summary-export=summary.json load-test.js
```

### HTML 리포트 생성 (확장 기능 필요)
```bash
k6 run --out json=results.json load-test.js
# 그 후 별도 도구로 HTML 변환
```

## 테스트 시나리오

### 1. 회원가입 (Setup 단계)
- **URL**: `POST http://localhost:8080/api/v1/auth/register`
- **Body**: 
  ```json
  {
    "email": "test@test.com",
    "password": "password1234"
  }
  ```
- **참고**: 이미 존재하는 경우 400 에러 무시

### 2. 로그인 (Setup 단계)
- **URL**: `POST http://localhost:8080/api/v1/login`
- **Body**: 
  ```json
  {
    "email": "test@test.com",
    "password": "password1234"
  }
  ```
- **응답**: `Set-Cookie` 헤더에서 `accessToken` 추출

### 3. URL 단축 (메인 테스트)
- **URL**: `POST http://localhost:8080/api/v1/url/shorten`
- **Body**: 
  ```json
  {
    "originalUrl": "http://localhost:8080/test/redirect-target/{uniqueId}"
  }
  ```
- **헤더**: `Cookie: accessToken={token}`
- **실행**: 5분 동안 반복

## 측정 지표

테스트 완료 후 다음 지표가 출력됩니다:

1. **생성된 URL 수**: 5분 동안 성공적으로 생성된 총 URL 개수
2. **평균 응답 시간**: 모든 요청의 평균 응답 시간
3. **P(90) 응답 시간**: 90%의 요청이 이 시간 이내에 완료
4. **P(95) 응답 시간**: 95%의 요청이 이 시간 이내에 완료

## 테스트 설정 변경

`load-test.js` 파일의 `options` 섹션에서 다음을 조정할 수 있습니다:

```javascript
export const options = {
    duration: '5m',  // 테스트 지속 시간
    vus: 1,          // 동시 가상 사용자 수
    thresholds: {
        'http_req_duration': ['p(90)<500', 'p(95)<1000'], // 성능 임계값
    },
};
```

### 동시 사용자 증가 예시
```javascript
export const options = {
    stages: [
        { duration: '1m', target: 10 },   // 1분 동안 10명으로 증가
        { duration: '3m', target: 10 },   // 3분 동안 10명 유지
        { duration: '1m', target: 0 },    // 1분 동안 0명으로 감소
    ],
};
```

## 트러블슈팅

### 연결 실패
- 애플리케이션이 `localhost:8080`에서 실행 중인지 확인
- 방화벽 설정 확인

### 인증 실패
- 회원가입 API가 정상 작동하는지 확인
- 로그인 API가 `Set-Cookie` 헤더로 토큰을 반환하는지 확인

### 캐싱 문제
- 스크립트는 각 요청마다 고유한 `randomDummy` 값을 생성하여 캐싱을 방지합니다
- 타임스탬프와 랜덤 문자열을 조합하여 고유성을 보장합니다

## 결과 예시

```
=============================================================
부하 테스트 결과 리포트
=============================================================
테스트 기간: 5분
생성된 URL 수: 1234개
평균 응답 시간: 45.67ms
P(90) 응답 시간: 89.23ms
P(95) 응답 시간: 112.45ms
=============================================================
```
