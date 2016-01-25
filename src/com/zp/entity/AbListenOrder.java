package com.zp.entity;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
/**
 * 司机听单设置
 * @author Administrator
 *
 */
public class AbListenOrder extends Model<AbListenOrder>{
	
	private static final long serialVersionUID = 1L;
	public static final AbListenOrder dao = new AbListenOrder();
	
	public static String TABLE = "ab_listen_city";

	/** 主键 **/
    public static final String ID = "id";

	/** 熟车id **/
    public static final String USER_ID = "user_id";
    
    /** 听单城市名 **/
    public static final String CITY_NAME = "city_name";
    
    public List<AbListenOrder> findByUserId(String user_id){
		StringBuffer sql = new StringBuffer(" select city_name as city from ab_listen_city where user_id = ? ");
		List<AbListenOrder> cityList = dao.find(sql.toString(), user_id);
	    return cityList;
	}
    
    public boolean delByUserId(String user_id){
    	StringBuffer sql = new StringBuffer(" delete from ab_listen_city where user_id = ? ");
    	Db.update(sql.toString(),user_id);
    	return true;
    }
    public boolean findByCityName(String city_name){
    	StringBuffer sql = new StringBuffer(" select *  from ab_listen_city where city_name = ? ");
    	Db.update(sql.toString(),city_name);
    	return true;
    }
}
