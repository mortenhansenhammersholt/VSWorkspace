package vag2egl;
/* VAG to EGL Migration Utility - v.3.0
 * (C) Copyright IBM Denmark A/S 2009
 */
import java.io.*;
import java.util.*;

public class Egl2Egl {

   static String eglProject    = null; // MH
   static String inpDir    = null;
   static String impTab[]  = {"ml-cm", "gen-cm", "kre-cm", "deb-cm", "inv-cm" }; 
   
   static String importML_cm   = "import ml.cm.*;";
   static String importGEN_cm  = "import gen.cm.*;";
   static String importDEB_cm  = "import deb.cm.*;";
   static String importKRE_cm  = "import kre.cm.*;";
   static String importINV_cm  = "import inv.cm.*;";
   static String importFAA_cm  = "import faa.cm.*;";
   static String importCLI_cm  = "import cli.cm.*;";

   static String importML_sql  = "import ml.sql.*;";
   static String importGEN_sql = "import gen.sql.*;";
   static String importDEB_sql = "import deb.sql.*;";
   static String importKRE_sql = "import kre.sql.*;";
   static String importINV_sql = "import inv.sql.*;";
   static String importFAA_sql = "import faa.sql.*;";
   static String importCLI_sql = "import cli.sql.*;";

   static String importSMC_cm  = "import smc.cm.*;";
   static String importSMC_sql = "import smc.sql.*;";


   static String importNYK_cm  = "import nyk.cm.*;";
   static String importNYK_sql = "import nyk.sql.*;";


   static String importBA_cm  = "import ba.cm.*;";
   static String importBA_sql = "import ba.sql.*;";

   //-----------------------------------------------------------
   public static void main(String[] args) {

      if (args.length < 1) {
         System.out.println("Enter directory name");
         System.exit(0);
      }      
      
      String fileName = args[0];

      eglProject = args[1]; // MH

      int idx = fileName.lastIndexOf('/');
      inpDir = fileName.substring(0, idx) + "/";
      System.out.println("inpDir=" + inpDir);

      try {
         listFiles(fileName);
      }
      catch (Exception e) {
         System.err.println(e);
      }
   }

