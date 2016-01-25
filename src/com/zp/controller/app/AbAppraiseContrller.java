package com.zp.controller.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.zp.entity.AbAppraise;
import com.zp.entity.AbAppraiseImg;
import com.zp.entity.AbOrder;
import com.zp.entity.AbTcExpressOrder;
import com.zp.entity.SysUser;
import com.zp.tools.CommonProcess;
import com.zp.tools.CommonSQL;
import com.zp.tools.CommonStaticData;
import com.zp.tools.DateUtil;
import com.zp.tools.ImageScale;
import com.zp.tools.StringUtil;

public class AbAppraiseContrller extends AbsAppController {


	
	/**
	 * app 增加评价 参数 orderid 
	 * 订单ID userid 
	 * 用户ID appmid 
	 * 商户ID mah 
	 * 动态评分-描述相符 ser
	 * 动态评分-服务态度 spd 
	 * 动态评分-发货速度 pvte 
	 * 是否匿名 1公开 2匿名 a
	 * ppraisetype 评价类型 1 好评 2中评3差评
	 * appraisecontent 描述 appimage 图片路径,多个图片用","分割
	 * 
	 * 返回值 true or false
	 * 
	 */
	public void addappraise() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String orderid = this.getPara("orderid");
		String userid = this.getPara("userid");
		String mid = this.getPara("appmid");
		String type = this.getPara("appraisetype");
		String pri = this.getPara("pvte");
		String content = this.getPara("appraisecontent");
		String appimage = this.getPara("appimage");
		String mahstr = this.getPara("mah");
		String serstr = this.getPara("ser");
		String spdstr = this.getPara("spd");

