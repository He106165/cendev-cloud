rem @echo off
echo ********************************
echo.
echo [��Ϣ] ���� cendev-cloud ȫ�����̡�
echo.
echo ********************************
cd %~dp0bin
cd
call s1-eureka.bat
call s2-config.bat
call s3-gateway.bat
call s4-system.bat
call s5-auth.bat
call s6-gen.bat
rem call s7-dfs.bat