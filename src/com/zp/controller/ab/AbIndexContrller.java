package com.zp.controller.ab;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.zp.config.CaptchaRender;
import com.zp.entity.AbAppraise;
import com.zp.entity.AbCityarea;
import com.zp.entity.AbFmcar;
import com.zp.entity.AbFmcarGroup;
import com.zp.entity.AbIntegralScore;
import com.zp.entity.AbMerchant;
import com.zp.entity.AbMerchantComment;
import com.zp.entity.AbMerchantImg;
import com.zp.entity.AbMerchantLeave;
import com.zp.entity.AbMerchantProduct;
import com.zp.entity.AbOrder;
import com.zp.entity.AbProductImg;
import com.zp.entity.CmsContent;
import com.zp.entity.SysConfig;
import com.zp.entity.SysUser;
import com.zp.entity.SysUserZFPwd;
import com.zp.entity.Message;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.interceptor.AccessAbInterceptor;
import com.zp.tools.CommonProcess;
import com.zp.tools.CommonSQL;
import com.zp.tools.CommonStaticData;
import com.zp.tools.DateUtil;
import com.zp.tools.MD5;
import com.zp.tools.SmsMessage;
import com.zp.tools.StringUtil;
import com.zp.tools.mail.SimpleMailSender;
import com.zp.tools.mail.TextMail;

public class AbIndexContrller extends Controller {
	public void index(){
		//获取分类数据
		if(this.getSessionAttr("currcity")==null){
			this.setSessionAttr("currcity", AbCityarea.dao.findById("119101"));
		}
		
		AbCityarea city = this.getSessionAttr("currcity");
		String sql = "select * from ab_subject where id in(select subject_id from ab_cityarea_subject s where s.city_id= ?) and p_id in(select id from ab_subject where mc in('联盟商家')) and is_display='1' order by seq_num";
		List<Record> list_subject = Db.find(sql,city.getStr("id"));
		int day = DateUtil.dayForWeek(DateUtil.getCurrentDate());
		String currtime = DateUtil.getCurrentDate("HH:mm:ss");
		List<Record> list_tmp = null;
		//添加子分类
		if(list_subject!=null&&list_subject.size()>0){
			for (int i = 0; i < list_subject.size(); i++) {
				sql = "select * from ab_subject where id in(select s.subject_id from ab_cityarea_subject s where s.city_id= ?) and p_id=? and is_display='1' order by seq_num";
				list_tmp = Db.find(sql,city.getStr("id"),list_subject.get(i).get("id"));
				list_subject.get(i).set("list_sub", list_tmp);
				
				//获取商品信息列表
				sql = "SELECT p.*,m.id merId, m.rate, m.credit, m.avghour,m.avgprice,m.avgptf,uf_getmerworktime("+day+",'"+currtime+"',m.id) iswork,m.chk_fgzrxd FROM ab_merchant_product p,ab_merchant m where p.sysfxs='1' and p.sfsj='1' and m.is_recommend='1' and m.tjkssj<='"+DateUtil.getCurrentDate()+"' and m.tjjzsj>='"+DateUtil.getCurrentDate()+"' and m.id=p.mid and p.subject_id=? and p.city_id=?";
				list_tmp = Db.find(sql,list_subject.get(i).get("id"),city.getStr("id"));
				
				//获取商户评价书
				if(list_tmp!=null&&list_tmp.size()>0){
					for (int j = 0; j < list_tmp.size(); j++) {
						sql = "select count(*) appcnt from ab_appraise where mer_id = ?";
						Record r = Db.findFirst(sql,list_tmp.get(j).getStr("merId"));
						list_tmp.get(j).set("appraise_count", r.getLong("appcnt"));
					}
				}
				list_subject.get(i).set("list_product", list_tmp);
			}
		}
		
		SysUser user = this.getSessionAttr("abuser");
		
		if(user!=null&&user.get("id")!=null){
			List<Record> list_sj_tmp = Db.find("select * from ab_merchant where id in(SELECT MID FROM ab_merchant_zjfw WHERE userid=? order by cjsj)",user.get("id"));
			this.setAttr("list_sj_tmp", list_sj_tmp);
		}
		
		SysConfig config = SysConfig.dao.findById("06");
		this.setAttr("config", config);
		this.setAttr("list_subject", list_subject);
		this.setAttr("currcity", city);
		render("/ab/index.html");
//		render("/ab/info/jfsc.html");
	}
	
	public void jfsc(){
		//获取分类数据
		if(this.getSessionAttr("currcity")==null){
			this.setSessionAttr("currcity", AbCityarea.dao.findById("119101"));
		}
		this.setAttr("currcity", this.getSessionAttr("currcity"));
		//获取新闻信息
		List<Record> list_news = Db.find("select * from cms_content where lbid=9 order by release_date desc limit 0,8"); 
		List<Record> list_hd = Db.find("select * from cms_content where lbid=10 order by release_date desc limit 0,8"); 
		SysConfig config = SysConfig.dao.findById("06");
		this.setAttr("config", config);
				
		this.setAttr("list_news", list_news);
		this.setAttr("list_hd", list_hd);
		render("/ab/info/jfsc.html");
	}
	
	public void jm(){
		render("/ab/jm/index.html");
	}
	
	public void yhxs(){
		String lbid = this.getPara(0);
		String sql= "";
		if(StringUtil.toStr(lbid).length()<1){
			sql= "select * from cms_content where lbid=11 or lbid in(select  id from cms_nrfl where sjfl=11) order by release_date desc";
		}else{
			sql= "select * from cms_content where lbid="+lbid+" order by release_date desc";
		}
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		List<Record> list_fl = Db.find("select * from cms_nrfl where sjfl=11");
		
		this.setAttr("listpage", listpage);
		this.setAttr("list_fl", list_fl);
		render("/ab/zsjm/online.html");
	}
	
	public void zsjm(){
		render("/ab/zsjm/index.html");
	}
	public void online(){
		render("/ab/zsjm/online.html");
	}
	
	public void logochange(){
		SysUser u = this.getSessionAttr("abuser");
		u = SysUser.dao.findById(u.get("id"));
		System.out.println("savelogo:"+u.getStr("id"));
		this.setAttr("vo",u);
		render("/ab/logochange.html");
	}
	
