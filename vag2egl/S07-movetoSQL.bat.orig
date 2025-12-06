@echo off
rem /* VAG to EGL Migration Utility - v.3.0
rem /* (C) Copyright IBM Denmark A/S 2009
rem /*
echo S07-movetoSQL.bat called
rem set __opath=%path%
rem set path=G:\jdk1.1.8\bin;.;

rem ********************************************
rem * change parms in bin\S0A-ProjectSetup.cmd file
rem ********************************************
call bin\S0A-ProjectSetup.cmd
if %V2EPARM_TRACE%. == . echo off
if not %V2EPARM_TRACE%. == . echo on

set sqldir=%V2EPARM_REPOSLOC%\%V2EPARM_PROJECT%\EGLSource\%V2EPARM_MODULE%\sql
if exist %sqldir% goto SQL_EXISTS
set V2EPARM_ERRORMSG=S07:md failed
md %sqldir%
if ERRORLEVEL 1 goto failme

:SQL_EXISTS
if exist %sqldir%\dummy.txt goto DUMMY_EXISTS
echo placeholder - this file should be present in directories if the directory would be empty >%sqldir%\dummy.txt

:DUMMY_EXISTS
if not exist VagSql.fld echo. > VagSql.fld
set V2EPARM_ERRORMSG=S07:copy failed
copy VagSql.fld %V2EPARM_REPOSLOC%\%V2EPARM_PROJECT%\EGLSource\*.*
if ERRORLEVEL 1 goto failme
set V2EPARM_ERRORMSG=S07:Egl2Egl4 failed
java vag2egl.Egl2Egl4 %V2EPARM_REPOSLOCFS%/%V2EPARM_PROJECT%/EGLSource/%V2EPARM_MODULE%-cm %V2EPARM_MODULE%
if ERRORLEVEL 1 goto failme
rem set path=%__opath%
rem set __opath=
if %V2EPARM_WAIT_WHEN_END%.==NO. goto exit
pause
goto exit

:failme
set V2EPARM_RC=1
echo ABEND: %V2EPARM_ERRORMSG%

:exit
echo S07-movetoSQL.bat end
