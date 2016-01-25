package com.zp.controller.admin;

import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.zp.config.CaptchaRender;
import com.zp.entity.SysMenu;
import com.zp.entity.SysUser;
import com.zp.interceptor.AccessAdminInterceptor;
import com.zp.tools.CityAttachAdminUtil;
import com.zp.tools.DateUtil;
import com.zp.tools.MD5;
import com.zp.tools.StringUtil;
@SuppressWarnings("static-access")
public class AdminController extends Controller {
	public void index(){
		setAttr("msg", "");
		setAttr("username", "");
		setAttr("password", "");
		render("/admin/portal/login.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void left(){
		SysUser u = this.getSessionAttr("user");
		String sql = "select * from sys_menu";
		String p_id = this.getPara(0);
		System.out.println(p_id);
		if(p_id!=null&&p_id.length()>0){
			sql+=" where p_id='"+p_id+"'";
		}else{
			//找出第一个显示的菜单
			SysMenu sm = SysMenu.dao.findFirst("select * from sys_menu sm where p_id='ROOT' and id in(select menu_id from sys_role_menu  m where m.role_id='"
			+u.getStr("role_id")+"') order by seq_num");
			
			sql+=" where p_id='"+sm.getStr("p_id")+"'";
		}
		
		sql+=" and id in(select menu_id from sys_role_menu  m where m.role_id='"+u.getStr("role_id")+"') order by seq_num ";
		
		
		SysMenu menu = new SysMenu();
		List<SysMenu> list_sub = menu.dao.find(sql);
		for(SysMenu sm : list_sub){
			
		}
		System.out.println(list_sub);
//		this.setAttr("list_sub", list_sub);
//		render("/admin/portal/left.html");
		this.renderJson(list_sub);
	}
	
	//生成验证码
    public void imgcode() {
        //这个是jfinal自带的验证码生成类，可以直接使用的
    	CaptchaRender img = new CaptchaRender(4); 
    	this.setSessionAttr(CaptchaRender.DEFAULT_CAPTCHA_MD5_CODE_KEY, img.getMd5RandonCode());
		render(img);
    }
    
	public void login(){
//    	String inputRandomCode = this.getPara("captchaCode");
        String username = this.getPara("username");
        String password = this.getPara("password");
        String msg = null;
        
//        Object objMd5RandomCode = this.getSessionAttr(CaptchaRender.DEFAULT_CAPTCHA_MD5_CODE_KEY);
//        if (!CaptchaRender.validate(objMd5RandomCode.toString(), inputRandomCode)) {
//        	msg = "验证码输入不正确,请重新输入！";
//        }else{
	        SysUser user = SysUser.dao.findFirst("select * from sys_user where loginid=? and loginpwd=?", username,MD5.GetMD5Code(password));
		    if (user != null) {
		    	if("1".equalsIgnoreCase(user.getStr("zhzt"))){
		    		//记录最后登录的IP和时间
			    	SysUser.dao.findById(user.get("id"))
			    	.set("login_ip", StringUtil.getRemoteLoginUserIp(this.getRequest()))
			    	.set("login_date", DateUtil.getCurrentDate()).update();
			    	this.setSessionAttr("user", user);
		    	}else if("0".equalsIgnoreCase(user.getStr("zhzt"))){
		    		msg = "帐户已被禁用，不能再登录本系统！";
		    	}else if("2".equalsIgnoreCase(user.getStr("zhzt"))){
		    		msg = "帐户在"+user.getStr("jykssj")+"至"+user.getStr("jyjzsj")+"被临时禁用，此期间不能登录本系统！";
		    	}
		    }else{
		    	msg = "用户名或密码不正确,请重新输入！";
		    }
//        }
        
        if(msg!=null&&msg.length()>0){
        	this.keepPara();
        	this.setAttr("msg", msg);
        	this.render("/admin/portal/login.html");
        }else{
        	this.redirect("/25console/portal");
        }
	}
	
	@Before(AccessAdminInterceptor.class)
    public void loginout(){
		if(this.getSession()!=null){
			this.getSession().invalidate();
		}
		redirect("/25console");
    }
	
	@Before(AccessAdminInterceptor.class)
    public void portal(){
		String flag = StringUtil.toStr(this.getPara("flag"));
		if(flag.length() == 0){
			render("/admin/portal/main.html");
			return;
		}
		SysUser u = (SysUser)this.getRequest().getSession().getAttribute("user");
		List<SysMenu> list = SysMenu.dao.find("select * from sys_menu sm where p_id='ROOT' and id in(select menu_id from sys_role_menu  m where m.role_id='"+u.getStr("role_id")+"') order by seq_num");
		for(SysMenu sm : list){
//			sm.set("subMenu", querySubMenuByPid(u,sm.getStr("id")));
			sm.set("exta", querySubMenuByPid(u,sm.getStr("id")));
		}
		this.setAttr("list", list);
		this.renderJson(list);
    }
	public List<SysMenu> querySubMenuByPid(SysUser u,String p_id){
		String sql = "select * from sys_menu";
		if(p_id!=null&&p_id.length()>0){
			sql+=" where p_id='"+p_id+"'";
		}else{
			//找出第一个显示的菜单
			SysMenu sm = SysMenu.dao.findFirst("select * from sys_menu sm where p_id='ROOT' and id in(select menu_id from sys_role_menu  m where m.role_id='"
			+u.getStr("role_id")+"') order by seq_num");
			
			sql+=" where p_id='"+sm.getStr("p_id")+"'";
		}
		sql+=" and id in(select menu_id from sys_role_menu  m where m.role_id='"+u.getStr("role_id")+"') order by seq_num ";
		return SysMenu.dao.find(sql);
	}
	
	/**
	 * 消息提醒
	 */
	@Before(AccessAdminInterceptor.class)
	public void inforeminder(){
		SysUser user = this.getSessionAttr("user");
		String auth =  CityAttachAdminUtil.getCityByAdmin(user.getStr("id"));
		//未指派订单数量统计
		this.setAttr("wzpns", Db.queryLong("select count(1) from ab_order where ddzt='2' and xdrid in(select id from sys_user where city_id in (" + auth + ") or area_id in (" + auth +"))").longValue());
		this.setAttr("hyhyrzns", Db.queryLong("select count(1) from sys_user where sfrzzt='1' and role_id='107' and (city_id in (" + auth + ") or area_id in (" + auth +"))").longValue());
		this.setAttr("dpwlrzns", Db.queryLong("select count(1) from sys_user where sfrzzt='1' and role_id='105' and (city_id in (" + auth + ") or area_id in (" + auth +"))").longValue());
		this.setAttr("sjrzns", Db.queryLong("select count(1) from sys_user where sfrzzt='1' and role_id='106' and (city_id in (" + auth + ") or area_id in (" + auth +"))").longValue());
		this.setAttr("yhkrzns", Db.queryLong("select count(1) from sys_user where certificatecard='2' and (city_id in (" + auth + ") or area_id in (" + auth +"))").longValue());
		this.setAttr("dpwlsprzns", Db.queryLong("select count(1) from ab_merchant_product where zt='0' and (city_id in (" + auth + ") or area_id in (" + auth +"))").longValue());
		this.setAttr("czns", Db.queryLong("select count(1) from log_user_deposit where status='0' and userid in(select id from sys_user where city_id in (" + auth + ") or area_id in (" + auth +"))").longValue());
		this.setAttr("txns", Db.queryLong("select count(1) from sys_cz_tx where result='0' and type='1' and userid in(select id from sys_user where city_id in (" + auth + ") or area_id in (" + auth +"))").longValue());
		this.render("/admin/portal/inforeminder.html");
	}
}
