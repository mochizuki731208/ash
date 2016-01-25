package com.zp.controller.ab;

import java.util.List;

import com.easemob.server.jersey.api.EasemobIMUsers;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.zp.entity.AbCityarea;
import com.zp.entity.AbIntegralScore;
import com.zp.entity.AbMerchant;
import com.zp.entity.AbSubject;
import com.zp.entity.SysUser;
import com.zp.entity.SysUserInvite;
import com.zp.entity.SysUserSubject;
import com.zp.entity.SysUserType;
import com.zp.tools.CommonStaticData;
import com.zp.tools.DateUtil;
import com.zp.tools.MD5;
import com.zp.tools.SmsMessage;
import com.zp.tools.StringUtil;
import com.zp.tools.mail.SimpleMailSender;

public class AbRegisterContrller extends Controller {
	public void reg(){
		AbCityarea city = this.getSessionAttr("currcity");
		this.setAttr("currcity", city);
		List<AbSubject> subList = AbSubject.dao.find("select * from ab_subject where p_id='ROOT' order by ccm,seq_num");
		this.setAttr("subList", subList);
		this.render("/ab/reg/reg.html");
	}
	
	//注册用户
	public void reguser(){
		AbCityarea city = this.getSessionAttr("currcity");
		this.setAttr("currcity", city);
		
		String userType = StringUtil.toStr(this.getPara("userType"));
		String userSubType = StringUtil.toStr(this.getPara("userSubType"));
		AbSubject subject = AbSubject.dao.findById(userType);
		String inviteCode = this.getPara("code");
		this.setAttr("invitecode", inviteCode);
		this.setAttr("subject", AbSubject.dao.findById(userSubType));
		if(subject.getStr("mc").equals("货源会员")){
			render("/ab/reg/reguser.html");
		}else if(subject.getStr("mc").equals("联盟商家")){
			render("/ab/reg/regmer.html");
		}else if(subject.getStr("mc").equals("物流企业")){
			this.setAttr("wl", "1");
			render("/ab/reg/regmer.html");
		}else if(subject.getStr("mc").equals("司机")){
			render("/ab/reg/regywy.html");
		}else{
			render("/ab/reg/reguser.html");
		}
	}
	
	//注册商户
	public void regmer(){
		AbCityarea city = this.getSessionAttr("currcity");
		this.setAttr("currcity", city);
		/*
		String cityid = city.getStr("id");
		List<AbSubject> list_subject = null;
		if(StringUtil.toStr(cityid).length()>0){
			list_subject = AbSubject.dao.find("SELECT * FROM ab_subject WHERE p_id='ROOT' AND id IN(SELECT subject_id FROM ab_cityarea_subject WHERE city_id=?) ORDER BY seq_num",cityid);
		}else{
			list_subject = AbSubject.dao.find("SELECT * FROM ab_subject WHERE p_id='ROOT' ORDER BY seq_num");
		}*/
//		List<AbSubject> list_subject = null;
//		list_subject = AbSubject.dao.find("SELECT * FROM ab_subject WHERE p_id='ROOT' ORDER BY seq_num");
		String userSubType = StringUtil.toStr(this.getPara("userSubType"));
		String userType = StringUtil.toStr(this.getPara("userType"));
		this.setAttr("userType", userType);
		this.setAttr("userSubType", userSubType);
//		this.setAttr("list_subject", list_subject);
		render("/ab/reg/regmer.html");
	}
	
	//注册业务员
	public void regywy(){
		AbCityarea city = this.getSessionAttr("currcity");
		this.setAttr("currcity", city);
		String cityid = city.getStr("id");
//		List<AbSubject> list_subject = null;
//		if(StringUtil.toStr(cityid).length()>0){
//			list_subject = AbSubject.dao.find("SELECT * FROM ab_subject WHERE p_id='ROOT' AND id IN(SELECT subject_id FROM ab_cityarea_subject WHERE city_id=?) ORDER BY seq_num",cityid);
//		}else{
//			list_subject = AbSubject.dao.find("SELECT * FROM ab_subject WHERE p_id='ROOT' ORDER BY seq_num");
//		}
		String userSubType = StringUtil.toStr(this.getPara("userSubType"));
		this.setAttr("userSubType", userSubType);
//		this.setAttr("list_subject", list_subject);
		render("/ab/reg/regywy.html");
	}
	
	public void sendSmsMsg(){
		String phone = this.getPara("phone");
		String smsCodeRand = this.getPara("smsCodeRand");
		try {
			SmsMessage.SendMessage(phone, smsCodeRand);
			renderJson(true);
		} catch (Exception e) {
			e.printStackTrace();
			renderJson(false);
		}
	}
	/**
	 * 用户注册
	 */
	public void registeruser(){
		SysUser user = new SysUser();
		this.setAttr("vo",user);
		render("/ab/reg/registeruser.html");
	}
	
