package com.web.util;



         import java.io.StringReader;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import org.dom4j.Document;
/*    */ import org.dom4j.DocumentFactory;
/*    */ import org.dom4j.Element;
/*    */ import org.dom4j.io.SAXReader;
/*    */ 
/*    */ public class Dom4jXmlProduct
/*    */ {
/*    */   public DataElement parse(String xml)
/*    */     throws XmlException
/*    */   {
/* 14 */     SAXReader saxReader = new SAXReader();
             Document doc;
/*    */     try
/*    */     {
/* 17 */       doc = saxReader.read(new StringReader(xml));
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/* 19 */       throw new XmlException(e.getMessage());
/*    */     }
/*    */     
/* 21 */     Element root = doc.getRootElement();
/* 22 */     DataElement data = new DataElement(root.getName());
/* 23 */     parseXml(root, data);
/* 24 */     return data;
/*    */   }
/*    */ 
/*    */   private void parseXml(Element xmlElement, DataElement dataElement)
/*    */   {
/* 29 */     if (xmlElement.elements().isEmpty()) {
/* 30 */       dataElement.setValue(xmlElement.getText());
/*    */     } else {
/* 32 */       Iterator children = xmlElement.elements().iterator();
/* 33 */       Element child = null;
/* 34 */       DataElement childData = null;
/* 35 */       while (children.hasNext()) {
/* 36 */         child = (Element)children.next();
/* 37 */         childData = new DataElement(child.getName());
/* 38 */         dataElement.addElement(childData);
/* 39 */         parseXml(child, childData);
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   private void constructXml(DocumentFactory docFactory, DataElement dataElement, Element xmlElement)
/*    */   {
/* 46 */     if (dataElement.getElementCount() == 0) {
/* 47 */       xmlElement.setText(dataElement.getValue());
/*    */     } else {
/* 49 */       Iterator children = dataElement.getElements().iterator();
/* 50 */       DataElement child = null;
/* 51 */       Element childXml = null;
/* 52 */       while (children.hasNext()) {
/* 53 */         child = (DataElement)children.next();
/* 54 */         childXml = addXmlNode(docFactory, xmlElement, child.getName(), "");
/* 55 */         constructXml(docFactory, child, childXml);
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   private Element addXmlNode(DocumentFactory docFactory, Element parent, String nodeName, String nodeValue) {
/* 61 */     Element element = docFactory.createElement(nodeName);
/* 62 */     element.setText(nodeValue);
/* 63 */     if (parent != null) {
/* 64 */       parent.add(element);
/*    */     }
/* 66 */     return element;
/*    */   }
/*    */ }

/* Location:           C:\Users\Administrator\AppData\Local\Temp\Rar$DRa1.607\ECMCommObj.jar
 * Qualified Name:     com.bocomm.ecm.util.xml.Dom4jXmlProduct
 * JD-Core Version:    0.6.2
 */