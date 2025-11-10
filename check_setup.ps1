# PowerShell script to check if Java and Maven are installed
Write-Host "=== Checking Installation Status ===" -ForegroundColor Cyan
Write-Host ""

# Check Java
Write-Host "Checking Java..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-Object -First 1
    Write-Host "✓ Java is installed!" -ForegroundColor Green
    Write-Host "  Version: $javaVersion" -ForegroundColor Gray
} catch {
    Write-Host "✗ Java is NOT installed or not in PATH" -ForegroundColor Red
    Write-Host "  Download from: https://adoptium.net/temurin/releases/" -ForegroundColor Yellow
    Write-Host "  Choose: Windows x64, JDK 17 or 21" -ForegroundColor Yellow
}

Write-Host ""

# Check Maven
Write-Host "Checking Maven..." -ForegroundColor Yellow
try {
    $mvnVersion = mvn -version 2>&1 | Select-Object -First 1
    Write-Host "✓ Maven is installed!" -ForegroundColor Green
    Write-Host "  Version: $mvnVersion" -ForegroundColor Gray
} catch {
    Write-Host "✗ Maven is NOT installed or not in PATH" -ForegroundColor Red
    Write-Host "  Note: Maven is optional if using Java Extension Pack" -ForegroundColor Yellow
    Write-Host "  Download from: https://maven.apache.org/download.cgi" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "=== Next Steps ===" -ForegroundColor Cyan
Write-Host ""

if (Get-Command java -ErrorAction SilentlyContinue) {
    Write-Host "1. Install Java Extension Pack in Cursor:" -ForegroundColor Green
    Write-Host "   - Press Ctrl+Shift+X" -ForegroundColor Gray
    Write-Host "   - Search: 'Extension Pack for Java' (by Microsoft)" -ForegroundColor Gray
    Write-Host "   - Click Install" -ForegroundColor Gray
    Write-Host ""
    Write-Host "2. Open CrisisChatbotApplication.java" -ForegroundColor Green
    Write-Host "   - Look for green Run button above main method" -ForegroundColor Gray
    Write-Host "   - Or right-click → 'Run Java'" -ForegroundColor Gray
} else {
    Write-Host "1. Install Java JDK first:" -ForegroundColor Yellow
    Write-Host "   - Download from: https://adoptium.net/temurin/releases/" -ForegroundColor Gray
    Write-Host "   - Install with 'Add to PATH' checked" -ForegroundColor Gray
    Write-Host "   - Restart Cursor after installation" -ForegroundColor Gray
}

Write-Host ""
Write-Host "3. Once running, open: http://localhost:8080" -ForegroundColor Green
Write-Host ""

