package com.zp.controller.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.core.Controller;
import com.zp.entity.AbCityarea;
import com.zp.entity.AbIntegralScore;
import com.zp.entity.SysUser;
import com.zp.tools.CommonStaticData;
import com.zp.tools.DateUtil;
import com.zp.tools.MD5;
import com.zp.tools.RandomUtil;
import com.zp.tools.SmsMessage;
import com.zp.tools.StringUtil;

public class AppIndexController extends AbsAppController {

	/**
	 * APP端用户登录
	 */
	public void login() {
		String username = this.getPara("username");
		String password = this.getPara("password");

		
     
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			if (checkParam(username, password)) {// 参数校验通过
				password  =  MD5.GetMD5Code(password);
				System.out.println(password);
				SysUser user = SysUser.dao
						.findFirst(
								"select * from sys_user where loginid=? and loginpwd=?",
								username, password);
				if (user == null || user.get("id") == null) {// 用户不存在
					resultMap.put("result", RESULT_FAIL);
					resultMap.put("msg", "用户名或者密码错误，请重新输入！");

				} else {
					user = SysUser.dao.findById(user.getStr("id"));
					
					if (user != null) {
						resultMap.put("mobile", user.getStr("mobile"));
						resultMap.put("mc", user.getStr("mc"));
						String dateStr = DateUtil.getCurrentDate();
						String lastdateStr = user.getStr("login_date") == null ? DateUtil
								.getCurrentDate() : user.getStr("login_date");
						user.set("login_date", dateStr).set(
								"login_ip",
								StringUtil.getRemoteLoginUserIp(this
										.getRequest()));
						// 增加积分变动记录,不是同一天增加2个积分
						if ("107".equals(user.get("role_id"))
								&& !DateUtil.isSameDay(lastdateStr)) {
							int jf = user.getInt(SysUser.JIFEN);
							user.set("jifen", jf + CommonStaticData.LOGIN_SCORE);
							AbIntegralScore ail = new AbIntegralScore();
							ail.set(AbIntegralScore.ID,
									StringUtil.getRandString32());
							ail.set(AbIntegralScore.TYPE,
									CommonStaticData.SCORE_TYPE2);
							ail.set(AbIntegralScore.VALUE,
									CommonStaticData.LOGIN_SCORE);
							ail.set(AbIntegralScore.DATETIME, dateStr);
							ail.set(AbIntegralScore.USER_ID, user.getStr("id"));
							ail.set(AbIntegralScore.FID, "");
							ail.save();
						}
						user.update();
						if (user.get("role_id").toString().contains("107")) {
							formatSuccessResponse(resultMap);
							resultMap.put("uid", user.getStr("id"));
							String logoPath = user.getStr(SysUser.LOGO);
							resultMap.put("headicon", getBaseUrl()+"/upload/"+logoPath);
							resultMap.put("sfrzzt", user.getStr("sfrzzt"));
							
							resultMap.put("zhlx", user.getStr("zhlx"));
						} else {
							resultMap.put("result", RESULT_FAIL);
							resultMap.put("msg", "非【会员】请选择其他登录方式！");
						}

					} else {
						resultMap.put("result", RESULT_FAIL);
						resultMap.put("msg", "用户名或者密码错误，请重新输入！");
					}
				}

			} else {
				formatInvalidParamResponse(resultMap);
			}
		} catch (Exception e) {// 调用失败
			formatInvokeFailResponse(e, resultMap);
		}

		// 返回给客户端json数据
		renderJson(resultMap);
	}

	/**
	 * 发送短信
	 */
	public void sendSmsMsg() {
		String phone = this.getPara("mobile");
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			if (checkParam(phone)) {
				String checkcode = RandomUtil.createRandomNumPwd(6);
				SmsMessage.SendMessage(phone, checkcode);
				resultMap.put("checkcode", checkcode);
				formatSuccessResponse(resultMap);
			} else {// 参数校验不通过
				formatInvalidParamResponse(resultMap);
			}
		} catch (Exception e) {
			formatInvokeFailResponse(e, resultMap);
		}

		renderJson(resultMap);
	}

	/**
	 * 获取城市列表（不包含商圈）
	 */
	public void getCityList() {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			List<AbCityarea> cityList = AbCityarea.dao
					.find("SELECT id,mc from ab_cityarea where type_id=0 order by seq_num");
			resultMap.put("data", cityList);
			formatSuccessResponse(resultMap);
		} catch (Exception e) {
			formatInvokeFailResponse(e, resultMap);
		}

		renderJson(resultMap);
	}

	/**
	 * 注册用户，只能由[会员]登录
	 */
	public void register() {
		String mobile = this.getPara("mobile");
		String password = this.getPara("password");
		Integer zhxl = this.getParaToInt("zhlx");
		String region = this.getPara("region_id");  //区域id
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			if (checkParam(mobile, password,region,zhxl)) {
				password  =  MD5.GetMD5Code(password);
				// 查看用户是否存在
				SysUser u = SysUser.dao.findFirst(
						"select id from sys_user where loginid=?", mobile);
				if (u != null) {// 用户存在
					resultMap.put("result", RESULT_FAIL);
					resultMap.put("msg", "该手机号码已经注册过了，请确认！");
				} else {
					String dateStr = DateUtil.getCurrentDate();
					SysUser user = new SysUser();
					String uuid = StringUtil.getUuid32();
					user.set("id", uuid);
					user.set("loginid", mobile);
					user.set("loginpwd", password);
					user.set("certificatemobile", "1");
					user.set("status", "0");
					user.set("mobile", mobile);
					user.set("create_date", dateStr);
					user.set("zhlx", 0);
					user.set("role_id", 107);
					user.set("area_id", region);
					try{
						AbCityarea aca = AbCityarea.dao.findById(region.substring(0, 6));
						if(null!=aca && null!= aca.getStr("user_id")){
							user.set("p_id", aca.getStr("user_id"));
						}else{
							user.set("p_id", "admin");
						}
					}catch(Exception e){
						user.set("p_id", "admin");
					}
					user.set("role_name", "会员");
					user.set("zhlx", zhxl);
					
					
					// 注册送30积分
					user.set(SysUser.JIFEN, CommonStaticData.REG_SCORE);
					user.save();
					// 增加积分变动
					AbIntegralScore ail = new AbIntegralScore();
					ail.set(AbIntegralScore.ID, StringUtil.getUuid32());
					ail.set(AbIntegralScore.TYPE, CommonStaticData.SCORE_TYPE1);
					ail.set(AbIntegralScore.VALUE, CommonStaticData.REG_SCORE);
					ail.set(AbIntegralScore.DATETIME, dateStr);
					ail.set(AbIntegralScore.USER_ID, uuid);
					ail.set(AbIntegralScore.FID, "");
					ail.save();

					resultMap.put("uid", uuid);
					formatSuccessResponse(resultMap);
				}
			} else {// 参数校验不通过
				formatInvalidParamResponse(resultMap);
			}
		} catch (Exception e) {
			formatInvokeFailResponse(e, resultMap);
		}

		renderJson(resultMap);
	}


}
