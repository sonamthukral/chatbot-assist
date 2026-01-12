# PowerShell script to set up ngrok for sharing localhost
Write-Host "Setting up ngrok to share your application..." -ForegroundColor Green
Write-Host ""

$ngrokPath = "$env:USERPROFILE\ngrok"
$ngrokExe = "$ngrokPath\ngrok.exe"

# Check if ngrok is installed
if (Test-Path $ngrokExe) {
    Write-Host "✅ ngrok found at: $ngrokExe" -ForegroundColor Green
} else {
    Write-Host "Downloading ngrok..." -ForegroundColor Yellow
    Write-Host "Please download ngrok from: https://ngrok.com/download" -ForegroundColor Cyan
    Write-Host ""
    
    # Try to download ngrok
    $ngrokUrl = "https://bin.equinox.io/c/b/ykrxPT7CjFe/ngrok-v3-stable-windows-amd64.zip"
    $zipFile = "$env:TEMP\ngrok.zip"
    
    try {
        Write-Host "Downloading ngrok..." -ForegroundColor Yellow
        Invoke-WebRequest -Uri $ngrokUrl -OutFile $zipFile -UseBasicParsing
        Write-Host "Extracting..." -ForegroundColor Yellow
        
        New-Item -ItemType Directory -Force -Path $ngrokPath | Out-Null
        Expand-Archive -Path $zipFile -DestinationPath $ngrokPath -Force
        Remove-Item -Path $zipFile -Force
        
        Write-Host "✅ ngrok downloaded and extracted!" -ForegroundColor Green
    } catch {
        Write-Host "❌ Could not download automatically." -ForegroundColor Red
        Write-Host "Please download manually from: https://ngrok.com/download" -ForegroundColor Yellow
        Write-Host "Extract ngrok.exe to: $ngrokPath" -ForegroundColor Yellow
        exit 1
    }
}

# Check if authtoken is configured
Write-Host ""
Write-Host "To use ngrok, you need:" -ForegroundColor Yellow
Write-Host "1. Sign up at: https://dashboard.ngrok.com/signup" -ForegroundColor Cyan
Write-Host "2. Get your authtoken from: https://dashboard.ngrok.com/get-started/your-authtoken" -ForegroundColor Cyan
Write-Host "3. Run: $ngrokExe config add-authtoken YOUR_TOKEN" -ForegroundColor Cyan
Write-Host ""
Write-Host "Then start the tunnel with:" -ForegroundColor Yellow
Write-Host "  $ngrokExe http 8080" -ForegroundColor White
Write-Host ""
Write-Host "This will give you a shareable URL like: https://abc123.ngrok-free.app" -ForegroundColor Green

