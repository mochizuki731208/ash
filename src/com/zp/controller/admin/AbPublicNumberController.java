package com.zp.controller.admin;

import java.util.List;

import com.jfinal.core.Controller;
import com.zp.entity.AbFmcar;
import com.zp.entity.SysPNumber;
import com.zp.entity.SysUser;

public class AbPublicNumberController extends Controller {

	public void index() {

//		this.setSessionAttr("abuser", null); // 先清除前台用户

		SysUser user = (SysUser) this.getSessionAttr("user"); // 后台用户

		if (user == null) { // 登录
			forwardAction("/25console");
		} else {
			List<SysPNumber> carlist = SysPNumber.dao.findByUserId(user
					.getStr("id"));
			this.setAttr("pnumberlist", carlist);
			this.setAttr("usertype", "admin");
			render("/admin/pnumber/my-trucks.html");
		}
	}
	
	public void del(){
		System.out.println("del");
		
		System.out.println("id:"+this.getPara("id"));

		SysUser user = (SysUser) this.getSessionAttr("user"); // 后台用户

		if (user == null) { // 登录
			forwardAction("/25console");
		} else {
			System.out.println("id:"+this.getPara("id"));
			String id = this.getPara("id");
			SysPNumber.dao.deleteById(id);
			
			List<SysPNumber> carlist = SysPNumber.dao.findByUserId(user
					.getStr("id"));
			this.setAttr("pnumberlist", carlist);
			this.setAttr("usertype", "admin");
			render("/admin/pnumber/my-trucks.html");
		}
	}

	public void add() {

		SysUser user = (SysUser) this.getSessionAttr("user"); // 后台用户

		if (user == null) { // 登录
			forwardAction("/25console");
		} else {
			
			System.out.println("add");
			SysPNumber pNumber = this.getModel(SysPNumber.class,"sys_pnumber");
			System.out.println("token:"+pNumber.get("token"));
			System.out.println("name:"+pNumber.get("name"));
			
			pNumber.set("uid", user.getStr("id"));
			
			pNumber.save();
			
			List<SysPNumber> carlist = SysPNumber.dao.findByUserId(user
					.getStr("id"));
			this.setAttr("pnumberlist", carlist);
			this.setAttr("usertype", "admin");
			render("/admin/pnumber/my-trucks.html");
		}
	}
}
