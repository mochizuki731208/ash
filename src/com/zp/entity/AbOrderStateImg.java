package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class AbOrderStateImg extends Model<AbOrderStateImg>{

    private static final long serialVersionUID = 1L;
    public static final AbOrderStateImg dao = new AbOrderStateImg();

    public static String TABLE = "ab_order_state_img";

	/** 主键 **/
    public static final String PK_ID = "pk_ab_order_img";
   
    /** 订单编号 **/
    public static final String ORDER_SN = "fk_order_sn";
    
    /** 司机编号 **/
    public static final String UID = "uid";
    
    /** 创建时间 **/
    public static final String CREATE_TIME = "create_time";
    
    /** 订单状态**/
    public static final String ORDER_STATE = "order_state";
    
    /** 拍照图片保存路径 **/
    public static final String ORDER_IMG_PATH = "order_img";
   
 }