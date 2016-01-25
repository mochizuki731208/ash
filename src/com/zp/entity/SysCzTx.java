package com.zp.entity;

import com.jfinal.plugin.activerecord.Model;

public class SysCzTx extends Model<SysCzTx> {
	private static final long serialVersionUID = 1L;
	public static final SysCzTx dao = new SysCzTx();
	public static String TABLE = "sys_cz_tx";

	/** 主键 **/
	public static final String ID = "id";
	/** 订单号 **/
	public static final String TRADENO = "tradeno";
	/** 0-充值 1-提现 2-付款 **/
	public static final String TYPE = "type";
	/** 0-等待 1-成功 2-失败 **/
	public static final String RESULT = "result";
	/** 发生金额 **/
	public static final String TOTALFEE = "totalfee";
	/** 登陆账户 **/
	public static final String USERID = "userid";
	
	public static final String ORDERID = "orderid";
	public static final String NUM = "num";
	public static final String DIPOSITID = "dipositid";
	public static final String YHKID = "yhkid";
	public static final String TXYHKID = "txyhkid";
	public static final String TXYHKNUM = "txyhknum";
	public static final String ZFBADDRESS = "zfbaddress";
	public static final String TXTYPE = "txtype";
	public static final String MC = "mc";
	public static final String TIME = "time";
	public static final String TOTIME = "totime";
	public static final String PC = "pc";
}

