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
import com.zp.tools.RandomUtil;
import com.zp.tools.SmsMessage;
import com.zp.tools.StringUtil;

public class AppCityController extends AbsAppController {

	
	public void hello(){
		renderJson(true);
	}
	
	/**
	 * APP端用户登录
	 */
	public void login() {
		String username = this.getPara("username");
		String password = this.getPara("password");

		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			if (checkParam(username, password)) {// 参数校验通过
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
						if (user.get("role_id").toString().contains("105")
								|| user.get("role_id").toString()
										.contains("106")
								|| user.get("role_id").toString()
										.contains("107")) {
							resultMap.put("uid", user.getStr("id"));
							formatSuccessResponse(resultMap);
						} else {
							resultMap.put("result", RESULT_FAIL);
							resultMap.put("msg", "非【会员】、【商户】、【业务员】请选择其他登录方式！");
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
			e.printStackTrace();
			formatInvokeFailResponse(e, resultMap);
		}

		renderJson(resultMap);
	}

	/**
	 * 更新用户所在位置
	 */
	public void switchCity() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		// 根据uid查询记录
		String uid = this.getPara("uid");
		String cityId = this.getPara("city_id");

		try {
			if (checkParam(uid, cityId)) {
				SysUser u = SysUser.dao.findById(uid);
				if (u == null) {
					formatInvalidParamResponse(resultMap);
				} else {
					u.update();
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
