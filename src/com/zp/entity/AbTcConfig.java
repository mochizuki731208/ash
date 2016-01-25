package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class AbTcConfig extends Model<AbTcConfig>{

    private static final long serialVersionUID = 1L;
    public static final AbTcConfig dao = new AbTcConfig();

    public static String TABLE = "ab_tc_config";

	/** 主键 **/
    public static final String ID = "id";

	/**  **/
    public static final String TYPE = "type";

	/** 名称，如：车辆种类、货物种类 **/
    public static final String NAME = "name";

	/** 关键词 **/
    public static final String KEY = "key";

	/** 值 **/
    public static final String VALUE = "value";

	/** 描述 **/
    public static final String DESC = "desc";
    
    /** 描述 **/
    public static final String CITY_ID = "city_id";

	/** 排序值 **/
    public static final String LISTORDER = "listorder";
 }