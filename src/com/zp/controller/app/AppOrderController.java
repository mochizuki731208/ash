package com.zp.controller.app;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.support.json.JSONUtils;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.zp.entity.AbCard;
import com.zp.entity.AbCardExpense;
import com.zp.entity.AbCityarea;
import com.zp.entity.AbFmcar;
import com.zp.entity.AbIntegralScore;
import com.zp.entity.AbMerchant;
import com.zp.entity.AbMerchantProduct;
import com.zp.entity.AbOrder;
import com.zp.entity.AbOrderItem;
import com.zp.entity.AbSjPosition;
import com.zp.entity.LogUserDeposit;
import com.zp.entity.SysConfig;
import com.zp.entity.SysCzTx;
import com.zp.entity.SysUser;
import com.zp.entity.SysYhkItem;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.interceptor.AccessAbInterceptor;
import com.zp.interceptor.AccessAdminInterceptor;
import com.zp.tools.CommonSQL;
import com.zp.tools.CommonStaticData;
import com.zp.tools.DateUtil;
import com.zp.tools.MapUtils;
import com.zp.tools.RandomUtil;
import com.zp.tools.SmsMessage;
import com.zp.tools.StringUtil;
import com.zp.tools.alipay.AlipaySubmit;

public class AppOrderController extends AbsAppController {

