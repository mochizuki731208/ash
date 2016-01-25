package com.zp.controller.app;

import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.zp.entity.AbMerchantLeave;
import com.zp.entity.AbMerchantReply;
import com.zp.entity.SysUser;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.tools.DateUtil;
import com.zp.tools.StringUtil;

public class AbLeaveContrller extends Controller {
	
	/**
	 * 增加催单接口
	 * 参数
	 *    userid  用户ID
	 *    mid	  商户ID
	 *    content 描述
	 *    
	 * 返回值 true or false
	 */
	public void addleave(){
		String userid = this.getPara("userid");
		String mid = this.getPara("mid");
		String order_id = this.getPara("order_id");
		String content = this.getPara("content");
		
		if(StringUtil.isNull(userid)||StringUtil.isNull(mid)||StringUtil.isNull(content)){
			this.renderJson(false);
		}else{
			AbMerchantLeave leave = new AbMerchantLeave();
			leave.set(AbMerchantLeave.ID, StringUtil.getUuid32());
			leave.set(AbMerchantLeave.CONTENT, content);
			leave.set(AbMerchantLeave.ORDER_ID, order_id);
			leave.set(AbMerchantLeave.MER_ID, mid);
			leave.set(AbMerchantLeave.USER_ID, userid);
			leave.set(AbMerchantLeave.DATETIME, DateUtil.getCurrentDate());
			leave.save();
			this.renderJson(true);
		}
	}
	
	/**
	 * 增加催单回复接口
	 * 参数
	 *    lid  催单留言ID
	 *    mid	  商户ID
	 *    content 描述
	 *    
	 * 返回值 true or false
	 */
	public void addreply(){
		
		String lid = this.getPara("lid");
		String content = this.getPara("content");
		
		if(StringUtil.isNull(lid)||StringUtil.isNull(content)){
			this.renderJson(false);
		}else{
			AbMerchantReply reply = new AbMerchantReply();
			reply.set(AbMerchantReply.ID, StringUtil.getUuid32());
			reply.set(AbMerchantReply.CONTENT, content);
			reply.set(AbMerchantReply.DATETIME, DateUtil.getCurrentDate());
			reply.set(AbMerchantReply.LEAVE_ID,  lid);
			reply.save();
			this.renderJson(true);
		}
	}
	
	
	/**
	 * 查询催单
	 * 参数
	 *    mid	  商户ID
	 *    
	 * 返回值 list
	 */
	public void listleave(){
		String mid = this.getPara("mid");
		List<Record> records = null;
		if(StringUtil.isNull(mid)){
		}else{
			records = Db.find("select am.*, su.mc from ab_merchant_leave am left join sys_user su on am.user_id = su.id where mer_id ='"+ mid+"' order by datetime desc");
		}
		this.renderJson(records);
	}
}
