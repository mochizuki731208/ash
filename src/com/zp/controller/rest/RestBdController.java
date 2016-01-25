package com.zp.controller.rest;
import java.math.BigDecimal;
import java.text.NumberFormat;
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
import com.zp.entity.AbOrder;
import com.zp.entity.AbOrderItemchargeoff;
import com.zp.entity.AbOrderPosition;
import com.zp.entity.AbOrderQuote;
import com.zp.entity.AbOrderSms;
import com.zp.entity.AbOrderStateImg;
import com.zp.entity.AbPolicy;
import com.zp.entity.AbPolicyRate;
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

public class RestBdController extends AbstractRestController {

	public void save_policy() {
		
		String uid = this.getPara("uid");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try{
		if (StringUtils.isEmpty(uid)) {
			formatInvalidParamResponse(resultMap);
		} else {

		AbPolicy abPolicy = new AbPolicy();
		String id = StringUtil.getUuid32();
		abPolicy.set("id", id);

		abPolicy.set("insurant", getPara("insurant"));
		abPolicy.set("insurant_tel", getPara("insurant_tel"));
		abPolicy.set("type_insurant", getPara("type_insurant"));
		abPolicy.set("goods_p_type", getPara("goods_p_type"));
		abPolicy.set("goods_s_type", getPara("goods_s_type"));
		abPolicy.set("goods_name", getPara("goods_name"));
		abPolicy.set("packing", getPara("packing"));
		abPolicy.set("goods_num", getPara("goods_num"));
		abPolicy.set("order_sn", getPara("order_sn"));
		abPolicy.set("transport_type", getPara("transport_type"));
		abPolicy.set("transport_name", getPara("transport_name"));
		abPolicy.set("car_no", getPara("car_no"));
		abPolicy.set("start_site_province",
				getPara("start_site_province"));
		abPolicy.set("start_site_city", getPara("start_site_city"));
		abPolicy.set("start_site_country",
				getPara("start_site_country"));
		abPolicy.set("transfer_site_province",
				getPara("transfer_site_province"));
		abPolicy.set("transfer_site_city",
				getPara("transfer_site_city"));
		abPolicy.set("transfer_site_country",
				getPara("transfer_site_country"));
		abPolicy
				.set("end_site_province", getPara("end_site_province"));
		abPolicy.set("end_site_city", getPara("end_site_city"));
		abPolicy.set("end_site_country", getPara("end_site_country"));
		abPolicy.set("start_time", getPara("start_time"));
		abPolicy.set("zxtk", getPara("zxtk"));
		abPolicy.set("fjtk", getPara("fjtk"));
		String quota = getPara("policy_quota");
		double f_quota = Double.parseDouble(quota);
		abPolicy.set("policy_quota", quota);
		
		// 查询费率
		AbPolicyRate rate = AbPolicyRate.dao.findFirst("select * from ab_policy_rate");
		double selft_rate = Double.parseDouble(rate.get("to_self_rate").toString());
		double insurer_rate= Double.parseDouble(rate.get("to_insurer_rate").toString());
		
		NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
		
		abPolicy.set("policy_cost", nf.format(f_quota * selft_rate / 100));
		abPolicy.set("policy_insurer", nf.format(f_quota * insurer_rate /100));
		abPolicy.set("policy_status", "01");
		abPolicy.set("opt_date", DateUtil.getCurrentDate());
		abPolicy.set("user_id", uid);
			if (abPolicy.save()) {
				resultMap.put("result", RESULT_SUCCESS);
				resultMap.put("msg", "下单成功");
			} else {
				resultMap.put("result", RESULT_FAIL);
				resultMap.put("msg", "下单失败");
			}
		}
		} catch (Exception e) {
			resultMap.put("result", RESULT_FAIL);
			resultMap.put("msg", "下单失败");
		}
		renderJson(resultMap);
	}
	public void update_policy() {
		String policy_id = this.getPara("policy_id");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (StringUtils.isEmpty(policy_id)) {
			formatInvalidParamResponse(resultMap);
		} else {
		AbPolicy policy = AbPolicy.dao.findById(policy_id);
		policy.set("policy_status", "02");// 已支付
		if(policy.update()) {
			resultMap.put("result", RESULT_SUCCESS);
			resultMap.put("msg", "支付成功");
		} else {
			resultMap.put("result", RESULT_FAIL);
			resultMap.put("msg", "支付失败");
		}
		}
		renderJson(resultMap);
	}
	
	public void policyDetail() {
		
		String id = this.getPara("id");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (StringUtils.isEmpty(id)) {
			formatInvalidParamResponse(resultMap);
		} else {
			AbPolicy policy = AbPolicy.dao.findById(id);
			resultMap.put("data",policy);
			resultMap.put("result", RESULT_SUCCESS);
			resultMap.put("msg", "操作成功");
		}
		// showdd.html
		renderJson(resultMap);
	}
	public void list() {
		
		String uid = this.getPara("uid");
		Integer pi = this.getParaToInt("pi");
		Integer ps = this.getParaToInt("ps");
		String status = this.getPara("status");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (StringUtils.isEmpty(uid)||pi == null || ps == null || uid == null || pi < 1 || ps < 1 ) {
			formatInvalidParamResponse(resultMap);
		} else {
			String query_sql = "select * from ab_policy b where b.user_id='" + uid + "' ";
			String order_sql = " order by b.opt_date desc";
			// 查询所有保单
			PageUtil all_listpage = DbPage.paginate(
					getStart(pi, ps), ps, query_sql + order_sql);
			
			if(StringUtils.isEmpty(status)||StringUtils.equals("00", status)){
				all_listpage = DbPage.paginate(
						getStart(pi, ps), ps, query_sql + order_sql);
			}else{
				all_listpage = DbPage.paginate(
						getStart(pi, ps), ps, query_sql +" and b.policy_status'"+ status + order_sql);
			}
			resultMap.put("data", all_listpage.getList());
			resultMap.put("result", RESULT_SUCCESS);
			resultMap.put("msg", "查询成功");
		}
		renderJson(resultMap);
	}
}
