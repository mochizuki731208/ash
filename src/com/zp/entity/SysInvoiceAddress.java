package com.zp.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jfinal.plugin.activerecord.Model;
/**
 * 发票地址
 * @author houzhi
 *
 */
public class SysInvoiceAddress extends Model<SysInvoiceAddress>{

	private static final long serialVersionUID = 1L;
	public static final SysInvoiceAddress dao = new SysInvoiceAddress();
	
	public static String TABLE = "sys_invoice_address";
	
	/** 主键 **/
    public static final String ID = "id";
    
    
    public static final String MOBILE = "mobile";
    
    public static final String NAME = "name";
    
	/** 地址详情 **/
    public static final String ADDRESS = "address";

    
    
    public List<SysInvoiceAddress> findByUserId(String userId){
    	System.out.println("userid:"+userId);
    	StringBuffer sql = new StringBuffer("select * from sys_pnumber where uid = ? ");
    	List<SysInvoiceAddress> list = dao.find(sql.toString(),userId);
    	return list;
    }
}
