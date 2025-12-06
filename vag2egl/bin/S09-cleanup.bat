echo off

rem ********************************************
rem * change parms in bin\S0A-ProjectSetup.cmd file
rem ********************************************
call bin\S0A-ProjectSetup.cmd
if %V2EPARM_TRACE%. == . echo off
if not %V2EPARM_TRACE%. == . echo on

del  %V2EPARM_REPOSLOC%\%V2EPARM_PROJECT%\EGLSource\callapl.txt
del  %V2EPARM_REPOSLOC%\%V2EPARM_PROJECT%\EGLSource\VagSql.fdb
del  %V2EPARM_REPOSLOC%\%V2EPARM_PROJECT%\EGLSource\VagSql.ref
del  %V2EPARM_REPOSLOC%\%V2EPARM_PROJECT%\EGLSource\VagSql.fld
del  %V2EPARM_REPOSLOC%\%V2EPARM_PROJECT%\EGLSource\imp /Q /S
rd   %V2EPARM_REPOSLOC%\%V2EPARM_PROJECT%\EGLSource\imp /Q /S


if %V2EPARM_WAIT_WHEN_END%.==NO. goto exit
pause
:exit
pause
