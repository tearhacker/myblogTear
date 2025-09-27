@echo off
:: =============================================
:: MySQL备份脚本（完美支持现有中文路径）
:: 版本：2.0
:: 特点：无需创建目录/100%兼容中文路径
:: =============================================

:: ---------- 初始化设置 ----------
setlocal enabledelayedexpansion
chcp 65001 > nul
cls
title MySQL备份程序 - 正在运行...

:: ---------- 用户配置区 ----------
set "MYSQL_PATH=D:\DevelopIDEenvironment\mysql-8.0.42-winx64\bin"
set "DB_USER=root"
set "DB_PASS=123456"
set "DB_NAME=myblog"
set "BACKUP_DIR=D:\Work_WJ\博客星梦耀光\sql"

:: ---------- 路径验证 ----------
if not exist "%BACKUP_DIR%" (
    echo [错误] 备份目录不存在：%BACKUP_DIR%
    echo 请确认路径是否正确或手动创建目录
    pause
    exit /b 1
)

:: ---------- 备份核心逻辑 ----------
:: 生成精确时间戳（解决空格问题）
for /f "tokens=2 delims==" %%a in ('wmic os get localdatetime /value') do set "datetime=%%a"
set "TIMESTAMP=%datetime:~0,4%-%datetime:~4,2%-%datetime:~6,2%_%datetime:~8,2%-%datetime:~10,2%"
set "BACKUP_FILE=%BACKUP_DIR%\%DB_NAME%_%TIMESTAMP%.sql"

:: 执行备份（添加错误重定向）
echo 正在备份数据库 %DB_NAME%...
cd /d "%MYSQL_PATH%"
mysqldump -u"%DB_USER%" -p"%DB_PASS%" --column-statistics=0 "%DB_NAME%" > "%BACKUP_FILE%" 2>nul

:: 结果验证
if errorlevel 1 (
    echo [错误] 备份失败！可能原因：
    echo 1. MySQL服务未运行
    echo 2. 认证失败
    echo 3. 数据库不存在
    echo 错误详情请检查：%BACKUP_DIR%\mysql_error.log
    mysqldump -u"%DB_USER%" -p"%DB_PASS%" "%DB_NAME%" 2> "%BACKUP_DIR%\mysql_error.log"
) else (
    echo [成功] 备份完成！
    echo 文件位置：%BACKUP_FILE%
    echo 文件大小： %~z0 bytes
)

:: 添加操作日志
echo %date% %time% - 备份操作执行 >> "%BACKUP_DIR%\backup_history.log"

pause
exit /b 0