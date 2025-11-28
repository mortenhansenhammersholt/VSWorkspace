@echo off
echo S04-build-fdb-xref.bat called
rem set __opath=%path%
rem set path=G:\jdk1.1.8\bin;.;

rem ********************************************
rem * change parms in S0A-ProjectSetup.cmd file
rem ********************************************
call S0A-ProjectSetup.cmd
if %V2EPARM_TRACE%. == . echo off
if not %V2EPARM_TRACE%. == . echo on

rem java VagSql ML CLI FAA GEN KRE DEB INV BA
set V2EPARM_ERRORMSG=S04:java vag2egl.VagSql failed
java vag2egl.VagSql %V2EPARM_ESFFILE%
if ERRORLEVEL 1 goto failme

rem set path=%__opath%
rem set __opath=

set V2EPARM_ERRORMSG=S04:callapl.txt not found
copy callapl.txt %V2EPARM_REPOSLOC%\%V2EPARM_PROJECT%\EGLSource\*.*
if ERRORLEVEL 1 goto failme
set V2EPARM_ERRORMSG=S04:VagSql.fdb not found
copy VagSql.fdb %V2EPARM_REPOSLOC%\%V2EPARM_PROJECT%\EGLSource\*.*
if ERRORLEVEL 1 goto failme
set V2EPARM_ERRORMSG=S04:VagSql.ref not found
copy VagSql.ref %V2EPARM_REPOSLOC%\%V2EPARM_PROJECT%\EGLSource\*.*
if ERRORLEVEL 1 goto failme
goto exit

:failme
set V2EPARM_RC=1

:exit
echo S04-build-fdb-xref.bat end
