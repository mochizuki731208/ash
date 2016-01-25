package com.zp.controller.trans;

import java.io.File;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.zp.entity.AbCityarea;
import com.zp.entity.AbMerchant;
import com.zp.entity.AbMerchantImg;
import com.zp.entity.AbMerchantLogimg;
import com.zp.entity.AbMerchantLogistics;
import com.zp.entity.AbMerchantLogline;
import com.zp.entity.AbMerchantLogtousu;
import com.zp.entity.AbSubject;
import com.zp.entity.SysUser;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.interceptor.AccessAdminInterceptor;
import com.zp.tools.CityAttachAdminUtil;
import com.zp.tools.DateUtil;
import com.zp.tools.MD5;
import com.zp.tools.StringUtil;

public class LogisticsAdminController extends Controller {

	@Before(AccessAdminInterceptor.class)
	public void list() {
		String mc  = StringUtil.toStr(this.getPara("mc"));
		String mobile  = StringUtil.toStr(this.getPara("mobile"));
		String sfrzzt  = StringUtil.toStr(this.getPara("sfrzzt"));
		String subjectid  = StringUtil.toStr(this.getPara("subjectid"));

		String sql = getSelect() + " where m.check_status='1' ";
		
		if(mc.length()>0){
			sql+=" and m.mc like '%"+mc+"%'";
		}
		if(mobile.length()>0){
			sql+=" and m.mobile like '%"+mobile+"%'";
		}
		
		if(subjectid.length()>0){
			sql+=" and subject_id ='" + subjectid + "'";
		}
		
		if(sfrzzt.length()>0){
			sql+=" and w.check_status = '"+sfrzzt+"'";
		}
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		SysUser u = this.getSessionAttr("user");

		String sql_subject = "select * from ab_subject where 1 = 1";
		if("104".equals(u.getStr("role_id"))){
			sql_subject += " and id in(" + CityAttachAdminUtil.getAuthSubjectIdsByUserId(u.getStr("id")) + ")";
		}else{
			sql_subject += " and p_id in('1019','1020')";
		}
		List<AbSubject> list_subject = AbSubject.dao.find(sql_subject);
		this.setAttr("list_subject", list_subject);
		this.setAttr("listpage", listpage);
		this.render("/admin/logi/list.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void show() {
		String mid = this.getPara("mid");
		if(mid!=null && mid.length() > 0) {
			String sql = getSelect() + " where m.id='" + mid + "'";
			Record record = Db.findFirst(sql);
			this.setAttr("logi", record);
		}
		this.render("/admin/logi/show.html");
	}
	
	private String getSelect() {
		return "select m.mc, m.city_name, m.img_url, m.thumbnail_url, m.logo, m.lng, m.lat, " +
				" m.mobile, m.xxdz, m.comments_total,m.hps,m.is_recommend, (m.hps/m.comments_total) as score, w.* " +
				" from ab_merchant AS m INNER JOIN ab_merchant_logistics AS w ON m.id = w.mid ";
	} 
	//---------------------------------
	// 功能函数
	//---------------------------------
	@Before(AccessAdminInterceptor.class)
	public void checktg() {
		String id = this.getPara("id");
		boolean flag = AbMerchantLogistics.dao.findById(id).set("check_status", "1").update();
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void checkbtg() {
		String id = this.getPara("id");
		boolean flag = AbMerchantLogistics.dao.findById(id).set("check_status", "2").update();
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void checktg_pl() {
		boolean flag = false;
		String ids = this.getPara("ids");
		ids = ids.replace(",", "','");
		Db.update("update ab_merchant_logistics set check_status='1' where id in ('"+ids+"')");
		flag = true;
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void checkbtg_pl() {
		boolean flag = false;
		String ids = this.getPara("ids");
		ids = ids.replace(",", "','");
		Db.update("update ab_merchant_logistics set check_status='2' where id in ('"+ids+"')");
		flag = true;
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void delete(){
		String id = this.getPara("id");
		List<AbMerchantLogimg> logimg = AbMerchantLogimg.dao.find("select * from ab_merchant_logimg where lid=?", id);
		if(logimg!=null&&logimg.size()>0){
			for (int j = 0; j < logimg.size(); j++) {
				logimg.get(j).delete();
				//删除老图片
				if(logimg.get(j)!=null&&logimg.get(j).get("larger")!=null){
					File file = new File(PathKit.getWebRootPath() + "\\upload\\" +logimg.get(j).get("larger"));
					if (file.exists())
						file.delete();
				}
				//删除老图片
				if(logimg.get(j)!=null&&logimg.get(j).get("thumbnail")!=null){
					File file = new File(PathKit.getWebRootPath() + "\\upload\\" +logimg.get(j).get("thumbnail"));
					if (file.exists())
						file.delete();
				}
			}
		}
		boolean flag = AbMerchantLogistics.dao.deleteById(id);
		this.renderJson(flag);
	}
	
	@Before({AccessAdminInterceptor.class, Tx.class})
	public void delete_pl(){
		boolean flag = false;
		String[] ids = this.getParaValues("ids");
		if(ids!=null&&ids.length>0){
			for (int i = 0; i < ids.length; i++) {
				String id = ids[i];
				List<AbMerchantLogimg> logimg = AbMerchantLogimg.dao.find("select * from ab_merchant_logimg where lid=?", id);
				if(logimg!=null&&logimg.size()>0){
					for (int j = 0; j < logimg.size(); j++) {
						logimg.get(j).delete();
						//删除老图片
						if(logimg.get(j)!=null&&logimg.get(j).get("larger")!=null){
							File file = new File(PathKit.getWebRootPath() + "\\upload\\" +logimg.get(j).get("larger"));
							if (file.exists())
								file.delete();
						}
						//删除老图片
						if(logimg.get(j)!=null&&logimg.get(j).get("thumbnail")!=null){
							File file = new File(PathKit.getWebRootPath() + "\\upload\\" +logimg.get(j).get("thumbnail"));
							if (file.exists())
								file.delete();
						}
					}
				}
				AbMerchantLogistics.dao.deleteById(id);
			}
		}
		flag = true;
		renderJson(flag);
	}
	
	///////////////////////////////////////////////////
	// 图片
	///////////////////////////////////////////////////
	@Before(AccessAdminInterceptor.class)
	public void listimg() {
		String lid = StringUtil.toStr(this.getPara("lid"));
		String sfrzzt  = StringUtil.toStr(this.getPara("sfrzzt"));
		String sql = "select * from ab_merchant_logimg where 1=1 ";
		if(lid.length() > 0) {
			sql += " and lid='" + lid + "'";
		}
		if(sfrzzt.length()>0){
			sql+=" and w.check_status = '"+sfrzzt+"'";
		}
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		this.setAttr("listpage", listpage);
		this.render("/admin/logi/listimg.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void showimg() {
		String id = this.getPara("id");
		if(id!=null && id.length() > 0) {
			AbMerchantLogimg img = AbMerchantLogimg.dao.findById(id);
			this.setAttr("img", img);
		}
		this.render("/admin/logi/showimg.html");
	}
	
	//---------------------------------
	// 功能函数
	//---------------------------------
	@Before(AccessAdminInterceptor.class)
	public void imgchecktg() {
		String id = this.getPara("id");
		boolean flag = AbMerchantLogimg.dao.findById(id).set("check_status", "1").update();
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void imgcheckbtg() {
		String id = this.getPara("id");
		boolean flag = AbMerchantLogimg.dao.findById(id).set("check_status", "2").update();
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void imgchecktg_pl() {
		boolean flag = false;
		String ids = this.getPara("ids");
		ids = ids.replace(",", "','");
		Db.update("update ab_merchant_logimg set check_status='2' where id in ('"+ids+"')");
		flag = true;
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void imgdelete(){
		String id = this.getPara("id");
		boolean flag = AbMerchantLogimg.dao.deleteById(id);
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void imgdelete_pl(){
		boolean flag = false;
		String ids = this.getPara("ids");
		if(ids!=null&&ids.length()>0){
			String[] a = ids.split(",");
			for (int i = 0; i < ids.length(); i++) {
				String id = a[i];
				Db.update("delete from ab_merchant_logimg where id=?", id);
			}
		}
		flag = true;
		this.renderJson(flag);
	}
	
	///////////////////////////////////////////////////
	// 投诉
	///////////////////////////////////////////////////	
	@Before(AccessAdminInterceptor.class)
	public void listts() {
		String lid = StringUtil.toStr(this.getPara("lid"));
		String sfrzzt  = StringUtil.toStr(this.getPara("sfrzzt"));
		String sql = "select * from ab_merchant_logtousu where 1=1 ";
		if(lid.length() > 0) {
			sql += " and lid='" + lid + "'";
		}
		if(sfrzzt.length()>0){
			sql+=" and w.check_status = '"+sfrzzt+"'";
		}
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		this.setAttr("listpage", listpage);
		this.render("/admin/logi/listtousu.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void showts() {
		String id = this.getPara("id");
		if(id!=null && id.length() > 0) {
			AbMerchantLogtousu ts = AbMerchantLogtousu.dao.findById(id);
			this.setAttr("ts", ts);
		}
		this.render("/admin/logi/showtousu.html");
	}
	
	//---------------------------------
	// 功能函数
	//---------------------------------
	@Before(AccessAdminInterceptor.class)
	public void tschecktg() {
		String id = this.getPara("id");
		boolean flag = AbMerchantLogtousu.dao.findById(id).set("check_status", "1").update();
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void tscheckbtg() {
		String id = this.getPara("id");
		boolean flag = AbMerchantLogtousu.dao.findById(id).set("check_status", "2").update();
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void tschecktg_pl() {
		boolean flag = false;
		String ids = this.getPara("ids");
		ids = ids.replace(",", "','");
		Db.update("update ab_merchant_logtousu set check_status='2' where id in ('"+ids+"')");
		flag = true;
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void tsdelete(){
		String id = this.getPara("id");
		boolean flag = AbMerchantLogtousu.dao.deleteById(id);
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void tsdelete_pl(){
		boolean flag = false;
		String ids = this.getPara("ids");
		if(ids!=null&&ids.length()>0){
			String[] a = ids.split(",");
			for (int i = 0; i < ids.length(); i++) {
				String id = a[i];
				Db.update("delete from ab_merchant_logtousu where id=?", id);
			}
		}
		flag = true;
		this.renderJson(flag);
	}
	
	/////////////////////////////////////
	// 注册物流企业账号
	/////////////////////////////////////
	
	@Before(AccessAdminInterceptor.class)
	public void addmer() {
		AbCityarea city = this.getSessionAttr("currcity");
		this.setAttr("currcity", city);
		render("/admin/logi/regmer.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void editmer() {
		String id = this.getPara("id");
		if(id !=null && id.length() > 0) {
			SysUser user = SysUser.dao.findById(id);
			AbMerchant mer = AbMerchant.dao.findById(user.get("mid"));
			setAttr("us", user);
			setAttr("mer", mer);
			setAttr("logi", AbMerchantLogistics.dao.findByMid(user.get("mid")));
		} else {
			String mid = this.getPara("mid");
			if(mid !=null && mid.length() > 0) {
				AbMerchant mer = AbMerchant.dao.findById(mid);
				SysUser user = SysUser.dao.findById(mer.get("user_id"));
				setAttr("us", user);
				setAttr("mer", mer);
				setAttr("logi", AbMerchantLogistics.dao.findByMid(mid));
			}
		}
		render("/admin/logi/regmer.html");
	}
	
	/**
	 * 保存商家信息
	 */
	@Before(AccessAdminInterceptor.class)
	public void savemer(){
		boolean flag= false;
		SysUser user = this.getModel(SysUser.class,"us");
		
		AbMerchant mer = this.getModel(AbMerchant.class,"mer");
		user.set("mc", mer.get("mc"));
		user.set("tel", mer.get("tel"));
		user.set("qq", mer.get("qq"));
		user.set("yzbm", mer.get("yzbm"));
		user.set("xxdz", mer.get("xxdz"));
		user.set("lng", mer.get("lng"));
		user.set("lat", mer.get("lat"));
		
		user.set("city_id", mer.get("city_id"));
		user.set("city_name", mer.get("city_name"));
		user.set("area_id", mer.get("area_id"));
		user.set("area_name", mer.get("area_name"));
		
		mer.set("mapbusiness", user.get("mapbusiness"));
		mer.set("mobile", user.get("mobile"));
		
		
		
		if(user.get("id")==null || user.get("id").toString().length() < 1) {
		
		String loginid = StringUtil.toStr(user.get("mobile"));
		if(Db.queryLong("SELECT COUNT(id) FROM sys_user WHERE loginid=?",loginid).longValue()>0){
			renderJson(flag);
			return;
		}
			String uuid = StringUtil.getUuid32();
			user.set("id", uuid);
			user.set("mid", uuid);
			String str_date = DateUtil.getCurrentDate();
			user.set("create_date", str_date);
			user.set("loginid", user.get("mobile"));
			
			mer.set("id", uuid);
			mer.set("user_id", uuid);
			mer.set("create_time", str_date);
			mer.set("check_status", "1");
			
			user.save();
			mer.save();
			AbMerchantLogistics logi = new AbMerchantLogistics();
			logi.set("id", uuid);
			logi.set("mid", uuid);
			logi.set("chief", this.getPara("logi_chief"));
			logi.set("servhotline", this.getPara("logi_servhotline", user.get("mobile").toString()));
			logi.save();
			//this.setSessionAttr("abuser", user);
		} else {
			AbMerchant oldmer = AbMerchant.dao.findById(mer.get("id"));
			String str_oldtitleimg ="";
			
			if(mer.get("img_url")!=null&&StringUtil.toStr(mer.get("img_url")).length()>0){
				str_oldtitleimg = oldmer.get("img_url");
			}
			//删除老图片
			if(str_oldtitleimg!=null){
				File file = new File(PathKit.getWebRootPath() + "\\upload\\" +str_oldtitleimg);
				if (file.exists())
					file.delete();
			}
			
			user.update();
			mer.update();
		}
		flag = true;
		renderJson(flag);
	}
	
	
	
	/**
	 * 保存商家信息
	 */
	@Before(AccessAdminInterceptor.class)
	public void savemer2(){
		boolean flag= false;
		String uuid = StringUtil.getUuid32();
		String str_date = DateUtil.getCurrentDate();
		// 获取上传文件名列表
		String[] imgurl = this.getParaValues("imgurl");
		String[] imgtype = this.getParaValues("imgtype");
		
		SysUser user = this.getModel(SysUser.class,"us");
		AbMerchantLogistics logi = this.getModel(AbMerchantLogistics.class,"lo");
		
		AbMerchant mer = this.getModel(AbMerchant.class, "tb");
		AbMerchant oldmer = AbMerchant.dao.findById(mer.get("id"));
		String str_oldtitleimg = "";

		if (oldmer!=null&&mer.get("img_url") != null
				&& StringUtil.toStr(mer.get("img_url")).length() > 0) {
			str_oldtitleimg = oldmer.get("img_url");
		}
		
		if (mer != null && mer.getStr("id") != null) {
			mer.update();
			user.update();
			user.set("id", mer.getStr("id") );
		}else {
			mer.set("id", uuid);
			mer.set("user_id", uuid);
			mer.set("create_time", str_date);
			mer.set("check_status", "1");
			
			String userSubType = StringUtil.toStr(this.getPara("userSubType"));
			AbSubject subject = AbSubject.dao.findById(userSubType);
			mer.set("subject_id", subject.get("id"));
			mer.set("subject_name", subject.get("mc"));
			
			mer.save();
			
		
		}
		
		user.set("mc", mer.get("mc"));
		user.set("mobile", mer.get("mobile"));
		user.set("qq", mer.get("qq"));
		user.set("yzbm", mer.get("yzbm"));
		user.set("xxdz", mer.get("xxdz"));
		user.set("lng", mer.get("lng"));
		user.set("lat", mer.get("lat"));
		user.set("city_id", mer.get("city_id"));
		user.set("city_name", mer.get("city_name"));
		user.set("area_id", mer.get("area_id"));
		user.set("area_name", mer.get("area_name"));
		
		if(user.get("id")==null || user.get("id").toString().length() < 1) {
			user.set("id", uuid);
			user.set("mid", uuid);
			user.set("create_date", str_date);
			user.set("loginid", user.get("mobile"));
			user.save();
		} 
		
		// 上传多个图片，对数据进行保存
		if (imgurl != null && imgurl.length > 0) {
			for (int i = 0; i < imgurl.length; i++) {
				AbMerchantImg img = new AbMerchantImg();
				img.set("id", uuid);
				img.set("type_name",
						imgtype[i] == null ? null : imgtype[i]
								.toLowerCase());
				img.set("lager", imgurl[i]);
				img.set("mid", mer.getStr("id"));
				img.set("scrid", user.get("id"));
				img.set("scrmc", user.get("name"));
				img.set("scsj",str_date);
				img.save();
			}
		}
					
		// 删除老图片
		if (str_oldtitleimg != null) {
			File file = new File(PathKit.getWebRootPath() + "\\upload\\"
					+ str_oldtitleimg);
			if (file.exists())
				file.delete();
		}
		
		// 获取运输方式
		String[] transtypes = this.getParaValues("transtype");
		// 获取放款方式
		String[] loan_types = this.getParaValues("loan_type");

		if (logi != null) {
			logi.set("loan_type",StringUtil.join(loan_types, ";", null, ""));
			logi.set("transtype",StringUtil.join(transtypes, ";", null, ""));
		}

		// AbMerchant vo = AbMerchant.dao.findById(logi.get("mid"));
		// if(vo!=null) logi.set("city_name", vo.get("city_name"));

		if (logi != null && logi.getStr("id") != null) {
			logi.update();
			flag = true;
		} else {
			logi.set("id", uuid);
			logi.set("createtime", str_date);
			logi.set("mid", uuid);
			logi.save();
		}
					
		flag = true;
		renderJson(flag);
	}
	
	/**
	 * 保存商家信息
	 */
	@Before(AccessAdminInterceptor.class)
	public void savemer1(){
		boolean flag= false;
		SysUser user = this.getModel(SysUser.class,"us");
		AbMerchant mer = this.getModel(AbMerchant.class,"mer");
		user.set("mc", mer.get("mc"));
		user.set("tel", mer.get("tel"));
		user.set("qq", mer.get("qq"));
		user.set("yzbm", mer.get("yzbm"));
		user.set("xxdz", mer.get("xxdz"));
		user.set("lng", mer.get("lng"));
		user.set("lat", mer.get("lat"));
		
		user.set("city_id", mer.get("city_id"));
		user.set("city_name", mer.get("city_name"));
		user.set("area_id", mer.get("area_id"));
		user.set("area_name", mer.get("area_name"));
		
		mer.set("mapbusiness", user.get("mapbusiness"));
		mer.set("mobile", user.get("mobile"));
		
		String userSubType = StringUtil.toStr(this.getPara("userSubType"));
		AbSubject subject = AbSubject.dao.findById(userSubType);
		mer.set("subject_id", subject.get("id"));
		mer.set("subject_name", subject.get("mc"));
		
		if(user.get("id")==null || user.get("id").toString().length() < 1) {
		
		String loginid = StringUtil.toStr(user.get("mobile"));
		if(Db.queryLong("SELECT COUNT(id) FROM sys_user WHERE loginid=?",loginid).longValue()>0){
			renderJson(flag);
			return;
		}
			String uuid = StringUtil.getUuid32();
			user.set("id", uuid);
			user.set("mid", uuid);
			String str_date = DateUtil.getCurrentDate();
			user.set("create_date", str_date);
			user.set("loginid", user.get("mobile"));
			user.set("loginpwd", MD5.GetMD5Code(user.getStr("loginpwd")));
			
			mer.set("id", uuid);
			mer.set("user_id", uuid);
			mer.set("create_time", str_date);
			mer.set("check_status", "1");
			
			user.save();
			mer.save();
			AbMerchantLogistics logi = new AbMerchantLogistics();
			logi.set("id", uuid);
			logi.set("mid", uuid);
			logi.set("chief", this.getPara("logi_chief"));
			logi.set("servhotline", this.getPara("logi_servhotline", user.get("mobile").toString()));
			logi.save();
			//this.setSessionAttr("abuser", user);
		} else {
			AbMerchant oldmer = AbMerchant.dao.findById(mer.get("id"));
			String str_oldtitleimg ="";
			
			if(mer.get("img_url")!=null&&StringUtil.toStr(mer.get("img_url")).length()>0){
				str_oldtitleimg = oldmer.get("img_url");
			}
			//删除老图片
			if(str_oldtitleimg!=null){
				File file = new File(PathKit.getWebRootPath() + "\\upload\\" +str_oldtitleimg);
				if (file.exists())
					file.delete();
			}
			
			user.update();
			mer.update();
		}
		flag = true;
		renderJson(flag);
	}
	
	// ////////////////////////////////
	// 物流企业信息管理
	// ////////////////////////////////

	@Before(AccessAdminInterceptor.class)
	public void edit1002company() {
		edit1002();
		render("/admin/logi/edit100201.html");
	}

	@Before(AccessAdminInterceptor.class)
	public void edit1002scene() {
		edit1002();
		this.setAttr("title", "公司场景");
		this.setAttr("subtitle", "场景");
		this.setAttr("imgdesc", "scene");
		this.setAttr("save_action", "save1002scene");
		this.setAttr("edit_action", "edit1002scene");
		render("/admin/logi/edit100202.html");
	}

	@Before(AccessAdminInterceptor.class)
	public void edit1002honor() {
		edit1002();
		this.setAttr("title", "公司荣誉");
		this.setAttr("subtitle", "荣誉");
		this.setAttr("imgdesc", "honor");
		this.setAttr("save_action", "save1002honor");
		this.setAttr("edit_action", "edit1002honor");
		render("/admin/logi/edit100202.html");
	}

	@Before(AccessAdminInterceptor.class)
	public void edit1002faith() {
		edit1002();
		this.setAttr("title", "诚信档案");
		this.setAttr("subtitle", "档案");
		this.setAttr("imgdesc", "faith");
		this.setAttr("save_action", "save1002faith");
		this.setAttr("edit_action", "edit1002faith");
		render("/admin/logi/edit100202.html");
	}

	@Before(AccessAdminInterceptor.class)
	public void edit1002tousu() {
		edit1002();
		this.setAttr("title", "投诉建议");
		this.setAttr("subtitle", "投诉建议");
		this.setAttr("read_action", "tousuread");
		this.setAttr("clear_action", "tousuclear");
		this.setAttr("delete_action", "tousudelete");
		this.setAttr("edit_action", "edit1002tousu");
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),
				14, "select * from ab_merchant_logtousu where lid='"
						+ getLogisticsID() + "'");
		this.setAttr("listpage", listpage);
		render("/admin/logi/edit100203.html");
	}

	@Before(AccessAdminInterceptor.class)
	public void edit1002line() {
		edit1002();
		this.setAttr("title", "物流线路");
		this.setAttr("subtitle", "物流线路");
		this.setAttr("save_action", "linesave");
		this.setAttr("clear_action", "lineclear");
		this.setAttr("delete_action", "linedelete");
		this.setAttr("modify_action", "edit1002lineone");
		this.setAttr("edit_action", "edit1002line");
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),
				14, "select * from ab_merchant_logline where lid='"
						+ getLogisticsID() + "'");
		this.setAttr("listpage", listpage);
		render("/admin/logi/edit100204.html");
	}

	@Before(AccessAdminInterceptor.class)
	public void edit1002lineone() {
		edit1002();
		this.setAttr("title", "物流线路");
		this.setAttr("subtitle", "物流线路");
		this.setAttr("save_action", "linesave");
		this.setAttr("modify_action", "edit1002lineone");
		this.setAttr("edit_action", "edit1002line");
		String id = this.getPara("id");
		if (id != null && id.length() > 0) {
			this.setAttr("line", AbMerchantLogline.dao.findById(id));
		}
		render("/admin/logi/edit100205.html");
	}

	/**
	 * 编辑页面都必须信息
	 */
	private void edit1002() {
		// 根据当前登录人信息获取商家信息
		AbCityarea city = this.getSessionAttr("currcity");
		this.setAttr("currcity", city);

		SysUser abuser = this.getSessionAttr("abuser");
		AbMerchant vo = new AbMerchant();
		String id = this.getPara("userid");
		if(id !=null && id.length() > 0) {
			SysUser user = SysUser.dao.findById(id);
			vo = AbMerchant.dao.findById(user.get("mid"));
		} else {
			String mid = this.getPara("mid");
			if(mid !=null && mid.length() > 0) {
				vo = AbMerchant.dao.findById(mid);
			}
		}
		AbMerchantLogistics lo = new AbMerchantLogistics();
		if (vo != null && vo.get("id") != null) {
			lo = AbMerchantLogistics.dao.findByMid(vo.get("id"));
			id = id==null || id.length() < 1 ? vo.getStr("user_id") : id;
		}
		
		lo = lo == null ? new AbMerchantLogistics() : lo;
		List<AbMerchantImg> img = AbMerchantImg.dao.findByMidShow(this.getPara("mid"));
		this.setAttr("lo", lo);
		this.setAttr("vo", vo);
		this.setAttr("userid", id);
		this.setAttr("mid", this.getPara("mid"));
		this.setAttr("list_img", img);
	}

	// //////////////////////////////////////////////
	@Before(AccessAdminInterceptor.class)
	public void save1002company() {
		boolean flag = false;
		try {
			// 获取主营路线
			String[] mainlines = this.getParaValues("mainline");
			// 获取运输方式
			String[] transtypes = this.getParaValues("transtype");
			// 获取放款方式
			String[] loan_types = this.getParaValues("loan_type");

			AbMerchantLogistics logi = this.getModel(AbMerchantLogistics.class,
					"lo");

			if (logi != null) {
				logi.set("mainline", StringUtil.join(mainlines, ";", null, ""));
				logi.set("transtype",
						StringUtil.join(transtypes, ";", null, ""));
				logi.set("loan_type",
						StringUtil.join(loan_types, ";", null, ""));
			}

			// AbMerchant vo = AbMerchant.dao.findById(logi.get("mid"));
			// if(vo!=null) logi.set("city_name", vo.get("city_name"));

			if (logi != null && logi.getStr("id") != null) {
				logi.update();
				flag = true;
			} else {
				logi.set("id", StringUtil.getRandString32());
				logi.set("createtime", DateUtil.getCurrentDate());
				logi.set("check_status", '1');
				logi.save();
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(flag);
	}

	
	// //////////////////////////////////////////////
	@Before(AccessAdminInterceptor.class)
	public void save1002company1() {
		boolean flag = false;
		try {
			// 获取主营路线
			String[] mainlines = this.getParaValues("mainline");
			// 获取运输方式
			String[] transtypes = this.getParaValues("transtype");
			// 获取放款方式
			String[] loan_types = this.getParaValues("loan_type");

			AbMerchantLogistics logi = this.getModel(AbMerchantLogistics.class,
					"lo");

			if (logi != null) {
				logi.set("mainline", StringUtil.join(mainlines, ";", null, ""));
				logi.set("transtype",
						StringUtil.join(transtypes, ";", null, ""));
				logi.set("loan_type",
						StringUtil.join(loan_types, ";", null, ""));
			}

			// AbMerchant vo = AbMerchant.dao.findById(logi.get("mid"));
			// if(vo!=null) logi.set("city_name", vo.get("city_name"));

			logi.set("id", StringUtil.getRandString32());
			logi.set("createtime", DateUtil.getCurrentDate());
			logi.set("check_status", '1');
			logi.save();
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void save1002scene() {
		save1002("scene");
		renderJson(true);
	}

	@Before(AccessAdminInterceptor.class)
	public void save1002honor() {
		save1002("honor");
		renderJson(true);
	}

	@Before(AccessAdminInterceptor.class)
	public void save1002faith() {
		save1002("faith");
		renderJson(true);
	}

	private void save1002(String action) {
		String loid = getPara("lo_id");
		System.out.println("save1002image loid is " + loid);
		saveMerImage(loid, getPara("imgurl01"), getPara("imgtype01"), "1",
				action);
		saveMerImage(loid, getPara("imgurl02"), getPara("imgtype02"), "2",
				action);
		saveMerImage(loid, getPara("imgurl03"), getPara("imgtype03"), "3",
				action);
		saveMerImage(loid, getPara("imgurl04"), getPara("imgtype04"), "4",
				action);
		saveMerImage(loid, getPara("imgurl05"), getPara("imgtype05"), "5",
				action);
		saveMerImage(loid, getPara("imgurl06"), getPara("imgtype06"), "6",
				action);
		saveMerImage(loid, getPara("imgurl07"), getPara("imgtype07"), "7",
				action);
		saveMerImage(loid, getPara("imgurl08"), getPara("imgtype08"), "8",
				action);
	}

	/**
	 * 上传图片，对数据进行保存
	 * 
	 * @param lid
	 * @param imgname
	 */
	private void saveMerImage(String lid, String imgurl, String imgtype,
			String num, String desc) {
		if (lid == null || lid.length() < 1)
			return;
		if (imgurl == null || imgurl.length() < 1)
			return;
		AbMerchantLogimg img = AbMerchantLogimg.dao.findByInfo(lid, num, desc);
		boolean bimg = true;
		if (img == null) {
			img = new AbMerchantLogimg();
			bimg = false;
			img.set("id", StringUtil.getRandString32());
			img.set("lid", lid);
			img.set("seq_num", num);
			img.set("description", desc);
		} else {
			String str_oldimg = img.get("lager");
			// 删除老图片
			if (str_oldimg != null && !str_oldimg.equals(imgurl)) {
				File file = new File(PathKit.getWebRootPath() + "\\upload\\"
						+ str_oldimg);
				if (file.exists())
					file.delete();
			}
		}
		img.set("type_name", imgtype == null ? null : imgtype.toLowerCase());
		img.set("lager", imgurl);
		img.set("scrid", this.getPara("userid"));
		img.set("scsj", DateUtil.getCurrentDate());
		if (bimg)
			img.update();
		else
			img.save();
	}

	// ////////////////
	// 投诉建议
	@Before(AccessAdminInterceptor.class)
	public void tousuread() {
		AbMerchantLogtousu.dao.readById(this.getPara("id"));
		renderJson(true);
	}

	@Before(AccessAdminInterceptor.class)
	public void tousureads() {
		String[] ids = this.getParaValues("id");
		AbMerchantLogtousu.dao.readByIds(ids);
		renderJson(true);
	}

	@Before(AccessAdminInterceptor.class)
	public void tousudelete() {
		String id = this.getPara("id");
		if (id != null && id.length() > 0) {
			AbMerchantLogtousu.dao.deleteById(id);
			renderJson(true);
			return;
		}
		renderJson(false);
	}

	@Before(AccessAdminInterceptor.class)
	public void tousudeletes() {
		String[] ids = this.getParaValues("id");
		AbMerchantLogtousu.dao.deleteByIds(ids);
		renderJson(true);
	}

	@Before(AccessAdminInterceptor.class)
	public void tousuclear() {
		AbMerchantLogtousu.dao.deleteByLID(getLogisticsID());
	}

	private String getLogisticsID() {
		// 根据当前登录人信息获取商家信息
		String id = this.getPara("userid");
		if(id !=null && id.length() > 0) {
			SysUser user = SysUser.dao.findById(id);
			// 根据人员信息查询商家信息
			if (user != null && user.get("mid") != null) {
				AbMerchantLogistics lo = AbMerchantLogistics.dao.findByMid(user.get("mid"));
				if (lo != null)
					return lo.get("id");
			}
		} else {
			String mid = this.getPara("mid");
			if(mid !=null && mid.length() > 0) {
				AbMerchantLogistics lo = AbMerchantLogistics.dao.findByMid(mid);
				if (lo != null)
					return lo.get("id");
			}
		}
		return null;
	}

	// ////////////////
	// 物流线路
	@Before(AccessAdminInterceptor.class)
	public void linedelete() {
		String id = this.getPara("id");
		if (id != null && id.length() > 0) {
			AbMerchantLogline.dao.deleteById(id);
			renderJson(true);
			return;
		}
		renderJson(false);
	}

	@Before(AccessAdminInterceptor.class)
	public void linedeletes() {
		String[] ids = this.getParaValues("id");
		AbMerchantLogline.dao.deleteByIds(ids);
		renderJson(true);
	}

	@Before(AccessAdminInterceptor.class)
	public void lineclear() {
		boolean flag = AbMerchantLogline.dao.deleteByLID(getLogisticsID());
		renderJson(flag);
	}

	@Before(AccessAdminInterceptor.class)
	public void linesave() {
		boolean flag = false;
		AbMerchantLogline line = this.getModel(AbMerchantLogline.class, "line");
		if (line != null && line.getStr("id") != null) {
			line.update();
			flag = true;
		} else {
			line.set("id", StringUtil.getRandString32());
			line.set("lid", getLogisticsID());
			line.save();
			flag = true;
		}
		renderJson(flag);
	}

	// ////////////////////////////////
	// end 物流企业信息管理
	// ////////////////////////////////
}
