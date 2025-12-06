@echo off
rem /* VAG to EGL Migration Utility - v.3.0
rem /* (C) Copyright IBM Denmark A/S 2009
rem /*
rem set __opath=%path%
rem set path=G:\jdk1.1.8\bin;.;
rem ************************
rem * Remember to add Customer as second parameter - FMS/SMC/NYK/KKI/ADG
rem java Vag2Egl2 NYK NYK
rem java Vag2Egl2 ZCA KKI
rem java Vag2Egl2 CLO KKI > S00-Vag2Egl2.out
rem java Vag2Egl2 SMC SMC > S00-Vag2Egl2.out
rem java VAG2Egl2 NYK NYK > S00-Vag2Egl2.out
rem java Vag2Egl2 BA BA > S00-Vag2Egl2.out
rem ************************

rem ********************************************
rem * change parms in bin\S0A-ProjectSetup.cmd file
rem ********************************************
call bin\S0A-ProjectSetup.cmd
if %V2EPARM_TRACE%. == . echo off
if not %V2EPARM_TRACE%. == . echo on

rem ** Check if esf file exists in current directory 
if exist %V2EPARM_MODULEPFX%.esf goto process2
if exist %V2EPARM_ESFFILELOC%\%V2EPARM_MODULEPFX%.esf goto process1
echo ABEND: The file %V2EPARM_ESFFILELOC%\%V2EPARM_MODULEPFX%.esf could not be found.
goto end

rem ** Copy esf file from source to current directory
:process1
copy %V2EPARM_ESFFILELOC%\%V2EPARM_MODULEPFX%.esf .
echo File %V2EPARM_MODULEPFX%.esf copied from %V2EPARM_ESFFILELOC%

rem ** process esf file
:process2
time /T > tempfile.out
date /T >>tempfile.out
type tempfile.out > S00-Vag2Egl2.out
java vag2egl.Vag2Egl2 %V2EPARM_MODULEPFX% %V2EPARM_CUSTOMER% >> S00-Vag2Egl2.out
time /T > tempfile.out
date /T >>tempfile.out
type tempfile.out >> S00-Vag2Egl2.out

rem set path=%__opath%
rem set __opath=

:end
if %V2EPARM_WAIT_WHEN_END%.==NO. goto exit
pause
:exit
