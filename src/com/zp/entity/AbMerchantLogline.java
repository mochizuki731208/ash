package com.zp.entity;

import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model; 
import com.jfinal.plugin.activerecord.tx.Tx;

public class AbMerchantLogline extends Model<AbMerchantLogline>{

    private static final long serialVersionUID = 1L;
    public static final AbMerchantLogline dao = new AbMerchantLogline();

    public static String TABLE = "ab_merchant_logline";

	/** 图片ID **/
    public static final String ID = "id";

	/** 物流企业ID **/
    public static final String LID = "lid";

	/** 出发地 **/
    public static final String FROM = "from";

	/** 目的地 **/
    public static final String TO = "to";

	/** 发车时间 **/
    public static final String FROM_TIME = "from_time";

	/** 到达时间 **/
    public static final String TO_TIME = "to_time";

	/** 按重量计价-价格 **/
    public static final String WEIGHT_PRICE = "weight_price";

	/** 按体积计价-价格 **/
    public static final String VOLUMN_PRICE = "volumn_price";

	/** 开始时间 **/
    public static final String START_TIME = "start_time";

	/** 结束时间 **/
    public static final String END_TIME = "end_time";
    
    /** 行驶天数 **/
    public static final String XSTS = "xsts";

	/** 描述 **/
    public static final String DESCRIPTION = "description";
    
    public List<AbMerchantLogline> getListByInfo(Object from, Object to) {
    	return find("select * from ab_merchant_logline where from=? and to=?", from, to);
    }
    
    public List<AbMerchantLogline> getListByInfo(Object lid) {
    	return find("select * from ab_merchant_logline where lid=?", lid);
    }
	
	public List<AbMerchantLogline> getListByInfoShow(Object lid) {
    	return find("select * from ab_merchant_logline where lid=?", lid);
    }
    
    public boolean deleteByLID(String lid) {
    	if(lid==null || lid.length() < 1) return false;
    	Db.update("delete from ab_merchant_logline where lid=?", lid);
    	return true;
    }
    
    @Before(Tx.class)
    public boolean deleteByIds(String[] ids) {
    	boolean flag = false;
    	if(ids==null || ids.length < 1) return false;
    	for (String id : ids) {
    		flag = deleteById(id);
		}
    	return flag;
    }
 }