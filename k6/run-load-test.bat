@echo off
REM K6 부하 테스트 실행 스크립트 (Windows)

echo ============================================
echo K6 부하 테스트 시작
echo ============================================
echo.

REM 결과 디렉토리 생성
if not exist ".\output" (
    mkdir ".\output"
    echo ✓ output 디렉토리 생성됨
)

echo 테스트 시작 시간: %date% %time%
echo 결과 저장 경로: output\
echo.

REM K6 Docker 컨테이너로 부하 테스트 실행
REM htmlReport가 스크립트 내에서 자동으로 HTML 생성
docker run --rm -i ^
  -v "%cd%\load":/scripts ^
  -v "%cd%\output":/output ^
  grafana/k6 run /scripts/load-test.js

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ============================================
    echo 테스트 완료!
    echo ============================================
    
    REM 가장 최근 HTML 파일 찾기
    for /f "delims=" %%f in ('dir /b /o-d ".\output\summary_*.html" 2^>nul') do (
        set "LATEST_HTML=%%f"
        goto :found_html
    )
    
    :found_html
    if defined LATEST_HTML (
        echo.
        echo 📊 생성된 파일:
        echo   - HTML 리포트: output\%LATEST_HTML%
        
        REM JSON 파일도 표시
        for /f "delims=" %%j in ('dir /b /o-d ".\output\summary_*.json" 2^>nul') do (
            echo   - JSON 데이터: output\%%j
            goto :found_json
        )
        
        :found_json
        echo.
        
        REM HTML 리포트 열기
        set /p OPEN_BROWSER="HTML 리포트를 브라우저에서 여시겠습니까? (Y/N) [기본값: Y]: "
        if not defined OPEN_BROWSER set OPEN_BROWSER=Y
        if /i "%OPEN_BROWSER%"=="Y" (
            start "" ".\output\%LATEST_HTML%"
            echo ✓ 브라우저에서 리포트를 열었습니다.
        )
    ) else (
        echo.
        echo ⚠ HTML 파일이 생성되지 않았습니다.
        echo output 디렉토리를 확인하세요.
    )
    
) else (
    echo.
    echo ============================================
    echo 테스트 실패!
    echo ============================================
    echo 오류가 발생했습니다. 로그를 확인하세요.
)

echo.
pause
