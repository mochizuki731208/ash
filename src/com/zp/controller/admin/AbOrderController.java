package com.zp.controller.admin;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.mysql.jdbc.StringUtils;
import com.zp.entity.AbCard;
import com.zp.entity.AbCardExpense;
import com.zp.entity.AbCityarea;
import com.zp.entity.AbFmcar;
import com.zp.entity.AbFmcarOrder;
import com.zp.entity.AbMerchant;
import com.zp.entity.AbMerchantPrint;
import com.zp.entity.AbMerchantProduct;
import com.zp.entity.AbOrder;
import com.zp.entity.AbOrderItem;
import com.zp.entity.AbOrderPosition;
import com.zp.entity.AbOrderSms;
import com.zp.entity.AbSjPosition;
import com.zp.entity.AbSubject;
import com.zp.entity.AbTcConfig;
import com.zp.entity.AbTcExpressOrder;
import com.zp.entity.AbTcExpressOrderDiaoDu;
import com.zp.entity.SysConfig;
import com.zp.entity.SysCzTx;
import com.zp.entity.SysUser;
import com.zp.entity.SysUserGroup;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.interceptor.AccessAdminInterceptor;
import com.zp.tools.CityAttachAdminUtil;
import com.zp.tools.CommonProcess;
import com.zp.tools.CommonSQL;
import com.zp.tools.CommonStaticData;
import com.zp.tools.DateUtil;
import com.zp.tools.ExportExcelUtil;
import com.zp.tools.MapUtils;
import com.zp.tools.PrintUtils;
import com.zp.tools.RandomUtil;
import com.zp.tools.SmsMessage;
import com.zp.tools.StringUtil;
import com.zp.tools.TuiSongUtil;
import com.zp.tools.alipay.AlipaySubmit;

public class AbOrderController extends Controller {
	@Before(AccessAdminInterceptor.class)
	public void add() {
		this.render("/admin/order/add.html");
	}

	/**
	 * 弹出的增加页面进行初始化操作
	 */
	@Before(AccessAdminInterceptor.class)
	public void addUpPage() {
		SysUser u = (SysUser) this.getRequest().getSession().getAttribute("user");
		String user_id = u.getStr(SysUser.ID);
		String sql = "select * from ab_cityarea where id is not null";
		if (!StringUtils.isNullOrEmpty(user_id)	&& "103".equals(u.getStr(SysUser.ROLE_ID))) {
			sql += " and user_id= '" + user_id + "'";
		}
		sql += " and type_id = '1'";// 商业圈
		List<AbCityarea> list_cityarea = AbCityarea.dao.find(sql);
		this.setAttr("list_cityarea", list_cityarea);
		List<AbSubject> list_subject = AbSubject.dao.find("select * from ab_subject where id like '1019%'");
		this.setAttr("list_subject", list_subject);
		String tel = StringUtil.toStr(this.getPara("tel"));
		this.setSessionAttr("tel", tel);
		this.setAttr("tel", tel);
		SysUser userNew = SysUser.dao.findFirst("select * from sys_user where mobile=?", tel); 
		if(null==userNew){
			userNew = new SysUser();
		}
		this.setAttr("userNew", userNew);
		// tree();
		this.render("/admin/order/addUpPageNew.html");
	}
	
