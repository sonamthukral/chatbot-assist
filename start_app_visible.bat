@echo off
echo ========================================
echo Starting Chatbot Application
echo ========================================
echo.
echo This window will show all console output
echo Keep this window open to see logs
echo.
echo Press Ctrl+C to stop the application
echo.
echo ========================================
echo.

REM Add Maven to PATH (update path if Maven is installed elsewhere)
REM set PATH=%PATH%;C:\Users\sonam\apache-maven\bin

REM Navigate to project directory
cd /d %~dp0

REM Start the application
mvn spring-boot:run

pause

