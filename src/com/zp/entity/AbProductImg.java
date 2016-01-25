package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class AbProductImg extends Model<AbProductImg>{

    private static final long serialVersionUID = 1L;
    public static final AbProductImg dao = new AbProductImg();

    public static String TABLE = "ab_product_img";

	/** 图片ID **/
    public static final String ID = "id";

	/** 外键ID **/
    public static final String F_ID = "f_id";

	/** 标题 **/
    public static final String TITLE = "title";

	/** 类型名称 **/
    public static final String TYPE_NAME = "type_name";

	/** 顺序号 **/
    public static final String SEQ_NUM = "seq_num";

	/** 大图 **/
    public static final String LAGER = "lager";

	/** 缩略图 **/
    public static final String THUMBNAIL = "thumbnail";

	/** 上传人ID **/
    public static final String SCRID = "scrid";

	/** 上传人名称 **/
    public static final String SCRMC = "scrmc";

	/** 上传时间 **/
    public static final String SCSJ = "scsj";

	/** 描述 **/
    public static final String DESCRIPTION = "description";
 }