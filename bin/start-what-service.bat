@echo off
if %1a==a echo ��������Ҫִ�еĹ��̵ļ�ƣ��磺eureka�� && goto end
echo.
echo [��Ϣ] ���� cendev-%1 ���̡�
echo.

cd %~dp0

cd ../cendev-service/cendev-%1/target

set JAVA_OPTS=-Xms256m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m

java -jar %JAVA_OPTS% cendev-%1-4.0.1-SNAPSHOT.jar

cd %~dp0

:end

pause