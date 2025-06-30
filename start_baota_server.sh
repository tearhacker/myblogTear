#!/bin/bash
# ========================================================
#               宝塔面板部署启动脚本
# ========================================================

echo "正在启动博客系统（宝塔面板环境）..."

# 设置环境变量
export SPRING_PROFILES_ACTIVE=baota
export FILE_UPLOAD_PATH=/www/wwwroot/yourdomain/uploads
export DB_PASSWORD=your_password_here

# 创建必要的目录
mkdir -p /www/wwwroot/yourdomain/uploads
mkdir -p /www/wwwlogs/myblog
mkdir -p /tmp

# 设置目录权限
chmod 755 /www/wwwroot/yourdomain/uploads
chmod 755 /www/wwwlogs/myblog
chmod 755 /tmp

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java运行环境，请安装JDK 8或更高版本"
    exit 1
fi

# 显示Java版本
java -version

# 启动应用
echo "使用配置文件: application-baota.yml"
echo "文件上传路径: $FILE_UPLOAD_PATH"
echo "数据库密码: $DB_PASSWORD"

java -jar -Xms512m -Xmx1024m \
     -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE \
     -Dfile.upload.path=$FILE_UPLOAD_PATH \
     -DDB_PASSWORD=$DB_PASSWORD \
     -Dserver.port=8080 \
     myblog.jar

if [ $? -ne 0 ]; then
    echo "应用启动失败，错误代码: $?"
    exit 1
fi

echo "应用已停止"
