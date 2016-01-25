package com.zp.controller.ab;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.zp.config.CaptchaRender;
import com.zp.entity.AbOrder;
import com.zp.entity.AbOrderChargeback;
import com.zp.entity.AbOrderChargebackItem;
import com.zp.entity.SysUser;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.tools.CommonProcess;
import com.zp.tools.CommonSQL;
import com.zp.tools.CommonStaticData;
import com.zp.tools.DateUtil;
import com.zp.tools.StringUtil;

public class AbChargebackContrller extends Controller {

	/**
	 * 申请退单
	 */
	public void addchargeback() {
		SysUser abuser = this.getSessionAttr("abuser");
		if (abuser == null || abuser.get("id") == null) {
			redirect("/ab/login");
		} else {
			String inputRandomCode = this.getPara("backCaptchaCode");
			String orderId = this.getPara("orderid");
			String content = this.getPara("applyBackContent");
			String img_url = this.getPara("apyimg");
			Object objMd5RandomCode = this
					.getSessionAttr(CaptchaRender.DEFAULT_CAPTCHA_MD5_CODE_KEY);
			// 验证验证码
			if (!CaptchaRender.validate(objMd5RandomCode.toString(),
					inputRandomCode)) {
				this.renderJson("1");
			}
			// 验证content
			else if (StringUtil.isNull(content) || content.length() > 100) {
				this.renderJson("2");
			}
			//判断是否已经申请退单
			else if(AbOrderChargeback.dao.findFirst("select * from ab_order_chargeback where order_id = '"+orderId+"'")!=null){
				this.renderJson("3");
			}
			// 插入退单记录
			else {
				abuser = SysUser.dao.findById(abuser.get("id"));
				AbOrder order = AbOrder.dao.findById(orderId);
				AbOrderChargeback cb = new AbOrderChargeback();
				cb.set(AbOrderChargeback.ID, StringUtil.getUuid32());
				cb.set(AbOrderChargeback.MER_ID, order.getStr(AbOrder.MID));
				cb.set(AbOrderChargeback.XDR_ID, order.getStr(AbOrder.XDRID));
				cb.set(AbOrderChargeback.QDR_ID, order.getStr(AbOrder.QDRID));
				cb.set(AbOrderChargeback.ORDER_ID, orderId);
				cb.set(AbOrderChargeback.APPLY_ID, abuser.get(SysUser.ID));
				cb.set(AbOrderChargeback.APPLY_DESC, content);
				cb.set(AbOrderChargeback.APPLY_TIME, DateUtil.getCurrentDate());
				cb.set(AbOrderChargeback.STATUS, CommonStaticData.CB_STATUS1);
//				申请人类型 1 用户 2商户 3业务员
				if(CommonStaticData.USER_TYPE_MEMBER.equals(abuser.get(SysUser.ROLE_ID))){
					cb.set(AbOrderChargeback.CB_STATUS, CommonStaticData.CB_TYPE1);
					cb.set(AbOrderChargeback.APPLY_TYPE, CommonStaticData.CB_APPLYTYPE_MEMBER);
				}else if(CommonStaticData.USER_TYPE_MERCHANT.equals(abuser.get(SysUser.ROLE_ID))){
					cb.set(AbOrderChargeback.CB_STATUS, CommonStaticData.CB_TYPE2);
					cb.set(AbOrderChargeback.APPLY_TYPE, CommonStaticData.CB_APPLYTYPE_MERCHANT);
				}else if(CommonStaticData.USER_TYPE_SERVICE.equals(abuser.get(SysUser.ROLE_ID))){
					cb.set(AbOrderChargeback.CB_STATUS, CommonStaticData.CB_TYPE8);
					cb.set(AbOrderChargeback.APPLY_TYPE, CommonStaticData.CB_APPLYTYPE_SERVICE);
				}
				if (!StringUtil.isNull(img_url)) {
					FileInputStream file = null;
					try {
						file = new FileInputStream(PathKit.getWebRootPath()
								+ "\\upload\\" + img_url);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					cb.set(AbOrderChargeback.APPLY_IMG_URL, img_url);
					cb.set(AbOrderChargeback.APPLY_IMG, file);
				}
				cb.save();
				
				String sn=order.getStr("sn");
				String userid=abuser.getStr("id");
				String roleid=abuser.getStr("role_id");
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String str_date =df.format(new Date());
				Record record=new Record();
				record.set("id", StringUtil.getUuid32());
				record.set("user_id", userid);
				record.set("role_id", roleid);
				record.set("mes_title", "退单消息");
				record.set("mes_type", "0");
				record.set("isread", "0");
				record.set("text", "申请退单成功！订单号："+sn);
				record.set("send_date", str_date);
				Db.save("message", record);
				
				this.renderJson("0");
			}
		}
	}
	
	/*用户，商户增加补充证据*/
	public void addcomplement(){
		SysUser abuser = this.getSessionAttr("abuser");
		if (abuser == null || abuser.get("id") == null) {
			redirect("/ab/login");
		} else {
			String cbid = this.getPara("cbid");
			String inputRandomCode = this.getPara("comCaptchaCode");
			String content = this.getPara("comContent");
			String img_url = this.getPara("apyimg");
			Object objMd5RandomCode = this
					.getSessionAttr(CaptchaRender.DEFAULT_CAPTCHA_MD5_CODE_KEY);
			// 验证验证码
			if (!CaptchaRender.validate(objMd5RandomCode.toString(),
					inputRandomCode)) {
				this.renderJson("1");
			}
			// 验证content
			else if (StringUtil.isNull(content) || content.length() > 100) {
				this.renderJson("2");
			}// 插入退单补充记录
			else{
				abuser = SysUser.dao.findById(abuser.get("id"));
				AbOrderChargebackItem aoci = new AbOrderChargebackItem();
				aoci.set(AbOrderChargebackItem.ID, StringUtil.getUuid32());
				aoci.set(AbOrderChargebackItem.CBID, cbid);
				aoci.set(AbOrderChargebackItem.UID, abuser.getStr(SysUser.ID));
				if(CommonStaticData.USER_TYPE_MEMBER.equals(abuser.get(SysUser.ROLE_ID))){
					aoci.set(AbOrderChargebackItem.TYPE, CommonStaticData.CB_APPLYTYPE_MEMBER);
				}else if(CommonStaticData.USER_TYPE_MERCHANT.equals(abuser.get(SysUser.ROLE_ID))){
					aoci.set(AbOrderChargebackItem.TYPE, CommonStaticData.CB_APPLYTYPE_MERCHANT);
				}else if(CommonStaticData.USER_TYPE_SERVICE.equals(abuser.get(SysUser.ROLE_ID))){
					aoci.set(AbOrderChargebackItem.TYPE, CommonStaticData.CB_APPLYTYPE_SERVICE);
				}
				aoci.set(AbOrderChargebackItem.CONTENT, content);
				aoci.set(AbOrderChargebackItem.DATETIME, DateUtil.getCurrentDate());
				if (img_url != null) {
					FileInputStream file = null;
					try {
						file = new FileInputStream(PathKit.getWebRootPath()
								+ "\\upload\\" + img_url);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					aoci.set(AbOrderChargebackItem.IMG_URL, img_url);
					aoci.set(AbOrderChargebackItem.IMAGE, file);
				}
				aoci.save();
				
				String userid=abuser.getStr("id");
				String roleid=abuser.getStr("role_id");
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String str_date =df.format(new Date());
				Record record=new Record();
				record.set("id", StringUtil.getUuid32());
				record.set("user_id", userid);
				record.set("role_id", roleid);
				record.set("mes_title", "退单消息");
				record.set("mes_type", "0");
				record.set("isread", "0");
				record.set("text", "证据已补充！");
				record.set("send_date", str_date);
				Db.save("message", record);
				
				this.renderJson("0");
			}
		}
	}

	/*商户退单列表*/
	public void listcbformer() {
		SysUser abuser = this.getSessionAttr("abuser");
		if (abuser == null || abuser.get("id") == null) {
			redirect("/ab/login");
		} else {
			String sn = this.getPara("sn");
			String mc = this.getPara("mc");
			String status = this.getPara("status");
			String mobile = this.getPara("mobile");
			PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),
					14, CommonSQL.findChargeBackForMerchant(abuser.getStr(SysUser.ID), sn, mc, mobile, status));
			this.setAttr("listpage", listpage);
			this.render("/ab/mer/listorderchargeback.html");
		}
	
	}
	
