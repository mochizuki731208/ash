package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class TcConfig extends Model<TcConfig>{

    private static final long serialVersionUID = 1L;
    public static final TcConfig dao = new TcConfig();

    public static String TABLE = "ab_tc_config";

	/** 主键 **/
    public static final String ID = "id";

	/** 类型 **/
    public static final String TYPE = "type";

	/** 名称 **/
    public static final String NAME = "name";

	/** 关键词 **/
    public static final String KEY = "key";

	/** 值 **/
    public static final String VALUE = "value";

	/** 备注 **/
    public static final String DESC = "desc";
    
    /** 排序 **/
    public static final String LISTORDER = "listorder";

 }