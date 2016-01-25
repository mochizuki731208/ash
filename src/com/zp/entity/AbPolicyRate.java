package com.zp.entity;

import com.jfinal.plugin.activerecord.Model;

public class AbPolicyRate extends Model<AbPolicyRate> {
	private static final long serialVersionUID = 1L;
	
	public static AbPolicyRate dao = new AbPolicyRate();
	
	public static final String id = "id";
	public static final String to_self_rate = "to_self_rate";
	public static final String to_insurer_rate = "to_insurer_rate";

}
