package com.zp.controller.admin;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.stream.FileImageOutputStream;

import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Record;
import com.zp.entity.AbAppraise;
import com.zp.entity.AbAppraiseImg;
import com.zp.entity.SysUser;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.tools.CommonProcess;
import com.zp.tools.CommonSQL;
import com.zp.tools.StringUtil;

public class AbAppraiseContrller extends Controller {
	
	public void listappraise(){
		SysUser aduser = this.getSessionAttr("user");
		if (aduser == null || aduser.get("id") == null) {
			redirect("/25console");
		} else {
			
			String user = StringUtil.toStr(this.getPara("username"));
			String merchant = StringUtil.toStr(this.getPara("merchant"));
			String content = StringUtil.toStr(this.getPara("content"));
			String sn = StringUtil.toStr(this.getPara("sn"));
			
			StringBuffer sb = new StringBuffer("select * from ( ");
			sb.append("select 	aa.*,	ao.subject_name, am.mc mmc,	am.credit, am.rate,	ao.mid,	ao.sn from ");
			sb.append("(select * from ab_appraise where  mer_id !='' ");
			if(!"admin".equals(aduser.getStr("id"))){
				sb.append("and mer_id in (select id from ab_merchant where area_id in (select id from ab_cityarea where user_id = '"+aduser.getStr(SysUser.ID)+"'))) aa ");
			}else{
				sb.append("and mer_id in (select id from ab_merchant where area_id in (select id from ab_cityarea))) aa ");
			}
			sb.append("left join ab_order ao on aa.order_id = ao.id ");
			sb.append("left join ab_merchant am on aa.mer_id = am.id ");
			sb.append("left join sys_user su on aa.user_id = su.id ");
			sb.append("union ");
			sb.append("select aa.*,	ao.subject_name, am.mc mmc,	am.credittype creadit, am.rate,	ao.mid,	ao.sn from ");
			sb.append("(select * from ab_appraise where  mer_id='' ");
			if(!"admin".equals(aduser.getStr("id"))){
				sb.append("and qdr_id in (select id from sys_user where area_id in (select id from ab_cityarea where user_id = '"+aduser.getStr(SysUser.ID)+"'))) aa ");
			}else{
				sb.append("and qdr_id in (select id from sys_user where area_id in (select id from ab_cityarea))) aa ");
			}
			sb.append("left join ab_order ao on aa.order_id = ao.id ");
			sb.append("left join sys_user am on aa.qdr_id = am.id ");
			sb.append("left join sys_user su on aa.user_id = su.id ");
			sb.append(") t where 1=1 ");
			
			if (user.length() > 0) {
				sb.append(" and mc like '%" + user + "%'");
			}
			if (merchant.length() > 0) {
				sb.append(" and mmc like '%" + merchant + "%'");
			}
			if (content.length() > 0) {
				sb.append(" and content like '%" + content + "%'");
			}
			if (sn.length() > 0) {
				sb.append(" and sn like '%" + sn + "%'");
			}
			sb.append(" order by datetime desc");
			PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sb.toString());
			try {
				for (Record record : listpage.getList()) {
					String appId = record.getStr(AbAppraise.ID);
					List<AbAppraiseImg> imgs = AbAppraiseImg.dao.find(CommonSQL.getAppraiseImgByAppId(appId));
					for(AbAppraiseImg img : imgs){
						byte[] data = img.getBytes(AbAppraiseImg.IMAGE);
						File file = new File(PathKit.getWebRootPath() + "\\upload\\"
								+ img.getStr(AbAppraiseImg.IMG_URL));
						if(!file.exists()&& data != null){
							file.createNewFile();
							FileImageOutputStream imageOutput = new FileImageOutputStream(
									file);
							imageOutput.write(data, 0, data.length);
							imageOutput.close();
						}
					}
					record.set("appraiseImgs", imgs);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.setAttr("listpage", listpage);
			this.render("/admin/appraise/listappraise.html");
		}
	}
	
	/*删除评价*/
	public void deleteappraise(){
		String id = StringUtil.toStr(this.getPara("id"));
		CommonProcess.deleteappraise(id);
		this.renderJson(true);
	}
}
