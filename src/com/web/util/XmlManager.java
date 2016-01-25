/*    */ package com.web.util;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.PrintStream;
/*    */ 
/*    */ public class XmlManager
/*    */ {
/*  9 */   private static Dom4jXmlProduct xmlParse = new Dom4jXmlProduct();
/*    */ 
/*    */   public static DataElement parse(String xml) throws XmlException
/*    */   {
/*    */     try {
/* 14 */       return xmlParse.parse(xml);
/*    */     } catch (XmlException e) {
/* 16 */       throw new XmlException(e.getMessage());
/*    */     }
/*    */   }
/*    */ 
/*    */   public static DataElement parseXml(String xmlfile) throws XmlException {
/*    */     try {
/* 22 */       String content = new String(XmlUtil.getBytes(xmlfile));
/* 23 */       return parse(content);
/*    */     } catch (IOException e) {
/* 25 */       throw new XmlException(e.getMessage());
/*    */     }
/*    */   }
/*    */ 
/*    */   public static DataElement parseXml(String xmlfile, String encoding) throws XmlException {
/*    */     try {
/* 31 */       String content = new String(XmlUtil.getBytes(xmlfile), encoding);
/* 32 */       return parse(content);
/*    */     } catch (IOException e) {
/* 34 */       throw new XmlException(e.getMessage());
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void main(String[] agvs) throws Exception {
/* 39 */     byte[] bytes = XmlUtil.getBytes("20110422_01_00001469_00001S40009.xml");
/* 40 */     System.out.println(new String(bytes, "UTF8"));
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\AppData\Local\Temp\Rar$DRa1.607\ECMCommObj.jar
 * Qualified Name:     com.bocomm.ecm.util.xml.XmlManager
 * JD-Core Version:    0.6.2
 */