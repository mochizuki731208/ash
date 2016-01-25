package com.zp.controller.admin;

import java.util.List;

import com.jfinal.core.Controller;
import com.zp.entity.AbFmcar;
import com.zp.entity.SysOrderAlert;
import com.zp.entity.SysPNumber;
import com.zp.entity.SysUser;

public class AbOrderAlertController extends Controller {

	public void index() {

//		this.setSessionAttr("abuser", null); // 先清除前台用户

		SysUser user = (SysUser) this.getSessionAttr("user"); // 后台用户

		if (user == null) { // 登录
			forwardAction("/25console");
		} else {
//			List<SysPNumber> carlist = SysPNumber.dao.findByUserId(user
//					.getStr("id"));
//			this.setAttr("pnumberlist", carlist);
			List<SysOrderAlert> alertLists = SysOrderAlert.dao.findByUserId(user.getStr("id"));
			if(alertLists == null || alertLists.size() == 0 ){
				this.setAttr("alert", "无");
			}else{
				this.setAttr("alert", alertLists.get(0).getStr("name"));
			}
			render("/admin/alert/alert.html");
		}
	}
	
	public void change(){
		System.out.println("change");
		

		SysUser user = (SysUser) this.getSessionAttr("user"); // 后台用户

		if (user == null) { // 登录
			forwardAction("/25console");
		} else {
			
			String name = this.getPara("name");
			
			
			List<SysOrderAlert> alertLists = SysOrderAlert.dao.findByUserId(user.getStr("id"));
			
			
			if(alertLists == null || alertLists.size() == 0 ){
				SysOrderAlert alert = new SysOrderAlert();
				alert.set("name", name);
				alert.set(SysOrderAlert.SOUND_PATH, "/res/alert/alert.mp3");
				alert.set(SysOrderAlert.UID, user.getStr("id"));
				
				alert.save();
				
				this.setAttr("alert", name);
			}else{
				SysOrderAlert alert = alertLists.get(0);
				alert.set("name", name);
				alert.set(SysOrderAlert.SOUND_PATH, "/res/alert/alert.mp3");
				alert.set(SysOrderAlert.UID, user.getStr("id"));
				alert.update();
				this.setAttr("alert", alertLists.get(0).getStr("name"));
			}
			render("/admin/alert/alert.html");
		}
	}


}
