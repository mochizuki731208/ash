package com.zp.controller.app;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.core.Controller;
import com.zp.entity.AbCityarea;
import com.zp.entity.AbIntegralScore;
import com.zp.entity.AbOrder;
import com.zp.entity.LogUserDeposit;
import com.zp.entity.SysCzTx;
import com.zp.entity.SysUser;
import com.zp.tools.CommonStaticData;
import com.zp.tools.DateUtil;
import com.zp.tools.RandomUtil;
import com.zp.tools.SmsMessage;
import com.zp.tools.StringUtil;

public class AppPayController extends AbsAppController {
	
	public void payNotify(){
		String notify_time = this.getPara("notify_time");
		String notify_type = this.getPara("notify_type");
		String notify_id = this.getPara("notify_id");
		String sign_type = this.getPara("sign_type");
		String sign = this.getPara("sign");
		
		if(!checkParam(notify_time,notify_type, notify_id, sign_type, sign)){
			renderHtml("fail params error");
		}else{
			//sign 
			//check pay account seller_id
			String seller_id = this.getPara("seller_id");
			System.out.println("payNotify time :"+notify_time+"");
			System.out.println("payNotify sign :"+sign+"");
			System.out.println("success");
			
			String out_trade_no = StringUtil.toStr(this.getPara("out_trade_no"));
			String total_fee = StringUtil.toStr(this.getPara("total_fee"));
			String trade_status = StringUtil.toStr(this.getPara("trade_status"));
			List<SysCzTx> cztx_list = SysCzTx.dao
					.find("select * from sys_cz_tx where tradeno='" + out_trade_no
							+ "'");
			if ("TRADE_SUCCESS".equals(trade_status)
					&& ("2".equals(cztx_list.get(0).getStr("type")))) {// 付款
				for (SysCzTx cztx : cztx_list) {
					if (cztx.getInt("num") == 1) {
						continue;
					}
					cztx.set("result", 1);
					cztx.set("num", 1);
					cztx.update();
					AbOrder order = AbOrder.dao.findById(cztx.getStr("orderid"));
					order.set("ddzt", "1");// 订单状态
					order.set("zfzt", "1");// 支付状态
					order.set("zfsj", DateUtil.getCurrentDate());// 支付状态
					order.update();
				}
			} else if ("TRADE_SUCCESS".equals(trade_status)
					&& ("0".equals(cztx_list.get(0).getStr("type")))) {// 充值
				for (SysCzTx cztx : cztx_list) {
					if (cztx.getInt("num") == 1) {
						continue;
					}
					cztx.set("result", 1);
					cztx.set("num", 1);
					cztx.update();
					LogUserDeposit lud = new LogUserDeposit();
					lud.set("id", StringUtil.getUuid32());
					SysUser user = SysUser.dao.findById(cztx.getStr("userid"));
					lud.set("userid", user.getStr("id"));
					lud.set("loginid", user.getStr("loginid"));
					lud.set("mc", user.getStr("mc"));
					lud.set("createtime", DateUtil.getCurrentDate());
					lud.set("czje", total_fee);
					lud.set("czje", total_fee);
					lud.set("status", 1);
					lud.set("remark", "支付宝在线充值" + total_fee + "元");
					lud.save();
					user.set(
							"zhye",
							user.getBigDecimal("zhye").add(
									new BigDecimal(total_fee)));
					user.update();
				}
			} else {
				// 支付失败
			}
		}
		
		renderHtml("success");
	}

}
