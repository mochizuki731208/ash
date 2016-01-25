package com.zp.entity;

import com.jfinal.plugin.activerecord.Model; 

public class TcExpressOrder extends Model<TcExpressOrder>{

    private static final long serialVersionUID = 1L;
    public static final TcExpressOrder dao = new TcExpressOrder();

    public static String TABLE = "ab_tc_express_order";

	/** 主键 **/
    public static final String ID = "id";

	/** 订单编号 **/
    public static final String SN = "sn";

	/** 快递订单模式，1：悬赏，2：招标 **/
    public static final String STYLE = "style";

	/** 车辆类型，小面包、金杯车 **/
    public static final String CAR = "car";

	/** 起步价 **/
    public static final String START_PRICE = "start_price";

	/** 超公里价 **/
    public static final String OVER_UNIT_PRICE = "over_unit_price";
    
    /** 夜间服务费 **/
    public static final String NIGHT_PRICE = "night_price";
    
    /** 货物类型 **/
    public static final String GOODS_TYPE = "goods_type";
    
    /** 发货人姓名 **/
    public static final String SEND_NAME = "send_name";
    
    /** 发货联系人电话 **/
    public static final String SEND_PHONE = "send_phone";
    
    /** 发送地址 **/
    public static final String SEND_ADDR = "send_addr";
    
    /** 收货人姓名 **/
    public static final String RCV_NAME1 = "rcv_name1";
    
    /** 收货联系电话 **/
    public static final String RCV_PHONE1 = "rcv_phone1";
    
    /** 收货地址 **/
    public static final String RCV_ADDR1 = "rcv_addr1";
    
    /** 收货人姓名2 **/
    public static final String RCV_NAME2 = "rcv_name2";
    
    /** 收货联系电话2 **/
    public static final String RCV_PHONE2 = "rcv_phone2";
    
    /** 收货地址2 **/
    public static final String RCV_ADDR2 = "rcv_addr2";
    
    /** 收货人姓名3 **/
    public static final String RCV_NAME3 = "rcv_name3";
    
    /** 收货联系电话3 **/
    public static final String RCV_PHONE3 = "rcv_phone3";
    
    /** 收货地址3 **/
    public static final String RCV_ADDR3 = "rcv_addr3";
    
    /** 收货人姓名4 **/
    public static final String RCV_NAME4 = "rcv_name4";
    
    /** 收货联系电话4 **/
    public static final String RCV_PHONE4 = "rcv_phone4";
    
    /** 收货地址4 **/
    public static final String RCV_ADDR4 = "rcv_addr4";
    
    /** 收货人姓名5 **/
    public static final String RCV_NAME5 = "rcv_name5";
    
    /** 收货联系电话5 **/
    public static final String RCV_PHONE5 = "rcv_phone5";
    
    /** 收货地址5 **/
    public static final String RCV_ADDR5 = "rcv_addr5";
    
    /** 公里数 **/
    public static final String KILO = "kilo";
    
    /** 发货时间 **/
    public static final String SEND_TIME = "send_time";
    
    /** 支付方式 **/
    public static final String PAY_TYPE = "pay_type";
    
    /** 回单价格 **/
    public static final String HUIDAN_PRICE = "huidan_price";
    
    /** 用户id **/
    public static final String USER_ID = "user_id";
    
    /** 用户id **/
    public static final String LIFT = "lift";
    
    /** 悬赏金额 **/
    public static final String MORE_MONEY = "more_money";
    
    /** 重量公斤 **/
    public static final String WEIGHT = "weight";
    
    /** 需要小推车 **/
    public static final String CART = "cart";
    
    /** 跟车人数 **/
    public static final String PEOPLE = "people";
	/** 运输类型 **/
	public static final String TRANSFER_TYPE = "transfer_type";
 }