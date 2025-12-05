@echo off

echo Building WDZImport command
echo please wait.... I'm working....
set temp=prep
if exist %temp%\. goto process1
md %temp%
:process1
if exist temp\. goto process2
md temp
:process2
set rdir=c:\vag2egl
rem ********************************************
rem * change parms in bin\S0A-ProjectSetup.cmd file
rem ********************************************
call bin\S0A-ProjectSetup.cmd
if %V2EPARM_TRACE%. == . echo off
if not %V2EPARM_TRACE%. == . echo on

rem **************************************************

   echo echo W01-O01-WDzImport.cmd called > W01-O01-WDzImport.cmd
   echo if %V2EPARM_TRACE%. == . echo off >> W01-O01-WDzImport.cmd
   echo if not %V2EPARM_TRACE%. == . echo on >> W01-O01-WDzImport.cmd
   echo set rdir=%rdir% >> W01-O01-WDzImport.cmd
   echo set path=%V2EPARM_SDPHOME%\jdk\jre\bin;%%PATH%% >> W01-O01-WDzImport.cmd
rem   echo set classpath=%V2EPARM_SDPHOME%\runtimes\base_v61\lib\startup.jar;c:\SDP\plugins\org.eclipse.equinox.launcher_1-0-101.R34x_v20080819.jar;c:\SDPShared\plugins\com.ibm.etools.egl.vagenmigration_7.5.1.RFB_20081106_1700.jar; >> W01-O01-WDzImport.cmd
remDSV   echo set classpath=%V2EPARM_SDPHOME%\plugins\org.eclipse.equinox.launcher_1.0.101.R34x_v20081125.jar;%V2EPARM_SDPSHAREDHOME%\plugins\com.ibm.etools.egl.vagenmigration_7.5.1.v20090612_1343.jar; >> W01-O01-WDzImport.cmd
   echo set classpath=%V2EPARM_SDPHOME%\plugins\org.eclipse.equinox.launcher_1.4.0.v20161219-1356.jar;%V2EPARM_SDPSHAREDHOME%\plugins\com.ibm.etools.egl.vagenmigration_7.5.105.v20210727_0640.jar; >> W01-O01-WDzImport.cmd
   echo cd %V2EPARM_SDPHOME% >> W01-O01-WDzImport.cmd
   echo echo init log... � %rdir%\W03-O01-WDzImport.log >> W01-O01-WDzImport.cmd

   echo if exist %V2EPARM_REPOSLOC%\%V2EPARM_PROJECT%\EGLSource\imp\. goto imp_done   >> W01-O01-WDzImport.cmd
   echo md %V2EPARM_REPOSLOC%\%V2EPARM_PROJECT%\EGLSource\imp  >> W01-O01-WDzImport.cmd
   echo :imp_done  >> W01-O01-WDzImport.cmd

echo building %V2EPARM_MODULE%\cm\item\*.esf
if exist %V2EPARM_MODULE%\cm\item\*.esf goto process_item
echo nothing to process
goto check_record
:process_item
   dir %V2EPARM_MODULE%\cm\item\*.esf /A:-D /B /O:N > %temp%\dir.txt

   for /F "eol=; tokens=1,2* delims==" %%i in (%temp%\dir.txt) do (
     echo java com.ibm.etools.egl.internal.vagenmigration.batch.VGMIG -importFile %rdir%\%V2EPARM_MODULEPFX%\cm\item\%%i -eglFile %V2EPARM_REPOSLOC%\%V2EPARM_PROJECT%\EGLSource\imp\cm-%%i.egl -data %V2EPARM_REPOSLOC% -package imp -overwrite �� %rdir%\W03-O01-WDzImport.log >> W01-O01-WDzImport.cmd
     )

:check_record
echo building %V2EPARM_MODULE%\cm\record\*.esf
if exist %V2EPARM_MODULE%\cm\record\*.esf goto process_record
echo nothing to process
goto check_popup
:process_record
   dir %V2EPARM_MODULE%\cm\record\*.esf /A:-D /B /O:N > %temp%\dir.txt

   for /F "eol=; tokens=1,2* delims==" %%i in (%temp%\dir.txt) do (
     echo java com.ibm.etools.egl.internal.vagenmigration.batch.VGMIG -importFile %rdir%\%V2EPARM_MODULEPFX%\cm\record\%%i -eglFile %V2EPARM_REPOSLOC%\%V2EPARM_PROJECT%\EGLSource\imp\cm-%%i.egl -data %V2EPARM_REPOSLOC% -package imp -overwrite �� %rdir%\W03-O01-WDzImport.log >> W01-O01-WDzImport.cmd
     )

