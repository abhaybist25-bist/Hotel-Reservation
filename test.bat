@echo off
cd /d "%~dp0"
setlocal enabledelayedexpansion

echo ==============================================================================
echo Hotel Reservation System - Automated Test Runner
echo ==============================================================================

where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    echo [INFO] Running tests via Maven: mvn test
    call mvn test
    exit /b %ERRORLEVEL%
)

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

set "CP=target\classes;target\test-classes;%M2_REPO%\javax\servlet\javax.servlet-api\4.0.1\javax.servlet-api-4.0.1.jar;%M2_REPO%\org\mongodb\mongodb-driver-sync\4.11.1\mongodb-driver-sync-4.11.1.jar;%M2_REPO%\org\mongodb\mongodb-driver-core\4.11.1\mongodb-driver-core-4.11.1.jar;%M2_REPO%\org\mongodb\bson\4.11.1\bson-4.11.1.jar;%M2_REPO%\org\mindrot\jbcrypt\0.4\jbcrypt-0.4.jar;%M2_REPO%\junit\junit\4.13.2\junit-4.13.2.jar;%M2_REPO%\org\hamcrest\hamcrest-core\1.3\hamcrest-core-1.3.jar;%M2_REPO%\org\mockito\mockito-core\4.11.0\mockito-core-4.11.0.jar;%M2_REPO%\net\bytebuddy\byte-buddy\1.12.19\byte-buddy-1.12.19.jar;%M2_REPO%\net\bytebuddy\byte-buddy-agent\1.12.19\byte-buddy-agent-1.12.19.jar;%M2_REPO%\org\objenesis\objenesis\3.3\objenesis-3.3.jar"

if not exist "target\test-classes" mkdir "target\test-classes"
copy /y src\main\resources\application.properties target\classes\ >nul 2>&1
powershell -NoProfile -Command "(Get-ChildItem -Path src\test\java -Filter *.java -Recurse | Resolve-Path -Relative) | Set-Content target\test_sources.txt"

echo [INFO] Compiling unit test suites...
"%JAVAC%" --release 17 -encoding UTF-8 -cp "%CP%" -d target\test-classes @target\test_sources.txt
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Test compilation failed.
    exit /b 1
)

echo [INFO] Executing JUnit 4 test runner...
"%JAVA%" -cp "%CP%" org.junit.runner.JUnitCore com.hotelreservation.DateUtilTest com.hotelreservation.BookingCostBreakdownTest com.hotelreservation.PasswordUtilTest com.hotelreservation.ValidationUtilTest com.hotelreservation.RoleAndStatusTransitionTest com.hotelreservation.BookingServiceLogicTest com.hotelreservation.HotelServiceTest

