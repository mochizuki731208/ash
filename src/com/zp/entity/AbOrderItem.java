package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class AbOrderItem extends Model<AbOrderItem>{

    private static final long serialVersionUID = 1L;
    public static final AbOrderItem dao = new AbOrderItem();

    public static String TABLE = "ab_order_item";

	/** 订单明细ID **/
    public static final String ID = "id";

	/** 订单ID **/
    public static final String ORDERID = "orderid";

	/** 创建时间 **/
    public static final String CREATETIME = "createtime";

	/** 修改时间 **/
    public static final String MODIFYTIME = "modifytime";

	/** 一级分类ID **/
    public static final String SUBJECT_ID = "subject_id";

	/** 一级分类名称 **/
    public static final String SUBJECT_NAME = "subject_name";

	/** 二级分类ID **/
    public static final String SUB_ID = "sub_id";

	/** 二级分类名称 **/
    public static final String SUB_NAME = "sub_name";

	/** 三级分类ID **/
    public static final String THR_ID = "thr_id";

	/** 三级分类名称 **/
    public static final String THR_NAME = "thr_name";

	/** 类型[0-货物类、1-服务类] **/
    public static final String IS_TYPE = "is_type";

	/** 商品ID **/
    public static final String ITEMID = "itemid";

	/** 商家名称 **/
    public static final String ITEMNAME = "itemname";

	/** 效果图 **/
    public static final String IMG_URL = "img_url";

	/** 缩略图 **/
    public static final String THUMBNAIL_URL = "thumbnail_url";

	/** 销售总量 **/
    public static final String QUANTITY = "quantity";

	/** 价格 **/
    public static final String PRICE = "price";

	/** 金额 **/
    public static final String TOTALMONEY = "totalmoney";

	/** 商品排序 **/
    public static final String SEQ_NUM = "seq_num";

	/** 跑腿费 **/
    public static final String PTF = "ptf";

	/** 佣金 **/
    public static final String YJ = "yj";

	/** 商家ID **/
    public static final String MID = "mid";

	/** 商家名称 **/
    public static final String MNAME = "mname";
 }