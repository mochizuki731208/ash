package com.zp.controller.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.yishenghuo.model.AbUser;
import net.yishenghuo.model.BaseBean;
import net.yishenghuo.model.Category;
import net.yishenghuo.model.CategorySub;
import net.yishenghuo.model.CityVo;
import net.yishenghuo.model.McCategory;
import net.yishenghuo.model.Merchant;
import net.yishenghuo.model.MerchantListVo;
import net.yishenghuo.model.SendCodeVo;
import net.yishenghuo.model.Version;

import org.apache.commons.lang.StringEscapeUtils;

import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.zp.entity.AbAppraise;
import com.zp.entity.AbAppraiseImg;
import com.zp.entity.AbCard;
import com.zp.entity.AbCardExpense;
import com.zp.entity.AbCityarea;
import com.zp.entity.AbIntegralScore;
import com.zp.entity.AbMerchant;
import com.zp.entity.AbMerchantLeave;
import com.zp.entity.AbMerchantPrint;
import com.zp.entity.AbMerchantProduct;
import com.zp.entity.AbMerchantReply;
import com.zp.entity.AbMerchantYysj;
import com.zp.entity.AbOrder;
import com.zp.entity.AbOrderChargeback;
import com.zp.entity.AbOrderItem;
import com.zp.entity.AbSubject;
import com.zp.entity.SysConfig;
import com.zp.entity.SysCzTx;
import com.zp.entity.SysUser;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.tools.CommonProcess;
import com.zp.tools.CommonSQL;
import com.zp.tools.CommonStaticData;
import com.zp.tools.DateUtil;
import com.zp.tools.MD5;
import com.zp.tools.PrintUtils;
import com.zp.tools.RandomUtil;
import com.zp.tools.SmsMessage;
import com.zp.tools.StringUtil;
import com.zp.tools.alipay.AlipaySubmit;

/**
 * 接收所有restful的请求（接口入口）
 * 
 */
public class RestController extends Controller {
	
	//获取短信码
	public void sms(){
		String phone = this.getPara("phone");
		String smsCodeRand = this.getPara("randomcode");
		
		String result = null;
		try {
			result = SmsMessage.SendMessage(phone, smsCodeRand);
		} catch (Exception e) {
		}
		SendCodeVo scv = new SendCodeVo();
		if (result != null && !"".equals(result))
		{
			scv.setCode(result);
			scv.setSucess(true);
		}
		else
		{
			scv.setSucess(false);
		}
		renderJson(scv);
	}
	
	//注册
	public void reguser(){
		SysUser user = this.getModel(SysUser.class,"user");
		String uuid = StringUtil.getUuid32();
		user.set("id", uuid);
		String loginid = this.getPara("loginnum");
		
		SysUser ss = SysUser.dao.findFirst("select * from sys_user where loginid=?", loginid);
		if (ss != null)
		{
			BaseBean info = new BaseBean();
			info.setSucess(false);
			info.setMsg("账号已经注册");
			renderJson(info);
			return;
		}
		user.set("loginid", loginid);
		user.set("certificatemobile", "1");
		user.set("status", "0");
		user.set("role_id", "107");
		user.set("role_name", "会员");
		user.set("create_date", DateUtil.getCurrentDate());
		user.set(SysUser.JIFEN, CommonStaticData.REG_SCORE);
		String password = this.getPara("password");
		user.set("loginpwd", MD5.GetMD5Code(password));
		String mc = this.getPara("mc");
		if (mc != null)
		user.set("mc", mc);
		String sex = this.getPara("sex");
		if (sex != null)
		user.set("sex", sex);
		String birth = this.getPara("birth");
		if (birth != null)
		user.set("birth", birth);
		String idcard = this.getPara("idcard");
		if (idcard != null)
		user.set("idcard", idcard);
		String email = this.getPara("email");
		if (email != null)
		user.set("email", email);
		String mobile = this.getPara("phone");
		if (mobile != null)
		user.set("mobile", mobile);
		String qq = this.getPara("qq");
		if (qq != null)
		user.set("qq", qq);
		String area_id = this.getPara("area_id");
		if (area_id != null)
		user.set("area_id", area_id);
		String area_name = this.getPara("area_name");
		if (area_name != null)
		user.set("area_name", area_name);
		String mid = this.getPara("mid");
		if (mid != null)
		user.set("mid", mid);
		user.save();
		BaseBean info = new BaseBean();
		renderJson(info);
	}
	
	//登录
	public void login(){
		String username = this.getPara("loginnum");
        String password = this.getPara("password");
		SysUser user = getUser();
		AbUser info = new AbUser();
		if (user != null)
		{
			info.setId(user.getStr(SysUser.ID));
			info.setLogid(user.getStr(user.LOGINID));
			info.setLoginnum(username);
			info.setPassword(password);
			//-------type 
			info.setQq(user.getStr(user.QQ));
			info.setPhone(user.getStr(user.MOBILE));
			info.setEmail(user.getStr(user.EMAIL));
			//pcate
			//cate
			//image
			info.setRegistertime(user.getDate(user.getStr(user.CREATE_DATE)));
			info.setLastlogintime(user.getDate(user.getStr(user.LOGIN_DATE)));
			info.setJifen(user.getInt(user.JIFEN));
			info.setMoney(user.getBigDecimal(user.ZHYE).doubleValue());
			info.setStatus(Integer.valueOf(user.getStr(user.STATUS)));
			//certificateperson
			//certificatecompany
			info.setCertificatephone(Integer.valueOf(user.getStr(user.CERTIFICATEMOBILE)));
			info.setCertificateemail(Integer.valueOf(user.getStr(user.CERTIFICATEEMAIL)));
			//safecode
			//question
			//answer
			//certificatepersonpicy
			//certificatepersonpicx
			//certificatecompanypic
			//per
			//tgloginnum
			//encrypt_type
			//verify_code
			//verify_type
		}
		else
		{
			info.setSucess(false);
		}
		renderJson(info);
	}

	//密码修改
	public void pwd(){
        String loginid = this.getPara("loginnum");
		String newpwd = this.getPara("newpassword");
		int rtn = Db.update("update sys_user set loginpwd=? where loginid=?", MD5.GetMD5Code(newpwd),loginid);
		BaseBean bb = new BaseBean();
		bb.setSucess(rtn>0?true:false);
		renderJson(bb);
	}
	
	//用户信息修改
	public void upuser()
	{
		SysUser user = getUser();
		BaseBean bb = new BaseBean();
		if (user != null)
		{
			String mc = this.getPara("mc");
			if (mc != null)
			user.set("mc", mc);
			String sex = this.getPara("sex");
			if (sex != null)
			user.set("sex", sex);
			String birth = this.getPara("birth");
			if (birth != null)
			user.set("birth", birth);
			String idcard = this.getPara("idcard");
			if (idcard != null)
			user.set("idcard", idcard);
			String email = this.getPara("email");
			if (email != null)
			user.set("email", email);
			String mobile = this.getPara("phone");
			if (mobile != null)
			user.set("mobile", mobile);
			String qq = this.getPara("qq");
			if (qq != null)
			user.set("qq", qq);
			String tel = this.getPara("tel");
			if (tel != null)
			user.set("tel", tel);
			boolean flag = user.update();
			bb.setSucess(flag);
		}
		else
		{
			bb.setSucess(false);
		}
		
		renderJson(bb);
	}
	
	//栏目列表
public void sublist(){
		
		String city_id = this.getPara("cityid");
        List<AbSubject> list = AbSubject.dao.find("select * from ab_subject where p_id='ROOT' and is_display='1' and id IN(SELECT subject_id FROM ab_cityarea_subject WHERE city_id='"+city_id+"')");
        List arrayList = new ArrayList();
        for (AbSubject as:list)
        {
        	Category c = new Category();
        	c.setName(as.getStr(as.MC));
        	String id = as.getStr(as.ID);
        	c.setNumid(id);
        	c.setParentid(as.getStr(as.P_ID));
        	c.setSort(as.getInt(as.SEQ_NUM)+"");
        	List<AbSubject> list2 = AbSubject.dao.find("select * from ab_subject where p_id=? and is_display='1' and id IN(SELECT subject_id FROM ab_cityarea_subject WHERE city_id='"+city_id+"')",id);
        	List<CategorySub> subs = c.getSubs();
        	for (AbSubject as2:list2)
        	{
        		CategorySub c2 = new CategorySub();
            	c2.setName(as2.getStr(as2.MC));
            	String id2 = as2.getStr(as2.ID);
            	c2.setNumid(id2);
            	c2.setParentid(as2.getStr(as2.P_ID));
            	c2.setSort(as2.getInt(as2.SEQ_NUM)+"");
            	subs.add(c2);
        	}
        	arrayList.add(c);
        }
		renderJson(arrayList);
	}
	
