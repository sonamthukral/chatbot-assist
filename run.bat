@echo off
echo Starting Spring Boot Application...
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java JDK 11 or later
    echo Download from: https://adoptium.net/
    pause
    exit /b 1
)

REM Check if Maven is installed
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven
    echo Download from: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

echo Java and Maven found!
echo.
echo Starting application...
echo Once you see "Started CrisisChatbotApplication", open:
echo http://localhost:8080
echo.
echo Press Ctrl+C to stop the application
echo.

mvn spring-boot:run

pause

