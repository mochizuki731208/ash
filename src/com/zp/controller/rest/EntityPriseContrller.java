package com.zp.controller.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zp.entity.AbMerchant;
import com.zp.entity.AbMerchantImg;
import com.zp.entity.SysUser;
import com.zp.tools.StringUtil;

public class EntityPriseContrller extends AbstractRestController  {
	/**
	* 获取企业信息
	*/
	public void saveEntityPriseInfo() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		// 根据当前登录人信息获取商家信息
		String id = getPara("id");// 主键 
		String userId = getPara("uid");// 用户id
		
		if (StringUtil.isNull(userId) && StringUtil.isNull(id)) {
			// 检查参数是否合法
			resultMap.put("result", RESULT_FAIL);
			resultMap.put("msg", RESULT_INVALID_PARAM);
		} else {
			
			AbMerchant vo = new AbMerchant();
			vo = AbMerchant.dao.findById(id);
			vo.set(AbMerchant.ID, id);
			String  mc = getPara("mc");// 商家名称
			vo.set(AbMerchant.MC, mc);
			String yzbm = getPara("yzbm");//  邮政编码
			vo.set(AbMerchant.YZBM, yzbm);
			String xxdz = getPara("xxdz");/** 详细地址 **/
			vo.set(AbMerchant.XXDZ, xxdz);
			String mobile = getPara("mobile");/** 手机号 **/
			vo.set(AbMerchant.MOBILE, mobile);
			String qq = getPara("qq");/** QQ号码 **/
			vo.set(AbMerchant.QQ, qq);
			String weibo = getPara("weibo");/** 微博 **/
			vo.set(AbMerchant.WEIBO, weibo);
			String weixin = getPara("weixin");/** 微信 **/
			vo.set(AbMerchant.WEIXIN, weixin);
			String brief = getPara("brief");//** 商家简介 **/
			vo.set(AbMerchant.BRIEF, brief);
			String sfzs = getPara("sfzs");/** 是否自送[0-否、1-是] **/
			vo.set(AbMerchant.SFZS, sfzs);
			String zdpsje = getPara("zdpsje");/** 最低配送金额 **/
			vo.set(AbMerchant.ZDPSJE, zdpsje);
			String chk_fgzrxd = getPara("chk_fgzrxd");/** 非工作日是否允许下单 **/
			vo.set(AbMerchant.CHK_FGZRXD, chk_fgzrxd);
//			String chk_tgfp = getPara("chk_tgfp");/** 是否选择提供发票 **/
//			vo.set(AbMerchant.CHK_TGFP, chk_tgfp);
			String tgfp = getPara("tgfp");/** 提供发票[0-否、1-是] **/
			vo.set(AbMerchant.TGFP, tgfp);
//			String chk_yhhd = getPara("chk_yhhd");/** 是否选择优惠活动 **/
//			vo.set(AbMerchant.CHK_YHHD, chk_yhhd);
			String mds = getPara("mds");/** 买多少 **/
			vo.set(AbMerchant.MDS, mds);
			String jds = getPara("jds");/** 减多少 **/
			vo.set(AbMerchant.JDS, jds);
//			String chk_sdsj = getPara("chk_sdsj");/** 是否选择送达时间 **/
//			vo.set(AbMerchant.c, jds);

			String sdsj = getPara("sdsj");/** 送达时间（分钟） **/
			vo.set(AbMerchant.SDSJ, sdsj);
			String cspf = getPara("cspf");	/** 超时赔付 **/
			vo.set(AbMerchant.CSPF, cspf);
//			String seq_num = getPara("seq_num");/** 推荐排序 **/
			String business_status = getPara("business_status");/** 营业状态[0-已打烊、1-营业中、2-休息中] **/
			vo.set(AbMerchant.BUSINESS_STATUS, business_status);
			resultMap.put("result", RESULT_SUCCESS);
			resultMap.put("msg", "信息保存成功!");
			vo.update();
		}
		renderJson(resultMap);
	}
	
	/**
	* 获取企业信息
	*/
	public void getEntityPriseInfo() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		// 根据当前登录人信息获取商家信息
		String userId = getPara("uid");// 用户id
		if (StringUtil.isNull(userId)) {
			// 检查参数是否合法
			resultMap.put("result", RESULT_FAIL);
			resultMap.put("msg", RESULT_INVALID_PARAM);
		} else {
			AbMerchant vo = new AbMerchant();
			// 根据人员信息查询商家信息
			vo = AbMerchant.dao.findById(userId);
			List<AbMerchantImg> list_img = AbMerchantImg.dao
					.find("select * from ab_merchant_img where mid=? order by seq_num", vo.getStr("id"));
			resultMap.put("merInfo", vo);
			resultMap.put("imgList", list_img);
			resultMap.put("result", RESULT_SUCCESS);
			resultMap.put("msg", "查询成功");
		}
		renderJson(resultMap);
	}
	
}
