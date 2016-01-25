package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class SysUser extends Model<SysUser>{

    private static final long serialVersionUID = 1L;
    public static final SysUser dao = new SysUser();

    public static String TABLE = "sys_user";

	/** 主键 **/
    public static final String ID = "id";

	/** 用户名 **/
    public static final String LOGINID = "loginid";

	/** 密码 **/
    public static final String LOGINPWD = "loginpwd";

	/** 姓名 **/
    public static final String MC = "mc";

	/** 性别[0-男、1-女] **/
    public static final String SEX = "sex";

	/** 出生日期 **/
    public static final String BIRTH = "birth";

	/** 身份证号 **/
    public static final String IDCARD = "idcard";
    
	/** 身份证照片正面 **/
    public static final String SFZZPZM = "sfzzpzm";

	/** 身份证照片背面 **/
    public static final String SFZZPBM = "sfzzpbm";

	/** 半身免冠工作照 **/
    public static final String GZZP = "gzzp";

	/** 邮政编码 **/
    public static final String YZBM = "yzbm";

	/** 详细地址 **/
    public static final String XXDZ = "xxdz";

	/** 邮箱 **/
    public static final String EMAIL = "email";

	/** 手机号 **/
    public static final String MOBILE = "mobile";

	/** 电话 **/
    public static final String TEL = "tel";

	/** QQ **/
    public static final String QQ = "qq";

	/** 银行卡开户行 **/
    public static final String KHH = "khh";

	/** 银行卡号 **/
    public static final String KHHKH = "khhkh";

	/** 紧急联络人 **/
    public static final String JJLLR = "jjllr";

	/** 联络人电话 **/
    public static final String JJLLRDH = "jjllrdh";
    
	/** 经度 **/
    public static final String LNG = "lng";

	/** 维度 **/
    public static final String LAT = "lat";

	/** 邮箱认证状态[0-未认证、1-已认证、2-认证中] **/
    public static final String CERTIFICATEEMAIL = "certificateemail";

	/** 手机认证状态[0-未认证、1-已认证、2-认证中] **/
    public static final String CERTIFICATEMOBILE = "certificatemobile";

	/** 银行卡认证状态[0-未认证、1-已认证、2-认证中] **/
    public static final String CERTIFICATECARD = "certificatecard";

	/** 认证金额 **/
    public static final String CERTIFICATEMONEY = "certificatemoney";

	/** 是否审核[1-已审核、0-未审核] **/
    public static final String STATUS = "status";

	/** 是否可用[0-否、1-是] **/
    public static final String IS_ACCOUNT_ENABLED = "is_account_enabled";

	/** 是否锁定[0-否、1-是] **/
    public static final String IS_ACCOUNT_LOCKED = "is_account_locked";

	/** 锁定时间 **/
    public static final String LOCKED_DATE = "locked_date";

	/** 最后登录日期 **/
    public static final String LOGIN_DATE = "login_date";

	/** 最后登录IP **/
    public static final String LOGIN_IP = "login_ip";

	/** logo头像 **/
    public static final String LOGO = "logo";

	/** 积分 **/
    public static final String JIFEN = "jifen";

	/** 创建时间 **/
    public static final String CREATE_DATE = "create_date";

	/** 修改时间 **/
    public static final String MODIFY_DATE = "modify_date";

	/** 角色ID **/
    public static final String ROLE_ID = "role_id";

	/** 角色名称 **/
    public static final String ROLE_NAME = "role_name";

	/** 上级管理员 **/
    public static final String P_ID = "p_id";

	/** 账户余额 **/
    public static final String ZHYE = "zhye";

	/** 佣金比例 **/
    public static final String PER = "per";

	/** 备注 **/
    public static final String REMARK = "remark";

	/** 商户ID **/
    public static final String MID = "mid";
    
    /** 昵称 **/
    public static final String NICK_NAME = "nick_name";
    
    /** 年龄 **/
    public static final String AGE = "age";
    
    /** 星座 **/
    public static final String CONSTELLATION = "constellation";
    
    /** 身高 **/
    public static final String HEIGHT = "height";
    
    /** 签名 **/
    public static final String SIGN = "sign";
    
    /** 月薪 **/
    public static final String SALARY = "salary";
    
    /** 地区 **/
    public static final String REGION = "region";
    
    /** 职业 **/
    public static final String WORK = "work";
    
    /** 情感 **/
    public static final String MARRIAGE = "marriage";
    
    /** 抽烟 **/
    public static final String SMOKING = "smoking";
    
    /** 喝酒 **/
    public static final String DRINK = "drink";
    
    /** 城市 id **/
    public static final String CITY_ID = "city_id";
    
    /** 城市名称 **/
    public static final String CITY_NAME = "city_name";
    
	/** 邀请码 **/
    public static final String INVITE = "invite";
    private static final  String ZHZT  = "zhzt";
    private static final  String JYKSSJ  = "jykssj";
    private static final  String JYJZSJ  = "jyjzsj";
    
	/** 业务员客户评级 **/
    public static final String RATE = "rate";
    
	/** 业务员信用等级 **/
    public static final String CREDIT = "credittype";
    
	/** 手机设备的唯一标示 **/
    public static final String  DEVICEID = "deviceid";

    /**司机所属群组**/
    public static final String GROUPID = "groupid";
    
    /**车牌号*/
    public static final String CPH = "cph";
    
    /**车长*/
    public static final String CC = "cc";
    
    /**车型*/
    public static final String CX = "cx";
    
    /**车体积(立方)*/
    public static final String CTJ = "ctj";
    
    /**长跑城市*/
    public static final String CPCS = "cpcs";

 }