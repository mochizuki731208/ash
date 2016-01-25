package com.zp.controller.ab;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.zp.entity.AbFmcar;
import com.zp.entity.AbFmcarCity;
import com.zp.entity.SysUser;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.interceptor.AccessAbInterceptor;
import com.zp.tools.DateUtil;
import com.zp.tools.StringUtil;

public class AbYwyController extends Controller{
	@Before(AccessAbInterceptor.class)
	public void listorder(){
		SysUser abuser = this.getSessionAttr("abuser");
		String sql = "select * from ab_order where xdrid='"+abuser.getStr("id")+"' order by xdsj desc";
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		this.setAttr("listpage", listpage);
		this.render("/ab/user/listorder.html");
	}
	@Before(AccessAbInterceptor.class)
	public void list_order_cxtj(){
		SysUser abuser = this.getSessionAttr("abuser");
		String kssj = StringUtil.toStr(this.getPara("kssj"));
		String jzsj = StringUtil.toStr(this.getPara("jzsj"));
		String ddzt = StringUtil.toStr(this.getPara("ddzt"));
		String queryType = StringUtil.toStr(this.getPara("queryType"));
//		String lastDate = StringUtil.toStr(this.getPara("lastDate"));
		if(queryType.equals("")) {
			queryType = "dayQuery";
			setAttr("queryType",queryType);
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);    //得到前一天
		Date date = calendar.getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String str_lastDate = df.format(date);
		
		String sql = "select ao.*,tc.style style,tc.min_price min_price,tc.max_price max_price,tc.pay_type from ab_order ao left join ab_tc_express_order tc on tc.sn=ao.sn where qdrid='"+abuser.getStr("id")+"' ";
		if("day".equals(queryType)){
			// 日报表
			sql+=" and qdsj>='"+DateUtil.getCurrentDate("yyyy-MM-dd")+" 00:00:00'";
			sql+=" and qdsj<='"+DateUtil.getCurrentDate("yyyy-MM-dd")+" 59:59:59'";
		}else if("week".equals(queryType)){
			// 周报表
			sql+=" and qdsj>='"+DateUtil.format(DateUtil.getMondayOfWeek(),"yyyy-MM-dd")+" 00:00:00'";
			sql+=" and qdsj<='"+DateUtil.format(DateUtil.getSundayOfWeek(),"yyyy-MM-dd")+" 59:59:59'";
		}else if("month".equals(queryType)) {
			// 月报表
			sql+=" and qdsj>='"+DateUtil.getCurrentDate("yyyy-MM")+"-01 00:00:00'";
			sql+=" and qdsj<='"+DateUtil.format(DateUtil.getLastDayOfMonth(),"yyyy-MM-dd")+" 59:59:59'";
		}else if("dayQuery".equals(queryType)){
			if(kssj.length()>0){
				sql+=" and qdsj>='"+kssj+" 00:00:00'";
			}
			if(jzsj.length()>0){
				sql+=" and qdsj<='"+jzsj+" 59:59:59'";
			}
		}else if("monthWeekQuery".equals(queryType)) {
			String query_year_begin = getPara("query_year_begin");
			String query_month_begin = getPara("query_month_begin");
			String query_year_end = getPara("query_year_end");
			String query_month_end = getPara("query_month_end");
			sql+=" and qdsj>='"+query_year_begin + "-" + query_month_begin +"-01 00:00:00'";
			sql+=" and qdsj<='"+query_year_end + "-" + query_month_end +"-31 59:59:59'";
		}
		if(!ddzt.equals("-1") && !ddzt.equals("")) {
			sql += " and ddzt='" + ddzt + "' ";
		}
		sql+="  order by xdsj desc";
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		
		
		sql = "SELECT qdrid,qdrname,COUNT(id) cnt,SUM(ddzje) je,SUBSTR(qdsj,1,10) rq FROM ab_order WHERE qdrid='"+abuser.getStr("id")+"' ";
		
		List<Record> listtj = null;
		if("day".equals(queryType)){
			// 日报表
			sql+=" and qdsj>='"+DateUtil.getCurrentDate("yyyy-MM-dd")+" 00:00:00'";
			sql+=" and qdsj<='"+DateUtil.getCurrentDate("yyyy-MM-dd")+" 59:59:59'";
		}else if("week".equals(queryType)){
			// 周报表
			sql+=" and qdsj>='"+DateUtil.format(DateUtil.getMondayOfWeek(),"yyyy-MM-dd")+" 00:00:00'";
			sql+=" and qdsj<='"+DateUtil.format(DateUtil.getSundayOfWeek(),"yyyy-MM-dd")+" 59:59:59'";
		}else if("month".equals(queryType)) {
			// 月报表
			sql+=" and qdsj>='"+DateUtil.getCurrentDate("yyyy-MM")+"-01 00:00:00'";
			sql+=" and qdsj<='"+DateUtil.format(DateUtil.getLastDayOfMonth(),"yyyy-MM-dd")+" 59:59:59'";
		}else if("dayQuery".equals(queryType)){
			if(kssj.length()>0){
				sql+=" and qdsj>='"+kssj+" 00:00:00'";
			}
			if(jzsj.length()>0){
				sql+=" and qdsj<='"+jzsj+" 59:59:59'";
			}
		}else if("monthWeekQuery".equals(queryType)) {
			String query_year_begin = getPara("query_year_begin");
			String query_month_begin = getPara("query_month_begin");
			String query_year_end = getPara("query_year_end");
			String query_month_end = getPara("query_month_end");
			sql+=" and qdsj>='"+query_year_begin + "-" + query_month_begin +"-01 00:00:00'";
			sql+=" and qdsj<='"+query_year_end + "-" + query_month_end +"-31 59:59:59'";
		}
		if(!ddzt.equals("-1") && !ddzt.equals("")) {
			sql += " and ddzt='" + ddzt + "' ";
		}
		sql+="  GROUP BY SUBSTR(qdsj,1,10)";
		listtj = Db.find(sql);
		
		
		Record r = null;
		sql="SELECT COUNT(id) totleorder,IFNULL(SUM(ddzje),0) totalmoney FROM ab_order WHERE qdrid='"+abuser.getStr("id")+"'";
		if("day".equals(queryType)){
			// 日报表
			sql+=" and qdsj>='"+DateUtil.getCurrentDate("yyyy-MM-dd")+" 00:00:00'";
			sql+=" and qdsj<='"+DateUtil.getCurrentDate("yyyy-MM-dd")+" 59:59:59'";
		}else if("week".equals(queryType)){
			// 周报表
			sql+=" and qdsj>='"+DateUtil.format(DateUtil.getMondayOfWeek(),"yyyy-MM-dd")+" 00:00:00'";
			sql+=" and qdsj<='"+DateUtil.format(DateUtil.getSundayOfWeek(),"yyyy-MM-dd")+" 59:59:59'";
		}else if("month".equals(queryType)) {
			// 月报表
			sql+=" and qdsj>='"+DateUtil.getCurrentDate("yyyy-MM")+"-01 00:00:00'";
			sql+=" and qdsj<='"+DateUtil.format(DateUtil.getLastDayOfMonth(),"yyyy-MM-dd")+" 59:59:59'";
		}else if("dayQuery".equals(queryType)){
			if(kssj.length()>0){
				sql+=" and qdsj>='"+kssj+" 00:00:00'";
			}
			if(jzsj.length()>0){
				sql+=" and qdsj<='"+jzsj+" 59:59:59'";
			}
		}else if("monthWeekQuery".equals(queryType)) {
			String query_year_begin = getPara("query_year_begin");
			String query_month_begin = getPara("query_month_begin");
			String query_year_end = getPara("query_year_end");
			String query_month_end = getPara("query_month_end");
			sql+=" and qdsj>='"+query_year_begin + "-" + query_month_begin +"-01 00:00:00'";
			sql+=" and qdsj<='"+query_year_end + "-" + query_month_end +"-31 59:59:59'";
		}
		if(!ddzt.equals("-1")) {
			sql += " and ddzt='" + ddzt + "' ";
		}
		r = Db.findFirst(sql);
		this.keepPara();
		this.setAttr("r", r);
		this.setAttr("listtj", listtj);
		this.setAttr("listpage", listpage);
		this.render("/ab/ywy/list_order_cxtj.html");
	}

