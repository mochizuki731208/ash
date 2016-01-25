package com.zp.entity;

import com.jfinal.plugin.activerecord.Model;

public class AbMerchantLogistics extends Model<AbMerchantLogistics> {

	private static final long serialVersionUID = 1L;
	
	public static final AbMerchantLogistics dao = new AbMerchantLogistics();
	
	public static final String TABLE = "ab_merchant_logistics";
	
	/** 信息ID **/
    public static final String ID = "id";
    /** 商铺ID **/
    public static final String MID = "mid";
    /** 主营线路 **/
    public static final String MAINLINE = "mainline";
    /** 营运模式 **/
    public static final String SMODE = "smode";
    /** 运输方式 **/
    public static final String TRANSTYPE = "transtype";
    /** 运输承保公司 **/
    public static final String TICC = "ticc";
    /** 放款周期 **/
    public static final String LOAN_PERIOD = "loan_period";
    /** 放款时间 **/
    public static final String LOAN_TIME = "loan_time";
    /** 放款方式 **/
    public static final String LOAN_TYPE = "loan_type";
    /** 查款电话 **/
    public static final String CHECKPHONE = "checkphone";
    /** QQ **/
    public static final String QQ = "qq";
    /** 收货地址 **/
    public static final String RECADDRESS = "recaddress";
    /** 卸货地址 **/
    public static final String DISADDRESS = "disaddress";
    /** 公司简介 **/
    public static final String PROFILE = "profile";
    /** 服务承诺 **/
    public static final String SERVPROMISE = "servpromise";
    /** 负责人 **/
    public static final String CHIEF = "chief";
    /** 服务监督电话 **/
    public static final String SERVHOTLINE = "servhotline";
    
    /**
     * 公司场景
     * 公司荣誉
     * 诚信档案
     * 都放在
     * 
     * **/
    
    public AbMerchantLogistics findByMid(Object mid) {
    	if(mid==null) return null;
    	return findFirst("select * from ab_merchant_logistics where mid=?", mid);
    }
    
    public AbMerchantLogistics findByMidShow(Object mid) {
    	if(mid==null) return null;
    	return findFirst("select * from ab_merchant_logistics where check_status='1' and mid=?", mid);
    }

}
