@echo off
rem /* VAG to EGL Migration Utility - v.3.0
rem /* (C) Copyright IBM Denmark A/S 2009
rem /*
echo S05-correct-fdb.bat called
rem set __opath=%path%
rem set path=G:\jdk1.1.8\bin;.;

rem ********************************************
rem * change parms in S0A-ProjectSetup.cmd file
rem ********************************************
call S0A-ProjectSetup.cmd
if %V2EPARM_TRACE%. == . echo off
if not %V2EPARM_TRACE%. == . echo on

set V2EPARM_ERRORMSG=S05:java vag2egl.Egl2Egl3 failed
java vag2egl.Egl2Egl3 %V2EPARM_REPOSLOCFS%/%V2EPARM_PROJECT%/EGLSource/imp 
if ERRORLEVEL 1 goto failme

rem set path=%__opath%
rem set __opath=
set V2EPARM_ERRORMSG=S05:del failed
del %V2EPARM_REPOSLOC%\%V2EPARM_PROJECT%\EGLSource\imp\i-*.*
if ERRORLEVEL 1 goto failme

if %V2EPARM_WAIT_WHEN_END%.==NO. goto exit
pause
goto exit

:failme
set V2EPARM_RC=1
echo ABEND: S05-correct-fdb.bat ends with message %V2EPARM_ERRORMSG%

:exit
echo S05-correct-fdb.bat end
:exit