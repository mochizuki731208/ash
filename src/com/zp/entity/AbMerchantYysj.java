package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class AbMerchantYysj extends Model<AbMerchantYysj>{

    private static final long serialVersionUID = 1L;
    public static final AbMerchantYysj dao = new AbMerchantYysj();

    public static String TABLE = "ab_merchant_yysj";

	/** 主键 **/
    public static final String ID = "id";

	/** 商家ID **/
    public static final String MID = "mid";

	/** 商家名称 **/
    public static final String MNAME = "mname";

	/** 星期 **/
    public static final String XQ = "xq";

	/** 上班时间 **/
    public static final String SBSJ = "sbsj";

	/** 下班时间 **/
    public static final String XBSJ = "xbsj";
 }