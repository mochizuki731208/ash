package com.zp.entity;

import com.jfinal.plugin.activerecord.Model;

/**
 * @author gengsan(71951)
 * @classname:AbTcExpressOrderDiaoDu
 * @date:2015年9月27日上午12:11:13
 */
public class AbTcExpressOrderDiaoDu extends Model<AbTcExpressOrderDiaoDu> {

	private static final long serialVersionUID = 1L;
	public static final AbTcExpressOrderDiaoDu dao = new AbTcExpressOrderDiaoDu();
	/** 招标订单报价 */
	public static String TABLE = "ab_tc_express_order_diaodu";

}
