package com.zp.controller.rest;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.xvolks.jnative.misc.basicStructures.DOUBLE;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.sun.corba.se.impl.encoding.TypeCodeOutputStream;
import com.zp.controller.ab.AbTcContrller;
import com.zp.entity.AbDriverPosition;
import com.zp.entity.AbFmcar;
import com.zp.entity.AbFmcarOrder;
import com.zp.entity.AbListenOrder;
import com.zp.entity.AbOrder;
import com.zp.entity.AbOrderItemchargeoff;
import com.zp.entity.AbOrderPosition;
import com.zp.entity.AbOrderQuote;
import com.zp.entity.AbOrderSms;
import com.zp.entity.AbOrderStateImg;
import com.zp.entity.AbSjPosition;
import com.zp.entity.AbTcExpressOrder;
import com.zp.entity.AbTcExpressOrderBaojia;
import com.zp.entity.SysUser;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.tools.AutoQuoteTimer;
import com.zp.tools.DateUtil;
import com.zp.tools.RandomUtil;
import com.zp.tools.SmsMessage;
import com.zp.tools.StringUtil;

public class RestOrderController extends AbstractRestController {
	public static final String REG_CONTENT = "尊敬的司机朋友，您有新的货运订单，请注意查看，谢谢。订单详情：";