	public void regsuccess(){
		render("/ab/reg/regsuccess.html");
	}
	
	public void checkloginid(){
		String loginid = StringUtil.toStr(this.getPara("mobile"));
		if(Db.queryLong("SELECT COUNT(id) FROM sys_user WHERE loginid=?",loginid).longValue()>0){
			renderJson(false);
		}else{
			renderJson(true);
		}
	}
	
	/**
	 * 
	 */
	public void savereguser(){
		boolean flag= false;
		String dateStr = DateUtil.getCurrentDate();
		SysUser user = this.getModel(SysUser.class,"user");
		String uuid = StringUtil.getUuid32();
		user.set("loginpwd", MD5.GetMD5Code(user.getStr("loginpwd")));
		user.set("id", uuid);
		user.set("loginid", user.get("mobile"));
		user.set("certificatemobile", "1");
		user.set("status", "0");
		user.set("create_date", dateStr);
//		String userType = StringUtil.toStr(this.getPara("userType"));
//		String userSubType = StringUtil.toStr(this.getPara("userSubType"));
//		SysUserType ut = new SysUserType();
//		ut.set("id", StringUtil.getUuid32());
//		ut.set("usertype", userType);
//		ut.set("usersubtype", userSubType);
//		ut.set("userid", uuid);
//		ut.save();
		//注册送30积分
		user.set(SysUser.JIFEN, CommonStaticData.REG_SCORE);
		user.save();
		//增加积分变动
		AbIntegralScore ail = new AbIntegralScore();
		ail.set(AbIntegralScore.ID, StringUtil.getUuid32());
		ail.set(AbIntegralScore.TYPE, CommonStaticData.SCORE_TYPE1);
		ail.set(AbIntegralScore.VALUE, CommonStaticData.REG_SCORE);
		ail.set(AbIntegralScore.DATETIME, dateStr);
		ail.set(AbIntegralScore.USER_ID, uuid);
		ail.set(AbIntegralScore.FID, "");
		ail.save();
		
		//判断是否好友邀请
		String invitecode = this.getPara("invitecode");
		if(!StringUtil.isNull(invitecode)){
			SysUser fuser = null;
			List<SysUser> sus = SysUser.dao.find("select * from sys_user where invite = '"+invitecode+"'");
			if(sus != null && sus.size()>0){
				fuser = sus.get(0);
			}
			//更新好友用户积分，增加邀请好友25积分
			if(fuser != null){
				String fuid = fuser.getStr(SysUser.ID);
				fuser.set(SysUser.JIFEN, fuser.getInt(SysUser.JIFEN)+CommonStaticData.ADD_SCORE);
				fuser.update();
				
				//增加积分变动
				AbIntegralScore as = new AbIntegralScore();
				as.set(AbIntegralScore.ID, StringUtil.getUuid32());
				as.set(AbIntegralScore.TYPE, CommonStaticData.SCORE_TYPE5);
				as.set(AbIntegralScore.VALUE, CommonStaticData.ADD_SCORE);
				as.set(AbIntegralScore.DATETIME, dateStr);
				as.set(AbIntegralScore.USER_ID, fuid);
				as.set(AbIntegralScore.FID, "");
				as.save();
				
				//增加邀请记录
				SysUserInvite sui = new SysUserInvite();
				sui.set(SysUserInvite.ID, StringUtil.getUuid32());
				sui.set(SysUserInvite.LOGINID, fuid);
				sui.set(SysUserInvite.INVITE_LOGINID, uuid);
				sui.save();
			}
		}
		//获取服务分类
		String[] subjectids = this.getParaValues("subjectid");
		if(subjectids!=null&&subjectids.length>0){
			for (int i = 0; i < subjectids.length; i++) {
				SysUserSubject.dao
				.set("id", StringUtil.getUuid32())
				.set("user_id", uuid)
				.set("subject_id", subjectids[i])
				.save();
			}
		}
		
		this.setSessionAttr("abuser", user);
		ObjectNode datanode = JsonNodeFactory.instance.objectNode();
        datanode.put("username",user.getStr("loginid"));
        datanode.put("password", user.getStr("loginpwd"));
        EasemobIMUsers.createNewIMUserSingle(datanode);
		flag = true;
		renderJson(flag);
	}
	
