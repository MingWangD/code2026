@echo off
echo 🚀 教育风险预警系统传统部署
echo ==========================================

REM 1. 检查Java环境
echo 1. 检查Java环境...
java -version
if errorlevel 1 (
    echo ❌ Java未安装或配置错误
    pause
    exit /b 1
)

REM 2. 检查MySQL
echo.
echo 2. 检查MySQL服务...
sc query MySQL80 | findstr "RUNNING"
if errorlevel 1 (
    echo ⚠️  MySQL服务未运行，正在启动...
    net start MySQL80
)

REM 3. 停止现有应用
echo.
echo 3. 停止现有应用进程...
taskkill /F /IM java.exe /T 2>nul
timeout /t 3 /nobreak >nul

REM 4. 备份旧版本
echo.
echo 4. 备份旧版本...
if exist "deploy\app.jar" (
    if not exist "deploy\backup" mkdir "deploy\backup"
    move "deploy\app.jar" "deploy\backup\app_%date:~0,4%%date:~5,2%%date:~8,2%_%time:~0,2%%time:~3,2%.jar"
)

REM 5. 准备部署目录
echo.
echo 5. 准备部署目录...
if not exist "deploy" mkdir "deploy"
if not exist "deploy\logs" mkdir "deploy\logs"
if not exist "deploy\config" mkdir "deploy\config"

REM 6. 复制Jar包
echo.
echo 6. 复制应用Jar包...
copy "springboot\target\springboot-0.0.1-SNAPSHOT.jar" "deploy\app.jar"

REM 7. 创建配置文件
echo.
echo 7. 创建生产环境配置...
(
echo # 生产环境配置
echo spring.datasource.url=jdbc:mysql://localhost:3306/code2026?useUnicode=true^&characterEncoding=utf8^&serverTimezone=Asia/Shanghai
echo spring.datasource.username=root
echo spring.datasource.password=Admin@123
echo.
echo # 应用配置
echo server.port=8080
echo server.servlet.context-path=/
echo.
echo # 日志配置
echo logging.file.name=deploy/logs/app.log
echo logging.level.com.example=INFO
echo logging.level.org.springframework=WARN
) > deploy\config\application-prod.properties

REM 8. 启动应用
echo.
echo 8. 启动应用...
cd deploy
echo 启动命令: java -jar app.jar --spring.config.additional-location=config/application-prod.properties
start "教育风险预警系统" cmd /c "java -jar app.jar --spring.config.additional-location=config/application-prod.properties ^& pause"

REM 9. 等待启动
echo.
echo 9. 等待应用启动（30秒）...
timeout /t 30 /nobreak >nul

REM 10. 测试
echo.
echo 10. 测试应用健康状态...
curl -s http://localhost:8080/api/health/ping
if errorlevel 1 (
    echo ❌ 应用启动失败，请查看日志
) else (
    echo ✅ 应用启动成功！
)

echo.
echo ==========================================
echo 📊 部署完成！
echo 🌐 访问地址: http://localhost:8080
echo 📍 健康检查: http://localhost:8080/api/health/ping
echo 📍 监控面板: http://localhost:8080/api/monitor/performance
echo 📂 日志目录: D:\demo\code2026\deploy\logs
echo ==========================================
echo.
pause