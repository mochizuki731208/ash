package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class AbOrderItemchargeoff extends Model<AbOrderItemchargeoff>{

    private static final long serialVersionUID = 1L;
    public static final AbOrderItemchargeoff dao = new AbOrderItemchargeoff();

    public static String TABLE = "ab_order_itemchargeoff";

	/** 退单ID **/
    public static final String ID = "id";

	/** 订单ID **/
    public static final String ORDERID = "orderid";

	/** 抢单人ID **/
    public static final String QDRID = "qdrid";

	/** 抢单人名称 **/
    public static final String QDRNAME = "qdrname";
    /** 是否中标0，未被货主选择，1已被货主选择**/
    public static final String RESTULT = "result";

	/** 退单原因 **/
    public static final String REASON = "reason";

	/** 商家ID **/
    public static final String MID = "mid";

	/** 商家名称 **/
    public static final String MNAME = "mname";
 }