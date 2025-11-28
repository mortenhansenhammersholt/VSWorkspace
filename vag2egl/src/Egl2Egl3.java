package vag2egl;
/* VAG to EGL Migration Utility - v.3.0
 * (C) Copyright IBM Denmark A/S 2009
 */
import java.io.*;
import java.util.*; 

public class Egl2Egl3 {

   static String   inpDir   = null; 
   static String[] aplTab = new String[100];
   static int      aplIx    = -1;    
   static String[] fdbTab = new String[100];
   static int      fdbIx    = -1;
   static int      frPos    = 0; 
   static int      toPos    = 0; 

   static String[][] refTab = new String[10000][3]; 
   static int        refIx  = -1;
   static String funcName   = "";    

   //-----------------------------------------------------------
   public static void main(String[] args) {

      if (args.length < 1) {
         System.out.println("Enter directory name");
         System.exit(0);
      }            
      
      String fileName = args[0];

      int idx = fileName.lastIndexOf('/');
      inpDir = fileName.substring(0, idx) + "/";
      System.out.println("Dir=" + inpDir);

      try {
         readRefFile(); 
         readCallTable(); 
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
                 if (fileNames[i].startsWith("i-")) {
                     System.out.println(fileName + "  " + fileNames[i]);                 
                     readFiles(fileName, fileNames[i]);  
                 }              
            }
         }
      }
      
   }

   //-----------------------------------------------------------
   public static boolean getUserString(String rec, String str) {        

      frPos = 0; 
      toPos = 0;

      int ix1;        

      ix1  = rec.toUpperCase().indexOf(str);      

      if (ix1 < 0)
          return false; 

      if (checkComment(rec, ix1)) 
          return false; 

      if (ix1==0) {
          frPos=ix1;
      } else {    
          for (int i=ix1-1; i>0; i--)  {
               if (rec.charAt(i)==' ' ||   
                   rec.charAt(i)==':' ||            
                   rec.charAt(i)=='(' ) {
                   frPos = i+1;
                   break; 
               }           
          }
      }

      for (int i=frPos+1; i<rec.length(); i++)  {
           if (rec.charAt(i)==' ' ||
               rec.charAt(i)=='.' || 
               rec.charAt(i)==';' ||
               rec.charAt(i)=='-' ||
               rec.charAt(i)==')' ) {
               toPos = i;
               break; 
           }           
      } 

      if (toPos <= frPos)
          return false; 

      //System.out.println("fnd=" + rec.substring(frPos, toPos) + " rec=" + rec);

      return true;
      
   }
   //-----------------------------------------------------------
   public static boolean checkComment(String rec, int pos) { 

      if (pos <=1) 
          return false; 

      if (rec.substring(0, 2).equals("//")) 
          return true; 

      for (int i=pos-1; i>0; i--)  {
           if (rec.substring(i, i+2).equals("*/")) {
               return false; 
           }
           if (rec.substring(i, i+2).equals("/*") || rec.substring(i, i+2).equals("//")) {
               return true; 
           }     
      }     
     
      return false; 

   }
   //-----------------------------------------------------------
   public static void loadCallFDB(String rec, int fpos, int tpos) {          

     String apl = "";     

     for (int i=tpos+1; i<rec.length(); i++)  {
          if (rec.charAt(i)==' ' ||
              rec.charAt(i)=='.' ||               
              rec.charAt(i)==';' ) {
              apl = rec.substring(tpos+1, i);
              break; 
           }           
     } 
     //System.out.println("call:" + rec.substring(fpos, tpos) + " apl=" + apl);  

     String fdb = "";  
     String id  = ""; 
     boolean fnd = false;

     if (aplIx >= 0) { 
         for (int i=0;i<=aplIx;i++) {
              id = aplTab[i];     
              if (id.substring(0, apl.length()).equals(apl)) {                
                  fdb = id.substring(8, id.length()); 
                  fnd = true; 
                  break;
              }
        }
     }                                
     if (fnd) {
        //System.out.println("apl=" + apl + " fdb=" + fdb + "."); 
        loadFDB(fdb); 
     } 

   }
   //-----------------------------------------------------------
   public static void loadFDB(String fdb) {          

     fdb = fdb.toUpperCase(); 

     if (fdbIx >= 0) { 
         for (int i=0;i<=fdbIx;i++) {
              if (fdbTab[i].equals(fdb)) {
                  return;
              }
        }
     }                                
   
     fdbIx+=1;     
     fdbTab[fdbIx]=fdb;          
     //System.out.println("fdb ix=" + fdbIx + " fdb=" + fdb);  

   }
 
   //-----------------------------------------------------------
   public static void checkFDB(String rec) {   

       if (fdbIx < 1)
           return; 

       int fpos   = 0; 
       int tpos   = 0; 

       for (int i=0; i<rec.length(); i++)  {
            if (rec.charAt(i)!=' ') {                             
                fpos = i;
                break; 
            }           
       }
      
       for (int i=fpos+1; i<rec.length(); i++)  {
           if (rec.charAt(i)==' ' ||
               rec.charAt(i)=='.' ||                
               rec.charAt(i)==';' ) {
               tpos = i;
               break; 
           }           
      }

      if (tpos <= fpos)
          return; 

      String fdb = rec.substring(fpos, tpos); 


      //System.out.println("xfdb=" + fdb + " rec=" + rec); 

       for (int i=0;i<=fdbIx;i++) {
            if (fdbTab[i].equals(fdb)) {
                fdbTab[i]=""; 
            }
       } 

   }

   //-----------------------------------------------------------
   public static void readFiles(String dirName, String fileName)
         throws IOException, FileNotFoundException {    
    
      String inpName, inpRec, outMem, outName, fileExt;
      int ix1, ix2; 
      int checkIx = 0;          
     
      inpName = dirName + "/" + fileName; 

      ix1     = fileName.indexOf('-');
      ix2     = fileName.lastIndexOf('.');
      fileExt = fileName.substring(ix2, fileName.length());
      outMem  = fileName.substring(ix1+1, ix2);      
      outName = dirName + "/" + outMem + fileExt; // fileName.substring(ix2, fileName.length());      
      //System.out.println("fileName=" + fileName + " member=" + outMem + " Name=" + outName);      
          
      fdbTab = new String[100];
      fdbIx  = -1; 
      boolean promptSw = false;

      try {
         FileInputStream inpFile =  new FileInputStream(inpName);
         BufferedReader  inpBuf  = new BufferedReader(new InputStreamReader(inpFile)); 

         PrintStream     outFile = new PrintStream(new FileOutputStream(outName));          
        
         while ((inpRec = inpBuf.readLine()) != null) { 
            
            //outFile.println(inpRec);
            
            if (inpRec.startsWith("Function "))
                getFunctionName(inpRec); 
        
            if (!funcName.equals("") && inpRec.indexOf("EZE_SETPAGE") >= 0) {
                inpRec = replaceSetPage(inpRec); 
            }            

            //if (!funcName.equals("") && inpRec.indexOf("EZE_UNKNOWN_SQLTABLE") >= 0) {
            if (!funcName.equals("") && inpRec.indexOf("EZE_UNKNOWN_SQL") >= 0) {
                inpRec = replaceSQLTable(inpRec); 
            }

            //HN??
            if (!funcName.equals("") && inpRec.indexOf("F9ERT.EZE_UNKNOWN_") >= 0) {
                inpRec = replaceUnknownString(inpRec); 
            }

            if (inpRec.startsWith("Program "))
                checkIx++; 

            if (checkIx==1 && inpRec.indexOf("// Data Declarations") >=0)                    
                checkIx++; 

            if (checkIx < 2) {                              
                if (getUserString(inpRec, "CALL "))
                    loadCallFDB(inpRec, frPos, toPos);
                if (getUserString(inpRec, "FDB"))
                    loadFDB(inpRec.substring(frPos, toPos));                  
                if (!promptSw) {
                    if (getUserString(inpRec, "F9PRPSQ")) {
                        loadFDB("F9PRFDB");  
                        promptSw = true; 
                    }
                }                               
            }    

            if (checkIx==2) {
                if (inpRec.length()==0 || inpRec.equals(" ")) {                    
                    for (int i=0;i<=fdbIx;i++) {
                         if (!fdbTab[i].equals("")) {
                             outFile.println("  " + fdbTab[i] + " " + fdbTab[i] + "; // HNEGL"); 
                         }
                    }                    
                    checkIx=9; 
                } else {
                    //System.out.println("fdb:" + inpRec); 
                    checkFDB(inpRec); 
                }
            }

            outFile.println(inpRec);            

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
   public static void readCallTable()
         throws IOException, FileNotFoundException {        
     
      String inpName = inpDir + "callapl.txt";   
      String inpRec;      
          
      aplTab = new String[100];
      aplIx  = -1; 

      try {
         FileInputStream inpFile =  new FileInputStream(inpName);
         BufferedReader  inpBuf  = new BufferedReader(new InputStreamReader(inpFile));                  
        
         while ((inpRec = inpBuf.readLine()) != null) { 
            aplIx++; 
            aplTab[aplIx]=inpRec;            
         } 
          
         inpFile.close();                              
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
   public static void readRefFile()
         throws IOException, FileNotFoundException {        
     
      String inpName = inpDir + "VagSql.ref";   
      String inpRec;      
          
      refTab = new String[10000][3];
      refIx  = -1; 

      try {
         FileInputStream inpFile =  new FileInputStream(inpName);
         BufferedReader  inpBuf  = new BufferedReader(new InputStreamReader(inpFile));      
        
         while ((inpRec = inpBuf.readLine()) != null) { 
            refIx++; 
            refTab[refIx][0]="";
            refTab[refIx][1]="";
            refTab[refIx][2]="";    
            int fld  = -1;  
            int fpos = 0;
            int tpos = 0; 
            //String[] str = new String[3];
            for (int i=0; i<inpRec.length(); i++)  {
                 if (inpRec.charAt(i)==';') {
                     fld++;
                     tpos=i; 
                     if (tpos > fpos) {
                         //str[fld] = inpRec.substring(fpos, tpos); 
                         refTab[refIx][fld]=inpRec.substring(fpos, tpos);;
                     }
                     fpos = i+1; tpos=0;                     
                 }           
                 if (fld==2)
                     break; 
            } 
            //System.out.println("0=" + str[0] + " 1=" + str[1] + " 2=" + str[2]); 
            //System.out.println(refTab[refIx][0] + " " + refTab[refIx][1] + " " + refTab[refIx][2]); 
         } 
          
         inpFile.close();                              
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
      
      int fpos = rec.indexOf(' ') + 1;
      int tpos = rec.indexOf('(') - 0;
      if (tpos > fpos) {
          funcName = rec.substring(fpos, tpos); 
          //System.out.println("func=" + rec.substring(fpos, tpos)); 
      }

   }

   //-----------------------------------------------------------
   public static String replaceSQLTable(String rec) {      

      /*      
      int fpos = rec.indexOf("EZE_UNKNOWN_SQLTABLE");
      if (fpos < 0)
          return rec; 

      for (int i=0;i<=refIx;i++) {               
           if (!refTab[i][0].equals("")) {                     
               if (funcName.equals(refTab[i][0])) {
                   String newRec = rec.substring(0, fpos) + refTab[i][2]; 
                   return newRec;                                      
               }
           }
      }
      */ 

      int fpos; 

      fpos = rec.indexOf("EZE_UNKNOWN_SQLTABLE");
      if (fpos >= 0) {
          for (int i=0;i<=refIx;i++) {               
               if (!refTab[i][0].equals("")) {                     
                   if (funcName.equals(refTab[i][0])) {
                       rec = rec.substring(0, fpos) + refTab[i][2];
                   }
               }      
          }
      }

      fpos = rec.indexOf("EZE_UNKNOWN_SQL_FORUPDATEOF");
      if (fpos >= 0) {
          for (int i=0;i<=refIx;i++) {               
               if (!refTab[i][0].equals("")) {                     
                   if (funcName.equals(refTab[i][0])) {
                       rec = rec.substring(0, fpos) + refTab[i][2];
                   }
               }      
          }
      }

      return rec; 

   }
   //-----------------------------------------------------------
   public static String replaceSetPage(String rec) {      
      
      int fpos = rec.indexOf("EZE_SETPAGE");
      if (fpos < 0)
          return rec; 

      String newRec = rec.substring(0, fpos) + "clearScreen();"; 
      return newRec; 
      
   }
    
   //-----------------------------------------------------------
   public static String replaceUnknownString(String rec) {      

      int fpos, tpos; 
    
      String in1 = "F9ERT.EZE_UNKNOWN_SEARCH_COLUMN"; 
      String in2 = "F9ERT.EZE_UNKNOWN_RETURN_COLUMN"; 

      String out1 = "F9ERT.VAGen_SQLCODE";            
      String out2 = "F9ERT.FDSMNO";   
  
      fpos = rec.indexOf(in1);    
      if (fpos >= 0) {
          tpos = fpos + in1.length();  
          rec = rec.substring(0, fpos) + out1 + rec.substring(tpos, rec.length());            
      }

      fpos = rec.indexOf(in2);    
      if (fpos >= 0) {
          tpos = fpos + in2.length();  
          rec = rec.substring(0, fpos) + out2 + rec.substring(tpos, rec.length());            
      }

      return rec; 
      
   }
 
   //-----------------------------------------------------------
}
