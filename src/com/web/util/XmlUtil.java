/*    */ package com.web.util;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.net.URL;
/*    */ import java.util.List;
/*    */ import java.util.regex.Matcher;
/*    */ import java.util.regex.Pattern;
/*    */ 
/*    */ public class XmlUtil
/*    */ {
/*    */   public static byte[] getBytes(String fileName)
/*    */     throws IOException
/*    */   {
/* 14 */     InputStream fis = CPFileFinder.getURL(fileName).openStream();
/* 15 */     if (fis == null) {
/* 16 */       return null;
/*    */     }
/* 18 */     int fileLen = fis.available();
/* 19 */     if (fileLen <= 0) {
/* 20 */       return null;
/*    */     }
/* 22 */     byte[] buffer = new byte[fileLen];
/* 23 */     int readLen = fis.read(buffer);
/* 24 */     if (readLen != fileLen) {
/* 25 */       return null;
/*    */     }
/* 27 */     if (buffer == null);
/* 30 */     fis.close();
/* 31 */     return buffer;
/*    */   }
/*    */ 
/*    */   public static String outDataElement(DataElement de, int level) {
/* 35 */     StringBuffer buffer = new StringBuffer();
/* 36 */     if (de != null) {
/* 37 */       for (int i = 0; i < level; i++) {
/* 38 */         buffer.append("  ");
/*    */       }
/* 40 */       buffer.append("<").append(de.getName()).append(">");
/* 41 */       List children = de.getElements();
/* 42 */       if (children.isEmpty()) {
/* 43 */         String value = de.getValue() == null ? "" : de.getValue();
/* 44 */         buffer.append(value);
/*    */       } else {
/* 46 */         int size = children.size();
/* 47 */         buffer.append("\n");
/* 48 */         for (int i = 0; i < size; i++) {
/* 49 */           buffer.append(outDataElement((DataElement)children.get(i), level + 1));
/*    */         }
/* 51 */         for (int i = 0; i < level; i++) {
/* 52 */           buffer.append("  ");
/*    */         }
/*    */       }
/* 55 */       buffer.append("</").append(de.getName()).append(">\n");
/*    */     }
/* 57 */     return buffer.toString();
/*    */   }
/*    */ 
/*    */   public static boolean isNaN(String input)
/*    */   {
/* 64 */     Pattern pattern = Pattern.compile("[0-9]+");
/* 65 */     Matcher matcher = pattern.matcher(input);
/* 66 */     return !matcher.matches();
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\AppData\Local\Temp\Rar$DRa1.607\ECMCommObj.jar
 * Qualified Name:     com.bocomm.ecm.util.xml.XmlUtil
 * JD-Core Version:    0.6.2
 */