package com.zp.controller.trans;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.zp.entity.AbAskPrice;
import com.zp.entity.AbCityarea;
import com.zp.entity.AbFmcar;
import com.zp.entity.AbSubject;
import com.zp.entity.SysUser;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.tools.DateUtil;
import com.zp.tools.StringUtil;

public class LogisticsIndexController extends Controller {

	public void index() {
		keepPara();
		search();
		getCity();
		getLogi();
		render("/ab/logi/index.html");
	}
	
	public void index1() {
		keepPara();
		search1();
		getCity();
		getLogi();
		render("/ab/logi/index1.html");
	}

	public void map() {
		keepPara();
		getCity();
		getLogi();
		render("/ab/logi/map.html");
	}

	public void ajax() {
		List<Record> record = Db.find(getSQL());
		renderJson(record);
	}

	
	
	private String getSelect() {
		return "select m.mc, m.city_name, m.img_url, m.thumbnail_url, m.logo, m.lng, m.lat, m.subject_name,"
				+ " m.mobile, m.comments_total, m.hps, (m.hps/m.comments_total) as score, w.* " 
				+",aml.xsts,aml.from,aml.to,aml.from_time,aml.to_time,aml.weight_price,aml.volumn_price,TIMESTAMPDIFF(DAY,aml.from_time,aml.to_time) as tian "
				+ " from ab_merchant AS m LEFT JOIN ab_merchant_logistics AS w ON m.id = w.mid "
				+ " left join  ab_merchant_logline aml on aml.lid = w.id  where m.check_status='1' " 
				+ "and w.check_status='1' and m.mobile!='' and m.is_display='1' "
				+ " and m.subject_id like '1020%' ";
		// "and m.user_id IN (select userid from sys_user_type where usertype=3)
		// ";
	}

	private String getSQL() {
		String from = getParaTrans("order_start_city_name");
		String to = getParaTrans("order_arr_city_name");
		String loginame = getParaTrans("loginame");
		String keywords = getParaTrans("keywords");
		// 商圈
		String[] cityarea = getParaValues("cityarea");
		this.setCheckboxAttr("cityarea", cityarea);
		// 服务分类
		String[] subject = getParaValues("subject");
		this.setCheckboxAttr("subject", subject);
		// 默认排序
		String mrorder = getParaTrans("mrorder");
		// 销量排序
		String zxlorder = getParaTrans("zxlorder");
		// 价格排序
		String avgpriceorder = getParaTrans("avgpriceorder");
		// 好评排序
		String hpsorder = getParaTrans("hpsorder");

		// 搜索物流企业
		String sql = getSelect();
//		String sqlfromto = "select g.lid from ab_merchant_logline as g where 1=1";
//		boolean bfromto = false;
		if (from != null && from.length() > 0) {
			sql += " and aml.from='" + from + "'";
//			bfromto = true;
		}
		if (to != null && to.length() > 0) {
			sql += " and aml.to='" + to + "'";
//			bfromto = true;
		}
//		if (bfromto)
//			sql += " and w.id in (" + sqlfromto + ") ";
		if (loginame != null && loginame.length() > 0)
			sql += " and m.mc like '%" + loginame.replace(" ", "%") + "%'";
		if (keywords != null && keywords.length() > 0){
			sql += " and w.profile like '%" + keywords.replace(" ", "%") + "%'";
		}else{
			AbCityarea currcity = this.getSessionAttr("currcity");
			if(currcity!=null&&currcity.getStr("mc")!=null){
				sql += " and w.profile like '%" + currcity.getStr("mc").replace(" ", "%") + "%'";
			}
		}
			
		if (cityarea != null && cityarea.length > 0)
			sql += " and m.area_id IN (" + trans(cityarea) + ") ";
		if (subject != null && subject.length > 0)
			sql += " and m.subject_id IN (" + trans(subject) + ") ";
		sql += " order by ";
		if (mrorder != null && mrorder.length() > 0)
			sql += " m.seq_num, ";
		if (zxlorder != null && zxlorder.length() > 0)
			sql += " m.zxl " + zxlorder + ", ";
		if (avgpriceorder != null && avgpriceorder.length() > 0)
			sql += " aml.weight_price " + avgpriceorder + ", ";
		if (hpsorder != null && hpsorder.length() > 0)
			sql += " m.hps " + hpsorder + ", ";
		sql += " m.id ";
		return sql;
	}

