package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class AbIntegralGoods extends Model<AbIntegralGoods>{

    private static final long serialVersionUID = 1L;
    public static final AbIntegralGoods dao = new AbIntegralGoods();

    public static String TABLE = "ab_integral_goods";

	/** 主键 **/
    public static final String ID = "id";

	/** 商品标题信息 **/
    public static final String TITLE = "title";

	/** 商户图片 **/
    public static final String IMAGE = "image";

	/**  **/
    public static final String IMG_URL = "img_url";

	/** 所需积分 **/
    public static final String SCORE = "score";

	/** 商户金额 **/
    public static final String MONEY = "money";

	/** 每人限制兑换数量 **/
    public static final String LIMIT = "limit";

	/** 商品数量 **/
    public static final String COUNT = "count";

	/** 状态 0 无效 1 有效 **/
    public static final String STATUS = "status";

	/** 兑换开始日期 **/
    public static final String STARTTIME = "starttime";

	/** 兑换结束日期 **/
    public static final String ENDTIME = "endtime";

	/** 商品详情 **/
    public static final String DESCRIPTION = "description";
 }