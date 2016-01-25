package com.zp.controller.trans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.core.Controller;
import com.zp.config.CaptchaRender;
import com.zp.entity.AbMerchant;
import com.zp.entity.AbMerchantImg;
import com.zp.entity.AbMerchantLogistics;
import com.zp.entity.AbMerchantLogtousu;
import com.zp.entity.AbSubject;
import com.zp.entity.SysUser;
import com.zp.tools.StringUtil;

public class LogisticsController extends Controller {

	public void index() {
		getContent("/trans/index.html","/");
	}
	public void pingjia() {
		getContent("/trans/index_severpingjia.html","/");
	}
	//公司简介
	public void profile() {
		getContent("/trans/profile.html","/");
	}
	//运营路线
	public void line() {
		getContent("/trans/line.html","/");
	}
	//公司场景
	public void scene() {
		getContent("/trans/scene.html","/");
	}
	//公司荣誉
	public void honor() {
		getContent("/trans/honor.html","/");
	}
	//诚信档案
	public void faith() {
		getContent("/trans/faith.html","/");
	}
	//联系我们
	public void contact() {
		getContent("/trans/contact.html","/");
	}
	//投诉建议
	public void tousu() {
		getContent("/trans/tousu.html","/");
	}
	
	private boolean getContent(String url1, String url2) {
		String loginid = getPara(0);
		this.setAttr("loginid", loginid);
		if(!StringUtil.isNull(loginid) && loginid.length() > 0) {
			Object mid = getMerchantID(loginid);
			if(mid!=null){
				AbMerchant vo = AbMerchant.dao.findById(mid);
				AbMerchantLogistics lo = AbMerchantLogistics.dao.findByMidShow(mid);
				List<AbMerchantImg> img = AbMerchantImg.dao.findByMidShow(mid);
				this.setAttr("lo", lo);
				this.setAttr("vo", vo);
				this.setAttr("img", img);

				if(url1!=null && url1.length() > 0) render(url1);
				return true;
			}
		}
		if(url2!=null && url2.length() > 0) redirect(url2);
		return false;
	}
	
	private Object getMerchantID(String username) {
		//根据当前登录人信息获取商家信息
		SysUser abuser = SysUser.dao.findFirst("select * from sys_user where loginid=?", username);
		if(abuser!=null) return abuser.get("mid");
		return null;
	}
	
	/*
	private String getLogisticsID() {
		//根据当前登录人信息获取商家信息
		SysUser abuser = this.getSessionAttr("abuser");
		//根据人员信息查询商家信息
		if(abuser!=null&&abuser.get("mid")!=null){
			AbMerchantLogistics lo = AbMerchantLogistics.dao.findByMidShow(abuser.get("mid"));
			if(lo!=null) return lo.get("id");
		}
		return null;
	}
	*/
	
	public void saveTousu() {
		if(!validateRequired("ts_contact", "请您输入联系人名称!")) return;
		if(!validateRequired("ts_phone", "请您输入联系电话!")) return;
		if(!validateRequired("ts_common", "请您输入投诉建议内容!")) return;
		if(!validateRequired("verify", "请您输入验证码!")) return;
		
		String inputRandomCode = this.getPara("verify");
		Object objMd5RandomCode = this.getSessionAttr(CaptchaRender.DEFAULT_CAPTCHA_MD5_CODE_KEY);
        if (!CaptchaRender.validate(objMd5RandomCode.toString(), inputRandomCode)) {
        	renderJson("<strong>验证码</strong>输入不正确,请重新输入!");
        	return;
        }
		AbMerchantLogtousu tousu = this.getModel(AbMerchantLogtousu.class,"ts");
		String lid = this.getPara("lo.id");
		if(lid!=null && lid.length() > 0) tousu.set("lid", lid);
		tousu.set("id", StringUtil.getRandString32());
		tousu.save();
		renderJson("flag", true);
	}
	
	private boolean validateRequired(String param, String msg) {
		String str = this.getPara(param);
		if(str!=null && str.length() > 0) 
			return true;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("msg", msg);
		map.put("flag", false);
		renderJson(map);
		System.out.println("validateRequired : " + msg);
		return false;
	}
}
