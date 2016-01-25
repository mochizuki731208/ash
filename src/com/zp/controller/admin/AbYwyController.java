package com.zp.controller.admin;

import java.io.File;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.zp.entity.SysUser;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.interceptor.AccessAdminInterceptor;
import com.zp.tools.CityAttachAdminUtil;
import com.zp.tools.DateUtil;
import com.zp.tools.StringUtil;

public class AbYwyController extends Controller {
	/**
	 * 显示所有会员信息
	 */
	@Before(AccessAdminInterceptor.class)
	public void listywycx(){
		String mc  = StringUtil.toStr(this.getPara("mc"));
		String mobile  = StringUtil.toStr(this.getPara("mobile"));
		String xxdz  = StringUtil.toStr(this.getPara("xxdz"));
		String loginid  = StringUtil.toStr(this.getPara("loginid"));
		
		String sql= "select * from sys_user where role_id='106' ";
		
		if(loginid.length()>0){
			sql+=" and loginid like '%"+loginid+"%'";
		}
		
		if(mc.length()>0){
			sql+=" and mc like '%"+mc+"%'";
		}
		if(mobile.length()>0){
			sql+=" and mobile like '%"+mobile+"%'";
		}
		
		SysUser u = this.getSessionAttr("user");
//		if(u!=null&&"103".equals(u.get("role_id"))){
//			sql+=" and (city_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("id")+"') or area_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("id")+"'))";
//		}else if("104".equals(u.get("role_id"))){//普通员工
//			sql+=" and (city_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("p_id")+"') or area_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("p_id")+"'))";
//		}
		if(u.getStr("id").length() > 0){
			sql += " and (city_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + ")";
			sql += " or area_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + "))";
		}
		
		if(xxdz.length()>0){
			sql+=" and xxdz like '%"+xxdz+"%'";
		}
		
		sql+=" order by create_date desc";
		
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		
		this.keepPara();
		this.setAttr("listpage", listpage);
		this.setAttr("xxdz", xxdz);
		this.setAttr("loginid", loginid);
		
		this.render("/admin/hy/listhycx.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void listywy_tj(){
		String mc  = StringUtil.toStr(this.getPara("mc"));
		String xxdz  = StringUtil.toStr(this.getPara("xxdz"));
		String loginid  = StringUtil.toStr(this.getPara("loginid"));
		// 按照角色查询
		String sys_role = StringUtil.toStr(getPara(0));
		
		String sql= "select * from sys_user where 1=1 ";
		
		if(sys_role.length() > 0) {
			sql+=" and role_id='" + sys_role + "' ";
		} else {
			// 默认查询司机
			sql += " and role_id='106' ";
		}
		
		if(loginid.length()>0){
			sql+=" and loginid like '%"+loginid+"%'";
		}
		
		if(mc.length()>0){
			sql+=" and mc like '%"+mc+"%'";
		}
		
		SysUser u = this.getSessionAttr("user");
//		if(u!=null&&"103".equals(u.get("role_id"))){
//			sql+=" and (city_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("id")+"') or area_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("id")+"'))";
//		}else if("104".equals(u.get("role_id"))){//普通员工
//			sql+=" and (city_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("p_id")+"') or area_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("p_id")+"'))";
//		}
		if(u.getStr("id").length() > 0){
			sql += " and (city_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + ")";
			sql += " or area_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + "))";
		}
		
		if(xxdz.length()>0){
			sql+=" and xxdz like '%"+xxdz+"%'";
		}
		
		sql+=" order by create_date desc";
		
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		
		this.setAttr("listpage", listpage);
		this.setAttr("mc", mc);
		this.setAttr("xxdz", xxdz);
		this.setAttr("loginid", loginid);
		
		this.render("/admin/hy/listywy_tj.html");
	}
	
	
	@Before(AccessAdminInterceptor.class)
	public void listywyczcx(){
		String mc  = StringUtil.toStr(this.getPara("mc"));
		String loginid  = StringUtil.toStr(this.getPara("loginid"));
		String status  = StringUtil.toStr(this.getPara("status"));
		String sql= "select * from log_user_deposit";
		
		if(loginid.length()>0){
			sql+=" and loginid like '%"+loginid+"%'";
		}
		
		if(mc.length()>0){
			sql+=" and mc like '%"+mc+"%'";
		}
		
		if(status.length()>0){
			sql+=" and status = '"+status+"'";
		}
		
		sql+=" order by createtime desc";
		
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		
		this.setAttr("listpage", listpage);
		this.setAttr("mc", mc);
		this.render("/admin/hy/listhyczcx.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void editywy(){
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
	public void saveywy(){
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
	public void delywy(){
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
}