@if %V2EPARM_TRACE%. == . echo off
@if not %V2EPARM_TRACE%. == . echo on

rem set __opath=%path%
rem set path=G:\jdk1.1.8\bin;.;

rem ********************************************
rem * change parms in S0A-ProjectSetup.cmd file
rem ********************************************
rem DO NOT - call bin\S0A-ProjectSetup.cmd

set rdir=C:\VAG2Egl 

set classpath=%rdir%;%classpath%
rem no longer used ------ java Egl2Egl2 c:/SDP/workspace/bmsr3270Smc/EGLSource/imp %1%
if errorlevel 1 goto konverteringsfejl
rem set path=%__opath%
rem set __opath=
%V2EPARM_REPOSLOC%\%V2EPARM_PROJECT%\bmsr3270Smc\EGLSource\imp\mapg-*.egl
goto end
:konverteringsfejl
echo ABEND: The conversion failed
if %V2EPARM_WAIT_WHEN_END%.==NO. goto exit
pause
:exit
:end
rem
