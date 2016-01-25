package com.zp.controller.app;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.ClearInterceptor;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.zp.entity.AbSubject;
import com.zp.entity.AbUserAddr;
import com.zp.entity.SysUser;
import com.zp.tools.ImageScale;
import com.zp.tools.ImageUtils;
import com.zp.tools.StringUtil;
import com.zp.tools.Setting.WatermarkPosition;

/**
 * 公共控制器, 提供一些共用的 action
 */
@ClearInterceptor
public class CommonController extends AbsAppController {
	
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
	
	
	/**
	 * 上传图片
	 */
	public void uploadMultiImage(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String filename = null;
		try {
			List<UploadFile> lists = this.getFiles();
			StringBuilder sb = new StringBuilder();
			Map<String,String> pathMap = new HashMap<String, String>();
			for(UploadFile f : lists){
				if(f==null){
					formatErrorMsgResponse("没有文件", resultMap);
				}else{
				
					filename = StringUtil.getUuid32()+StringUtil.getExt(f.getFileName());
					f.getFile().renameTo(new File(f.getSaveDirectory()+ filename));
					//对文件进行缩略图生成
					ImageScale is = new ImageScale();
					try {
						is.saveImageAsJpg(f.getSaveDirectory()+ filename, f.getSaveDirectory()+ StringUtil.getThumbnail(filename), 800, 600);
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						File parent= new File(f.getSaveDirectory()+"sy");
						if(!parent.exists()){
							parent.mkdirs();
						}
						ImageUtils.addWatermark(new File(f.getSaveDirectory()+ filename), new File(f.getSaveDirectory()+"sy/"+ filename), new File(JFinal.me().getServletContext()
								.getRealPath("/")+"/qrcode/yhl.png"), WatermarkPosition.center, 50);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					(new File(f.getSaveDirectory()+f.getOriginalFileName())).delete();
					sb.append(filename+"#");
					pathMap.put(f.getParameterName(), filename);
				}
			}
			formatSuccessResponse(resultMap);
			resultMap.put("path", pathMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			formatInvokeFailResponse(e, resultMap);
		}
		renderJson(resultMap);
	}
	
	/**
	 * 上传图片
	 */
	public void uploadImage(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String filename = null;
		try {
			UploadFile f = this.getFile();
			
			if(f==null){
				formatErrorMsgResponse("没有文件", resultMap);
			}else{
			
				filename = StringUtil.getUuid32()+StringUtil.getExt(f.getFileName());
				f.getFile().renameTo(new File(f.getSaveDirectory()+ filename));
				//对文件进行缩略图生成
				ImageScale is = new ImageScale();
				try {
					is.saveImageAsJpg(f.getSaveDirectory()+ filename, f.getSaveDirectory()+ StringUtil.getThumbnail(filename), 800, 600);
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					File parent= new File(f.getSaveDirectory()+"sy");
					if(!parent.exists()){
						parent.mkdirs();
					}
					ImageUtils.addWatermark(new File(f.getSaveDirectory()+ filename), new File(f.getSaveDirectory()+"sy/"+ filename), new File(JFinal.me().getServletContext()
							.getRealPath("/")+"/qrcode/yhl.png"), WatermarkPosition.center, 50);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				(new File(f.getSaveDirectory()+f.getOriginalFileName())).delete();
				
				formatSuccessResponse(resultMap);
				resultMap.put("path", filename);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			formatInvokeFailResponse(e, resultMap);
		}
		renderJson(resultMap);
	}
	
	/**
	 * 获得城市
	 */
	public void subarea(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String p_id = StringUtil.toStr(this.getPara("p_id"));
		List<Record> list = Db.find("select id,mc from ab_cityarea where p_id ='"+p_id+"' and type_id='0'");
		formatSuccessResponse(resultMap);
		resultMap.put("data", list);
		renderJson(resultMap);
	}
	
	
	/**
	 * 获得所有的城市
	 */
	public void allCity(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String p_id = StringUtil.toStr(this.getPara("p_id"));
		List<Record> list = Db.find("select id,mc,p_id from ab_cityarea where type_id='0' and p_id <> 'ROOT'");
		formatSuccessResponse(resultMap);
		resultMap.put("data", list);
		renderJson(resultMap);
	}
	
	/**
	 * 获得区域
	 */
	public void subBusinessArea(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String p_id = StringUtil.toStr(this.getPara("p_id"));
		List<Record> list = Db.find("select id,mc from ab_cityarea where p_id ='"+p_id+"' and type_id='1'");
		formatSuccessResponse(resultMap);
		resultMap.put("data", list);
		renderJson(resultMap);
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
	
	public void subsubject(){
		String p_id = StringUtil.toStr(this.getPara("p_id"));
		List<Record> list = Db.find("select id,mc from ab_subject where p_id ='"+p_id+"'  order by ccm,seq_num");
		renderJson(list);
	}
	public void root(){
		List<Record> list = Db.find("select id,mc as name from ab_subject where p_id='ROOT' and (id=1018 or id=1020) order by ccm,seq_num");
		renderJson(list);
	}
	public void dirversubsubject(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String p_id = StringUtil.toStr(this.getPara("p_id"));
		List<Record> list = Db.find("select id,mc from ab_subject where p_id ='"+p_id+"'  order by ccm,seq_num");
		resultMap.put("data", list);
		resultMap.put("result", RESULT_SUCCESS);
		resultMap.put("msg", "查询成功");
		renderJson(resultMap);
	}
	public void driverroot(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Record> list = Db.find("select id,mc as name from ab_subject where p_id='ROOT' and (id=1017 or id=1020) order by ccm,seq_num");
		resultMap.put("data", list);
		resultMap.put("result", RESULT_SUCCESS);
		resultMap.put("msg", "查询成功");
		renderJson(resultMap);
	}
	
}





