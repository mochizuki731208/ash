package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class SysAreaSubjectPtf extends Model<SysAreaSubjectPtf>{

    private static final long serialVersionUID = 1L;
    public static final SysAreaSubjectPtf dao = new SysAreaSubjectPtf();

    public static String TABLE = "sys_area_subject_ptf";
    
	/** 主键 **/
    public static final String ID = "id";

	/** 分类ID **/
    public static final String SUBJECT_ID = "subject_id";

	/** 商圈ID **/
    public static final String AREA_ID = "area_id";

	/** 跑腿费 **/
    public static final String SRVMONEY = "srvmoney";
 }