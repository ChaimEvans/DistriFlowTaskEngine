@echo off
echo 启动DistriFlowTaskEngine...
echo.
echo 请确保MySQL服务已启动，并且已创建数据库 distriflow_task_engine
echo.
echo 按任意键继续启动应用...
pause
echo.
echo 正在启动Spring Boot应用...
gradlew.bat bootRun
