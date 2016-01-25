package com.zp.controller.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.zp.entity.AbCard;
import com.zp.entity.AbCardType;
import com.zp.entity.SysUser;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.tools.CommonProcess;
import com.zp.tools.CommonSQL;
import com.zp.tools.CommonStaticData;
import com.zp.tools.DateUtil;
import com.zp.tools.StringUtil;
@SuppressWarnings("static-access")
public class AbCardController extends Controller {
	/*查询卡类别*/
	public void listcardtype(){
		String title = this.getPara("title");
		String type = this.getPara("type");
		String status = this.getPara("status");
		
		StringBuffer sb = new StringBuffer("select * from ab_card_type where 1=1 ");
		if(!StringUtil.isNull(title)){
			sb.append(" and title like '%").append(title).append("%'");
		}
		if(!StringUtil.isNull(status)){
			sb.append(" and status = '").append(status).append("'");
		}
		if(!StringUtil.isNull(type)){
			sb.append(" and type = '").append(type).append("'");
		}
		sb.append(" order by create_time desc");
		
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),
				14, sb.toString());
		CommonProcess.createImageFile(listpage.getList());
		
		this.setAttr("listpage", listpage);
		render("/admin/card/listcardtype.html");
	}
	
	/*卡类别预编辑*/
	public void editcardtype(){
		boolean isAdd = true;
		String id = StringUtil.toStr(this.getPara("id"));
		AbCardType vo = new AbCardType();
		if (id.length() > 0) {
			vo = vo.dao.findById(id);
			isAdd = false;
		}
		this.setAttr("vo", vo);
		this.setAttr("isAdd", isAdd);
		render("/admin/card/editcardtype.html");
	}
	
	/*卡类型保存*/
	public void savecardtype(){
		String img_url = this.getPara("cardtype_image");
		AbCardType cardtype = this.getModel(AbCardType.class, "cardtype");

		String str_oldtitleimg = null;
		if (img_url != null) {
			str_oldtitleimg = cardtype.getStr("img_url");
			FileInputStream file = null;
			try {
				file = new FileInputStream(PathKit.getWebRootPath()
						+ "\\upload\\" + img_url);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			cardtype.set("img_url", img_url);
			cardtype.set("image", file);
		}

		if (cardtype != null && cardtype.getStr("id") != null) {
			cardtype.update();
		} else {
			cardtype.set(AbCardType.ID, StringUtil.getUuid32());
			cardtype.set(AbCardType.CREATE_TIME, DateUtil.getCurrentDate());
			cardtype.save();
		}

		// 删除老图片
		File file = null;
		if (str_oldtitleimg != null) {
			file = new File(PathKit.getWebRootPath() + "\\upload\\"
					+ str_oldtitleimg);
			if (file.exists())
				file.delete();
		}
		redirect("/admin/card/listcardtype");
	}
	
	/*查询会员卡*/
	public void listcard(){
		String title = this.getPara("title");
		String type = this.getPara("type");
		String status = this.getPara("status");
		String number = this.getPara("number");
		String active = this.getPara("active");
		String purchase = this.getPara("purchase");
		String name = this.getPara("name");
		String saletype = this.getPara("saletype");
		StringBuffer sb = new StringBuffer("select * from ( ");
		sb.append("select ac.*, act.name, act.image, act.img_url, act.money, act.title, act.type, act.price, act.valid_days ");
		sb.append(" from ab_card ac left join ab_card_type act on ac.type_id = act.id ) t where 1=1 ");
		if(!StringUtil.isNull(title)){
			sb.append(" and title like '%").append(title).append("%'");
		}
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
		if(!StringUtil.isNull(purchase)){
			sb.append(" and purchase_status = '").append(purchase).append("'");
		}
		if(!StringUtil.isNull(name)){
			sb.append(" and name = like '%").append(name).append("%'");
		}
		if(!StringUtil.isNull(saletype)){
			sb.append(" and sale_type = '").append(saletype).append("'");
		}
		sb.append(" order by create_time desc");
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),
				14, sb.toString());
		CommonProcess.createImageFile(listpage.getList());
		
		this.setAttr("listpage", listpage);
		render("/admin/card/listcard.html");
	}
	
	/*卡类别预编辑*/
	public void editcard(){
		boolean isAdd = true;
		String id = StringUtil.toStr(this.getPara("id"));
		AbCard vo = new AbCard();
		List<AbCardType> cardtypes = AbCardType.dao.find("select * from ab_card_type where status = '1' ");
		if (id.length() > 0) {
			vo = vo.dao.findById(id);
			isAdd = false;
		}
		this.setAttr("cardtypes", cardtypes);
		this.setAttr("vo", vo);
		this.setAttr("isAdd", isAdd);
		render("/admin/card/editcard.html");
	}
	
	/*会员卡保存*/
	public void savecard(){
		AbCard card = this.getModel(AbCard.class, "card");
		if (card != null && card.getStr("id") != null) {
//			AbCard c = AbCard.dao.findById(card.getStr(AbCard.ID));
//			AbCardType act = AbCardType.dao.findById(c.getStr(AbCard.TYPE_ID));
//			if("1".equals(card.getStr(AbCard.ACTIVE))){
//				card.set(AbCard.ACTIVE_TIME, DateUtil.getCurrentDate());
//				card.set(AbCard.EXPIRE_TIME, DateUtil.getAddDateStr(act.getInt(AbCardType.VALID_DAYS)));
//				card.set(AbCard.PURCHASE_STATUS, "2");
//			}
			card.update();
		} else {
			AbCardType act = AbCardType.dao.findById(card.getStr(AbCard.TYPE_ID));
			String datestr = DateUtil.getCurrentDate();
			int cnt = Integer.valueOf(this.getPara("cnt"));
			for(int i = 0;i<cnt;i++){
				card.set(AbCard.ID, StringUtil.getUuid32());
				card.set(AbCard.CARD_NUMBER, CommonProcess.getCardNumber());
				card.set(AbCard.CREATE_TIME, datestr);
				card.set(AbCard.RMONEY, act.getBigDecimal(AbCardType.MONEY));
				//后台创建卡都是线下卡
				card.set(AbCard.SALE_TYPE, CommonStaticData.CARD_SALETYPE_OFFLINE);
				card.save();
			}
		}
		redirect("/admin/card/listcard");
	}
	
	/*用户会员卡查询*/
	public void listallusercard(){
		SysUser aduser = this.getSessionAttr("user");
		if (aduser == null || aduser.get("id") == null) {
			redirect("/25console");
		} else {
			String mc = this.getPara("mc");
			String mobile = this.getPara("mobile");
			String type = this.getPara("type");
			String status = this.getPara("status");
			String number = this.getPara("number");
			String active = this.getPara("active");
			String purchase = this.getPara("purchase");
			String name = this.getPara("name");
			String saletype = this.getPara("saletype");
			StringBuffer sb = new StringBuffer("select * from ( ");
			sb.append("select su.mc, su.mobile,auc.p_way,auc.p_datetime, ac.*, act.name, act.image, act.img_url, act.money, act.title, act.type, act.price, act.valid_days ");
			//增加区域
			sb.append(" from (select * from ab_card_user where user_id in (select id from sys_user where area_id in (select id from ab_cityarea where user_id = '"+aduser.getStr(SysUser.ID)+"'))) auc ");
			sb.append(" left join ab_card ac on auc.card_id = ac.id ");
			sb.append(" left join sys_user su on auc.user_id = su.id ");
			sb.append(" left join ab_card_type act on ac.type_id = act.id ) t where 1=1 ");
			if(!StringUtil.isNull(mc)){
				sb.append(" and mc like '%").append(mc).append("%'");
			}
			if(!StringUtil.isNull(mobile)){
				sb.append(" and mobile like '%").append(mobile).append("%'");
			}
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
			if(!StringUtil.isNull(purchase)){
				sb.append(" and purchase_status = '").append(purchase).append("'");
			}
			if(!StringUtil.isNull(name)){
				sb.append(" and name like '%").append(name).append("%'");
			}
			if(!StringUtil.isNull(saletype)){
				sb.append(" and sale_type = '").append(saletype).append("'");
			}
			sb.append(" order by create_time desc");
			PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),
					14, sb.toString());
			CommonProcess.createImageFile(listpage.getList());
			
			this.setAttr("listpage", listpage);
			render("/admin/card/listallusercard.html");
		}
	}
	
	public void listcardexpense(){
		SysUser aduser = this.getSessionAttr("user");
		if (aduser == null || aduser.get("id") == null) {
			redirect("/25console");
		} else {
			String cardtype = this.getPara("cardtype");
			String cardnumber = this.getPara("cardnumber");
			String number = this.getPara("number");
			StringBuffer sb = new StringBuffer(CommonSQL.findAllCardExpense());
			sb.append(" and user_id in (select id from sys_user where area_id in (select id from ab_cityarea where user_id = '"+aduser.getStr(SysUser.ID)+"')) ");
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
			render("/admin/card/listcardexpense.html");
		}
	}
	
	public void listaddexpense(){
		SysUser aduser = this.getSessionAttr("user");
		if (aduser == null || aduser.get("id") == null) {
			redirect("/25console");
		} else {
			String cardtype = this.getPara("cardtype");
			String cardnumber = this.getPara("cardnumber");
			StringBuffer sb = new StringBuffer(CommonSQL.findAddExpense());
			sb.append(" and user_id in (select id from sys_user where area_id in (select id from ab_cityarea where user_id = '"+aduser.getStr(SysUser.ID)+"')) ");
			if(!StringUtil.isNull(cardnumber)){
				sb.append(" and card_number like '%").append(cardnumber).append("%'");
			}
			if(!StringUtil.isNull(cardtype)){
				sb.append(" and type = '").append(cardtype).append("'");
			}
			sb.append(" order by create_time");
			PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),
					14, sb.toString());
			this.setAttr("listpage", listpage);
			render("/admin/card/listaddexpense.html");
		}
	}
}
