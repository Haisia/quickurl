# 빠른 시작 가이드

## 1단계: 애플리케이션 실행 확인

Spring Boot 애플리케이션이 실행되고 있는지 확인:

```cmd
curl http://localhost:8080/actuator/health
```

정상 응답: `{"status":"UP"}`

## 2단계: 연결 테스트 (권장)

```cmd
cd k6
test-connection.bat
```

또는 PowerShell:

```powershell
cd k6
.\test-connection.ps1
```

모든 테스트가 통과하면 다음 단계로 진행하세요.

## 3단계: 부하 테스트 실행

### CMD에서:
```cmd
cd k6
run-load-test.bat
```

### PowerShell에서:
```powershell
cd k6
.\run-load-test.ps1
```

## 4단계: 결과 확인

테스트가 완료되면 다음이 자동으로 생성됩니다:

### 1. JSON 결과 파일
- `results/load-test-result-[timestamp].json` - 상세 결과
- `results/load-test-summary-[timestamp].json` - 요약 결과

### 2. HTML 리포트 (자동 생성)
- `results/load-test-report-[timestamp].html` - 시각화된 리포트
- 테스트 완료 후 브라우저에서 열 것인지 물어봅니다

### 3. 콘솔 출력
PowerShell 스크립트는 다음을 자동으로 표시합니다:
- 평균/P90/P95/최소/최대 응답 시간
- 생성된 URL 수
- 총 요청 수 및 초당 요청 수
- 실패율

## HTML 리포트 재생성

이미 생성된 JSON 결과를 다시 HTML로 변환하려면:

```cmd
cd k6
generate-html-report.bat
```

또는 PowerShell:

```powershell
cd k6
.\generate-html-report.ps1
```

## 예상 실행 시간

- 연결 테스트: 약 10초
- 부하 테스트: 정확히 5분
- 총 소요 시간: 약 5분 10초

## 트러블슈팅

### "connection refused" 오류
→ 애플리케이션이 실행 중이 아닙니다. 1단계로 돌아가세요.

### "host.docker.internal" 연결 오류
→ Docker Desktop을 업데이트하거나 WSL 2 엔진을 활성화하세요.

### Docker가 없음
→ [Docker Desktop](https://www.docker.com/products/docker-desktop) 설치 필요

## 참고사항

- **테스트 계정**: `test@test.com` / `password1234`
- **테스트 기간**: 5분 고정
- **동시 사용자**: 1명 (기본값)
- **측정 항목**: 평균/P90/P95 응답시간, 생성된 URL 수

더 자세한 정보는 [README.md](README.md)를 참고하세요.
