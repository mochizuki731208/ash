package com.zp.controller.admin;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.mysql.jdbc.StringUtils;
import com.zp.entity.AbAddress;
import com.zp.entity.AbCityarea;
import com.zp.entity.AbFmcar;
import com.zp.entity.AbFmcarCity;
import com.zp.entity.AbOrder;
import com.zp.entity.AbPolicyRate;
import com.zp.entity.AbSjPosition;
import com.zp.entity.AbTcConfig;
import com.zp.entity.AbTcExpressOrder;
import com.zp.entity.SysConfig;
import com.zp.entity.SysCzTx;
import com.zp.entity.SysRole;
import com.zp.entity.SysUser;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.tools.CityAttachAdminUtil;
import com.zp.tools.DateUtil;
import com.zp.tools.MD5;
import com.zp.tools.RandomUtil;
import com.zp.tools.StringUtil;
import com.zp.tools.alipay.AlipaySubmit;
@SuppressWarnings("static-access")
public class SysUserController extends Controller {
	
	public void list(){
		SysUser user = (SysUser)this.getSessionAttr("user");
		String loginid = StringUtil.toStr(this.getPara("loginid"));
		String roleid = StringUtil.toStr(this.getPara("roleid"));
		String sql = "select * from sys_user where id is not null ";
		if(loginid.length()>0){
			sql+=" and loginid like '%"+loginid+"%'";
		}
		if(roleid.length()>0){
			sql+=" and role_id='"+roleid+"'";
		}
		
		if(!user.getStr("role_id").equals("101")){
			sql+=" and role_id >'104'";
//			sql += " and city_id='" + user.getStr("id") + "'";
			sql += " and (city_id in (" + CityAttachAdminUtil.getCityByAdmin(user.getStr("id")) + ")";
			sql += " or area_id in (" + CityAttachAdminUtil.getCityByAdmin(user.getStr("id")) + "))";
			sql += " union select * from sys_user where id = '" + user.getStr("id") + "'";
			if(loginid.length()>0){
				sql+=" and loginid like '%"+loginid+"%'";
			}
		}
		
		sql+=" order by create_date asc";
		
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		String sqlRole = "";
		if(!user.getStr("role_id").equals("101")){
			sqlRole = "select * from sys_role where id >'104' order by sxh";
		}else{
			sqlRole = "select * from sys_role order by sxh";
		}
		List<SysRole> list_role = SysRole.dao.find(sqlRole);
		this.setAttr("listpage", listpage);
		this.setAttr("list_role", list_role);
		this.setAttr("roleid", roleid);
		this.setAttr("loginid", loginid);
		this.render("/admin/user/list.html");
	}
	
	// 保险费率管理
	public void managePolicyRate() {
		AbPolicyRate apr = AbPolicyRate.dao.findFirst("select * from ab_policy_rate");
		this.setAttr("apr", apr);
		render("/admin/user/managePolicyRate.html");
	}
	
	public void updatePolicyRate() {
		String id = this.getPara("id");
		String to_self_rate = this.getPara("to_self_rate");
		String to_insurer_rate = this.getPara("to_insurer_rate");
		AbPolicyRate rate = AbPolicyRate.dao.findById(id);
		rate.set("to_self_rate", to_self_rate);
		rate.set("to_insurer_rate", to_insurer_rate);
		if(rate.update()) {
			renderJson("1");
		} else {
			renderJson("2");
		}
	}
	
	/**
	 * 密码重置
	 */
	public void listusermmcz(){
		SysUser user = (SysUser)this.getSessionAttr("user");
		String loginid = StringUtil.toStr(this.getPara("loginid"));
		String roleid = StringUtil.toStr(this.getPara("roleid"));
		String sql = "select * from sys_user where id is not null ";
		if(loginid.length()>0){
			sql+=" and loginid like '%"+loginid+"%'";
		}
		if(roleid.length()>0){
			sql+=" and role_id='"+roleid+"'";
		}
		if(!user.getStr("role_id").equals("101")){
			sql+=" and role_id >'104'";
//			sql += " and p_id='" + user.getStr("id") + "'";
			sql += " and (city_id in (" + CityAttachAdminUtil.getCityByAdmin(user.getStr("id")) + ")";
			sql += " or area_id in (" + CityAttachAdminUtil.getCityByAdmin(user.getStr("id")) + "))";
			sql += " union select * from sys_user where id = '" + user.getStr("id") + "'";
			if(loginid.length()>0){
				sql+=" and loginid like '%"+loginid+"%'";
			}
		}
		
		sql+=" order by create_date asc";
		
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		String sqlRole = "";
		if(!user.getStr("role_id").equals("101")){
			sqlRole = "select * from sys_role where id >'104' order by sxh";
		}else{
			sqlRole = "select * from sys_role order by sxh";
		}
		List<SysRole> list_role = SysRole.dao.find(sqlRole);
		this.setAttr("listpage", listpage);
		this.setAttr("list_role", list_role);
		this.setAttr("roleid", roleid);
		this.setAttr("loginid", loginid);
		this.render("/admin/user/listusermmcz.html");
	}
	