	private String getSQL1() {
		String from = getParaTrans("order_start_city_name");
		String to = getParaTrans("order_arr_city_name");
		String loginame = getParaTrans("loginame");
		String keywords = getParaTrans("keywords");
		// 商圈
		String[] cityarea = getParaValues("cityarea");
		this.setCheckboxAttr("cityarea", cityarea);
		// 服务分类
		String[] subject = getParaValues("subject");
		this.setCheckboxAttr("subject", subject);
		// 默认排序
		String mrorder = getParaTrans("mrorder");
		// 销量排序
		String zxlorder = getParaTrans("zxlorder");
		// 价格排序
		String avgpriceorder = getParaTrans("avgpriceorder");
		// 好评排序
		String hpsorder = getParaTrans("hpsorder");

		// 搜索物流企业
		String sql = getSelect();
//		String sqlfromto = "select g.lid from ab_merchant_logline as g where 1=1";
//		boolean bfromto = false;
		if (from != null && from.length() > 0) {
			sql += " and aml.from='" + from + "'";
//			bfromto = true;
		}
		if (to != null && to.length() > 0) {
			sql += " and aml.to='" + to + "'";
//			bfromto = true;
		}
//		if (bfromto)
//			sql += " and w.id in (" + sqlfromto + ") ";
		if (loginame != null && loginame.length() > 0)
			sql += " and m.mc like '%" + loginame.replace(" ", "%") + "%'";
		if (keywords != null && keywords.length() > 0){
			sql += " and w.profile like '%" + keywords.replace(" ", "%") + "%'";
		}else{
			/*AbCityarea currcity = this.getSessionAttr("currcity");
			if(currcity!=null&&currcity.getStr("mc")!=null){
				sql += " and w.profile like '%" + currcity.getStr("mc").replace(" ", "%") + "%'";
			}*/
		}
			
		if (cityarea != null && cityarea.length > 0)
			sql += " and m.area_id IN (" + trans(cityarea) + ") ";
		if (subject != null && subject.length > 0)
			sql += " and m.subject_id IN (" + trans(subject) + ") ";
		sql += " order by ";
		if (mrorder != null && mrorder.length() > 0)
			sql += " m.seq_num, ";
		if (zxlorder != null && zxlorder.length() > 0)
			sql += " m.zxl " + zxlorder + ", ";
		if (avgpriceorder != null && avgpriceorder.length() > 0)
			sql += " aml.weight_price " + avgpriceorder + ", ";
		if (hpsorder != null && hpsorder.length() > 0)
			sql += " m.hps " + hpsorder + ", ";
		sql += " m.id ";
		return sql;
	}
	
