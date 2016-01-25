package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class AbSubjectMerchant extends Model<AbSubjectMerchant>{

    private static final long serialVersionUID = 1L;
    public static final AbSubjectMerchant dao = new AbSubjectMerchant();

    public static String TABLE = "ab_subject_merchant";

	/** 主键 **/
    public static final String ID = "id";

	/** 商家ID **/
    public static final String M_ID = "m_id";

	/** 分类ID **/
    public static final String SUBJECT_ID = "subject_id";

	/** 跑腿费 **/
    public static final String SRVMONEY = "srvmoney";
 }