:check_popup
echo building %V2EPARM_MODULE%\cm\popup\*.esf
if exist %V2EPARM_MODULE%\cm\popup\*.esf goto process_popup
echo nothing to process
goto fileRename_prepare
:process_popup
   dir %V2EPARM_MODULE%\cm\popup\*.esf /A:-D /B /O:N > %temp%\dir.txt

   for /F "eol=; tokens=1,2* delims==" %%i in (%temp%\dir.txt) do (
     echo java com.ibm.etools.egl.internal.vagenmigration.batch.VGMIG -importFile %rdir%\%V2EPARM_MODULEPFX%\cm\popup\%%i -eglFile %V2EPARM_REPOSLOC%\%V2EPARM_PROJECT%\EGLSource\imp\%%i.egl -data %V2EPARM_REPOSLOC% -package imp -overwrite �� %rdir%\W03-O01-WDzImport.log >> W01-O01-WDzImport.cmd
     )

:fileRename_prepare
rem echo call %rdir%\T03-01-fileRenamePrepare.cmd %V2EPARM_MODULEPFX%*G mapg-%V2EPARM_MODULEL% >> W01-O01-WDzImport.cmd
rem echo call %rdir%\W03-O02-fileRename.cmd  >> W01-O01-WDzImport.cmd
rem echo call %rdir%\T03-02-extractMapg.bat all >> W01-O01-WDzImport.cmd

rem ------------- if need for special handling ----------------start
rem echo building special ones
rem echo java com.ibm.etools.egl.internal.vagenmigration.batch.VGMIG -importFile %rdir%\%V2EPARM_MODULEPFX%\table\F9ERT.esf -eglFile %V2EPARM_REPOSLOC%\%V2EPARM_PROJECT%\EGLSource\imp\F9ERT.egl -data %V2EPARM_REPOSLOC% -package imp -overwrite �� %rdir%\W03-O01-WDzImport.log >> W01-O01-WDzImport.cmd
rem echo ren %V2EPARM_REPOSLOC%\%V2EPARM_PROJECT%\EGLSource\imp\F9ERT.egl %V2EPARM_MODULE%-cm-F9ERT.egl >> W01-O01-WDzImport.cmd
rem ------------- if need for special handling ----------------end

echo building %V2EPARM_MODULE%\cm\*.esf
if exist %V2EPARM_MODULE%\cm\*.esf goto process_cm
echo nothing to process
goto check_table
:process_cm
   dir %V2EPARM_MODULE%\cm\*.esf /A:-D /B /O:N > %temp%\dir.txt

   for /F "eol=; tokens=1,2* delims==" %%i in (%temp%\dir.txt) do (
     echo java com.ibm.etools.egl.internal.vagenmigration.batch.VGMIG -importFile %rdir%\%V2EPARM_MODULEPFX%\cm\%%i -eglFile %V2EPARM_REPOSLOC%\%V2EPARM_PROJECT%\EGLSource\imp\cm-%%i.egl -data %V2EPARM_REPOSLOC% -package imp -overwrite �� %rdir%\W03-O01-WDzImport.log >> W01-O01-WDzImport.cmd
     )

:check_table
echo building %V2EPARM_MODULE%\table\*.esf
if exist %V2EPARM_MODULE%\table\*.esf goto process_table
echo nothing to process
goto check_mapg
:process_table
   dir %V2EPARM_MODULE%\table\*.esf /A:-D /B /O:N > %temp%\dir.txt

   for /F "eol=; tokens=1,2* delims==" %%i in (%temp%\dir.txt) do (
     echo java com.ibm.etools.egl.internal.vagenmigration.batch.VGMIG -importFile %rdir%\%V2EPARM_MODULEPFX%\table\%%i -eglFile %V2EPARM_REPOSLOC%\%V2EPARM_PROJECT%\EGLSource\imp\%%i.egl -data %V2EPARM_REPOSLOC% -package imp -overwrite �� %rdir%\W03-O01-WDzImport.log >> W01-O01-WDzImport.cmd
     )

