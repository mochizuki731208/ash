package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class LogUserDeposit extends Model<LogUserDeposit>{

    private static final long serialVersionUID = 1L;
    public static final LogUserDeposit dao = new LogUserDeposit();

    public static String TABLE = "log_user_deposit";

	/** 主键 **/
    public static final String ID = "id";

	/** 用户ID **/
    public static final String USERID = "userid";

	/** 用户名称 **/
    public static final String LOGINID = "loginid";

	/** 姓名 **/
    public static final String MC = "mc";

	/** 充值时间 **/
    public static final String CREATETIME = "createtime";

	/** 充值金额 **/
    public static final String CZJE = "czje";

	/** 充值结果[1-成功、0-失败] **/
    public static final String STATUS = "status";

	/** 备注 **/
    public static final String REMARK = "remark";
 }