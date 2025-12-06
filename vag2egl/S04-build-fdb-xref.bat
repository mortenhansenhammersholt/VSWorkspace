@echo off
call "%~dp0bin\S04-build-fdb-xref.bat" %*
exit /b %errorlevel%
