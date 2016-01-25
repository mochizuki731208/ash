package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class SysUserZFPwd extends Model<SysUserZFPwd>{

    private static final long serialVersionUID = 1L;
    public static final SysUserZFPwd dao = new SysUserZFPwd();

    public static String TABLE = "sys_user_zfpwd";

	/** 主键 **/
    public static final String ID = "id";

	/** 用户id **/
    public static final String USERID = "userid";

	/** 支付密码 **/
    public static final String ZFPWD = "zfpwd";

 }