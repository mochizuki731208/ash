package com.zp.entity;

import com.jfinal.plugin.activerecord.Model;

public class AbSjPosition extends Model<AbSjPosition>{
	private static final long serialVersionUID = 1L;
	public static final AbSjPosition dao = new AbSjPosition();
	public static String TABLE = "ab_sj_position";

	/** 主键 **/
    public static final String ID = "id";
    public static final String SJID = "sjid";
    public static final String SJMC = "sjmc";
    public static final String JD = "jd";
    public static final String WD = "wd";
    public static final String DQSJ = "dqsj";
    public static final String ZT = "zt";
}