	public void savelogochange(){
		boolean flag = false;
		SysUser u = this.getModel(SysUser.class,"tb");
		u.update();
		u = SysUser.dao.findById(u.get("id"));
		this.setSessionAttr("abuser", u);
		flag = true;
		renderJson(flag);
	}
	/**
	 * 选择城市
	 */
	public void selcity(){
		List<Record> list_city = Db.find("select * from ab_cityarea where p_id='ROOT' order by seq_num");
		List<Record> list_sub = null;
		
		if(list_city!=null&&list_city.size()>0){
			for (int i = 0; i < list_city.size(); i++) {
				list_sub = Db.find("select * from ab_cityarea where p_id='"+list_city.get(i).getStr("id")+"' order by seq_num");
				list_city.get(i).set("listsub", list_sub);
			}
		}
		
		//获取当前城市下的商圈
		AbCityarea city = this.getSessionAttr("currcity");
		List<Record> listcurrent = Db.find("select * from ab_cityarea where p_id='"+city.getStr("id")+"' order by seq_num");
		this.setAttr("listcurrent", listcurrent);
		this.setAttr("list_city", list_city);
		render("/ab/city.html");
	}
	
	public void setcurrcity(){
		boolean flag = false;
		String cityid = StringUtil.toStr(this.getPara("cityid"));
		AbCityarea city = AbCityarea.dao.findById(cityid);
		this.setSessionAttr("currcity", city);
		System.out.println("currcity: " + city);
		flag = true;
		renderJson(flag);
	}
	
	/**
	 * 商家信息列表
	 */
	public void listitemquery(){
		AbCityarea city = this.getSessionAttr("currcity");
		String keyword = StringUtil.toStr(this.getPara("keyword")); //大分类
		String findtype = StringUtil.toStr(this.getPara("findtype")); //大分类
		
		String sql = "";
		if(StringUtil.toStr(findtype).length()<1){
			sql = "select count(id) from ab_merchant where mc like '%"+StringEscapeUtils.escapeSql(keyword)+"%' and city_id='"+StringEscapeUtils.escapeSql(city.getStr("id"))+"'";
			if(Db.queryLong(sql).longValue()>0){
				findtype = "";
			}
		}
		
		
		if(StringUtil.toStr(findtype).length()<1){
			sql = "select count(id) from ab_merchant_product where mc like '%"+StringEscapeUtils.escapeSql(keyword)+"%' and city_id='"+StringEscapeUtils.escapeSql(city.getStr("id"))+"'";
			if(Db.queryLong(sql).longValue()>0){
				findtype = "fd";
			}
		}
		
		
		if("fd".equals(findtype)){
			sql = "select *,(SELECT COUNT(id) FROM ab_order_item p WHERE p.itemid=t.id) cnt from ab_merchant_product t where zt ='1' ";
			if(city!=null){
				sql+=" and city_id='"+StringEscapeUtils.escapeSql(city.getStr("id"))+"'";
			}
			if(keyword.length()>0){
				sql+=" and mc like '%"+StringEscapeUtils.escapeSql(keyword)+"%'";
			}
		}else{
			sql = "select *,(SELECT COUNT(id) FROM ab_order o WHERE o.mid=t.id) cnt from ab_merchant t where check_status='1' ";
			if(city!=null){
				sql+=" and city_id='"+StringEscapeUtils.escapeSql(city.getStr("id"))+"'";
			}
			//商家名称
			if(keyword.length()>0){
				sql+=" and mc like '%"+StringEscapeUtils.escapeSql(keyword)+"%'";
			}
		}
		
		
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		this.keepPara();
		this.setAttr("listpage", listpage);
		this.setAttr("findtype", findtype);
		this.setAttr("currcity", city);
		render("/ab/listitemquery.html");
	}
	/**
	 * 商家信息列表
	 */
	public void listitem(){
		AbCityarea city = this.getSessionAttr("currcity");
		this.setAttr("currcity", city);
		String city_id = StringUtil.toStr(this.getPara(0)); //城市
		String subject_id = StringUtil.toStr(this.getPara(1)); //大分类
		
		String area_ids  = StringUtil.join(this.getParaValues("area_id"), ",","'", "");//小商圈
		String[] sub_ids  = this.getParaValues("sub_id");//分类
		String str_sub_ids  = StringUtil.join(this.getParaValues("sub_id"), ",","'", "");//分类
		String orderby = this.getPara("orderby");
		//输入框条件
		String mname = StringUtil.toStr(this.getPara("mname"));
		String yyzt = StringUtil.toStr(this.getPara("yyzt"));
		
		int day = DateUtil.dayForWeek(DateUtil.getCurrentDate());
		String currtime = DateUtil.getCurrentDate("HH:mm:ss");
		
		String sql = "select *,uf_getmerworktime("+day+",'"+currtime+"',t.id) iswork from ab_merchant t where check_status='1' ";

		if(yyzt.length()>0){
			sql+=" and uf_getmerworktime("+day+",'"+currtime+"',t.id)='"+yyzt+"'";
		}
		
		if(city_id.length()>0){
			sql+=" and city_id='"+StringEscapeUtils.escapeSql(city_id)+"'";
		}
		if(subject_id.length()>0){
			sql+=" and subject_id='"+StringEscapeUtils.escapeSql(subject_id)+"'";
		}
		if(area_ids.length()>0){
			sql+=" and area_id in ("+area_ids+")";
		}else{
			AbCityarea area = this.getSessionAttr("currarea");
			if(area!=null){
				area_ids = area.getStr("id");
			}
		}
		
		//查询小分类
		if(sub_ids!=null && sub_ids.length>0){
			String substr="";
			for (int i = 0; i < sub_ids.length; i++) {
				if(i==0){
					substr+=("(\'"+sub_ids[i]+"\' in(select sub_id from ab_merchant_product where mid=t.id)");
				}else{
					substr+=(" or \'"+sub_ids[i]+"\' in(select sub_id from ab_merchant_product where mid=t.id)");
				}
				
				if(i==sub_ids.length-1){
					substr+=")";
				}
			}
			sql+= " and "+substr;
		}
		
		
		//商家名称
		if(mname.length()>0){
			sql+=" and mc like '%"+StringEscapeUtils.escapeSql(mname)+"%'";
		}
		//营业状态
		//if(yyzt.length()>0){
		//	sql+=" and business_status ="+yyzt;
		//}
		//排序
		if(StringUtil.toStr(orderby).length()>0){
			sql+=" order by "+orderby;
		}else{
			sql+=" order by avgprice";
		}
		
		
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		CommonProcess.setCreditType(listpage);
		
		/**
		if(listpage.getList()!=null&&listpage.getList().size()>0){
			for (int j = 0; j < listpage.getList().size(); j++) {
				//获取当前日期是否工作中
				sql = "SELECT COUNT(id) cnt FROM ab_merchant_yysj WHERE xq="+day+" AND sbsj<='"+currtime+"' AND xbsj>='"+currtime+"' AND MID='"+listpage.getList().get(j).getStr("id")+"'";
				if(Db.queryLong(sql).longValue()>0){
					listpage.getList().get(j).set("iswork", true);
				}else{
					listpage.getList().get(j).set("iswork", false);
				}
				
				//计算平均消费费用
				//sql = "SELECT IFNULL(AVG(price),0) avgprice FROM ab_order_item WHERE MID='"+listpage.getList().get(j).getStr("id")+"'";
				//r = Db.findFirst(sql);
				//listpage.getList().get(j).set("avgprice", r.getBigDecimal("avgprice"));
				//计算平均跑腿费和用时时长
				//sql="SELECT IFNULL(AVG(TIMESTAMPDIFF(HOUR,xdsj,shsj)),0) avghour,IFNULL(AVG(ptf),0) avgptf FROM ab_order WHERE ddzt='9' AND MID='"+listpage.getList().get(j).getStr("id")+"'";
				//r = Db.findFirst(sql);
				//listpage.getList().get(j).set("avghour", r.getBigDecimal("avghour"));
				//listpage.getList().get(j).set("avgptf", r.getBigDecimal("avgptf"));
			}
		}
		*/
		
		sql = "select * from ab_subject" +
				" where p_id = ? and id in(select subject_id from ab_cityarea_subject cs where cs.city_id = ?) and is_display = '1' and is_enable = '1'";
		List<Record> list_xfl = Db.find(sql,subject_id,city_id);
		
		this.setAttr("list_xfl", list_xfl);
		this.setAttr("listpage", listpage);
		this.setAttr("area_ids", area_ids);
		this.setAttr("sub_ids", str_sub_ids);
		this.setAttr("city_id", city_id);
		this.setAttr("subject_id", subject_id);
		this.setAttr("mname", mname);
		this.setAttr("yyzt", yyzt);
		this.setAttr("orderby", orderby);
		render("/ab/listitem.html");
	}
	
	
	/**
	 * 获取详细信息
	 */
	public void merinfo(){
		String id = this.getPara(0);
		AbMerchant vo = AbMerchant.dao.findById(id);
		//获取所有商家图片
		List<AbMerchantImg> list_img = AbMerchantImg.dao.find("select * from ab_merchant_img where mid=? order by seq_num ",id);
		//获取所有产品信息
		List<AbMerchantProduct> list_pro = AbMerchantProduct.dao.find("select * from ab_merchant_product where mid=? order by seq_num ",id);
		//获取所有评论
		List<AbMerchantComment> list_comm = AbMerchantComment.dao.find("select * from ab_merchant_comment order by createtime desc");
		
		//设置当前用户信息
		SysUser user = this.getSessionAttr("abuser");
		if(user!=null&&user.get("id")!=null){
			this.setAttr("userid", user.get("id"));
		}
		this.setAttr("vo", vo);
		this.setAttr("list_img", list_img);
		this.setAttr("list_pro", list_pro);
		this.setAttr("list_comm", list_comm);
		render("/ab/merinfo.html");
	}
	
