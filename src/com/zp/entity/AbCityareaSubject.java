package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class AbCityareaSubject extends Model<AbCityareaSubject>{

    private static final long serialVersionUID = 1L;
    public static final AbCityareaSubject dao = new AbCityareaSubject();

    public static String TABLE = "ab_cityarea_subject";

	/** 主键 **/
    public static final String ID = "id";

	/** 城市ID **/
    public static final String CITY_ID = "city_id";

	/** 分类ID **/
    public static final String SUBJECT_ID = "subject_id";
    
    /** 区域员工ID**/
    public static final String USER_ID = "user_id";
 }