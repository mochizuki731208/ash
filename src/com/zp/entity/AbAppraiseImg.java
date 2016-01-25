package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class AbAppraiseImg extends Model<AbAppraiseImg>{

    private static final long serialVersionUID = 1L;
    public static final AbAppraiseImg dao = new AbAppraiseImg();

    public static String TABLE = "ab_appraise_img";

	/** 主键 **/
    public static final String ID = "id";

	/** 上传图片 **/
    public static final String IMAGE = "image";

	/** 图片路径 **/
    public static final String IMG_URL = "img_url";

	/** 评价ID **/
    public static final String APP_ID = "app_id";
 }