	/**
	 * 获取详细信息
	 */
	public void detail(){
		AbCityarea city = this.getSessionAttr("currcity");
		this.setAttr("currcity", city);
		String id = this.getPara(0);
		String proname = StringUtil.toStr(this.getPara("proname")); //大分类

		//是否允许留言
		String leaveFlag = "0";
		Record vo = Db.findById("ab_merchant",id);
		//获取所有商家图片
		List<AbMerchantImg> list_img = AbMerchantImg.dao.find("select * from ab_merchant_img where mid=? order by seq_num ",id);
		//如果当前登录人已经存在，则从当前登录人信息中找出购物车中已选择商品的数量
		SysUser user = this.getSessionAttr("abuser");

		// 获取所有评论
		// List<AbMerchantComment> list_comm =
		// AbMerchantComment.dao.find("select * from ab_merchant_comment order by createtime desc");
		List<Record> list_comm = Db.find(CommonSQL.getAppraiseByMid(id));
		List<AbAppraise> aprsone = AbAppraise.dao.find(CommonSQL.getAppraiseTypeByMid(id, "1"));
		List<AbAppraise> aprstwo = AbAppraise.dao.find(CommonSQL.getAppraiseTypeByMid(id, "2"));
		List<AbAppraise> aprsthree = AbAppraise.dao.find(CommonSQL.getAppraiseTypeByMid(id, "3"));
			for (int i = 0;i<list_comm.size();i++) {
				List<Record> imgs = Db.find(CommonSQL.getAppraiseImgByAppId(list_comm.get(i).getStr(AbAppraise.ID)));
				list_comm.get(i).set("appsImgs", imgs);
				if (imgs != null && imgs.size() > 0) {
					CommonProcess.createImageFile(imgs);
				}
			}
		// 获取所有留言
		List<Record> list_leave = Db.find(CommonSQL.getLeaveByMid(id));
		for (int i = 0;i<list_leave.size();i++) {
			String leaveId = list_leave.get(i).getStr(AbMerchantLeave.ID);
			List<Record> amrs = Db.find("select * from ab_merchant_reply where leave_id = ?",leaveId);
			list_leave.get(i).set("replys", amrs);
		}
		if (user != null && user.get("id") != null) {
			this.setAttr("userid", user.get("id"));
			// 判断登陆用户类型
			// 商户
			List<AbOrder> list_order = AbOrder.dao.find("select * from ab_order where ddzt in ('2','3') and mid=? and xdrid=?",id,user.get("id"));
			if ("105".equals(user.getStr(SysUser.ROLE_ID))) {
				leaveFlag = "2";
			} else if ("107".equals(user.getStr(SysUser.ROLE_ID))&& list_order.size() > 0) {
				// 设置是否能发表留言
				leaveFlag = "1";
			}
		}

		int day = DateUtil.dayForWeek(DateUtil.getCurrentDate());
		String currtime = DateUtil.getCurrentDate("HH:mm:ss");
		//获取当前日期是否工作中
		String	sql = "SELECT uf_getmerworktime(?,?,?)";
		vo.set("iswork", Db.queryStr(sql,day,currtime,id));
		
		//查看分类
		List<Record> list_sub = Db.find("SELECT DISTINCT thr_id id,thr_name mc FROM ab_merchant_product  WHERE MID=? order by thr_id",vo.getStr("id"));
		if(list_sub!=null&&list_sub.size()>0){
			for (int i = 0; i < list_sub.size(); i++) {
				List<Record> list_pro =	Db.find("select * from ab_merchant_product where mid=? and thr_id=? and zt='1' and mc like ? order by seq_num",id,list_sub.get(i).get("id"),"%"+proname+"%");
				for (int j = 0; j < list_pro.size(); j++) {
					if(user!=null&&user.get("id")!=null){
						sql = "select IFNULL(SUM(quantity),0) from ab_order_item where itemid=? and orderid in(select id from ab_order where xdrid=? and mid=? and ddzt='0')";
						list_pro.get(j).set("procnt", Db.queryBigDecimal(sql,list_pro.get(j).get("id"),user.get("id"),id).longValue());
					}else{
						list_pro.get(j).set("procnt", Long.parseLong("0"));
					}
				}
				list_sub.get(i).set("list_pro", list_pro);
			}
		}
		
		//查看订单详细数据
		List<Record> list_order = null;
		
		if (user != null && user.get("id") != null) {
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
		if(user!=null&&user.get("id")!=null){
			r = Db.findFirst("SELECT IFNULL(SUM(spzj),0) spzj,IFNULL(SUM(ptf),0) ptf,IFNULL(SUM(yhje),0) yhje,IFNULL(SUM(ddzje),0) ddzje FROM ab_order WHERE ddzt='0' AND xdrid=?",user.get("id"));
			if(Db.queryLong("select count(id) from sys_user_store where userid=? and fkid=?",user.get("id"),id).longValue()>0){
				this.setAttr("sfsc", true);
			}else{
				this.setAttr("sfsc", false);
			}
		}else{
			this.setAttr("sfsc", false);
		}
		
		/*
		String str_cook_sj = this.getCookie("list_cookie_sj");
		if(str_cook_sj==null||str_cook_sj.length()<330){
			str_cook_sj = "";
			str_cook_sj +=id+",";
		}else{
			str_cook_sj = str_cook_sj.substring(33,str_cook_sj.length());
			str_cook_sj +=id+",";
		}
		if(StringUtil.toStr(str_cook_sj).length()>1){
			str_cook_sj = str_cook_sj.substring(0,str_cook_sj.length()-1);
		}*/
		//this.setCookie("list_cookie_sj", str_cook_sj, 302400);
		
		
		if(user!=null&&user.get("id")!=null){
			//插入到数据库中
			if(Db.queryLong("select count(id) from ab_merchant_zjfw where userid=? and mid=?",user.get("id"),id).longValue()>0){
				Db.update("update ab_merchant_zjfw set cjsj=? where userid=? and mid=?",DateUtil.getCurrentDate(),user.get("id"),id);
			}else{
				Db.update("insert into ab_merchant_zjfw(id,userid,mid,cjsj)values(?,?,?,?)",StringUtil.getRandString32(),user.get("id"),id,DateUtil.getCurrentDate());
			}
		}
		
		this.setAttr("vo", vo);
		this.setAttr("proname", proname);
		this.setAttr("r", r);
		this.setAttr("list_order", list_order);
		this.setAttr("list_img", list_img);
		this.setAttr("list_comm", list_comm);
		this.setAttr("count_appall", list_comm == null ?0:list_comm.size());
		this.setAttr("count_appone", aprsone == null ?0:aprsone.size());
		this.setAttr("count_apptwo", aprstwo == null ?0:aprstwo.size());
		this.setAttr("count_appthree", aprsthree == null ?0:aprsthree.size());
		this.setAttr("list_sub", list_sub);
		this.setAttr("list_leave", list_leave);
		this.setAttr("leaveflag", leaveFlag);
		
		render("/ab/detail.html");
	}
	
	/**
	 * 商品详细信息
	 */
	public void prodetail(){
		String pid = this.getPara(0);
		AbMerchantProduct p = AbMerchantProduct.dao.findById(pid);
		List<AbProductImg> list_img = AbProductImg.dao.find("select * from ab_product_img where f_id=?",pid);
		SysUser user = this.getSessionAttr("abuser");
		
		int day = DateUtil.dayForWeek(DateUtil.getCurrentDate());
		String currtime = DateUtil.getCurrentDate("HH:mm:ss");
		
		Record vo = Db.findById("ab_merchant",p.get("mid"));
		
		//获取当前日期是否工作中
		String	sql = "SELECT uf_getmerworktime(?,?,?)";
		vo.set("iswork", Db.queryStr(sql,day,currtime,vo.get("id")));
		
		
		//查看订单详细数据
		List<Record> list_order = null;
		
		if (user != null && user.get("id") != null) {
			this.setAttr("userid", user.get("id"));
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
		
		if(user!=null&&user.get("id")!=null){
			if(Db.queryLong("select count(id) from sys_user_store where userid=? and fkid=?",user.get("id"),p.get("id")).longValue()>0){
				this.setAttr("sfsc", true);
			}else{
				this.setAttr("sfsc", false);
			}
		}else{
			this.setAttr("sfsc", false);
		}
		this.setAttr("pcnt", pcnt);
		this.setAttr("list_order", list_order);
		this.setAttr("r", r);
		this.setAttr("vo", vo);
		this.setAttr("p", p);
		this.setAttr("list_img", list_img);
		render("/ab/detailpro.html");
	}
	
	public void login(){
		String referer = this.getRequest().getHeader("Referer");
		if(referer != null && !"".equals(referer)){
			this.setSessionAttr("Referer", referer);
		}
		render("/ab/login.html");
	}
	public void loginout(){
		if(this.getSession()!=null){
//			this.getSession().invalidate();
			this.getSession().setAttribute("abuser", null);
		}
		redirect("/");
	}
	
	//public static final ConcurrentHashMap<String, HttpSession> sessionList = new ConcurrentHashMap<String, HttpSession>();
	
	public void checklogin(){
		String inputRandomCode = this.getPara("captchaCode");
        String username = this.getPara("username");
        String password = this.getPara("password");
        
        Object objMd5RandomCode = this.getSessionAttr(CaptchaRender.DEFAULT_CAPTCHA_MD5_CODE_KEY);
        if (!CaptchaRender.validate(objMd5RandomCode.toString(), inputRandomCode)) {
            this.setAttr("capcodeMsg", "验证码输入不正确,请重新输入！");
            render("/ab/login.html");
        }else{
        	String k  =  MD5.GetMD5Code(password);
	        SysUser user = SysUser.dao.findFirst("select * from sys_user where loginid=? and loginpwd=?", username,MD5.GetMD5Code(password));
	        if(user==null||user.get("id")==null){
	        	this.setAttr("nameMsg", "帐号无效，请重新输入！");
	            render("/ab/login.html");
	        }else{
		        user = SysUser.dao.findById(user.getStr("id"));
			    if (user != null) {
			    	String dateStr = DateUtil.getCurrentDate();
			    	String lastdateStr = user.getStr("login_date")==null?DateUtil.getCurrentDate():user.getStr("login_date");
			    	user.set("login_date", dateStr).set("login_ip", StringUtil.getRemoteLoginUserIp(this.getRequest()));
			    	this.setSessionAttr("abuser", user);
			    	
			    	
			    	//增加积分变动记录,不是同一天增加2个积分
		    		if("107".equals(user.get("role_id"))&&!DateUtil.isSameDay(lastdateStr)){
	    				int jf = user.getInt(SysUser.JIFEN);
	    				user.set("jifen", jf+CommonStaticData.LOGIN_SCORE);
	    				AbIntegralScore ail = new AbIntegralScore();
	    				ail.set(AbIntegralScore.ID, StringUtil.getRandString32());
	    				ail.set(AbIntegralScore.TYPE, CommonStaticData.SCORE_TYPE2);
	    				ail.set(AbIntegralScore.VALUE, CommonStaticData.LOGIN_SCORE);
	    				ail.set(AbIntegralScore.DATETIME, dateStr);
	    				ail.set(AbIntegralScore.USER_ID, user.getStr("id"));
	    				ail.set(AbIntegralScore.FID, "");
	    				ail.save();
	    			}
		    		user.update();
			    	if(user.get("role_id").toString().contains("105")||user.get("role_id").toString().contains("106")||user.get("role_id").toString().contains("107")){
//			    		//让老的session作废
//			    		this.getSession().setAttribute("abuser", user);
//						// 让老的session作废
//						HttpSession prevSession = sessionList.get(user.getStr("id"));
//						if (prevSession != null && prevSession != this.getSession()){
//							try {
//								prevSession.invalidate();
//							} catch (Exception e) {
//								
//							}
//						}
//						sessionList.put(user.getStr("id"), this.getSession());
			    		//根据不同的用户跳转到不同的后台
			    		if("107".equals(user.get("role_id"))){//如果是普通的用户
			    			redirect("/ab/mainuser");
			    		}else if("106".equals(user.get("role_id"))){//如果是业务员
			    			redirect("/ab/mainywy");
			    		}else if("105".equals(user.get("role_id"))){//如果是商家 /ab/logi 物流企业  其他事店铺
			    			
			    			
			    			//
			    			SysUser abuser = this.getSessionAttr("abuser");
			    			String kssj = StringUtil.toStr(this.getPara("kssj"));
			    			String jzsj = StringUtil.toStr(this.getPara("jzsj"));
			    			String ddzt = StringUtil.toStr(this.getPara("ddzt"));
			    			String queryType = StringUtil.toStr(this.getPara("queryType"));
//			    			String lastDate = StringUtil.toStr(this.getPara("lastDate"));
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
			    			//
			    			
			    			
			    			redirect("/ab/mainmer");
			    		}else{
			    			this.setAttr("msg", "");
			    			render("/ab/login.html");
			    		}
			    		
			    	}else{
			    		 this.setAttr("nameMsg", "非【会员】、【商户】、【业务员】请选择其他登录方式！");
			    		 render("/ab/login.html");
			    	}
			    	
			    }else{
			    	 this.setAttr("nameMsg", "用户名或密码不正确,请重新输入！");
			    	 render("/ab/login.html");
			    }
	        }
	        //用户存在判断
	    }
	        
	}
	
	/**
	 * 用户后台
	 */
	@Before(AccessAbInterceptor.class)
	public void mainuser(){
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.DATE, -1);    //得到前一天 
		cal.add(Calendar.MONTH, -1);    //得到前一个月
		long date = cal.getTimeInMillis();
		String str_date =df.format(new Date(date));
		
		SysUser abuser = (SysUser)this.getSessionAttr("abuser");
		SysUser vo = SysUser.dao.findById(abuser.get("id"));
		this.setSessionAttr("abuser", vo);//将数据库最新信息更新到前台
		long notmescount=Db.queryLong("select count(id) from message where user_id=? and isread='0'", abuser.get("id")).longValue();
		this.setAttr("notmescount", notmescount);
		long cnt_sj = Db.queryLong("SELECT COUNT(id) FROM sys_user_store WHERE userid=? AND lx=0",abuser.get("id")).longValue();
		long cnt_sp = Db.queryLong("SELECT COUNT(id) FROM sys_user_store WHERE userid=? AND lx=1",abuser.get("id")).longValue();
		long cnt_dds = Db.queryLong("SELECT COUNT(id) FROM ab_order WHERE xdrid=? AND xdsj >='"+str_date+"'",abuser.get("id")).longValue();
		
		String ddzt = StringUtil.toStr(this.getPara("ddzt"));
		String zfzt = StringUtil.toStr(this.getPara("zfzt"));
		String sjqrzt = StringUtil.toStr(this.getPara("sjqrzt"));
		String sfjd = StringUtil.toStr(this.getPara("sfjd"));
		
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from (");
		sb.append("select ao.*, aa.id appid, aoc.id cbid, aoc.cb_status from ");
		sb.append(" (select * from ab_order where xdrid='"+abuser.getStr("id")+"') ao ");
		sb.append(" left join ab_appraise aa on ao.id = aa.order_id ");
		sb.append(" left join ab_order_chargeback aoc on aoc.order_id = ao.id ) t where 1 = 1 ");
		if(ddzt.length()>0){
			sb.append(" and t.ddzt='"+ddzt+"'");
		}
		if(zfzt.length()>0){
			sb.append(" and zfzt='"+zfzt+"'");
		}
		if(sjqrzt.length()>0){
			sb.append(" and sjqrzt='"+sjqrzt+"'");
		}
		if(sfjd.length()>0){
			sb.append(" and sfjd='"+sfjd+"'");
		}
		String sqlDoing = sb.toString();
		sb.append(" order by t.xdsj desc");
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sb.toString());
		sqlDoing += " and t.zffs in ('1','2') order by t.xdsj desc";
		PageUtil listpage1 = DbPage.paginate(this.getParaToInt("curPage",1), 14, sqlDoing);
		//判断是否可以做退单操作
		for(Record record : listpage.getList()){
			//String dateStr = record.getStr(AbOrder.XDSJ);
			//if(DateUtil.isBackTime(dateStr)){
				if(StringUtil.isNull(record.getStr("cbid"))&& CommonProcess.existQdr(record)){
					record.set("isback", true);
				}else{
					record.set("isback", false);
				}
			//}else{
			//	record.set("isback", false);
			//}
		}
		for(Record record : listpage1.getList()){
			String dateStr = record.getStr(AbOrder.XDSJ);
			if(DateUtil.isBackTime(dateStr)){
				if(StringUtil.isNull(record.getStr("cbid"))){
					record.set("isback", true);
				}else{
					record.set("isback", false);
				}
			}else{
				record.set("isback", false);
			}
		}
		this.keepPara();
		this.setAttr("listpage", listpage);
		this.setAttr("listpage1", listpage1);
		
		this.setAttr("cnt_sj", cnt_sj);
		this.setAttr("cnt_sp", cnt_sp);
		this.setAttr("cnt_dds", cnt_dds);
		this.setAttr("vo", vo);
		// 安全等级：
		int securtynum = 0;
		if ("1".equals(vo.getStr("certificateemail"))) {
			securtynum++;
		}
		if ("1".equals(vo.getStr("certificatemobile"))) {
			securtynum++;
		}
		if ("1".equals(vo.getStr("certificatecard"))) {
			securtynum++;
		}
		this.setAttr("securtynum", securtynum);
		render("/ab/user/main.html");
	}
	
	/**
	 * 商家后台
	 */
	@Before(AccessAbInterceptor.class)
	public void mainmer(){
		AbCityarea city = this.getSessionAttr("currcity");
		this.setAttr("currcity", city);
		//根据当前登录人信息获取商家信息
		SysUser abuser = (SysUser)this.getSessionAttr("abuser");
		SysUser user = SysUser.dao.findById(abuser.get("id"));
		this.setSessionAttr("abuser", user);//将数据库最新信息更新到前台
		long notmescount=Db.queryLong("select count(id) from message where user_id=? and isread='0'", abuser.get("id")).longValue();
		this.setAttr("notmescount", notmescount);
		Record vo = new Record();
		//根据人员信息查询商家信息
		if(abuser!=null&&abuser.get("mid")!=null){
//			vo =AbMerchant.dao.findById(abuser.get("mid"));
			vo = Db.findFirst("select * from ab_merchant where id = '"+abuser.get("mid")+"'");
		}
		
		List<AbMerchantImg> list_img = AbMerchantImg.dao.find("select * from ab_merchant_img where mid=? order by seq_num",vo.getStr("id"));
		
		
		long cnt_dd = Db.queryLong("SELECT IFNULL(COUNT(id),0) FROM ab_order WHERE MID=? and ddzt<>'0'",vo.getStr("id")).longValue();
		long cnt_wcldd = Db.queryLong("SELECT IFNULL(COUNT(id),0) FROM ab_order WHERE MID=? and ddzt='1'",vo.getStr("id")).longValue();	//未处理订单数
		long cnt_jrdd = Db.queryLong("SELECT IFNULL(COUNT(id),0) FROM ab_order WHERE MID=? and ddzt='1' and xdsj like '"+DateUtil.getCurrentDate("yyyy-MM-dd")+"%'",vo.getStr("id")).longValue();
		long cnt_comment = Db.queryLong("SELECT IFNULL(COUNT(id),0) FROM ab_merchant_comment WHERE MID=?",vo.getStr("id"));
		List<AbOrder> list_wcl_order = AbOrder.dao.find("select * from ab_order where mid=? and ddzt='1'", vo.getStr("id"));	//未处理订单
		List<AbOrder> list_order = AbOrder.dao.find("select * from ab_order where xdsj like ? and mid=? and ddzt='1'","%"+DateUtil.getCurrentDate("yyyy-MM-dd")+"%", vo.getStr("id"));
		
		//设置credit
		CommonProcess.setSingleCreditType(vo);
		// 安全等级：
		int securtynum = 0;
		if ("1".equals(vo.getStr("certificateemail"))) {
			securtynum++;
		}
		if ("1".equals(vo.getStr("certificatemobile"))) {
			securtynum++;
		}
		if ("1".equals(vo.getStr("certificatecard"))) {
			securtynum++;
		}
		this.setAttr("securtynum", securtynum);
		this.setAttr("list_img", list_img);
		this.setAttr("list_order", list_order);
		this.setAttr("list_wcl_order", list_wcl_order);
		this.setAttr("vo", vo);
		this.setAttr("cnt_dd", cnt_dd);
		this.setAttr("cnt_wcldd", cnt_wcldd);
		this.setAttr("cnt_jrdd", cnt_jrdd);
		this.setAttr("cnt_comment", cnt_comment);
		render("/ab/mer/main.html");
	}
	
	////////////////////////
	//zmb 2015-02-08
	//TODO 功能确认后删除注释
	@Before(AccessAbInterceptor.class)
	public void certiCompanyIdentity(){
		SysUser user = this.getSessionAttr("abuser");
		user = SysUser.dao.findById(user.get("id"));
		this.setAttr("vo", user);
		render("/ab/user_company.html");
	}
	
	@Before(AccessAbInterceptor.class)
	public void certiUserIdentity(){
		SysUser user = this.getSessionAttr("abuser");
		user = SysUser.dao.findById(user.get("id"));
		this.setAttr("vo", user);
		render("/ab/user_identity.html");
	}
	
	@Before(AccessAbInterceptor.class)
	public void user_info(){
		SysUser user = this.getSessionAttr("abuser");
		user = SysUser.dao.findById(user.get("id"));
		this.setAttr("vo", user);
		render("/ab/user_info.html");
	}
	//////////////////////////
	
	/**
	 * 业务员后台
	 */
	@Before(AccessAbInterceptor.class)
	public void mainywy(){
		SysUser abuser = (SysUser)this.getSessionAttr("abuser");
		SysUser user = SysUser.dao.findById(abuser.get("id"));
		this.setSessionAttr("abuser", user);//将数据库最新信息更新到前台
		long notmescount=Db.queryLong("select count(id) from message where user_id=? and isread='0'", abuser.get("id")).longValue();
		this.setAttr("notmescount", notmescount);
		Record ur = Db.findFirst("select * from sys_user where id = ? ", user.get("id"));
		CommonProcess.setSingleCreditType(ur);
		// 安全等级：
		int securtynum = 0;
		if ("1".equals(user.getStr("certificateemail"))) {
			securtynum++;
		}
		if ("1".equals(user.getStr("certificatemobile"))) {
			securtynum++;
		}
		if ("1".equals(user.getStr("certificatecard"))) {
			securtynum++;
		}
		this.setAttr("securtynum", securtynum);
		this.setAttr("vo", ur);
		render("/ab/ywy/main.html");
	}
	
	
	@Before(AccessAbInterceptor.class)
	public void certiEmail(){
		SysUser user = this.getSessionAttr("abuser");
		user = SysUser.dao.findById(user.get("id"));
		this.setAttr("vo", user);
		render("/ab/certificateemail.html");
	}
	@Before(AccessAbInterceptor.class)
	public void certiEmailSend(){
		boolean flag = false;
		try {
			String id = this.getPara("id");
			String mail = this.getPara("email");
			String subject = "【爱帮网】邮件认证";
			String DomainName=SmsMessage.getDomain();
			String content = DomainName+"/ab/certiEmailUpdate/"+id+"-"+StringUtil.getRandString32();
			SimpleMailSender.sendMail(mail, subject, content, true);
			SysUser u = SysUser.dao.findById(id);
			u.set("certificateemail", "2").set("email", mail).update();
			this.setSessionAttr("abuser", SysUser.dao.findById(id));
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(flag);
	}
	
	public void certiEmailUpdate(){
		String id = this.getPara(0);
		boolean flag = SysUser.dao.findById(id).set("certificateemail", "1").update();
		if(flag)
			redirect("/ab");
		else
			renderNull();
	}
	
	@Before(AccessAbInterceptor.class)
	public void certiCard(){
		render("/ab/certificatecard.html");
	}
	
	////////////////////////
	//zmb 2015-02-08
	//TODO 功能确认后删除注释
	@Before(AccessAbInterceptor.class)
	public void certiYwyIdentity(){
		SysUser user = this.getSessionAttr("abuser");
		user = SysUser.dao.findById(user.get("id"));
		AbFmcar car=AbFmcar.dao.findByMobile(user.getStr("loginid"));
		this.setAttr("car", car);
		this.setAttr("vo", user);
		render("/ab/user_pt2.html");
	}
	//////////////////////////

	@Before(AccessAbInterceptor.class)
	public void certiMobileSend(){
		boolean flag = false;
		try{
			String id = this.getPara("id");
			flag =SysUser.dao.findById(id)
			.set("certificatemobile", "1")
			.update();
			
			if(flag){
				this.setSessionAttr("abuser", SysUser.dao.findById(id));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(flag);
	}
	
	@Before(AccessAbInterceptor.class)
	public void certiCardSend(){
		boolean flag = false;
		try{
			String id = this.getPara("id");
			String mc = this.getPara("mc");
			String idcard = this.getPara("idcard");
			String khh = this.getPara("khh");
			String khhkh = this.getPara("khhkh");
			
			BigDecimal b2 = new BigDecimal(StringUtil.getRandomDec());
			flag =SysUser.dao.findById(id)
			.set("mc", mc)
			.set("idcard", idcard)
			.set("khh", khh)
			.set("khhkh", khhkh)
			.set("certificatecard", "2")
			.set("certificatemoney", b2)
			.update();
			
			if(flag){
				this.setSessionAttr("abuser", SysUser.dao.findById(id));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(flag);
	}
	@Before(AccessAbInterceptor.class)
	public void certiCardMoney(){
		boolean flag = false;
		try{
			String id = this.getPara("id");
			String money = this.getPara("certificatemoney");
			if(Db.findFirst("select count(id) cnt from sys_user where id=? and certificatemoney=?",id,money).getLong("cnt").longValue()>0){
				//认证成功
				flag =SysUser.dao.findById(id).set("certificatecard", "1").update();
				if(flag){
					this.setSessionAttr("abuser", SysUser.dao.findById(id));
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(flag);
	}
	
	@Before(AccessAbInterceptor.class)
	public void certiMobile(){
		SysUser u = this.getSessionAttr("abuser");
		u = SysUser.dao.findById(u.get("id"));
		this.setAttr("vo", u);
		render("/ab/certificatemobile.html");
	}
	
	@Before(AccessAbInterceptor.class)
	public void modifyMobile(){
		SysUser u = this.getSessionAttr("abuser");
		u = SysUser.dao.findById(u.get("id"));
		this.setAttr("vo", u);
		render("/ab/modifyMobile.html");
	}
	@Before(AccessAbInterceptor.class)
	public void saveModifyMobile(){
		boolean flag = false;
		try{
			String id = this.getPara("id");
			String sjh = this.getPara("sjh");
			SysUser u = SysUser.dao.findById(id);
			u.set("mobile", sjh);
			u.set("loginid", sjh);
			u.update();
			this.setSessionAttr("user", u);
			flag = true;
		}catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(flag);
	}
	
	@Before(AccessAbInterceptor.class)
	public void editpwd(){
		render("/ab/editpwd.html");
	}
	
	@Before(AccessAbInterceptor.class)
	public void zfpwd(){
		
		render("/ab/zfpwd.html");
	}
	
	@Before(AccessAbInterceptor.class)
	public void checkzfpwd(){
		boolean flag = false;
		try{
			String loginid = this.getPara("loginid");
			if(Db.findFirst("select count(*) cnt from sys_user_zfpwd where userid='" + loginid + "'").getLong("cnt").longValue() < 1){
				flag = true;
			}else{
				String loginpwd = this.getPara("oldpwd");
				if(Db.findFirst("select count(id) cnt from sys_user_zfpwd where userid=? and zfpwd=?",loginid,loginpwd).getLong("cnt").longValue()>0){
					flag = true;
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(flag);
	}
	
	@Before(AccessAbInterceptor.class)
	public void savemodifyzfpwd(){
		boolean flag = false;
		try{
			String loginid = this.getPara("loginid");
			String newpwd = this.getPara("newpwd");
			long num = Db.findFirst("select count(*) cnt from sys_user_zfpwd where userid='" + loginid + "'").getLong("cnt").longValue();
			if(num < 1){
				SysUserZFPwd suzp = new SysUserZFPwd();
				suzp.set("id", StringUtil.getUuid32());
				suzp.set("userid", loginid);
				suzp.set("zfpwd", newpwd);
				suzp.save();
				flag = true;
			}else{
				int rtn = Db.update("update sys_user_zfpwd set zfpwd=? where userid=?", newpwd,loginid);
				if(rtn>0){
					flag = true;
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(flag);
	}
	
	@Before(AccessAbInterceptor.class)
	public void checkpwd(){
		boolean flag = false;
		try{
			String loginid = this.getPara("loginid");
			String loginpwd = this.getPara("oldpwd");
			if(Db.findFirst("select count(id) cnt from sys_user where loginid=? and loginpwd=?",loginid,MD5.GetMD5Code(loginpwd)).getLong("cnt").longValue()>0){
				flag = true;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(flag);
	}
	
	@Before(AccessAbInterceptor.class)
	public void savemodifypwd(){
		boolean flag = false;
		try{
			String loginid = this.getPara("loginid");
			String newpwd = this.getPara("newpwd");
			int rtn = Db.update("update sys_user set loginpwd=? where loginid=?", MD5.GetMD5Code(newpwd),loginid);
			if(rtn>0){
				flag = true;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(flag);
	}
	
	@Before(AccessAbInterceptor.class)
	public void savemainuser(){
		SysUser user = SysUser.dao.findById(this.getPara("u_id"));
		user.set("mc",StringUtil.toStr(this.getPara("mc")));
		user.set("sex",StringUtil.toStr(this.getPara("vo.sex")));
		user.set("birth",StringUtil.toStr(this.getPara("birth")));
		user.set("idcard",StringUtil.toStr(this.getPara("idcard")));
		user.set("loginid",StringUtil.toStr(this.getPara("loginid")));
		user.set("login_date",StringUtil.toStr(this.getPara("login_date")));
		user.set("login_ip",StringUtil.toStr(this.getPara("login_ip")));
		user.set("email",StringUtil.toStr(this.getPara("email")));
		user.set("mobile",StringUtil.toStr(this.getPara("mobile")));
		user.set("tel",StringUtil.toStr(this.getPara("tel")));
		user.set("qq",StringUtil.toStr(this.getPara("qq")));
		user.set("nick_name",StringUtil.toStr(this.getPara("nick_name")));
		user.set("age",StringUtil.toStr(this.getPara("age")));
		user.set("constellation",StringUtil.toStr(this.getPara("constellation")));
		user.set("height",StringUtil.toStr(this.getPara("u_height")));
		user.set("sign",StringUtil.toStr(this.getPara("sign")));
		user.set("salary",StringUtil.toStr(this.getPara("salary")));
		user.set("region",StringUtil.toStr(this.getPara("region")));
		user.set("work",StringUtil.toStr(this.getPara("work")));
		user.set("marriage",StringUtil.toStr(this.getPara("marriage")));
		user.set("smoking",StringUtil.toStr(this.getPara("smoking")));
		user.set("drink",StringUtil.toStr(this.getPara("drink")));
		user.update();
	}
	
	public void ab_gzwx(){
		render("/ab/gzwx/gz_wx.html");
	}
	
	public void ab_cflj(){
		render("/ab/gzwx/cflj.html");
	}
	
	@Before(AccessAbInterceptor.class)
	public void save_user_info(){
		boolean flag = false;
		SysUser user =this.getModel(SysUser.class,"tb");
		if(user!=null&&user.get("id")!=null){
			user.update();
			user = SysUser.dao.findById(user.get("id"));
			this.setSessionAttr("abuser", user);
		}
		flag = true;
		renderJson(flag);
	}
	
	public void sendemailmsg(){
		render("sendemailmsg.html");
	}
	
	public void sendmobilemsg(){
		render("sendmobilemsg.html");
	}
	
	public void checkEmailExist(){
		boolean flag = false;
		String email = this.getPara("email");
		SysUser user = SysUser.dao.findFirst("select * from sys_user where email=?",email);
		if(user!=null){
			flag = true;
		}
		renderJson(flag);
	}
	
	public void checkMobileExist(){
		boolean flag = false;
		String mobile = this.getPara("mobile");
		SysUser user = SysUser.dao.findFirst("select * from sys_user where mobile=?",mobile);
		if(user!=null){
			flag = true;
		}
		renderJson(flag);
	}
	
	public void modifypwdbyemail(){
		String id = this.getPara(0);
		SysUser user =  SysUser.dao.findById(id);
		this.setAttr("vo", user);
		render("/ab/modifypwdbyemail.html");
	}
	
	public void savemodifyemail(){
		boolean flag = false;
		String id = this.getPara("id");
		String pwd = this.getPara("pwd");
		SysUser.dao.findById(id).set("loginpwd", MD5.GetMD5Code(pwd)).update();
		flag = true;
		renderJson(flag);
	}
	
	public void savepwd_by_mobile(){
		boolean flag = false;
		String mobile = this.getPara("mobile");
		String pwd = this.getPara("pwd");
		SysUser u = SysUser.dao.findFirst("select * from sys_user where mobile =?",mobile);
		u.set("loginpwd", MD5.GetMD5Code(pwd)).update();
		flag = true;
		renderJson(flag);
	}
	
	
	public void sendmodifypwd(){
		boolean flag = false;
		String email = this.getPara("email");
		SysUser user = SysUser.dao.findFirst("select * from sys_user where email=?",email);
		flag = TextMail.sendMail(email, "密码找回", "http://www.yijuhome.cn/ab/modifypwdbyemail/"+user.getStr("id"), true);
		renderJson(flag);
	}
	
	public void newslist(){
		String lbid = this.getPara(0);
		String sql= "select * from cms_content where id is not null ";
		if(StringUtil.toStr(lbid).length()>0){
			sql +=" and lbid = "+lbid;
		}else{
			sql +=" and lbid = 9";
		}
		sql+=" order by release_date desc";
		
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		this.setAttr("listpage", listpage);
		render("/ab/newslist.html");
	}
	
	public void newsshow(){
		CmsContent vo = CmsContent.dao.findById(this.getPara(0));
		this.setAttr("vo", vo);
		render("/ab/newsshow.html");
	}
	@Before(AccessAbInterceptor.class)
	public void mainmessage(){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.DATE, -1);    //得到前一天 
		cal.add(Calendar.MONTH, -1);    //得到前一个月
		long date = cal.getTimeInMillis();
		String str_date =df.format(new Date(date));
		
		SysUser abuser = (SysUser)this.getSessionAttr("abuser");
		SysUser vo = SysUser.dao.findById(abuser.get("id"));
		this.setSessionAttr("abuser", vo);//将数据库最新信息更新到前台
		long messagecount=Db.queryLong("select count(id) from message where user_id=?", abuser.get("id")).longValue();
		StringBuffer sb=new StringBuffer();
		sb.append("select * from message where user_id='");
		String userid=abuser.get("id");
		sb.append(userid);
		sb.append("'");
		PageUtil listpage=DbPage.paginate(this.getParaToInt("curPage",1), 14, sb.toString());
		this.setAttr("listpage", listpage);
		render("/ab/message/main.html");
	}
	@Before(AccessAbInterceptor.class)
	public void deletemes(){
		String[] id=getParaValues("chooseid");
		if(id.length==0){
			render("/ab/message/main.html");
		}
		else{
			for(int i=0;i<id.length;i++){
				Db.deleteById("message", id[i]);
			}
			mainmessage();
			//render("/ab/message/main.html");
		}
		
	}
}
