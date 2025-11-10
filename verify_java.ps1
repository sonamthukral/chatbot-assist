# Quick Java Verification Script
Write-Host "=== Verifying Java Installation ===" -ForegroundColor Cyan
Write-Host ""

$javaInstalled = $false
try {
    $javaOutput = java -version 2>&1
    if ($javaOutput -match "version") {
        Write-Host "✓ Java is installed!" -ForegroundColor Green
        Write-Host $javaOutput[0] -ForegroundColor Gray
        $javaInstalled = $true
    }
} catch {
    Write-Host "✗ Java is NOT found" -ForegroundColor Red
}

Write-Host ""

if ($javaInstalled) {
    Write-Host "=== Next Steps ===" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "1. Install Java Extension Pack in Cursor:" -ForegroundColor Yellow
    Write-Host "   - Press Ctrl+Shift+X" -ForegroundColor White
    Write-Host "   - Search: 'Extension Pack for Java' (by Microsoft)" -ForegroundColor White
    Write-Host "   - Click Install" -ForegroundColor White
    Write-Host ""
    Write-Host "2. After installing the extension:" -ForegroundColor Yellow
    Write-Host "   - Restart Cursor" -ForegroundColor White
    Write-Host "   - Open: src/main/java/com/suicide/questionbank/CrisisChatbotApplication.java" -ForegroundColor White
    Write-Host "   - Look for green Run button above main method" -ForegroundColor White
    Write-Host ""
    Write-Host "3. Once running, open: http://localhost:8080" -ForegroundColor Green
} else {
    Write-Host "Please install Java first:" -ForegroundColor Yellow
    Write-Host "1. Download from: https://adoptium.net/temurin/releases/" -ForegroundColor White
    Write-Host "2. Install with 'Add to PATH' checked" -ForegroundColor White
    Write-Host "3. Restart Cursor after installation" -ForegroundColor White
    Write-Host "4. Run this script again to verify" -ForegroundColor White
}

Write-Host ""

