package com.zp.entity;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

public class AbFmcarOrderBaoJia extends Model<AbFmcarOrderBaoJia>{

    private static final long serialVersionUID = 1L;
    public static final AbFmcarOrderBaoJia dao = new AbFmcarOrderBaoJia();
    public static String TABLE = "ab_fmcar_order_baojia";
    
    
    public AbFmcarOrderBaoJia  findByOrderDriver(String orderid,String driverid){
    	return dao.findFirst("select * from ab_fmcar_order_baojia where orderid=? and sjid=?",orderid,driverid);
    	
    }
    public boolean updateByOrderDriver(String orderid,String driverid,String money){
		String sql="update ab_fmcar_order_baojia set price=? where orderid=? and sjid=?";
		return Db.update(sql,money,orderid,driverid)==1;
    }
 }