	//市内发货
	@Before(AccessAdminInterceptor.class)
	public void initAddTcOrder(){
		this.setSessionAttr("tc_order", null);
		this.setAttr("zb", this.getPara("zb"));
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
		this.render("/admin/order/initAddTcOrder.html");
	}
	
	
	/**
	 * order form 合并 saveStep1 and saveStep2 两个方法
	 */
	public void isUser() {
		SysUser user = this.getSessionAttr("abuser");
		if(user!=null){
			renderJson("0");
		}else{
			renderJson("4");
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
		String total =StringUtil.toStr(this.getPara("sum"));
		
		String from =StringUtil.toStr(this.getPara("from"));
		
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
		if (total == null||total.equals(""))
			total = "0";

		SysUser user = this.getSessionAttr("abuser");
		if(user!=null){
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
				
				//指派司机
				if(getParaToInt("way")==3||getParaToInt("way")==4){
					String carid=this.getPara("carid");
					shunting(orderid, carid);
				}
				
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
					if (zhmoney==null||zhmoney.add(bd).compareTo(finalprice) == -1) {
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
			renderJson("4");
		}
	}

	//调车
	private void shunting(String orderid,String pcarid){
		Map<String,String> fields=new HashMap<String,String>();
		if(pcarid==null||pcarid.equals("")){
			fields.clear();
			fields.put("length", this.getPara("carlength"));
			fields.put("type", getPara("cartype"));			
		}else{
			fields.clear();
			fields.put("id", pcarid);
		}
		List<AbFmcar> cars=AbFmcar.dao.findByFields(fields);
		for(AbFmcar car :cars){
			String carid=car.getStr("id");
			AbTcExpressOrderDiaoDu ateodu = AbTcExpressOrderDiaoDu.dao.findFirst("select * from ab_tc_express_order_diaodu where orderid=? and carid=?", orderid,carid);
			if(null==ateodu){
				ateodu = new AbTcExpressOrderDiaoDu();
				ateodu.set("id", StringUtil.getUuid32());
				ateodu.set("orderid", orderid);
				ateodu.set("carid",carid);
				ateodu.set("cjsj", DateUtil.getCurrentDate());
				ateodu.save();
			}else{
				ateodu.set("orderid", orderid);
				ateodu.set("carid", carid);
				ateodu.set("cjsj", DateUtil.getCurrentDate());
				ateodu.update();
			}
			
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
		String from =parseEncode(this.getPara("from"));
		
		String to = parseEncode(this.getPara("to"));
		
		String nightPrice = StringUtil.toStr(this.getPara("nightPrice"));
		String sendName = parseEncode(this.getPara("sendName"));
		String sendPhone = StringUtil.toStr(this.getPara("sendPhone"));
		String rcvName = parseEncode(this.getPara("rcvName"));
		String rcvPhone = StringUtil.toStr(this.getPara("rcvPhone"));
		String moreMoney = StringUtil.toStr(this.getPara("moreMoney"));
		String orderStyle = StringUtil.toStr(this.getPara("orderStyle"));
		String minPrice = StringUtil.toStr(this.getPara("minPrice"));
		String maxPrice = StringUtil.toStr(this.getPara("maxPrice"));
		String onePrice = StringUtil.toStr(this.getPara("onePrice"));

		String rcvName2 = parseEncode(this.getPara("rcvName2"));
		String rcvPhone2 = parseEncode(this.getPara("rcvPhone2"));
		String to2 = parseEncode(this.getPara("rcvAddr2"));
		String rcvName3 = parseEncode(this.getPara("rcvName3"));
		String rcvPhone3 = parseEncode(this.getPara("rcvPhone3"));
		String to3 = parseEncode(this.getPara("rcvAddr3"));
		String rcvName4 = parseEncode(this.getPara("rcvName4"));
		String rcvPhone4 = parseEncode(this.getPara("rcvPhone4"));
		String to4 = parseEncode(this.getPara("rcvAddr4"));
		String rcvName5 = parseEncode(this.getPara("rcvName5"));
		String rcvPhone5 = StringUtil.toStr(this.getPara("rcvPhone5"));
		String to5 = parseEncode(this.getPara("rcvAddr5"));

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
		if(user!=null){
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
					order.set("total_price", new BigDecimal(Double.parseDouble(total)));
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
		}else{
			
		}
		render("/ab/tc/showtcdd.html");
	}
	
	private SysUser getUserByMobile(String mobile) {
		String sql = "select * from sys_user where mobile=?";
		SysUser u = SysUser.dao.findFirst(sql, mobile);
		if (u != null) {
			return u;
		}
		return null;
	}
	
	public static void tuisong(String orderid) {

		JPushClient jpushClient = new JPushClient("27001ef040594c440f79fcc1","469e1e136c984f33dc2a28e9", 3);
		try {
			Map<String, String> extras = new HashMap<String, String>();
			extras.put("orderId", orderid);
			PushPayload payload = PushPayload.newBuilder().setPlatform(Platform.all()).setAudience(Audience.all())
					.setNotification(Notification.android("附近有新的订单，请点击查看！", "123快运",	extras)).build();
			PushResult result = jpushClient.sendPush(payload);
		} catch (APIConnectionException e) {
			e.printStackTrace();
		} catch (APIRequestException e) {
			e.printStackTrace();
		}
	}
	
	private Map<String, Object> parseCarConfig(List<AbTcConfig> list) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (AbTcConfig c : list) {
			map.put(c.getStr("key"), c.get("value"));
		}
		return map;
	}

	@Before(AccessAdminInterceptor.class)
	public void carLong(){
		String ccArrLong = StringUtil.toStr(this.getPara("ccArr"));
		String cc = StringUtil.toStr(this.getPara("cc"));
		this.setAttr("cc", cc);
		this.setAttr("ccList", Arrays.asList(ccArrLong.split(",")));
		this.render("/admin/order/carLong.html");
	}
	@Before(AccessAdminInterceptor.class)
	public void reCalJuLi(){
		this.render("/admin/order/reCalJuLi.html");
	}
	/**
	 * 同城订单第一页的提交按钮执行的方法
	 */
	public void saveStep1() {
		String tel= StringUtil.toStr(this.getPara("tel"));
		if(tel.length() > 0){
			this.setSessionAttr("tel", tel);//更新下单人的手机号码，也就是登录账号
		}
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
		
		String send_addr_wd  = StringUtil.toStr(this.getPara("from_lat"));
		String send_addr_jd = StringUtil.toStr(this.getPara("from_lng"));
		String rcv_addr1_wd = StringUtil.toStr(this.getPara("to_lat"));
		String rcv_addr1_jd = StringUtil.toStr(this.getPara("to_lng"));
		String rcv_addr2_wd = StringUtil.toStr(this.getPara("rcvAddr2_lat"));
		String rcv_addr2_jd = StringUtil.toStr(this.getPara("rcvAddr2_lng"));
		String rcv_addr3_wd = StringUtil.toStr(this.getPara("rcvAddr3_lat"));
		String rcv_addr3_jd = StringUtil.toStr(this.getPara("rcvAddr3_lng"));
		String rcv_addr4_wd = StringUtil.toStr(this.getPara("rcvAddr4_lat"));
		String rcv_addr4_jd = StringUtil.toStr(this.getPara("rcvAddr4_lng"));
		String rcv_addr5_wd = StringUtil.toStr(this.getPara("rcvAddr5_lat"));
		String rcv_addr5_jd = StringUtil.toStr(this.getPara("rcvAddr5_lng"));
		String cc = StringUtil.toStr(this.getPara("cc"));
		if(StringUtil.isNull(cc)){
			cc = "0";		
		}
		System.out.println("saveStep01");
		try {
			AbTcExpressOrder order = new AbTcExpressOrder();
			order.set("car", carType);
			if(StringUtil.isNull(basePrice)) {
				order.set("start_price", 0);
			} else {
				order.set("start_price", new BigDecimal(Double.parseDouble(basePrice)));
			}
			if(StringUtil.isNull(overPrice)) {
				order.set("over_unit_price",  0);
			} else {
				order.set("over_unit_price",  new BigDecimal(Double.parseDouble(overPrice)));
			}
			if(StringUtil.isNull(overPrice)) {
				order.set("night_price", 0);
			} else {
				order.set("over_unit_price",  new BigDecimal(Double.parseDouble(overPrice)));
			}
//			order.set("start_price",
//					new BigDecimal(Double.parseDouble(basePrice)));
//			order.set("over_unit_price",
//					new BigDecimal(Double.parseDouble(overPrice)));
//			order.set("night_price",
//					new BigDecimal(Double.parseDouble(nightPrice)));
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
			order.set("send_addr_wd", send_addr_wd);
			order.set("send_addr_jd", send_addr_jd);
			order.set("rcv_addr1_wd", rcv_addr1_wd);
			order.set("rcv_addr1_jd", rcv_addr1_jd);
			order.set("rcv_addr2_wd", rcv_addr2_wd);
			order.set("rcv_addr2_jd", rcv_addr2_jd);
			order.set("rcv_addr3_wd", rcv_addr3_wd);
			order.set("rcv_addr3_jd", rcv_addr3_jd);
			order.set("rcv_addr4_wd", rcv_addr4_wd);
			order.set("rcv_addr4_jd", rcv_addr4_jd);
			order.set("rcv_addr5_wd", rcv_addr5_wd);
			order.set("rcv_addr5_jd", rcv_addr5_jd);
			if(StringUtil.isNull(kilo)) {
				order.set("kilo", 0);
			} else {
				order.set("kilo", Integer.parseInt(kilo) );
			}
			if(StringUtil.isNull(overPrice)) {
				order.set("total_price", 0);
			} else {
				order.set("total_price", new BigDecimal(Double.parseDouble(total)) );
			}
			order.set("style", orderStyle);
			order.set("min_price", minPrice);
			order.set("max_price", maxPrice);
			order.set("more_money","".equals(moreMoney) ? 0 : Double.parseDouble(moreMoney));
			order.set("cc", cc);
			this.setSessionAttr("tc_order", order);
			renderJson("{\"success\":\"success\"}");		
		} catch (Exception e) {
			e.printStackTrace();
			renderJson("{\"fail\":\"fail\"}");
		}
	}
	@Before(AccessAdminInterceptor.class)
	public void prepare() {
		this.setAttr("zb", this.getPara("zb"));
		this.setAttr("tc_order", this.getSessionAttr("tc_order"));
		List<AbTcConfig> goodsTypeList = AbTcConfig.dao
				.find("select * from ab_tc_config where type in ('03')");
		// 查找会员卡金额
		//判断当前号码是不是已注册用户
		
		SysUser abuser = SysUser.dao.findFirst("select * from sys_user where loginid=?",this.getSessionAttr("tel"));
		if(null==abuser){
			this.setAttr("cardtotalmoney", 0);
		}else{
			BigDecimal bd = new BigDecimal(0);
			List<Record> ecards = Db.find(CommonSQL.findUserCard(
					abuser.getStr(SysUser.ID), CommonStaticData.CARD_TYPE_EXPENSE));
			if (ecards != null && ecards.size() > 0) {
				for (Record r : ecards) {
					bd = bd.add(r.getBigDecimal(AbCard.RMONEY));
				}
			}
			this.setAttr("cardtotalmoney", bd.doubleValue());
		}
		
		this.setAttr("goodsList", goodsTypeList);
		render("/admin/order/prepare.html");
	}
	
	public void saveStep2() {
		String total = StringUtil.toStr(this.getPara("sum"));
		String payType = StringUtil.toStr(this.getPara("payType"));
		if(StringUtil.isNull(payType)){
			payType = "1";//默认为在线支付
		}
		AbTcExpressOrder order = this.getSessionAttr("tc_order");
		//快递订单模式，2：悬赏，1：招标
		String orderStyle = order.getStr(AbTcExpressOrder.STYLE);
		if (total == null)
			total = "";
		// 验证余额是否够用
		// 查找会员卡金额
		String tel = this.getSessionAttr("tel");
		SysUser user = SysUser.dao.findFirst("select * from sys_user where loginid=?",tel);
		if(null==user){
			payType="3";//下单人不存在换为到付
		}
		// 保存订单数据
		String weight = StringUtil.toStr(this.getPara("weight"));
		String people = StringUtil.toStr(this.getPara("people"));
		if(StringUtil.isNull(people)){
			people = "0";
		}
		String cart = StringUtil.toStr(this.getPara("cart"));

		String goodsType = StringUtil.toStr(this.getPara("goodsType"));
		String goodsMount = StringUtil.toStr(this.getPara("goods_mount"));
		if(StringUtil.isNull(goodsMount))
			goodsMount = "0";
		String goodsVolumn = StringUtil.toStr(this.getPara("goods_volumn"));
		if(StringUtil.isNull(goodsVolumn))
			goodsVolumn = "0";
		String lift = StringUtil.toStr(this.getPara("lift"));
		String sendTime = StringUtil.toStr(this.getPara("sendTime"));

		String huidan_price = StringUtil.toStr(this.getPara("huidan_price"));
		if(StringUtil.isNull(huidan_price))
			huidan_price = "0";
		String service_price = StringUtil.toStr(this.getPara("service_price"));
		if(StringUtil.isNull(service_price))
			service_price = "0";
		String night_price = StringUtil.toStr(this.getPara("night_price"));
		if(StringUtil.isNull(night_price))
			night_price = "0";
		String dateStr = DateUtil.getCurrentDate();
		try {
			
			order.set("total_price",new BigDecimal(Double.parseDouble(total)));
			order.set("send_time", sendTime);
			order.set("lift", lift);
			order.set("goods_type", goodsType);
			order.set("goods_mount", Integer.parseInt(goodsMount));
			order.set("goods_volumn", Integer.parseInt(goodsVolumn));
			order.set("huidan_price",new BigDecimal(Double.parseDouble(huidan_price)));
			order.set("service_price",new BigDecimal(Double.parseDouble(service_price)));
			order.set("pay_type", payType);
			if(StringUtil.isNull(weight)){
				weight = "0";
			}
			order.set("weight", Integer.parseInt(weight));
			if(StringUtil.isNull(people)){
				people = "0";
			}
			order.set("people", Integer.parseInt(people));
			order.set("cart", cart);
			if(StringUtil.isNull(night_price)){
				night_price = "0";
			}
			order.set("night_price", new BigDecimal(Double.parseDouble(night_price)));
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

			if(null!=user){
				o.set("xdrid", user.get("id"));
				o.set("xdrmc", user.get("mc"));
				o.set("xdrdh", user.get("mobile"));
				o.set("xdsj", dateStr);
				o.set("city_id", user.getStr("city_id"));
				o.set("city_name", user.getStr("city_name"));
				o.set("area_id", user.getStr("area_id"));
				o.set("area_name", user.getStr("area_name"));
			}else{
				o.set("xdrid", "");
				o.set("xdrmc", "");
				o.set("xdrdh", tel);
				o.set("xdsj", dateStr);
				o.set("city_id","");
				o.set("city_name", "");
				//如果下单人的没有注册，则将区域管理员定为该订单的商圈
				SysUser useradmin = (SysUser)this.getSessionAttr("user");
				o.set("area_id", useradmin.getStr("area_id"));
				o.set("area_name", useradmin.getStr("area_name"));
			}

			//同步到数据库
			order.save();
			o.save();
			
			if(null==user){
				renderJson("3");//该用户未注册
				return;
			}
			// 会员卡总金额
			BigDecimal bd = new BigDecimal(0);
			List<Record> ecards = Db.find(CommonSQL.findUserCard(
					user.getStr(SysUser.ID), CommonStaticData.CARD_TYPE_EXPENSE));
			if (ecards != null && ecards.size() > 0) {
				for (Record r : ecards) {
					bd = bd.add(r.getBigDecimal(AbCard.RMONEY));
				}
			}
			// 订单实际支付金额
			BigDecimal finalprice = new BigDecimal(total);
			// 个人账户余额
			BigDecimal zhmoney = user.getBigDecimal(SysUser.ZHYE);
			// 支付方式是在线支付，验证余额
			if ("2".equals(orderStyle)&&"1".equals(payType) && zhmoney.add(bd).compareTo(finalprice) == -1) {
				renderJson("0");
			} else {
				renderJson("1");
			}
			
			if ("2".equals(orderStyle)&&"1".equals(payType)) {
				o.set(AbOrder.ZFZT, "1");
				o.set(AbOrder.ZFSJ, dateStr);
				// 判断是否有消费会员卡抵扣金额
				finalprice = CommonProcess.cardDecution(ecards, finalprice,orderid);
				// 增加消费积分
				user.set(SysUser.JIFEN, user.getInt(SysUser.JIFEN)+ new BigDecimal(total).intValue());
				// 会员卡抵扣不了的，扣账户余额
				if (finalprice.doubleValue() > 0) {
					user.set("zhye",user.getBigDecimal("zhye").subtract(finalprice));
				}
				user.update();
			}
			// 这个是测试司机
			//jpushOrder(new String[]{"15999970376"}, o, order);
			String yzm = RandomUtil.createRandomPwd(6);// 获取刘伟短信验证
			String content = "尊敬的用户,您好。您的订单已发货，您可凭借验证码【" + yzm
					+ "】进行收货确认，请务必要收到货物当面才告知送货人员，谢谢。";
			try {
				if(SmsMessage.isSendSms("c5")){//下单成功：c5.订单发货：c6.司机接收订单：c7.送达提醒评价：c8.用户注册：c9
					SmsMessage.SendMessage(o.getStr("lxrdh"), content);//给下单人发送短信
					if(!StringUtils.isNullOrEmpty(order.getStr("rcv_phone1"))){
						SmsMessage.SendMessage(order.getStr("rcv_phone1"), content);
						insertOrderSms(o.getStr("id"),order.getStr("rcv_phone1"),yzm);
					}
					if(!StringUtils.isNullOrEmpty(order.getStr("rcv_phone2"))){
						SmsMessage.SendMessage(order.getStr("rcv_phone2"), content);
						insertOrderSms(o.getStr("id"),order.getStr("rcv_phone2"),yzm);
					}
					if(!StringUtils.isNullOrEmpty(order.getStr("rcv_phone3"))){
						SmsMessage.SendMessage(order.getStr("rcv_phone3"), content);
						insertOrderSms(o.getStr("id"),order.getStr("rcv_phone3"),yzm);
					}
					if(!StringUtils.isNullOrEmpty(order.getStr("rcv_phone4"))){
						SmsMessage.SendMessage(order.getStr("rcv_phone4"), content);
						insertOrderSms(o.getStr("id"),order.getStr("rcv_phone4"),yzm);
					}
					if(!StringUtils.isNullOrEmpty(order.getStr("rcv_phone5"))){
						SmsMessage.SendMessage(order.getStr("rcv_phone5"), content);
						insertOrderSms(o.getStr("id"),order.getStr("rcv_phone5"),yzm);
					}
				}
			} catch (Exception e) {
			}// 给收货人发短信
			TuiSongUtil.Tuisong(o.getStr("id"), TuiSongUtil.getSomeSjId(o.getStr("id")));//推送处理
		} catch (Exception e) {
			e.printStackTrace();
			renderJson("2");
		}
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
	
	public void pay() {
		this.setAttr("tc_order", this.getSessionAttr("tc_order"));
		this.render("/admin/order/pay.html");
	}
	
	/**
	 * 初始商家列表
	 */
	@Before(AccessAdminInterceptor.class)
	public void initMerchant() {
		String area_id = StringUtil.toStr(this.getPara("area_id"));
		String sql = "select * from ab_merchant where id is not null";
		if(area_id.length() > 0){
			sql += " and area_id = '" + area_id + "'";
		}
		List<AbMerchant> list_merchant = AbMerchant.dao.find(sql);
		this.renderJson("list_merchant", list_merchant);
	}

	/**
	 * 查询商品列表
	 */
	@Before(AccessAdminInterceptor.class)
	public void initProduct() {
		String subject_id = StringUtil.toStr(this.getPara("subject_id"));
		String id = StringUtil.toStr(this.getPara("id"));
		String area_id = StringUtil.toStr(this.getPara("area_id"));
		String sname = StringUtil.toStr(this.getPara("sname"));
		String sprice = StringUtil.toStr(this.getPara("sprice"));
		String sql = "select * from ab_merchant_product where id is not null";
		if (!StringUtils.isNullOrEmpty(subject_id)) {
			sql += " and subject_id='" + subject_id + "'";
		}
		if (!StringUtils.isNullOrEmpty(id)) {
			sql += " and id='" + id + "'";
		}
		if (!StringUtils.isNullOrEmpty(area_id)) {
			sql += " and area_id='" + area_id + "'";
		}
		if (!StringUtils.isNullOrEmpty(sprice)) {
			sql += " and price='" + sprice + "'";
		}
		if (!StringUtils.isNullOrEmpty(sname)) {
			sql += " and mc like '%" + sname + "%'";
		}
		List<AbMerchantProduct> list_product = AbMerchantProduct.dao.find(sql);
		this.renderJson("list_product", list_product);
	}

	/**
	 * 查询商品信息
	 */
	@Before(AccessAdminInterceptor.class)
	public void getOneProduct() {
		String id = StringUtil.toStr(this.getPara("id"));
		String sql = "select * from ab_merchant_product where id='" + id + "'";
		List<AbMerchantProduct> list_product = AbMerchantProduct.dao.find(sql);
		this.renderJson(list_product.get(0));
	}

	/**
	 * 初始化未指派页面
	 */
	@Before(AccessAdminInterceptor.class)
	public void weiZhiPai() {
		String orderType = StringUtil.toStr(this.getPara("orderType"));//订单种类，1为物流订单，其他为店铺订单
		if(orderType.length() == 0){//即默认为店铺订单
			orderType = "1";
		}
		this.setAttr("orderType", orderType);
		getAllAdmin();
		SysUser u = (SysUser) this.getRequest().getSession().getAttribute("user");
		String admin_id  =StringUtil.toStr(this.getPara("admin_id"));
		admin_id = admin_id.length() == 0 ? u.getStr("id") : admin_id;
		this.setAttr("admin_id", admin_id);
		String sn = StringUtil.toStr(this.getPara("sn"));
		String xdrmc = StringUtil.toStr(this.getPara("xdrmc"));
		String yt = StringUtil.toStr(this.getPara("yt"));
		String ddzt = StringUtil.toStr(this.getPara("ddzt"));
		String is_type = StringUtil.toStr(this.getPara("is_type"));
		String startxdsj = StringUtil.toStr(this.getPara("startxdsj"));
		String endxdsj = StringUtil.toStr(this.getPara("endxdsj"));
		String xdrdh = StringUtil.toStr(this.getPara("xdrdh"));
		String lxr = StringUtil.toStr(this.getPara("lxr"));
		String lxrdh = StringUtil.toStr(this.getPara("lxrdh"));
		String shdz = StringUtil.toStr(this.getPara("shdz"));
		String startdz = StringUtil.toStr(this.getPara("startdz"));
		String tab = StringUtil.toStr(this.getPara("tab"));
		String istrue = StringUtil.toStr(this.getPara("istrue"));
		if(istrue.length() == 0){
			istrue = "1";
		}
		String isFiveMinutes = StringUtil.toStr(this.getPara("isFiveMinutes"));
		this.setAttr("isFiveMinutes", isFiveMinutes);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if("0".equals(orderType)){//店铺订单
			String sql = "select a.*,b.xxdz,c.zhye from ab_order a left join ab_merchant b on a.mid= b.id left join  sys_user c on c.id= a.xdrid where a.id is not null";
			sql += " and upper(left(a.sn,2)) != 'TC'";
			if(u.getStr("role_id").equals("104")){
				sql += " and b.subject_id in(" + CityAttachAdminUtil.getAuthSubjectIdsByUserId(u.getStr("id")) + ")";
			}
			if (!StringUtils.isNullOrEmpty(sn)) {
				sql += " and a.sn like '%" + sn + "%'";
			}
			if (!StringUtils.isNullOrEmpty(xdrmc)) {
				sql += " and a.xdrmc like '%" + xdrmc + "%'";
			}
			if (!StringUtils.isNullOrEmpty(yt)) {
				sql += " and a.yt= '" + yt + "'";
			}
			if (!StringUtils.isNullOrEmpty(is_type)) {
				sql += " and a.is_type= '" + is_type + "'";
			}
			if (!StringUtils.isNullOrEmpty(ddzt)) {
				sql += " and a.ddzt= '" + ddzt + "'";
			} else {
				sql += " and a.ddzt = '2'";// 过滤未指派的订单
			}
			if (!StringUtils.isNullOrEmpty(startxdsj)) {
				sql += " and a.xdsj >= '" + startxdsj + "'";
			}
			if (!StringUtils.isNullOrEmpty(endxdsj)) {
				sql += " and a.xdsj <= '" + endxdsj + "'";
			}
			if(!StringUtils.isNullOrEmpty(isFiveMinutes)){
				Calendar cnew = Calendar.getInstance();
				cnew.setTimeInMillis(cnew.getTimeInMillis() - 5 * 60 * 1000);
				sql += " and a.xdsj <= '" + sdf.format(cnew.getTime()) + "'";
			}
			if (!StringUtils.isNullOrEmpty(xdrdh)) {
				sql += " and a.xdrdh like '%" + xdrdh + "%'";
			}
			if (!StringUtils.isNullOrEmpty(lxr)) {
				sql += " and a.lxr like '%" + lxr + "%'";
			}
			if (!StringUtils.isNullOrEmpty(lxrdh)) {
				sql += " and a.lxrdh like '%" + lxrdh + "%'";
			}
			if (!StringUtils.isNullOrEmpty(shdz)) {
				sql += " and a.shdz like '%" + shdz + "%'";
			}

			if (!StringUtils.isNullOrEmpty(startdz)) {
				sql += " and b.xxdz like '%" + startdz + "%'";
			}
			if (StringUtils.isNullOrEmpty(tab)) {
				tab = "t0";
			}
//			if(admin_id.length() > 0 && !admin_id.equals("admin")){
//				sql += " and c.p_id='" +admin_id + "'";
//			}else if(!admin_id.equals("admin") && !u.getStr("id").equals("admin")){
//				sql += " and c.p_id='" +u.getStr("id") + "'";
//			}
			// 对时间进行处理
			String starttime = "";
			String endtime = "";
			// 当天
			if ("t0".equals(tab)) {
				Calendar cd = Calendar.getInstance();
				cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
				starttime = sdf.format(cd.getTime());
				sql += " and a.xdsj >= '" + starttime + "'";
				cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
				endtime = sdf.format(cd.getTime());
				sql += " and a.xdsj <= '" + endtime + "'";
			} else if ("t1".equals(tab)) {
				Calendar cd = Calendar.getInstance();
				cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH) - 1, 0, 0, 0);
				starttime = sdf.format(cd.getTime());
				sql += " and a.xdsj >= '" + starttime + "'";
				cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
				endtime = sdf.format(cd.getTime());
				sql += " and a.xdsj <= '" + endtime + "'";
			} else if ("t2".equals(tab)) {
				Calendar cd = Calendar.getInstance();
				cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH) - 2, 0, 0, 0);
				starttime = sdf.format(cd.getTime());
				sql += " and a.xdsj >= '" + starttime + "'";
				cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
				endtime = sdf.format(cd.getTime());
				sql += " and a.xdsj <= '" + endtime + "'";
			} else if ("t3".equals(tab)) {
				Calendar cd = Calendar.getInstance();
				cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH) - 6, 0, 0, 0);
				starttime = sdf.format(cd.getTime());
				sql += " and a.xdsj >= '" + starttime + "'";
				cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH) + 6, 23, 59, 59);
				endtime = sdf.format(cd.getTime());
				sql += " and a.xdsj <= '" + endtime + "'";
			} else if ("t4".equals(tab)) {
				Calendar cd = Calendar.getInstance();
				cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH), 1, 0, 0, 0);
				starttime = sdf.format(cd.getTime());
				sql += " and a.xdsj >= '" + starttime + "'";
			} else if ("t5".equals(tab)) {
				Calendar cd = Calendar.getInstance();
				cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH) - 1, 1, 0, 0,0);
				starttime = sdf.format(cd.getTime());
				sql += " and a.xdsj >= '" + starttime + "'";
				cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH) + 1, 0, 23,59, 59);
				endtime = sdf.format(cd.getTime());
				sql += " and a.xdsj <= '" + endtime + "'";
			} else {
				// 所有订单
			}

			String userSubType = StringUtil.toStr(this.getPara("userSubType"));
			if(userSubType.length() > 0){
				sql += " and a.xdrid in (select id from sys_user where zhlx like '" + userSubType + "%')";
			}
			this.setAttr("userSubType", userSubType);
			// 商圈信息的查询
			String user_id = u.getStr(SysUser.ID);
			String sql_area = "select * from ab_cityarea where id is not null";
			if (!StringUtils.isNullOrEmpty(user_id)	&& "103".equals(u.getStr(SysUser.ROLE_ID))) {
				sql_area += " and user_id= '" + user_id + "'";
			}
			sql_area += " and type_id = '1'";// 商业圈
			List<AbCityarea> list_cityarea = AbCityarea.dao.find(sql_area);
			this.setAttr("list_cityarea", list_cityarea);// 商业圈列表
			if(admin_id.length() > 0 && (!u.getStr("role_id").equals("104") || orderType.equals("1"))){
				sql += " and (c.city_id in (" + CityAttachAdminUtil.getCityByAdmin(admin_id) + ")";
				sql += " or c.area_id in (" + CityAttachAdminUtil.getCityByAdmin(admin_id) + "))";
			}
			sql += " order by a.xdsj desc";
			AbOrder order = new AbOrder();
			order.set("sn", sn);
			order.set("xdrmc", xdrmc);
			order.set("yt", yt);
			order.set("is_type", is_type);
			order.set("ddzt", ddzt);
			order.set("xdrdh", xdrdh);
			order.set("lxr", lxr);
			order.set("lxrdh", lxrdh);
			order.set("shdz", shdz);
			order.set("istrue", istrue);
			this.setAttr("startxdsj", startxdsj);
			this.setAttr("endxdsj", endxdsj);
			this.setAttr("startdz", startdz);
			PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1), 10,sql);
			this.setAttr("order", order);
			this.setAttr("tab", tab);
			this.setAttr("listpage", listpage);

			Calendar cd = Calendar.getInstance();
			String endtime1 = sdf.format(cd.getTime());
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH), cd.get(Calendar.HOUR_OF_DAY),
					cd.get(Calendar.MINUTE) - 1, cd.get(Calendar.MILLISECOND));
			String starttime1 = sdf.format(cd.getTime());
			String sql_1 = "select * from ab_order where xdsj >='" + starttime1 + "' and xdsj <='" + endtime1 + "'";
			List<AbOrder> list = AbOrder.dao.find(sql_1);
			if (null != list && list.size() > 0) {
				this.setAttr("new", "11");
			} else {
				this.setAttr("new", "");
			}
			if ("101".equals(u.getStr("role_id"))) {
				this.setAttr("cancancel", "1");
			} else {
				this.setAttr("cancancel", "0");
			}
			//导出
			String flagExport = StringUtil.toStr(this.getPara("flagExport"));
			if(flagExport.length() > 0){
				String name = "订单明细.xls";
				String path = this.getClass().getResource("/").toString().replace("WEB-INF/classes/", "").replace("file:/", "");
				boolean flag = ExportExcelUtil.doExportToExcel(path + "admin/order/" + name, sql, this.getParaToInt("curPage", 1), starttime, endtime,"ab_order");
				if(flag){
					this.renderFile("/admin/order/"  + name);
					return;
				}else{
					//不执行任何操作
				}
			}
			this.setAttr("totalcount", Db.queryLong("select count(1) as totalcount from (" +sql + ") a"));
			this.setAttr("totalmoney", Db.queryBigDecimal("select sum(ddzje) as totalmoney from (" +sql + ") a"));
			this.render("/admin/order/orderWeiZhiPai_dp.html");
		}else{//物流订单
			StringBuffer sb = new StringBuffer();
			sb.append(" select * from (");
			sb.append("select ao.*, ss.zhye zhye,aa.id appid, aoc.id cbid, aoc.cb_status,tc.style style,tc.send_time,tc.send_addr as send_addr,tc.min_price min_price,tc.max_price max_price,tc.pay_type,tc.goods_volumn");
			sb.append(",tc.rcv_addr1,tc.rcv_addr2,tc.rcv_addr3,tc.rcv_addr4,tc.rcv_addr5,tc.rcv_phone1,tc.rcv_phone2,tc.rcv_phone3,tc.rcv_phone4,tc.rcv_phone5,tc.rcv_name1,tc.rcv_name2,tc.rcv_name3,tc.rcv_name4,tc.rcv_name5 ");
			sb.append(" from  (select * from ab_order where 1=1 and upper(left(sn,2))='TC') ao ");
			sb.append(" left join ab_appraise aa on ao.id = aa.order_id ");
			sb.append(" left join sys_user ss on ao.xdrid=ss.id");
			sb.append(" left join ab_tc_express_order tc on tc.sn = ao.sn ");
			sb.append(" left join ab_order_chargeback aoc on aoc.order_id = ao.id ) t where 1 = 1 ");
			if (ddzt.length() > 0) {
				sb.append(" and t.ddzt='" + ddzt + "'");
			}else{
				sb.append(" and t.ddzt = '1'");// 过滤未指派的订单
			}
			
			if (!StringUtils.isNullOrEmpty(sn)) {
				sb.append(" and t.sn like '%" + sn + "%'");
			}
			if (!StringUtils.isNullOrEmpty(xdrmc)) {
				sb.append( " and t.xdrmc like '%" + xdrmc + "%'");
			}
			if (!StringUtils.isNullOrEmpty(startxdsj)) {
				sb.append( " and t.xdsj >= '" + startxdsj + "'");
			}
			if (!StringUtils.isNullOrEmpty(endxdsj)) {
				sb.append( " and t.xdsj <= '" + endxdsj + "'");
			}
			if(!StringUtils.isNullOrEmpty(isFiveMinutes)){
				Calendar cnew = Calendar.getInstance();
				cnew.setTimeInMillis(cnew.getTimeInMillis() - 5 * 60 * 1000);
				sb.append( " and t.xdsj <= '" + sdf.format(cnew.getTime()) + "'");
			}
			if (!StringUtils.isNullOrEmpty(xdrdh)) {
				sb.append( " and t.xdrdh like '%" + xdrdh + "%'");
			}
			if (!StringUtils.isNullOrEmpty(lxr)) {
				sb.append(  " and t.lxr like '%" + lxr + "%'");
			}
			if (!StringUtils.isNullOrEmpty(lxrdh)) {
				sb.append(  " and t.lxrdh like '%" + lxrdh + "%'");
			}
			if (!StringUtils.isNullOrEmpty(shdz)) {
				sb.append( " and t.shdz like '%" + shdz + "%'");
			}
			if (!StringUtils.isNullOrEmpty(istrue)) {
				sb.append( " and t.istrue = '" + istrue + "'");
			}

			if(admin_id.length() > 0 && (!u.getStr("role_id").equals("104") || orderType.equals("1"))){
				sb.append(" and (city_id in (" + CityAttachAdminUtil.getCityByAdmin(admin_id) + ")");
				sb.append(" or area_id in (" + CityAttachAdminUtil.getCityByAdmin(admin_id) + "))");
			}
			if (StringUtils.isNullOrEmpty(tab)) {
				tab = "t0";
			}
			// 对时间进行处理
			String starttime  = "";
			String endtime = "";
			// 当天
			if ("t0".equals(tab)) {
				Calendar cd = Calendar.getInstance();
				cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
				starttime = sdf.format(cd.getTime());
				sb.append(" and t.xdsj >= '" + starttime + "'");
				cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
				endtime = sdf.format(cd.getTime());
				sb.append(" and t.xdsj <= '" + endtime + "'");
			} else if ("t1".equals(tab)) {
				Calendar cd = Calendar.getInstance();
				cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH) - 1, 0, 0, 0);
				starttime = sdf.format(cd.getTime());
				sb.append( " and t.xdsj >= '" + starttime + "'");
				cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
				endtime = sdf.format(cd.getTime());
				sb.append(" and t.xdsj <= '" + endtime + "'");
			} else if ("t2".equals(tab)) {
				Calendar cd = Calendar.getInstance();
				cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH) - 2, 0, 0, 0);
				starttime = sdf.format(cd.getTime());
				sb.append(" and t.xdsj >= '" + starttime + "'");
				cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
				endtime = sdf.format(cd.getTime());
				sb.append(" and t.xdsj <= '" + endtime + "'");
			} else if ("t3".equals(tab)) {
				Calendar cd = Calendar.getInstance();
				cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH) - 6, 0, 0, 0);
				starttime = sdf.format(cd.getTime());
				sb.append(" and t.xdsj >= '" + starttime + "'");
				cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH) + 6, 23, 59, 59);
				endtime = sdf.format(cd.getTime());
				sb.append(" and t.xdsj <= '" + endtime + "'");
			} else if ("t4".equals(tab)) {
				Calendar cd = Calendar.getInstance();
				cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH), 1, 0, 0, 0);
				starttime = sdf.format(cd.getTime());
				sb.append(" and t.xdsj >= '" + starttime + "'");
			} else if ("t5".equals(tab)) {
				Calendar cd = Calendar.getInstance();
				cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH) - 1, 1, 0, 0,0);
				starttime = sdf.format(cd.getTime());
				sb.append(" and t.xdsj >= '" + starttime + "'");
				cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH) + 1, 0, 23,59, 59);
				endtime = sdf.format(cd.getTime());
				sb.append(" and t.xdsj <= '" + endtime + "'");
			} else {
				// 所有订单
			}
			String userSubType = StringUtil.toStr(this.getPara("userSubType"));
			if(userSubType.length() > 0){
				sb.append(" and t.xdrid in (select id from sys_user where zhlx like '" + userSubType + "%')");
			}
			this.setAttr("userSubType", userSubType);
			sb.append(" order by t.xdsj desc");
			AbOrder order = new AbOrder();
			order.set("sn", sn);
			order.set("xdrmc", xdrmc);
			order.set("yt", yt);
			order.set("is_type", is_type);
			order.set("ddzt", ddzt);
			order.set("xdrdh", xdrdh);
			order.set("lxr", lxr);
			order.set("lxrdh", lxrdh);
			order.set("shdz", shdz);
			order.set("istrue", istrue);
			this.setAttr("startxdsj", startxdsj);
			this.setAttr("endxdsj", endxdsj);
			this.setAttr("startdz", startdz);
			PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1), 10,sb.toString());
			// 判断是否可以做退单操作
			for (Record record : listpage.getList()) {
				String dateStr = record.getStr(AbOrder.XDSJ);
				// 时间在1-24小时内，不能重复维权，如果是同城需要有接单人
				if (DateUtil.isBackTime(dateStr)&& StringUtil.isNull(record.getStr("cbid"))&& CommonProcess.existQdr(record)) {
					record.set("isback", true);
				} else {
					record.set("isback", false);
				}
			}
			this.setAttr("order", order);
			this.setAttr("tab", tab);
			this.setAttr("listpage", listpage);

			if ("101".equals(u.getStr("role_id"))) {
				this.setAttr("cancancel", "1");
			} else {
				this.setAttr("cancancel", "0");
			}
			//导出
			String flagExport = StringUtil.toStr(this.getPara("flagExport"));
			if(flagExport.length() > 0){
				String name = "订单明细.xls";
				String path = this.getClass().getResource("/").toString().replace("WEB-INF/classes/", "").replace("file:/", "");
				boolean flag = ExportExcelUtil.doExportToExcel(path + "admin/order/" + name, sb.toString(), this.getParaToInt("curPage", 1), starttime, endtime,"ab_order");
				if(flag){
					this.renderFile("/admin/order/"  + name);
					return;
				}else{
					//不执行任何操作
				}
			}
			this.setAttr("totalcount", Db.queryLong("select count(1) as totalcount from (" +sb.toString() + ") a"));
			this.setAttr("totalmoney", Db.queryBigDecimal("select sum(ddzje) as totalmoney from (" +sb.toString() + ") a"));
			this.render("/admin/order/orderWeiZhiPai_tc.html");
		}
	}

	@Before(AccessAdminInterceptor.class)
	public void showdd(){
		SysUser u = (SysUser)this.getSessionAttr("user");
		String id = this.getPara("id");
		AbOrder vo = new AbOrder();
		if(StringUtil.toStr(id).length()>0){
			vo = AbOrder.dao.findById(id);
		}
		this.setAttr("vo", vo);
		List<SysUserGroup> suglist = new ArrayList<SysUserGroup>();
		AbCityarea aca = AbCityarea.dao.findFirst("select * from ab_cityarea where user_id=? and ccm='1'",  u.getStr("id"));
		if(null!=aca){
			if(!"admin".equals(u.getStr("id"))){
				suglist = SysUserGroup.dao.find("select * from sys_user_group where cityid=?",aca.getStr("id"));
				this.setAttr("tb_city_id", aca.getStr("id"));
			}else{
				suglist = SysUserGroup.dao.find("select * from sys_user_group");
			}
		}else{
			if("admin".equals(u.getStr("id"))){
				suglist = SysUserGroup.dao.find("select * from sys_user_group");
			}
		}
		this.setAttr("suglist", suglist);
		String sn = vo.getStr("sn");
		if(sn != null && sn.startsWith("TC")){
			AbTcExpressOrder tcOrder = AbTcExpressOrder.dao.findFirst("select * from ab_tc_express_order where sn=?", sn);
			this.setAttr("to", tcOrder);
			// 接单人的信息
			String qdrid = vo.getStr("qdrid");
			if(!StringUtil.isNull(qdrid)){
				SysUser user = SysUser.dao.findById(qdrid);
				this.setAttr("jdr", user);
			}
		}
		this.setAttr("user", u);
		render("/admin/order/showtcdd.html");
	}
	
	/**
	 *订单注销，商家或者用户自己可以调用
	 */
	public void saveorderzf(){
		boolean flag = false;
		try {
			String id = StringUtil.toStr(this.getPara("id"));//订单ID
			SysUser abuser = this.getSessionAttr("user");
			
			AbOrder.dao.findById(id)
			.set("ddzt", "8")
			.set("ddcxrid", abuser.get("id"))
			.set("ddcxrmc", abuser.get("mc"))
			.set("ddcxsj", DateUtil.getCurrentDate())
			.update();
			
			flag = true;
			
			//将会员卡金额退换
			List<AbCardExpense> aceList = AbCardExpense.dao.find("select * from ab_card_expense where order_id=?", id);
			for(AbCardExpense ace : aceList){
				ace.set("status", "0");//抵扣失败，资金退回
				ace.update();
				AbCard acd = AbCard.dao.findById(ace.getStr("card_id"));
				acd.set("rmoney", acd.getBigDecimal("rmoney").add(ace.getBigDecimal("money")));
				acd.set("status", "1");
				acd.update();//会员卡资金恢复
				List<SysCzTx> actList = SysCzTx.dao.find("select * from sys_cz_tx where orderid=?", ace.getStr("order_id"));
				for(SysCzTx sct:actList){
					sct.set("result", "2");
					sct.update();
					SysUser user = SysUser.dao.findById(sct.getStr("userid"));
					user.set("zhye", user.getBigDecimal("zhye").add(new BigDecimal(sct.getStr("totalfee"))));
					user.update();
				}
			}
			//将可用余额退换
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		flag = true;
		renderJson(flag);
	}
	/**
	 * 初始化正在执行页面
	 */
	@Before(AccessAdminInterceptor.class)
	public void doing() {
		SysUser u = (SysUser) this.getRequest().getSession().getAttribute("user");
		String user_id = u.getStr(SysUser.ID);
		String orderType = StringUtil.toStr(this.getPara("orderType"));
		if(orderType.length() == 0){
			orderType = "1";//默认显示店铺的。1为同城物流订单
		}
		this.setAttr("orderType", orderType);
		getAllAdmin();
		String admin_id  =StringUtil.toStr(this.getPara("admin_id"));
		admin_id = admin_id.length() == 0 ? user_id : admin_id;
		this.setAttr("admin_id", admin_id);
		String sn = StringUtil.toStr(this.getPara("sn"));
		String xdrmc = StringUtil.toStr(this.getPara("xdrmc"));
		String yt = StringUtil.toStr(this.getPara("yt"));
		String ddzt = StringUtil.toStr(this.getPara("ddzt"));
		String is_type = StringUtil.toStr(this.getPara("is_type"));
		String startxdsj = StringUtil.toStr(this.getPara("startxdsj"));
		String endxdsj = StringUtil.toStr(this.getPara("endxdsj"));
		String xdrdh = StringUtil.toStr(this.getPara("xdrdh"));
		String lxr = StringUtil.toStr(this.getPara("lxr"));
		String lxrdh = StringUtil.toStr(this.getPara("lxrdh"));
		String shdz = StringUtil.toStr(this.getPara("shdz"));
		String startdz = StringUtil.toStr(this.getPara("startdz"));
		String tab = StringUtil.toStr(this.getPara("tab"));
		String istrue = StringUtil.toStr(this.getPara("istrue"));
		if(istrue.length()==0){
			istrue="1";
		}
		String sql = "";
		if(orderType.equals("0")){
			sql = "select a.*,b.xxdz,c.zhye from ab_order a left join ab_merchant b on a.mid= b.id left join  sys_user c on c.id= a.xdrid where a.id is not null";
			sql += " and upper(left(a.sn,2)) != 'TC'";
			if(u.getStr("role_id").equals("104")){
				sql += " and b.subject_id in(" + CityAttachAdminUtil.getAuthSubjectIdsByUserId(u.getStr("id")) + ")";
			}
		}else{
			sql = "select a.*,c.zhye,d.send_addr,d.send_time,d.rcv_phone1,d.rcv_phone2,d.rcv_phone3,d.rcv_phone4,d.rcv_phone5,d.rcv_name1,d.rcv_name2,d.rcv_name3,d.rcv_name4,d.rcv_name5,d.rcv_addr1,d.rcv_addr2,d.rcv_addr3,d.rcv_addr4,d.rcv_addr5,d.goods_volumn from ab_order a left join  sys_user c on c.id= a.xdrid LEFT JOIN ab_tc_express_order d ON a.sn= d.sn where a.id is not null";
			sql += " and upper(left(a.sn,2)) = 'TC'";
			sql += " and a.istrue='" + istrue + "'";
		}
		if (!StringUtils.isNullOrEmpty(sn)) {
			sql += " and a.sn like '%" + sn + "%'";
		}
		if (!StringUtils.isNullOrEmpty(xdrmc)) {
			sql += " and a.xdrmc like '%" + xdrmc + "%'";
		}
		if (!StringUtils.isNullOrEmpty(yt)) {
			sql += " and a.yt= '" + yt + "'";
		}
		if (!StringUtils.isNullOrEmpty(is_type)) {
			sql += " and a.is_type= '" + is_type + "'";
		}
		if (!StringUtils.isNullOrEmpty(ddzt)) {
			sql += " and a.ddzt= '" + ddzt + "'";
		}else{
			sql += " and a.ddzt = '3'";// 过滤未指派的订单
		}
		if (!StringUtils.isNullOrEmpty(startxdsj)) {
			sql += " and a.xdsj >= '" + startxdsj + "'";
		}
		if (!StringUtils.isNullOrEmpty(endxdsj)) {
			sql += " and a.xdsj <= '" + endxdsj + "'";
		}
		if (!StringUtils.isNullOrEmpty(xdrdh)) {
			sql += " and a.xdrdh like '%" + xdrdh + "%'";
		}
		if (!StringUtils.isNullOrEmpty(lxr)) {
			sql += " and a.lxr like '%" + lxr + "%'";
		}
		if (!StringUtils.isNullOrEmpty(lxrdh)) {
			sql += " and a.lxrdh like '%" + lxrdh + "%'";
		}
		if (!StringUtils.isNullOrEmpty(shdz)) {
			sql += " and a.shdz like '%" + shdz + "%'";
		}
		
		if(istrue.length() > 0 && !orderType.equals("0")){
			sql += " and a.istrue='" + istrue + "'";
		}

		if(admin_id.length() > 0 && (!u.getStr("role_id").equals("104") || orderType.equals("1"))){
			sql += " and (c.city_id in (" + CityAttachAdminUtil.getCityByAdmin(admin_id) + ")";
			sql += " or c.area_id in (" + CityAttachAdminUtil.getCityByAdmin(admin_id) + "))";
		}
		if (!StringUtils.isNullOrEmpty(startdz)) {
			if(orderType.equals("0")){
				sql += " and b.xxdz like '%" + startdz + "%'";
			}else{
				sql += " and d.send_addr like '%" + startdz + "%'";
			}
		}
		if (StringUtils.isNullOrEmpty(tab)) {
			tab = "t0";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 对时间进行处理
		String starttime = "";
		String endtime = "";
		// 当天
		if ("t0".equals(tab)) {
			Calendar cd = Calendar.getInstance();
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
			starttime = sdf.format(cd.getTime());
			sql += " and a.xdsj >= '" + starttime + "'";
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
			endtime = sdf.format(cd.getTime());
			sql += " and a.xdsj <= '" + endtime + "'";
		} else if ("t1".equals(tab)) {
			Calendar cd = Calendar.getInstance();
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH) - 1, 0, 0, 0);
			starttime = sdf.format(cd.getTime());
			sql += " and a.xdsj >= '" + starttime + "'";
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
			endtime = sdf.format(cd.getTime());
			sql += " and a.xdsj <= '" + endtime + "'";
		} else if ("t2".equals(tab)) {
			Calendar cd = Calendar.getInstance();
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH) - 2, 0, 0, 0);
			starttime = sdf.format(cd.getTime());
			sql += " and a.xdsj >= '" + starttime + "'";
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
			endtime = sdf.format(cd.getTime());
			sql += " and a.xdsj <= '" + endtime + "'";
		} else if ("t3".equals(tab)) {
			Calendar cd = Calendar.getInstance();
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH) - 6, 0, 0, 0);
			starttime = sdf.format(cd.getTime());
			sql += " and a.xdsj >= '" + starttime + "'";
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH) + 6, 23, 59, 59);
			endtime = sdf.format(cd.getTime());
			sql += " and a.xdsj <= '" + endtime + "'";
		} else if ("t4".equals(tab)) {
			Calendar cd = Calendar.getInstance();
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH), 1, 0, 0, 0);
			starttime = sdf.format(cd.getTime());
			sql += " and a.xdsj >= '" + starttime + "'";
		} else if ("t5".equals(tab)) {
			Calendar cd = Calendar.getInstance();
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH) - 1, 1, 0, 0,0);
			starttime = sdf.format(cd.getTime());
			sql += " and a.xdsj >= '" + starttime + "'";
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH) + 1, 0, 23,59, 59);
			endtime = sdf.format(cd.getTime());
			sql += " and a.xdsj <= '" + endtime + "'";
		} else {
			// 所有订单
		}
		String userSubType = StringUtil.toStr(this.getPara("userSubType"));
		if(userSubType.length() > 0){
			sql += " and a.xdrid in (select id from sys_user where zhlx like '" + userSubType + "%')";
		}
		this.setAttr("userSubType", userSubType);
		// 商圈信息的查询
