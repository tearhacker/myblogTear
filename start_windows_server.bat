@echo off
REM ========================================================
REM               Windows云服务器启动脚本
REM ========================================================

echo 正在启动博客系统（Windows云服务器环境）...

REM 设置Java环境变量（如果未设置）
if "%JAVA_HOME%"=="" (
    echo 警告: 未设置JAVA_HOME环境变量，尝试使用系统默认Java
)

REM 设置环境变量
set SPRING_PROFILES_ACTIVE=windows
set FILE_UPLOAD_PATH=D:\webapps\uploads
set DB_PASSWORD=your_password_here

REM 创建必要的目录
if not exist "D:\webapps\uploads" mkdir "D:\webapps\uploads"
if not exist "D:\logs\myblog" mkdir "D:\logs\myblog"
if not exist "C:\temp" mkdir "C:\temp"

REM 检查Java版本
java -version
if %errorlevel% neq 0 (
    echo 错误: 未找到Java运行环境，请安装JDK 8或更高版本
    pause
    exit /b 1
)

REM 启动应用
echo 使用配置文件: application-windows.yml
echo 文件上传路径: %FILE_UPLOAD_PATH%
echo 数据库密码: %DB_PASSWORD%

java -jar -Xms512m -Xmx1024m ^
     -Dspring.profiles.active=%SPRING_PROFILES_ACTIVE% ^
     -Dfile.upload.path=%FILE_UPLOAD_PATH% ^
     -DDB_PASSWORD=%DB_PASSWORD% ^
     -Dserver.port=80 ^
     myblog.jar

if %errorlevel% neq 0 (
    echo 应用启动失败，错误代码: %errorlevel%
    pause
)

echo 应用已停止
pause
