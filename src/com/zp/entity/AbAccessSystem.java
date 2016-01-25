package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class AbAccessSystem extends Model<AbAccessSystem>{

    private static final long serialVersionUID = 1L;
    public static final AbAccessSystem dao = new AbAccessSystem();

    public static String TABLE = "ab_access_system";

	/** 授权码 主键 **/
    public static final String ID = "id";

	/** 接入系统名称 **/
    public static final String NAME = "name";

	/** 详细描述 **/
    public static final String DESC = "desc";

	/** 联系人姓名 **/
    public static final String LINKMAN = "linkman";

	/** 联系人电话 **/
    public static final String LINKPHONE = "linkphone";

	/** 联系人手机 **/
    public static final String MOBILE = "mobile";

	/** 接入时间 **/
    public static final String CREATE_TIME = "create_time";
 }