@echo off
cd /d "%~dp0"
setlocal enabledelayedexpansion

echo ==============================================================================
echo Deploy Hotel Reservation System to Tomcat 9
echo ==============================================================================

set "TOMCAT_DIR=C:\Users\Abhay\Downloads\apache-tomcat-9.0.123-windows-x64\apache-tomcat-9.0.123"

if not exist "%TOMCAT_DIR%\webapps" (
    echo [ERROR] Tomcat webapps folder not found at: %TOMCAT_DIR%\webapps
    echo Please check the path and try again.
    pause
    exit /b 1
)

echo [INFO] Copying target\hotel-reservation-system.war to Tomcat webapps...
copy /y "target\hotel-reservation-system.war" "%TOMCAT_DIR%\webapps\"

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Failed to copy WAR file.
    pause
    exit /b 1
)

echo.
echo ==============================================================================
echo [SUCCESS] WAR deployed to Tomcat!
echo.
echo Starting Tomcat now...
echo ==============================================================================
echo.

set "CATALINA_HOME=%TOMCAT_DIR%"
set "CATALINA_BASE=%TOMCAT_DIR%"
if not defined JAVA_HOME (
    set "JAVA_HOME=C:\Program Files\Java\jdk-25"
)

pushd "%TOMCAT_DIR%\bin"
call startup.bat
popd

echo.
echo Tomcat startup script triggered.
echo Open your browser to: http://localhost:8080/hotel-reservation-system/
echo.
pause

