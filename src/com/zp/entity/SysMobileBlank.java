package com.zp.entity;

import com.jfinal.plugin.activerecord.Model;

public class SysMobileBlank extends Model<SysMobileBlank>{

	private static final long serialVersionUID = 1L;
	public static final SysMobileBlank dao = new SysMobileBlank();
	
	public static String TABLE = "sys_mobile_blank";

	/** 主键 **/
    public static final String ID = "id";

	/** 用户信息 **/
    public static final String CAR_NO = "userid";

	/** 黑名单号码 **/
    public static final String DRIVER = "mobile";
}
