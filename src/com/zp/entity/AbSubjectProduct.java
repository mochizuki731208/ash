package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class AbSubjectProduct extends Model<AbSubjectProduct>{

    private static final long serialVersionUID = 1L;
    public static final AbSubjectProduct dao = new AbSubjectProduct();

    public static String TABLE = "ab_subject_product";

	/** 主键 **/
    public static final String ID = "id";

	/** 分类ID **/
    public static final String SUBJECT_ID = "subject_id";

	/** 名称 **/
    public static final String MC = "mc";
    
    /** 价格 **/
    public static final String PRICE = "price";
    
    /** 图片地址 **/
    public static final String IMG_URL = "img_url";
    
    /** 备注 **/
    public static final String REMARK = "remark";
    
 }