package com.zp.controller.ab;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.zp.entity.AbCard;
import com.zp.entity.AbCardType;
import com.zp.entity.AbCardUser;
import com.zp.entity.AbCityarea;
import com.zp.entity.SysConfig;
import com.zp.entity.SysCzTx;
import com.zp.entity.SysUser;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.interceptor.AccessAbInterceptor;
import com.zp.tools.CommonProcess;
import com.zp.tools.CommonSQL;
import com.zp.tools.CommonStaticData;
import com.zp.tools.DateUtil;
import com.zp.tools.StringUtil;
import com.zp.tools.alipay.AlipaySubmit;
@SuppressWarnings("static-access")
public class AbCardController extends Controller {
	
	/*会员卡送餐卡购买页面*/
	public void purchasesend(){
		SysUser abuser = this.getSessionAttr("abuser");
//		if (abuser == null || abuser.get("id") == null) {
//			redirect("/ab/login");
//		} else {
			//查询有效，并且有制卡的卡类别记录
//			List<Record> cardtypes = Db.find("select * from ab_card_type where status = '1' and type = '1' and id in (select type_id from ab_card where status = '1' and purchase_status = '0')"); 
			//查询送餐卡
			List<Record> cardtypes = Db.find("select * from ab_card_type where status = '1' and type = '1' order by create_time ");
			CommonProcess.createImageFile(cardtypes);
			if (abuser != null && abuser.get("id") != null) {
				this.setAttr("userid", abuser.get("id"));
			}
			this.setAttr("cardtypes", cardtypes);
			render("/ab/card/hyk.html");
//		}
	}
	
	/*会员卡消费卡购买页面*/
	public void purchaseexpense(){
		SysUser abuser = this.getSessionAttr("abuser");
		
			//查询有效，并且有制卡的卡类别记录
//			List<Record> cardtypes = Db.find("select * from ab_card_type where status = '1' and type = '2' and id in (select type_id from ab_card where status = '1' and purchase_status = '0')"); 
//			CommonProcess.createImageFile(cardtypes);
			if (abuser != null && abuser.get("id") != null) {
				this.setAttr("userid", abuser.get("id"));
			}
			render("/ab/card/hykboss.html");
	}
	
	@Before(AccessAbInterceptor.class)
	public void hykorder(){
		SysUser abuser = this.getSessionAttr("abuser");
		if (abuser == null || abuser.get("id") == null) {
			redirect("/ab/login");
		} else {
			String id = this.getPara(0);
			AbCardType cardtype = AbCardType.dao.findById(id);
			CommonProcess.createSingleImage(cardtype.getBytes(AbCardType.IMAGE), cardtype.getStr(AbCardType.IMG_URL));
			this.setAttr("userid", abuser.get("id"));
			this.setAttr("vo", cardtype);
			render("/ab/card/hykorder.html");
			
		}
	}
	
//	@Before(AccessAbInterceptor.class)
//	public void checkCardType(){
//		String ddzje = this.getPara("total_ddzje");
//		SysUser user = this.getSessionAttr("abuser");
//		user = SysUser.dao.findById(user.get("id"));
//		BigDecimal zhye = user.getBigDecimal("zhye");
//		BigDecimal d_ddzje = BigDecimal.valueOf(Double.valueOf(ddzje));
//		
//		if(zhye.compareTo(d_ddzje)==-1){
//			renderJson("1");
//		}else{
//			String cnt = this.getPara("purchase_cnt");
//			String cardtypeid = this.getPara("cardtypeid");
//			List<AbCard> cards = AbCard.dao.find("select * from ab_card where status = '1' and purchase_status = '0' and type_id = '"+cardtypeid+"'");
//			if(Integer.valueOf(cnt) > cards.size()){
//				renderJson("2");
//			}else{
//				renderJson("3");
//			}
//		}
//	}
	
