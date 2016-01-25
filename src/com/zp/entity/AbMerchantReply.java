package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class AbMerchantReply extends Model<AbMerchantReply>{

    private static final long serialVersionUID = 1L;
    public static final AbMerchantReply dao = new AbMerchantReply();

    public static String TABLE = "ab_merchant_reply";

	/** 主键 **/
    public static final String ID = "id";

	/** 留言ID **/
    public static final String LEAVE_ID = "leave_id";

	/** 回复内容 **/
    public static final String CONTENT = "content";

	/** 回复时间 **/
    public static final String DATETIME = "datetime";
 }