package com.zp.tools;

import java.util.HashMap;
import java.util.Map;

public class CommonStaticData {
	public static String CB_TYPE1 = "01";//用户申请
	public static String CB_TYPE2 = "02";//商户申请
	public static String CB_TYPE3 = "03";//商户申诉
	public static String CB_TYPE4 = "04";//取消退单
	public static String CB_TYPE5 = "05";//客服介入
	public static String CB_TYPE6 = "06";//退单成功
	public static String CB_TYPE7 = "07";//退单失败
	public static String CB_TYPE8 = "08";//业务员申请
	public static String CB_TYPE9 = "09";//客服申请
	
	public static String CB_STATUS1 = "1";//仲裁申请
	public static String CB_STATUS2 = "2";//仲裁中
	public static String CB_STATUS3 = "3";//仲裁完成
	
	//退单申请类型
	public static String CB_APPLYTYPE_MEMBER = "1";
	public static String CB_APPLYTYPE_MERCHANT = "2";
	public static String CB_APPLYTYPE_SERVICE = "3";
	
	public static String MSG_ONE = "商户逾期无操作（48小时），系统默认退单成功！";
	public static String MSG_TWO = "商户申诉后，用户48小时未操作，系统默认取消退单！";
	
	public static String SCORE_TYPE1 = "01";
	public static String SCORE_TYPE2 = "02";
	public static String SCORE_TYPE3 = "03";
	public static String SCORE_TYPE4 = "04";
	public static String SCORE_TYPE5 = "05";
	public static String SCORE_TYPE6 = "06";
	public static String SCORE_TYPE7 = "07";
	public static String SCORE_TYPE8 = "08";
	public static int REG_SCORE = 30;//注册积分30
	public static int LOGIN_SCORE = 2;//	每天登陆2积分
	public static int DOWNLOAD_SCORE = 50;//下载app50积分
	public static int APPRAISE_SCORE = 5;//购买商品15天内评价一次5积分
	public static int ADD_SCORE = 25;//推荐好友注册25积分
//	public static int SPEND_SCORE = 5;//消费多少金额送就送多少
	public static Map<String, String> map = new HashMap<String, String>();
	static{
		map.put(SCORE_TYPE1, "注册");
		map.put(SCORE_TYPE2, "每日登录");
		map.put(SCORE_TYPE3, "下载App");
		map.put(SCORE_TYPE4, "购买商品15天内评价");
		map.put(SCORE_TYPE5, "邀请好友注册");
		map.put(SCORE_TYPE6, "消费获得");
		map.put(SCORE_TYPE7, "积分兑换");
		map.put(SCORE_TYPE8, "退单扣除");
	}
	public static String getScoreTypeStr(String type){
		return map.get(type);
	}
	
	//会员卡状态
	public static String CARD_STATUS_VALID = "1";
	public static String CARD_STATUS_INVALID = "0";
	//会员卡激活状态 0 未激活 1激活
	public static String CARD_ACTIVE_VALID = "1";
	public static String CARD_ACTIVE_INVALID = "0";
	//会员卡购买状态 0 未购买 1已购买
	public static String CARD_PURCHASE_VALID = "1";
	public static String CARD_PURCHASE_INVALID = "0";
	//会员卡销售途径  1线上，2线下
	public static String CARD_SALETYPE_ONLINE = "1";
	public static String CARD_SALETYPE_OFFLINE = "2";
	//会员卡类型 1 送餐卡 2 消费卡
	public static String CARD_TYPE_SEND = "1";
	public static String CARD_TYPE_EXPENSE = "2";
	
	//会员卡消费记录状态 0 无效 1有效
	public static String CARD_EXPENSE_VALID = "1";
	public static String CARD_EXPENSE_INVALID = "0";
	
	//用户类型
	public static String USER_TYPE_MEMBER = "107";
	public static String USER_TYPE_MERCHANT = "105";
	public static String USER_TYPE_SERVICE = "106";
	
	//订单类型
	public static String ORDER_TYPE_TC = "TC";
	public static String ORDER_TYPE_KS = "KS";
	public static String ORDER_TYPE_DD = "DD";
	
}
