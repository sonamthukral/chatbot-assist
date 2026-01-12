# PowerShell script to download and install Maven automatically
Write-Host "Installing Maven..." -ForegroundColor Green

$mavenVersion = "3.9.5"
$mavenUrl = "https://archive.apache.org/dist/maven/maven-3/$mavenVersion/binaries/apache-maven-$mavenVersion-bin.zip"
$installDir = "$env:USERPROFILE\apache-maven"
$zipFile = "$env:TEMP\apache-maven-$mavenVersion-bin.zip"

# Create install directory
New-Item -ItemType Directory -Force -Path $installDir | Out-Null

# Download Maven
Write-Host "Downloading Maven $mavenVersion..." -ForegroundColor Yellow
try {
    Invoke-WebRequest -Uri $mavenUrl -OutFile $zipFile -UseBasicParsing
    Write-Host "Download complete!" -ForegroundColor Green
} catch {
    Write-Host "Error downloading Maven: $_" -ForegroundColor Red
    Write-Host "Please download manually from: https://maven.apache.org/download.cgi" -ForegroundColor Yellow
    exit 1
}

# Extract Maven
Write-Host "Extracting Maven..." -ForegroundColor Yellow
try {
    Expand-Archive -Path $zipFile -DestinationPath $env:TEMP -Force
    $extractedPath = "$env:TEMP\apache-maven-$mavenVersion"
    Move-Item -Path "$extractedPath\*" -Destination $installDir -Force
    Remove-Item -Path $extractedPath -Force
    Remove-Item -Path $zipFile -Force
    Write-Host "Extraction complete!" -ForegroundColor Green
} catch {
    Write-Host "Error extracting Maven: $_" -ForegroundColor Red
    exit 1
}

# Add to PATH for current session
$mavenBin = "$installDir\bin"
$env:Path += ";$mavenBin"

# Verify installation
Write-Host "`nVerifying installation..." -ForegroundColor Yellow
& "$mavenBin\mvn.cmd" -version
if ($LASTEXITCODE -eq 0) {
    Write-Host "`nMaven installed successfully!" -ForegroundColor Green
    Write-Host "Maven location: $installDir" -ForegroundColor Cyan
    Write-Host "`nTo make this permanent, add to PATH:" -ForegroundColor Yellow
    Write-Host "$mavenBin" -ForegroundColor Cyan
    Write-Host "`nFor now, Maven is available in this session." -ForegroundColor Green
    Write-Host "You can now run: mvn spring-boot:run" -ForegroundColor Cyan
} else {
    Write-Host "Installation verification failed." -ForegroundColor Red
}

