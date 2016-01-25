package com.zp.controller.app;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import com.jfinal.core.Controller;
import com.zp.entity.AbFmcar;
import com.zp.entity.AbFmcarOrder;
import com.zp.entity.AbSjPosition;
import com.zp.entity.SysUser;
import com.zp.tools.SmsMessage;
import com.zp.tools.StringUtil;

public class AppFmcarOrdersController extends AbsAppController {

	public static final String REG_CONTENT = "尊敬的司机朋友，您有新的货运订单，请注意查看，谢谢。订单详情：";

	/**
	 * 熟车下单
	 */
	public void orderCar() {
		System.out.println("orderCar");
		String cardid = getPara("cardid");
		String uid = getPara("uid");
		String loadTimeStr = this.getPara("load_time"); //
		String send_sms_flag = this.getPara("send_sms_flag");
		String sms_content = getPara("sms_content");

		String start_province_name = getPara("start_province_name");
		String start_city_name = getPara("start_city_name");
		String start_county_name = getPara("start_county_name");
		String start_addr = getPara("start_addr");

		String arr_province_name = getPara("arr_province_name");
		String arr_city_name = getPara("arr_city_name");
		String arr_county_name = getPara("arr_county_name");
		String arr_addr = getPara("arr_addr");

		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (!checkParam(cardid, uid, loadTimeStr, start_addr, arr_addr)) {

			formatInvalidParamResponse(resultMap);
		} else {
			try {
				long loadTime = 0;
				try {
					loadTime = Long.valueOf(loadTimeStr);
				} catch (Exception e) {
					e.printStackTrace();
					formatInvokeFailResponse(e, resultMap);
					renderJson(resultMap);
					return;
				}
				System.out.println("send_sms_flag===" + send_sms_flag);
				AbFmcar car = AbFmcar.dao.findById(cardid);
				AbFmcarOrder order = null;
				if (car != null) {
					String car_length = car.getStr("length");
					String car_type = car.getStr("type");

					order = new AbFmcarOrder();
					order.set("load_time", new Date(loadTime));
					order.set("car_length", car_length);
					order.set("car_type", car_type);
					order.set("car_id", cardid);

					order.set("start_province_name", start_province_name);
					order.set("start_city_name", start_city_name);
					order.set("start_county_name", start_county_name);
					order.set("start_addr", start_addr);

					order.set("arr_province_name", arr_province_name);
					order.set("arr_city_name", arr_city_name);
					order.set("arr_county_name", arr_county_name);
					order.set("arr_addr", arr_addr);

				} else {
					System.out.println("car is null respond with cardid");
					resultMap.put("result", -1);
					resultMap.put("msg", "the car not exist with the cardid");
					renderJson(resultMap);
					return;
				}
				String id = StringUtil.getRandString32();
				order.set("id", id);
				order.set("create_id", uid);
				order.set("create_time", new Date());

				order.set("status", "待成交");
				order.save();
				// 发短信
				if ("on".equals(send_sms_flag)) {
					String content = REG_CONTENT
							+ "起始地："
							+ order.getStr("start_province_name")
							+ order.getStr("start_city_name")
							+ order.getStr("start_county_name")
							+ order.getStr("start_addr")
							+ "，到达地："
							+ order.getStr("arr_province_name")
							+ order.getStr("arr_city_name")
							+ order.getStr("arr_county_name")
							+ order.getStr("arr_addr")
							+ "，装货时间："
							+ DateFormatUtils.format(
									order.getDate("load_time"),
									"yyyy-MM-dd HH:mm") + "，联系电话："
							+ order.getStr("mobile") + "，请先和货主确定";
					if (sms_content != null) {
						content += "   " + sms_content;
					}
					if (car != null) {
						String mobile = car.getStr("mobile");

						if (StringUtils.isNotBlank(mobile)) {

							SmsMessage.SendMessage(mobile, content);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				formatInvokeFailResponse(e, resultMap);
				renderJson(resultMap);
				return;
			}
			formatSuccessResponse(resultMap);
		}
		renderJson(resultMap);
	}

	/**
	 * siji xinxi
	 */
	public void driverPosition() {
		String mobile = getPara("mobile");
		String uid = getPara("uid");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (!checkParam(mobile, uid)) {
			formatInvalidParamResponse(resultMap);
		} else {
			SysUser user = SysUser.dao.findFirst(
					"select * from sys_user where mobile=? and role_id=?",
					mobile, "106");
			
			if (user != null) {
				// resultMap.put(, value)
				AbSjPosition sjPosition = AbSjPosition.dao.findFirst("select * from ab_sj_position where sjid=? order by dqsj desc limit 1",user.getStr("id"));
				if(sjPosition == null ){
					sjPosition = new AbSjPosition();
					resultMap.put("result", 101);
					resultMap.put("msg", "there has no position");
				}else{
					resultMap.put("jz", user.getStr("jz"));
					resultMap.put("sfrzzt", user.getStr("sfrzzt"));
					resultMap.put("xsz", user.getStr("xsz"));
					resultMap.put("sfz", user.getStr("sfz"));
					BigDecimal rate = user.getBigDecimal("rate");
					double drate = 0 ;
					if( rate != null )
						drate = rate.doubleValue();
					resultMap.put("rate", drate);
					Integer credittype = user.getInt("credittype");
					if(credittype == null )
						credittype = new Integer(0);
					resultMap.put("credittype", credittype.intValue());
					resultMap.put("jd",sjPosition.getStr("jd"));
					resultMap.put("wd",sjPosition.getStr("wd"));
					resultMap.put("dqsj",sjPosition.getStr("dqsj"));
					formatSuccessResponse(resultMap);
				}
				// `jz` varchar(100) DEFAULT NULL COMMENT '驾照',
				// `sfrzzt` char(1) DEFAULT '0' COMMENT
				// '认证状态[0-未认证、1-已提交、2-审核通过、3-认证不通过]',
				// `xsz` varchar(100) DEFAULT NULL COMMENT '行驶证',
				// `sfz` varchar(100) DEFAULT NULL COMMENT '身份证',
				// `rate` decimal(14,4) DEFAULT '0.0000' COMMENT '评价分数',
				// `credittype` int(11) DEFAULT '0' COMMENT '信用等级',
				
			} else {
				resultMap.put("result", 100);
				resultMap.put("msg", "司机未注册");
			}
		}
		renderJson(resultMap);
	}
	
	/**
	 * siji xinxi
	 */
	public void driverInfo() {
		String mobile = getPara("mobile");
		String uid = getPara("uid");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (!checkParam(mobile, uid)) {
			formatInvalidParamResponse(resultMap);
		} else {
			SysUser user = SysUser.dao.findFirst(
					"select * from sys_user where mobile=? and role_id=?",
					mobile, "106");
			if (user != null) {
				// resultMap.put(, value)
				resultMap.put("jz", user.getStr("jz"));
				resultMap.put("sfrzzt", user.getStr("sfrzzt"));
				resultMap.put("xsz", user.getStr("xsz"));
				resultMap.put("sfz", user.getStr("sfz"));
				resultMap.put("rate", user.getStr("rate"));
				resultMap.put("credittype", user.getStr("credittype"));
				resultMap.put("is_busy", "0");
				// `jz` varchar(100) DEFAULT NULL COMMENT '驾照',
				// `sfrzzt` char(1) DEFAULT '0' COMMENT
				// '认证状态[0-未认证、1-已提交、2-审核通过、3-认证不通过]',
				// `xsz` varchar(100) DEFAULT NULL COMMENT '行驶证',
				// `sfz` varchar(100) DEFAULT NULL COMMENT '身份证',
				// `rate` decimal(14,4) DEFAULT '0.0000' COMMENT '评价分数',
				// `credittype` int(11) DEFAULT '0' COMMENT '信用等级',
				formatSuccessResponse(resultMap);
			} else {
				resultMap.put("result", 100);
				resultMap.put("msg", "司机未注册");
			}
		}
		renderJson(resultMap);
	}

	/**
	 * 取消订单
	 */
	public void cancelOrder() {
		System.out.println("cancelOrder hello");
		String id = getPara("orderid");
		String uid = this.getPara("uid");

		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (!checkParam(id, uid)) {
			formatInvalidParamResponse(resultMap);
		} else {
			SysUser user = SysUser.dao.findById(uid);
			if (user == null) {
				resultMap.put("result", -1);
				resultMap.put("msg", "uid error");
			}

			// AbFmcarOrder.dao.findById(id);
			// TODO check
			System.out.println("id=" + id);
			AbFmcarOrder.dao.deleteById(id);
			formatSuccessResponse(resultMap);
		}
		renderJson(resultMap);
	}

	// public void cancelOrder(){
	// Map<String, Object> resultMap = new HashMap<String, Object>();
	// resultMap.put("result", 0);
	// resultMap.put("msg", "success");
	// renderJson(resultMap);
	// }

	public void hello() {
		renderJson(true);
	}

}
