package com.zp.controller.admin;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.zp.entity.AbCard;
import com.zp.entity.AbCardExpense;
import com.zp.entity.AbIntegralScore;
import com.zp.entity.AbOrder;
import com.zp.entity.AbOrderChargeback;
import com.zp.entity.SysUser;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.tools.CommonProcess;
import com.zp.tools.CommonSQL;
import com.zp.tools.CommonStaticData;
import com.zp.tools.DateUtil;
import com.zp.tools.StringUtil;

public class AbChargebackContrller extends Controller{
	
	/**
	 * 仲裁管理
	 */
	public void listallcb(){
//		SysUser abuser = this.getSessionAttr("abuser");
		String sn = this.getPara("sn");
		String mnname = this.getPara("mnname");
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, CommonSQL.findAllChargeBack(sn, mnname));
		this.setAttr("listpage", listpage);
		this.render("/admin/chargeback/listallcb.html");
	}
	
	public void editcb(){
		String cbid = this.getPara("id");
		String isShow = this.getPara("isshow");
		List<Record> list =  Db.find(CommonSQL.getChargebackById(cbid));
		CommonProcess.createChargebackImageFile(list);
		List<Record> items = Db.find(CommonSQL.getChargebackItemByCbid(cbid));
		CommonProcess.createImageFile(items);
		this.setAttr("cb", list.get(0));
		this.setAttr("items", items);
		if("0".equals(isShow)){
			this.render("/admin/chargeback/editcb.html");
		}else{
			this.render("/admin/chargeback/showcb.html");
		}
			
	}
	
	/*管理员仲裁退单*/
	public void updatecb(){
		SysUser abuser = this.getSessionAttr("user");
		String agree = this.getPara("agree");
		String id = this.getPara("cbid");
		String content = this.getPara("content");
		String backmoney = this.getPara("money");
		String dateStr = DateUtil.getCurrentDate();
		AbOrderChargeback ocb = AbOrderChargeback.dao.findById(id);
		ocb.set(AbOrderChargeback.RESULT_DESC, content);
		ocb.set(AbOrderChargeback.RESULT_TIME, dateStr);
		ocb.set(AbOrderChargeback.SERVICE_ID, abuser.getStr(SysUser.ID));
		//退单成功
		if("1".equals(agree)){
			//金额
			AbOrder order = AbOrder.dao.findById(ocb.getStr(AbOrderChargeback.ORDER_ID));
			BigDecimal money = order.getBigDecimal(AbOrder.DDZJE);
			BigDecimal bm = new BigDecimal(backmoney);
			BigDecimal backm = new BigDecimal(backmoney);
			//更新订单状态
			ocb.set(AbOrderChargeback.CB_STATUS, CommonStaticData.CB_TYPE6);
			ocb.set(AbOrderChargeback.BACK_MONEY, backm);
			order.set(AbOrder.DDZT, "5");
			order.update();
			//查找该订单有没有用会员卡抵扣
			StringBuffer sb = new StringBuffer(CommonSQL.findAllCardExpense());
			sb.append(" and order_id = '").append(order.getStr(AbOrder.ID)).append("'");
			List<Record> records = Db.find(sb.toString());
			if(records != null && records.size()>0){
				for(Record r : records){
					//会员卡类型，消费卡
					if(CommonStaticData.CARD_TYPE_EXPENSE.equals(r.getStr("type"))){
						AbCard card = AbCard.dao.findById(r.getStr(AbCardExpense.CARD_ID));
						card.set(AbCard.STATUS, CommonStaticData.CARD_STATUS_VALID);
						BigDecimal decution = r.getBigDecimal(AbCardExpense.MONEY);
						//抵扣金额大于退还金额，将退还金额直接返回到会员卡中
						if(decution.compareTo(bm)==1){
							card.set(AbCard.RMONEY, card.getBigDecimal(AbCard.RMONEY).add(bm));
							card.update();
							bm = new BigDecimal(0);
							break;
						}else{
							card.set(AbCard.RMONEY, decution);
							card.update();
							bm = bm.subtract(r.getBigDecimal(AbCardExpense.MONEY));
						}
					}
				}
			}
			String sn = order.getStr(AbOrder.SN);
			SysUser mer = null;
			//普通订单
			if(CommonStaticData.ORDER_TYPE_DD.equals(sn.substring(0, 2))){
				mer = SysUser.dao.findById(ocb.getStr(AbOrderChargeback.MER_ID));
			}
			//同城下单
			else if(CommonStaticData.ORDER_TYPE_TC.equals(sn.substring(0, 2))
					||CommonStaticData.ORDER_TYPE_KS.equals(sn.substring(0, 2))){
				mer = SysUser.dao.findById(ocb.getStr(AbOrderChargeback.QDR_ID));
			}
			//商户余额金额减少
			BigDecimal merye = mer.getBigDecimal(SysUser.ZHYE);
			//加上订单金额，减去退款金额
			mer.set(SysUser.ZHYE, merye.add(money).subtract(backm));
			mer.update();
			//用户余额增加
			//如果退还金额>0，
			SysUser user = SysUser.dao.findById(ocb.getStr(AbOrderChargeback.XDR_ID));
			if(bm.doubleValue()>0){
				BigDecimal userye = mer.getBigDecimal(SysUser.ZHYE);
				user.set(SysUser.ZHYE, userye.add(bm));
			}
			
			//判断是否已经支付，如果已经支付，则扣除积分
			if("1".equals(order.getStr(AbOrder.ZFZT))){
				int jf = user.getInt(SysUser.JIFEN);
				int mjf = backm.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
				user.set(SysUser.JIFEN, jf-mjf);
				AbIntegralScore ail = new AbIntegralScore();
				ail.set(AbIntegralScore.ID, StringUtil.getRandString32());
				ail.set(AbIntegralScore.TYPE, CommonStaticData.SCORE_TYPE8);
				ail.set(AbIntegralScore.VALUE, 0-mjf);
				ail.set(AbIntegralScore.DATETIME, dateStr);
				ail.set(AbIntegralScore.USER_ID, user.getStr("id"));
				ail.set(AbIntegralScore.FID, order.getStr(AbOrder.ID));
				ail.save();
			}
			user.update();
		}else{
			ocb.set(AbOrderChargeback.CB_STATUS, CommonStaticData.CB_TYPE7);
		}
		ocb.set(AbOrderChargeback.STATUS, CommonStaticData.CB_STATUS3);
		ocb.update();
		this.redirect("/admin/chargeback/listallcb");
	}
	
	//一个月内的维权次数查询
	public void listcount(){
		String cnt = this.getPara("cnt");
		String mname = this.getPara("mname");
//		Date nowDate = new Date();
//		long st = DateUtil.getTimeSubtractOfDay(nowDate.getTime(), -30);
//		Date sd = new Date(st);
//		String startDate = DateUtil.format(sd, DateUtil.defDtPtn);
//		String endDate = DateUtil.getCurrentDate();
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, CommonSQL.findChargeBackCount(cnt, mname));
		this.setAttr("listpage", listpage);
		this.render("/admin/chargeback/listcount.html");	
	}
}
