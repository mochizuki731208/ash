package com.zp.controller.admin;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.core.Controller;
import com.zp.entity.AbCityarea;
import com.zp.entity.AbTcConfig;
import com.zp.entity.AbTcExpressOrder;
import com.zp.entity.SysUser;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.tools.CityAttachAdminUtil;
import com.zp.tools.StringUtil;

@SuppressWarnings("static-access")
public class TcController extends Controller {

	public void orders() {
		String sn = this.getPara("sn");
		String shenhe = this.getPara("shenhe");
		int pageSize = 10;
		if(this.getPara("pageSize") != null){
			pageSize = this.getParaToInt("pageSize");
		}
		this.setAttr("sn", sn);
		this.setAttr("shenhe", shenhe);
		this.setAttr("pageSize", pageSize);
		String sql = "select o.* from ab_tc_express_order o left join ab_order ao on ao.sn=o.sn where 1=1 ";
		if (sn != null && !sn.equals("")) {
			sql += " and o.sn like '%" + sn + "%'";
		}
		if(shenhe != null && !"".equals(shenhe)){
			if(shenhe.equals("-1"))
				sql += " and o.shenhe is null";
			else
				sql += " and o.shenhe='"+shenhe+"'";
		}
		
		SysUser user = this.getSessionAttr("user");
		String roleId= user.getStr("role_id");
		if("103".equals(roleId)){//区域管理员
			sql += " and ((ao.city_id in (select id from ab_cityarea where user_id='"+user.getStr("id")+"')) or (ao.area_id in (select id from ab_cityarea where user_id='"+user.getStr("id")+"')))";
		}
		
		sql += " order by sn desc";
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1), pageSize, sql);
		this.setAttr("listpage", listpage);
		this.render("/admin/tc/order_list.html");
	}
	
	public void shenhe() {
		String sns = this.getPara("ids");
		String[] snArr = sns.split(",");
		String ok = this.getPara("shenhe");
		boolean flag = false;
		if(snArr != null && snArr.length > 0){
			for(String sn : snArr){
				AbTcExpressOrder o = AbTcExpressOrder.dao.findFirst("select * from ab_tc_express_order where id=?", sn);
				if(o != null){
					o.set(AbTcExpressOrder.SHENHE, "1".equals(ok)?"1":"0");
					flag = o.update();
				}
			}
		}
		this.renderJson(flag);
	}
	public void delOrder() {
		String ids = this.getPara("ids");
		boolean flag = false;
		if(ids != null && ids.length() > 0){
			for(String id : ids.split(",")){
				flag = AbTcExpressOrder.dao.deleteById(id);
			}
		}
		this.renderJson(flag);
	}

	public void cars() {
		String name = this.getPara("name");
		String cityId = this.getPara("c_city_id");
		this.setAttr("c_city_id", cityId);
		String sql = "select c.*,a.mc as city_name from ab_tc_config c left join ab_cityarea a on a.id=c.city_id where type in ('01') "
				+ (name == null ? "" : " and name like '%" + name + "%'") + (cityId == null || "".equals(cityId) ? "" : " and city_id='" + cityId + "'") + " order by listorder,name";
		SysUser user = this.getSessionAttr("user");
		String roleId= user.getStr("role_id");
		if("103".equals(roleId)){//区域管理员
			sql = "select c.*,a.mc as city_name from ab_tc_config c left join ab_cityarea a on a.id=c.city_id where type in ('01') and c.city_id in ("+CityAttachAdminUtil.getCityByAdmin(user.getStr("id"))+")";
			if(cityId!=null && cityId.length() > 0){
				sql += " and c.city_id='" + cityId + "'";
			}
			sql+= (name == null ? "" : " and name like '%" + name + "%'") + " order by listorder,name";
		}
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1), 14, sql);
		this.setAttr("listpage", listpage);
		this.render("/admin/tc/config_list.html");
	}

	public void goods() {
		String name = this.getPara("name");
		String cityId = this.getPara("c_city_id");
		this.setAttr("c_city_id", cityId);
		String sql = "select * from ab_tc_config where type in ('03')"
				+ (name == null ? "" : " and name like '%" + name + "%'") + (cityId == null || "".equals(cityId) ? "" : " and city_id='" + cityId + "'") + " order by listorder,name";
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1), 14, sql);
		this.setAttr("listpage", listpage);
		this.render("/admin/tc/config_list.html");
	}

	public void others() {
		String name = this.getPara("name");
		String cityId = this.getPara("c_city_id");
		this.setAttr("c_city_id", cityId);
		String sql = "select * from ab_tc_config where type not in ('01','03')"
				+ (name == null ? "" : " and name like '%" + name + "%'") + (cityId == null || "".equals(cityId) ? "" : " and city_id='" + cityId + "'") + " order by type";
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1), 14, sql);
		this.setAttr("listpage", listpage);
		this.render("/admin/tc/config_list.html");
	}

	public void deleteConfig() {
		boolean flag = false;
		String id = this.getPara("id");
		AbTcConfig c = AbTcConfig.dao.findById(Integer.parseInt(id));
		flag = c.deleteById(Integer.parseInt(id));
		this.renderJson(flag);
	}

	public void saveConfig() {
		boolean flag = false;
		String id = this.getPara("c_id");
		String key1=this.getPara("key1");
		String key=this.getPara("c_key");
		
		String cname=this.getPara("c_name");
		String cname1=this.getPara("c_name1");
		
		if (id != null && !id.equals("")) {
			AbTcConfig c = AbTcConfig.dao.findById(Integer.parseInt(id));
			c.set("type", this.getPara("c_type"));
			
			if(cname=="自定义"){
				c.set("name", cname1);
			}
			//c.set("name", this.getPara("c_name"));
			c.set("key", key);
			if(key=="自定义"){
				c.set("key", key1);
			}
			c.set("value", this.getPara("c_value"));
			c.set("desc", this.getPara("c_desc"));
			c.set("listorder", this.getPara("c_listorder"));
			c.set("city_id", this.getPara("c_city_id"));
			flag = c.update();
		} else {
			//AbTcConfig c = this.getModel(AbTcConfig.class, "c");
			AbTcConfig c=new AbTcConfig ();
			c.set("type", this.getPara("c_type"));
			c.set("name", this.getPara("c_name"));
			c.set("key", key);
			if(key=="自定义"){
				c.set("key", key1);
			}
			c.set("value", this.getPara("c_value"));
			c.set("desc", this.getPara("c_desc"));
			c.set("listorder", this.getPara("c_listorder"));
			c.set("city_id", this.getPara("c_city_id"));
			flag = c.save();
		}
		this.renderJson(flag);
	}

	public void editConfig() {
		String id = StringUtil.toStr(this.getPara("id"));
		if (id != null && !id.equals("")) {
			AbTcConfig c = AbTcConfig.dao.findById(Integer.parseInt(id));
			this.setAttr("c", c);
		}
		render("/admin/tc/edit.html");
	}
	
	public void cities() {
		SysUser user = this.getSessionAttr("user");
		String roleId= user.getStr("role_id");
		List list = new ArrayList();
		String sql = "select * from ab_cityarea c where length(id)=6";
		if("103".equals(roleId)){//区域管理员
//			sql += " and user_id=?";
			sql += " and id in(" + CityAttachAdminUtil.getCityByAdmin(user.getStr("id")) + ")";
			list = AbCityarea.dao.find(sql);
		}else{//系统管理员
			list = AbCityarea.dao.find(sql);
		}
		this.renderJson(list);
	}
}
