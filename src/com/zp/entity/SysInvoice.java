package com.zp.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jfinal.plugin.activerecord.Model;
/**
 * 发票
 * @author houzhi
 *
 */
public class SysInvoice extends Model<SysInvoice>{

	private static final long serialVersionUID = 1L;
	public static final SysInvoice dao = new SysInvoice();
	
	public static String TABLE = "sys_invoice";
	
	
	/** 主键 **/
    public static final String ID = "id";
    
    
    public static final String UID = "uid";
    
    /**
     * 订单id
     */
    public static final String ORDER_ID = "order_id";
   
    /**
     * 订单号
     */
    public static final String ORDER_SN = "order_sn";
    
    /**
     * 区域id
     */ 
    public static final String AREA_ID = "area_id"; 

	/** 发票抬头（个人或者公司名称） **/
    public static final String TYPE = "type";

	/** 金额 **/
    public static final String MONEY = "money";

	/** 发票详情，内容 **/
    public static final String CONTENT = "content";

	/** 表示这张发票是否已经开了。送达了e **/
    public static final String HAVE_DONE = "have_done";

    /** 对应的地址id **/
    public static final String ADDRESS_ID = "address_id";
    
    
    public List<SysInvoice> findByUserId(String userId){
    	System.out.println("userid:"+userId);
    	StringBuffer sql = new StringBuffer("select * from sys_pnumber where uid = ? ");
    	List<SysInvoice> list = dao.find(sql.toString(),userId);
    	return list;
    }
}
