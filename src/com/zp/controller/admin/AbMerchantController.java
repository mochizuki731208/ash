package com.zp.controller.admin;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.zp.entity.AbCityarea;
import com.zp.entity.AbMerchant;
import com.zp.entity.AbMerchantImg;
import com.zp.entity.AbMerchantLogline;
import com.zp.entity.AbMerchantProduct;
import com.zp.entity.AbProductImg;
import com.zp.entity.AbShywyjl;
import com.zp.entity.AbSubject;
import com.zp.entity.SysUser;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.interceptor.AccessAdminInterceptor;
import com.zp.tools.CityAttachAdminUtil;
import com.zp.tools.DateUtil;
import com.zp.tools.MD5;
import com.zp.tools.StringUtil;

public class AbMerchantController extends Controller {
	@Before(AccessAdminInterceptor.class)
	public void listsjsh(){
		String mc  = StringUtil.toStr(this.getPara("mname"));
		String sjh  = StringUtil.toStr(this.getPara("sjh"));
		String check_status  = StringUtil.toStr(this.getPara("check_status"));
		String subjectid  = StringUtil.toStr(this.getPara("subjectid"));
		//获取当前用户信息
		SysUser u = this.getSessionAttr("user");
		List<SysUser> admin_list = SysUser.dao.find("select * from sys_user where role_id in ('101','103')");
		if("103".equals(u.get("role_id"))){
			admin_list.removeAll(admin_list);
			admin_list.add(u);
			this.setAttr("admin_list", admin_list);
		}else if("104".equals(u.getStr("role_id"))){
			admin_list.removeAll(admin_list);
			this.setAttr("admin_list", admin_list);
		}else{
			this.setAttr("admin_list", admin_list);
		}
		String admin_id = StringUtil.toStr(this.getPara("admin_id"));
		if(admin_id.length() == 0){
			admin_id = u.get("id");
		}
		this.setAttr("admin_id", admin_id);
		String sql = "select * from ab_merchant where id is not null ";
		if(mc.length()>0){
			sql+=" and mc like '%"+mc+"%'";
		}
		if(sjh.length()>0){
			sql+=" and mobile like '%"+sjh+"%'";
		}
		String ds = StringUtil.toStr(this.getPara("ds"));
		if(ds.length() > 0){
			check_status="0";
		}
		if(check_status.length()>0){
			sql+=" and check_status = '"+check_status+"'";
		}
		
//		if(u!=null&&"103".equals(u.get("role_id"))){
//			sql+=" and (city_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("id")+"') or area_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("id")+"'))";
//		}else if("104".equals(u.get("role_id"))){//普通员工
//			sql+=" and (city_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("p_id")+"') or area_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("p_id")+"'))";
//		}
		
		String sql_subject = "select * from ab_subject where 1 = 1";
		if("104".equals(u.getStr("role_id"))){
			sql_subject += " and id in(" + CityAttachAdminUtil.getAuthSubjectIdsByUserId(u.getStr("id")) + ")";
		}else{
			sql_subject += " and p_id in('1019')";
		}
		List<AbSubject> list_subject = AbSubject.dao.find(sql_subject);
		
		if(subjectid.length()>0){
			sql+=" and subject_id ='" + subjectid + "'";
		}else{
			sql+=" and subject_id  in (";

			for(AbSubject abSubject:list_subject){
				sql+=abSubject.getStr("id");
			}
			sql+=" ) ";
		}
//		if(admin_id.length() > 0 && !admin_id.equals("admin")){
//			sql += " and user_id in (select id from sys_user where p_id='" + admin_id+"')";
//		}else{
//			if(!u.getStr("id").equals("admin")){
//				sql += " and user_id in (select id from sys_user where p_id='" + u.getStr("id")+"')";
//			}
//		}
		if(u.getStr("id").length() > 0 && (!"104".equals(u.getStr("role_id")))){
			sql += " and (city_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + ")";
			sql += " or area_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + "))";
		}
		if("104".equals(u.getStr("role_id"))){
			sql += " and subject_id in(" + CityAttachAdminUtil.getAuthSubjectIdsByUserId(u.getStr("id")) + ")";
		}
		sql+="order by create_time desc";
		
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		
		this.keepPara();
		this.setAttr("listpage", listpage);
		this.setAttr("list_subject", list_subject);
		this.setAttr("subjectid", subjectid);
		this.render("/admin/merchant/listsjsh.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void listsjtj(){
		String mc  = StringUtil.toStr(this.getPara("mc"));
		String subjectid  = StringUtil.toStr(this.getPara("subjectid"));
		String sql = "select * from ab_merchant where id is not null ";
		if(mc.length()>0){
			sql+=" and mc like '%"+mc+"%'";
		}
		
		if(subjectid.length()>0){
			sql+=" and subject_id = '"+subjectid+"'";
		}
		sql+="order by id asc";
		
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		
		List<AbSubject> list_subject = AbSubject.dao.find("select * from ab_subject where p_id='ROOT'");
		this.setAttr("listpage", listpage);
		this.setAttr("list_subject", list_subject);
		this.setAttr("mc", mc);
		this.setAttr("subjectid", subjectid);
		this.render("/admin/merchant/listsjtj.html");
	}
	
	
	@Before(AccessAdminInterceptor.class)
	public void edittjsz(){
		String id  = StringUtil.toStr(this.getPara("id"));
		AbMerchant vo = AbMerchant.dao.findById(id);
		this.setAttr("vo", vo);
		this.render("/admin/merchant/editsjtj.html");
	}
	/**
	 * 推荐商家
	 */
	@Before(AccessAdminInterceptor.class)
	public void savesjtjyes(){
		boolean flag = false;
		AbMerchant m =  this.getModel(AbMerchant.class,"tb");
//		m.set("mobile", m.getStr("loginid"));
		m.update();
		flag = true;
		this.renderJson(flag);
	}
	
	/**
	 * 取消对商家的推荐
	 */
	@Before(AccessAdminInterceptor.class)
	public void savesjtjno(){
		String id = this.getPara("id");
		boolean flag = AbMerchant.dao.findById(id)
		.set("is_recommend", "0")
		.set("tjkssj", null)
		.set("tjjzsj", null)
		.update();
		this.renderJson(flag);
	}
	
	/**
	 * 显示所有商家信息
	 */
	@Before(AccessAdminInterceptor.class)
	public void listsjcx(){
		String mc  = StringUtil.toStr(this.getPara("mc"));
		String sjh  = StringUtil.toStr(this.getPara("sjh"));
		String subjectid  = StringUtil.toStr(this.getPara("subjectid"));
		String cityid  = StringUtil.toStr(this.getPara("cityid"));
		String sql = "select * from ab_merchant where id is not null ";
		if(mc.length()>0){
			sql+=" and mc like '%"+mc+"%'";
		}
		if(sjh.length()>0){
			sql+=" and mobile like '%"+sjh+"%'";
		}
		//获取当前用户信息
		SysUser u = this.getSessionAttr("user");
//		if(!"admin".equals(u.getStr("id"))){
//			sql += " and user_id in(select id from sys_user where p_id='" + u.getStr("id") + "')";
//		}
		
//		if(u!=null&&"103".equals(u.get("role_id"))){
////			sql+=" and (city_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("id")+"') or area_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("id")+"'))";
//		}else if("104".equals(u.get("role_id"))){//普通员工
//			sql+=" and (city_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("p_id")+"') or area_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("p_id")+"'))";
//		}
		if(subjectid.length()>0){
			sql+=" and subject_id ='" + subjectid + "'";
		}
//		if(cityid.length()>0){
//			sql+=" and city_id = '"+cityid+"'";
//		}
		if(u.getStr("id").length() > 0 && (!"104".equals(u.getStr("role_id")))){
			sql += " and (city_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + ")";
			sql += " or area_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + "))";
		}
		if("104".equals(u.getStr("role_id"))){
			sql += " and subject_id in(" + CityAttachAdminUtil.getAuthSubjectIdsByUserId(u.getStr("id")) + ")";
		}
		sql+="order by create_time desc";
		
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		
		String sql_subject = "select * from ab_subject where 1 = 1";
		if("104".equals(u.getStr("role_id"))){
			sql_subject += " and id in(" + CityAttachAdminUtil.getAuthSubjectIdsByUserId(u.getStr("id")) + ")";
		}else{
			sql_subject += " and p_id in('1019','1020')";
		}
		List<AbSubject> list_subject = AbSubject.dao.find(sql_subject);
		List<AbCityarea> list_city = AbCityarea.dao.find("select * from ab_cityarea where p_id='ROOT'");
		this.keepPara();
		this.setAttr("listpage", listpage);
		this.setAttr("list_subject", list_subject);
		this.setAttr("list_city", list_city);
		this.setAttr("mc", mc);
		this.setAttr("subjectid", subjectid);
		this.setAttr("cityid", cityid);
		this.render("/admin/merchant/listsjcx.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void addsj(){
		String id = this.getPara(0);
		AbMerchant vo = new AbMerchant();
		Record tbuser = new Record();
		if(StringUtil.toStr(id).length()>0){
			vo = AbMerchant.dao.findById(id);
			tbuser = Db.findFirst("select * from sys_user where mid=?",id);
		}
		
		List<AbMerchantImg> list_img = AbMerchantImg.dao.find("select * from ab_merchant_img where mid='"+id+"' order by seq_num");
		List<AbSubject> list_subject = AbSubject.dao.find("select * from ab_subject where p_id in('1019','1020')");
		this.setAttr("vo", vo);
		this.setAttr("tbuser", tbuser);
		this.setAttr("list_img", list_img);
		this.setAttr("list_subject", list_subject);
		render("/admin/merchant/addsj.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void editsj(){
		String id = this.getPara("id");
		AbMerchant vo = new AbMerchant();
		Record tbuser = new Record();
		if(StringUtil.toStr(id).length()>0){
			vo = AbMerchant.dao.findById(id);
			tbuser = Db.findFirst("select * from sys_user where mid='"+id+"'");
		}
		
		List<AbMerchantImg> list_img = AbMerchantImg.dao.find("select * from ab_merchant_img where mid='"+id+"' order by seq_num");
		this.setAttr("vo", vo);
		this.setAttr("tbuser", tbuser);
		this.setAttr("list_img", list_img);
		render("/admin/merchant/editsj.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void savesj(){
		boolean flag = false;
		
		try {
			//获取上传文件名列表
			String[] imgurl = this.getParaValues("imgurl");
			String[] imgtype = this.getParaValues("imgtype");
			SysUser user = this.getSessionAttr("user");
			AbMerchant  mer = this.getModel(AbMerchant.class,"tb");
			SysUser  u = this.getModel(SysUser.class,"tbuser");
			AbMerchant oldmer = AbMerchant.dao.findById(mer.get("id"));
			String str_oldtitleimg ="";
			
			if(mer.get("img_url")!=null&&StringUtil.toStr(mer.get("img_url")).length()>0){
				str_oldtitleimg = oldmer.get("img_url");
			}

			u.set("mc", mer.get("mc"));
			u.set("mobile", mer.get("mobile"));
			u.set("mc", mer.get("mc"));
			u.set("zhlx","0");
			u.set("xxdz", mer.get("xxdz"));
			u.set("city_id", mer.get("city_id"));
			u.set("city_name", mer.get("city_name"));
			u.set("area_id", mer.get("area_id"));
			u.set("area_name", mer.get("area_name"));
			u.set("yzbm", mer.get("yzbm"));
			if(u.getStr("p_id")==null){
				u.set("p_id", user.getStr("id"));
			}
			if(mer!=null&&mer.getStr("id")!=null){
				mer.update();
				if(!this.getPara("oldpwd").equals(u.get("loginpwd"))){
					u.set("loginpwd", MD5.GetMD5Code(u.getStr("loginpwd")));
				}
				mer.set("user_id", mer.getStr("id"));
				u.update();
			}else{
				//获取分类属于货物类还是服务类
				Record r = Db.findFirst("select * from ab_subject where id='"+mer.getStr("subject_id")+"'");
				if(r!=null){
					mer.set("is_type", r.get("is_type"));
				}
				mer.set("id", StringUtil.getRandString32());
				mer.set("create_time", DateUtil.getCurrentDate());
				mer.set("user_id", mer.getStr("id"));
				mer.save();
				
				u.set("id", mer.get("id"));
				u.set("mid", mer.get("id"));
				u.set("create_date", mer.get("create_time"));
				u.set("loginpwd", MD5.GetMD5Code(u.getStr("loginpwd")));
				u.save();
			}
			//上传多个图片，对数据进行保存
			if(imgurl!=null&&imgurl.length>0){
				for (int i = 0; i < imgurl.length; i++) {
					AbMerchantImg img = new AbMerchantImg();
					img.set("id", StringUtil.getRandString32());
					img.set("type_name", imgtype[i]==null?null:imgtype[i].toLowerCase());
					img.set("lager", imgurl[i]);
					img.set("mid", mer.getStr("id"));
					img.set("scrid",user.get("id"));
					img.set("scrmc",user.get("name"));
					img.set("scsj",DateUtil.getCurrentDate());
					img.save();
				}
			}
			
			
			//删除老图片
			if(str_oldtitleimg!=null){
				File file = new File(PathKit.getWebRootPath() + "\\upload\\" +str_oldtitleimg);
				if (file.exists())
					file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		flag = true;
		renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void delete(){
		boolean flag = false;
		
		try {
			
			//删除商家信息
			String id = this.getPara("id");
			List<AbMerchantProduct> list_p = AbMerchantProduct.dao.find("select * from ab_merchant_product where mid=?",id); 
			
			AbMerchant mer = AbMerchant.dao.findById(id);
			Db.update("delete from sys_user where mid=?",id);
			Db.update("delete from ab_merchant_product where mid=?",id);
			mer.delete();
			
			if(list_p!=null&&list_p.size()>0){
				for (int i = 0; i < list_p.size(); i++) {
					list_p.get(i).delete();
					//删除老图片
					if(list_p.get(i)!=null&&list_p.get(i).get("img_url")!=null){
						File file = new File(PathKit.getWebRootPath() + "\\upload\\" +list_p.get(i).get("img_url"));
						if (file.exists())
							file.delete();
					}
					//删除老图片
					if(list_p.get(i)!=null&&list_p.get(i).get("thumbnail_url")!=null){
						File file = new File(PathKit.getWebRootPath() + "\\upload\\" +list_p.get(i).get("thumbnail_url"));
						if (file.exists())
							file.delete();
					}
				}
			}
			
			
			//删除老图片
			if(mer!=null&&mer.get("img_url")!=null){
				File file = new File(PathKit.getWebRootPath() + "\\upload\\" +mer.get("img_url"));
				if (file.exists())
					file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		flag = true;
		renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void delete_pl(){
		boolean flag = false;
		String ids = this.getPara("ids");
		try {
			if(ids!=null&&ids.length()>0){
				String[] a = ids.split(",");
				for (int i = 0; i < ids.length(); i++) {
					//删除商家信息
					String id = a[i];
					List<AbMerchantProduct> list_p = AbMerchantProduct.dao.find("select * from ab_merchant_product where mid=?",id); 
					
					AbMerchant mer = AbMerchant.dao.findById(id);
					Db.update("delete from sys_user where mid=?",id);
					Db.update("delete from ab_merchant_product where mid=?",id);
					mer.delete();
					
					if(list_p!=null&&list_p.size()>0){
						for (int j = 0; j < list_p.size(); j++) {
							list_p.get(i).delete();
							//删除老图片
							if(list_p.get(j)!=null&&list_p.get(j).get("img_url")!=null){
								File file = new File(PathKit.getWebRootPath() + "\\upload\\" +list_p.get(j).get("img_url"));
								if (file.exists())
									file.delete();
							}
							//删除老图片
							if(list_p.get(j)!=null&&list_p.get(j).get("thumbnail_url")!=null){
								File file = new File(PathKit.getWebRootPath() + "\\upload\\" +list_p.get(j).get("thumbnail_url"));
								if (file.exists())
									file.delete();
							}
						}
					}
					
					
					//删除老图片
					if(mer!=null&&mer.get("img_url")!=null){
						File file = new File(PathKit.getWebRootPath() + "\\upload\\" +mer.get("img_url"));
						if (file.exists())
							file.delete();
					}
					
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		flag = true;
		renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void listsjspsh(){
		SysUser u = (SysUser)this.getSessionAttr("user");
		String mc  = StringUtil.toStr(this.getPara("mc"));
		String mname  = StringUtil.toStr(this.getPara("mname"));
		String subjectid  = StringUtil.toStr(this.getPara("subjectid"));
		String zt  = StringUtil.toStr(this.getPara("zt"));
		
		String sql= "select * from ab_merchant_product where id is not null and subject_id != 1002";
		
		if(mc.length()>0){
			sql+=" and mc like '%"+mc+"%'";
		}
		
//		if(u!=null&&"103".equals(u.get("role_id"))){
//			sql+=" and (city_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("id")+"') or area_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("id")+"'))";
//		}else if("104".equals(u.get("role_id"))){//普通员工
//			sql+=" and (city_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("p_id")+"') or area_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("p_id")+"'))";
//		}
		if(u.getStr("id").length() > 0 && (!"104".equals(u.getStr("role_id")))){
			sql += " and (city_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + ")";
			sql += " or area_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + "))";
		}
		if("104".equals(u.getStr("role_id"))){
			sql += " and subject_id in(" + CityAttachAdminUtil.getAuthSubjectIdsByUserId(u.getStr("id")) + ")";
		}
		
		if(mname.length()>0){
			sql+=" and mname like '%"+mname+"%'";
		}
		if(subjectid.length()>0){
			sql+=" and (subject_id ='" + subjectid + "' or sub_id='" + subjectid + "' or thr_id='" + subjectid +"')";
		}
		String ds = StringUtil.toStr(this.getPara("ds"));
		if(ds.length() > 0){
			zt="0";
		}
		if(zt.length()>0){
			sql+=" and zt = '"+zt+"'";
		}
		
		sql+=" order by createtime desc";
		
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		
		String sql_subject = "select * from ab_subject where 1 = 1";
		if("104".equals(u.getStr("role_id"))){
			sql_subject += " and id in(" + CityAttachAdminUtil.getAuthSubjectIdsByUserId(u.getStr("id")) + ")";
		}else{
//			sql_subject += " and p_id in('1019','1020')";
			sql_subject += " and (p_id like'1019%' or p_id like'1020*')";
		}
		List<AbSubject> list_subject = AbSubject.dao.find(sql_subject);
		this.setAttr("listpage", listpage);
		this.setAttr("list_subject", list_subject);
		this.setAttr("mc", mc);
		this.setAttr("mname", mname);
		this.setAttr("subjectid", subjectid);
		this.render("/admin/merchant/listsjspsh.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void listsjspshwl(){
		SysUser u = (SysUser)this.getSessionAttr("user");
		String mc  = StringUtil.toStr(this.getPara("mc"));
		String mname  = StringUtil.toStr(this.getPara("mname"));
		String subjectid  = StringUtil.toStr(this.getPara("subjectid"));
		String zt  = StringUtil.toStr(this.getPara("zt"));
		
		String sql= "select * from ab_merchant_product where id is not null and subject_id = 1002";
		
		if(mc.length()>0){
			sql+=" and mc like '%"+mc+"%'";
		}
		
//		if(u!=null&&"103".equals(u.get("role_id"))){
//			sql+=" and (city_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("id")+"') or area_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("id")+"'))";
//		}else if("104".equals(u.get("role_id"))){//普通员工
//			sql+=" and (city_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("p_id")+"') or area_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("p_id")+"'))";
//		}
		if(u.getStr("id").length() > 0 && (!"104".equals(u.getStr("role_id")))){
			sql += " and (city_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + ")";
			sql += " or area_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + "))";
		}
		if("104".equals(u.getStr("role_id"))){
			sql += " and subject_id in(" + CityAttachAdminUtil.getAuthSubjectIdsByUserId(u.getStr("id")) + ")";
		}
		
		if(mname.length()>0){
			sql+=" and mname like '%"+mname+"%'";
		}
		if(subjectid.length()>0){
			sql+=" and (subject_id ='" + subjectid + "' or sub_id='" + subjectid + "' or thr_id='" + subjectid +"')";
		}
		String ds = StringUtil.toStr(this.getPara("ds"));
		if(ds.length() > 0){
			zt="0";
		}
		
		
		sql+=" order by createtime desc";
		
		sql = "select am.mc,aml.* from ab_merchant am inner join ab_merchant_logistics amlo "
				+ "on am.id=amlo.mid inner join ab_merchant_logline aml on amlo.id= aml.lid";
		
		if(zt.length()>0){
			sql+=" and aml.check_status = '"+zt+"'";
		}
		
		if(mname.length()>0){
			sql+=" and am.mc like '%"+mname+"%'";
		}
		
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		
		String sql_subject = "select * from ab_subject where 1 = 1";
		if("104".equals(u.getStr("role_id"))){
			sql_subject += " and id in(" + CityAttachAdminUtil.getAuthSubjectIdsByUserId(u.getStr("id")) + ")";
		}else{
//			sql_subject += " and p_id in('1019','1020')";
			sql_subject += " and (p_id like'1019%' or p_id like'1020*')";
		}
		List<AbSubject> list_subject = AbSubject.dao.find(sql_subject);
		this.setAttr("listpage", listpage);
		this.setAttr("list_subject", list_subject);
		this.setAttr("mc", mc);
		this.setAttr("mname", mname);
		this.setAttr("subjectid", subjectid);
		this.render("/admin/merchant/listsjspshwl.html");
	}
	
	
	@Before(AccessAdminInterceptor.class)
	public void listsjspcx(){
		SysUser u = (SysUser)this.getSessionAttr("user");
		String mc  = StringUtil.toStr(this.getPara("mc"));
		String mname  = StringUtil.toStr(this.getPara("mname"));
		String subjectid  = StringUtil.toStr(this.getPara("subjectid"));
		//获取该用户所在区域下的所有商户商品
//		String city_id = "";
//		if("103".equals(u.get("role_id"))){
//			city_id = Db.findFirst("select id from ab_cityarea where user_id='"+u.getStr("id")+"'").getStr("id");
//		}else if("104".equals(u.get("role_id"))){//普通员工
//			city_id = Db.findFirst("select id from ab_cityarea where user_id='"+u.getStr("p_id")+"'").getStr("id");
//		}
		
		String sql= "select * from ab_merchant_product where id is not null ";
		
		if(mc.length()>0){
			sql+=" and mc like '%"+mc+"%'";
		}
		if(mname.length()>0){
			sql+=" and mname like '%"+mname+"%'";
		}
		if(subjectid.length()>0){
			sql+=" and subject_id in (select id from ab_subject where p_id='" + subjectid + "')";
		}
		
		if(u.getStr("id").length() > 0){
			sql += " and mid in(select id from ab_merchant where (city_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + ")";
			sql += " or area_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + ")))";
		}
		
		if(u.getStr("id").length() > 0 && (!"104".equals(u.getStr("role_id")))){
			sql += " and mid in(select id from ab_merchant where (city_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + ")";
			sql += " or area_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + ")))";
		}
		if("104".equals(u.getStr("role_id"))){
			sql += " and subject_id in(" + CityAttachAdminUtil.getAuthSubjectIdsByUserId(u.getStr("id")) + ")";
		}
		
		sql+=" order by createtime desc";
		
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		
		String sql_subject = "select * from ab_subject where 1 = 1";
		if("104".equals(u.getStr("role_id"))){
			sql_subject += " and id in(" + CityAttachAdminUtil.getAuthSubjectIdsByUserId(u.getStr("id")) + ")";
		}else{
			sql_subject += " and p_id='ROOT'";
		}
		List<AbSubject> list_subject = AbSubject.dao.find(sql_subject);
		this.setAttr("listpage", listpage);
		this.setAttr("list_subject", list_subject);
		this.setAttr("mc", mc);
		this.setAttr("mname", mname);
		this.setAttr("subjectid", subjectid);
		this.render("/admin/merchant/listsjspcx.html");
	}
	/**
	 * 商品佣金设置
	 */
	@Before(AccessAdminInterceptor.class)
	public void listproductyj(){
		SysUser u = (SysUser)this.getSessionAttr("user");
		String mc  = StringUtil.toStr(this.getPara("mc"));
		String mname  = StringUtil.toStr(this.getPara("mname"));
		String subjectid  = StringUtil.toStr(this.getPara("subjectid"));
		//获取该用户所在区域下的所有商户商品
		String city_id = "";
		if("103".equals(u.get("role_id"))){
			city_id = Db.findFirst("select id from ab_cityarea where user_id='"+u.getStr("id")+"'").getStr("id");
		}else if("104".equals(u.get("role_id"))){//普通员工
			city_id = Db.findFirst("select id from ab_cityarea where user_id='"+u.getStr("p_id")+"'").getStr("id");
		}
		
		String sql= "select * from ab_merchant_product where id is not null ";
		
		if(mc.length()>0){
			sql+=" and mc like '%"+mc+"%'";
		}
		if(mname.length()>0){
			sql+=" and mname like '%"+mname+"%'";
		}
		if(subjectid.length()>0){
			sql+=" and subject_id = '"+subjectid+"'";
		}
		
		if(StringUtil.toStr(city_id).length()>0){
			sql+=" and mid in(select id from ab_merchant where city_id='"+city_id+"')";
		}
		
		sql+=" order by createtime desc";
		
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		
		List<AbSubject> list_subject = AbSubject.dao.find("select * from ab_subject where p_id='ROOT'");
		this.setAttr("listpage", listpage);
		this.setAttr("list_subject", list_subject);
		this.setAttr("mc", mc);
		this.setAttr("mname", mname);
		this.setAttr("subjectid", subjectid);
		this.render("/admin/merchant/listproduct_yj.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void saveproductyj(){
		String id = this.getPara("id");
		String yj = this.getPara("yj");
		BigDecimal bd=new BigDecimal(yj);
		boolean flag = AbMerchantProduct.dao.findById(id).set("yj", bd).update();
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void savesjyj(){
		String id = this.getPara("id");
		String yj = this.getPara("yj");
		BigDecimal bd=new BigDecimal(yj);
		boolean flag = AbMerchant.dao.findById(id).set("yj", bd).update();
		
		String user_id = AbMerchant.dao.findById(id).getStr("user_id");
		if(!StringUtil.isNull(user_id)){
			SysUser u = SysUser.dao.findById(user_id);
			if(u != null){
				u.set("per", bd);
				flag = u.update();
			}
		}
		
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void showmerchant(){
		AbMerchant vo = AbMerchant.dao.findById(this.getPara(0));
		SysUser u = new SysUser();
		if(vo!=null&&StringUtil.toStr(vo.get("user_id")).length()>0){
			u = SysUser.dao.findById(vo.getStr("user_id"));
		}
		List<AbMerchantImg> list_img = AbMerchantImg.dao.find("select * from ab_merchant_img where mid='"+vo.get("id")+"' order by seq_num");
		this.setAttr("list_img", list_img);
		this.setAttr("vo", vo);
		this.setAttr("u", u);
		render("/admin/merchant/showmerchant.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void showsp(){
		AbMerchantProduct vo = AbMerchantProduct.dao.findById(this.getPara(0));
		List<AbProductImg> list_img = AbProductImg.dao.find("select * from ab_product_img where f_id='"+vo.get("id")+"' order by scsj");
		this.setAttr("list_img", list_img);
		this.setAttr("vo", vo);
		render("/admin/merchant/showsp.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void editproduct(){
		String id = this.getPara("id");
		AbMerchant m = null;
		AbMerchantProduct  vo = new AbMerchantProduct();
		if(StringUtil.toStr(id).length()>0){
			vo = AbMerchantProduct.dao.findById(id);
			m = AbMerchant.dao.findById(vo.get("mid"));
		}else{
			String mid = this.getPara("mid");
			m =AbMerchant.dao.findById(this.getPara("mid"));
			vo.set("mid", mid);
			vo.set("subject_id", m.get("subject_id"));
			vo.set("subject_name", m.get("subject_name"));
			vo.set("is_type", m.get("is_type"));
		}
		List<AbProductImg> list_img = AbProductImg.dao.find("select * from ab_product_img where f_id='"+vo.getStr("id")+"' order by seq_num");
		List<AbSubject> list_subject = AbSubject.dao.find("select * from ab_subject where p_id like'%"+m.getStr("subject_id")+"%'");
		this.setAttr("vo", vo);
		this.setAttr("list_img", list_img);
		this.setAttr("list_subject", list_subject);
		render("/admin/merchant/editproduct.html");
	}
	
	
	@Before(AccessAdminInterceptor.class)
	public void saveproduct(){
		
		boolean flag = false;
		String img_url = this.getPara("img_url");
		
		//获取上传文件名列表
		String[] imgurl = this.getParaValues("imgurl");
		String[] imgtype = this.getParaValues("imgtype");
		
		AbMerchantProduct  product = this.getModel(AbMerchantProduct.class,"product");
		
		//如果上传了新图片，则将新图片进行保存，同时保存临时的老图片
		String str_oldtitleimg = null;
		if(img_url!=null){
			str_oldtitleimg = product.getStr("img_url");
			product.set("img_url", img_url);
			//对文件进行缩略图生成
			product.set("img_url", img_url);
		}
		
		if(product!=null&&product.getStr("id")!=null){
			product.update();
		}else{
			product.set("id", StringUtil.getUuid32());
			product.set("createtime", DateUtil.getCurrentDate());
			product.save();
		}
		  
		
		//上传多个图片，对数据进行保存
		if(imgurl!=null&&imgurl.length>0){
			SysUser user = this.getSessionAttr("user");
			for (int i = 0; i < imgurl.length; i++) {
				AbProductImg img = new AbProductImg();
				img.set("type_name", imgtype[i]);
				img.set("lager", imgurl[i]);
				img.set("f_id", product.getStr("id"));
				img.set("scsj", DateUtil.getCurrentDate());
				img.set("scrid", user.get("id"));
				img.set("scrmc", user.get("mc"));
				img.set("id", StringUtil.getUuid32());
				img.save();
			}
		}
		
		//删除老图片
		File file = null;
		if(str_oldtitleimg!=null){
			file = new File(PathKit.getWebRootPath() + "\\upload\\" +str_oldtitleimg);
			if (file.exists())
				file.delete();
		}
		
		flag = true;
		renderJson(flag);
	}
	
	
	@Before(AccessAdminInterceptor.class)
	public void delproduct(){
		boolean flag = false;
		String id = this.getPara("id");
		File f = null;
		//删除产品或者服务、删除产品或服务下的图片数据
		AbMerchantProduct.dao.deleteById(id);
		List<AbProductImg> list_img = AbProductImg.dao.find("select * from ab_product_img where f_id='"+id+"' order by seq_num");
		
		for (int i = 0; i <list_img.size(); i++) {
			AbProductImg.dao.deleteById(list_img.get(i).getStr("id"));
		}
		//删除附件
		for (int i = 0; i <list_img.size(); i++) {
			f = new File(PathKit.getWebRootPath() + "\\upload\\" +list_img.get(i).getStr("lager"));
			if(f.exists()){
				f.delete();
			}
			
			f = new File(PathKit.getWebRootPath() + "\\upload\\" +list_img.get(i).getStr("thumbnail"));
			if(f.exists()){
				f.delete();
			}
		}
		flag = true;
		renderJson(flag);
	}
	
	
	@Before(AccessAdminInterceptor.class)
	public void delproduct_pl(){
		boolean flag = false;
		String ids = this.getPara("ids");
		
		if(ids!=null&&ids.length()>0){
			String[] str = ids.split(",");
			for (int i = 0; i < str.length; i++) {
				String id = str[i];
				File f = null;
				//删除产品或者服务、删除产品或服务下的图片数据
				AbMerchantProduct.dao.deleteById(id);
				List<AbProductImg> list_img = AbProductImg.dao.find("select * from ab_product_img where f_id='"+id+"' order by seq_num");
				
				for (int j = 0; j <list_img.size(); j++) {
					AbProductImg.dao.deleteById(list_img.get(j).getStr("id"));
				}
				//删除附件
				for (int j = 0; j <list_img.size(); j++) {
					f = new File(PathKit.getWebRootPath() + "\\upload\\" +list_img.get(j).getStr("lager"));
					if(f.exists()){
						f.delete();
					}
					
					f = new File(PathKit.getWebRootPath() + "\\upload\\" +list_img.get(j).getStr("thumbnail"));
					if(f.exists()){
						f.delete();
					}
				}
			}
		}
		
		
		flag = true;
		renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void delproduct_pl_wlxl(){
		boolean flag = false;
		String ids = this.getPara("ids");
		
		if(ids!=null&&ids.length()>0){
			String[] str = ids.split(",");
			for (int i = 0; i < str.length; i++) {
				String id = str[i];
				File f = null;
				//删除产品或者服务、删除产品或服务下的图片数据
				AbMerchantLogline.dao.deleteById(id);
			}
		}
		
		
		flag = true;
		renderJson(flag);
	}
	
	
	@Before(AccessAdminInterceptor.class)
	public void sptg(){
		String id = this.getPara("id");
		boolean flag = AbMerchant.dao.findById(id).set("check_status", "1").update();
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void sptg_pl(){
		boolean flag = false;
		String ids = this.getPara("ids");
		ids = ids.replace(",", "','");
		Db.update("update ab_merchant set check_status='1' where id in ('"+ids+"')");
		flag = true;
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void spbtg(){
		String id = this.getPara("id");
		boolean flag = AbMerchant.dao.findById(id).set("check_status", "2").update();
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void spbtg_pl(){
		boolean flag = false;
		String ids = this.getPara("ids");
		ids = ids.replace(",", "','");
		Db.update("update ab_merchant set check_status='2' where id in ('"+ids+"')");
		flag = true;
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void spsptg(){
		String id = this.getPara("id");
		boolean flag = AbMerchantProduct.dao.findById(id).set("zt", "1").update();
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void spsptg_pl(){
		boolean flag = false;
		String ids = this.getPara("ids");
		ids = ids.replace(",", "','");
		Db.update("update ab_merchant_product set zt='1' where id in ('"+ids+"')");
		flag = true;
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void spsptg_pl_wlxl(){
		boolean flag = false;
		String ids = this.getPara("ids");
		ids = ids.replace(",", "','");
		Db.update("update ab_merchant_logline set check_status='1' where id in ('"+ids+"')");
		flag = true;
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void spspbtg(){
		String id = this.getPara("id");
		boolean flag = AbMerchantProduct.dao.findById(id).set("zt", "2").update();
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void spspbtg_pl(){
		boolean flag = false;
		String ids = this.getPara("ids");
		ids = ids.replace(",", "','");
		Db.update("update ab_merchant_product set zt='2' where id in ('"+ids+"')");
		flag = true;
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void spspbtg_pl_wlxl(){
		boolean flag = false;
		String ids = this.getPara("ids");
		ids = ids.replace(",", "','");
		Db.update("update ab_merchant_logline set check_status='2' where id in ('"+ids+"')");
		flag = true;
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void sptj(){
		String id = this.getPara("id");
		boolean flag = AbMerchantProduct.dao.findById(id).set("sysfxs", "1").update();
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void spqxtj(){
		String id = this.getPara("id");
		boolean flag = AbMerchantProduct.dao.findById(id).set("sysfxs", "0").update();
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void listsjywyjlcx(){
		String mname  = StringUtil.toStr(this.getPara("mname"));
		String ywymc  = StringUtil.toStr(this.getPara("ywymc"));
		String cityid  = StringUtil.toStr(this.getPara("cityid"));
		SysUser u = this.getSessionAttr("user");
		String sql= "select * from ab_shywyjl where id is not null ";
		
		if(mname.length()>0){
			sql+=" and mname like '%"+mname+"%'";
		}
		
		if(ywymc.length()>0){
			sql+=" and ywymc like '%"+ywymc+"%'";
		}
		
		if(cityid.length()>0){
			sql+=" and cityid = '"+cityid+"'";
		}
		
		sql+=" order by cityid,mid,ywyid";
		
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		String sql_subject = "select * from ab_subject where 1 = 1";
		if("104".equals(u.getStr("role_id"))){
			sql_subject += " and id in(" + CityAttachAdminUtil.getAuthSubjectIdsByUserId(u.getStr("id")) + ")";
		}else{
			sql_subject += " and p_id='ROOT'";
		}
		List<AbSubject> list_subject = AbSubject.dao.find(sql_subject);
		
		this.setAttr("listpage", listpage);
		this.setAttr("list_subject", list_subject);
		this.setAttr("cityid", cityid);
		this.setAttr("ywymc", ywymc);
		this.setAttr("mname", mname);
		this.render("/admin/merchant/listsjywyjlcx.html");
	}
	
	
	/**
	 * 计算距离
	 */
	@Before(AccessAdminInterceptor.class)
	public void saveSjywyjl(){
		boolean flag = false;
		try{
			String city_id = StringUtil.toStr(this.getPara("city_id"));
			String sql  = "SELECT id,mc,city_id,city_name,subject_id,subject_name,lng,lat from ab_merchant where id is not null ";
			if(city_id.length()>0){
				sql+=" and city_id='"+city_id+"'";
			}
			
			List<Record> list_mer = Db.find(sql);
			
			sql = "select u.id,u.mc,u.lng,u.lat FROM sys_user u where role_id ='106' ";
			if(city_id.length()>0){
				sql+=" and city_id='"+city_id+"'";
			}
			List<Record> list_ywy = Db.find(sql);
			
			if(list_mer!=null&&list_mer.size()>0 && list_ywy!=null &&list_ywy.size()>0){
				for (int i = 0; i < list_mer.size(); i++) {
					for (int j = 0; j < list_ywy.size(); j++) {
						//如果存在就不在进行重新生成
						
						if(Db.queryLong("select count(id) from ab_shywyjl where mid='"+list_mer.get(i).getStr("id")+"' and ywyid='"+list_ywy.get(j).getStr("id")+"'").longValue()<1){
							AbShywyjl.dao
							.set("id", StringUtil.getUuid32())
							.set("mid", list_mer.get(i).getStr("id"))
							.set("mname", list_mer.get(i).getStr("mc"))
							.set("cityid", list_mer.get(i).getStr("city_id"))
							.set("citymc", list_mer.get(i).getStr("city_name"))
							.set("subject_id", list_mer.get(i).getStr("subject_id"))
							.set("subject_name", list_mer.get(i).getStr("subject_name"))
							.set("sjjd", list_mer.get(i).getStr("lng"))
							.set("sjwd", list_mer.get(i).getStr("lat"))
							.set("ywyid", list_ywy.get(j).getStr("id"))
							.set("ywymc", list_ywy.get(j).getStr("mc"))
							.set("ywyjd", list_ywy.get(j).getStr("lng"))
							.set("ywywd", list_ywy.get(j).getStr("lat"))
							.set("lzjl", StringUtil.Distance(Double.parseDouble(list_mer.get(i).getStr("lng")), Double.parseDouble(list_mer.get(i).getStr("lat")), Double.parseDouble(list_ywy.get(j).getStr("lng")), Double.parseDouble(list_ywy.get(j).getStr("lat"))))
							.save();
						}
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		flag = true;
		renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void list_sj(){
		String mc  = StringUtil.toStr(this.getPara("mc"));
		String subjectid  = StringUtil.toStr(this.getPara("subjectid"));
		String cityid  = StringUtil.toStr(this.getPara("cityid"));
		String sql = "select * from ab_merchant where id is not null ";
		if(mc.length()>0){
			sql+=" and mc like '%"+mc+"%'";
		}
		SysUser u = this.getSessionAttr("user");
//		if(!"admin".equals(u.getStr("id"))){
//			sql += " and user_id in(select id from sys_user where p_id='" + u.getStr("id") + "')";
//		}
//		if(u!=null&&"103".equals(u.get("role_id"))){
//			sql+=" and (city_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("id")+"') or area_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("id")+"'))";
//		}else if("104".equals(u.get("role_id"))){//普通员工
//			sql+=" and (city_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("p_id")+"') or area_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("p_id")+"'))";
//		}
		if(subjectid.length()>0){
			sql+=" and subject_id in (select id from ab_subject where p_id='" + subjectid + "')";
		}
//		if(cityid.length()>0){
//			sql+=" and city_id = '"+cityid+"'";
//		}
		if(u.getStr("id").length() > 0 && (!"104".equals(u.getStr("role_id")))){
			sql += " and (city_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + ")";
			sql += " or area_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + "))";
		}
		if("104".equals(u.getStr("role_id"))){
			sql += " and subject_id in(" + CityAttachAdminUtil.getAuthSubjectIdsByUserId(u.getStr("id")) + ")";
		}
		sql+=" order by id asc";
		
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		
		String sql_subject = "select * from ab_subject where 1 = 1";
		if("104".equals(u.getStr("role_id"))){
			sql_subject += " and id in(" + CityAttachAdminUtil.getAuthSubjectIdsByUserId(u.getStr("id")) + ")";
		}else{
			sql_subject += " and p_id='ROOT'";
		}
		List<AbSubject> list_subject = AbSubject.dao.find(sql_subject);
		List<AbCityarea> list_city = AbCityarea.dao.find("select * from ab_cityarea where p_id='ROOT'");
		
		this.setAttr("listpage", listpage);
		this.setAttr("list_subject", list_subject);
		this.setAttr("list_city", list_city);
		this.setAttr("mc", mc);
		this.setAttr("subjectid", subjectid);
		this.setAttr("cityid", cityid);
		this.render("/admin/merchant/list_sj.html");
	}

	/**
	 * 
	 */
	@Before(AccessAdminInterceptor.class)
	public void list_sp_tj(){
		String kssj = StringUtil.toStr(this.getPara("kssj"));
		String jzsj = StringUtil.toStr(this.getPara("jzsj"));
		String currDate = StringUtil.toStr(this.getPara("currDate"));
		String lastDate = StringUtil.toStr(this.getPara("lastDate"));
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);    //得到前一天
		Date date = calendar.getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String str_lastDate = df.format(date);
		
		String mid = this.getPara(0);
		String sql = "SELECT itemid,itemname,img_url,thumbnail_url,price,IFNULL(SUM(quantity),0) quantity,IFNULL(SUM(totalmoney),0) totalmoney FROM ab_order_item  where mid='"+mid+"' ";
		
		if("yes".equals(currDate)){
			sql+=" and createtime>='"+DateUtil.getCurrentDate("yyyy-MM-dd")+" 00:00:00'";
			sql+=" and createtime<='"+DateUtil.getCurrentDate("yyyy-MM-dd")+" 59:59:59'";
		}else if("yes".equals(lastDate)){
			sql+=" and createtime>='"+str_lastDate+" 00:00:00'";
			sql+=" and createtime<='"+str_lastDate+" 59:59:59'";
		}else{
			if(kssj.length()>0){
				sql+=" and createtime>='"+kssj+" 00:00:00'";
			}
			if(jzsj.length()>0){
				sql+=" and createtime<='"+jzsj+" 59:59:59'";
			}
		}
		
		sql+="  GROUP BY itemid";
		List<Record> list_pro = Db.find(sql);
		
		//计算总金额
		sql = "SELECT IFNULL(SUM(totalmoney),0) totalmoney,IFNULL(COUNT( DISTINCT orderid),0) totleorder FROM ab_order_item  where mid='"+mid+"' ";
		if("yes".equals(currDate)){
			sql+=" and createtime>='"+DateUtil.getCurrentDate("yyyy-MM-dd")+" 00:00:00'";
			sql+=" and createtime<='"+DateUtil.getCurrentDate("yyyy-MM-dd")+" 59:59:59'";
		}else if("yes".equals(lastDate)){
			sql+=" and createtime>='"+str_lastDate+" 00:00:00'";
			sql+=" and createtime<='"+str_lastDate+" 59:59:59'";
		}else{
			if(kssj.length()>0){
				sql+=" and createtime>='"+kssj+" 00:00:00'";
			}
			if(jzsj.length()>0){
				sql+=" and createtime<='"+jzsj+" 59:59:59'";
			}
		}
		
		Record r = Db.findFirst(sql);
		this.keepPara();
		this.setAttr("list_pro", list_pro);
		this.setAttr("r", r);
		this.render("/admin/merchant/list_sp_tj.html");
	}
	
	/**
	 * 按商品销售统计
	 */
	@Before(AccessAdminInterceptor.class)
	public void list_zxs_tj(){
		String kssj = StringUtil.toStr(this.getPara("kssj"));
		String jzsj = StringUtil.toStr(this.getPara("jzsj"));
		String currDate = StringUtil.toStr(this.getPara("currDate"));
		String lastDate = StringUtil.toStr(this.getPara("lastDate"));
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1);    //得到前一个
		Date date = calendar.getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM");
		String str_lastDate = df.format(date);
		
		String mid = this.getPara(0);
		String sql = "SELECT IFNULL(SUM(ddzje),0) ddzje,IFNULL(COUNT(id),0) cnt,SUBSTRING(xdsj,1,10) xdsj FROM ab_order where mid='"+mid+"' ";
		
		if("yes".equals(currDate)){
			sql+=" and xdsj>='"+DateUtil.getCurrentDate("yyyy-MM")+"-01 00:00:00'";
			sql+=" and xdsj<='"+DateUtil.getCurrentDate("yyyy-MM")+"-31 59:59:59'";
		}else if("yes".equals(lastDate)){
			sql+=" and xdsj>='"+str_lastDate+"-01 00:00:00'";
			sql+=" and xdsj<='"+str_lastDate+"-31 59:59:59'";
		}else{
			if(kssj.length()>0){
				sql+=" and xdsj>='"+kssj+"-01 00:00:00'";
			}
			if(jzsj.length()>0){
				sql+=" and xdsj<='"+jzsj+"-31 59:59:59'";
			}
		}
		
		sql+="  GROUP BY SUBSTRING(xdsj,1,10)";
		List<Record> list_pro = Db.find(sql);
		
		
		//计算总金额
		sql = "SELECT IFNULL(SUM(ddzje),0) totalmoney,IFNULL(COUNT(id),0) totleorder FROM ab_order  where mid='"+mid+"' ";
		if("yes".equals(currDate)){
			sql+=" and xdsj>='"+DateUtil.getCurrentDate("yyyy-MM")+"-01 00:00:00'";
			sql+=" and xdsj<='"+DateUtil.getCurrentDate("yyyy-MM")+"-31 59:59:59'";
		}else if("yes".equals(lastDate)){
			sql+=" and xdsj>='"+str_lastDate+"-01 00:00:00'";
			sql+=" and xdsj<='"+str_lastDate+"-31 59:59:59'";
		}else{
			if(kssj.length()>0){
				sql+=" and xdsj>='"+kssj+"-01 00:00:00'";
			}
			if(jzsj.length()>0){
				sql+=" and xdsj<='"+jzsj+"-31 59:59:59'";
			}
		}
		
		Record r = Db.findFirst(sql);
		this.keepPara();
		this.setAttr("list_pro", list_pro);
		this.setAttr("r", r);
		this.render("/admin/merchant/list_zxs_tj.html");
	}
	
	public void list_xspm(){
		
		String kssj = this.getPara("kssj"); 
		String jzsj = this.getPara("jzsj"); 
		
		String sql = "select * from (SELECT MID,mname,ddzje,SUM(ddzje) sumval FROM ab_order where id is not null ";
		SysUser u = this.getSessionAttr("user");
//		if(u!=null&&"103".equals(u.get("role_id"))){
//			sql+=" and (city_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("id")+"') or area_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("id")+"'))";
//		}else if("104".equals(u.get("role_id"))){//普通员工
//			sql+=" and (city_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("p_id")+"') or area_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("p_id")+"'))";
//		}
		
		if (StringUtil.toStr(kssj).length()>0){
			sql+=" and xdsj>='"+kssj+" 00:00:00'";
		}
		if(u.getStr("id").length() > 0){
			sql += " and (city_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + ")";
			sql += " or area_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + "))";
		}
		if (StringUtil.toStr(jzsj).length()>0){
			sql+=" and xdsj<='"+jzsj+" 59:59:59'";
		}
		
		sql+=" GROUP BY MID,mname) k ORDER BY k.sumval desc limit 0,10";
		this.keepPara();
		this.setAttr("list", Db.find(sql));
		this.render("/admin/merchant/list_xspm.html");
	}
}
