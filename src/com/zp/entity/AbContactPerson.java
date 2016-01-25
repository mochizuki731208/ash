package com.zp.entity;

import com.jfinal.plugin.activerecord.Model;

public class AbContactPerson extends Model<AbContactPerson> {

	public static final AbContactPerson dao = new AbContactPerson();
	
	private static final long serialVersionUID = 1L;
	public static String ID = "id";
	public static String LINKMAN = "linkman";
	public static String MOBILE = "mobile";
	public static String USER_ID = "user_id";
	public static String OPT_DATE = "opt_date";
}
