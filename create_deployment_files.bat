@echo off
echo 🚀 创建教育风险预警系统部署文件...

REM 创建目录结构
echo 📁 创建目录...
mkdir "D:\demo\code2026\deployment" 2>nul
mkdir "D:\demo\code2026\deployment\docker" 2>nul
mkdir "D:\demo\code2026\deployment\docker\mysql" 2>nul
mkdir "D:\demo\code2026\deployment\nginx" 2>nul

REM 1. 创建Dockerfile
echo 📄 创建Dockerfile...
(
echo # 使用官方OpenJDK 17镜像
echo FROM openjdk:17-jdk-slim
echo.
echo # 设置工作目录
echo WORKDIR /app
echo.
echo # 设置时区
echo ENV TZ=Asia/Shanghai
echo RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime ^&^& echo $TZ ^> /etc/timezone
echo.
echo # 安装必要工具
echo RUN apt-get update ^&^& apt-get install -y ^
echo     curl ^
echo     ^&^& rm -rf /var/lib/apt/lists/*
echo.
echo # 添加应用jar包
echo COPY springboot/target/code2026-0.0.1-SNAPSHOT.jar app.jar
echo.
echo # 暴露端口
echo EXPOSE 8080
echo.
echo # JVM参数配置
echo ENV JAVA_OPTS="-server -Xmx512m -Xms256m -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+ParallelRefProcEnabled"
echo.
echo # 健康检查
echo HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 ^
echo     CMD curl -f http://localhost:8080/api/health/ping || exit 1
echo.
echo # 启动命令
echo ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
) > "D:\demo\code2026\deployment\docker\Dockerfile"

REM 2. 创建docker-compose.yml
echo 📄 创建docker-compose.yml...
(
echo version: '3.8'
echo.
echo services:
echo   # MySQL数据库
echo   mysql:
echo     image: mysql:8.0
echo     container_name: edu-risk-mysql
echo     environment:
echo       MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD:-Admin@123}
echo       MYSQL_DATABASE: code2026
echo       TZ: Asia/Shanghai
echo     volumes:
echo       - mysql_data:/var/lib/mysql
echo       - ./mysql/my.cnf:/etc/mysql/conf.d/my.cnf
echo       - ./mysql/init.sql:/docker-entrypoint-initdb.d/init.sql
echo     ports:
echo       - "3307:3306"
echo     networks:
echo       - edu-risk-network
echo     healthcheck:
echo       test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
echo       timeout: 20s
echo       retries: 10
echo.
echo   # Spring Boot应用
echo   springboot-app:
echo     build:
echo       context: ../../
echo       dockerfile: deployment/docker/Dockerfile
echo     container_name: edu-risk-backend
echo     environment:
echo       SPRING_PROFILES_ACTIVE: prod
echo       SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/code2026?useUnicode=true^&characterEncoding=utf8^&serverTimezone=Asia/Shanghai
echo       SPRING_DATASOURCE_USERNAME: root
echo       SPRING_DATASOURCE_PASSWORD: ${MYSQL_ROOT_PASSWORD:-Admin@123}
echo     ports:
echo       - "8080:8080"
echo     depends_on:
echo       mysql:
echo         condition: service_healthy
echo     networks:
echo       - edu-risk-network
echo     restart: unless-stopped
echo.
echo   # Redis缓存
echo   redis:
echo     image: redis:7-alpine
echo     container_name: edu-risk-redis
echo     ports:
echo       - "6379:6379"
echo     command: redis-server --appendonly yes
echo     volumes:
echo       - redis_data:/data
echo     networks:
echo       - edu-risk-network
echo.
echo # 网络定义
echo networks:
echo   edu-risk-network:
echo     driver: bridge
echo.
echo # 数据卷定义
echo volumes:
echo   mysql_data:
echo   redis_data:
) > "D:\demo\code2026\deployment\docker\docker-compose.yml"

REM 3. 创建MySQL配置文件
echo 📄 创建MySQL配置...
(
echo [mysqld]
echo character-set-server=utf8mb4
echo collation-server=utf8mb4_unicode_ci
echo default-time-zone='+08:00'
echo.
echo max_connections=1000
echo innodb_buffer_pool_size=256M
echo innodb_log_file_size=64M
echo.
echo wait_timeout=600
echo interactive_timeout=600
echo.
echo [mysql]
echo default-character-set=utf8mb4
echo.
echo [client]
echo default-character-set=utf8mb4
) > "D:\demo\code2026\deployment\docker\mysql\my.cnf"

REM 4. 创建MySQL初始化脚本
echo 📄 创建MySQL初始化脚本...
(
echo -- 初始化脚本：确保数据库存在
echo CREATE DATABASE IF NOT EXISTS code2026 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
echo.
echo -- 使用code2026数据库
echo USE code2026;
echo.
echo -- 系统指标表（如果应用未自动创建）
echo CREATE TABLE IF NOT EXISTS system_metrics (
echo     id BIGINT AUTO_INCREMENT PRIMARY KEY,
echo     timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
echo     qps DOUBLE,
echo     response_time_ms DOUBLE,
echo     success_rate DOUBLE,
echo     active_sessions INT,
echo     memory_usage_mb DOUBLE,
echo     cpu_usage_percent DOUBLE,
echo     status VARCHAR(20)
echo );
) > "D:\demo\code2026\deployment\docker\mysql\init.sql"

REM 5. 创建Nginx配置
echo 📄 创建Nginx配置...
(
echo worker_processes auto;
echo.
echo events {
echo     worker_connections 1024;
echo }
echo.
echo http {
echo     include       mime.types;
echo     default_type  application/octet-stream;
echo.
echo     # 日志格式
echo     log_format main '$remote_addr - $remote_user [$time_local] "$request" '
echo                       '$status $body_bytes_sent "$http_referer" '
echo                       '"$http_user_agent" "$http_x_forwarded_for"';
echo.
echo     access_log  /var/log/nginx/access.log main;
echo     error_log   /var/log/nginx/error.log warn;
echo.
echo     # 反向代理配置
echo     upstream springboot_backend {
echo         server springboot-app:8080;
echo     }
echo.
echo     server {
echo         listen 80;
echo         server_name localhost;
echo.
echo         location / {
echo             proxy_pass http://springboot_backend;
echo             proxy_set_header Host $host;
echo             proxy_set_header X-Real-IP $remote_addr;
echo             proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
echo             proxy_set_header X-Forwarded-Proto $scheme;
echo         }
echo.
echo         # 健康检查端点
echo         location /health {
echo             proxy_pass http://springboot_backend/api/health;
echo         }
echo     }
echo }
) > "D:\demo\code2026\deployment\nginx\nginx.conf"

echo ✅ 所有部署文件已创建完成！
echo.
echo 📁 目录结构：
echo D:\demo\code2026\deployment\
echo ├── docker\
echo │   ├── Dockerfile                 [Docker镜像构建文件]
echo │   ├── docker-compose.yml        [Docker编排配置文件 - YAML格式]
echo │   └── mysql\
echo │       ├── init.sql              [SQL初始化脚本]
echo │       └── my.cnf               [MySQL配置文件]
echo └── nginx\
echo     └── nginx.conf               [Nginx配置文件]
echo.
echo 🚀 下一步操作：
echo 1. cd /d D:\demo\code2026\springboot
echo 2. mvn clean package -DskipTests
echo 3. cd ../deployment\docker
echo 4. docker-compose up -d --build
pause