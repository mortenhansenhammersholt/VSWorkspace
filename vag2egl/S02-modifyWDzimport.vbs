Option Explicit
Dim sTmp,sNewFile,sSourceFile
dim fs,objSourceFile,objDestFile

	sSourceFile = "W01-O01-WDzImport.cmd"
	sNewFile    = "W02-O01-WDzImport.cmd"

	Set fs = CreateObject("Scripting.FileSystemObject")

	Set objSourceFile = fs.OpenTextFile (sSourceFile, 1)

	Set objDestFile = fs.OpenTextFile(sNewFile, 2, True)

	Do Until objSourceFile.AtEndOfStream
    		sTmp = objSourceFile.Readline
		sTmp = replace(sTmp,"esf.egl","egl",1,-1,1)
		sTmp = replace(sTmp,"¤",">",1,-1,1)
		objDestFile.Writeline sTmp
	Loop

	set objSourceFile = nothing
	set objDestFile = nothing
	set fs = nothing