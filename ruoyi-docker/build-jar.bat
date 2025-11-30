@echo off
echo ========================================
echo 重新打包后端jar包
echo ========================================
echo.

cd /d %~dp0\..

echo [1/3] 清理并打包项目...
call mvn clean package -Dmaven.test.skip=true

if %errorlevel% neq 0 (
    echo [错误] 打包失败！
    pause
    exit /b 1
)

echo.
echo [2/3] 复制jar包到docker目录...
if exist "ruoyi-admin\target\ruoyi-admin.jar" (
    copy /Y "ruoyi-admin\target\ruoyi-admin.jar" "ruoyi-docker\backend\jar\ruoyi-admin.jar"
    echo [成功] jar包已复制到 ruoyi-docker\backend\jar\
) else (
    echo [错误] 找不到打包后的jar文件！
    pause
    exit /b 1
)

echo.
echo [3/3] 完成！
echo.
echo 现在可以运行: cd ruoyi-docker ^&^& docker-compose up -d --build
echo.
pause

