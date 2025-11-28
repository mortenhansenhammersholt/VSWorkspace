@echo off
rem /* VAG to EGL Migration Utility - v.3.0
rem /* (C) Copyright IBM Denmark A/S 2009
rem /*
rem set __opath=%path%
rem set path=G:\jdk1.1.8\bin;.;

rem ********************************************
rem * change parms in S0A-ProjectSetup.cmd file
rem ********************************************
call S0A-ProjectSetup.cmd
if %V2EPARM_TRACE%. == . echo off
if not %V2EPARM_TRACE%. == . echo on

echo S06 calls: java vag2egl.Egl2Egl %V2EPARM_REPOSLOCFS%/%V2EPARM_PROJECT%/EGLSource/imp %V2EPARM_MODULE% %V2EPARM_PROJECT%
rem 2009.1010 moha
java vag2egl.Egl2Egl %V2EPARM_REPOSLOCFS%/%V2EPARM_PROJECT%/EGLSource/imp %V2EPARM_MODULE% %V2EPARM_CUSTOMER%

rem set path=%__opath%
rem set __opath=
if %V2EPARM_WAIT_WHEN_END%.==NO. goto exit
pause
:exit
