package com.zp.entity;

import com.jfinal.plugin.activerecord.Model;

public class AbDriverPosition extends Model<AbDriverPosition> {
	private static final long serialVersionUID = 1L;
	public static final AbDriverPosition dao = new AbDriverPosition();
	public static String TABLE = "ab_sj_position";

	/** 地图位置标志 **/
	public static final String ID = "id";

	/** 司机ID **/
	public static final String DRIVER_ID = "sjid";
	
	/** 司机名称 **/
	public static final String DRIVER_NAME = "sjmc";

	/** 经度 **/
	public static final String JD = "jd";
	
	/** 维度 **/
	public static final String WD = "wd";
	
	/** 当前时间 **/
	public static final String DQSJ = "dqsj";
	
	/** 状态[0-离线，1-在线，2-空闲，3-忙碌，4-收工回家] **/
	public static final String ZT = "zt";

}
