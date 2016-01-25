package com.zp.entity;

import com.jfinal.plugin.activerecord.Model;

public class AbPolicy extends Model<AbPolicy> {

	private static final long serialVersionUID = 1L;

	public static final AbPolicy dao = new AbPolicy();

	public static final String ID = "id";
	public static final String insurant = "insurant";
	public static final String insurant_tel = "insurant_tel";
	public static final String type_insurant = "type_insurant";
	public static final String goods_p_type = "goods_p_type";
	public static final String goods_s_type = "goods_s_type";
	public static final String goods_name = "goods_name";
	public static final String packing = "packing";
	public static final String goods_num = "goods_num";
	public static final String order_sn = "order_sn";
	public static final String transport_type = "transport_type";
	public static final String transport_name = "transport_name";
	public static final String car_no = "car_no";
	public static final String start_site = "start_site";
	public static final String transfer_site = "transfer_site";
	public static final String end_site = "end_site";
	public static final String start_time = "start_time";
	public static final String zxtk = "zxtk";
	public static final String fjtk = "fjtk";
	public static final String policy_quota = "policy_quota";
	public static final String policy_cost = "policy_cost";
	public static final String policy_insurer = "policy_insurer";
	// 保单状态01:未支付 02:已支付 03:已完成
	public static final String policy_status = "policy_status";
	public static final String OPT_DATE = "opt_date";
	
	public static final String POLICY_SN = "policy_sn";
}
