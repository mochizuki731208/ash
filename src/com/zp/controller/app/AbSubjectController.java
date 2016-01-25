package com.zp.controller.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.core.Controller;
import com.zp.entity.AbSubject;

public class AbSubjectController extends Controller {
	
	public void getIndexClass(){
		Map<String, String> fields = new HashMap<String, String>();
		
		Map<String, String[]> map = getParaMap();
		for (String key : map.keySet()) {
			fields.put(key, getPara(key));
		}
		
		List<AbSubject> list = AbSubject.dao.findByFields(fields);
		renderJson(list);
	}
}
