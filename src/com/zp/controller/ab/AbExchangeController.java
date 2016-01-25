package com.zp.controller.ab;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.zp.entity.AbIntegralExchange;
import com.zp.entity.AbIntegralGoods;
import com.zp.entity.AbIntegralScore;
import com.zp.entity.SysUser;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.tools.CommonProcess;
import com.zp.tools.CommonSQL;
import com.zp.tools.CommonStaticData;
import com.zp.tools.DateUtil;
import com.zp.tools.StringUtil;

public class AbExchangeController extends Controller {
	/*前台积分商品列表*/
	public void listgoodsexchange() {
		SysUser abuser = this.getSessionAttr("abuser");
		String sql = "select * from ab_integral_goods where status= 1 and endtime >='"
				+ DateUtil.getCurrentDate()
				+ "' and starttime <'"
				+ DateUtil.getCurrentDate() + "' order by score asc";
		List<Record> list = Db.find(sql);
		CommonProcess.createImageFile(list);
		this.setAttr("goods", list);
		this.setAttr("abuser", abuser);
		this.render("/ab/integral/listintegralexchange.html");
	}

	/*进入兑换页面前*/
	public void editexchange() {
		SysUser abuser = this.getSessionAttr("abuser");
		if (abuser == null || abuser.get("id") == null) {
			redirect("/ab/login");
		} else {
			String id = StringUtil.toStr(this.getPara("id"));
			AbIntegralGoods vo = new AbIntegralGoods();
			if (id.length() > 0) {
				vo = vo.dao.findById(id);
			}
			this.setAttr("vo", vo);
			this.setAttr("abuser", abuser);
			render("/ab/integral/addexchange.html");
		}
	}

	/*兑换商品*/
	public void addexchange() {
		AbIntegralExchange exc = this.getModel(AbIntegralExchange.class, "exc");
		String goodsId = exc.getStr(AbIntegralExchange.GOODS_ID);
		String userId = exc.getStr(AbIntegralExchange.USER_ID);
		SysUser user = SysUser.dao.findById(userId);
		AbIntegralGoods goods = AbIntegralGoods.dao.findById(goodsId);
		// 当前兑换数量
		int count = exc.getInt(AbIntegralExchange.COUNT);
		// 商品每人兑换限定数量
		int limit = goods.getInt(AbIntegralGoods.LIMIT);
		// 商户兑换积分
		int score = goods.getInt(AbIntegralGoods.SCORE);
		// 用户当前积分
		int current = StringUtil.isNull(String.valueOf(user
				.getInt(SysUser.JIFEN))) ? 0 : user.getInt(SysUser.JIFEN);
		// 验证是否超过限制
		if (isExceedCount(goodsId, userId, count, limit)) {
			AbIntegralGoods vo = new AbIntegralGoods();
			this.setAttr("vo", vo.dao.findById(goodsId));
			this.setAttr("abuser", this.getSessionAttr("abuser"));
			this.setAttr("limitMsg", "累计兑换数量超过限定,请重新输入！");
			render("/ab/integral/addexchange.html");
		}
		// 验证积分是否足够
		else if (count * score > current) {
			AbIntegralGoods vo = new AbIntegralGoods();
			this.setAttr("vo", vo.dao.findById(goodsId));
			this.setAttr("abuser", this.getSessionAttr("abuser"));
			this.setAttr("limitMsg", "积分不够,请重新输入！");
			render("/ab/integral/addexchange.html");
		} else {
			// 更新用户积分
			int cal = current - count * score;
			user.set(SysUser.JIFEN, cal);
			user.update();
			// 更新积分商品数量
			int num = goods.getInt(AbIntegralGoods.COUNT) - count;
			goods.set(AbIntegralGoods.COUNT, num);
			goods.update();
			String dateStr = DateUtil.getCurrentDate();
			// 保存兑换信息
			//如果支付类型为null，设置类型为4，无需支付
			if(StringUtil.isNull(exc.getStr(AbIntegralExchange.PAY_TYPE))){
				exc.set(AbIntegralExchange.PAY_TYPE, "4");
			}
			String excId = StringUtil.getRandString32();
			exc.set("id", excId);
			exc.set("datetime", dateStr);
			exc.save();
			//保存积分变动记录
			AbIntegralScore ail = new AbIntegralScore();
			ail.set(AbIntegralScore.ID, StringUtil.getRandString32());
			ail.set(AbIntegralScore.TYPE, CommonStaticData.SCORE_TYPE7);
			ail.set(AbIntegralScore.VALUE, 0 - count * score);
			ail.set(AbIntegralScore.DATETIME, dateStr);
			ail.set(AbIntegralScore.USER_ID, user.getStr(SysUser.ID));
			ail.set(AbIntegralScore.FID, excId);
			ail.save();
			redirect("/ab/exc/listgoodsexchange");
		}
	}

