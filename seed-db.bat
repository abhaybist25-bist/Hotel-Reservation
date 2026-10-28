@echo off
cd /d "%~dp0"
setlocal enabledelayedexpansion

echo ==============================================================================
echo Hotel Reservation System - Database Seeder
echo ==============================================================================

if not defined JAVA_HOME (
    if exist "C:\Program Files\Java\jdk-25" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-25"
    ) else (
        echo [ERROR] Please set JAVA_HOME
        exit /b 1
    )
)

set "JAVA=%JAVA_HOME%\bin\java.exe"
set "JAVAC=%JAVA_HOME%\bin\javac.exe"
set "M2_REPO=%USERPROFILE%\.m2\repository"

set "CP=target\classes;%M2_REPO%\javax\servlet\javax.servlet-api\4.0.1\javax.servlet-api-4.0.1.jar;%M2_REPO%\org\mongodb\mongodb-driver-sync\4.11.1\mongodb-driver-sync-4.11.1.jar;%M2_REPO%\org\mongodb\mongodb-driver-core\4.11.1\mongodb-driver-core-4.11.1.jar;%M2_REPO%\org\mongodb\bson\4.11.1\bson-4.11.1.jar;%M2_REPO%\org\mongodb\bson-record-codec\4.11.1\bson-record-codec-4.11.1.jar;%M2_REPO%\org\mindrot\jbcrypt\0.4\jbcrypt-0.4.jar"

copy /y src\main\resources\application.properties target\classes\ >nul 2>&1

"%JAVA%" -cp "%CP%" com.hotelreservation.config.DatabaseInitializer

