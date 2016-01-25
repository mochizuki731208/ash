package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class SysUserStore extends Model<SysUserStore>{

    private static final long serialVersionUID = 1L;
    public static final SysUserStore dao = new SysUserStore();

    public static String TABLE = "sys_user_store";

	/** 主键 **/
    public static final String ID = "id";

	/** 用户ID **/
    public static final String USERID = "userid";

	/** 店铺ID **/
    public static final String MID = "mid";

	/** 店铺名称 **/
    public static final String MNAME = "mname";
 }