	public boolean isExceedCount(String goodsId, String userId, int cnt,
			int limit) {
		String sql = "select sum(count) cnt from ab_integral_exchange where goods_id = '"
				+ goodsId + "' and user_id = '" + userId + "' group by user_id, goods_id";
		List<Record> records = Db.find(sql);
		if(records != null && records.size()>0){
			cnt += records.get(0).getBigDecimal("cnt").intValue();
		}
		if (cnt > limit)
			return true;
		return false;
	}
	
	/*积分变化列表*/
	public void listscore(){
		SysUser abuser = this.getSessionAttr("abuser");
		if (abuser == null || abuser.get("id") == null) {
			redirect("/ab/login");
		} else {
			String userId = abuser.getStr(SysUser.ID);
			SysUser user = SysUser.dao.findById(userId);
			// 查找当前用户积分变化记录
			List<Record> listscore = Db.find(CommonSQL.getScoreChangeByUserId(userId, "1"));
			CommonProcess.createImageFile(listscore);
			// 查找当前用户已消耗的记录
			int usedscore = 0;
			String sql = "select sum(value) val from ab_integral_score a where a.value < 0 and a.user_id = '"+userId+"' group by user_id ";
			List<Record> records = Db.find(sql);
			
			if(records != null && records.size()>0){
				usedscore = 0 - records.get(0).getBigDecimal("val").intValue();
			}
//			this.keepPara();
			this.setAttr("listscore", listscore);
			this.setAttr("usedscore", usedscore);
			this.setAttr("score", user.getInt(SysUser.JIFEN));
			this.render("/ab/user/listscore.html");
		}
	}
	
	public void listpartscore(){
		SysUser abuser = this.getSessionAttr("abuser");
		String userId = abuser.getStr(SysUser.ID);
		String data = this.getPara("data");
		System.out.println(data);
		List<Record> records = new ArrayList<Record>();
		//1 全部记录， 2正积分 3负积分
		if("1".equals(data)){
			// 查找当前用户积分变化记录
			records = Db.find(CommonSQL.getScoreChangeByUserId(userId, "1"));
		}else if("2".equals(data)){
			records = Db.find(CommonSQL.getScoreChangeByUserId(userId, "2"));
		}else{
			records = Db.find(CommonSQL.getScoreChangeByUserId(userId, "3"));
		}
		this.renderJson(getScoreStr(records));
	}
	
	/*用户中心积分兑换记录列表*/
	public void listexchange() {
		String sql = "select * from (select aie.*, aig.title,aig.image,aig.img_url,aig.score,su.mc, su.mobile from ab_integral_exchange aie left join ab_integral_goods aig on aie.goods_id = aig.id left join sys_user su on aie.user_id = su.id) t where 1=1 ";
		sql += " order by datetime desc";

		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),
				14, sql);
		CommonProcess.createImageFile(listpage.getList());
		this.setAttr("listpage", listpage);
		this.render("/ab/user/listexchange.html");
	}

	private String getScoreStr(List<Record> records) {
		StringBuffer sb = new StringBuffer();
		String imagepath = JFinal.me().getContextPath();
		sb.append("<div id=\"J_pointDetail\"><ul class=\"item-list\" id=\"J_item-list\">");
        for(Record record : records){
        	int val = record.getInt(AbIntegralScore.VALUE);
        	String dt = record.getStr(AbIntegralScore.DATETIME);
        	String type = CommonStaticData.getScoreTypeStr(record.getStr(AbIntegralScore.TYPE));
        	sb.append("<li class=\"item clearfix\"><div class=\"why\">");
        	sb.append("<a class=\"img\" href=\"#\" target=\"_blank\">");
        	if(CommonStaticData.SCORE_TYPE7.equals(record.getStr(AbIntegralScore.TYPE))){
        		sb.append("<img src=\"").append(imagepath).append("/upload/").append(record.getStr("img_url"));
        	}else{
        		sb.append("<img src=\"").append(imagepath).append("/res/css/ab/images/caomei.gif");
        	}
        	sb.append("\" width=\"60\" height=\"60\">");
        	sb.append("</a><a class=\"title\" target=\"_blank\" href=\"#\">");
        	sb.append(record.getStr("title")).append("</a><span class=\"order-number\">");
        	if(!StringUtil.isNull(record.getStr("sn"))){
        		sb.append("编号：").append(record.getStr("sn"));
        	}
        	sb.append("</span></div>");
        	sb.append("<div class=\"what\"><span class=\"1  plus \">");
        	if(val>0){
        		sb.append("+");
        	}
        	sb.append(val).append("</span></div><div class=\"when\">");
        	sb.append(dt).append("</div><div class=\"notes\">").append(type);
        	sb.append("</div></li></ul></div>");
        }
		return sb.toString();
	}
}
