package com.zp.controller.app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

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

public class AbChargebackContrller extends AbsAppController {

	/**
	 * 用户申请退单 参数 userid 用户ID orderid 订单ID content 描述 apyimg 图片路径
	 * 
	 * 返回值 true or false
	 */
	public void addchargeback() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String userid = this.getPara("userid");
		String orderId = this.getPara("orderid");
		String content = this.getPara("content");
		String img_url = this.getPara("apyimg");
		if (StringUtil.isNull(userid) || StringUtil.isNull(orderId)
				|| StringUtil.isNull(content)) {
			formatInvalidParamResponse(resultMap);
		} else {
			// 插入退单记录
			SysUser abuser = SysUser.dao.findById(userid);
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
			// 申请人类型 1 用户 2商户 3业务员
			if (CommonStaticData.USER_TYPE_MEMBER.equals(abuser
					.get(SysUser.ROLE_ID))) {
				cb.set(AbOrderChargeback.CB_STATUS, CommonStaticData.CB_TYPE1);
				cb.set(AbOrderChargeback.APPLY_TYPE,
						CommonStaticData.CB_APPLYTYPE_MEMBER);
			} else if (CommonStaticData.USER_TYPE_MERCHANT.equals(abuser
					.get(SysUser.ROLE_ID))) {
				cb.set(AbOrderChargeback.CB_STATUS, CommonStaticData.CB_TYPE2);
				cb.set(AbOrderChargeback.APPLY_TYPE,
						CommonStaticData.CB_APPLYTYPE_MERCHANT);
			} else if (CommonStaticData.USER_TYPE_SERVICE.equals(abuser
					.get(SysUser.ROLE_ID))) {
				cb.set(AbOrderChargeback.CB_STATUS, CommonStaticData.CB_TYPE8);
				cb.set(AbOrderChargeback.APPLY_TYPE,
						CommonStaticData.CB_APPLYTYPE_SERVICE);
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
			formatSuccessResponse(resultMap);
		}
		renderJson(resultMap);
	}
	public void acc() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String userid = this.getPara("uid");
		String orderId = this.getPara("orderid");
		if (StringUtil.isNull(userid) || StringUtil.isNull(orderId)
				 ) {
			formatInvalidParamResponse(resultMap);
		} else {
			Record r = Db.findFirst("select o.sn as sn,o.id,apply_desc,apply_img_url,apply_time,apply_type,apply_id,judge_desc,judge_time,judge_id,rep_desc,rep_img_url,rep_time from ab_order_chargeback join ab_order o on order_id=o.id  where order_id=? and xdr_id=?",orderId,userid);
			List<Record> ls = new ArrayList<Record>();
			if(r!=null){
				ls = Db.find("select * from ab_order_chargeback_item where cbid=?",r.getStr("id"));
			}
			resultMap.put("data", r);
			formatSuccessResponse(resultMap);
		}
		renderJson(resultMap);
	}
	public void list() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String userid = this.getPara("uid");
		Integer pi = this.getParaToInt("pi");
		Integer ps = this.getParaToInt("ps");
		if (StringUtil.isNull(userid) || pi ==null || ps ==null
		) {
			formatInvalidParamResponse(resultMap);
		} else {
			List<Record> list = Db.find("select o.sn as sn,o.id,apply_desc,apply_img_url,apply_time,apply_type,apply_id,judge_desc,judge_time,judge_id,rep_desc,rep_img_url,rep_time from ab_order_chargeback join ab_order o on order_id=o.id  where xdr_id=? limit ?,?",userid,getStart(pi, ps),ps);
			if(list!=null){
				for(Record r: list){
					List<Record> ls = new ArrayList<Record>();
					if(r!=null){
						ls = Db.find("select * from ab_order_chargeback_item where cbid=?",r.getStr("id"));
					}
					r.set("items", ls);
				}
			}
			resultMap.put("data", list);
			formatSuccessResponse(resultMap);
		}
		renderJson(resultMap);
	}

	// /* 用户，商户增加补充证据 */
	public void addcomplement() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String uid = this.getPara("uid");
		if (StringUtils.isEmpty(uid)) {
			formatInvalidParamResponse(resultMap);
		} else {
			String cbid = this.getPara("cbid");
			String content = this.getPara("comContent");
			String img_url = this.getPara("apyimg");
			 
			// 验证content
			 if (StringUtil.isNull(content)) {
				 resultMap.put("result", RESULT_FAIL);
				 resultMap.put("msg", "维权内容为空");
			}// 插入退单补充记录
			else {
				AbOrderChargebackItem aoci = new AbOrderChargebackItem();
				aoci.set(AbOrderChargebackItem.ID, StringUtil.getUuid32());
				aoci.set(AbOrderChargebackItem.CBID, cbid);
				aoci.set(AbOrderChargebackItem.UID, uid);
				aoci.set(AbOrderChargebackItem.TYPE, 1);
				aoci.set(AbOrderChargebackItem.CONTENT, content);
				aoci.set(AbOrderChargebackItem.DATETIME, DateUtil
						.getCurrentDate());
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
				formatSuccessResponse(resultMap);
			}
		}
		renderJson(resultMap);
	}

