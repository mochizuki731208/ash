package com.zp.config;

import com.jfinal.config.Routes;

public class Route extends Routes {

	public void config() {
		add("/", com.zp.controller.ab.AbOrderController.class, "/ab/index");
		add("/common", com.zp.controller.comm.CommonController.class, "/common"); // 公共应用

		add("/25console", com.zp.controller.admin.AdminController.class, "/25console");
		add("/admin/card", com.zp.controller.admin.AbCardController.class, "/admin/card"); // 会员卡管理
		add("/admin/config", com.zp.controller.admin.SysConfigController.class, "/admin/config"); // 系统服务配置
		add("/admin/role", com.zp.controller.admin.SysRoleController.class, "/admin/role"); // 系统服务配置
		add("/admin/city", com.zp.controller.admin.AbCityController.class, "/admin/city");
		add("/admin/subject", com.zp.controller.admin.AbSubjectController.class, "/admin/subject");
		add("/admin/deposit", com.zp.controller.admin.AbDepositController.class, "/admin/deposit");
		add("/admin/user", com.zp.controller.admin.SysUserController.class, "/admin/user");
		add("/admin/merchant", com.zp.controller.admin.AbMerchantController.class, "/admin/merchant"); // 商家信息管理
		add("/admin/hy", com.zp.controller.admin.AbHyController.class, "/admin/hy"); // 会员信息管理
		add("/admin/ywy", com.zp.controller.admin.AbYwyController.class, "/admin/ywy"); // 业务员信息管理
		add("/admin/dd", com.zp.controller.admin.AbDdController.class, "/admin/dd"); // 订单信息管理
		add("/admin/tc", com.zp.controller.admin.TcController.class, "/admin/tc"); // 同城快递管理
		add("/admin/integral", com.zp.controller.admin.AbIntegralController.class, "/admin/integral"); // 积分兑换商品管理
		add("/admin/leave", com.zp.controller.admin.AbLeaveContrller.class, "/admin/leave"); // 积分兑换商品管理
		add("/admin/appraise", com.zp.controller.admin.AbAppraiseContrller.class, "/admin/appraise"); // 积分兑换商品管理
		add("/admin/chargeback", com.zp.controller.admin.AbChargebackContrller.class, "/admin/chargeback"); // 退单管理
		add("/admin/order", com.zp.controller.admin.AbOrderController.class, "/admin/order"); // 积分兑换商品管理
		//add("/admin/ordernew", com.zp.controller.admin.AbOrderNewController.class, "/admin/ordernew"); // 积分兑换商品管理
		add("/admin/rz", com.zp.controller.admin.AbRzController.class, "/admin/rz"); // 认证
		add("/admin/cms", com.zp.controller.admin.CmsController.class, "/admin/cms"); // 认证
		add("/admin/fmcar", com.zp.controller.admin.AbFmcarController.class, "/admin/fmcar"); // 评价
		add("/admin/pnumber", com.zp.controller.admin.AbPublicNumberController.class, "/admin/pnumber"); // 公众号
		add("/admin/orderalert", com.zp.controller.admin.AbOrderAlertController.class, "/admin/orderalert"); // 铃声
		
		add("/ab", com.zp.controller.ab.AbIndexContrller.class, "/ab");
		add("/ab/card", com.zp.controller.ab.AbCardController.class, "/ab/card"); // 会员卡管理
		add("/ab/reg", com.zp.controller.ab.AbRegisterContrller.class, "/ab/reg");
		//add("/ab/message", com.zp.controller.ab.AbMessageContrller.class, "/ab/message");
		add("/ab/mer", com.zp.controller.ab.AbMerController.class, "/ab/mer");
		add("/ab/ywy", com.zp.controller.ab.AbYwyController.class, "/ab/ywy");
		add("/ab/user", com.zp.controller.ab.AbUserController.class, "/ab/user");
		add("/ab/order", com.zp.controller.ab.AbOrderController.class, "/ab/order");
		add("/ab/tc", com.zp.controller.ab.AbTcContrller.class, "/ab/tc");
		add("/ab/ks", com.zp.controller.ab.AbKsContrller.class, "/ab/ks");
		add("/ab/exc", com.zp.controller.ab.AbExchangeController.class, "/ab/exc");
		add("/ab/leave", com.zp.controller.ab.AbLeaveContrller.class, "/ab/leave"); // 留言
		add("/ab/appraise", com.zp.controller.ab.AbAppraiseContrller.class, "/ab/appraise"); // 评价
		add("/ab/chargeback", com.zp.controller.ab.AbChargebackContrller.class, "/ab/chargeback"); // 评价
		add("/ab/fmcar", com.zp.controller.ab.AbFmcarController.class, "/ab/fmcar"); // 评价
		add("/ab/fmcargroup", com.zp.controller.ab.AbFmcarGroupController.class, "/ab/fmcargroup"); // 评价
		//add("/ab/fmcarorders", com.zp.controller.ab.AbFmcarOrdersController.class, "/ab/fmcarorders"); // 我的熟车-订单
		add("/ab/invoice", com.zp.controller.ab.AbInvoiceController.class, "/ab/invoice"); // 发票
		add("/ab/subject", com.zp.controller.ab.AbSubjectController.class, "/ab/subject");
		// -------------- 物流企业 ---------------------//
		add("/admin/logi", com.zp.controller.trans.LogisticsAdminController.class, "/admin/logi");//物流公司管理
		add("/ab/logi", com.zp.controller.trans.LogisticsIndexController.class, "/ab/logi");//物流公司导航
		add("/trans", com.zp.controller.trans.LogisticsController.class, "/trans");//物流公司主页
		// -------------- 物流企业 ---------------------//

		// -------------- 客户端接口 ---------------------//
		add("/rest/index", com.zp.controller.rest.RestIndexContrller.class);
		add("/rest/common", com.zp.controller.rest.RestCommonController.class);
		add("/rest/order", com.zp.controller.rest.RestOrderController.class);
		add("/rest/fmcar", com.zp.controller.rest.RestFmCarController.class);
		add("/rest/acc", com.zp.controller.rest.ChargebackContrller.class);
		add("/rest/shop", com.zp.controller.rest.RestShopController.class);
		add("/rest/entity", com.zp.controller.rest.EntityPriseContrller.class);
		add("/rest/logistics", com.zp.controller.rest.RestLogisticsController.class);
//		add("/rest/bd", com.zp.controller.rest.RestBdController.class);
		add("/rest/rm", com.zp.controller.rest.RestContactsController.class);
		add("/rest/notice", com.zp.controller.rest.RestNoticeController.class);
		add("/rest/invoice", com.zp.controller.rest.RestInvoiceController.class);
		add("/rest/listen", com.zp.controller.rest.RestListenController.class);
		// -------------- 客户端接口 ---------------------//
		
		add("/ab/news", com.zp.controller.ab.AbNewsContrller.class, "/ab/news");
		add("/ab/service", com.zp.controller.rest.RestController.class, "/ab/service");
	
		add("/app/subject", com.zp.controller.app.AbSubjectController.class, "/app/subject"); // app获取分类信息
		add("/app/merchant", com.zp.controller.app.AbMerchantController.class, "/app/merchant"); // app获取商家信息
		add("/app/appraise", com.zp.controller.app.AbAppraiseContrller.class, "/app/appraise"); // 评价接口
		add("/app/leave", com.zp.controller.app.AbLeaveContrller.class, "/app/leave"); // 催单接口
		add("/app/chargeback", com.zp.controller.app.AbChargebackContrller.class, "/app/chargeback"); // 退单接口

		add("/app/tc", com.zp.controller.app.AbTcController.class, "/app/tc"); // app快递接口
		add("/app/ks", com.zp.controller.app.AbKsController.class, "/app/ks"); // app快速下单接口

		add("/app/index", com.zp.controller.app.AppIndexController.class, "/app/index"); // app login
		add("/app/city", com.zp.controller.app.AppCityController.class); // app city
		add("/app/fmcar",com.zp.controller.app.AppFmcarController.class); // /fmcar
		add("/app/fmcarorders", com.zp.controller.app.AppFmcarOrdersController.class); // 我的熟车-订单
		add("/app/order", com.zp.controller.app.AppOrderController.class); // 订单
		add("/app/pay", com.zp.controller.app.AppPayController.class); // 支付
		add("/app/common", com.zp.controller.app.CommonController.class); // 通用
		add("/app/user", com.zp.controller.app.AppUserController.class); // 用户
		add("/diaodu", com.zp.controller.comm.OrderDiaoDuController.class,"/diaodu"); //车辆调度
	}
}
