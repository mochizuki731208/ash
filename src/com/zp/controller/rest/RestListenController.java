package com.zp.controller.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.zp.entity.AbListenOrder;

/**
 *发票
 * 
 */
public class RestListenController extends AbstractRestController {
	/**
	 * 发送信息
	 */
	public void update() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String uid = this.getPara("uid");
		String citys = this.getPara("city");

		if (StringUtils.isBlank(uid) || StringUtils.isBlank(citys)) {
			formatInvalidParamResponse(resultMap);
		} else {
			AbListenOrder listen = new AbListenOrder();
			listen.delByUserId(uid);
			String[] arr = citys.split(",");
			for(String str : arr){
				AbListenOrder tmp = new AbListenOrder();
				tmp.set("id", UUID.randomUUID().toString().replaceAll("-", ""));
				tmp.set("user_id", uid);
				tmp.set("city_name", str);
				tmp.save();
			}
						resultMap.put("result", RESULT_SUCCESS);
						resultMap.put("msg", "操作成功");
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
	
		if (StringUtils.isBlank(uid)) {
			formatInvalidParamResponse(resultMap);
		} else {
			AbListenOrder listen = new AbListenOrder();
			resultMap.put("data", listen.findByUserId(uid));
			resultMap.put("result", RESULT_SUCCESS);
			resultMap.put("msg", "查询成功");
		}
		renderJson(resultMap);
	}

}
