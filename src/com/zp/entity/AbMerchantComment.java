package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class AbMerchantComment extends Model<AbMerchantComment>{

    private static final long serialVersionUID = 1L;
    public static final AbMerchantComment dao = new AbMerchantComment();

    public static String TABLE = "ab_merchant_comment";

	/** 评论ID **/
    public static final String ID = "id";

	/** 商家ID **/
    public static final String MID = "mid";

	/** 信用评级 **/
    public static final String RATE = "rate";

	/** 服务评级 **/
    public static final String GRADE = "grade";

	/** 标题 **/
    public static final String TITLE = "title";

	/** 评论内容 **/
    public static final String CONTENT = "content";

	/** 评论时间 **/
    public static final String CREATETIME = "createtime";

	/** 评论IP **/
    public static final String IP = "ip";

	/** 评论人ID **/
    public static final String USERID = "userid";

	/** 评论人名称 **/
    public static final String USERNAME = "username";

	/** 备注 **/
    public static final String REMARK = "remark";
 }