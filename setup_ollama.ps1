# PowerShell script to download and set up Ollama
Write-Host "Setting up Ollama (Local LLM)..." -ForegroundColor Green
Write-Host ""

# Check if Ollama is already installed
$ollamaInstalled = Get-Command ollama -ErrorAction SilentlyContinue

if ($ollamaInstalled) {
    Write-Host "✅ Ollama is already installed!" -ForegroundColor Green
    Write-Host "Version: " -NoNewline
    ollama --version
} else {
    Write-Host "Downloading Ollama..." -ForegroundColor Yellow
    Write-Host "Please download Ollama from: https://ollama.ai/download" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "After downloading:" -ForegroundColor Yellow
    Write-Host "1. Run the installer" -ForegroundColor White
    Write-Host "2. Come back and run this script again to download a model" -ForegroundColor White
    Write-Host ""
    
    # Try to open the download page
    Start-Process "https://ollama.ai/download"
    
    $continue = Read-Host "Press Enter after you've installed Ollama, or type 'skip' to continue anyway"
    if ($continue -eq "skip") {
        Write-Host "Skipping Ollama installation check..." -ForegroundColor Yellow
    }
}

# Check if Ollama is running
Write-Host ""
Write-Host "Checking if Ollama is running..." -ForegroundColor Yellow
try {
    $ollamaList = ollama list 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Ollama is running!" -ForegroundColor Green
    } else {
        Write-Host "⚠️ Ollama might not be running. Starting it..." -ForegroundColor Yellow
        Start-Process "ollama" -ArgumentList "serve" -WindowStyle Hidden
        Start-Sleep -Seconds 3
    }
} catch {
    Write-Host "⚠️ Could not check Ollama status. Make sure it's installed and running." -ForegroundColor Yellow
}

# Download a model
Write-Host ""
Write-Host "Downloading a small, fast model (llama3.2:1b)..." -ForegroundColor Yellow
Write-Host "This is a small model that will work well on most computers." -ForegroundColor Cyan
Write-Host ""

$modelChoice = Read-Host "Download model? (y/n) [default: y]"
if ($modelChoice -eq "" -or $modelChoice -eq "y" -or $modelChoice -eq "Y") {
    Write-Host "Downloading llama3.2:1b (this may take a few minutes)..." -ForegroundColor Yellow
    ollama pull llama3.2:1b
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Model downloaded successfully!" -ForegroundColor Green
    } else {
        Write-Host "❌ Model download failed. You can try manually: ollama pull llama3.2:1b" -ForegroundColor Red
    }
} else {
    Write-Host "Skipping model download." -ForegroundColor Yellow
    Write-Host "You can download a model manually with: ollama pull llama3.2:1b" -ForegroundColor Cyan
}

Write-Host ""
Write-Host "Setup complete!" -ForegroundColor Green
Write-Host "Next: Update application.properties to use Ollama" -ForegroundColor Cyan