//		if("103".equals(u.getStr("role_id")) && orderType.equals("0")){
//			sql += " and a.area_id in (select id from ab_cityarea where user_id= '" + user_id + "'  and type_id = '1')";
//		}
		sql += " order by a.xdsj desc";
		AbOrder order = new AbOrder();
		order.set("sn", sn);
		order.set("xdrmc", xdrmc);
		order.set("yt", yt);
		order.set("is_type", is_type);
		order.set("ddzt", ddzt);
		order.set("xdrdh", xdrdh);
		order.set("lxr", lxr);
		order.set("lxrdh", lxrdh);
		order.set("shdz", shdz);
		order.set("istrue", istrue);
		this.setAttr("startxdsj", startxdsj);
		this.setAttr("endxdsj", endxdsj);
		this.setAttr("startdz", startdz);
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1), 10, sql);
		this.setAttr("order", order);
		this.setAttr("tab", tab);
		this.setAttr("listpage", listpage);
		this.setAttr("totalcount", Db.queryLong("select count(1) as totalcount from (" +sql + ") a"));
		this.setAttr("totalmoney", Db.queryBigDecimal("select sum(ddzje) as totalmoney from (" +sql + ") a"));
		//导出
		String flagExport = StringUtil.toStr(this.getPara("flagExport"));
		if(flagExport.length() > 0){
			String name = "订单明细.xls";
			String path = this.getClass().getResource("/").toString().replace("WEB-INF/classes/", "").replace("file:/", "");
			boolean flag = ExportExcelUtil.doExportToExcel(path + "admin/order/" + name, sql, this.getParaToInt("curPage", 1), starttime, endtime,"ab_order");
			if(flag){
				this.renderFile("/admin/order/"  + name);
				return;
			}else{
				//不执行任何操作
			}
		}
		if(orderType.equals("0")){
			this.render("/admin/order/doing_DP.html");
		}else{
			this.render("/admin/order/doing_TC.html");
		}
	}

	/**
	 * 初始化完成页面
	 */
	@Before(AccessAdminInterceptor.class)
	public void wanCheng() {
		SysUser u = (SysUser) this.getRequest().getSession().getAttribute("user");
		String orderType = StringUtil.toStr(this.getPara("orderType"));
		if(orderType.length() == 0){
			orderType = "1";
		}
		this.setAttr("orderType", orderType);
		getAllAdmin();
		String admin_id  =StringUtil.toStr(this.getPara("admin_id"));
		admin_id = admin_id.length() == 0 ? u.getStr("id") : admin_id;
		this.setAttr("admin_id", admin_id);
		String sn = StringUtil.toStr(this.getPara("sn"));
		String xdrmc = StringUtil.toStr(this.getPara("xdrmc"));
		String yt = StringUtil.toStr(this.getPara("yt"));
		String ddzt = StringUtil.toStr(this.getPara("ddzt"));
		String is_type = StringUtil.toStr(this.getPara("is_type"));
		String startxdsj = StringUtil.toStr(this.getPara("startxdsj"));
		String endxdsj = StringUtil.toStr(this.getPara("endxdsj"));
		String xdrdh = StringUtil.toStr(this.getPara("xdrdh"));
		String lxr = StringUtil.toStr(this.getPara("lxr"));
		String lxrdh = StringUtil.toStr(this.getPara("lxrdh"));
		String shdz = StringUtil.toStr(this.getPara("shdz"));
		String startdz = StringUtil.toStr(this.getPara("startdz"));
		String istrue = StringUtil.toStr(this.getPara("istrue"));
		if(istrue.length()==0){
			istrue="1";
		}
		String tab = StringUtil.toStr(this.getPara("tab"));

		String sql = "";
		if(orderType.equals("0")){
			sql = "select a.*,b.xxdz,c.zhye from ab_order a left join ab_merchant b on a.mid= b.id left join  sys_user c on c.id= a.xdrid where a.id is not null";
			sql += " and upper(left(a.sn,2)) != 'TC'";
			if(u.getStr("role_id").equals("104")){
				sql += " and b.subject_id in(" + CityAttachAdminUtil.getAuthSubjectIdsByUserId(u.getStr("id")) + ")";
			}
		}else{
			sql = "select a.*,c.zhye,d.send_addr,d.send_time,d.rcv_phone1,d.goods_volumn,d.rcv_phone2,d.rcv_phone3,d.rcv_phone4,d.rcv_phone5,d.rcv_name1,d.rcv_name2,d.rcv_name3,d.rcv_name4,d.rcv_name5,d.rcv_addr1,d.rcv_addr2,d.rcv_addr3,d.rcv_addr4,d.rcv_addr5  from ab_order a left join  sys_user c on c.id= a.xdrid LEFT JOIN ab_tc_express_order d ON a.sn= d.sn where a.id is not null";
			sql += " and upper(left(a.sn,2)) = 'TC'";
			sql += " and a.istrue = '" + istrue + "'";
		}
		if (!StringUtils.isNullOrEmpty(sn)) {
			sql += " and a.sn like '%" + sn + "%'";
		}
		if (!StringUtils.isNullOrEmpty(xdrmc)) {
			sql += " and a.xdrmc like '%" + xdrmc + "%'";
		}
		if (!StringUtils.isNullOrEmpty(yt)) {
			sql += " and a.yt= '" + yt + "'";
		}
		if (!StringUtils.isNullOrEmpty(is_type)) {
			sql += " and a.is_type= '" + is_type + "'";
		}
		if (!StringUtils.isNullOrEmpty(ddzt)) {
			sql += " and a.ddzt= '" + ddzt + "'";
		} else {
			sql += " and a.ddzt in ('5','4')";// 过滤未指派的订单
		}
		if (!StringUtils.isNullOrEmpty(startxdsj)) {
			sql += " and a.xdsj >= '" + startxdsj + "'";
		}
		if (!StringUtils.isNullOrEmpty(endxdsj)) {
			sql += " and a.xdsj <= '" + endxdsj + "'";
		}
		if (!StringUtils.isNullOrEmpty(xdrdh)) {
			sql += " and a.xdrdh like '%" + xdrdh + "%'";
		}
		if (!StringUtils.isNullOrEmpty(lxr)) {
			sql += " and a.lxr like '%" + lxr + "%'";
		}
		if (!StringUtils.isNullOrEmpty(lxrdh)) {
			sql += " and a.lxrdh like '%" + lxrdh + "%'";
		}
		if (!StringUtils.isNullOrEmpty(shdz)) {
			sql += " and a.shdz like '%" + shdz + "%'";
		}

		if (!StringUtils.isNullOrEmpty(startdz)) {
			sql += " and b.xxdz like '%" + startdz + "%'";
		}
		if (StringUtils.isNullOrEmpty(tab)) {
			tab = "t0";
		}
//		if(admin_id.length() > 0 && !admin_id.equals("admin")){
//			sql += " and c.p_id='" + admin_id + "'";
//		}else if(!admin_id.equals("admin") && !user_id.equals("admin")){
//			sql += " and c.p_id='" + user_id + "'";
//		}
		if(admin_id.length() > 0 && (!u.getStr("role_id").equals("104") || orderType.equals("1"))){
			sql += " and (c.city_id in (" + CityAttachAdminUtil.getCityByAdmin(admin_id) + ")";
			sql += " or c.area_id in (" + CityAttachAdminUtil.getCityByAdmin(admin_id) + "))";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 对时间进行处理
		String starttime = "";
		String endtime = "";
		// 当天
		if ("t0".equals(tab)) {
			Calendar cd = Calendar.getInstance();
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
			starttime = sdf.format(cd.getTime());
			sql += " and a.xdsj >= '" + starttime + "'";
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
			endtime = sdf.format(cd.getTime());
			sql += " and a.xdsj <= '" + endtime + "'";
		} else if ("t1".equals(tab)) {
			Calendar cd = Calendar.getInstance();
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH) - 1, 0, 0, 0);
			starttime = sdf.format(cd.getTime());
			sql += " and a.xdsj >= '" + starttime + "'";
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
			endtime = sdf.format(cd.getTime());
			sql += " and a.xdsj <= '" + endtime + "'";
		} else if ("t2".equals(tab)) {
			Calendar cd = Calendar.getInstance();
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH) - 2, 0, 0, 0);
			starttime = sdf.format(cd.getTime());
			sql += " and a.xdsj >= '" + starttime + "'";
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
			endtime = sdf.format(cd.getTime());
			sql += " and a.xdsj <= '" + endtime + "'";
		} else if ("t3".equals(tab)) {
			Calendar cd = Calendar.getInstance();
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH) - 6, 0, 0, 0);
			starttime = sdf.format(cd.getTime());
			sql += " and a.xdsj >= '" + starttime + "'";
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH) + 6, 23, 59, 59);
			endtime = sdf.format(cd.getTime());
			sql += " and a.xdsj <= '" + endtime + "'";
		} else if ("t4".equals(tab)) {
			Calendar cd = Calendar.getInstance();
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH), 1, 0, 0, 0);
			starttime = sdf.format(cd.getTime());
			sql += " and a.xdsj >= '" + starttime + "'";
		} else if ("t5".equals(tab)) {
			Calendar cd = Calendar.getInstance();
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH) - 1, 1, 0, 0,0);
			starttime = sdf.format(cd.getTime());
			sql += " and a.xdsj >= '" + starttime + "'";
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH) + 1, 0, 23,59, 59);
			endtime = sdf.format(cd.getTime());
			sql += " and a.xdsj <= '" + endtime + "'";
		} else {
			// 所有订单
		}
		String userSubType = StringUtil.toStr(this.getPara("userSubType"));
		if(userSubType.length() > 0){
			sql += " and a.xdrid in (select id from sys_user where zhlx like '" + userSubType + "%')";
		}
		this.setAttr("userSubType", userSubType);
		// 商圈信息的查询
//		if("103".equals(u.getStr("role_id")) && orderType.equals("0")){
//			sql += " and a.area_id in (select id from ab_cityarea where user_id= '" + user_id + "'  and type_id = '1')";
//		}
		sql += " order by a.xdsj desc";
		AbOrder order = new AbOrder();
		order.set("sn", sn);
		order.set("xdrmc", xdrmc);
		order.set("yt", yt);
		order.set("is_type", is_type);
		order.set("ddzt", ddzt);
		order.set("xdrdh", xdrdh);
		order.set("lxr", lxr);
		order.set("lxrdh", lxrdh);
		order.set("shdz", shdz);
		order.set("istrue", istrue);
		this.setAttr("startxdsj", startxdsj);
		this.setAttr("endxdsj", endxdsj);
		this.setAttr("startdz", startdz);
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1), 10,sql);
		this.setAttr("order", order);
		this.setAttr("tab", tab);
		this.setAttr("listpage", listpage);
		this.setAttr("totalcount", Db.queryLong("select count(1) as totalcount from (" +sql + ") a"));
		this.setAttr("totalmoney", Db.queryBigDecimal("select sum(ddzje) as totalmoney from (" +sql + ") a"));
		//导出
		String flagExport = StringUtil.toStr(this.getPara("flagExport"));
		if(flagExport.length() > 0){
			String name = "订单明细.xls";
			String path = this.getClass().getResource("/").toString().replace("WEB-INF/classes/", "").replace("file:/", "");
			boolean flag = ExportExcelUtil.doExportToExcel(path + "admin/order/" + name, sql, this.getParaToInt("curPage", 1), starttime, endtime,"ab_order");
			if(flag){
				this.renderFile("/admin/order/"  + name);
				return;
			}else{
				//不执行任何操作
			}
		}
		if(orderType.equals("0")){
			this.render("/admin/order/wanCheng_DP.html");
		}else{
			this.render("/admin/order/wanCheng_TC.html");
		}
	}

	/**
	 * 初始化取消订单页面
	 */
	@Before(AccessAdminInterceptor.class)
	public void quXiao() {
		SysUser u = (SysUser) this.getRequest().getSession().getAttribute("user");
		String orderType = StringUtil.toStr(this.getPara("orderType"));
		if(orderType.length() == 0){
			orderType = "1";
		}
		this.setAttr("orderType", orderType);
		getAllAdmin();
		String admin_id  =StringUtil.toStr(this.getPara("admin_id"));
		admin_id = admin_id.length() == 0 ? u.getStr("id") : admin_id;
		this.setAttr("admin_id", admin_id);
		String sn = StringUtil.toStr(this.getPara("sn"));
		String xdrmc = StringUtil.toStr(this.getPara("xdrmc"));
		String yt = StringUtil.toStr(this.getPara("yt"));
		String ddzt = StringUtil.toStr(this.getPara("ddzt"));
		String is_type = StringUtil.toStr(this.getPara("is_type"));
		String startxdsj = StringUtil.toStr(this.getPara("startxdsj"));
		String endxdsj = StringUtil.toStr(this.getPara("endxdsj"));
		String xdrdh = StringUtil.toStr(this.getPara("xdrdh"));
		String lxr = StringUtil.toStr(this.getPara("lxr"));
		String lxrdh = StringUtil.toStr(this.getPara("lxrdh"));
		String shdz = StringUtil.toStr(this.getPara("shdz"));
		String startdz = StringUtil.toStr(this.getPara("startdz"));
		String istrue = StringUtil.toStr(this.getPara("istrue"));
		if(istrue.length()==0){
			istrue="1";
		}
		String tab = StringUtil.toStr(this.getPara("tab"));

		String sql = "";
		if(orderType.equals("0")){
			sql = "select a.*,b.xxdz,c.zhye from ab_order a left join ab_merchant b on a.mid= b.id left join  sys_user c on c.id= a.xdrid where a.id is not null";
			sql += " and upper(left(a.sn,2)) != 'TC'";
		}else{
			sql = "select a.*,c.zhye,d.send_addr,d.send_time,d.rcv_phone1,d.goods_volumn,d.rcv_phone2,d.rcv_phone3,d.rcv_phone4,d.rcv_phone5,d.rcv_name1,d.rcv_name2,d.rcv_name3,d.rcv_name4,d.rcv_name5,d.rcv_addr1,d.rcv_addr2,d.rcv_addr3,d.rcv_addr4,d.rcv_addr5  from ab_order a left join  sys_user c on c.id= a.xdrid LEFT JOIN ab_tc_express_order d ON a.sn= d.sn where a.id is not null";
			sql += " and upper(left(a.sn,2)) = 'TC'";
			sql += " and a.istrue='" + istrue + "'";
		}
		
		if (!StringUtils.isNullOrEmpty(sn)) {
			sql += " and a.sn like '%" + sn + "%'";
		}
		if (!StringUtils.isNullOrEmpty(xdrmc)) {
			sql += " and a.xdrmc like '%" + xdrmc + "%'";
		}
		if (!StringUtils.isNullOrEmpty(yt)) {
			sql += " and a.yt= '" + yt + "'";
		}
		if (!StringUtils.isNullOrEmpty(is_type)) {
			sql += " and a.is_type= '" + is_type + "'";
		}
		if (!StringUtils.isNullOrEmpty(ddzt)) {
			sql += " and a.ddzt= '" + ddzt + "'";
		} else {
			sql += " and a.ddzt = '8'";// 过滤未指派的订单
		}
		if (!StringUtils.isNullOrEmpty(startxdsj)) {
			sql += " and a.xdsj >= '" + startxdsj + "'";
		}
		if (!StringUtils.isNullOrEmpty(endxdsj)) {
			sql += " and a.xdsj <= '" + endxdsj + "'";
		}
		if (!StringUtils.isNullOrEmpty(xdrdh)) {
			sql += " and a.xdrdh like '%" + xdrdh + "%'";
		}
		if (!StringUtils.isNullOrEmpty(lxr)) {
			sql += " and a.lxr like '%" + lxr + "%'";
		}
		if (!StringUtils.isNullOrEmpty(lxrdh)) {
			sql += " and a.lxrdh like '%" + lxrdh + "%'";
		}
		if (!StringUtils.isNullOrEmpty(shdz)) {
			sql += " and a.shdz like '%" + shdz + "%'";
		}

		if (!StringUtils.isNullOrEmpty(startdz)) {
			sql += " and b.xxdz like '%" + startdz + "%'";
		}
		if (StringUtils.isNullOrEmpty(tab)) {
			tab = "t0";
		}
//		if(admin_id.length() > 0 && !admin_id.equals("admin")){
//			sql += " and c.p_id='" + admin_id + "'";
//		}else if(!admin_id.equals("admin") && !user_id.equals("admin")){
//			sql += " and c.p_id='" + user_id + "'";
//		}
		if(admin_id.length() > 0){
			sql += " and (c.city_id in (" + CityAttachAdminUtil.getCityByAdmin(admin_id) + ")";
			sql += " or c.area_id in (" + CityAttachAdminUtil.getCityByAdmin(admin_id) + "))";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 对时间进行处理
		String starttime = "";
		String endtime = "";
		// 当天
		if ("t0".equals(tab)) {
			Calendar cd = Calendar.getInstance();
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
			starttime = sdf.format(cd.getTime());
			sql += " and a.xdsj >= '" + starttime + "'";
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
			endtime = sdf.format(cd.getTime());
			sql += " and a.xdsj <= '" + endtime + "'";
		} else if ("t1".equals(tab)) {
			Calendar cd = Calendar.getInstance();
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH) - 1, 0, 0, 0);
			starttime = sdf.format(cd.getTime());
			sql += " and a.xdsj >= '" + starttime + "'";
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
			endtime = sdf.format(cd.getTime());
			sql += " and a.xdsj <= '" + endtime + "'";
		} else if ("t2".equals(tab)) {
			Calendar cd = Calendar.getInstance();
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH) - 2, 0, 0, 0);
			starttime = sdf.format(cd.getTime());
			sql += " and a.xdsj >= '" + starttime + "'";
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
			endtime = sdf.format(cd.getTime());
			sql += " and a.xdsj <= '" + endtime + "'";
		} else if ("t3".equals(tab)) {
			Calendar cd = Calendar.getInstance();
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH) - 6, 0, 0, 0);
			starttime = sdf.format(cd.getTime());
			sql += " and a.xdsj >= '" + starttime + "'";
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH) + 6, 23, 59, 59);
			endtime = sdf.format(cd.getTime());
			sql += " and a.xdsj <= '" + endtime + "'";
		} else if ("t4".equals(tab)) {
			Calendar cd = Calendar.getInstance();
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH), 1, 0, 0, 0);
			starttime = sdf.format(cd.getTime());
			sql += " and a.xdsj >= '" + starttime + "'";
		} else if ("t5".equals(tab)) {
			Calendar cd = Calendar.getInstance();
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH) - 1, 1, 0, 0,0);
			starttime = sdf.format(cd.getTime());
			sql += " and a.xdsj >= '" + starttime + "'";
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH) + 1, 0, 23,59, 59);
			endtime = sdf.format(cd.getTime());
			sql += " and a.xdsj <= '" + endtime + "'";
		} else {
			// 所有订单
		}
		// 商圈信息的查询
