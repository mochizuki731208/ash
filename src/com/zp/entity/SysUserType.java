package com.zp.entity;

import com.jfinal.plugin.activerecord.Model;

public class SysUserType extends Model<SysUserType> {
	private static final long serialVersionUID = 1L;
	public static final SysUserType dao = new SysUserType();

	public static String TABLE = "sys_user_type";

	/** 主键 **/
	public static final String ID = "id";
	/** 类型1 **/
	public static final String USERTYPE = "usertype";
	/** 类型2 **/
	public static final String USERSUBTYPE = "usersubtype";
	/** 用户id **/
	public static final String USERID = "userid";
	
	public SysUserType findModel(Object uid) {
		return findFirst("select * from sys_user_type where userid=?", uid);
	}
}
