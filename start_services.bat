@echo off
title Start Blog+Redis+SOCKS Services
color 0a
setlocal enabledelayedexpansion

:: Service Configuration
set BLOG_JAR=TearMyBlog.jar
set SOCKS_JAR=home-server.jar
set SOCKS_PORT=9999
set JAVA_OPTS=-Xmx1024M -Xms256M

:: Check required files
echo [INFO] Checking required files...
if not exist "%BLOG_JAR%" (
    echo [ERROR] %BLOG_JAR% not found in current directory!
    pause
    exit /b 1
)
if not exist "%SOCKS_JAR%" (
    echo [ERROR] %SOCKS_JAR% not found in current directory!
    pause
    exit /b 1
)

:: Verify Redis in system PATH
where redis-server.exe >nul 2>&1
if errorlevel 1 (
    echo [ERROR] redis-server.exe not found in system PATH!
    echo Please install Redis or add it to your system PATH
    pause
    exit /b 1
)
echo [OK] All dependencies verified.
echo.

:: 1. Start Redis (from system PATH)
echo [1/3] Starting Redis service...
start /B redis-server.exe
timeout /t 2 >nul
tasklist | findstr /i "redis-server.exe" >nul
if errorlevel 1 (
    echo [FAILED] Redis failed to start
) else (
    for /f "tokens=2" %%p in ('tasklist /fi "imagename eq redis-server.exe" /nh') do set pid=%%p
    echo [RUNNING] Redis started (PID: !pid!)
)

:: 2. Start Blog
echo [2/3] Starting Blog service...
start /B javaw -jar "%BLOG_JAR%"
timeout /t 2 >nul
tasklist | findstr /i "javaw.exe" >nul
if errorlevel 1 (
    echo [FAILED] Blog failed to start
) else (
    echo [RUNNING] Blog service started
)

:: 3. Start SOCKS
echo [3/3] Starting SOCKS service...
start /B java %JAVA_OPTS% -jar "%SOCKS_JAR%" --server.port=%SOCKS_PORT%
timeout /t 3 >nul
tasklist | findstr /i "java.exe" | findstr "%SOCKS_JAR%" >nul
if errorlevel 1 (
    echo [FAILED] SOCKS service failed to start
) else (
    for /f "tokens=2" %%p in ('tasklist /fi "imagename eq java.exe" /nh ^| findstr "%SOCKS_JAR%"') do set pid=%%p
    echo [RUNNING] SOCKS started (Port:%SOCKS_PORT%, PID:!pid!)
)

:: Final status
echo.
echo === SERVICE STATUS ===
echo Redis:    redis-server.exe
echo Blog:     javaw.exe
echo SOCKS:    java.exe (port %SOCKS_PORT%)
echo.
tasklist /FI "IMAGENAME eq redis-server.exe" /FI "IMAGENAME eq javaw.exe" /FI "IMAGENAME eq java.exe"

echo.
echo === PORT CHECK ===
netstat -ano | findstr ":%SOCKS_PORT%"

echo.
echo [NOTE] Press any key to close. Services will keep running.
echo Use stop_services.bat to terminate all services.
pause >nul
