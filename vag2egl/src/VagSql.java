package vag2egl;
/* VAG to EGL Migration Utility - v.3.0
 * (C) Copyright IBM Denmark A/S 2009
 */
import java.io.*;
import java.util.*; 

public class VagSql {
  
   static String      refFunc     = "";  
   static String      refFuncFdb  = ""; 
   static String      refRec      = "";  
   static String      refRecTable = ""; 
   static String      refRecLabel = ""; 
   static String[][]  refTab      = new String[10000][3]; 
   static int         refIx       = -1; 
   static String[][]  fdbTab      = new String[10000][3]; 
   static int         fdbIx       = -1;

   static String      fileName    = "";
   static int         recCnt      = 0;   
   static int         recIdx      = 0;

   //********************************************************
   public static void main (String args[]) {

      if (args.length < 1) {
         System.out.println("Enter filename");
         System.exit(0);
      }                

    String inpRec;     
     
    try {
       PrintStream fpRef = new PrintStream(new FileOutputStream("VagSql.ref"));
       PrintStream fpFdb = new PrintStream(new FileOutputStream("VagSql.fdb"));
       
       for (int i=0; i<args.length; i++) {        
            fileName = args[i]; 
            FileInputStream inpFile =  new FileInputStream(fileName + ".esf");
            BufferedReader inpBuf = new BufferedReader(new InputStreamReader(inpFile)); 
            recCnt = 0; recIdx = 0; 
            while ((inpRec = inpBuf.readLine()) != null) {                              
                if (inpRec.length() > 0) {                   
                    createRefTable(inpRec);               
                }              
            } 
            inpFile.close();                                
       }   
      
       for (int i=0;i<=refIx;i++) {                          
            for (int j=0;j<=fdbIx;j++) {               
                 String fdb = fdbTab[j][0];                                
                 if (refTab[i][1].equals(fdb)) { 
                     refTab[i][2] = fdbTab[j][1];
                     break;    
                 }
            }
            fpRef.println(refTab[i][0] + ";" + refTab[i][1] + ";" + refTab[i][2] + ";");  
            if (refTab[i][2].equals("")) {
                System.out.println("notfnd: " + refTab[i][0] + " " + refTab[i][1]);
            }         
       }        
       fpRef.close();

       for (int i=0;i<=fdbIx;i++) {               
            if (!fdbTab[i][0].equals("")) {                 
                 fpFdb.println(fdbTab[i][0] + ";" + fdbTab[i][1] + ";");    
            }
       }        
       fpFdb.close();
       
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

  //-------------------------------------------------------
  public static void createRefTable(String inpRec) {          

    recCnt++;
    recIdx++; 
    if (recIdx == 10000) {
        System.out.println("Counter " + fileName + " : " + recCnt);
        recIdx = 0; 
    }

    int idx = 0; 
    boolean fnd = false;  

    if (inpRec.startsWith(":program ") ||
        inpRec.startsWith(":map ")     || 
        inpRec.startsWith(":mapg ")    || 
        inpRec.startsWith(":tble ")    ||             
        inpRec.startsWith(":item ")    ) {
        refRec      = ""; 
        refRecTable = ""; 
        refRecLabel = ""; 
        refFunc     = "";  
        refFuncFdb  = "";        
        return; 
    }
    
    if (inpRec.length() > 28 && 
        inpRec.substring(23, 28).equals("ZF185") ) {
        refFunc    = "";        
        refFuncFdb = "";  
        return; 
    }

    // Record ---
    if (inpRec.startsWith(":erecord.")) {
        refRec      = ""; 
        refRecTable = ""; 
        refRecLabel = ""; 
    } 
    if (inpRec.startsWith(":record ")) {
        refFunc     = ""; 
        refRecTable = ""; 
        refRecLabel = "";
        refRec   = inpRec.substring(23, inpRec.length());
    }
    if (!refRec.equals("") && inpRec.startsWith(":sqltable ")) {         
        idx = inpRec.substring(24, inpRec.length()).indexOf('\'') + 24;                   
        if (idx > 23) {                    
            refRecTable = inpRec.substring(24, idx).toUpperCase(); 
            fnd = false; 
            //System.out.println("rec=" + refRec + " table=" + refRecTable); 
            for (int i=0;i<=fdbIx;i++) {                                                     
                 if (refRec.equals(fdbTab[i][0])) {
                     if (!fdbTab[i][1].equals(""))
                         fdbTab[fdbIx][1] += ", " + refRecTable;
                     fnd = true;                  
                     break;                
                 }
            }
            if (!fnd) {
                fdbIx+=1;                 
                fdbTab[fdbIx][0] = refRec; 
                fdbTab[fdbIx][1] = refRecTable; 
                //System.out.println("fdb=" + refRec + " table=" + refRecTable);               
            }
        }
        
    
    }
    if (!refRecTable.equals("") && inpRec.length() > 23 && 
        inpRec.substring(11, 16).equals("label")     ){
        idx = inpRec.substring(24, inpRec.length()).indexOf('\'') + 24;
        if (idx > 23) {                    
            refRecLabel = inpRec.substring(24, idx).toUpperCase(); 
            fnd = false;                  
            //System.out.println("rec=" + refRec + " label=" + refRecLabel); 
            for (int i=0;i<=fdbIx;i++) {                                                     
                 if (refRec.equals(fdbTab[i][0])) {                    
                     fdbTab[i][1] += " " + refRecLabel;
                     fnd = true;                  
                     break;                
                 }
            }
            if (!fnd) {
                //fdbIx+=1;                 
                //fdbTab[fdbIx][0] = refRec; 
                //fdbTab[fdbIx][1] = refRecTable; 
                System.out.println("err=" + refRec + " table=" + refRecTable);               
            }           
        }                     
    }


    // Function ---
    if (inpRec.startsWith(":efunc.")) {
        refFunc    = "";        
        refFuncFdb = "";  
    }
    if (inpRec.startsWith(":func ") && inpRec.length() > 23) {
        refRec  = "";               
        idx = inpRec.substring(23, inpRec.length()).indexOf(' ') + 23; 
        if (idx >23)
            refFunc = inpRec.substring(23, idx);   
        else
            refFunc = inpRec.substring(23, inpRec.length());
    }
    if (!refFunc.equals("") && inpRec.length() > 63 &&
        inpRec.substring(11, 15).equals("date")     &&
        inpRec.substring(54, 60).equals("option")   ){           
        String opt = inpRec.substring(63, inpRec.length()); 
        if (opt.startsWith("CONVERSE")) {
            refFunc    = ""; 
            refFuncFdb = ""; 
        } 
        if (opt.startsWith("DISPLAY")) {
            refFunc    = ""; 
            refFuncFdb = ""; 
        }         
    }
    if (!refFunc.equals("") && inpRec.length() > 23 &&
        inpRec.substring(11, 17).equals("object")   ){                       
        idx = inpRec.substring(23, inpRec.length()).indexOf(' ') + 23; 
        if (idx >23)
            refFuncFdb = inpRec.substring(23, idx);   
        else
            refFuncFdb = inpRec.substring(23, inpRec.length()); 
        refIx++; 
        refTab[refIx][0] = refFunc;
        refTab[refIx][1] = refFuncFdb; 
        refTab[refIx][2] = "";                
        //System.out.println("func=" + refFunc + " fdb=" + refFuncFdb); 
        //System.out.println("ref=" + refTab[refIx][0] + " " + refTab[refIx][1] + ".");
        //fpRef.println(refFuncFdb + refFunc);                        
    }
    
  }
 
  //-----------------------------------------------------------
}
