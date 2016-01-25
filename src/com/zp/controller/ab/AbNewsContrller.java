package com.zp.controller.ab;

import com.jfinal.core.Controller;
import com.zp.entity.CmsContent;

public class AbNewsContrller extends Controller {
	public void index(){
		String url = this.getPara(0);
		String id = this.getPara(1);
		CmsContent vo = CmsContent.dao.findById(id);
		this.setAttr("vo", vo);
		this.setAttr("id", id);
		String str_newurl = "/ab/news/"+url+".html";
		render(str_newurl);
	}
}