   //-----------------------------------------------------------
   public static void listFiles(String fileName) 
         throws IOException, FileNotFoundException {
   
      File file = new File(fileName);            

      if (file.isDirectory()) {
         String[] fileNames = file.list(); // <-- return fileNames
         if (fileNames != null) {
             for (int i=0; i<fileNames.length; i++)  {
                 readFiles(fileName, fileNames[i]); 
            }
         }
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
   public static void readFiles(String dirName, String fileName)
         throws IOException, FileNotFoundException {    
    
      String inpName, inpRec, outDir, outName, outRec=null;

      inpName = dirName + "/" + fileName; 

      int idx = 0;       
      int fileNameLength = 0;
      
      if (fileName.length() > 8) {
         idx = fileName.substring(0, 9).lastIndexOf('-');
         fileNameLength = 8;
      } else {
         idx = fileName.lastIndexOf('-');
         fileNameLength = fileName.length();
      }


      String fn = fileName.substring(0, fileNameLength);
      String dn = fileName.substring(0, fileNameLength);
      fn = fn.replaceAll("-", ".");
      dn = dn.replaceAll("-", "/");
      if (fileNameLength >= fileName.length()) {
    	fileName = fn;  
      } else {
        fileName = fn + fileName.substring(fileNameLength);
        dn += fileName.substring(fileNameLength);
      }
/*      
      // Replace "-" with "." in the package part of the file name
      try {
        for (int i1 = 0; i1 < fileNameLength; i1++){
System.out.println("ZZZ1b-A fileName=\"" + fileName + "\" i1=" + i1);
          if (fileName.substring(i1,i1+1).equals("-")) {
        	String fn;
        	if (i1 == 0) {
        	  // first char
System.out.println("ZZZ1b-B first char");
        		fn = "." + fileName.substring(1);
        	} else {
        	  if (i1 == fileName.length() -1) {
                // last char in whole file name. Note: it should NOT be
                // fileNameLength, but fileName.length()
System.out.println("ZZZ1b-C last char");
                fn = fileName.substring(0, i1) + ".";
              }	else {
System.out.println("ZZZ1b-D mid char");
                fn = fileName.substring(0, i1) + "." + fileName.substring(i1+1);
              }
        	}
            fileName = fn;
          }
System.out.println("ZZZ1b fileName = " + fileName);        	 
        }
      } catch (Exception e) {
    	// NOP
      }
System.out.println("ZZZ1c fileName = " + fileName);        	 
      */
      //outDir = fileName.substring(0, idx); 
      outDir = dn.substring(0, idx); 

      outName = inpDir + outDir + "/" + dn.substring(idx+1, dn.length()); 
      //System.out.println("dir=" + dirName + " fileName=" + fileName + " out=" + outName); 

      try {
         FileInputStream inpFile =  new FileInputStream(inpName);
         BufferedReader inpBuf = new BufferedReader(new InputStreamReader(inpFile)); 
        
         PrintStream outFile = null; 
         String pathName = inpDir + outDir;         
         if (checkPath(pathName)) {
             outFile = new PrintStream(new FileOutputStream(outName)); 
         } else {
             System.exit(0);
         }
          
         while ((inpRec = inpBuf.readLine()) != null) { 
             //System.out.println(inpRec);
             if (outRec==null) {
                 outRec = "package " + fileName.substring(0, idx) + ";"; 
                 outFile.println(outRec);
                 if (outDir.length() == 5 && outDir.equals("ml-cm")) { //HN 2007-04-10
                     outFile.println(importML_sql);
                 }
                 if (outDir.length() == 2 && outDir.equals("ml")) {                     
                     outFile.println(importML_cm);
                     outFile.println(importML_sql);
                 }
                 if (outDir.length() == 6 && outDir.equals("gen-cm")) {                     
                     outFile.println(importML_cm);
                     outFile.println(importML_sql);
                     outFile.println(importGEN_sql); //HN 2007-04-10
                 }
                 if (outDir.length() == 3 && outDir.equals("gen")) {                     
                     outFile.println(importML_cm);                     
                     outFile.println(importML_sql);
                     outFile.println(importGEN_cm);
                     outFile.println(importGEN_sql);
                 }
                 if (outDir.length() == 6 && outDir.equals("kre-cm")) {                     
                     outFile.println(importML_cm);                     
                     outFile.println(importML_sql);
                     outFile.println(importGEN_cm);
                     outFile.println(importGEN_sql);
                     outFile.println(importKRE_sql); //HN 2007-04-10 
                 }
                 if (outDir.length() == 3 && outDir.equals("kre")) {                     
                     outFile.println(importML_cm);   
                     outFile.println(importML_sql);                  
                     outFile.println(importGEN_cm);
                     outFile.println(importGEN_sql);                     
                     outFile.println(importKRE_cm);
                     outFile.println(importKRE_sql);
                 }
                 if (outDir.length() == 6 && outDir.equals("deb-cm")) {                     
                     outFile.println(importML_cm);                     
                     outFile.println(importML_sql);
                     outFile.println(importGEN_cm);
                     outFile.println(importGEN_sql);
                     outFile.println(importDEB_sql); //HN 2007-04-10
                 }
                 if (outDir.length() == 3 && outDir.equals("deb")) {                     
                     outFile.println(importML_cm);                    
                     outFile.println(importML_sql);
                     outFile.println(importGEN_cm);
                     outFile.println(importGEN_sql);                    
                     outFile.println(importDEB_cm);
                     outFile.println(importDEB_sql);
                 }
                 if (outDir.length() == 6 && outDir.equals("inv-cm")) {                    
                     outFile.println(importML_cm);                    
                     outFile.println(importML_sql);
                     outFile.println(importGEN_cm);
                     outFile.println(importGEN_sql);                     
                     outFile.println(importDEB_cm);
                     outFile.println(importDEB_sql);
                     outFile.println(importINV_sql); //HN 2007-04-10
                 }
                 if (outDir.length() == 3 && outDir.equals("inv")) {                     
                     outFile.println(importML_cm);                     
                     outFile.println(importML_sql);
                     outFile.println(importGEN_cm);                     
                     outFile.println(importGEN_sql); 
                     outFile.println(importDEB_cm);                     
                     outFile.println(importDEB_sql);
                     outFile.println(importINV_cm);
                     outFile.println(importINV_sql);
                 }
                 if (outDir.length() == 6 && outDir.equals("faa-cm")) {                     
                     outFile.println(importML_cm);                     
                     outFile.println(importML_sql);
                     outFile.println(importFAA_sql); //HN 2007-04-10
                 }
                 if (outDir.length() == 3 && outDir.equals("faa")) {                     
                     outFile.println(importML_cm);                     
                     outFile.println(importML_sql);
                     outFile.println(importFAA_cm);
                     outFile.println(importFAA_sql);                    
                 }
                 if (outDir.length() == 6 && outDir.equals("cli-cm")) {                     
                     outFile.println(importML_cm);                     
                     outFile.println(importML_sql);
                     outFile.println(importCLI_sql); //HN 2007-04-10
                 }
                 if (outDir.length() == 3 && outDir.equals("cli")) {                     
                     outFile.println(importML_cm);                     
                     outFile.println(importML_sql);
                     outFile.println(importCLI_cm);
                     outFile.println(importCLI_sql);                    
                 }
                 if (outDir.length() == 6 && outDir.equals("smc-cm")) {                    
                     outFile.println(importML_cm);                    
                     outFile.println(importML_sql);
                     outFile.println(importGEN_cm);
                     outFile.println(importGEN_sql);                     
                     outFile.println(importDEB_cm);
                     outFile.println(importDEB_sql);
                     outFile.println(importINV_sql); //HN 2007-04-10
                     outFile.println(importKRE_cm);
                     outFile.println(importKRE_sql);
                 }
                 if (outDir.length() == 3 && outDir.equals("smc")) {                     
                     outFile.println(importSMC_cm);                     
                     outFile.println(importSMC_sql);                     
                     outFile.println(importML_cm);                     
                     outFile.println(importML_sql);
                     outFile.println(importGEN_cm);                     
                     outFile.println(importGEN_sql); 
                     outFile.println(importDEB_cm);                     
                     outFile.println(importDEB_sql);
                     outFile.println(importINV_cm);
                     outFile.println(importINV_sql);
                     outFile.println(importKRE_cm);
                     outFile.println(importKRE_sql);
                 }
                 if (outDir.length() == 6 && outDir.equals("nyk-cm")) {                    
                     outFile.println(importML_cm);                    
                     outFile.println(importML_sql);
                     outFile.println(importGEN_cm);
                     outFile.println(importGEN_sql);                     
                     outFile.println(importKRE_cm);
                     outFile.println(importKRE_sql);
                 }
                 if (outDir.length() == 3 && outDir.equals("nyk")) {                     
                     outFile.println(importNYK_cm);                     
                     outFile.println(importNYK_sql);                     
                     outFile.println(importML_cm);                     
                     outFile.println(importML_sql);
                     outFile.println(importGEN_cm);                     
                     outFile.println(importGEN_sql); 
                     outFile.println(importKRE_cm);
                     outFile.println(importKRE_sql);
                 }
                 if (outDir.length() == 5 && outDir.equals("ba-cm")) {                    
                     outFile.println(importML_cm);                    
                     outFile.println(importML_sql);
                     outFile.println(importGEN_cm);
                     outFile.println(importGEN_sql);                     
                     outFile.println(importKRE_cm);
                     outFile.println(importKRE_sql);
                 }
                 if (outDir.length() == 2 && outDir.equals("ba")) {                     
                     outFile.println(importBA_cm);                     
                     outFile.println(importBA_sql);                     
                     outFile.println(importML_cm);                     
                     outFile.println(importML_sql);
                     outFile.println(importGEN_cm);                     
                     outFile.println(importGEN_sql); 
                     outFile.println(importKRE_cm);
                     outFile.println(importKRE_sql);
                 }
 //                if (outDir.length() == 3 && outDir.equals(eglProject)) {  
 //                    outFile.println("import " + eglProject + ".cm.*;");                     
 //                    outFile.println("import " + eglProject + ".sql.*;");                     
 //                }
                   if (outDir.length() == 2 && outDir.equals(eglProject)) {  
                     outFile.println("import " + eglProject + ".cm.*;");                     
                     outFile.println("import " + eglProject + ".sql.*;");
                     outFile.println("import " + "atp.cm.*;");
                 }

             } else {
                 outFile.println(inpRec);
             }
         } 
         inpFile.close();  
         outFile.close();                   
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

}
