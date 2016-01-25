package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class SysUserSubject extends Model<SysUserSubject>{

    private static final long serialVersionUID = 1L;
    public static final SysUserSubject dao = new SysUserSubject();

    public static String TABLE = "sys_user_subject";

	/** 主键 **/
    public static final String ID = "id";

	/** 业务员ID **/
    public static final String USER_ID = "user_id";

	/** 分类ID **/
    public static final String SUBJECT_ID = "subject_id";

	/** 商圈ID **/
    public static final String AREA_ID = "area_id";

	/** 跑腿费 **/
    public static final String SRVMONEY = "srvmoney";
 }