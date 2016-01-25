package com.zp.entity;

import com.jfinal.plugin.activerecord.Model;

public class AbTcExpressOrder extends Model<AbTcExpressOrder>{

    private static final long serialVersionUID = 1L;
    public static final AbTcExpressOrder dao = new AbTcExpressOrder();

    public static String TABLE = "ab_tc_express_order";

	/** 订单ID **/
    public static final String ID = "id";

	/** 订单编号 **/
    public static final String SN = "sn";

	/** 快递订单模式，1：悬赏，2：招标 **/
    public static final String STYLE = "style";

	/** 车辆类型，小面包、金杯车 **/
    public static final String CAR = "car";

	/** 起步价 **/
    public static final String START_PRICE = "start_price";

	/** 超公里单价 **/
    public static final String OVER_UNIT_PRICE = "over_unit_price";

	/** 夜间服务费 **/
    public static final String NIGHT_PRICE = "night_price";

	/** 货物类型 **/
    public static final String GOODS_TYPE = "goods_type";

	/** 送货姓名 **/
    public static final String SEND_NAME = "send_name";

	/** 送货电话 **/
    public static final String SEND_PHONE = "send_phone";

	/** 送货地址 **/
    public static final String SEND_ADDR = "send_addr";

	/** 收货姓名 **/
    public static final String RCV_NAME1 = "rcv_name1";

	/** 收货电话 **/
    public static final String RCV_PHONE1 = "rcv_phone1";

	/** 收货地址 **/
    public static final String RCV_ADDR1 = "rcv_addr1";

	/** 收货姓名 **/
    public static final String RCV_NAME2 = "rcv_name2";

	/** 收货电话 **/
    public static final String RCV_PHONE2 = "rcv_phone2";

	/** 收货地址 **/
    public static final String RCV_ADDR2 = "rcv_addr2";

	/** 收货姓名 **/
    public static final String RCV_NAME3 = "rcv_name3";

	/** 收货电话 **/
    public static final String RCV_PHONE3 = "rcv_phone3";

	/** 收货地址 **/
    public static final String RCV_ADDR3 = "rcv_addr3";

	/** 收货姓名 **/
    public static final String RCV_NAME4 = "rcv_name4";

	/** 收货电话 **/
    public static final String RCV_PHONE4 = "rcv_phone4";

	/** 收货地址 **/
    public static final String RCV_ADDR4 = "rcv_addr4";

	/** 收货姓名 **/
    public static final String RCV_NAME5 = "rcv_name5";

	/** 收货电话 **/
    public static final String RCV_PHONE5 = "rcv_phone5";

	/** 收货地址 **/
    public static final String RCV_ADDR5 = "rcv_addr5";

	/** 行驶里程 **/
    public static final String KILO = "kilo";

	/** 送货时间 **/
    public static final String SEND_TIME = "send_time";

	/** 支付方式 **/
    public static final String PAY_TYPE = "pay_type";

	/** 回单价格 **/
    public static final String HUIDAN_PRICE = "huidan_price";

	/** 代收货款，服务费用 **/
    public static final String SERVICE_PRICE = "service_price";

	/** 最终费用 **/
    public static final String TOTAL_PRICE = "total_price";

	/**  **/
    public static final String USER_ID = "user_id";

	/** 有无电梯，1有，0无 **/
    public static final String LIFT = "lift";

	/** 悬赏金额 **/
    public static final String MORE_MONEY = "more_money";

	/** 货物重量（公斤） **/
    public static final String WEIGHT = "weight";

	/** 跟车人数 **/
    public static final String PEOPLE = "people";

	/** 需要小推车（1需要，其他不需要） **/
    public static final String CART = "cart";
    
	/** 审核状态, 1:通过，0或NULL：未通过 **/
    public static final String SHENHE = "shenhe";
    
	/** 最低价格 **/
    public static final String MIN_PRICE = "min_price";
    
	/** 最高价格 **/
    public static final String MAX_PRICE = "max_price";
    
	/** 备注 **/
    public static final String REMARK = "remark";
    
	/** 其他字段 **/
    public static final String PIC_SEND = "pic_send";
    public static final String PIC_ARRIVAL = "pic_arrival";
    public static final String GOODS_MOUNT = "goods_mount";
    public static final String GOODS_VOLUMN = "goods_volumn";
    
	/** 车长 **/
    public static final String CC = "cc";
    
	/** 经纬度 **/
    public static final String SEND_ADDR_WD = "send_addr_wd";
    public static final String SEND_ADDR_JD = "send_addr_jd";
    public static final String RCV_ADDR1_WD = "rcv_addr1_wd";
    public static final String RCV_ADDR1_JD = "rcv_addr1_jd";
    public static final String RCV_ADDR2_WD = "rcv_addr2_wd";
    public static final String RCV_ADDR2_JD = "rcv_addr2_jd";
    public static final String RCV_ADDR3_WD = "rcv_addr3_wd";
    public static final String RCV_ADDR3_JD = "rcv_addr3_jd";
    public static final String RCV_ADDR4_WD = "rcv_addr4_wd";
    public static final String RCV_ADDR4_JD = "rcv_addr4_jd";
    public static final String RCV_ADDR5_WD = "rcv_addr5_wd";
    public static final String RCV_ADDR5_JD = "rcv_addr5_jd";
    
	/** 收货人userid **/
    public static final String SH_USER_ID = "sh_user_id";
    public static final String SH_USER_ID2 = "sh_user_id2";
    public static final String SH_USER_ID3 = "sh_user_id3";
    public static final String SH_USER_ID4 = "sh_user_id4";
    public static final String SH_USER_ID5 = "sh_user_id5";
    
	/** 产品名称 **/
    public static final String PRODUCT_NAME = "product_name";
	/** 产品货号 **/
    public static final String PRODUCT_SN 	= "product_sn";
	/** 厂家 **/
    public static final String PRODUCER 	= "producer";
	/** 车辆 **/
    public static final String CHE_LIANG	= "cheliang";
	/** 车队和物流公司 **/
    public static final String TRANSPORT 	= "transport";
	/** 仓库 **/
    public static final String CANG_KU 		= "cangku";
	/** 操作人 **/
    public static final String OPERATOR		= "operator";
	/** 是否为批量导入的订单 **/
    public static final String IS_BATCH		= "is_batch";
	/** 数据来源 **/
    public static final String DATA_SOURCE		= "data_source";
	/** 数据状态 **/
    public static final String DATA_STATUS		= "data_status";
	/** 运输类型 **/
	public static final String TRANSFER_TYPE = "transfer_type";

 }