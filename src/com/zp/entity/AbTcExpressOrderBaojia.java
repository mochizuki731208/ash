package com.zp.entity;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model; 

public class AbTcExpressOrderBaojia extends Model<AbTcExpressOrderBaojia>{

    private static final long serialVersionUID = 1L;
    public static final AbTcExpressOrderBaojia dao = new AbTcExpressOrderBaojia();

    public static String TABLE = "ab_tc_express_order_baojia";

	/** 订单编号 **/
    public static final String SN = "sn";

    /** 司机id **/
    public static final String SIJI_ID = "siji_id";
    
    /** 金额 **/
    public static final String MONEY = "money";
    
	public AbTcExpressOrderBaojia findBySnDriverid(String sn,String driverid){
    	return dao.findFirst("select * from ab_tc_express_order_baojia where sn=? and siji_id=?",sn,driverid);

	}

    public boolean updateByOrderDriver(String sn,String driverid,String money){
		String sql="update ab_tc_express_order_baojia set money=? where sn=? and siji_id=?";
		return Db.update(sql,money,sn,driverid)==1;
    }

 }