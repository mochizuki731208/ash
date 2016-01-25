package com.zp.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jfinal.plugin.activerecord.Model;
/**
 * 微信公众号
 * @author houzhi
 *
 */
public class SysPNumber extends Model<SysPNumber>{

	private static final long serialVersionUID = 1L;
	public static final SysPNumber dao = new SysPNumber();
	
	public static String TABLE = "sys_pnumber";


	
	
	/** 主键 **/
    public static final String ID = "id";

	/** 用户id **/
    public static final String UID = "uid";

	/** 公众号名称 **/
    public static final String NAME = "name";

	/** 公众号token **/
    public static final String TOKEN = "token";

	/** 原始id **/
    public static final String ORIGIN_ID = "origin_id";

	/** 公众号类型 **/
    public static final String TYPE = "type";

    /** email **/
    public static final String EMAIL = "email";
    
	/** 公众号描述 **/
    public static final String DESCRIBLE = "describle";

	/** 公众号APPID **/
    public static final String APPID = "appid";

	/** 公众号APPSECRET **/
    public static final String APPSECRET = "appsecret";
    
    
    public List<SysPNumber> findByUserId(String userId){
    	System.out.println("userid:"+userId);
    	StringBuffer sql = new StringBuffer("select * from sys_pnumber where uid = ? ");
    	List<SysPNumber> list = dao.find(sql.toString(),userId);
    	return list;
    }
}
