package vag2egl;
/* VAG to EGL Migration Utility - v.3.0
 * (C) Copyright IBM Denmark A/S 2009
 */
import java.io.*;
import java.util.*; 

public class Egl2Egl4 {   

   static String   eglProject   = null;
   static String   inpDir   = null;
   static String   inpFile   = null;  
   static String   sqlDir   = null; 
   static String   oldDir   = null; 

   static String   prefix   = null; 
  
   static int      frPos    = 0; 
   static int      toPos    = 0; 

   static String[][] fldTab = new String[10000][2]; 
   static int      fldIx  = -1;
   static String   funcName = "";   
   static String   recName  = ""; 

   static PrintStream fpFld          = null;
   static String      fldNewFileId   = null;
   static String      fldOldFileId   = null;
   static String      fldTabFileId[] = new String[100000];  
   static int         fldTabFileIx   = -1; 
   static String rec1                = null;   

   static String importML_cm  = "import ml.cm.*;";
   static String importGEN_cm = "import gen.cm.*;";
   static String importDEB_cm = "import deb.cm.*;";
   static String importKRE_cm = "import kre.cm.*;";
   static String importINV_cm = "import inv.cm.*;";
   static String importFAA_cm = "import faa.cm.*;";
   static String importCLI_cm = "import cli.cm.*;";
   static String importSMC_cm = "import smc.cm.*;";
   static String importNYK_cm = "import nyk.cm.*;";
   static String importBA_cm = "import ba.cm.*;";

   //-----------------------------------------------------------
   public static void main(String[] args) {
     
      int ix1, ix2; 

      if (args.length < 1) {
         System.out.println("Enter directory name");
         System.exit(0);
      }            
      
      String dirName = args[0];

      eglProject = args[1].toUpperCase();
      System.out.println("eglProject=" + eglProject); // Test

      ix1    = dirName.lastIndexOf('/');
      ix2    = dirName.indexOf('-', ix1);
      if (ix2 < 0)
          ix2 = dirName.length();  
        
      inpDir = dirName.substring(0, ix1) + "/";
      inpFile = dirName.substring(ix1+1, dirName.length()).toUpperCase();
      prefix  = dirName.substring(ix1+1, ix2).toLowerCase();
      System.out.println("inpDir=" + inpDir + " inpFile=" + inpFile + " prefix=" + prefix);    
      
      String pathName;

      ix1    = dirName.indexOf('-');
      if (ix1 < 0)
          ix1 = dirName.length()-1; 
      pathName = dirName.substring(0, ix1+1) + "sql";
      if (checkPath(pathName)) {  
          sqlDir = pathName;  
      } else {                            
          System.exit(0);
      }
      pathName = dirName.substring(0, ix1+1) + "old";
      if (checkPath(pathName)) {  
          oldDir = pathName + "/"; 
      } else {                            
          System.exit(0);
      }

      //System.out.println("sqlDir=" + sqlDir + " oldDir=" + oldDir);

      try {
         loadRefFile();           
         listFiles(dirName);
      }
      catch (Exception e) {
         System.err.println(e);
      }
   }

   //-----------------------------------------------------------
   public static void listFiles(String dirName) 
         throws IOException, FileNotFoundException {
   
      File file = new File(dirName);    
     
      if (file.isDirectory()) {           
         String[] fileNames = file.list(); // <-- return fileNames
         if (fileNames != null) {
             for (int i=0; i<fileNames.length; i++)  {
                 //if (fileNames[i].startsWith("i-")) {
                 if (fileNames[i].indexOf("-TABLE") < 0) {
                     System.out.println(dirName + "  " + fileNames[i]);                 
                     readFiles(dirName, fileNames[i]);  
                 }              
            }
         }
      }
      
   }

