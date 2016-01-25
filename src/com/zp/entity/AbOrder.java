package com.zp.entity;

import com.jfinal.plugin.activerecord.Model;

public class AbOrder extends Model<AbOrder>{

    private static final long serialVersionUID = 1L;
    public static final AbOrder dao = new AbOrder();

    public static String TABLE = "ab_order";

	/** 订单ID **/
    public static final String ID = "id";

	/** 订单编号 **/
    public static final String SN = "sn";

	/** 分类ID **/
    public static final String SUBJECT_ID = "subject_id";

	/** 分类名称 **/
    public static final String SUBJECT_NAME = "subject_name";
	/** 运输类型 **/
	public static final String ORDER_TYPE = "order_type";
	/** 类型[0-货物类、1-服务类] **/
    public static final String IS_TYPE = "is_type";

	/** 用途[0-犒劳自己、1-赠送他人] **/
    public static final String YT = "yt";

	/** 商家ID **/
    public static final String MID = "mid";

	/** 商家名称 **/
    public static final String MNAME = "mname";

	/** 商家地理经度 **/
    public static final String SJJD = "sjjd";

	/** 商家地理纬度 **/
    public static final String SJWD = "sjwd";

	/** 商品总价 **/
    public static final String SPZJ = "spzj";

	/** 订单总金额 **/
    public static final String DDZJE = "ddzje";

	/** 优惠金额 **/
    public static final String YHJE = "yhje";

	/** 送达时间 **/
    public static final String SDSJ = "sdsj";

	/** 赠送积分 **/
    public static final String ZSJF = "zsjf";

	/** 支付状态[0-未支付、1-已支付] **/
    public static final String ZFZT = "zfzt";

	/** 支付时间 **/
    public static final String ZFSJ = "zfsj";

	/**
	 * 订单状态[0-购物车、1-已提交、2-取货中、3-送货中、4-已送达、5-已签收、6-拒单、7-已退单、8-撤单]
	 * **/
    public static final String DDZT = "ddzt";

	/** 收货邮政编码 **/
    public static final String YZBM = "yzbm";

	/** 收货通讯地址 **/
    public static final String SHDZ = "shdz";

	/** 收货联系人 **/
    public static final String LXR = "lxr";

	/** 收货人联系电话 **/
    public static final String LXRDH = "lxrdh";

	/** 地址是否确认[0-未确认、1-已确认] **/
    public static final String DZSFQR = "dzsfqr";

	/** 地址确认时间 **/
    public static final String DZQRSJ = "dzqrsj";

	/** 地址确认IP **/
    public static final String DZQRIP = "dzqrip";

	/** 送货地址地图精度 **/
    public static final String SHDZJD = "shdzjd";

	/** 送货地址地图纬度 **/
    public static final String SHDZWD = "shdzwd";

	/** 是否打包[0-否、1-是] **/
    public static final String SFDB = "sfdb";

	/** 打包要求 **/
    public static final String DBYQ = "dbyq";

	/** 打包编号 **/
    public static final String DBBH = "dbbh";

	/** 打包时间 **/
    public static final String DBSJ = "dbsj";

	/** 打包是否审核[0-待审、1-已审核] **/
    public static final String SHZT = "shzt";

	/** 特殊要求 **/
    public static final String MEMO = "memo";

	/** 距离 **/
    public static final String JL = "jl";

	/** 跑腿费金额 **/
    public static final String PTF = "ptf";

	/** 是否免除腿费[0-否、1-是] **/
    public static final String MCPTF = "mcptf";

	/** 免费跑腿费说明 **/
    public static final String MCPTFSM = "mcptfsm";

	/** 下单时间 **/
    public static final String XDSJ = "xdsj";

	/** 下单人ID **/
    public static final String XDRID = "xdrid";

	/** 下单人名称 **/
    public static final String XDRMC = "xdrmc";

	/** 下单人电话 **/
    public static final String XDRDH = "xdrdh";

	/** 订单撤销时间 **/
    public static final String DDCXSJ = "ddcxsj";

	/** 订单撤销人 **/
    public static final String DDCXRID = "ddcxrid";

	/** 订单撤销人名称 **/
    public static final String DDCXRMC = "ddcxrmc";

	/** 接单人ID **/
    public static final String QDRID = "qdrid";
    
	/** 接单时间 **/
    public static final String QDSJ = "qdsj";

	/** 接单人名称 **/
    public static final String QDRNAME = "qdrname";

	/** 接单人地理经度 **/
    public static final String JDRJD = "jdrjd";

	/** 接单人地理纬度 **/
    public static final String JDRWD = "jdrwd";

	/** 收货时间 **/
    public static final String SHSJ = "shsj";

	/** 收货确认人ID **/
    public static final String SHQRRID = "shqrrid";

	/** 收货确认人名称 **/
    public static final String SHQRRMC = "shqrrmc";

	/** 认证码 **/
    public static final String RZM = "rzm";

	/** 是否结算[0-未结算、1-已结算] **/
    public static final String IS_ACCOUNT = "is_account";

	/** 佣金比例 **/
    public static final String PER = "per";

	/** 结算金额 **/
    public static final String ACCOUNTMONEY = "accountmoney";

	/** 结算时间 **/
    public static final String ACCOUNTDATE = "accountdate";

	/** 结算人ID **/
    public static final String ACCOUNTID = "accountid";

	/** 结算人名称 **/
    public static final String ACCOUNTNAME = "accountname";
    public static final String CITY_ID = "city_id";
    public static final String CITY_NAME = "city_name";

	/** 结算人备注 **/
    public static final String ACCOUNTREMARK = "accountremark";
    
	/** 是否是假定单，1-真 0-假 默认是1 **/
    public static final String ISTRUE = "istrue";
    
	/** 保证金 **/
    public static final String BZJ = "bzj";
    /**
	 * 下单方式或订单类型：同城订单1，熟车下单3，同城找车：4，物流公司下单：5
	 * **/
    public static final String WAY = "way";
    /**
	 * 意向价
	 */
    public static final String YXJG = "yxj";
 }