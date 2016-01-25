package com.zp.controller.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.core.Controller;
import com.zp.entity.AbMerchant;

public class AbMerchantController extends Controller {
	
	public void getStoreList(){
		Map<String, String> fields = new HashMap<String, String>();
		
		String subject_id = getPara("subject_id");
		if(subject_id != null){
			fields.put("subject_id", subject_id);
		}
		
		List<AbMerchant> list = AbMerchant.dao.findByFields(fields);
		renderJson(list);
	}
}
