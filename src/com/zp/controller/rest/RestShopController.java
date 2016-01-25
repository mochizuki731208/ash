package com.zp.controller.rest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.omg.PortableInterceptor.SUCCESSFUL;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.zp.entity.AbCard;
import com.zp.entity.AbCardExpense;
import com.zp.entity.AbIntegralScore;
import com.zp.entity.AbMerchant;
import com.zp.entity.AbMerchantProduct;
import com.zp.entity.AbOrder;
import com.zp.entity.AbOrderItem;
import com.zp.entity.AbProductImg;
import com.zp.entity.SysCzTx;
import com.zp.entity.SysUser;
import com.zp.tools.CommonSQL;
import com.zp.tools.CommonStaticData;
import com.zp.tools.DateUtil;
import com.zp.tools.RandomUtil;
import com.zp.tools.SmsMessage;
import com.zp.tools.StringUtil;
import com.zp.tools.TuiSongUtil;

public class RestShopController extends AbstractRestController {
	/**
	 * 
	 */
	public void category() {
		Map<String, Object> resultMap = new HashMap<String, Object>();		
		try {
			String id = "c959de70210645abb633332a6e828953";
			//查看分类
			List<Record> list_sub = Db.find("SELECT DISTINCT thr_id id,thr_name mc,a.img_url img_url,a.thumbnail_url thumbnail_url FROM ab_merchant_product a where MID =? order by thr_id",id);
			resultMap.put("data", list_sub);
		}catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(resultMap);
	}
	public void products() {
		Map<String, Object> resultMap = new HashMap<String, Object>();		
		try {
			String category = this.getPara("category");
			String id = "c959de70210645abb633332a6e828953";
			//查看分类
			List<Record> products = Db.find("select * from ab_merchant_product where mid=? and thr_id=? and zt='1' order by seq_num",id,category);
			resultMap.put("data", products);
		}catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(resultMap);
	}
	
	public void addtocart() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		//商家id
		String mid = this.getPara("mid");
		//商品id
		String proid = this.getPara("proid");
		//添加数量
		int addnum = this.getParaToInt("addnum");
		String uid = this.getPara("uid");
		if (StringUtils.isEmpty(uid)) {
			formatInvalidParamResponse(resultMap);
		} else {
		SysUser user = SysUser.dao.findById(uid);
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
		resultMap.put("result", RESULT_SUCCESS);
		resultMap.put("msg", "下单成功");
		}
		renderJson(resultMap);
	}
	/**
	 * 商品详细信息
	 */
	public void prodetail(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String pid = this.getPara("pid");
		String uid = this.getPara("uid");
		if (StringUtils.isEmpty(pid)) {
			formatInvalidParamResponse(resultMap);
		} else {
		
		AbMerchantProduct p = AbMerchantProduct.dao.findById(pid);
		List<AbProductImg> list_img = AbProductImg.dao.find("select * from ab_product_img where f_id=?",pid);
		
		int day = DateUtil.dayForWeek(DateUtil.getCurrentDate());
		String currtime = DateUtil.getCurrentDate("HH:mm:ss");
		
//		Record vo = Db.findById("ab_merchant",p.get("mid"));
//		
//		//获取当前日期是否工作中
//		String	sql = "SELECT uf_getmerworktime(?,?,?)";
//		vo.set("iswork", Db.queryStr(sql,day,currtime,vo.get("id")));
		
		
		//查看订单详细数据
		List<Record> list_order = null;
		SysUser user =  null;
		if (StringUtils.isNotEmpty(uid)) {
			user =  SysUser.dao.findById(uid);
		}
		if(user != null && user.get("id") != null){
			
		
			list_order = Db.find("SELECT * FROM ab_order WHERE ddzt='0' AND xdrid=?",user.get("id"));
			//获取每个订单的商品数
			if(list_order!=null&&list_order.size()>0){
				for (int i = 0; i < list_order.size(); i++) {
					list_order.get(i).set("list_item", Db.find("select * from ab_order_item where orderid=? order by itemname",list_order.get(i).get("id")));
				}
			}
		}
		
		//获取订单总额
		Record r = null;
		long pcnt = 0;
		if (user != null && user.get("id") != null) {
			r = Db.findFirst("SELECT IFNULL(SUM(spzj),0) spzj,IFNULL(SUM(ptf),0) ptf,IFNULL(SUM(yhje),0) yhje,IFNULL(SUM(ddzje),0) ddzje FROM ab_order WHERE ddzt='0' AND xdrid=?",user.get("id"));
			//获取当前商品购买的数量
			pcnt = Db.queryBigDecimal("select ifnull(sum(quantity),0) from ab_order_item where orderid in (SELECT id FROM ab_order WHERE ddzt='0' AND xdrid=?) AND itemid=?",user.get("id"),p.get("id")).longValue();
		}
		
//		if(user!=null&&user.get("id")!=null){
//			if(Db.queryLong("select count(id) from sys_user_store where userid=? and fkid=?",user.get("id"),p.get("id")).longValue()>0){
//				resultMap.put("sfsc", true);
//			}else{
//				resultMap.put("sfsc", false);
//			}
//		}else{
//			//是否收藏
//			resultMap.put("sfsc", false);
//		}
		//购物车商品数
		resultMap.put("pcnt", pcnt);
		//订单详情
//		resultMap.put("list_order", list_order);
//		//订单信息
//		resultMap.put("r", r);
		//商家
//		resultMap.put("shop", vo);
		//商品
		resultMap.put("product", p);
		//照片
		resultMap.put("list_img", list_img);
		}
		renderJson(resultMap);
	}
	
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
//			try {
//				String return_url = "http://www.yijuhome.cn/ab/order/payBack";
////				this.redirect(alipayapi(tradeno, "支付宝付款", b_total_ddzje
////						.toString(), "在线购买", return_url));
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
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
	
	
	
	
	}