	/**
	 * 订单明细状态
	 */
	public void queryOrderStatus() {
		String uid = this.getPara("uid");
		String orderid = this.getPara("orderid");

		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			if (checkParam(orderid, uid)) {

				AbOrder vo = AbOrder.dao.findById(orderid);

				if (vo==null || vo.getStr(AbOrder.XDRID)==null || !uid.equals(vo.getStr(AbOrder.XDRID))) { //uid must match
					resultMap.put("result", -1);
					resultMap.put("msg", uid + " can't query the orderid "
							+ orderid + " status ,which does not belong to it");
				} else {

					resultMap.put("status", vo.getStr(AbOrder.DDZT));
					formatSuccessResponse(resultMap);
				}
			} else {// 参数校验不通过
				formatInvalidParamResponse(resultMap);

			}
		} catch (Exception e) {
			formatInvokeFailResponse(e, resultMap);
		}
		renderJson(resultMap);
	}

	/**
	 * track the position status of order
	 */
	public void trackOrder() {
		String uid = this.getPara("uid");
		String orderid = this.getPara("orderid");

		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			if (checkParam(orderid, uid)) {

				AbOrder vo = AbOrder.dao.findById(orderid);
				System.out.println(vo);
				if (vo==null || vo.getStr(AbOrder.XDRID)==null || !uid.equals(vo.getStr(AbOrder.XDRID))) { //uid must match
					resultMap.put("result", -1);
					resultMap.put("msg", uid + " can't query the orderid "
							+ orderid + " status ,which does not belong to it");
				} else {
					//resultMap.put("longitude", vo.getStr(AbOrder.SHDZJD));
					//resultMap.put("latitude", vo.getStr(AbOrder.SHDZWD));
					
					AbSjPosition sjPosition = AbSjPosition.dao.findFirst("select * from ab_sj_position where sjid=? order by dqsj desc limit 1",vo.getStr("qdrid"));
					if(sjPosition == null ){
						sjPosition = new AbSjPosition();
						resultMap.put("result", 101);
						resultMap.put("msg", "there has no position");
					}else{
						resultMap.put("longitude",sjPosition.getStr("jd"));
						resultMap.put("latitude",sjPosition.getStr("wd"));
						resultMap.put("dqsj",sjPosition.getStr("dqsj"));
						
						
						formatSuccessResponse(resultMap);
					}
				}
			} else {// 参数校验不通过
				formatInvalidParamResponse(resultMap);

			}
		} catch (Exception e) {
			formatInvokeFailResponse(e, resultMap);
		}
		renderJson(resultMap);
	}

	public void delorder() {
		boolean flag = false;
		String id = this.getPara("id");
		AbOrder.dao.deleteById(id);// 级联删除订单明细信息
		Db.update("delete from ab_order_item where orderid='" + id + "'");
		flag = true;
		renderJson(flag);
	}
	public void addMoney(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String id = StringUtil.toStr(this.getPara("sn"));
		String addMoney = StringUtil.toStr(this.getPara("addMoney"));
		String uid = StringUtil.toStr(this.getPara("uid"));
		try {
			if (checkParam(uid, id,addMoney)) {
				String sql = "UPDATE  ab_order SET ddzje=? where sn = '"
					+ id + "'";
				String sql_tc = "UPDATE  ab_tc_express_order SET total_price=? where sn = '"
					+ id + "'";
			Double ddzje = Double.parseDouble(addMoney);
			Db.update(sql, ddzje);
			Db.update(sql_tc, ddzje);
			formatSuccessResponse(resultMap);
			}else{
				formatInvalidParamResponse(resultMap);
			}}catch (Exception e) {
				formatErrorMsgResponse("错误", resultMap);
			}
			renderJson(resultMap);
		
		
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
		order.set("spzj", spzj).set("yhje", yhje).set("ptf", ptf)
				.set("ddzje", ddzje).update();
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
			order.set("spzj", spzj).set("yhje", yhje).set("ptf", ptf)
					.set("ddzje", ddzje).update();
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

			AbOrder.dao
					.findById(id)
					.set("yzbm", yzbm)
					.set("shdz", shdz)
					.set("shdzjd", shdzjd)
					.set("shdzwd", shdzwd)
					.set("lxr", lxr)
					.set("lxrdh", lxrdh)
					.set("xdsj", DateUtil.getCurrentDate())
					.set("lxrdh", lxrdh)
					.set("dzsfqr", "1")
					.set("dzqrip",
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
			if ("1".equals(m.get("sfzs"))) {
				AbOrder.dao.findById(id).set("ddzt", "2").update();
			} else {
				AbOrder.dao.findById(id).set("ddzt", "2").update();
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
		SysUser user = this.getSessionAttr("abuser");
		if (user != null && user.get("id") != null) {
			userid = user.get("id");
			this.setAttr("userid", userid);
		}

		List<Record> list_order = Db.find(
				"select * from ab_order where ddzt='0' and xdrid=?", userid);
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

		List<Record> list_m = Db
				.find("select distinct id,mc,business_status,uf_getmerworktime("
						+ day
						+ ",'"
						+ currtime
						+ "',id) iswork from ab_merchant where id in(select mid from ab_order where ddzt='0' and xdrid=?)",
						userid);

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

		this.setAttr("finalprice", finalprice);
		this.setAttr("deduction", deduction);
		this.setAttr("vo", vo);
		this.setAttr("list_m", list_m);
		this.setAttr("list_order", list_order);
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

		// boolean isSendCard = false;//是否有送餐卡
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
				BigDecimal sendmoney = order.getBigDecimal("ptf").add(
						BigDecimal.valueOf(Double.parseDouble(cjlfjf[i])));
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
				List<Record> ecards = Db.find(CommonSQL.findUserCard(userid,
						CommonStaticData.CARD_TYPE_EXPENSE));
				if (ecards != null && ecards.size() > 0) {
					for (Record r : ecards) {
						if (price.doubleValue() > 0) {
							AbCard card = AbCard.dao.findById(r.getStr("id"));
							AbCardExpense ace = new AbCardExpense();
							ace.set(AbCardExpense.ID, StringUtil.getUuid32());
							ace.set(AbCardExpense.ORDER_ID, orderids[i]);
							ace.set(AbCardExpense.CARD_ID, card.get("id"));
							ace.set(AbCardExpense.STATUS,
									CommonStaticData.CARD_EXPENSE_VALID);
							// 总价金额大于单张会员卡余额
							BigDecimal rmoney = r.getBigDecimal(AbCard.RMONEY);
							if (price.compareTo(r.getBigDecimal(AbCard.RMONEY)) == 1) {
								// 插入会员卡使用记录
								price = price.subtract(rmoney);
								ace.set(AbCardExpense.MONEY, rmoney);
								ace.save();
								// 将会员卡，扣除余额，置为无效
								card.set(AbCard.STATUS,
										CommonStaticData.CARD_STATUS_INVALID);
								card.set(AbCard.RMONEY, new BigDecimal(0));
								card.update();
							} else {
								// 插入会员卡使用记录
								ace.set(AbCardExpense.MONEY, price);
								ace.save();
								// 将会员卡，扣除余额
								card.set(AbCard.RMONEY,
										card.getBigDecimal(AbCard.RMONEY)
												.subtract(price));
								card.update();
								price = new BigDecimal(0);
							}
						}
					}
					finalprice = finalprice.add(price);
				}
				// 查找配送方式
				AbMerchant m = AbMerchant.dao.findById(order.get("mid"));
				order.set("lxr", lxr)
						.set("lxrdh", lxrdh)
						.set("shdz", shdz)
						.set("memo", memo)
						.set("yqsdsj", yqsdsj)
						.set("zffs", zffs)
						.set("cjlfjf", cjlfjf[i])
						.set("ddzje",
								spzjmoney.add(sendmoney).subtract(yhjemoney))
						.set("psfs", "1".equals(m.get("sfzs")) ? "0" : "1")
						.set("sfdb", sfdb)
						.set("dbbh", StringUtil.getRandString32())
						.set("dbsj", str_date).set("dbspzj", b_total_spzj)
						.set("dbptf", b_total_ptf)
						.set("dbcjlfjf", b_total_cjlfjf)
						.set("dbyhje", b_total_yhje)
						.set("dbddzje", b_total_ddzje).set("xdsj", str_date)
						.set("xdrid", user.get("id"))
						.set("xdrmc", user.get("mc")).set("ddzt", "1");
				if ("2".equals(zffs)) {
					order.set("ddzt", "0");// 如果是支付宝付款，此处状态变更由回调方法进行处理payback
				}
				order.update();
			}
		}

		// 增加消费积分
		user.set(SysUser.JIFEN,
				user.getInt(SysUser.JIFEN) + b_total_ddzje.intValue());
		user.update();
		// 生成认证码，并发送短信
		String randcode = RandomUtil.createRandom(6);

		if ("1".equals(zffs)) {
			if (finalprice.doubleValue() > 0) {
				user.set("zhye", user.getBigDecimal("zhye")
						.subtract(finalprice));
				user.update();
			}
			if (orderids != null && orderids.length > 0) {
				for (int i = 0; i < orderids.length; i++) {
					AbOrder order = AbOrder.dao.findById(orderids[i]);
					order.set("zfzt", "1").set("zfsj", str_date)
							.set("rzm", randcode).update();
				}
			}

			if (orderids != null && orderids.length > 0) {
				for (int i = 0; i < orderids.length; i++) {
					try {
						AbOrder order = AbOrder.dao.findById(orderids[i]);
						SmsMessage.SendMessage(
								user.getStr("loginid"),
								user.getStr("mc") + "您好，订单号："
										+ order.getStr("sn") + "下单成功，认证码："
										+ randcode
										+ "，请妥善保存你的认证码，凭认证码签收已购买的商品。");
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
			//b_total_ddzje = new BigDecimal("0.01");// 测试使用，上线必须注释掉
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
				alipayapi(tradeno, "eeee", b_total_ddzje.toString(), "在线购买",return_url);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("3".equals(zffs)) {
			// 货到付款
			if (orderids != null && orderids.length > 0) {
				for (int i = 0; i < orderids.length; i++) {
					AbOrder order = AbOrder.dao.findById(orderids[i]);
					order.set("zfzt", "0").set("rzm", randcode).update();
				}
			}
			if (orderids != null && orderids.length > 0) {
				for (int i = 0; i < orderids.length; i++) {
					try {
						AbOrder order = AbOrder.dao.findById(orderids[i]);
						SmsMessage.SendMessage(
								user.getStr("loginid"),
								user.getStr("mc") + "您好，订单号："
										+ order.getStr("sn") + "下单成功，认证码："
										+ randcode
										+ "，请妥善保存你的认证码，凭认证码签收已购买的商品。");
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
		String ddzje = this.getPara("total_ddzje");
		SysUser user = this.getSessionAttr("abuser");
		user = SysUser.dao.findById(user.get("id"));
		BigDecimal zhye = user.getBigDecimal("zhye");
		BigDecimal d_ddzje = BigDecimal.valueOf(Double.valueOf(ddzje));

		if (zhye.compareTo(d_ddzje) == -1) {
			renderJson(false);
		} else {
			renderJson(true);
		}
	}

	public void alipayapi(String WIDout_trade_no, String WIDsubject,
			String WIDtotal_fee, String WIDbody, String return_url)
			throws UnsupportedEncodingException {
		// 查询支付宝相关信息
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
		// 支付类型
		String payment_type = "1";
		// 必填，不能修改
		// 卖家支付宝帐户
		// String seller_email = new
		// String(this.getPara("WIDseller_email").getBytes("ISO-8859-1"),"UTF-8");
		String seller_email = config.getStr("c5");
		// 必填

		// 商户订单号
		String out_trade_no = new String(
				WIDout_trade_no.getBytes("ISO-8859-1"), "UTF-8");
		// 商户网站订单系统中唯一订单号，必填

		// 订单名称
		String subject = new String(WIDsubject.getBytes("ISO-8859-1"), "UTF-8");
		// 必填
		// 付款金额
		String total_fee = new String(WIDtotal_fee.getBytes("ISO-8859-1"),
				"UTF-8");
		// 必填
		// 订单描述

		String body = new String(WIDbody.getBytes("ISO-8859-1"), "UTF-8");
		// 商品展示地址
		String show_url = "http://www.yijuhome.cn";
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
		this.redirect(sHtmlText);
	}

	public void payBack() {
		String out_trade_no = StringUtil.toStr(this.getPara("out_trade_no"));
		String total_fee = StringUtil.toStr(this.getPara("total_fee"));
		String trade_status = StringUtil.toStr(this.getPara("trade_status"));
		List<SysCzTx> cztx_list = SysCzTx.dao
				.find("select * from sys_cz_tx where tradeno='" + out_trade_no
						+ "'");
		if ("TRADE_SUCCESS".equals(trade_status)
				&& ("2".equals(cztx_list.get(0).getStr("type")))) {// 付款
			for (SysCzTx cztx : cztx_list) {
				if (cztx.getInt("num") == 1) {
					continue;
				}
				cztx.set("result", 1);
				cztx.set("num", 1);
				cztx.update();
				AbOrder order = AbOrder.dao.findById(cztx.getStr("orderid"));
				order.set("ddzt", "1");// 订单状态
				order.set("zfzt", "1");// 支付状态
				order.set("zfsj", DateUtil.getCurrentDate());// 支付状态
				order.update();
			}
		} else if ("TRADE_SUCCESS".equals(trade_status)
				&& ("0".equals(cztx_list.get(0).getStr("type")))) {// 充值
			for (SysCzTx cztx : cztx_list) {
				if (cztx.getInt("num") == 1) {
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
				lud.set("czje", total_fee);
				lud.set("status", 1);
				lud.set("remark", "支付宝在线充值" + total_fee + "元");
				lud.save();
				user.set(
						"zhye",
						user.getBigDecimal("zhye").add(
								new BigDecimal(total_fee)));
				user.update();
			}
		} else {
			// 支付失败
		}
		this.render("/ab/dd/ddsuccess.html");
	}
	
	public void xOrder() {
		try{
			String uid = getPara("uid");
			SysUser.dao.deleteById(uid);
			}catch(Exception e){
				renderJson(false);
				return ;
			}
		renderJson(true);
	}
	public void initalipayapi() {
		String WIDtotal_fee = StringUtil.toStr(this.getPara("WIDtotal_fee"));
		String type = StringUtil.toStr(this.getPara("type"));// 0-充值 1-提现 2-付款
		String WIDout_trade_no = StringUtil.toStr(this
				.getPara("WIDout_trade_no"));
		SysUser u = this.getSessionAttr("abuser");
		String zffs = StringUtil.toStr(this.getPara("zzfs"));
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

			this.render("/ab/dd/ddsuccess.html");
		} else {// 线上支付
			String return_url = "http://www.yijuhome.cn/ab/order/payBack";
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
				alipayapi(WIDout_trade_no, WIDsubject, WIDtotal_fee, WIDbody,
						return_url);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
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
		SysUser user = SysUser.dao.findById(u.getStr("id"));
		user.set(
				"zhye",
				user.getBigDecimal("zhye").subtract(
						new BigDecimal(WIDtotal_fee)));// 现将金额扣除，后期提现失败，提现金额退换
		// user.update();
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
		// initTx
		// SysConfig config = SysConfig.dao.findById("06");
		// if("1".equals(config.getStr("c1"))){//自动处理
		// SysConfig zfbconfig=
		// SysConfig.dao.findFirst("select * from sys_config where c6='admin'");
		// String email = zfbconfig.getStr("c5");//付款方的支付宝账号
		// String service = "batch_trans_notify";//接口名称
		// String partner = zfbconfig.getStr("c1");//合作伙伴ID
		// String _input_charset = "utf-8";
		// String pay_date = "20150318";
		// String batch_no = "20150318003";
		// String batch_num = "1";
		// String account_name = "深圳市邦翔电子商务有限公司";
		// String batch_fee = "0.01";
		// String detail_data = "2015031803^GCCGNN@hotmail.com^耿叁^0.01^批量付款";
		// Map<String, String> sParaTemp = new HashMap<String, String>();
		// sParaTemp.put("email", email);
		// sParaTemp.put("service", service);
		// sParaTemp.put("partner", partner);
		// sParaTemp.put("_input_charset", _input_charset);
		// sParaTemp.put("pay_date", pay_date);
		// sParaTemp.put("batch_no", batch_no);
		// sParaTemp.put("batch_num", batch_num);
		// sParaTemp.put("account_name", account_name);
		// sParaTemp.put("batch_fee", batch_fee);
		// sParaTemp.put("detail_data", detail_data);
		// String sHtmlText = AlipaySubmit.buildRequest(sParaTemp,"get","确认");
		// this.redirect(sHtmlText);
		// }else{
		// this.render("/ab/dd/ddsuccess.html");
		// }
		this.render("/ab/dd/ddsuccess.html");
	}

	/**
	 * 跳进熟车地图查看页面
	 */
	@Before(AccessAbInterceptor.class)
	public void mapTrucks() {
		this.render("/ab/fmcar/mapTrucks.html");
	}

	/**
	 * 查询具体的熟车
	 */
	@Before(AccessAbInterceptor.class)
	public void searchMyFmCar() {
		String ismyfr = StringUtil.toStr(this.getPara("ismyfr"));
		String carType = StringUtil.toStr(this.getPara("carType"));
		String carLength = StringUtil.toStr(this.getPara("carLength"));
		String[] lngMinMax = StringUtil.toStr(this.getPara("lngMinMax")).split(
				",");
		String[] latMinMax = StringUtil.toStr(this.getPara("latMinMax")).split(
				",");
		SysUser user = (SysUser) this.getSessionAttr("abuser"); // 后台用户
		List<AbFmcar> carlist = new ArrayList<AbFmcar>();
		String sql = "select a.*,b.* from ab_fmcar as a left join (select c.id as shrid,c.mobile mobile1,d.jd,d.wd,d.zt from sys_user c left join ab_order_position d on c.id= d.shrid where 1 = 1";
		if (lngMinMax.length > 1) {
			sql += " and d.jd <=" + lngMinMax[1] + " and d.jd >="
					+ lngMinMax[0];
		}
		if (latMinMax.length > 1) {
			sql += " and d.wd <=" + latMinMax[1] + " and d.wd >="
					+ latMinMax[0];
		}
		sql += ") b on a.mobile = b.mobile1 where 1 = 1";
		if (ismyfr.length() > 0) {
			sql += " and a.id in (select car_id from ab_fmcar_user where user_id = '"
					+ user.getStr("id") + "')";
		} else {
			sql += " and a.id in (select car_id from ab_fmcar_user)";
		}

		if (carType.length() > 0) {
			sql += " and a.type = '" + carType + "'";
		}
		if (carLength.length() > 0) {
			sql += " and a.length = '" + carLength + "'";
		}
		carlist = AbFmcar.dao.find(sql);
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),
				carlist.size(), sql);
		this.renderJson(listpage.getList());
	}
}
