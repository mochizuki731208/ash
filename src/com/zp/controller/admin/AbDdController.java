package com.zp.controller.admin;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.zp.entity.AbMerchant;
import com.zp.entity.AbMerchantProduct;
import com.zp.entity.AbOrder;
import com.zp.entity.AbOrderItem;
import com.zp.entity.AbPolicy;
import com.zp.entity.AbProductImg;
import com.zp.entity.AbSjPosition;
import com.zp.entity.AbSubject;
import com.zp.entity.AbTcExpressOrder;
import com.zp.entity.SysUser;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.tools.CityAttachAdminUtil;
import com.zp.tools.DateUtil;
import com.zp.tools.StringUtil;

public class AbDdController extends Controller {

	public void listddsh() {
		SysUser u = (SysUser) this.getSessionAttr("user");
		String sn = StringUtil.toStr(this.getPara("sn"));
		String mname = StringUtil.toStr(this.getPara("mname"));
		String subjectid = StringUtil.toStr(this.getPara("subjectid"));
		String yt = StringUtil.toStr(this.getPara("yt"));
		// 获取该用户所在区域下的所有商户商品
		String city_id = "";
		if ("103".equals(u.get("role_id"))) {
			city_id = u.getStr("city_id");
		} else if ("104".equals(u.get("role_id"))) {// 普通员工
			city_id = Db.findFirst(
					"select id from ab_cityarea where user_id='"
							+ u.getStr("p_id") + "'").getStr("id");
		}

		String sql = "select * from ab_order where sfdb = '1' ";

		if (sn.length() > 0) {
			sql += " and sn like '%" + sn + "%'";
		}
		if (mname.length() > 0) {
			sql += " and mname like '%" + mname + "%'";
		}
		if (subjectid.length() > 0) {
			sql += " and subject_id = '" + subjectid + "'";
		}
		if (yt.length() > 0) {
			sql += " and yt = '" + yt + "'";
		}
		if (StringUtil.toStr(city_id).length() > 0) {
			sql += " and mid in(select id from ab_merchant where city_id='"
					+ city_id + "')";
		}

		sql += " and ddzt!='0' ";

		sql += " order by xdsj desc";

		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),
				14, sql);

		List<AbSubject> list_subject = AbSubject.dao
				.find("select * from ab_subject where p_id='ROOT'");
		this.setAttr("listpage", listpage);
		this.setAttr("list_subject", list_subject);
		this.setAttr("sn", sn);
		this.setAttr("yt", yt);
		this.setAttr("mname", mname);
		this.setAttr("subjectid", subjectid);
		this.render("/admin/dd/listddsh.html");
	}

	public void listddcx() {
		SysUser u = (SysUser) this.getSessionAttr("user");
		String sn = StringUtil.toStr(this.getPara("sn"));
		String istype = StringUtil.toStr(this.getPara("istype"));
		String yt = StringUtil.toStr(this.getPara("yt"));
		String mname = StringUtil.toStr(this.getPara("mname"));
		String subjectid = StringUtil.toStr(this.getPara("subjectid"));

		String sql = "select * from ab_order where id is not null ";

		// 获取该用户所在区域下的所有商户商品
		// if(u!=null&&"103".equals(u.get("role_id"))){
		// sql+=" and (city_id in(SELECT id FROM ab_cityarea WHERE
		// user_id='"+u.getStr("id")+"') or area_id in(SELECT id FROM
		// ab_cityarea WHERE user_id='"+u.getStr("id")+"'))";
		// }else if("104".equals(u.get("role_id"))){//普通员工
		// sql+=" and (city_id in(SELECT id FROM ab_cityarea WHERE
		// user_id='"+u.getStr("p_id")+"') or area_id in(SELECT id FROM
		// ab_cityarea WHERE user_id='"+u.getStr("p_id")+"'))";
		// }

		if (sn.length() > 0) {
			sql += " and sn like '%" + sn + "%'";
		}
		if (yt.length() > 0) {
			sql += " and yt = '" + yt + "'";
		}
		if (istype.length() > 0) {
			sql += " and is_type = '" + istype + "'";
		}
		if (mname.length() > 0) {
			sql += " and mname like '%" + mname + "%'";
		}
		if (subjectid.length() > 0) {
			sql += " and subject_id in (select id from ab_subject where p_id like'"
					+ subjectid + "%')";
		}
		if (!"admin".equals(u.getStr("id"))) {
			sql += " and xdrid in(select id from sys_user where 1=1";
		}
		if (u.getStr("id").length() > 0 && (!"104".equals(u.getStr("role_id")))) {
			sql += " and (city_id in ("
					+ CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + ")";
			sql += " or area_id in ("
					+ CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + "))";
		}
		if (u.getStr("role_id").equals("104")) {
			sql += " and subject_id in("
					+ CityAttachAdminUtil.getAuthSubjectIdsByUserId(u
							.getStr("id")) + ")";
		}
		sql += " and ddzt!='0' ";

		sql += " order by xdsj desc";

		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),
				14, sql);

		String sql_subject = "select * from ab_subject where 1 = 1";
		if ("104".equals(u.getStr("role_id"))) {
			sql_subject += " and id in("
					+ CityAttachAdminUtil.getAuthSubjectIdsByUserId(u
							.getStr("id")) + ")";
		} else {
			sql_subject += " and p_id='ROOT'";
		}
		List<AbSubject> list_subject = AbSubject.dao.find(sql_subject);
		this.setAttr("listpage", listpage);
		this.setAttr("list_subject", list_subject);
		this.setAttr("sn", sn);
		this.setAttr("yt", yt);
		this.setAttr("istype", istype);
		this.setAttr("mname", mname);
		this.setAttr("subjectid", subjectid);
		this.render("/admin/dd/listddcx.html");
	}

	public void editdd() {
		String id = this.getPara("id");
		AbMerchant vo = new AbMerchant();
		if (StringUtil.toStr(id).length() > 0) {
			vo = AbMerchant.dao.findById(id);
		}

		List<AbProductImg> list_img = AbProductImg.dao
				.find("select * from ab_merchant_img where f_id='" + id
						+ "' order by seq_num");
		List<AbSubject> list_subject = AbSubject.dao
				.find("select * from ab_subject where p_id='ROOT'");
		this.setAttr("vo", vo);
		this.setAttr("list_img", list_img);
		this.setAttr("list_subject", list_subject);
		render("/admin/merchant/editsj.html");
	}

	public void showdd() {
		String id = this.getPara("id");
		AbOrder vo = new AbOrder();
		if (StringUtil.toStr(id).length() > 0) {
			vo = AbOrder.dao.findById(id);
		}
		this.setAttr("vo", vo);
		String sn = vo.getStr("sn");
		if (sn != null && sn.startsWith("TC")) {
			AbTcExpressOrder tcOrder = AbTcExpressOrder.dao.findFirst(
					"select * from ab_tc_express_order where sn=?", sn);
			this.setAttr("to", tcOrder);
			// 接单人的信息
			String qdrid = vo.getStr("qdrid");
			if (!StringUtil.isNull(qdrid)) {
				SysUser user = SysUser.dao.findById(qdrid);
				this.setAttr("jdr", user);
			}
			render("/admin/dd/showtcdd.html");
			return;
		}
		List<AbOrderItem> list_item = AbOrderItem.dao
				.find("select * from ab_order_item where orderid='" + id
						+ "' order by seq_num");
		this.setAttr("list_item", list_item);
		render("/admin/dd/showdd.html");
	}
	
	
	public void to_updateprice() {
		String id = this.getPara("id");
		AbOrder vo = new AbOrder();
		this.setAttr("id", id);
		if (StringUtil.toStr(id).length() > 0) {
			vo = AbOrder.dao.findById(id);
		}
		this.setAttr("vo", vo);
		String sn = vo.getStr("sn");
		if (sn != null && sn.startsWith("TC")) {
			AbTcExpressOrder tcOrder = AbTcExpressOrder.dao.findFirst(
					"select * from ab_tc_express_order where sn=?", sn);
			this.setAttr("to", tcOrder);
			// 接单人的信息
			String qdrid = vo.getStr("qdrid");
			if (!StringUtil.isNull(qdrid)) {
				SysUser user = SysUser.dao.findById(qdrid);
				this.setAttr("jdr", user);
			}
			render("/admin/dd/addprice.html");
			
			return;
		}
		List<AbOrderItem> list_item = AbOrderItem.dao
				.find("select * from ab_order_item where orderid='" + id
						+ "' order by seq_num");
		this.setAttr("list_item", list_item);
		render("/admin/dd/addprice.html");
	}
	public void updateprice() {
		boolean flag = false;
		String id = this.getPara("id");
		String price=this.getPara("price");
		int a=Db.update("update ab_order set ddzje=? where id=?",price, id);
		System.out.println(a+"---"+id+"---"+price);
		
		String orderid=Db.queryStr("select sn from ab_order where id=?",id);
		String userid=Db.queryStr("select xdrid from ab_order where id=?",id);
		String roleid=Db.queryStr("select role_id from sys_user where id=?",userid);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str_date =df.format(new Date());
		Record record=new Record();
		record.set("id", StringUtil.getUuid32());
		record.set("user_id", userid);
		record.set("role_id", roleid);
		record.set("mes_title", "订单消息");
		record.set("mes_type", "0");
		record.set("isread", "0");
		record.set("text", "订单加价成功！加价为："+price+"元,订单号："+orderid);
		record.set("send_date", str_date);
		Db.save("message", record);
		
		flag = true;
		renderJson(flag);
	}
	
	public void to_updatebzj() {
		String id = this.getPara("id");
		AbOrder vo = new AbOrder();
		this.setAttr("id", id);
		if (StringUtil.toStr(id).length() > 0) {
			vo = AbOrder.dao.findById(id);
		}
		this.setAttr("vo", vo);
		String sn = vo.getStr("sn");
		if (sn != null && sn.startsWith("TC")) {
			AbTcExpressOrder tcOrder = AbTcExpressOrder.dao.findFirst(
					"select * from ab_tc_express_order where sn=?", sn);
			this.setAttr("to", tcOrder);
			// 接单人的信息
			String qdrid = vo.getStr("qdrid");
			if (!StringUtil.isNull(qdrid)) {
				SysUser user = SysUser.dao.findById(qdrid);
				this.setAttr("jdr", user);
			}
			render("/admin/dd/addbzj.html");
			
			return;
		}
		List<AbOrderItem> list_item = AbOrderItem.dao
				.find("select * from ab_order_item where orderid='" + id
						+ "' order by seq_num");
		this.setAttr("list_item", list_item);
		render("/admin/dd/addbzj.html");
	}
	public void updatebzj() {
		boolean flag = false;
		String id = this.getPara("id");
		String price=this.getPara("price");
		int a=Db.update("update ab_order set bzj=? where id=?",price, id);
		
		String orderid=Db.queryStr("select sn from ab_order where id=?",id);
		String userid=Db.queryStr("select xdrid from ab_order where id=?",id);
		String roleid=Db.queryStr("select role_id from sys_user where id=?",userid);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str_date =df.format(new Date());
		Record record=new Record();
		record.set("id", StringUtil.getUuid32());
		record.set("user_id", userid);
		record.set("role_id", roleid);
		record.set("mes_title", "订单消息");
		record.set("mes_type", "0");
		record.set("isread", "0");
		record.set("text", "保证金缴纳成功！缴纳金额："+price+"元，订单号："+orderid);
		record.set("send_date", str_date);
		Db.save("message", record);
		
		System.out.println(a+"---"+id+"---"+price);
		
		flag = true;
		
		renderJson(flag);
	}
	public void issued_policy() {
		SysUser user = this.getSessionAttr("abuser");
		if (user == null) { // 登录
			render("/ab/login.html");
			return;
		}
		if ("fromSave".equals(this.getPara())) {

		}
		render("/admin/dd/showpolicy.html");
	}

	public void policyDetail() {
		String id = this.getPara();
		AbPolicy policy = AbPolicy.dao.findById(id);
		this.setAttr("vo",policy);
		// showdd.html
		render("/admin/dd/policyDetail.html");
	}
	
	/**
	 * 下单
	 */
	public void issued_policy1() {
		// 订单ID
		String order_id = getPara(0);
		this.setAttr("order_id", order_id);
		String from = getPara(1);
		this.setAttr("from", (from == null || from.equals("")) ? "" : from);
		// 查询订单详细信息
		AbOrder vo = new AbOrder();
		if (StringUtil.toStr(order_id).length() > 0) {
			vo = AbOrder.dao.findById(order_id);
		}
		this.setAttr("vo", vo);
		String sn = vo.getStr("sn");
		if (sn != null && sn.startsWith("TC")) {
			AbTcExpressOrder tcOrder = AbTcExpressOrder.dao.findFirst(
					"select * from ab_tc_express_order where sn=?", sn);
			this.setAttr("to", tcOrder);
			// 接单人的信息
			String qdrid = vo.getStr("qdrid");
			if (!StringUtil.isNull(qdrid)) {
				SysUser user = SysUser.dao.findById(qdrid);
				this.setAttr("jdr", user);
			}
			// 查询保单信息
			AbPolicy policy = AbPolicy.dao.findFirst(
					"select * from ab_policy t where order_id=?", order_id);
			if (policy == null) {
				// 还没有下保单
				this.setAttr("policy", new AbPolicy());
				this.setAttr("is_policy", "0");
			} else {
				// 已经下保单
				this.setAttr("is_policy", "1");
				this.setAttr("policy", policy);
			}
			render("/admin/dd/showpolicy.html");
			return;
		}
	}

	// 保存保险单
	// public void save_policy() {
	// String from = getPara("from");
	// String order_id = getPara("order_id");
	// String goods_name = getPara("goods_name");
	// String transport_name = getPara("transport_name");
	// String policy_money = getPara("policy_money");
	// AbPolicy policy = new AbPolicy();
	// policy.set(AbPolicy.GOODS_NAME, goods_name);
	// policy.set(AbPolicy.ID, StringUtil.getUuid32());
	// policy.set(AbPolicy.OPT_DATE, DateUtil.format(new Date(), "yyyy-MM-dd
	// hh:mm:ss"));
	// policy.set(AbPolicy.ORDER_ID, order_id);
	// policy.set(AbPolicy.POLICY_MONEY, policy_money);
	// policy.set(AbPolicy.POLICY_STATUS, "01");// 未支付
	// policy.set(AbPolicy.TRANSPORT_NAME, transport_name);
	// if(policy.save()) {
	// if (from != null && from.equals("fromPolicy")) {
	// // 跳转到投保明细
	// renderJson("2");
	// } else {
	// renderJson("1");
	// }
	// }else {
	// renderJson("0");
	// }
	// }

	public void save_policy() {
		SysUser user = this.getSessionAttr("abuser");
		if (user == null) { // 登录
			render("/ab/login.html");
			return;
		}

		AbPolicy abPolicy = new AbPolicy();
		String id = StringUtil.getUuid32();
		abPolicy.set("id", id);

		abPolicy.set("insurant", getPara("abPolicy.insurant"));
		abPolicy.set("insurant_tel", getPara("abPolicy.insurant_tel"));
		abPolicy.set("type_insurant", getPara("abPolicy.type_insurant"));
		abPolicy.set("goods_p_type", getPara("abPolicy.goods_p_type"));
		abPolicy.set("goods_s_type", getPara("abPolicy.goods_s_type"));
		abPolicy.set("goods_name", getPara("abPolicy.goods_name"));
		abPolicy.set("packing", getPara("abPolicy.packing"));
		abPolicy.set("goods_num", getPara("abPolicy.goods_num"));
		abPolicy.set("order_sn", getPara("abPolicy.order_sn"));
		abPolicy.set("transport_type", getPara("abPolicy.transport_type"));
		abPolicy.set("transport_name", getPara("abPolicy.transport_name"));
		abPolicy.set("car_no", getPara("abPolicy.car_no"));
		abPolicy.set("start_site_province",
				getPara("abPolicy.start_site_province"));
		abPolicy.set("start_site_city", getPara("abPolicy.start_site_city"));
		abPolicy.set("start_site_country",
				getPara("abPolicy.start_site_country"));
		abPolicy.set("transfer_site_province",
				getPara("abPolicy.transfer_site_province"));
		abPolicy.set("transfer_site_city",
				getPara("abPolicy.transfer_site_city"));
		abPolicy.set("transfer_site_country",
				getPara("abPolicy.transfer_site_country"));
		abPolicy
				.set("end_site_province", getPara("abPolicy.end_site_province"));
		abPolicy.set("end_site_city", getPara("abPolicy.end_site_city"));
		abPolicy.set("end_site_country", getPara("abPolicy.end_site_country"));
		abPolicy.set("start_time", getPara("abPolicy.start_time"));
		abPolicy.set("zxtk", getPara("abPolicy.zxtk"));
		abPolicy.set("fjtk", getPara("abPolicy.fjtk"));
		abPolicy.set("policy_quota", getPara("abPolicy.policy_quota"));
		abPolicy.set("policy_cost", getPara("abPolicy.policy_cost"));
		abPolicy.set("policy_status", "01");
		abPolicy.set("opt_date", DateUtil.getCurrentDate());
		abPolicy.set("user_id", user.get("id"));
		try {
			if (abPolicy.save()) {
				this.renderText(id);
			} else {
				this.renderText("2");
			}
		} catch (Exception e) {
			this.renderText("2");
		}
	}

	public void update_policy() {
		String policy_id = this.getPara("policy_id");
		AbPolicy policy = AbPolicy.dao.findById(policy_id);
		policy.set("policy_status", "02");// 已支付
		if(policy.update()) {
			renderText("1");
		} else {
			renderText("2");
		}
	}
	
	public void showOrderBySn() {
		boolean isTC = false;
		String sn = this.getPara("sn");
		if (sn.startsWith("TC")) {// 同城物流订单
			isTC = true;
		}
		AbOrder vo = new AbOrder();
		AbTcExpressOrder aeo = new AbTcExpressOrder();
		List<AbOrderItem> list_item = new ArrayList<AbOrderItem>();
		List<AbSjPosition> asp = new ArrayList<AbSjPosition>();
		SysUser addressSj = null;
		AbMerchant mer = null;
		SysUser usersj = null;
		if (isTC) {
			vo = AbOrder.dao.findFirst("select * from ab_order where sn = ?",
					sn);
			aeo = AbTcExpressOrder.dao.findFirst(
					"select * from ab_tc_express_order where sn=?", sn);
			if (null != vo) {
				if (null != vo.getStr("qdrid")) {// 接单人地理位置
					asp = AbSjPosition.dao
							.find(
									"select * from ab_sj_position where sjid=? order by dqsj desc",
									vo.getStr("qdrid"));
					usersj = SysUser.dao.findById(vo.getStr("qdrid"));
					if (null == usersj) {
						usersj = new SysUser();
					}
				}
			}
		} else {
			if (StringUtil.toStr(sn).length() > 0) {
				vo = AbOrder.dao.findFirst(
						"select * from ab_order where sn = ?", sn);
				if (null != vo) {
					list_item = AbOrderItem.dao
							.find("select * from ab_order_item where orderid='"
									+ vo.getStr("id") + "' order by seq_num");
					if (null != vo.getStr("mid")
							&& vo.getStr("mid").length() > 0) {
						addressSj = SysUser.dao.findById(vo.getStr("mid"));
						mer = AbMerchant.dao.findById(vo.getStr("mid"));
					} else {
						addressSj = new SysUser();
						mer = new AbMerchant();
					}
					if (null != vo.getStr("qdrid")) {// 接单人地理位置
						asp = AbSjPosition.dao
								.find(
										"select * from ab_sj_position where sjid=? order by dqsj desc",
										vo.getStr("qdrid"));
						usersj = SysUser.dao.findById(vo.getStr("qdrid"));
						if (null == usersj) {
							usersj = new SysUser();
						}
					}
				}
			}
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("isTC", isTC);
		result.put("vo", vo);
		result.put("list_item", list_item);
		result.put("addressSj", addressSj);
		result.put("asp", asp);
		result.put("mer", mer);
		result.put("aeo", aeo);
		result.put("usersj", usersj);
		this.renderJson(result);
	}

	public void policyBySn() {
		boolean isTC = false;
		String sn = this.getPara("sn");
		if (sn.startsWith("TC")) {// 同城物流订单
			isTC = true;
		}
		AbOrder vo = new AbOrder();
		AbTcExpressOrder aeo = new AbTcExpressOrder();
		List<AbOrderItem> list_item = new ArrayList<AbOrderItem>();
		List<AbSjPosition> asp = new ArrayList<AbSjPosition>();
		SysUser addressSj = null;
		AbMerchant mer = null;
		SysUser usersj = null;
		AbPolicy ap = new AbPolicy();
		if (isTC) {
			vo = AbOrder.dao.findFirst("select * from ab_order where sn = ?",
					sn);
			aeo = AbTcExpressOrder.dao.findFirst(
					"select * from ab_tc_express_order where sn=?", sn);
			if (null != vo) {
				if (null != vo.getStr("qdrid")) {// 接单人地理位置
					asp = AbSjPosition.dao
							.find(
									"select * from ab_sj_position where sjid=? order by dqsj desc",
									vo.getStr("qdrid"));
					usersj = SysUser.dao.findById(vo.getStr("qdrid"));
					if (null == usersj) {
						usersj = new SysUser();
					}
				}
				List<AbPolicy> aps = AbPolicy.dao
						.find("select * from ab_policy where order_id='"
								+ vo.getStr("id") + "'");
				if (aps == null || aps.size() == 0) {
					ap = null;
				} else {
					ap = aps.get(0);
				}
			}
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("isTC", isTC);
		result.put("vo", vo);
		result.put("list_item", list_item);
		result.put("addressSj", addressSj);
		result.put("asp", asp);
		result.put("mer", mer);
		result.put("aeo", aeo);
		result.put("usersj", usersj);
		result.put("policy", ap);
		this.renderJson(result);
	}

	public void savedd() {
		String img_url = this.getPara("img_url");

		// 获取上传文件名列表
		String[] imgurl = this.getParaValues("imgurl");
		String[] imgtype = this.getParaValues("imgtype");

		AbMerchantProduct product = this.getModel(AbMerchantProduct.class,
				"product");

		// 如果上传了新图片，则将新图片进行保存，同时保存临时的老图片
		String str_oldtitleimg = null;
		if (img_url != null) {
			str_oldtitleimg = product.getStr("img_url");
			product.set("img_url", img_url);
			// 对文件进行缩略图生成
			product.set("img_url", img_url);
		}

		if (product != null && product.getStr("id") != null) {
			product.update();
		} else {
			product.set("id", StringUtil.getUuid32());
			product.set("createtime", DateUtil.getCurrentDate());
			product.save();
		}

		// 上传多个图片，对数据进行保存
		if (imgurl != null && imgurl.length > 0) {
			SysUser user = this.getSessionAttr("user");
			for (int i = 0; i < imgurl.length; i++) {
				AbProductImg img = new AbProductImg();
				img.set("type_name", imgtype[i]);
				img.set("lager", imgurl[i]);
				img.set("f_id", product.getStr("id"));
				img.set("scsj", DateUtil.getCurrentDate());
				img.set("scrid", user.get("id"));
				img.set("scrmc", user.get("mc"));
				img.set("id", StringUtil.getUuid32());
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
		}

		redirect("/admin/merchant/listproduct");

	}

	public void deldd() {
		boolean flag = false;
		String id = this.getPara("id");
		File f = null;
		// 删除产品或者服务、删除产品或服务下的图片数据
		AbMerchantProduct.dao.deleteById(id);
		List<AbProductImg> list_img = AbProductImg.dao
				.find("select * from ab_product_img where f_id='" + id
						+ "' order by seq_num");

		for (int i = 0; i < list_img.size(); i++) {
			AbProductImg.dao.deleteById(list_img.get(i).getStr("id"));
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

	public void sptg() {
		int id = this.getParaToInt("id");
		boolean flag = AbMerchant.dao.findById(id).set("check_status", "1")
				.update();
		this.renderJson(flag);
	}

	public void spbtg() {
		int id = this.getParaToInt("id");
		boolean flag = AbMerchant.dao.findById(id).set("check_status", "0")
				.update();
		this.renderJson(flag);
	}
	
	public void showmessage(){
		String id=this.getPara("id");
		String date=Db.queryStr("select send_date from message where id=?", id);
		String text=Db.queryStr("select text from message where id=?", id);
		String title=Db.queryStr("select mes_title from message where id=?", id);
		Db.update("update message set isread='1' where id=?", id);
		this.setAttr("date", date);
		this.setAttr("text", text);
		this.setAttr("title", title);
		render("/admin/dd/showmessage.html");
	}
}
