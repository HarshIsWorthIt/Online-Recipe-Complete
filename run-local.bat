@echo off
setlocal
cd /d "%~dp0"

echo Building and starting Recipely at http://localhost:8080
mvn clean package
if errorlevel 1 (
  echo Build failed. Check that Java 21 and Maven 3.9 or later are installed.
  pause
  exit /b 1
)

mvn jetty:run
endlocal
