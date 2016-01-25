package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class AbUserAddr extends Model<AbUserAddr>{

    private static final long serialVersionUID = 1L;
    public static final AbUserAddr dao = new AbUserAddr();

    public static String TABLE = "ab_user_address";

	/** 主键 **/
    public static final String ID = "id";

	/** 名称 **/
    public static final String NAME = "name";

	/** 地址 **/
    public static final String ADDR = "addr";

	/** 排序值 **/
    public static final String ORDER_NUM = "order_num";

	/** 所属用户的id **/
    public static final String USER_ID = "user_id";
    
    public static final String linkman = "linkman";
    public static final String mobile = "mobile";
 }