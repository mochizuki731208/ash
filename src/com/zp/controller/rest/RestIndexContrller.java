package com.zp.controller.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.zp.entity.AbCityarea;
import com.zp.entity.AbFmcar;
import com.zp.entity.AbFmcarCity;
import com.zp.entity.AbFmcarUser;
import com.zp.entity.AbIntegralScore;
import com.zp.entity.SysUser;
import com.zp.tools.CommonStaticData;
import com.zp.tools.DateUtil;
import com.zp.tools.MD5;
import com.zp.tools.RandomUtil;
import com.zp.tools.SmsMessage;
import com.zp.tools.StringUtil;

public class RestIndexContrller extends AbstractRestController {
	
	/**
	* 保存我的信息
	*/
	public void savePersionalInfo(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String userId = getPara("uid");// 用户id
		String mobile = getPara("mobile");// 联系手机
		String driver = getPara("driver");// 司机姓名
		String carNo = getPara("carNo");// 车牌号
		String length = getPara("length");// 车长
		String type = getPara("type");// 车型 
		String carId = getPara("carId");// 
		String tjh = getPara("tjh");// 
		String isProtect =  getPara("isProtect");//
		String runCitys = getPara("runCitys");// 常用城市，多个以逗号隔开，每个项由cityId加“#”再加cityName组成，如：330300#大同，140900#漳州
		if(StringUtil.isNull(userId) || StringUtil.isNull(mobile) || StringUtil.isNull(driver) ||
				StringUtil.isNull(carNo) || StringUtil.isNull(length) || StringUtil.isNull(type) || StringUtil.isNull(runCitys)) {
			//检查参数是否合法			
			resultMap.put("result", RESULT_FAIL);
			resultMap.put("msg", RESULT_INVALID_PARAM);
		} else {
			//修改，把司机信息提到sys_user中
			
			AbFmcar car = null;
			if(StringUtil.isNull(carId)) {
				carId = StringUtil.getRandString32();
				car = new AbFmcar();
				setColumnVal(mobile, driver, carNo, length, type, carId, tjh, isProtect, car);
				car.save();
				AbFmcarUser carUser = new AbFmcarUser();
				carUser.set(AbFmcarUser.ID, StringUtil.getRandString32());
				carUser.set(AbFmcarUser.CAR_ID, carId);
				carUser.set(AbFmcarUser.USER_ID, userId);
				carUser.save();
			} else {
				car = AbFmcar.dao.findById(carId);
				setColumnVal(mobile, driver, carNo, length, type, carId, tjh, isProtect, car);
				AbFmcarUser carUser = AbFmcarUser.dao.findFirst("SELECT * FROM ab_fmcar_user WHERE car_id=? AND user_id=?", new Object[]{carId, userId});
				if(null == carUser) {
					carUser = new AbFmcarUser();
					carUser.set(AbFmcarUser.ID, StringUtil.getRandString32());
					carUser.set(AbFmcarUser.CAR_ID, carId);
					carUser.set(AbFmcarUser.USER_ID, userId);
					carUser.save();
				}				
				car.update();
			}
			addFmCarCitys(runCitys, carId);
			resultMap.put("carId", carId);
			resultMap.put("result", RESULT_SUCCESS);
			resultMap.put("msg", "保存成功");
		}
		renderJson(resultMap);	
	}
	public void findPwd(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String mobile = this.getPara("mobile");
		String pwd = this.getPara("pwd");
		if(StringUtil.isNull(mobile) || StringUtil.isNull(pwd) ) {
			//检查参数是否合法			
			resultMap.put("result", RESULT_FAIL);
			resultMap.put("msg", RESULT_INVALID_PARAM);
		} else {
			SysUser u = SysUser.dao.findFirst("select * from sys_user where loginid =?",mobile);
			u.set("loginpwd", MD5.GetMD5Code(pwd)).update();
			resultMap.put("result", RESULT_SUCCESS);
			resultMap.put("msg", "修改成功");
		}
		renderJson(resultMap);	
	}
	private void setColumnVal(String mobile, String driver, String carNo,
			String length, String type, String carId, String tjh, String isProtect ,AbFmcar car) {
		car.set("id", carId);
		car.set(AbFmcar.DRIVER, driver);
		car.set(AbFmcar.RECOMMEND_NO, tjh);
		car.set(AbFmcar.CAR_NO, carNo);
		car.set(AbFmcar.LENGTH, length);
		car.set(AbFmcar.TYPE, type);
		car.set(AbFmcar.MOBILE, mobile);
		car.set(AbFmcar.IS_PROTECT, isProtect);
	}
	
