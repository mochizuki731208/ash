package com.zp.controller.ab;

import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.zp.entity.AbSubject;
import com.zp.tools.StringUtil;

public class AbSubjectController extends Controller{
	public void save(){
		Db.update("insert into ab_cityarea(name) values ('lijinwei');");
		//Db.update("insert into ab_cityarea(id) values ('lijinwei');");
	}
	
	public void getSubSubject(){
		String userType = StringUtil.toStr(this.getPara("userType"));
		List<AbSubject> subList = AbSubject.dao.find("select * from ab_subject where p_id='" + userType + "' order by ccm,seq_num");
		this.renderJson(subList);
	}
}
