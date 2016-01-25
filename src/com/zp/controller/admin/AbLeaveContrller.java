package com.zp.controller.admin;

import java.util.List;

import com.alibaba.druid.util.StringUtils;
import com.jfinal.core.Controller;
import com.zp.entity.AbMerchantLeave;
import com.zp.entity.AbMerchantReply;
import com.zp.entity.SysUser;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.tools.StringUtil;

public class AbLeaveContrller extends Controller {
	public void listleave(){
		SysUser aduser = this.getSessionAttr("user");
		if (aduser == null || aduser.get("id") == null) {
			redirect("/25console");
		} else {
			String adminId = aduser.getStr(SysUser.ID);
			String user = StringUtil.toStr(this.getPara("username"));
			String merchant = StringUtil.toStr(this.getPara("merchant"));
			String content = StringUtil.toStr(this.getPara("content"));
			StringBuffer sb = new StringBuffer("select * from ( ");
			sb.append("select aml.id leave_id,su.mc,am.mc mmc, suq.mc qmc, '' as rid, aml.datetime,aml.content from ");
			if(!"admin".equals(aduser.getStr("id"))){
				sb.append("(select * from ab_merchant_leave where user_id in (select id from sys_user where area_id in (select id from ab_cityarea where user_id = '"+adminId+"'))) aml ");
			}else{
				sb.append("(select * from ab_merchant_leave where user_id in (select id from sys_user where area_id in (select id from ab_cityarea))) aml ");
			}
			sb.append("left join sys_user su on aml.user_id = su.id ");
			sb.append("left join ab_merchant am on aml.mer_id = am.id ");
			sb.append("left join sys_user suq on aml.qdr_id = suq.id ");
			sb.append("UNION ");
			sb.append("select amr.leave_id,'' mc,am.mc mmc, suq.mc qmc, amr.id rid, amr.datetime,amr.content from ");
			if(!"admin".equals(aduser.getStr("id"))){
				sb.append("(select * from ab_merchant_reply where leave_id in (select id from ab_merchant_leave where user_id in (select id from sys_user where area_id in (select id from ab_cityarea where user_id = '"+adminId+"')))) amr ");
			}else{
				sb.append("(select * from ab_merchant_reply where leave_id in (select id from ab_merchant_leave where user_id in (select id from sys_user where area_id in (select id from ab_cityarea)))) amr ");
			}
			sb.append("left join ab_merchant_leave aml on amr.leave_id = aml.id ");
			sb.append("left join ab_merchant am on aml.mer_id = am.id ");
			sb.append("left join sys_user suq on aml.qdr_id = suq.id ");
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
			
			sb.append(" order by datetime desc");
			PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sb.toString());
			this.setAttr("listpage", listpage);
			this.render("/admin/leave/listleave.html");
		}
	}
	
	public void deleteleave(){
		String lid = StringUtil.toStr(this.getPara("lid"));
		String rid = StringUtil.toStr(this.getPara("rid"));
		if(StringUtils.isEmpty(rid)){
			String sql = "select * from ab_merchant_reply where leave_id ='"+lid+"'";
			List<AbMerchantReply> amrs = AbMerchantReply.dao.find(sql);
			for(AbMerchantReply amr : amrs){
				AbMerchantReply.dao.deleteById(amr.getStr(AbMerchantReply.ID));
			}
			this.renderJson(AbMerchantLeave.dao.deleteById(lid));
		}else{
			this.renderJson(AbMerchantReply.dao.deleteById(rid));
		}
	}
}
