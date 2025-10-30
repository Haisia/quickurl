@echo off
REM K6 테스트 결과를 HTML로 변환하는 스크립트

echo ============================================
echo K6 HTML 리포트 생성
echo ============================================
echo.

REM JSON 파일 목록 표시
echo results 디렉토리의 JSON 파일:
echo.
dir /b /o-d ".\results\load-test-result-*.json" 2>nul

if %ERRORLEVEL% NEQ 0 (
    echo ❌ results 디렉토리에 JSON 파일이 없습니다.
    echo 먼저 부하 테스트를 실행하세요: run-load-test.bat
    echo.
    pause
    exit /b 1
)

echo.
echo 가장 최근 파일을 사용합니다...
echo.

REM 가장 최근 JSON 파일 찾기
for /f "delims=" %%f in ('dir /b /o-d ".\results\load-test-result-*.json" 2^>nul') do (
    set "LATEST_JSON=%%f"
    goto :found
)

:found
if "%LATEST_JSON%"=="" (
    echo ❌ JSON 파일을 찾을 수 없습니다.
    pause
    exit /b 1
)

echo 선택된 파일: %LATEST_JSON%

REM 타임스탬프 추출
for /f "tokens=3 delims=-." %%a in ("%LATEST_JSON%") do set TIMESTAMP=%%a
set HTML_FILE=load-test-report-%TIMESTAMP%.html

echo HTML 파일: %HTML_FILE%
echo.
echo HTML 리포트 생성 중...

REM Docker를 사용하여 HTML 생성
docker run --rm -v "%cd%\results":/results ghcr.io/benc-uk/k6-reporter:latest /results/%LATEST_JSON% /results/%HTML_FILE% >nul 2>&1

if exist ".\results\%HTML_FILE%" (
    echo.
    echo ============================================
    echo ✓ HTML 리포트 생성 완료!
    echo ============================================
    echo 파일: results\%HTML_FILE%
    echo.
    
    set /p OPEN_BROWSER="브라우저에서 열시겠습니까? (Y/N): "
    if /i "%OPEN_BROWSER%"=="Y" (
        start "" ".\results\%HTML_FILE%"
        echo ✓ 브라우저에서 리포트를 열었습니다.
    )
) else (
    echo.
    echo ============================================
    echo ❌ HTML 리포트 생성 실패
    echo ============================================
    echo.
    echo 대체 방법:
    echo 1. npm install -g k6-to-html
    echo 2. k6-to-html results\%LATEST_JSON% --output results\%HTML_FILE%
)

echo.
pause