	/**
	 * 下单给熟车接口
	 */
	public void addCarOrder() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			String createId = getPara("uid");// 用户id
			String mobile = getPara("mobile");// 联系手机
			String length = getPara("carLength");// 车长
			String type = getPara("carType");// 车型
			String carId = getPara("carId");//
			String startProvinceName = getPara("startProvinceName");// 出发地 省
			String startCityName = getPara("startCityName");// 出发地 市
			String startCountyName = getPara("startCountyName");// 出发地 区
			String startAddr = getPara("startAddr");// 出发地 详细地址
			String arrProvinceName = getPara("arrProvinceName");// 目的地 省
			String arrCityName = getPara("arrCityName");// 目的地 市
			String arrCountyName = getPara("arrCountyName");// 目的地 区
			String arrAddr = getPara("arrAddr");// 目的地 详细地址
			String remark = getPara("remark");// 目的地 区
			String createTime = getPara("createTime");// 发货时间
			if (StringUtil.isNull(createId) || StringUtil.isNull(mobile)
					|| StringUtil.isNull(length) || StringUtil.isNull(carId)
					|| StringUtil.isNull(arrAddr)
					|| StringUtil.isNull(startAddr)) {
				// 检查参数是否合法
				resultMap.put("result", RESULT_FAIL);
				resultMap.put("msg", RESULT_INVALID_PARAM);
			} else {

				AbFmcarOrder order = new AbFmcarOrder();
				order.set(AbFmcarOrder.ID, StringUtil.getRandString32());
				order.set(AbFmcarOrder.CAR_TYPE, type);
				order.set(AbFmcarOrder.CAR_ID, carId);
				order.set(AbFmcarOrder.START_PROVINCE_NAME, startProvinceName);
				order.set(AbFmcarOrder.START_CITY_NAME, startCityName);
				order.set(AbFmcarOrder.START_COUNTY_NAME, startCountyName);
				order.set(AbFmcarOrder.START_ADDR, startAddr);
				order.set(AbFmcarOrder.ARR_PROVINCE_NAME, arrProvinceName);
				order.set(AbFmcarOrder.ARR_CITY_NAME, arrCityName);
				order.set(AbFmcarOrder.ARR_COUNTY_NAME, arrCountyName);
				order.set(AbFmcarOrder.ARR_ADDR, arrAddr);
				order.set(AbFmcarOrder.REMARK, remark);
				order.set(AbFmcarOrder.CREATE_TIME, createTime);
				order.set(AbFmcarOrder.LOAD_TIME, createTime);
				order.set(AbFmcarOrder.CAR_LENGTH, length);
				order.set(AbFmcarOrder.CREATE_ID, createId);
				order.set(AbFmcarOrder.MOBILE, mobile);
				order.save();
				StringBuilder sb = new StringBuilder(REG_CONTENT);
				sb.append("起始地：");
				if (!StringUtil.isNull(order.getStr("start_province_name"))) {
					sb.append(order.getStr("start_province_name"));
				}
				if (!StringUtil.isNull(order.getStr("start_city_name"))) {
					sb.append(order.getStr("start_city_name"));
				}
				if (!StringUtil.isNull(order.getStr("start_county_name"))) {
					sb.append(order.getStr("start_county_name"));
				}
				if (!StringUtil.isNull(order.getStr("start_addr"))) {
					sb.append(order.getStr("start_addr"));
				}
				sb.append("，到达地：");
				if (!StringUtil.isNull(order.getStr("arr_province_name"))) {
					sb.append(order.getStr("arr_province_name"));
				}
				if (!StringUtil.isNull(order.getStr("arr_city_name"))) {
					sb.append(order.getStr("arr_city_name"));
				}
				if (!StringUtil.isNull(order.getStr("arr_county_name"))) {
					sb.append(order.getStr("arr_county_name"));
				}
				if (!StringUtil.isNull(order.getStr("arr_addr"))) {
					sb.append(order.getStr("arr_addr"));
				}
				sb.append("，装货时间：" + createTime);
				sb.append("，联系电话：" + order.getStr("mobile"));
				sb.append("，请先和货主确定");
				SmsMessage.SendMessage(mobile, sb.toString());
				String yzm = RandomUtil.createRandomPwd(6);
				AbOrderSms sms = new AbOrderSms();
				sms.set("id", StringUtil.getUuid32());
				sms.set("orderid", order.getStr(AbFmcarOrder.ID));
				sms.set("shrdh", order.getStr("mobile"));
				sms.set("yzm", yzm);
				sms.set("createtime", DateUtil.getCurrentDate());
				sms.set("timelong", 15 * 24 * 60 * 60 * 1000);
				sms.save();
				SmsMessage.SendMessage(order.getStr("mobile"), yzm);
				resultMap.put("result", RESULT_SUCCESS);
				resultMap.put("msg", "下单成功");
			}
		} catch (Exception e) {
			formatInvokeFailResponse(e, resultMap);
		}
		renderJson(resultMap);
	}

	public void getMyOrerList() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			// 获取分页信息
			String uid = this.getPara("uid");
			Integer pi = this.getParaToInt("pi");
			Integer ps = this.getParaToInt("ps");
			if (pi == null || ps == null || uid == null || pi < 1 || ps < 1) {
				formatInvalidParamResponse(resultMap);
			} else {
				StringBuilder orderBuf = new StringBuilder();
				orderBuf
						.append(" SELECT a.istrue, a.id, a.xdrdh, a.xdsj, a.sjjd,a.sjwd,b.min_price,b.max_price, b.send_time,b.send_addr,b.style,b.rcv_addr1,b.total_price, ");
				orderBuf
						.append(" a.city_name,a.ddzje,a.sn,a.is_type,a.mapbusiness,a.zffs, a.shdz,lxrdh,a.shdzjd,a.shdzwd,");
				orderBuf
						.append(" b.rcv_addr2,b.rcv_addr3,b.rcv_addr4,b.rcv_addr5,b.car,");
				orderBuf
						.append(" b.people,b.huidan_price,b.pay_type,b.weight,b.send_addr_jd,b.send_addr_wd,");
				orderBuf
						.append(" b.goods_type,b.lift ,a.ddzt, a.xdrid,b.goods_mount ,b.goods_volumn, b.kilo,b.pay_type,");
				orderBuf
						.append(" b.rcv_addr3_jd, b.rcv_addr2_wd, b.rcv_addr2_jd, b.rcv_addr1_wd, b.rcv_addr1_jd");
				orderBuf
						.append(" ,b.rcv_addr5_wd, b.rcv_addr5_jd, b.rcv_addr4_wd, b.rcv_addr4_jd, b.rcv_addr3_wd,b.rcv_phone5,b.rcv_phone4,b.rcv_phone3,b.rcv_phone2,b.rcv_phone1 ");
				orderBuf
						.append(" from ab_order a LEFT JOIN ab_tc_express_order b ON a.sn = b.sn where a.ddzt!=1  AND a.qdrid=? ORDER BY b.send_time DESC ");
				List<AbTcExpressOrder> orderList = AbTcExpressOrder.dao.find(
						orderBuf.toString(), uid);
				resultMap.put("data", orderList);
				resultMap.put("result", RESULT_SUCCESS);
			}
		} catch (Exception e) {
			formatInvokeFailResponse(e, resultMap);
		}
		renderJson(resultMap);
	}

	/**
	 * http://127.0.0.1/ash/rest/order/getOrderList?pi=1&ps=10&type=2&lng=
	 * 113.257807&lat=22.677382 获取订单列表（只获取服务类）
	 */
	public void getOrderList() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			// 获取分页信息
			Integer pi = this.getParaToInt("pi");
			Integer ps = this.getParaToInt("ps");
			// 0-全部，1-悬赏，2-招标，3-雇佣
			Integer type = this.getParaToInt("type");
			String lng = this.getPara("lng");
			String lat = this.getPara("lat");
			if (pi == null || ps == null || type == null || pi < 1 || ps < 1
					|| type < 0 || type > 3) {
				formatInvalidParamResponse(resultMap);
			} else {
				String uid = this.getPara("uid");
				//获取当前用户的听单城市
				StringBuffer sb = new StringBuffer();
				if (StringUtils.isNotEmpty(uid)) {
					
					List<AbListenOrder> ls = AbListenOrder.dao.find("select * from ab_listen_city where user_id=?",uid);
					if(ls!=null && ls.size()>0){
						
						for(AbListenOrder alo : ls){
							if(sb.length()>1){
								sb.append(",");
							}
							sb.append("'").append(alo.getStr("city_name")).append("'");
						}
						
					}
				}
				
				StringBuilder orderBuf = new StringBuilder();
				orderBuf
						.append("SELECT a.istrue, a.id, a.xdrdh, a.xdsj, a.sjjd,a.sjwd,b.min_price,b.max_price,b.send_time,b.send_addr,b.style,b.rcv_addr1,b.total_price, ");
				orderBuf
						.append(" a.city_name,a.ddzje,a.sn,a.is_type,a.mapbusiness,a.zffs, a.shdz,lxrdh,a.shdzjd,a.shdzwd,");
				orderBuf
						.append(" b.rcv_addr2,b.rcv_addr3,b.rcv_addr4,b.rcv_addr5,b.car,");
				orderBuf
						.append(" b.people,b.huidan_price,b.pay_type,b.weight,b.send_addr_jd,b.send_addr_wd,");
				orderBuf
						.append(" b.goods_type,b.lift ,a.ddzt, a.xdrid,b.goods_mount ,b.goods_volumn, b.kilo,b.pay_type,  ");
				orderBuf
						.append(" b.rcv_addr3_jd, b.rcv_addr2_wd, b.rcv_addr2_jd, b.rcv_addr1_wd, b.rcv_addr1_jd");
				orderBuf
						.append(" ,b.rcv_addr5_wd, b.rcv_addr5_jd, b.rcv_addr4_wd, b.rcv_addr4_jd, b.rcv_addr3_wd,b.rcv_phone5,b.rcv_phone4,b.rcv_phone3,b.rcv_phone2,b.rcv_phone1,b.remark  ");
				
				orderBuf.append(" from (SELECT * FROM ab_order  WHERE ddzt=1 AND (psfs!=0 or psfs is null )");
								
								
				if(sb.length()>0){
					orderBuf.append(" and ( city_name in("+sb.toString()).append(") or qdrid='"+uid+"' ) and way=1");	
				}
				
				orderBuf.append(")  AS a LEFT JOIN ");//
				
				// if(StringUtil.isNull(lng) && StringUtil.isNull(lat)) {
				orderBuf.append(" ab_tc_express_order ");//
				// } else {
				// orderBuf.append("(SELECT * FROM (");//
				// orderBuf.append(" SELECT *, ROUND(6378.138*2*ASIN(SQRT(POW(SIN( ("+lat+"*PI()/180-send_addr_wd*PI()/180)/2),2)");//
				// orderBuf.append(" +COS("+lat+"*PI()/180)*COS(send_addr_wd*PI()/180)* ");//
				// orderBuf.append(" POW(SIN( ("+lng+" *PI()/180-send_addr_jd*PI()/180)/2),2)))*1000) AS juli FROM   ab_tc_express_order ");//
				// orderBuf.append(" WHERE send_addr_jd >0) AS tt WHERE   juli < 100000 )");//
				// }
				orderBuf
						.append(" AS b ON a.sn = b.sn WHERE a.ddzt=1 and b.send_addr IS NOT NULL");//
				orderBuf.append(" ORDER BY b.send_time DESC  LIMIT ?,? ");
				System.out.println(orderBuf);
			
				List<AbTcExpressOrder> orderList = AbTcExpressOrder.dao.find(
						orderBuf.toString(), getStart(pi, ps), ps);
				for (AbTcExpressOrder order : orderList) {
					Record r = Db
							.findFirst(
									"SELECT count(1) as cont  FROM ab_order_itemchargeoff i where i.orderid=?",
									order.getStr(AbTcExpressOrder.SN));
					long dds = r.getLong("cont");
					// 判断登录
				
					order.put("compete", "0");
					if (StringUtils.isNotEmpty(uid)) {
						// 查询当前用户是否已抢单
						Record c = Db
								.findFirst(
										"SELECT count(1) as cont  FROM ab_order_itemchargeoff i  where i.orderid=? and i.qdrid=?",
										order.getStr(AbTcExpressOrder.SN), uid);
						order.put("compete", c.getLong("cont") > 0 ? 1 : 0);

					}

					order.put("qdrs", dds);
				}
				resultMap.put("data", orderList);
				resultMap.put("result", RESULT_SUCCESS);
			}
		} catch (Exception e) {
			formatInvokeFailResponse(e, resultMap);
		}
		renderJson(resultMap);
	}
	public void wtcyd() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			// 获取分页信息
			Integer pi = this.getParaToInt("pi");
			Integer ps = this.getParaToInt("ps");
			// 0-全部，1-悬赏，2-招标，3-雇佣
			Integer type = this.getParaToInt("type");
			String lng = this.getPara("lng");
			String lat = this.getPara("lat");
			if (pi == null || ps == null || type == null || pi < 1 || ps < 1
					|| type < 0 || type > 3) {
				formatInvalidParamResponse(resultMap);
			} else {
				String uid = this.getPara("uid");
				//获取当前用户的听单城市
				
				StringBuilder orderBuf = new StringBuilder();
				orderBuf
				.append("SELECT a.istrue, a.id, a.xdrdh, a.xdsj, a.sjjd,a.sjwd,b.min_price,b.max_price,b.send_time,b.send_addr,b.style,b.rcv_addr1,b.total_price, ");
				orderBuf
				.append(" a.city_name,a.ddzje,a.sn,a.is_type,a.mapbusiness,a.zffs, a.shdz,lxrdh,a.shdzjd,a.shdzwd,");
				orderBuf
				.append(" b.rcv_addr2,b.rcv_addr3,b.rcv_addr4,b.rcv_addr5,b.car,");
				orderBuf
				.append(" b.people,b.huidan_price,b.pay_type,b.weight,b.send_addr_jd,b.send_addr_wd,");
				orderBuf
				.append(" b.goods_type,b.lift ,a.ddzt, a.xdrid,b.goods_mount ,b.goods_volumn, b.kilo,b.pay_type,  ");
				orderBuf
				.append(" b.rcv_addr3_jd, b.rcv_addr2_wd, b.rcv_addr2_jd, b.rcv_addr1_wd, b.rcv_addr1_jd");
				orderBuf
				.append(" ,b.rcv_addr5_wd, b.rcv_addr5_jd, b.rcv_addr4_wd, b.rcv_addr4_jd, b.rcv_addr3_wd,b.rcv_phone5,b.rcv_phone4,b.rcv_phone3,b.rcv_phone2,b.rcv_phone1,b.remark  ");
				
				orderBuf.append(" from (SELECT * FROM ab_order  WHERE ddzt=1 AND (psfs!=0 or psfs is null )");
				
				
				orderBuf.append(" and  fmcar ='"+uid+"' and way !=1");	
				
				orderBuf.append(")  AS a LEFT JOIN ");//
				
				// if(StringUtil.isNull(lng) && StringUtil.isNull(lat)) {
				orderBuf.append(" ab_tc_express_order ");//
				// } else {
				// orderBuf.append("(SELECT * FROM (");//
				// orderBuf.append(" SELECT *, ROUND(6378.138*2*ASIN(SQRT(POW(SIN( ("+lat+"*PI()/180-send_addr_wd*PI()/180)/2),2)");//
				// orderBuf.append(" +COS("+lat+"*PI()/180)*COS(send_addr_wd*PI()/180)* ");//
				// orderBuf.append(" POW(SIN( ("+lng+" *PI()/180-send_addr_jd*PI()/180)/2),2)))*1000) AS juli FROM   ab_tc_express_order ");//
				// orderBuf.append(" WHERE send_addr_jd >0) AS tt WHERE   juli < 100000 )");//
				// }
				orderBuf
				.append(" AS b ON a.sn = b.sn WHERE a.ddzt=1 and b.send_addr IS NOT NULL");//
				orderBuf.append(" ORDER BY b.send_time DESC  LIMIT ?,? ");
				System.out.println(orderBuf);
				
				List<AbTcExpressOrder> orderList = AbTcExpressOrder.dao.find(
						orderBuf.toString(), getStart(pi, ps), ps);
				for (AbTcExpressOrder order : orderList) {
					Record r = Db
					.findFirst(
							"SELECT count(1) as cont  FROM ab_order_itemchargeoff i where i.orderid=?",
							order.getStr(AbTcExpressOrder.SN));
					long dds = r.getLong("cont");
					// 判断登录
					
					order.put("compete", "0");
					if (StringUtils.isNotEmpty(uid)) {
						// 查询当前用户是否已抢单
						Record c = Db
						.findFirst(
								"SELECT count(1) as cont  FROM ab_order_itemchargeoff i  where i.orderid=? and i.qdrid=?",
								order.getStr(AbTcExpressOrder.SN), uid);
						order.put("compete", c.getLong("cont") > 0 ? 1 : 0);
						
					}
					
					order.put("qdrs", dds);
				}
				resultMap.put("data", orderList);
				resultMap.put("result", RESULT_SUCCESS);
			}
		} catch (Exception e) {
			formatInvokeFailResponse(e, resultMap);
		}
		renderJson(resultMap);
	}

	/**
	 * 获取订单详情
	 */
	public void getOrderDetail() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			// 获取分页信息
			String orderId = getPara("orderId");
			if (StringUtil.isNull(orderId)) {
				formatInvalidParamResponse(resultMap);
			} else {
				List<AbTcExpressOrder> orderList = AbTcExpressOrder.dao
						.find("SELECT * from ab_order a left JOIN ab_tc_express_order b ON a.sn = b.sn where  a.id='"
								+ orderId + "'");
				resultMap.put("compete", "0");
				// 接单数和是否接单
				String mid = getPara("mid");
				if (StringUtils.isNotEmpty(mid)) {
					// 已登录，判断是否已抢单
					Record r = Db
							.findFirst(
									"SELECT count(1) as cont  FROM ab_order_itemchargeoff i where i.orderid=? and i.qdrid=?",
									orderId, mid);
					resultMap.put("compete", r.getLong("cont"));
				}
				// 获取抢单数
				Record r = Db
						.findFirst(
								"SELECT count(1) as cont  FROM ab_order_itemchargeoff i JOIN sys_user u ON i.qdrid = u.id where i.orderid=?",
								orderId);
				long dds = r.getLong("cont");
				System.out.println(dds);
				resultMap.put("data", orderList);
				resultMap.put("sqjdrs", dds);
				resultMap.put("result", RESULT_SUCCESS);
			}
		} catch (Exception e) {
			formatInvokeFailResponse(e, resultMap);
		}
		renderJson(resultMap);
	}

	/**
	 * 拒单
	 */
	public void notReceiveOrder() {
		String orderId = getPara("orderId");
		String uid = getPara("uid");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (!StringUtil.isNull(orderId) && !StringUtil.isNull(uid)) {
			AbOrder ateo = AbOrder.dao.findFirst(
					"SELECT * FROM ab_order WHERE sn=?", orderId);
			if (null != ateo) {
				if ("1".equals(ateo.get(AbOrder.DDZT))) {
					ateo.set(AbOrder.QDRID, uid);
					ateo.set(AbOrder.DDZT, 6);
					if (ateo.update()) {
						resultMap.put("result", RESULT_SUCCESS);
						resultMap.put("msg", "拒单成功");
					} else {
						resultMap.put("result", RESULT_FAIL);
						resultMap.put("msg", "拒单失败,请稍后重试");
					}
				} else {
					resultMap.put("ddzt", ateo.get(AbOrder.DDZT));
					resultMap.put("result", RESULT_FAIL);
					resultMap.put("msg", "订单状态错误");
				}
			} else {
				resultMap.put("ddzt", "-1");
				resultMap.put("result", RESULT_FAIL);
				resultMap.put("msg", "订单不存在");
			}
		} else {
			formatInvalidParamResponse(resultMap);
		}
		renderJson(resultMap);
	}

	// 应用于招标模式，金额由司机报的情况
	private boolean checkYj(AbOrder order, String uid, BigDecimal money) {
		SysUser user = SysUser.dao.findById(uid);
		BigDecimal zhye = user.getBigDecimal("zhye");// 司机账户余额
		zhye = zhye == null ? new BigDecimal(0) : zhye;
		BigDecimal yj = user.getBigDecimal("per");// 佣金比例
		yj = yj == null ? new BigDecimal(0) : yj;
		if (yj == null || zhye == null) {
			return false;
		}
		BigDecimal minMoney = money.multiply(yj.divide(new BigDecimal(100)));// 最小要求的余额
		if (zhye != null && zhye.compareTo(minMoney) >= 0) {
			return true;
		} else {
			return false;
		}
	}

	// 应用于招标模式，金额由司机报的情况
	private boolean checkYj(AbOrder order, String uid) {
		BigDecimal money = order.getBigDecimal("ddzje");
		return checkYj(order, uid, money);
	}

	/**
	 * 取消订单
	 */
	public void cancelOrder() {
		String orderId = getPara("orderId");
		String uid = getPara("uid");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (!StringUtil.isNull(orderId) && !StringUtil.isNull(uid)) {
			AbOrder ateo = AbOrder.dao.findFirst(
					"SELECT * FROM ab_order WHERE sn=?", orderId);
			if (null != ateo) {
				if ("2".equals(ateo.get(AbOrder.DDZT))) {
					ateo.set(AbOrder.QDRID, "");
					ateo.set(AbOrder.DDZT, 1);
					SysUser siji = SysUser.dao.findById(uid);
					BigDecimal per = siji.getBigDecimal("per");// 佣金比例, %
					per = per.multiply(new BigDecimal(0.01));
					BigDecimal zhye = siji.getBigDecimal("zhye");// 余额
					BigDecimal money = ateo.getBigDecimal("ddzje");// 订单总金额
					BigDecimal djje = per.multiply(money);// 扣除金额
					if (zhye == null) {
						zhye = new BigDecimal(0);
					}
					zhye = zhye.add(djje);
					siji.set("zhye", zhye);
					if (siji.update() && ateo.update()) {
						resultMap.put("result", RESULT_SUCCESS);
						resultMap.put("msg", "取消成功");
					} else {
						resultMap.put("result", RESULT_FAIL);
						resultMap.put("msg", "取消失败,请稍后重试");
					}
				} else {
					resultMap.put("result", RESULT_FAIL);
					resultMap.put("msg", "订单状态不对");
				}
			} else {
				resultMap.put("result", RESULT_FAIL);
				resultMap.put("msg", "订单不存在");
			}
		} else {
			formatInvalidParamResponse(resultMap);
		}
		renderJson(resultMap);
	}

	/**
	 * 接单
	 */
	public void receiveOrder() {
		String orderId = getPara("orderId");
		String uid = getPara("uid");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (!StringUtil.isNull(orderId) && !StringUtil.isNull(uid)) {
			AbOrder ateo = AbOrder.dao.findFirst(
					"SELECT * FROM ab_order WHERE sn=?", orderId);
			if (null != ateo) {
				if ("0".equals(ateo.get(AbOrder.ISTRUE))) {// 处理假订单
					ateo.set(AbOrder.DDZT, 2);
					ateo.update();
					resultMap.put("ddzt", 2);
					resultMap.put("result", RESULT_FAIL);
					resultMap.put("msg", "订单已被抢");
				} else if ("1".equals(ateo.get(AbOrder.DDZT))) {
					// 检验司机的余额是否足够接单
					if (!checkYj(ateo, uid)) {
						resultMap.put("ddzt", 1);
						resultMap.put("result", RESULT_FAIL);
						resultMap.put("msg", "接单失败,请稍后重试");
					} else {
						// 直接扣除司机的佣金
						SysUser siji = SysUser.dao.findById(uid);
						if (siji == null) {
							resultMap.put("ddzt", 1);
							resultMap.put("result", RESULT_FAIL);
							resultMap.put("msg", "司机id不存在");
						} else {
							BigDecimal per = siji.getBigDecimal("per");// 佣金比例,
							// %
							per = per.multiply(new BigDecimal(0.01));
							BigDecimal zhye = siji.getBigDecimal("zhye");// 余额
							BigDecimal money = ateo.getBigDecimal("ddzje");// 订单总金额
							if (zhye == null) {
								zhye = new BigDecimal(0);
							}
							BigDecimal djje = per.multiply(money);// 扣除金额
							zhye = zhye.subtract(djje);
							if (zhye.doubleValue() < 0) {
								resultMap.put("ddzt", 1);
								resultMap.put("result", RESULT_FAIL);
								resultMap.put("msg", "账户余额不足以抵扣佣金");
							} else {
								siji.set("zhye", zhye);
								ateo.set(AbOrder.QDRID, uid);
								ateo.set(AbOrder.QDSJ, DateUtil
										.getAddDateStr(0));
								ateo.set(AbOrder.DDZT, 2);
								if (siji.update() && ateo.update()) {
									resultMap.put("ddzt", 2);
									resultMap.put("result", RESULT_SUCCESS);
									resultMap.put("msg", "接单成功");
								} else {
									resultMap.put("ddzt", 1);
									resultMap.put("result", RESULT_FAIL);
									resultMap.put("msg", "接单失败,请稍后重试");
								}
							}
						}
					}
				} else {
					resultMap.put("ddzt", ateo.get(AbOrder.DDZT));
					resultMap.put("result", RESULT_FAIL);
					resultMap.put("msg", "订单已被抢");
				}
			} else {
				resultMap.put("ddzt", "-1");
				resultMap.put("result", RESULT_FAIL);
				resultMap.put("msg", "订单不存在");
			}
		} else {
			formatInvalidParamResponse(resultMap);
		}
		renderJson(resultMap);
	}

	private static class ReceiveOrderRunner implements Runnable {

		private String sn;

		public ReceiveOrderRunner(String sn) {
			this.sn = sn;
		}

		public void run() {
			try {
				Thread.sleep(5 * 60 * 1000);// 5分钟以后，自动选中第一位司机
				AbOrder o = AbOrder.dao.findFirst(
						"select * from ab_order where sn=?", sn);
				if (o != null && o.getStr("ddzt").equals("1")
						&& StringUtil.isNull(o.getStr(AbOrder.QDRID))) {
					AbTcExpressOrderBaojia bj = AbTcExpressOrderBaojia.dao
							.findFirst("select * from ab_tc_express_order_baojia where sn=? order by bj_time asc");
					if (bj != null) {
						o.set(AbOrder.QDRID, bj.getStr("siji_id"));
						o.set(AbOrder.QDSJ, DateUtil.getAddDateStr(0));
						o.set(AbOrder.DDZT, 2);
						o.update();
						System.out.println("自动选择第一个报价的司机接单："
								+ bj.getStr("sjiji_id") + ",sn:" + sn);
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * http://127.0.0.1/ash/rest/order/getBjSiList?orderSn=TC148935217 得到报价司机列表
	 */
	public void getBjSiList() {
		String orderSn = getPara("orderSn");// 订单编号
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (StringUtil.isNull(orderSn)) {
			formatInvalidParamResponse(resultMap);
		} else {
			StringBuilder sqlBuf = new StringBuilder("	SELECT  * FROM  (");
			sqlBuf
					.append(" SELECT COUNT(0) AS jdcs,qdrid FROM ab_order WHERE xdsj > ?  GROUP BY qdrid");
			sqlBuf.append(" ) AS uu, ");
			sqlBuf.append(" (SELECT ttu.*,af.type,af.lng,af.lat FROM (  ");
			sqlBuf
					.append(" SELECT dteob.sn, dteob.siji_id, dteob.money, su.loginid,  ");
			sqlBuf
					.append(" su.mc,su.dtz,su.role_id, su.logo,su.jifen,dteob.bj_time FROM ");
			sqlBuf
					.append(" ab_tc_express_order_baojia AS dteob,sys_user AS su WHERE ");
			sqlBuf
					.append(" dteob.siji_id=su.id AND dteob.sn=?) AS ttu LEFT JOIN ab_fmcar AS af ON ttu.loginid = af.mobile");
			sqlBuf.append(" ) AS tu WHERE uu.qdrid = tu.siji_id ");
			// RestLogUtils.writeToLocal(sqlBuf.toString(), true);
			List<AbOrder> orderLists = AbOrder.dao.find(sqlBuf.toString(),
					DateUtil.getAddDateStr(-90), orderSn);
			resultMap.put("result", RESULT_SUCCESS);
			resultMap.put("data", orderLists);
			resultMap.put("msg", "查询成功");
		}
		renderJson(resultMap);
	}

	/**
	 * 竞标订单报价
	 */
	public void quoteOrder() {
		String orderId = getPara("orderId");
		String uid = getPara("uid");
		String quoteMoney = getPara("quoteMoney");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (!StringUtil.isNull(orderId) && !StringUtil.isNull(uid)
				&& !StringUtil.isNull(quoteMoney)) {
			try {
				long isPj = Db.findFirst(
						"SELECT COUNT(0) ctn FROM ab_tc_express_order_baojia WHERE siji_id='"
								+ uid + "' AND sn='" + orderId + "' ").getLong(
						"ctn");
				if (isPj > 0) {
					resultMap.put("result", RESULT_FAIL);
					resultMap.put("msg", "您已经给改订单报啦价,不必重复报价");
				} else {
					AbOrder ateo = AbOrder.dao.findFirst(
							"SELECT * FROM ab_order WHERE sn=?", orderId);
					if (null != ateo) {
						if ("1".equals(ateo.get(AbOrder.DDZT))) {
							// 检查司机
							SysUser siji = SysUser.dao.findById(uid);
							if (siji == null) {
								resultMap.put("result", RESULT_FAIL);
								resultMap.put("msg", "司机id不存在");
							} else {
								BigDecimal per = siji.getBigDecimal("per");// 佣金比例,
								// %
								per = per.multiply(new BigDecimal(0.01));
								BigDecimal zhye = siji.getBigDecimal("zhye");// 余额
								BigDecimal oldDjje = siji.getBigDecimal("djje");// 之前的冻结金额
								if (oldDjje == null) {
									oldDjje = new BigDecimal(0);
								}
								if (zhye == null) {
									zhye = new BigDecimal(0);
								}
								BigDecimal djje = per.multiply(new BigDecimal(
										quoteMoney));// 冻结金额
								zhye = zhye.subtract(djje);
								if (zhye.doubleValue() < 0) {
									resultMap.put("result", RESULT_FAIL);
									resultMap.put("msg", "账户余额不足以抵扣佣金");
								} else {
									siji.set("zhye", zhye);
									siji.set("djje", oldDjje.add(djje));
									AbOrderQuote aoq = new AbOrderQuote();
									aoq.set(AbOrderQuote.CREATE_TIME, DateUtil
											.getCurrentDate());
									// aoq.set(AbOrderQuote.ID,
									// StringUtil.getRandString32());
									aoq.set(AbOrderQuote.ORDER_SN, orderId);
									aoq.set(AbOrderQuote.QUOTE_MONEY,
											quoteMoney);
									aoq.set(AbOrderQuote.UID, uid);
									if (siji.update() && aoq.save()) {
										resultMap.put("ddzt", 1);
										resultMap.put("result", RESULT_SUCCESS);
										resultMap.put("msg", "报价成功,请静候客户佳音");
										// 如果是第一个报价，则进入5分钟倒计时，如果客户没有选择谁中标，则默认选择第一个人中标
										List<AbOrderQuote> list = AbOrderQuote.dao
												.find(
														"select * from ab_tc_express_order_baojia where sn=?",
														new Object[] { orderId });
										if (list != null && list.size() == 1) {
											new AutoQuoteTimer(orderId,
													new Date()).start();
										}
									} else {
										resultMap.put("ddzt", 1);
										resultMap.put("result", RESULT_FAIL);
										resultMap.put("msg", "信息保存失败,请稍后重试");
									}
								}
							}
						} else {
							resultMap.put("ddzt", ateo.get(AbOrder.DDZT));
							resultMap.put("result", RESULT_FAIL);
							resultMap.put("msg", "订单已过期");
						}
					} else {
						resultMap.put("ddzt", -1);
						resultMap.put("result", RESULT_FAIL);
						resultMap.put("msg", "订单不存在");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				resultMap.put("ddzt", 1);
				resultMap.put("result", RESULT_FAIL);
				resultMap.put("msg", "系统错误");
			}
		} else {
			formatInvalidParamResponse(resultMap);
		}
		renderJson(resultMap);
	}

	/**
	 * 确认取货
	 */
	public void comfrimGetOrder() {
		String orderId = getPara("orderId");
		String uid = getPara("uid");
		String filePath = getPara("filePath");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (!StringUtil.isNull(orderId) && !StringUtil.isNull(uid)) {
			if (orderId.indexOf(",") > 0) {
				String[] arr = orderId.split(",");
				StringBuffer sns = new StringBuffer();
				for (String str : arr) {
					if (sns.length() > 1) {
						sns.append(",");
					}
					sns.append("'").append(str).append("'");
				}

				String sql = "update ab_order set ddzt=3 where sn in("
						+ sns.toString() + ") and ddzt=2";
				Db.update(sql);
				resultMap.put("result", RESULT_SUCCESS);
				resultMap.put("msg", "取货成功");
			} else {

				AbOrder ateo = AbOrder.dao.findFirst(
						"SELECT * FROM ab_order WHERE sn =?", orderId);
				if (null != ateo) {
					if ("2".equals(ateo.get(AbOrder.DDZT))) {
						ateo.set(AbOrder.DDZT, 3);
						if (!StringUtil.isNull(filePath)) {
							AbOrderStateImg stateImg = new AbOrderStateImg();
							stateImg.set(AbOrderStateImg.CREATE_TIME, DateUtil
									.getCurrentDate());
							stateImg.set(AbOrderStateImg.ORDER_SN, orderId);
							stateImg.set(AbOrderStateImg.ORDER_STATE, 3);
							stateImg.set(AbOrderStateImg.PK_ID, StringUtil
									.getRandString32());
							stateImg.set(AbOrderStateImg.UID, uid);
							stateImg.set(AbOrderStateImg.ORDER_IMG_PATH,
									filePath);
							stateImg.save();
						}
						if (ateo.update()) {
							resultMap.put("result", RESULT_SUCCESS);
							resultMap.put("msg", "取货成功");
						} else {
							resultMap.put("result", RESULT_FAIL);
							resultMap.put("msg", "取货失败,请稍后重试");
						}
					} else {
						resultMap.put("result", RESULT_FAIL);
						resultMap.put("msg", "订单状态错误");
					}
				} else {
					resultMap.put("result", RESULT_FAIL);
					resultMap.put("msg", "订单不存在");
				}
			}
		} else {
			formatInvalidParamResponse(resultMap);
		}
		renderJson(resultMap);
	}

	/**
	 * 确认签收
	 */
	public void orderQianShou() {
		String orderId = getPara("orderId");
		String uid = getPara("uid");
		String yzm = getPara("yzm");
		String filePath = getPara("filePath");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (!StringUtil.isNull(orderId) && !StringUtil.isNull(uid)
				&& !StringUtil.isNull(yzm)) {

			AbOrder ateo = AbOrder.dao.findFirst(
					"SELECT * FROM ab_order WHERE sn=?", orderId);
			if (null != ateo) {
				if ("4".equals(ateo.get(AbOrder.DDZT))) {
					ateo.set(AbOrder.DDZT, 5);
					AbOrderSms orSms = AbOrderSms.dao.findFirst(
							"SELECT * FROM ab_order_sms WHERE orderid = ?",
							ateo.getStr(AbOrder.ID));
					// yzm 短信验证
					if (null != orSms) {
						String yzmServer = orSms.getStr(AbOrderSms.YZM);
						if (yzm.equals(yzmServer)) {
							if (ateo.update()) {
								resultMap.put("result", RESULT_SUCCESS);
								resultMap.put("msg", "签收成功");
							} else {
								resultMap.put("result", RESULT_FAIL);
								resultMap.put("msg", "签失败,请稍后重试");
							}
						} else {
							resultMap.put("result", RESULT_FAIL);
							resultMap.put("msg", "签失败,验证码错误");
						}
					}
					 AbOrderStateImg stateImg = new AbOrderStateImg();
					 stateImg.set(AbOrderStateImg.CREATE_TIME,
					 DateUtil.getCurrentDate());
					 stateImg.set(AbOrderStateImg.ORDER_SN, orderId);
					 stateImg.set(AbOrderStateImg.ORDER_STATE, 3);
					 stateImg.set(AbOrderStateImg.PK_ID,
					 StringUtil.getRandString32());
					 stateImg.set(AbOrderStateImg.UID, uid);
					 stateImg.set(AbOrderStateImg.ORDER_IMG_PATH, filePath);
					 stateImg.save();
				} else {
					resultMap.put("result", RESULT_FAIL);
					resultMap.put("msg", "订单状态错误");
				}
			} else {
				resultMap.put("result", RESULT_FAIL);
				resultMap.put("msg", "订单不存在");
			}
		} else {
			formatInvalidParamResponse(resultMap);
		}
		renderJson(resultMap);
	}

	/**
	 * 确认送达
	 */
	public void images() {
		String sn = getPara("sn");
		String uid = getPara("uid");
		String order_state = getPara("order_state");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (!StringUtil.isNull(sn) && !StringUtil.isNull(uid)) {
			String sql = "select order_img as image,fk_order_sn as sn ,create_time as createtime,order_state from ab_order_state_img  where fk_order_sn='"+sn+"'";
			if(StringUtils.isNotEmpty(order_state)){
				sql = sql + " and order_state="+ order_state;
			}
			
			List<Record> data = Db.find(sql);
			resultMap.put("data", data);
			resultMap.put("result", RESULT_SUCCESS);
			resultMap.put("msg", "查询成功");
		}else{
			formatInvalidParamResponse(resultMap);
		}
		renderJson(resultMap);
	}
	/**
	 * 确认送达
	 */
	public void comfrimSdOrder() {
		String orderId = getPara("orderId");
		String uid = getPara("uid");
		String filePath = getPara("filePath");
		String yzm = getPara("yzm");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (!StringUtil.isNull(orderId) && !StringUtil.isNull(uid)) {
			if (orderId.indexOf(",") > 1) {

				String[] arr = orderId.split(",");
				StringBuffer sn = new StringBuffer();
				for (String str : arr) {
					if (sn.length() > 1) {
						sn.append(",");
					}
					sn.append("'").append(str).append(",");
				}

				String sql = "update ab_order set ddzt=4 where ddzt=3 and sn in("
						+ sn.toString() + ")";
				int s = Db.update(sql);
				if(s>0){
					resultMap.put("result", RESULT_SUCCESS);
					resultMap.put("msg", "成功送达");
				}
			} else {

				AbOrder ateo = AbOrder.dao.findFirst(
						"SELECT * FROM ab_order WHERE sn=?", orderId);

				if (null != ateo) {
					if ("3".equals(ateo.get(AbOrder.DDZT))) {

						ateo.set(AbOrder.SDSJ, DateUtil.getCurrentDate());
						if (!StringUtil.isNull(filePath)) {
							AbOrderStateImg stateImg = new AbOrderStateImg();
							stateImg.set(AbOrderStateImg.CREATE_TIME, DateUtil
									.getCurrentDate());
							stateImg.set(AbOrderStateImg.ORDER_SN, orderId);
							stateImg.set(AbOrderStateImg.ORDER_STATE, 3);
							stateImg.set(AbOrderStateImg.PK_ID, StringUtil
									.getRandString32());
							stateImg.set(AbOrderStateImg.UID, uid);
							stateImg.set(AbOrderStateImg.ORDER_IMG_PATH,
									filePath);
							stateImg.save();
						}
						if (StringUtil.isNull(yzm)) {
							ateo.set(AbOrder.DDZT, 4);
							if (ateo.update()) {
								resultMap.put("result", RESULT_SUCCESS);
								resultMap.put("msg", "成功送达");
							} else {
								resultMap.put("result", RESULT_FAIL);
								resultMap.put("msg", "数据保存失败,请稍后重试");
							}
						} else {
							StringBuilder sqlBuf = new StringBuilder(
									"SELECT * FROM (");
							sqlBuf
									.append(" SELECT * FROM ab_order_sms WHERE orderid =?");
							sqlBuf.append(" OR  orderid=?)");
							sqlBuf.append(" AS aos WHERE yzm=?");
							AbOrderSms orSms = AbOrderSms.dao.findFirst(sqlBuf
									.toString(), new Object[] { orderId,
									ateo.getStr(AbOrder.ID), yzm });
							if (null != orSms) {
								ateo.set(AbOrder.DDZT, 5);
								String yzmServer = orSms.getStr(AbOrderSms.YZM);
								if (yzm.equals(yzmServer)) {
									if (ateo.update()) {
										resultMap.put("result", RESULT_SUCCESS);
										resultMap.put("msg", "签收成功");
									} else {
										resultMap.put("result", RESULT_FAIL);
										resultMap.put("msg", "签收失败,请稍后重试");
									}
								} else {
									resultMap.put("result", RESULT_FAIL);
									resultMap.put("msg", "签收失败,验证码错误");
								}
							} else {
								resultMap.put("result", RESULT_FAIL);
								resultMap.put("msg", "签收失败,验证码错误");
							}
						}
					} else {
						resultMap.put("result", RESULT_FAIL);
						resultMap.put("msg", "订单状态错误");
					}
				} else {
					resultMap.put("result", RESULT_FAIL);
					resultMap.put("msg", "订单不存在");
				}
			}
		} else {
			formatInvalidParamResponse(resultMap);
		}
		renderJson(resultMap);
	}

	// public String getUploadFileSavePath(){
	// String filename = null;
	// try {
	// UploadFile f = this.getFile();
	// filename = StringUtil.getUuid32() + StringUtil.getExt(f.getFileName());
	// f.getFile().renameTo(new File(f.getSaveDirectory()+ filename));
	// //对文件进行缩略图生成
	// ImageScale is = new ImageScale();
	// try {
	// is.saveImageAsJpg(f.getSaveDirectory()+ filename, f.getSaveDirectory()+
	// StringUtil.getThumbnail(filename), 800, 600);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// (new File(f.getSaveDirectory()+f.getOriginalFileName())).delete();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return "upload/" + filename;
	// }
	public double GetDistance(double lon1, double lat1, double lon2, double lat2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lon1) - rad(lon2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		// s = Math.round(s * 10000) / 10000;
		return s;
	}

	private double rad(double d) {
		return d * Math.PI / 180.0;
	}

	private final double EARTH_RADIUS = 6378137;

	/**
	 * 上传地理位置信息保存
	 */
	public void uploadLoaction() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String orderid = getPara("orderid");// 订单ID
		String mid = getPara("mid");// 商家ID
		String uid = getPara("shrid");// 送货人ID 也就是司机id
		String jd = getPara("jd");// 经度
		String wd = getPara("wd");// 维度
		String mobile = getPara("mobile");// 手机号码
		String location = getPara("location");// 当前位置

		if (StringUtil.isNull(uid) || StringUtil.isNull(jd)
				|| StringUtil.isNull(wd)) {
			formatInvalidParamResponse(resultMap);
		} else {
			try {
				String curTime = DateUtil.getCurrentDate();
				AbSjPosition position = new AbSjPosition();
				position.set(AbDriverPosition.ID, StringUtil.getRandString32());
				position.set(AbDriverPosition.DRIVER_ID, uid);
				position.set(AbDriverPosition.DRIVER_NAME, "");
				position.set(AbDriverPosition.JD, jd);
				position.set(AbDriverPosition.WD, wd);
				position.set(AbDriverPosition.DQSJ, curTime);
				position.save();
				
				SysUser user = SysUser.dao.findById(uid);
				
				AbFmcar af = AbFmcar.dao.findByMobile(user.getStr("mobile"));
				if (null != af) {
					af.set(AbFmcar.LOCATION, location);
					af.set(AbFmcar.LOCATION_TIME, curTime);
					af.set("lng", wd);
					af.set("lat", jd);
					af.update();
				}
				
				double dis = 1000;
				try {
					dis = GetDistance(Double.parseDouble(jd), Double
							.parseDouble(wd), Double.parseDouble(user
							.getStr(SysUser.LNG)), Double.parseDouble(user
							.getStr(SysUser.LAT)));
				} catch (Exception e) {
					e.printStackTrace();
				}
				user.set(SysUser.LAT, wd);
				user.set(SysUser.LNG, jd);
				if (user.update()) {
					System.out.println(user.getStr("mc") + ":"
							+ user.getStr("mobile") + wd + "," + jd);
				} else {
					System.out.println(user.getStr("mc") + ":"
							+ user.getStr("mobile") + wd + "," + jd + "更新失败");
				}
				System.out.println(user.getStr("mc") + ":移动距离，" + dis);
				if (dis > 50) {

					// 获取所有该司机的所有正在送货的订单，然后保存其位置信息
					List<AbOrder> sending = AbOrder.dao
							.find(
									"select * from ab_order where qdrid=? and ddzt='3'",
									uid);
					for (int i = 0; i < sending.size(); i++) {
						AbOrder order = sending.get(i);
						AbOrderPosition aop = new AbOrderPosition();
						aop.set(AbOrderPosition.ID, StringUtil
								.getRandString32());
						aop.set(AbOrderPosition.JD, jd);
						aop.set(AbOrderPosition.ORDERID, order.get(AbOrder.ID));
						aop.set(AbOrderPosition.WD, wd);
						aop.set(AbOrderPosition.MID, mid);
						aop.set(AbOrderPosition.SHRID, uid);
						aop.set(AbOrderPosition.DQSJ, curTime);
						if (!aop.save()) {
							System.out.println(user.getStr("mc") + ":位置信息保存失败");
						}
					}
				}
				resultMap.put("result", RESULT_SUCCESS);
				resultMap.put("msg", "数据保存成功");
			} catch (Exception e) {
				e.printStackTrace();
				resultMap.put("result", RESULT_FAIL);
				resultMap.put("msg", "数据保存失败");
			}
		}
		renderJson(resultMap);
	}

	public void getDriverCurLocation() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String mobile = getPara("mobile");// 送货人ID 也就是司机id
		if (StringUtil.isNull(mobile)) {
			formatInvalidParamResponse(resultMap);
		} else {
			AbSjPosition position = AbSjPosition.dao
					.findFirst(
							"SELECT * FROM ab_sj_position WHERE sjid IN(SELECT id FROM sys_user WHERE mobile=?) ORDER BY dqsj DESC LIMIT 0,1",
							new Object[] { mobile });
			resultMap.put("result", RESULT_SUCCESS);
			resultMap.put("position", position);
			resultMap.put("msg", "数据保存成功");
		}
		renderJson(resultMap);
	}

	// private List<AbTcExpressOrder> getXSOrderList(int pi,int ps){
	// List<AbTcExpressOrder> orderList = AbTcExpressOrder.dao
	// .find("SELECT b.sn,b.send_time,b.send_addr,b.rcv_addr1,b.total_price from ab_order a INNER JOIN ab_tc_express_order b ON a.sn = b.sn where a.is_type=1 and style=1 ORDER BY b.send_time DESC LIMIT ?,? ",
	// getStart(pi, ps), ps);
	//		
	// return orderList;
	// }
	//	
	// private List<AbTcExpressOrder> getZBOrderList(int pi,int ps){
	// List<AbTcExpressOrder> orderList = AbTcExpressOrder.dao
	// .find("SELECT b.min_price,b.max_price,b.sn,b.send_time,b.send_addr,b.rcv_addr1,b.total_price from ab_order a INNER JOIN ab_tc_express_order b ON a.sn = b.sn where a.is_type=1 and style=2 ORDER BY b.send_time DESC LIMIT ?,? ",
	// getStart(pi, ps), ps);
	//		
	// return orderList;
	// }

	/**
	 * 得到接单的司机
	 */
	public void getOrderSiji() {
		String orderSn = getPara("orderSn");// 订单编号
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (StringUtil.isNull(orderSn)) {
			formatInvalidParamResponse(resultMap);
		} else {
			StringBuilder sqlBuf = new StringBuilder(
					"select u.* from sys_user u left join ab_order o on o.qdrid=u.id where sn=?");
			SysUser u = SysUser.dao.findFirst(sqlBuf.toString(), orderSn);
			resultMap.put("result", RESULT_SUCCESS);
			resultMap.put("data", u);
			resultMap.put("msg", "查询成功");
		}
		renderJson(resultMap);
	}

	/**
	 * 接单
	 */
	public void selectSiji() {
		String orderId = getPara("orderId");
		String uid = getPara("uid");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (!StringUtil.isNull(orderId) && !StringUtil.isNull(uid)) {
			AbOrder ateo = AbOrder.dao.findFirst(
					"SELECT * FROM ab_order WHERE sn=?", orderId);
			if (null != ateo) {
				if ("0".equals(ateo.get(AbOrder.ISTRUE))) {// 处理假订单
					ateo.set(AbOrder.DDZT, 2);
					ateo.update();
					resultMap.put("ddzt", 2);
					resultMap.put("result", RESULT_FAIL);
					resultMap.put("msg", "这是假订单");
				} else if ("1".equals(ateo.get(AbOrder.DDZT))) {
					// 直接扣除司机的佣金
					SysUser siji = SysUser.dao.findById(uid);
					if (siji == null) {
						resultMap.put("ddzt", 1);
						resultMap.put("result", RESULT_FAIL);
						resultMap.put("msg", "司机id不存在");
					} else {
						ateo.set(AbOrder.QDRID, uid);
						ateo.set(AbOrder.QDSJ, DateUtil.getAddDateStr(0));
						ateo.set(AbOrder.DDZT, 2);
						if (ateo.update()) {
							// 修改抢单列表，选择司机
							resultMap.put("ddzt", 2);
							resultMap.put("result", RESULT_SUCCESS);
							resultMap.put("msg", "接单成功");

							List<AbOrderItemchargeoff> list = AbOrderItemchargeoff.dao
									.find(
											"select * from ab_order_itemchargeoff where orderid=?",
											orderId);

							if (list != null) {
								for (int i = 0; i < list.size(); i++) {
									AbOrderItemchargeoff item = list.get(i);
									String content = "司机朋友您好，很抱歉通知您，编号号为："
											+ orderId + ",的订单，货主已选择其他司机进行运送。";
									if (StringUtils
											.equals(
													item
															.getStr(AbOrderItemchargeoff.QDRID),
													uid)) {
										content = "司机朋友您好，恭喜您！编号号为：" + orderId
												+ ",的订单，您已中标，请尽快与货主联系取货！！！";
									}
									resultMap.put("msg", "司机选择成功");
									try {
										String phone = SysUser.dao
												.findById(
														item
																.getStr(AbOrderItemchargeoff.QDRID))
												.getStr(SysUser.MOBILE);
										SmsMessage.SendMessage(phone, content);
										AbTcContrller.insertOrderSms(orderId,
												phone, content);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						} else {
							resultMap.put("ddzt", 1);
							resultMap.put("result", RESULT_FAIL);
							resultMap.put("msg", "接单失败,请稍后重试");
						}
					}
				} else {
					resultMap.put("ddzt", ateo.get(AbOrder.DDZT));
					resultMap.put("result", RESULT_FAIL);
					resultMap.put("msg", "订单已被抢");
				}
			} else {
				resultMap.put("ddzt", "-1");
				resultMap.put("result", RESULT_FAIL);
				resultMap.put("msg", "订单不存在");
			}
		} else {
			formatInvalidParamResponse(resultMap);
		}
		renderJson(resultMap);
	}

	/**
	 * 司机抢单
	 */
	public void driverCompete() {
		String orderSn = getPara("orderId");// 订单编号
		String uid = getPara("uid");// 订单编号
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (StringUtil.isNull(orderSn) || StringUtil.isNull(uid)) {
			formatInvalidParamResponse(resultMap);
		} else {
			// 首先查看是否已抢单

			AbOrderItemchargeoff compete = AbOrderItemchargeoff.dao
					.findFirst(
							"select * from ab_order_itemchargeoff where qdrid=? and orderid=?",
							uid, orderSn);
			if (compete != null && compete.get("id") != null) {
				resultMap.put("result", RESULT_FAIL);
				resultMap.put("sn", orderSn);
				resultMap.put("msg", "您已报名接单，请等待货主选择");
			} else {
				compete = new AbOrderItemchargeoff();
				compete.set(AbOrderItemchargeoff.ORDERID, orderSn);
				compete.set(AbOrderItemchargeoff.QDRID, uid);
				compete.set(AbOrderItemchargeoff.QDRNAME, SysUser.dao.findById(
						uid).get(SysUser.MC));
				compete.set(AbOrderItemchargeoff.RESTULT, "0");
				compete.set(AbOrderItemchargeoff.ID, UUID.randomUUID()
						.toString().replaceAll("-", ""));
				if (compete.save()) {
					String content = "亲爱的货主您好！您的订单编号号为：" + orderSn
							+ ",的订单，您已有司机报名服务，请及时查看";
					// 查询货主
					AbOrder order = AbOrder.dao.findFirst(
							"select * from ab_order where sn=?", orderSn);

					try {
						SmsMessage.SendMessage(order.getStr(AbOrder.XDRDH),
								content);
						AbTcContrller.insertOrderSms(orderSn, order
								.getStr(AbOrder.XDRDH), content);
					} catch (Exception e) {
						e.printStackTrace();
					}
					resultMap.put("result", RESULT_SUCCESS);
					resultMap.put("msg", "您已报名接单，请等待货主选择");
				} else {
					resultMap.put("result", RESULT_FAIL);
					resultMap.put("msg", "操作失败");
				}
			}

		}
		resultMap.put("sn", orderSn);
		renderJson(resultMap);
	}

	/**
	 * 判断司机是否抢单
	 */
	public void isCompete() {
		String orderSn = getPara("orderId");// 订单编号
		String uid = getPara("uid");// 订单编号
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (StringUtil.isNull(orderSn) || StringUtil.isNull(uid)) {
			formatInvalidParamResponse(resultMap);
		} else {
			AbOrderItemchargeoff compete = new AbOrderItemchargeoff();
			compete.set(AbOrderItemchargeoff.ORDERID, orderSn);
			compete.set(AbOrderItemchargeoff.QDRID, uid);
			compete.set(AbOrderItemchargeoff.QDRNAME, SysUser.dao.findById(uid)
					.get(SysUser.MC));
			compete.set(AbOrderItemchargeoff.RESTULT, "0");
			compete.set(AbOrderItemchargeoff.ID, UUID.randomUUID().toString()
					.replaceAll("-", ""));
			AbOrderItemchargeoff qd = AbOrderItemchargeoff.dao
					.findFirst(
							"select * from ab_order_itemchargeoff where qdrid=? and orderid=?",
							uid, orderSn);
			if (qd == null) {
				resultMap.put("sn", orderSn);
				resultMap.put("result", RESULT_SUCCESS);
				resultMap.put("msg", "可以抢单");
			} else {
				resultMap.put("result", RESULT_FAIL);
				resultMap.put("msg", "您已抢单，请等待货主选择");
			}
		}
		renderJson(resultMap);
	}

	/**
	 * 根据订单查询已报名司机
	 */
	public void competedByOrder() {
		String orderSn = getPara("sn");// 订单编号
		Integer pi = this.getParaToInt("pi");
		Integer ps = this.getParaToInt("ps");
		if (!checkParam(pi)) {
			pi = 1;
		}
		if (!checkParam(ps)) {
			ps = 10;
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (StringUtil.isNull(orderSn)) {
			formatInvalidParamResponse(resultMap);
		} else {
			AbTcExpressOrder order = AbTcExpressOrder.dao.findFirst(
					"select * from ab_tc_express_order where sn =?", orderSn);

			Record r = Db
					.findFirst(
							"SELECT count(1) as cont  FROM ab_order_itemchargeoff i JOIN sys_user u ON i.qdrid = u.id where i.orderid=?",
							orderSn);
			long dds = r.getLong("cont");
			if (dds < 10) {
				pi = 1;
			}
			List<Record> list = Db
					.find(
							"SELECT u.id uid,u.mc name,i.orderid orderid,u.lat lat,u.lng lng,u.mapbusiness address,u.logo logo,u.rate rate,(SELECT COUNT(1) AS cont  FROM ab_order o WHERE o.qdrid =u.id AND ddzt='5') AS fwcs,u.mobile FROM ab_order_itemchargeoff i JOIN sys_user u ON i.qdrid = u.id where i.orderid=? limit ?,?",
							orderSn, getStart(pi, ps), ps);
			resultMap.put("result", RESULT_SUCCESS);
			resultMap.put("data", list);
			resultMap.put("dds", dds);
			resultMap.put("jd", order.get(AbTcExpressOrder.SEND_ADDR_JD));
			resultMap.put("wd", order.get(AbTcExpressOrder.SEND_ADDR_WD));
			resultMap.put("sn", orderSn);

		}

		renderJson(resultMap);
	}

	/**
	 * 司机接单统计
	 */
	public void jdtj() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String id = this.getPara("uid");

		String kssj = StringUtil.toStr(this.getPara("kssj"));
		String jzsj = StringUtil.toStr(this.getPara("jzsj"));
		String ddzt = this.getPara("ddzt");
		Integer pi = this.getParaToInt("pi");
		Integer ps = this.getParaToInt("ps");
		if(StringUtils.equals("0", ddzt)){
			ddzt = null;
		}
		if (!checkParam(pi)) {
			pi = 1;
		}
		if (!checkParam(ps)) {
			ps = 10;
		}
		if (StringUtils.isEmpty(id)) {
			formatInvalidParamResponse(resultMap);
		} else {

			SysUser vo = SysUser.dao.findById(id);
			String list_sql = "select  ao.zfzt as zfzt,ao.xdrmc as xdrmc,ao.xdrdh as xdrdh,ao.lxrdh as lxrdh,ao.lxr as lxr,ao.jl as jl,tc.pay_type,ao.sn as sn from ab_order ao left join ab_tc_express_order tc on tc.sn=ao.sn where qdrid='"
					+ id + "' ";
			String sql = "SELECT qdrid,qdrname,COUNT(id) cnt,SUM(ddzje) je,SUBSTR(qdsj,1,10) rq FROM ab_order WHERE qdrid='"
					+ id + "' ";

			List<Record> list = null;

			if (kssj.length() > 0) {
				sql += " and qdsj>='" + kssj + " 00:00:00'  ";
				list_sql += " and qdsj>='" + kssj + " 00:00:00' ";
			}
			if (jzsj.length() > 0) {
				sql += " and qdsj<='" + jzsj + " 59:59:59' ";
				list_sql += " and qdsj<='" + jzsj + " 59:59:59' ";
			}
			if (StringUtils.isNotEmpty(ddzt)) {
				sql += " and ddzt=" + ddzt;
				list_sql += " and ddzt=" + ddzt;
			}

			list_sql += "  order BY qdsj";
			list = Db.find(list_sql + " limit ?,?", getStart(pi, ps), ps);

			List<Record> r = null;
			sql = "SELECT COUNT(id) totleorder,IFNULL(SUM(ddzje),0) totalmoney,ddzt FROM ab_order  WHERE qdrid='"
					+ id + "'";
			if (kssj.length() > 0) {
				sql += " and qdsj>='" + kssj + " 00:00:00'";
			}
			if (jzsj.length() > 0) {
				sql += " and qdsj<='" + jzsj + " 59:59:59'";
			}
			if (StringUtils.isNotEmpty(ddzt)) {
				sql += " and ddzt=" + ddzt;
			}
			sql = sql + "  group by ddzt ";
			r = Db.find(sql);
			resultMap.put("data", list);
			resultMap.put("total", r);
			resultMap.put("result", RESULT_SUCCESS);
			resultMap.put("msg", "查询成功");
		}

		renderJson(resultMap);
	}
	/**
	 * 
	 * 
	 * 打表
	 */
	public void js() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String uid = this.getPara("uid");
		
		String sn = StringUtil.toStr(this.getPara("sn"));
		int sj = this.getParaToInt("sj");
		String ddzt = this.getPara("ddzt");
		if (StringUtils.isEmpty(uid)||StringUtils.isEmpty(ddzt)||StringUtils.isEmpty(sn)|| sj<1) {
			formatInvalidParamResponse(resultMap);
		} else {
			Record r = Db.findFirst("select count(1) as con from ab_order_dabiao where sn=? and ddzt=?",sn,ddzt);
			if(r.getLong("con")>0){
				Db.update("update ab_order_dabiao set sj=? where sn=? and ddzt=?",sj,sn,ddzt);
			}else{
				Db.update("insert into ab_order_dabiao(id,sn,ddzt,sj) values('"+(UUID.randomUUID().toString().replaceAll("-", ""))+"',?,?,?",sn,ddzt,sj);
			}
			resultMap.put("result", RESULT_SUCCESS);
			resultMap.put("msg", "操作成功");
		}
		
		renderJson(resultMap);
	}
	public void db() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String uid = this.getPara("uid");
		
		String sn = StringUtil.toStr(this.getPara("sn"));
		String ddzt = this.getPara("ddzt");
		if (StringUtils.isEmpty(uid)||StringUtils.isEmpty(ddzt)||StringUtils.isEmpty(sn)) {
			formatInvalidParamResponse(resultMap);
		} else {
			List<Record> r = Db.find("select ddzt,sn from ab_order_dabiao where sn=? ",sn);
			resultMap.put("data", r);
			resultMap.put("result", RESULT_SUCCESS);
			resultMap.put("msg", "操作成功");
		}
		
		renderJson(resultMap);
	}
}
