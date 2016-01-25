package com.zp.entity;

import java.util.List;

import com.jfinal.plugin.activerecord.Model; 

public class AbMerchantImg extends Model<AbMerchantImg>{

    private static final long serialVersionUID = 1L;
    public static final AbMerchantImg dao = new AbMerchantImg();

    public static String TABLE = "ab_merchant_img";

	/** 图片ID **/
    public static final String ID = "id";

	/** 商家ID **/
    public static final String MID = "mid";

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
    
    
    public List<AbMerchantImg> findByMidShow(Object mid) {
    	if(mid==null) return null;
    	return find("select * from ab_merchant_img where mid=?", mid);
    }

 }