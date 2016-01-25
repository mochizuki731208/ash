package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class AbUserCityarea extends Model<AbUserCityarea>{

    private static final long serialVersionUID = 1L;
    public static final AbUserCityarea dao = new AbUserCityarea();

    public static String TABLE = "ab_user_cityarea";

	/** 编码 **/
    public static final String ID = "id";

	/** 用户ID **/
    public static final String USER_ID = "user_id";

	/** 区域ID **/
    public static final String CITY_ID = "city_id";
 }