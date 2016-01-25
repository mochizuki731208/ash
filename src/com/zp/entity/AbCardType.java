package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class AbCardType extends Model<AbCardType>{

    private static final long serialVersionUID = 1L;
    public static final AbCardType dao = new AbCardType();

    public static String TABLE = "ab_card_type";

	/** 主键 **/
    public static final String ID = "id";
    
    /** 会员卡名称 **/
    public static final String NAME = "name";

	/** 卡类型 1 送餐卡 2 消费卡 **/
    public static final String TYPE = "type";

	/** 卡类型状态 0. 有效 1无效 **/
    public static final String STATUS = "status";

	/** 卡面值 **/
    public static final String MONEY = "money";
    
    /** 购买金额 **/
    public static final String PRICE = "price";
    
	/** 有效天数 **/
    public static final String VALID_DAYS = "valid_days";
    
    /** 会员卡图片 **/
    public static final String IMAGE = "image";
    
    /** 图片路径 **/
    public static final String IMG_URL = "img_url";
    
    /** 创建时间 **/
    public static final String CREATE_TIME = "create_time";
    
 }