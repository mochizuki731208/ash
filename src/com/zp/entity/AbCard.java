package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class AbCard extends Model<AbCard>{

    private static final long serialVersionUID = 1L;
    public static final AbCard dao = new AbCard();

    public static String TABLE = "ab_card";

	/** 主键 **/
    public static final String ID = "id";

	/** 卡号 **/
    public static final String CARD_NUMBER = "card_number";

	/** 卡类别ID **/
    public static final String TYPE_ID = "type_id";

	/** 状态 0 无效 1 有效 **/
    public static final String STATUS = "status";
    
	/** 激活状态 0 未激活 1激活 **/
    public static final String ACTIVE = "active";
    
	/** 购买状态 0 未购买 1购买 **/
    public static final String PURCHASE_STATUS = "purchase_status";
    
    /** 销售状态 1 线上 2线下 **/
    public static final String SALE_TYPE = "sale_type";
    
	/** 过期时间 **/
    public static final String EXPIRE_TIME = "expire_time";

    /** 创建时间 **/
    public static final String CREATE_TIME = "create_time";
    
    /** 激活时间 **/
    public static final String ACTIVE_TIME = "active_time";
    
    /** 余额 **/
    public static final String RMONEY = "rmoney";
 }