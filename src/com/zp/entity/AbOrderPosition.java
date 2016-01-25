package com.zp.entity;

import com.jfinal.plugin.activerecord.Model;

public class AbOrderPosition extends Model<AbOrderPosition> {
	private static final long serialVersionUID = 1L;
	public static final AbOrderPosition dao = new AbOrderPosition();
	public static String TABLE = "ab_order_position";

	/** 地图位置标志 **/
	public static final String ID = "id";

	/** 订单ID **/
	public static final String ORDERID = "orderid";

	/** 商家ID **/
	public static final String MID = "mid";
	/** 商家名称 **/
	public static final String MNAME = "mname";
	/** 送货人ID **/
	public static final String SHRID = "shrid";
	/** 送货人名称 **/
	public static final String SHRMC = "shrmc";
	/** 经度 **/
	public static final String JD = "jd";
	/** 维度 **/
	public static final String WD = "wd";
	/** 当前时间 **/
	public static final String DQSJ = "dqsj";
	/** 状态[0-离线，1-在线，2-空闲，3-忙碌，4-收工回家] **/
	public static final String ZT = "zt";
}
