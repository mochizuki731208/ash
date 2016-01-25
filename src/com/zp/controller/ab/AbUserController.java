package com.zp.controller.ab;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.zp.entity.AbOrder;
import com.zp.entity.AbOrderItem;
import com.zp.entity.AbTcExpressOrder;
import com.zp.entity.SysUser;
import com.zp.entity.SysYhkItem;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.interceptor.AccessAbInterceptor;
import com.zp.tools.CommonProcess;
import com.zp.tools.DateUtil;
import com.zp.tools.RandomUtil;
import com.zp.tools.SmsMessage;
import com.zp.tools.StringUtil;

public class AbUserController extends Controller {
	@Before(AccessAbInterceptor.class)
	public void listorder() {
		String ddzt = StringUtil.toStr(this.getPara("ddzt"));
		String zfzt = StringUtil.toStr(this.getPara("zfzt"));
		String sjqrzt = StringUtil.toStr(this.getPara("sjqrzt"));
		String sfjd = StringUtil.toStr(this.getPara("sfjd"));
		String appstatus = StringUtil.toStr(this.getPara("appstatus"));
		SysUser abuser = this.getSessionAttr("abuser");
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from (");
		sb.append("select ao.*, aa.id appid, aoc.id cbid, aoc.cb_status,eo.style,eo.min_price,eo.max_price from ");
		sb.append(" (select * from ab_order where xdrid='"
				+ abuser.getStr("id") + "' and upper(left(sn,2)) <> 'TC') ao ");
		sb.append(" left join ab_appraise aa on ao.id = aa.order_id ");
		sb.append(" left join ab_tc_express_order eo on eo.sn = ao.sn ");
		sb.append(" left join ab_order_chargeback aoc on aoc.order_id = ao.id ) t where 1 = 1 ");
		if (ddzt.length() > 0) {
			sb.append(" and t.ddzt='" + ddzt + "'");
		}
		if (zfzt.length() > 0) {
			sb.append(" and zfzt='" + zfzt + "'");
		}
		if (sjqrzt.length() > 0) {
			sb.append(" and sjqrzt='" + sjqrzt + "'");
		}
		if (sfjd.length() > 0) {
			sb.append(" and sfjd='" + sfjd + "'");
		}
		if (appstatus.length() > 0) {
			if ("0".equals(appstatus)) {
				sb.append(" and appid is null ");
			} else if ("1".equals(appstatus)) {
				sb.append(" and appid is not null ");
			}
		}

		sb.append(" order by t.xdsj desc");
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),
				14, sb.toString());
		// 判断是否可以做退单操作
		for (Record record : listpage.getList()) {
			String dateStr = record.getStr(AbOrder.XDSJ);
			// 时间在1-24小时内，不能重复维权，如果是同城需要有接单人
			if (DateUtil.isBackTime(dateStr)
					&& StringUtil.isNull(record.getStr("cbid"))
					&& CommonProcess.existQdr(record)) {
				record.set("isback", true);
			} else {
				record.set("isback", false);
			}
		}
		this.keepPara();
		this.setAttr("listpage", listpage);
		this.render("/ab/user/listorder.html");
	}

	@Before(AccessAbInterceptor.class)
	public void listuserstore() {
		SysUser abuser = this.getSessionAttr("abuser");
		String lx = this.getPara(0);
		String sql = "select * from sys_user_store where userid='"
				+ abuser.getStr("id") + "' and lx='" + lx + "'";// 店铺
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),
				14, sql);
		this.keepPara();
		this.setAttr("listpage", listpage);
		this.render("/ab/user/listuserstore.html");
	}

	@Before(AccessAbInterceptor.class)
	public void delstore() {
		boolean flag = false;
		String id = this.getPara("id");
		Db.update("delete from sys_user_store where id=?", id);
		flag = true;
		renderJson(flag);
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
	public void listuserdeposit() {
		SysUser abuser = this.getSessionAttr("abuser");
		SysUser u = SysUser.dao.findById(abuser.get("id"));
		String sql = "select * from log_user_deposit where userid='"
				+ abuser.getStr("id") + "' order by createtime desc";
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),
				14, sql);
		this.setAttr("u", u);
		this.setAttr("listpage", listpage);
		this.render("/ab/user/listuserdeposit.html");
	}

	@Before(AccessAbInterceptor.class)
	public void delorder() {
		boolean flag = false;
		String id = this.getPara("id");
		AbOrder.dao.deleteById(id);// 级联删除订单明细信息
		flag = true;
		renderJson(flag);
	}

	@Before(AccessAbInterceptor.class)
	public void myshopcart() {
		String id = StringUtil.toStr(this.getPara(0));
		AbOrder order = AbOrder.dao.findById(id);
		// 获取订单的商品信息
		List<AbOrderItem> list = AbOrderItem.dao
				.find("select * from ab_order_item where orderid=? order by itemname",
						order.getStr("id"));

		int day = DateUtil.dayForWeek(DateUtil.getCurrentDate());
		String currtime = DateUtil.getCurrentDate("HH:mm:ss");
		Record m = Db.findFirst("select *, uf_getmerworktime(" + day + ",'"
				+ currtime + "',m.id) iswork from ab_merchant m where id=?",
				order.get("mid"));

		this.setAttr("list", list);
		this.setAttr("vo", order);
		this.setAttr("m", m);
		render("/ab/dd/dd.html");
	}

	@Before(AccessAbInterceptor.class)
	public void addrmgmt() {
		render("/ab/user/addr.html");
	}

	@Before(AccessAbInterceptor.class)
	public void invite() {
		SysUser abuser = this.getSessionAttr("abuser");
		if (abuser == null || abuser.get("id") == null) {
			redirect("/ab/login");
		} else {
			try {
				InputStream inputStream = SmsMessage.class.getClassLoader()
						.getResourceAsStream("config.properties");
				Properties property = new Properties();
				property.load(inputStream);
				String domainName = property.getProperty("DomainName");
				SysUser user = SysUser.dao.findById(abuser.getStr("id"));
				String code = user.getStr(SysUser.INVITE);
				if (StringUtil.isNull(code)) {
					code = StringUtil.getRandString32();
					user.set(SysUser.INVITE, code);
					user.update();
				}
				// String domain = getRequest().getRemoteHost();
				// int port = getRequest().getServerPort();
				this.setAttr("code", code);
				this.setAttr("domain", domainName);
				render("/ab/invite.html");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 跳进支付宝充值页面
	 */
	@Before(AccessAbInterceptor.class)
	public void yhcz() {
		this.setAttr("WIDout_trade_no", System.currentTimeMillis());
		this.setAttr("type", "0");// 0-充值 1-提现 2-付款
		this.setAttr("type_name", "充值");
		this.setAttr("WIDsubject", "充值");
		this.setAttr("WIDbody", "充值");
		// 查询线下支付方式的银行卡信息
		List<SysYhkItem> yhk_list = SysYhkItem.dao
				.find("select * from sys_yhka_item");
		this.setAttr("yhk_list", yhk_list);
		render("/ab/alipay/alipayindex.html");
	}

	/**
	 * 进入提现页面
	 */
	@Before(AccessAbInterceptor.class)
	public void tx() {
		this.setAttr("WIDout_trade_no", System.currentTimeMillis());
		this.setAttr("type_name", "提现");
		SysUser abuser = this.getSessionAttr("abuser");
		SysUser vo = SysUser.dao.findById(abuser.get("id"));
		this.setSessionAttr("abuser", vo);// 将数据库最新信息更新到前台
		this.setAttr("zhye", vo.getBigDecimal("zhye"));
		this.setAttr("mc", vo.getStr("mc"));
		render("/ab/alipay/tx.html");
	}

	/**
	 * 进入资金记录页面
	 */
	@Before(AccessAbInterceptor.class)
	public void txcz() {
		String type = StringUtil.toStr(this.getPara("type"));
		SysUser abuser = this.getSessionAttr("abuser");
		String sql = "select * from sys_cz_tx where userid = '"
				+ abuser.getStr("id") + "'";
		if (type.length() > 0) {
			sql += " and type= '" + type + "'";
		}
		sql += " order by tradeno desc";
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),
				14, sql);
		this.setAttr("listpage", listpage);
		this.setAttr("type", type);
		this.render("/ab/alipay/txcz.html");
	}

	public void saverz() {
		boolean flag = false;
		SysUser user = this.getModel(SysUser.class, "tb");
		user.set("tjsj", DateUtil.getCurrentDate());
		user.set("sfrzzt", "1");
		user.update();
		user = SysUser.dao.findById(user.get("id"));
		this.setSessionAttr("abuser", user);
		flag = true;
		renderJson(flag);
	}

	// 物流订单
	@Before(AccessAbInterceptor.class)
	public void listtcorder() {
		// modify begin
		// changbaijian/bug汇总——物流配送中心添加订单统计功能_修改查询方法，将查询条件拼接抽出方法，实现方法复用
		SysUser abuser = this.getSessionAttr("abuser");
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from (");
		// 抽离的限定条件
		queryDistributionOrder(sb, abuser);
		// modify end
		// changbaijian/bug汇总——物流配送中心添加订单统计功能_修改查询方法，将查询条件拼接抽出方法，实现方法复用
		sb.append(" order by t.xdsj desc");

		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),
				14, sb.toString());
		// 查询订单统计数量、金额/日期t统计图 changbaijian add
		StringBuffer sbu = new StringBuffer();
		sbu.append("SELECT ID,COUNT(ID) orderNum,ddzje,SUBSTR(send_time, 1, 10) orderDate FROM ( SELECT * FROM (");

		// 抽离的限定条件
		queryDistributionOrder(sbu, abuser);
		sbu.append(" )MM GROUP BY SUBSTR(MM.send_time, 1, 10)");
		sbu.append(" ORDER BY STR_TO_DATE(MM.send_time,'%Y-%m-%d') ");
		List<Record> orderList = Db.find(sbu.toString());

		StringBuffer countSbu = new StringBuffer();
		countSbu.append("SELECT SUM(t.ddzje) totalMoney,COUNT(1) totalNum  from ( ");

		// 抽离的限定条件
		queryDistributionOrder(countSbu, abuser);

		Record rec = Db.findFirst(countSbu.toString());
		// 判断是否可以做退单操作
		for (Record record : listpage.getList()) {
			String dateStr = record.getStr(AbOrder.XDSJ);
			// 时间在1-24小时内，不能重复维权，如果是同城需要有接单人
			if (DateUtil.isBackTime(dateStr)
					&& StringUtil.isNull(record.getStr("cbid"))
					&& CommonProcess.existQdr(record)) {
				record.set("isback", true);
			} else {
				record.set("isback", false);
			}
		}
		this.keepPara();
		this.setAttr("listpage", listpage);
		this.setAttr("orderList", orderList);
		this.setAttr("rec", rec);
		if (getPara("way") != null
				&& (this.getParaToInt("way") == 3 || getParaToInt("way") == 4)) {
			// Record r=
			// Db.findFirst("select count(id) from AbTcExpressOrderBaojia");
			// AbTcExpressOrderBaojia
			// baojia=AbTcExpressOrderBaojia.dao.findFirst("select count(id) from AbTcExpressOrderBaojia");

			setAttr("zje", "意向价");
			setAttr("bzj", "总金额");
		} else {
			setAttr("zje", "总金额");
			setAttr("bzj", "保证金");
		}
		this.render("/ab/user/listtcorder.html");
	}

	/**
	 * 查询物流配送订单列表，抽出的公用方法 param StringBuffer sb
	 * 
	 * @see 功能实现
	 * @remark create changbaijian/bug汇总1.1
	 *         物流配送中心添加订单统计功能_修改查询方法，将查询条件拼接抽出方法，实现方法复用
	 */
	private void queryDistributionOrder(StringBuffer sb, SysUser abuser) {
		String ddzt = StringUtil.toStr(this.getPara("ddzt"));
		String zfzt = StringUtil.toStr(this.getPara("zfzt"));
		String sjqrzt = StringUtil.toStr(this.getPara("sjqrzt"));
		String sfjd = StringUtil.toStr(this.getPara("sfjd"));
		String appstatus = StringUtil.toStr(this.getPara("appstatus"));
		
		String way = StringUtil.toStr(this.getPara("way"));
		if (way.length() == 0) {
			way = "1";
		}

		String sf=StringUtil.toStr(this.getPara("sf"));
		
		// 添加按月、周、天查询
		String queryType = StringUtil.toStr(this.getPara("queryType"));

		sb.append("select ao.*, aa.id appid, aoc.id cbid, aoc.cb_status,tc.style style,tc.min_price min_price,tc.max_price max_price,tc.pay_type,tc.sh_user_id,tc.sh_user_id2,tc.sh_user_id3,tc.sh_user_id4,tc.sh_user_id5,tc.rcv_phone1,tc.rcv_phone2,tc.rcv_phone3,tc.rcv_phone4,tc.rcv_phone5, tc.send_time,tc.rcv_name1,tc.product_name,tc.product_sn,tc.producer,tc.cheliang,tc.transport,tc.cangku,tc.operator from ");
		sb.append(" (select a.*,(select count(id) from ab_tc_express_order_diaodu where orderid=a.id) ddcount,(select count(id)as bj from ab_fmcar_order_baojia where orderid=a.id) bjcount from ab_order a where upper(left(sn,2))='TC') ao ");
		sb.append(" left join ab_appraise aa on ao.id = aa.order_id ");
		sb.append(" left join ab_tc_express_order tc on tc.sn = ao.sn ");
		sb.append(" left join ab_order_chargeback aoc on aoc.order_id = ao.id ) t where way="+way);
		if(way.equals('3')||way.equals("4")||way.equals("5")){
			sb.append("  and xdrid='"+abuser.getStr("id")+"' ");
		}
		else{
			if(sf.length()>0&&sf.equalsIgnoreCase("s")){
				sb.append(" and (lxrdh='"+abuser.getStr("loginid")
						+"'  or rcv_phone1='"+abuser.getStr("loginid")
						+"'  or rcv_phone2='"+abuser.getStr("loginid")
						+"'  or rcv_phone3='"+abuser.getStr("loginid")
						+"'  or rcv_phone4='"+abuser.getStr("loginid")
						+"'  or rcv_phone5='"+abuser.getStr("loginid")
						+"')");
			}
			if(sf.length()>0&&sf.equalsIgnoreCase("f")){
				sb.append(" and xdrid='"+abuser.getStr("id")+"' ");
			}
		}
//		" (xdrid='"
//				+ abuser.getStr("id")
//				+ "' or sh_user_id='"
//				+ abuser.getStr("id")
//				+ "'"
//				+ " or sh_user_id2='"
//				+ abuser.getStr("id")
//				+ "'"
//				+ " or sh_user_id3='"
//				+ abuser.getStr("id")
//				+ "'"
//				+ " or sh_user_id4='"
//				+ abuser.getStr("id")
//				+ "'"
//				+ " or sh_user_id5='"
//				+ abuser.getStr("id") + "'" + ") ");

		if (ddzt.length() > 0) {
			sb.append(" and t.ddzt='" + ddzt + "'");
		}
		if (zfzt.length() > 0) {
			sb.append(" and zfzt='" + zfzt + "'");
		}
		if (sjqrzt.length() > 0) {
			sb.append(" and sjqrzt='" + sjqrzt + "'");
		}
		if (sfjd.length() > 0) {
			sb.append(" and sfjd='" + sfjd + "'");
		}
		if (appstatus.length() > 0) {
			if ("0".equals(appstatus)) {
				sb.append(" and appid is null ");
			} else if ("1".equals(appstatus)) {
				sb.append(" and appid is not null ");
			}
		}
		

		
		String p_rcvName = this.getPara("p_rcvName");
		String p_product_name = this.getPara("p_product_name");
		String p_product_sn = this.getPara("p_product_sn");
		String p_producer = this.getPara("p_producer");
		String p_cheliang = this.getPara("p_cheliang");
		String p_transport = this.getPara("p_transport");
		String p_cangku = this.getPara("p_cangku");
		String p_operator = this.getPara("p_operator");
		String p_start_date = this.getPara("p_start_date");
		String p_end_date = this.getPara("p_end_date");

		if (!StringUtil.isNull(p_rcvName)) {
			sb.append(" and rcv_name1 like '%" + p_rcvName + "%'");
		}
		if (!StringUtil.isNull(p_product_name)) {
			sb.append(" and product_name like '%" + p_product_name + "%'");
		}
		if (!StringUtil.isNull(p_product_sn)) {
			sb.append(" and product_sn like '%" + p_product_sn + "%'");
		}
		if (!StringUtil.isNull(p_producer)) {
			sb.append(" and producer like '%" + p_producer + "%'");
		}
		if (!StringUtil.isNull(p_cheliang)) {
			sb.append(" and cheliang like '%" + p_cheliang + "%'");
		}
		if (!StringUtil.isNull(p_transport)) {
			sb.append(" and transport like '%" + p_transport + "%'");
		}
		if (!StringUtil.isNull(p_cangku)) {
			sb.append(" and cangku like '%" + p_cangku + "%'");
		}
		if (!StringUtil.isNull(p_operator)) {
			sb.append(" and operator like '%" + p_operator + "%'");
		}
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
		if (queryType.equals("")) {
			// 点击的查询按钮
			if (!StringUtil.isNull(p_start_date)) {
				try {
					fmt.parse(p_start_date);
					sb.append(" and send_time >= '" + p_start_date + "'");
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			if (!StringUtil.isNull(p_end_date)) {
				try {
					Date date = fmt.parse(p_end_date);
					// long time = date.getTime() + 24 * 60 * 60 * 1000;
					// date = new Date(time);
					// sb.append(" and send_time < '" + fmt.format(date) + "'");
					sb.append(" and send_time < '" + p_end_date + "'");
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		} else if (queryType.equals("day")) {
			sb.append(" and send_time >= '"
					+ DateUtil.getCurrentDate("yyyy-MM-dd") + " 00:00:00'");
			sb.append(" and send_time < '"
					+ DateUtil.getCurrentDate("yyyy-MM-dd") + " 59:59:59'");
		} else if (queryType.equals("week")) {
			sb.append(" and send_time >= '"
					+ DateUtil.format(DateUtil.getMondayOfWeek(), "yyyy-MM-dd")
					+ " 00:00:00'");
			sb.append(" and send_time < '"
					+ DateUtil.format(DateUtil.getSundayOfWeek(), "yyyy-MM-dd")
					+ " 59:59:59'");
		} else if (queryType.equals("month")) {
			sb.append(" and send_time >= '"
					+ DateUtil.getCurrentDate("yyyy-MM") + "-01 00:00:00'");
			sb.append(" and send_time < '"
					+ DateUtil.format(DateUtil.getLastDayOfMonth(),
							"yyyy-MM-dd") + " 59:59:59'");
		}
	}

	@Before(AccessAbInterceptor.class)
	public void uploadTcOrder() {
//		SysUser user = (SysUser)this.getSessionAttr("abuser");
//		this.setAttr("list", excelData.get(user.getStr("id")));
		String flag = StringUtil.toStr(this.getPara("flag"));//是否二次请求页面
		if(flag.length()==0){
			this.render("/ab/user/uploadtcorder.html");
		}else{
			SysUser user = (SysUser)this.getSessionAttr("abuser");
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("data", excelData.get(user.getStr("id")));
			renderJson(resultMap);
		}
	}

	public void actionUploadExcel() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String userId = this.getPara("userid");
		if (userId == null) {
			resultMap.put("successNum", 0);
			resultMap.put("errorMsg", "用户未登录");
			renderJson(false);
		} else {

			Workbook wb = null;
			Sheet sheet = null;
			FileInputStream is = null;

			try {
				UploadFile f = this.getFile();
				File excel = f.getFile();
				is = new FileInputStream(excel);
				String fileName = excel.getName();
				if (StringUtils.isBlank(fileName)) {
					renderJson("导入文档为空!");
					return;
				} else if (fileName.toLowerCase().endsWith("xls")) {
					wb = new HSSFWorkbook(is);
				} else if (fileName.toLowerCase().endsWith("xlsx")) {
					wb = new XSSFWorkbook(is);
				} else {
					renderJson("文档格式不正确!");
					return;
				}

				if (wb.getNumberOfSheets() < 0) {
					renderJson("文档中没有工作表!");
					return;
				}

				sheet = wb.getSheetAt(0);

				List list = new ArrayList();
				StringBuffer errorMsg = new StringBuffer("");
				int successNum = 0;
				int start = sheet.getFirstRowNum() + 3; // 标题不计
				int end = sheet.getLastRowNum();
				for (int i = start; i <= end; i++) {
					try {
						Row row = sheet.getRow(i);
						int index = row.getFirstCellNum();
						String startAddr = getCellValue(row.getCell(index++));
						if (StringUtil.isNull(startAddr)) {
							continue;
						}
						System.out.println("start addr : " + startAddr);
						String sender = getCellValue(row.getCell(index++));
						String sendPhone = getCellValue(row.getCell(index++));
						String addr = getCellValue(row.getCell(index++));
						String receiver = getCellValue(row.getCell(index++));
						String rcvPhone = getCellValue(row.getCell(index++));
						// 添加零担或整车类型
						String transferType = getCellValue(row.getCell(index++));
						String cheliang = getCellValue(row.getCell(index++));

						String productName = getCellValue(row.getCell(index++));
						String productSn = getCellValue(row.getCell(index++));
						String producer = getCellValue(row.getCell(index++));
						String cangku = getCellValue(row.getCell(index++));
						String operator = getCellValue(row.getCell(index++));

						String volumn = getCellValue(row.getCell(index++));
						String weight = getCellValue(row.getCell(index++));
						String money = getCellValue(row.getCell(index++));

						Map<String, String> map = new HashMap<String, String>();
						map.put("sender", sender);
						if (StringUtil.isNull(sender)) {
							renderJson("第" + row + "行，发货人为空了。");
							return;
						}
						map.put("sendPhone", sendPhone);
						if (StringUtil.isNull(sendPhone)) {
							renderJson("第" + row + "行，发货人电话为空了。");
							return;
						}
						map.put("sendAddr", startAddr);
						if (StringUtil.isNull(startAddr)) {
							renderJson("第" + row + "行，发货地址为空了。");
							return;
						}
						map.put("addr", addr);
						if (StringUtil.isNull(addr)) {
							renderJson("第" + row + "行，收货地址为空了。");
							return;
						}
						map.put("receiver", receiver);
						if (StringUtil.isNull(receiver)) {
							renderJson("第" + row + "行，收货人为空了。");
							return;
						}
						map.put("rcvPhone", rcvPhone);
						if (StringUtil.isNull(rcvPhone)) {
							renderJson("第" + row + "行，收货人电话为空了。");
							return;
						}
						// 添加零担或整车类型
						map.put("transferType", transferType);
						if (StringUtil.isNull(transferType)) {
							renderJson("第" + row + "行，送货类型为空了。");
							return;
						}
						map.put("cheliang", cheliang);
						if (StringUtil.isNull(cheliang)) {
							renderJson("第" + row + "行，车辆为空了。");
							return;
						}
						map.put("productName", productName);
						if (StringUtil.isNull(productName)) {
							renderJson("第" + row + "行，产品名称为空了。");
							return;
						}
						map.put("productSn", productSn);
						if (StringUtil.isNull(productSn)) {
							renderJson("第" + row + "行，产品编号为空了。");
							return;
						}
						map.put("producer", producer);
						if (StringUtil.isNull(producer)) {
							renderJson("第" + row + "行，厂家为空了。");
							return;
						}
						map.put("cangku", cangku);
						if (StringUtil.isNull(cangku)) {
							renderJson("第" + row + "行，仓库为空了。");
							return;
						}
						map.put("operator", operator);
						map.put("volumn", volumn);
						if (StringUtil.isNull(volumn)) {
							renderJson("第" + row + "行，体积为空了。");
							return;
						}
						map.put("weight", weight);
						if (StringUtil.isNull(volumn)) {
							renderJson("第" + row + "行，重量为空了。");
							return;
						}
						map.put("money", money);
						// 添加整车账单计算方式
						String cityName = "";
						if (startAddr.indexOf("市") > 0) {
							cityName = startAddr.split("市")[0];
						} else if (startAddr.indexOf("区") > 0) {
							cityName = startAddr.split("区")[0];
						} else if (startAddr.indexOf("省") > 0) {
							cityName = startAddr.split("省")[0];
						} else {
							cityName = startAddr.substring(0, 1);
							// renderJson("第" + row + "行，出发地必须包含市或区或省。");
						}
						if ("整车".equals(transferType)) {
							String sql = "select *  from ab_tc_config where city_id in(select id from ab_cityarea a where mc like '%"
									+ cityName + "%' )";
							List<Record> arrList = Db.find(sql);
							// 定义接受的数据类型
							String str = "";
							int k = 0;

							for (int j = 0; j < arrList.size(); j++) {
								if (cheliang.contains((CharSequence) arrList
										.get(j).get("name"))) {

									str += (String) arrList.get(j).get("value")
											+ "-";

								}

							}
							map.put("total", str);
						}

						map.put("sn", "TC" + RandomUtil.createRandomNumPwd(9));//设置好订单编号
						list.add(map);

						successNum++;
					} catch (Exception e) {
						e.printStackTrace();
						errorMsg.append(e.getMessage());
					}
				}
				resultMap.put("data", list);
				resultMap.put("successNum", successNum);
				resultMap.put("errorMsg", errorMsg.toString());
				// this.setSessionAttr("excelData", list);
				excelData.put(userId, list);
			} catch (Exception e) {
				e.printStackTrace();
				renderJson(e.getMessage());
				return;
			} finally {
				try {
					if (is != null) {
						is.close();
					}
				} catch (Exception e2) {
				}
			}
			renderJson(resultMap);
		}
	}

	private static Map excelData = new HashMap();

	private String getCellValue(Cell cell) {
		String cellValue = "";
		if (cell != null) {
			int cellType = cell.getCellType();
			switch (cellType) {
			case Cell.CELL_TYPE_BOOLEAN:
				cellValue = String.valueOf(cell.getBooleanCellValue());
				break;
			case Cell.CELL_TYPE_NUMERIC:
				cellValue = "" + cell.getNumericCellValue();
				break;
			default:
				cellValue = String.valueOf(cell.getStringCellValue());

			}
		}
		return cellValue;
	}
	
	@Before(AccessAbInterceptor.class)
	public void bachpay(){
		Map result = new HashMap();//处理结果
		try{
			SysUser user = this.getSessionAttr("abuser");
			// 获得传过来的费用和路程,订单号
			String kilo = getPara("kilo");
			String total_price = getPara("total_price");
			String sns = getPara("sns");
			// 将获得数据划分成数组
			String[] kiloArr = kilo.split("-");
			String[] total_priceArr = total_price.split("-");
			String[] snArr = sns.split("-");
			Map snMap = new HashMap();
			for(int k = 0;k < snArr.length;k++){
				snMap.put(snArr[k], k);
			}
			//计算要支付的总额。与账户余额进行比较
			double totalprice = 0;
			for(String str:total_priceArr){
				totalprice += Double.parseDouble(str);
			}
			double zhye = Double.parseDouble(user.get("zhye") + "");
			if(zhye < totalprice){//账户余额不足部分
				result.put("msg", "failed");
				renderJson(false);
				return;
			}else{
				user.set("zhye", zhye-totalprice);
//				user.update();
			}
			List list = (List) excelData.get(user.get("id"));
			if (list == null) {
				result.put("msg", "error");
				renderJson(false);
				return;
			}
			
			List lastlist = new ArrayList();
			for(Object obj : list){
				Map map = (Map)obj;
				String sn = (String)map.get("sn");
				int i = -1;
				try{
					i = Integer.parseInt(snMap.get(sn) + "");
				}catch (Exception e) {
					i=-1;
				}
				if(i!=-1){//该订单号存在
					AbTcExpressOrder order = new AbTcExpressOrder();
//					String total = (String) map.get("money");
//					if (StringUtil.isNull(total)) {
//						total = "0";
//					}
					order.set("total_price",new BigDecimal(Double.parseDouble(total_priceArr[i])));
					order.set("send_time", new Date());
					order.set("lift", "");
					order.set("goods_type", "");
					order.set("goods_mount", 1);
					String volumn = (String) map.get("volumn");
					order.set("goods_volumn", volumn);
					order.set("huidan_price", 0);
					order.set("service_price", 0);
					order.set("pay_type", 1);//在线支付
					String weight = (String) map.get("weight");
					if (StringUtil.isNull(weight)) {
						weight = "0";
					}
					int w = (int) (Double.parseDouble(weight) * 1000);
					order.set("weight", w);
					order.set("people", null);
					order.set("cart", null);
					order.set("night_price", null);
					order.set("remark", null);
					order.set("product_name", map.get("productName"));
					order.set("cheliang", map.get("cheliang"));
					order.set("send_name", map.get("sender"));
					order.set("send_phone", map.get("sendPhone"));
					order.set("send_addr", map.get("sendAddr"));
					order.set("rcv_name1", map.get("receiver"));
					order.set("rcv_phone1", map.get("rcvPhone"));
					order.set("rcv_addr1", map.get("addr"));
					// 添加运输类型
					order.set("transfer_type", map.get("transferType"));
					// 添加运输公里和运输的金钱
					order.set("kilo", kiloArr[i]);
					order.set("total_price", total_priceArr[i]);
					order.set("product_sn", map.get("productSn"));
					order.set("cangku", map.get("cangku"));
					order.set("producer", map.get("producer"));
					order.set("operator", map.get("operator"));
					order.set("is_batch", "Y");
					// 数组自增
//					i++;
					// 保存订单
//					String sn = null;
//					sn = "TC" + RandomUtil.createRandomNumPwd(9);
					order.set("sn", sn);

					// 保存父订单表
					// 生成订单数据
					String orderid = StringUtil.getUuid32();
					// 保存购物车订单数据
					AbOrder o = new AbOrder();
					o.set("id", orderid);
					o.set("sn", sn);
					o.set("spzj", new BigDecimal(Double.parseDouble(total_priceArr[i])));

					o.set("is_type", "1");
					o.set("ddzje", new BigDecimal(Double.parseDouble(total_priceArr[i])));
					o.set("ddzt", "2");
					o.set("lxr", order.get("rcv_name1"));
					o.set("lxrdh", order.get("rcv_phone1"));
					o.set("shdz", order.get("rcv_addr1"));

					o.set("xdrid", user.get("id"));
					o.set("xdrmc", user.get("mc"));
					o.set("xdrdh", user.get("mobile"));
					o.set("xdsj", DateUtil.getCurrentDate());
					o.set("city_id", user.getStr("city_id"));
					o.set("city_name", user.getStr("city_name"));
					o.set("area_id", user.getStr("area_id"));
					o.set("area_name", user.getStr("area_name"));
					o.set("zfzt", 1);
					// 同步到数据库
					order.save();
					o.save();

					AbTcContrller.tuisong(orderid);
					String yzm = RandomUtil.createRandomPwd(6);
					SmsMessage.SendMessage(
							order.getStr(AbTcExpressOrder.RCV_PHONE1), yzm);
					AbTcContrller.insertOrderSms(sn,
							order.getStr(AbTcExpressOrder.RCV_PHONE1), yzm);
				}else{
					lastlist.add(map);
				}
			}
			user.update();
			excelData.put(user.getStr("id"), lastlist);
			result.put("msg", "success");
			renderJson(result);
		}catch(Exception e){
			e.printStackTrace();
			result.put("msg", "error");
			renderJson(result);
		}
	}

	@Before(AccessAbInterceptor.class)
	public void saveUploadExcel() {
		try {
			SysUser user = this.getSessionAttr("abuser");

			// 获得传过来的费用和路程

			String kilo = getPara("kilo");

			String total_price = getPara("total_price");

			// 将获得数据划分成数组
			String[] kiloArr = kilo.split("-");
			String[] total_priceArr = total_price.split("-");
			// List list = this.getSessionAttr("excelData");
			List list = (List) excelData.get(user.get("id"));
			if (list == null) {
				renderJson(false);
				return;
			}

			// 获得划分数组标志
			int i = 0;
			for (Object obj : list) {
				Map map = (Map) obj;
				AbTcExpressOrder order = new AbTcExpressOrder();
				String total = (String) map.get("money");
				if (StringUtil.isNull(total)) {
					total = "0";
				}
				order.set("total_price",
						new BigDecimal(Double.parseDouble(total)));
				order.set("send_time", new Date());
				order.set("lift", "");
				order.set("goods_type", "");
				order.set("goods_mount", 1);
				String volumn = (String) map.get("volumn");
				order.set("goods_volumn", volumn);
				order.set("huidan_price", 0);
				order.set("service_price", 0);
				order.set("pay_type", 3);
				String weight = (String) map.get("weight");
				if (StringUtil.isNull(weight)) {
					weight = "0";
				}
				int w = (int) (Double.parseDouble(weight) * 1000);
				order.set("weight", w);
				order.set("people", null);
				order.set("cart", null);
				order.set("night_price", null);
				order.set("remark", null);
				order.set("product_name", map.get("productName"));
				order.set("cheliang", map.get("cheliang"));
				order.set("send_name", map.get("sender"));
				order.set("send_phone", map.get("sendPhone"));
				order.set("send_addr", map.get("sendAddr"));
				order.set("rcv_name1", map.get("receiver"));
				order.set("rcv_phone1", map.get("rcvPhone"));
				order.set("rcv_addr1", map.get("addr"));
				// 添加运输类型
				order.set("transfer_type", map.get("transferType"));
				// 添加运输公里和运输的金钱
				order.set("kilo", kiloArr[i]);
				order.set("total_price", total_priceArr[i]);
				order.set("product_sn", map.get("productSn"));
				order.set("cangku", map.get("cangku"));
				order.set("producer", map.get("producer"));
				order.set("operator", map.get("operator"));
				order.set("is_batch", "Y");
				// 数组自增
				i++;
				// 保存订单
				String sn = null;
				sn = "TC" + RandomUtil.createRandomNumPwd(9);
				order.set("sn", sn);

				// 保存父订单表
				// 生成订单数据
				String orderid = StringUtil.getUuid32();
				// 保存购物车订单数据
				AbOrder o = new AbOrder();
				o.set("id", orderid);
				o.set("sn", sn);
				o.set("spzj", new BigDecimal(Double.parseDouble(total)));

				o.set("is_type", "1");
				o.set("ddzje", new BigDecimal(Double.parseDouble(total)));
				o.set("ddzt", "1");
				o.set("lxr", order.get("rcv_name1"));
				o.set("lxrdh", order.get("rcv_phone1"));
				o.set("shdz", order.get("rcv_addr1"));

				o.set("xdrid", user.get("id"));
				o.set("xdrmc", user.get("mc"));
				o.set("xdrdh", user.get("mobile"));
				o.set("xdsj", DateUtil.getCurrentDate());
				o.set("city_id", user.getStr("city_id"));
				o.set("city_name", user.getStr("city_name"));
				o.set("area_id", user.getStr("area_id"));
				o.set("area_name", user.getStr("area_name"));

				// 同步到数据库
				order.save();
				o.save();

				AbTcContrller.tuisong(orderid);
				String yzm = RandomUtil.createRandomPwd(6);
				SmsMessage.SendMessage(
						order.getStr(AbTcExpressOrder.RCV_PHONE1), yzm);
				AbTcContrller.insertOrderSms(sn,
						order.getStr(AbTcExpressOrder.RCV_PHONE1), yzm);
			}
			excelData.put(user.get("id"),new ArrayList());
			renderJson(true);
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(false);
		}
	}

}
