# 연결 테스트 스크립트 - 애플리케이션이 정상 작동하는지 확인

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "애플리케이션 연결 테스트" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# 테스트 1: localhost 연결
Write-Host "[1/3] localhost:8080 연결 테스트..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -UseBasicParsing -TimeoutSec 5 -ErrorAction Stop
    Write-Host "✓ localhost:8080 연결 성공" -ForegroundColor Green
    Write-Host "  HTTP 상태 코드: $($response.StatusCode)" -ForegroundColor White
} catch {
    Write-Host "✗ localhost:8080 연결 실패" -ForegroundColor Red
    Write-Host "  오류: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    Write-Host "해결 방법:" -ForegroundColor Yellow
    Write-Host "  1. Spring Boot 애플리케이션을 실행하세요" -ForegroundColor White
    Write-Host "  2. 포트 8080이 다른 프로세스에 의해 사용되지 않는지 확인하세요" -ForegroundColor White
}
Write-Host ""

# 테스트 2: Docker에서 host.docker.internal 연결
Write-Host "[2/3] Docker에서 host.docker.internal 연결 테스트..." -ForegroundColor Yellow
try {
    $dockerResult = docker run --rm curlimages/curl:latest curl -s -o /dev/null -w "%{http_code}" http://host.docker.internal:8080/actuator/health 2>&1
    
    if ($LASTEXITCODE -eq 0 -and $dockerResult -match "^\d{3}$") {
        Write-Host "✓ Docker에서 host.docker.internal 연결 성공" -ForegroundColor Green
        Write-Host "  HTTP 상태 코드: $dockerResult" -ForegroundColor White
    } else {
        throw "연결 실패"
    }
} catch {
    Write-Host "✗ Docker에서 host.docker.internal 연결 실패" -ForegroundColor Red
    Write-Host ""
    Write-Host "해결 방법:" -ForegroundColor Yellow
    Write-Host "  1. Docker Desktop이 실행 중인지 확인" -ForegroundColor White
    Write-Host "  2. Docker Desktop 버전 업데이트 (최소 18.03 이상)" -ForegroundColor White
    Write-Host "  3. Settings → General → 'Use the WSL 2 based engine' 활성화" -ForegroundColor White
}
Write-Host ""

# 테스트 3: API 응답 확인
Write-Host "[3/3] 애플리케이션 API 상태 확인..." -ForegroundColor Yellow
try {
    $healthResponse = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -UseBasicParsing -TimeoutSec 5 -ErrorAction Stop
    Write-Host "✓ API 정상 작동" -ForegroundColor Green
    Write-Host "  상태: $($healthResponse.status)" -ForegroundColor White
    
    # JSON 출력
    Write-Host ""
    Write-Host "API 응답:" -ForegroundColor Cyan
    $healthResponse | ConvertTo-Json -Depth 3 | Write-Host -ForegroundColor White
} catch {
    Write-Host "✗ API 응답 확인 실패" -ForegroundColor Red
}
Write-Host ""

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "테스트 완료" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "모든 연결이 성공하면 부하 테스트를 실행할 수 있습니다:" -ForegroundColor Green
Write-Host "  .\run-load-test.ps1" -ForegroundColor White
Write-Host ""

Write-Host "Press any key to continue..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
