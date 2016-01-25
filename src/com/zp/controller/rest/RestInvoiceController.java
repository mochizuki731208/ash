package com.zp.controller.rest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.zp.entity.AbInvoice;
import com.zp.entity.AbOrder;

/**
 *发票
 * 
 */
public class RestInvoiceController extends AbstractRestController {
	/**
	 */
	public void apply() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String uid = this.getPara("uid");
		String amount = this.getPara("amount");
		String header = this.getPara("header");
		String content = this.getPara("content");
		String phone = this.getPara("phone");
		String name = this.getPara("name");
		String address = this.getPara("address");
		Integer type = this.getParaToInt("type");

		if (StringUtils.isBlank(uid)  
				|| StringUtils.isBlank(header) || StringUtils.isBlank(content)
				|| StringUtils.isBlank(phone) || StringUtils.isBlank(name)
				|| StringUtils.isBlank(address) || StringUtils.isBlank(amount)) {
			formatInvalidParamResponse(resultMap);
		} else {


						AbInvoice invoice = new AbInvoice();
						invoice.set("uid", uid);
						invoice.set("header", header);
						invoice.set("amount", Double.parseDouble(amount));
						invoice.set("content", content);
						invoice.set("status", "0");
						invoice.set("phone", phone);
						invoice.set("name", name);
						invoice.set("id", UUID.randomUUID().toString().replaceAll("-", ""));
						invoice.set("address", address);
						invoice.set("type", type);
						
						invoice.set("idate", new Date());

						if (invoice.save()) {
							resultMap.put("result", RESULT_SUCCESS);
							resultMap.put("msg", "申请成功");
						} else {
							resultMap.put("result", RESULT_FAIL);
							resultMap.put("msg", "申请失败");
						}
		}
		renderJson(resultMap);
	}
	public void qs() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String uid = this.getPara("uid");
		String id = this.getPara("id");
		
		if (StringUtils.isBlank(uid)  
				|| StringUtils.isBlank(id) ) {
			formatInvalidParamResponse(resultMap);
		} else {
			
			
			AbInvoice invoice = null;
			invoice = AbInvoice.dao.findFirst("select * from ab_invoice where uid=? and id=?",uid,id);
			if(invoice!=null){
				if(1 == invoice.getInt("status")){
					invoice.set("status", 2);
					if (invoice.update()) {
						resultMap.put("result", RESULT_SUCCESS);
						resultMap.put("msg", "签收成功");
					} else {
						resultMap.put("result", RESULT_FAIL);
						resultMap.put("msg", "签收失败");
					}
				}else if( 0== invoice.getInt("status")){
					resultMap.put("result", RESULT_FAIL);
					resultMap.put("msg", "没有通过的发票不能签收");
				}else if( 2== invoice.getInt("status")){

					resultMap.put("result", RESULT_FAIL);
					resultMap.put("msg", "发票已签收");
				}
			}else{
				resultMap.put("result", RESULT_FAIL);
				resultMap.put("msg", "发票不存在，请确认");
				
			}
		}
		renderJson(resultMap);
	}

	/**
	 * 读取消息列表
	 */
	public void list() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		// 送发人
		String uid = this.getPara("uid");
	
		String status = this.getPara("status");
		Integer pi = this.getParaToInt("pi");
		Integer ps = this.getParaToInt("ps");
		if (pi == null || ps == null || pi < 1 || ps < 1 || StringUtils.isBlank(uid)) {
			formatInvalidParamResponse(resultMap);
		} else {
			String sql = "select * from ab_invoice where  uid='"
					+ uid + "'";

			if (StringUtils.isNotEmpty(status)) {
				sql = sql + " and status='" + status + "'";
			}
			sql = sql+ " order by idate desc limit ?,?";
			List<Record> data = Db.find(sql, getStart(pi, ps), ps);
			resultMap.put("data", data);
			resultMap.put("result", RESULT_SUCCESS);
		}
		renderJson(resultMap);
	}
	public void max() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		// 送发人
		String uid = this.getPara("uid");
		
		if (StringUtils.isBlank(uid)) {
			formatInvalidParamResponse(resultMap);
		} else {
			String sql = "select sum(ddzje) as zje from ab_order where  xdrid='"
				+ uid + "' and ddzt=5";
			
			 Record data = Db.findFirst(sql);
			resultMap.put("max", data.getBigDecimal("zje") ==null ?0.0:data.getBigDecimal("zje"));
			resultMap.put("result", RESULT_SUCCESS);
		}
		renderJson(resultMap);
	}

}
