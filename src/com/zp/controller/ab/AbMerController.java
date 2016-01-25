package com.zp.controller.ab;

import java.io.File;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.zp.entity.AbCityarea;
import com.zp.entity.AbMerchant;
import com.zp.entity.AbMerchantComment;
import com.zp.entity.AbMerchantImg;
import com.zp.entity.AbMerchantLogimg;
import com.zp.entity.AbMerchantLogistics;
import com.zp.entity.AbMerchantLogline;
import com.zp.entity.AbMerchantLogtousu;
import com.zp.entity.AbMerchantPrint;
import com.zp.entity.AbMerchantProduct;
import com.zp.entity.AbMerchantYysj;
import com.zp.entity.AbOrder;
import com.zp.entity.AbOrderItem;
import com.zp.entity.AbProductImg;
import com.zp.entity.AbSubject;
import com.zp.entity.SysUser;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.interceptor.AccessAbInterceptor;
import com.zp.tools.DateUtil;
import com.zp.tools.PrintUtils;
import com.zp.tools.StringUtil;

public class AbMerController extends Controller {
	// ////////////////////////////////
	// 物流企业信息管理
	// ////////////////////////////////

	@Before(AccessAbInterceptor.class)
	public void edit1002company() {
		edit1002();
		render("/ab/mer/edit100201.html");
	}

	@Before(AccessAbInterceptor.class)
	public void edit1002scene() {
		edit1002();
		this.setAttr("title", "公司场景");
		this.setAttr("subtitle", "场景");
		this.setAttr("imgdesc", "scene");
		this.setAttr("save_action", "save1002scene");
		this.setAttr("edit_action", "edit1002scene");
		render("/ab/mer/edit100202.html");
	}

	@Before(AccessAbInterceptor.class)
	public void edit1002honor() {
		edit1002();
		this.setAttr("title", "公司荣誉");
		this.setAttr("subtitle", "荣誉");
		this.setAttr("imgdesc", "honor");
		this.setAttr("save_action", "save1002honor");
		this.setAttr("edit_action", "edit1002honor");
		render("/ab/mer/edit100202.html");
	}

	@Before(AccessAbInterceptor.class)
	public void edit1002faith() {
		edit1002();
		this.setAttr("title", "诚信档案");
		this.setAttr("subtitle", "档案");
		this.setAttr("imgdesc", "faith");
		this.setAttr("save_action", "save1002faith");
		this.setAttr("edit_action", "edit1002faith");
		render("/ab/mer/edit100202.html");
	}

