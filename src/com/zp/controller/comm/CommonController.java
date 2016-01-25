package com.zp.controller.comm;

import java.io.File;
import java.util.List;

import com.jfinal.aop.ClearInterceptor;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.zp.tools.ImageScale;
import com.zp.tools.StringUtil;

/**
 * 公共控制器, 提供一些共用的 action
 */
@ClearInterceptor
public class CommonController extends Controller {
	
	public void upload(){
		UploadFile f = this.getFile();
		
		String filename=StringUtil.getUuid32()+StringUtil.getExt(f.getFileName());
		/*
		System.out.println(f.getContentType());
		System.out.println(f.getFileName());
		System.out.println(f.getOriginalFileName());
		System.out.println(f.getParameterName());
		System.out.println(f.getSaveDirectory());
		System.out.println(PathKit.getRootClassPath());
		System.out.println(PathKit.getWebRootPath() + "\\upload\\" + filename);
		*/
		
		f.getFile().renameTo(new File(f.getSaveDirectory()+ filename));
		
		//对文件进行缩略图生成
		ImageScale is = new ImageScale();
		try {
			is.saveImageAsJpg(f.getSaveDirectory()+ filename, f.getSaveDirectory()+ StringUtil.getThumbnail(filename), 800, 600);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		(new File(f.getSaveDirectory()+f.getOriginalFileName())).delete();
		renderText(filename);
	}
	
	public void upload2(){
		String filename = null;
		try {
			UploadFile f = this.getFile();
			filename = StringUtil.getUuid32()+StringUtil.getExt(f.getFileName());
			f.getFile().renameTo(new File(f.getSaveDirectory()+ filename));
			//对文件进行缩略图生成
			ImageScale is = new ImageScale();
			try {
				is.saveImageAsJpg(f.getSaveDirectory()+ filename, f.getSaveDirectory()+ StringUtil.getThumbnail(filename), 800, 600);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			(new File(f.getSaveDirectory()+f.getOriginalFileName())).delete();
			renderJson("{\"status\":1, \"data\":\""+filename+"\"}");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			renderJson("{\"status\":0}");
		}
	}
	
	public void subarea(){
		String p_id = StringUtil.toStr(this.getPara("p_id"));
		List<Record> list = Db.find("select id,mc from ab_cityarea where p_id ='"+p_id+"'");
		renderJson(list);
	}
	public void getcity(){
		String cityname = StringUtil.toStr(this.getPara("cityname"));
		List<Record> list = Db.find("SELECT * FROM ab_cityarea WHERE mc LIKE ? AND ccm=0" +
				" UNION " +
				" SELECT * FROM ab_cityarea WHERE id IN(SELECT p_id FROM ab_cityarea WHERE mc LIKE ? AND ccm=1)","%"+cityname+"%","%"+cityname+"%");
		String str_html = "";
		if(list!=null&&list.size()>0){
			for (int i = 0; i < list.size(); i++) {
				str_html += "<div class=\"lei\"><span class=\"en\">"+list.get(i).getStr("mc")+"</span>";
				str_html+="<div class=\"citylist\">";
				List<Record> list_sub = Db.find("SELECT * FROM ab_cityarea WHERE mc LIKE ? AND ccm=1 and p_id='"+list.get(i).getStr("id")+"'","%"+cityname+"%");
				if(list_sub!=null&&list_sub.size()>0){
					for (int j = 0; j < list_sub.size(); j++) {
						str_html +=" <a href=\"javascript:;\" data='"+list_sub.get(j).getStr("id")+"' onclick=\"uf_selectcity('"+list_sub.get(j).getStr("id")+"','"+list_sub.get(j).getStr("mc")+"')\">"+list_sub.get(j).getStr("mc")+"</a>";
					}
				}
				str_html +=" 	</div>";
				str_html +="</div>";
			}
		}
		renderText(str_html);
	}
	
	public void getsubcity(){
		String cityname = StringUtil.toStr(this.getPara("cityname"));
		List<Record> list = Db.find("SELECT * FROM ab_cityarea WHERE mc LIKE ? AND ccm=1","%"+cityname+"%");
		renderJson(list);
	}
	
	public void getu(){
		List<Record> list = Db.find("SELECT * FROM sys_user");
		renderJson(list);
	}
	
	public void subsubject(){
		String p_id = StringUtil.toStr(this.getPara("p_id"));
		List<Record> list = Db.find("select id,mc from ab_subject where p_id ='"+p_id+"'");
		renderJson(list);
	}
	
	public void checkloginid(){
		String loginid = StringUtil.toStr(this.getPara("loginid"));
		String id = StringUtil.toStr(this.getPara("id"));
		if(Db.queryLong("SELECT COUNT(id) FROM sys_user WHERE loginid=? and id!=?",loginid,id).longValue()>0){
			renderJson(false);
		}else{
			renderJson(true);
		}
	}
}





