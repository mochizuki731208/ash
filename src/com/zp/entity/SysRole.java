package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class SysRole extends Model<SysRole>{

    private static final long serialVersionUID = 1L;
    public static final SysRole dao = new SysRole();

    public static String TABLE = "sys_role";

	/** 角色id **/
    public static final String ID = "id";

	/** 角色名称 **/
    public static final String JSMC = "jsmc";

	/** 顺序号 **/
    public static final String SXH = "sxh";

	/** 角色说明 **/
    public static final String DDESC = "ddesc";
 }