//		if("103".equals(u.getStr("role_id"))&& orderType.equals("0")){
//			sql += " and a.area_id in (select id from ab_cityarea where user_id= '" + user_id + "'  and type_id = '1')";
//		}
		sql += " order by a.xdsj desc";
		AbOrder order = new AbOrder();
		order.set("sn", sn);
		order.set("xdrmc", xdrmc);
		order.set("yt", yt);
		order.set("is_type", is_type);
		order.set("ddzt", ddzt);
		order.set("xdrdh", xdrdh);
		order.set("lxr", lxr);
		order.set("lxrdh", lxrdh);
		order.set("shdz", shdz);
		order.set("istrue", istrue);
		this.setAttr("startxdsj", startxdsj);
		this.setAttr("endxdsj", endxdsj);
		this.setAttr("startdz", startdz);
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1), 10,sql);
		this.setAttr("order", order);
		this.setAttr("tab", tab);
		this.setAttr("listpage", listpage);
		this.setAttr("totalcount", Db.queryLong("select count(1) as totalcount from (" +sql + ") a"));
		this.setAttr("totalmoney", Db.queryBigDecimal("select sum(ddzje) as totalmoney from (" +sql + ") a"));
		//导出
		String flagExport = StringUtil.toStr(this.getPara("flagExport"));
		if(flagExport.length() > 0){
			String name = "订单明细.xls";
			String path = this.getClass().getResource("/").toString().replace("WEB-INF/classes/", "").replace("file:/", "");
			boolean flag = ExportExcelUtil.doExportToExcel(path + "admin/order/" + name, sql, this.getParaToInt("curPage", 1), starttime, endtime,"ab_order");
			if(flag){
				this.renderFile("/admin/order/"  + name);
				return;
			}else{
				//不执行任何操作
			}
		}
		
		if(orderType.equals("0")){
			this.render("/admin/order/quXiao_DP.html");
		}else{
			this.render("/admin/order/quXiao_TC.html");
		}
		
	}

	/**
	 * 初始化订单汇总页面
	 */
	@Before(AccessAdminInterceptor.class)
	public void all() {
		String orderType = StringUtil.toStr(this.getPara("orderType"));
		if(orderType.length() == 0){
			orderType = "1";
		}
		this.setAttr("orderType", orderType);
		getAllAdmin();
		SysUser u = (SysUser) this.getRequest().getSession().getAttribute("user");
		String admin_id  =StringUtil.toStr(this.getPara("admin_id"));
		admin_id = admin_id.length() == 0 ? u.getStr("id") : admin_id;
		this.setAttr("admin_id", admin_id);
		String sn = StringUtil.toStr(this.getPara("sn"));
		String xdrmc = StringUtil.toStr(this.getPara("xdrmc"));
		String yt = StringUtil.toStr(this.getPara("yt"));
		String ddzt = StringUtil.toStr(this.getPara("ddzt"));
		String is_type = StringUtil.toStr(this.getPara("is_type"));
		String startxdsj = StringUtil.toStr(this.getPara("startxdsj"));
		String endxdsj = StringUtil.toStr(this.getPara("endxdsj"));
		String xdrdh = StringUtil.toStr(this.getPara("xdrdh"));
		String lxr = StringUtil.toStr(this.getPara("lxr"));
		String lxrdh = StringUtil.toStr(this.getPara("lxrdh"));
		String shdz = StringUtil.toStr(this.getPara("shdz"));
		String startdz = StringUtil.toStr(this.getPara("startdz"));
		String istrue = StringUtil.toStr(this.getPara("istrue"));
		if(istrue.length()==0){
			istrue="1";
		}
		String tab = StringUtil.toStr(this.getPara("tab"));
		String sql = "";
		if(orderType.equals("0")){
			sql = "select a.*,b.xxdz,c.zhye from ab_order a left join ab_merchant b on a.mid= b.id left join  sys_user c on c.id= a.xdrid where a.id is not null";
			sql += " and upper(left(a.sn,2)) != 'TC'";
			if(u.getStr("role_id").equals("104")){
				sql += " and b.subject_id in(" + CityAttachAdminUtil.getAuthSubjectIdsByUserId(u.getStr("id")) + ")";
			}
		}else{
			sql = "select a.*,c.zhye,d.send_addr,d.send_time,d.goods_volumn,d.rcv_phone1,d.rcv_phone2,d.rcv_phone3,d.rcv_phone4,d.rcv_phone5,d.rcv_name1,d.rcv_name2,d.rcv_name3,d.rcv_name4,d.rcv_name5,d.rcv_addr1,d.rcv_addr2,d.rcv_addr3,d.rcv_addr4,d.rcv_addr5  from ab_order a left join  sys_user c on c.id= a.xdrid LEFT JOIN ab_tc_express_order d ON a.sn= d.sn where a.id is not null";
			sql += " and upper(left(a.sn,2)) = 'TC'";
			sql += " and a.istrue='" + istrue + "'";
		}
		
		if (!StringUtils.isNullOrEmpty(sn)) {
			sql += " and a.sn like '%" + sn + "%'";
		}
		if (!StringUtils.isNullOrEmpty(xdrmc)) {
			sql += " and a.xdrmc like '%" + xdrmc + "%'";
		}
		if (!StringUtils.isNullOrEmpty(yt)) {
			sql += " and a.yt= '" + yt + "'";
		}
		if (!StringUtils.isNullOrEmpty(is_type)) {
			sql += " and a.is_type= '" + is_type + "'";
		}
		if (!StringUtils.isNullOrEmpty(ddzt)) {
			sql += " and a.ddzt= '" + ddzt + "'";
		}
		if (!StringUtils.isNullOrEmpty(startxdsj)) {
			sql += " and a.xdsj >= '" + startxdsj + "'";
		}
		if (!StringUtils.isNullOrEmpty(endxdsj)) {
			sql += " and a.xdsj <= '" + endxdsj + "'";
		}
		if (!StringUtils.isNullOrEmpty(xdrdh)) {
			sql += " and a.xdrdh like '%" + xdrdh + "%'";
		}
		if (!StringUtils.isNullOrEmpty(lxr)) {
			sql += " and a.lxr like '%" + lxr + "%'";
		}
		if (!StringUtils.isNullOrEmpty(lxrdh)) {
			sql += " and a.lxrdh like '%" + lxrdh + "%'";
		}
		if (!StringUtils.isNullOrEmpty(shdz)) {
			sql += " and a.shdz like '%" + shdz + "%'";
		}

		if (!StringUtils.isNullOrEmpty(startdz)) {
			sql += " and b.xxdz like '%" + startdz + "%'";
		}
		if (StringUtils.isNullOrEmpty(tab)) {
			tab = "t0";
		}
//		if(admin_id.length() > 0 && !admin_id.equals("admin")){
//			sql += " and c.p_id='" + admin_id + "'";
//		}else if(!admin_id.equals("admin") && !user_id.equals("admin")){
//			sql += " and c.p_id='" + user_id + "'";
//		}
		if(admin_id.length() > 0 && (!u.getStr("role_id").equals("104") || orderType.equals("1"))){
			sql += " and (c.city_id in (" + CityAttachAdminUtil.getCityByAdmin(admin_id) + ")";
			sql += " or c.area_id in (" + CityAttachAdminUtil.getCityByAdmin(admin_id) + "))";
		}
			
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 对时间进行处理
		String starttime = "";
		String endtime = "";
		// 当天
		if ("t0".equals(tab)) {
			Calendar cd = Calendar.getInstance();
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
			starttime = sdf.format(cd.getTime());
			sql += " and a.xdsj >= '" + starttime + "'";
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
			endtime = sdf.format(cd.getTime());
			sql += " and a.xdsj <= '" + endtime + "'";
		} else if ("t1".equals(tab)) {
			Calendar cd = Calendar.getInstance();
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH) - 1, 0, 0, 0);
			starttime = sdf.format(cd.getTime());
			sql += " and a.xdsj >= '" + starttime + "'";
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
			endtime = sdf.format(cd.getTime());
			sql += " and a.xdsj <= '" + endtime + "'";
		} else if ("t2".equals(tab)) {
			Calendar cd = Calendar.getInstance();
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH) - 2, 0, 0, 0);
			starttime = sdf.format(cd.getTime());
			sql += " and a.xdsj >= '" + starttime + "'";
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
			endtime = sdf.format(cd.getTime());
			sql += " and a.xdsj <= '" + endtime + "'";
		} else if ("t3".equals(tab)) {
			Calendar cd = Calendar.getInstance();
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH) - 6, 0, 0, 0);
			starttime = sdf.format(cd.getTime());
			sql += " and a.xdsj >= '" + starttime + "'";
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH) + 6, 23, 59, 59);
			endtime = sdf.format(cd.getTime());
			sql += " and a.xdsj <= '" + endtime + "'";
		} else if ("t4".equals(tab)) {
			Calendar cd = Calendar.getInstance();
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH), 1, 0, 0, 0);
			starttime = sdf.format(cd.getTime());
			sql += " and a.xdsj >= '" + starttime + "'";
		} else if ("t5".equals(tab)) {
			Calendar cd = Calendar.getInstance();
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH) - 1, 1, 0, 0,0);
			starttime = sdf.format(cd.getTime());
			sql += " and a.xdsj >= '" + starttime + "'";
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH) + 1, 0, 23,59, 59);
			endtime = sdf.format(cd.getTime());
			sql += " and a.xdsj <= '" + endtime + "'";
		} else if("t7".equals(tab)){
			// 获取参数
			String query_year = StringUtil.toStr(this.getPara("query_year"));
			String query_month = StringUtil.toStr(this.getPara("query_month"));
			String query_week = StringUtil.toStr(this.getPara("query_week"));
			this.setAttr("query_year", query_year);
			this.setAttr("query_month", query_month);
			this.setAttr("query_week", query_week);
			// yyyy-MM-dd HH:mm:ss
			if(query_week.equals("00")) {
				// 按月查询
				starttime = query_year + "-" + query_month + "-01 00:00:00";
				Calendar c = Calendar.getInstance();
				try {
					c.setTime(DateUtil.parse(starttime, "yyyy-MM-dd HH:mm:ss"));
					c.add(Calendar.MONTH, 1);
					endtime = DateUtil.format(c.getTime(), "yyyy-MM-dd HH:mm:ss");
				} catch (ParseException e) {
					e.printStackTrace();
				}
				sql += " and a.xdsj >= '" + starttime + "'";
				sql += " and a.xdsj < '" + endtime + "'";
			} else if(query_week.equals("01")) {
				// 第一周,1-7号
				starttime = query_year + "-" + query_month + "-01 00:00:00";
				endtime = query_year + "-" + query_month + "-08 00:00:00";
				sql += " and a.xdsj >= '" + starttime + "'";
				sql += " and a.xdsj < '" + endtime + "'";
			} else if(query_week.equals("02")) {
				// 第二周,8-14
				starttime = query_year + "-" + query_month + "-08 00:00:00";
				endtime = query_year + "-" + query_month + "-15 00:00:00";
				sql += " and a.xdsj >= '" + starttime + "'";
				sql += " and a.xdsj < '" + endtime + "'";
			} else if(query_week.equals("03")) {
				// 第三周,15-21
				starttime = query_year + "-" + query_month + "-15 00:00:00";
				endtime = query_year + "-" + query_month + "-22 00:00:00";
				sql += " and a.xdsj >= '" + starttime + "'";
				sql += " and a.xdsj < '" + endtime + "'";
			} else if(query_week.equals("04")) {
				// 第四周
				starttime = query_year + "-" + query_month + "-22 00:00:00";
				Calendar c = Calendar.getInstance();
				try {
					c.setTime(DateUtil.parse(query_year + "-" + query_month + "-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
					c.add(Calendar.MONTH, 1);
					endtime = DateUtil.format(c.getTime(), "yyyy-MM-dd HH:mm:ss");
				} catch (ParseException e) {
					e.printStackTrace();
				}
				sql += " and a.xdsj >= '" + starttime + "'";
				sql += " and a.xdsj < '" + endtime + "'";
			}
		} else {
			// 所有订单
		}
		// 商圈信息的查询
		String userSubType = StringUtil.toStr(this.getPara("userSubType"));
		if(userSubType.length() > 0){
			sql += " and a.xdrid in (select id from sys_user where zhlx like '" + userSubType + "%')";
		}
		this.setAttr("userSubType", userSubType);
//		if("103".equals(u.getStr("role_id")) && orderType.equals("0")){
//			sql += " and a.area_id in (select id from ab_cityarea where user_id= '" + user_id + "'  and type_id = '1')";
//		}
		sql += " order by a.xdsj desc";
		AbOrder order = new AbOrder();
		order.set("sn", sn);
		order.set("xdrmc", xdrmc);
		order.set("yt", yt);
		order.set("is_type", is_type);
		order.set("ddzt", ddzt);
		order.set("xdrdh", xdrdh);
		order.set("lxr", lxr);
		order.set("lxrdh", lxrdh);
		order.set("shdz", shdz);
		order.set("istrue", istrue);
		this.setAttr("startxdsj", startxdsj);
		this.setAttr("endxdsj", endxdsj);
		this.setAttr("startdz", startdz);
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1), 10,sql);
		this.setAttr("order", order);
		this.setAttr("tab", tab);
		this.setAttr("listpage", listpage);
		this.setAttr("totalcount", Db.queryLong("select count(1) as totalcount from (" +sql + ") a"));
		this.setAttr("totalmoney", Db.queryBigDecimal("select sum(ddzje) as totalmoney from (" +sql + ") a"));
		
		//导出
		String flagExport = StringUtil.toStr(this.getPara("flagExport"));
		if(flagExport.length() > 0){
			String name = "订单明细.xls";
			String path = this.getClass().getResource("/").toString().replace("WEB-INF/classes/", "").replace("file:/", "");
			boolean flag = ExportExcelUtil.doExportToExcel(path + "admin/order/" + name, sql, this.getParaToInt("curPage", 1), starttime, endtime,"ab_order");
			if(flag){
				this.renderFile("/admin/order/"  + name);
				return;
			}else{
				//不执行任何操作
			}
		}
		
		if(orderType.equals("0")){
			this.render("/admin/order/all_DP.html");
		}else{
			this.render("/admin/order/all_TC.html");
		}
	}

	/**
	 * 单开控制中心百度页面
	 */
	@Before(AccessAdminInterceptor.class)
	public void controllerBaiDu() {
//		String id = StringUtil.toStr(this.getPara("id"));
//		if (StringUtils.isNullOrEmpty(id)) {
//			this.render("/admin/order/controllerBaiDu.html");
//			return;
//		}
//		String[] lngMinMax = StringUtil.toStr(this.getPara("lngMinMax")).split(",");
//		String[] latMinMax = StringUtil.toStr(this.getPara("latMinMax")).split(",");
//		String sql = "select * from(select * from (select a.id as id,b.zt as zt,a.zhye as zhye,a.mobile as mobile,a.loginid as loginid,a.mc as mc,b.jd as lng,b.wd as lat,a.gzzp as gzzp,b.dqsj as modify_date from sys_user a,ab_sj_position b where a.id=b.sjid";
//		sql += " and role_id='106'";
//		if(lngMinMax.length > 1){
//			sql += " and b.jd <=" + lngMinMax[1] + " and b.jd >=" + lngMinMax[0];
//		}
//		if(latMinMax.length > 1){
//			sql += " and b.wd <=" + latMinMax[1] + " and b.wd >=" + latMinMax[0];
//		}
//		String msg = StringUtil.toStr(this.getPara("msg"));
//		if (!StringUtils.isNullOrEmpty(msg)) {
//			sql += " and (a.loginid like '%" + msg + "%'";
//			sql += " or a.mc like '%" + msg + "%'";
//			sql += " or a.mobile like '%" + msg + "%')";
//		}
//		sql += " order by modify_date desc) c group by c.id) d";
//		// 状态的查询
//		String selectStr = StringUtil.toStr(this.getPara("selectStr"));
//		if (!StringUtils.isNullOrEmpty(selectStr)) {
//			sql += " where d.zt in (" + selectStr + ")";
//		}
//		// 查询业务员
//		List<SysUser> pdys = SysUser.dao.find(sql);
//		this.renderJson(pdys);
		this.setAttr("isadmin", "1");
		this.render("/ab/fmcar/mapTrucksAdmin.html");
	}

	/**
	 * 保存订单
	 */
	@Before(AccessAdminInterceptor.class)
	public void save() {
		int zjf = 0;
		AbOrder order = new AbOrder();
		String id = StringUtil.getUuid32();
		order.set("id", id);// 主键
		String sn = RandomUtil.createRandomNumPwd(9);
		while(Db.queryLong("select count(id) cnt from ab_order where sn='"+sn+"'").longValue()>0){
			sn = RandomUtil.createRandomNumPwd(9);
		}
		order.set("sn", sn);
		
		String merchant_id = StringUtil.toStr(this.getPara("merchant_id"));// 商家id
		AbMerchant am = AbMerchant.dao.findById(merchant_id);
		order.set("mid", am.get("id"));
		order.set("mname", am.get("mc"));
		order.set("is_type", am.get("is_type"));// 类型 0-货物类 1-服务类
		order.set("yt", StringUtil.toStr(this.getPara("yt")));// 用途 0-犒劳自己 1-赠送他人
		order.set("sjjd", am.get("lng"));
		order.set("sjwd", am.get("lat"));
		order.set("mapbusiness", am.get("mapbusiness"));
		order.set("subject_id", am.get("subject_id"));// 分类id
		order.set("subject_name", am.get("subject_name"));// 分类名称
		order.set("city_id", am.get("city_id"));
		order.set("city_name", am.get("city_name"));
		order.set("zsjf", zjf);
		order.set("yzbm", StringUtil.toStr(this.getPara("yzbm")));
		order.set("shdz", StringUtil.toStr(this.getPara("shdz")));// 收货地址
		order.set("lxr", StringUtil.toStr(this.getPara("lxr")));// 收获联系人
		order.set("lxrdh", StringUtil.toStr(this.getPara("lxrdh")));// 联系人联系电话
		SysUser cusomer = SysUser.dao.findFirst("select * from sys_user where loginid=?", order.getStr("lxrdh"));
		if(null!=cusomer){
			order.set("xdrid", cusomer.getStr("id"));
		}
		order.set("xdrmc", StringUtil.toStr(this.getPara("xdrmc")));
		order.set("xdrdh", StringUtil.toStr(this.getPara("xdrdh")));// 下单人电话
		order.set("xdsj", DateUtil.getCurrentDate());
		order.set("shdzjd", StringUtil.toStr(this.getPara("shdzjd")));// 收货地址
		order.set("shdzwd", StringUtil.toStr(this.getPara("shdzwd")));// 邮政编码
		String jl = StringUtil.toStr(this.getPara("jl"));
		order.set("jl", jl);// 距离
		BigDecimal yhje = new BigDecimal(StringUtil.toStr(this.getPara("yhje")));
		order.set("yhje", yhje);// 优惠金额
		order.set("psfs", am.getStr("sfzs"));
		
		//区域的获取
		String area_id = StringUtil.toStr(this.getPara("area_id"));
		order.set("area_id", area_id);
		if("1".equals(am.get("sfzs"))){
			order.set("ddzt", "3");
		}else{
			order.set("ddzt", "2");
		}
		order.save();// 保存订单
		/** 添加订单-结束 **/

		/** 添加订单下的附带商品-开始 **/
		String[] addnums = this.getParaValues("addnum");
		String[] addids = this.getParaValues("addid");
		for (int i = 0; i < addnums.length; i++) {
			// 未选填商品数量的商品直接过滤
			if (StringUtils.isNullOrEmpty(addnums[i])|| StringUtils.isNullOrEmpty(addids[i])) {
				continue;
			}
			AbMerchantProduct amp = AbMerchantProduct.dao.findById(addids[i]);
			AbOrderItem item = new AbOrderItem();
			item.set("id", StringUtil.getUuid32());
			item.set("orderid", id);
			item.set("createtime", DateUtil.getCurrentDate());
			item.set("subject_id", amp.get("subject_id"));
			item.set("subject_name", amp.get("subject_name"));
			item.set("itemid", amp.get("id"));
			item.set("itemname", amp.get("mc"));
			item.set("img_url", amp.get("img_url"));
			item.set("thumbnail_url", amp.get("thumbnail_url"));
			item.set("quantity", addnums[i]);
			item.set("price", amp.getBigDecimal("price"));
			item.set("totalmoney",amp.getBigDecimal("price").multiply(new BigDecimal(Double.valueOf(addnums[i]))).doubleValue());
			BigDecimal fwf = null;
			//计算跑腿费，如果是自送，获取商家设置的跑腿费，如果是其他人送货，获取分类的跑腿费
			if ("1".equals(am.get("sfzs"))){
				fwf = Db.queryBigDecimal("SELECT MAX(ptf) FROM ab_merchant_product" +
						" WHERE MID = ? AND id IN(" +
						"SELECT itemid FROM ab_order_item WHERE orderid=?)",merchant_id,id);
			}else{
				fwf = Db.queryBigDecimal("SELECT IFNULL(MAX(srvmoney),0) ptf FROM sys_area_subject_ptf WHERE area_id=? AND subject_id in(SELECT thr_id FROM ab_order_item WHERE orderid=?)",amp.getStr("area_id"), id);
			}
			item.set("ptf", fwf);
			item.set("mid", amp.get("mid"));
			item.set("mname", amp.get("mname"));
			zjf += amp.getInt("jf").intValue() * Double.valueOf(addnums[i]);
			item.save();
		}
		
		String yzm = RandomUtil.createRandomPwd(6);// 获取刘伟短信验证
		String content = "尊敬的用户,您好。您的订单已发货，您可凭借验证码【" + yzm
				+ "】进行收货确认，请务必要收到货物当面才告知送货人员，谢谢。";
		// 针对已派单的情况进行派单处理----修改为不进入派单状态
		String qdrid = StringUtil.toStr(this.getPara("qdrid"));
		if (!StringUtils.isNullOrEmpty(qdrid)) {
			AbOrder pro = AbOrder.dao.findById(id);
			pro.set("qdrid", qdrid);
			pro.set("ddzt", 3);// 进入派送状态
			pro.set("qdrname", StringUtil.toStr(this.getPara("qdrname")));// 接单人名称
			pro.set("jdrjd", StringUtil.toStr(this.getPara("jdrjd")));// 接单人所在地经纬度
			pro.set("jdrwd", StringUtil.toStr(this.getPara("jdrwd")));
			pro.set("jl", StringUtil.toStr(this.getPara("jl")));
			pro.update();
			
			SysUser usertemp = SysUser.dao.findById(qdrid);
			// 发送短信给收货人，
			String contentPaoTui = "尊敬的司机朋友，您有新的货运订单，请注意查看，谢谢。订单详情：起始地："
					+ am.getStr("xxdz") + "到达地：" + order.getStr("shdz");
			contentPaoTui += "装货时间：" + pro.getStr("xdsj") + "联系电话：" + order.getStr("lxrdh")
					+ "，出发前和货主联系确定，如不方便接单请联系客服（客服电话号码）取掉订单";
			try {
				if(SmsMessage.isSendSms("c5")){//下单成功：c5.订单发货：c6.司机接收订单：c7.送达提醒评价：c8.用户注册：c9
					SmsMessage.SendMessage(order.getStr("lxrdh"), content);// 给收货人发短信
				}
				if(SmsMessage.isSendSms("c6")){//下单成功：c5.订单发货：c6.司机接收订单：c7.送达提醒评价：c8.用户注册：c9
					SmsMessage.SendMessage(usertemp.getStr("mobile"), contentPaoTui);// 给跑腿人发短信
				}
			}catch(Exception e){
				
			}
		}else{//未指派执行推送
			try {
				if(SmsMessage.isSendSms("c5")){//下单成功：c5.订单发货：c6.司机接收订单：c7.送达提醒评价：c8.用户注册：c9
					SmsMessage.SendMessage(order.getStr("lxrdh"), content);
				}
			} catch (Exception e) {
			}// 给收货人发短信
			TuiSongUtil.Tuisong(order.getStr("id"), TuiSongUtil.getSomeSjId(order.getStr("id")));//推送处理
		}
		insertOrderSms(id,order.getStr("lxrdh"),yzm);

		// 修改商品的数量和总订单金额,商品总价
		BigDecimal spzj = Db.queryBigDecimal("SELECT SUM(price * quantity) FROM ab_order_item WHERE orderid = ?",id);
		BigDecimal cjlfjf = Db.queryBigDecimal("SELECT ifnull(cjlfjf,0) FROM   ab_order WHERE  id = ?",id);
		BigDecimal ptf = Db.queryBigDecimal("SELECT IFNULL(Max(ptf),0) FROM   ab_order_item WHERE  orderid = ?",id);
		//计算跑腿费，如果是自送，获取商家设置的跑腿费，如果是其他人送货，获取分类的跑腿费
		if ("1".equals(am.get("sfzs"))){
			ptf = Db.queryBigDecimal("SELECT IFNULL(Max(ptf),0) FROM   ab_order_item WHERE  orderid = ?",id);
		}else{
			ptf = Db.queryBigDecimal("SELECT IFNULL(MAX(srvmoney),0) ptf FROM sys_area_subject_ptf WHERE area_id=? AND subject_id in(SELECT thr_id FROM ab_order_item WHERE orderid=?)",am.getStr("area_id"),id);
		}
		//计算商家优惠、商品总金额、跑腿费、订单总金额
		if("1".equals(am.get("chk_yhhd"))){
			BigDecimal mds = am.getBigDecimal("mds");
			if(!(spzj.compareTo(mds)==-1)){
				yhje =  am.getBigDecimal("jds");
			}
		}
		BigDecimal ddzje = spzj.add(ptf).add(cjlfjf).subtract(yhje);
		String sql ="UPDATE  ab_order SET spzj=?,yhje=?,ptf=?,cjlfjf=?,ddzje=? where id = '"+id+"'";
		Db.update(sql,spzj,yhje,ptf,cjlfjf,ddzje);

		//执行订单打印操作
		AbMerchantPrint amp = AbMerchantPrint.dao.findFirst("select * from ab_merchant_print where mid=?", merchant_id);
		if(null != amp && "1".equals(amp.getStr("iswork"))){
			PrintUtils.partner = amp.getStr("partner");
			PrintUtils.machine_code = amp.getStr("machinecode");
			PrintUtils.apiKey = amp.getStr("apikey");
			PrintUtils.mKey = amp.getStr("mkey");
		}
		if(PrintUtils.pmRequest()){
			PrintUtils.sendRequest(PrintUtils.forMatterMsg(id));
		}
		this.renderJson(true);
	}
	
	public void doPrintAgin(){
		String merchant_id = StringUtil.toStr(this.getPara("mid"));
		String id = StringUtil.toStr(this.getPara("id"));
		if (!StringUtils.isNullOrEmpty(merchant_id)) {
			AbMerchantPrint amp = AbMerchantPrint.dao.findFirst("select * from ab_merchant_print where mid=?", merchant_id);
			if(null != amp && "1".equals(amp.getStr("iswork"))){
				PrintUtils.partner = amp.getStr("partner");
				PrintUtils.machine_code = amp.getStr("machinecode");
				PrintUtils.apiKey = amp.getStr("apikey");
				PrintUtils.mKey = amp.getStr("mkey");
			}
		}
		if(PrintUtils.pmRequest()){
			String msg = PrintUtils.forMatterMsg(id);
			if(PrintUtils.sendRequest(msg)){
				this.renderJson(true);
			}else{
				this.renderJson(false);
			}
		}else{
			this.renderJson(false);
		}
	}

	/**
	 * 指派/修改订单公用初始化方法
	 */
	@Before(AccessAdminInterceptor.class)
	public void getOneOrderById() {
		SysUser u = (SysUser) this.getSessionAttr("user");
		String user_id = u.getStr(SysUser.ID);
		String sql = "select * from ab_cityarea where id is not null";
		if (!StringUtils.isNullOrEmpty(user_id)&& "103".equals(u.getStr(SysUser.ROLE_ID))) {
			sql += " and user_id= '" + user_id + "'";
		}
		sql += " and type_id = '1'";// 商业圈
		List<AbCityarea> list_cityarea = AbCityarea.dao.find(sql);
		this.setAttr("list_cityarea", list_cityarea);// 商业圈列表
		List<AbSubject> list_subject = AbSubject.dao.find("select * from ab_subject where p_id='ROOT'");
		this.setAttr("list_subject", list_subject);// 类别
		String id = StringUtil.toStr(this.getPara("id"));
		AbOrder order = AbOrder.dao.findById(id);
		this.setAttr("order", order);// 订单相关信息
		boolean isShui = false;//默认不是桶装水
		// 获取该订单下的所有商品信息
		if (!StringUtils.isNullOrEmpty(order.getStr("id"))) {
			List<AbOrderItem> itemss = AbOrderItem.dao.find("select * from ab_order_item where orderid='"+ order.getStr("id") + "'");
			this.setAttr("itemss", itemss);
			if(null!=itemss){
				for(AbOrderItem item : itemss){
					if(item.getStr("subject_id").equals("1005")){
						isShui = true;//是桶装水
					}
				}
			}
		}

		if (!StringUtils.isNullOrEmpty(order.getStr("qdrid"))) {
			SysUser jdr = SysUser.dao.findById(order.getStr("qdrid"));
			this.setAttr("jdr", jdr);// 接单人的相关信息，传到前台进行使用
		}

		AbMerchant ac = AbMerchant.dao.findById(order.get("mid"));
		this.setAttr("ac", ac);// 商家信息

		String sql1 = "select * from ab_merchant where id is not null";
		if(null!=ac){
			sql1 += " and area_id = '" + ac.getStr("area_id") + "'";
		}
		List<AbMerchant> list_merchant = AbMerchant.dao.find(sql1);
		this.setAttr("list_merchant", list_merchant);// 商家列表
		//查询司机分组
		List<SysUserGroup> suglist = new ArrayList<SysUserGroup>();
		AbCityarea aca = AbCityarea.dao.findFirst("select * from ab_cityarea where user_id=? and ccm='1'", user_id);
		if(null != aca ){
			if(!"admin".equals(user_id)){
				suglist =  SysUserGroup.dao.find("select * from sys_user_group where cityid='" + aca.getStr("id") + "'");
				this.setAttr("tb_city_id", aca.getStr("id"));
			}else{
				suglist = SysUserGroup.dao.find("select * from sys_user_group");
			}
		}else{
			if("admin".equals(user_id)){
				suglist = SysUserGroup.dao.find("select * from sys_user_group");
			}
		}
		this.setAttr("suglist", suglist);
		this.setAttr("user", u);
		if(isShui){
			try {
//				String xdrid = order.getStr("xdrid");//下单人的id，接下来要获取下单人的地理位置
				Map map= MapUtils.getLngLat("", order.getStr("shdz"));
				this.setAttr("xqjd", map.get("lng"));
				this.setAttr("xqwd", map.get("lat"));
			} catch (Exception e) {
			}
			this.render("/admin/order/editorder_Shui.html");
			return;
		}
		this.render("/admin/order/editorder.html");
	}

	public void getMerChantNearBy(){
		String julitemp = StringUtil.toStr(this.getPara("julitemp"));
		String shdzjd = StringUtil.toStr(this.getPara("shdzjd"));
		String shdzwd = StringUtil.toStr(this.getPara("shdzwd"));
		BigDecimal jd = new BigDecimal(shdzjd);
		BigDecimal wd = new BigDecimal(shdzwd);
		BigDecimal jd_min = jd.subtract(new BigDecimal(julitemp));
		BigDecimal jd_max = jd.add(new BigDecimal(julitemp));
		BigDecimal wd_min = wd.subtract(new BigDecimal(julitemp));
		BigDecimal wd_max = wd.add(new BigDecimal(julitemp));
		String sql = "select a.*,b.lng as lng,b.lat as lat from sys_user a,ab_merchant b where a.id=b.id and b.subject_id='1005'";
		if(shdzjd.length() > 0 && shdzwd.length() > 0 && !"-1".equals(julitemp)){
			sql += " and b.lng >= '" + jd_min + "'";
			sql += " and b.lng <= '" + jd_max + "'";
			sql += " and b.lat >= '" + wd_min + "'";
			sql += " and b.lat <= '" + wd_max + "'";
		}
		List<SysUser> merList = SysUser.dao.find(sql);
		this.renderJson(merList);
	}
	/**
	 * 跑腿人员的查询
	 */
	@Before(AccessAdminInterceptor.class)
	public void getPaoTui() {
		SysUser u = (SysUser)this.getSessionAttr("user");
		String zt = StringUtil.toStr(this.getPara("zt"));
		String sql = "select * from(select a.id as id,a.sjzt as zt,a.xxdz as xxdz,a.zhye as zhye,a.loginid as mobile,a.loginid as loginid,a.mc as mc,b.jd as lng,b.wd as lat,a.gzzp as gzzp,b.dqsj as modify_date,cc.type as type,cc.vv vv from sys_user a,ab_sj_position b,ab_fmcar cc where a.id=b.sjid and a.mobile=cc.mobile";
		if(zt.length() > 0){
			if(zt.equals("2")){
				sql += " and a.sjzt = '"  + zt + "'";
			}else{
				sql += " and a.sjzt != '2'";
			}
		}
		String groupid = StringUtil.toStr(this.getPara("groupid"));
		if(groupid.length() > 0){
			sql += " and a.groupid = '" + groupid + "'";
		}else{
			if(u.getStr("id").length() > 0){
				sql += " and (city_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + ")";
				sql += " or area_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + "))";
			}
		}
		sql += " and a.role_id = '106' group by id order by modify_date desc) d where 1 = 1";
		
		String sjjd = StringUtil.toStr(this.getPara("sjjd"));//起点经度 20公里相差0.2
		String sjwd = StringUtil.toStr(this.getPara("sjwd"));//起点纬度    20公里差0.18
		
		String kd  = StringUtil.toStr(this.getPara("kd"));
		if(kd.length() == 0){
			kd="0.19";
		}
		if(sjjd.length() > 0 && sjwd.length() > 0 && !"-1".equals(kd)){
			BigDecimal jd = new BigDecimal(sjjd);
			BigDecimal wd = new BigDecimal(sjwd);
			BigDecimal jd_min = jd.subtract(new BigDecimal(kd));
			BigDecimal jd_max = jd.add(new BigDecimal(kd));
			BigDecimal wd_min = wd.subtract(new BigDecimal(kd));
			BigDecimal wd_max = wd.add(new BigDecimal(kd));
			sql += " and d.lng >= '" + jd_min + "'";
			sql += " and d.lng <= '" + jd_max + "'";
			sql += " and d.lat >= '" + wd_min + "'";
			sql += " and d.lat <= '" + wd_max + "'";
		}
		List<SysUser> paotui_list = SysUser.dao.find(sql);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("paotui_list", paotui_list);
		result.put("car_list", paotui_list);
		this.renderJson(result);
	}

	/**
	 * 执行派单
	 */
	@Before(AccessAdminInterceptor.class)
	public void doZhiPai() {
		String id = StringUtil.toStr(this.getPara("id"));
		AbOrder pro = AbOrder.dao.findById(id);

		String qdrid = StringUtil.toStr(this.getPara("qdrid"));// 跑腿人员ID
		boolean hasSj = qdrid.length() > 0;
		// 如果选择了跑腿人员即进入派送状态：ddzt=3即可
		if (!StringUtils.isNullOrEmpty(qdrid)) {
			pro.set("qdrid", qdrid);
			pro.set("ddzt", 3);// 进入派送状态
			pro.set("qdrname", StringUtil.toStr(this.getPara("qdrname")));// 接单人名称
			pro.set("jdrjd", StringUtil.toStr(this.getPara("jdrjd")));// 接单人所在地经纬度
			pro.set("jdrwd", StringUtil.toStr(this.getPara("jdrwd")));
			pro.set("qdsj", DateUtil.getCurrentDate());
		}
		pro.set("jl", StringUtil.toStr(this.getPara("jl")));
		pro.set("sjjd", StringUtil.toStr(this.getPara("sjjd")));
		pro.set("sjwd", StringUtil.toStr(this.getPara("sjwd")));
		pro.set("mapbusiness", StringUtil.toStr(this.getPara("mapbusiness")));
		String shdz = StringUtil.toStr(this.getPara("shdz"));// 收货地址
		if (!StringUtils.isNullOrEmpty(shdz)) {
			pro.set("shdz", shdz);//
			pro.set("shdzjd", StringUtil.toStr(this.getPara("shdzjd")));// 接单人名称
			pro.set("shdzwd", StringUtil.toStr(this.getPara("shdzwd")));// 接单人所在地经纬度
		}
		String yt = StringUtil.toStr(this.getPara("yt"));// 收获联系人
		if (!StringUtils.isNullOrEmpty(yt)) {
			pro.set("yt", yt);//
		}
		String lxr = StringUtil.toStr(this.getPara("lxr"));// 收获联系人
		if (!StringUtils.isNullOrEmpty(lxr)) {
			pro.set("lxr", lxr);//
		}
		AbMerchant mc = AbMerchant.dao.findById(pro.getStr("mid"));
		String lxrdh = StringUtil.toStr(this.getPara("lxrdh"));// 收货联系人电话
		if (!StringUtils.isNullOrEmpty(lxrdh)) {
			pro.set("lxrdh", lxrdh);//
			SysUser usertemp = SysUser.dao.findById(qdrid);
			// 发送短信给收货人，
			String yzm = RandomUtil.createRandomPwd(6);// 获取刘伟短信验证
			String content = "尊敬的用户,您好。您的订单已发货，您可凭借验证码【" + yzm
					+ "】进行收货确认，请务必要收到货物当面才告知送货人员，谢谢。";
			String contentPaoTui = "尊敬的司机朋友，您有新的货运订单，请注意查看，谢谢。订单详情：起始地："
					+ mc.getStr("xxdz") + "到达地：" + shdz;
			contentPaoTui += "装货时间：" + pro.getStr("xdsj") + "联系电话：" + lxrdh
					+ "，出发前和货主联系确定，如不方便接单请联系客服（客服电话号码）取掉订单";
			try {
				SmsMessage.SendMessage(lxrdh, content);// 给收货人发短信
				if(hasSj){
					
					if(SmsMessage.isSendSms("c6")){//下单成功：c5.订单发货：c6.司机接收订单：c7.送达提醒评价：c8.用户注册：c9
						SmsMessage.SendMessage(usertemp.getStr("mobile"), contentPaoTui);// 给跑腿人发短信
					}
				}
				insertOrderSms(id,lxrdh,yzm);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
		}

		String yzbm = StringUtil.toStr(this.getPara("yzbm"));// 收货人邮政编码
		if (!StringUtils.isNullOrEmpty(yzbm)) {
			pro.set("yzbm", yzbm);//
		}

		String xdrmc = StringUtil.toStr(this.getPara("xdrmc"));// 下单人名称
		if (!StringUtils.isNullOrEmpty(xdrmc)) {
			pro.set("xdrmc", xdrmc);//
		}
		String xdrdh = StringUtil.toStr(this.getPara("xdrdh"));// 下单人电话
		if (!StringUtils.isNullOrEmpty(xdrdh)) {
			pro.set("xdrdh", xdrdh);//
		}
		String yhje = StringUtil.toStr(this.getPara("yhje"));// 优惠金额
		if (!StringUtils.isNullOrEmpty(yhje)) {
			pro.set("yhje", yhje);//
		}
		pro.update();
		
		// 修改商品的数量和总订单金额,商品总价
		BigDecimal spzj = Db.queryBigDecimal("SELECT SUM(price * quantity) FROM ab_order_item WHERE orderid = ?",id);
		BigDecimal cjlfjf = Db.queryBigDecimal("SELECT ifnull(cjlfjf,0) FROM   ab_order WHERE  id = ?",id);
		BigDecimal ptf = Db.queryBigDecimal("SELECT IFNULL(Max(ptf),0) FROM   ab_order_item WHERE  orderid = ?",id);
		//计算跑腿费，如果是自送，获取商家设置的跑腿费，如果是其他人送货，获取分类的跑腿费
		if ("1".equals(mc.get("sfzs"))){
			ptf = Db.queryBigDecimal("SELECT IFNULL(Max(ptf),0) FROM   ab_order_item WHERE  orderid = ?",id);
		}else{
			ptf = Db.queryBigDecimal("SELECT IFNULL(MAX(srvmoney),0) ptf FROM sys_area_subject_ptf WHERE area_id=? AND subject_id in(SELECT thr_id FROM ab_order_item WHERE orderid=?)",mc.getStr("area_id"),id);
		}
		//计算商家优惠、商品总金额、跑腿费、订单总金额
		if("1".equals(mc.get("chk_yhhd"))){
			BigDecimal mds = mc.getBigDecimal("mds");
			if(!(spzj.compareTo(mds)==-1)){
				yhje =  mc.getBigDecimal("jds").toString();
			}
		}
		BigDecimal ddzje = spzj.add(ptf).add(cjlfjf).subtract(new BigDecimal(yhje));
		String sql ="UPDATE  ab_order SET spzj=?,yhje=?,ptf=?,cjlfjf=?,ddzje=? where id = '"+id+"'";
		Db.update(sql,spzj,yhje,ptf,cjlfjf,ddzje);

		this.renderJson(true);
	}

	/**
	 * 根据上好佳ID获取商家所有的信息
	 */
	@Before(AccessAdminInterceptor.class)
	public void getMerchantByID() {
		String merchant_id = StringUtil.toStr(this.getPara("merchant_id"));// 商家ID
		AbMerchant abm = AbMerchant.dao.findById(merchant_id);
		this.renderJson(abm);
	}

	/**
	 * 根据订单ID取消订单
	 */
	@Before(AccessAdminInterceptor.class)
	public void doCancelOrder() {
		String id = StringUtil.toStr(this.getPara("id"));// 订单ID
		AbOrder order = AbOrder.dao.findById(id);
		boolean flag = false;
		if (null != order) {
			order.set("ddzt", 8);// 订单撤销之后所处的状态
			order.set("ddcxsj", DateUtil.getCurrentDate());// 订单撤销时间
			SysUser u = (SysUser) this.getRequest().getSession().getAttribute("user");// 获取撤销订单的人员，当前是管理员进行撤销的
			order.set("ddcxrid", u.getStr("id"));// 订单撤销人ID
			order.set("ddcxrmc", u.getStr("mc"));// 订单撤销人名称
			order.update();
			flag = true;
		}
		this.renderJson(flag);
	}

	/**
	 * 跟踪主体方法,是否显示小球，只有去点击显示小球按钮，才会切入小球模式
	 */
	public void guiji() {
		String isQiu = StringUtil.toStr(this.getPara("isQiu"));// 订单ID
		if (!StringUtils.isNullOrEmpty(isQiu)) {
			this.setAttr("isQiu", isQiu);
			this.setAttr("isQiuDes", "去掉小球");
		} else {
			this.setAttr("isQiuDes", "显示小球");
		}
		
		
		String id = StringUtil.toStr(this.getPara("id"));// 订单ID
		AbOrder order = AbOrder.dao.findById(id);
		this.setAttr("order", order);//订单信息

		SysUser su = new SysUser();
		// 获取接单人相关信息
		if (null != order && !StringUtils.isNullOrEmpty(order.getStr("qdrid"))) {
			String qdrid = order.getStr("qdrid");
			su = SysUser.dao.findById(qdrid);
		}
		if("1" == order.getStr("psfs")){//商家自送方式的轨迹处理
			String qdrid = order.getStr("mid");
			su = SysUser.dao.findById(qdrid);
		}
		this.setAttr("su", su);//接单人信息
		if(order.getStr("sn").startsWith("TC")){
			this.setAttr("tcorder", AbTcExpressOrder.dao.findFirst("select * from ab_tc_express_order where sn=?", order.getStr("sn")));
			this.render("/admin/order/guiji_TC.html");
		}else{
			AbMerchant mer = AbMerchant.dao.findById(order.getStr("mid"));
			this.setAttr("mer", mer);//店铺发货地点
			this.render("/admin/order/guiji.html");
		}
	}

	/**
	 * 定时去后台查询跑腿人员的最新位置
	 */
//	@Before(AccessAdminInterceptor.class)
	public void huaGuiJi() {
		String shrid = StringUtil.toStr(this.getPara("shrid"));// 跑腿人员ID
		String xdsj = StringUtil.toStr(this.getPara("xdsj"));
		String orderid = StringUtil.toStr(this.getPara("orderid"));
		AbOrder order = null;
		AbTcExpressOrder tcorder = null;
		if(!StringUtils.isNullOrEmpty(orderid)){
			//获取下单时间
			
			order =	AbOrder.dao.findById(orderid);
			tcorder = AbTcExpressOrder.dao.findFirst("select * from ab_tc_express_order where sn=?",order.getStr("sn"));
			if(order!=null){
				List<AbOrderPosition> list = new ArrayList<AbOrderPosition>();
				
				List<AbOrderPosition> orderPosition = AbOrderPosition.dao.find("select * from ab_order_position where orderid=? order by dqsj asc",orderid);
				
				//相同的位置只画一次
				Map<String,String> pos = new HashMap<String, String>();
				
				if(orderPosition==null || orderPosition.size() ==0 ){
					orderPosition = new ArrayList<AbOrderPosition>();
					SysUser user = SysUser.dao.findById(order.getStr(AbOrder.QDRID));
					AbOrderPosition position = new AbOrderPosition();
					try{
						position.set(AbOrderPosition.JD, user.getStr("lng"));
						position.set(AbOrderPosition.ORDERID, order.get(AbOrder.ID));
						position.set(AbOrderPosition.WD, user.get("lat"));
						position.set(AbOrderPosition.MID, "");
						position.set(AbOrderPosition.SHRID, order.get("xdrid"));
						position.set(AbOrderPosition.DQSJ, new Date());
						orderPosition.add(position);
					}catch (Exception e) {
						e.printStackTrace();
						position.set(AbOrderPosition.JD, order.get("shdzjd"));
						position.set(AbOrderPosition.ORDERID, order.get(AbOrder.ID));
						position.set(AbOrderPosition.WD, order.get("shdzwd"));
						position.set(AbOrderPosition.MID, "");
						position.set(AbOrderPosition.SHRID, order.get("xdrid"));
						position.set(AbOrderPosition.DQSJ, new Date());
						System.out.println(order.getStr(AbOrder.QDRID)+"无法获取司机位置");
					}
				}else{
					for(AbOrderPosition p : orderPosition){
						String key = p.getStr(AbOrderPosition.JD)+"_"+p.getStr(AbOrderPosition.WD);
						if(!pos.containsKey(key)){
							list.add(p);
							pos.put(key, "");
						}
					}
				}
				
				Map<String,Object> map= new HashMap<String,Object>();
				
				map.put("data", list);
				map.put("order", tcorder);
				this.renderJson(map);
			}
			
		}else{
			String sql = "select * from ab_sj_position where 1= 1";
			if (!StringUtils.isNullOrEmpty(shrid)) {
				sql += " and sjid ='" + shrid + "'";
			}
			if (!StringUtils.isNullOrEmpty(xdsj)) {
				sql += " and dqsj >='" + xdsj + "'";
			}
			sql += " order by dqsj";
			List<AbSjPosition> position_list = AbSjPosition.dao.find(sql);
			Map<String,Object> map= new HashMap<String,Object>();
			map.put("data", position_list);
			map.put("order", tcorder);
			this.renderJson(map);
		}
	}

	/**
	 * 完成订单前的查询，将订单涉及到的信息传到前台，进行显示
	 */
	@Before(AccessAdminInterceptor.class)
	public void prewancheng() {
		String id = StringUtil.toStr(this.getPara("id"));// 订单ID
		AbOrder order = AbOrder.dao.findById(id);
		if (StringUtils.isNullOrEmpty(order.getStr("sdsj"))) {// 送达时间
			order.set("sdsj", DateUtil.getCurrentDate());
		}
		this.setAttr("order", order);

		if (!StringUtils.isNullOrEmpty(order.getStr("qdrid"))) {
			SysUser su = SysUser.dao.findById(order.getStr("qdrid"));
			this.setAttr("su", su);
		}

		List<AbSubject> list_subject = AbSubject.dao.find("select * from ab_subject where p_id='ROOT'");
		this.setAttr("list_subject", list_subject);// 类别

		this.render("/admin/order/prewancheng.html");
	}

	/**
	 * 完成订单前的查询，入库
	 */
	@Before(AccessAdminInterceptor.class)
	public void doWanCheng() {
		String id = StringUtil.toStr(this.getPara("id"));// 订单ID
		// 获取最新订单信息
		AbOrder order = AbOrder.dao.findById(id);
		String subject_id = StringUtil.toStr(this.getPara("subject_id"));// 类别
		if (!StringUtils.isNullOrEmpty(subject_id)) {
			order.set("subject_id", subject_id);
			AbSubject as = AbSubject.dao.findById(subject_id);
			order.set("subject_name", as.getStr("mc"));
		}
		String xdrid = StringUtil.toStr(this.getPara("xdrid"));// 下单人
		if (!StringUtils.isNullOrEmpty(xdrid)) {
			order.set("xdrid", xdrid);
			order.set("xdrmc", StringUtil.toStr(this.getPara("xdrmc")));
			order.set("xdrdh", StringUtil.toStr(this.getPara("xdrdh")));
			// 更新到人员列表中
			SysUser su = SysUser.dao.findById(xdrid);
			if(null!= su){
				su.set("mc", StringUtil.toStr(this.getPara("xdrmc")));
				su.set("mobile", StringUtil.toStr(this.getPara("xdrdh")));
				su.update();
			}
		}
		String lxr = StringUtil.toStr(this.getPara("lxr"));// 收获联系人
		if (!StringUtils.isNullOrEmpty(lxr)) {
			order.set("lxr", lxr);
		}
		String lxrdh = StringUtil.toStr(this.getPara("lxrdh"));// 收获联系人电话
		if (!StringUtils.isNullOrEmpty(lxrdh)) {
			order.set("lxrdh", lxrdh);
		}
		String yzbm = StringUtil.toStr(this.getPara("yzbm"));// 收货人邮政编码
		if (!StringUtils.isNullOrEmpty(yzbm)) {
			order.set("yzbm", yzbm);
		}
		String shdz = StringUtil.toStr(this.getPara("shdz"));// 收货人地址
		if (!StringUtils.isNullOrEmpty(shdz)) {
			order.set("shdz", shdz);
		}

		String xdsj = StringUtil.toStr(this.getPara("xdsj"));// 出发时间
		if (!StringUtils.isNullOrEmpty(xdsj)) {
			order.set("xdsj", xdsj);
		}
		String sdsj = StringUtil.toStr(this.getPara("sdsj"));// 到达时间
		if (!StringUtils.isNullOrEmpty(sdsj)) {
			order.set("sdsj", sdsj);
		}
		String jl = StringUtil.toStr(this.getPara("jl"));// 距离
		if (!StringUtils.isNullOrEmpty(jl)) {
			order.set("jl", jl);
		}
		String zffs = StringUtil.toStr(this.getPara("zffs"));// 结算方式
		if (!StringUtils.isNullOrEmpty(zffs)) {
			order.set("zffs", zffs);
		}
		String accountremark = StringUtil.toStr(this.getPara("accountremark"));// 备注
		if (!StringUtils.isNullOrEmpty(accountremark)) {
			order.set("accountremark", accountremark);
		}
		SysUser u = (SysUser) this.getRequest().getSession().getAttribute("user");// 获取撤销订单的人员，当前是管理员进行撤销的
		order.set("accountid", u.getStr("id"));// 订单撤销人ID
		order.set("accountname", u.getStr("mc"));// 订单撤销人名称
		order.set("accountdate", DateUtil.getCurrentDate());// 订单撤销人名称
		order.set("ddzt", 5);
		order.update();
		this.renderJson(true);
	}

	/**
	 * 进入修改退回页面
	 */
	@Before(AccessAdminInterceptor.class)
	public void preeditcancel() {
		String id = StringUtil.toStr(this.getPara("id"));// 订单ID
		AbOrder order = AbOrder.dao.findById(id);
		if (StringUtils.isNullOrEmpty(order.getStr("sdsj"))) {// 送达时间
			order.set("sdsj", DateUtil.getCurrentDate());
		}
		this.setAttr("order", order);

		if (!StringUtils.isNullOrEmpty(order.getStr("qdrid"))) {
			SysUser su = SysUser.dao.findById(order.getStr("qdrid"));
			this.setAttr("su", su);
		}

		List<AbSubject> list_subject = AbSubject.dao.find("select * from ab_subject where p_id='ROOT'");
		this.setAttr("list_subject", list_subject);// 类别
		this.render("/admin/order/editcancel.html");
	}

	/**
	 * 执行退回未指派的操作
	 */
	@Before(AccessAdminInterceptor.class)
	public void toWeiZhiPai() {
		boolean flag = false;
		String id = StringUtil.toStr(this.getPara("id"));
		if (!StringUtils.isNullOrEmpty(id)) {
			AbOrder order = AbOrder.dao.findById(id);
			if (null != order) {
				order.set("ddzt", 2);// 订单状态
				order.set("qdrid", "");// 清楚派单人员的相关信息
				order.set("qdrname", "");// 清楚派单人员的相关信息
				order.set("jdrjd", "");// 清楚派单人员的相关信息
				order.set("jdrwd", "");// 清楚派单人员的相关信息
				order.set("jl", "");// 清楚派单人员的相关信息
				order.update();
				flag = true;
			}
		}
		this.renderJson(flag);
	}

	/**
	 * 执行更新
	 */
	@Before(AccessAdminInterceptor.class)
	public void doEdit() {
		String id = StringUtil.toStr(this.getPara("id"));// 订单ID
		// 获取最新订单信息
		AbOrder order = AbOrder.dao.findById(id);
		String subject_id = StringUtil.toStr(this.getPara("subject_id"));// 类别
		if (!StringUtils.isNullOrEmpty(subject_id)) {
			order.set("subject_id", subject_id);
			AbSubject as = AbSubject.dao.findById(subject_id);
			order.set("subject_name", as.getStr("mc"));
		}
		String xdrid = StringUtil.toStr(this.getPara("xdrid"));// 下单人
		if (!StringUtils.isNullOrEmpty(xdrid)) {
			order.set("xdrid", xdrid);
			order.set("xdrmc", StringUtil.toStr(this.getPara("xdrmc")));
			order.set("xdrdh", StringUtil.toStr(this.getPara("xdrdh")));
			// 更新到人员列表中
			SysUser su = SysUser.dao.findById(xdrid);
			su.set("mc", StringUtil.toStr(this.getPara("xdrmc")));
			su.set("mobile", StringUtil.toStr(this.getPara("xdrdh")));
			su.update();
		}
		String lxr = StringUtil.toStr(this.getPara("lxr"));// 收获联系人
		if (!StringUtils.isNullOrEmpty(lxr)) {
			order.set("lxr", lxr);
		}
		String lxrdh = StringUtil.toStr(this.getPara("lxrdh"));// 收获联系人电话
		if (!StringUtils.isNullOrEmpty(lxrdh)) {
			order.set("lxrdh", lxrdh);
		}
		String yzbm = StringUtil.toStr(this.getPara("yzbm"));// 收货人邮政编码
		if (!StringUtils.isNullOrEmpty(yzbm)) {
			order.set("yzbm", yzbm);
		}
		String shdz = StringUtil.toStr(this.getPara("shdz"));// 收货人地址
		if (!StringUtils.isNullOrEmpty(shdz)) {
			order.set("shdz", shdz);
		}

		String xdsj = StringUtil.toStr(this.getPara("xdsj"));// 出发时间
		if (!StringUtils.isNullOrEmpty(xdsj)) {
			order.set("xdsj", xdsj);
		}
		String sdsj = StringUtil.toStr(this.getPara("sdsj"));// 到达时间
		if (!StringUtils.isNullOrEmpty(sdsj)) {
			order.set("sdsj", sdsj);
		}
		String jl = StringUtil.toStr(this.getPara("jl"));// 距离
		if (!StringUtils.isNullOrEmpty(jl)) {
			order.set("jl", jl);
		}
		String zffs = StringUtil.toStr(this.getPara("zffs"));// 结算方式
		if (!StringUtils.isNullOrEmpty(zffs)) {
			order.set("zffs", zffs);
		}

		// accountremark
		String accountremark = StringUtil.toStr(this.getPara("accountremark"));// 备注
		if (!StringUtils.isNullOrEmpty(accountremark)) {
			order.set("accountremark", accountremark);
		}
		order.update();
		this.renderJson(true);
	}

	/**
	 * 初始化批量指派
	 */
	@Before(AccessAdminInterceptor.class)
	public void initpiliangzhipai() {
		String area_id = StringUtil.toStr(this.getPara("area_id"));
		String sql = "select a.* from ab_order a,ab_merchant b where a.mid = b.id";
		// 添加未指派订单的状态
		sql += " and  a.ddzt = '2'";

		// 商圈有信息的进行相关订单的查询
		if (!StringUtils.isNullOrEmpty(area_id)) {
			sql += " and b.area_id = '" + area_id + "'";
		}
		List<AbOrder> order_list = AbOrder.dao.find(sql);
		this.setAttr("order_list", order_list);

		SysUser u = (SysUser) this.getRequest().getSession().getAttribute("user");
		String user_id = u.getStr(SysUser.ID);
		String sql_area = "select * from ab_cityarea where id is not null";
		if (!StringUtils.isNullOrEmpty(user_id)&& "103".equals(u.getStr(SysUser.ROLE_ID))) {
			sql_area += " and user_id= '" + user_id + "'";
		}
		sql_area += " and type_id = '1'";// 商业圈
		sql_area += " and id = '" + area_id + "'";// 商业圈
		List<AbCityarea> list_cityarea = AbCityarea.dao.find(sql_area);
		this.setAttr("cityarea", list_cityarea.get(0));

		// 获取类别
		List<AbSubject> list_subject = AbSubject.dao.find("select * from ab_subject where p_id='ROOT'");
		this.setAttr("list_subject", list_subject);

		this.render("/admin/order/initpiliangzhipai.html");
	}

	/**
	 * 过滤订单
	 */
	@Before(AccessAdminInterceptor.class)
	public void getOrdersBySubject() {
		String area_id = StringUtil.toStr(this.getPara("area_id"));
		String subject_id = StringUtil.toStr(this.getPara("subject_id"));
		String sql = "select a.* from ab_order a,ab_merchant b where a.mid = b.id";
		// 添加未指派订单的状态
		sql += " and  a.ddzt = '2'";

		// 商圈有信息的进行相关订单的查询
		if (!StringUtils.isNullOrEmpty(area_id)) {
			sql += " and b.area_id = '" + area_id + "'";
		}
		// 类别过滤
		if (!StringUtils.isNullOrEmpty(subject_id)) {
			sql += " and a.subject_id = " + subject_id;
		}
		List<AbOrder> order_list = AbOrder.dao.find(sql);
		this.renderJson(order_list);
	}

	/**
	 * 批量指派
	 */
	@Before(AccessAdminInterceptor.class)
	public void doPiLiangZhiPai() {
		String area_id = StringUtil.toStr(this.getPara("area_id"));
		String qdrid = StringUtil.toStr(this.getPara("qdrid"));
		String qdrname = StringUtil.toStr(this.getPara("qdrname"));
		String jdrjd = StringUtil.toStr(this.getPara("jdrjd"));
		String jdrwd = StringUtil.toStr(this.getPara("jdrwd"));

		String subject_id = StringUtil.toStr(this.getPara("subject_id"));

		String sql = "select a.* from ab_order a,ab_merchant b where a.mid = b.id";
		sql += " and  a.ddzt = '2'";
		// 商圈有信息的进行相关订单的查询
		if (!StringUtils.isNullOrEmpty(area_id)) {
			sql += " and b.area_id = '" + area_id + "'";
		}
		// 类别过滤
		if (!StringUtils.isNullOrEmpty(subject_id)) {
			sql += " and a.subject_id = " + subject_id;
		}
		List<AbOrder> order_list = AbOrder.dao.find(sql);
		for (AbOrder order : order_list) {
			if (StringUtils.isNullOrEmpty(order.getStr("lxr"))
					|| StringUtils.isNullOrEmpty(order.getStr("lxrdh"))
					|| StringUtils.isNullOrEmpty(order.getStr("shdzjd"))) {
				this.renderJson(false);
				return;
			}
		}
		for (AbOrder order : order_list) {
			order.set("qdrid", qdrid);
			order.set("qdrname", qdrname);
			order.set("jdrjd", jdrjd);
			order.set("jdrwd", jdrwd);
			order.set("ddzt", 3);
			order.update();
			AbMerchant mc = AbMerchant.dao.findById(order.getStr("mid"));
			String lxrdh = order.getStr("lxrdh");// 收货联系人电话
			SysUser usertemp = SysUser.dao.findById(qdrid);
			// 发送短信给收货人，
			String yzm = RandomUtil.createRandomPwd(6);// 获取刘伟短信验证
			String content = "尊敬的用户,您好。您的订单已发货，您可凭借验证码【" + yzm
					+ "】进行收货确认，请务必要收到货物当面才告知送货人员，谢谢。";
			String contentPaoTui = "尊敬的司机朋友，您有新的货运订单，请注意查看，谢谢。订单详情：起始地："
					+ mc.getStr("xxdz") + "到达地：" + order.getStr("shdz");
			contentPaoTui += "装货时间：" + order.getStr("xdsj") + "联系电话：" + lxrdh
					+ "，出发前和货主联系确定，如不方便接单请联系客服（客服电话号码）取掉订单";
			try {
				if(SmsMessage.isSendSms("c5")){//下单成功：c5.订单发货：c6.司机接收订单：c7.送达提醒评价：c8.用户注册：c9
					SmsMessage.SendMessage(lxrdh, content);// 给收货人发短信
				}
				
				if(SmsMessage.isSendSms("c6")){//下单成功：c5.订单发货：c6.司机接收订单：c7.送达提醒评价：c8.用户注册：c9
					SmsMessage.SendMessage(usertemp.getStr("mobile"), contentPaoTui);// 给跑腿人发短信
				}
				insertOrderSms(order.getStr("id"),lxrdh,yzm);
			}catch(Exception e){
				
			}
		}
		this.renderJson(true);
	}

	@Before(AccessAdminInterceptor.class)
	public void zfbcz() {
		String id = StringUtil.getUuid32();
		String tradeno = System.currentTimeMillis() + "";
		SysCzTx cztx = new SysCzTx();
		cztx.set("id", id);
		cztx.set("tradeno", tradeno);
		cztx.set("type", 0);
		cztx.set("result", 0);
		cztx.set("totalfee", 0.01);
		SysUser u = (SysUser) this.getRequest().getSession()
				.getAttribute("user");// 后台用管理员的，前台用客户自己的 ：abuser
		String user_id = u.getStr(SysUser.ID);
		cztx.set("userid", user_id);
		cztx.save();
		this.setAttr("WIDbody", "abc");
		this.setAttr("WIDtotal_fee", 0.01);
		this.setAttr("WIDsubject", "eeee");
		this.setAttr("WIDout_trade_no", tradeno);
		this.render("/admin/order/alipayindex.html");
	}

	@Before(AccessAdminInterceptor.class)
	public void alipayapi() throws UnsupportedEncodingException {
		HttpServletRequest request = this.getRequest();
		SysUser u = (SysUser)request.getSession().getAttribute("user");
		String user_id = u.getStr(SysUser.ID);
		// 查询支付宝相关信息
		SysConfig config = SysConfig.dao.findFirst("select * from sys_config where c6='" + user_id + "'");
		if(null == config){//如果区域管理员没有设置支付宝参数，可以使用默认的支付宝参数
			config = new SysConfig();
			config.set("c1", "2088511526488301");
			config.set("c2", "sl9w4o4of1h3hbg81ko20o8f7cjyd52b");
			config.set("c5", "banxiandianzi@163.com");
		}
		// 支付类型
		String payment_type = "1";
		String http = request.getScheme();
		String ip = request.getServerName();
		int port = request.getServerPort();
		// 必填，不能修改
		// 页面跳转同步通知页面路径
		String return_url = http + "://" + ip + ":" + port + "/admin/order/payBack";
		// 需http://格式的完整路径，不能加?id=123这类自定义参数，不能写成http://localhost/

		// 卖家支付宝帐户
		// String seller_email = new
		// String(this.getPara("WIDseller_email").getBytes("ISO-8859-1"),"UTF-8");
		String seller_email = config.getStr("c5");
		// 必填

		// 商户订单号
		String out_trade_no = new String(this.getPara("WIDout_trade_no").getBytes("UTF-8"), "UTF-8");
		// 商户网站订单系统中唯一订单号，必填

		// 订单名称
		String subject = new String(this.getPara("WIDsubject").getBytes("UTF-8"), "UTF-8");
		// 必填
		// 付款金额
		String total_fee = new String(this.getPara("WIDtotal_fee").getBytes("UTF-8"), "UTF-8");
		// 必填
		// 订单描述

		String body = new String(this.getPara("WIDbody").getBytes("UTF-8"), "UTF-8");
		// 商品展示地址
		String show_url = http + "://" + ip + ":" + port;
		// 需以http://开头的完整路径，例如：http://www.商户网址.com/myorder.html

		// 把请求参数打包成数组
		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", "create_direct_pay_by_user");
		sParaTemp.put("partner", config.getStr("c1"));
		sParaTemp.put("_input_charset", "utf-8");
		sParaTemp.put("payment_type", payment_type);
		// sParaTemp.put("notify_url", notify_url);
		sParaTemp.put("return_url", return_url);
		sParaTemp.put("seller_email", seller_email);
		sParaTemp.put("out_trade_no", out_trade_no);
		sParaTemp.put("subject", subject);
		sParaTemp.put("total_fee", total_fee);
		sParaTemp.put("body", body);
		sParaTemp.put("show_url", show_url);

		// 建立请求
		String sHtmlText = AlipaySubmit.buildRequest(sParaTemp, "get", "确认");
		this.setAttr("sHtmlText", sHtmlText);
		this.render("/admin/order/alipayapi.html");
	}

	@Before(AccessAdminInterceptor.class)
	public void payBack() {
		String out_trade_no = StringUtil.toStr(this.getPara("out_trade_no"));
		String total_fee = StringUtil.toStr(this.getPara("total_fee"));
		String trade_status = StringUtil.toStr(this.getPara("trade_status"));
		SysCzTx cztx = SysCzTx.dao.findFirst("select * from sys_cz_tx where tradeno='"+ out_trade_no + "'");
		// 充值成功
		if ("TRADE_SUCCESS".equals(trade_status)&& "0".equals(cztx.getStr("result"))&& "0".equals(cztx.getStr("type"))) {
			cztx.set("result", 1);
			cztx.update();
			SysUser user = SysUser.dao.findById(cztx.getStr("userid"));
			user.set("zhye",user.getBigDecimal("zhye").add(new BigDecimal(total_fee)));
			user.update();
		}
		// 提现
		// 付款
		this.render("/admin/order/payBack.html");
	}
	
	/**
	 * 订单时间分布
	 */
	@Before(AccessAdminInterceptor.class)
	public void ordertime(){
		SysUser u = (SysUser) this.getRequest().getSession().getAttribute("user");
		String starttime = StringUtil.toStr(this.getPara("starttime"));
		String endtime = StringUtil.toStr(this.getPara("endtime"));
		if(StringUtils.isNullOrEmpty(starttime)){
			this.render("/admin/order/ordertime.html");
		}else{
			String user_id = u.getStr(SysUser.ID);
			starttime +=" 00:00:00";
			endtime += " 23:59:59";
//			String sql = "select * from ab_order a left join ab_merchant b on a.mid=b.id where 1 = 1";
//			if("103".equals(u.getStr("role_id"))){
//				sql +=" and b.area_id in (select id from ab_cityarea where user_id = '" + user_id + "')";
//			}
//			sql +=" and a.xdsj >= '" + starttime +"' and a.xdsj <='" + endtime + "'";
			
			String sqlNew = "select a.* from ab_order a where 1 = 1 ";
			if("103".equals(u.getStr("role_id"))){
				sqlNew +=" and a.xdrdh in (select mobile from sys_user where city_id in (" + CityAttachAdminUtil.getCityByAdmin(user_id) + ")";
				sqlNew += " or area_id in (" + CityAttachAdminUtil.getCityByAdmin(user_id) + "))";
			}
			sqlNew +=" and a.xdsj >= '" + starttime +"' and a.xdsj <='" + endtime + "'";
			String sqlCar = "select * from ab_fmcar_order a where 1 = 1";
			if("103".equals(u.getStr("role_id"))){
				sqlCar +=" and a.create_id in (select id from sys_user where city_id in (" + CityAttachAdminUtil.getCityByAdmin(user_id) + ")";
				sqlCar += " or area_id in (" + CityAttachAdminUtil.getCityByAdmin(user_id) + "))";
			}
			sqlCar +=" and a.create_time >= '" + starttime +"' and a.create_time <='" + endtime + "'";
			List<AbOrder> order_List = AbOrder.dao.find(sqlNew);
			List<AbFmcarOrder> afcoList = AbFmcarOrder.dao.find(sqlCar);
			String[] data1 = new String[24];for(int i = 0; i < 24;i++){data1[i] ="0";}
			String[] data2 = new String[24];for(int i = 0; i < 24;i++){data2[i] ="0";}
			for(AbOrder order : order_List){
				String xdsjtemp = order.getStr("xdsj").substring(11, 13);
				int index = Integer.parseInt(xdsjtemp);
				int inttemp = Integer.parseInt(data1[index]);
				data1[index] = (inttemp + 1) + "";
				float moneytemp = Float.parseFloat(data2[index]);
				BigDecimal ddzje = order.getBigDecimal("ddzje");
				data2[index] = (ddzje.add(new BigDecimal(moneytemp))).toString();
			}
			for(AbFmcarOrder order : afcoList){
				SimpleDateFormat  sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String xdsjtemp = sdf.format(order.getTimestamp("create_time")).substring(11, 13);
				int index = Integer.parseInt(xdsjtemp);
				int inttemp = Integer.parseInt(data1[index]);
				data1[index] = (inttemp + 1) + "";
//				float moneytemp = Float.parseFloat(data2[index]);
//				BigDecimal ddzje = order.getBigDecimal("ddzje");
//				data2[index] = (ddzje.add(new BigDecimal(moneytemp))).toString();
			}
			Map<String,Object> map = new HashMap<String,Object>();
			String[] legend = {"订单量","总金额"};
			map.put("legend", legend);
			//x做标注
			List<String> category = new ArrayList<String>();
			for(int i = 0; i < 24;i++){category.add(i + "");}
			map.put("category", category.toArray());
			map.put("data1", data1);
			map.put("data2", data2);
			this.renderJson(map);
		}
	}
	/**
	 * 订单来源分布
	 */
	@Before(AccessAdminInterceptor.class)
	public void ordersource(){
		String starttime = StringUtil.toStr(this.getPara("starttime"));
		String endtime = StringUtil.toStr(this.getPara("endtime"));
		if(StringUtils.isNullOrEmpty(starttime)){
			this.render("/admin/order/ordersource.html");
		}else{
			SysUser u = (SysUser) this.getRequest().getSession().getAttribute("user");
			String user_id = u.getStr(SysUser.ID);
			starttime +=" 00:00:00";
			endtime += " 23:59:59";
			String sql = "select * from ab_order a,ab_merchant b where a.mid=b.id";
			if("103".equals(u.getStr("role_id"))){
				sql +=" and b.area_id in (select id from ab_cityarea where user_id = '" + user_id + "')";
			}
			sql +=" and a.xdsj >= '" + starttime +"' and a.xdsj <='" + endtime + "'";
			List<AbOrder> order_List = AbOrder.dao.find(sql);
			List<String> category = new ArrayList<String>();//X轴数据显示
			Map<String,Object> map1 = new HashMap<String,Object>();//订单量
			Map<String,Object> map2 = new HashMap<String,Object>();//订单总额
			for(AbOrder order : order_List){
				String typeSource  = order.getStr("mapbusiness");
				typeSource = StringUtils.isNullOrEmpty(typeSource) ? "其他" : typeSource;
				if(!category.contains(typeSource)){
					category.add(typeSource);
				}
				if(!map1.containsKey(typeSource)){
					map1.put(typeSource, 0);
				}
				map1.put(typeSource, Integer.parseInt(map1.get(typeSource).toString()) + 1);
				
				if(!map2.containsKey(typeSource)){
					map2.put(typeSource, 0);
				}
				BigDecimal ddzje = order.getBigDecimal("ddzje");
				map2.put(typeSource, ddzje.add(new BigDecimal(map2.get(typeSource).toString())));
			}
			if(category.isEmpty()){
				category.add("0");
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("legend", new String[]{"订单量","总金额"});
				map.put("category", category.toArray());
				map.put("data1", new String[]{"0"});
				map.put("data2", new String[]{"0"});
				this.renderJson(map);
				return;
			}
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("legend", new String[]{"订单量","总金额"});
			map.put("category", category.toArray());
			
			String[] data1 = new String[category.size()];
			for(int i = 0;i < category.size();i++){
				data1[i] = map1.get(category.get(i)).toString();
			}
			map.put("data1", data1);
			String[] data2 = new String[category.size()];
			for(int i = 0;i < category.size();i++){
				data2[i] = map2.get(category.get(i)).toString();
			}
			map.put("data2", data2);
			this.renderJson(map);
		}
	}
	@Before(AccessAdminInterceptor.class)
	public void orderarea(){
		//查询当前管理员下的商圈信息
		SysUser u = (SysUser) this.getRequest().getSession().getAttribute("user");
		String user_id = u.getStr(SysUser.ID);
		String sql = "select * from ab_cityarea where id is not null";
		if (!StringUtils.isNullOrEmpty(user_id)	&& "103".equals(u.getStr(SysUser.ROLE_ID))) {
			sql += " and user_id= '" + user_id + "'";//区域管理员由此条件，超级管理员查询所有的商圈
		}
		sql += " and type_id = '1'";// 商业圈
		List<AbCityarea> list_cityarea = AbCityarea.dao.find(sql);
		String area_id = StringUtil.toStr(this.getPara("area_id"));
		if(StringUtils.isNullOrEmpty(area_id)){
			this.setAttr("list_cityarea", list_cityarea);
			this.render("/admin/order/orderareanums.html");
		}else{
			Map<String,Object> map = new HashMap<String,Object>();
			List<String> category = new ArrayList<String>();
			String[] data1 = new String[30];//订单量
			String[] data2 = new String[30];//接单人
			Calendar cd = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			for(int i = 0; i< 30; i++){
				if(i!=0){
					cd.set(Calendar.DAY_OF_MONTH,cd.get(Calendar.DAY_OF_MONTH) - 1);
				}
				category.add(sdf.format(cd.getTime()));
				String sqltemp;
				String sqlCarTemp;
				String sqlFmOrder;
				sqltemp = "select count(*) from ab_order a,sys_user b where a.xdrdh=b.mobile and a.xdsj like '" + sdf.format(cd.getTime()) + "%'";
				sqlCarTemp = "select count(*) from(select a.* from ab_sj_position a,ab_order b,sys_user c where a.sjid=b.qdrid and a.sjid=c.id";
				sqlFmOrder = "select count(*) from(select a.* from ab_fmcar_order a,sys_user b where a.create_id = b.id";
				if("103".equals(u.getStr(SysUser.ROLE_ID))){
					sqltemp += " and (b.city_id in (" + CityAttachAdminUtil.getCityByAdmin(user_id) + ") or b.area_id in (" + CityAttachAdminUtil.getCityByAdmin(user_id) + "))";
					sqlFmOrder += " and (b.city_id in (" + CityAttachAdminUtil.getCityByAdmin(user_id) + ") or b.area_id in ("+ CityAttachAdminUtil.getCityByAdmin(user_id) + "))";
					sqlCarTemp += " and (c.city_id in (" + CityAttachAdminUtil.getCityByAdmin(user_id) + ") or c.area_id in ("+ CityAttachAdminUtil.getCityByAdmin(user_id) + "))";
				}
				sqlCarTemp +=" and a.dqsj like'" + sdf.format(cd.getTime()) + "%' group by sjid) a";
				sqlFmOrder +=" and a.create_time like'" + sdf.format(cd.getTime()) + "%' group by create_id) a";
				data1[i] = Db.queryNumber(sqltemp).longValue() +  Db.queryNumber(sqlFmOrder).longValue() + "";
				data2[i] = Db.queryNumber(sqlCarTemp).toString();
			}
			map.put("category", category.toArray());
			map.put("data1", data1);
			map.put("data2", data2);
			map.put("legend", new String[]{"订单量","跑腿数量"});
			this.renderJson(map);
		}
	}
	
	/**
	 * 跳进熟车地图查看页面
	 */
	@Before(AccessAdminInterceptor.class)
	public void mapTrucks(){
		this.render("/admin/order/mapTrucks.html");
	}
	
	/**
	 * 查询具体的熟车
	 */
	@Before(AccessAdminInterceptor.class)
	public void searchMyFmCar(){
		String ismyfr = StringUtil.toStr(this.getPara("ismyfr"));
		String carType = StringUtil.toStr(this.getPara("carType"));
		String carLength = StringUtil.toStr(this.getPara("carLength"));
		String runcity = StringUtil.toStr(this.getPara("runcity"));
		String mobile = StringUtil.toStr(this.getPara("mobile"));
		String zt = StringUtil.toStr(this.getPara("carZt"));
		String[] lngMinMax = StringUtil.toStr(this.getPara("lngMinMax")).split(",");
		String[] latMinMax = StringUtil.toStr(this.getPara("latMinMax")).split(",");
		SysUser user = (SysUser) this.getSessionAttr("user");  //后台用户
		List<AbFmcar> carlist = new ArrayList<AbFmcar>();
		String sql = "select a.*,b.* from ab_fmcar as a,(select * from(select c.id as shrid,c.loginid mobile1,d.jd,d.wd,c.sjzt zt,d.dqsj as dqsj,c.rate as rate from sys_user c left join ab_sj_position d on c.id= d.sjid and c.role_id='106' where 1 = 1";
		
		if(user.getStr("id").length() > 0){
			sql += " and (c.city_id in (" + CityAttachAdminUtil.getCityByAdmin(user.getStr("id")) + ")";
			sql += " or c.area_id in (" + CityAttachAdminUtil.getCityByAdmin(user.getStr("id")) + "))";
		}
		if(mobile.length() != 11){
			if(lngMinMax.length > 1){
				sql += " and d.jd <=" + lngMinMax[1] + " and d.jd >=" + lngMinMax[0];
			}
			if(latMinMax.length > 1){
				sql += " and d.wd <=" + latMinMax[1] + " and d.wd >=" + latMinMax[0];
			}
		}
		sql += " ORDER BY dqsj DESC) u WHERE 1 =1  GROUP BY shrid ) b where a.mobile = b.mobile1";
		
		if(ismyfr.length() > 0){
			sql += " and a.id in (select car_id from ab_fmcar_user where user_id = '" + user.getStr("id") + "')";
		}
		if(carType.length() > 0){
			sql += " and a.type = '" + carType + "'";
		}
		if(carLength.length() > 0){
			sql += " and a.length = '" + carLength + "'";
		}
		if(runcity.length() > 0){
			String[] strArr = runcity.split(",");
			String msg = "";
			for(String str : strArr){
				msg += "'" + str + "',";
			}
			sql += " and a.id in (select car_id from ab_fmcar_city where city_name in (" + msg.substring(0, msg.length() - 1) +"))";
		}
		if(mobile.length() > 0){
			sql += " and a.mobile like'%" + mobile +"%'";
		}
		if(zt.length() > 0){
			if("2".equals(zt)){
				sql += " and b.zt = '" + zt + "'";
			}else{
				sql += " and b.zt != '2'";
			}
		}
		carlist = AbFmcar.dao.find(sql);
		if(null == carlist || carlist.isEmpty()){
			this.renderJson(new ArrayList());
		}else{
			PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1), carlist.size(),sql);
			this.renderJson(listpage.getList());
		}
	}

	
	/**
	 * 同城订单指定司机
	 */
	@Before(AccessAdminInterceptor.class)
	public void doZhiPaiTC(){
		String orderid = StringUtil.toStr(this.getPara("id"));
		String jdrmobile = StringUtil.toStr(this.getPara("mobile"));
		AbOrder order = AbOrder.dao.findById(orderid);
		SysUser user = SysUser.dao.findFirst("select * from sys_user where loginid=?", jdrmobile);
		AbFmcar car = AbFmcar.dao.findFirst("select * from ab_fmcar where mobile=?", jdrmobile);
		double total = Double.parseDouble(null==car.getStr("vv")? "0":car.getStr("vv"));
		AbTcExpressOrder ateo = AbTcExpressOrder.dao.findFirst("select * from ab_tc_express_order where sn = ?",order.getStr("sn"));
		String goods_volumn = StringUtil.toStr(ateo.getStr("goods_volumn"));
		goods_volumn = goods_volumn.length() == 0 ? "0" : goods_volumn;
		double usedvv = Double.parseDouble(goods_volumn);
		if(total < usedvv){
			this.renderJson(false);
		}else{
			order.set("qdrid", user.get(SysUser.ID));
			order.set("qdrname", user.get(SysUser.MC));
			order.set("ddzt", 3);
			order.update();
			// 发送短信给收货人，
			String yzm = RandomUtil.createRandomPwd(6);// 获取刘伟短信验证
			String content = "尊敬的用户,您好。您的订单已发货，您可凭借验证码【" + yzm
					+ "】进行收货确认，请务必要收到货物当面才告知送货人员，谢谢。";
			String contentPaoTui = "尊敬的司机朋友，您有新的货运订单，请注意查看，谢谢。订单详情：起始地："
					+ ateo.getStr("send_addr") + "到达地：" + ateo.getStr("rcv_addr1");
			contentPaoTui += "装货时间：" + ateo.getStr("send_time") + "联系电话：" +  ateo.getStr("rcv_phone1")
					+ "，出发前和货主联系确定，如不方便接单请联系客服（客服电话号码）取掉订单";
			try {
				if(SmsMessage.isSendSms("c5")){//下单成功：c5.订单发货：c6.司机接收订单：c7.送达提醒评价：c8.用户注册：c9
					if(!StringUtils.isNullOrEmpty(ateo.getStr("rcv_phone1"))){
						SmsMessage.SendMessage(ateo.getStr("rcv_phone1"), content);// 给收货人发短信
						insertOrderSms(order.getStr("id"),ateo.getStr("rcv_phone1"),yzm);
					}
					if(!StringUtils.isNullOrEmpty(ateo.getStr("rcv_phone2"))){
						SmsMessage.SendMessage(ateo.getStr("rcv_phone2"), content);// 给收货人发短信
						insertOrderSms(order.getStr("id"),ateo.getStr("rcv_phone2"),yzm);
					}
					if(!StringUtils.isNullOrEmpty(ateo.getStr("rcv_phone3"))){
						SmsMessage.SendMessage(ateo.getStr("rcv_phone3"), content);// 给收货人发短信
						insertOrderSms(order.getStr("id"),ateo.getStr("rcv_phone3"),yzm);
					}
					if(!StringUtils.isNullOrEmpty(ateo.getStr("rcv_phone4"))){
						SmsMessage.SendMessage(ateo.getStr("rcv_phone4"), content);// 给收货人发短信
						insertOrderSms(order.getStr("id"),ateo.getStr("rcv_phone4"),yzm);
					}
					if(!StringUtils.isNullOrEmpty(ateo.getStr("rcv_phone5"))){
						SmsMessage.SendMessage(ateo.getStr("rcv_phone5"), content);// 给收货人发短信
						insertOrderSms(order.getStr("id"),ateo.getStr("rcv_phone5"),yzm);
					}
				}
				if(SmsMessage.isSendSms("c6")){//下单成功：c5.订单发货：c6.司机接收订单：c7.送达提醒评价：c8.用户注册：c9
					SmsMessage.SendMessage(user.getStr("mobile"), contentPaoTui);// 给跑腿人发短信
				}
				this.renderJson(true);
			}catch(Exception e){
				this.renderJson(false);
			}
		}
		
	}
	
	@Before(AccessAdminInterceptor.class)
	public void initPrintPage(){
		this.render("/admin/order/initPrintPage.html");
	}
	@Before(AccessAdminInterceptor.class)
	public void doPrint(){
		String msg = StringUtil.toStr(this.getPara("msg"));
		if(PrintUtils.pmRequest()){
			this.renderJson(PrintUtils.sendRequest(msg));
		}else{
			this.renderJson(false);
		}
	}
	
	/**
	 * 进入多订单同时指派页面
	 */
	@Before(AccessAdminInterceptor.class)
	public void toMultiZhiPaiPage(){
		SysUser u = (SysUser) this.getRequest().getSession().getAttribute("user");
		String user_id = u.getStr(SysUser.ID);
		String sql = "select * from ab_cityarea where id is not null";
		if (!StringUtils.isNullOrEmpty(user_id)	&& "103".equals(u.getStr(SysUser.ROLE_ID))) {
			sql += " and user_id= '" + user_id + "'";//区域管理员由此条件，超级管理员查询所有的商圈
		}
		sql += " and type_id = '1'";// 商业圈
		List<AbCityarea> list_cityarea = AbCityarea.dao.find(sql);
		this.setAttr("list_cityarea", list_cityarea);
		//查询该管理员下的所有未指派订单
		String orderType = StringUtil.toStr(this.getPara("orderType"));
		String area_id = StringUtil.toStr(this.getPara("area_id"));
		String sql_order = "";
		if(orderType.equals("0")){//店铺订单
			sql_order = "select a.*,b.xxdz from ab_order a left join ab_merchant b on a.mid = b.id";
			sql_order += " where upper(left(a.sn,2)) != 'TC'";
			sql_order += " and a.ddzt = '2'";
//			sql_order += " and b.area_id in (select id from ab_cityarea where 1 = 1";
//			if("103".equals(u.getStr(SysUser.ROLE_ID))){
//				sql_order += " and user_id= '" + user_id + "'";
//			}
			if(area_id.length() > 0){
				sql_order += " and id = '" + area_id + "'";
			}
			if(!"admin".equals(u.getStr("id"))){
				sql_order += " and a.xdrid in( select id from sys_user where city_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + ")";
				sql_order += " or area_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + "))";
			}
