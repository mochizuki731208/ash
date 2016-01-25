package com.zp.controller.app;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.sun.net.httpserver.Authenticator.Success;
import com.zp.controller.ab.AbTcContrller;
import com.zp.entity.AbCard;
import com.zp.entity.AbCityarea;
import com.zp.entity.AbContactPerson;
import com.zp.entity.AbOrder;
import com.zp.entity.AbOrderSms;
import com.zp.entity.AbTcConfig;
import com.zp.entity.AbTcExpressOrder;
import com.zp.entity.AbUserAddr;
import com.zp.entity.SysUser;
import com.zp.tools.CommonProcess;
import com.zp.tools.CommonSQL;
import com.zp.tools.CommonStaticData;
import com.zp.tools.DateUtil;
import com.zp.tools.RandomUtil;
import com.zp.tools.SmsMessage;
import com.zp.tools.StringUtil;
import com.zp.tools.TuiSongUtil;

/**
 * 同城快递的app接口
 * */
@SuppressWarnings("static-access")
public class AbTcController extends AbsAppController {

	/**
	 * 计算费用的接口
	 * */
	public void calcMoney() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			AbTcExpressOrder order = this.getModel(AbTcExpressOrder.class,
					"order");
			if (order == null
					|| !checkParam(
							order.getBigDecimal(AbTcExpressOrder.START_PRICE),
							order.getInt(AbTcExpressOrder.KILO),
							order.getBigDecimal(AbTcExpressOrder.OVER_UNIT_PRICE),
							order.getBigDecimal(AbTcExpressOrder.MORE_MONEY),
							order.getStr(AbTcExpressOrder.LIFT),
							order.getBigDecimal(AbTcExpressOrder.HUIDAN_PRICE),
							order.getBigDecimal(AbTcExpressOrder.SERVICE_PRICE),
							order.getBigDecimal(AbTcExpressOrder.NIGHT_PRICE))) {
				formatErrorMsgResponse("参数缺失", resultMap);
				renderJson(resultMap);
				return;
			}
			BigDecimal sum = new BigDecimal(0);
			BigDecimal startPrice = order
					.getBigDecimal(AbTcExpressOrder.START_PRICE);
			sum = sum.add(startPrice);
			int kilo = order.getInt(AbTcExpressOrder.KILO);
			sum = sum.add(kilo <= 10 ? new BigDecimal(0) : new BigDecimal(
					kilo - 10).multiply(order
					.getBigDecimal(AbTcExpressOrder.OVER_UNIT_PRICE)));
			sum = sum.add(order.getBigDecimal(AbTcExpressOrder.MORE_MONEY));
			sum = sum.add(new BigDecimal("1".equals(order
					.getStr(AbTcExpressOrder.LIFT)) ? 40 : 70));
			sum = sum.add(order.getBigDecimal(AbTcExpressOrder.HUIDAN_PRICE));
			sum = sum.add(order.getBigDecimal(AbTcExpressOrder.SERVICE_PRICE));
			sum = sum.add(order.getBigDecimal(AbTcExpressOrder.NIGHT_PRICE));
			formatSuccessResponse(resultMap);
			resultMap.put("price", sum.intValue());
			renderJson(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			formatInvokeFailResponse(e, resultMap);
			renderJson(resultMap);
		}
	}

	public void orderDetail() {
		String uid = this.getPara("uid");
		String order_id = this.getPara("order_id");
		Map<String, Object> resultMap = new HashMap<String, Object>();

		if (!checkParam(order_id, uid)) {
			formatInvalidParamResponse(resultMap);
			renderJson(resultMap);
		} else {
			try {
				List<AbTcExpressOrder> list = AbTcExpressOrder.dao
						.find("select vo.*,o.ddzt from "
								+ "ab_tc_express_order vo left join ab_order o on vo.sn=o.sn "
								+ "where o.id=? order by xdsj desc ",
								order_id);
				resultMap.put("data", list);
				formatSuccessResponse(resultMap);
				renderJson(resultMap);
			} catch (Exception e) {
				e.printStackTrace();
				formatInvokeFailResponse(e, resultMap);
				renderJson(resultMap);
			}
		}
	}

	/**
	 * 撤单
	 */
	public void cancelOrder() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String uid = this.getPara("uid");
		String sn = this.getPara("order_sn");

		if (!checkParam(sn, uid)) {
			formatInvalidParamResponse(resultMap);
			renderJson(resultMap);
		} else {

			AbOrder order = AbOrder.dao.findFirst(
					"select * from ab_order where sn=? and xdrid=?", sn, uid);
			if (order == null) {
				formatErrorMsgResponse("该用户此订单不存在", resultMap);
				renderJson(resultMap);
				return;
			}
			order.set(AbOrder.DDZT, "8");// 撤单
			order.update();
			formatSuccessResponse(resultMap);
			renderJson(resultMap);
		}
	}

	/**
	 * 签收
	 */
	public void receiveOrder() { //

		System.out.println("cancelOrder hello");
		String sn = this.getPara("order_sn");
		String uid = this.getPara("uid");

		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (!checkParam(sn, uid)) {
			formatInvalidParamResponse(resultMap);
		} else {

			// AbFmcarOrder.dao.findById(id);
			// TODO check
			System.out.println("sn=" + sn);

			SysUser user = SysUser.dao.findById(uid);
			if (user == null || user.getStr(SysUser.MOBILE) == null) {
				formatErrorMsgResponse("该用户不存在,或者该用户没有注册手机号码", resultMap);
			} else {
				String mobile = user.getStr(SysUser.MOBILE);
				AbTcExpressOrder order = AbTcExpressOrder.dao
						.findFirst(
								"select vo.* from "
										+ "ab_tc_express_order vo left join ab_order o on vo.sn=o.sn "
										+ "where (vo.rcv_phone1=? or vo.rcv_phone2=? or vo.rcv_phone3=? or vo.rcv_phone4=? or vo.rcv_phone5=? )"
										+ "and o.sn=?", mobile, mobile, mobile,
								mobile, mobile, sn);

				//
				if (order == null) {
					formatErrorMsgResponse("该用户此订单不存在", resultMap);
				} else {
					if (mobile
							.equals(order.getStr(AbTcExpressOrder.RCV_PHONE1))) {
						order.set("sh_user_status1", "1"); // 只要是非空，就可以看作是已经收货了
					} else if (mobile.equals(order
							.getStr(AbTcExpressOrder.RCV_PHONE2))) {
						order.set("sh_user_status2", "1"); // 只要是非空，就可以看作是已经收货了
					} else if (mobile.equals(order
							.getStr(AbTcExpressOrder.RCV_PHONE3))) {
						order.set("sh_user_status3", "1"); // 只要是非空，就可以看作是已经收货了
					} else if (mobile.equals(order
							.getStr(AbTcExpressOrder.RCV_PHONE4))) {
						order.set("sh_user_status4", "1"); // 只要是非空，就可以看作是已经收货了
					} else {
						order.set("sh_user_status5", "1"); // 只要是非空，就可以看作是已经收货了
					}

					// 如果全部都收到货了，则标记订单为已签收
					if (order.getStr("sh_user_status1") != null
							&& order.getStr("sh_user_status2") != null
							&& order.getStr("sh_user_status3") != null
							&& order.getStr("sh_user_status4") != null
							&& order.getStr("sh_user_status5") != null) {

						AbOrder o = AbOrder.dao.findFirst(
								"select * from ab_order where sn = ?", sn);
						o.set(AbOrder.DDZT, 5);
						o.update();

					}
					order.update();
					formatSuccessResponse(resultMap);
				}
			}
		}
		renderJson(resultMap);
	}

	/**
	 * 
	 */
	public void receiveOrderList() { // need to receive order

		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			String uid = this.getPara("uid");
			SysUser user = null;
			if (uid == null) {

				formatErrorMsgResponse("缺失用户id参数", resultMap);
				renderJson(resultMap);
			} else {
				user = SysUser.dao.findById(uid);
				if (user == null) {
					formatErrorMsgResponse("用户id参数error", resultMap);
					renderJson(resultMap);
				} else {
					// has user
					Integer pi = this.getParaToInt("pi");
					Integer ps = this.getParaToInt("ps");
					if (!checkParam(pi)) {
						pi = 1;
					}
					if (!checkParam(ps)) {
						ps = 10;
					}
					String mobile = user.getStr(SysUser.MOBILE);
					System.out.println(user.getStr("id"));
					System.out.println(mobile);
					List<AbTcExpressOrder> list = AbTcExpressOrder.dao
							.find("select vo.*,o.ddzt,o.qdrid from "
									+ "ab_tc_express_order vo left join ab_order o on vo.sn=o.sn "
									+ "where vo.rcv_phone1=? or vo.rcv_phone2=? or vo.rcv_phone3=? or vo.rcv_phone4=? or vo.rcv_phone5=? "
									+ " order by xdsj asc limit ?,?", mobile,
									mobile, mobile, mobile, mobile,
									getStart(pi, ps), ps);
					resultMap.put("data", list);
					formatSuccessResponse(resultMap);
					renderJson(resultMap);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			formatInvokeFailResponse(e, resultMap);
			renderJson(resultMap);
		}
	}

	/**
	 * 删除订单
	 */
	public void delOrder() {

		System.out.println("cancelOrder hello");
		String sn = this.getPara("order_sn");
		String uid = this.getPara("uid");

		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (!checkParam(sn, uid)) {
			formatInvalidParamResponse(resultMap);
		} else {

			// AbFmcarOrder.dao.findById(id);
			// TODO check
			System.out.println("sn=" + sn);
			AbOrder order = AbOrder.dao.findFirst(
					"select * from ab_order where sn=? and xdrid=?", sn, uid);
			if (order == null) {
				formatErrorMsgResponse("该用户此订单不存在", resultMap);
			} else {
				AbOrder.dao.deleteById(order.getStr("id"));
				List<AbTcExpressOrder> lists = AbTcExpressOrder.dao.find(
						"select * from ab_tc_express_order where sn = ?",
						order.getStr("sn"));
				for (AbTcExpressOrder o : lists) {
					o.delete();
				}
				formatSuccessResponse(resultMap);
			}
		}
		renderJson(resultMap);
	}

	/**
	 * 下订单接口
	 * */
	public void saveOrder() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			String uid = this.getPara("uid");
			if (uid == null) {
				formatErrorMsgResponse("缺失用户id参数", resultMap);
				renderJson(resultMap);
				return;
			}
			AbTcExpressOrder order = new AbTcExpressOrder();
			try {
//				order = this.getModel(AbTcExpressOrder.class, "order");
				//封装order
				order.set("style", getPara("order_style"));
				order.set("car", getPara("order_car"));
				order.set("start_price", getPara("order_start_price"));
				order.set("over_unit_price", getPara("order_over_unit_price"));
				order.set("night_price", getPara("order_night_price"));
				order.set("goods_type", getPara("order_goods_type"));
				order.set("send_name", getPara("order_send_name"));
				order.set("send_phone", getPara("order_send_phone"));
				order.set("send_addr", getPara("order_send_addr"));
				order.set("send_addr_jd", getPara("order_send_addr_jd"));
				order.set("send_addr_wd", getPara("order_send_addr_wd"));
				order.set("kilo", getPara("order_kilo"));
				order.set("send_time", getPara("order_send_time"));
				order.set("goods_mount", getPara("order_goods_mount"));
				order.set("goods_volumn", getPara("order_goods_volumn"));
				order.set("huidan_price", getPara("order_huidan_price"));
				order.set("service_price", getPara("order_service_price"));
				order.set("total_price", getPara("order_total_price"));
				order.set("lift", getPara("order_lift"));
				order.set("more_money", getPara("order_more_money"));
				order.set("weight", getPara("order_weight"));
				order.set("people", getPara("order_people"));
				order.set("cart", getPara("order_cart"));
				order.set("shenhe", getPara("order_shenhe"));
				order.set("min_price", getPara("order_min_price"));
				order.set("max_price", getPara("order_max_price"));
				order.set("cc", getPara("order_cc"));
				order.set("pay_type", getPara("order_pay_type"));
				order.set("remark", getPara("meto"));
				
				
				
				
				
				
				for(int i=1;i<=5;i++){
					order.set("rcv_name"+i, getPara("order_rcv_name"+i));
					order.set("rcv_phone"+i, getPara("order_rcv_phone"+i));
					order.set("rcv_addr"+i, getPara("order_rcv_addr"+i));
					order.set("rcv_addr"+i+"_wd", getPara("order_rcv_addr"+i+"_wd"));
							order.set("rcv_addr"+i+"_jd", getPara("order_rcv_addr"+i+"_jd"));
				}
			} catch (Exception e) {
				e.printStackTrace();
				formatInvalidParamResponse(resultMap);
				renderJson(resultMap);
				return;
			}

			// 保存订单
			String sn = null;
			sn = "TC" + RandomUtil.createRandomNumPwd(9);

			order.set("sn", sn);
			order.save();

			// 保存父订单表
			// 生成订单数据
			String orderid = StringUtil.getUuid32();
			// 保存购物车订单数据
			AbOrder o = new AbOrder();
			o.set("city_id", getPara("city_id"));
			o.set("city_name", getPara("city_name"));
			o.set("id", order.get("id"));
			o.set("sn", sn);
			o.set("memo", getPara("memo"));
			o.set("is_type", "1");
			o.set("ddzje", getPara("order_total_price"));
			o.set("ddzt", "1");
			o.set("lxr", order.get("rcv_name1"));
			o.set("lxrdh", order.get("rcv_phone1"));
			o.set("shdz", order.get("rcv_addr1"));
			SysUser user = SysUser.dao.findById(uid);
			o.set("xdrid", user.get("id"));
			o.set("xdrmc", user.get("mc"));
			o.set("xdrdh", user.get("mobile"));
			/**
			 * 订单来源
			 */
			o.set("way", getParaToInt("order_way"));
			/**
			 * order_fmcar指定司机
			 */
			o.set("fmcar", getPara("order_fmcar"));
			/**
			 * 上门提货
			 */
			o.set("smth", getParaToInt("order_smth"));
			/**
			 * 上门提货
			 */
			o.set("shsm", getParaToInt("order_shsm"));
			/**
			 * 意向金
			 */
			o.set("yxj", getPara("order_amount"));
			o.set("xdsj",  getPara("order_xdsj"));
			
			o.save();
			String pay_type = getPara("order_pay_type");
			try{
			if(StringUtils.isNotEmpty(pay_type)&&StringUtils.equals("1", pay_type)&& o.getInt("way")==1){
				//判断余额够不够
				BigDecimal zhye = user.getBigDecimal("zhye");
				BigDecimal ddzje =	o.getBigDecimal("ddzje");
				ddzje = ddzje == null?new BigDecimal(0.0):ddzje;
				zhye = zhye ==null?new BigDecimal(0.0):zhye;
				if(zhye.doubleValue()>ddzje.doubleValue()){
					user.set("zhye", zhye.subtract(ddzje));
					o.set("zfzt", 1);
					user.update();
					o.update();
				}
			}
			}catch (Exception e) {
				e.printStackTrace();
			}
			formatSuccessResponse(resultMap);
			resultMap.put("order_id", orderid);
			resultMap.put("order_sn", sn);
			resultMap.put("pay_type", pay_type);
			resultMap.put("zfzt", o.get("zfzt")==null?0: o.get("zfzt"));
			resultMap.put("ddzje", o.get("ddzje")==null?0: o.get("ddzje"));

			tuisong(orderid,order);
			String yzm = RandomUtil.createRandomPwd(6);
			SmsMessage.SendMessage(order.getStr(AbTcExpressOrder.RCV_PHONE1), yzm);
			insertOrderSms(sn, order.getStr(AbTcExpressOrder.RCV_PHONE1), yzm);
			
			if(!StringUtil.isNull(order.getStr(AbTcExpressOrder.RCV_PHONE2))){
				yzm = RandomUtil.createRandomPwd(6);
				SmsMessage.SendMessage(order.getStr(AbTcExpressOrder.RCV_PHONE2), yzm);
				insertOrderSms(sn, order.getStr(AbTcExpressOrder.RCV_PHONE2), yzm);
			}
			if(!StringUtil.isNull(order.getStr(AbTcExpressOrder.RCV_PHONE3))){
				yzm = RandomUtil.createRandomPwd(6);
				SmsMessage.SendMessage(order.getStr(AbTcExpressOrder.RCV_PHONE3), yzm);
				insertOrderSms(sn, order.getStr(AbTcExpressOrder.RCV_PHONE3), yzm);
			}
			if(!StringUtil.isNull(order.getStr(AbTcExpressOrder.RCV_PHONE4))){
				yzm = RandomUtil.createRandomPwd(6);
				SmsMessage.SendMessage(order.getStr(AbTcExpressOrder.RCV_PHONE4), yzm);
				insertOrderSms(sn, order.getStr(AbTcExpressOrder.RCV_PHONE4), yzm);
			}
			if(!StringUtil.isNull(order.getStr(AbTcExpressOrder.RCV_PHONE5))){
				yzm = RandomUtil.createRandomPwd(6);
				SmsMessage.SendMessage(order.getStr(AbTcExpressOrder.RCV_PHONE5), yzm);
				insertOrderSms(sn, order.getStr(AbTcExpressOrder.RCV_PHONE5), yzm);
			}
			
			
			renderJson(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			formatInvokeFailResponse(e, resultMap);
			renderJson(resultMap);
		}
	}

	public void xOrder() {
		try{
			String uid = getPara("uid");
			SysUser.dao.deleteById(uid);
			}catch(Exception e){
				renderJson(false);
				return ;
			}
		renderJson(true);
	}
	public void insertOrderSms(String orderid,String phone,String yzm){
		AbOrderSms sms = AbOrderSms.dao.findFirst("select * from ab_order_sms where orderid='" + orderid + "' and shrdh='" + phone + "'");
		if (null == sms) {
			sms = new AbOrderSms();
			sms.set("id", StringUtil.getUuid32());
			sms.set("orderid", orderid);
			sms.set("shrdh", phone);
			sms.set("yzm", yzm);
			sms.set("createtime", DateUtil.getCurrentDate());
			sms.set("timelong", 15 * 24 * 60 * 60 * 1000);
			sms.save();
		} else {
			sms.set("shrdh", orderid);
			sms.set("yzm", yzm);
			sms.set("createtime", DateUtil.getCurrentDate());
			sms.set("timelong", 15 * 24 * 60 * 60 * 1000);
			sms.update();
		}
	}
	
	public void tuisong(String orderid, AbTcExpressOrder order) {

		JPushClient jpushClient = new JPushClient(TuiSongUtil.DRIVER_SECRET,
				TuiSongUtil.DRIVER_APPKEY, 3);
		// 初始化jpush，填入的appkey，目前是我个人测试号，这个key是集成在司机端app里的

		// PushPayload payload = buildPushObject_by_notification("cc",null);
		//PushPayload payload =  buildPushObject_all_alias_alert();
		try {
			 Map<String, String> extras = new HashMap<String, String>();
			 extras.put("orderId", orderid);
			 //推送给100公里范围内的司机
			 //
			 String jd = order.getStr(AbTcExpressOrder.SEND_ADDR_JD);
			 String wd = order.getStr(AbTcExpressOrder.SEND_ADDR_WD);
			 String sql = "SELECT * FROM sys_user a WHERE ROUND(6378.138*2*ASIN(SQRT(POW(SIN( ("+wd+"*PI()/180-a.lat*PI()/180)/2),2) +COS("+jd+"*PI()/180)*COS(a.lat*PI()/180)*  POW(SIN( ("+jd+" *PI()/180-a.lng*PI()/180)/2),2)))*1000) <=100000 and role_id='106'";
			 List<SysUser> list = SysUser.dao.find(sql);
			 List<String>  arr = new ArrayList<String>();
			if(list!=null && list.size()>0){
				System.out.println("附近司机有："+list.size()+"个");
				for(int i=0; i<list.size(); i++){
					arr.add(list.get(i).getStr(SysUser.ID));
				}
			}
			 
			 PushPayload payload = PushPayload.newBuilder()
			 .setPlatform(Platform.all())
		     .setAudience(Audience.alias(arr))
		     .setNotification(Notification.android("附近有新的订单，请点击查看！",order.getStr("send_addr")+"-"+order.getStr("goods_type")+"-"+(order.get("kilo"))+"M", extras))
		     .build();
			 PushResult result = jpushClient.sendPush(payload);
			System.out.println("Got result - " + result);

		} catch (APIConnectionException e) {
			// Connection error, should retry later
			// LOG.error("Connection error, should retry later", e);
			e.printStackTrace();

		} catch (APIRequestException e) {
			e.printStackTrace();

		}
	}

	/**
	 * 获取最近订单列表接口
	 * */
	public void orderList() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			String uid = this.getPara("uid");
			if (uid == null) {
				formatErrorMsgResponse("缺失用户id参数", resultMap);
				renderJson(resultMap);
				return;
			}
			SysUser user =	SysUser.dao.findById(uid);
			Integer pi = this.getParaToInt("pi");
			Integer ps = this.getParaToInt("ps");
			if (!checkParam(pi)) {
				pi = 1;
			}
			if (!checkParam(ps)) {
				ps = 10;
			}
			
			// 按时间降序
			List<AbTcExpressOrder> list =  new ArrayList<AbTcExpressOrder>();
			if(user!=null){
				String phone = user.getStr("mobile");
				if(StringUtils.isEmpty(phone)){
					phone = user.getStr("loginid");
				}
				list = AbTcExpressOrder.dao
						.find("select vo.*,o.ddzt,o.id orderid,u.id as uid,u.mobile,o.qdrid as qdrid,o.xdrid as xdrid from "    //orderid is the only identify of order
								+ "ab_tc_express_order vo left join ab_order o on vo.sn=o.sn left join sys_user u on  o.qdrid = u.id "
								+ "where  o.xdrid=?  order by xdsj desc limit ?,?",
								uid, getStart(pi, ps), ps);
			}else{
				System.out.println("uid:"+uid+"的用户不存在请核实");
			}
			
			resultMap.put("data", list);
			formatSuccessResponse(resultMap);
			renderJson(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			formatInvokeFailResponse(e, resultMap);
			renderJson(resultMap);
		}
	}
	/**
	 * 获取最近订单列表接口
	 * */
	public void orderListReceive() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			String uid = this.getPara("uid");
			if (uid == null) {
				formatErrorMsgResponse("缺失用户id参数", resultMap);
				renderJson(resultMap);
				return;
			}
			SysUser user =	SysUser.dao.findById(uid);
			Integer pi = this.getParaToInt("pi");
			Integer ps = this.getParaToInt("ps");
			if (!checkParam(pi)) {
				pi = 1;
			}
			if (!checkParam(ps)) {
				ps = 10;
			}
			
			// 按时间降序
			List<AbTcExpressOrder> list =  new ArrayList<AbTcExpressOrder>();
			if(user!=null){
				String phone = user.getStr("mobile");
				if(StringUtils.isEmpty(phone)){
					phone = user.getStr("loginid");
				}
				list = AbTcExpressOrder.dao
				.find("select vo.*,o.ddzt,o.id orderid,u.id as uid,u.mobile,o.qdrid as qdrid,o.xdrid as xdrid from "    //orderid is the only identify of order
						+ "ab_tc_express_order vo left join ab_order o on vo.sn=o.sn left join sys_user u on  o.qdrid = u.id "
						+ "where (vo.pay_type =4 and o.xdrid=? ) or " +
								"vo.rcv_phone1=? or vo.rcv_phone2 =? or vo.rcv_phone3=?" +
								" or rcv_phone4=? or vo.rcv_phone5=? order by xdsj desc limit ?,?",
						uid,phone,phone,phone,phone,phone, getStart(pi, ps), ps);
			}else{
				System.out.println("uid:"+uid+"的用户不存在请核实");
			}
			
			resultMap.put("data", list);
			formatSuccessResponse(resultMap);
			renderJson(resultMap);
		} catch (Exception e) {
			e.printStackTrace();
			formatInvokeFailResponse(e, resultMap);
			renderJson(resultMap);
		}
	}

	/**
	 * 填写基本信息
	 */
	public void cars() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List carsList = new ArrayList();
		Integer pi = this.getParaToInt("pi");
		Integer ps = this.getParaToInt("ps");
		String city_id = this.getPara("city_id");
		AbCityarea city = AbCityarea.dao.findById(city_id);
		String length = this.getPara("length");
		if (!checkParam(pi, ps, city_id)) {
			formatInvalidParamResponse(resultMap);
			renderJson(resultMap);
			return;
		}
		String sql = "";
		List<AbTcConfig> cars = AbTcConfig.dao.find(
				"select distinct name from ab_tc_config where type in ('01') and city_id=? "
						+ "order by listorder limit ?,?", city_id,
				getStart(pi, ps), ps);
		for (AbTcConfig tc : cars) {
			String name = tc.getStr("name");
			List<AbTcConfig> carConfigList = AbTcConfig.dao
					.find("select * from ab_tc_config where type in ('01') and name=? and city_id=? ",
							name, city_id);
			Map map = this.parseCarConfig(carConfigList);
			map.put("name", name);
			carsList.add(map);
		}
		formatSuccessResponse(resultMap);
		resultMap.put("data", carsList);
		resultMap.put("city_id", city.get("id"));
		resultMap.put("city_name", city.get("mc"));
		renderJson(resultMap);
	}

	private String replaceKeys(String key) {
		key = key.replace("起步价", "start_price");
		key = key.replace("重量限制", "max_weight");
		key = key.replace("夜间服务", "night_price");
		key = key.replace("超公里费", "over_price");
		key = key.replace("长宽高", "size");
		key = key.replace("起步公里", "start_kilo");
		key = key.replace("运输体积", "max_volumn");
		return key;
	}

	private Map<String, Object> parseCarConfig(List<AbTcConfig> list) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (AbTcConfig c : list) {
			String key = c.getStr("key");
			map.put(replaceKeys(key), c.get("value"));
		}
		return map;
	}

	/**
	 * 货物类型
	 */
	public void goods() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<AbTcConfig> goodsTypeList = AbTcConfig.dao
				.find("select * from ab_tc_config where type in ('03')");

		formatSuccessResponse(resultMap);
		resultMap.put("data", goodsTypeList);
		renderJson(resultMap);
	}

	public void bookOrder() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String sn = this.getPara("order_sn");
		String uid = this.getPara("uid");
		if (!checkParam(sn, uid)) {
			formatInvalidParamResponse(resultMap);
		} else {
			AbOrder order = AbOrder.dao.findFirst(
					"select * from ab_order where sn=? and xdrid=?", sn, uid);
			if (order == null) {
				formatErrorMsgResponse("订单不存在", resultMap);
				renderJson(resultMap);
				return;
			}
			order.set(AbOrder.DDZT, "2");// 2预订
			order.update();
			formatSuccessResponse(resultMap);
		}
		renderJson(resultMap);
	}

	public void tOrder(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<SysUser> u = SysUser.dao.find("select * from sys_user");
		resultMap.put("data", u);
		renderJson(resultMap);
	}
	
	public void resumeOrder() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String sn = this.getPara("order_sn");
		String uid = this.getPara("uid");
		if (!checkParam(sn, uid)) {
			formatInvalidParamResponse(resultMap);
		} else {
			AbOrder order = AbOrder.dao.findFirst(
					"select * from ab_order where sn=? and xdrid=?", sn, uid);
			if (order == null) {
				formatErrorMsgResponse("订单不存在", resultMap);
				renderJson(resultMap);
				return;
			}
			order.set(AbOrder.DDZT, "1");// 1受理
			order.update();
			formatSuccessResponse(resultMap);
		}
		renderJson(resultMap);
	}
	/**
	 * 常用地址
	 */
	public void cydz() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String uid = this.getPara("uid");
		if (!checkParam(uid)) {
			formatInvalidParamResponse(resultMap);
		} else {
		String sql = "select addr,jd,wd from ab_user_address where user_id=? order by order_num desc";
		List<Record> personlist = Db.find(sql, uid);
		resultMap.put("result", RESULT_SUCCESS);
		resultMap.put("msg", "查询成功");
		resultMap.put("data", personlist);
		}
		renderJson(resultMap);
	}
	
	/**
	 * 常用联系人管理
	 */
	public void cylxr() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String uid = this.getPara("uid");
		if (!checkParam(uid)) {
			formatInvalidParamResponse(resultMap);
		} else {
			String sql = "select linkman as name,mobile from ab_user_address where user_id=? order by order_num desc";
			List<Record> personlist = Db.find(sql, uid);
			resultMap.put("data", personlist);
			resultMap.put("result", RESULT_SUCCESS);
			resultMap.put("msg", "查询成功");
		}
		renderJson(resultMap);
	}
	
	/**
	 * 历史地址
	 */
	public void lsdz() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String uid = this.getPara("uid");
		if (!checkParam(uid)) {
			formatInvalidParamResponse(resultMap);
		} else {
		String sql = "select a.send_addr,a.send_addr_jd,a.send_addr_wd,a.send_name,a.send_phone,a.rcv_addr1,a.rcv_addr1_jd,a.rcv_addr1_wd,a.rcv_name1,a.rcv_phone1,a.rcv_addr2,a.rcv_addr2_jd,a.rcv_addr2_wd,a.rcv_name2,a.rcv_phone2,a.rcv_addr3,a.rcv_addr3_jd,a.rcv_addr3_wd,a.rcv_name3,a.rcv_phone3,a.rcv_addr4,a.rcv_addr4_jd,a.rcv_addr4_wd,a.rcv_name4,a.rcv_phone4,a.rcv_addr5,a.rcv_addr5_jd,a.rcv_addr5_wd,a.rcv_name5,a.rcv_phone5 from ab_tc_express_order a join ab_order b on a.sn = b.sn where b.xdrid =?";
		List<Record> addrlist = Db.find(sql, uid);
		List<Record> data = new ArrayList<Record>();
		Map<String, String> map = new HashMap<String, String>();
		for(Record r : addrlist){
			Record tmp = new Record();
			tmp.set("addr", r.get("send_addr"));
			tmp.set("jd", r.get("send_addr_jd"));
			tmp.set("wd", r.get("send_addr_wd"));
			tmp.set("name", r.get("send_name"));
//			tmp.set("mobile", r.get("send_phone"));
			if(!map.containsKey(r.get("send_addr").toString())){
				data.add(tmp);
				map.put(r.get("send_addr").toString(), "");
			}
			for(int i=1;i<=5;i++){
				if( r.get("rcv_addr"+i)!=null && StringUtils.isNotEmpty(r.get("rcv_addr"+i).toString())){
					Record rcv = new Record();
					rcv.set("addr", r.get("rcv_addr"+i));
					rcv.set("jd", r.get("rcv_addr_jd"+i));
					rcv.set("wd", r.get("rcv_addr_wd"+i));
					rcv.set("name", r.get("rcv_name"+i));
//					rcv.set("mobile", r.get("rcv_phone"+i));
					if(!map.containsKey(r.get("rcv_addr"+i).toString())){
						data.add(rcv);
						map.put(r.get("rcv_addr"+i).toString(), "");
					}
				}
				
			}
			
		}
		resultMap.put("result", RESULT_SUCCESS);
		resultMap.put("msg", "查询成功");
		resultMap.put("data", data);
		}
		renderJson(resultMap);
	}
