package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 
/**
 * 发票
 * @author Administrator
 *
 */
public class AbInvoice extends Model<AbInvoice>{

    private static final long serialVersionUID = 1L;
    public static final AbInvoice dao = new AbInvoice();

    public static String TABLE = "ab_notice";

	/** 主键 **/
    public static final String ID = "id";


	/** 用户ID **/
    public static final String UID = "uid";
    
    public static final String ORDERSN = "ordersn";
    
    public static final String MONEY = "money";
    
    public static final String HEADER = "header";
   /**
    */
    public static final String CONTENT = "content";
    public static final String PHONE = "phone";
    public static final String NAME = "name";
    public static final String ADDRESS = "address";
    


 }