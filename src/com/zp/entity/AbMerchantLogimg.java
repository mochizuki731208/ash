package com.zp.entity;

import java.util.List;

import com.jfinal.plugin.activerecord.Model; 

public class AbMerchantLogimg extends Model<AbMerchantLogimg>{

    private static final long serialVersionUID = 1L;
    public static final AbMerchantLogimg dao = new AbMerchantLogimg();

    public static String TABLE = "ab_merchant_logimg";

	/** 图片ID **/
    public static final String ID = "id";

	/** 物流企业ID **/
    public static final String LID = "lid";

	/** 标题 **/
    public static final String TITLE = "title";

	/** 类型名称 **/
    public static final String TYPE_NAME = "type_name";

	/** 顺序号 **/
    public static final String SEQ_NUM = "seq_num";

	/** 大图 **/
    public static final String LAGER = "lager";

	/** 缩略图 **/
    public static final String THUMBNAIL = "thumbnail";

	/** 上传人ID **/
    public static final String SCRID = "scrid";

	/** 上传人名称 **/
    public static final String SCRMC = "scrmc";

	/** 上传时间 **/
    public static final String SCSJ = "scsj";

	/** 描述 **/
    public static final String DESCRIPTION = "description";
    
    public AbMerchantLogimg findByInfo(Object lid, Object num, Object desc) {
    	return findFirst("select * from ab_merchant_logimg where lid=? and seq_num=? and description=?", lid, num, desc);
    }
    
    public AbMerchantLogimg findByInfoShow(Object lid, Object num, Object desc) {
    	return findFirst("select * from ab_merchant_logimg where check_status='1' and lid=? and seq_num=? and description=?", lid, num, desc);
    }
    
    public List<AbMerchantLogimg> getListByInfo(Object lid, Object desc) {
    	return find("select * from ab_merchant_logimg where lid=? and description=? order by seq_num", lid, desc);
    }
    
    public List<AbMerchantLogimg> getListByInfoShow(Object lid, Object desc) {
    	return find("select * from ab_merchant_logimg where check_status='1' and lid=? and description=? order by seq_num", lid, desc);
    }
 }