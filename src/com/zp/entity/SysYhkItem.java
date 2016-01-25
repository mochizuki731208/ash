package com.zp.entity;

import com.jfinal.plugin.activerecord.Model;

public class SysYhkItem  extends Model<SysYhkItem> {
	private static final long serialVersionUID = 1L;
	public static final SysYhkItem dao = new SysYhkItem();
	public static String TABLE = "sys_yhka_item";
	/** 主键 **/
	public static final String ID = "id";
	/** 开户行**/
	public static final String YHKNAME = "yhkname";
	/** 账号 **/
	public static final String YHKCODE = "yhkcode";
	/** 开户人 **/
	public static final String YHKUSER = "yhkuser";
}
