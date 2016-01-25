package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class AbSubjectProductOrder extends Model<AbSubjectProductOrder>{

    private static final long serialVersionUID = 1L;
    public static final AbSubjectProductOrder dao = new AbSubjectProductOrder();

    public static String TABLE = "ab_subject_product_order";

	/** 产品id **/
    public static final String ID = "pid";

	/** 订单ID **/
    public static final String SUBJECT_ID = "oid";

    /** 数量 **/
    public static final String NUM = "num";
 }