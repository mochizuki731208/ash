package com.zp.controller.ab;

import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.zp.config.CaptchaRender;
import com.zp.entity.AbMerchantLeave;
import com.zp.entity.AbMerchantReply;
import com.zp.entity.SysUser;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.tools.CommonSQL;
import com.zp.tools.CommonStaticData;
import com.zp.tools.DateUtil;
import com.zp.tools.StringUtil;

public class AbLeaveContrller extends Controller {
	
	public void addleave(){
		SysUser abuser = this.getSessionAttr("abuser");
		if(abuser==null||abuser.get("id")==null){
			redirect("/ab/login");
		}else{
			String inputRandomCode = this.getPara("captchaCode");
			String content = this.getPara("content");
			String isrefresh = this.getPara("isrefresh");//1为刷新，0为不刷新
			Object objMd5RandomCode = this.getSessionAttr(CaptchaRender.DEFAULT_CAPTCHA_MD5_CODE_KEY);
			//验证验证码
			if (!CaptchaRender.validate(objMd5RandomCode.toString(), inputRandomCode)) {
		          this.renderJson("1");
		    }
			//验证content
			else if(StringUtil.isNull(content)||content.length()>100){
		    	 this.renderJson("2");
		    }
			//插入留言记录
			else{
				AbMerchantLeave leave = new AbMerchantLeave();
				leave.set(AbMerchantLeave.ID, StringUtil.getUuid32());
				leave.set(AbMerchantLeave.CONTENT, content);
				leave.set(AbMerchantLeave.MER_ID, this.getPara("mid"));
				leave.set(AbMerchantLeave.QDR_ID, this.getPara("qdrid"));
				leave.set(AbMerchantLeave.ORDER_ID, this.getPara("orderid"));
				leave.set(AbMerchantLeave.USER_ID, abuser.get("id"));
				leave.set(AbMerchantLeave.DATETIME, DateUtil.getCurrentDate());
				leave.save();
				if("1".equals(isrefresh)){
					this.renderJson(getLeaveStr(false, this.getPara("mid")));
				}else{
					this.renderJson("0");
				}
			}
		}
	}
	
	public void addreply(){
		SysUser abuser = this.getSessionAttr("abuser");
		if(abuser==null||abuser.get("id")==null){
			redirect("/ab/login");
		}else{
			String inputRandomCode = this.getPara("captchaCode");
			String content = this.getPara("content");
			String isrefresh = this.getPara("isrefresh");//1为刷新，0为不刷新
			Object objMd5RandomCode = this.getSessionAttr(CaptchaRender.DEFAULT_CAPTCHA_MD5_CODE_KEY);
			//验证验证码
			if (!CaptchaRender.validate(objMd5RandomCode.toString(), inputRandomCode)) {
		          this.renderJson("1");
		    }
			//验证content
			else if(StringUtil.isNull(content)||content.length()>100){
		    	 this.renderJson("2");
		    }
			//插入回复记录
			else{
				AbMerchantReply reply = new AbMerchantReply();
				reply.set(AbMerchantReply.ID, StringUtil.getUuid32());
				reply.set(AbMerchantReply.CONTENT, content);
				reply.set(AbMerchantReply.DATETIME, DateUtil.getCurrentDate());
				reply.set(AbMerchantReply.LEAVE_ID,  this.getPara("lid"));
				reply.save();
				if("1".equals(isrefresh)){
					this.renderJson(getLeaveStr(false, this.getPara("mid")));
				}else{
					this.renderJson("0");
				}
			}
		}
	}
	
	public void listorder(){
		SysUser abuser = this.getSessionAttr("abuser");
		if(abuser==null||abuser.get("id")==null){
			redirect("/ab/login");
		}else{
			String sql = "select * from ab_order where ddzt in ('2','3') and  xdrid='"+ abuser.get("id")+"' ";
			PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
			this.keepPara();
			this.setAttr("listpage", listpage);
			this.render("/ab/user/listleaveorder.html");
		}
	}
	
	/*查询该商户或跑腿人员的留言记录*/
	public void listleave(){
		SysUser abuser = this.getSessionAttr("abuser");
		if(abuser==null||abuser.get("id")==null){
			redirect("/ab/login");
		}else{
			abuser = SysUser.dao.findById(abuser.get("id"));
			StringBuffer sb = new StringBuffer();
			if(CommonStaticData.USER_TYPE_MERCHANT.equals(abuser.getStr(SysUser.ROLE_ID))){
				sb.append("select am.*, su.mc from ab_merchant_leave am left join sys_user su on am.user_id = su.id where mer_id ='"+ abuser.get("id")+"' order by datetime desc");
			}else{
				sb.append("select am.*, su.mc from ab_merchant_leave am left join sys_user su on am.user_id = su.id where qdr_id ='"+ abuser.get("id")+"' order by datetime desc");
			}
			PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sb.toString());
			this.keepPara();
			this.setAttr("listpage", listpage);
			this.render("/ab/user/listmerchantleave.html");
		}
	}
	
	public String getLeaveStr(boolean isMer, String mid){
		StringBuffer sb = new StringBuffer();
//		 System.out.println(JFinal.me().getContextPath());
//		 System.out.println(JFinal.me().getServletContext().getContextPath());
		//获取所有留言
		List<Record> list_leave = Db.find(CommonSQL.getLeaveByMid(mid));
		String imagepath = JFinal.me().getContextPath();
		for(Record aml : list_leave){
			String leaveId = aml.getStr(AbMerchantLeave.ID);
			List<AbMerchantReply> amrs = AbMerchantReply.dao.find("select * from ab_merchant_reply where leave_id = '"+leaveId+"'");
			sb.append("<div class=\"rst-comment\"><img alt=\"guest\" src=\"").append(imagepath).append("/res/css/ab/images/user_superman.png\" class=\"user-avatar\">");
			if(isMer){
				sb.append("<div style=\"float: right\"><a id=\"replyMsgBtn\" href=\"#replyDiv\" onclick=\"uf_leave_show('").append(leaveId).append("')\">回复留言</a></div>");
			}
			sb.append("<div class=\"user-comment\">");

			sb.append("<p class=\"rst-comment-info\"><span class=\"name\">").append(aml.getStr("mc"));
			sb.append("</span>").append(aml.getStr(AbMerchantLeave.DATETIME)).append("</p>");

			sb.append("<p class=\"rst-comment-content\">");
			sb.append(aml.getStr(AbMerchantLeave.CONTENT)).append("</p></div>");
			sb.append("<span class=\"clear\"></span></div><div class=\"rst-reply-wrapper\">");
			for(AbMerchantReply amr : amrs){
				sb.append("<div class=\"rst-reply\"> <img src=\"").append(imagepath).append("/res/css/ab/images/shangjia.jpeg\" class=\"manager-avatar\">");
				sb.append("<div class=\"manager-comment\">");
				sb.append("<p class=\"rst-comment-info\"><span class=\"name\">餐厅回复</span>").append(amr.getStr(AbMerchantReply.DATETIME)).append("</p>");
				sb.append("<p class=\"rst-comment-content\">").append(amr.getStr(AbMerchantReply.CONTENT)).append("</p>");
				sb.append("</div><span class=\"clear\"></span></div>");
			}
			sb.append("</div></div>");
		}
		return sb.toString();
	}
}
