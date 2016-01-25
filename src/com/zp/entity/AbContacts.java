package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 
/**
 * 人脉
 * @author Administrator
 *
 */
public class AbContacts extends Model<AbContacts>{

    private static final long serialVersionUID = 1L;
    public static final AbContacts dao = new AbContacts();

    public static String TABLE = "ab_contacts";

	/** 主键 **/
    public static final String ID = "id";


	/** 用户ID **/
    public static final String UID = "uid";
    
    public static final String FRIEND = "friendid";
    /**1：商家，2：司机，3：用户*/
    public static final String FRIEND_TYPE = "friendtype";
    /**
     * 默认为0,1为关系关注
     */
    public static final String GZ = "gz";
    /**
     * 默认为0,1为关系好友
     */
    public static final String HY = "hy";
    /**
     * 好友电话
     */
    public static final String MOBILE = "mobile";
    /**
     * 时间
     */
    public static final String ADD_DATE = "add_date";


 }