   //-----------------------------------------------------------
   public static void readFiles(String dirName, String fileName)
         throws IOException, FileNotFoundException {    
    
      String inpName, inpRec, sqlMem, sqlName, fileExt;       
      boolean fnd   = false;
      boolean merge = false; 

      funcName = ""; 
      recName  = "";        
      fldNewFileId = "";
      rec1         = null;   
      int cnt      = 0; //MH01 2007-04-30
     
      inpName = dirName + "/" + fileName; 

      int ix1 = fileName.lastIndexOf('.');
      fileExt = fileName.substring(ix1, fileName.length());
      sqlMem  = fileName.substring(0, 4) + fileExt;        
      sqlName = sqlDir + "/" + fileName;
  
      //System.out.println("fileName=" + fileName + " member=" + sqlMem + " Name=" + sqlName);   
      //System.out.println("dirName=" + dirName + " sqlDir=" + sqlDir);   

      try {         
         BufferedReader  inpBuf  = new BufferedReader(new FileReader(inpName));                            
         while ((inpRec = inpBuf.readLine()) != null && !fnd) {                                 
            if (inpRec.startsWith("Function ")) {
                getFunctionName(inpRec);                              
            }
            if (!funcName.equals("")) {              
                 if (inpRec.indexOf("F9ERS()") >=0 )
                     fnd = true;
                 if (inpRec.indexOf("#sql") >=0 )
                     fnd = true;                 
            }   
            if (inpRec.startsWith("Record ")) {
                getRecordName(inpRec);              
            }
            if (!recName.equals("")) {
                 if (inpRec.indexOf("sqlRecord") >=0 )
                     fnd = true;
            }  
            if (inpRec.startsWith("DataItem ")) {
                if (checkMergeItem(inpRec))
                    merge = true; 
            } 
         }   
         inpBuf.close();  

         cnt = 0; // MH 2007-04-30 
         if (fnd) {
             inpBuf = new BufferedReader(new FileReader(inpName));       
             PrintStream sqlFile = new PrintStream(new FileOutputStream(sqlName));             
             while ((inpRec = inpBuf.readLine()) != null) {  
                if (rec1 != null) { //MH 2007-04-30
                    if (cnt == 0) { //MH 2007-04-30
                        cnt++;      //MH 2007-04-30
                    } else {        //MH 2007-04-30                               
                        sqlFile.println(inpRec);
                    }               //MH 2007-04-30 
                } else {                
                    rec1 = inpRec;                     
                    WriteImport(sqlFile);                 
                }               
             } 
             inpBuf.close(); 
             sqlFile.close();  
        
             File file1 = new File(inpName);             
             File file2 = new File(oldDir + fileName);            
             boolean success = file1.renameTo(file2);
             if (!success) {             
                System.out.println("Error rename " + inpName + " " + oldDir + fileName);
             }         
         }

         if (!fldNewFileId.equals("") && merge) {              

             inpBuf = new BufferedReader(new FileReader(inpName));                               

             cnt = 0; // MH 2007-04-30 
             while ((inpRec = inpBuf.readLine()) != null) {  
                if (rec1 == null) {
                    rec1 = inpRec; 
                } else {                                                     
                    if (cnt == 0) { //MH 2007-04-30
                        cnt++;      //MH 2007-04-30
                    } else {        //MH 2007-04-30                               
                       //WriteSqlField(sqlDir, inpRec);
                       WriteSqlField(dirName, inpRec);
                    }               //MH 2007-04-30 
                }
             } 
             inpBuf.close();  

             File file1 = new File(inpName);             
             File file2 = new File(oldDir + fileName);            
             boolean success = file1.renameTo(file2);
             if (!success) {             
                System.out.println("Error rename " + inpName + " " + oldDir + fileName);
             }             
         }
         
      }

      catch (FileNotFoundException e) {
         System.out.println("File not found: " + e);
         System.exit(0);
      } 
      catch (IOException e) {
         System.out.println("IO error: " + e);
         System.exit(0);
      }

   }
   
