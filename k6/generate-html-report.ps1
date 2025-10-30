# K6 테스트 결과를 HTML로 변환하는 스크립트
# 사용법: .\generate-html-report.ps1 [JSON파일명]

param(
    [string]$JsonFileName = ""
)

Write-Host "============================================" -ForegroundColor Cyan
Write-Host "K6 HTML 리포트 생성" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

$currentDir = Get-Location

# JSON 파일 찾기
if ($JsonFileName -eq "") {
    # results 디렉토리에서 가장 최근 JSON 파일 찾기
    $jsonFiles = Get-ChildItem -Path ".\results" -Filter "load-test-result-*.json" | Sort-Object LastWriteTime -Descending
    
    if ($jsonFiles.Count -eq 0) {
        Write-Host "❌ results 디렉토리에 JSON 파일이 없습니다." -ForegroundColor Red
        Write-Host "먼저 부하 테스트를 실행하세요: .\run-load-test.ps1" -ForegroundColor Yellow
        exit 1
    }
    
    Write-Host "발견된 JSON 파일:" -ForegroundColor Yellow
    for ($i = 0; $i -lt [Math]::Min(5, $jsonFiles.Count); $i++) {
        Write-Host "  [$i] $($jsonFiles[$i].Name) - $($jsonFiles[$i].LastWriteTime)" -ForegroundColor White
    }
    Write-Host ""
    
    Write-Host "변환할 파일 번호를 선택하세요 (기본값: 0 - 최신 파일): " -ForegroundColor Yellow -NoNewline
    $selection = Read-Host
    
    if ($selection -eq "") {
        $selection = 0
    }
    
    $selectedFile = $jsonFiles[[int]$selection]
    $JsonFileName = $selectedFile.Name
    Write-Host "선택된 파일: $JsonFileName" -ForegroundColor Green
} else {
    # 전체 경로가 아니라면 results 디렉토리 추가
    if (-not $JsonFileName.Contains("\")) {
        $JsonFileName = "results\$JsonFileName"
    }
    
    if (-not (Test-Path $JsonFileName)) {
        Write-Host "❌ 파일을 찾을 수 없습니다: $JsonFileName" -ForegroundColor Red
        exit 1
    }
}

Write-Host ""
Write-Host "HTML 리포트 생성 중..." -ForegroundColor Cyan

# 파일명에서 타임스탬프 추출
$timestamp = ""
if ($JsonFileName -match "load-test-result-(\d{8}_\d{6})\.json") {
    $timestamp = $Matches[1]
}

$htmlFileName = if ($timestamp -ne "") {
    "load-test-report-$timestamp.html"
} else {
    "load-test-report.html"
}

# Docker를 사용하여 HTML 생성
$jsonPath = if ($JsonFileName.StartsWith("results\")) {
    "/results/" + $JsonFileName.Substring(8)
} else {
    "/results/$JsonFileName"
}

docker run --rm -v "${currentDir}\results:/results" ghcr.io/benc-uk/k6-reporter:latest "$jsonPath" "/results/$htmlFileName"

if ($LASTEXITCODE -eq 0 -and (Test-Path ".\results\$htmlFileName")) {
    Write-Host ""
    Write-Host "============================================" -ForegroundColor Green
    Write-Host "✓ HTML 리포트 생성 완료!" -ForegroundColor Green
    Write-Host "============================================" -ForegroundColor Green
    Write-Host "파일: results\$htmlFileName" -ForegroundColor White
    Write-Host ""
    
    # 브라우저에서 열기
    Write-Host "브라우저에서 열시겠습니까? (Y/N): " -ForegroundColor Yellow -NoNewline
    $openBrowser = Read-Host
    
    if ($openBrowser -eq 'Y' -or $openBrowser -eq 'y') {
        Start-Process ".\results\$htmlFileName"
        Write-Host "✓ 브라우저에서 리포트를 열었습니다." -ForegroundColor Green
    }
} else {
    Write-Host ""
    Write-Host "============================================" -ForegroundColor Red
    Write-Host "❌ HTML 리포트 생성 실패" -ForegroundColor Red
    Write-Host "============================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "대체 방법:" -ForegroundColor Yellow
    Write-Host "1. npm install -g k6-to-html" -ForegroundColor White
    Write-Host "2. k6-to-html results\$JsonFileName --output results\$htmlFileName" -ForegroundColor White
}

Write-Host ""
Write-Host "Press any key to continue..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