	@Before(AccessAbInterceptor.class)
	public void saverz(){
		boolean flag = false;
		SysUser user = this.getModel(SysUser.class,"tb");
		SysUser loginUser = this.getSessionAttr("abuser");
		String name=this.getPara("abFmcar_driver");
		user.set("mc", name);
		user.set("tjsj", DateUtil.getCurrentDate());
		user.set("sfrzzt", "1");
		user.set(SysUser.ID, loginUser.get(SysUser.ID));
		user.update();
		
		//保存车辆信息
		AbFmcar car=AbFmcar.dao.findByMobile(getPara("abFmcar_mobile"));
		boolean update=false;
		if(car==null){
			car=new AbFmcar();
			car.set("id", StringUtil.getRandString32());
			update=false;
		}else update=true;
		car.set("driver", name);
		car.set("car_no", getPara("abFmcar_car_no"));
		car.set("mobile",getPara("abFmcar_mobile") );
		
		car.set("length",getPara("abFmcar_length") );
		car.set("type",getPara("abFmcar_type") );
		car.set("vv",getPara("abFmcar_vv") );
		if(update){
			car.update();
		}else car.save();
		
		//保存常跑城市
		AbFmcarCity.dao.delByCarId(car.getStr("id"));
		String citynames=getPara("runcity");
		String[] citys=citynames.split(",");
		AbFmcarCity cc=new AbFmcarCity();
		for(String n:citys){
			cc.set("city_name", n);
			cc.set("car_id", car.get("id"));
			cc.set("id", StringUtil.getRandString32());
			cc.save();
		}
		flag = true;
		renderJson(flag);
	}
	

}