   //-----------------------------------------------------------
   public static void loadRefFile()
         throws IOException, FileNotFoundException {        
     
      String refName = inpDir + "VagSql.fld";   
      String refRec;  
      int pos1 = 0;    
      int pos2 = 0;
          
      fldTab = new String[10000][2];
      fldIx  = -1; 

      try {
         FileInputStream refFile =  new FileInputStream(refName);
         BufferedReader  refBuf  = new BufferedReader(new InputStreamReader(refFile));                
         while ((refRec = refBuf.readLine()) != null) { 
            fldIx++; 
            pos1 = refRec.indexOf(';');
            if (pos1 >= 0) {
                pos2 = refRec.indexOf(';', pos1+1);
                fldTab[fldIx][0] = refRec.substring(0, pos1); 
                fldTab[fldIx][1] = refRec.substring(pos1+1, pos2);
            }
         } 
          
         refFile.close();                              
      }

      catch (FileNotFoundException e) {
         System.out.println("File not found: " + e);
         System.exit(0);
      } 
      catch (IOException e) {
         System.out.println("IO error: " + e);
         System.exit(0);
      }

   }

   //-----------------------------------------------------------
   public static void getFunctionName(String rec) {   

      funcName = "";    
      
      int fpos = rec.indexOf(' ') + 1;
      int tpos = rec.indexOf('(') - 0;
      if (tpos > fpos) {
          funcName = rec.substring(fpos, tpos); 
          //System.out.println("func=" + rec.substring(fpos, tpos)); 
      }

   }

   //-----------------------------------------------------------
   public static void getRecordName(String rec) {   

      recName = "";    
      
      int fpos = rec.indexOf(' ') + 1;

      if (fpos >=0) {
          int tpos = rec.indexOf(' ', fpos+1);
          if (tpos > fpos) {
              recName = rec.substring(fpos, tpos); 
              //System.out.println("rec=" + rec.substring(fpos, tpos)); 
      }
      }

   }
      
   //-----------------------------------------------------------
   public static boolean checkMergeItem(String rec) { 

      int tpos = rec.indexOf(' ', 9);

      if (tpos < 0)
          return false; 
       
      String item = rec.substring(9, tpos); 

      for (int i=0;i<=fldIx;i++) {
           if (fldTab[i][1].equals(item)) {
               fldNewFileId = fldTab[i][0] + "-TABLE"; 
               return true;            
           }
      }

      return false; 

   }

   //-------------------------------------------------------
   public static void WriteSqlField(String dirName, String inpRec) {    

     boolean fnd = false;              
     
     if (fldTabFileIx >= 0) { 
         for (int i=0;i<=fldTabFileIx;i++) {
              if (!fldTabFileId[i].equals("") && fldTabFileId[i].equals(fldNewFileId)) {
                   fnd = true;
                   break;
              }
         }
     }                
 
     try {              
              
        if (fldNewFileId != fldOldFileId) {          
            if (fpFld != null)
                fpFld.close();                                          
            fpFld = new PrintStream(
                    new FileOutputStream(dirName + "/" + fldNewFileId + ".egl", true));
            if (!fnd && fldNewFileId!=null) {
                 fldTabFileIx+=1;               
                 fldTabFileId[fldTabFileIx]=fldNewFileId;                    
                 //WriteImport(fpFld); 
                 //MH 2007-04-30 fpFld.println(rec1);
                 WriteImportCM(fpFld); //MH 2007-04-30 
            }     
            fpFld.println(inpRec);
            fldOldFileId = fldNewFileId;
        } else {            
            fpFld.println(inpRec);
           
        }       
       
     }

     catch (FileNotFoundException e) {
        System.out.println("File not found: " + e);
        System.out.println("FileID=" + fldNewFileId + " Old fileID=" + fldOldFileId);
        System.out.println("Record=" + inpRec);
        System.exit(0);  
     } 
     catch (IOException e) {
        System.out.println("IO error: " + e);
        System.out.println("FileID=" + fldNewFileId + " Old fileID=" + fldOldFileId);
        System.out.println("Record=" + inpRec);
        System.exit(0);
     }      

   } 

