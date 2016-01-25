package com.zp.controller.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.zp.entity.AbIntegralExchange;
import com.zp.entity.AbIntegralGoods;
import com.zp.entity.SysUser;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.interceptor.AccessAdminInterceptor;
import com.zp.tools.CommonProcess;
import com.zp.tools.StringUtil;

public class AbIntegralController extends Controller {
	/**
	 * 显示所有积分兑换商品记录
	 */
	@Before(AccessAdminInterceptor.class)
	public void listgoods() {
		String title = StringUtil.toStr(this.getPara("title"));

		String sql = "select * from ab_integral_goods where 1=1 ";

		if (title.length() > 0) {
			sql += " and title like '%" + title + "%'";
		}

		sql += " order by starttime desc";

		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),
				14, sql);
		CommonProcess.createImageFile(listpage.getList());

		this.setAttr("listpage", listpage);
		this.setAttr("title", title);

		this.render("/admin/integral/listgoods.html");
	}

	public void editgoods() {
		String id = StringUtil.toStr(this.getPara("id"));
		AbIntegralGoods vo = new AbIntegralGoods();
		if (id.length() > 0) {
			vo = vo.dao.findById(id);
		}
		this.setAttr("vo", vo);
		render("/admin/integral/editgoods.html");
	}

	public void savegoods() {
		String img_url = this.getPara("goods_image");
		AbIntegralGoods goods = this.getModel(AbIntegralGoods.class, "goods");

		String str_oldtitleimg = null;
		if (img_url != null) {
			str_oldtitleimg = goods.getStr("img_url");
			FileInputStream file = null;
			try {
				file = new FileInputStream(PathKit.getWebRootPath()
						+ "\\upload\\" + img_url);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			goods.set("img_url", img_url);
			goods.set("image", file);
		}

		if (goods != null && goods.getStr("id") != null) {
			goods.update();
		} else {
			goods.set("id", StringUtil.getUuid32());
			goods.save();
		}

		// 删除老图片
		File file = null;
		if (str_oldtitleimg != null) {
			file = new File(PathKit.getWebRootPath() + "\\upload\\"
					+ str_oldtitleimg);
			if (file.exists())
				file.delete();
		}
		redirect("/admin/integral/listgoods");
	}

	/*
	 * 删除
	 */
	public void deletegoods() {
		String id = this.getPara("id");
		String imgurl = AbIntegralGoods.dao.findById(id).get(AbIntegralGoods.IMG_URL);

		// 删除附件
		File f = new File(PathKit.getWebRootPath() + "\\upload\\" + imgurl);
		if (f.exists()) {
			f.delete();
		}
		boolean flag = AbIntegralGoods.dao.deleteById(id);
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void listexchange() {
		SysUser aduser = this.getSessionAttr("user");
		if (aduser == null || aduser.get("id") == null) {
			redirect("/25console");
		} else {
			String adminId = aduser.getStr(SysUser.ID);
			String title = StringUtil.toStr(this.getPara("title"));
			String rectMobile = StringUtil.toStr(this.getPara("rect_mobile"));
			String rect = StringUtil.toStr(this.getPara("rect"));
			StringBuffer sb = new StringBuffer();
			sb.append("select * from (select aie.*, aig.title,aig.image,aig.img_url,aig.score,su.mc, su.mobile from ");
			sb.append("(select * from ab_integral_exchange where user_id in (select id from sys_user where area_id in (select id from ab_cityarea where user_id = '"+adminId+"'))) aie ");
			sb.append("left join ab_integral_goods aig on aie.goods_id = aig.id left join sys_user su on aie.user_id = su.id) t ");
			sb.append("where 1=1 ");
			
			if (title.length() > 0) {
				sb.append(" and t.title like '%" + title + "%'");
			}
			if (rectMobile.length() > 0) {
				sb.append(" and t.rect_mobile like '%" + rectMobile + "%'");
			}
			if (rect.length() > 0) {
				sb.append(" and t.rect like '%" + rect + "%'");
			}
			
			sb.append(" order by datetime desc");
			
			PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),
					14, sb.toString());
			CommonProcess.createImageFile(listpage.getList());
			
			this.setAttr("listpage", listpage);
			this.setAttr("title", title);
			
			this.render("/admin/integral/listexchange.html");
		}
	}
	
	public void editexchange() {
		String id = StringUtil.toStr(this.getPara("id"));
		AbIntegralExchange vo = new AbIntegralExchange();
		if (id.length() > 0) {
			vo = vo.dao.findById(id);
		}
		this.setAttr("vo", vo);
		render("/admin/integral/editexchange.html");
	}
	

	/*
	 * 修改积分兑换记录
	 */
	public void saveexchange() {
		AbIntegralExchange uexc = this.getModel(AbIntegralExchange.class, "exc");
		AbIntegralExchange vo = AbIntegralExchange.dao.findById(uexc.getStr(AbIntegralExchange.ID));
		vo.set(AbIntegralExchange.RECT, uexc.getStr(AbIntegralExchange.RECT));
		vo.set(AbIntegralExchange.RECT_MOBILE, uexc.getStr(AbIntegralExchange.RECT_MOBILE));
		vo.set(AbIntegralExchange.RECT_ADRS, uexc.getStr(AbIntegralExchange.RECT_ADRS));
		vo.set(AbIntegralExchange.SEND_NUMBER, uexc.getStr(AbIntegralExchange.SEND_NUMBER));
		vo.set(AbIntegralExchange.SEND_STATUS, uexc.getStr(AbIntegralExchange.SEND_STATUS));
		vo.set(AbIntegralExchange.SEND_TIME, uexc.getStr(AbIntegralExchange.SEND_TIME));
		vo.set(AbIntegralExchange.SEND_TYPE, uexc.getStr(AbIntegralExchange.SEND_TYPE));
		vo.update();
		redirect("/admin/integral/listexchange");
	}
	
	/*
	 * 删除积分兑换记录
	 */
	public void deleteexchange() {
		String id = this.getPara("id");
//		String imgurl = AbIntegralExchange.dao.findById(id).get(AbIntegralExchange.IMG_URL);

		// 删除附件
//		File f = new File(PathKit.getWebRootPath() + "\\upload\\" + imgurl);
//		if (f.exists()) {
//			f.delete();
//		}
		this.renderJson(AbIntegralExchange.dao.deleteById(id));
	}
	

}
