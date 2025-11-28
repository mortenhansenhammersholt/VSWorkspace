# Copilot Instructions for VAG2EGL Migration Utility

## Project Overview

**VAG2EGL** is a legacy system migration utility that converts IBM VAG (COBOL/VS) applications to IBM EGL (Enterprise Generation Language) format. This is a complex ETL (Extract-Transform-Load) pipeline written in Java with batch scripting, converting database schema definitions (.esf files) and compiled applications.

The codebase bridges Windows batch scripts (S00-S09 orchestration), Java transformation tools, and IBM EGL/SQL migration logic. **Work primarily in Java** for core logic; batch scripts are orchestration wrappers.

## Architecture & Data Flow

### Four-Stage Migration Pipeline

The project executes **9 sequential stages** (S00-S09, no S08), each with specific responsibilities:

1. **S00 (Vag2Egl2)** - Initialize project and prepare workspace
2. **S01 (buildWDzimport)** - Parse ESF files, build import commands dynamically
3. **S02 (modifyWDzimport)** - VBScript post-processing of generated commands
4. **S03 (WDzImport)** - Execute the generated import commands
5. **S04 (build-fdb-xref)** - Build cross-reference tables via `VagSql.java`
6. **S05 (correct-fdb)** - Correct database references via `Egl2Egl3.java`
7. **S06 (copyEGLfiles)** - Copy and refactor EGL imports via `Egl2Egl.java`
8. **S07 (movetoSQL)** - Convert SQL mappings via `Egl2Egl4.java` (MANUAL step)
9. **S09 (cleanup)** - Delete temporary files and artifacts

**Critical insight**: Run `S-all.bat` for the full pipeline, but S07-S09 are currently manual; check `S-all.bat` line 56.

### Component Interactions

```
DSV/                                  Input source
├── *.esf files                        (Enterprise definition files)
├── cm/ (common modules)
├── table/ (SQL/database schemas)
└── mapg/ (mapping files)

S0A-ProjectSetup.cmd ─────────────── Central configuration hub
                                      All Sxx scripts source this

Java Transformation Chain:
VagSql.java                           Builds reference tables (VagSql.fdb, VagSql.ref)
  ↓ (Output files loaded by subsequent steps)
Egl2Egl3.java                         Corrects FDB references (processes i-*.egl files)
  ↓ (Processes output from S04)
Egl2Egl.java                          Adds module-specific imports (main refactoring)
  ↓ (Processes imp/ directory)
Egl2Egl4.java                         Converts SQL field definitions (creates SQL files)
```

## Critical Configuration

**File: `S0A-ProjectSetup.cmd`** – All dynamic paths/project names originate here:

```batch
set V2EPARM_MODULE=dsv              # App group name (lowercase)
set V2EPARM_MODULEPFX=DSV            # App group name (UPPERCASE)
set V2EPARM_PROJECT=DSV              # EGL project name
set V2EPARM_CUSTOMER=DSV             # Customer identifier
set V2EPARM_REPOSLOC=C:\SDP\workspace  # Repository base path (NO trailing slash)
```

When fixing scripts/paths: **Always update `S0A-ProjectSetup.cmd` first**. All batch scripts source this via `call S0A-ProjectSetup.cmd`.

## Key Patterns & Conventions

### 1. Module Import Mapping (Hardcoded)

Java classes maintain static import arrays mapped to module acronyms:

```java
// Egl2Egl.java / Egl2Egl4.java pattern:
static String importML_cm = "import ml.cm.*;";
static String importGEN_cm = "import gen.cm.*;";
// ... (ml, gen, kre, deb, inv, faa, cli, smc, nyk, ba modules)
```

Module name is derived from directory path: `impTab[] = {"ml-cm", "gen-cm", "kre-cm", ...}` (Egl2Egl.java:12). When adding new modules, add new static imports AND update these arrays.

### 2. File Processing Pattern

All Java tools follow this structure:
- Accept directory path as first argument
- Recursively list files in directory
- Process each file with specific naming conventions
- Output transformed files to target directory

Example (Egl2Egl3.java):
```java
listFiles(fileName);  // Recursive directory scan
// Processes only i-*.egl files (line 55: if (fileNames[i].startsWith("i-")))
```

### 3. Error Handling & Exit Codes

Batch scripts use error level checking (Windows-specific):
```batch
call %programname%
if %V2EPARM_RC%.==0. goto SUCCESS   # Check RC=0 for success
echo %programname% exited with %V2EPARM_ERRORMSG%
```