   //-------------------------------------------------------
   public static void WriteImport(PrintStream printStr) { 

      printStr.println("package " + prefix + "-sql;");     

      System.out.println("eglProject=" + eglProject); // Test
      System.out.println("   inpFile=" + inpFile); // Test

      if (inpFile.startsWith(eglProject)) {  //MH                               
          System.out.println("   before"); // Test
          printStr.println("import " + prefix + ".cm.*;"); //MH
          System.out.println("   after"); // Test
      }
      System.out.println("   done"); // Test

      if (inpFile.equals("ML-CM")) {                                 
          printStr.println("import ml.cm.*;");
      }
      
      if (inpFile.equals("FAA-CM")) {                                
          printStr.println("import ml.cm.*;");
          printStr.println("import ml.sql.*;");
          printStr.println("import faa.cm.*;");
      }

      if (inpFile.equals("CLI-CM")) {                                
          printStr.println("import ml.cm.*;");
          printStr.println("import ml.sql.*;");
          printStr.println("import cli.cm.*;");
      }

      if (inpFile.equals("GEN-CM")) {                                
          printStr.println("import ml.cm.*;");
          printStr.println("import ml.sql.*;");
          printStr.println("import gen.cm.*;");
      }

      if (inpFile.equals("KRE-CM")) {                                
          printStr.println("import ml.cm.*;");
          printStr.println("import ml.sql.*;");
          printStr.println("import gen.cm.*;");
          printStr.println("import gen.sql.*;");
          printStr.println("import kre.cm.*;");
      }

      if (inpFile.equals("DEB-CM")) {                                
          printStr.println("import ml.cm.*;");
          printStr.println("import ml.sql.*;");
          printStr.println("import gen.cm.*;");
          printStr.println("import gen.sql.*;");
          printStr.println("import deb.cm.*;");
      }

      if (inpFile.equals("INV-CM")) {                                
          printStr.println("import ml.cm.*;");
          printStr.println("import ml.sql.*;");
          printStr.println("import gen.cm.*;");
          printStr.println("import gen.sql.*;");
          printStr.println("import deb.cm.*;");
          printStr.println("import deb.sql.*;");
          printStr.println("import inv.cm.*;");
      }

      if (inpFile.equals("SMC-CM")) {                                
          printStr.println("import ml.cm.*;");
          printStr.println("import ml.sql.*;");
          printStr.println("import gen.cm.*;");
          printStr.println("import gen.sql.*;");
          printStr.println("import deb.cm.*;");
          printStr.println("import deb.sql.*;");
          printStr.println("import kre.cm.*;");
          printStr.println("import kre.sql.*;");
          printStr.println("import inv.cm.*;");
          printStr.println("import inv.sql.*;");
          printStr.println("import smc.cm.*;");
      }

      if (inpFile.equals("NYK-CM")) {                                
          printStr.println("import ml.cm.*;");
          printStr.println("import ml.sql.*;");
          printStr.println("import gen.cm.*;");
          printStr.println("import gen.sql.*;");
          printStr.println("import kre.cm.*;");
          printStr.println("import kre.sql.*;");
          printStr.println("import nyk.cm.*;");
      }
      if (inpFile.equals("BA-CM")) {                                
          printStr.println("import ba.cm.*;");
          printStr.println("import ml.cm.*;");
          printStr.println("import ml.sql.*;");
          printStr.println("import gen.cm.*;");
          printStr.println("import gen.sql.*;");
          printStr.println("import kre.cm.*;");
          printStr.println("import kre.sql.*;");
          printStr.println("import cli.cm.*;");
          printStr.println("import cli.sql.*;");
      }

   }

