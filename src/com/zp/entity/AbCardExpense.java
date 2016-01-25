package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class AbCardExpense extends Model<AbCardExpense>{

    private static final long serialVersionUID = 1L;
    public static final AbCardExpense dao = new AbCardExpense();

    public static String TABLE = "ab_card_expenwse";

	/** 主键 **/
    public static final String ID = "id";

	/** 订单ID **/
    public static final String ORDER_ID = "order_id";

	/** 卡ID **/
    public static final String CARD_ID = "card_id";

	/** 抵扣金额**/
    public static final String MONEY = "money";
    
	/** 状态 1有效 0无效**/
    public static final String STATUS = "status";
    
	/** 备注**/
    public static final String REMARK = "remark";

 }