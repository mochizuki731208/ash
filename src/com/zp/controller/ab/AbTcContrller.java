package com.zp.controller.ab;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.zp.entity.AbCard;
import com.zp.entity.AbCityarea;
import com.zp.entity.AbContactPerson;
import com.zp.entity.AbFmcar;
import com.zp.entity.AbOrder;
import com.zp.entity.AbOrderSms;
import com.zp.entity.AbTcConfig;
import com.zp.entity.AbTcExpressOrder;
import com.zp.entity.AbUserAddr;
import com.zp.entity.SysConfig;
import com.zp.entity.SysCzTx;
import com.zp.entity.SysUser;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.interceptor.AccessAbInterceptor;
import com.zp.tools.CommonProcess;
import com.zp.tools.CommonSQL;
import com.zp.tools.CommonStaticData;
import com.zp.tools.DateUtil;
import com.zp.tools.RandomUtil;
import com.zp.tools.SmsMessage;
import com.zp.tools.StringUtil;
import com.zp.tools.alipay.AlipaySubmit;

public class AbTcContrller extends Controller {
	/**
	 * order form 合并 saveStep1 and saveStep2 两个方法
	 */
	public void isUser() {
		
		SysUser user = this.getSessionAttr("abuser");
		if(user!=null){
			renderJson("0");
		}else{
			renderJson("3");
		}
	}
	