   //-------------------------------------------------------
   public static void WriteImportCM(PrintStream printStr) { 

      printStr.println("package " + prefix + "-cm;");     

      if (inpFile.equals("ML-CM")) {                                 
          printStr.println("import ml.sql.*;");
      }
      
      if (inpFile.equals("FAA-CM")) {                                
          printStr.println("import ml.cm.*;");
          printStr.println("import ml.sql.*;");
          printStr.println("import faa.sql.*;");
      }

      if (inpFile.equals("CLI-CM")) {                                
          printStr.println("import ml.cm.*;");
          printStr.println("import ml.sql.*;");
          printStr.println("import cli.sql.*;");
      }

      if (inpFile.equals("GEN-CM")) {                                
          printStr.println("import ml.cm.*;");
          printStr.println("import ml.sql.*;");
          printStr.println("import gen.sql.*;");
      }

      if (inpFile.equals("KRE-CM")) {                                
          printStr.println("import ml.cm.*;");
          printStr.println("import ml.sql.*;");
          printStr.println("import gen.cm.*;");
          printStr.println("import gen.sql.*;");
          printStr.println("import kre.sql.*;");
      }

      if (inpFile.equals("DEB-CM")) {                                
          printStr.println("import ml.cm.*;");
          printStr.println("import ml.sql.*;");
          printStr.println("import gen.cm.*;");
          printStr.println("import gen.sql.*;");
          printStr.println("import deb.sql.*;");
      }

      if (inpFile.equals("INV-CM")) {                                
          printStr.println("import ml.cm.*;");
          printStr.println("import ml.sql.*;");
          printStr.println("import gen.cm.*;");
          printStr.println("import gen.sql.*;");
          printStr.println("import deb.cm.*;");
          printStr.println("import deb.sql.*;");
          printStr.println("import inv.sql.*;");
      }

      if (inpFile.equals("SMC-CM")) {                                
          printStr.println("import ml.cm.*;");
          printStr.println("import ml.sql.*;");
          printStr.println("import gen.cm.*;");
          printStr.println("import gen.sql.*;");
          printStr.println("import deb.cm.*;");
          printStr.println("import deb.sql.*;");
          printStr.println("import kre.cm.*;");
          printStr.println("import kre.sql.*;");
          printStr.println("import inv.cm.*;");
          printStr.println("import inv.sql.*;");
          printStr.println("import smc.cm.*;");
      }

      if (inpFile.equals("NYK-CM")) {                                
          printStr.println("import ml.cm.*;");
          printStr.println("import ml.sql.*;");
          printStr.println("import gen.cm.*;");
          printStr.println("import gen.sql.*;");
          printStr.println("import kre.cm.*;");
          printStr.println("import kre.sql.*;");
          printStr.println("import nyk.cm.*;");
      }

      if (inpFile.equals("BA-CM")) {                                
          printStr.println("import ba.cm.*;");
          printStr.println("import ml.cm.*;");
          printStr.println("import ml.sql.*;");
          printStr.println("import gen.cm.*;");
          printStr.println("import gen.sql.*;");
          printStr.println("import kre.cm.*;");
          printStr.println("import kre.sql.*;");
          printStr.println("import cli.cm.*;");
          printStr.println("import cli.sql.*;");
      }
   }

   //-----------------------------------------------------------
   public static boolean checkPath(String pathName) {                                
         
      boolean exists = (new File(pathName)).exists();
      if (exists)
          return true;         

      StringTokenizer dir = new StringTokenizer(pathName,"/"); 

      String path = "";
      int    idx  = 0;

      while (dir.hasMoreTokens ()) {
         idx++; 
         if (idx==1) {
             path  = dir.nextToken(); 
         } else {
             path += "/" + dir.nextToken();
         } 
         exists = (new File(path)).exists();             
         if (!exists) {       
             // Create a directory; all non-existent ancestor directories are automatically created        
             boolean success = (new File(path)).mkdirs();
             if (!success) {           
                 System.out.println("Error create directory " + path); 
                 return false; 
             } else {
                 System.out.println("Create directory " + path);
             }                       
         }
      }
 
      return true; 
      
   }   
 
   //-----------------------------------------------------------
}
