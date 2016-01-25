/*     */ package com.web.util;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ 
/*     */ public class DataElement
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 3371775644439185563L;
/*  11 */   private String name = null;
/*  12 */   private String value = null;
/*  13 */   private List<DataElement> elements = null;
/*  14 */   private HashMap<String, Integer> order = null;
/*  15 */   private int childOrder = -1;
/*  16 */   private DataElement parent = null;
/*     */ 
/*     */   public DataElement(String name) {
/*  19 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public String getName() {
/*  23 */     return this.name;
/*     */   }
/*     */ 
/*     */   public void setName(String name) {
/*  27 */     this.name = name;
/*     */   }
/*     */ 
/*     */   public String getValue() {
/*  31 */     return this.value;
/*     */   }
/*     */ 
/*     */   public void setValue(String value) {
/*  35 */     this.value = value;
/*     */   }
/*     */ 
/*     */   public List<DataElement> getElements() {
/*  39 */     if (this.elements == null) {
/*  40 */       this.elements = new LinkedList();
/*  41 */       this.order = new HashMap();
/*     */     }
/*  43 */     return this.elements;
/*     */   }
/*     */ 
/*     */   public void addElement(DataElement data) {
/*  47 */     if (data != null) {
/*  48 */       getElements().add(data);
/*  49 */       String key = data.getName();
/*  50 */       this.childOrder += 1;
/*  51 */       if (this.order.containsKey(key)) {
/*  52 */         key = key + this.childOrder;
/*     */       }
/*  54 */       this.order.put(key, Integer.valueOf(this.childOrder));
/*  55 */       data.setParent(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeDataElement() {
/*  60 */     if (this.elements != null) {
/*  61 */       this.elements.clear();
/*  62 */       this.order.clear();
/*  63 */       this.childOrder = -1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeDataElementAt(String node) {
/*  68 */     if (this.order.containsKey(node)) {
/*  69 */       int index = ((Integer)this.order.get(node)).intValue();
/*  70 */       this.elements.remove(index);
/*  71 */       this.order.remove(node);
/*  72 */       this.childOrder -= 1;
/*  73 */       for (Map.Entry orderMap : this.order.entrySet()) {
/*  74 */         int orderValue = ((Integer)orderMap.getValue()).intValue();
/*  75 */         if (orderValue > index)
/*  76 */           orderMap.setValue(Integer.valueOf(orderValue - 1));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setDataElement(DataElement dataElement)
/*     */   {
/*  83 */     removeDataElement();
/*  84 */     addElement(dataElement);
/*     */   }
/*     */ 
/*     */   public String toString() {
/*  88 */     StringBuffer content = new StringBuffer();
/*  89 */     content.append("<" + getName() + ">");
/*  90 */     if (this.childOrder == -1)
/*  91 */       content.append(getValue());
/*     */     else {
/*  93 */       for (int i = 0; i <= this.childOrder; i++) {
/*  94 */         DataElement de = getDataElementAt(i);
/*  95 */         content = content.append(de.toString());
/*     */       }
/*     */     }
/*  98 */     content.append("</" + getName() + ">");
/*  99 */     return content.toString();
/*     */   }
/*     */ 
/*     */   public DataElement getDataElementAt(String name) throws XmlException {
/* 103 */     int index = name.indexOf(".");
/* 104 */     int pos = -1;
/* 105 */     String elementName = name;
/* 106 */     if (index >= 0) {
/* 107 */       elementName = name.substring(0, index);
/*     */     }
/* 109 */     if (this.order == null)
/* 110 */       return null;
/*     */     try
/*     */     {
/* 113 */       if (this.order.containsKey(elementName)) {
/* 114 */         pos = ((Integer)this.order.get(elementName)).intValue();
/*     */       }
/*     */       else {
/* 117 */         if (XmlUtil.isNaN(elementName)) {
/* 118 */           return null;
/*     */         }
/* 120 */         pos = Integer.parseInt(elementName, 10);
/*     */       }
/*     */ 
/* 123 */       if (index == -1) {
/* 124 */         return getDataElementAt(pos);
/*     */       }
/* 126 */       DataElement de = getDataElementAt(pos);
/* 127 */       if (de == null) {
/* 128 */         return null;
/*     */       }
/* 130 */       return de.getDataElementAt(name.substring(index + 1));
/*     */     } catch (Exception e) {
/* 132 */       throw new XmlException(e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public DataElement getDataElementAt(int index) {
/* 137 */     if ((index >= 0) && (index <= this.childOrder)) {
/* 138 */       return (DataElement)getElements().get(index);
/*     */     }
/* 140 */     return null;
/*     */   }
/*     */ 
/*     */   public int getElementCount()
/*     */   {
/* 145 */     return this.childOrder + 1;
/*     */   }
/*     */ 
/*     */   public DataElement getParent() {
/* 149 */     return this.parent;
/*     */   }
/*     */ 
/*     */   public void setParent(DataElement parent) {
/* 153 */     this.parent = parent;
/*     */   }
/*     */ }

/* Location:           C:\Users\Administrator\AppData\Local\Temp\Rar$DRa1.607\ECMCommObj.jar
 * Qualified Name:     com.bocomm.ecm.util.xml.DataElement
 * JD-Core Version:    0.6.2
 */