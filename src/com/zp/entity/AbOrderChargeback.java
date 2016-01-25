package com.zp.entity;

import com.jfinal.plugin.activerecord.Model;

public class AbOrderChargeback extends Model<AbOrderChargeback>{

    private static final long serialVersionUID = 1L;
    public static final AbOrderChargeback dao = new AbOrderChargeback();

    public static String TABLE = "ab_order_chargeback";

	/** 主键 **/
    public static final String ID = "id";
    
    /** 下单人ID **/
    public static final String XDR_ID = "xdr_id";

	/** 商户ID **/
    public static final String MER_ID = "mer_id";

	/** 订单ID **/
    public static final String ORDER_ID = "order_id";
    
    /** 接单人ID **/
    public static final String QDR_ID = "qdr_id";
    
    /** 仲裁状态  1 申请仲裁 2 仲裁中 3仲裁成功 4 仲裁失败 **/
    public static final String STATUS = "status";
    
    /** 退单流程状态 **/
    public static final String CB_STATUS = "cb_status";
    
    /**退单申请人ID **/
    public static final String APPLY_ID = "apply_id";
    
    /**退单人类型 申请人类型 1 用户 2商户 3业务员 **/
    public static final String APPLY_TYPE = "apply_type";

    /**退单申请描述 **/
    public static final String APPLY_DESC = "apply_desc";

    /**退单申请时间 **/
    public static final String APPLY_TIME = "apply_time";
    
    /**申请图片路径 **/
    public static final String APPLY_IMG_URL = "apply_img_url";
    
    /**申请图片 **/
    public static final String APPLY_IMG = "apply_img";
    
    /**回复内容 **/
    public static final String REP_DESC = "rep_desc";

    /**回复时间 **/
    public static final String REP_TIME = "rep_time";
    
    /**回复图片路径 **/
    public static final String REP_IMG_URL = "rep_img_url";

    /**回复图片 **/
    public static final String REP_IMG = "rep_img";
    
    /**仲裁申请人ID **/
    public static final String JUDGE_ID = "judge_id";
    
    /**仲裁描述 **/
    public static final String JUDGE_DESC = "judge_desc";

    /**申请仲裁时间 **/
    public static final String JUDGE_TIME = "judge_time";
    
    /**结果描述 **/
    public static final String RESULT_DESC = "result_desc";

    /**结果时间 **/
    public static final String RESULT_TIME = "result_time";
    
    /**客服ID **/
    public static final String SERVICE_ID = "service_id";
    
    /**退单金额 **/
    public static final String BACK_MONEY = "back_money";

}