	public void listcityuser(){
		String loginid = StringUtil.toStr(this.getPara("loginid"));
		String sql = "select * from sys_user where id is not null ";
		SysUser u = this.getSessionAttr("user");
		if("103".equals(u.get("role_id"))){
			if(u.getStr("id").length() > 0){
				sql += " and (city_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + ")";
				sql += " or area_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + "))";
			}
		}
		
		sql+=" and role_id='104' order by create_date asc";
		
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		this.setAttr("listpage", listpage);
		this.setAttr("loginid", loginid);
		this.render("/admin/user/listcityuser.html");
	}
	
	public void listcitymng(){
		String loginid = StringUtil.toStr(this.getPara("loginid"));
		String sql = "select * from sys_user where id is not null ";
		SysUser u = this.getSessionAttr("user");
		if(loginid.length()>0){
			sql+=" and loginid like '%"+loginid+"%'";
		}
		if("103".equals(u.get("role_id"))){
			if(u.getStr("id").length() > 0){
				sql += " and (city_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + ")";
				sql += " or area_id in (" + CityAttachAdminUtil.getCityByAdmin(u.getStr("id")) + "))";
			}
		}
		sql+=" and role_id='103' order by create_date asc";
		
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		this.setAttr("listpage", listpage);
		this.setAttr("loginid", loginid);
		this.render("/admin/user/listcitymng.html");
	}
	
	public void delete(){
		boolean flag = false;
		try {
			String id = this.getPara("id");
			SysUser user = SysUser.dao.findById(id);
			SysUser.dao.deleteById(id);
			//删除附件
			File f = null;
			if(StringUtil.toStr(user.get("sfzzpzm")).length()>0){
				f = new File(PathKit.getWebRootPath() + "\\upload\\"+user.get("sfzzpzm"));
				if(f.exists())
					f.delete();
			}
			
			if(StringUtil.toStr(user.get("sfzzpbm")).length()>0){
				f = new File(PathKit.getWebRootPath() + "\\upload\\"+user.get("sfzzpbm"));
				if(f.exists())
					f.delete();
			}
			if(StringUtil.toStr(user.get("gzzp")).length()>0){
				f = new File(PathKit.getWebRootPath() + "\\upload\\"+user.get("gzzp"));
				if(f.exists())
					f.delete();
			}
			
			if(StringUtil.toStr(user.get("logo")).length()>0){
				f = new File(PathKit.getWebRootPath() + "\\upload\\"+user.get("logo"));
				if(f.exists())
					f.delete();
			}
			
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.renderJson(flag);
	}
	
	public void save(){
		SysUser  user = this.getModel(SysUser.class,"user");
		if(null==user.getStr("city_id") || user.getStr("city_id").length() == 0){
			SysUser admin = (SysUser)this.getSessionAttr("user");
//			user.set("city_id", admin.getStr("city_id"));
			AbCityarea aca = AbCityarea.dao.findFirst("select * from ab_cityarea where user_id=?", admin.getStr("id"));
			if(null!=aca){
				user.set("city_id", aca.getStr("id"));
			}
		}
		boolean flag = false;
		if(user!=null&&StringUtil.toStr(user.getStr("id")).length()>0){
			//查詢以前的密碼，如果一致，則不進行加密
			if(!user.get("loginpwd").equals(this.getPara("oldpwd"))){
				user.set("loginpwd", MD5.GetMD5Code(user.getStr("loginpwd")));
			}
			flag = user.update();
		}else{
			user.set("id", StringUtil.getUuid32());
			user.set("create_date",DateUtil.getCurrentDate());
			user.set("loginpwd", MD5.GetMD5Code(user.getStr("loginpwd")));
			flag = user.save();
		}
		
		this.renderJson(flag);
	}
	
	
	public void save_modifypwd(){
		SysUser  user = this.getModel(SysUser.class,"user");
		
		boolean flag = false;
		if(user!=null&&StringUtil.toStr(user.getStr("id")).length()>0){
			//查詢以前的密碼，如果一致，則不進行加密
			if(!user.get("loginpwd").equals(this.getPara("oldpwd"))){
				user.set("loginpwd", MD5.GetMD5Code(user.getStr("loginpwd")));
				user.update();
			}
		}
		
		flag = true;
		this.renderJson(flag);
	}
	
	
	public void savezhzt(){
		SysUser  user = this.getModel(SysUser.class,"user");
		boolean flag = false;
		if(user!=null&&StringUtil.toStr(user.getStr("id")).length()>0){
			user.update();
		}
		flag = true;
		this.renderJson(flag);
	}
	
	public void saveusermmcz(){
		boolean flag = false;
		try {
			String id = StringUtil.toStr(this.getPara("id"));
			if(id.length()>0){
				SysUser.dao.findById(id).set("loginpwd", MD5.GetMD5Code("888888")).update();
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.renderJson(flag);
	}
	
	public void edit(){
		String id = StringUtil.toStr(this.getPara("id"));
		SysUser  vo = new SysUser();
		if(id.length()>0){
			vo = vo.dao.findById(id);
		}
		List<SysRole> list_role = SysRole.dao.find("select * from sys_role order by sxh");
		this.setAttr("list_role", list_role);
		this.setAttr("vo", vo);
		render("/admin/user/edit.html");
	}
	
	
	public void edit_modifypwd(){
		String id = StringUtil.toStr(this.getPara("id"));
		SysUser  vo = new SysUser();
		if(id.length()>0){
			vo = vo.dao.findById(id);
		}
		this.setAttr("vo", vo);
		render("/admin/user/edit_modifypwd.html");
	}
	
	
	public void modifypwd(){
		SysUser  vo = this.getSessionAttr("user");
		if(vo!=null&&StringUtil.toStr(vo.getStr("id")).length()>0){
			vo = SysUser.dao.findById(vo.getStr("id"));
		}
		this.setAttr("vo", vo);
		render("/admin/user/modifypwd.html");
	}
	
	
	public void editzhzt(){
		String id = StringUtil.toStr(this.getPara("id"));
		SysUser  vo = new SysUser();
		if(id.length()>0){
			vo = vo.dao.findById(id);
		}
		this.setAttr("vo", vo);
		render("/admin/user/editzhzt.html");
	}
	
	
	public void getAllAdmin(){
		String id = StringUtil.toStr(this.getPara("id"));
		List<SysUser> adminList = SysUser.dao.find("select * from sys_user where role_id in ('101','103')");
		SysUser  vo = new SysUser();
		if(id.length()>0){
			vo = vo.dao.findById(id);
		}
		this.setAttr("vo", vo);
		this.setAttr("adminList", adminList);
		render("/admin/user/getAllAdmin.html");
	}
	
	
	public void doSetAdminForVip(){
		String id = StringUtil.toStr(this.getPara("id"));
		String admin_id = StringUtil.toStr(this.getPara("admin_id"));
		SysUser user = SysUser.dao.findById(id);
		user.set("p_id", admin_id);
		user.update();
		renderJson(true);
	}
	
	
	public void editcityuser(){
		String id = StringUtil.toStr(this.getPara("id"));
		SysUser  vo = new SysUser();
		if(id.length()>0){
			vo = vo.dao.findById(id);
		}else{
			SysUser s_u = this.getSessionAttr("user");
			vo.set("p_id", s_u.get("id"));
		}
		this.setAttr("vo", vo);
		render("/admin/user/editcityuser.html");
	}
	
	public void editcitymng(){
		String id = StringUtil.toStr(this.getPara("id"));
		SysUser  vo = new SysUser();
		if(id.length()>0){
			vo = vo.dao.findById(id);
		}else{
			SysUser s_u = this.getSessionAttr("user");
			vo.set("p_id", s_u.get("id"));
		}
		this.setAttr("vo", vo);
		render("/admin/user/editcitymng.html");
	}
	
	/**
	 * 提现查询
	 */
	
	public void listtx(){
		SysUser user = (SysUser)this.getSessionAttr("user");
		String result  = StringUtil.toStr(this.getPara("result"));
		String txtype  = StringUtil.toStr(this.getPara("txtype"));
		String sql = "select a.* from sys_cz_tx a,sys_user b where a.userid = b.id and a.type = '1'";
		String ds = StringUtil.toStr(this.getPara("ds"));
		if(ds.length() > 0){
			result="0";
		}
		if(result.length()>0){
			sql+=" and a.result = '"+result+"'";
		}
		if(txtype.length()>0){
			sql+=" and a.txtype = '"+txtype+"'";
		}
		if(user.getStr("id").length() > 0){
			sql += " and (b.city_id in (" + CityAttachAdminUtil.getCityByAdmin(user.getStr("id")) + ")";
			sql += " or b.area_id in (" + CityAttachAdminUtil.getCityByAdmin(user.getStr("id")) + "))";
		}
		sql+=" order by a.time desc";
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		this.setAttr("listpage", listpage);
	}
	/**
	 * 充值查询
	 */
	
	public void listcz(){
		SysUser user = (SysUser)this.getSessionAttr("user");
		String status  = StringUtil.toStr(this.getPara("status"));
		String sql= "select a.* from log_user_deposit a,sys_user b where a.userid=b.id";
		if(user.getStr("id").length() > 0){
			sql += " and (b.city_id in (" + CityAttachAdminUtil.getCityByAdmin(user.getStr("id")) + ")";
			sql += " or b.area_id in (" + CityAttachAdminUtil.getCityByAdmin(user.getStr("id")) + "))";
		}
		String ds = StringUtil.toStr(this.getPara("ds"));
		if(ds.length() > 0){
			status="0";
		}
		if(status.length()>0){
			sql+=" and a.status = '"+status+"'";
		}
		sql+=" order by a.createtime desc";
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		this.setAttr("listpage", listpage);
	}
	
	public void doSure(){
		String id = StringUtil.toStr(this.getPara("id"));
		if(!StringUtils.isNullOrEmpty(id)){
			SysCzTx cztx = SysCzTx.dao.findById(id);
			if(!"0".equals(cztx.getStr("result"))){
				renderJson(false);
			}else{
				cztx.set("result", 1);
				cztx.set("totime", DateUtil.getCurrentDate());
				cztx.update();
				renderJson(true);
			}
		}else{
			renderJson(false);
		}
	}
	public void doFailure(){
		String id = StringUtil.toStr(this.getPara("id"));
		if(!StringUtils.isNullOrEmpty(id)){
			SysCzTx cztx = SysCzTx.dao.findById(id);
			if("1".equals(cztx.getStr("result"))){
				renderJson(false);
			}else{
				cztx.set("result", 2);
				cztx.update();
				SysUser user = SysUser.dao.findById(cztx.getStr("userid"));
				user.set("zhye", user.getBigDecimal("zhye").add(new BigDecimal(cztx.getStr("totalfee"))));
				user.update();
				renderJson(true);
			}
		}else{
			renderJson(false);
		}
	}
	public void batchfukuan(){
		SysUser u = (SysUser) this.getSessionAttr("user");
		String sql = "select a.* from sys_cz_tx a,sys_user b where a.userid=b.id";
		sql += " and type='1' and result='0' and txtype='0'";
		if(!"admin".equals(u.getStr("id"))){
			sql += " and b.p_id='" + u.getStr("id") + "'";
		}
		List<SysCzTx> cztx_list = SysCzTx.dao.find(sql);
		if(null==cztx_list || cztx_list.size() ==0){//没有记录
			this.renderJson("result","error");
		}
		else{//有记录
			String user_id = u.getStr(SysUser.ID);
			SysConfig zfbconfig = null;
			boolean flag = false;
			if(null!= u.getStr("city_id") && u.getStr("city_id").length() > 0){
				AbCityarea aca = AbCityarea.dao.findById(u.getStr("city_id"));
				if(null!=aca && null!=aca.getStr("user_id") && aca.getStr("user_id").length() > 0){
					zfbconfig= SysConfig.dao.findFirst("select * from sys_config where c6='" + aca.getStr("user_id") + "'");
					if(null!=zfbconfig){
						flag = true;
					}
				}
			}
			if(!flag){
				zfbconfig = new SysConfig();
				zfbconfig.set("c1", "2088511526488301");
				zfbconfig.set("c2", "sl9w4o4of1h3hbg81ko20o8f7cjyd52b");
				zfbconfig.set("c5", "banxiandianzi@163.com");
			}
			String email = zfbconfig.getStr("c5");//付款方的支付宝账号
			String service = "batch_trans_notify";//接口名称
			String partner = zfbconfig.getStr("c1");//合作伙伴ID
			String _input_charset = "utf-8";
			String notify_url = "http://www.yijuhome.cn/admin/user/payback";
			String account_name = "深圳市邦翔电子商务有限公司";
			String pay_date  = DateUtil.getCurrentDate("yyyyMMdd");
			String batch_no  = System.currentTimeMillis() + "";
			String batch_num  = cztx_list.size() + "";
			float batch_fee = 0;
			String detail_data = "";
			for(SysCzTx sc : cztx_list){
				batch_fee += Float.parseFloat(sc.getStr("totalfee"));
				detail_data +=sc.getStr("tradeno") + "^" +
							sc.getStr("zfbaddress")+ "^" + 
							sc.getStr("mc")+"^" +sc.getStr("totalfee")+
							"^" + "批量付款|";
				sc.set("pc", batch_no);
				sc.update();
			}
			detail_data = detail_data.substring(0, detail_data.length() - 1);
			Map<String, String> sParaTemp = new HashMap<String, String>();
			sParaTemp.put("email", email);
			sParaTemp.put("service", service);
			sParaTemp.put("partner", partner);
			sParaTemp.put("_input_charset", _input_charset);
			sParaTemp.put("pay_date", pay_date);
			sParaTemp.put("batch_no", batch_no);
			sParaTemp.put("batch_num", batch_num);
			sParaTemp.put("account_name", account_name);
			sParaTemp.put("batch_fee", batch_fee + "");
			sParaTemp.put("detail_data", detail_data);
			sParaTemp.put("notify_url", notify_url);
			try {
				String sHtmlText = AlipaySubmit.buildRequest(sParaTemp,"get","确认");
				this.renderJson("result",sHtmlText);
			} catch (UnsupportedEncodingException e) {
				this.renderJson("result","error");
			}
			
		}
	}
	public void payback(){
		String batch_no = StringUtil.toStr(this.getPara("batch_no"));
		String sql = "select * from sys_cz_tx where 1=1";
		sql += " and pc=" + batch_no + "'";
		List<SysCzTx> cztx_list = SysCzTx.dao.find(sql);
		for(SysCzTx sc : cztx_list){
			sc.set("result", 1);
			sc.update();
		}
	}
	
	public void sj(){
		String sql = "select * from ab_tc_express_order a where a.sn in (";
		sql += "select sn from ab_order where istrue='0') order by a.send_time desc";
		List<AbTcExpressOrder> ateolist = AbTcExpressOrder.dao.find(sql);
		this.setAttr("ateolist", ateolist);
		this.render("/admin/user/sj.html");
	}
	
	public void bachAddSJ(){
		String nums = StringUtil.toStr(this.getPara("nums"));
		String mapbusiness = StringUtil.toStr(this.getPara("mapbusiness"));
		String xxdz = StringUtil.toStr(this.getPara("xxdz"));
		String cityname = StringUtil.toStr(this.getPara("cityname"));
		String lng = StringUtil.toStr(this.getPara("lng"));
		String lat = StringUtil.toStr(this.getPara("lat"));
		Random rand = new Random();
		List<SysUser> adminList = SysUser.dao.find("select * from sys_user where role_id=?", "103");
		try{
			for(int i = 0; i < Integer.parseInt(nums); i++){
				SysUser user = new SysUser();
				String uuid = StringUtil.getUuid32();
				String mobile = 1 + RandomUtil.createRandomNumMobile(10);
				user.set("id", uuid);
				user.set("loginid", mobile);
				user.set("loginpwd", MD5.GetMD5Code("0"));
				user.set("zhzt", "1");
				user.set("mc", getXingShi().get(rand.nextInt(getXingShi().size())));
				user.set("idcard", RandomUtil.createRandomNumMobile(18));
				user.set("xxdz", xxdz);
				user.set("mobile", mobile);
				user.set("lng", lng.split("\\.")[0] + "." + RandomUtil.createRandomNumMobile(6));
				user.set("lat", lat.split("\\.")[0] + "." + RandomUtil.createRandomNumMobile(6));
				user.set("mapbusiness", mapbusiness);
				user.set("certificateemail", "0");
				user.set("certificatemobile", "1");
				user.set("certificatecard", "1");
				user.set("certificatemoney", "0.38");
				user.set("status", "1");
				user.set("is_account_enabled", "1");
				user.set("is_account_locked", "1");
				user.set("role_id", "106");
				user.set("role_name", "业务员");
				try{
					user.set("p_id", adminList.get((int)(Math.random()*(adminList.size() - 1))).getStr("id"));
				}catch(Exception e){
					user.set("p_id","admin");
				}
				user.set("create_date", DateUtil.getCurrentDate());
				user.set("zhye", 100 + i);
				user.set("per", 1);
				user.set("zhlx", 0);
				user.set("sfrzzt", 2);
				user.set("tjsj", DateUtil.getCurrentDate());
				user.set("shrid", "admin");
				user.set("shrmc", "系统管理员");
				if(cityname.endsWith("市")){
					cityname = cityname.substring(0, cityname.length() - 1);
				}
				AbCityarea aca = AbCityarea.dao.findFirst("select * from ab_cityarea where mc = ?", cityname);
				if(null != aca){
					user.set("city_id", aca.getStr("id"));
					user.set("city_name", cityname);
					AbCityarea acatemp = AbCityarea.dao.findFirst("select * from ab_cityarea where p_id = ?", aca.getStr("id"));
					if(null!=acatemp){
						user.set("area_id", acatemp.getStr("id"));
						user.set("area_name", acatemp.getStr("mc"));
					}else{
						user.set("area_id", "109100100");
						user.set("area_name", "二七万达商圈");
					}
				}else{
					user.set("city_id", "109100");
					user.set("city_name", "郑州");
					user.set("area_id", "109100100");
					user.set("area_name", "二七万达商圈");
				}
				user.set("rate", "1");
				user.set("sjzt", i % 5);
				user.save();
				
				AbFmcar car = new AbFmcar();
				String uuid_car = StringUtil.getUuid32();
				car.set("id", uuid_car);
				Object[] obar = getCityCarCode().values().toArray();
				car.set("car_no", obar[(int)(Math.random()*(obar.length - 1))] + RandomUtil.createRandomNumMobile(5));
				car.set("driver", user.getStr("mc"));
				car.set("mobile", mobile);
				car.set("length", getCarLength().get((int)(Math.random()*18)));
				car.set("type", getCarType().get((int)(Math.random()*10)));
				car.set("istrue", 0);
				car.set("vv", (int)(Math.random()*10 + 5));
				car.save();
				
				AbFmcarCity car_city = new AbFmcarCity();
				String uuid_city = StringUtil.getUuid32();
				car_city.set("id", uuid_city);
				car_city.set("car_id", uuid_car);
				car_city.set("city_name", getCityCode().get(cityname));
				car_city.save();
				
				AbSjPosition asp = new AbSjPosition();
				String uuid_sj = StringUtil.getUuid32();
				asp.set("id", uuid_sj);
				asp.set("sjid", uuid);
				asp.set("sjmc", user.getStr("mc"));
				asp.set("jd", lng.split("\\.")[0] + "." + RandomUtil.createRandomNumMobile(6));
				asp.set("wd", lat.split("\\.")[0] + "." + RandomUtil.createRandomNumMobile(6));
				asp.set("dqsj", DateUtil.getCurrentDate());
				asp.set("zt", i % 5);
				asp.save();
			}
			this.renderJson(true);
		}catch(Exception e){
			e.printStackTrace();
			this.renderJson(false);
		}
	}
	
	public void addJiaDan(){
		/**生成假定单的个数**/
		String nums = StringUtil.toStr(this.getPara("jiadannums"));
		long num = 0;
		try{
			num = Long.parseLong(nums);
			//货物类型：
			List<AbTcConfig> goodsTypeList = AbTcConfig.dao.find("select * from ab_tc_config where type in ('03')");
			List<AbAddress> addRessList = AbAddress.dao.find("select * from ab_address");
			Random rand = new Random();
			for(int i = 0; i < num; i++){
				AbTcExpressOrder ateo = new AbTcExpressOrder();
				AbAddress aa = addRessList.get((int)(Math.random()*addRessList.size()));
				AbAddress aa1 = addRessList.get((int)(Math.random()*addRessList.size()));
				AbOrder order = new AbOrder();
				order.set("id", StringUtil.getUuid32());
				order.set("sn", "TC" + RandomUtil.createRandomNumPwd(9));
				order.set("is_type", "1");
				int tempmoney =  (int)(Math.random() * 1000);
				order.set("spzj",tempmoney);
				order.set("ddzje", tempmoney);
				order.set("yhje", new BigDecimal(0));
				order.set("xdsj", DateUtil.getCurrentDate());
				order.set("ddzt", "1");
				order.set("istrue", "0");
				order.set("shdz", aa1.getStr("address"));
				order.set("xdrmc",  getXingShi().get(rand.nextInt(getXingShi().size())));
				order.set("xdrdh", 1 + RandomUtil.createRandomNumMobile(10));
				order.set("lxrdh", 1 + RandomUtil.createRandomNumMobile(10));
				order.set("lxr", getXingShi().get(rand.nextInt(getXingShi().size())));
				order.save();
				ateo.set("sn", order.getStr("sn"));
				ateo.set("style", "1");
				ateo.set("start_price", new BigDecimal(200));
				ateo.set("goods_type", goodsTypeList.get((int)(Math.random()*goodsTypeList.size())).getStr("value"));
				ateo.set("send_name", getXingShi().get(rand.nextInt(getXingShi().size())));
				ateo.set("send_phone", 1 + RandomUtil.createRandomNumMobile(10));
				ateo.set("send_addr",aa.getStr("address"));
				ateo.set("send_addr_jd", aa.getStr("jd"));
				ateo.set("send_addr_wd", aa.getStr("wd"));
				
				ateo.set("rcv_name1", order.getStr("lxr"));
				ateo.set("rcv_phone1", order.getStr("lxrdh"));
				ateo.set("rcv_addr1",aa1.getStr("address"));
				ateo.set("rcv_addr1_jd", aa1.getStr("jd"));
				ateo.set("rcv_addr1_wd", aa1.getStr("wd"));
				//生成其他收货信息
				int tempnums = (int)(Math.random() * 5) + 2;
				for(int j = 2; j < tempnums;j++){
					AbAddress aatemp = addRessList.get((int)(Math.random()*addRessList.size()));
					ateo.set("rcv_name" + j, getXingShi().get(rand.nextInt(getXingShi().size())));
					ateo.set("rcv_phone" + j, 1 + RandomUtil.createRandomNumMobile(10));
					ateo.set("rcv_addr" + j,aatemp.getStr("address"));
					ateo.set("rcv_addr" + j + "_jd", aatemp.getStr("jd"));
					ateo.set("rcv_addr" + j + "_wd", aatemp.getStr("wd"));
				}
				ateo.set("kilo", (int)(Math.random() * 100));
				ateo.set("pay_type", (int)(Math.random() * 3) + 1);
				ateo.set("huidan_price", 3);
				ateo.set("service_price", 10);
				ateo.set("total_price", tempmoney);
				ateo.set("lift", 1);
				ateo.set("cart", 1);
				ateo.set("weight", (int)(Math.random() * 110));
				ateo.set("send_time", DateUtil.getRandomDate());
				ateo.set("goods_mount", (int)(Math.random() * 5 + 1));
				ateo.set("goods_volumn", (int)(Math.random() * 6 + 1));
				ateo.set("car", getCarType().get((int)(Math.random()*10)));
				ateo.save();
			}
			this.renderJson(true);
		}catch(Exception e){
			this.renderJson(false);
		}
	}
	
	public void delAllJiaDan(){
		try{
			String sql = "select * from ab_tc_express_order a where a.sn in (";
			sql += "select sn from ab_order where istrue='0')";
			List<AbTcExpressOrder> ateo = AbTcExpressOrder.dao.find(sql);
			if(null!=ateo){
				for(AbTcExpressOrder temp : ateo){
					if(null!=temp){
						temp.delete();
					}
				}
			}
			
			sql = "select * from ab_order where istrue='0'";
			List<AbOrder> orderlist = AbOrder.dao.find(sql);
			if(null!=orderlist){
				for(AbOrder temp : orderlist){
					if(null!=temp){
						temp.delete();
					}
				}
			}
			this.renderJson(true);
		}catch(Exception e){
			this.renderJson(false);
		}
	}
	
	/**
	 * 返回姓氏
	 * @return
	 */
	public static Map<Integer,String> getXingShi(){
		StringBuffer xb = new StringBuffer("赵 钱 孙 李 周 吴 郑 王 冯 陈 褚 卫 蒋 沈 韩 杨 ");  
		xb.append("朱 秦 尤 许 何 吕 施 张 孔 曹 严 华 金 魏 陶 姜 ");
		xb.append("戚 谢 邹 喻 柏 水 窦 章 云 苏 潘 葛 奚 范 彭 郎 ");
		xb.append("鲁 韦 昌 马 苗 凤 花 方 俞 任 袁 柳 酆 鲍 史 唐 ");
		xb.append("费 廉 岑 薛 雷 贺 倪 汤 滕 殷 罗 毕 郝 邬 安 常 ");
		xb.append("乐 于 时 傅 皮 卞 齐 康 伍 余 元 卜 顾 孟 平 黄 ");
		xb.append("和 穆 萧 尹 姚 邵 湛 汪 祁 毛 禹 狄 米 贝 明 臧 ");
		xb.append("计 伏 成 戴 谈 宋 茅 庞 熊 纪 舒 屈 项 祝 董 梁 "); 
		xb.append("杜 阮 蓝 闵 席 季 麻 强 贾 路 娄 危 江 童 颜 郭 ");
		xb.append("梅 盛 林 刁 钟 徐 邱 骆 高 夏 蔡 田 樊 胡 凌 霍 ");
		xb.append("虞 万 支 柯 昝 管 卢 莫 经 房 裘 缪 干 解 应 宗 ");
		xb.append("丁 宣 贲 邓 郁 单 杭 洪 包 诸 左 石 崔 吉 钮 龚 ");
		xb.append("程 嵇 邢 滑 裴 陆 荣 翁 荀 羊 於 惠 甄 曲 家 封 ");
		xb.append("芮 羿 储 靳 汲 邴 糜 松 井 段 富 巫 乌 焦 巴 弓 ");
		xb.append("牧 隗 山 谷 车 侯 宓 蓬 全 郗 班 仰 秋 仲 伊 宫 ");
		xb.append("宁 仇 栾 暴 甘 钭 厉 戎 祖 武 符 刘 景 詹 束 龙 ");
		xb.append("叶 幸 司 韶 郜 黎 蓟 薄 印 宿 白 怀 蒲 台 从 鄂 ");
		xb.append("索 咸 籍 赖 卓 蔺 屠 蒙 池 乔 阴 郁 胥 能 苍 双 ");
		xb.append("闻 莘 党 翟 谭 贡 劳 逄 姬 申 扶 堵 冉 宰 郦 雍 ");
		xb.append("却 璩 桑 桂 濮 牛 寿 通 边 扈 燕 冀 郏 浦 尚 农 ");
		xb.append("温 别 庄 晏 柴 瞿 阎 充 慕 连 茹 习 宦 艾 鱼 容 ");
		xb.append("向 古 易 慎 戈 廖 庚 终 暨 居 衡 步 都 耿 满 弘 ");
		xb.append("匡 国 文 寇 广 禄 阙 东 殴 殳 沃 利 蔚 越 夔 隆 ");
		xb.append("师 巩 厍 聂 晁 勾 敖 融 冷 訾 辛 阚 那 简 饶 空 ");
		xb.append("曾 毋 沙 乜 养 鞠 须 丰 巢 关 蒯 相 查 后 荆 红 ");
		Map<Integer,String> result = new HashMap<Integer,String>();
		String[] xbAarr = xb.toString().split(" ");
		for(int i = 0; i < xbAarr.length; i++){
			result.put(i, xbAarr[i] + "师傅");
		}
		return result;
	}
	
	/**
	 * 车型
	 * @return 返回车类map对象
	 */
	public static Map<Integer,String> getCarType(){
		Map<Integer,String> result = new HashMap<Integer,String>();
		result.put(0, "三轮车");
		result.put(1, "面包车");
		result.put(2, "面包车");
		result.put(3, "厢车");
		result.put(4, "平板车");
		result.put(5, "高低板车");
		result.put(6, "高栏车");
		result.put(7, "冷藏车");
		result.put(8, "危险品车");
		result.put(9, "大件车");
		return result;
	}

	/**
	 * 车厂
	 * 
	 * @return 返回车类map对象
	 */
	public static Map<Integer,String> getCarLength(){
		Map<Integer,String> result = new HashMap<Integer,String>();
		result.put(0, "1.8米");
		result.put(1, "24米");
		result.put(2, "2.6车");
		result.put(3, "3.2米");
		result.put(4, "4.2米");
		result.put(5, "5.2米");
		result.put(6, "5.8米");
		result.put(7, "6.2米");
		result.put(8, "6.5米");
		result.put(9, "6.8米");
		result.put(10, "7.2米");
		result.put(11, "8米");
		result.put(12, "9.6米");
		result.put(13, "13米");
		result.put(14, "15米");
		result.put(15, "16.5米");
		result.put(16, "17.5米");
		result.put(17, "18.5米");
		result.put(18, "20米");
		return result;
	}
	
	/**
	 * 获取城市id之前
	 * @return
	 */
	public static Map<String,String> getCityCode(){
		Map<String,String> result = new HashMap<String,String>();
		result.put("北京", "120000#北京");
		result.put("安徽", "130000#安徽");
		result.put("安庆", "130100#安庆");
		result.put("蚌埠", "130200#蚌埠");
		result.put("巢湖", "130300#巢湖");
		result.put("池州", "130400#池州");
		result.put("滁州", "130500#滁州");
		result.put("阜阳", "130600#阜阳");
		result.put("淮北", "130700#淮北");
		result.put("淮南", "130800#淮南");
		result.put("黄山", "130900#黄山");
		result.put("六安", "131000#六安");
		result.put("马鞍山", "131100#马鞍山");
		result.put("宿州", "131200#宿州");
		result.put("铜陵", "131300#铜陵");
		result.put("芜湖", "131400#芜湖");
		result.put("宣城", "131500#宣城");
		result.put("亳州", "131600#亳州");
		result.put("合肥", "131700#合肥");
		result.put("福建", "140000#福建");
		result.put("福州", "140100#福州");
		result.put("龙岩",  "140200#龙岩");
		result.put("南平",  "140300#南平");
		result.put("宁德",  "140400#宁德");
		result.put("莆田",  "140500#莆田");
		result.put("泉州",  "140600#泉州");
		result.put("三明",  "140700#三明");
		result.put("厦门",  "140800#厦门");
		result.put("漳州",  "140900#漳州");
		result.put("甘肃",  "150000#甘肃");
		result.put("兰州",  "150100#兰州");
		result.put("白银",  "150200#白银");
		result.put("定西",  "150300#定西");
		result.put("甘南",  "150400#甘南");
		result.put("嘉峪关",  "150500#嘉峪关");
		result.put("金昌",  "150600#金昌"); 
		result.put("酒泉",  "150700#酒泉");
		result.put("临夏",  "150800#临夏"); 
		result.put("陇南",  "150900#陇南");
		result.put("平凉",  "151000#平凉");
		result.put("庆阳",  "151100#庆阳");
		result.put("天水",  "151200#天水");
		result.put("武威",  "151300#武威");
		result.put("张掖",  "151400#张掖");
		result.put("广东",  "160000#广东");
		result.put("广州",  "160100#广州");
		result.put("深圳",  "160200#深圳");
		result.put("潮州",  "160300#潮州");
		result.put("东莞",  "160400#东莞");
		result.put("佛山",  "160500#佛山");
		result.put("河源",  "160600#河源");
		result.put("惠州",  "160700#惠州");
		result.put("江门",  "160800#江门");
		result.put("揭阳",  "160900#揭阳");
		result.put("茂名",  "161000#茂名");
		result.put("梅州",  "161100#梅州");
		result.put("清远",  "161200#清远");
		result.put("汕头",  "161300#汕头");
		result.put("汕尾",  "161400#汕尾");
		result.put("韶关",  "161500#韶关");
		result.put("阳江",  "161600#阳江");
		result.put("云浮",  "161700#云浮");
		result.put("湛江",  "161800#湛江");
		result.put("肇庆",  "161900#肇庆");
		result.put("中山",  "162000#中山");
		result.put("珠海",  "162100#珠海");
		result.put("广西",  "170000#广西");
		result.put("南宁",  "170100#南宁");
		result.put("桂林",  "170200#桂林");
		result.put("百色",  "170300#百色");
		result.put("北海",  "170400#北海");
		result.put("崇左",  "170500#崇左");
		result.put("防城港",  "170600#防城港");
		result.put("贵港",  "170700#贵港");
		result.put("河池",  "170800#河池");
		result.put("贺州",  "170900#贺州");
		result.put("来宾",  "171000#来宾");
		result.put("柳州",  "171100#柳州");
		result.put("钦州",  "171200#钦州");
		result.put("梧州",  "171300#梧州");
		result.put("玉林" , "171400#玉林");
		result.put("贵州" , "180000#贵州");
		result.put("贵阳" , "180100#贵阳");
		result.put("安顺" , "180200#安顺");
		result.put("毕节" , "180300#毕节");
		result.put("六盘水" , "180400#六盘水");
		result.put("黔东南" , "180500#黔东南");
		result.put("黔南" , "180600#黔南");
		result.put("黔西南" , "180700#黔西南");
		result.put("铜仁" , "180800#铜仁");
		result.put("遵义" , "180900#遵义");
		result.put("海南" , "190000#海南");
		result.put("海口" , "190100#海口");
		result.put("三亚" , "190200#三亚");
		result.put("白沙" , "190300#白沙");
		result.put("保亭" , "190400#保亭");
		result.put("昌江" , "190500#昌江");
		result.put("澄迈" , "190600#澄迈");
		result.put("定安" , "190700#定安");
		result.put("东方" , "190800#东方");
		result.put("乐东" , "190900#乐东");
		result.put("临高" , "191000#临高");
		result.put("陵水" , "191100#陵水");
		result.put("琼海" , "191200#琼海");
		result.put("琼中" , "191300#琼中");
		result.put("屯昌" , "191400#屯昌");
		result.put("万宁" , "191500#万宁");
		result.put("文昌" , "191600#文昌");
		result.put("五指山" , "191700#五指山");
		result.put("儋州" , "191800儋州#");
		result.put("河北" , "200000#河北");
		result.put("石家庄" , "200100#石家庄");
		result.put("保定" , "200200#保定");
		result.put("沧州" , "200300#沧州");
		result.put("承德" , "200400#承德");
		result.put("邯郸" , "200500#邯郸");
		result.put("衡水" , "200600#衡水");
		result.put("廊坊" , "200700#廊坊");
		result.put("秦皇岛" , "200800#秦皇岛");
		result.put("唐山" , "200900#唐山");
		result.put("邢台" , "201000#邢台");
		result.put("张家口" , "201100#张家口");
		result.put("河南" , "210000#河南");
		result.put("郑州" , "210100#郑州");
		result.put("洛阳" , "210200#洛阳");
		result.put("开封" , "210300#开封");
		result.put("安阳" , "210400#安阳");
		result.put("鹤壁" , "210500#鹤壁");
		result.put("济源" , "210600#济源");
		result.put("焦作" , "210700#焦作");
		result.put("南阳" , "210800#南阳");
		result.put("平顶山" , "210900#平顶山");
		result.put("三门峡" , "211000#三门峡");
		result.put("商丘" , "211100#商丘");
		result.put("新乡" , "211200#新乡");
		result.put("信阳" , "211300#信阳");
		result.put("许昌" , "211400#许昌");
		result.put("周口" , "211500#周口");
		result.put("驻马店" , "211600#驻马店");
		result.put("漯河" , "211700#漯河");
		result.put("濮阳" , "211800#濮阳");
		result.put("黑龙江" , "220000#黑龙江");
		result.put("哈尔滨" , "220100#哈尔滨");
		result.put("大庆" , "220200#大庆");
		result.put("大兴安岭" , "220300#大兴安岭");
		result.put("鹤岗" , "220400#鹤岗");
		result.put("黑河" , "220500#黑河");
		result.put("鸡西" , "220600#鸡西");
		result.put("佳木斯" , "220700#佳木斯");
		result.put("牡丹江" , "220800#牡丹江");
		result.put("七台河" , "220900#七台河");
		result.put("齐齐哈尔" , "221000#齐齐哈尔");
		result.put("双鸭山" , "221100#双鸭山");
		result.put("绥化" , "221200#绥化");
		result.put("伊春" , "221300#伊春");
		result.put("湖北" , "230000#湖北");
		result.put("武汉" , "230100#武汉");
		result.put("仙桃" , "230200#仙桃");
		result.put("鄂州" , "230300#鄂州");
		result.put("黄冈" , "230400#黄冈");
		result.put("黄石" , "230500#黄石");
		result.put("荆门" , "230600#荆门");
		result.put("荆州" , "230700#荆州");
		result.put("潜江" , "230800#潜江");
		result.put("神农架林" , "230900#神农架林");
		result.put("十堰" , "231000#十堰");
		result.put("随州" , "231100#随州");
		result.put("天门" , "231200#天门");
		result.put("咸宁" , "231300#咸宁");
		result.put("襄樊" , "231400#襄樊");
		result.put("孝感" , "231500#孝感");
		result.put("宜昌" , "231600#宜昌");
		result.put("恩施" , "231700#恩施");
		result.put("湖南" , "240000#湖南");
		result.put("长沙" , "240100#长沙");
		result.put("张家界" , "240200#张家界");
		result.put("常德" , "240300#常德");
		result.put("郴州" , "240400#郴州");
		result.put("衡阳" , "240500#衡阳");
		result.put("怀化" , "240600#怀化");
		result.put("娄底" , "240700#娄底");
		result.put("邵阳" , "240800#邵阳");
		result.put("湘潭" , "240900#湘潭");
		result.put("湘西" , "241000#湘西");
		result.put("益阳" , "241100#益阳");
		result.put("永州" , "241200#永州");
		result.put("岳阳" , "241300#岳阳");
		result.put("株洲" , "241400#株洲");
		result.put("吉林" , "250000#吉林");
		result.put("长春" , "250100#长春");
		result.put("吉林" , "250200#吉林");
		result.put("白城" , "250300#白城");
		result.put("白山" , "250400#白山");
		result.put("辽源" , "250500#辽源");
		result.put("四平" , "250600#四平");
		result.put("松原" , "250700#松原");
		result.put("通化" , "250800#通化");
		result.put("延边" , "250900#延边");
		result.put("江苏" , "260000#江苏");
		result.put("南京" , "260100#南京");
		result.put("苏州" , "260200#苏州");
		result.put("无锡" , "260300#无锡");
		result.put("常州" , "260400#常州");
		result.put("淮安" , "260500#淮安");
		result.put("连云港" , "260600#连云港");
		result.put("南通" , "260700#南通");
		result.put("宿迁" , "260800#宿迁");
		result.put("泰州" , "260900#泰州");
		result.put("徐州" , "261000#徐州");
		result.put("盐城" , "261100#盐城");
		result.put("扬州" , "261200#扬州");
		result.put("镇江" , "261300#镇江");
		result.put("江西" , "270000#江西");
		result.put("南昌" , "270100#南昌");
		result.put("抚州" , "270200#抚州");
		result.put("赣州" , "270300#赣州");
		result.put("吉安" , "270400#吉安");
		result.put("景德镇" , "270500#景德镇");
		result.put("九江" , "270600#九江");
		result.put("萍乡" , "270700#萍乡");
		result.put("上饶" , "270800#上饶");
		result.put("新余" , "270900#新余");
		result.put("宜春" , "271000#宜春");
		result.put("鹰潭" , "271100#鹰潭");
		result.put("辽宁" , "280000#辽宁");
		result.put("沈阳" , "280100#沈阳");
		result.put("大连" , "280200#大连");
		result.put("鞍山" , "280300#鞍山");
		result.put("本溪" , "280400#本溪");
		result.put("朝阳" , "280500#朝阳");
		result.put("丹东" , "280600#丹东");
		result.put("抚顺" , "280700#抚顺");
		result.put("阜新" , "280800#阜新");
		result.put("葫芦岛" , "280900#葫芦岛");
		result.put("锦州" , "281000#锦州");
		result.put("辽阳" , "281100#辽阳");
		result.put("盘锦" , "281200#盘锦");
		result.put("铁岭" , "281300#铁岭");
		result.put("营口" , "281400#营口");
		result.put("内蒙古" , "290000#内蒙古");
		result.put("呼和浩特" , "290100#呼和浩特");
		result.put("阿拉善盟" , "290200#阿拉善盟");
		result.put("巴彦淖尔" , "290300#巴彦淖尔");
		result.put("包头" , "290400#包头");
		result.put("赤峰" , "290500#赤峰");
		result.put("鄂尔多斯" , "290600#鄂尔多斯");
		result.put("呼伦贝尔" , "290700#呼伦贝尔");
		result.put("通辽" , "290800#通辽");
		result.put("乌海" , "290900#乌海");
		result.put("乌兰察布" , "291000#乌兰察布");
		result.put("锡林郭勒" , "291100#锡林郭勒");
		result.put("兴安盟" , "291200#兴安盟");
		result.put("宁夏" , "300000#宁夏");
		result.put("银川" , "300100#银川");
		result.put("固原" , "300200#固原");
		result.put("石嘴山" , "300300#石嘴山");
		result.put("吴忠" , "300400#吴忠");
		result.put("中卫" , "300500#中卫");
		result.put("青海" , "310000#青海");
		result.put("西宁" , "310100#西宁");
		result.put("果洛" , "310200#果洛");
		result.put("海北" , "310300#海北");
		result.put("海东" , "310400#海东");
		result.put("海南" , "310500#海南");
		result.put("海西" , "310600#海西");
		result.put("黄南" , "310700#黄南"); 
		result.put("玉树" , "310800#玉树");
		result.put("山东" , "320000#山东");
		result.put("济南" , "320100#济南");
		result.put("青岛" , "320200#青岛");
		result.put("滨州" , "320300#滨州");
		result.put("德州" , "320400#德州");
		result.put("东营" , "320500#东营");
		result.put("菏泽" , "320600#菏泽");
		result.put("济宁" , "320700#济宁");
		result.put("莱芜" , "320800#莱芜");
		result.put("聊城" , "320900#聊城");
		result.put("临沂" , "321000#临沂");
		result.put("日照" , "321100#日照");
		result.put("泰安" , "321200#泰安");
		result.put("威海" , "321300#威海");
		result.put("潍坊" , "321400#潍坊");
		result.put("烟台" , "321500#烟台");
		result.put("枣庄" , "321600#枣庄");
		result.put("淄博" , "321700#淄博");
		result.put("山西" , "330000#山西");
		result.put("太原",  "330100#太原");
		result.put("长治" , "330200#长治");
		result.put("大同" , "330300#大同");
		result.put("晋城" , "330400#晋城");
		result.put("晋中" , "330500#晋中");
		result.put("临汾" , "330600#临汾");
		result.put("吕梁" , "330700#吕梁");
		result.put("朔州" , "330800#朔州");
		result.put("忻州" , "330900#忻州");
		result.put("阳泉" , "331000#阳泉");
		result.put("运城" , "331100#运城");
		result.put("陕西" , "340000#陕西");
		result.put("西安" , "340100#西安");
		result.put("安康" , "340200#安康");
		result.put("宝鸡" , "340300#宝鸡");
		result.put("汉中" , "340400#汉中"); 
		result.put("商洛" , "340500#商洛");
		result.put("铜川" , "340600#铜川");
		result.put("渭南" , "340700#渭南");
		result.put("咸阳" , "340800#咸阳");
		result.put("延安" , "340900#延安");
		result.put("榆林" , "341000#榆林");
		result.put("上海" , "350000#上海");
		result.put("四川" , "360000#四川");
		result.put("成都" , "360100#成都");
		result.put("绵阳" , "360200#绵阳");
		result.put("阿坝" , "360300#阿坝");
		result.put("巴中" , "360400#巴中");
		result.put("达州" , "360500#达州");
		result.put("德阳" , "360600#德阳");
		result.put("甘孜" , "360700#甘孜");
		result.put("广安" , "360800#广安");
		result.put("广元" , "360900#广元");
		result.put("乐山" , "361000#乐山");
		result.put("凉山" , "361100#凉山");
		result.put("眉山" , "361200#眉山");
		result.put("南充" , "361300#南充");
		result.put("内江" , "361400#内江");
		result.put("攀枝花" , "361500#攀枝花");
		result.put("遂宁" , "361600#遂宁");
		result.put("雅安" , "361700#雅安");
		result.put("宜宾" , "361800#宜宾");
		result.put("资阳" , "361900#资阳");
		result.put("自贡" , "362000#自贡");
		result.put("泸州" , "362100#泸州");
		result.put("天津" , "370000#天津");
		result.put("西藏" , "380000#西藏");
		result.put("拉萨" , "380100#拉萨");
		result.put("阿里" , "380200#阿里");
		result.put("昌都" , "380300#昌都");
		result.put("林芝" , "380400#林芝");
		result.put("那曲" , "380500#那曲");
		result.put("日喀则" , "380600#日喀则");
		result.put("山南" , "380700#山南");
		result.put("新疆" , "390000#新疆");
		result.put("乌鲁木齐" , "390100#乌鲁木齐");
		result.put("阿克苏" , "390200#阿克苏");
		result.put("阿拉尔" , "390300#阿拉尔");
		result.put("巴音郭楞" , "390400#巴音郭楞");
		result.put("博尔塔拉" , "390500#博尔塔拉");
		result.put("昌吉" , "390600#昌吉");
		result.put("哈密" , "390700#哈密");
		result.put("和田" , "390800#和田");
		result.put("喀什" , "390900#喀什");
		result.put("克拉玛依" , "391000#克拉玛依");
		result.put("克孜勒苏" , "391100#克孜勒苏");
		result.put("石河子" , "391200#石河子");
		result.put("图木舒克" , "391300#图木舒克");
		result.put("吐鲁番" , "391400#吐鲁番");
		result.put("五家渠" , "391500#五家渠");
		result.put("伊犁" , "391600#伊犁");
		result.put("云南" , "400000#云南");
		result.put("昆明" , "400100#昆明");
		result.put("怒江" , "400200#怒江");
		result.put("普洱" , "400300#普洱");
		result.put("丽江" , "400400#丽江");
		result.put("保山" , "400500#保山");
		result.put("楚雄" , "400600#楚雄");
		result.put("大理" , "400700#大理");
		result.put("德宏" , "400800#德宏");
		result.put("迪庆" , "400900#迪庆");
		result.put("红河" , "401000#红河");
		result.put("临沧" , "401100#临沧");
		result.put("曲靖" , "401200#曲靖");
		result.put("文山" , "401300#文山");
		result.put("西双版纳" , "401400#西双版纳");
		result.put("玉溪" , "401500#玉溪");
		result.put("昭通" , "401600#昭通");
		result.put("浙江" , "410000#浙江");
		result.put("杭州" , "410100#杭州");
		result.put("湖州" , "410200#湖州");
		result.put("嘉兴" , "410300#嘉兴");
		result.put("金华" , "410400#金华");
		result.put("丽水" , "410500#丽水");
		result.put("宁波" , "410600#宁波");
		result.put("绍兴" , "410700#绍兴");
		result.put("台州" , "410800#台州");
		result.put("温州" , "410900#温州");
		result.put("舟山" , "411000#舟山");
		result.put("衢州" , "411100#衢州");
		result.put("重庆" , "420000#重庆");
		result.put("香港" , "430000#香港");
		result.put("沙田" , "430100#沙田");
		result.put("东区" , "430200#东区");
		result.put("观塘" , "430300#观塘");
		result.put("黄大仙" , "430400#黄大仙");
		result.put("九龙城" , "430500#九龙城");
		result.put("屯门" , "430600#屯门");
		result.put("葵青" , "430700#葵青");
		result.put("元朗" , "430800#元朗");
		result.put("深水埗" , "430900#深水埗");
		result.put("西贡" , "431000#西贡");
		result.put("大埔" , "431100#大埔");
		result.put("湾仔" , "431200#湾仔");
		result.put("油尖旺" , "431300#油尖旺");
		result.put("北区" , "431400#北区");
		result.put("南区" , "431500#南区");
		result.put("荃湾" , "431600#荃湾");
		result.put("中西" , "431700#中西");
		result.put("离岛" , "431800#离岛");
		result.put("澳门" , "440000#澳门");
		result.put("澳门" , "440100#澳门");
		result.put("台湾" , "450000#台湾");
		result.put("台湾" , "450100#台湾");
		return result;
	}
	
	/**
	 * 获取城市id之前
	 * @return
	 */
	public static Map<String,String> getCityCarCode(){
		Map<String,String> result = new HashMap<String,String>();
		result.put("北京", "京A");
		result.put("安庆", "皖H");
		result.put("蚌埠", "皖C");
		result.put("巢湖", "皖Q");
		result.put("池州", "皖R");
		result.put("滁州", "皖M");
		result.put("阜阳", "皖K");
		result.put("淮北", "皖F");
		result.put("淮南", "皖D");
		result.put("黄山", "皖J");
		result.put("六安", "皖N");
		result.put("马鞍山", "皖E");
		result.put("宿州", "皖L");
		result.put("铜陵", "皖G");
		result.put("芜湖", "皖B");
		result.put("宣城", "皖P");
		result.put("亳州", "皖S");
		result.put("合肥", "皖A");
		result.put("福州", "闽A");
		result.put("龙岩",  "闽F");
		result.put("南平",  "闽H");
		result.put("宁德",  "闽J");
		result.put("莆田",  "闽B");
		result.put("泉州",  "闽C");
		result.put("三明",  "闽G");
		result.put("厦门",  "闽D");
		result.put("漳州",  "闽E");
		result.put("兰州",  "甘A");
		result.put("白银",  "甘D");
		result.put("定西",  "甘J");
		result.put("甘南",  "甘P");
		result.put("嘉峪关",  "甘B");
		result.put("金昌",  "甘C"); 
		result.put("酒泉",  "甘F");
		result.put("临夏",  "甘N"); 
		result.put("陇南",  "甘K");
		result.put("平凉",  "甘L");
		result.put("庆阳",  "甘M");
		result.put("天水",  "甘E");
		result.put("武威",  "甘H");
		result.put("张掖",  "甘G");
		result.put("广州",  "粤A");
		result.put("深圳",  "粤B");
		result.put("潮州",  "粤U");
		result.put("东莞",  "粤S");
		result.put("佛山",  "粤E");
		result.put("河源",  "粤P");
		result.put("惠州",  "粤L");
		result.put("江门",  "粤J");
		result.put("揭阳",  "粤V");
		result.put("茂名",  "粤K");
		result.put("梅州",  "粤M");
		result.put("清远",  "粤R");
		result.put("汕头",  "粤D");
		result.put("汕尾",  "粤N");
		result.put("韶关",  "粤F");
		result.put("阳江",  "粤Q");
		result.put("云浮",  "粤W");
		result.put("湛江",  "粤G");
		result.put("肇庆",  "粤H");
		result.put("中山",  "粤T");
		result.put("珠海",  "粤C");
		result.put("南宁",  "桂A");
		result.put("桂林",  "桂C");
		result.put("百色",  "桂L");
		result.put("北海",  "桂E");
		result.put("崇左",  "桂F");
		result.put("防城港",  "桂P");
		result.put("贵港",  "桂Q");
		result.put("河池",  "桂M");
		result.put("贺州",  "桂J");
		result.put("来宾",  "桂G");
		result.put("柳州",  "桂B");
		result.put("钦州",  "桂N");
		result.put("梧州",  "桂D");
		result.put("玉林" , "桂K");
		result.put("贵阳" , "贵A");
		result.put("安顺" , "贵G");
		result.put("毕节" , "贵F");
		result.put("六盘水" , "贵B");
		result.put("黔东南" , "贵H");
		result.put("黔南" , "贵J");
		result.put("黔西南" , "贵E");
		result.put("铜仁" , "贵D");
		result.put("遵义" , "贵C");
		result.put("海口" , "琼A");
		result.put("三亚" , "琼B");
		result.put("石家庄" , "冀A");
		result.put("保定" , "冀F");
		result.put("沧州" , "冀J");
		result.put("承德" , "冀H");
		result.put("邯郸" , "冀D");
		result.put("衡水" , "冀T");
		result.put("廊坊" , "冀R");
		result.put("秦皇岛" , "冀C");
		result.put("唐山" , "冀B");
		result.put("邢台" , "冀E");
		result.put("张家口" , "冀G");
		result.put("郑州" , "豫A");
		result.put("洛阳" , "豫C");
		result.put("开封" , "豫B");
		result.put("安阳" , "豫E");
		result.put("鹤壁" , "豫F");
		result.put("济源" , "豫U");
		result.put("焦作" , "豫H");
		result.put("南阳" , "豫R");
		result.put("平顶山" , "豫D");
		result.put("三门峡" , "豫M");
		result.put("商丘" , "豫N");
		result.put("新乡" , "豫G");
		result.put("信阳" , "豫S");
		result.put("许昌" , "豫K");
		result.put("周口" , "豫P");
		result.put("驻马店" , "豫Q");
		result.put("漯河" , "豫L");
		result.put("濮阳" , "豫J");
		result.put("哈尔滨" , "黑A");
		result.put("大庆" , "黑E");
		result.put("大兴安岭" , "黑P");
		result.put("鹤岗" , "黑H");
		result.put("黑河" , "黑N");
		result.put("鸡西" , "黑G");
		result.put("佳木斯" , "黑D");
		result.put("牡丹江" , "黑C");
		result.put("七台河" , "黑K");
		result.put("齐齐哈尔" , "黑B");
		result.put("双鸭山" , "黑J");
		result.put("绥化" , "黑M");
		result.put("伊春" , "黑F");
		result.put("武汉" , "鄂A");
		result.put("仙桃" , "鄂M");
		result.put("鄂州" , "鄂G");
		result.put("黄冈" , "鄂J");
		result.put("黄石" , "鄂B");
		result.put("荆门" , "鄂H");
		result.put("荆州" , "鄂D");
		result.put("潜江" , "鄂N");
		result.put("神农架林" , "鄂P");
		result.put("十堰" , "鄂C");
		result.put("随州" , "鄂S");
		result.put("天门" , "鄂R");
		result.put("咸宁" , "鄂L");
		result.put("襄樊" , "鄂F");
		result.put("孝感" , "鄂K");
		result.put("宜昌" , "鄂E");
		result.put("恩施" , "鄂Q");
		result.put("长沙" , "湘A");
		result.put("张家界" , "湘G");
		result.put("常德" , "湘J");
		result.put("郴州" , "湘L");
		result.put("衡阳" , "湘D");
		result.put("怀化" , "湘N");
		result.put("娄底" , "湘K");
		result.put("邵阳" , "湘E");
		result.put("湘潭" , "湘C");
		result.put("湘西" , "湘P");
		result.put("益阳" , "湘H");
		result.put("永州" , "湘M");
		result.put("岳阳" , "湘F");
		result.put("株洲" , "湘B");
		result.put("长春" , "吉A");
		result.put("吉林" , "吉B");
		result.put("白城" , "吉G");
		result.put("白山" , "吉F");
		result.put("辽源" , "吉D");
		result.put("四平" , "吉C");
		result.put("松原" , "吉J");
		result.put("通化" , "吉E");
		result.put("延边" , "吉H");
		result.put("南京" , "苏A");
		result.put("苏州" , "苏E");
		result.put("无锡" , "苏B");
		result.put("常州" , "苏D");
		result.put("淮安" , "苏H");
		result.put("连云港" , "苏G");
		result.put("南通" , "苏F");
		result.put("宿迁" , "苏N");
		result.put("泰州" , "苏M");
		result.put("徐州" , "苏C");
		result.put("盐城" , "苏J");
		result.put("扬州" , "苏K");
		result.put("镇江" , "苏L");
		result.put("南昌" , "赣A");
		result.put("抚州" , "赣F");
		result.put("赣州" , "赣B");
		result.put("吉安" , "赣D");
		result.put("景德镇" , "赣H");
		result.put("九江" , "赣G");
		result.put("萍乡" , "赣J");
		result.put("上饶" , "赣E");
		result.put("新余" , "赣K");
		result.put("宜春" , "赣C");
		result.put("鹰潭" , "赣L");
		result.put("沈阳" , "辽A");
		result.put("大连" , "辽B");
		result.put("鞍山" , "辽C");
		result.put("本溪" , "辽E");
		result.put("朝阳" , "辽N");
		result.put("丹东" , "辽F");
		result.put("抚顺" , "辽D");
		result.put("阜新" , "辽J");
		result.put("葫芦岛" , "辽P");
		result.put("锦州" , "辽G");
		result.put("辽阳" , "辽K");
		result.put("盘锦" , "辽L");
		result.put("铁岭" , "辽M");
		result.put("营口" , "辽H");
		result.put("呼和浩特" , "蒙A");
		result.put("阿拉善盟" , "蒙M");
		result.put("巴彦淖尔" , "蒙L");
		result.put("包头" , "蒙B");
		result.put("赤峰" , "蒙D");
		result.put("鄂尔多斯" , "蒙K");
		result.put("呼伦贝尔" , "蒙E");
		result.put("通辽" , "蒙G");
		result.put("乌海" , "蒙C");
		result.put("乌兰察布" , "蒙J");
		result.put("锡林郭勒" , "蒙H");
		result.put("兴安盟" , "蒙F");
		result.put("银川" , "宁A");
		result.put("固原" , "宁D");
		result.put("石嘴山" , "宁B");
		result.put("吴忠" , "宁C");
		result.put("中卫" , "宁E");
		result.put("西宁" , "青A");
		result.put("果洛" , "青F");
		result.put("海北" , "青C");
		result.put("海东" , "青B");
		result.put("海南" , "青E");
		result.put("海西" , "青H");
		result.put("黄南" , "青D"); 
		result.put("玉树" , "青G");
		result.put("济南" , "鲁A");
		result.put("青岛" , "鲁B");
		result.put("滨州" , "鲁M");
		result.put("德州" , "鲁N");
		result.put("东营" , "鲁E");
		result.put("菏泽" , "鲁R");
		result.put("济宁" , "鲁H");
		result.put("莱芜" , "鲁S");
		result.put("聊城" , "鲁P");
		result.put("临沂" , "鲁Q");
		result.put("日照" , "鲁L");
		result.put("泰安" , "鲁J");
		result.put("威海" , "鲁K");
		result.put("潍坊" , "鲁G");
		result.put("烟台" , "鲁F");
		result.put("枣庄" , "鲁D");
		result.put("淄博" , "鲁C");
		result.put("太原",  "晋A");
		result.put("长治" , "晋D");
		result.put("大同" , "晋B");
		result.put("晋城" , "晋E");
		result.put("晋中" , "晋K");
		result.put("临汾" , "晋L");
		result.put("吕梁" , "晋J");
		result.put("朔州" , "晋F");
		result.put("忻州" , "晋H");
		result.put("阳泉" , "晋C");
		result.put("运城" , "晋M");
		result.put("西安" , "陕A");
		result.put("安康" , "陕G");
		result.put("宝鸡" , "陕C");
		result.put("汉中" , "陕F"); 
		result.put("商洛" , "陕H");
		result.put("铜川" , "陕B");
		result.put("渭南" , "陕E");
		result.put("咸阳" , "陕D");
		result.put("延安" , "陕J");
		result.put("榆林" , "陕K");
		result.put("上海" , "沪A");
		result.put("成都" , "川A");
		result.put("绵阳" , "川B");
		result.put("阿坝" , "川U");
		result.put("巴中" , "川Y");
		result.put("达州" , "川S");
		result.put("德阳" , "川F");
		result.put("甘孜" , "川V");
		result.put("广安" , "川X");
		result.put("广元" , "川H");
		result.put("乐山" , "川L");
		result.put("凉山" , "川W");
		result.put("眉山" , "川Z");
		result.put("南充" , "川R");
		result.put("内江" , "川K");
		result.put("攀枝花" , "川D");
		result.put("遂宁" , "川J");
		result.put("雅安" , "川T");
		result.put("宜宾" , "川Q");
		result.put("资阳" , "川M");
		result.put("自贡" , "川C");
		result.put("泸州" , "川E");
		result.put("天津" , "津A");
		result.put("拉萨" , "藏A");
		result.put("阿里" , "藏F");
		result.put("昌都" , "藏B");
		result.put("林芝" , "藏G");
		result.put("那曲" , "藏E");
		result.put("日喀则" , "藏D");
		result.put("山南" , "藏C");
		result.put("乌鲁木齐" , "新A");
		result.put("阿克苏" , "新N");
		result.put("巴音郭楞" , "新M");
		result.put("博尔塔拉" , "新E");
		result.put("昌吉" , "新B");
		result.put("哈密" , "新L");
		result.put("和田" , "新R");
		result.put("喀什" , "新Q");
		result.put("克拉玛依" , "新J");
		result.put("克孜勒苏" , "新P");
		result.put("石河子" , "新C");
		result.put("吐鲁番" , "新K");
		result.put("伊犁" , "新F");
		result.put("昆明" , "云A");
		result.put("怒江" , "云Q");
		result.put("普洱" , "云J");
		result.put("丽江" , "云P");
		result.put("保山" , "云M");
		result.put("楚雄" , "云E");
		result.put("大理" , "云L");
		result.put("德宏" , "云N");
		result.put("迪庆" , "云R");
		result.put("红河" , "云G");
		result.put("临沧" , "云S");
		result.put("曲靖" , "云D");
		result.put("文山" , "云H");
		result.put("西双版纳" , "云K");
		result.put("玉溪" , "云F");
		result.put("昭通" , "云C");
		result.put("杭州" , "浙A");
		result.put("湖州" , "浙E");
		result.put("嘉兴" , "浙F");
		result.put("金华" , "浙G");
		result.put("丽水" , "浙K");
		result.put("宁波" , "浙B");
		result.put("绍兴" , "浙D");
		result.put("台州" , "浙J");
		result.put("温州" , "浙C");
		result.put("舟山" , "浙L");
		result.put("衢州" , "浙H");
		result.put("重庆" , "渝A");
		return result;
	}
	
	public void addAddressPage(){
		String sql = "select * from ab_address";
		List<AbAddress> list = AbAddress.dao.find(sql);
		this.setAttr("list", list);
		this.render("/admin/user/address.html");
	}
	
	public void doAddAddress(){
		try{
			String address = StringUtil.toStr(this.getPara("xxdz"));
			String jd = StringUtil.toStr(this.getPara("lng"));
			String wd = StringUtil.toStr(this.getPara("lat"));
			AbAddress abAdress = new AbAddress();
			abAdress.set("id", StringUtil.getUuid32());
			abAdress.set("address", address);
			abAdress.set("jd", jd);
			abAdress.set("wd", wd);
			abAdress.save();
			this.renderJson(true);
		}catch(Exception e){
			e.printStackTrace();
			this.renderJson(false);
		}
	}
	public void doDelAddress(){
		try{
			String id = StringUtil.toStr(this.getPara("id"));
			AbAddress abAdress =  AbAddress.dao.findById(id);
			abAdress.delete();
			this.renderJson(true);
		}catch(Exception e){
			e.printStackTrace();
			this.renderJson(false);
		}
	}
	
	public void certiCard(){
		SysUser user = (SysUser)this.getSessionAttr("user");
		String loginid = StringUtil.toStr(this.getPara("loginid"));
		String mobile = StringUtil.toStr(this.getPara("mobile"));
		String role_id = StringUtil.toStr(this.getPara("role_id"));
		String sql = "select * from sys_user where 1 = 1";
		if(role_id.length() > 0){
			sql += " and role_id = '" +role_id + "'";
			this.setAttr("role_id", role_id);
		}else{
			if(!user.getStr("role_id").equals("101")){
				sql += " and role_id >'104'";
			}
		}
//		if(!"admin".equals(user.getStr("id"))){
//			sql += " and p_id = '" + user.getStr("id") + "'";
//		}
		if(user.getStr("id").length() > 0){
			sql += " and (city_id in (" + CityAttachAdminUtil.getCityByAdmin(user.getStr("id")) + ")";
			sql += " or area_id in (" + CityAttachAdminUtil.getCityByAdmin(user.getStr("id")) + "))";
		}
		if(loginid.length() > 0){
			sql += " and loginid like'%" +loginid + "%'";
			this.setAttr("loginid", loginid);
		}
		if(mobile.length() > 0){
			sql += " and mobile like '%" +mobile + "%'";
			this.setAttr("mobile", mobile);
		}
		String ds = StringUtil.toStr(this.getPara("ds"));
		if(ds.length() > 0){
			sql += " and certificatecard = 2";
		}
		sql += " order by certificatecard desc";
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage",1), 14, sql);
		this.setAttr("listpage", listpage);
		String sqlRole = "select * from sys_role where 1 = 1";
		if(!user.getStr("role_id").equals("101")){
			sqlRole += " and id > '104'";
		}
		sqlRole += " order by sxh";
		List<SysRole> list_role = SysRole.dao.find(sqlRole);
		this.setAttr("list_role", list_role);
		this.render("/admin/user/certiCard.html");
	}
	
	public void alertDaQianPage(){
		String id = StringUtil.toStr(this.getPara("id"));
		SysUser user = SysUser.dao.findById(id);
		this.setAttr("vo", user);
		this.render("/admin/user/daqianpage.html");
	}
	
	public void doDaQian(){
		String id = StringUtil.toStr(this.getPara("id"));
		String certificatemoney = StringUtil.toStr(this.getPara("certificatemoney"));
		DecimalFormat df = new DecimalFormat(".00");
		try{
			SysUser user = SysUser.dao.findById(id);
			double j =  Double.parseDouble(certificatemoney);
			if(j >= 1 || j <0.01){
				this.renderJson(false);
				return;
			}
			user.set("certificatemoney", df.format(j)).update();
			this.renderJson(true);
		}catch(Exception e){
			this.renderJson(false);
		}
	}
}