	private String parseEncode(String en) {
		// TODO Auto-generated method stub
		String to = "";
		try {
			to = new String(en.getBytes("ISO-8859-1"),"utf-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return to;
	}
	public void index() {
		/*
		 * this.setSessionAttr("tc_order", null); this.setAttr("zb",
		 * this.getPara("zb")); start(); } public void test() {
		 */
		this.setSessionAttr("tc_order", null);
		
		this.setAttr("vp", this.getPara("vp"));
		this.setAttr("wp", this.getPara("wp"));
		this.setAttr("zb", this.getPara("zb"));
		if(!StringUtil.isNull(this.getPara("mc")))
			this.setAttr("mc", parseEncode(this.getPara("mc")));
		if(!StringUtil.isNull(this.getPara("from")))
		this.setAttr("from", parseEncode(this.getPara("from")));
		if(!StringUtil.isNull(this.getPara("to")))
		this.setAttr("to", parseEncode(this.getPara("to")));
		this.setAttr("mobile", this.getPara("mobile"));
		// start();
		AbCityarea city = this.getSessionAttr("currcity");
		this.setAttr("currcity", city);
		
		// -------------------------
		// cars
		List carsList = new ArrayList();
		List<AbTcConfig> cars = AbTcConfig.dao
				.find(
						"select distinct name from ab_tc_config where type in ('01') and city_id=? order by listorder",
						city.getStr("id"));
		for (AbTcConfig tc : cars) {
			String name = tc.getStr("name");
			List<AbTcConfig> carConfigList = AbTcConfig.dao
					.find(
							"select * from ab_tc_config where type in ('01') and name=? and city_id=?",
							name, city.getStr("id"));
			Map map = this.parseCarConfig(carConfigList);
			map.put("name", name);
			carsList.add(map);
		}
		this.setAttr("cars", carsList);

		// -------------------------
		// carsloop
		List carsLoop = new ArrayList();
		// 大于4个的处理
		if (carsList.size() > 4) {
			for (int i = 0; i <= carsList.size() / 4; i++) {
				List list = new ArrayList();
				carsLoop.add(list);

				if (carsList.size() > i * 4)
					list.add(carsList.get(i * 4));
				if (carsList.size() > i * 4 + 1)
					list.add(carsList.get(i * 4 + 1));
				if (carsList.size() > i * 4 + 2)
					list.add(carsList.get(i * 4 + 2));
				if (carsList.size() > i * 4 + 3)
					list.add(carsList.get(i * 4 + 3));
			}
		} else {
			carsLoop.add(carsList);
		}
		this.setAttr("carsLoop", carsLoop);
		// -------------------------
		SysUser abuser = this.getSessionAttr("abuser");
		this.setAttr("abuser", abuser);
		// ---------------------------
		// prepare
		this.setAttr("zb", this.getPara("zb"));

		List<AbTcConfig> goodsTypeList = AbTcConfig.dao
				.find("select * from ab_tc_config where type in ('03')");

		BigDecimal bd = new BigDecimal(0);
		if (abuser != null) {
			// 登录用户可以查找会员卡金额
			List<Record> ecards = Db.find(CommonSQL.findUserCard(abuser
					.getStr(SysUser.ID), CommonStaticData.CARD_TYPE_EXPENSE));
			if (ecards != null && ecards.size() > 0) {
				for (Record r : ecards) {
					bd = bd.add(r.getBigDecimal(AbCard.RMONEY));
				}
			}
		}
		this.setAttr("goodsList", goodsTypeList);
		this.setAttr("cardtotalmoney", bd.doubleValue());
		if("S".equals(this.getPara("zb"))){//如果是商家 /ab/logi 物流企业  其他事店铺
			render("/ab/tc/index_s.html");
		}else if ("F".equals(this.getPara("zb"))){
			String way=this.getPara("way");
			String carid=this.getPara("carid");
			AbFmcar car=AbFmcar.dao.findById(carid);
			setAttr("way",way);
			setAttr("car", car);
			render("/ab/tc/index_fmcar.html");			
		}else
		{
			this.setAttr("msg", "");
			render("/ab/tc/index.html");
		}
		
	}

	public void start() {
		AbCityarea city = this.getSessionAttr("currcity");
		this.setAttr("currcity", city);

		// -------------------------
		// cars
		List carsList = new ArrayList();
		List<AbTcConfig> cars = AbTcConfig.dao
				.find(
						"select distinct name from ab_tc_config where type in ('01') and city_id=? order by listorder",
						city.getStr("id"));
		for (AbTcConfig tc : cars) {
			String name = tc.getStr("name");
			List<AbTcConfig> carConfigList = AbTcConfig.dao
					.find(
							"select * from ab_tc_config where type in ('01') and name=? and city_id=?",
							name, city.getStr("id"));
			Map map = this.parseCarConfig(carConfigList);
			map.put("name", name);
			carsList.add(map);
		}
		this.setAttr("cars", carsList);

		// -------------------------
		// carsloop
		List carsLoop = new ArrayList();
		// 大于4个的处理
		if (carsList.size() > 4) {
			for (int i = 0; i <= carsList.size() / 4; i++) {
				List list = new ArrayList();
				carsLoop.add(list);

				if (carsList.size() > i * 4)
					list.add(carsList.get(i * 4));
				if (carsList.size() > i * 4 + 1)
					list.add(carsList.get(i * 4 + 1));
				if (carsList.size() > i * 4 + 2)
					list.add(carsList.get(i * 4 + 2));
				if (carsList.size() > i * 4 + 3)
					list.add(carsList.get(i * 4 + 3));
			}
		} else {
			carsLoop.add(carsList);
		}
		this.setAttr("carsLoop", carsLoop);
		// -------------------------
		SysUser abuser = this.getSessionAttr("abuser");
		this.setAttr("abuser", abuser);
		render("/ab/tc/start.html");
	}

	@Before(AccessAbInterceptor.class)
	public void prepare() {
		this.setAttr("zb", this.getPara("zb"));
		this.setAttr("tc_order", this.getSessionAttr("tc_order"));

		List<AbTcConfig> goodsTypeList = AbTcConfig.dao
				.find("select * from ab_tc_config where type in ('03')");
		// 查找会员卡金额
		SysUser abuser = this.getSessionAttr("abuser");
		BigDecimal bd = new BigDecimal(0);
		List<Record> ecards = Db.find(CommonSQL.findUserCard(abuser
				.getStr(SysUser.ID), CommonStaticData.CARD_TYPE_EXPENSE));
		if (ecards != null && ecards.size() > 0) {
			for (Record r : ecards) {
				bd = bd.add(r.getBigDecimal(AbCard.RMONEY));
			}
		}
		this.setAttr("goodsList", goodsTypeList);
		this.setAttr("cardtotalmoney", bd.doubleValue());
		render("/ab/tc/prepare.html");
	}

	public void pay() {
		this.setAttr("tc_order", this.getSessionAttr("tc_order"));
		this.setAttr("isok", this.getPara("isok"));
		render("/ab/tc/pay.html");
	}

	private Map<String, Object> parseCarConfig(List<AbTcConfig> list) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (AbTcConfig c : list) {
			map.put(c.getStr("key"), c.get("value"));
		}
		return map;
	}

	private SysUser getUserByMobile(String mobile) {
		String sql = "select * from sys_user where mobile=?";
		SysUser u = SysUser.dao.findFirst(sql, mobile);
		if (u != null) {
			return u;
		}
		return null;
	}

	public void saveStep1() {
		String basePrice = StringUtil.toStr(this.getPara("basePrice"));
		String overPrice = StringUtil.toStr(this.getPara("overPrice"));
		String kilo = StringUtil.toStr(this.getPara("kilo"));
		String carType = StringUtil.toStr(this.getPara("carType"));
		String total = StringUtil.toStr(this.getPara("sum"));
		String from = StringUtil.toStr(this.getPara("from"));
		String to = StringUtil.toStr(this.getPara("to"));
		String nightPrice = StringUtil.toStr(this.getPara("nightPrice"));
		String sendName = StringUtil.toStr(this.getPara("sendName"));
		String sendPhone = StringUtil.toStr(this.getPara("sendPhone"));
		String rcvName = StringUtil.toStr(this.getPara("rcvName"));
		String rcvPhone = StringUtil.toStr(this.getPara("rcvPhone"));
		String moreMoney = StringUtil.toStr(this.getPara("moreMoney"));
		String orderStyle = StringUtil.toStr(this.getPara("orderStyle"));
		String minPrice = StringUtil.toStr(this.getPara("minPrice"));
		String maxPrice = StringUtil.toStr(this.getPara("maxPrice"));

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

		System.out.println("tc.saveStep01");
		try {
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
			order.set("more_money", "".equals(moreMoney) ? 0 : Double
					.parseDouble(moreMoney));
			order.set("cc", cc);
			this.setSessionAttr("tc_order", order);
			renderJson("{\"success\":\"success\"}");
		} catch (Exception e) {
			e.printStackTrace();
			renderJson("{\"fail\":\"fail\"}");
		}
	}

	public void saveStep2() {
		String total = StringUtil.toStr(this.getPara("sum"));
		String payType = StringUtil.toStr(this.getPara("payType"));
		if (StringUtil.isNull(payType)) {
			payType = "1";// 默认为在线支付
		}
		AbTcExpressOrder order = this.getSessionAttr("tc_order");
		// 快递订单模式，2：悬赏，1：招标
		String orderStyle = order.getStr(AbTcExpressOrder.STYLE);
		if (total == null)
			total = "";

		SysUser user = this.getSessionAttr("abuser");
		user = SysUser.dao.findById(user.getStr(SysUser.ID));

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
		try {

			order.set("total_price", new BigDecimal(Double.parseDouble(total)));
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
			// if (Db.findFirst(
			// "select count(id) cnt from ab_tc_express_order where sn like 'TC"
			// + DateUtil.getCurrentDate("yyyyMMdd") + "%'")
			// .getLong("cnt") < 1) {
			// sn = "TC" + DateUtil.getCurrentDate("yyyyMMdd") + "000001";
			// } else {
			// // 取最大值加1
			// sn = StringUtil.convertStr(Db.findFirst(
			// "select max(sn) maxid from ab_tc_express_order where sn like 'TC"
			// + DateUtil.getCurrentDate("yyyyMMdd")
			// + "%'").getStr("maxid"));
			// sn = StringUtil.getStrSeqCode(false, sn, 6);
			// }
			sn = "TC" + RandomUtil.createRandomNumPwd(9);
			order.set("sn", sn);

			// 保存父订单表
			// 生成订单数据
			String orderid = StringUtil.getUuid32();
			// 保存购物车订单数据
			AbOrder o = new AbOrder();
			o.set("id", orderid);
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

			tuisong(orderid);
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
					renderJson("1");
				}
			} else {
				renderJson("1");
			}
			// 这个是测试司机
			// jpushOrder(new String[]{"15999970376"}, o, order);
		} catch (Exception e) {
			e.printStackTrace();
			renderJson("2");
		}
	}

	/**
	 * order form 合并 saveStep1 and saveStep2 两个方法
	 */
	public void saveOrderForm() {
		// --------------------------------
		// setp1
		String basePrice = StringUtil.toStr(this.getPara("basePrice"));
		String overPrice = StringUtil.toStr(this.getPara("overPrice"));
		String kilo = StringUtil.toStr(this.getPara("kilo"));
		String carType = StringUtil.toStr(this.getPara("carType"));
		String total = StringUtil.toStr(this.getPara("sum"));
		String from = StringUtil.toStr(this.getPara("from"));
		String to = StringUtil.toStr(this.getPara("to"));
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

		SysUser user = this.getSessionAttr("abuser");
		
		if(user!=null&&SysUser.ID!=null&&user.getStr(SysUser.ID) != null){
			user = SysUser.dao.findById(user.getStr(SysUser.ID));
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
	
				tuisong(orderid);
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
					if(zhmoney==null){
						renderJson("0");
					}else if (zhmoney.add(bd).compareTo(finalprice) == -1) {
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
						renderJson("1");
					}
				} else {
					renderJson("1");
				}
				// ----------------------------
				this.setSessionAttr("tc_order", order);
				// renderJson("{\"success\":\"success\"}");
			} catch (Exception e) {
				e.printStackTrace();
				// renderJson("{\"fail\":\"fail\"}");
				renderJson("2");
			}
		}else{
			renderJson("3");
		}
	}

	/**
	 * preview order 预览order内容
	 */
	public void previewOrder() {
		// --------------------------------
		// setp1
		String basePrice = StringUtil.toStr(this.getPara("basePrice"));
		String overPrice = StringUtil.toStr(this.getPara("overPrice"));
		String kilo = StringUtil.toStr(this.getPara("kilo"));
		String carType = StringUtil.toStr(this.getPara("carType"));
		String total = StringUtil.toStr(this.getPara("sum"));
		String from = StringUtil.toStr(this.getPara("from"));
		String to = StringUtil.toStr(this.getPara("to"));
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

		SysUser user = this.getSessionAttr("abuser");
		user = SysUser.dao.findById(user.getStr(SysUser.ID));

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
			System.out.println("onePrice : " + onePrice);
			System.out.println("one_price : " + order.get("one_price"));
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
			/*
			 * String sn = null; sn = "TC" + RandomUtil.createRandomNumPwd(9);
			 * order.set("sn", sn);
			 */
			// 保存父订单表
			// 生成订单数据
			// String orderid = StringUtil.getUuid32();
			// 保存购物车订单数据
			AbOrder o = new AbOrder();
			// o.set("id", orderid);
			// o.set("sn", sn);
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
			// order.save();
			// o.save();

			// ----------------------------
			this.setAttr("vo", o);
			this.setAttr("to", order);
		} catch (Exception e) {
			e.printStackTrace();
		}
		render("/ab/tc/showtcdd.html");
	}

	public static void insertOrderSms(String orderid, String phone, String yzm) {
		AbOrderSms sms = AbOrderSms.dao
				.findFirst("select * from ab_order_sms where orderid='"
						+ orderid + "' and shrdh='" + phone + "'");
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

	public static void tuisong(String orderid) {

		JPushClient jpushClient = new JPushClient("27001ef040594c440f79fcc1",
				"469e1e136c984f33dc2a28e9", 3);
		// 初始化jpush，填入的appkey，目前是我个人测试号，这个key是集成在司机端app里的

		// PushPayload payload = buildPushObject_by_notification("cc",null);
		// PushPayload payload = buildPushObject_all_alias_alert();
		// PushPayload payload = PushPayload.alertAll("附近有新的订单，请点击查看！");

		try {
			Map<String, String> extras = new HashMap<String, String>();
			extras.put("orderId", orderid);
			PushPayload payload = PushPayload.newBuilder().setPlatform(
					Platform.all()).setAudience(Audience.all())
					.setNotification(
							Notification.android("附近有新的订单，请点击查看！", "123快运",
									extras)).build();
			PushResult result = jpushClient.sendPush(payload);

			System.out.println("Got result - " + result);
			// LOG.info("Got result - " + result);

		} catch (APIConnectionException e) {
			// Connection error, should retry later
			// LOG.error("Connection error, should retry later", e);
			e.printStackTrace();

		} catch (APIRequestException e) {
			e.printStackTrace();

		}
	}

	/**
	 * 推送订单给司机 add by hedeyou 2015-04-22 01：02
	 * 
	 * @param accounts
	 *            司机电话号码数组
	 * @param orderVo
	 *            订单信息
	 * @param orderDetailVo
	 *            订单详细信息
	 */
	private void jpushOrder(String accounts[], AbOrder orderVo,
			AbTcExpressOrder orderDetailVo) {
		if (accounts == null || accounts.length == 0 || orderVo == null
				&& orderDetailVo == null) {
			return;
		}
		try {
			JPushClient jpushClient = new JPushClient();
			Map<String, String> extras = new HashMap<String, String>();
			extras.put("pushType", "getOrder");
			// a.xdrdh, a.xdsj, a.sjjd,a.sjwd,b.min_price,b.max_price,
			// b.sn,b.send_time,b.send_addr,b.style,b.rcv_addr1,b.total_price,
			// b.rcv_addr2,b.rcv_addr3,b.rcv_addr4,b.rcv_addr5,b.car,
			// b.people,b.huidan_price,b.pay_type,b.weight,
			// b.lift ,a.ddzt, a.xdrid 这是查询订单是用到的列
			PushPayload payload = PushPayload
					.newBuilder()
					.setPlatform(Platform.all())
					.setAudience(Audience.alias(accounts))
					.setNotification(
							Notification.android("您有一个订单", "您有一个订单", extras))
					.setMessage(
							Message
									.newBuilder()
									.setMsgContent("getOrder")
									// 下单 标示
									.addExtra(AbOrder.XDRDH,
											orderVo.getStr(AbOrder.XDRDH))
									.addExtra(AbOrder.XDSJ,
											orderVo.getStr(AbOrder.XDSJ))
									.addExtra(AbOrder.SJJD,
											orderVo.getStr(AbOrder.SJJD))
									.addExtra(AbOrder.SJWD,
											orderVo.getStr(AbOrder.SJWD))
									.addExtra(AbOrder.DDZT,
											orderVo.getStr(AbOrder.DDZT))
									.addExtra(AbOrder.XDRID,
											orderVo.getStr(AbOrder.XDRID))
									.addExtra(
											AbTcExpressOrder.MIN_PRICE,
											orderDetailVo
													.getStr(AbTcExpressOrder.MIN_PRICE))
									.addExtra(
											AbTcExpressOrder.SN,
											orderDetailVo
													.getStr(AbTcExpressOrder.SN))
									.addExtra(
											AbTcExpressOrder.SEND_TIME,
											orderDetailVo
													.getStr(AbTcExpressOrder.SEND_TIME))
									.addExtra(
											AbTcExpressOrder.SEND_ADDR,
											orderDetailVo
													.getStr(AbTcExpressOrder.SEND_ADDR))
									.addExtra(
											AbTcExpressOrder.STYLE,
											orderDetailVo
													.getStr(AbTcExpressOrder.STYLE))
									.addExtra(
											AbTcExpressOrder.RCV_ADDR1,
											orderDetailVo
													.getStr(AbTcExpressOrder.RCV_ADDR1))
									.addExtra(
											AbTcExpressOrder.RCV_ADDR2,
											orderDetailVo
													.getStr(AbTcExpressOrder.RCV_ADDR2))
									.addExtra(
											AbTcExpressOrder.RCV_ADDR3,
											orderDetailVo
													.getStr(AbTcExpressOrder.RCV_ADDR3))
									.addExtra(
											AbTcExpressOrder.RCV_ADDR4,
											orderDetailVo
													.getStr(AbTcExpressOrder.RCV_ADDR4))
									.addExtra(
											AbTcExpressOrder.RCV_ADDR5,
											orderDetailVo
													.getStr(AbTcExpressOrder.RCV_ADDR5))

									.addExtra(
											AbTcExpressOrder.TOTAL_PRICE,
											orderDetailVo
													.getStr(AbTcExpressOrder.TOTAL_PRICE))
									.addExtra(
											AbTcExpressOrder.CAR,
											orderDetailVo
													.getStr(AbTcExpressOrder.CAR))
									.addExtra(
											AbTcExpressOrder.PEOPLE,
											orderDetailVo
													.getStr(AbTcExpressOrder.PEOPLE))
									.addExtra(
											AbTcExpressOrder.HUIDAN_PRICE,
											orderDetailVo
													.getStr(AbTcExpressOrder.HUIDAN_PRICE))
									.addExtra(
											AbTcExpressOrder.PAY_TYPE,
											orderDetailVo
													.getStr(AbTcExpressOrder.PAY_TYPE))
									.addExtra(
											AbTcExpressOrder.WEIGHT,
											orderDetailVo
													.getStr(AbTcExpressOrder.WEIGHT))
									.addExtra(
											AbTcExpressOrder.LIFT,
											orderDetailVo
													.getStr(AbTcExpressOrder.LIFT))
									.build()).build();
			jpushClient.sendPush(payload);
		} catch (APIConnectionException e) {
			e.printStackTrace();
		} catch (APIRequestException e) {
			e.printStackTrace();
		}
	}

	/** 常用地址管理 */

	@Before(AccessAbInterceptor.class)
	public void cydz() {
		SysUser abuser = this.getSessionAttr("abuser");
		String sql = "select * from ab_user_address where user_id=? order by order_num";
		List<AbUserAddr> addrlist = AbUserAddr.dao.find(sql, abuser
				.getStr("id"));
		this.setAttr("addrlist", addrlist);

		this.render("/ab/user/listaddr.html");
	}

	/**
	 * 常用联系人管理
	 */
	public void cylxr() {
		SysUser abuser = this.getSessionAttr("abuser");
		if (abuser == null) {
			// 跳转到登陆界面
			redirect("/ab/login");
			return;
		}
		String sql = "select * from ab_contact_person where user_id=? order by opt_date desc";
		List<AbContactPerson> personlist = AbContactPerson.dao.find(sql, abuser
				.getStr("id"));
		this.setAttr("personlist", personlist);
		this.setAttr("user_id", abuser.getStr("id"));

		this.render("/ab/user/listcontactperson.html");
	}

	/**
	 * 常用联系人添加或修改
	 */
	public void cylxr_deal() {
		String user_id = getPara("user_id");
		String add_user_id = getPara("add_user_id");
		String add_name = getPara("add_name");
		String add_mobile = getPara("add_mobile");
		if (add_user_id == null || add_user_id.equals("")) {
			AbContactPerson person = new AbContactPerson();
			person.set(AbContactPerson.USER_ID, user_id);
			person.set(AbContactPerson.ID, StringUtil.getUuid32());
			person.set(AbContactPerson.LINKMAN, add_name);
			person.set(AbContactPerson.MOBILE, add_mobile);
			person.set(AbContactPerson.OPT_DATE, DateUtil.format(new Date(),
					"yyyy-MM-dd HH:mm:ss"));
			person.save();
		} else {
			AbContactPerson person = AbContactPerson.dao.findById(add_user_id);
			person.set(AbContactPerson.LINKMAN, add_name);
			person.set(AbContactPerson.MOBILE, add_mobile);
			person.update();
		}
		this.redirect("/ab/tc/cylxr");
	}

	/**
	 * 删除常用收货人
	 */
	public void deletePerson() {
		String add_user_id = getPara("add_user_id");
		AbContactPerson.dao.deleteById(add_user_id);
		this.redirect("/ab/tc/cylxr");
	}

	public void editaddr() {
		String id = this.getPara("id");
		if (id != null && !id.equals("")) {
			AbUserAddr addr = AbUserAddr.dao.findById(id);
			this.setAttr("addr", addr);
		}
		this.render("/ab/user/editaddr.html");
	}

	public void saveaddr() {
		String id = this.getPara("id");
		String name = this.getPara("name");
		
		String linkman = this.getPara("linkman");
		String mobile = this.getPara("mobile");
		
		String addr = this.getPara("addr");
		String order_num = this.getPara("order_num");
		String jd = this.getPara("jd");
		String wd = this.getPara("wd");
		SysUser abuser = this.getSessionAttr("abuser");
		if (id == null || "".equals(id)) {
			AbUserAddr a = new AbUserAddr();
			a.set("id", StringUtil.getUuid32());
			a.set("name", name);
			a.set("linkman", linkman);
			a.set("mobile", mobile);
			a.set("addr", addr);
			a.set("order_num", order_num);
			a.set("user_id", abuser.get("id"));
			a.set("jd", jd);
			a.set("wd", wd);
			a.save();
		} else {
			AbUserAddr a = AbUserAddr.dao.findById(id);
			if (a == null) {
				renderJson("{\"fail\":\"fail\"}");
				return;
			} else {
				a.set("name", name);
				a.set("linkman", linkman);
				a.set("mobile", mobile);
				a.set("addr", addr);
				a.set("order_num", order_num);
				a.update();
			}
		}
		renderJson("{\"success\":\"success\"}");
	}

	public void deladdr() {
		String id = this.getPara("id");
		AbUserAddr a = AbUserAddr.dao.findById(id);
		if (a == null) {
			renderJson("{\"fail\":\"fail\"}");
		} else {
			a.delete();
			renderJson("{\"success\":\"success\"}");
		}
	}

	public void payorder() {
		Map result = new HashMap();
		String orderid = this.getPara("id");
		AbOrder o = AbOrder.dao.findById(orderid);
		SysUser user = this.getSessionAttr("abuser");
		user = SysUser.dao.findById(user.getStr(SysUser.ID));
		AbTcExpressOrder ateo = AbTcExpressOrder.dao.findFirst(
				"select * from ab_tc_express_order where sn=?", o.getStr("sn"));
		// this.redirect("http://127.0.0.1/ad/dd/initalipay?id=" +
		// o.getStr("sn"));
		if (null != ateo && null != ateo.getInt("pay_type")
				&& 1 == ateo.getInt("pay_type")) {
			result.put("key", 3);
			result.put("id", ateo.getInt("id"));
			renderJson(result);
			return;
		}
		// 会员卡总金额
		BigDecimal bd = new BigDecimal(0);
		List<Record> ecards = Db.find(CommonSQL.findUserCard(user
				.getStr(SysUser.ID), CommonStaticData.CARD_TYPE_EXPENSE));
		if (ecards != null && ecards.size() > 0) {
			for (Record r : ecards) {
				bd = bd.add(r.getBigDecimal(AbCard.RMONEY));
			}
		}
		// 订单实际支付金额
		BigDecimal finalprice = o.getBigDecimal(AbOrder.DDZJE);
		// 个人账户余额
		BigDecimal zhmoney = user.getBigDecimal(SysUser.ZHYE);
		// 支付方式是在线支付，验证余额
		if (zhmoney.add(bd).compareTo(finalprice) == -1) {
			result.put("key", 0);
			renderJson(result);
		} else {
			try {
				String dateStr = DateUtil.getCurrentDate();
				o.set(AbOrder.ZFZT, "1");
				o.set(AbOrder.ZFSJ, dateStr);
				// 判断是否有消费会员卡抵扣金额
				finalprice = CommonProcess.cardDecution(ecards, finalprice,
						orderid);
				// 增加消费积分
				user.set(SysUser.JIFEN, user.getInt(SysUser.JIFEN)
						+ o.getBigDecimal(AbOrder.DDZJE).intValue());
				// 会员卡抵扣不了的，扣账户余额
				if (finalprice.doubleValue() > 0) {
					user.set("zhye", user.getBigDecimal("zhye").subtract(
							finalprice));
				}
				user.update();
				o.update();
				result.put("key", 1);
				renderJson(result);
			} catch (Exception e) {
				e.printStackTrace();
				result.put("key", 2);
				renderJson(result);
			}

		}

	}

	public void initalipay() {
		String id = StringUtil.toStr(this.getPara("id"));
		AbTcExpressOrder aeo = AbTcExpressOrder.dao.findById(id);
		String return_url = "http://www.yijuhome.cn/ab/tc/payBack";
		String tradeno = System.currentTimeMillis() + "";
		SysCzTx sct = new SysCzTx();
		sct.set("id", StringUtil.getUuid32());
		sct.set("tradeno", tradeno);
		sct.set("orderid", aeo.getStr("sn"));
		sct.save();
		try {
			// aeo.set("total_price", new BigDecimal("0.01"));//上线的时候注释掉
			this.redirect(alipayapi(tradeno, "同城订单付款", aeo.getBigDecimal(
					"total_price").toString(), aeo.getStr("goods_type"),
					return_url));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void payBack() {
		String out_trade_no = StringUtil.toStr(this.getPara("out_trade_no"));
		String total_fee = StringUtil.toStr(this.getPara("total_fee"));
		String trade_status = StringUtil.toStr(this.getPara("trade_status"));
		List<SysCzTx> cztx_list = SysCzTx.dao
				.find("select * from sys_cz_tx where tradeno='" + out_trade_no
						+ "'");
		if (cztx_list.size() > 0) {
			if ("TRADE_SUCCESS".equals(trade_status)) {
				SysCzTx sct = cztx_list.get(0);
				AbOrder order = AbOrder.dao.findFirst(
						"select * from ab_order where sn=?", sct
								.getStr("orderid"));
				order.set("zfzt", "1");
				order.update();
			}
		}
		this.render("/ab/dd/ddsuccess.html");
	}

	public String alipayapi(String WIDout_trade_no, String WIDsubject,
			String WIDtotal_fee, String WIDbody, String return_url)
			throws UnsupportedEncodingException {
		// 查询支付宝相关信息
		SysUser u = this.getSessionAttr("abuser");

		SysConfig config = null;
		boolean flag = false;
		if (null != u.getStr("city_id") && u.getStr("city_id").length() > 0) {
			AbCityarea aca = AbCityarea.dao.findById(u.getStr("area_id"));
			if (null != aca && null != aca.getStr("user_id")
					&& aca.getStr("user_id").length() > 0) {
				config = SysConfig.dao
						.findFirst("select * from sys_config where c6='"
								+ aca.getStr("user_id") + "'");
				if (null != config) {
					flag = true;
				}
			}
		}
		if (!flag) {
			config = new SysConfig();
			config.set("c1", "2088511526488301");
			config.set("c2", "sl9w4o4of1h3hbg81ko20o8f7cjyd52b");
			config.set("c5", "banxiandianzi@163.com");
		}
		// 支付类型
		String payment_type = "1";
		// 必填，不能修改
		// 卖家支付宝帐户
		// String seller_email = new
		// String(this.getPara("WIDseller_email").getBytes("ISO-8859-1"),"UTF-8");
		String seller_email = config.getStr("c5");
		// 必填

		// 商户订单号
		String out_trade_no = new String(WIDout_trade_no.getBytes("utf-8"),
				"UTF-8");
		// 商户网站订单系统中唯一订单号，必填

		// 订单名称
		String subject = new String(WIDsubject.getBytes("utf-8"), "UTF-8");
		// 必填
		// 付款金额
		String total_fee = new String(WIDtotal_fee.getBytes("utf-8"), "UTF-8");
		// 必填
		// 订单描述

		String body = new String(WIDbody.getBytes("utf-8"), "UTF-8");
		// 商品展示地址
		String show_url = "http://www.yijuhome.cn";
		// 需以http://开头的完整路径，例如：http://www.商户网址.com/myorder.html
		// 把请求参数打包成数组
		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", "create_direct_pay_by_user");
		sParaTemp.put("partner", config.getStr("c1"));
		sParaTemp.put("_input_charset", "utf-8");
		sParaTemp.put("payment_type", payment_type);
		sParaTemp.put("return_url", return_url);
		sParaTemp.put("seller_email", seller_email);
		sParaTemp.put("out_trade_no", out_trade_no);
		sParaTemp.put("subject", subject);
		sParaTemp.put("total_fee", total_fee);
		sParaTemp.put("body", body);
		sParaTemp.put("show_url", show_url);
		// 建立请求
		String sHtmlText = AlipaySubmit.buildRequest(sParaTemp, "get", "确认");
		// this.redirect(sHtmlText);
		return sHtmlText;
	}

	/** 快递的宣传页面 */
	public void ky() {
		// this.render("/ab/wl/ky/index.html");
		this.render("/ab/ps/index.html");
		this.setSessionAttr("zb", "");
		this.setAttr("zb", this.getSessionAttr("zb"));
	}

	public void kyDriverJoin() {
		// this.render("/ab/wl/ky/driverJoin.html");
		this.render("/ab/ps/sj.html");
		this.setAttr("zb", this.getSessionAttr("zb"));
	}

	public void kyZifei() {
		// this.render("/ab/wl/ky/zifei.html");
		this.render("/ab/ps/zf.html");
		this.setAttr("zb", this.getSessionAttr("zb"));
	}

	public void kyJieshao() {
		this.render("/ab/ps/zx.html");
		this.setAttr("zb", this.getSessionAttr("zb"));
	}

	public void tc() {
		this.render("/ab/wl/tc/index.html");
	}

	public void tcDriverJoin() {
		this.render("/ab/wl/tc/driverJoin.html");
	}

	public void tcZifei() {
		this.render("/ab/wl/tc/zifei.html");
	}

	public void zx() {
		// this.render("/ab/wl/zx/index.html");
		this.render("/ab/ps/zx.html");
		this.setSessionAttr("zb", "Y");
		this.setAttr("zb", this.getSessionAttr("zb"));
	}

	public void zxDriverJoin() {
		this.render("/ab/wl/zx/driverJoin.html");
	}

	public void zxZifei() {
		this.render("/ab/wl/zx/zifei.html");
	}

	public void selOldAddr() {
		try {
			SysUser abuser = this.getSessionAttr("abuser");
			if (abuser == null) {
				renderJson("{\"fail\":\"fail\"}");
				return;
			}
			String sql = "select * from ab_user_address where user_id=? order by order_num";
			List<AbUserAddr> addrlist = AbUserAddr.dao.find(sql, abuser
					.getStr("id"));
			renderJson(addrlist);
		} catch (Exception e) {
			renderJson("{\"fail\":\"fail\"}");
			e.printStackTrace();
		}
	}

	// 查询保险单
	public void bxd() {
		SysUser abuser = this.getSessionAttr("abuser");
		if (abuser == null) {
			redirect("/ab/login");
			return;
		}
		String index = getPara();
		this.setAttr("index", index);
		String query_sql = "select * from ab_policy b where b.user_id='" + abuser.getStr("id") + "' ";
		String order_sql = " order by b.opt_date desc";
		// 查询所有保单
		PageUtil all_listpage = DbPage.paginate(
				this.getParaToInt("all_curPage", 1), 14, query_sql + order_sql);
		this.setAttr("all_listpage", all_listpage);
		// 查询未支付
		String wzf_sql = " and b.policy_status='01' ";
		PageUtil wzf_listpage = DbPage.paginate(
				this.getParaToInt("wzf_curPage", 1), 14, query_sql + wzf_sql
						+ order_sql);
		this.setAttr("wzf_listpage", wzf_listpage);
		// 查询已支付
		String yzf_sql = " and b.policy_status='02' ";
		PageUtil yzf_listpage = DbPage.paginate(
				this.getParaToInt("yzf_curPage", 1), 14, query_sql + yzf_sql
						+ order_sql);
		this.setAttr("yzf_listpage", yzf_listpage);
		// 查询已完成
		String ywc_sql = " and b.policy_status='03' ";
		PageUtil ywc_listpage = DbPage.paginate(
				this.getParaToInt("ywc_curPage", 1), 14, query_sql + ywc_sql
						+ order_sql);
		this.setAttr("ywc_listpage", ywc_listpage); 
		render("/ab/alipay/bxd.html");
	}
}
