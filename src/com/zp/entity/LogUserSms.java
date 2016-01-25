package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class LogUserSms extends Model<LogUserSms>{

    private static final long serialVersionUID = 1L;
    public static final LogUserSms dao = new LogUserSms();

    public static String TABLE = "log_user_sms";

	/** 主键 **/
    public static final String ID = "id";

	/** 用户ID **/
    public static final String USERID = "userid";

	/** 用户名称 **/
    public static final String USERNAME = "username";

	/** 手机号 **/
    public static final String PHONE = "phone";

	/** 内容 **/
    public static final String SMSCONTENT = "smscontent";

	/** 发送时间 **/
    public static final String SENDTIME = "sendtime";

	/** 发送结果[1-成功、0-失败] **/
    public static final String SENDSTATUS = "sendstatus";

	/** 发送类型 **/
    public static final String SENDTYPE = "sendtype";
 }