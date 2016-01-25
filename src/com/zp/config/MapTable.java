package com.zp.config;

import com.jfinal.plugin.IPlugin; 
import com.jfinal.plugin.activerecord.ActiveRecordPlugin; 
import com.zp.entity.SysUserType;

public class MapTable{

    public static IPlugin getArp(ActiveRecordPlugin arp) {


		arp.addMapping("ab_appraise", com.zp.entity.AbAppraise.class);
		arp.addMapping("ab_appraise_img", com.zp.entity.AbAppraiseImg.class);
		arp.addMapping("ab_card", com.zp.entity.AbCard.class);
		arp.addMapping("ab_card_type", com.zp.entity.AbCardType.class);
		arp.addMapping("ab_card_user", com.zp.entity.AbCardUser.class);
		arp.addMapping("ab_card_expense", com.zp.entity.AbCardExpense.class);
		arp.addMapping("ab_cityarea", com.zp.entity.AbCityarea.class);
//		arp.addMapping("ab_common_cityarea", com.zp.entity.AbCommonCityarea.class);
		arp.addMapping("ab_cityarea_subject", com.zp.entity.AbCityareaSubject.class);
		arp.addMapping("ab_integral_exchange", com.zp.entity.AbIntegralExchange.class);
		arp.addMapping("ab_integral_goods", com.zp.entity.AbIntegralGoods.class);
		arp.addMapping("ab_integral_score", com.zp.entity.AbIntegralScore.class);
		arp.addMapping("ab_merchant", com.zp.entity.AbMerchant.class);
		arp.addMapping("ab_merchant_comment", com.zp.entity.AbMerchantComment.class);
		arp.addMapping("ab_merchant_img", com.zp.entity.AbMerchantImg.class);
		arp.addMapping("ab_merchant_leave", com.zp.entity.AbMerchantLeave.class);
		arp.addMapping("ab_merchant_product", com.zp.entity.AbMerchantProduct.class);
		arp.addMapping("ab_merchant_reply", com.zp.entity.AbMerchantReply.class);
		arp.addMapping("ab_merchant_yysj", com.zp.entity.AbMerchantYysj.class);
		arp.addMapping("ab_merchant_print", com.zp.entity.AbMerchantPrint.class);
		arp.addMapping("ab_order", com.zp.entity.AbOrder.class);
		arp.addMapping("ab_order_chargeback", com.zp.entity.AbOrderChargeback.class);
		arp.addMapping("ab_order_chargeback_item", com.zp.entity.AbOrderChargebackItem.class);
		arp.addMapping("ab_order_item", com.zp.entity.AbOrderItem.class);
		arp.addMapping("ab_order_position", com.zp.entity.AbOrderPosition.class);
		arp.addMapping("ab_order_itemchargeoff", com.zp.entity.AbOrderItemchargeoff.class);
		arp.addMapping("ab_product_comment", com.zp.entity.AbProductComment.class);
		arp.addMapping("ab_product_img", com.zp.entity.AbProductImg.class);
		arp.addMapping("ab_shywyjl", com.zp.entity.AbShywyjl.class);
		arp.addMapping("ab_subject", com.zp.entity.AbSubject.class);
		arp.addMapping("ab_subject_merchant", com.zp.entity.AbSubjectMerchant.class);
		arp.addMapping("ab_tc_config", com.zp.entity.AbTcConfig.class);
		arp.addMapping("ab_tc_express_order", com.zp.entity.AbTcExpressOrder.class);
		arp.addMapping("ab_user_cityarea", com.zp.entity.AbUserCityarea.class);
		arp.addMapping("ab_user_address", com.zp.entity.AbUserAddr.class);
		arp.addMapping("ab_subject_product", com.zp.entity.AbSubjectProduct.class);
		arp.addMapping("ab_subject_product_order", com.zp.entity.AbSubjectProductOrder.class);
		arp.addMapping("log_user_deposit", com.zp.entity.LogUserDeposit.class);
		arp.addMapping("log_user_sms", com.zp.entity.LogUserSms.class);
		arp.addMapping("log_user_vipcard", com.zp.entity.LogUserVipcard.class);
		arp.addMapping("sys_area_subject_ptf", com.zp.entity.SysAreaSubjectPtf.class);
		arp.addMapping("sys_config", com.zp.entity.SysConfig.class);
		arp.addMapping("sys_menu", com.zp.entity.SysMenu.class);
		arp.addMapping("sys_role", com.zp.entity.SysRole.class);
		arp.addMapping("sys_role_menu", com.zp.entity.SysRoleMenu.class);
		arp.addMapping("sys_user", com.zp.entity.SysUser.class);
//		arp.addMapping("ab_access_system", com.zp.entity.AbAccessSystem.class);
		arp.addMapping("sys_user_store", com.zp.entity.SysUserStore.class);
		arp.addMapping("sys_user_subject", com.zp.entity.SysUserSubject.class);
		arp.addMapping("sys_cz_tx", com.zp.entity.SysCzTx.class);
		arp.addMapping("sys_user_invite", com.zp.entity.SysUserInvite.class);
		arp.addMapping("sys_yhka_item", com.zp.entity.SysYhkItem.class);
		arp.addMapping("ab_order_sms", com.zp.entity.AbOrderSms.class);
		
		//熟车相关
		arp.addMapping("ab_fmcar", com.zp.entity.AbFmcar.class);
		arp.addMapping("ab_fmcar_city", com.zp.entity.AbFmcarCity.class);
		arp.addMapping("ab_fmcar_group", com.zp.entity.AbFmcarGroup.class);
		arp.addMapping("ab_fmcar_order", com.zp.entity.AbFmcarOrder.class);
		arp.addMapping("ab_fmcar_order_car", com.zp.entity.AbFmcarOrderCar.class);
		arp.addMapping("ab_fmcar_user", com.zp.entity.AbFmcarUser.class);
		arp.addMapping("ab_fmcar_order_baojia", com.zp.entity.AbFmcarOrderBaoJia.class);
		
		
		
		
		arp.addMapping("cms_content", com.zp.entity.CmsContent.class);
		arp.addMapping("cms_nrfl", com.zp.entity.CmsNrfl.class);
		arp.addMapping("sys_user_zfpwd", com.zp.entity.SysUserZFPwd.class);
		arp.addMapping("sys_mobile_blank", com.zp.entity.SysMobileBlank.class);
		arp.addMapping("ab_sj_position", com.zp.entity.AbSjPosition.class);
		arp.addMapping("ab_address", com.zp.entity.AbAddress.class);
		arp.addMapping("sys_user_group", com.zp.entity.SysUserGroup.class);
		arp.addMapping("sys_user_type", SysUserType.class);
		
//		arp.addMapping("sys_pnumber", com.zp.entity.SysPNumber.class);
		arp.addMapping("sys_order_alert", com.zp.entity.SysOrderAlert.class);
		arp.addMapping("sys_invoice", com.zp.entity.SysInvoice.class);
		arp.addMapping("sys_invoice_address", com.zp.entity.SysInvoiceAddress.class);
		arp.addMapping("ab_order_state_img", com.zp.entity.AbOrderStateImg.class);
		//配置订单报价 保存表
        //配置订单报价 保存表
		arp.addMapping("ab_tc_express_order_baojia", com.zp.entity.AbOrderQuote.class);
		
		// 配置询价单
		arp.addMapping("ab_ask_price", com.zp.entity.AbAskPrice.class);
		// 常用联系人
		// arp.addMapping("ab_contact_person", com.zp.entity.AbContactPerson.class);
		// 保险单
		arp.addMapping("ab_policy", com.zp.entity.AbPolicy.class);
		// 保险费率
//		arp.addMapping("ab_policy_rate", com.zp.entity.AbPolicyRate.class);
		
		//物流企业
		arp.addMapping("ab_merchant_logistics", com.zp.entity.AbMerchantLogistics.class);
		arp.addMapping("ab_merchant_logimg", com.zp.entity.AbMerchantLogimg.class);
		arp.addMapping("ab_merchant_logtousu", com.zp.entity.AbMerchantLogtousu.class);
		arp.addMapping("ab_merchant_logline", com.zp.entity.AbMerchantLogline.class);
		//人脉
//		arp.addMapping("ab_contacts", com.zp.entity.AbContacts.class);
		
		//调度表
		arp.addMapping("ab_tc_express_order_diaodu", com.zp.entity.AbTcExpressOrderDiaoDu.class);
		//系统消息
		arp.addMapping("ab_notice", com.zp.entity.AbNotice.class);
		arp.addMapping("ab_invoice", com.zp.entity.AbInvoice.class);
		//听单
		arp.addMapping("ab_listen_city", com.zp.entity.AbListenOrder.class);
		return arp;
	 }
 }
