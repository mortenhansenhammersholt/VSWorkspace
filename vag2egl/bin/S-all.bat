@echo off

rem Orchestrator that runs other scripts from the bin directory.
rem Uses %~dp0 which expands to this script's directory (bin\)

:S00
set programname="%~dp0S00-Vag2Egl2.cmd"
echo will call %programname%
call %programname%
if %V2EPARM_RC%.==0. goto S01
echo %programname% exited with errormsg %V2EPARM_ERRORMSG%
goto exit

:S01
set programname="%~dp0S01-buildWDzimport.cmd"
echo will call %programname%
call %programname%
if %V2EPARM_RC%.==0. goto S02
echo %programname% exited with errormsg %V2EPARM_ERRORMSG%
goto exit

:S02
set programname="%~dp0S02-modifyWDzimport.vbs"
echo will call %programname%
call %programname%
if %V2EPARM_RC%.==0. goto S03
echo %programname% exited with errormsg %V2EPARM_ERRORMSG%
goto exit

:S03
set programname="%~dp0S03-WDzImport.cmd"
echo will call %programname%
call %programname%
if %V2EPARM_RC%.==0. goto S04
echo %programname% exited with errormsg %V2EPARM_ERRORMSG%
goto exit

:S04
set programname="%~dp0S04-build-fdb-xref.bat"
echo will call %programname%
call %programname%
if %V2EPARM_RC%.==0. goto S05
echo %programname% exited with errormsg %V2EPARM_ERRORMSG%
goto exit

:S05
set programname="%~dp0S05-correct-fdb.bat"
echo will call %programname%
call %programname%
if %V2EPARM_RC%.==0. goto S06
echo %programname% exited with errormsg %V2EPARM_ERRORMSG%
goto exit

:S06
set programname="%~dp0S06-copyEGLfiles.bat"
echo will call %programname%
call %programname%
if %V2EPARM_RC%.==0. goto S07
echo %programname% exited with errormsg %V2EPARM_ERRORMSG%
goto exit

:S07

ECHO TEMP SOLUTION - RUN 7 AND 9 MANUALLY
GOTO EXIT

set programname="%~dp0S07-movetoSQL.bat"
echo will call %programname%
call %programname%
if %V2EPARM_RC%.==0. goto S09
echo %programname% exited with errormsg %V2EPARM_ERRORMSG%
goto exit

rem There is no S08

:S09
set programname="%~dp0S09-cleanup.bat"
echo will call %programname%
call %programname%
if %V2EPARM_RC%.==0. goto exit
echo %programname% exited with errormsg %V2EPARM_ERRORMSG%
goto exit

echo S-all will exit
:exit