:check_mapg
rem * 20090925 echo call %rdir%\T03-01-fileRenamePrepare.cmd %V2EPARM_MODULEPFX%* %V2EPARM_MODULEL% >> W01-O01-WDzImport.cmd
rem * 20090925 echo call %rdir%\W03-O02-fileRename.cmd  >> W01-O01-WDzImport.cmd


echo building %V2EPARM_MODULE%\mapg\*.esf
if exist %V2EPARM_MODULE%\mapg\*.esf goto process_mapg
echo nothing to process
goto check_esf
:process_mapg
   dir %V2EPARM_MODULE%\mapg\*.esf /A:-D /B /O:N > %temp%\dir.txt

   for /F "eol=; tokens=1,2* delims==" %%i in (%temp%\dir.txt) do (
     echo java com.ibm.etools.egl.internal.vagenmigration.batch.VGMIG -importFile %rdir%\%V2EPARM_MODULEPFX%\mapg\%%i -eglFile %V2EPARM_REPOSLOC%\%V2EPARM_PROJECT%\EGLSource\imp\%%i.egl -data %V2EPARM_REPOSLOC% -package imp -overwrite �� %rdir%\W03-O01-WDzImport.log >> W01-O01-WDzImport.cmd
     )

rem -------------- must be modified to match Customer - if needed -----------start
rem echo call %rdir%\T03-01-fileRenamePrepare.cmd A*G mapg-%V2EPARM_MODULEL% >> W01-O01-WDzImport.cmd
rem echo call %rdir%\W03-O02-fileRename.cmd  >> W01-O01-WDzImport.cmd
rem echo call %rdir%\T03-01-fileRenamePrepare.cmd D*G mapg-%V2EPARM_MODULEL% >> W01-O01-WDzImport.cmd
rem echo call %rdir%\W03-O02-fileRename.cmd  >> W01-O01-WDzImport.cmd
rem echo call %rdir%\T03-01-fileRenamePrepare.cmd F*G mapg-%V2EPARM_MODULEL% >> W01-O01-WDzImport.cmd
rem echo call %rdir%\W03-O02-fileRename.cmd  >> W01-O01-WDzImport.cmd
rem echo call %rdir%\T03-01-fileRenamePrepare.cmd K*G mapg-%V2EPARM_MODULEL% >> W01-O01-WDzImport.cmd
rem echo call %rdir%\W03-O02-fileRename.cmd  >> W01-O01-WDzImport.cmd
rem echo call %rdir%\T03-02-extractMapg.bat  >> W01-O01-WDzImport.cmd
rem -------------- must be modified to match Customer -----------end


echo building %V2EPARM_MODULE%\*.esf
if exist %V2EPARM_MODULE%\. goto process_esf
echo nothing to process
goto no_more_to_check
:process_esf
   dir %V2EPARM_MODULE%\*.esf /A:-D /B /O:N > %temp%\dir.txt

   for /F "eol=; tokens=1,2* delims==" %%i in (%temp%\dir.txt) do (
     echo java com.ibm.etools.egl.internal.vagenmigration.batch.VGMIG -importFile %rdir%\%V2EPARM_MODULEPFX%\%%i -eglFile %V2EPARM_REPOSLOC%\%V2EPARM_PROJECT%\EGLSource\imp\%%i.egl -data %V2EPARM_REPOSLOC% -package imp -overwrite �� %rdir%\W03-O01-WDzImport.log >> W01-O01-WDzImport.cmd
     )

:no_more_to_check
rem * 20090925 echo call %rdir%\T03-01-fileRenamePrepare.cmd %V2EPARM_MODULEPFX%* %V2EPARM_MODULEL% >> W01-O01-WDzImport.cmd
rem * 20090925 echo call %rdir%\W03-O02-fileRename.cmd  >> W01-O01-WDzImport.cmd

echo call %rdir%\T03-01-fileRenamePrepare.cmd * i-%V2EPARM_MODULE% >> W01-O01-WDzImport.cmd
echo call %rdir%\W03-O02-fileRename.cmd  >> W01-O01-WDzImport.cmd

if %V2EPARM_WAIT_WHEN_END%.==NO. goto exit
   echo pause >> W01-O01-WDzImport.cmd
pause
:exit