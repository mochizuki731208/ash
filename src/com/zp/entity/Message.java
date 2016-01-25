package com.zp.entity;

import com.jfinal.plugin.activerecord.Model;

public class Message extends Model<Message>{
	 private static final long serialVersionUID = 1L;
	 public static final Message dao=new Message();
	 
	 public static String TABLE="message";
	 
	 public static final String ID="id";
	 
	 public static final String USER_ID="user_id";

	 public static final String ROLE_ID="role_id";
	 
	 public static final String MES_TYPE="mes_type";
	 
	 public static final String ISREAD="isread";
	 
	 public static final String TEXT="text";
	 
	 public static final String SEND_DATE="send_date";
}