//			sql_order += " and type_id = '1'";// 商业圈
//			sql_order += ")";
		}else{//同城物流订单
			orderType = "1";
			sql_order = "select b.*,a.id as orderid,a.ddzt as ddzt,a.shdz as shdz,a.ddzje as ddzje from ab_order a, ab_tc_express_order b where a.sn = b.sn and upper(left(a.sn,2)) = 'TC'";
			sql_order += " and a.ddzt = '1'";
			if(area_id.length() > 0){
				sql_order += " and a.area_id = '" + area_id + "'";
			}
			if(!"admin".equals(u.getStr("id"))){
				sql_order += " and a.xdrid in( select id from sys_user where city_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + ")";
				sql_order += " or area_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + "))";
			}
		}
//		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1), 10, sql_order);
		List<AbOrder> listpage = AbOrder.dao.find(sql_order);
		String flag = StringUtil.toStr(this.getPara("flag"));
		if(flag.length() != 0){
			this.renderJson(listpage);
			return;
		}
		this.setAttr("orderType", orderType);
		this.setAttr("area_id", area_id);
		this.setAttr("listpage", listpage);
		List<SysUserGroup> suglist = new ArrayList<SysUserGroup>();
		AbCityarea aca = AbCityarea.dao.findFirst("select * from ab_cityarea where user_id=? and ccm='1'",  u.getStr("id"));
		if(null!=aca){
			if(!"admin".equals(u.getStr("id"))){
				suglist = SysUserGroup.dao.find("select * from sys_user_group where cityid=?",aca.getStr("id"));
				this.setAttr("tb_city_id", aca.getStr("id"));
			}else{
				suglist = SysUserGroup.dao.find("select * from sys_user_group");
			}
		}else{
			if("admin".equals(u.getStr("id"))){
				suglist = SysUserGroup.dao.find("select * from sys_user_group");
			}
		}
		this.setAttr("suglist", suglist);
		this.setAttr("user", u);
		this.render("/admin/order/MultiZhiPaiPage.html");
	}
	
	/**
	 * 查询已选订单有联系的订单集合
	 */
	@Before(AccessAdminInterceptor.class)
	public void searchMultiOrder(){
		try{
			SysUser u = (SysUser) this.getRequest().getSession().getAttribute("user");
			String user_id = u.getStr(SysUser.ID);
			String orderid = StringUtil.toStr(this.getPara("orderid"));//基础订单id
			String orderType = StringUtil.toStr(this.getPara("orderType"));//0店铺  1同城
			String area_id = StringUtil.toStr(this.getPara("area_id"));//商圈
			String multiType = StringUtil.toStr(this.getPara("multiType"));//0 发货地  1 收货地
			String juli = StringUtil.toStr(this.getPara("juli"));//距离集中
			String time = StringUtil.toStr(this.getPara("time"));
			String xdsj = "";
			if(time.length() > 0){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Calendar cd = Calendar.getInstance();
				cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH), cd.get(Calendar.HOUR_OF_DAY), cd.get(Calendar.MINUTE) - Integer.parseInt(time), cd.get(Calendar.MILLISECOND));
				xdsj = sdf.format(cd.getTime());
			}
			if(juli.length() == 0){
				juli = "0.05";
			}
			String sql_order = "";
			if(orderType.length() == 0 || orderType.equals("0")){
				AbOrder order = AbOrder.dao.findById(orderid);
				if("0".equals(multiType)){
					BigDecimal jd_min = new BigDecimal(order.getStr("sjjd")).subtract(new BigDecimal(juli));
					BigDecimal jd_max = new BigDecimal(order.getStr("sjjd")).add(new BigDecimal(juli));
					BigDecimal jw_min = new BigDecimal(order.getStr("sjwd")).subtract(new BigDecimal(juli));
					BigDecimal jw_max = new BigDecimal(order.getStr("sjwd")).add(new BigDecimal(juli));
					sql_order = "select a.*,b.xxdz from ab_order a, ab_merchant b where a.mid = b.id";
					sql_order += " and upper(left(a.sn,2)) != 'TC'";
					sql_order += " and a.ddzt = '2'";
					sql_order += " and a.sjjd >= '" + jd_min + "'";
					sql_order += " and a.sjjd <= '" + jd_max + "'";
					sql_order += " and a.sjwd >= '" + jw_min + "'";
					sql_order += " and a.sjwd <= '" + jw_max + "'";
					if(xdsj.length() > 0){
						sql_order += " and a.xdsj >= '" + xdsj + "'";
					}
					sql_order += " and b.area_id in (select id from ab_cityarea where 1 = 1";
					if("103".equals(u.getStr(SysUser.ROLE_ID))){
						sql_order += " and user_id= '" + user_id + "'";
					}
					if(area_id.length() > 0){
						sql_order += " and id = '" + area_id + "'";
					}
					sql_order += " and type_id = '1'";// 商业圈
					sql_order += ")";
				}else if("1".equals(multiType)){
					BigDecimal jd_min = new BigDecimal(order.getStr("shdzjd")).subtract(new BigDecimal(juli));
					BigDecimal jd_max = new BigDecimal(order.getStr("shdzjd")).add(new BigDecimal(juli));
					BigDecimal jw_min = new BigDecimal(order.getStr("shdzwd")).subtract(new BigDecimal(juli));
					BigDecimal jw_max = new BigDecimal(order.getStr("shdzwd")).add(new BigDecimal(juli));
					sql_order = "select a.*,b.xxdz from ab_order a, ab_merchant b where a.mid = b.id";
					sql_order += " and upper(left(a.sn,2)) != 'TC'";
					sql_order += " and a.ddzt = '2'";
					sql_order += " and a.shdzjd >= '" + jd_min + "'";
					sql_order += " and a.shdzjd <= '" + jd_max + "'";
					sql_order += " and a.shdzwd >= '" + jw_min + "'";
					sql_order += " and a.shdzwd <= '" + jw_max + "'";
					if(xdsj.length() > 0){
						sql_order += " and a.xdsj >= '" + xdsj + "'";
					}
					sql_order += " and b.area_id in (select id from ab_cityarea where 1 = 1";
					if("103".equals(u.getStr(SysUser.ROLE_ID))){
						sql_order += " and user_id= '" + user_id + "'";
					}
					if(area_id.length() > 0){
						sql_order += " and id = '" + area_id + "'";
					}
					sql_order += " and type_id = '1'";// 商业圈
					sql_order += ")";
				}else{
					BigDecimal jd_min1 = new BigDecimal(order.getStr("sjjd")).subtract(new BigDecimal(juli));
					BigDecimal jd_max1 = new BigDecimal(order.getStr("sjjd")).add(new BigDecimal(juli));
					BigDecimal jw_min1 = new BigDecimal(order.getStr("sjwd")).subtract(new BigDecimal(juli));
					BigDecimal jw_max1 = new BigDecimal(order.getStr("sjwd")).add(new BigDecimal(juli));
					BigDecimal jd_min = new BigDecimal(order.getStr("shdzjd")).subtract(new BigDecimal(juli));
					BigDecimal jd_max = new BigDecimal(order.getStr("shdzjd")).add(new BigDecimal(juli));
					BigDecimal jw_min = new BigDecimal(order.getStr("shdzwd")).subtract(new BigDecimal(juli));
					BigDecimal jw_max = new BigDecimal(order.getStr("shdzwd")).add(new BigDecimal(juli));
					sql_order = "select a.*,b.xxdz from ab_order a, ab_merchant b where a.mid = b.id";
					sql_order += " and upper(left(a.sn,2)) != 'TC'";
					sql_order += " and a.ddzt = '2'";
					sql_order += " and a.shdzjd >= '" + jd_min + "'";
					sql_order += " and a.shdzjd <= '" + jd_max + "'";
					sql_order += " and a.shdzwd >= '" + jw_min + "'";
					sql_order += " and a.shdzwd <= '" + jw_max + "'";
					sql_order += " and a.sjjd >= '" + jd_min1 + "'";
					sql_order += " and a.sjjd <= '" + jd_max1+ "'";
					sql_order += " and a.sjwd >= '" + jw_min1 + "'";
					sql_order += " and a.sjwd <= '" + jw_max1 + "'";
					if(xdsj.length() > 0){
						sql_order += " and a.xdsj >= '" + xdsj + "'";
					}
					sql_order += " and b.area_id in (select id from ab_cityarea where 1 = 1";
					if("103".equals(u.getStr(SysUser.ROLE_ID))){
						sql_order += " and user_id= '" + user_id + "'";
					}
					if(area_id.length() > 0){
						sql_order += " and id = '" + area_id + "'";
					}
					sql_order += " and type_id = '1'";// 商业圈
					sql_order += ")";
				}
			}else{
				//AbOrder order = AbOrder.dao.findById(orderid);
				AbTcExpressOrder ateo = AbTcExpressOrder.dao.findById(orderid);
				if("0".equals(multiType)){
					BigDecimal jd_min = new BigDecimal(ateo.getStr("send_addr_jd")).subtract(new BigDecimal(juli));
					BigDecimal jd_max = new BigDecimal(ateo.getStr("send_addr_jd")).add(new BigDecimal(juli));
					BigDecimal jw_min = new BigDecimal(ateo.getStr("send_addr_wd")).subtract(new BigDecimal(juli));
					BigDecimal jw_max = new BigDecimal(ateo.getStr("send_addr_wd")).add(new BigDecimal(juli));
					sql_order = "select b.*,a.id as orderid,a.xdsj as xdsj,a.shdz as shdz,a.ddzje as ddzje from ab_order a, ab_tc_express_order b where a.sn = b.sn and upper(left(a.sn,2)) = 'TC'";
					sql_order += " and a.ddzt = '1'";
					if(area_id.length() > 0){
						sql_order += " and a.area_id = '" + area_id + "'";
					}
					if(xdsj.length() > 0){
						sql_order += " and a.xdsj >= '" + xdsj + "'";
					}
					sql_order += " and b.send_addr_jd >= '" + jd_min + "'";
					sql_order += " and b.send_addr_jd <= '" + jd_max + "'";
					sql_order += " and b.send_addr_wd >= '" + jw_min + "'";
					sql_order += " and b.send_addr_wd <= '" + jw_max + "'";
				}else if("1".equals(multiType)){
					BigDecimal jd_min = new BigDecimal(ateo.getStr("rcv_addr1_jd")).subtract(new BigDecimal(juli));
					BigDecimal jd_max = new BigDecimal(ateo.getStr("rcv_addr1_jd")).add(new BigDecimal(juli));
					BigDecimal jw_min = new BigDecimal(ateo.getStr("rcv_addr1_wd")).subtract(new BigDecimal(juli));
					BigDecimal jw_max = new BigDecimal(ateo.getStr("rcv_addr1_wd")).add(new BigDecimal(juli));
					sql_order = "select b.*,a.id as orderid,a.ddzt as ddzt,a.shdz as shdz,a.ddzje as ddzje from ab_order a, ab_tc_express_order b where a.sn = b.sn and upper(left(a.sn,2)) = 'TC'";
					sql_order += " and a.ddzt = '1'";
					if(area_id.length() > 0){
						sql_order += " and a.area_id = '" + area_id + "'";
					}
					if(xdsj.length() > 0){
						sql_order += " and a.xdsj >= '" + xdsj + "'";
					}
					sql_order += " and b.rcv_addr1_jd >= '" + jd_min + "'";
					sql_order += " and b.rcv_addr1_jd <= '" + jd_max + "'";
					sql_order += " and b.rcv_addr1_wd >= '" + jw_min + "'";
					sql_order += " and b.rcv_addr1_wd <= '" + jw_max + "'";
				}else{
					BigDecimal jd_min1 = new BigDecimal(ateo.getStr("send_addr_jd")).subtract(new BigDecimal(juli));
					BigDecimal jd_max1 = new BigDecimal(ateo.getStr("send_addr_jd")).add(new BigDecimal(juli));
					BigDecimal jw_min1 = new BigDecimal(ateo.getStr("send_addr_wd")).subtract(new BigDecimal(juli));
					BigDecimal jw_max1 = new BigDecimal(ateo.getStr("send_addr_wd")).add(new BigDecimal(juli));
					BigDecimal jd_min = new BigDecimal(ateo.getStr("rcv_addr1_jd")).subtract(new BigDecimal(juli));
					BigDecimal jd_max = new BigDecimal(ateo.getStr("rcv_addr1_jd")).add(new BigDecimal(juli));
					BigDecimal jw_min = new BigDecimal(ateo.getStr("rcv_addr1_wd")).subtract(new BigDecimal(juli));
					BigDecimal jw_max = new BigDecimal(ateo.getStr("rcv_addr1_wd")).add(new BigDecimal(juli));
					sql_order = "select b.*,a.id as orderid,a.ddzt as ddzt,a.shdz as shdz,a.ddzje as ddzje from ab_order a, ab_tc_express_order b where a.sn = b.sn and upper(left(a.sn,2)) = 'TC'";
					sql_order += " and a.ddzt = '1'";
					if(area_id.length() > 0){
						sql_order += " and a.area_id = '" + area_id + "'";
					}
					if(xdsj.length() > 0){
						sql_order += " and a.xdsj >= '" + xdsj + "'";
					}
					sql_order += " and b.rcv_addr1_jd >= '" + jd_min + "'";
					sql_order += " and b.rcv_addr1_jd <= '" + jd_max + "'";
					sql_order += " and b.rcv_addr1_wd >= '" + jw_min + "'";
					sql_order += " and b.rcv_addr1_wd <= '" + jw_max + "'";
					sql_order += " and b.send_addr_jd >= '" + jd_min1 + "'";
					sql_order += " and b.send_addr_jd <= '" + jd_max1 + "'";
					sql_order += " and b.send_addr_wd >= '" + jw_min1 + "'";
					sql_order += " and b.send_addr_wd <= '" + jw_max1 + "'";
				}
			}
			if(!"admin".equals(u.getStr("id"))){
				sql_order += " and a.xdrid in( select id from sys_user where city_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + ")";
				sql_order += " or area_id in(" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + ")" + ")";
			}
			List<AbOrder> order_list = AbOrder.dao.find(sql_order);
			this.renderJson(order_list);
		}catch(Exception e){
			this.renderJson(new ArrayList<AbOrder>());
		}
		
	}
	@Before(AccessAdminInterceptor.class)
	public void doZhiPaiMulti(){
		String sjid = StringUtil.toStr(this.getPara("sjid"));
		String[] selectidArr = this.getParaValues("selectid");
		String orderType = StringUtil.toStr(this.getPara("orderType"));
		if(sjid.length() > 0 && selectidArr.length > 0){
			SysUser sj = SysUser.dao.findById(sjid);
			if(null==sj){
				this.renderJson(false);
			}else{
				if(orderType.equals("0")){
					double usedvv = 0;
					for(String orderid : selectidArr){
						AbOrder order = AbOrder.dao.findById(orderid);
						AbTcExpressOrder ateo = AbTcExpressOrder.dao.findFirst("select * from ab_tc_express_order where sn = ?", order.getStr("sn"));
						String goods_volumn = StringUtil.toStr(ateo.getStr("goods_volumn"));
						if(goods_volumn.length()==0){goods_volumn="0";};
						usedvv += Double.parseDouble(goods_volumn);
					}
					AbFmcar car = AbFmcar.dao.findFirst("select * from ab_fmcar where mobile=?", sj.getStr("loginid"));
					double total = Double.parseDouble(car.getStr("vv"));
					if(total < usedvv){
						this.renderJson("vv");
					}
				}
				for(String orderid : selectidArr){
					AbOrder order = AbOrder.dao.findById(orderid);
					order.set("ddzt", 3);
					order.set("qdrid", sjid);
					order.set("qdrname", sj.getStr("mc"));
					order.set("jdrjd", sj.getStr("lng"));
					order.set("jdrwd", sj.getStr("lat"));
					order.update();
					String lxrdh = order.getStr("lxrdh");// 收货联系人电话
					// 发送短信给收货人，
					String yzm = RandomUtil.createRandomPwd(6);// 获取刘伟短信验证
					String content = "尊敬的用户,您好。您的订单已发货，您可凭借验证码【" + yzm
							+ "】进行收货确认，请务必要收到货物当面才告知送货人员，谢谢。";
					String contentPaoTui = "";
					if(orderType.equals("0")){
						AbMerchant mc = AbMerchant.dao.findById(order.getStr("mid"));
						contentPaoTui = "尊敬的司机朋友，您有新的货运订单，请注意查看，谢谢。订单详情：起始地："
								+ mc.getStr("xxdz") + "到达地：" + order.getStr("shdz");
					}else{
						AbTcExpressOrder ateo = AbTcExpressOrder.dao.findFirst("select * from ab_tc_express_order where sn = ?", order.getStr("sn"));
						contentPaoTui = "尊敬的司机朋友，您有新的货运订单，请注意查看，谢谢。订单详情：起始地："
								+ ateo.getStr("send_addr") + "到达地：" + order.getStr("shdz");
					}
					contentPaoTui += "装货时间：" + order.getStr("xdsj") + "联系电话：" + lxrdh
							+ "，出发前和货主联系确定，如不方便接单请联系客服（10086）取掉订单";
					try {
						if(SmsMessage.isSendSms("c5")){//下单成功：c5.订单发货：c6.司机接收订单：c7.送达提醒评价：c8.用户注册：c9
							SmsMessage.SendMessage(lxrdh, content);// 给收货人发短信
						}
						if(SmsMessage.isSendSms("c6")){//下单成功：c5.订单发货：c6.司机接收订单：c7.送达提醒评价：c8.用户注册：c9
							SmsMessage.SendMessage(sj.getStr("mobile"), contentPaoTui);// 给跑腿人发短信
						}
						insertOrderSms(order.getStr("id"),lxrdh,yzm);
					}catch(Exception e){
						
					}
				}
				this.renderJson(true);
			}
		}else{
			this.renderJson(false);
		}
	}
	@Before(AccessAdminInterceptor.class)
	public void getAllAdmin(){
		SysUser u = this.getSessionAttr("user");
		List<SysUser> admin_list = SysUser.dao.find("select * from sys_user where role_id in ('101','103')");
		if("103".equals(u.get("role_id"))){//区域管理员
			admin_list.removeAll(admin_list);
			admin_list.add(u);
			this.setAttr("admin_list", admin_list);
		}else if("104".equals(u.getStr("role_id"))){//操作工
			admin_list.removeAll(admin_list);
			this.setAttr("admin_list", admin_list);
		}else{//超级管理员
			this.setAttr("admin_list", admin_list);
		}
		//查詢用戶類型
		this.setAttr("subject_list", AbSubject.dao.find("select * from ab_subject where p_id in ('1017','1018','1019','1020')"));
	}
	
	@Before(AccessAdminInterceptor.class)
	public void fmcarorder(){
		getAllAdmin();
		String tab = StringUtil.toStr(this.getPara("tab"));
		if(tab.length() == 0){
			tab = "t0";
		}
		this.setAttr("tab", tab);
		String sql = "select a.*,b.mc as create_mc,b.loginid as create_mobile from ab_fmcar_order a ,sys_user b where a.create_id = b.id";
		//起始地址
		String startaddr = StringUtil.toStr(this.getPara("startaddr"));
		if(startaddr.length() > 0){
			sql += " and a.start_addr like '%" + startaddr + "%'";
		}
		this.setAttr("startaddr", startaddr);
		String arraddr = StringUtil.toStr(this.getPara("arraddr"));
		if(arraddr.length() > 0){
			sql += " and a.arr_addr like '%" + arraddr + "%'";
		}
		this.setAttr("arraddr", arraddr);
		String car_type = StringUtil.toStr(this.getPara("car_type"));
		if(car_type.length() > 0){
			sql += " and a.car_type ='" + car_type + "'";
		}
		this.setAttr("car_type", car_type);
		String car_length = StringUtil.toStr(this.getPara("car_length"));
		if(car_length.length() > 0){
			sql += " and a.car_length ='" + car_length + "'";
		}
		this.setAttr("car_length", car_length);
		String xdrmc = StringUtil.toStr(this.getPara("xdrmc"));
		if(xdrmc.length() > 0){
			sql += " and b.mc like '%" + xdrmc + "%'";
		}
		this.setAttr("xdrmc", xdrmc);
		String mobile = StringUtil.toStr(this.getPara("mobile"));
		if(mobile.length() > 0){
			sql += " and a.loginid like '%" + mobile + "%'";
		}
		this.setAttr("mobile", mobile);
		String status = StringUtil.toStr(this.getPara("status"));
		if(status.length() > 0){
			sql += " and a.status ='" + status + "'";
		}
		this.setAttr("status", status);
		SysUser u = this.getSessionAttr("user");
		String admin_id = StringUtil.toStr(this.getPara("admin_id"));
		admin_id = admin_id.length() == 0 ? u.getStr("id") : admin_id;
		if(admin_id.length() > 0){
			sql += " and (b.city_id in (" + CityAttachAdminUtil.getCityByAdmin(admin_id) + ")";
			sql += " or b.area_id in (" + CityAttachAdminUtil.getCityByAdmin(admin_id) + "))";
		}
		this.setAttr("admin_id", admin_id);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String starttime  = "";
		String endtime  = "";
		if ("t0".equals(tab)) {
			Calendar cd = Calendar.getInstance();
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
			starttime = sdf.format(cd.getTime());
			sql += " and a.create_time >= '" + starttime + "'";
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
			endtime = sdf.format(cd.getTime());
			sql += " and a.create_time <= '" + endtime + "'";
		} else if ("t1".equals(tab)) {
			Calendar cd = Calendar.getInstance();
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH) - 1, 0, 0, 0);
			starttime = sdf.format(cd.getTime());
			sql += " and a.create_time >= '" + starttime + "'";
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
			endtime = sdf.format(cd.getTime());
			sql += " and a.create_time <= '" + endtime + "'";
		} else if ("t2".equals(tab)) {
			Calendar cd = Calendar.getInstance();
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH) - 2, 0, 0, 0);
			starttime = sdf.format(cd.getTime());
			sql += " and a.create_time >= '" + starttime + "'";
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
			endtime = sdf.format(cd.getTime());
			sql += " and a.create_time <= '" + endtime + "'";
		} else if ("t3".equals(tab)) {
			Calendar cd = Calendar.getInstance();
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH) - 6, 0, 0, 0);
			starttime = sdf.format(cd.getTime());
			sql += " and a.create_time >= '" + starttime + "'";
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),cd.get(Calendar.DAY_OF_MONTH) + 6, 23, 59, 59);
			endtime = sdf.format(cd.getTime());
			sql += " and a.create_time <= '" + endtime + "'";
		} else if ("t4".equals(tab)) {
			Calendar cd = Calendar.getInstance();
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH), 1, 0, 0, 0);
			starttime = sdf.format(cd.getTime());
			sql += " and a.create_time >= '" + starttime + "'";
		} else if ("t5".equals(tab)) {
			Calendar cd = Calendar.getInstance();
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH) - 1, 1, 0, 0,0);
			starttime = sdf.format(cd.getTime());
			sql += " and a.create_time >= '" + starttime + "'";
			cd.set(cd.get(Calendar.YEAR), cd.get(Calendar.MONTH) + 1, 0, 23,59, 59);
			endtime = sdf.format(cd.getTime());
			sql += " and a.create_time <= '" + endtime + "'";
		} else {
			// 所有订单
		}
		sql += " order by a.create_time desc";
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1), 10,sql);
		this.setAttr("listpage", listpage);
		this.setAttr("totalcount", Db.queryLong("select count(1) as totalcount from (" +sql + ") a"));
		//导出
		String flagExport = StringUtil.toStr(this.getPara("flagExport"));
		if(flagExport.length() > 0){
			String name = "订单明细.xls";
			String path = this.getClass().getResource("/").toString().replace("WEB-INF/classes/", "").replace("file:/", "");
			boolean flag = ExportExcelUtil.doExportToExcel(path + "admin/order/" + name, sql, this.getParaToInt("curPage", 1), starttime, endtime,"ab_fmcar_order");
			if(flag){
				this.renderFile("/admin/order/"  + name);
				return;
			}else{
				//不执行任何操作
			}
		}
		this.render("/admin/order/fmcarorder.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void initSysUserGroupPage(){
		SysUser user = (SysUser)this.getSessionAttr("user");
		String groupname = StringUtil.toStr(this.getPara("groupname"));
		String tb_city_id = StringUtil.toStr(this.getPara("tb_city_id"));
		if(!"admin".equals(user.getStr("id"))){
			AbCityarea aca = AbCityarea.dao.findFirst("select * from ab_cityarea where user_id = ? and id > 1000", user.getStr("id"));
			if(null == aca){
				tb_city_id = "xxxxxxxxxx";//级超级管理员没有设置该区域管理员所述地方，
			}else{
				tb_city_id = aca.getStr("id");
			}
		}
		String sql = "select a.id id,a.groupname groupname,b.mc cityid from sys_user_group a,ab_cityarea b where a.cityid = b.id";
		if(groupname.length() > 0){
			sql += " and a.groupname like '%" + groupname + "%'";
		}
		if(tb_city_id.length() > 0){
			sql += " and a.cityid= '" + tb_city_id + "'";
		}
		List<SysUserGroup> grouplist = SysUserGroup.dao.find(sql);
		this.setAttr("grouplist", grouplist);
		this.setAttr("groupname", groupname);
		this.setAttr("city_id", tb_city_id);
		this.setAttr("user", user);
		this.render("/admin/order/initSysUserGroupPage.html");
	}
	
	/**
	 * 删除一个组别
	 */
	@Before(AccessAdminInterceptor.class)
	public void delGroup(){
		String id = StringUtil.toStr(this.getPara("id"));
		try{
			SysUserGroup sug = SysUserGroup.dao.findById(id);
			if(null!=sug){
				//该群组存在，判断该分组是否可以删除。即司机没有引用即可
				List<SysUser> userList = SysUser.dao.find("select * from sys_user where groupid = ?", id);
				if(null == userList || userList.isEmpty()){
					sug.delete();
					this.renderJson(true);
				}else{
					this.renderJson(false);
				}
			}else{
				this.renderJson(false);
			}
		}catch(Exception e){
			this.renderJson(false);
		}
	}
	
	/**
	 * 添加修改一个组别
	 */
	@Before(AccessAdminInterceptor.class)
	public void initAddUpdatePage(){
		SysUser user = (SysUser)this.getSessionAttr("user");
		String id = StringUtil.toStr(this.getPara("id"));
		SysUserGroup sug;
		if(id.length() > 0){
			sug = SysUserGroup.dao.findById(id);
			if(null == sug){
				sug = new SysUserGroup();
				AbCityarea aca = AbCityarea.dao.findFirst("select * from ab_cityarea where user_id=? and id > 1000", user.getStr("id"));
				if(null!=aca){
					sug.set("cityid", aca.getStr("id"));
				}
			}
		}else{
			sug = new SysUserGroup();
			AbCityarea aca = AbCityarea.dao.findFirst("select * from ab_cityarea where user_id=? and id > 1000", user.getStr("id"));
			if(null!=aca){
				sug.set("cityid", aca.getStr("id"));
			}
		}
		this.setAttr("sug", sug);
		this.setAttr("user", user);
		this.render("/admin/order/initAddUpdatePage.html");
	}
	@Before(AccessAdminInterceptor.class)
	public void getGroupByCityId(){
		String cityid = StringUtil.toStr(this.getPara("cityid"));
		String sql = " select a.* from sys_user_group a";
		if(cityid.length() > 0){
			sql += " where a.cityid = '" + cityid + "'"; 
		}
		this.renderJson(SysUserGroup.dao.find(sql));
	}
	
	/**
	 * 添加修改一个组别
	 */
	@Before(AccessAdminInterceptor.class)
	public void addOrUpdateGroup(){
		String id = StringUtil.toStr(this.getPara("id"));
		String groupname = StringUtil.toStr(this.getPara("groupname"));
		String tb_city_id = StringUtil.toStr(this.getPara("tb_city_id"));
		SysUserGroup temp = SysUserGroup.dao.findFirst("select * from sys_user_group where id != ? and groupname=? and cityid=?", id,groupname,tb_city_id);
		if(null!=temp){
			this.renderJson(false);
			return;
		}
		try{
			if(id.length() > 0){//修改一个组别
				SysUserGroup sug = SysUserGroup.dao.findById(id);
				if(null == sug){//不存在的话新增一个
					sug = new SysUserGroup();
					sug.set("id", StringUtil.getUuid32());
					sug.set("groupname", groupname);
					sug.set("cityid", tb_city_id);
					sug.save();
					this.renderJson(true);
				}else{//确实存在就修改组名
					sug.set("groupname", groupname);
					sug.set("cityid", tb_city_id);
					sug.update();
					this.renderJson(true);
				}
			}else{//新增一个组别
				SysUserGroup sug = new SysUserGroup();
				sug.set("id", StringUtil.getUuid32());
				sug.set("groupname", groupname);
				sug.set("cityid", tb_city_id);
				sug.save();
				this.renderJson(true);
			}
		}catch(Exception e){
			this.renderJson(false);//出现异常的情况，返回前台提示
		}
	}
	
	@Before(AccessAdminInterceptor.class)
	public void searchDriver(){
		String id = StringUtil.toStr(this.getPara("id"));
		AbFmcar c = AbFmcar.dao.findById(id);
		SysUser u  = SysUser.dao.findFirst("select * from sys_user where loginid='" + c.getStr("mobile") + "'");
		AbSjPosition s = AbSjPosition.dao.findFirst("select * from ab_sj_position where sjid='" + u.getStr("id") + "'");
		List<AbOrder> oh = AbOrder.dao.find("select * from ab_order where qdrid='" + u.getStr("id") + "' and ddzt > '3' order by qdsj limit 0,10");
		List<AbOrder> od = AbOrder.dao.find("select * from ab_order where qdrid='" + u.getStr("id") + "' and ddzt='3'");
		double totalUsed = 0;
		for(AbOrder temp : od){
			if(temp.getStr("sn").startsWith("TC")){
				String goods_volumn = Db.queryStr("select goods_volumn from ab_tc_express_order where sn=?", temp.getStr("sn"));
				if(StringUtils.isNullOrEmpty(goods_volumn)){
					goods_volumn = "0";
				}
				totalUsed += Double.parseDouble(goods_volumn);
			}
		}
		double vv = Double.parseDouble(c.getStr("vv"));
		this.setAttr("totalUnUsed",  vv - totalUsed);
		this.setAttr("c", c);
		this.setAttr("u", u);
		this.setAttr("s", s);
		this.setAttr("oh", oh);
		this.setAttr("od", od);
		this.render("/admin/order/driverAndOrders.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void searchOrderNearByDriver(){
		SysUser user = (SysUser)this.getSessionAttr("user");
		String juli = StringUtil.toStr(this.getPara("juli"));
		String sjjd = StringUtil.toStr(this.getPara("sjjd"));
		String sjwd = StringUtil.toStr(this.getPara("sjwd"));
		BigDecimal jd_min = new BigDecimal(sjjd).subtract(new BigDecimal(juli));
		BigDecimal jd_max = new BigDecimal(sjjd).add(new BigDecimal(juli));
		BigDecimal jw_min = new BigDecimal(sjwd).subtract(new BigDecimal(juli));
		BigDecimal jw_max = new BigDecimal(sjwd).add(new BigDecimal(juli));
		String sql = "select a.*,b.lng lng,b.lat lat,'0' as memo from ab_order a, ab_merchant b where a.mid = b.id";
		sql += " and upper(left(a.sn,2)) != 'TC'";
		sql += " and a.ddzt = '2'";
		sql += " and a.sjjd >= " + jd_min;
		sql += " and a.sjjd <= " + jd_max;
		sql += " and a.sjwd >= " + jw_min;
		sql += " and a.sjwd <= " + jw_max;
		sql += " and a.xdrid in ( select id from sys_user ";
		sql += " where (city_id in (" + CityAttachAdminUtil.getCityByAdmin(user.getStr("id")) + ")";
		sql += " or area_id in (" + CityAttachAdminUtil.getCityByAdmin(user.getStr("id")) + "))";
		sql += ")";
		sql += " union ";
		sql += " select a.*,b.send_addr_jd lng,b.send_addr_wd,b.goods_volumn memo from ab_order a, ab_tc_express_order b where a.sn = b.sn and upper(left(a.sn,2)) = 'TC'";
		sql += " and a.ddzt = '1'";
		sql += " and b.send_addr_jd >= " + jd_min;
		sql += " and b.send_addr_jd <= " + jd_max;
		sql += " and b.send_addr_wd >= " + jw_min;
		sql += " and b.send_addr_wd <= " + jw_max;
		sql += " and a.xdrid in ( select id from sys_user ";
		sql += " where (city_id in (" + CityAttachAdminUtil.getCityByAdmin(user.getStr("id")) + ")";
		sql += " or area_id in (" + CityAttachAdminUtil.getCityByAdmin(user.getStr("id")) + "))";
		sql += ")";
		this.renderJson(AbOrder.dao.find(sql));
		
	}
	@Before(AccessAdminInterceptor.class)
	public void doNearByZhiPai(){
		String sjid = StringUtil.toStr(this.getPara("sjid"));
		String[] selectidArr = this.getParaValues("selectid");
		double unused =Double.parseDouble(StringUtil.toStr( this.getPara("unused")));
		if(sjid.length() > 0 && selectidArr.length > 0){
			SysUser sj = SysUser.dao.findById(sjid);
			if(null==sj){
				this.renderJson(false);
			}else{
				double totalUsed = 0;
				for(String orderid : selectidArr){
					AbOrder order = AbOrder.dao.findById(orderid);
					if(order.getStr("sn").startsWith("sn")){
						String goods_volumn = Db.queryStr("select goods_volumn from ab_tc_express_order where sn=?", order.getStr("sn"));
						if(StringUtils.isNullOrEmpty(goods_volumn)){
							goods_volumn = "0";
						}
						totalUsed += Double.parseDouble(goods_volumn);
					}
				}
				if(unused < totalUsed){
					this.renderJson("vv");
					return;
				}
				for(String orderid : selectidArr){
					AbOrder order = AbOrder.dao.findById(orderid);
					order.set("ddzt", 3);
					order.set("qdrid", sjid);
					order.set("qdrname", sj.getStr("mc"));
					order.set("jdrjd", sj.getStr("lng"));
					order.set("jdrwd", sj.getStr("lat"));
					order.update();
					String lxrdh = order.getStr("lxrdh");// 收货联系人电话
					// 发送短信给收货人，
					String yzm = RandomUtil.createRandomPwd(6);// 获取刘伟短信验证
					String content = "尊敬的用户,您好。您的订单已发货，您可凭借验证码【" + yzm
							+ "】进行收货确认，请务必要收到货物当面才告知送货人员，谢谢。";
					String contentPaoTui = "";
					if(!order.getStr("sn").startsWith("TC")){
						AbMerchant mc = AbMerchant.dao.findById(order.getStr("mid"));
						contentPaoTui = "尊敬的司机朋友，您有新的货运订单，请注意查看，谢谢。订单详情：起始地："
								+ mc.getStr("xxdz") + "到达地：" + order.getStr("shdz");
					}else{
						AbTcExpressOrder ateo = AbTcExpressOrder.dao.findFirst("select * from ab_tc_express_order where sn = ?", order.getStr("sn"));
						contentPaoTui = "尊敬的司机朋友，您有新的货运订单，请注意查看，谢谢。订单详情：起始地："
								+ ateo.getStr("send_addr") + "到达地：" + order.getStr("shdz");
					}
					contentPaoTui += "装货时间：" + order.getStr("xdsj") + "联系电话：" + lxrdh
							+ "，出发前和货主联系确定，如不方便接单请联系客服（10086）取掉订单";
					try {
						if(SmsMessage.isSendSms("c5")){//下单成功：c5.订单发货：c6.司机接收订单：c7.送达提醒评价：c8.用户注册：c9
							SmsMessage.SendMessage(lxrdh, content);// 给收货人发短信
						}
						if(SmsMessage.isSendSms("c6")){//下单成功：c5.订单发货：c6.司机接收订单：c7.送达提醒评价：c8.用户注册：c9
							SmsMessage.SendMessage(sj.getStr("mobile"), contentPaoTui);// 给跑腿人发短信
						}
						insertOrderSms(order.getStr("id"),lxrdh,yzm);
					}catch(Exception e){
						
					}
				}
				this.renderJson(true);
			}
		}else{
			this.renderJson(false);
		}
	}
}
