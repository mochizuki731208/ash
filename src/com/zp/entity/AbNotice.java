package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 
/**
 * 系统消息
 * @author Administrator
 *
 */
public class AbNotice extends Model<AbNotice>{

    private static final long serialVersionUID = 1L;
    public static final AbNotice dao = new AbNotice();

    public static String TABLE = "ab_notice";

	/** 主键 **/
    public static final String ID = "id";


	/** 用户ID **/
    public static final String UID = "uid";
    
    public static final String SEND_NAME = "send_name";
    
    public static final String RECEIVE_NAME = "receive_name";
    
    public static final String REV_ID = "revid";
    /**来源方式1：极光推送，0：系统新增*/
    public static final String IS_JPUSH = "is_jpush";
   /**
    * 消息状态1：已读,0:未读，-1:删除
    */
    public static final String FRIEND_TYPE = "status";
    
    /**
     * 消息分组1、物流状态，2、系统通知，3、及时聊天，4、客服服务
     */
    public static final String NGROUP = "ngroup";
  
    /**
     * 消息内容
     */
    public static final String NCONTENT = "ncontent";
    /**
     * 标题
     */
    public static final String NTITLE = "ntitle";
    /**
     * 图文消息url
     */
    public static final String NURL = "nurl";
    /**
     * 发送时间
     */
    public static final String NDATE = "ndate";


 }