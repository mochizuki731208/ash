package com.zp.entity;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

public class AbOrderQuote extends Model<AbOrderQuote> {

	private static final long serialVersionUID = 1L;
	public static final AbOrderQuote dao = new AbOrderQuote();
	/** 招标订单报价 */
	public static String TABLE = "ab_tc_express_order_baojia";
	
	/** 订单号 **/
	public static final String ORDER_SN = "sn";

	/** 司机的USER id **/
	public static final String UID = "siji_id";

	/** 报价时间 **/
	public static final String CREATE_TIME = "bj_time";

	/** 报价的具体金额 **/
	public static final String QUOTE_MONEY = "money";

	public AbOrderQuote findBySnDriverid(String sn,String driverid){
    	return dao.findFirst("select * from ab_tc_express_order_baojia where sn=? and siji_id=?",sn,driverid);

	}

    public boolean updateBySnDriver(String sn,String driverid,String money){
		String sql="update ab_tc_express_order_baojia set money=? where sn=? and siji_id=?";
		return Db.update(sql,money,sn,driverid)==1;
    }
}