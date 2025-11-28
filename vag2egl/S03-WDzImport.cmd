rem /* VAG to EGL Migration Utility - v.3.0
rem /* (C) Copyright IBM Denmark A/S 2009
rem /*
if %V2EPARM_TRACE%. == . echo off
if not %V2EPARM_TRACE%. == . echo on
echo ******************************************************** >> W03-O01-WDzImport.all
echo ******************************************************** >> W03-O01-WDzImport.all
type W03-O01-WDzImport.log >> W03-O01-WDzImport.all

call W02-O01-WDzImport.cmd
cd c:\vag2egl\