		if (StringUtil.isNull(orderid) || StringUtil.isNull(userid)
				|| StringUtil.isNull(type)
				|| StringUtil.isNull(pri) || StringUtil.isNull(content)
				|| StringUtil.isNull(mahstr) || StringUtil.isNull(serstr)
				|| StringUtil.isNull(spdstr)) {
			formatInvalidParamResponse(resultMap);
		} else {
			try {
				BigDecimal mah = new BigDecimal(Integer.valueOf(mahstr));
				BigDecimal ser = new BigDecimal(Integer.valueOf(serstr));
				BigDecimal spd = new BigDecimal(Integer.valueOf(spdstr));
				AbOrder order = AbOrder.dao.findById(orderid);
				String qdrid = null;
				if(order ==null){
					AbTcExpressOrder tmp = AbTcExpressOrder.dao.findById(orderid);
					if(tmp!=null){
						order =	AbOrder.dao.findFirst("select * from ab_order where sn = ?",tmp.getStr("sn"));
						qdrid = order.getStr(AbOrder.QDRID);
					}else{
						qdrid = tmp.getStr(AbOrder.QDRID);
					}
				}else{
					qdrid = order.getStr(AbOrder.QDRID);
				}
				BigDecimal total = mah.add(ser).add(spd);
				AbAppraise app =  AbAppraise.dao.findFirst("select * from ab_appraise where user_id=? and order_id=?",userid,orderid);
				boolean flag = false;
				if(app==null){
					app = new AbAppraise();
					String appId = StringUtil.getUuid32();
					app.set(AbAppraise.ID, appId);
					flag = true;
				}
				
				
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
				if(flag){
					app.save();
				}else{
					app.update();
				}

				if (!StringUtil.isNull(appimage)) {
					String[] img_urls = appimage.split(",");
					if (img_urls != null) {
						for (String url : img_urls) {
							AbAppraiseImg aai = new AbAppraiseImg();
							FileInputStream file = null;

							file = new FileInputStream(PathKit.getWebRootPath()
									+ "\\upload\\" + url);

							aai.set(AbAppraiseImg.ID, StringUtil.getUuid32());
							aai.set(AbAppraiseImg.APP_ID, app.get("id"));
							aai.set(AbAppraiseImg.IMAGE, file);
							aai.set(AbAppraiseImg.IMG_URL, url);
							aai.save();
						}
					}
				}
				// 增加而修改商户,跑腿人员信用等级和客户评价
				CommonProcess.updateMechantCreditAndRate(mid, type, true);
				CommonProcess.updateQDRCreditAndRate(qdrid, mid, type, true);
				formatSuccessResponse(resultMap);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		renderJson(resultMap);
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
	public void appraise() {
		String uid = this.getPara("uid");
		String orderid = this.getPara("orderid");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Record> apps = null;
		if (StringUtil.isNull(uid)) {
			formatInvalidParamResponse(resultMap);
		} else {
			// 判断 uid是商户还是跑腿人员
			SysUser su = SysUser.dao.findById(uid);
			if (su != null) {
				if (CommonStaticData.USER_TYPE_SERVICE.equals(su
						.getStr(SysUser.ROLE_ID))) {
					apps = Db.find(CommonSQL.getAppraiseByQdrid(uid));
				}else{
					StringBuffer sb = new StringBuffer("select * from ( ");
					sb.append("select aa.*, su.mc from ");
					sb.append("(select * from ab_appraise where  user_id = '").append(uid).append("' and order_id = '").append(orderid+"') aa ");
					sb.append("left join sys_user su on aa.user_id = su.id ");
					sb.append(") t where 1=1 ");
					apps = Db.find(sb.toString());
				}
				// 显示图片
					for (int i = 0; i < apps.size(); i++) {
						List<Record> imgs = Db.find(CommonSQL
								.getAppraiseImgByAppId(apps.get(i).getStr(
										AbAppraise.ID)));
						apps.get(i).set("appsimgs", imgs);
						if (imgs != null && imgs.size() > 0) {
							CommonProcess.createImageFile(imgs);
						}
					}
					resultMap.put("result", RESULT_SUCCESS);
					resultMap.put("msg", "查询成功");
					resultMap.put("data", apps);
			}
		}
		renderJson(resultMap);
	}
	public void list() {
		String uid = this.getPara("uid");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Record> apps = null;
		if (StringUtil.isNull(uid)) {
			formatInvalidParamResponse(resultMap);
		} else {
			// 判断 uid是商户还是跑腿人员
			SysUser su = SysUser.dao.findById(uid);
			if (su != null) {
				if (CommonStaticData.USER_TYPE_SERVICE.equals(su
						.getStr(SysUser.ROLE_ID))) {
					apps = Db.find(CommonSQL.getAppraiseByQdrid(uid));
				}else{
					StringBuffer sb = new StringBuffer("select * from ( ");
					sb.append("select aa.*, su.mc from ");
					sb.append("(select a.*,o.sn as sn from ab_appraise a join ab_order o on a.order_id = o.id where  a.user_id = '").append(uid).append("') aa ");
					sb.append("left join sys_user su on aa.user_id = su.id ");
					sb.append(") t where 1=1 ");
					apps = Db.find(sb.toString());
				}
				// 显示图片
				for (int i = 0; i < apps.size(); i++) {
					List<Record> imgs = Db.find(CommonSQL
							.getAppraiseImgByAppId(apps.get(i).getStr(
									AbAppraise.ID)));
					apps.get(i).set("appsimgs", imgs);
					if (imgs != null && imgs.size() > 0) {
						CommonProcess.createImageFile(imgs);
					}
				}
				resultMap.put("result", RESULT_SUCCESS);
				resultMap.put("msg", "查询成功");
				resultMap.put("data", apps);
			}
		}
		renderJson(resultMap);
	}

	/**
	 * app 商户或跑腿人员评价查询 参数 type 1.商户 2.跑腿人员
	 * 
	 * 返回值 list
	 * 
	 */

	public void findappraiseorder() {
		String type = this.getPara("type");
		List<Record> apps = null;
		if (StringUtil.isNull(type)) {
		} else {
			// 判断 uid是商户还是跑腿人员
			// 查询商户评价
			if ("1".equals(type)) {
				apps = Db
						.find("select m.mc, m.rate from ab_merchant m order by m.rate desc");
			}
			// 查询业务员评价
			else if ("2".equals(type)) {
				apps = Db
						.find("select mc , CONVERT(rate,CHAR(32)) rate from sys_user where role_id = '106' order by rate ");
			}
			this.renderJson(apps);
		}
	}

	/**
	 * app 商户或跑腿人员评价次数查询 参数 uid 用户ID type 1 好评 2中评 3差评
	 * 
	 * 返回值 int
	 * 
	 */
	public void findappraisecount() {
		String uid = this.getPara("uid");
		String type = this.getPara("type");
		List<AbAppraise> apps = new ArrayList<AbAppraise>();
		if (StringUtil.isNull(uid) || StringUtil.isNull(type)) {
		} else {
			SysUser su = SysUser.dao.findById(uid);
			if (su != null) {
				if (CommonStaticData.USER_TYPE_MERCHANT.equals(su
						.getStr(SysUser.ROLE_ID))) {
					apps = AbAppraise.dao.find(CommonSQL.getAppraiseTypeByMid(uid, type));
				} else if (CommonStaticData.USER_TYPE_SERVICE.equals(su
						.getStr(SysUser.ROLE_ID))) {
					apps = AbAppraise.dao.find(CommonSQL.getAppraiseTypeByQdrid(uid, type));
				}
			}
			this.renderJson(apps.size());
		}
		

	}
}
