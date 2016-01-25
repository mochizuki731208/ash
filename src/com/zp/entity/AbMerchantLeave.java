package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class AbMerchantLeave extends Model<AbMerchantLeave>{

    private static final long serialVersionUID = 1L;
    public static final AbMerchantLeave dao = new AbMerchantLeave();

    public static String TABLE = "ab_merchant_leave";

	/** 主键 **/
    public static final String ID = "id";

	/** 用户ID **/
    public static final String USER_ID = "user_id";

	/** 商家ID **/
    public static final String MER_ID = "mer_id";
    
    /** 跑腿人员ID **/
    public static final String QDR_ID = "qdr_id";
    
    /** 订单ID **/
    public static final String ORDER_ID = "order_id";

	/** 留言时间 **/
    public static final String DATETIME = "datetime";

	/** 留言内容 **/
    public static final String CONTENT = "content";
 }