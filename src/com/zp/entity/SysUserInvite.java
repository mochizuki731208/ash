package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class SysUserInvite extends Model<SysUserInvite>{

    private static final long serialVersionUID = 1L;
    public static final SysUserInvite dao = new SysUserInvite();

    public static String TABLE = "sys_user_invite";

	/** 主键 **/
    public static final String ID = "id";

	/** 用户名 **/
    public static final String LOGINID = "loginid";

	/** 邀请用户名 **/
    public static final String INVITE_LOGINID = "invite_loginid";
 }