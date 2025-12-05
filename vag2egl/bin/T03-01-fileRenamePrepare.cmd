@if %V2EPARM_TRACE%. == . echo off
@if not %V2EPARM_TRACE%. == . echo on
echo T03-01-fileRenamePrepare.cmd called
echo Building prepareCopy command
echo please wait.... I'm working....

set rdir=%~dp0
rem set
set temp=prep
set mask=%1%
set pref=%2%

rem ********************************************
rem * change parms in S0A-ProjectSetup.cmd file
rem ********************************************
call "%~dp0S0A-ProjectSetup.cmd"


echo echo W03-O02-fileRename.cmd called > "%~dp0W03-O02-fileRename.cmd"
rem echo echo on >> %rdir%\W03-O02-fileRename.cmd

echo building files
   dir %V2EPARM_REPOSLOC%\%V2EPARM_PROJECT%\EGLSource\imp\%mask%.egl /A:-D /B /O:N > "%~dp0%temp%\dir.txt"

   for /F "eol=; tokens=1,2* delims==" %%i in ("%~dp0%temp%\dir.txt") do (
     echo if exist %V2EPARM_REPOSLOC%\%V2EPARM_PROJECT%\EGLSource\imp\%pref%-%%i del %V2EPARM_REPOSLOC%\%V2EPARM_PROJECT%\EGLSource\imp\%pref%-%%i  >> "%~dp0W03-O02-fileRename.cmd"
     echo ren %V2EPARM_REPOSLOC%\%V2EPARM_PROJECT%\EGLSource\imp\%%i %pref%-%%i >> "%~dp0W03-O02-fileRename.cmd"
     )

rem    echo pause >> %rdir%\W03-O02-fileRename.cmd

if %V2EPARM_WAIT_WHEN_END%.==NO. goto exit
rem pause
:exit