	private void search() {
		String from = getParaTrans("order_start_city_name");
		String to = getParaTrans("order_arr_city_name");
		String loginame = getParaTrans("loginame");
		String keywords = getParaTrans("keywords");
		String sql  = getSQL();
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1), 5,
				sql);
		this.setAttr("listpage", listpage);

		String curDate = DateUtil.getCurrentDate();
		// 推荐物流企业
		String resql = getSelect()
				+ " and m.is_recommend = '1' and m.tjkssj < '" + curDate
				+ "' and m.tjjzsj > '" + curDate + "' order by m.seq_num ";
		System.out.println("tuijian: " + resql);
		PageUtil relistpage = DbPage.paginate(this.getParaToInt("curPage", 1),
				4, resql);
		this.setAttr("tuijian", relistpage);
		this.setAttr("start_city_name", from);
		this.setAttr("arr_city_name", to);
		this.setAttr("loginame", loginame);
		this.setAttr("keywords", keywords);
		
		// 商圈
		String[] cityarea = getParaValues("cityarea");
		this.setCheckboxAttr("cityarea", cityarea);
		// 服务分类
		String[] subject = getParaValues("subject");
		this.setCheckboxAttr("subject", subject);
		// 默认排序
		String mrorder = getParaTrans("mrorder");
		// 销量排序
		String zxlorder = getParaTrans("zxlorder");
		// 价格排序
		String avgpriceorder = getParaTrans("avgpriceorder");
		// 好评排序
		String hpsorder = getParaTrans("hpsorder");
		
		this.setAttr("avgpriceorder", avgpriceorder==null?"ASC":avgpriceorder);

	}
	
	private void search1() {
		String from = getParaTrans("order_start_city_name");
		String to = getParaTrans("order_arr_city_name");
		String loginame = getParaTrans("loginame");
		String keywords = getParaTrans("keywords");
		String sql  = getSQL1();
		
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1), 5,
				sql);
		this.setAttr("listpage", listpage);

		String curDate = DateUtil.getCurrentDate();
		// 推荐物流企业
		String resql = getSelect()
				+ " and m.is_recommend = '1' and m.tjkssj < '" + curDate
				+ "' and m.tjjzsj > '" + curDate + "' order by m.seq_num ";
		System.out.println("tuijian: " + resql);
		PageUtil relistpage = DbPage.paginate(this.getParaToInt("curPage", 1),
				4, resql);
		this.setAttr("tuijian", relistpage);
		this.setAttr("start_city_name", from);
		this.setAttr("arr_city_name", to);
		this.setAttr("loginame", loginame);
		this.setAttr("keywords", keywords);
		
		// 商圈
		String[] cityarea = getParaValues("cityarea");
		this.setCheckboxAttr("cityarea", cityarea);
		// 服务分类
		String[] subject = getParaValues("subject");
		this.setCheckboxAttr("subject", subject);
		// 默认排序
		String mrorder = getParaTrans("mrorder");
		// 销量排序
		String zxlorder = getParaTrans("zxlorder");
		// 价格排序
		String avgpriceorder = getParaTrans("avgpriceorder");
		// 好评排序
		String hpsorder = getParaTrans("hpsorder");
		
		this.setAttr("avgpriceorder", avgpriceorder==null?"ASC":avgpriceorder);

	}

	private void getCity() {
		AbCityarea city = this.getSessionAttr("currcity");
		this.setAttr("currcity", city);
		if (city != null && city.get("id") != null) {
			List<AbCityarea> listcity = AbCityarea.dao.find(
					"select * from ab_cityarea where p_id=?", city.get("id"));
			this.setAttr("listcity", listcity);
		}
	}

	private void getLogi() {
		List<AbSubject> list = AbSubject.dao.find(
				"select * from ab_subject where p_id=?", 1020);
		this.setAttr("logis", list);
	}

	private String trans(String params[]) {
		String res = "";
		if (params == null)
			return res;
		for (String str : params) {
			res += "," + str;
		}
		return res.substring(1);
	}

	private void setCheckboxAttr(String name, String[] params) {
		if (params == null) {
			this.setAttr(name, new String[] { "4od7dsk", "49ksjcids" });
		} else if (params.length == 1) {
			this.setAttr(name, new String[] { params[0], "49ksjcids" });
		} else {
			this.setAttr(name, params);
		}
		System.out.println("checkbox: " + trans(params));
	}

	/**
	 * 防止参数的sql注入
	 * 
	 * @param name
	 * @return
	 */
	private String getParaTrans(String name) {
		String param = this.getPara(name);
		if (param != null && param.trim().length() > 0) {
			return TransactSQLInjection(param);
		}
		return null;
	}

	/**
	 * 防止sql注入
	 * 
	 * @param sql
	 * @return
	 */
	private String TransactSQLInjection(String sql) {
		return sql.replaceAll(".*([';]+|(--)+).*", " ");
	}

	// 询价
	public void askPrice() {
		SysUser user = this.getSessionAttr("abuser");
		if (user != null) {
			user = SysUser.dao.findById(user.getStr(SysUser.ID));
			// 查询当前登陆人员
			setAttr("c_user_id", user.get("id"));
			// 查询省、市、区域
			try {
				AbCityarea city = AbCityarea.dao.findById(user.get("city_id"));
				setAttr("user_source_city_id", city.get("id"));
				setAttr("user_source_city_mc", city.get("mc"));
				AbCityarea area = AbCityarea.dao.findById(user.get("area_id"));
				setAttr("user_source_area_id", area.get("id"));
				setAttr("user_source_area_mc", area.get("mc"));
				AbCityarea province = AbCityarea.dao.findById(city.get("p_id"));
				setAttr("user_source_province_id", province.get("id"));
				setAttr("user_source_province_mc", province.get("mc"));
			} catch (Exception e) {
				setAttr("user_source_city_id", "");
				setAttr("user_source_city_mc", "");
				setAttr("user_source_area_id", "");
				setAttr("user_source_area_mc", "");
				setAttr("user_source_province_id", "");
				setAttr("user_source_province_mc", "");
			}
		} else {
			setAttr("c_user_id", "");
			// 跳转到登陆界面
			redirect("/ab/login");
			return;
		}
		render("/ab/logi/askprice.html");
	}

	// 查询下级城市或地区
	public void queryLowerLevelCity() {
		String p_id = getPara("p_id");
		renderJson(AbCityarea.queryLowerLevelArea(p_id));
	}

	// 验证是否存在物流公司或者熟车
	public void validate() {
		String ask_price_type = getPara("ask_price_type");
		if (ask_price_type.equals("1")) {
			// 物流公司
			renderJson(query_wl_size());
		} else {
			// 熟车
			renderJson(query_sc_size());
		}
	}

	private int query_sc_size() {
		String ismyfr = StringUtil.toStr(this.getPara("ismyfr"));
		String runcity = StringUtil.toStr(this.getPara("target_city"));
		SysUser user = (SysUser) this.getSessionAttr("abuser"); // 前台台用户
		List<AbFmcar> carlist = new ArrayList<AbFmcar>();
		String sql = "select a.*,b.* from ab_fmcar as a,(select * from(select c.id as shrid,c.loginid mobile1,d.jd,d.wd,c.sjzt zt,d.dqsj as dqsj,c.rate as rate from sys_user c left join ab_sj_position d on c.id= d.sjid and c.role_id='106' where 1 = 1";

		sql += " ORDER BY dqsj DESC) u WHERE 1 =1  GROUP BY shrid ) b where a.mobile = b.mobile1";

		if (ismyfr.length() > 0 && null != user) {
			sql += " and a.id in (select car_id from ab_fmcar_user where user_id = '"
					+ user.getStr("id") + "')";
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
		// 黑名单的车子除外
		if (null != user) {
			sql += " and a.mobile not in (select mobile from sys_mobile_blank where userid= '"
					+ user.getStr("id") + "')";
		}

		carlist = AbFmcar.dao.find(sql);
		return carlist.size();
	}

	private int query_wl_size() {
		String from = getParaTrans("source_city");
		String to = getParaTrans("target_city");
		// 商圈
		String[] cityarea = getParaValues("source_area");

		// 搜索物流企业
		String sql = getSelect();
		String sqlfromto = "select g.lid from ab_merchant_logline as g where 1=1";
		boolean bfromto = false;
		if (from != null && from.length() > 0) {
			sqlfromto += " and g.from='" + from + "'";
			bfromto = true;
		}
		if (to != null && to.length() > 0) {
			sqlfromto += " and g.to='" + to + "'";
			bfromto = true;
		}
		if (bfromto)
			sql += " and w.id in (" + sqlfromto + ") ";
		if (cityarea != null && cityarea.length > 0)
			sql += " and m.area_id IN (" + trans(cityarea) + ") ";
		List<Record> record = Db.find(sql);
		return record.size();
	}

	// 保存询价单
	public void saveAskPrice() {
		AbAskPrice aap = new AbAskPrice();
		aap.set("id", StringUtil.getUuid32());
		String ask_price_type = getPara("ask_price_type");
		aap.set("ask_price_type", ask_price_type);
		// 起始地省份
		String source_province = getPara("source_province");
		aap.set("source_province", source_province);
		// 起始地城市
		String source_city = getPara("source_city");
		aap.set("source_city", source_city);
		// 起始地地区
		String source_area = getPara("source_area");
		aap.set("source_area", source_area);
		// 目的地省份
		String target_province = getPara("target_province");
		aap.set("target_province", target_province);
		// 目的地城市
		String target_city = getPara("target_city");
		aap.set("target_city", target_city);
		// 目的地地区
		String target_area = getPara("target_area");
		aap.set("target_area", target_area);
		// 货物名称
		String goods_name = "";
		try {
			goods_name = new String(
					getPara("goods_name").getBytes("iso8859-1"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		aap.set("goods_name", goods_name);
		// 货物类型[1:重货 2:轻货]
		String goods_type = getPara("goods_type");
		aap.set("goods_type", goods_type);
		// 重量/体积
		String goods_size = getPara("goods_size");
		aap.set("goods_size", goods_size);
		// 用车时间
		String usecar_date = getPara("usecar_date");
		aap.set("usecar_date", usecar_date);
		// 备注
		String remark = "";
		try {
			remark = new String(getPara("remark").getBytes("iso8859-1"),
					"utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		aap.set("remark", remark);
		// 联系人
		String linkman = "";
		try {
			linkman = new String(getPara("linkman").getBytes("iso8859-1"),
					"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		aap.set("linkman", linkman);
		// 手机
		String mobile = getPara("mobile");
		aap.set("mobile", mobile);
		// 用户ID
		String user_id = getPara("user_id");
		aap.set("user_id", user_id);
		// 下单时间
		String askprice_date = DateUtil.getCurrentDate();
		aap.set("askprice_date", askprice_date);

		aap.save();

		// render("/ab/logi/map.html");
		redirect("/ab/logi/askPrice");
	}

	// 查询询价单
	public void queryaskprice() {
		String queryType = StringUtil.toStr(this.getPara("queryType"));
		SysUser abuser = this.getSessionAttr("abuser");
		String p_start_date = this.getPara("p_start_date");
		String p_end_date = this.getPara("p_end_date");

		StringBuffer sb = new StringBuffer(
				"select * from ab_ask_price where user_id = '"
						+ abuser.get(SysUser.ID) + "' ");

		if (queryType.equals("")) {
			// 点击的查询按钮
			if (!StringUtil.isNull(p_start_date)) {
				sb.append(" and askprice_date >= '" + p_start_date + "'");
			}
			if (!StringUtil.isNull(p_end_date)) {
				sb.append(" and askprice_date < '" + p_end_date + "'");
			}
		} else if (queryType.equals("day")) {
			sb.append(" and askprice_date >= '"
					+ DateUtil.getCurrentDate("yyyy-MM-dd") + " 00:00:00'");
			sb.append(" and askprice_date < '"
					+ DateUtil.getCurrentDate("yyyy-MM-dd") + " 59:59:59'");
		} else if (queryType.equals("week")) {
			sb.append(" and askprice_date >= '"
					+ DateUtil.format(DateUtil.getMondayOfWeek(), "yyyy-MM-dd")
					+ " 00:00:00'");
			sb.append(" and askprice_date < '"
					+ DateUtil.format(DateUtil.getSundayOfWeek(), "yyyy-MM-dd")
					+ " 59:59:59'");
		} else if (queryType.equals("month")) {
			sb.append(" and askprice_date >= '"
					+ DateUtil.getCurrentDate("yyyy-MM") + "-01 00:00:00'");
			sb.append(" and askprice_date < '"
					+ DateUtil.format(DateUtil.getLastDayOfMonth(),
							"yyyy-MM-dd") + " 59:59:59'");
		}

		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),
				14, sb.toString());

		Map<String, String> cities = new HashMap<String, String>();
		// 查询单位
		for (AbCityarea tmp : AbCityarea.dao.find("select * from ab_cityarea")) {
			cities.put(tmp.getStr(AbCityarea.ID), tmp.getStr(AbCityarea.MC));
		}

		this.keepPara();
		this.setAttr("listpage", listpage);
		this.setAttr("cities", cities);
		render("/ab/logi/listaskprice.html");
	}
}
