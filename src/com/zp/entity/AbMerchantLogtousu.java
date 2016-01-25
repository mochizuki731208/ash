package com.zp.entity;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model; 
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;

public class AbMerchantLogtousu extends Model<AbMerchantLogtousu>{

    private static final long serialVersionUID = 1L;
    public static final AbMerchantLogtousu dao = new AbMerchantLogtousu();

    public static String TABLE = "ab_merchant_logtousu";

	/** ID **/
    public static final String ID = "id";

	/** 物流企业ID **/
    public static final String LID = "lid";

	/** 联系人 **/
    public static final String CONTACT = "contact";

	/** 联系电话 **/
    public static final String PHONE = "phone";

	/** 内容 **/
    public static final String COMMON = "common";

    /** 是否已读 **/
    public static final String BREAD = "bread";
    
    public void deleteByLID(String lid) {
    	if(lid==null || lid.length() < 1) return;
    	Db.delete("ab_merchant_logtousu", new Record().set("lid", lid));
    }
    
    @Before(Tx.class)
    public void deleteByIds(String[] ids) {
    	if(ids==null || ids.length < 1) return;
    	for (String id : ids) {
			deleteById(id);
		}
    }
    
    public void readById(String id) {
    	if(id==null || id.length() < 1) return;
    	Db.update("update ab_merchant_logtousu set bread=? where id=?",	"1", id);
    }
    
    @Before(Tx.class)
    public void readByIds(String[] ids) {
    	if(ids==null || ids.length < 1) return;
    	for (String id : ids) {
    		readById(id);
		}
    }
 }