package com.zp.controller.admin;

import java.util.List;

import com.jfinal.core.Controller;
import com.zp.entity.AbFmcar;
import com.zp.entity.SysUser;

public class AbFmcarController extends Controller {
	
	public void index(){
		
		//this.setSessionAttr("abuser", null);  //先清除前台用户
		
		SysUser user = (SysUser) this.getSessionAttr("user");  //后台用户
		
		if( user == null ){ //登录
			forwardAction("/25console");
		}else{
			List<AbFmcar> carlist = AbFmcar.dao.findByUserId(user.getStr("id"),null);
			this.setAttr("carlist", carlist);
			this.setAttr("usertype", "admin");
			render("/ab/fmcar/my-trucks.html");
		}
	}
}
