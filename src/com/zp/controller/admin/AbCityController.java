package com.zp.controller.admin;

import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.zp.entity.AbCityarea;
import com.zp.entity.SysUser;
import com.zp.interceptor.AccessAdminInterceptor;
import com.zp.tools.CityAttachAdminUtil;
import com.zp.tools.StringUtil;
import com.zp.tools.TreeNodeBean;
@SuppressWarnings("static-access")
public class AbCityController extends Controller {

	@Before(AccessAdminInterceptor.class)
	public void tree(){
		//构建树
		SysUser user = (SysUser)this.getSessionAttr("user");
		//首先判断该管理员是超级管理员还是省级管理员。还是市级管理员
		String SQL = "";
		String root = "";
		if("admin".equals(user.getStr("id"))){
			//超级管理员。操作
			SQL = "select distinct t.* from ab_cityarea m,ab_cityarea t where m.id=t.p_id  order by seq_num";
			root = "ROOT";
		}else{
			List<AbCityarea> tempList = AbCityarea.dao.find("select * from ab_cityarea where user_id= '" + user.getStr("id") + "' order by ccm");
			if(null != tempList && !tempList.isEmpty()){
				for(AbCityarea temp : tempList){
					if(SQL.length() > 0){
						SQL += " union ";	
					}
					SQL += "select * from ab_cityarea where id like '" + temp.getStr("id") + "%'";
					root = root.length() ==0 ? temp.getStr("p_id") :root;
				}
			}else{
				this.setAttr("str_Script", null);
				this.render("/admin/city/tree.html");
				return;
			}
		}
		
		
		String sql = "select distinct t.* from ab_cityarea m,ab_cityarea t where m.id=t.p_id";
		if(!"admin".equals(user.getStr("id"))){
			sql += " and t.user_id = '" + user.getStr("id") + "'";
		}
		sql += " order by seq_num";
		List<AbCityarea> list = AbCityarea.dao.find(SQL);
		TreeNodeBean treeNodeBean = new TreeNodeBean("TreeData");
		treeNodeBean.addRootNode(root, "区域分类", false, false);
		String parentID;
		String curtreeID;
		String showText;
		String str_Script = "";

		for (int i = 0; i < list.size(); i++) {
			parentID = list.get(i).getStr("p_id");
			curtreeID = list.get(i).getStr("id");
			showText = list.get(i).getStr("mc");
			treeNodeBean.addTreeNode(parentID,curtreeID,showText,false,false);
		}
		str_Script = treeNodeBean.addTreeTail("div_left");
		this.setAttr("str_Script", str_Script);
		this.render("/admin/city/tree.html");
	}
	
	/**
	 * 获取员工信息列表
	 */
	@Before(AccessAdminInterceptor.class)
	public void listcityuser(){
		SysUser u = this.getSessionAttr("user");
		String sql = "select * from sys_user where p_id=1";
//		if("103".equals(u.get("role_id"))){
//			if(u.getStr("id").length() > 0){
//				sql += " and (city_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + ")";
//				sql += " or area_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + "))";
//			}
//		}
		sql += "order by create_date";
		//获取默认管理员
		List<SysUser> list = SysUser.dao.find(sql);
		this.setAttr("list", list);
		render("/admin/city/listcityuser.html");
	}
	@Before(AccessAdminInterceptor.class)
	public void selectcityadmin(){
		SysUser u = this.getSessionAttr("user");
		String sql = "select * from sys_user where role_id='103'";
		if("103".equals(u.get("role_id"))){
			if(u.getStr("id").length() > 0){
				sql += " and (city_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + ")";
				sql += " or area_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + "))";
			}
		}
		sql += "order by create_date";
		//获取默认管理员
		List<SysUser> list = SysUser.dao.find(sql);
		this.setAttr("list", list);
		render("/admin/city/selectcityadmin.html");
	}
	@Before(AccessAdminInterceptor.class)
	public void savecityadmin(){
		boolean flag = false;
		String user_id = this.getPara("user_id");
		String city_id = this.getPara("city_id");
		SysUser u = SysUser.dao.findById(user_id);
		Db.update("update ab_cityarea set user_id='"+user_id+"',user_mc='"+u.getStr("mc")+"' where id='"+city_id+"'");
		flag = true;
		renderJson(flag);
	}
	
	/**
	 * 城市列表
	 */
	@Before(AccessAdminInterceptor.class)
	public void list(){
		SysUser user = (SysUser)this.getSessionAttr("user");
		String p_id = this.getPara("p_id");
		String text = this.getPara("text");
		if(!"admin".equals(user.getStr("id")) && "区域分类".equals(text)){
			this.setAttr("list", null);
			this.setAttr("p_id", null);
			render("/admin/city/list.html");
			return;
		}
		String sql = "select * from ab_cityarea where p_id='"+p_id+"' ";
//		if(!"admin".equals(user.getStr("id"))){
//			sql += " and user_id = '" + user.getStr("id") + "'";
//		}
		sql += "order by seq_num";
		List<AbCityarea> list = AbCityarea.dao.find(sql);
		this.setAttr("list", list);
		this.setAttr("p_id", p_id);
		render("/admin/city/list.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void edit(){
		String id = this.getPara("id");
		String m = this.getPara("m");
		AbCityarea  vo = new AbCityarea();
		
		if(StringUtil.toStr(id).length()>0){
			vo = vo.dao.findById(id);
		}else{
			vo.set("p_id", this.getPara("p_id"));
			if(this.getPara("p_id").trim().length() >= 6){
				vo.set("type_id", "1");
			}else{
				vo.set("type_id", "0");
			}
		}
		
		this.setAttr("method", m);
		this.setAttr("vo", vo);
		render("/admin/city/edit.html");
	}
	/*
	 * 保存
	 * */
	@Before(AccessAdminInterceptor.class)
	public void save(){
		boolean flag = false;
		AbCityarea  city = this.getModel(AbCityarea.class,"city");
		String method = this.getPara("m");
		
		if(city!=null&&city.get("p_id")!=null){
			//获取上级菜单的层次码，对层次码进行加1操作。
			AbCityarea s = new AbCityarea();
			city.set("ccm", s.dao.findById(city.getStr("p_id")).getInt("ccm")+1);
		}else{
			city.set("ccm", 0);
		}
		
		if(!("add".equals(method))){
			city.update();
		}else{
			//获取最大的ID号加1操作
			if(Db.findFirst("select count(id) cnt from ab_cityarea where p_id='"+city.getStr("p_id")+"'").getLong("cnt").intValue()<1){
				city.set("id",StringUtil.getStrSeqCode(true, city.getStr("p_id"), 3));
			}else{
				String maxid = Db.findFirst("select id from ab_cityarea where p_id='"+city.getStr("p_id")+"' order by id desc").getStr("id");
				city.set("id",StringUtil.getStrSeqCode(false, maxid, 3));
			}
			
			city.save();
		}
		flag = true;
		renderJson(flag);
	}
	
	/*
	 * 删除
	 * */
	@Before(AccessAdminInterceptor.class)
	public void delete(){
		String id = this.getPara("id");
		boolean flag = AbCityarea.dao.deleteById(id);
		this.renderJson(flag);
	}
	
	/*
	 * 物流订单的审核
	 * */
	@Before(AccessAdminInterceptor.class)
	public void tcshenhe(){
		boolean flag = false;
		try {
			String id = this.getPara("id");
			String shenhe = this.getPara("shenhe");
			AbCityarea c = AbCityarea.dao.findById(id);
			c.set("tc_shenhe", shenhe);
			flag = c.update();
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		this.renderJson(flag);
	}
	
}
