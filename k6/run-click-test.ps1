# K6 URL 클릭 부하 테스트 실행 스크립트 (PowerShell)
# 사용법: .\run-click-test.ps1

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "K6 URL 클릭 부하 테스트 시작" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# 현재 디렉토리 가져오기
$currentDir = Get-Location

# 결과 디렉토리 생성
$outputDir = ".\output"
if (-not (Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir | Out-Null
    Write-Host "✓ output 디렉토리 생성됨" -ForegroundColor Green
}

Write-Host "테스트 시작 시간: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')" -ForegroundColor Yellow
Write-Host "결과 저장 경로: $outputDir\" -ForegroundColor Yellow
Write-Host ""

# K6 Docker 컨테이너로 부하 테스트 실행
# htmlReport가 스크립트 내에서 자동으로 HTML 생성
docker run --rm -i `
  -v "${currentDir}\load:/scripts" `
  -v "${currentDir}\output:/output" `
  grafana/k6 run /scripts/click-test.js

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "============================================" -ForegroundColor Green
    Write-Host "테스트 완료!" -ForegroundColor Green
    Write-Host "============================================" -ForegroundColor Green
    
    # 생성된 파일 찾기
    $htmlFiles = Get-ChildItem -Path $outputDir -Filter "click-test_*.html" | Sort-Object LastWriteTime -Descending
    $jsonFiles = Get-ChildItem -Path $outputDir -Filter "click-test_*.json" | Sort-Object LastWriteTime -Descending
    
    if ($htmlFiles.Count -gt 0) {
        $latestHtml = $htmlFiles[0]
        Write-Host ""
        Write-Host "📊 생성된 파일:" -ForegroundColor Cyan
        Write-Host "  - HTML 리포트: output\$($latestHtml.Name)" -ForegroundColor White
        
        if ($jsonFiles.Count -gt 0) {
            Write-Host "  - JSON 데이터: output\$($jsonFiles[0].Name)" -ForegroundColor White
        }
        
        Write-Host ""
        
        # JSON 파일에서 요약 정보 파싱 시도
        if ($jsonFiles.Count -gt 0) {
            try {
                $jsonContent = Get-Content $jsonFiles[0].FullName -Raw -Encoding UTF8
                $summary = $jsonContent | ConvertFrom-Json
                
                Write-Host "============================================" -ForegroundColor Cyan
                Write-Host "테스트 요약" -ForegroundColor Cyan
                Write-Host "============================================" -ForegroundColor Cyan
                
                # 주요 메트릭 출력
                if ($summary.metrics.http_req_duration) {
                    $httpDuration = $summary.metrics.http_req_duration.values
                    Write-Host "평균 응답 시간: $([math]::Round($httpDuration.avg, 2))ms" -ForegroundColor White
                    Write-Host "P(90) 응답 시간: $([math]::Round($httpDuration.'p(90)', 2))ms" -ForegroundColor White
                    Write-Host "P(95) 응답 시간: $([math]::Round($httpDuration.'p(95)', 2))ms" -ForegroundColor White
                    Write-Host "최소 응답 시간: $([math]::Round($httpDuration.min, 2))ms" -ForegroundColor White
                    Write-Host "최대 응답 시간: $([math]::Round($httpDuration.max, 2))ms" -ForegroundColor White
                }
                
                if ($summary.metrics.click_success_count) {
                    $clickCount = $summary.metrics.click_success_count.values.count
                    Write-Host "클릭 성공 횟수: $clickCount 개" -ForegroundColor Yellow
                }
                
                if ($summary.metrics.http_reqs) {
                    $totalReqs = $summary.metrics.http_reqs.values.count
                    $reqRate = [math]::Round($summary.metrics.http_reqs.values.rate, 2)
                    Write-Host "총 요청 수: $totalReqs 개" -ForegroundColor White
                    Write-Host "초당 요청 수: $reqRate req/s" -ForegroundColor White
                }
                
                if ($summary.metrics.http_req_failed) {
                    $failedRate = $summary.metrics.http_req_failed.values.rate
                    $failedPct = [math]::Round($failedRate * 100, 4)
                    if ($failedPct -gt 0) {
                        Write-Host "실패율: $failedPct%" -ForegroundColor Red
                    } else {
                        Write-Host "실패율: $failedPct%" -ForegroundColor Green
                    }
                }
                
                Write-Host ""
            }
            catch {
                Write-Host "⚠ JSON 파싱 중 오류 발생 (무시됨)" -ForegroundColor Yellow
            }
        }
        
        # HTML 리포트 열기
        Write-Host "HTML 리포트를 브라우저에서 여시겠습니까? (Y/N) [기본값: Y]: " -ForegroundColor Yellow -NoNewline
        $openBrowser = Read-Host
        
        if ($openBrowser -eq '' -or $openBrowser -eq 'Y' -or $openBrowser -eq 'y') {
            Start-Process $latestHtml.FullName
            Write-Host "✓ 브라우저에서 리포트를 열었습니다." -ForegroundColor Green
        }
    } else {
        Write-Host "⚠ HTML 파일이 생성되지 않았습니다." -ForegroundColor Yellow
        Write-Host "output 디렉토리를 확인하세요: $outputDir" -ForegroundColor Yellow
    }
    
} else {
    Write-Host ""
    Write-Host "============================================" -ForegroundColor Red
    Write-Host "테스트 실패!" -ForegroundColor Red
    Write-Host "============================================" -ForegroundColor Red
    Write-Host "오류가 발생했습니다. 로그를 확인하세요." -ForegroundColor Red
}

Write-Host ""
Write-Host "Press any key to continue..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