	//私人定制（过日子）
	public void sublist2(){
		
		String city_id = this.getPara("cityid");
        List<AbSubject> list = AbSubject.dao.find("select * from ab_subject where id='1017' and p_id='ROOT' and is_display='1' and id IN(SELECT subject_id FROM ab_cityarea_subject WHERE city_id='"+city_id+"')");
        List arrayList = new ArrayList();
        for (AbSubject as:list)
        {
        	Category c = new Category();
        	c.setName(as.getStr(as.MC));
        	String id = as.getStr(as.ID);
        	c.setNumid(id);
        	c.setParentid(as.getStr(as.P_ID));
        	c.setSort(as.getInt(as.SEQ_NUM)+"");
        	List<AbSubject> list2 = AbSubject.dao.find("select * from ab_subject where p_id=? and is_display='1' and id IN(SELECT subject_id FROM ab_cityarea_subject WHERE city_id='"+city_id+"')",id);
        	List<CategorySub> subs = c.getSubs();
        	for (AbSubject as2:list2)
        	{
        		CategorySub c2 = new CategorySub();
            	c2.setName(as2.getStr(as2.MC));
            	String id2 = as2.getStr(as2.ID);
            	c2.setNumid(id2);
            	c2.setParentid(as2.getStr(as2.P_ID));
            	c2.setSort(as2.getInt(as2.SEQ_NUM)+"");
            	subs.add(c2);
        	}
        	arrayList.add(c);
        }
		renderJson(arrayList);
	}
	private double rad(double d)  
    {  
       return d * Math.PI / 180.0;  
    }
	private final  double EARTH_RADIUS = 6378137;
	public double GetDistance(double lon1,double lat1,double lon2, double lat2)  
    {  
       double radLat1 = rad(lat1);  
       double radLat2 = rad(lat2);  
       double a = radLat1 - radLat2;  
       double b = rad(lon1) - rad(lon2);  
       double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2)+Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));  
       s = s * EARTH_RADIUS;  
       //s = Math.round(s * 10000) / 10000;  
       return s;  
    }
	private Merchant getMerchantRecord(Record vo,String lon1,String lat1,String pricestart,String priceend)
	{
		Merchant mc = new Merchant();
		//计算平均消费费用
//		String sql = "SELECT IFNULL(AVG(price),0) avgprice FROM ab_order_item WHERE MID='"+vo.getStr("id")+"'";
//		Record r = Db.findFirst(sql);
		if (pricestart != null && !"".equals(pricestart) && priceend != null && !"".equals(priceend))
		{
			//过滤价格之外的
//			String a = r.getBigDecimal("avgprice")+"";
			String a = vo.getBigDecimal("avgprice")+"";
	        double s = Double.valueOf(pricestart);
	        double e = Double.valueOf(priceend);
	        double avg = Double.valueOf(a);
			if (avg > e) {
	            return null;
	        }
	        if (avg < s) {
	        	return null;
	        }
		}
		mc.setAvgprice(vo.getBigDecimal("avgprice")+"");
//		vo.set("avgprice", r.getBigDecimal("avgprice"));
		//计算平均跑腿费和用时时长
//		sql="SELECT IFNULL(AVG(TIMESTAMPDIFF(HOUR,xdsj,shsj)),0) avghour,IFNULL(AVG(ptf),0) avgptf FROM ab_order WHERE ddzt='9' AND MID='"+vo.getStr("id")+"'";
//		r = Db.findFirst(sql);
//		vo.set("avghour", r.getBigDecimal("avghour"));
//		vo.set("avgptf", r.getBigDecimal("avgptf"));
		mc.setAvghour(vo.getInt("avghour")+"");		
		mc.setAvgptf(vo.getBigDecimal("avgptf")+"");
		
		mc.setAddr(vo.getStr("xxdz"));
		mc.setArea(vo.getStr("area_name"));
		mc.setArea_id(vo.getStr("area_id"));
		//cate
		//ccost
		mc.setCity(vo.getStr("city_name"));
		mc.setCommentcount(vo.getInt("comments_total"));
//		mc.setDdesc(vo.getStr("brief"));
		//ddist
		mc.setDetails(vo.getStr("brief"));
		mc.setId(vo.getStr("id"));
		//iid
		mc.setImg_url(vo.getStr("img_url"));
		//index_num
		//isbook
		String iscomm = vo.getStr("is_recommend");
		if (iscomm != null)
		mc.setIsrecommend(Integer.valueOf(iscomm));
		//isupport
		String vlat = vo.getStr("lat");
		String vlng = vo.getStr("lng");
		mc.setLat(vlat);
		mc.setLng(vlng);
		if (lon1 == null || lat1 == null)
		{
			mc.setDistance("");	
		}
		else
		{
			double aa = Double.valueOf(lon1);
			double bb = Double.valueOf(lat1);
			if (vlng != null && vlat != null)
			{
				double cc = Double.valueOf(vlng);
				double dd = Double.valueOf(vlat);
				
				double ee = GetDistance(aa, bb, cc, dd);
				mc.setDistance(ee+"");
			}
			else
			{
				mc.setDistance("");
			}
		}
		//loginnum
		mc.setName(vo.getStr("mc"));
		//ordercount
		mc.setRate(vo.getStr("rate"));
		//result_num
		mc.setSort(vo.getInt("seq_num"));
		//status
		mc.setSubjectname(vo.getStr("subject_name"));
		//tag
		mc.setTel(vo.getStr("mobile"));
		//total
		mc.setType(vo.getStr("is_type"));
		//mc.setWap_url(wap_url)
		//mc.setWeb_url(web_url);
		//mc.setWebname(webname)
		mc.setWork_time(vo.getStr("worktime"));
		String buiss = vo.getStr("business_status");
		if (buiss != null)
		mc.setStatus(Integer.valueOf(buiss));
		
		String sql2 = "select * from ab_merchant_yysj where mid='"+vo.getStr("id")+"' order by xq, sbsj desc";
	    List<AbMerchantYysj>  list2 = AbMerchantYysj.dao.find(sql2);
	    mc.setYysj(list2);
	    mc.setZdpsje(vo.getBigDecimal("zdpsje")+"");
		return mc;
	}
	private Merchant getMerchant(AbMerchant vo)
	{
		Merchant mc = new Merchant();
		//计算平均消费费用
//		String sql = "SELECT IFNULL(AVG(price),0) avgprice FROM ab_order_item WHERE MID='"+vo.getStr("id")+"'";
//		Record r = Db.findFirst(sql);
//		mc.setAvgprice(r.getBigDecimal("avgprice")+"");
		mc.setAvgprice(vo.getBigDecimal("avgprice")+"");
		
//		vo.set("avgprice", r.getBigDecimal("avgprice"));
		//计算平均跑腿费和用时时长
//		sql="SELECT IFNULL(AVG(TIMESTAMPDIFF(HOUR,xdsj,shsj)),0) avghour,IFNULL(AVG(ptf),0) avgptf FROM ab_order WHERE ddzt='9' AND MID='"+vo.getStr("id")+"'";
//		r = Db.findFirst(sql);
//		vo.set("avghour", r.getBigDecimal("avghour"));
//		vo.set("avgptf", r.getBigDecimal("avgptf"));
		
//		mc.setAvghour(r.getBigDecimal("avghour")+"");		
		mc.setAvghour(vo.getInt("avghour")+"");
//		mc.setAvgptf(r.getBigDecimal("avgptf")+"");
		mc.setAvgptf(vo.getBigDecimal("avgptf")+"");
		
		mc.setAddr(vo.getStr(vo.XXDZ));
		mc.setArea(vo.getStr(vo.AREA_NAME));
		mc.setArea_id(vo.getStr(vo.AREA_ID));
		//cate
		//ccost
		mc.setCity(vo.getStr(vo.CITY_NAME));
		mc.setCommentcount(vo.getInt(vo.COMMENTS_TOTAL));
		mc.setDdesc(vo.getStr(vo.BRIEF));
		//ddist
		mc.setDetails(vo.getStr(vo.BRIEF));
		mc.setId(vo.getStr(vo.ID));
		//iid
		mc.setImg_url(vo.getStr(vo.IMG_URL));
		//index_num
		//isbook
		mc.setIsrecommend(Integer.valueOf(vo.getStr(vo.IS_RECOMMEND)));
		//isupport
		mc.setLat(vo.getStr(vo.LAT));
		mc.setLng(vo.getStr(vo.LNG));
		//loginnum
		mc.setName(vo.getStr(vo.MC));
		//ordercount
		mc.setRate(vo.getStr(vo.RATE));
		//result_num
		mc.setSort(vo.getInt(vo.SEQ_NUM));
		//status
		mc.setSubjectname(vo.getStr(vo.SUBJECT_NAME));
		//tag
		mc.setTel(vo.getStr(vo.MOBILE));
		//total
		mc.setType(vo.getStr(vo.IS_TYPE));
		//mc.setWap_url(wap_url)
		//mc.setWeb_url(web_url);
		//mc.setWebname(webname)
		mc.setWork_time(vo.getStr(vo.WORKTIME));
		mc.setStatus(Integer.valueOf(vo.getStr(vo.BUSINESS_STATUS)));
		mc.setZdpsje(vo.getBigDecimal("zdpsje")+"");
		return mc;
	}
	//商户详情
	public void mc(){
		String id = this.getPara("id");
		AbMerchant vo = AbMerchant.dao.findById(id);
		
		Merchant mc = getMerchant(vo);
		//查看分类
		List<Record> list_sub = Db.find("SELECT DISTINCT thr_id id,thr_name mc FROM ab_merchant_product WHERE MID='"+vo.getStr("id")+"' ");
		List<McCategory> mclist = new ArrayList<McCategory>();
		for (Record rc:list_sub)
		{
			McCategory mcc = new McCategory();
			mcc.setId(rc.getStr("id"));
			mcc.setName(rc.getStr("mc"));
			
			 String sql1 = "select * from ab_merchant_product where id is not null "+ " and mid='" + vo.getStr("id") + "'"+ " and thr_id='" + rc.getStr("id") + "'"+ " order by seq_num";
			 List<AbMerchantProduct> list = AbMerchantProduct.dao.find(sql1);
			 mcc.setSubs(list);
			 mclist.add(mcc);
		}
	    mc.setMctype(mclist);
	    String sql2 = "select * from ab_merchant_yysj where mid='"+id+"' order by xq, sbsj desc";
	    List<AbMerchantYysj>  list2 = AbMerchantYysj.dao.find(sql2);
	    mc.setYysj(list2);
		renderJson(mc);
	}
		
	public void query(){
		String city_id = this.getPara("city_id");		
		String keyword = StringUtil.toStr(this.getPara("keyword")); //大分类
//			String sql = "select *,(SELECT COUNT(id) FROM ab_order o WHERE o.mid=t.id) cnt from ab_merchant t where id is not null ";
//			if(city_id!=null){
//				sql+=" and city_id='"+StringEscapeUtils.escapeSql(city_id)+"'";
//			}
//			//商家名称
//			if(keyword.length()>0){
//				sql+=" and mc like '%"+StringEscapeUtils.escapeSql(keyword)+"%'";
//			}
//			
		String findtype = StringUtil.toStr(this.getPara("findtype")); //fd  按商品搜索
		
		String sql = "";
		if(StringUtil.toStr(findtype).length()<1){
			sql = "select count(id) from ab_merchant where mc like '%"+StringEscapeUtils.escapeSql(keyword)+"%' and city_id='"+StringEscapeUtils.escapeSql(city_id)+"'";
			if(Db.queryLong(sql).longValue()>0){
				findtype = "";
			}
		}
		
		
		if(StringUtil.toStr(findtype).length()<1){
			sql = "select count(id) from ab_merchant_product where mc like '%"+StringEscapeUtils.escapeSql(keyword)+"%' and city_id='"+StringEscapeUtils.escapeSql(city_id)+"'";
			if(Db.queryLong(sql).longValue()>0){
				findtype = "fd";
			}
		}
		
		
		if("fd".equals(findtype)){
			sql = "select *,(SELECT COUNT(id) FROM ab_order_item p WHERE p.itemid=t.id) cnt from ab_merchant_product t where zt ='1' ";
			if(city_id!=null){
				sql+=" and city_id='"+StringEscapeUtils.escapeSql(city_id)+"'";
			}
			if(keyword.length()>0){
				sql+=" and mc like '%"+StringEscapeUtils.escapeSql(keyword)+"%'";
			}
		}else{
			sql = "select *,(SELECT COUNT(id) FROM ab_order o WHERE o.mid=t.id) cnt from ab_merchant t where check_status='1' ";
			if(city_id!=null){
				sql+=" and city_id='"+StringEscapeUtils.escapeSql(city_id)+"'";
			}
			//商家名称
			if(keyword.length()>0){
				sql+=" and mc like '%"+StringEscapeUtils.escapeSql(keyword)+"%'";
			}
		}
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		renderJson(listpage);
		
//		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
//		List<Merchant> mclist = new ArrayList();
//        for (Record record : listpage.getList())
//        {
//        	Merchant mc = getMerchantRecord(record,null,null,"","");
//        	if (mc != null)
//        	{
//        		mclist.add(mc);
//        	}
//        }
//        Collections.sort(mclist);
//        MerchantListVo mclv = new MerchantListVo();
//        mclv.setMerchant_list(mclist);
//        mclv.setSize(listpage.getTotalPage()+"");
//        mclv.setCount_no(listpage.getCurPage()+"");
//		renderJson(mclv);
	}
	
	//模糊查询商户
	public void likemc(){
		String city_id = this.getPara("city_id");		
//		String name = this.getPara("cityname");
		String subject_id = this.getPara("subject_id");
		String area_ids = this.getPara("area_ids");
		String business_status = this.getPara("business_status");
		String pricestart = this.getPara("pricestart");
		String priceend = this.getPara("priceend");
		String lon1 = this.getPara("lon1");
		String lat1 = this.getPara("lat1");
//        List<AbMerchant> list = null;//AbMerchant.dao.find("select * from ab_merchant where city_name like '%"+name+"%'");
        String one = " where id is not null ";
        if (city_id != null && !"".equals(city_id))
        {
        	one += "and city_id= '"+city_id+"'";
        }
        if (subject_id != null && !"".equals(subject_id))
        {
        	one += " and subject_id= '"+subject_id+"'";
        }
        if(area_ids != null && !"".equals(area_ids)){
			one+=" and area_id in ('"+area_ids+"')";
		}
        if (business_status != null && !"".equals(business_status))
        {
        	one += " and business_status = '"+business_status+"'";
        }
        String sql = "select * from ab_merchant"+one;
        //分页有瑕疵，在帅选人均消费后不是真实的分页，还是下面语句的分页
        PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
//        list = AbMerchant.dao.find(sql);
        List<Merchant> mclist = new ArrayList();
        
    	for (Record record : listpage.getList())
        {
//            	AbMerchant vo = abmc;
        	Merchant mc = getMerchantRecord(record,lon1,lat1,pricestart,priceend);
        	if (mc != null)
        	{
        		mclist.add(mc);
        	}
        }
        Collections.sort(mclist);
        MerchantListVo mclv = new MerchantListVo();
        mclv.setMerchant_list(mclist);
        mclv.setSize(listpage.getTotalPage()+"");
        mclv.setCount_no(listpage.getCurPage()+"");
		renderJson(mclv);
	}
	
	//模糊查询商户
	public void likemcbyrandom(){
		String one = this.getPara("length");		
        String sql = "select * from ab_merchant order by rand() limit "+one;
        //分页有瑕疵，在帅选人均消费后不是真实的分页，还是下面语句的分页
        PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
//        list = AbMerchant.dao.find(sql);
        List<Merchant> mclist = new ArrayList();
        for (Record record : listpage.getList())
        {
//        	AbMerchant vo = abmc;
        	Merchant mc = getMerchantRecord(record,null,null,null,null);
        	if (mc != null)
        	{
        		mclist.add(mc);
        	}
        }
        Collections.sort(mclist);
        MerchantListVo mclv = new MerchantListVo();
        mclv.setMerchant_list(mclist);
        mclv.setSize(listpage.getTotalPage()+"");
        mclv.setCount_no(listpage.getCurPage()+"");
		renderJson(mclv);
	}
	//城市列表
	public void citylist(){
		List<AbCityarea> list = AbCityarea.dao.find("select * from ab_cityarea m where m.type_id=0 and m.p_id='ROOT' order by seq_num");
		List<CityVo> clist = new ArrayList();
		for (AbCityarea ac:list)
		{
			CityVo cv = new CityVo();
			cv.setCity(ac.getStr(ac.MC));
			cv.setKey(ac.getStr(ac.ID));
			
			List<AbCityarea> list2 = AbCityarea.dao.find("select * from ab_cityarea m where m.type_id=0 and m.p_id='"+ac.getStr("id")+"' order by seq_num");
			
			List<CityVo> subs = new ArrayList();
			for (AbCityarea ac2:list2)
			{
				CityVo cv2 = new CityVo();
				cv2.setCity(ac2.getStr(ac2.MC));
				cv2.setKey(ac2.getStr(ac2.ID));
				subs.add(cv2);
			}
			cv.setSubs(subs);
			clist.add(cv);
		}
		
		renderJson(clist);
	}
	//城市列表
	public void cities(){
		List<AbCityarea> list = AbCityarea.dao.find("select * from ab_cityarea m where m.type_id=0 and m.p_id='ROOT' order by seq_num");
		List<CityVo> clist = new ArrayList();
		for (AbCityarea ac:list)
		{
			CityVo cv = new CityVo();
			cv.setCity(ac.getStr(ac.MC));
			cv.setKey(ac.getStr(ac.ID));
			
			List<AbCityarea> list2 = AbCityarea.dao.find("select * from ab_cityarea m where m.type_id=0 and m.p_id='"+ac.getStr("id")+"' order by seq_num");
			
			List<CityVo> subs = new ArrayList();
			for (AbCityarea ac2:list2)
			{
				CityVo cv2 = new CityVo();
				cv2.setCity(ac2.getStr(ac2.MC));
				cv2.setKey(ac2.getStr(ac2.ID));
				List<AbCityarea> list3 = AbCityarea.dao.find("select * from ab_cityarea m where m.type_id=0 and m.p_id='"+ac2.getStr("id")+"' order by seq_num");
				List<CityVo> subs2 = new ArrayList();
				for (AbCityarea ac3:list3)
				{
					CityVo cv3 = new CityVo();
					cv3.setCity(ac3.getStr(ac3.MC));
					cv3.setKey(ac3.getStr(ac3.ID));
					
					subs2.add(cv3);
				}
				cv2.setSubs(subs2);
				subs.add(cv2);
			}
			cv.setSubs(subs);
			clist.add(cv);
		}
		
		renderJson(clist);
	}
	//城市列表，按字母排序
	public void citylist2(){
		List<AbCityarea> list = AbCityarea.dao.find("select * from ab_cityarea m where m.type_id=0 and m.p_id!='ROOT' or mc='北京' or mc='上海' or mc='重庆' or mc='天津' order by seq_num");
		List<CityVo> clist = new ArrayList();
		for (AbCityarea ac:list)
		{
			CityVo cv = new CityVo();
			cv.setCity(ac.getStr(ac.MC));
			cv.setKey(ac.getStr(ac.ID));
			
			clist.add(cv);
		}
		CityComparator comparator = new CityComparator();  
	    Collections.sort(clist,comparator);  
		renderJson(clist);
	}
	//城市商圈
	public void cityarea(){
		String id = this.getPara("cityid");
		List<AbCityarea> list = AbCityarea.dao.find("select * from ab_cityarea m where m.type_id=1 and m.p_id=? order by seq_num",id);
		List<CityVo> clist = new ArrayList();
		for (AbCityarea ac:list)
		{
			CityVo cv = new CityVo();
			cv.setCity(ac.getStr(ac.MC));
			cv.setKey(ac.getStr(ac.ID));
			clist.add(cv);
		}
		renderJson(clist);
	}
	
	//添加至购物车
	public void addtocart(){
		String mid = this.getPara("mid");
		String proid = this.getPara("proid");
		int addnum = this.getParaToInt("addnum");
		SysUser user = getUser();
		//查询购物车中有没有当前商家的订单
		String sql = "select count(id) cnt from ab_order where ddzt='0' and xdrid=? and mid=?";
		String orderid = "";
		
		AbMerchant m = AbMerchant.dao.findById(mid);
		AbMerchantProduct p = AbMerchantProduct.dao.findById(proid);
		
		if(Db.findFirst(sql,user.getStr("id"),mid).getLong("cnt")>0){
			//说明存在此数据，将所选择的的商品添加到购物车中
			sql = "select id from ab_order where ddzt='0' and xdrid=? and mid=?";
			orderid = Db.findFirst(sql,user.getStr("id"),mid).getStr("id");
			//检查当前商品有没有
			sql = "select count(id) cnt from ab_order_item where orderid=? and itemid=?";
			if(Db.findFirst(sql,orderid,proid).getLong("cnt")>0){
				//修改商品的数量和总订单金额
				sql = "select * from ab_order_item where orderid=? and itemid=?";
				AbOrderItem item = AbOrderItem.dao.findFirst(sql,orderid,proid);
				item.set("quantity", addnum);
				BigDecimal b2 = new BigDecimal((item.getInt("quantity")));
				item.set("totalmoney",p.getBigDecimal("price").multiply(b2));
				item.set("price",p.getBigDecimal("price"));
				
				//添加商品
				BigDecimal fwf = null;
				//计算跑腿费，如果是自送，获取商家设置的跑腿费，如果是其他人送货，获取分类的跑腿费
				if ("1".equals(m.get("sfzs"))){
					fwf = Db.queryBigDecimal("SELECT MAX(ptf) FROM ab_merchant_product" +
							" WHERE MID = ? AND id IN(" +
							"SELECT itemid FROM ab_order_item WHERE orderid=?)",mid,orderid);
				}else{
					fwf = Db.queryBigDecimal("SELECT IFNULL(MAX(srvmoney),0) ptf FROM sys_area_subject_ptf WHERE area_id=? AND subject_id in(SELECT thr_id FROM ab_order_item WHERE orderid=?)",p.getStr("area_id"), orderid);
				}
				item.set("ptf", fwf);
				item.update();
				//计算总金额
			}else{
				//添加商品
				BigDecimal fwf = null;
				
				//计算跑腿费，如果是自送，获取商家设置的跑腿费，如果是其他人送货，获取分类的跑腿费
				if ("1".equals(m.get("sfzs"))){
					//fwf = p.getBigDecimal("ptf");
					fwf = Db.queryBigDecimal("SELECT MAX(ptf) FROM ab_merchant_product" +
							" WHERE MID = ? AND id IN(" +
							"SELECT itemid FROM ab_order_item WHERE orderid=?)",mid,orderid);
				}else{
					fwf = Db.queryBigDecimal("SELECT IFNULL(MAX(srvmoney),0) ptf FROM sys_area_subject_ptf WHERE area_id=? AND subject_id in(SELECT thr_id FROM ab_order_item WHERE orderid=?)",p.getStr("area_id"), orderid);
				}
				
				//添加商品
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
				item.set("quantity",addnum);
				item.set("price", p.getBigDecimal("price"));
				BigDecimal b2 = new BigDecimal(Double.valueOf(addnum));
				item.set("totalmoney",p.getBigDecimal("price").multiply(b2).doubleValue());
				item.set("ptf", fwf);
				item.set("mid", p.get("mid"));
				item.set("mname", p.get("mname"));
				item.save();
			}
			
		}else{
			//生成订单数据
			orderid = StringUtil.getUuid32();
			//保存购物车订单数据
			AbOrder order = new AbOrder();
			order.set("id", orderid);
			//生成订单号
			String sn = RandomUtil.createRandomNumPwd(9);
			while(Db.queryLong("select count(id) cnt from ab_order where sn='"+sn+"'").longValue()>0){
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
			order.set("zsjf", p.getInt("jf").intValue()*addnum);
			order.set("yzbm", user.get("yzbm"));
			order.set("shdz", user.get("xxdz"));
			order.set("lxr", user.get("mc"));
			order.set("lxrdh", user.get("mobile"));
			order.set("xdrid", user.get("id"));
			order.set("xdrmc", user.get("mc"));
			order.set("xdrdh", user.get("mobile"));
			order.set("xdsj", DateUtil.getCurrentDate());
			order.set("ddzt", "0");
			order.save();
			
			BigDecimal fwf = null;
			
			//计算跑腿费，如果是自送，获取商家设置的跑腿费，如果是其他人送货，获取分类的跑腿费
			if ("1".equals(m.get("sfzs"))){
				//fwf = p.getBigDecimal("ptf");
				fwf = Db.queryBigDecimal("SELECT MAX(ptf) FROM ab_merchant_product" +
						" WHERE MID = ? AND id IN(" +
						"SELECT itemid FROM ab_order_item WHERE orderid=?)",mid,orderid);
			}else{
				fwf = Db.queryBigDecimal("SELECT IFNULL(MAX(srvmoney),0) ptf FROM sys_area_subject_ptf WHERE area_id=? AND subject_id in(SELECT thr_id FROM ab_order_item WHERE orderid=?)",p.getStr("area_id"), orderid);
			}
			
			
			//添加商品
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
			item.set("quantity",addnum);
			item.set("price", p.getBigDecimal("price"));
			BigDecimal b2 = new BigDecimal(Double.valueOf(addnum));
			item.set("totalmoney",p.getBigDecimal("price").multiply(b2).doubleValue());
			item.set("ptf", fwf);
			item.set("mid", p.get("mid"));
			item.set("mname", p.get("mname"));
			item.save();
		}
		
		
		//获取商品总金额
		BigDecimal spzj = Db.queryBigDecimal("SELECT SUM(price * quantity) FROM ab_order_item WHERE orderid = ?",orderid);
		BigDecimal cjlfjf = Db.queryBigDecimal("SELECT ifnull(cjlfjf,0) FROM   ab_order WHERE  id = ?",orderid);
		BigDecimal ptf = Db.queryBigDecimal("SELECT IFNULL(Max(ptf),0) FROM   ab_order_item WHERE  orderid = ?",orderid);
		
		//计算跑腿费，如果是自送，获取商家设置的跑腿费，如果是其他人送货，获取分类的跑腿费
		if ("1".equals(m.get("sfzs"))){
			ptf = Db.queryBigDecimal("SELECT IFNULL(Max(ptf),0) FROM   ab_order_item WHERE  orderid = ?",orderid);
		}else{
			ptf = Db.queryBigDecimal("SELECT IFNULL(MAX(srvmoney),0) ptf FROM sys_area_subject_ptf WHERE area_id=? AND subject_id in(SELECT thr_id FROM ab_order_item WHERE orderid=?)",p.getStr("area_id"),orderid);
		}
		
		BigDecimal yhje = new BigDecimal(Double.valueOf("0"));
		//计算商家优惠、商品总金额、跑腿费、订单总金额
		if("1".equals(m.get("chk_yhhd"))){
			BigDecimal mds = m.getBigDecimal("mds");
			if(!(spzj.compareTo(mds)==-1)){
				yhje =  m.getBigDecimal("jds");
			}
		}
		
		BigDecimal ddzje = spzj.add(ptf).add(cjlfjf).subtract(yhje);
		
		sql ="UPDATE  ab_order SET spzj=?,yhje=?,ptf=?,cjlfjf=?,ddzje=? where id = '"+orderid+"'";
		Db.update(sql,spzj,yhje,ptf,cjlfjf,ddzje);
		
		BaseBean info = new BaseBean();
		renderJson(info);
	}
	
	//购物车
	public void mycart(){
		SysUser user = getUser();
		if(user!=null&&user.get("id")!=null){
			List<Record> list_order = null;//Db.find("select * from ab_order where ddzt='0' and xdrid=?",user.getStr("id"));
			String mid = this.getPara("mid");
			if (mid != null && !"".equals(mid))
			{
				list_order = Db.find("select * from ab_order where ddzt='0' and xdrid=? and mid=?",user.getStr("id"),mid);
			}
			else
			{
				list_order = Db.find("select * from ab_order where ddzt='0' and xdrid=?",user.getStr("id"));
			}
			if(list_order!=null&&list_order.size()>0){
				for (int i = 0; i < list_order.size(); i++) {
					list_order.get(i).set("list_order_item", Db.find("select * from ab_order_item where orderid=?",list_order.get(i).get("id")));
				}
			}
			
			renderJson(list_order);
		}
		else
		{
			BaseBean info = new BaseBean();
			info.setSucess(false);
			
			renderJson(info);
		}
		
	}
	
	//提交订单
	public void suborder()
	{
		String[] orderids = this.getParaValues("orderid");
		String sfdb = this.getPara("sfdb");
		
		String lxr = this.getPara("lxr");
		String lxrdh = this.getPara("lxrdh");
		String shdz = this.getPara("shdz");
		String memo = this.getPara("memo");
		String yqsdsj = this.getPara("yqsdsj");
		
		String total_spzj = this.getPara("total_spzj");
		String total_ptf = this.getPara("total_ptf");
		String total_yhje = this.getPara("total_yhje");
		String total_ddzje = this.getPara("total_ddzje");
		
		BigDecimal b_total_spzj = BigDecimal.valueOf(Double.valueOf(total_spzj));
		BigDecimal b_total_ptf = BigDecimal.valueOf(Double.valueOf(total_ptf));
		BigDecimal b_total_yhje = BigDecimal.valueOf(Double.valueOf(total_yhje));
		BigDecimal b_total_ddzje = BigDecimal.valueOf(Double.valueOf(total_ddzje));
		
		String zffs = StringUtil.toStr(this.getPara("zffs"));
//		SysUser user = this.getSessionAttr("abuser");
//		user = SysUser.dao.findById(user.get("id"));
		SysUser user = getUser();
		String userid = user.getStr(SysUser.ID);
		
//		boolean isSendCard = false;//是否有送餐卡
		String sendCardId = null;//送餐卡ID
		//判断是否有送餐会员卡可以抵扣配送费和超距离附加费
		List<Record> scards = Db.find(CommonSQL.findUserCard(userid, CommonStaticData.CARD_TYPE_SEND)); 
		if(scards != null && scards.size()>0){
			sendCardId = scards.get(0).getStr(AbCard.ID);
		}
		
		//订单实际支付金额
		BigDecimal finalprice = new BigDecimal(0);
		String str_date = DateUtil.getCurrentDate();
		
		if(orderids!=null&&orderids.length>0){
			for (int i = 0; i < orderids.length; i++) {
				//每个订单实际支付金额
				BigDecimal price = new BigDecimal(0);
				AbOrder order = AbOrder.dao.findById(orderids[i]);
				BigDecimal sendmoney = order.getBigDecimal("ptf");
				BigDecimal spzjmoney = order.getBigDecimal("spzj");
				BigDecimal yhjemoney = order.getBigDecimal("yhje"); 
				price = price.add(spzjmoney).subtract(yhjemoney);
				//判断是否使用送餐卡
				if(!StringUtil.isNull(sendCardId)&&!sendmoney.equals(0)){
					AbCardExpense ace = new AbCardExpense();
					ace.set(AbCardExpense.ID, StringUtil.getUuid32());
					ace.set(AbCardExpense.ORDER_ID, orderids[i]);
					ace.set(AbCardExpense.CARD_ID, sendCardId);
					ace.set(AbCardExpense.MONEY, sendmoney);
					ace.set(AbCardExpense.STATUS, CommonStaticData.CARD_EXPENSE_VALID);
					ace.save();
				}else{
					price = price.add(sendmoney);
				}
				
				//增加积分增长记录
				AbIntegralScore ail = new AbIntegralScore();
				ail.set(AbIntegralScore.ID, StringUtil.getRandString32());
				ail.set(AbIntegralScore.TYPE, CommonStaticData.SCORE_TYPE6);
				ail.set(AbIntegralScore.VALUE, sendmoney.add(spzjmoney).subtract(yhjemoney).intValue());
				ail.set(AbIntegralScore.DATETIME, str_date);
				ail.set(AbIntegralScore.USER_ID, userid);
				ail.set(AbIntegralScore.FID, orderids[i]);
				ail.save();
				
				//判断是否有消费会员卡抵扣金额
				List<Record> ecards = Db.find(CommonSQL.findUserCard(userid, CommonStaticData.CARD_TYPE_EXPENSE)); 
				if(ecards != null && ecards.size()>0){
					for(Record r : ecards){
						if(price.doubleValue() > 0){
							AbCard card = AbCard.dao.findById(r.getStr("id"));
							AbCardExpense ace = new AbCardExpense();
							ace.set(AbCardExpense.ID, StringUtil.getUuid32());
							ace.set(AbCardExpense.ORDER_ID, orderids[i]);
							ace.set(AbCardExpense.CARD_ID, card.get("id"));
							ace.set(AbCardExpense.STATUS, CommonStaticData.CARD_EXPENSE_VALID);
							//总价金额大于单张会员卡余额
							BigDecimal rmoney = r.getBigDecimal(AbCard.RMONEY);
							if(price.compareTo(r.getBigDecimal(AbCard.RMONEY))==1){
								//插入会员卡使用记录
								price = price.subtract(rmoney);
								ace.set(AbCardExpense.MONEY, rmoney);
								ace.save();
								//将会员卡，扣除余额，置为无效
								card.set(AbCard.STATUS, CommonStaticData.CARD_STATUS_INVALID);
								card.set(AbCard.RMONEY, new BigDecimal(0));
								card.update();
							}else{
								//插入会员卡使用记录
								ace.set(AbCardExpense.MONEY, price);
								ace.save();
								//将会员卡，扣除余额
								card.set(AbCard.RMONEY, card.getBigDecimal(AbCard.RMONEY).subtract(price));
								card.update();
								price = new BigDecimal(0);
							}
						}
					}
					finalprice = finalprice.add(price);
				}		
				//查找配送方式
				AbMerchant m = AbMerchant.dao.findById(order.get("mid"));
				order
				.set("lxr", lxr)
				.set("lxrdh", lxrdh)
				.set("shdz", shdz)
				.set("memo", memo)
				.set("yqsdsj", yqsdsj)
				.set("zffs", zffs)
				.set("ddzje", spzjmoney.add(sendmoney).subtract(yhjemoney))
				.set("psfs", "1".equals(m.get("sfzs"))?"0":"1")
				.set("sfdb", sfdb)
				.set("dbbh", StringUtil.getRandString32())
				.set("spzj", b_total_spzj)
				.set("ptf", b_total_ptf)
				.set("yhje", b_total_yhje)
				.set("ddzje", b_total_ddzje)
				.set("xdsj", str_date)
				.set("xdrid", user.get("id"))
				.set("xdrmc", user.get("mc"))
				.set("ddzt", "1");
				if("2".equals(zffs)){
					order.set("ddzt", "0");//如果是支付宝付款，此处状态变更由回调方法进行处理payback
				}
				order.update();
			}
		}
		
		//增加消费积分
		user.set(SysUser.JIFEN, user.getInt(SysUser.JIFEN)+ b_total_ddzje.intValue());
		user.update();
		//生成认证码，并发送短信
		String randcode = RandomUtil.createRandom(6);
		
		if(orderids!=null&&orderids.length>0){
			for (int i = 0; i < orderids.length; i++) {
				if(PrintUtils.pmRequest()){
					PrintUtils.sendRequest(PrintUtils.forMatterMsg(orderids[i]));
				}
			}
		}
		
		if("1".equals(zffs)){
			if(finalprice.doubleValue() > 0){
				user.set("zhye", user.getBigDecimal("zhye").subtract(finalprice));
				user.update();
			}
			if(orderids!=null&&orderids.length>0){
				for (int i = 0; i < orderids.length; i++) {
					AbOrder order = AbOrder.dao.findById(orderids[i]);
					order
					.set("zfzt", "1")
					.set("zfsj", str_date)
					.set("rzm", randcode)
					.update();
				}
			}
			
			if(orderids!=null&&orderids.length>0){
				for (int i = 0; i < orderids.length; i++) {
					try {
						AbOrder order = AbOrder.dao.findById(orderids[i]);
						SmsMessage.SendMessage(user.getStr("loginid"), user.getStr("mc")+"您好，订单号："+order.getStr("sn")+"下单成功，认证码："+randcode+"，请妥善保存你的认证码，凭认证码签收已购买的商品。");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			BaseBean info = new BaseBean();
			renderJson(info);
		}else if("2".equals(zffs)){
			//跳转到支付宝页面
			String tradeno = System.currentTimeMillis() + "";
			SysUser u = this.getSessionAttr("abuser");
			String user_id = u.getStr(SysUser.ID);
			//b_total_ddzje = new BigDecimal("0.01");//测试使用，上线必须注释掉
			if(orderids!=null&&orderids.length>0){
				for (int i = 0; i < orderids.length; i++) {
					AbOrder order = AbOrder.dao.findById(orderids[i]);
					SysCzTx cztxtemp = SysCzTx.dao.findFirst("select * from sys_cz_tx where userid='" + user_id + "' and orderid='" +  order.getStr("id") + "'");
					if(null != cztxtemp){
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
				alipayapi(tradeno,"eeee",b_total_ddzje.toString(),"在线购买",return_url);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}else if("3".equals(zffs)){ 
			//货到付款
			if(orderids!=null&&orderids.length>0){
				for (int i = 0; i < orderids.length; i++) {
					AbOrder order = AbOrder.dao.findById(orderids[i]);
					order
					.set("zfzt", "0")
					.set("rzm", randcode)
					.update();
				}
			}
			if(orderids!=null&&orderids.length>0){
				for (int i = 0; i < orderids.length; i++) {
					try {
						AbOrder order = AbOrder.dao.findById(orderids[i]);
						SmsMessage.SendMessage(user.getStr("loginid"), user.getStr("mc")+"您好，订单号："+order.getStr("sn")+"下单成功，认证码："+randcode+"，请妥善保存你的认证码，凭认证码签收已购买的商品。");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			BaseBean info = new BaseBean();
			renderJson(info);
		}
	}
	
	public String alipayapi(String WIDout_trade_no,String WIDsubject,
			String WIDtotal_fee,String WIDbody,String return_url) throws UnsupportedEncodingException{
		//查询支付宝相关信息
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
		//支付类型
		String payment_type = "1";
		//必填，不能修改
		//卖家支付宝帐户
		//String seller_email = new String(this.getPara("WIDseller_email").getBytes("ISO-8859-1"),"UTF-8");
		String seller_email = config.getStr("c5");
		//必填

		//商户订单号
		String out_trade_no = new String(WIDout_trade_no.getBytes("utf-8"),"UTF-8");
		//商户网站订单系统中唯一订单号，必填

		//订单名称
		String subject = new String(WIDsubject.getBytes("utf-8"),"UTF-8");
		//必填
		//付款金额
		String total_fee = new String(WIDtotal_fee.getBytes("utf-8"),"UTF-8");
		//必填
		//订单描述

		String body = new String(WIDbody.getBytes("utf-8"),"UTF-8");
		//商品展示地址
		String show_url = "http://www.yijuhome.cn";
		//需以http://开头的完整路径，例如：http://www.商户网址.com/myorder.html
		//把请求参数打包成数组
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
		//建立请求
		String sHtmlText = AlipaySubmit.buildRequest(sParaTemp,"get","确认");
		//this.redirect(sHtmlText);
		return sHtmlText;
	}
	
	//订单列表
	public void orderlist()
	{
		SysUser user = getUser();
		List<AbOrder> list = AbOrder.dao.find("select * from ab_order where xdrid=? order by xdsj desc",user.getStr("id"));
		if(list!=null&&list.size()>0){
			for (int i = 0; i < list.size(); i++) {
				List<AbOrderItem> item = AbOrderItem.dao.find("select * from ab_order_item where orderid='"+list.get(i).getStr("id")+"'");
				list.get(i).put("listitem", item);
			}
		}
		renderJson(list);
	}
	
	//订单
	public void order()
	{
		String id = this.getPara("id");
		
		AbOrder vo = null;
		if(StringUtil.toStr(id).length()>0){
			vo = AbOrder.dao.findById(id);
		}
		
		if (vo != null)
		{
			renderJson(vo);
		}else
		{
			BaseBean info = new BaseBean();
			info.setSucess(false);
			renderJson(info);
		}
	}
	
	//删除购物车中商品
	public void delorder()
	{
		String id = StringUtil.toStr(this.getPara("id"));		//删除订单中的商品
		AbOrderItem item = AbOrderItem.dao.findById(id);
		
		//查询是否为唯一商品
		if(Db.queryLong("select count(id) from ab_order_item where orderid=?",item.get("orderid")).longValue()>1){
			AbOrderItem.dao.deleteById(id);
			//获取订单明细和商家信息
			AbOrder order = AbOrder.dao.findById(item.get("orderid"));
			AbMerchant m = AbMerchant.dao.findById(order.get("mid"));
			
			//获取商品总金额
			BigDecimal spzj = Db.queryBigDecimal("SELECT SUM(price * quantity) FROM ab_order_item WHERE orderid = ?",order.get("id"));
			BigDecimal ptf = Db.queryBigDecimal("SELECT ifnull(Max(ptf),0) FROM   ab_order_item WHERE  orderid = ?",order.get("id"));
			BigDecimal cjlfjf = Db.queryBigDecimal("SELECT ifnull(cjlfjf,0) FROM   ab_order WHERE  id = ?",order.get("id"));
			
			BigDecimal yhje = new BigDecimal(Double.valueOf("0"));
			//计算商家优惠、商品总金额、跑腿费、订单总金额
			if("1".equals(m.get("chk_yhhd"))){
				BigDecimal mds = m.getBigDecimal("mds");
				if(!(spzj.compareTo(mds)==-1)){
					yhje =  m.getBigDecimal("jds");
				}
			}
			
			BigDecimal ddzje = spzj.subtract(yhje).add(ptf).add(cjlfjf);	//订单总金额
			order.set("spzj", spzj).set("yhje", yhje).set("ptf", ptf).set("ddzje", ddzje).update();
		}else{
			Db.deleteById("ab_order", item.get("orderid"));
		}
		BaseBean info = new BaseBean();
		renderJson(info);
	}
	
	//用户申请退单
	public void addchargeback() {
		SysUser abuser = getUser();
		String orderId = this.getPara("orderid");
		String content = this.getPara("content");
		String img_url = this.getPara("apyimg");
		if (abuser == null || StringUtil.isNull(orderId)
				|| StringUtil.isNull(content)) {
			BaseBean info = new BaseBean();
			info.setSucess(false);
			renderJson(info);
		} else {
			// 插入退单记录
			AbOrder order = AbOrder.dao.findById(orderId);
			AbOrderChargeback cb = new AbOrderChargeback();
			cb.set(AbOrderChargeback.ID, StringUtil.getUuid32());
			cb.set(AbOrderChargeback.MER_ID, order.getStr(AbOrder.MID));
			cb.set(AbOrderChargeback.XDR_ID, order.getStr(AbOrder.XDRID));
			cb.set(AbOrderChargeback.QDR_ID, order.getStr(AbOrder.QDRID));
			cb.set(AbOrderChargeback.ORDER_ID, orderId);
			cb.set(AbOrderChargeback.APPLY_ID, abuser.get(SysUser.ID));
			cb.set(AbOrderChargeback.APPLY_DESC, content);
			cb.set(AbOrderChargeback.APPLY_TIME, DateUtil.getCurrentDate());
			cb.set(AbOrderChargeback.STATUS, CommonStaticData.CB_STATUS1);
			// 申请人类型 1 用户 2商户 3业务员
			if (CommonStaticData.USER_TYPE_MEMBER.equals(abuser
					.get(SysUser.ROLE_ID))) {
				cb.set(AbOrderChargeback.CB_STATUS, CommonStaticData.CB_TYPE1);
				cb.set(AbOrderChargeback.APPLY_TYPE,
						CommonStaticData.CB_APPLYTYPE_MEMBER);
			} else if (CommonStaticData.USER_TYPE_MERCHANT.equals(abuser
					.get(SysUser.ROLE_ID))) {
				cb.set(AbOrderChargeback.CB_STATUS, CommonStaticData.CB_TYPE2);
				cb.set(AbOrderChargeback.APPLY_TYPE,
						CommonStaticData.CB_APPLYTYPE_MERCHANT);
			} else if (CommonStaticData.USER_TYPE_SERVICE.equals(abuser
					.get(SysUser.ROLE_ID))) {
				cb.set(AbOrderChargeback.CB_STATUS, CommonStaticData.CB_TYPE8);
				cb.set(AbOrderChargeback.APPLY_TYPE,
						CommonStaticData.CB_APPLYTYPE_SERVICE);
			}
			if (!StringUtil.isNull(img_url)) {
				FileInputStream file = null;
				try {
					file = new FileInputStream(PathKit.getWebRootPath()
							+ "\\upload\\" + img_url);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				cb.set(AbOrderChargeback.APPLY_IMG_URL, img_url);
				cb.set(AbOrderChargeback.APPLY_IMG, file);
			}
			cb.save();
			BaseBean info = new BaseBean();
			renderJson(info);
		}
	}
	
	public void upload(){
		UploadFile f = this.getFile();
		String filename=StringUtil.getUuid32()+StringUtil.getExt(f.getFileName());
		f.getFile().renameTo(new File(f.getSaveDirectory()+ filename));
		//对文件进行缩略图生成
//		ImageScale is = new ImageScale();
//		try {
//			is.saveImageAsJpg(f.getSaveDirectory()+ filename, f.getSaveDirectory()+ StringUtil.getThumbnail(filename), 800, 600);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
//		(new File(f.getSaveDirectory()+f.getOriginalFileName())).delete();
		renderText(filename);
	}
	
	//催单
	public void addleave(){
		SysUser abuser = getUser();
		String mid = this.getPara("mid");
		String content = this.getPara("content");
		if(abuser == null ||StringUtil.isNull(mid)||StringUtil.isNull(content)){
			BaseBean info = new BaseBean();
			info.setSucess(false);
			renderJson(info);
		}else{
			AbMerchantLeave leave = new AbMerchantLeave();
			leave.set(AbMerchantLeave.ID, StringUtil.getUuid32());
			leave.set(AbMerchantLeave.CONTENT, content);
			leave.set(AbMerchantLeave.MER_ID, mid);
			leave.set(AbMerchantLeave.USER_ID, abuser.get(SysUser.ID));
			leave.set(AbMerchantLeave.DATETIME, DateUtil.getCurrentDate());
			leave.set(AbMerchantLeave.ORDER_ID, this.getPara("orderid"));
			leave.save();
			BaseBean info = new BaseBean();
			renderJson(info);
		}
	}
	
	//取消订单
	public void cancelOrder () {
		String sn = getPara("sn");
		String uid = getPara("uid");
		SysUser abuser = getUser();
		if(!StringUtil.isNull(sn) && abuser != null) {
			AbOrder ateo = AbOrder.dao.findFirst("SELECT * FROM ab_order WHERE sn=?", sn);
			if(null != ateo) {
				if("1".equals(ateo.get(AbOrder.DDZT))) {
					ateo.set(AbOrder.QDRID, uid);
					ateo.set(AbOrder.DDZT, 6);
				     if(ateo.update()) {
				    	 BaseBean info = new BaseBean();
				    	 info.setMsg("拒单成功");
						 renderJson(info);
				     } else {
				    	 BaseBean info = new BaseBean();
				    	 info.setSucess(false);
				    	 info.setMsg("拒单失败,请稍后重试");
						 renderJson(info);
				     }
				} else {
					BaseBean info = new BaseBean();
			    	 info.setSucess(false);
			    	 info.setMsg("订单状态错误");
					 renderJson(info);
				}
			} else {
				BaseBean info = new BaseBean();
		    	 info.setSucess(false);
		    	 info.setMsg("订单状态错误");
				 renderJson(info);
			}
		} else {
			BaseBean info = new BaseBean();
	    	 info.setSucess(false);
	    	 info.setMsg("订单不存在");
			 renderJson(info);
		}
	}
	
	//签收
	public void signOrder () {
		String orderId = getPara("sn");
		SysUser abuser = getUsernum();
		if(!StringUtil.isNull(orderId) && abuser != null) {
			if (orderId.indexOf(",") > 1) {

				String[] arr = orderId.split(",");
				StringBuffer sn = new StringBuffer();
				for (String str : arr) {
					if (sn.length() > 1) {
						sn.append(",");
					}
					sn.append("'").append(str).append(",");
				}

				String sql = "update ab_order set ddzt=5 where ddzt=4 and sn in("
						+ sn.toString() + ")";
				int s = Db.update(sql);
				if(s>0){
					 BaseBean info = new BaseBean();
			    	 info.setSucess(true);
			    	 info.setMsg("签收成功");
					 renderJson(info);
				}
			} else {
			
			
			AbOrder ateo = AbOrder.dao.findFirst("SELECT * FROM ab_order WHERE sn=?", orderId);
			if(null != ateo) {
				if("4".equals(ateo.get(AbOrder.DDZT))) {
					ateo.set(AbOrder.DDZT, 5);
				     if(ateo.update()) {
				    	 BaseBean info = new BaseBean();
				    	 info.setSucess(true);
				    	 info.setMsg("签收成功");
						 renderJson(info);
				     } else {
				    	 BaseBean info = new BaseBean();
				    	 info.setSucess(false);
				    	 info.setMsg("签收失败,请稍后重试");
						 renderJson(info);
				     }
				} else {
					BaseBean info = new BaseBean();
			    	 info.setSucess(false);
			    	 info.setMsg("订单状态错误");
					 renderJson(info);
				}
			} else {
				BaseBean info = new BaseBean();
		    	 info.setSucess(false);
		    	 info.setMsg("订单状态错误");
				 renderJson(info);
			}
			}
		} else {
			BaseBean info = new BaseBean();
	    	 info.setSucess(false);
	    	 info.setMsg("订单不存在");
			 renderJson(info);
		}
	}
	
	/**
	 * 评价查询接口
	 * app 商户或跑腿人员评价查询 参数 uid 用户ID type 评价类型 1 好评 2中评 3差评 0全部 imgchecked 是否显示图片
	 * 1显示 0不显示
	 * 
	 * 返回值 list
	 * 
	 */
	public void findappraise() {
		String uid = this.getPara("uid");
		String type = this.getPara("type");
		String imgchecked = this.getPara("imgchecked");
		List<Record> apps = null;
		if (StringUtil.isNull(uid) || StringUtil.isNull(type)
				|| StringUtil.isNull(imgchecked)) {
			 BaseBean info = new BaseBean();
	    	 info.setSucess(false);
	    	 info.setMsg("参数不对");
			 renderJson(info);
		} else {
			// 判断 uid是商户还是跑腿人员
			SysUser su = SysUser.dao.findById(uid);
			if (su != null) {
				if (CommonStaticData.USER_TYPE_MERCHANT.equals(su
						.getStr(SysUser.ROLE_ID))) {
					// 全部记录
					if ("0".equals(type)) {
						apps = Db.find(CommonSQL.getAppraiseByMid(uid));
					} else {
						apps = Db.find(CommonSQL.getAppraiseByMidAndType(uid,
								type));
					}
				} else if (CommonStaticData.USER_TYPE_SERVICE.equals(su
						.getStr(SysUser.ROLE_ID))) {
					apps = Db.find(CommonSQL.getAppraiseByQdrid(uid));
				}
				// 显示图片
				if ("1".equals(imgchecked)) {
					for (int i = 0; i < apps.size(); i++) {
						List<Record> imgs = Db.find(CommonSQL
								.getAppraiseImgByAppId(apps.get(i).getStr(
										AbAppraise.ID)));
						apps.get(i).set("appsimgs", imgs);
						if (imgs != null && imgs.size() > 0) {
							CommonProcess.createImageFile(imgs);
						}
					}
				}
				this.renderJson(apps);
			}
		}
	}
	
	/**
	 * 根据用户ID，orderid查询
	 * uid:用户id
	 * orderid：订单id
	 */
	public void findorderappraise() {
		String uid = this.getPara("uid");
		String orderid = this.getPara("orderid");
		if (StringUtil.isNull(uid) || StringUtil.isNull(orderid)) {
			 BaseBean info = new BaseBean();
	    	 info.setSucess(false);
	    	 info.setMsg("参数不对");
			 renderJson(info);
		} else {
			String sql = "select * from ab_appraise where  user_id = ? and order_id = ?";
			List<AbAppraise> apps = AbAppraise.dao.find(sql,uid,orderid);
			renderJson(apps);
		}
	}
	/**
	 * 增加评价
	 * app 增加评价 参数 orderid 订单ID userid 用户ID appmid 商户ID mah 动态评分-描述相符 ser
	 * 动态评分-服务态度 spd 动态评分-发货速度 pvte 是否匿名 1公开 2匿名 appraisetype 评价类型 1 好评 2中评3差评
	 * appraisecontent 描述 appimage 图片路径,多个图片用","分割
	 * 
	 * 返回值 true or false
	 * 
	 */
	public void addappraise() {
		String orderid = this.getPara("orderid");
		String userid = this.getPara("userid");
		String mid = this.getPara("appmid");
		String type = this.getPara("appraisetype");
		String pri = this.getPara("private");
		String content = this.getPara("appraisecontent");
		String appimage = this.getPara("appimage");
		String mahstr = this.getPara("mah");
		String serstr = this.getPara("ser");
		String spdstr = this.getPara("spd");

		if (StringUtil.isNull(orderid) || StringUtil.isNull(userid)
				|| StringUtil.isNull(mid) || StringUtil.isNull(type)
				|| StringUtil.isNull(pri) || StringUtil.isNull(content)
				|| StringUtil.isNull(mahstr) || StringUtil.isNull(serstr)
				|| StringUtil.isNull(spdstr)) {
			renderJson(false);
		} else {
			try {
				BigDecimal mah = new BigDecimal(Integer.valueOf(mahstr));
				BigDecimal ser = new BigDecimal(Integer.valueOf(serstr));
				BigDecimal spd = new BigDecimal(Integer.valueOf(spdstr));
				AbOrder order = AbOrder.dao.findById(orderid);
				String qdrid = order.getStr(AbOrder.QDRID);
				BigDecimal total = mah.add(ser).add(spd);

				AbAppraise app = new AbAppraise();
				String appId = StringUtil.getUuid32();
				app.set(AbAppraise.ID, appId);
				app.set(AbAppraise.CONTENT, content);
				app.set(AbAppraise.MER_ID, mid);
				app.set(AbAppraise.USER_ID, userid);
				app.set(AbAppraise.ORDER_ID, orderid);
				app.set(AbAppraise.QDR_ID, qdrid);
				app.set(AbAppraise.TYPE, type);
				app.set(AbAppraise.PRIVATE, pri);
				app.set(AbAppraise.DYNC_MAH, mah.intValue());
				app.set(AbAppraise.DYNC_SER, ser.intValue());
				app.set(AbAppraise.DYNC_SPD, spd.intValue());
				app.set(AbAppraise.DYNC_VAL,
						total.divide(new BigDecimal(3), 1,
								BigDecimal.ROUND_HALF_UP).doubleValue());
				app.set(AbAppraise.DATETIME, DateUtil.getCurrentDate());
				app.save();

				if (!StringUtil.isNull(appimage)) {
					String[] img_urls = appimage.split(",");
					if (img_urls != null) {
						for (String url : img_urls) {
							AbAppraiseImg aai = new AbAppraiseImg();
							FileInputStream file = null;

							file = new FileInputStream(PathKit.getWebRootPath()
									+ "\\upload\\" + url);

							aai.set(AbAppraiseImg.ID, StringUtil.getUuid32());
							aai.set(AbAppraiseImg.APP_ID, appId);
							aai.set(AbAppraiseImg.IMAGE, file);
							aai.set(AbAppraiseImg.IMG_URL, url);
							aai.save();
						}
					}
				}
				// 增加而修改商户,跑腿人员信用等级和客户评价
				CommonProcess.updateMechantCreditAndRate(mid, type, true);
				CommonProcess.updateQDRCreditAndRate(qdrid, mid, type, true);
				renderJson(true);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				renderJson(false);
			}
		}
	}

	/**
	 * app 删除评价 参数 id 评价记录ID 返回值 true or false
	 * 
	 */
	public void deleteappraise() {
		String id = this.getPara("id");
		if (StringUtil.isNull(id)) {
			this.renderJson(false);
		} else {
			CommonProcess.deleteappraise(id);
			this.renderJson(true);
		}
	}
	
	/**
	 * 增加催单回复接口
	 * 参数
	 *    lid  催单留言ID
	 *    mid	  商户ID
	 *    content 描述
	 *    
	 * 返回值 true or false
	 */
	public void addreply(){
		
		String lid = this.getPara("lid");
		String content = this.getPara("content");
		
		if(StringUtil.isNull(lid)||StringUtil.isNull(content)){
			this.renderJson(false);
		}else{
			AbMerchantReply reply = new AbMerchantReply();
			reply.set(AbMerchantReply.ID, StringUtil.getUuid32());
			reply.set(AbMerchantReply.CONTENT, content);
			reply.set(AbMerchantReply.DATETIME, DateUtil.getCurrentDate());
			reply.set(AbMerchantReply.LEAVE_ID,  lid);
			reply.save();
			this.renderJson(true);
		}
	}
	
	/**
	 * 查询催单
	 * 参数
	 *    mid	  商户ID
	 *    
	 * 返回值 list
	 */
	public void listleave(){
		String mid = this.getPara("mid");
		List<Record> records = null;
		if(StringUtil.isNull(mid)){
		}else{
			records = Db.find("select am.*, su.mc from ab_merchant_leave am left join sys_user su on am.user_id = su.id where mer_id ='"+ mid+"' order by datetime desc");
		}
		this.renderJson(records);
	}
	
	/**
	 * 根据orderid查询催单
	 * orderid：订单id
	 */
	public void findlistleave() {
		String orderid = this.getPara("orderid");
		if (StringUtil.isNull(orderid)) {
			 BaseBean info = new BaseBean();
	    	 info.setSucess(false);
	    	 info.setMsg("参数不对");
			 renderJson(info);
		} else {
			String sql = "select * from ab_merchant_leave where  order_id = ?";
			List<AbMerchantLeave> apps = AbMerchantLeave.dao.find(sql,orderid);
			renderJson(apps);
		}
	}
	
	/**
	 * 根据orderid查询退單
	 * orderid：订单id
	 */
	public void findlistback() {
		String orderid = this.getPara("orderid");
		if (StringUtil.isNull(orderid)) {
			 BaseBean info = new BaseBean();
	    	 info.setSucess(false);
	    	 info.setMsg("参数不对");
			 renderJson(info);
		} else {
			String sql = "select * from ab_order_chargeback where  order_id = ?";
			List<AbOrderChargeback> apps = AbOrderChargeback.dao.find(sql,orderid);
			renderJson(apps);
		}
	}
	/**
	 * 商户回复退单 参数 userid 用户ID cbid 退单ID agree 1同意退单，2申诉 content 描述
	 * 
	 * 返回值 true or false
	 */
	public void addcbReponse() {
		String userid = this.getPara("userid");
		String cbid = this.getPara("cbid");
		String agree = this.getPara("agree");
		String content = this.getPara("content");
		if (StringUtil.isNull(userid) || StringUtil.isNull(cbid)
				|| StringUtil.isNull(agree) || StringUtil.isNull(content)) {
			this.renderJson(false);
		} else {
			AbOrderChargeback cb = AbOrderChargeback.dao.findById(cbid);
			cb.set(AbOrderChargeback.REP_DESC, content);
			cb.set(AbOrderChargeback.REP_TIME, DateUtil.getCurrentDate());
			// 同意
			if (agree.equals("1")) {
				cb.set(AbOrderChargeback.CB_STATUS, CommonStaticData.CB_TYPE6);
				cb.set(AbOrderChargeback.STATUS, CommonStaticData.CB_STATUS3);
				// 金额
				AbOrder order = AbOrder.dao.findById(cb
						.getStr(AbOrderChargeback.ORDER_ID));
				BigDecimal money = order.getBigDecimal(AbOrder.DDZJE);
				// 更新订单状态
				order.set(AbOrder.DDZT, "5");
				order.update();
				// 商户余额金额减少
				SysUser mer = SysUser.dao.findById(userid);
				BigDecimal merye = mer.getBigDecimal(SysUser.ZHYE);
				mer.set(SysUser.ZHYE, merye.subtract(money));
				mer.update();
				// 用户余额增加
				SysUser user = SysUser.dao.findById(cb
						.getStr(AbOrderChargeback.XDR_ID));
				BigDecimal userye = mer.getBigDecimal(SysUser.ZHYE);
				user.set(SysUser.ZHYE, userye.add(money));
				// 判断是否已经支付，如果已经支付，则扣除积分
				if ("1".equals(order.getStr(AbOrder.ZFZT))) {
					int jf = user.getInt(SysUser.JIFEN);
					int mjf = money.setScale(0, BigDecimal.ROUND_HALF_UP)
							.intValue();
					user.set(SysUser.JIFEN, jf - mjf);
				}
				user.update();
			} else {
				cb.set(AbOrderChargeback.CB_STATUS, CommonStaticData.CB_TYPE3);
			}
			cb.update();
			this.renderJson(true);
		}
	}
	
	/**
	 * app 在线升级
	 */
	public void version() {
		Version v = new Version();
		renderJson(v);
	}
	
	/**
	 * 商户回复退单 参数 userid 用户ID cbid 退单ID agree 0不同意退款，1同意退款 2不同意退款并客服介入 content 描述
	 * 
	 * 返回值 true or false
	 */
	public void addcbJudge() {
		String cbid = this.getPara("cbid");
		String agree = this.getPara("agree");
		String content = this.getPara("content");
		String userid = this.getPara("userid");
		if (StringUtil.isNull(cbid) || StringUtil.isNull(agree)
				|| StringUtil.isNull(content)|| StringUtil.isNull(userid)) {
			this.renderJson(false);
		} else {
			AbOrderChargeback cb = AbOrderChargeback.dao.findById(cbid);
			// 取消退单
			if (agree.equals("1")) {
				cb.set(AbOrderChargeback.CB_STATUS, CommonStaticData.CB_TYPE4);
				cb.set(AbOrderChargeback.STATUS, CommonStaticData.CB_STATUS3);
			} else if (agree.equals("0")) {
				cb.set(AbOrderChargeback.CB_STATUS, CommonStaticData.CB_TYPE9);
			} else {
				cb.set(AbOrderChargeback.JUDGE_ID, userid);
				cb.set(AbOrderChargeback.JUDGE_DESC, content);
				cb.set(AbOrderChargeback.JUDGE_TIME, DateUtil.getCurrentDate());
				cb.set(AbOrderChargeback.CB_STATUS, CommonStaticData.CB_TYPE5);
				cb.set(AbOrderChargeback.STATUS, CommonStaticData.CB_STATUS2);
			}
			cb.update();
			this.renderJson(true);
		}
	}
	
	/**
	 * 打印
	 * 
	 */
	public void printorder() {
		String mid = this.getPara("mid");
		String id = this.getPara("orderid");
		AbMerchantPrint amp = AbMerchantPrint.dao.findFirst("select * from ab_merchant_print where mid=?", mid);
		if(null != amp && "1".equals(amp.getStr("iswork"))){
			PrintUtils.partner = amp.getStr("partner");
			PrintUtils.machine_code = amp.getStr("machinecode");
			PrintUtils.apiKey = amp.getStr("apikey");
			PrintUtils.mKey = amp.getStr("mkey");
		}
		if(PrintUtils.pmRequest()){
			PrintUtils.sendRequest(PrintUtils.forMatterMsg(id));
		}
		this.renderJson(true);
	}
	
	private  SysUser getUser()
	{
		String username = this.getPara("loginnum");
        String password = this.getPara("password");
		return SysUser.dao.findFirst("select * from sys_user where loginid=? and loginpwd=?", username,MD5.GetMD5Code(password));
	}
	private  SysUser getUsernum()
	{
		String uid = this.getPara("uid");
		return SysUser.dao.findFirst("select * from sys_user where id=? ", uid);
	}
	
}
