package com.zp.controller.admin;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.zp.entity.AbSubject;
import com.zp.entity.SysUser;
import com.zp.entity.SysUserGroup;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.interceptor.AccessAdminInterceptor;
import com.zp.tools.CityAttachAdminUtil;
import com.zp.tools.DateUtil;
import com.zp.tools.MD5;
import com.zp.tools.StringUtil;

public class AbRzController extends Controller {
	@Before(AccessAdminInterceptor.class)
	public void listsj_rz(){
		String mc  = StringUtil.toStr(this.getPara("mc"));
		String mobile  = StringUtil.toStr(this.getPara("mobile"));
		String sfrzzt  = StringUtil.toStr(this.getPara("sfrzzt"));
		String zhlx  = StringUtil.toStr(this.getPara("zhlx"));
		String sql = "select su.* from sys_user su "
				+ "left join sys_user_type sut on sut.userid = su.id "
				+ "where role_id='105' and sut.usertype='1019'";
		if(mc.length()>0){
			sql+=" and mc like '%"+mc+"%'";
		}
		if(mobile.length()>0){
			sql+=" and mobile like '%"+mobile+"%'";
		}
		
		
		
		SysUser u = this.getSessionAttr("user");
//		if(!"admin".equals(u.getStr("id"))){
//			sql += " and p_id='" + u.getStr("id") + "'";
//		}
//		if(u!=null&&"103".equals(u.get("role_id"))){
//			//sql+=" and (city_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("id")+"') or area_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("id")+"'))";
//		}else if("104".equals(u.get("role_id"))){//普通员工
//			sql+=" and (city_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("p_id")+"') or area_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("p_id")+"'))";
//		}
		if(u.getStr("id").length() > 0 && (!"104".equals(u.getStr("role_id")))){
			sql += " and (city_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + ")";
			sql += " or area_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + "))";
		}
		if("104".equals(u.getStr("role_id"))){
			sql += " and id in (select id from ab_merchant where subject_id in(" + CityAttachAdminUtil.getAuthSubjectIdsByUserId(u.getStr("id")) + "))";
		}
		if(sfrzzt.length()>0){
			sql+=" and sfrzzt = '"+sfrzzt+"'";
		}
		if(zhlx.length() > 0){
			sql += " and zhlx ='" + zhlx + "'";
		}
		
		sql+="order by tjsj desc";
		
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		this.keepPara();
		this.setAttr("listpage", listpage);
		this.setAttr("subject_list", AbSubject.dao.find("select * from ab_subject where p_id in('1019','1020')"));
		this.render("/admin/rz/listsj_rz.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void wllistsj_rz(){
		String mc  = StringUtil.toStr(this.getPara("mc"));
		String mobile  = StringUtil.toStr(this.getPara("mobile"));
		String sfrzzt  = StringUtil.toStr(this.getPara("sfrzzt"));
		String zhlx  = StringUtil.toStr(this.getPara("zhlx"));
		String sql = "select su.* from sys_user su "
				+ "left join sys_user_type sut on sut.userid = su.id "
				+ "where role_id='105' and sut.usertype='1020'";
		if(mc.length()>0){
			sql+=" and mc like '%"+mc+"%'";
		}
		if(mobile.length()>0){
			sql+=" and mobile like '%"+mobile+"%'";
		}
		
		
		
		SysUser u = this.getSessionAttr("user");
//		if(!"admin".equals(u.getStr("id"))){
//			sql += " and p_id='" + u.getStr("id") + "'";
//		}
//		if(u!=null&&"103".equals(u.get("role_id"))){
//			//sql+=" and (city_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("id")+"') or area_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("id")+"'))";
//		}else if("104".equals(u.get("role_id"))){//普通员工
//			sql+=" and (city_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("p_id")+"') or area_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("p_id")+"'))";
//		}
		if(u.getStr("id").length() > 0 && (!"104".equals(u.getStr("role_id")))){
			sql += " and (city_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + ")";
			sql += " or area_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + "))";
		}
		if("104".equals(u.getStr("role_id"))){
			sql += " and id in (select id from ab_merchant where subject_id in(" + CityAttachAdminUtil.getAuthSubjectIdsByUserId(u.getStr("id")) + "))";
		}
		if(sfrzzt.length()>0){
			sql+=" and sfrzzt = '"+sfrzzt+"'";
		}
		if(zhlx.length() > 0){
			sql += " and zhlx ='" + zhlx + "'";
		}
		
		sql+="order by tjsj desc";
		
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		this.keepPara();
		this.setAttr("listpage", listpage);
		this.setAttr("subject_list", AbSubject.dao.find("select * from ab_subject where p_id in('1019','1020')"));
		this.render("/admin/rz/listsj_rz.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void sjrzshtg(){
		boolean flag = false;
		String id = this.getPara("id");
		SysUser u = this.getSessionAttr("user");
		SysUser.dao.findById(id)
		.set("sfrzzt", "2")
		.set("shsj", DateUtil.getCurrentDate())
		.set("shrid", u.get("id"))
		.set("shrmc", u.get("mc"))
		.update();
		flag = true;
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void sjrzshbtg(){
		boolean flag = false;
		String id = this.getPara("id");
		SysUser u = this.getSessionAttr("user");
		SysUser.dao.findById(id)
		.set("sfrzzt", "3")
		.set("shsj", DateUtil.getCurrentDate())
		.set("shrid", u.get("id"))
		.set("shrmc", u.get("mc"))
		.update();
		flag = true;
		this.renderJson(flag);
	}
	
	
	@Before(AccessAdminInterceptor.class)
	public void qyrzshtg_pl(){
		boolean flag = false;
		String ids = this.getPara("ids");
		SysUser u = this.getSessionAttr("user");
		if(StringUtil.toStr(ids).length()>0){
			String[] str = ids.split(",");
			for (int i = 0; i < str.length; i++) {
				SysUser.dao.findById(str[i])
				.set("sfrzzt", "2")
				.set("shsj", DateUtil.getCurrentDate())
				.set("shrid", u.get("id"))
				.set("shrmc", u.get("mc"))
				.update();
			}
		}
		
		flag = true;
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void qyrzshbtg_pl(){
		boolean flag = false;
		String ids = this.getPara("ids");
		SysUser u = this.getSessionAttr("user");
		if(StringUtil.toStr(ids).length()>0){
			String[] str = ids.split(",");
			for (int i = 0; i < str.length; i++) {
				SysUser.dao.findById(str[i])
				.set("sfrzzt", "3")
				.set("shsj", DateUtil.getCurrentDate())
				.set("shrid", u.get("id"))
				.set("shrmc", u.get("mc"))
				.update();
			}
		}
		
		flag = true;
		this.renderJson(flag);
	}
	
	
	@Before(AccessAdminInterceptor.class)
	public void editywy(){
		String id = this.getPara(0);
		SysUser vo = new SysUser();
		if(StringUtil.toStr(id).length()>0){
			vo = SysUser.dao.findById(id);
		}
		this.setAttr("vo", vo);
		if(null != vo.getStr("groupid") && vo.getStr("groupid").length() > 0){
			SysUserGroup sug = SysUserGroup.dao.findById(vo.getStr("groupid"));
			this.setAttr("sug", sug);
		}
		//查询司机分组
		this.setAttr("subject_list", AbSubject.dao.find("select * from ab_subject where p_id='1017'"));
		render("/admin/rz/editywy.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void saveywy(){
		boolean flag = false;
		SysUser user = this.getModel(SysUser.class,"tb");
		user.set("mobile", user.getStr("loginid"));
//		SysUser puser = (SysUser)this.getRequest().getSession().getAttribute("user");
//		if(user.getStr("p_id")==null){
//			user.set("p_id", puser.getStr("id"));
//		}
		if(user!=null&&user.get("id")!=null){
			if(!this.getPara("oldpwd").equals(user.get("loginpwd"))){
				user.set("loginpwd", MD5.GetMD5Code(user.getStr("loginpwd")));
			}
			user.set("modify_date", DateUtil.getCurrentDate());
			user.update();
		}else{
			user.set("id", StringUtil.getRandString32());
			user.set("loginpwd", MD5.GetMD5Code(user.getStr("loginpwd")));
			user.set("create_date", DateUtil.getCurrentDate());
			user.save();
		}
		
		flag = true;
		this.renderJson(flag);
	}
	
	
	@Before(AccessAdminInterceptor.class)
	public void edit_hy_gr(){
		String id = this.getPara(0);
		SysUser vo = new SysUser();
		if(StringUtil.toStr(id).length()>0){
			vo = SysUser.dao.findById(id);
		}
		this.setAttr("vo", vo);
		render("/admin/rz/edit_hy_gr.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void edit_hy_qy(){
		String id = this.getPara(0);
		SysUser vo = new SysUser();
		if(StringUtil.toStr(id).length()>0){
			vo = SysUser.dao.findById(id);
		}
		this.setAttr("vo", vo);
		this.setAttr("subject_list", AbSubject.dao.find("select * from ab_subject where p_id='1018'"));
		render("/admin/rz/edit_hy_qy.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void showywy_jd(){
		String id = this.getPara(0);
	
		String kssj = StringUtil.toStr(this.getPara("kssj"));
		String jzsj = StringUtil.toStr(this.getPara("jzsj"));
		String currDate = StringUtil.toStr(this.getPara("currDate"));
		String lastDate = StringUtil.toStr(this.getPara("lastDate"));
		String queryType = StringUtil.toStr(this.getPara("queryType"));
		String ddzt = StringUtil.toStr(this.getPara("ddzt"));
		
		if(queryType.equals("")) {
			queryType = "dayQuery";
			setAttr("queryType",queryType);
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);    //得到前一天
		Date date = calendar.getTime();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String str_lastDate = df.format(date);
	
		SysUser vo = SysUser.dao.findById(id);
		String list_sql = "select ao.*,tc.style style,tc.min_price min_price,tc.max_price max_price,tc.pay_type from ab_order ao left join ab_tc_express_order tc on tc.sn=ao.sn where qdrid='"+id+"' ";
		String sql = "SELECT qdrid,qdrname,COUNT(id) cnt,SUM(ddzje) je,SUBSTR(qdsj,1,10) rq FROM ab_order WHERE qdrid='"+id+"' ";
		
		List<Record> list = null;
		if("day".equals(queryType)){
			// 日报表
			sql+=" and qdsj>='"+DateUtil.getCurrentDate("yyyy-MM-dd")+" 00:00:00'";
			sql+=" and qdsj<='"+DateUtil.getCurrentDate("yyyy-MM-dd")+" 59:59:59'";
			
			list_sql+=" and qdsj>='"+DateUtil.getCurrentDate("yyyy-MM-dd")+" 00:00:00'";
			list_sql+=" and qdsj<='"+DateUtil.getCurrentDate("yyyy-MM-dd")+" 59:59:59'";
		}else if("week".equals(queryType)){
			// 周报表
			sql+=" and qdsj>='"+DateUtil.format(DateUtil.getMondayOfWeek(),"yyyy-MM-dd")+" 00:00:00'";
			sql+=" and qdsj<='"+DateUtil.format(DateUtil.getSundayOfWeek(),"yyyy-MM-dd")+" 59:59:59'";
			
			list_sql+=" and qdsj>='"+DateUtil.format(DateUtil.getMondayOfWeek(),"yyyy-MM-dd")+" 00:00:00'";
			list_sql+=" and qdsj<='"+DateUtil.format(DateUtil.getSundayOfWeek(),"yyyy-MM-dd")+" 59:59:59'";
		}else if("month".equals(queryType)) {
			// 月报表
			sql+=" and qdsj>='"+DateUtil.getCurrentDate("yyyy-MM")+"-01 00:00:00'";
			sql+=" and qdsj<='"+DateUtil.format(DateUtil.getLastDayOfMonth(),"yyyy-MM-dd")+" 59:59:59'";
			
			list_sql+=" and qdsj>='"+DateUtil.getCurrentDate("yyyy-MM")+"-01 00:00:00'";
			list_sql+=" and qdsj<='"+DateUtil.format(DateUtil.getLastDayOfMonth(),"yyyy-MM-dd")+" 59:59:59'";
		}else if("dayQuery".equals(queryType)){
			if(kssj.length()>0){
				sql+=" and qdsj>='"+kssj+" 00:00:00'";
				
				list_sql+=" and qdsj>='"+kssj+" 00:00:00'";
			}
			if(jzsj.length()>0){
				sql+=" and qdsj<='"+jzsj+" 59:59:59'";
				
				list_sql+=" and qdsj<='"+jzsj+" 59:59:59'";
			}
		}else if("monthWeekQuery".equals(queryType)) {
			String query_year_begin = getPara("query_year_begin");
			String query_month_begin = getPara("query_month_begin");
			String query_year_end = getPara("query_year_end");
			String query_month_end = getPara("query_month_end");
			sql+=" and qdsj>='"+query_year_begin + "-" + query_month_begin +"-01 00:00:00'";
			sql+=" and qdsj<='"+query_year_end + "-" + query_month_end +"-31 59:59:59'";
			
			list_sql+=" and qdsj>='"+query_year_begin + "-" + query_month_begin +"-01 00:00:00'";
			list_sql+=" and qdsj<='"+query_year_end + "-" + query_month_end +"-31 59:59:59'";
		}
		if(!ddzt.equals("-1")) {
			sql += " and ddzt='" + ddzt + "' ";
			
			list_sql += " and ddzt='" + ddzt + "' ";
		}
		
		sql+="  GROUP BY SUBSTR(qdsj,1,10)";
		list = Db.find(sql);
		
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, list_sql);
		
		Record r = null;
		sql="SELECT COUNT(id) totleorder,IFNULL(SUM(ddzje),0) totalmoney FROM ab_order WHERE qdrid='"+id+"'";
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
		this.setAttr("list", list);
		this.setAttr("vo", vo);
		this.setAttr("r", r);
		this.setAttr("listpage", listpage);
		render("/admin/rz/showywy_jd.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void deleteywy(){
		boolean flag = false;
		String id = this.getPara("id");
		SysUser.dao.findById(id).delete();
		flag = true;
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void deleteywy_pl(){
		boolean flag = false;
		String ids = this.getPara("ids");
		Db.update("delete from sys_user where id in('"+ids.replace(",", "','")+"')");
		flag = true;
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void showmer_qy(){
		String id = this.getPara(0);
		SysUser vo = SysUser.dao.findById(id);
		this.setAttr("vo", vo);
		render("/admin/rz/showmer_qy.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void showmer_gr(){
		String id = this.getPara(0);
		SysUser vo = SysUser.dao.findById(id);
		this.setAttr("vo", vo);
		render("/admin/rz/showmer_gr.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void listywy_rz(){
		SysUser u = this.getSessionAttr("user");
		String mc  = StringUtil.toStr(this.getPara("mc"));
		String mobile  = StringUtil.toStr(this.getPara("mobile"));
		String groupid = StringUtil.toStr(this.getPara("groupid"));
		String zhlx  = StringUtil.toStr(this.getPara("zhlx"));
		String tb_city_id = StringUtil.toStr(this.getPara("tb_city_id"));
		String sfrzzt  = StringUtil.toStr(this.getPara("sfrzzt"));
		List<SysUser> admin_list = SysUser.dao.find("select * from sys_user where role_id in ('101','103')");
		if("103".equals(u.get("role_id"))){
			admin_list.removeAll(admin_list);
			admin_list.add(u);
			this.setAttr("admin_list", admin_list);
		}else{
			this.setAttr("admin_list", admin_list);
		}
		String admin_id = StringUtil.toStr(this.getPara("admin_id"));
		if(admin_id.length() == 0){
			admin_id = u.get("id");
		}
		this.setAttr("admin_id", admin_id);
		String sql = "select * from sys_user where role_id='106' ";
		if(mc.length()>0){
			sql+=" and mc like '%"+mc+"%'";
		}
		
		if(mobile.length()>0){
			sql+=" and mobile like '%"+mobile+"%'";
		}
		if(groupid.length() > 0){
			sql += " and groupid = '" + groupid + "'";
		}
		if(tb_city_id.length() > 0){
			sql += " and city_id = '" + tb_city_id + "'";
		}
		if(groupid.length() > 0){
			sql += " and groupid = '" + groupid + "'";
		}
		if(zhlx.length() > 0){
			sql += " and zhlx ='" + zhlx + "'";
		}
//		if(u!=null&&"103".equals(u.get("role_id"))){
//			sql+=" and (city_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("id")+"') or area_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("id")+"'))";
//		}else if("104".equals(u.get("role_id"))){//普通员工
//			sql+=" and (city_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("p_id")+"') or area_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("p_id")+"'))";
//		}
		String ds = StringUtil.toStr(this.getPara("ds"));
		if(ds.length() > 0){
			sfrzzt="1";
		}
		if(sfrzzt.length()>0){
			sql+=" and sfrzzt = '"+sfrzzt+"'";
		}
//		if(admin_id.length() > 0 && !admin_id.equals("admin")){
//			sql += " and  p_id='" + admin_id+"'";
//		}else{
//			if(!u.getStr("id").equals("admin")){
//				sql += " and  p_id='" + u.getStr("id")+"'";
//			}
//		}
		if(admin_id.length() > 0){
			sql += " and (city_id in (" + CityAttachAdminUtil.getCityByAdmin(admin_id) + ")";
			sql += " or area_id in (" + CityAttachAdminUtil.getCityByAdmin(admin_id) + "))";
		}
		sql+="order by tjsj desc";
		
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		//查询司机分组
		String sqlsug = "select * from sys_user_group";
		if(tb_city_id.length() > 0){
			sqlsug += " where cityid = '" + tb_city_id + "'";
		}
		List<SysUserGroup> suglist = SysUserGroup.dao.find(sqlsug);
		this.setAttr("suglist", suglist);
		this.setAttr("city_id", tb_city_id);
		this.keepPara();
		this.setAttr("listpage", listpage);
		this.setAttr("groupid", groupid);
		this.setAttr("subject_list", AbSubject.dao.find("select * from ab_subject where p_id in('1017')"));
		this.render("/admin/rz/listywy_rz.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void listhy_rz(){
		SysUser u = this.getSessionAttr("user");
		String mc  = StringUtil.toStr(this.getPara("mc"));
		String mobile  = StringUtil.toStr(this.getPara("mobile"));
		String zhlx  = StringUtil.toStr(this.getPara("zhlx"));
		String sfrzzt  = StringUtil.toStr(this.getPara("sfrzzt"));
		List<SysUser> admin_list = SysUser.dao.find("select * from sys_user where role_id in ('101','103')");
		if("103".equals(u.get("role_id"))){
			admin_list.removeAll(admin_list);
			admin_list.add(u);
			this.setAttr("admin_list", admin_list);
		}else{
			this.setAttr("admin_list", admin_list);
		}
		String admin_id = StringUtil.toStr(this.getPara("admin_id"));
		if(admin_id.length() == 0){
			admin_id = u.get("id");
		}
		this.setAttr("admin_id", admin_id);
		String sql = "select a.*,b.mc as subject_name from sys_user a left join ab_subject b on a.zhlx=b.id where a.role_id='107' ";
		if(mc.length()>0){
			sql+=" and a.mc like '%"+mc+"%'";
		}
		if(mobile.length()>0){
			sql+=" and a.mobile like '%"+mobile+"%'";
		}
		if(zhlx.length() > 0){
			sql += " and a.zhlx ='" + zhlx + "'";
		}
//		if(u!=null&&"103".equals(u.get("role_id"))){
//			sql+=" and (city_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("id")+"') or area_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("id")+"'))";
//		}else if("104".equals(u.get("role_id"))){//普通员工
//			sql+=" and (city_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("p_id")+"') or area_id in(SELECT id FROM ab_cityarea WHERE user_id='"+u.getStr("p_id")+"'))";
//		}
		String ds = StringUtil.toStr(this.getPara("ds"));
		if(ds.length() > 0){
			sfrzzt="1";
		}
		if(sfrzzt.length()>0){
			sql+=" and a.sfrzzt = '"+sfrzzt+"'";
		}
//		if(admin_id.length() > 0 && !admin_id.equals("admin")){
//			sql += " and  p_id='" + admin_id+"'";
//		}else{
//			if(!u.getStr("id").equals("admin")){
//				sql += " and  p_id='" + u.getStr("id")+"'";
//			}
//		}
		if(admin_id.length() > 0){
			sql += " and (a.city_id in (" + CityAttachAdminUtil.getCityByAdmin(admin_id) + ")";
			sql += " or a.area_id in (" + CityAttachAdminUtil.getCityByAdmin(admin_id) + "))";
		}
		sql+="order by a.tjsj desc";
		
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		this.keepPara();
		this.setAttr("listpage", listpage);
		this.setAttr("subject_list", AbSubject.dao.find("select * from ab_subject where p_id='1018'"));
		this.render("/admin/rz/listhy_rz.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void ywyrzshtg(){
		boolean flag = false;
		String id = this.getPara("id");
		SysUser u = this.getSessionAttr("user");
		SysUser.dao.findById(id)
		.set("sfrzzt", "2")
		.set("shsj", DateUtil.getCurrentDate())
		.set("shrid", u.get("id"))
		.set("shrmc", u.get("mc"))
		.update();
		//TODO 添加一条认证通过消息到message数据库
		flag = true;
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void ywyrzshtg_pl(){
		boolean flag = false;
		String ids = this.getPara("ids");
		SysUser u = this.getSessionAttr("user");
		if(StringUtil.toStr(ids).length()>0){
			String[] str = ids.split(",");
			for (int i = 0; i < str.length; i++) {
				SysUser.dao.findById(str[i])
				.set("sfrzzt", "2")
				.set("shsj", DateUtil.getCurrentDate())
				.set("shrid", u.get("id"))
				.set("shrmc", u.get("mc"))
				.update();
			}
		}
		
		flag = true;
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void ywyrzshbtg_pl(){
		boolean flag = false;
		String ids = this.getPara("ids");
		SysUser u = this.getSessionAttr("user");
		if(StringUtil.toStr(ids).length()>0){
			String[] str = ids.split(",");
			for (int i = 0; i < str.length; i++) {
				SysUser.dao.findById(str[i])
				.set("sfrzzt", "3")
				.set("shsj", DateUtil.getCurrentDate())
				.set("shrid", u.get("id"))
				.set("shrmc", u.get("mc"))
				.update();
			}
		}
		
		flag = true;
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void ywyrzshbtg(){
		boolean flag = false;
		String id = this.getPara("id");
		SysUser u = this.getSessionAttr("user");
		SysUser.dao.findById(id)
		.set("sfrzzt", "3")
		.set("shsj", DateUtil.getCurrentDate())
		.set("shrid", u.get("id"))
		.set("shrmc", u.get("mc"))
		.update();
		//TODO 添加一条认证不通过的消息到数据库message
		flag = true;
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void saveywyyj(){
		boolean flag = false;
		String id = this.getPara("id");
		String yj = this.getPara("yj");
		SysUser.dao.findById(id)
		.set("per", BigDecimal.valueOf(Double.parseDouble(yj)))
		.update();
		flag = true;
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void showywy_sj(){
		String id = this.getPara(0);
		SysUser vo = SysUser.dao.findById(id);
		this.setAttr("vo", vo);
		render("/admin/rz/showywy_sj.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void showywy_gr(){
		String id = this.getPara(0);
		SysUser vo = SysUser.dao.findById(id);
		this.setAttr("vo", vo);
		render("/admin/rz/showywy_gr.html");
	}
	
	
	@Before(AccessAdminInterceptor.class)
	public void show_hy_qy(){
		String id = this.getPara(0);
		SysUser vo = SysUser.dao.findById(id);
		this.setAttr("vo", vo);
		render("/admin/rz/show_hy_qy.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void show_hy_gr(){
		String id = this.getPara(0);
		SysUser vo = SysUser.dao.findById(id);
		this.setAttr("vo", vo);
		render("/admin/rz/show_hy_gr.html");
	}
}
