@echo off
set "JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.18.8-hotspot"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo ============================================
echo  Employee Management System - Backend
echo ============================================
echo.
echo Java Home: %JAVA_HOME%
java -version
echo.

if "%DB2_USER%"=="" (
    set /p DB2_USER=Enter DB2 Username: 
)
if "%DB2_PASS%"=="" (
    set /p DB2_PASS=Enter DB2 Password: 
)
echo DB2 User: %DB2_USER%
echo.
echo Starting Spring Boot...
echo.

cd /d "%~dp0"
call mvnw.cmd spring-boot:run
