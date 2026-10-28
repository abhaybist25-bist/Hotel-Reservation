@echo off
cd /d "%~dp0"
echo ==============================================================================
echo Start Local MongoDB Server (Port 27017)
echo ==============================================================================

set "MONGO_BIN=C:\Program Files\MongoDB\Server\8.2\bin\mongod.exe"
set "MONGO_DATA=%~dp0mongo-data"

if not exist "%MONGO_BIN%" (
    echo [ERROR] MongoDB Server executable not found at: %MONGO_BIN%
    echo Please install MongoDB Community Server or adjust the path in this script.
    pause
    exit /b 1
)

if not exist "%MONGO_DATA%" (
    echo [INFO] Creating user-writable data directory at: %MONGO_DATA%
    mkdir "%MONGO_DATA%"
)

echo [INFO] Data path: %MONGO_DATA%
echo [INFO] Starting MongoDB daemon listening on 127.0.0.1:27017...
echo [INFO] Press Ctrl+C in this terminal window to stop the server.
echo.
"%MONGO_BIN%" --dbpath "%MONGO_DATA%" --port 27017 --bind_ip 127.0.0.1

