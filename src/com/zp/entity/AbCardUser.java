package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class AbCardUser extends Model<AbCardUser>{

    private static final long serialVersionUID = 1L;
    public static final AbCardUser dao = new AbCardUser();

    public static String TABLE = "ab_card_user";

	/** 主键 **/
    public static final String ID = "id";

	/** 用户ID **/
    public static final String USER_ID = "user_id";

	/** 卡ID **/
    public static final String CARD_ID = "card_id";

	/** 购买时间**/
    public static final String P_DATETIME = "p_datetime";

    /** 支付方式[0-充值直扣、1-支付宝 2-线下购买]**/
    public static final String P_WAY = "p_way";
    
    /** 备注**/
    public static final String REMARK = "remark";

 }