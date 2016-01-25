package com.zp.entity;

import com.jfinal.plugin.activerecord.Model;

public class AbOrderSms extends Model<AbOrderSms> {
	private static final long serialVersionUID = 1L;
	public static final AbOrderSms dao = new AbOrderSms();

	public static String TABLE = "ab_order_sms";
	public static final String ID = "id";
	public static final String ORDERID = "orderid";
	public static final String SHRDH = "shrdh";
	public static final String YZM = "yzm";
	public static final String CREATETIME = "createtime";
	public static final String TIMELONG = "timelong";

}
