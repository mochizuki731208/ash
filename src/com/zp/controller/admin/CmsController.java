package com.zp.controller.admin;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.zp.entity.CmsContent;
import com.zp.entity.CmsNrfl;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.interceptor.AccessAdminInterceptor;
import com.zp.tools.DateUtil;
import com.zp.tools.StringUtil;

public class CmsController extends Controller {
	@Before(AccessAdminInterceptor.class)
	public void listfl(){
		String lbmc = this.getPara("lbmc");
		String sjfl = this.getPara("sjfl");
		String sql= "select *,(select count(*) from cms_nrfl f where f.sjfl=t.id) cnt from cms_nrfl t where id is not null ";
		if(StringUtil.toStr(lbmc).length()>0){
			sql+=" and lbmc like '%"+lbmc+"%'";
		}
		if(StringUtil.toStr(sjfl).length()>0){
			sql+=" and sjfl='"+sjfl+"'";
		}
		sql+=" order by sxh";
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		this.keepPara();
		this.setAttr("listpage", listpage);
		render("/admin/cms/listfl.html");
	}
	@Before(AccessAdminInterceptor.class)
	public void editnrfl(){
		String id = this.getPara(0);
		CmsNrfl c = new CmsNrfl();
		if(StringUtil.toStr(id).length()>0){
			c = CmsNrfl.dao.findById(id);
		}
		this.setAttr("vo", c);
		this.setAttr("list_sj", Db.find("select * from cms_nrfl"));
		render("/admin/cms/editnrfl.html");
	}
	@Before(AccessAdminInterceptor.class)
	public void savenrfl(){
		CmsNrfl c = this.getModel(CmsNrfl.class,"tb");
		if(c!=null&&c.get("id")!=null){
			c.update();
		}else{
			c.save();
		}
		redirect("/admin/cms/listfl");
	}
	@Before(AccessAdminInterceptor.class)
	public void deletenrfl(){
		boolean flag = false;
		String id = this.getPara(0);
		CmsNrfl.dao.findById(id).delete();
		flag = true;
		renderJson(flag);
	}
	@Before(AccessAdminInterceptor.class)
	public void listcontent(){
		String title = this.getPara("title");
		String author = this.getPara("author");
		String seo_keyword = this.getPara("seo_keyword");
		String sql= "select *,(select lbmc from cms_nrfl l where l.id=c.lbid) lbmc from cms_content c where id is not null ";
		if(StringUtil.toStr(title).length()>0){
			sql+=" and title like '%"+title+"%'";
		}
		if(StringUtil.toStr(author).length()>0){
			sql+=" and author like '%"+author+"%'";
		}
		if(StringUtil.toStr(seo_keyword).length()>0){
			sql+=" and seo_keyword like '%"+seo_keyword+"%'";
		}
		
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		this.keepPara();
		this.setAttr("listpage", listpage);
		this.setAttr("listlb", Db.find("select id,lbmc from cms_nrfl order by sxh"));
		
		render("/admin/cms/listcontent.html");
	}
	@Before(AccessAdminInterceptor.class)
	public void edit(){
		String id = this.getPara(0);
		CmsContent c = new CmsContent();
		if(StringUtil.toStr(id).length()>0){
			c = CmsContent.dao.findById(id);
		}
		this.setAttr("vo", c);
		this.setAttr("listnrlb", Db.find("select * from cms_nrfl order by sxh"));
		render("/admin/cms/edit.html");
	}
	@Before(AccessAdminInterceptor.class)
	public void save(){
		CmsContent c = this.getModel(CmsContent.class,"tb");
		if(c!=null&&c.get("id")!=null){
			c.update();
		}else{
			c.set("release_date", DateUtil.getCurrentDate());
			c.save();
		}
		redirect("/admin/cms/listcontent");
	}
	@Before(AccessAdminInterceptor.class)
	public void delete(){
		boolean flag = false;
		String id = this.getPara(0);
		CmsContent.dao.findById(id).delete();
		flag = true;
		renderJson(flag);
	}
	@Before(AccessAdminInterceptor.class)
	public void save_fb(){
		boolean flag = false;
		String id = this.getPara(0);
		CmsContent.dao.findById(id)
		.set("is_publication", "1")
		.set("release_date", DateUtil.getCurrentDate())
		.update();
		flag = true;
		renderJson(flag);
	}
}