	/**
	 * 保存“我的数车” 对应的常跑城市
	 */
	private void addFmCarCitys(String runCitys, String carId) {		
		String citys[] = runCitys.split(";");
		int ciLength = citys.length;
		for (int i = 0; i < ciLength; i++) {
			AbFmcarCity  afc = new AbFmcarCity();
			String city = citys[i];
			if(!StringUtil.isNull(city)){
				String citys2[] = city.split("#");
				afc.set(AbFmcarCity.CITY_NAME, citys2[1]);
			}			
			afc.set(AbFmcarCity.ID, StringUtil.getRandString32());
			afc.set(AbFmcarCity.CAR_ID, carId);			
			afc.save();
		}
	}
	
	/**
	 * APP用户得到我的信息
	 */
   public void getPersionalInfo() {
	   String mobile = this.getPara("mobile");//获取当前登录人的帐号 也就是电话号码
	   String uid = this.getPara("uid");//uid
	   Map<String, Object> resultMap = new HashMap<String, Object>();
	   if(checkParam(mobile, uid)){
			AbFmcar car = AbFmcar.dao.findByMobile(mobile);//根据电话号码查询对应的熟车
			resultMap.put("result", RESULT_SUCCESS);
			if(null != car) {
				resultMap.put(AbFmcar.ID, car.getStr(AbFmcar.ID));
				resultMap.put(AbFmcar.CAR_NO, car.getStr(AbFmcar.CAR_NO));
				resultMap.put(AbFmcar.DRIVER, car.getStr(AbFmcar.DRIVER));
				resultMap.put(AbFmcar.TYPE, car.getStr(AbFmcar.TYPE));
				resultMap.put(AbFmcar.LENGTH, car.getStr(AbFmcar.LENGTH));
				resultMap.put(AbFmcar.RECOMMEND_NO, car.getStr(AbFmcar.RECOMMEND_NO));
				resultMap.put(AbFmcar.IS_PROTECT, car.getInt(AbFmcar.IS_PROTECT));
//				StringBuffer carcity = new StringBuffer("");
				List<AbFmcarCity> cityList = AbFmcarCity.dao.findByCarId(car.getStr(AbFmcar.ID)); 
//				if(cityList != null && cityList.size() > 0){
//					int len = cityList.size();
//					for(int i=0; i<(len-1); i++){
//						carcity.append(cityList.get(i).getStr("city_name") + ",");
//					}
//					carcity.append(cityList.get(len-1).getStr("city_name"));
//				}
				resultMap.put("carcity", cityList);
			}
	   } else {
		   formatInvalidParamResponse(resultMap); 
	   }
	   // 返回给客户端json数据
	   renderJson(resultMap);
   }
	
   public void updateHeadImg(){
	   Map<String, Object> resultMap = new HashMap<String, Object>();
		String uid = this.getPara("uid");
		// 根据uid查询记录
		String headImgPath = this.getPara("headImgPath"); //驾照图片的文件名。
		String roleId = this.getPara("roleId"); //驾照图片的文件名。
		try {
			if (checkParam(uid, headImgPath, roleId)) {
				SysUser user= SysUser.dao.findById(uid);
				if (user == null) {
					formatInvalidParamResponse(resultMap);
				} else {
					if("106".equals(roleId)) {//司机
						user.set("dtz", headImgPath);//大头照的文件名
					}else {//企业
						user.set(SysUser.LOGO, headImgPath);
					}
					user.update();
					resultMap.put("result", RESULT_SUCCESS);
					resultMap.put("msg", "修改成功");
				}
			} else {// 参数校验不通过
				formatInvalidParamResponse(resultMap);
			}
		} catch (Exception e) {
			formatInvokeFailResponse(e, resultMap);
		}
		renderJson(resultMap);
   }
   
