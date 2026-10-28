@echo off
cd /d "%~dp0"
setlocal enabledelayedexpansion

echo ==============================================================================
echo Hotel Reservation System - Maven WAR Build Script
echo ==============================================================================

where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo [INFO] Detected Maven installation on PATH. Running: mvn clean package
    call mvn clean package
    if %ERRORLEVEL% NEQ 0 (
        echo [ERROR] Maven build failed.
        exit /b %ERRORLEVEL%
    )
    echo [SUCCESS] Application packaged at: target\hotel-reservation-system.war
    exit /b 0
)

echo [INFO] 'mvn' command not found on system PATH.
echo [INFO] Building with installed JDK and cached Maven dependencies...

if not defined JAVA_HOME (
    if exist "C:\Program Files\Java\jdk-25" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-25"
    ) else (
        echo [ERROR] Please set JAVA_HOME to JDK 17+
        exit /b 1
    )
)

echo [INFO] Using JDK: %JAVA_HOME%
set "JAVAC=%JAVA_HOME%\bin\javac.exe"
set "JAR=%JAVA_HOME%\bin\jar.exe"

set "M2_REPO=%USERPROFILE%\.m2\repository"
set "CP=%M2_REPO%\javax\servlet\javax.servlet-api\4.0.1\javax.servlet-api-4.0.1.jar;%M2_REPO%\javax\servlet\jstl\1.2\jstl-1.2.jar;%M2_REPO%\org\mongodb\mongodb-driver-sync\4.11.1\mongodb-driver-sync-4.11.1.jar;%M2_REPO%\org\mongodb\mongodb-driver-core\4.11.1\mongodb-driver-core-4.11.1.jar;%M2_REPO%\org\mongodb\bson\4.11.1\bson-4.11.1.jar;%M2_REPO%\org\mindrot\jbcrypt\0.4\jbcrypt-0.4.jar"

if not exist "target\classes" mkdir "target\classes"
powershell -NoProfile -Command "(Get-ChildItem -Path src\main\java -Filter *.java -Recurse | Resolve-Path -Relative) | Set-Content target\sources.txt"

echo [INFO] Compiling Java classes with target 17...
"%JAVAC%" --release 17 -encoding UTF-8 -cp "%CP%" -d target\classes @target\sources.txt
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed.
    exit /b 1
)

copy /y src\main\resources\application.properties target\classes\ >nul

echo [INFO] Assembling WAR structure...
set "WAR_DIR=target\hotel-reservation-system"
if not exist "%WAR_DIR%\WEB-INF\lib" mkdir "%WAR_DIR%\WEB-INF\lib"
if not exist "%WAR_DIR%\WEB-INF\classes" mkdir "%WAR_DIR%\WEB-INF\classes"

xcopy /s /y /q target\classes\* "%WAR_DIR%\WEB-INF\classes\" >nul
copy /y "%M2_REPO%\javax\servlet\jstl\1.2\jstl-1.2.jar" "%WAR_DIR%\WEB-INF\lib\" >nul
copy /y "%M2_REPO%\org\mongodb\mongodb-driver-sync\4.11.1\mongodb-driver-sync-4.11.1.jar" "%WAR_DIR%\WEB-INF\lib\" >nul
copy /y "%M2_REPO%\org\mongodb\mongodb-driver-core\4.11.1\mongodb-driver-core-4.11.1.jar" "%WAR_DIR%\WEB-INF\lib\" >nul
copy /y "%M2_REPO%\org\mongodb\bson\4.11.1\bson-4.11.1.jar" "%WAR_DIR%\WEB-INF\lib\" >nul
copy /y "%M2_REPO%\org\mongodb\bson-record-codec\4.11.1\bson-record-codec-4.11.1.jar" "%WAR_DIR%\WEB-INF\lib\" >nul
copy /y "%M2_REPO%\org\mindrot\jbcrypt\0.4\jbcrypt-0.4.jar" "%WAR_DIR%\WEB-INF\lib\" >nul

copy /y src\main\webapp\WEB-INF\web.xml "%WAR_DIR%\WEB-INF\" >nul
xcopy /s /y /q src\main\webapp\WEB-INF\views\* "%WAR_DIR%\WEB-INF\views\" >nul
xcopy /s /y /q src\main\webapp\assets\* "%WAR_DIR%\assets\" >nul

echo [INFO] Packaging target\hotel-reservation-system.war...
"%JAR%" -cvf target\hotel-reservation-system.war -C "%WAR_DIR%" . >nul

echo ==============================================================================
echo [SUCCESS] Package created: target\hotel-reservation-system.war
echo ==============================================================================

