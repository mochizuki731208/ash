package com.zp.entity;

import com.jfinal.plugin.activerecord.Model;

public class AbOrderChargebackItem extends Model<AbOrderChargebackItem>{

    private static final long serialVersionUID = 1L;
    public static final AbOrderChargebackItem dao = new AbOrderChargebackItem();

    public static String TABLE = "ab_order_chargeback_item";

	/** 主键 **/
    public static final String ID = "id";
    
    /** 退单ID **/
    public static final String CBID = "cbid";
    
    /** 用户ID **/
    public static final String UID = "uid";
    
    /** 内容 **/
    public static final String CONTENT = "content";

    /**图片路径 **/
    public static final String IMG_URL = "img_url";
    
    /**图片 **/
    public static final String IMAGE = "image";
    
    /**补充人类型 1 用户 2商户**/
    public static final String TYPE = "type";

    /**补充时间 **/
    public static final String DATETIME = "datetime";
    
}
