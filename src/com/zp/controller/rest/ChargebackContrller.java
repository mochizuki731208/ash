/**
 * 
 */
package com.zp.controller.rest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.zp.config.CaptchaRender;
import com.zp.entity.AbOrder;
import com.zp.entity.AbOrderChargeback;
import com.zp.entity.AbOrderChargebackItem;
import com.zp.entity.AbTcExpressOrder;
import com.zp.entity.LogUserDeposit;
import com.zp.entity.SysCzTx;
import com.zp.entity.SysUser;
import com.zp.tools.CommonStaticData;
import com.zp.tools.DateUtil;
import com.zp.tools.StringUtil;

/**
 * @author Administrator
 *
 */
public class ChargebackContrller extends AbstractRestController {
	/**
	 * 回复维权
	 */
	public void backChange() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String userid = this.getPara("uid");
		String backId = this.getPara("backId");
		String backCon = this.getPara("backCon");
		if (StringUtil.isNull(userid) || StringUtil.isNull(backId) || StringUtil.isNull(backId)) {
			formatInvalidParamResponse(resultMap);
		} else {
			AbOrderChargeback chargeback = AbOrderChargeback.dao.findById(backId);
			if(null != chargeback) {
				chargeback.set(AbOrderChargeback.REP_DESC, backCon);
				chargeback.set(AbOrderChargeback.REP_TIME, DateUtil.getCurrentDate());
				chargeback.update();
				resultMap.put("result", RESULT_SUCCESS);
				resultMap.put("msg", "回复成功");
			} else {
				resultMap.put("result", RESULT_FAIL);
				resultMap.put("msg", "回复失败,数据异常");
			}
		}
		renderJson(resultMap);
	}
	
	/**
	 * 获取订单对应的维权列表
	 */
	public void getChageBackList() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String userid = this.getPara("uid");
		String orderId = this.getPara("orderid");
		if (StringUtil.isNull(userid) || StringUtil.isNull(orderId)) {
			formatInvalidParamResponse(resultMap);
		} else {
			AbOrder order = AbOrder.dao.findById(orderId);
			if(order == null){
				AbTcExpressOrder tc = 	AbTcExpressOrder.dao.findById(orderId);
				if(tc!=null){
					order = AbOrder.dao.findFirst("select * from ab_order where sn = ?",tc.getStr("sn"));
					if(order!=null){
						orderId = order.getStr("id");
					}
				}
			}
			List<AbOrderChargeback> list = AbOrderChargeback.dao.find("SELECT aocb.*,tu.mc FROM ab_order_chargeback AS aocb,sys_user  AS tu WHERE aocb.apply_id = tu.id AND  aocb.order_id=?", new Object[]{orderId});
			resultMap.put("result", RESULT_SUCCESS);
			resultMap.put("msg", "查询成功");
			resultMap.put("data", list);
		}
		renderJson(resultMap);
	}
	/**
	 * 获取订单对应的维权列表
	 */
	public void acc() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String userid = this.getPara("uid");
		String orderId = this.getPara("orderid");
		if (StringUtil.isNull(userid) || StringUtil.isNull(orderId)) {
			formatInvalidParamResponse(resultMap);
		} else {
			AbOrder order = AbOrder.dao.findById(orderId);
			if(order == null){
				AbTcExpressOrder tc = 	AbTcExpressOrder.dao.findById(orderId);
				if(tc!=null){
					order = AbOrder.dao.findFirst("select * from ab_order where sn = ?",tc.getStr("sn"));
					if(order!=null){
						orderId = order.getStr("id");
					}
				}
			}
			Record r = Db.findFirst("SELECT aocb.*,tu.mc FROM ab_order_chargeback AS aocb,sys_user  AS tu WHERE aocb.apply_id = tu.id AND  aocb.order_id=?", new Object[]{orderId});
			resultMap.put("result", RESULT_SUCCESS);
			resultMap.put("msg", "查询成功");
			resultMap.put("data", r);
		}
		renderJson(resultMap);
	}
	public void list() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String userid = this.getPara("uid");
		Integer pi = this.getParaToInt("pi");
		Integer ps = this.getParaToInt("ps");
		if (!checkParam(pi)) {
			pi = 1;
		}
		if (!checkParam(ps)) {
			ps = 10;
		}
		if (StringUtil.isNull(userid)) {
			formatInvalidParamResponse(resultMap);
		} else {
			List<Record> list = Db.find("SELECT a.*,b.sn as sn FROM ab_order_chargeback a join ab_order b where a.order_id=b.id AND  a.xdr_id=? order by apply_time desc limit ?,?",  userid,getStart(pi, ps),ps);
			resultMap.put("result", RESULT_SUCCESS);
			resultMap.put("msg", "查询成功");
			resultMap.put("data", list);
		}
		renderJson(resultMap);
	}
	
	/**
	 * 用户申请退单 参数 userid 用户ID orderid 订单ID content 描述 apyimg 图片路径
	 * 
	 * 返回值 true or false
	 */
	public void addchargeback() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String userid = this.getPara("uid");
		String orderId = this.getPara("orderid");
		String content = this.getPara("content");
		String img_url = this.getPara("apyimg");
		if (StringUtil.isNull(userid) || StringUtil.isNull(orderId)
				|| StringUtil.isNull(content)) {
			formatInvalidParamResponse(resultMap);
		} else {
			// 插入退单记录
			SysUser abuser = SysUser.dao.findById(userid);
			AbOrder order = AbOrder.dao.findFirst("SELECT * FROM ab_order WHERE sn='" + orderId + "' ");
			if(order ==null){
				order =  AbOrder.dao.findFirst("SELECT * FROM ab_order WHERE id='" + orderId + "' ");
			}
			if(null == abuser || null == order) {
				resultMap.put("result", RESULT_FAIL);
				resultMap.put("msg", "数据异常,请稍后重试!");
				
			}else{
				AbOrderChargeback cb = null;
				cb = AbOrderChargeback.dao.findFirst("select * from ab_order_chargeback where order_id = '"+orderId+"'");
				boolean flag = false;
				if( cb == null){
					cb = new AbOrderChargeback();
					cb.set(AbOrderChargeback.ID, StringUtil.getUuid32());
					flag = true;
				}
				 
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
				if(flag){
					cb.update();
				}else{
					cb.save();
				}
				resultMap.put("result", RESULT_SUCCESS);
				resultMap.put("msg", "维权成功,请耐心等待调解结果");
		   }
		}
		renderJson(resultMap);
	}
	/*用户，商户增加补充证据*/
	public void addcomplement(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String userid = this.getPara("uid");
		String orderId = this.getPara("orderid");
		if (StringUtil.isNull(userid) || StringUtil.isNull(orderId)
			) {
			formatInvalidParamResponse(resultMap);
		} else {
			String cbid = this.getPara("cbid");
			String content = this.getPara("content");
			String img_url = this.getPara("apyimg");
			// 验证验证码
			// 验证content
				SysUser abuser = SysUser.dao.findById(userid);
				AbOrderChargebackItem aoci = new AbOrderChargebackItem();
				aoci.set(AbOrderChargebackItem.ID, StringUtil.getUuid32());
				aoci.set(AbOrderChargebackItem.CBID, cbid);
				aoci.set(AbOrderChargebackItem.UID,userid);
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
				resultMap.put("result", RESULT_SUCCESS);
				resultMap.put("msg", "维权成功,请耐心等待调解结果");
			}
		renderJson(resultMap);
	}

	/**
	 * 获取评价列表 参数 userid 用户ID
	 * 
	 * 返回值 true or false
	 */
	public void getPjList() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String uid = this.getPara("uid");
		String roleId = this.getPara("roleId"); 
		if (StringUtil.isNull(uid) || StringUtil.isNull(roleId)) {
			formatInvalidParamResponse(resultMap);
		} else {
			StringBuffer sb = new StringBuffer("select * from (select ");
			sb.append(" aa.id,aa.user_id,aa.mer_id,aa.order_id, aa.qdr_id, aa.datetime, ");
			sb.append(" aa.type,aa.content, aa.dync_mah, aa.dync_ser, aa.dync_spd, ");
			sb.append(" aa.dync_val, aa.private AS privates,");		
			sb.append(" su.mc, am.mc mmc, ao.sn, aai.img_url from ");
			if (CommonStaticData.USER_TYPE_MERCHANT.equals(roleId)){//商家
				sb.append("(select * from ab_appraise where mer_id = '" + uid + "') aa ");
			}  else if(CommonStaticData.USER_TYPE_SERVICE.equals(roleId)){//司机
				sb.append("(select * from ab_appraise where qdr_id = '" + uid + "') aa ");
			}  else {//用户
				sb.append("(select * from ab_appraise where user_id = '" + uid + "') aa ");
			}			  
			sb.append("left join sys_user su on aa.user_id = su.id ");
			sb.append("left join ab_merchant am on aa.mer_id = am.id ");
			sb.append("left join ab_order ao on aa.order_id = ao.id ");
			sb.append("LEFT JOIN ab_appraise_img aai ON aai.app_id = ao.id  ");
			sb.append(") t where 1=1 ");
			List<Record> apps = Db.find(sb.toString());
			resultMap.put("plList", apps);
			resultMap.put("result", RESULT_SUCCESS);
			resultMap.put("msg", "评价列表获取成功");
		}
		renderJson(resultMap);
	}
	
	public void saveCzResult() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String uid = this.getPara("uid");
		String loginid = this.getPara("loginid");
		String mc = this.getPara("mc");
		String czje = this.getPara("czje");
		String czResult = this.getPara("czResult");
		try {
			if (StringUtil.isNull(uid) || StringUtil.isNull(loginid) || StringUtil.isNull(czje)) {
				formatInvalidParamResponse(resultMap);
			} else {
				if("1".equals(czResult)) {
					SysUser abuser = SysUser.dao.findById(uid);
					if(null != abuser) {
						BigDecimal ud = abuser.getBigDecimal(SysUser.ZHYE);
						double f = Double.parseDouble(czje);
						ud = ud.add(BigDecimal.valueOf(f));
						abuser.set(SysUser.ZHYE,ud);
						abuser.update();
					}
				}
				LogUserDeposit lud = new LogUserDeposit();
				String ludid = StringUtil.getUuid32();
				lud.set("id", ludid);
				lud.set("userid", uid);
				lud.set("loginid", loginid);
				lud.set("mc", mc);
				lud.set("czje", czje);
				// '充值结果[1: -成功、0:  -失败     2: 支付结果确认中]',
				lud.set("status", czResult);
				lud.set("remark", mc+ "手机充值" + czje + "元");
				lud.save();
				resultMap.put("result", RESULT_SUCCESS);
				resultMap.put("msg", "操作成功");
			}
		} catch (Exception e) {
			resultMap.put("result", RESULT_FAIL);
			resultMap.put("msg", "系统错误，请稍后重试!");
			e.printStackTrace();
			RestLogUtils.writeToLocal(RestLogUtils.getStackTrace(e), true);
		}
		this.renderJson(resultMap);
	}
	
	public void alipaySuccess() {		
//		Map<String, String[]>  map = this.getParaMap();
//		StringBuilder sb = new StringBuilder();
//		 for (Map.Entry<String, String[]> entry : map.entrySet()) {
//			   String strs[] = entry.getValue();
//			   if(null != strs) {
//				   int leng = strs.length;
//				   sb.append(" name " + entry.getKey() +"  val: ");
//				   for (int i = 0; i < leng; i++) {
//					   sb.append(strs[i]+"  ");
//				   }
//				   sb.append("\r\n");
//			   }
//		}
//		RestLogUtils.writeToLocal(sb.toString(), true);
		this.renderJson("{\"result\":0,\"isEnable\":\"1\"}");
	}
	
	/**
	 * 提现数据处理
	 *  
	 */
	public void txAction()  {
		String uid = this.getPara("uid");
		String WIDtotal_fee = StringUtil.toStr(this.getPara("WIDtotal_fee"));//提现金额
		String WIDout_trade_no = StringUtil.toStr(this.getPara("WIDout_trade_no"));//交易流水号
		String zffs = StringUtil.toStr(this.getPara("zzfs"));//提现方式
		String zfbzh = StringUtil.toStr(this.getPara("zfbzh"));//支付宝账号
		String yhkid = StringUtil.toStr(this.getPara("yhkid"));//提现银行卡id
		String yhknum = StringUtil.toStr(this.getPara("yhknum"));//提现银行卡卡号
		String zfpwd = StringUtil.toStr(this.getPara("zfpwd"));//支付密码
		SysUser user = SysUser.dao.findById(uid);
		Map<String,Object> result = new HashMap<String,Object>();		
		try {
			if(Db.findFirst("select count(*) ctn from sys_user_zfpwd where userid='" + user.getStr("loginid") + "' and zfpwd='" + zfpwd + "'").getLong("ctn") == 0){
				result.put("msg", "支付密码错误!");
				result.put("result", RESULT_FAIL);
			} else if(user.getBigDecimal("zhye").subtract(new BigDecimal(WIDtotal_fee)).doubleValue() < 0){//账户余额不足以提现
				result.put("result", RESULT_FAIL);
				result.put("msg", "账户余额不足!");
			} else {
				user.set("zhye", user.getBigDecimal("zhye").subtract(new BigDecimal(WIDtotal_fee)));//现将金额扣除，后期提现失败，提现金额退换
				user.update();
				this.setSessionAttr("abuser", user);
				if("0".equals(zffs)){//支付宝提现
					SysCzTx cztx = new SysCzTx();
					cztx.set("id", StringUtil.getUuid32());
					cztx.set("tradeno", WIDout_trade_no);
					cztx.set("type", 1);//0-充值 1-提现 2-付款
					cztx.set("result", 0);//0-等待 1-成功 2-失败
					cztx.set("totalfee", WIDtotal_fee);
					cztx.set("userid", user.getStr("id"));
					cztx.set("mc", user.getStr("mc"));
					cztx.set("time", DateUtil.getCurrentDate());
					cztx.set("zfbaddress", zfbzh);
					cztx.set("txtype", 0);//提现种类 0-支付宝  1-银行卡
					cztx.save();
				} else {//银行卡提现
					SysCzTx cztx = new SysCzTx();
					cztx.set("id", StringUtil.getUuid32());
					cztx.set("tradeno", WIDout_trade_no);
					cztx.set("type", 1);//0-充值 1-提现 2-付款
					cztx.set("result", 0);//0-等待 1-成功 2-失败
					cztx.set("totalfee", WIDtotal_fee);
					cztx.set("userid", user.getStr("id"));
					cztx.set("mc", user.getStr("mc"));
					cztx.set("txtype", 1);//提现种类 0-支付宝  1-银行卡
					cztx.set("txyhkid", yhkid);
					cztx.set("txyhknum", yhknum);
					cztx.set("time", DateUtil.getCurrentDate());
					cztx.save();
				}
				result.put("msg", "提现申请已经提交，约7个工作日内到账，请您耐心等待!");
				result.put("result", RESULT_SUCCESS);
			}
		} catch (Exception e) {
			result.put("result", RESULT_FAIL);
			result.put("msg", "系统错误，请稍后重试!");
			e.printStackTrace();
			RestLogUtils.writeToLocal(RestLogUtils.getStackTrace(e), true);
		}
		this.renderJson(result);
	}
	
}
