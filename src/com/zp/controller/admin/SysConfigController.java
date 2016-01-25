package com.zp.controller.admin;

import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.mysql.jdbc.StringUtils;
import com.zp.entity.SysConfig;
import com.zp.entity.SysUser;

public class SysConfigController extends Controller {
	
	/**
	 * @作者：李金伟
	 * @公司：郑州兆普软件科技有限公司
	 * @日期：2014年12月29日01:02:35
	 * @描述：短信接口
	 */
	public void editsms(){
		SysConfig vo =SysConfig.dao.findById("01");
		if(vo==null){
			vo = new SysConfig();
			vo.set("id", "01");
		}
		this.setAttr("vo", vo);
		render("/admin/config/editsms.html");
	}
	/**
	 * @作者：李金伟
	 * @公司：郑州兆普软件科技有限公司
	 * @日期：2014年12月29日01:02:35
	 * @描述：支付接口
	 */
	public void editalipay(){
		SysUser u = (SysUser) this.getRequest().getSession()
				.getAttribute("user");
		String user_id = u.getStr(SysUser.ID);
		SysConfig vo =SysConfig.dao.findFirst("select * from sys_config where c6='" + user_id + "'");
		if(vo==null){
			vo = new SysConfig();
			vo.set("c6", user_id);
		}
		this.setAttr("vo", vo);
		render("/admin/config/editalipay.html");
	}
	
	/**
	 * @作者：李金伟
	 * @公司：郑州兆普软件科技有限公司
	 * @日期：2014年12月29日01:02:35
	 * @描述：电子邮箱
	 */
	public void editemail(){
		SysConfig vo =SysConfig.dao.findById("03");
		if(vo==null){
			vo = new SysConfig();
			vo.set("id", "03");
		}
		this.setAttr("vo", vo);
		render("/admin/config/editemail.html");
	}
	public void edityxrz(){
		SysConfig vo =SysConfig.dao.findById("04");
		if(vo==null){
			vo = new SysConfig();
			vo.set("id", "04");
		}
		this.setAttr("vo", vo);
		render("/admin/config/edityxrz.html");
	}
	public void editfwf(){
		SysConfig vo =SysConfig.dao.findById("05");
		if(vo==null){
			vo = new SysConfig();
			vo.set("id", "05");
		}
		this.setAttr("vo", vo);
		render("/admin/config/editfwf.html");
	}
	
	public void editsygjc(){
		SysConfig vo =SysConfig.dao.findById("06");
		if(vo==null){
			vo = new SysConfig();
			vo.set("id", "05");
		}
		this.setAttr("vo", vo);
		render("/admin/config/editsygjc.html");
	}
	
	public void saveconfig(){
		boolean flag = false;
		SysConfig  config = this.getModel(SysConfig.class,"config");
		
		if(!StringUtils.isNullOrEmpty(config.getStr("id"))){
			if(Db.findFirst("select count(id) cnt from sys_config where id='"+config.getStr("id")+"'").getLong("cnt").intValue()>0){
				config.update();
			}else{
				config.save();
			}
		}else{
			List<SysConfig> sc = SysConfig.dao.find("select * from sys_config");
			long length = sc.size();
			config.set("id", length + 1);
			SysUser u = (SysUser) this.getRequest().getSession()
					.getAttribute("user");
			String user_id = u.getStr(SysUser.ID);
			config.set("c6", user_id);
			config.save();
		}
		
		flag = true;
		renderJson(flag);
	}
	public void edittxconfig(){
		SysConfig vo =SysConfig.dao.findById("06");
		if(vo==null){
			vo = new SysConfig();
			vo.set("id", "06");
		}
		this.setAttr("vo", vo);
		render("/admin/config/edittxconfig.html");
	}
}
