/*    */ package com.web.util;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.net.MalformedURLException;
/*    */ import java.net.URL;
/*    */ 
/*    */ public abstract class CPFileFinder
/*    */ {
/*    */   public static URL getURL(String filename)
/*    */   {
/* 22 */     URL url = null;
/* 23 */     ClassLoader cl = getTCL();
/* 24 */     url = cl.getResource(filename);
/* 25 */     if (url == null) {
/* 26 */       cl = CPFileFinder.class.getClassLoader();
/* 27 */       url = cl.getResource(filename);
/*    */     }
/* 29 */     if (url == null)
/* 30 */       url = ClassLoader.getSystemResource(filename);
/* 31 */     if (url == null) {
/*    */       try {
/* 33 */         File file = new File(filename);
/* 34 */         if (file.exists())
/* 35 */           url = new File(filename).toURL();
/*    */       } catch (MalformedURLException e) {
/* 37 */         e.printStackTrace();
/*    */       }
/*    */ 
/*    */     }
/*    */ 
/* 42 */     return url;
/*    */   }
/*    */ 
/*    */   private static ClassLoader getTCL() {
/* 46 */     return Thread.currentThread().getContextClassLoader();
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\AppData\Local\Temp\Rar$DRa1.607\ECMCommObj.jar
 * Qualified Name:     com.bocomm.ecm.util.CPFileFinder
 * JD-Core Version:    0.6.2
 */