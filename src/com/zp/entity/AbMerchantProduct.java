package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class AbMerchantProduct extends Model<AbMerchantProduct>{

    private static final long serialVersionUID = 1L;
    public static final AbMerchantProduct dao = new AbMerchantProduct();

    public static String TABLE = "ab_merchant_product";

	/** 主键 **/
    public static final String ID = "id";

	/** 城市ID **/
    public static final String CITY_ID = "city_id";

	/** 城市名称 **/
    public static final String CITY_NAME = "city_name";

	/** 商圈ID **/
    public static final String AREA_ID = "area_id";

	/** 商圈名称 **/
    public static final String AREA_NAME = "area_name";

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

	/** 商家名称 **/
    public static final String MC = "mc";

	/** 效果图 **/
    public static final String IMG_URL = "img_url";

	/** 缩略图 **/
    public static final String THUMBNAIL_URL = "thumbnail_url";

	/** 销售总量 **/
    public static final String SALENUM = "salenum";

	/** 商品介绍 **/
    public static final String PRODUCTINFO = "productinfo";

	/** 价格 **/
    public static final String PRICE = "price";

	/** 门店价 **/
    public static final String MDPRICE = "mdprice";

	/** 跑腿费 **/
    public static final String PTF = "ptf";

	/** 优惠金额 **/
    public static final String YHJE = "yhje";

	/** 赠送开始时间 **/
    public static final String YHKSSJ = "yhkssj";

	/** 优惠截止时间 **/
    public static final String YHJZSJ = "yhjzsj";

	/** 赠送积分 **/
    public static final String JF = "jf";

	/** 审核状态[0-未审核、1-已审核] **/
    public static final String ZT = "zt";

	/** 首页是否显示[0-否、1-是] **/
    public static final String SYSFXS = "sysfxs";

	/** 佣金 **/
    public static final String YJ = "yj";

	/** 商家ID **/
    public static final String MID = "mid";

	/** 商家名称 **/
    public static final String MNAME = "mname";

	/** 商品排序 **/
    public static final String SEQ_NUM = "seq_num";

	/** 创建时间 **/
    public static final String CREATETIME = "createtime";
 }