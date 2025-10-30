# K6 부하 테스트 가이드

## 📁 디렉토리 구조

```
k6/
├── load/
│   └── load-test.js          # K6 테스트 스크립트 (htmlReport 내장)
├── output/                    # 테스트 결과 자동 저장 (HTML + JSON)
├── run-load-test.bat          # Windows CMD 실행 스크립트
├── run-load-test.ps1          # Windows PowerShell 실행 스크립트
└── README.md                  # 이 파일
```

## 🚀 빠른 시작

### 1. 애플리케이션 실행 확인
```cmd
curl http://localhost:8080/actuator/health
```

### 2. 부하 테스트 실행

#### PowerShell (권장)
```powershell
cd k6
.\run-load-test.ps1
```

#### CMD
```cmd
cd k6
run-load-test.bat
```

## 📊 테스트 시나리오

### 테스트 1: URL 생성 테스트 (load-test.js)

#### 자동 실행 단계
1. **회원가입** - `POST /api/v1/auth/register`
2. **로그인** - `POST /api/v1/auth/login` (accessToken 획득)
3. **URL 단축 반복** - `POST /api/v1/url/shorten` (5분간)

#### 측정 지표
- ✅ 생성된 URL 수
- ⏱️ 평균/P90/P95 응답 시간
- 📊 초당 요청 수 (RPS)
- ❌ 실패율

#### 실행 방법
```powershell
.\run-load-test.ps1
```

### 테스트 2: URL 클릭 테스트 (click-test.js)

#### 자동 실행 단계
1. **회원가입** - `POST /api/v1/auth/register`
2. **로그인** - `POST /api/v1/auth/login` (accessToken 획득)
3. **URL 1개 생성** - `POST /api/v1/url/shorten` (short_key 획득)
4. **URL 클릭 반복** - `GET /{short_key}` (5분간 리다이렉트 테스트)

#### 측정 지표
- ✅ 클릭 성공 횟수
- ⏱️ 평균/P90/P95 응답 시간
- 📊 초당 요청 수 (RPS)
- ❌ 실패율

#### 실행 방법
```powershell
.\run-click-test.ps1
```

#### 특징
- Rate Limit 비활성화 상태에서 테스트
- 리다이렉트 성능 측정 (최대 5회 리다이렉트)
- 동일 URL에 대한 반복 클릭 처리량 측정

## 📈 결과 확인

### 자동 생성되는 파일
테스트 완료 후 `output/` 디렉토리에 자동 생성:

1. **HTML 리포트** (`summary_YYYY-MM-DDTHH-MM-SS.html`)
   - 시각화된 그래프와 차트
   - 응답 시간 분포
   - 처리량(Throughput) 차트
   - 임계값 달성 여부

2. **JSON 데이터** (`summary_YYYY-MM-DDTHH-MM-SS.json`)
   - 상세한 메트릭 데이터
   - 프로그래밍 방식 분석 가능

### HTML 리포트 예시
![K6 Report](https://raw.githubusercontent.com/benc-uk/k6-reporter/main/docs/screenshot.png)

## ⚙️ 설정 변경

`load/load-test.js` 파일을 수정하여 테스트 설정 변경:

### 테스트 기간 및 사용자 수
```javascript
export const options = {
  duration: '5m',  // 5분 → 원하는 시간으로 변경
  vus: 1,          // 동시 사용자 1명 → 원하는 숫자로 변경
};
```

### 단계별 부하 증가
```javascript
export const options = {
  stages: [
    { duration: '1m', target: 10 },   // 1분 동안 10명으로 증가
    { duration: '3m', target: 10 },   // 3분 동안 10명 유지
    { duration: '1m', target: 0 },    // 1분 동안 0명으로 감소
  ],
};
```

### 임계값 조정
```javascript
export const options = {
  thresholds: {
    'http_req_duration': ['p(90)<500', 'p(95)<1000'], // ms 단위
  },
};
```

## 🐳 Docker 명령어 직접 실행

스크립트 없이 직접 실행하려면:

```cmd
docker run --rm -i ^
  -v "%cd%\load":/scripts ^
  -v "%cd%\output":/output ^
  grafana/k6 run /scripts/load-test.js
```

PowerShell:
```powershell
docker run --rm -i `
  -v "${PWD}\load:/scripts" `
  -v "${PWD}\output:/output" `
  grafana/k6 run /scripts/load-test.js
```

## 🔍 트러블슈팅

### 연결 실패 (connection refused)
**증상**: `dial tcp 127.0.0.1:8080: connect: connection refused`

**해결**:
1. 애플리케이션이 실행 중인지 확인
2. Docker Desktop이 실행 중인지 확인
3. Windows 방화벽 설정 확인

### HTML 파일이 생성되지 않음
**원인**: 스크립트의 `handleSummary` 함수 오류

**해결**:
1. `load/load-test.js` 파일이 최신 버전인지 확인
2. Docker 볼륨 마운트가 정상인지 확인:
   ```cmd
   docker run --rm -v "%cd%\load":/scripts alpine ls /scripts
   ```

### host.docker.internal 연결 오류
**원인**: 구 버전 Docker Desktop

**해결**:
- Docker Desktop 업데이트 (최소 18.03 이상)
- WSL 2 엔진 활성화: Settings → General → "Use the WSL 2 based engine"

## 📖 참고 자료

### K6 공식 문서
- [K6 Documentation](https://k6.io/docs/)
- [K6 Metrics](https://k6.io/docs/using-k6/metrics/)
- [K6 Thresholds](https://k6.io/docs/using-k6/thresholds/)

### HTML 리포터
- [k6-reporter GitHub](https://github.com/benc-uk/k6-reporter)

## 💡 팁

### 1. 테스트 전 워밍업
큰 규모의 테스트 전에는 짧은 워밍업 테스트 실행 권장:
```javascript
export const options = {
  stages: [
    { duration: '30s', target: 5 },   // 워밍업
    { duration: '5m', target: 50 },   // 실제 테스트
  ],
};
```

### 2. 결과 비교
여러 테스트 결과를 비교하려면 output 디렉토리의 JSON 파일 활용

### 3. CI/CD 통합
GitHub Actions나 Jenkins에서 실행 가능:
```yaml
- name: Run K6 Load Test
  run: |
    docker run --rm -i \
      -v ${{ github.workspace }}/k6/load:/scripts \
      -v ${{ github.workspace }}/k6/output:/output \
      grafana/k6 run /scripts/load-test.js
```

## 🎯 성능 목표

기본 설정 기준:
- ✅ P95 응답 시간 < 1000ms
- ✅ P90 응답 시간 < 500ms
- ✅ 실패율 < 1%
- ✅ 5분간 안정적 처리

목표 미달성 시 애플리케이션 최적화 필요!