	/*购买会员卡*/
	@Before(AccessAbInterceptor.class)
	public synchronized void savehykgm(){
		SysUser user = this.getSessionAttr("abuser");
		String userid = user.get("id");
		user = SysUser.dao.findById(userid);
		
		String cnt = this.getPara("purchase_cnt");
		int count = Integer.valueOf(cnt);
		String cardtypeid = this.getPara("cardtypeid");
		AbCardType cardtype = AbCardType.dao.findById(cardtypeid);
		
		//用户余额
		BigDecimal zhye = user.getBigDecimal("zhye");
		//总价格
		double totalprice = count*cardtype.getBigDecimal(AbCardType.PRICE).doubleValue();
		BigDecimal tp = BigDecimal.valueOf(Double.valueOf(totalprice));
		
		String pway = StringUtil.toStr(this.getPara("p_way"));//支付方式，默认是充值直冲
		if(pway.length()==0){
			pway="0";
		}
		if(pway.equals("0")){//充值直冲
			//卡余额不足
			if(zhye.compareTo(tp)==-1){
				zhyefail();
			}else{
				//线上会员卡数量不足
//				List<AbCard> cards = AbCard.dao.find("select * from ab_card where status = '1' and purchase_status = '0' and sale_type = '1' and type_id = '"+cardtypeid+"'");
//				if(count > cards.size()){
//					this.setAttr("flag", "2");
//					render("/ab/card/hykfailure.html");
//				}else{
					//购买成功 1.创建用户购买会员卡记录，2，扣除余额
					//1创建记录
					String datestr = DateUtil.getCurrentDate();
					for(int i = 0;i<count;i++){
						AbCard card = new AbCard();
						card.set(AbCard.ID, StringUtil.getUuid32());
						card.set(AbCard.CARD_NUMBER, CommonProcess.getCardNumber());
						card.set(AbCard.CREATE_TIME, datestr);
						card.set(AbCard.RMONEY, cardtype.getBigDecimal(AbCardType.MONEY));
						card.set(AbCard.PURCHASE_STATUS, CommonStaticData.CARD_PURCHASE_VALID);
						card.set(AbCard.SALE_TYPE, CommonStaticData.CARD_SALETYPE_ONLINE);
						card.set(AbCard.TYPE_ID, cardtypeid);
						card.save();
						AbCardUser carduser = new AbCardUser();
						carduser.set(AbCardUser.ID, StringUtil.getUuid32());
						carduser.set(AbCardUser.USER_ID, userid);
						carduser.set(AbCardUser.CARD_ID, card.get(AbCard.ID));
						carduser.set(AbCardUser.P_WAY, pway);
						carduser.set(AbCardUser.P_DATETIME, datestr);
						carduser.save();
					}
//					for(int i = 0;i<count;i++){
//						AbCard card = cards.get(i);
//						//插入购买会员卡记录					
//						AbCardUser carduser = new AbCardUser();
//						carduser.set(AbCardUser.ID, StringUtil.getUuid32());
//						carduser.set(AbCardUser.USER_ID, userid);
//						carduser.set(AbCardUser.CARD_ID, card.get(AbCard.ID));
//						carduser.set(AbCardUser.P_WAY, pway);
//						carduser.set(AbCardUser.P_DATETIME, datestr);
//						carduser.save();
//						//更新会员卡状态
//						card.set(AbCard.PURCHASE_STATUS, "1");
//						card.update();
//					}
					//2 扣除价格
					user.set(SysUser.ZHYE, zhye.subtract(tp));
					user.update();
					render("/ab/card/hyksuccess.html");
//				}
			}
		}else{//支付宝充值
			String tradeno = System.currentTimeMillis() + "";
			String datestr = DateUtil.getCurrentDate();
			List<String> cardIdArr = new ArrayList<String>();
			for(int i = 0;i<count;i++){
				AbCard card = new AbCard();
				card.set(AbCard.ID, StringUtil.getUuid32());
				card.set(AbCard.CARD_NUMBER, CommonProcess.getCardNumber());
				card.set(AbCard.CREATE_TIME, datestr);
				card.set(AbCard.RMONEY, cardtype.getBigDecimal(AbCardType.MONEY));
				card.set(AbCard.PURCHASE_STATUS, CommonStaticData.CARD_PURCHASE_INVALID);
				card.set(AbCard.SALE_TYPE, CommonStaticData.CARD_SALETYPE_ONLINE);
				card.set(AbCard.TYPE_ID, cardtypeid);
				card.save();
				AbCardUser carduser = new AbCardUser();
				carduser.set(AbCardUser.ID, StringUtil.getUuid32());
				carduser.set(AbCardUser.USER_ID, userid);
				carduser.set(AbCardUser.CARD_ID, card.get(AbCard.ID));
				carduser.set(AbCardUser.P_WAY, pway);
				carduser.set(AbCardUser.P_DATETIME, datestr);
				carduser.save();
				cardIdArr.add(card.getStr(AbCard.ID));
			}
			if(cardIdArr.size() > 0){//将会员卡id，userid，tradeno记录在sys_tc_cz表中，方便回调函数进行操作.
				SysCzTx sct = new SysCzTx();
				sct.set("id", StringUtil.getUuid32());
				sct.set("tradeno", tradeno);
				sct.set("userid", userid);
				sct.set("orderid", StringUtil.join(cardIdArr,",",null,""));
				sct.save();
			}
			
			String return_url = "http://www.yijuhome.cn/ab/card/payBack";
			try {
				//tp = new BigDecimal("0.01");//模拟金额，正式上线之后，此行代码必须注释掉
				this.redirect(alipayapi(tradeno,"支付宝支付",tp.toString(),"在线购买会员卡",return_url));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void zhyefail(){
		render("/ab/card/hykfailure.html");
	}
	
	public void payBack(){
		String out_trade_no = StringUtil.toStr(this.getPara("out_trade_no"));
		String trade_status = StringUtil.toStr(this.getPara("trade_status"));
		List<SysCzTx> cztx_list = SysCzTx.dao.find("select * from sys_cz_tx where tradeno='" + out_trade_no + "'");
		if("TRADE_SUCCESS".equals(trade_status)){
			for(SysCzTx cztx : cztx_list){
				String userid = cztx.getStr("userid");
				String orderid = cztx.getStr("orderid");
				String[] cardidArr = orderid.split(",");
				for(String cardid : cardidArr){
					AbCard ac = AbCard.dao.findById(cardid);
					ac.set(AbCard.PURCHASE_STATUS, CommonStaticData.CARD_PURCHASE_VALID);
					ac.update();
				}
			}
		}else{//购买失败
			zhyefail();
		}
		this.render("/ab/card/hyksuccess.html");
	}
	public void getcard(){
		List<Record> list = Db.find("SELECT * FROM sys_user");
		renderJson(list);
	}
	
	public String alipayapi(String WIDout_trade_no,String WIDsubject,
			String WIDtotal_fee,String WIDbody,String return_url) throws UnsupportedEncodingException{
		//查询支付宝相关信息
		SysUser u = this.getSessionAttr("abuser");
		
		SysConfig config = null;
		boolean flag = false;
		if(null!= u.getStr("city_id") && u.getStr("city_id").length() > 0){
			AbCityarea aca = AbCityarea.dao.findById(u.getStr("city_id"));
			if(null!=aca && null!=aca.getStr("user_id") && aca.getStr("user_id").length() > 0){
				config= SysConfig.dao.findFirst("select * from sys_config where c6='" + aca.getStr("user_id") + "'");
				if(null!=config){
					flag = true;
				}
			}
		}
		if(!flag){
			config = new SysConfig();
			config.set("c1", "2088511526488301");
			config.set("c2", "sl9w4o4of1h3hbg81ko20o8f7cjyd52b");
			config.set("c5", "banxiandianzi@163.com");
		}
		//支付类型
		String payment_type = "1";
		//必填，不能修改
		//卖家支付宝帐户
		//String seller_email = new String(this.getPara("WIDseller_email").getBytes("ISO-8859-1"),"UTF-8");
		String seller_email = config.getStr("c5");
		//必填

		//商户订单号
		String out_trade_no = new String(WIDout_trade_no.getBytes("utf-8"),"UTF-8");
		//商户网站订单系统中唯一订单号，必填

		//订单名称
		String subject = new String(WIDsubject.getBytes("utf-8"),"UTF-8");
		//必填
		//付款金额
		String total_fee = new String(WIDtotal_fee.getBytes("utf-8"),"UTF-8");
		//必填
		//订单描述

		String body = new String(WIDbody.getBytes("utf-8"),"UTF-8");
		//商品展示地址
		String show_url = "http://www.yijuhome.cn";
		//需以http://开头的完整路径，例如：http://www.商户网址.com/myorder.html
		//把请求参数打包成数组
		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", "create_direct_pay_by_user");
        sParaTemp.put("partner", config.getStr("c1"));
        sParaTemp.put("_input_charset", "utf-8");
		sParaTemp.put("payment_type", payment_type);
		sParaTemp.put("return_url", return_url);
		sParaTemp.put("seller_email", seller_email);
		sParaTemp.put("out_trade_no", out_trade_no);
		sParaTemp.put("subject", subject);
		sParaTemp.put("total_fee", total_fee);
		sParaTemp.put("body", body);
		sParaTemp.put("show_url", show_url);
		//建立请求
		String sHtmlText = AlipaySubmit.buildRequest(sParaTemp,"get","确认");
		//this.redirect(sHtmlText);
		return sHtmlText;
	}
	
	  /*查询用户会员卡*/
	public void listusercards(){
		SysUser abuser = this.getSessionAttr("abuser");
		if (abuser == null || abuser.get("id") == null) {
			redirect("/ab/login");
		} else {
			String userid = abuser.get("id");
			String type = this.getPara("type");
			String status = this.getPara("status");
			String number = this.getPara("number");
			String active = this.getPara("active");
			String name = this.getPara("name");
			StringBuffer sb = new StringBuffer(CommonSQL.getUserCards(userid));
			if(!StringUtil.isNull(status)){
				sb.append(" and status = '").append(status).append("'");
			}
			if(!StringUtil.isNull(type)){
				sb.append(" and type = '").append(type).append("'");
			}
			if(!StringUtil.isNull(number)){
				sb.append(" and card_number like '%").append(number).append("%'");
			}
			if(!StringUtil.isNull(active)){
				sb.append(" and active = '").append(active).append("'");
			}
			if(!StringUtil.isNull(name)){
				sb.append(" and name like '%").append(name).append("%'");
			}
			
			sb.append(" order by create_time desc");
			PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),
					14, sb.toString());
			CommonProcess.createImageFile(listpage.getList());
			this.setAttr("listpage", listpage);
			this.render("/ab/card/listusercards.html");
		}
	}
	
	public void activeusercard(){
		SysUser abuser = this.getSessionAttr("abuser");
		if (abuser == null || abuser.get("id") == null) {
			redirect("/ab/login");
		} else {
			String userid = abuser.get("id");
			String type = this.getPara("type");
			String number = this.getPara("number");
			String name = this.getPara("name");
			StringBuffer sb = new StringBuffer(CommonSQL.getUserCards(userid));
			sb.append(" and status = '1'");
			sb.append(" and active = '0'");
			if(!StringUtil.isNull(type)){
				sb.append(" and type = '").append(type).append("'");
			}
			if(!StringUtil.isNull(number)){
				sb.append(" and card_number like '%").append(number).append("%'");
			}
			if(!StringUtil.isNull(name)){
				sb.append(" and name like '%").append(name).append("%'");
			}
			
			sb.append(" order by create_time desc");
			PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),
					14, sb.toString());
			CommonProcess.createImageFile(listpage.getList());
			this.setAttr("listpage", listpage);
			this.render("/ab/card/listactivecards.html");
		}
	}
	
	public void active(){
		SysUser abuser = this.getSessionAttr("abuser");
		if (abuser == null || abuser.get("id") == null) {
			redirect("/ab/login");
		} else {
			//线上线下卡激活标识
			String flag = this.getPara("activeflag");
			//线上卡激活
			if("1".equals(flag)){
				String cardid = this.getPara("id");
				AbCard card = AbCard.dao.findById(cardid);
				AbCardType act = AbCardType.dao.findById(card.getStr(AbCard.TYPE_ID));
				card.set(AbCard.ACTIVE_TIME, DateUtil.getCurrentDate());
				card.set(AbCard.EXPIRE_TIME, DateUtil.getAddDateStr(act.getInt(AbCardType.VALID_DAYS)));
				card.set(AbCard.ACTIVE, "1");
				card.update();
				this.renderJson("0");
			}
			//线下卡激活
			else{
				String cardnumber = this.getPara("cardnumber");
				AbCard card = AbCard.dao.findFirst("select * from ab_card where status = '1' and purchase_status = '0' and sale_type = '2' and card_number = '"+cardnumber+"'");
				if(card == null){
					//卡号无效
					this.renderJson("1");
				}else{
					//更新卡信息
					String datestr =  DateUtil.getCurrentDate();
					AbCardType act = AbCardType.dao.findById(card.getStr(AbCard.TYPE_ID));
					card.set(AbCard.ACTIVE_TIME, datestr);
					card.set(AbCard.EXPIRE_TIME, DateUtil.getAddDateStr(act.getInt(AbCardType.VALID_DAYS)));
					card.set(AbCard.PURCHASE_STATUS, "1");
					card.set(AbCard.ACTIVE, "1");
					card.update();
					//插入购买会员卡记录					
					AbCardUser carduser = new AbCardUser();
					carduser.set(AbCardUser.ID, StringUtil.getUuid32());
					carduser.set(AbCardUser.USER_ID, abuser.get("id"));
					carduser.set(AbCardUser.CARD_ID, card.get(AbCard.ID));
					carduser.set(AbCardUser.P_WAY, "2");
					carduser.set(AbCardUser.P_DATETIME, datestr);
					carduser.save();
					this.renderJson("0");
				}
			}
		}
	}
	
	//会员卡消费记录
	public void listcardexpense(){
		SysUser abuser = this.getSessionAttr("abuser");
		if (abuser == null || abuser.get("id") == null) {
			redirect("/ab/login");
		} else {
			String userid = abuser.getStr(SysUser.ID);
			String cardtype = this.getPara("cardtype");
			String cardnumber = this.getPara("cardnumber");
			String number = this.getPara("number");
			StringBuffer sb = new StringBuffer(CommonSQL.findAllCardExpense());
			sb.append(" and card_id in (select card_id from ab_card_user where user_id = '");
			sb.append(userid).append("') ");
			if(!StringUtil.isNull(cardnumber)){
				sb.append(" and card_number like '%").append(cardnumber).append("%'");
			}
			if(!StringUtil.isNull(number)){
				sb.append(" and sn like '%").append(number).append("%'");
			}
			if(!StringUtil.isNull(cardtype)){
				sb.append(" and type = '").append(cardtype).append("'");
			}
			sb.append(" order by xdsj desc");
			
			PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),
					14, sb.toString());
			this.setAttr("listpage", listpage);
			this.render("/ab/card/listcardexpense.html");
		}
	}
}