	/**
	 * APP端用户登录
	 */
	public void login() {
		String username = this.getPara("username");
		String password = this.getPara("password");
		String deviceid = this.getPara("deviceid");
		//
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			if (checkParam(username, password, deviceid)) {// 参数校验通过
				SysUser user = SysUser.dao.findFirst("SELECT  * FROM sys_user  WHERE loginid=? and loginpwd=?", 
						username, password);
				if (user == null || user.get("id") == null) {// 用户不存在
					resultMap.put("result", RESULT_FAIL);
					resultMap.put("msg", "用户名或者密码错误，请重新输入！");
				} else {
					user = SysUser.dao.findById(user.getStr("id"));
				 	if (user != null) {
						String dateStr = DateUtil.getCurrentDate();
						String lastdateStr = user.getStr("login_date") == null ? DateUtil
								.getCurrentDate()
								: user.getStr("login_date");
						user.set("login_date", dateStr).set("login_ip",
								StringUtil.getRemoteLoginUserIp(this.getRequest()));
						// 增加积分变动记录,不是同一天增加2个积分
						if ("107".equals(user.get("role_id"))
								&& !DateUtil.isSameDay(lastdateStr)) {
							int jf = user.getInt(SysUser.JIFEN);
							user.set("jifen", jf + CommonStaticData.LOGIN_SCORE);
							AbIntegralScore ail = new AbIntegralScore();
							ail.set(AbIntegralScore.ID, StringUtil.getRandString32());
							ail.set(AbIntegralScore.TYPE, CommonStaticData.SCORE_TYPE2);
							ail
									.set(AbIntegralScore.VALUE,
											CommonStaticData.LOGIN_SCORE);
							ail.set(AbIntegralScore.DATETIME, dateStr);
							ail.set(AbIntegralScore.USER_ID, user.getStr("id"));
							ail.set(AbIntegralScore.FID, "");
							ail.save();
						}						
						if (user.get("role_id").toString().contains("105")
								|| user.get("role_id").toString().contains("107")) {
//							if(StringUtil.isNull(user.getStr(SysUser.DEVICEID))) {
//								user.set("is_online", "1");
//								user.set(SysUser.DEVICEID, deviceid);
//								setUpSuccess(resultMap, user);
//							} else if(!deviceid.equals(user.getStr(SysUser.DEVICEID))){
//								resultMap.put("result", RESULT_FAIL);
//								resultMap.put("msg", "手机识别错误,请用注册手机登录");
//							|| user.get("role_id").toString().contains("107")
//							}else {
								user.set("is_online", "0");
								setUpSuccess(resultMap, user);
//							}
						} else {
							resultMap.put("result", RESULT_FAIL);
							resultMap.put("msg", "非【会员】、【商户】、【业务员】请选择其他登录方式！");
						}
						user.update();
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
	
	private void setUpSuccess(Map<String, Object> resultMap, SysUser user) {
		resultMap.put("result", RESULT_SUCCESS);
		resultMap.put("uid", user.getStr("id"));
		resultMap.put(SysUser.LOGINID, user.getStr(SysUser.LOGINID));
		resultMap.put("sjzt", user.getStr("sjzt"));
		resultMap.put(SysUser.MC, user.getStr(SysUser.MC));
		resultMap.put(SysUser.LOGO, user.getStr(SysUser.LOGO));
		
		resultMap.put(SysUser.RATE, user.getBigDecimal(SysUser.RATE));
		resultMap.put(SysUser.CREDIT, user.get(SysUser.CREDIT));
		
		resultMap.put(SysUser.MOBILE, user.getStr(SysUser.MOBILE));
		resultMap.put(SysUser.ROLE_ID, user.getStr(SysUser.ROLE_ID));
		resultMap.put(SysUser.REGION, user.getStr(SysUser.REGION));
		resultMap.put(SysUser.ZHYE, user.getBigDecimal(SysUser.ZHYE));
		resultMap.put(SysUser.PER, user.getBigDecimal(SysUser.PER));
		resultMap.put("sfrzzt", user.get("sfrzzt"));
		resultMap.put("jz", user.getStr("jz"));//驾照图片的文件名。
		resultMap.put("ct", user.getStr("ct"));// 车头和车牌号码的文件名
		resultMap.put("jz", user.getStr("jz"));// 车身的文件名
		resultMap.put("cs", user.getStr("cs"));// 车身的文件名 
		resultMap.put("xsz", user.getStr("xsz"));//行驶证的文件名
		resultMap.put("dtz", user.getStr("dtz"));// 大头照的文件名
		
		resultMap.put("qymc", user.getStr("qymc"));//企业名称
		resultMap.put("qyyx", user.getStr("qyyx"));//企业邮箱
		resultMap.put("qydz", user.getStr("qydz"));//企业地址
		resultMap.put("yyzzzch", user.getStr("yyzzzch"));//营业执照号
		resultMap.put("zzjgdm", user.getStr("zzjgdm"));//组织机构代码
		resultMap.put("yyzsfzh", user.getStr("yyzsfzh"));//运营者身份证号
		resultMap.put("yyzzsmj", user.getStr("yyzzsmj"));//营业执照副本扫描件
		resultMap.put("yyzscsfzzp", user.getStr("yyzscsfzzp"));//运营者手持照片
	}

	/**
	 * 发送短信
	 */
	public void sendSmsMsg() {
		String phone = this.getPara("phone");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			if (checkParam(phone)) {
				String checkcode = RandomUtil.createRandomNumPwd(6);
				SmsMessage.SendMessage(phone, checkcode);
				resultMap.put("checkcode", checkcode);
				resultMap.put("result", RESULT_SUCCESS);
			} else {// 参数校验不通过
				formatInvalidParamResponse(resultMap);
			}
		} catch (Exception e) {
			formatInvokeFailResponse(e, resultMap);
		}
		renderJson(resultMap);
	}

	/**
	 * SELECT id,mc from ab_cityarea, type_id where order by seq_num
	 * 获取商圈列表（不包含城市）
	 */
	public void getCityAreaList() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			List<AbCityarea> cityList = AbCityarea.dao
					.find("SELECT id,mc,type_id FROM ab_cityarea WHERE type_id IS NOT NULL  ORDER BY type_id");
			resultMap.put("data", cityList);
			resultMap.put("result", RESULT_SUCCESS);
		} catch (Exception e) {
			formatInvokeFailResponse(e, resultMap);
			
		}
		renderJson(resultMap);
	}

	/**
	 * 注册用户
	 */
	public void register() {
		String mobile = this.getPara("mobile");
		String password = this.getPara("password");
		String deviceid = this.getPara("deviceid");
		// 类型，0为司机，1为商户
		Integer type = this.getParaToInt("type");
		String cityId = this.getPara("cityId");//商圈id
		String cityName = this.getPara("cityName");//城市名称
		String checkCode = this.getPara("checkCode");//验证码
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			if (checkParam(mobile, password, type, deviceid)) {
				if (type != 0 && type != 1) {
					resultMap.put("result", RESULT_FAIL);
					resultMap.put("msg", RESULT_INVALID_PARAM);
				} else {
					// 查看用户是否存在
					SysUser u = SysUser.dao.findFirst("select id from sys_user where loginid=?", mobile);
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
						user.set("city_name", cityName);
						
//						user.set(SysUser.DEVICEID, deviceid);
						user.set("certificatemobile", "1");
						user.set("status", "0");
						user.set("mobile", mobile);
						user.set("create_date", dateStr);
						user.set("area_id", cityId);
						user.set("zhlx", 0);
						if (type == 0) {// 司机
							user.set("role_id", 106);
							user.set("role_name", "业务员");
						} else if (type == 1) {// 商户
							user.set("role_id", 105);
							user.set("role_name", "店铺商家");
						}

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
						resultMap.put("result", RESULT_SUCCESS);
					}
				}
			} else {// 参数校验不通过
				formatInvalidParamResponse(resultMap);
			}
		} catch (Exception e) {
			formatInvokeFailResponse(e, resultMap);
		}

