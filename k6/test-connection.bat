@echo off
REM 연결 테스트 스크립트 - 애플리케이션이 정상 작동하는지 확인

echo ============================================
echo 애플리케이션 연결 테스트
echo ============================================
echo.

echo [1/3] localhost:8080 연결 테스트...
curl -s -o nul -w "HTTP 상태 코드: %%{http_code}\n" http://localhost:8080/actuator/health
if %ERRORLEVEL% NEQ 0 (
    echo ✗ localhost:8080 연결 실패
    echo.
) else (
    echo ✓ localhost:8080 연결 성공
    echo.
)

echo [2/3] Docker에서 host.docker.internal 연결 테스트...
docker run --rm curlimages/curl:latest curl -s -o /dev/null -w "HTTP 상태 코드: %%{http_code}\n" http://host.docker.internal:8080/actuator/health
if %ERRORLEVEL% NEQ 0 (
    echo ✗ Docker에서 host.docker.internal 연결 실패
    echo.
    echo 해결 방법:
    echo 1. Docker Desktop이 실행 중인지 확인
    echo 2. Docker Desktop 버전 업데이트 (18.03 이상)
    echo 3. WSL 2 엔진 사용 설정 확인
    echo.
) else (
    echo ✓ Docker에서 host.docker.internal 연결 성공
    echo.
)

echo [3/3] 애플리케이션 API 테스트...
curl -s http://localhost:8080/actuator/health
echo.
echo.

echo ============================================
echo 테스트 완료
echo ============================================
echo.
echo 모든 연결이 성공하면 부하 테스트를 실행할 수 있습니다:
echo   run-load-test.bat
echo.

pause
