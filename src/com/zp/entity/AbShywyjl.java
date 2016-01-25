package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class AbShywyjl extends Model<AbShywyjl>{

    private static final long serialVersionUID = 1L;
    public static final AbShywyjl dao = new AbShywyjl();

    public static String TABLE = "ab_shywyjl";

	/** 订单明细ID **/
    public static final String ID = "id";

	/** 商家ID **/
    public static final String MID = "mid";

	/** 商家名称 **/
    public static final String MNAME = "mname";

	/** 所属城市ID **/
    public static final String CITYID = "cityid";

	/** 所属城市名称 **/
    public static final String CITYMC = "citymc";

	/** 一级分类ID **/
    public static final String SUBJECT_ID = "subject_id";

	/** 一级分类名称 **/
    public static final String SUBJECT_NAME = "subject_name";

	/** 商家地理经度 **/
    public static final String SJJD = "sjjd";

	/** 商家地理纬度 **/
    public static final String SJWD = "sjwd";

	/** 业务员ID **/
    public static final String YWYID = "ywyid";

	/** 业务员名称 **/
    public static final String YWYMC = "ywymc";

	/** 商家地理经度 **/
    public static final String YWYJD = "ywyjd";

	/** 商家地理纬度 **/
    public static final String YWYWD = "ywywd";

	/** 两者距离[单位：米] **/
    public static final String LZJL = "lzjl";
 }