		renderJson(resultMap);
	}
	
	/**
	 * 更新司机认证信息
	 */
	public void editYwyRzInfo() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String uid = this.getPara("uid");
		// 根据uid查询记录
		String jz = this.getPara("jz"); //驾照图片的文件名。
		String cph = this.getPara("cph");// 车头和车牌号码的文件名
		String xsz = this.getPara("xsz");// 行驶证的文件名
		String sfz = this.getPara("sfz");// 身份证的文件名
		String mc = this.getPara("mc");// 身份证的文件名
		String idcard = this.getPara("idcard");// 身份证的文件名
		String cc = this.getPara("cc");// 身份证的文件名
		String cx = this.getPara("cx");// 身份证的文件名
		String ctj = this.getPara("ctj");// 身份证的文件名
		String cpcs = this.getPara("cpcs");// 身份证的文件名
//		mc
//		idcard
//		cph
//		cc
//		cx
//		ctj
//		cpcs
//		xsz 
//		sfz 

		try {
			if (checkParam(uid)) {
				SysUser user = SysUser.dao.findById(uid);
				user.set("tjsj", DateUtil.getCurrentDate());
				user.set("sfrzzt", "1");
				user.set("jz", jz);
				user.set("idcard", idcard);
				user.set("cph", cph);
				user.set("cc", cc);
				user.set("cx", cx);
				user.set("ctj", ctj);
				user.set("cpcs", cpcs);
				user.set("xsz", xsz);
				user.set("mc", mc);
				user.set("sfzzpzm", sfz);
						/** 认证状态[0-未认证、1-已提交、2-审核通过、3-认证不通过]**/
						resultMap.put("sfrzzt", "1");
						user.update();
					resultMap.put("result", RESULT_SUCCESS);
					resultMap.put("msg", "提交成功");
			} else {// 参数校验不通过
				formatInvalidParamResponse(resultMap);
			}
		} catch (Exception e) {
			formatInvokeFailResponse(e, resultMap);
		}

		renderJson(resultMap);
	}

	/**
	 * 更新企业认证信息
	 */
	public void editQyRzInfo() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String uid = this.getPara("uid");
		// 根据uid查询记录
		String qymc = this.getPara("qymc");//企业名称
		String qyyx = this.getPara("qyyx");//企业邮箱
		String qydz = this.getPara("qydz");//企业地址
		String yyzzzch = this.getPara("yyzzzch");// 营业执照号
		String zzjgdm = this.getPara("zzjgdm");// 组织机构代码
		String yyzsfzh = this.getPara("yyzsfzh");// 运营者身份证号
		String yyzzsmj = this.getPara("yyzzsmj");// 营业执照副本扫描件
		String yyzscsfzzp = this.getPara("yyzscsfzzp");// 运营者手持照片

		try {
			if (checkParam(uid, qymc, qyyx, qydz, yyzzzch, zzjgdm, yyzsfzh, yyzzsmj, yyzscsfzzp)) {
				SysUser u = SysUser.dao.findById(uid);
				if (u == null) {
					formatInvalidParamResponse(resultMap);
				} else {
					u.set("qymc", qymc);
					u.set("qyyx", qyyx);
					u.set("qydz", qydz);
					u.set("yyzzzch", yyzzzch);
					u.set("zzjgdm", zzjgdm);
					u.set("yyzsfzh", yyzsfzh);
					u.set("yyzzsmj", yyzzsmj);
					u.set("yyzscsfzzp", yyzscsfzzp);
					u.update();

					resultMap.put("result", RESULT_SUCCESS);
				}
			} else {// 参数校验不通过
				formatInvalidParamResponse(resultMap);
			}
		} catch (Exception e) {
			formatInvokeFailResponse(e, resultMap);
		}
		renderJson(resultMap);
	}

	/**
	 * 开始接单或者暂停接单
	 */
	public void staerOrStopRecivieOrder() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String uid = this.getPara("uid");
		try {
			if (checkParam(uid)) {
				SysUser u = SysUser.dao.findById(uid);
				if (u == null) {
					formatInvalidParamResponse(resultMap);
				} else {
					/** 0-在线、1-离线、2-空闲、3-忙碌、4-手工回家 **/
					if("3".equals(u.getStr("sjzt"))) {
						u.set("sjzt", "2");
						resultMap.put("sjzt", "2");
					} else {
						u.set("sjzt", "3");
						resultMap.put("sjzt", "3");
					}
					u.update();
					resultMap.put("result", RESULT_SUCCESS);
				}
			} else {// 参数校验不通过
				formatInvalidParamResponse(resultMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			formatInvokeFailResponse(e, resultMap);
		}
		renderJson(resultMap);
	}

	/**
	 *退出app
	 */
	public void exitApp() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String uid = this.getPara("uid");
		try {
			if (checkParam(uid)) {
				SysUser u = SysUser.dao.findById(uid);
				if (u == null) {
					formatInvalidParamResponse(resultMap);
				} else {	
					//是否在线  0 在线    1不在线
					/** 0-在线、1-离线、2-空闲、3-忙碌、4-手工回家 **/
					u.set("is_online", "1");
					u.update();
					resultMap.put("result", RESULT_SUCCESS);
				}
			} else {// 参数校验不通过
				formatInvalidParamResponse(resultMap);
			}
		} catch (Exception e) {
			formatInvokeFailResponse(e, resultMap);
		}
		renderJson(resultMap);
	}
	public void getUser() {
		String uid = this.getPara("uid");
		renderJson(SysUser.dao.findById(uid));
	}
	
}