	public void saveregywy(){
		boolean flag= false;
		SysUser user = this.getModel(SysUser.class,"user");
		String uuid = StringUtil.getUuid32();
		user.set("id", uuid);
		user.set("loginid", user.get("mobile"));
		user.set("loginpwd", MD5.GetMD5Code(user.getStr("loginpwd")));
		user.set("certificatemobile", "1");
		user.set("status", "0");
		user.set("create_date", DateUtil.getCurrentDate());
//		AbCityarea aca = AbCityarea.dao.findById(user.getStr("city_id"));
//		if(null!=aca && null!= aca.getStr("user_id")){
//			user.set("p_id", aca.getStr("user_id"));
//		}else{
//			user.set("p_id", "admin");
//		}
		String userType = StringUtil.toStr(this.getPara("userType"));
		String userSubType = StringUtil.toStr(this.getPara("userSubType"));
		SysUserType ut = new SysUserType();
		ut.set("id", StringUtil.getUuid32());
		ut.set("usertype", userType);
		ut.set("usersubtype", userSubType);
		ut.set("userid", uuid);
		ut.save();
		user.save();
		this.setSessionAttr("abuser", user);
		ObjectNode datanode = JsonNodeFactory.instance.objectNode();
        datanode.put("username",user.getStr("loginid"));
        datanode.put("password", user.getStr("loginpwd"));
        EasemobIMUsers.createNewIMUserSingle(datanode);
		flag = true;
		renderJson(flag);
	}
	
	/**
	 * 保存商家信息
	 */
	public void saveregmer(){
		boolean flag= false;
		SysUser user = this.getModel(SysUser.class,"user");
		AbMerchant mer = this.getModel(AbMerchant.class,"mer");
		String uuid = StringUtil.getUuid32();
		String str_date = DateUtil.getCurrentDate();
		user.set("id", uuid);
		user.set("loginid", user.get("mobile"));
		user.set("loginpwd", MD5.GetMD5Code(user.getStr("loginpwd")));
		user.set("mid", uuid);
		user.set("mc", mer.get("mc"));
		user.set("tel", mer.get("tel"));
		user.set("qq", mer.get("qq"));
		user.set("yzbm", mer.get("yzbm"));
		user.set("xxdz", mer.get("xxdz"));
		user.set("lng", mer.get("lng"));
		user.set("lat", mer.get("lat"));
		user.set("create_date", str_date);
		user.set("city_id", mer.get("city_id"));
		user.set("city_name", mer.get("city_name"));
		user.set("area_id", mer.get("area_id"));
		user.set("area_name", mer.get("area_name"));
//		AbCityarea aca = AbCityarea.dao.findById(user.getStr("city_id"));
//		if(null!=aca && null!= aca.getStr("user_id")){
//			user.set("p_id", aca.getStr("user_id"));
//		}else{
//			user.set("p_id", "admin");
//		}
		mer.set("id", uuid);
		mer.set("mapbusiness", user.get("mapbusiness"));
		mer.set("mobile", user.get("mobile"));
		mer.set("user_id", uuid);
		mer.set("create_time", str_date);
//		String userType = StringUtil.toStr(this.getPara("userType"));
//		String userSubType = StringUtil.toStr(this.getPara("userSubType"));
//		SysUserType ut = new SysUserType();
//		ut.set("id", StringUtil.getUuid32());
//		ut.set("usertype", userType);
//		ut.set("usersubtype", userSubType);
//		ut.set("userid", uuid);
//		ut.save();
		
		//获取分类是货物还是服务
		
//		AbSubject sub = AbSubject.dao.findById(mer.get("subject_id"));
//		mer.set("is_type", sub.get("is_type"));
		user.save();
		mer.save();
		ObjectNode datanode = JsonNodeFactory.instance.objectNode();
        datanode.put("username",user.getStr("loginid"));
        datanode.put("password", user.getStr("loginpwd"));
        EasemobIMUsers.createNewIMUserSingle(datanode);
		this.setSessionAttr("abuser", user);
		
		flag = true;
		renderJson(flag);
	}
	
	/**
	 * 选择城市
	 */
	public void seljwd(){
		render("/ab/reg/seljwd.html");
	}
	
	public void certiEmail(){
		render("/ab/cer/certificateemail.html");
	}
	
	public void certiEmailSend(){
		String id = this.getPara("id");
		String mail = this.getPara("email");
		String subject = "【爱帮网】邮件认证";
		String content = "http://www.yijuhome.cn/ab/certiEmailUpdate/"+id+"-"+StringUtil.getRandString32();
		boolean flag = SimpleMailSender.sendMail(mail, subject, content, true);
		if(flag){
			flag = SysUser.dao.findById(id).set("certificateemail", "2").update();
			if(flag){
				this.setSessionAttr("abuser", SysUser.dao.findById(id));
			}
		}
		renderJson(flag);
	}
	
	public void certiEmailUpdate(){
		String id = this.getPara(0);
		boolean flag = SysUser.dao.findById(id).set("certificateemail", "1").update();
		if(flag)
			redirect("http://www.yijuhome.cn");
		else
			redirect("http://www.yijuhome.cn/ab/login");
	}
	
	public void certiPhone(){
		render("/ab/cer/certificatephone.html");
	}
	
	public void certiCard(){
		render("/ab/cer/certificatecard.html");
	}
	
	
}
