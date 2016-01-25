package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class AbIntegralScore extends Model<AbIntegralScore>{

    private static final long serialVersionUID = 1L;
    public static final AbIntegralScore dao = new AbIntegralScore();

    public static String TABLE = "ab_integral_score";

	/** 主键 **/
    public static final String ID = "id";

	/** 积分变化类型   **/
    public static final String TYPE = "type";

	/** 分值 **/
    public static final String VALUE = "value";

	/** 积分变动时间 **/
    public static final String DATETIME = "datetime";

	/** 用户ID **/
    public static final String USER_ID = "user_id";

	/** 外联ID **/
    public static final String FID = "fid";
 }