	@Before(AccessAbInterceptor.class)
	public void edit1002tousu() {
		edit1002();
		this.setAttr("title", "投诉建议");
		this.setAttr("subtitle", "投诉建议");
		this.setAttr("read_action", "tousuread");
		this.setAttr("clear_action", "tousuclear");
		this.setAttr("delete_action", "tousudelete");
		this.setAttr("edit_action", "edit1002tousu");
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),
				14, "select * from ab_merchant_logtousu where lid='"
						+ getLogisticsID() + "'");
		this.setAttr("listpage", listpage);
		render("/ab/mer/edit100203.html");
	}

	@Before(AccessAbInterceptor.class)
	public void edit1002line() {
		edit1002();
		this.setAttr("title", "物流线路");
		this.setAttr("subtitle", "物流线路");
		this.setAttr("save_action", "linesave");
		this.setAttr("clear_action", "lineclear");
		this.setAttr("delete_action", "linedelete");
		this.setAttr("modify_action", "edit1002lineone");
		this.setAttr("edit_action", "edit1002line");
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),
				14, "select * from ab_merchant_logline where lid='"
						+ getLogisticsID() + "'");
		this.setAttr("listpage", listpage);
		render("/ab/mer/edit100204.html");
	}

	@Before(AccessAbInterceptor.class)
	public void edit1002lineone() {
		edit1002();
		this.setAttr("title", "物流线路");
		this.setAttr("subtitle", "物流线路");
		this.setAttr("save_action", "linesave");
		this.setAttr("modify_action", "edit1002lineone");
		this.setAttr("edit_action", "edit1002line");
		String id = this.getPara("id");
		if (id != null && id.length() > 0) {
			this.setAttr("line", AbMerchantLogline.dao.findById(id));
		}
		render("/ab/mer/edit100205.html");
	}

	/**
	 * 编辑页面都必须信息
	 */
	private void edit1002() {
		// 根据当前登录人信息获取商家信息
		// start();
		AbCityarea city = this.getSessionAttr("currcity");
		this.setAttr("currcity", city);
		SysUser abuser = this.getSessionAttr("abuser");
		AbMerchant vo = new AbMerchant();
		// 根据人员信息查询商家信息
		if (abuser != null && abuser.get("mid") != null) {
			vo = AbMerchant.dao.findById(abuser.get("mid"));
		}
		AbMerchantLogistics lo = new AbMerchantLogistics();
		if (vo != null && vo.get("id") != null) {
			lo = AbMerchantLogistics.dao.findByMid(vo.get("id"));
		}
		lo = lo == null ? new AbMerchantLogistics() : lo;
		List<AbMerchantImg> img = AbMerchantImg.dao.findByMidShow(abuser.get("mid"));

		this.setAttr("lo", lo);
		this.setAttr("vo", vo);
		this.setAttr("list_img", img);

	}

	// //////////////////////////////////////////////
	@Before(AccessAbInterceptor.class)
	public void save1002company() {
		boolean flag = false;
		try {
			
			// 获取上传文件名列表
			String[] imgurl = this.getParaValues("imgurl");
			String[] imgtype = this.getParaValues("imgtype");

			AbMerchant mer = this.getModel(AbMerchant.class, "tb");
			AbMerchant oldmer = AbMerchant.dao.findById(mer.get("id"));
			String str_oldtitleimg = "";

			if (mer.get("img_url") != null
					&& StringUtil.toStr(mer.get("img_url")).length() > 0) {
				str_oldtitleimg = oldmer.get("img_url");
			}

			if (mer != null && mer.getStr("id") != null) {
				mer.update();

				SysUser u = this.getSessionAttr("abuser");
				u.set("mc", mer.get("mc"));
				u.set("yzbm", mer.get("yzbm"));
				u.update();
				this.setSessionAttr("abuser", u);
			} else {
				mer.set("id", StringUtil.getRandString32());
				mer.set("create_time", DateUtil.getCurrentDate());
				mer.save();
			}
			// 上传多个图片，对数据进行保存
			if (imgurl != null && imgurl.length > 0) {
				SysUser abuser = this.getSessionAttr("abuser");
				for (int i = 0; i < imgurl.length; i++) {
					AbMerchantImg img = new AbMerchantImg();
					img.set("id", StringUtil.getRandString32());
					img.set("type_name",
							imgtype[i] == null ? null : imgtype[i]
									.toLowerCase());
					img.set("lager", imgurl[i]);
					img.set("mid", mer.getStr("id"));
					img.set("scrid", abuser.get("id"));
					img.set("scrmc", abuser.get("name"));
					img.set("scsj", DateUtil.getCurrentDate());
					img.save();
				}
			}

			// 删除老图片
			if (str_oldtitleimg != null) {
				File file = new File(PathKit.getWebRootPath() + "\\upload\\"
						+ str_oldtitleimg);
				if (file.exists())
					file.delete();
			}
			
			
			// 获取主营路线
			String[] mainlines = this.getParaValues("mainline");
			// 获取运输方式
			String[] transtypes = this.getParaValues("transtype");
			// 获取放款方式
			String[] loan_types = this.getParaValues("loan_type");

			AbMerchantLogistics logi = this.getModel(AbMerchantLogistics.class,
					"lo");

			if (logi != null) {
				logi.set("mainline", StringUtil.join(mainlines, ";", null, ""));
				logi.set("transtype",
						StringUtil.join(transtypes, ";", null, ""));
				logi.set("loan_type",
						StringUtil.join(loan_types, ";", null, ""));
			}

			// AbMerchant vo = AbMerchant.dao.findById(logi.get("mid"));
			// if(vo!=null) logi.set("city_name", vo.get("city_name"));

			if (logi != null && logi.getStr("id") != null) {
				logi.update();
				flag = true;
			} else {
				logi.set("id", StringUtil.getRandString32());
				logi.set("createtime", DateUtil.getCurrentDate());
				logi.save();
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(flag);
	}

	@Before(AccessAbInterceptor.class)
	public void save1002scene() {
		save1002("scene");
		renderJson(true);
	}

	@Before(AccessAbInterceptor.class)
	public void save1002honor() {
		save1002("honor");
		renderJson(true);
	}

	@Before(AccessAbInterceptor.class)
	public void save1002faith() {
		save1002("faith");
		renderJson(true);
	}

	private void save1002(String action) {
		String loid = getPara("lo_id");
		System.out.println("save1002image loid is " + loid);
		saveMerImage(loid, getPara("imgurl01"), getPara("imgtype01"), "1",
				action);
		saveMerImage(loid, getPara("imgurl02"), getPara("imgtype02"), "2",
				action);
		saveMerImage(loid, getPara("imgurl03"), getPara("imgtype03"), "3",
				action);
		saveMerImage(loid, getPara("imgurl04"), getPara("imgtype04"), "4",
				action);
		saveMerImage(loid, getPara("imgurl05"), getPara("imgtype05"), "5",
				action);
		saveMerImage(loid, getPara("imgurl06"), getPara("imgtype06"), "6",
				action);
		saveMerImage(loid, getPara("imgurl07"), getPara("imgtype07"), "7",
				action);
		saveMerImage(loid, getPara("imgurl08"), getPara("imgtype08"), "8",
				action);
	}

	/**
	 * 上传图片，对数据进行保存
	 * 
	 * @param lid
	 * @param imgname
	 */
	private void saveMerImage(String lid, String imgurl, String imgtype,
			String num, String desc) {
		if (lid == null || lid.length() < 1)
			return;
		if (imgurl == null || imgurl.length() < 1)
			return;
		SysUser abuser = this.getSessionAttr("abuser");
		AbMerchantLogimg img = AbMerchantLogimg.dao.findByInfo(lid, num, desc);
		boolean bimg = true;
		if (img == null) {
			img = new AbMerchantLogimg();
			bimg = false;
			img.set("id", StringUtil.getRandString32());
			img.set("lid", lid);
			img.set("seq_num", num);
			img.set("description", desc);
		} else {
			String str_oldimg = img.get("lager");
			// 删除老图片
			if (str_oldimg != null && !str_oldimg.equals(imgurl)) {
				File file = new File(PathKit.getWebRootPath() + "\\upload\\"
						+ str_oldimg);
				if (file.exists())
					file.delete();
			}
		}
		img.set("type_name", imgtype == null ? null : imgtype.toLowerCase());
		img.set("lager", imgurl);
		img.set("scrid", abuser.get("id"));
		img.set("scrmc", abuser.get("name"));
		img.set("scsj", DateUtil.getCurrentDate());
		if (bimg)
			img.update();
		else
			img.save();
	}

	// ////////////////
	// 投诉建议
	@Before(AccessAbInterceptor.class)
	public void tousuread() {
		AbMerchantLogtousu.dao.readById(this.getPara("id"));
		renderJson(true);
	}

	@Before(AccessAbInterceptor.class)
	public void tousureads() {
		String[] ids = this.getParaValues("id");
		AbMerchantLogtousu.dao.readByIds(ids);
		renderJson(true);
	}

	@Before(AccessAbInterceptor.class)
	public void tousudelete() {
		String id = this.getPara("id");
		if (id != null && id.length() > 0) {
			AbMerchantLogtousu.dao.deleteById(id);
			renderJson(true);
			return;
		}
		renderJson(false);
	}

	@Before(AccessAbInterceptor.class)
	public void tousudeletes() {
		String[] ids = this.getParaValues("id");
		AbMerchantLogtousu.dao.deleteByIds(ids);
		renderJson(true);
	}

	@Before(AccessAbInterceptor.class)
	public void tousuclear() {
		AbMerchantLogtousu.dao.deleteByLID(getLogisticsID());
	}

	private String getLogisticsID() {
		// 根据当前登录人信息获取商家信息
		SysUser abuser = this.getSessionAttr("abuser");
		// 根据人员信息查询商家信息
		if (abuser != null && abuser.get("mid") != null) {
			AbMerchantLogistics lo = AbMerchantLogistics.dao.findByMid(abuser
					.get("mid"));
			if (lo != null)
				return lo.get("id");
		}
		return null;
	}

	// ////////////////
	// 物流线路
	@Before(AccessAbInterceptor.class)
	public void linedelete() {
		String id = this.getPara("id");
		if (id != null && id.length() > 0) {
			AbMerchantLogline.dao.deleteById(id);
			renderJson(true);
			return;
		}
		renderJson(false);
	}

	@Before(AccessAbInterceptor.class)
	public void linedeletes() {
		String[] ids = this.getParaValues("id");
		AbMerchantLogline.dao.deleteByIds(ids);
		renderJson(true);
	}

	@Before(AccessAbInterceptor.class)
	public void lineclear() {
		boolean flag = AbMerchantLogline.dao.deleteByLID(getLogisticsID());
		renderJson(flag);
	}

	@Before(AccessAbInterceptor.class)
	public void linesave() {
		boolean flag = false;
		AbMerchantLogline line = this.getModel(AbMerchantLogline.class, "line");
		if (line != null && line.getStr("id") != null) {
			line.update();
			flag = true;
		} else {
			line.set("id", StringUtil.getRandString32());
			line.set("lid", getLogisticsID());
			line.save();
			flag = true;
		}
		renderJson(flag);
	}

	// ////////////////////////////////
	// end 物流企业信息管理
	// ////////////////////////////////
	@Before(AccessAbInterceptor.class)
	public void editmerinfo() {
		// 根据当前登录人信息获取商家信息
		SysUser abuser = this.getSessionAttr("abuser");
		AbMerchant vo = new AbMerchant();
		// 根据人员信息查询商家信息
		if (abuser != null && abuser.get("mid") != null) {
			vo = AbMerchant.dao.findById(abuser.get("mid"));
		}

		List<AbMerchantImg> list_img = AbMerchantImg.dao.find(
				"select * from ab_merchant_img where mid=? order by seq_num",
				vo.getStr("id"));
		this.setAttr("list_img", list_img);
		this.setAttr("vo", vo);
		render("/ab/mer/editmerinfo.html");
	}

	/**
	 * 日常营业信息编辑
	 */
	@Before(AccessAbInterceptor.class)
	public void editmerrcyy() {
		// 根据当前登录人信息获取商家信息
		SysUser abuser = this.getSessionAttr("abuser");
		AbMerchant vo = new AbMerchant();
		// 根据人员信息查询商家信息
		if (abuser != null && abuser.get("mid") != null) {
			vo = AbMerchant.dao.findById(abuser.get("mid"));
		}

		this.setAttr("vo", vo);
		render("/ab/mer/editmerrcyy.html");
	}

	/**
	 * 保存营业信息
	 */
	@Before(AccessAbInterceptor.class)
	public void savemerrcyy() {
		boolean flag = false;
		AbMerchant mer = this.getModel(AbMerchant.class, "tb");
		mer.update();
		flag = true;
		renderJson(flag);
	}

	@Before(AccessAbInterceptor.class)
	public void listproduct() {
		AbCityarea city = this.getSessionAttr("currcity");
		this.setAttr("currcity", city);
		// 根据当前登录人信息获取商家信息
		SysUser abuser = this.getSessionAttr("abuser");
		AbMerchant merchant = AbMerchant.dao.findFirst(
				"select id,mc from ab_merchant where id=?",
				abuser.getStr("mid"));
		List<AbMerchantProduct> listproduct = AbMerchantProduct.dao
				.find("select * from ab_merchant_product where mid=? order by seq_num",
						merchant.getStr("id"));
		this.setAttr("listproduct", listproduct);
		this.setAttr("mid", merchant.getStr("id"));
		this.render("/ab/mer/listproduct.html");
	}

	@Before(AccessAbInterceptor.class)
	public void yuLan() {
		SysUser user = this.getSessionAttr("abuser");
		String id = StringUtil.toStr(this.getPara("id"));
		Record t = Db.findById("ab_merchant_product", id);
		if (user != null && user.get("id") != null) {
			String sql = "select IFNULL(SUM(quantity),0) from ab_order_item where itemid=? and orderid in(select id from ab_order where xdrid=? and mid=? and ddzt='0')";
			t.set("procnt",
					Db.queryBigDecimal(sql, t.get("id"), user.get("id"), id)
							.longValue());
		} else {
			t.set("procnt", Long.parseLong("0"));
		}
		this.setAttr("t", t);
		this.render("/ab/mer/yuLan.html");
	}

	@Before(AccessAbInterceptor.class)
	public void listpl() {
		// 根据当前登录人信息获取商家信息
		SysUser abuser = this.getSessionAttr("abuser");
		AbMerchant merchant = AbMerchant.dao.findFirst(
				"select id,mc from ab_merchant where user_id=?",
				abuser.getStr("id"));
		// 获取平均得分
		Record r = Db
				.findFirst(
						"SELECT ROUND(AVG(rate),1) rate,ROUND(AVG(grade),1) grade FROM ab_merchant_comment WHERE mid=?",
						merchant.getStr("id"));
		List<AbMerchantComment> listcomm = AbMerchantComment.dao
				.find("select * from ab_merchant_comment where mid=? order by createtime desc",
						merchant.getStr("id"));
		this.setAttr("r", r);
		this.setAttr("listcomm", listcomm);
		this.setAttr("mid", merchant.getInt("id"));
		this.render("/ab/mer/listpl.html");
	}

	@Before(AccessAbInterceptor.class)
	public void listorder() {
		SysUser abuser = this.getSessionAttr("abuser");
		List<AbOrder> listorder = AbOrder.dao.find(
				"select * from ab_order where mid=? order by createtime desc",
				abuser.getStr("mid"));
		this.setAttr("listorder", listorder);
		this.render("/ab/mer/listorder.html");
	}

	@Before(AccessAbInterceptor.class)
	public void listitem() {
		int orderid = this.getParaToInt(0);
		List<AbOrderItem> listitem = AbOrderItem.dao
				.find("select * from ab_order_item where orderid=? order by createtime desc",
						orderid);
		this.setAttr("listitem", listitem);
		this.render("/ab/mer/listitem.html");
	}

	/**
	 * 充值记录查询
	 */
	@Before(AccessAbInterceptor.class)
	public void listczjl() {
		// 根据当前登录人信息获取商家信息
		SysUser abuser = this.getSessionAttr("abuser");
		SysUser u = SysUser.dao.findById(abuser.get("id"));
		String sql = "select * from log_user_deposit where userid='"
				+ abuser.getStr("id") + "' order by createtime desc";
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),
				14, sql);
		this.setAttr("listpage", listpage);
		this.setAttr("zhye", u.getBigDecimal("zhye").doubleValue());
		this.render("/ab/mer/listczjl.html");
	}

	@Before(AccessAbInterceptor.class)
	public void editmerzssz() {
		SysUser user = this.getSessionAttr("abuser");
		AbMerchant m = AbMerchant.dao.findById(user.get("mid"));
		this.setAttr("vo", m);
		render("/ab/mer/editmerzssz.html");
	}

	@Before(AccessAbInterceptor.class)
	public void addproduct() {
		String mid = this.getPara("mid");

		AbMerchant m = AbMerchant.dao.findById(mid);
		;
		AbMerchantProduct vo = new AbMerchantProduct();
		vo.set("mid", mid);
		List<AbSubject> list_subject = AbSubject.dao
				.find("select * from ab_subject where p_id=? and id IN(SELECT subject_id FROM ab_cityarea_subject WHERE city_id='"
						+ m.getStr("city_id") + "')", m.getStr("subject_id"));
		this.setAttr("vo", vo);
		this.setAttr("m", m);
		this.setAttr("list_subject", list_subject);
		render("/ab/mer/addproduct.html");
	}

	@Before(AccessAbInterceptor.class)
	public void editproduct() {
		String id = this.getPara("id");
		String mid = this.getPara("mid");

		AbMerchant m = null;
		AbMerchantProduct vo = new AbMerchantProduct();
		if (StringUtil.toStr(id).length() > 0) {
			vo = AbMerchantProduct.dao.findById(id);
			m = AbMerchant.dao.findById(vo.get("mid"));
		} else {
			m = AbMerchant.dao.findById(mid);
			vo.set("mid", mid);
			vo.set("mname", m.get("mc"));
			vo.set("subject_id", m.get("subject_id"));
			vo.set("subject_name", m.get("subject_name"));
			vo.set("is_type", m.get("is_type"));
			vo.set("salenum", m.get("0"));
		}

		List<AbProductImg> list_img = AbProductImg.dao.find(
				"select * from ab_product_img where f_id=? order by seq_num",
				vo.getStr("id"));
		List<AbSubject> list_subject = AbSubject.dao
				.find("select * from ab_subject where p_id=? and id IN(SELECT subject_id FROM ab_cityarea_subject WHERE city_id='"
						+ m.getStr("city_id") + "')", m.getStr("subject_id"));
		this.setAttr("vo", vo);
		this.setAttr("m", m);
		this.setAttr("list_img", list_img);
		this.setAttr("list_subject", list_subject);
		render("/ab/mer/editproduct.html");
	}

	@Before(AccessAbInterceptor.class)
	public void savemerinfo() {
		boolean flag = false;
		try {
			// 获取上传文件名列表
			String[] imgurl = this.getParaValues("imgurl");
			String[] imgtype = this.getParaValues("imgtype");

			AbMerchant mer = this.getModel(AbMerchant.class, "tb");
			AbMerchant oldmer = AbMerchant.dao.findById(mer.get("id"));
			String str_oldtitleimg = "";

			if (mer.get("img_url") != null
					&& StringUtil.toStr(mer.get("img_url")).length() > 0) {
				str_oldtitleimg = oldmer.get("img_url");
			}

			if (mer != null && mer.getStr("id") != null) {
				mer.update();

				SysUser u = this.getSessionAttr("abuser");
				u.set("mc", mer.get("mc"));
				u.set("yzbm", mer.get("yzbm"));
				u.update();
				this.setSessionAttr("abuser", u);
			} else {
				mer.set("id", StringUtil.getRandString32());
				mer.set("createtime", DateUtil.getCurrentDate());
				mer.save();
			}
			// 上传多个图片，对数据进行保存
			if (imgurl != null && imgurl.length > 0) {
				SysUser abuser = this.getSessionAttr("abuser");
				for (int i = 0; i < imgurl.length; i++) {
					AbMerchantImg img = new AbMerchantImg();
					img.set("id", StringUtil.getRandString32());
					img.set("type_name",
							imgtype[i] == null ? null : imgtype[i]
									.toLowerCase());
					img.set("lager", imgurl[i]);
					img.set("mid", mer.getStr("id"));
					img.set("scrid", abuser.get("id"));
					img.set("scrmc", abuser.get("name"));
					img.set("scsj", DateUtil.getCurrentDate());
					img.save();
				}
			}

			// 删除老图片
			if (str_oldtitleimg != null) {
				File file = new File(PathKit.getWebRootPath() + "\\upload\\"
						+ str_oldtitleimg);
				if (file.exists())
					file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		flag = true;
		renderJson(flag);
	}

	@Before(AccessAbInterceptor.class)
	public void saveproduct() {

		// 获取上传文件名列表
		String[] imgurl = this.getParaValues("imgurl");
		String[] imgtype = this.getParaValues("imgtype");

		AbMerchantProduct product = this.getModel(AbMerchantProduct.class,
				"product");
		String img_url = product.getStr("img_url");
		// 如果上传了新图片，则将新图片进行保存，同时保存临时的老图片
		String str_oldtitleimg = null;
		if (img_url != null) {
			if (product != null && product.getStr("id") != null) {
				// 如果是更新，则存储老图片
				AbMerchantProduct p = AbMerchantProduct.dao.findById(product
						.getStr("id"));
				str_oldtitleimg = p.getStr("img_url");
			}
			product.set("img_url", img_url);
			product.set("thumbnail_url", StringUtil.getThumbnail(img_url));
			// 对文件进行缩略图生成
		}

		if (product != null && product.getStr("id") != null) {
			product.update();
		} else {
			// 查询商家信息
			AbMerchant m = AbMerchant.dao.findById(product.getStr("mid"));
			product.set("mname", m.get("mc"));
			product.set("city_id", m.get("city_id"));
			product.set("city_name", m.get("city_name"));
			product.set("area_id", m.get("area_id"));
			product.set("area_name", m.get("area_name"));
			product.set("subject_id", m.get("subject_id"));
			product.set("subject_name", m.get("subject_name"));
			product.set("is_type", m.get("is_type"));
			product.set("salenum", "0");
			product.set("id", StringUtil.getRandString32());
			product.set("createtime", DateUtil.getCurrentDate());
			product.save();
		}
		SysUser abuser = this.getSessionAttr("abuser");
		// 上传多个图片，对数据进行保存
		if (imgurl != null && imgurl.length > 0) {
			for (int i = 0; i < imgurl.length; i++) {
				AbProductImg img = new AbProductImg();
				img.set("id", StringUtil.getRandString32());
				img.set("type_name",
						imgtype[i] == null ? null : imgtype[i].toLowerCase());
				img.set("lager", imgurl[i]);
				img.set("thumbnail", StringUtil.getThumbnail(imgurl[i]));
				img.set("f_id", product.getStr("id"));
				img.set("scrid", abuser.get("id"));
				img.set("scrmc", abuser.get("mc"));
				img.set("scsj", DateUtil.getCurrentDate());
				img.save();
			}
		}

		// 删除老图片
		File file = null;
		if (str_oldtitleimg != null) {
			file = new File(PathKit.getWebRootPath() + "\\upload\\"
					+ str_oldtitleimg);
			if (file.exists())
				file.delete();
			// 删除缩略图
			file = new File(PathKit.getWebRootPath() + "\\upload\\"
					+ StringUtil.getThumbnail(str_oldtitleimg));
			if (file.exists())
				file.delete();
		}

		redirect("/ab/mer/listproduct");
	}

	@Before(AccessAbInterceptor.class)
	public void delproduct() {
		boolean flag = false;
		String id = this.getPara("id");
		File f = null;
		// 删除产品或者服务、删除产品或服务下的图片数据
		AbMerchantProduct p = AbMerchantProduct.dao.findById(id);

		// 删除首页图及缩略图
		String img_url = p.getStr("img_url");
		String img_url_t = p.getStr("thumbnail_url");

		// 删除附件数据
		List<AbProductImg> list_img = AbProductImg.dao.find(
				"select * from ab_product_img where f_id=? order by seq_num",
				id);

		for (int i = 0; i < list_img.size(); i++) {
			AbProductImg.dao.deleteById(list_img.get(i).getStr("id"));
		}

		p.deleteById(id);

		// 删除首页
		f = new File(PathKit.getWebRootPath() + "\\upload\\" + img_url);
		if (f.exists()) {
			f.delete();
		}
		f = new File(PathKit.getWebRootPath() + "\\upload\\" + img_url_t);
		if (f.exists()) {
			f.delete();
		}
		// 删除附件
		for (int i = 0; i < list_img.size(); i++) {
			f = new File(PathKit.getWebRootPath() + "\\upload\\"
					+ list_img.get(i).getStr("lager"));
			if (f.exists()) {
				f.delete();
			}

			f = new File(PathKit.getWebRootPath() + "\\upload\\"
					+ list_img.get(i).getStr("thumbnail"));
			if (f.exists()) {
				f.delete();
			}
		}
		flag = true;
		renderJson(flag);
	}

	/**
	 * 商品上架
	 */
	@Before(AccessAbInterceptor.class)
	public void updatemerprosj() {
		boolean flag = false;
		String id = this.getPara("id");
		AbMerchantProduct.dao.findById(id).set("sfsj", "1").update();
		flag = true;
		renderJson(flag);
	}

	/**
	 * 商品下架
	 */
	@Before(AccessAbInterceptor.class)
	public void updatemerproxj() {
		boolean flag = false;
		String id = this.getPara("id");
		AbMerchantProduct.dao.findById(id).set("sfsj", "0").update();
		flag = true;
		renderJson(flag);
	}

	/**
	 * 营业时间管理
	 */
	@Before(AccessAbInterceptor.class)
	public void list_yysj() {
		// 根据当前登录人信息获取商家信息
		SysUser abuser = this.getSessionAttr("abuser");
		String sql = "select * from ab_merchant_yysj where mid='"
				+ abuser.getStr("mid") + "' order by xq, sbsj desc";
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),
				14, sql);
		this.setAttr("listpage", listpage);
		this.render("/ab/mer/list_yysj.html");
	}

	/**
	 * 营业时间编辑
	 */
	@Before(AccessAbInterceptor.class)
	public void edit_yysj() {
		String id = this.getPara("id");
		SysUser abuser = this.getSessionAttr("abuser");
		AbMerchantYysj vo = null;
		if (StringUtil.toStr(id).length() > 0) {
			vo = AbMerchantYysj.dao.findById(id);
		} else {
			vo = new AbMerchantYysj();
			vo.set("mid", abuser.get("mid"));
			vo.set("mname", abuser.get("mname"));
			vo.set("xq", 1);
		}
		this.setAttr("vo", vo);
		render("/ab/mer/edit_yysj.html");
	}

	/**
	 * 营业时间录入保存
	 */
	@Before(AccessAbInterceptor.class)
	public void save_yysj() {
		boolean flag = false;
		AbMerchantYysj yysj = this.getModel(AbMerchantYysj.class, "tb");
		if (yysj != null && yysj.getStr("id") != null) {
			yysj.update();
		} else {
			yysj.set("id", StringUtil.getRandString32());
			yysj.save();
		}
		flag = true;
		renderJson(flag);
	}

	/**
	 * 营业时间录入保存
	 */
	@Before(AccessAbInterceptor.class)
	public void del_yysj() {
		boolean flag = false;
		String id = this.getPara("id");
		AbMerchantYysj.dao.deleteById(id);
		flag = true;
		renderJson(flag);
	}

	/**
	 * 保存收藏的商家信息
	 */
	@Before(AccessAbInterceptor.class)
	public void save_sc() {
		boolean flag = false;
		String lx = this.getPara("lx");
		String fkid = this.getPara("fkid");
		SysUser u = this.getSessionAttr("abuser");
		String fkname = "";
		if ("0".equals(lx)) {
			// 商家信息
			AbMerchant m = AbMerchant.dao.findById(fkid);
			fkname = m.getStr("mc");
		} else {
			AbMerchantProduct p = AbMerchantProduct.dao.findById(fkid);
			fkname = p.getStr("mc");
		}

		Db.update("delete from sys_user_store where userid='" + u.getStr("id")
				+ "' and fkid = '" + fkid + "'");
		Db.update(
				"insert into sys_user_store(id,userid,lx,fkid,fkname)values(?,?,?,?,?)",
				StringUtil.getRandString32(), u.get("id"), lx, fkid, fkname);
		flag = true;
		renderJson(flag);
	}

	public void saverz() {
		boolean flag = false;
		SysUser user = this.getModel(SysUser.class, "tb");
		user.set("tjsj", DateUtil.getCurrentDate());
		user.set("sfrzzt", "1");
		user.update();
		flag = true;
		renderJson(flag);
	}

	@Before(AccessAbInterceptor.class)
	public void listprint() {
		SysUser abuser = this.getSessionAttr("abuser");
		List<AbMerchantPrint> listprint = AbMerchantPrint.dao.find(
				"select * from ab_merchant_print where mid = ?",
				abuser.getStr("mid"));
		this.setAttr("listprint", listprint);
		this.render("/ab/mer/listprint.html");
	}

	@Before(AccessAbInterceptor.class)
	public void initSaveUpdatePrint() {
		String id = StringUtil.toStr(this.getPara("id"));
		AbMerchantPrint vo;
		if (id.length() == 0) {
			vo = new AbMerchantPrint();
			// vo.set("id", StringUtil.getUuid32());
		} else {
			vo = AbMerchantPrint.dao.findById(id);
		}
		this.setAttr("vo", vo);
		this.render("/ab/mer/initsaveupdateprint.html");
	}

	@Before(AccessAbInterceptor.class)
	public void doSaveUpdatePrint() {
		try {
			String partner = StringUtil.toStr(this.getPara("partner"));
			String machinecode = StringUtil.toStr(this.getPara("machinecode"));
			String apikey = StringUtil.toStr(this.getPara("apikey"));
			String mkey = StringUtil.toStr(this.getPara("mkey"));
			String mobile = StringUtil.toStr(this.getPara("mobile"));
			String iswork = StringUtil.toStr(this.getPara("iswork"));
			String id = StringUtil.toStr(this.getPara("id"));
			String mid = StringUtil.toStr(this.getPara("mid"));
			SysUser abuser = this.getSessionAttr("abuser");
			if (mid.length() == 0) {
				mid = abuser.getStr("mid");
			}
			AbMerchantPrint amp;
			if (id.length() == 0) {
				amp = new AbMerchantPrint();
				amp.set("id", StringUtil.getUuid32());
				amp.set("partner", partner);
				amp.set("machinecode", machinecode);
				amp.set("apikey", apikey);
				amp.set("mkey", mkey);
				amp.set("mobile", mobile);
				amp.set("iswork", iswork);
				amp.set("mid", mid);
				amp.save();
			} else {
				amp = AbMerchantPrint.dao.findById(id);
				amp.set("partner", partner);
				amp.set("machinecode", machinecode);
				amp.set("apikey", apikey);
				amp.set("mkey", mkey);
				amp.set("mobile", mobile);
				amp.set("iswork", iswork);
				amp.set("mid", mid);
				amp.update();
			}
			this.renderJson(true);
		} catch (Exception e) {
			this.renderJson(false);
		}
	}

	@Before(AccessAbInterceptor.class)
	public void doDeletePrint() {
		String id = StringUtil.toStr(this.getPara("id"));
		if (id.length() == 0) {
			this.renderJson(false);
		} else {
			AbMerchantPrint vo = AbMerchantPrint.dao.findById(id);
			this.renderJson(vo.delete());
		}
	}

	@Before(AccessAbInterceptor.class)
	public void changeWorkSatus() {
		try {
			String id = StringUtil.toStr(this.getPara("id"));
			String isWork = StringUtil.toStr(this.getPara("isWork"));
			AbMerchantPrint vo = AbMerchantPrint.dao.findById(id);
			vo.set("iswork", isWork);
			vo.update();
			this.renderJson(true);
		} catch (Exception e) {
			this.renderJson(false);
		}
	}

	@Before(AccessAbInterceptor.class)
	public void initPrintMsg() {
		String id = StringUtil.toStr(this.getPara("id"));
		this.setAttr("id", id);
		this.render("/ab/mer/initprintmsg.html");
	}

	@Before(AccessAbInterceptor.class)
	public void doPrintMsg() {
		String id = StringUtil.toStr(this.getPara("id"));
		String msg = StringUtil.toStr(this.getPara("msg"));
		AbMerchantPrint vo = AbMerchantPrint.dao.findById(id);
		PrintUtils.apiKey = vo.getStr("apikey");
		PrintUtils.partner = vo.getStr("partner");
		PrintUtils.mKey = vo.getStr("mkey");
		PrintUtils.machine_code = vo.getStr("machinecode");
		if (PrintUtils.pmRequest()) {
			if (PrintUtils.sendRequest(msg)) {
				this.renderJson(true);
			} else {
				this.renderJson(false);
			}
		} else {
			this.renderJson(false);
		}
	}
}
