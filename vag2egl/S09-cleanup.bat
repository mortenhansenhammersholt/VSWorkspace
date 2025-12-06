@echo off
call "%~dp0bin\S09-cleanup.bat" %*
exit /b %errorlevel%
