package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class AbIntegralExchange extends Model<AbIntegralExchange>{

    private static final long serialVersionUID = 1L;
    public static final AbIntegralExchange dao = new AbIntegralExchange();

    public static String TABLE = "ab_integral_exchange";

	/** 主键 **/
    public static final String ID = "id";

	/** 积分商品ID **/
    public static final String GOODS_ID = "goods_id";

	/** 兑换用户ID **/
    public static final String USER_ID = "user_id";

	/** 兑换数量 **/
    public static final String COUNT = "count";

	/** 收件人 **/
    public static final String RECT = "rect";

	/** 收件人地址 **/
    public static final String RECT_ADRS = "rect_adrs";

	/** 收件人电话 **/
    public static final String RECT_MOBILE = "rect_mobile";

	/** 兑换时间 **/
    public static final String DATETIME = "datetime";

	/** 发货状态 0 未发货 1发货 **/
    public static final String SEND_STATUS = "send_status";

	/** 发货时间 **/
    public static final String SEND_TIME = "send_time";

	/** 快递名称 **/
    public static final String SEND_TYPE = "send_type";

	/** 快递单号 **/
    public static final String SEND_NUMBER = "send_number";

	/** 备注 **/
    public static final String REMARK = "remark";

	/** 支付方式 1 充值直扣，2 支付宝，3 货到付款, 4 无支付 **/
    public static final String PAY_TYPE = "pay_type";
 }