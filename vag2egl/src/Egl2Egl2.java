package vag2egl;
/* VAG to EGL Migration Utility - v.3.0
 * (C) Copyright IBM Denmark A/S 2009
 */
import java.io.*;
import java.util.*; 

public class Egl2Egl2 {

   static String inpDir   = null;     
   static String popupSw  = "Y";  

   //-----------------------------------------------------------
   public static void main(String[] args) {

      if (args.length < 1) {
         System.out.println("Enter directory name");
         System.exit(0);
      }      

      if (args.length > 1) {
          popupSw = args[1]; 
      }
      
      String fileName = args[0];

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
                 if (fileNames[i].startsWith("mapg-")) {
                     System.out.println(fileName + " member=" + fileNames[i]);                 
                     readFiles(fileName, fileNames[i]);  
                 }              
            }
         }
      }
      
   }

   //-----------------------------------------------------------
   public static boolean checkPopup(String mapId) { 

      if (!popupSw.equals("Y"))
           return false; 
         
      if ( (mapId.substring(0, 4).startsWith("F903")) ||
           (mapId.substring(0, 4).startsWith("F904")) || 
           (mapId.substring(0, 4).startsWith("F910")) ||
           (mapId.substring(0, 4).startsWith("F912")) ||                 
           (mapId.substring(0, 3).startsWith("F94"))  ||
           (mapId.substring(0, 3).startsWith("F95"))  ||
           (mapId.substring(0, 4).startsWith("F5JC")) ||  //HN??
           (mapId.substring(0, 4).startsWith("F969")) ){                                                
            return true; 
      }      
 
      if ( (mapId.substring(0, 3).startsWith("G94"))  ){                                                        
            return true; 
      } 

      if ( (mapId.substring(0, 4).startsWith("D941")) ){                                                          
            return true; 
      }
      
      if ( (mapId.substring(0, 4).startsWith("I941")) ||
           (mapId.substring(0, 4).startsWith("I942")) ||
           (mapId.substring(0, 4).startsWith("I943")) ||
           (mapId.substring(0, 4).startsWith("I944")) ){                                                      
            return true;          
        }

      if ( (mapId.substring(0, 4).startsWith("K941")) ||
           (mapId.substring(0, 4).startsWith("K943")) ||
           (mapId.substring(0, 4).startsWith("K944")) ){                                                    
            return true; 
      } 

      if ( (mapId.substring(0, 4).startsWith("A940")) ||
           (mapId.substring(0, 4).startsWith("A941")) ||
           (mapId.substring(0, 4).startsWith("A942")) ||             
           (mapId.substring(0, 4).startsWith("A943")) ||
           (mapId.substring(0, 4).startsWith("A944")) ||
           (mapId.substring(0, 4).startsWith("A945")) ){                                                    
            return true; 
      } 

      if ( (mapId.substring(0, 3).startsWith("L94")) ){                                                     
            return true; 
      }  

      return false; 
      
   }

   //-----------------------------------------------------------
   public static void readFiles(String dirName, String fileName)
         throws IOException, FileNotFoundException {    
    
      String inpName, inpRec, groupId, grpName, mapId, mapName, fileExt;
      int ix1, ix2;      
      boolean group = false; 
      boolean map   = false;   
      boolean bypass = false; 

      inpName = dirName + "/" + fileName; 

      ix1     = fileName.indexOf('-');
      ix2     = fileName.lastIndexOf('.');
      fileExt = fileName.substring(ix2, fileName.length());
      groupId = fileName.substring(ix1+1, ix2);      
      grpName = dirName + "/" + groupId + fileExt; // fileName.substring(ix2, fileName.length());      
      //System.out.println("fileName=" + fileName + " group=" + groupId + " Name=" + grpName);      
          
      try {
         FileInputStream inpFile =  new FileInputStream(inpName);
         BufferedReader  inpBuf  = new BufferedReader(new InputStreamReader(inpFile)); 

         PrintStream grpFile = null; 
         PrintStream mapFile = null;           
         grpFile = new PrintStream(new FileOutputStream(grpName)); 

         String packageTab[] = new String[50]; 
         int packageIx = -1; 
        
         while ((inpRec = inpBuf.readLine()) != null) { 
            //System.out.println(inpRec);
            if (inpRec.startsWith("package ")) {
                packageIx+=1; 
                packageTab[packageIx]=inpRec;                
            } else { 
               if (inpRec.startsWith("FormGroup ")) {
                   group = true;                     
                   if (packageIx >= 0) { 
                       for (int i=0;i<=packageIx;i++) {
                            grpFile.println(packageTab[i]);                            
                       }
                       grpFile.println(" "); 
                   }                                
               }  
               if (inpRec.startsWith("Form ")) {                        
                   ix1   = inpRec.indexOf(' ', 5);  
                   mapId = inpRec.substring(5, ix1);                    
                   //System.out.println("map=" + mapId + "   name=" + mapName);    
                   if (!checkPopup(mapId)) {
                       map     = true;
                       mapName = dirName + "/" + groupId + "-" + mapId + fileExt;
                       mapFile = new PrintStream(new FileOutputStream(mapName)); 
                       //System.out.println("map=" + mapId + " Name=" + mapName); 
                       if (packageIx >= 0) { 
                           for (int i=0;i<=packageIx;i++) {
                                mapFile.println(packageTab[i]);                            
                           }
                           mapFile.println(" "); 
                       } 
                   } else {
                       map = false; 
                   }
               } 

               if (group) {                 
                   grpFile.println(inpRec);
                   if (inpRec.startsWith("end ")) {
                      group = false;               
                   } 
               }

               if (map) {                 
                   mapFile.println(inpRec);
                   if (inpRec.startsWith("end ")) {
                      mapFile.close();
                      map = false;               
                   } 
               }

            }                                            
            
         } 
          
         inpFile.close();  
         grpFile.close();                   
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
 
}
