package com.zp.entity;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

public class AbMessage extends Model<AbMessage>{
	 private static final long serialVersionUID = 1L;
	 public static final AbMessage dao=new AbMessage();
	 
	 public List<AbMessage> findByUserId(String userId){
	    	StringBuffer sql = new StringBuffer("select * from message where user_id=?");
	    	List<AbMessage> list = dao.find(sql.toString(),userId);
	    	return list;
	 }
	 
	 public SysUser getSysUser() {
	        return SysUser.dao.findById(get("id"));
	 }
}