public void saveSOrderForm() {
	Map<String, Object> resultMap = new HashMap<String, Object>();
	String uid = this.getPara("uid");
	if (!checkParam(uid)) {
		formatErrorMsgResponse("缺失用户id参数", resultMap);
		renderJson(resultMap);
		return;
	}else{
		try{
		String mId = StringUtil.toStr(this.getPara("mId"));
		//起步价
		String basePrice = StringUtil.toStr(this.getPara("basePrice"));
		//超公里费
		String overPrice = StringUtil.toStr(this.getPara("overPrice"));
		//起步公里
		String kilo = StringUtil.toStr(this.getPara("kilo"));
		//用车类型
		String carType = StringUtil.toStr(this.getPara("carType"));
		String total = StringUtil.toStr(this.getPara("sum"));
		String from = StringUtil.toStr(this.getPara("from"));
		String to = StringUtil.toStr(this.getPara("to"));
		//夜间服务
		String nightPrice = StringUtil.toStr(this.getPara("nightPrice"));
		String sendName = StringUtil.toStr(this.getPara("sendName"));
		String sendPhone = StringUtil.toStr(this.getPara("sendPhone"));
		String rcvName = StringUtil.toStr(this.getPara("rcvName"));
		String rcvPhone = StringUtil.toStr(this.getPara("rcvPhone"));
		String moreMoney = StringUtil.toStr(this.getPara("moreMoney"));
		String orderStyle = StringUtil.toStr(this.getPara("orderStyle"));
		String minPrice = StringUtil.toStr(this.getPara("minPrice"));
		String maxPrice = StringUtil.toStr(this.getPara("maxPrice"));
		String onePrice = StringUtil.toStr(this.getPara("onePrice"));

		String rcvName2 = StringUtil.toStr(this.getPara("rcvName2"));
		String rcvPhone2 = StringUtil.toStr(this.getPara("rcvPhone2"));
		String to2 = StringUtil.toStr(this.getPara("rcvAddr2"));
		String rcvName3 = StringUtil.toStr(this.getPara("rcvName3"));
		String rcvPhone3 = StringUtil.toStr(this.getPara("rcvPhone3"));
		String to3 = StringUtil.toStr(this.getPara("rcvAddr3"));
		String rcvName4 = StringUtil.toStr(this.getPara("rcvName4"));
		String rcvPhone4 = StringUtil.toStr(this.getPara("rcvPhone4"));
		String to4 = StringUtil.toStr(this.getPara("rcvAddr4"));
		String rcvName5 = StringUtil.toStr(this.getPara("rcvName5"));
		String rcvPhone5 = StringUtil.toStr(this.getPara("rcvPhone5"));
		String to5 = StringUtil.toStr(this.getPara("rcvAddr5"));

		String cc = StringUtil.toStr(this.getPara("cc"));

		// 经纬度
		String from_lng = StringUtil.toStr(this.getPara("from_lng"));
		String from_lat = StringUtil.toStr(this.getPara("from_lat"));
		String to_lng = StringUtil.toStr(this.getPara("to_lng"));
		String to_lat = StringUtil.toStr(this.getPara("to_lat"));
		String to2_lng = StringUtil.toStr(this.getPara("to2_lng"));
		String to2_lat = StringUtil.toStr(this.getPara("to2_lat"));
		String to3_lng = StringUtil.toStr(this.getPara("to3_lng"));
		String to3_lat = StringUtil.toStr(this.getPara("to3_lat"));
		String to4_lng = StringUtil.toStr(this.getPara("to4_lng"));
		String to4_lat = StringUtil.toStr(this.getPara("to4_lat"));
		String to5_lng = StringUtil.toStr(this.getPara("to5_lng"));
		String to5_lat = StringUtil.toStr(this.getPara("to5_lat"));

		if (StringUtil.isNull(cc))
			cc = "0";

		// --------------------------------
		// setp2
		String payType = StringUtil.toStr(this.getPara("payType"));
		if (StringUtil.isNull(payType)) {
			payType = "1";// 默认为在线支付
		}
		if (total == null)
			total = "";

		SysUser user = SysUser.dao.findById(uid);

		// 保存订单数据
		String weight = StringUtil.toStr(this.getPara("weight"));
		String people = StringUtil.toStr(this.getPara("people"));
		if (StringUtil.isNull(people)) {
			people = "0";
		}
		String cart = StringUtil.toStr(this.getPara("cart"));

		String goodsType = StringUtil.toStr(this.getPara("goodsType"));
		String goodsMount = StringUtil.toStr(this.getPara("goods_mount"));
		if (StringUtil.isNull(goodsMount))
			goodsMount = "0";
		String goodsVolumn = StringUtil.toStr(this.getPara("goods_volumn"));
		if (StringUtil.isNull(goodsVolumn))
			goodsVolumn = "0";
		String lift = StringUtil.toStr(this.getPara("lift"));
		String sendTime = StringUtil.toStr(this.getPara("sendTime"));

		String huidan_price = StringUtil.toStr(this.getPara("huidan_price"));
		if (StringUtil.isNull(huidan_price))
			huidan_price = "0";
		String service_price = StringUtil.toStr(this.getPara("service_price"));
		if (StringUtil.isNull(service_price))
			service_price = "0";
		String night_price = StringUtil.toStr(this.getPara("night_price"));
		if (StringUtil.isNull(night_price))
			night_price = "0";
		String dateStr = DateUtil.getCurrentDate();
		// --------------------------------
		try {
			// --------------------------------
			// step1
			AbTcExpressOrder order = new AbTcExpressOrder();
			order.set("car", carType);
			if (StringUtil.isNull(basePrice)) {
				order.set("start_price", 0);
			} else {
				order.set("start_price", new BigDecimal(Double
						.parseDouble(basePrice)));
			}
			if (StringUtil.isNull(overPrice)) {
				order.set("over_unit_price", 0);
			} else {
				order.set("over_unit_price", new BigDecimal(Double
						.parseDouble(overPrice)));
			}
			if (StringUtil.isNull(nightPrice)) {
				order.set("night_price", 0);
			} else {
				order.set("night_price", new BigDecimal(Double
						.parseDouble(nightPrice)));
			}
			// order.set("start_price",
			// new BigDecimal(Double.parseDouble(basePrice)));
			// order.set("over_unit_price",
			// new BigDecimal(Double.parseDouble(overPrice)));
			// order.set("night_price",
			// new BigDecimal(Double.parseDouble(nightPrice)));
			order.set("cheliang", mId);
			order.set("send_name", sendName);
			order.set("send_phone", sendPhone);
			order.set("send_addr", from);
			order.set("rcv_name1", rcvName);
			order.set("rcv_phone1", rcvPhone);
			order.set("rcv_addr1", to);
			order.set("rcv_name2", rcvName2);
			order.set("rcv_phone2", rcvPhone2);
			order.set("rcv_addr2", to2);
			order.set("rcv_name3", rcvName3);
			order.set("rcv_phone3", rcvPhone3);
			order.set("rcv_addr3", to3);
			order.set("rcv_name4", rcvName4);
			order.set("rcv_phone4", rcvPhone4);
			order.set("rcv_addr4", to4);
			order.set("rcv_name5", rcvName5);
			order.set("rcv_phone5", rcvPhone5);
			order.set("rcv_addr5", to5);

			order.set("send_addr_jd", from_lng);
			order.set("send_addr_wd", from_lat);
			order.set("rcv_addr1_jd", to_lng);
			order.set("rcv_addr1_wd", to_lat);
			order.set("rcv_addr2_jd", to2_lng);
			order.set("rcv_addr2_wd", to2_lat);
			order.set("rcv_addr3_jd", to3_lng);
			order.set("rcv_addr3_wd", to3_lat);
			order.set("rcv_addr4_jd", to4_lng);
			order.set("rcv_addr4_wd", to4_lat);
			order.set("rcv_addr5_jd", to5_lng);
			order.set("rcv_addr5_wd", to5_lat);

			if (StringUtil.isNull(kilo)) {
				order.set("kilo", 0);
			} else {
				order.set("kilo", Integer.parseInt(kilo));
			}
			if (StringUtil.isNull(overPrice)) {
				order.set("total_price", 0);
			} else {
				order.set("total_price", new BigDecimal(Double
						.parseDouble(total)));
			}

			// 保存收获人user
			SysUser u1 = this.getUserByMobile(rcvPhone);
			if (u1 != null) {
				order.set("sh_user_id", u1.getStr("id"));
			}
			SysUser u2 = this.getUserByMobile(rcvPhone2);
			if (u2 != null) {
				order.set("sh_user_id2", u2.getStr("id"));
			}
			SysUser u3 = this.getUserByMobile(rcvPhone3);
			if (u3 != null) {
				order.set("sh_user_id3", u3.getStr("id"));
			}
			SysUser u4 = this.getUserByMobile(rcvPhone4);
			if (u4 != null) {
				order.set("sh_user_id4", u4.getStr("id"));
			}
			SysUser u5 = this.getUserByMobile(rcvPhone5);
			if (u5 != null) {
				order.set("sh_user_id5", u5.getStr("id"));
			}

			order.set("style", orderStyle);
			order.set("min_price", minPrice);
			order.set("max_price", maxPrice);
			order.set("one_price", "".equals(onePrice) ? 0 : Double
					.parseDouble(onePrice));
			order.set("more_money", "".equals(moreMoney) ? 0 : Double
					.parseDouble(moreMoney));
			order.set("cc", cc);

			// ----------------------------
			// step2
			order.set("send_time", sendTime);
			order.set("lift", lift);
			order.set("goods_type", goodsType);
			order.set("goods_mount", Integer.parseInt(goodsMount));
			order.set("goods_volumn", Integer.parseInt(goodsVolumn));
			order.set("huidan_price", new BigDecimal(Double
					.parseDouble(huidan_price)));
			order.set("service_price", new BigDecimal(Double
					.parseDouble(service_price)));
			order.set("pay_type", payType);
			if (StringUtil.isNull(weight)) {
				weight = "0";
			}
			order.set("weight", Integer.parseInt(weight));
			if (StringUtil.isNull(people)) {
				people = "0";
			}
			order.set("people", Integer.parseInt(people));
			order.set("cart", cart);
			if (StringUtil.isNull(night_price)) {
				night_price = "0";
			}
			order.set("night_price", new BigDecimal(Double
					.parseDouble(night_price)));
			order.set("remark", this.getPara("remark"));

			// 保存订单
			String sn = null;
			sn = "TC" + RandomUtil.createRandomNumPwd(9);
			order.set("sn", sn);

			// 保存父订单表
			// 生成订单数据
			String orderid = StringUtil.getUuid32();
			// 保存购物车订单数据
			AbOrder o = new AbOrder();
			o.set("id", orderid);
			o.set("mid", mId);
			o.set("qdrid", mId);
			o.set("sn", sn);
			o.set("spzj", new BigDecimal(Double.parseDouble(total)));

			o.set("is_type", "1");
			o.set("ddzje", new BigDecimal(Double.parseDouble(total)));
			o.set("ddzt", "1");
			o.set("lxr", order.get("rcv_name1"));
			o.set("lxrdh", order.get("rcv_phone1"));
			o.set("shdz", order.get("rcv_addr1"));

			o.set("xdrid", user.get("id"));
			o.set("xdrmc", user.get("mc"));
			o.set("xdrdh", user.get("mobile"));
			o.set("xdsj", dateStr);
			o.set("city_id", user.getStr("city_id"));
			o.set("city_name", user.getStr("city_name"));
			o.set("area_id", user.getStr("area_id"));
			o.set("area_name", user.getStr("area_name"));

			// 同步到数据库
			order.save();
			o.save();
			try{
			AbTcContrller.tuisong(orderid);
			}catch (Exception e) {
				e.printStackTrace();
				System.out.println("极光推送失败");
			}
			String yzm = RandomUtil.createRandomPwd(6);
			SmsMessage.SendMessage(order.getStr(AbTcExpressOrder.RCV_PHONE1),
					yzm);
			insertOrderSms(sn, order.getStr(AbTcExpressOrder.RCV_PHONE1), yzm);

			if (!StringUtil.isNull(order.getStr(AbTcExpressOrder.RCV_PHONE2))) {
				yzm = RandomUtil.createRandomPwd(6);
				SmsMessage.SendMessage(order
						.getStr(AbTcExpressOrder.RCV_PHONE2), yzm);
				insertOrderSms(sn, order.getStr(AbTcExpressOrder.RCV_PHONE2),
						yzm);
			}
			if (!StringUtil.isNull(order.getStr(AbTcExpressOrder.RCV_PHONE3))) {
				yzm = RandomUtil.createRandomPwd(6);
				SmsMessage.SendMessage(order
						.getStr(AbTcExpressOrder.RCV_PHONE3), yzm);
				insertOrderSms(sn, order.getStr(AbTcExpressOrder.RCV_PHONE3),
						yzm);
			}
			if (!StringUtil.isNull(order.getStr(AbTcExpressOrder.RCV_PHONE4))) {
				yzm = RandomUtil.createRandomPwd(6);
				SmsMessage.SendMessage(order
						.getStr(AbTcExpressOrder.RCV_PHONE4), yzm);
				insertOrderSms(sn, order.getStr(AbTcExpressOrder.RCV_PHONE4),
						yzm);
			}
			if (!StringUtil.isNull(order.getStr(AbTcExpressOrder.RCV_PHONE5))) {
				yzm = RandomUtil.createRandomPwd(6);
				SmsMessage.SendMessage(order
						.getStr(AbTcExpressOrder.RCV_PHONE5), yzm);
				insertOrderSms(sn, order.getStr(AbTcExpressOrder.RCV_PHONE5),
						yzm);
			}

			// 在线支付和悬赏下单，进行余额和会员卡的抵扣
			if ("1".equals(orderStyle) && "1".equals(payType)) {
				// 会员卡总金额
				BigDecimal bd = new BigDecimal(0);
				List<Record> ecards = Db.find(CommonSQL
						.findUserCard(user.getStr(SysUser.ID),
								CommonStaticData.CARD_TYPE_EXPENSE));
				if (ecards != null && ecards.size() > 0) {
					for (Record r : ecards) {
						bd = bd.add(r.getBigDecimal(AbCard.RMONEY));
					}
				}
				// 订单实际支付金额
				BigDecimal finalprice = new BigDecimal(total);
				// 个人账户余额
				BigDecimal zhmoney = user.getBigDecimal(SysUser.ZHYE);
				// 验证余额
				if (zhmoney.add(bd).compareTo(finalprice) == -1) {
					renderJson("0");
				} else {
					o.set(AbOrder.ZFZT, "1");
					o.set(AbOrder.ZFSJ, dateStr);
					o.update();
					// 判断是否有消费会员卡抵扣金额
					finalprice = CommonProcess.cardDecution(ecards, finalprice,
							orderid);
					// 增加消费积分
					user.set(SysUser.JIFEN, user.getInt(SysUser.JIFEN)
							+ new BigDecimal(total).intValue());
					// 会员卡抵扣不了的，扣账户余额
					if (finalprice.doubleValue() > 0) {
						user.set("zhye", user.getBigDecimal("zhye").subtract(
								finalprice));
					}
					user.update();
				}
			}
			// ----------------------------
			// renderJson("{\"success\":\"success\"}");
			resultMap.put("result", RESULT_SUCCESS);
			resultMap.put("msg", "下单成功");
		} catch (Exception e) {
			e.printStackTrace();
		
			resultMap.put("result", RESULT_FAIL);
			resultMap.put("msg", "请核对下单参数");
		}
		}catch (Exception e) {
			e.printStackTrace();
			resultMap.put("result", RESULT_FAIL);
			resultMap.put("msg", "请核对下单参数");
		}
	}
	renderJson(resultMap);
	}
private SysUser getUserByMobile(String mobile) {
	String sql = "select * from sys_user where mobile=?";
	SysUser u = SysUser.dao.findFirst(sql, mobile);
	if (u != null) {
		return u;
	}
	return null;
}
}