//	/* 商户退单列表 */
//	public void listcbformer() {
//		SysUser abuser = this.getSessionAttr("abuser");
//		if (abuser == null || abuser.get("id") == null) {
//			redirect("/ab/login");
//		} else {
//			String sn = this.getPara("sn");
//			String mc = this.getPara("mc");
//			String mobile = this.getPara("mobile");
//			PageUtil listpage = DbPage.paginate(
//					this.getParaToInt("curPage", 1), 14, CommonSQL
//							.findChargeBackForMerchant(abuser
//									.getStr(SysUser.ID), sn, mc, mobile));
//			this.setAttr("listpage", listpage);
//			this.render("/ab/mer/listorderchargeback.html");
//		}
//
//	}

//	/* 用户退单列表 */
//	public void listcbforuser() {
//		SysUser abuser = this.getSessionAttr("abuser");
//		if (abuser == null || abuser.get("id") == null) {
//			redirect("/ab/login");
//		} else {
//			String sn = this.getPara("sn");
//			String name = this.getPara("mname");
//			PageUtil listpage = DbPage.paginate(
//					this.getParaToInt("curPage", 1), 14, CommonSQL
//							.findChargeBackForUser(abuser.getStr(SysUser.ID),
//									sn, name));
//			this.setAttr("listpage", listpage);
//			this.render("/ab/user/listorderchargeback.html");
//		}
//	}

	/**
	 * 商户回复退单 参数 userid 用户ID cbid 退单ID agree 1同意退单，2申诉 content 描述
	 * 
	 * 返回值 true or false
	 */
	public void addcbReponse() {
		String userid = this.getPara("userid");
		String cbid = this.getPara("cbid");
		String agree = this.getPara("agree");
		String content = this.getPara("content");
		if (StringUtil.isNull(userid) || StringUtil.isNull(cbid)
				|| StringUtil.isNull(agree) || StringUtil.isNull(content)) {
			this.renderJson(false);
		} else {
			AbOrderChargeback cb = AbOrderChargeback.dao.findById(cbid);
			cb.set(AbOrderChargeback.REP_DESC, content);
			cb.set(AbOrderChargeback.REP_TIME, DateUtil.getCurrentDate());
			// 同意
			if (agree.equals("1")) {
				cb.set(AbOrderChargeback.CB_STATUS, CommonStaticData.CB_TYPE6);
				cb.set(AbOrderChargeback.STATUS, CommonStaticData.CB_STATUS3);
				// 金额
				AbOrder order = AbOrder.dao.findById(cb
						.getStr(AbOrderChargeback.ORDER_ID));
				BigDecimal money = order.getBigDecimal(AbOrder.DDZJE);
				// 更新订单状态
				order.set(AbOrder.DDZT, "5");
				order.update();
				// 商户余额金额减少
				SysUser mer = SysUser.dao.findById(userid);
				BigDecimal merye = mer.getBigDecimal(SysUser.ZHYE);
				mer.set(SysUser.ZHYE, merye.subtract(money));
				mer.update();
				// 用户余额增加
				SysUser user = SysUser.dao.findById(cb
						.getStr(AbOrderChargeback.XDR_ID));
				BigDecimal userye = mer.getBigDecimal(SysUser.ZHYE);
				user.set(SysUser.ZHYE, userye.add(money));
				// 判断是否已经支付，如果已经支付，则扣除积分
				if ("1".equals(order.getStr(AbOrder.ZFZT))) {
					int jf = user.getInt(SysUser.JIFEN);
					int mjf = money.setScale(0, BigDecimal.ROUND_HALF_UP)
							.intValue();
					user.set(SysUser.JIFEN, jf - mjf);
				}
				user.update();
			} else {
				cb.set(AbOrderChargeback.CB_STATUS, CommonStaticData.CB_TYPE3);
			}
			cb.update();
			this.renderJson(true);
		}
	}

	/**
	 * 商户回复退单 参数 userid 用户ID cbid 退单ID agree 0不同意退款，1同意退款 2不同意退款并客服介入 content 描述
	 * 
	 * 返回值 true or false
	 */
	public void addcbJudge() {
		String cbid = this.getPara("cbid");
		String agree = this.getPara("agree");
		String content = this.getPara("content");
		String userid = this.getPara("userid");
		if (StringUtil.isNull(cbid) || StringUtil.isNull(agree)
				|| StringUtil.isNull(content)|| StringUtil.isNull(userid)) {
			this.renderJson(false);
		} else {
			AbOrderChargeback cb = AbOrderChargeback.dao.findById(cbid);
			// 取消退单
			if (agree.equals("1")) {
				cb.set(AbOrderChargeback.CB_STATUS, CommonStaticData.CB_TYPE4);
				cb.set(AbOrderChargeback.STATUS, CommonStaticData.CB_STATUS3);
			} else if (agree.equals("0")) {
				cb.set(AbOrderChargeback.CB_STATUS, CommonStaticData.CB_TYPE9);
			} else {
				cb.set(AbOrderChargeback.JUDGE_ID, userid);
				cb.set(AbOrderChargeback.JUDGE_DESC, content);
				cb.set(AbOrderChargeback.JUDGE_TIME, DateUtil.getCurrentDate());
				cb.set(AbOrderChargeback.CB_STATUS, CommonStaticData.CB_TYPE5);
				cb.set(AbOrderChargeback.STATUS, CommonStaticData.CB_STATUS2);
			}
			cb.update();
			this.renderJson(true);
		}
	}

	/**
	 * 商户回复退单 参数 userid 用户ID cbid 退单ID  content 描述
	 * 
	 * 返回值 true or false
	 */
	
	public void applyJudge() {
		String cbid = this.getPara("cbid");
		String content = this.getPara("content");
		String userid = this.getPara("userid");
		if (StringUtil.isNull(cbid) || StringUtil.isNull(userid)
				|| StringUtil.isNull(content)) {
			this.renderJson(false);
		} else {
			AbOrderChargeback cb = AbOrderChargeback.dao.findById(cbid);
			cb.set(AbOrderChargeback.JUDGE_ID, userid);
			cb.set(AbOrderChargeback.JUDGE_DESC, content);
			cb.set(AbOrderChargeback.JUDGE_TIME, DateUtil.getCurrentDate());
			cb.set(AbOrderChargeback.CB_STATUS, CommonStaticData.CB_TYPE5);
			cb.set(AbOrderChargeback.STATUS, CommonStaticData.CB_STATUS2);
			cb.update();
			this.renderJson(true);
		}
	}
}