**Always set `V2EPARM_RC` and `V2EPARM_ERRORMSG`** in batch scripts before exiting.

### 4. String Parsing in Java

Heavy use of `lastIndexOf()` and substring manipulation to extract components:

```java
// Egl2Egl4.java pattern (line 56-58):
ix1 = dirName.lastIndexOf('/');
ix2 = dirName.indexOf('-', ix1);
prefix = dirName.substring(ix1+1, ix2).toLowerCase();
```

Avoid regex unless performance-critical; this codebase predates Java regex conventions.

### 5. Batch Directory Scanning

Creates temp file listing via `dir` command (slow but reliable):

```batch
dir %V2EPARM_MODULE%\cm\*.esf /A:-D /B /O:N > %temp%\dir.txt
for /F "eol=; tokens=1,2* delims==" %%i in (%temp%\dir.txt) do (
    echo java ... %%i >> W01-O01-WDzImport.cmd
)
```

Used because S01 generates command files dynamically; each ESF file gets a Java import invocation appended to `W01-O01-WDzImport.cmd`.

## File Organization

```
vag2egl/
├── S-all.bat                 Main orchestrator (execute this)
├── S0A-ProjectSetup.cmd      CENTRAL CONFIG (modify for new projects)
├── S00-S09 scripts           Individual pipeline stages
├── src/
│   ├── Egl2Egl.java         Adds module imports (S06)
│   ├── Egl2Egl2.java        [Legacy/variant]
│   ├── Egl2Egl3.java        Corrects FDB references (S05)
│   ├── Egl2Egl4.java        SQL field conversion (S07)
│   ├── VagSql.java          Cross-reference builder (S04)
│   └── Vag2Egl2.java        [Legacy/variant]
├── DSV/                      Example project (application group)
│   ├── DSV.esf              Master schema file
│   ├── cm/                  Common modules (*.esf files)
│   ├── mapg/                Mapping definitions
│   └── table/               Table schemas
├── VagSql.fdb               Cross-ref database (output, line 3)
├── VagSql.ref               Reference table (output)
└── prep/                    Temporary prep directory
```

## Debugging & Workflow

### Running the Migration

```powershell
# Option 1: Full automated pipeline (up to S06)
cd c:\Users\morte\VSWorkspace\vag2egl
S-all.bat

# Option 2: Single stage (e.g., S04)
S04-build-fdb-xref.bat

# Option 3: Run Java tools directly
java -cp src vag2egl.VagSql DSV
java -cp src vag2egl.Egl2Egl c:\path\EGLSource\imp DSV
```

### Checking Output

- **After S04**: `VagSql.fdb` and `VagSql.ref` should exist and contain cross-references
- **After S05**: `i-*.egl` files in imp/ directory should have corrected imports
- **After S06**: EGL files should have module-specific imports (ml.cm.*, gen.cm.*, etc.)
- **After S07**: SQL files should exist in the sql/ directory

### Common Issues

1. **Missing import statement**: Check module acronym in `Egl2Egl.java` static imports
2. **Wrong file not found error**: Verify `V2EPARM_REPOSLOC` path in `S0A-ProjectSetup.cmd`
3. **"i-" files not processed**: Egl2Egl3 only processes files starting with "i-"; check S05 output
4. **Path issues in Java**: Java uses forward slashes ("/") in paths; batch uses backslashes ("\\")

## Conventions for Code Changes

- **Java package**: Always `package vag2egl;`
- **File I/O**: Use `FileInputStream`/`FileOutputStream` (pre-NIO style)
- **Imports needed**: Always add `java.io.*` and `java.util.*`
- **Batch error handling**: Set both `V2EPARM_RC` (0=success) and `V2EPARM_ERRORMSG`
- **Path variables**: Use forward slashes in Java, backslashes in batch; use `%V2EPARM_REPOSLOC%` family of variables
- **Exit codes**: Java tools use `System.exit(0)` for success, non-zero for failure

## External Dependencies

- **IBM EGL Tools**: Invoked via `com.ibm.etools.egl.internal.vagenmigration.batch.VGMIG` class (hardcoded in S01, S03)
- **Java version**: Commented references suggest Java 1.1.8 era; code runs on modern JVM but maintains compatibility patterns
- **Operating System**: Windows-only (batch scripts, hardcoded paths like `C:\SDP\workspace`)

