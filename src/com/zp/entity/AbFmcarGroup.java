package com.zp.entity;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.plugin.activerecord.Model;

public class AbFmcarGroup  extends Model<AbFmcarGroup>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final AbFmcarGroup dao = new AbFmcarGroup();
	public static String TABLE = "ab_fmcar_group";
	
	/** 主键 **/
    public static final String ID = "id";
    
	/** 分组名称 **/
    public static final String GROUPNAME = "groupname";
    
	/** 用户id **/
    public static final String USER_ID = "user_id";
    
    
    public  List<AbFmcarGroup> findByUserid(String userid){
    	String sql="select * from ab_fmcar_group where user_id = ?";
    	ArrayList<Object> params = new ArrayList<Object>();
    	params.add(userid);
    	List<AbFmcarGroup> list=dao.find(sql, params.toArray());
    	
    	return list;
    }
    


}
