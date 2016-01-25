package com.zp.entity;

import com.jfinal.plugin.activerecord.Model;

public class SysUserGroup extends Model<SysUserGroup> {
	private static final long serialVersionUID = 1L;
	public static final SysUserGroup dao = new SysUserGroup();

	public static String TABLE = "sys_user_group";

	/** 主键 **/
	public static final String ID = "id";

	/** 组名 **/
	public static final String GROUPNAME = "proupname";
	
	/** 城市id **/
	public static final String CITYID = "cityid";
}
