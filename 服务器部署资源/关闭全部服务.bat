@echo off
title Stop All Services
color 0a

echo [INFO] Stopping all services...

:: Stop Redis
taskkill /F /IM redis-server.exe >nul 2>&1
if errorlevel 1 (
    echo [INFO] Redis was not running
) else (
    echo [STOPPED] Redis service terminated
)

:: Stop Blog
taskkill /F /IM javaw.exe >nul 2>&1
if errorlevel 1 (
    echo [INFO] Blog was not running
) else (
    echo [STOPPED] Blog service terminated
)

:: Stop SOCKS
taskkill /F /IM java.exe >nul 2>&1
if errorlevel 1 (
    echo [INFO] SOCKS was not running
) else (
    echo [STOPPED] SOCKS service terminated
)

echo.
echo [NOTE] All services have been stopped
pause