package com.zp.controller.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.easemob.server.comm.Constants;
import com.easemob.server.jersey.api.EasemobIMUsers;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.zp.entity.AbCityarea;
import com.zp.entity.AbFmcar;
import com.zp.entity.AbIntegralScore;
import com.zp.entity.AbMerchant;
import com.zp.entity.AbSubject;
import com.zp.entity.AbUserAddr;
import com.zp.entity.SysUser;
import com.zp.entity.SysUserInvite;
import com.zp.entity.SysUserSubject;
import com.zp.entity.SysUserType;
import com.zp.tools.CommonStaticData;
import com.zp.tools.DateUtil;
import com.zp.tools.MD5;
import com.zp.tools.QRCodeUtil;
import com.zp.tools.StringUtil;

public class AppUserController extends AbsAppController {

	/**
	 * 设置logo
	 */
	public void setLogo() {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		// System.out.println(getRequest().getRemoteHost());
		// System.out.println(getRequest().getLocalName());
		// System.out.println(getRequest().getServerName());
		// System.out.println(getRequest().getPathInfo());
		// System.out.println(getRequest().getPathTranslated());

		String uid = this.getPara("uid");
		String logoPath = this.getPara("logopath");
		try {
			if (checkParam(uid, logoPath)) {
				// 查看用户是否存在
				SysUser u = SysUser.dao.findById(uid);
				if (u == null) {
					formatErrorMsgResponse("用户不存在", resultMap);
				} else {
					String url = getRequest().getRequestURL().toString();
					String path = getRequest().getServletPath().toString();
					String basePath = url.replace(path, "");
					System.out.println(basePath);
					System.out.println(JFinal.me().getServletContext()
							.getRealPath("/"));
					String realPath = JFinal.me().getServletContext()
							.getRealPath("/");
					File f = new File(realPath + "/upload/" + logoPath);
					if (!checkImageExists(logoPath)) {
						formatErrorMsgResponse("logopath 参数错误:" + logoPath,
								resultMap);
					} else {
						u.set(SysUser.LOGO, logoPath);

						u.update();
						;
						System.out
								.println(getBaseUrl() + "/upload/" + logoPath);
						resultMap.put("headicon", getBaseUrl() + "/upload/"
								+ logoPath);
						formatSuccessResponse(resultMap);
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
	 * 企业用户身份验证
	 */
	public void companyCertiUserIdentity() {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		SysUser user = this.getModel(SysUser.class, "u");
		try {
			if (checkParam(user.getStr(SysUser.ID), user.getStr("qymc"), // 企业名称
					user.getStr("qyyx"), // 企业邮箱
					user.getStr("qydz"), // 企业地址
					user.getStr("yyzzzch"), // 营业执照注册号
					user.getStr("zzjgdm"), // 组织机构代码
					user.getStr("yyzsfzh"), // 运营者身份证号码
					user.getStr("yyzzsmj"), // 营业执照扫描件
					user.getStr("yyzscsfzzp") // 运营者手持身份证照片
			)) {
				SysUser u = SysUser.dao.findById(user.getStr(SysUser.ID));
				if (u == null) {// 用户不存在
					formatErrorMsgResponse("用户不存在", resultMap);
				} else {
					if (!checkImageExists(user.getStr("yyzzsmj"))) {
						formatErrorMsgResponse("yyzzsmj 参数错误:"
								+ user.getStr("yyzzsmj"), resultMap);
						return;
					}

					if (!checkImageExists(user.getStr("yyzscsfzzp"))) {
						formatErrorMsgResponse("yyzscsfzzp 参数错误:"
								+ user.getStr("yyzscsfzzp"), resultMap);
						return;
					}
					user.set("tjsj", DateUtil.getCurrentDate());
					user.set("sfrzzt", "1");
					// 认证送 积分
					// user.set(SysUser.JIFEN, CommonStaticData.REG_SCORE);
					user.update();

					// // 增加积分变动
					// AbIntegralScore ail = new AbIntegralScore();
					// ail.set(AbIntegralScore.ID, StringUtil.getUuid32());
					// ail.set(AbIntegralScore.TYPE,
					// CommonStaticData.SCORE_TYPE1);
					// ail.set(AbIntegralScore.VALUE,
					// CommonStaticData.REG_SCORE);
					// ail.set(AbIntegralScore.DATETIME, dateStr);
					// ail.set(AbIntegralScore.USER_ID, uuid);
					// ail.set(AbIntegralScore.FID, "");
					// ail.save();

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

	/**
	 * 普通用户身份验证
	 */
	public void commonCertiUserIdentity() {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		SysUser user = new SysUser();
		user.set(SysUser.ID, this.getPara("u_id"));
		user.set(SysUser.CITY_ID, this.getPara("u_city_id"));
		user.set(SysUser.CITY_NAME, this.getPara("u_city_name"));
		user.set(SysUser.MC, this.getPara("u_mc"));
		user.set(SysUser.IDCARD, this.getPara("u_idcard"));
		user.set(SysUser.JJLLR, this.getPara("u_jjllr"));
		user.set(SysUser.SFZZPZM, this.getPara("u_sfzzpzm"));
		user.set(SysUser.SFZZPZM, this.getPara("u_sfzzpbm"));
		try {
			if (checkParam(
					user.getStr(SysUser.ID),


					user.getStr("city_id"), user.getStr("city_name"), user
							.getStr(SysUser.MC), user.getStr(SysUser.IDCARD),
					user.getStr(SysUser.JJLLR),
					user.getStr("sfzzpzm"), user.getStr("sfzzpbm")
			)) {
				SysUser u = SysUser.dao.findById(user.getStr(SysUser.ID));
				if (u == null) {// 用户不存在
					formatErrorMsgResponse("用户不存在", resultMap);
				} else {
					if (!checkImageExists(user.getStr("sfzzpbm"))) {
						formatErrorMsgResponse("sfzzpbm 参数错误:"
								+ user.getStr("sfzzpbm"), resultMap);
						return;
					}

					if (!checkImageExists(user.getStr("sfzzpzm"))) {
						formatErrorMsgResponse("sfzzpzm 参数错误:"
								+ user.getStr("sfzzpzm"), resultMap);
						return;
					}

					user.set("tjsj", DateUtil.getCurrentDate());
					user.set("sfrzzt", "1");
					// 认证送 积分
					// user.set(SysUser.JIFEN, CommonStaticData.REG_SCORE);
					user.update();

					// // 增加积分变动
					// AbIntegralScore ail = new AbIntegralScore();
					// ail.set(AbIntegralScore.ID, StringUtil.getUuid32());
					// ail.set(AbIntegralScore.TYPE,
					// CommonStaticData.SCORE_TYPE1);
					// ail.set(AbIntegralScore.VALUE,
					// CommonStaticData.REG_SCORE);
					// ail.set(AbIntegralScore.DATETIME, dateStr);
					// ail.set(AbIntegralScore.USER_ID, uuid);
					// ail.set(AbIntegralScore.FID, "");
					// ail.save();

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

	public void addUserAddress() {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		AbUserAddr addr = getModel(AbUserAddr.class, "add");

		if (checkParam(addr.getStr("user_id"), addr.getStr("addr"), addr
				.getStr("name"), addr.getStr("jd"), addr.getStr("jd"), addr
				.getStr("wd"))) {
			try {
				if (addr.getStr("id") == null || "".equals(addr.getStr("id"))) {
					addr.set("id", StringUtil.getUuid32());
					addr.save();
				} else {
					addr.update();
				}
				formatSuccessResponse(resultMap);
			} catch (Exception e) {
				e.printStackTrace();
				formatErrorMsgResponse("添加失败,是否已经重复添加", resultMap);
			}
		} else {
			formatInvalidParamResponse(resultMap);
		}
		renderJson(resultMap);
	}

	public void userAddress() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String uid = getPara("uid");

		if (checkParam(uid)) {
			try {
				List<AbUserAddr> lists = AbUserAddr.dao.find(
						"select * from ab_user_address where user_id = ?", uid);
				resultMap.put("data", lists);
				formatSuccessResponse(resultMap);
			} catch (Exception e) {
				e.printStackTrace();
				formatErrorMsgResponse("添加失败,是否已经重复添加", resultMap);
			}
		} else {
			formatInvalidParamResponse(resultMap);
		}
		renderJson(resultMap);
	}

	public void driverDetails() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String uid = getPara("uid");
		String phonenum = getPara("phonenum");

		if (checkParam(uid)) {
			try {
				AbFmcar fmcar = AbFmcar.dao
						.findFirst(
								"SELECT a.* FROM ab_fmcar a JOIN ab_fmcar_user b ON a.id = b.car_id WHERE b.user_id = ?",
								uid);
				SysUser sysuser = SysUser.dao.findFirst(
						"select * from sys_user where id=?", uid);
				// fmcar.set(AbFmcar.DRIVER, sysuser.getStr(SysUser.MC));
				if (fmcar == null) {
					fmcar = AbFmcar.dao.findFirst(
							"SELECT a.* FROM ab_fmcar a where a.mobile=?",
							sysuser.getStr("mobile"));
				}
				Record r = Db
						.findFirst(
								"SELECT COUNT(1) as cont  FROM ab_order o WHERE o.qdrid =? AND ddzt='5'",
								uid);
				long dds = r.getLong("cont");
				Record avg = Db
						.findFirst(
								"SELECT AVG(dync_mah) as msxf,AVG(dync_ser),AVG(dync_spd) FROM ab_appraise WHERE qdr_id=?",
								sysuser.getStr("id"));
				Record hps = Db
						.findFirst(
								"SELECT COUNT(1) AS hp FROM ab_appraise WHERE TYPE= '1' and  qdr_id=? UNION SELECT COUNT(1) AS zp FROM ab_appraise WHERE TYPE= '2' and qdr_id=? UNION SELECT COUNT(1) AS cp FROM ab_appraise WHERE TYPE= '3' and qdr_id=?",
								sysuser.getStr("id"), sysuser.getStr("id"),
								sysuser.getStr("id"));
				resultMap.put("idcard", sysuser.getStr("idcard"));
				resultMap.put("sfzzpzm", sysuser.getStr("sfzzpzm"));
				resultMap.put("sfzzpbm", sysuser.getStr("sfzzpbm"));
				resultMap.put("gzzp", sysuser.getStr("gzzp"));
				resultMap.put("CERTIFICATEEMAIL", sysuser
						.getStr("CERTIFICATEEMAIL"));
				resultMap.put("logo", sysuser.getStr("logo"));
				resultMap.put("data", sysuser);
				resultMap.put("car", fmcar);
				resultMap.put("jds", dds);// 订单数
				resultMap.put("msxf", hps.getDouble("msxf"));// 描述相符
				resultMap.put("fwdt", hps.getDouble("fwdt"));// 服务态度
				resultMap.put("fhsd", hps.getDouble("fhsd"));// 服务态度
				resultMap.put("hps", hps.getLong("hp") == null ? 0 : hps
						.getLong("hp"));// 评价
				resultMap.put("zps", hps.getLong("zp") == null ? 0 : hps
						.getLong("zp"));// 评价
				resultMap.put("cps", hps.getLong("cp") == null ? 0 : hps
						.getLong("cp"));// 评价
				formatSuccessResponse(resultMap);
			} catch (Exception e) {
				e.printStackTrace();
				formatErrorMsgResponse("错误", resultMap);
			}
		} else {
			formatInvalidParamResponse(resultMap);
		}
		renderJson(resultMap);
	}

	public void driverInfo() {

		Map<String, Object> resultMap = new HashMap<String, Object>();
		String uid = getPara("uid");

		if (checkParam(uid)) {
			try {
//				AbFmcar fmcar = AbFmcar.dao
//						.findFirst(
//								"SELECT a.* FROM ab_fmcar a JOIN ab_fmcar_user b ON a.id = b.car_id WHERE b.user_id = ?",
//								uid);
				SysUser sysuser = SysUser.dao.findFirst(
						"select * from sys_user where id=?", uid);
				// fmcar.set(AbFmcar.DRIVER, sysuser.getStr(SysUser.MC));
//				if (fmcar == null) {
//					fmcar = AbFmcar.dao.findFirst(
//							"SELECT a.* FROM ab_fmcar a where a.mobile=?",
//							sysuser.getStr("mobile"));
//				}
				if (sysuser != null) {

					Record r = Db
							.findFirst(
									"SELECT COUNT(1) as cont  FROM ab_order o WHERE o.qdrid =? AND ddzt='5'",
									uid);
					long dds = r.getLong("cont");
					Record avg = Db
							.findFirst(
									"SELECT AVG(dync_mah) as msxf,AVG(dync_ser),AVG(dync_spd) FROM ab_appraise WHERE qdr_id=?",
									sysuser.getStr("id"));
					Record hps = Db
							.findFirst(
									"SELECT COUNT(1) AS hp FROM ab_appraise WHERE TYPE= '1' and  qdr_id=? UNION SELECT COUNT(1) AS zp FROM ab_appraise WHERE TYPE= '2' and qdr_id=? UNION SELECT COUNT(1) AS cp FROM ab_appraise WHERE TYPE= '3' and qdr_id=?",
									sysuser.getStr("id"), sysuser.getStr("id"),
									sysuser.getStr("id"));

					Record data = new Record();
					Map<String, String> pic = new HashMap<String, String>();

					pic.put("logo", sysuser.getStr("logo"));
					pic.put("sfzzpzm", sysuser.getStr("sfzzpzm"));
					pic.put("sfzzpbm", sysuser.getStr("sfzzpbm"));
					pic.put("xsz", sysuser.getStr("xsz"));
					pic.put("jz", sysuser.getStr("jz"));
					pic.put("gzzp", sysuser.getStr("gzzp"));
					pic.put("ct", sysuser.getStr("ct"));
					pic.put("cs", sysuser.getStr("cs"));
					pic.put("dtz", sysuser.getStr("dtz"));
					data.set("pic", pic);

					Map<String, Object> pjqk = new HashMap<String, Object>();
					pjqk.put("msxf", hps.getDouble("msxf"));// 描述相符
					pjqk.put("fwdt", hps.getDouble("fwdt"));// 服务态度
					pjqk.put("fhsd", hps.getDouble("fhsd"));// 服务态度
					pjqk.put("hps", hps.getLong("hp") == null ? 0 : hps
							.getLong("hp"));// 评价
					pjqk.put("zps", hps.getLong("zp") == null ? 0 : hps
							.getLong("zp"));// 评价
					pjqk.put("cps", hps.getLong("cp") == null ? 0 : hps
							.getLong("cp"));// 评价
					pjqk.put("jds", dds);// 订单数
					data.set("pjqk", pjqk);
					Map<String, Object> certificate = new HashMap<String, Object>();

					certificate.put("certificateemail", sysuser
							.get("certificateemail"));
					certificate.put("certificatemobile", sysuser
							.get("certificatemobile"));
					certificate.put("certificatecard", sysuser
							.get("certificatecard"));
					certificate.put("certificatemoney", sysuser
							.get("certificatemoney"));
					certificate.put("status", sysuser.get("status"));
					data.set("certificate", certificate);

					Map<String, Object> car = new HashMap<String, Object>();
//					if (fmcar != null) {
//						car.put("type", fmcar.get("type"));
//						car.put("length", fmcar.get("length"));
//						car.put("location", fmcar.get("location"));
//						car.put("location_time", fmcar.get("location_time"));
//						car.put("vv", fmcar.get("vv"));
//						car.put("driver", fmcar.get("driver"));
//						car.put("car_no", fmcar.get("car_no"));
//					}
					car.put("type", sysuser.get("cx"));
					car.put("length", sysuser.get("cc"));
					car.put("location", sysuser.get("location"));
					car.put("location_time", sysuser.get("location_time"));
					car.put("vv", sysuser.get("ctj"));
					car.put("car_no", sysuser.get("cph"));
					car.put("name", sysuser.get("mc"));
					car.put("idcard", sysuser.getStr("idcard"));
					car.put("mobile", sysuser.get("mobile"));
					car.put("cpcs", sysuser.get("cpcs"));
					data.set("driver", car);

					resultMap.put("data", data);
					resultMap.put("code", "0");
					resultMap.put("msg", "调用成功");
				} else {
					resultMap.put("code", "-1");
					resultMap.put("msg", "调用失败，用户id错误");
				}

			} catch (Exception e) {
				e.printStackTrace();
				formatErrorMsgResponse("错误", resultMap);
			}
		} else {
			formatInvalidParamResponse(resultMap);
		}
		renderJson(resultMap);
	}

	/**
	 * 根据用户手机号生成二维码
	 */
	public void quickResponseCode() {
		String uid = getPara("uid");
		if (checkParam(uid)) {
			SysUser user = SysUser.dao.findById(uid);
			if (user != null) {

				String text = "http://www.ddhuodi.cn/app/user/apk?uid=" + uid;
				OutputStream sos = null;
				try {

					String realPath = JFinal.me().getServletContext()
							.getRealPath("/");
					File f = new File(realPath + "/qrcode/" + uid + ".png");
					if (!f.getParentFile().exists()) {
						f.getParentFile().mkdirs();
					}
					if (!f.exists()) {
						QRCodeUtil.encode(text, realPath + "/qrcode/yhl.png", f
								.getParent(), true, uid + ".png");
					}
					System.out.println(f.getAbsolutePath());
					getResponse().setHeader("Pragma", "no-cache");
					getResponse().setContentType("image/png");
					getResponse().setDateHeader("Expires", 0);
					sos = getResponse().getOutputStream();
					// 创建文件输入流
					FileInputStream is = new FileInputStream(f);
					// 创建缓冲区
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = is.read(buffer)) != -1) {
						sos.write(buffer, 0, len);
					}
					is.close();
					// renderFile(f);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (sos != null)
						try {
							sos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
			}
		}
	}

	/**
	 * 根据用户手机号生成二维码
	 */
	public void apk() {
		String uid = getPara("uid");
		// SysUser.
		String realPath = JFinal.me().getServletContext().getRealPath("/");
		File f = new File(realPath + "/versions/huodi.apk");
		renderFile(f);

	}

	/**
		 * 
		 */
	public void reguser() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			String dateStr = DateUtil.getCurrentDate();
			SysUser user = new SysUser();
			String mobile =	this.getPara("user_mobile");
			String loginpwd =	this.getPara("user_loginpwd");
			String area_name =	this.getPara("user_area_name");
			String city_id =		this.getPara("user_city_id");
			
			user.set("mobile", mobile);
			user.set("loginpwd", loginpwd);
			user.set("area_name", area_name);
			user.set("city_id", city_id);
			
			String uuid = StringUtil.getUuid32();
			Record record = Db.findFirst(
					"select count(1) con from sys_user where loginid=?", user
							.get("mobile"));
			if (record.getLong("con") > 0) {
				resultMap.put("result", RESULT_FAIL);
				resultMap.put("msg", "电话号码已注册，请修改");
			} else {
				user.set("loginpwd", MD5.GetMD5Code(user.getStr("loginpwd")));
				user.set("id", uuid);
				user.set("loginid", user.get("mobile"));
				user.set("certificatemobile", "1");
				user.set("status", "0");
				user.set("role_id", 107);
				user.set("role_name", "会员");
				user.set("create_date", dateStr);
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

				// 判断是否好友邀请
				String invitecode = this.getPara("invitecode");
				if (!StringUtil.isNull(invitecode)) {
					SysUser fuser = null;
					List<SysUser> sus = SysUser.dao
							.find("select * from sys_user where invite = '"
									+ invitecode + "'");
					if (sus != null && sus.size() > 0) {
						fuser = sus.get(0);
					}
					// 更新好友用户积分，增加邀请好友25积分
					if (fuser != null) {
						String fuid = fuser.getStr(SysUser.ID);
						fuser.set(SysUser.JIFEN, fuser.getInt(SysUser.JIFEN)
								+ CommonStaticData.ADD_SCORE);
						fuser.update();

						// 增加积分变动
						AbIntegralScore as = new AbIntegralScore();
						as.set(AbIntegralScore.ID, StringUtil.getUuid32());
						as.set(AbIntegralScore.TYPE,
								CommonStaticData.SCORE_TYPE5);
						as.set(AbIntegralScore.VALUE,
								CommonStaticData.ADD_SCORE);
						as.set(AbIntegralScore.DATETIME, dateStr);
						as.set(AbIntegralScore.USER_ID, fuid);
						as.set(AbIntegralScore.FID, "");
						as.save();

						// 增加邀请记录
						SysUserInvite sui = new SysUserInvite();
						sui.set(SysUserInvite.ID, StringUtil.getUuid32());
						sui.set(SysUserInvite.LOGINID, fuid);
						sui.set(SysUserInvite.INVITE_LOGINID, uuid);
						sui.save();
					}
				}
				// 获取服务分类
				String[] subjectids = this.getParaValues("subjectid");
				if (subjectids != null && subjectids.length > 0) {
					for (int i = 0; i < subjectids.length; i++) {
						SysUserSubject.dao.set("id", StringUtil.getUuid32())
								.set("user_id", uuid).set("subject_id",
										subjectids[i]).save();
					}
				}

				Record r = Db.findFirst(
						"select id,mc,status,mobile from sys_user where id=?",
						uuid);
				ObjectNode datanode = JsonNodeFactory.instance.objectNode();
		        datanode.put("username",user.getStr("loginid"));
		        datanode.put("password", user.getStr("loginpwd"));
		        EasemobIMUsers.createNewIMUserSingle(datanode);
				resultMap.put("result", RESULT_SUCCESS);
				resultMap.put("msg", "注册成功");
				resultMap.put("data", r);
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("result", RESULT_FAIL);
			resultMap.put("msg", "错误，请确认参数是否正确");
		}
		renderJson(resultMap);
	}

	/**
		 * 
		 */
	public void regdriver() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			SysUser user = new SysUser();
			String mobile =	this.getPara("user_mobile");
			String loginpwd =	this.getPara("user_loginpwd");
			String area_name =	this.getPara("user_area_name");
			String city_id =		this.getPara("user_city_id");
			
			user.set("mobile", mobile);
			user.set("loginpwd", loginpwd);
			user.set("area_name", area_name);
			user.set("city_id", city_id);
			
			String uuid = StringUtil.getUuid32();
			Record record = Db.findFirst(
					"select count(1) con from sys_user where loginid=?", user
							.get("mobile"));
			if (record.getLong("con") > 0) {
				resultMap.put("result", RESULT_FAIL);
				resultMap.put("msg", "电话号码已注册，请修改");
			} else {

				user.set("id", uuid);
				user.set("loginid", user.get("mobile"));
				if(StringUtils.isEmpty(loginpwd)){
					loginpwd = "123456";
				}
				user.set("loginpwd", MD5.GetMD5Code(loginpwd));
				user.set("certificatemobile", "1");
				user.set("status", "0");
				user.set("role_id", 106);
				user.set("role_name", "业务员");
				user.set("create_date", DateUtil.getCurrentDate());
				String userType = StringUtil.toStr(this.getPara("userType"));
				String userSubType = StringUtil.toStr(this
						.getPara("userSubType"));
				SysUserType ut = new SysUserType();
				ut.set("id", StringUtil.getUuid32());
				ut.set("usertype", userType);
				ut.set("usersubtype", userSubType);
				ut.set("userid", uuid);
				ut.save();
				user.save();
				Record r = Db.findFirst(
						"select id,mc,status,mobile from sys_user where id=?",
						uuid);
				r.set("usertype", userType);
				r.set("usersubtype", userSubType);
				ObjectNode datanode = JsonNodeFactory.instance.objectNode();
		        datanode.put("username",user.getStr("loginid"));
		        datanode.put("password", user.getStr("loginpwd"));
		        EasemobIMUsers.createNewIMUserSingle(datanode);
				resultMap.put("result", RESULT_SUCCESS);
				resultMap.put("msg", "注册成功");
				resultMap.put("data", r);
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultMap.put("result", RESULT_FAIL);
			resultMap.put("msg", "错误，请确认参数是否正确");
		}
		renderJson(resultMap);
	}

	/**
	 * 保存商家信息
	 */
	public void saveregmer() {
		boolean flag = false;
		SysUser user = this.getModel(SysUser.class, "user");
		AbMerchant mer = this.getModel(AbMerchant.class, "mer");
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
		// AbCityarea aca = AbCityarea.dao.findById(user.getStr("city_id"));
		// if(null!=aca && null!= aca.getStr("user_id")){
		// user.set("p_id", aca.getStr("user_id"));
		// }else{
		// user.set("p_id", "admin");
		// }
		mer.set("id", uuid);
		mer.set("mapbusiness", user.get("mapbusiness"));
		mer.set("mobile", user.get("mobile"));
		mer.set("user_id", uuid);
		mer.set("create_time", str_date);
		// String userType = StringUtil.toStr(this.getPara("userType"));
		// String userSubType = StringUtil.toStr(this.getPara("userSubType"));
		// SysUserType ut = new SysUserType();
		// ut.set("id", StringUtil.getUuid32());
		// ut.set("usertype", userType);
		// ut.set("usersubtype", userSubType);
		// ut.set("userid", uuid);
		// ut.save();

		// 获取分类是货物还是服务

		// AbSubject sub = AbSubject.dao.findById(mer.get("subject_id"));
		// mer.set("is_type", sub.get("is_type"));
		user.save();
		mer.save();

		this.setSessionAttr("abuser", user);

		flag = true;
		renderJson(flag);
	}
	public void sjrz(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String uid = getPara("uid");

		if (checkParam(uid)) {
		SysUser user = this.getModel(SysUser.class,"tb");
		
		String cpcss[] = getParaValues("tb_cpcs");
		StringBuffer cpcs = new StringBuffer();
		
		if(cpcss.length > 0)
		{
			for(String str : cpcss)
			{
				cpcs.append(str+",");
			}
		}
		
		user.set(SysUser.CPCS, cpcs.toString());
		user.set("tjsj", DateUtil.getCurrentDate());
		user.set("sfrzzt", "1");
		user.set(SysUser.ID, uid);
		user.update();
		}else{
			formatInvalidParamResponse(resultMap);
		}
		renderJson(resultMap);
	}
	public void zhye(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String uid = getPara("uid");
		
		if (checkParam(uid)) {
			
			SysUser user = SysUser.dao.findById(uid);
			resultMap.put("zhye", user.get("zhye"));
			resultMap.put("result", RESULT_SUCCESS);
			resultMap.put("msg", "查询成功");
			
		}else{
			formatInvalidParamResponse(resultMap);
		}
		renderJson(resultMap);
	}
}
