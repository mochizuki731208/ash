package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class AbAppraise extends Model<AbAppraise>{

    private static final long serialVersionUID = 1L;
    public static final AbAppraise dao = new AbAppraise();

    public static String TABLE = "ab_appraise";

	/** 主键 **/
    public static final String ID = "id";

	/** 用户ID **/
    public static final String USER_ID = "user_id";

	/** 商户ID **/
    public static final String MER_ID = "mer_id";

	/** 订单ID **/
    public static final String ORDER_ID = "order_id";
    
	/** 接单人ID **/
    public static final String QDR_ID = "qdr_id";

	/** 评价时间 **/
    public static final String DATETIME = "datetime";

	/** 评价类型 1 好评 2中评 3差评 **/
    public static final String TYPE = "type";

	/**  评价说明  **/
    public static final String CONTENT = "content";

	/** 动态评分-描述相符 **/
    public static final String DYNC_MAH = "dync_mah";

	/** 动态评分-服务态度 **/
    public static final String DYNC_SER = "dync_ser";

	/** 动态评分- 发货速度 **/
    public static final String DYNC_SPD = "dync_spd";

	/**  **/
    public static final String DYNC_VAL = "dync_val";

	/** 是否匿名 1公开 2匿名 **/
    public static final String PRIVATE = "private";
 }