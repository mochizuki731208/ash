package com.zp.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jfinal.plugin.activerecord.Model;
/**
 *铃声
 * @author houzhi
 *
 */
public class SysOrderAlert extends Model<SysOrderAlert>{

	private static final long serialVersionUID = 1L;
	public static final SysOrderAlert dao = new SysOrderAlert();
	
	public static String TABLE = "sys_order_alert";


	
	
	/** 主键 **/
    public static final String ID = "id";

	/** 用户id **/
    public static final String UID = "uid";

	/** 铃声名称 **/
    public static final String NAME = "name";

	/** 铃声路径 **/
    public static final String SOUND_PATH  = "sound_path";

    
    
	/** 订单id **/
    public static final String ORDERID = "orderid";
    
    
    public List<SysOrderAlert> findByUserId(String userId){
    	System.out.println("userid:"+userId);
    	StringBuffer sql = new StringBuffer("select * from sys_order_alert where uid = ? ");
    	List<SysOrderAlert> list = dao.find(sql.toString(),userId);
    	return list;
    }
}
