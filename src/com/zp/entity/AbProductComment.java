package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class AbProductComment extends Model<AbProductComment>{

    private static final long serialVersionUID = 1L;
    public static final AbProductComment dao = new AbProductComment();

    public static String TABLE = "ab_product_comment";

	/** 评论ID **/
    public static final String ID = "id";

	/** 商品ID **/
    public static final String PID = "pid";

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