	/*用户退单列表*/
	public void listcbforuser() {
		SysUser abuser = this.getSessionAttr("abuser");
		if (abuser == null || abuser.get("id") == null) {
			redirect("/ab/login");
		} else {
			String sn = this.getPara("sn");
			String name = this.getPara("mname");
			String status = this.getPara("status");
			PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),
					14, CommonSQL.findChargeBackForUser(abuser.getStr(SysUser.ID), sn, name, status));
			this.setAttr("listpage", listpage);
			this.render("/ab/user/listorderchargeback.html");
		}
	}
	
	/*业务员退单列表*/
	public void listcbforser() {
		SysUser abuser = this.getSessionAttr("abuser");
		if (abuser == null || abuser.get("id") == null) {
			redirect("/ab/login");
		} else {
			String sn = this.getPara("sn");
			String mc = this.getPara("mc");
			String mobile = this.getPara("mobile");
			String status = this.getPara("status");
			PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),
					14, CommonSQL.findChargeBackForService(abuser.getStr(SysUser.ID), sn, mc, mobile, status));
			this.setAttr("listpage", listpage);
			this.render("/ab/mer/listorderchargeback.html");
		}
	}
	
	/*商户回复 1同意退单，2申诉*/
	public void addcbReponse(){
		SysUser abuser = this.getSessionAttr("abuser");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str_date =df.format(new Date());
		if (abuser == null || abuser.get("id") == null) {
			redirect("/ab/login");
		} else {
			String cbid = this.getPara("cbid");
			String agree = this.getPara("agree");
			String inputRandomCode = this.getPara("captchaCode");
			String content = this.getPara("content");
			Object objMd5RandomCode = this
					.getSessionAttr(CaptchaRender.DEFAULT_CAPTCHA_MD5_CODE_KEY);
			// 验证验证码
			if (!CaptchaRender.validate(objMd5RandomCode.toString(),
					inputRandomCode)) {
				this.renderJson("1");
			}
			// 验证content
			else if (StringUtil.isNull(content) || content.length() > 100) {
				this.renderJson("2");
			}// 更新退单记录
			else {
				AbOrderChargeback cb = AbOrderChargeback.dao.findById(cbid);
				cb.set(AbOrderChargeback.REP_DESC, content);
				cb.set(AbOrderChargeback.REP_TIME, DateUtil.getCurrentDate());
				SysUser mer = SysUser.dao.findById(abuser.getStr(SysUser.ID));
				SysUser user = SysUser.dao.findById(cb.getStr(AbOrderChargeback.XDR_ID));
				//同意
				if(agree.equals("1")){
					//更新退单状态
					cb.set(AbOrderChargeback.CB_STATUS, CommonStaticData.CB_TYPE6);
					cb.set(AbOrderChargeback.STATUS, CommonStaticData.CB_STATUS3);
					//金额
					AbOrder order = AbOrder.dao.findById(cb.getStr(AbOrderChargeback.ORDER_ID));
					BigDecimal money = order.getBigDecimal(AbOrder.DDZJE);
					//更新订单状态
					order.set(AbOrder.DDZT, "5");
					order.update();
					//商户余额金额减少
					
					BigDecimal merye = mer.getBigDecimal(SysUser.ZHYE);
					mer.set(SysUser.ZHYE, merye.subtract(money));
					mer.update();
					//用户余额增加
					
					BigDecimal userye = mer.getBigDecimal(SysUser.ZHYE);
					user.set(SysUser.ZHYE, userye.add(money));
					//判断是否已经支付，如果已经支付，则扣除积分
					if("1".equals(order.getStr(AbOrder.ZFZT))){
						int jf = user.getInt(SysUser.JIFEN);
						int mjf = money.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
						user.set(SysUser.JIFEN, jf-mjf);
					}
					user.update();
					
					String sn=order.getStr("sn");
					String userid=user.getStr("id");
					String roleid=user.getStr("role_id");	
					Record record=new Record();
					record.set("id", StringUtil.getUuid32());
					record.set("user_id", userid);
					record.set("role_id", roleid);
					record.set("mes_title", "退单消息");
					record.set("mes_type", "0");
					record.set("isread", "0");
					record.set("text", "商户同意退单！订单号："+sn);
					record.set("send_date", str_date);
					Db.save("message", record);
					
				}else{
					cb.set(AbOrderChargeback.CB_STATUS, CommonStaticData.CB_TYPE3);
					
					String userid=user.getStr("id");
					String roleid=user.getStr("role_id");	
					Record record=new Record();
					record.set("id", StringUtil.getUuid32());
					record.set("user_id", userid);
					record.set("role_id", roleid);
					record.set("mes_title", "退单消息");
					record.set("mes_type", "0");
					record.set("isread", "0");
					record.set("text", "商户已提起申诉！");
					record.set("send_date", str_date);
					Db.save("message", record);
				}
				cb.update();
				this.renderJson("0");
			}
		}
	}
	
	/*用户回复，0不同意退款，1同意退款  2不同意退款并客服介入*/
	public void addcbJudge(){
		SysUser abuser = this.getSessionAttr("abuser");
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str_date =df.format(new Date());
		
		
		if (abuser == null || abuser.get("id") == null) {
			redirect("/ab/login");
		} else {
			String cbid = this.getPara("cbid");
			String agree = this.getPara("agree");
			String inputRandomCode = this.getPara("captchaCode");
			String content = this.getPara("content");
			Object objMd5RandomCode = this
					.getSessionAttr(CaptchaRender.DEFAULT_CAPTCHA_MD5_CODE_KEY);
			// 验证验证码
			if (!CaptchaRender.validate(objMd5RandomCode.toString(),
					inputRandomCode)) {
				this.renderJson("1");
			}
			// 验证content
			else if (agree.equals("2")&&(StringUtil.isNull(content) || content.length() > 100)) {
				this.renderJson("2");
			}// 更新退单记录
			
			
			else {
				AbOrderChargeback cb = AbOrderChargeback.dao.findById(cbid);
				SysUser user = SysUser.dao.findById(abuser.getStr(SysUser.ID));
				SysUser mer = SysUser.dao.findById(cb.getStr(AbOrderChargeback.XDR_ID));
				// 取消退单
				if(agree.equals("1")){
					cb.set(AbOrderChargeback.CB_STATUS, CommonStaticData.CB_TYPE4);
					cb.set(AbOrderChargeback.STATUS, CommonStaticData.CB_STATUS3);
					
					String userid=mer.getStr("id");
					String roleid=mer.getStr("role_id");	
					Record record=new Record();
					record.set("id", StringUtil.getUuid32());
					record.set("user_id", userid);
					record.set("role_id", roleid);
					record.set("mes_title", "退单消息");
					record.set("mes_type", "0");
					record.set("isread", "0");
					record.set("text", "用户已同意退款！");
					record.set("send_date", str_date);
					Db.save("message", record);
					
				}else if(agree.equals("0")){
					cb.set(AbOrderChargeback.CB_STATUS, CommonStaticData.CB_TYPE9);
					
					String userid=mer.getStr("id");
					String roleid=mer.getStr("role_id");	
					Record record=new Record();
					record.set("id", StringUtil.getUuid32());
					record.set("user_id", userid);
					record.set("role_id", roleid);
					record.set("mes_title", "退单消息");
					record.set("mes_type", "0");
					record.set("isread", "0");
					record.set("text", "用户不同意退款！");
					record.set("send_date", str_date);
					Db.save("message", record);
					
				}else{
					cb.set(AbOrderChargeback.JUDGE_ID, abuser.get(SysUser.ID));
					cb.set(AbOrderChargeback.JUDGE_DESC, content);
					cb.set(AbOrderChargeback.JUDGE_TIME, DateUtil.getCurrentDate());
					cb.set(AbOrderChargeback.CB_STATUS, CommonStaticData.CB_TYPE5);
					cb.set(AbOrderChargeback.STATUS, CommonStaticData.CB_STATUS2);
					
					String userid=mer.getStr("id");
					String roleid=mer.getStr("role_id");	
					Record record=new Record();
					record.set("id", StringUtil.getUuid32());
					record.set("user_id", userid);
					record.set("role_id", roleid);
					record.set("mes_title", "退单消息");
					record.set("mes_type", "0");
					record.set("isread", "0"); 
					record.set("text", "用户同意退款！用户申请了客服介入。");
					record.set("send_date", str_date);
					Db.save("message", record);
					
				}
				cb.update();
				this.renderJson("0");
			}
		}
	}
	
	/*申请客服介入*/
	public void applycbJudge(){
		SysUser abuser = this.getSessionAttr("abuser");
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str_date =df.format(new Date());
		
		if (abuser == null || abuser.get("id") == null) {
			redirect("/ab/login");
		} else {
			String cbid = this.getPara("cbid");
			String inputRandomCode = this.getPara("captchaCode");
			String content = this.getPara("content");
			Object objMd5RandomCode = this
					.getSessionAttr(CaptchaRender.DEFAULT_CAPTCHA_MD5_CODE_KEY);
			// 验证验证码
			if (!CaptchaRender.validate(objMd5RandomCode.toString(),
					inputRandomCode)) {
				this.renderJson("1");
			}
			// 验证content
			else if (StringUtil.isNull(content) || content.length() > 100) {
				this.renderJson("2");
			}// 更新退单记录
			else {
				AbOrderChargeback cb = AbOrderChargeback.dao.findById(cbid);
				cb.set(AbOrderChargeback.JUDGE_ID, abuser.get(SysUser.ID));
				cb.set(AbOrderChargeback.JUDGE_DESC, content);
				cb.set(AbOrderChargeback.JUDGE_TIME, DateUtil.getCurrentDate());
				cb.set(AbOrderChargeback.CB_STATUS, CommonStaticData.CB_TYPE5);
				cb.set(AbOrderChargeback.STATUS, CommonStaticData.CB_STATUS2);
				cb.update();
				
				String userid=abuser.getStr("id");
				String roleid=abuser.getStr("role_id");	
				Record record=new Record();
				record.set("id", StringUtil.getUuid32());
				record.set("user_id", userid);
				record.set("role_id", roleid);
				record.set("mes_title", "退单消息");
				record.set("mes_type", "0");
				record.set("isread", "0"); 
				record.set("text", "您已申请了客服介入！");
				record.set("send_date", str_date);
				Db.save("message", record);
				
				this.renderJson("0");
			}
		}
	}
	
	/*退单处理显示*/
	public void showcb(){
		SysUser abuser = this.getSessionAttr("abuser");
		if (abuser == null || abuser.get("id") == null) {
			redirect("/ab/login");
		} else {
			String cbid = this.getPara("id");
			List<Record> list =  Db.find(CommonSQL.getChargebackById(cbid));
			CommonProcess.createChargebackImageFile(list);

			//查询补充说明
			List<Record> items = Db.find(CommonSQL.getChargebackItemByCbid(cbid));
			CommonProcess.createImageFile(items);
			this.setAttr("items", items);
			this.setAttr("role", abuser.getStr(SysUser.ROLE_ID));
			this.setAttr("cb", list.get(0));
			this.render("/ab/user/listcbdetail.html");
		}
	}

	//商户申请退单列表
	public void listmerordercb(){
		String ddzt = StringUtil.toStr(this.getPara("ddzt"));
		String zfzt = StringUtil.toStr(this.getPara("zfzt"));
		String sjqrzt = StringUtil.toStr(this.getPara("sjqrzt"));
		String sfjd = StringUtil.toStr(this.getPara("sfjd"));
		
		SysUser abuser = this.getSessionAttr("abuser");
		StringBuffer sb = new StringBuffer();
		sb.append("select * from (");
		sb.append("select ao.*, aoc.id cbid, aoc.cb_status from (");
		sb.append("select * from ab_order where mid='"+abuser.getStr("id")+"' and ddzt<>'0' ");
		sb.append("AND TIMESTAMPDIFF(HOUR,xdsj,CURRENT_TIMESTAMP) > 1 ");
		sb.append("AND TIMESTAMPDIFF(HOUR,xdsj,CURRENT_TIMESTAMP) < 24 ");
		if(ddzt.length()>0){
			sb.append(" and ddzt='"+ddzt+"'");
		}
		if(zfzt.length()>0){
			sb.append(" and zfzt='"+zfzt+"'");
		}
		if(sjqrzt.length()>0){
			sb.append(" and sjqrzt='"+sjqrzt+"'");
		}
		if(sfjd.length()>0){
			sb.append(" and sfjd='"+sfjd+"'");
		}
		sb.append(") ao left join ab_order_chargeback aoc on aoc.order_id = ao.id");
		sb.append(") a where cbid is null");
		sb.append(" order by xdsj desc");
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sb.toString());
		this.keepPara();
		this.setAttr("listpage", listpage);
		this.render("/ab/mer/listmerordercb.html");
	}
	
	//业务员申请退单列表
	public void listserordercb(){
		String ddzt = StringUtil.toStr(this.getPara("ddzt"));
		String zfzt = StringUtil.toStr(this.getPara("zfzt"));
		String sjqrzt = StringUtil.toStr(this.getPara("sjqrzt"));
		String sfjd = StringUtil.toStr(this.getPara("sfjd"));
		
		SysUser abuser = this.getSessionAttr("abuser");
		StringBuffer sb = new StringBuffer();
		sb.append("select * from (");
		sb.append("select ao.*, aoc.id cbid, aoc.cb_status from (");
		sb.append("select * from ab_order where qdrid='"+abuser.getStr("id")+"' and ddzt<>'0' ");
		sb.append("AND TIMESTAMPDIFF(HOUR,xdsj,CURRENT_TIMESTAMP) > 1 ");
		sb.append("AND TIMESTAMPDIFF(HOUR,xdsj,CURRENT_TIMESTAMP) < 24 ");
		if(ddzt.length()>0){
			sb.append(" and ddzt='"+ddzt+"'");
		}
		if(zfzt.length()>0){
			sb.append(" and zfzt='"+zfzt+"'");
		}
		if(sjqrzt.length()>0){
			sb.append(" and sjqrzt='"+sjqrzt+"'");
		}
		if(sfjd.length()>0){
			sb.append(" and sfjd='"+sfjd+"'");
		}
		sb.append(") ao left join ab_order_chargeback aoc on aoc.order_id = ao.id");
		sb.append(") a where cbid is null");
		sb.append(" order by xdsj desc");
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sb.toString());
		this.keepPara();
		this.setAttr("listpage", listpage);
		this.render("/ab/mer/listmerordercb.html");
	}
}
