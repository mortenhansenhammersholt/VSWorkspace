@echo off

:S00
set programname=S00-Vag2Egl2.cmd
echo will call %programname%
call %programname%
if %V2EPARM_RC%.==0. goto S01
echo %programname% exited with errormsg %V2EPARM_ERRORMSG%
goto exit

:S01
set programname=S01-buildWDzimport.cmd
echo will call %programname%
call %programname%
if %V2EPARM_RC%.==0. goto S02
echo %programname% exited with errormsg %V2EPARM_ERRORMSG%
goto exit

:S02
set programname=S02-modifyWDzimport.vbs
echo will call %programname%
call %programname%
if %V2EPARM_RC%.==0. goto S03
echo %programname% exited with errormsg %V2EPARM_ERRORMSG%
goto exit

:S03
set programname=S03-WDzImport.cmd
echo will call %programname%
call %programname%
if %V2EPARM_RC%.==0. goto S04
echo %programname% exited with errormsg %V2EPARM_ERRORMSG%
goto exit

:S04
set programname=S04-build-fdb-xref.bat
echo will call %programname%
call %programname%
if %V2EPARM_RC%.==0. goto S05
echo %programname% exited with errormsg %V2EPARM_ERRORMSG%
goto exit

:S05
set programname=S05-correct-fdb.bat
echo will call %programname%
call %programname%
if %V2EPARM_RC%.==0. goto S06
echo %programname% exited with errormsg %V2EPARM_ERRORMSG%
goto exit

:S06
set programname=S06-copyEGLfiles.bat
echo will call %programname%
call %programname%
if %V2EPARM_RC%.==0. goto S07
echo %programname% exited with errormsg %V2EPARM_ERRORMSG%
goto exit

:S07

ECHO TEMP SOLUTION - RUN 7 AND 9 MANUALLY
GOTO EXIT

set programname=S07-movetoSQL.bat
echo will call %programname%
call %programname%
if %V2EPARM_RC%.==0. goto S09
echo %programname% exited with errormsg %V2EPARM_ERRORMSG%
goto exit

rem There is no S08 

:S09
set programname=S09-cleanup.bat
echo will call %programname%
call %programname%
if %V2EPARM_RC%.==0. goto exit
echo %programname% exited with errormsg %V2EPARM_ERRORMSG%
goto exit

echo S-all will exit
:exit