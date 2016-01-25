package com.zp.entity;

import com.jfinal.plugin.activerecord.Model;

public class AbAddress  extends Model<AbAddress>{
	 private static final long serialVersionUID = 1L;
	    public static final AbAddress dao = new AbAddress();

	    public static String TABLE = "ab_address";

		/** 主键 **/
	    public static final String ID = "id";
	    
	    /** 地址**/
	    public static final String ADDRESS = "address";
	    
	    /** 精度 **/
	    public static final String JD = "jd";
	    
	    /** 纬度 **/
	    public static final String WD = "wd";
}
