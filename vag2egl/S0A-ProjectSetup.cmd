rem This program should be called from all Sxx*.cmd/.bat files needing special 
rem *********************************************************
rem ** Application group parameters
rem **
rem ** These parameters should be configured for every
rem ** application group, you work with. I.e., every time you
rem ** begin migrating a new application group, you should 
rem ** define these values.
rem *********************************************************

rem ** V2EPARM_MODULE should contain the application group name, eg. anc (lowercase)
set V2EPARM_MODULE=dsv

rem ** V2EPARM_MODULEPFX should contain the application group name, eg. ANC (UPPERCASE)
rem ** Typically an uppercase version of V2EPARM_MODULE
set V2EPARM_MODULEPFX=DSV



rem *********************************************************
rem ** EGL Project definition parameters
rem **
rem ** These parameters should be changed each time if individual EGL projects are needed
rem *********************************************************

rem ** V2EPARM_PROJECT should contain the EGL project name, eg. KKI (UPPERCASE)
set V2EPARM_PROJECT=DSV

rem *********************************************************
rem ** Derived application group parameters
rem **
rem ** These parameters are normally derived version of
rem ** the above parameters. Should normally be left as is.
rem *********************************************************

rem ** V2EPARM_MODULEL should contain the application group name, eg. anc (lowercase)
rem ** Typically the same as V2EPARM_MODULE
set V2EPARM_MODULEL=%V2EPARM_MODULE%

rem ** V2EPARM_ESFFILE should contain the application group name, eg. ANC (UPPERCASE)
rem ** Typically the same as V2EPARM_MODULE
set V2EPARM_ESFFILE=%V2EPARM_MODULEPFX%


rem *********************************************************
rem ** One time definition parameters
rem **
rem ** These parameters should only be defined one time.
rem *********************************************************

rem ** V2EPARM_CUSTOMER should contain the customer name, eg. KKI (UPPERCASE)
set V2EPARM_CUSTOMER=DSV


rem ** V2EPARM_REPOSLOC should contain the repository location
rem ** It should be a fully qualified path name and it should NOT end with a backslash
set V2EPARM_REPOSLOC=C:\SDP\workspace

rem ** V2EPARM_REPOSLOCFS same as V2EPARM_REPOSLOC but with forward slashes 
rem ** It should be a fully qualified path name and it should NOT end with a slash
set V2EPARM_REPOSLOCFS=C:/SDP/workspace

rem ** V2EPARM_ESFFILELOC should contain the ESF file location
rem ** It should be a fully qualified path name and it should NOT end with a backslash
REM set V2EPARM_ESFFILELOC=C:\SDP\workspaceKKI\KKI\EGLSource\KKIESF
set V2EPARM_ESFFILELOC=C:\Vag2egl

rem ** V2EPARM_SDPHOME should contain the path the your SDP directory
rem ** It should be a fully qualified path name and it should NOT end with a backslash
set V2EPARM_SDPHOME=C:\SDP

rem ** V2EPARM_SDPSHAREDHOME should contain the path the your SDPShared directory
rem ** It should be a fully qualified path name and it should NOT end with a backslash
set V2EPARM_SDPSHAREDHOME=c:\RBD\IBMIMShared

rem ** V2EPARM_WAIT_WHEN_END defines program behaviour - when set to NO, the programs
rem ** executed will not pause at end. Any other value or not defined at all makes
rem ** program pause at end.
set V2EPARM_WAIT_WHEN_END=YES

rem ** V2EPARM_RC V2EPARM_ERRORMSG and should just be defined. It is for error processing.
set V2EPARM_RC=0
set V2EPARM_ERRORMSG=.

rem ** V2EPARM_TRACE .. when defined to any value, echo will be on in all programs
set V2EPARM_TRACE=aaa

