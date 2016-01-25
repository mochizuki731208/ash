package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class LogUserVipcard extends Model<LogUserVipcard>{

    private static final long serialVersionUID = 1L;
    public static final LogUserVipcard dao = new LogUserVipcard();

    public static String TABLE = "log_user_vipcard";

	/** 主键 **/
    public static final String ID = "id";

	/** 消费类型 **/
    public static final String CARDTYPE = "cardtype";

	/** 剩余服务次数 **/
    public static final String REMAINSRVNUM = "remainsrvnum";

	/** 用户ID **/
    public static final String USER_ID = "user_id";

	/** 用户名称 **/
    public static final String USER_NAME = "user_name";

	/** 订单ID **/
    public static final String ORDER_ID = "order_id";

	/** 消费说明 **/
    public static final String SRVMEMO = "srvmemo";

	/** 消费时间 **/
    public static final String SRVTIME = "srvtime";
 }