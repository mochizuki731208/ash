package com.zp.controller.admin;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.mysql.jdbc.StringUtils;
import com.zp.entity.LogUserDeposit;
import com.zp.entity.SysCzTx;
import com.zp.entity.SysUser;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.interceptor.AccessAdminInterceptor;
import com.zp.tools.CityAttachAdminUtil;
import com.zp.tools.DateUtil;
import com.zp.tools.StringUtil;

public class AbHyController extends Controller {
	/**
	 * 显示所有会员信息
	 */
	@Before(AccessAdminInterceptor.class)
	public void listhycx(){
		String mc  = StringUtil.toStr(this.getPara("mc"));
		String mobile  = StringUtil.toStr(this.getPara("mobile"));
		String xxdz  = StringUtil.toStr(this.getPara("xxdz"));
		String loginid  = StringUtil.toStr(this.getPara("loginid"));
		String userSubType  = StringUtil.toStr(this.getPara("userSubType"));
		
		String sql= "select * from sys_user where role_id='107' ";
		
		if(loginid.length()>0){
			sql+=" and loginid like '%"+loginid+"%'";
		}
		
		if(mc.length()>0){
			sql+=" and mc like '%"+mc+"%'";
		}
		
		if(mobile.length()>0){
			sql+=" and mobile like '%"+mobile+"%'";
		}
		
		if(xxdz.length()>0){
			sql+=" and xxdz like '%"+xxdz+"%'";
		}
		SysUser user = (SysUser)this.getSessionAttr("user");
		if(user.getStr("id").length() > 0){
			sql += " and (city_id in (" + CityAttachAdminUtil.getCityByAdmin(user.getStr("id")) + ")";
			sql += " or area_id in (" + CityAttachAdminUtil.getCityByAdmin(user.getStr("id")) + "))";
		}
		
		if(userSubType.length() > 0){
			sql += " and id in( select userid from  sys_user_type where usertype='1' and usersubtype='" +userSubType + "')"; 
		}
		
		sql+=" order by create_date desc";
		
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		this.keepPara();
		this.setAttr("listpage", listpage);
		this.setAttr("mc", mc);
		this.setAttr("xxdz", xxdz);
		this.setAttr("loginid", loginid);
		this.setAttr("userSubType", userSubType);
		this.render("/admin/hy/listhycx.html");
	}
	public void gethy(){
		List<Record> list = Db.find("SELECT * FROM sys_user");
		renderJson(list);
	}
	@Before(AccessAdminInterceptor.class)
	public void listhyczcx(){
		String mc  = StringUtil.toStr(this.getPara("mc"));
		String loginid  = StringUtil.toStr(this.getPara("loginid"));
		String status  = StringUtil.toStr(this.getPara("status"));
		String sql= "select * from log_user_deposit where 1= 1";
		
		if(loginid.length()>0){
			sql+=" and loginid like '%"+loginid+"%'";
		}
		
		if(mc.length()>0){
			sql+=" and mc like '%"+mc+"%'";
		}
		
		if(status.length()>0){
			sql+=" and status = '"+status+"'";
		}
		SysUser user = (SysUser)this.getSessionAttr("user");
		if(!"admin".equals(user.getStr("id"))){
			sql += " and userid in(select id from sys_user where city_id in (" + CityAttachAdminUtil.getCityByAdmin(user.getStr("id")) + ")";
			sql += " or area_id in (" + CityAttachAdminUtil.getCityByAdmin(user.getStr("id")) + "))";
		}
		
		sql+=" order by createtime desc";
		
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		
		this.setAttr("listpage", listpage);
		this.setAttr("mc", mc);
		this.render("/admin/hy/listhyczcx.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void edithy(){
		String id = this.getPara("id");
		SysUser vo = new SysUser();
		if(StringUtil.toStr(id).length()>0){
			vo = SysUser.dao.findById(id);
		}
		this.setAttr("vo", vo);
		render("/admin/hy/edithy.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void showinfo(){
		SysUser vo = SysUser.dao.findById(this.getPara(0));
		this.setAttr("vo", vo);
		render("/admin/hy/showinfo.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void savehy(){
		boolean flag = false;
		try {
			SysUser  user = this.getModel(SysUser.class,"tb");
			if(user!=null&&user.getStr("id")!=null){
				user.update();
			}else{
				user.set("id", StringUtil.getUuid32());
				user.set("createtime", DateUtil.getCurrentDate());
				user.save();
			}
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(flag);
	}
	
	
	@Before(AccessAdminInterceptor.class)
	public void delhy(){
		boolean flag = false;
		try {
			String id = this.getPara("id");
			
			SysUser user = SysUser.dao.findById(id);
			//删除产品或者服务、删除产品或服务下的图片数据
			SysUser.dao.deleteById(id);
			
			File f = new File(PathKit.getWebRootPath() + "\\upload\\" +user.getStr("logo"));
			if(f.exists()){
				f.delete();
			}
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void doSure(){
		String id = StringUtil.toStr(this.getPara("id"));
		if(!StringUtils.isNullOrEmpty(id)){
			LogUserDeposit lud = LogUserDeposit.dao.findById(id);
			if("1".equals(lud.getStr("status"))){
				renderJson(false);
			}else{
				lud.set("status", 1);
				lud.set("createtime", DateUtil.getCurrentDate());
				lud.update();
				
				SysCzTx sc = SysCzTx.dao.findFirst("select * from sys_cz_tx where dipositid='" + id + "'");
				sc.set("result", 1);
				sc.update();
				
				SysUser user = SysUser.dao.findById(lud.getStr("userid"));
				user.set("zhye", user.getBigDecimal("zhye").add(new BigDecimal(lud.getDouble("czje"))));
				user.update();
				renderJson(true);
			}
		}else{
			renderJson(false);
		}
	}
}
