package com.zp.job;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.zp.entity.AbMerchantProduct;
import com.zp.entity.AbOrder;
import com.zp.entity.AbOrderItem;
import com.zp.entity.AbTcExpressOrder;
import com.zp.entity.SysUser;
import com.zp.tools.DateUtil;

public class OrderFinalJieSuanJob  implements Job{
	private static int DAYS = -3;// 系统默认15天给于好评
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		//结算逻辑
		List<AbOrder> orderList = AbOrder.dao.find("select * from ab_order where ddzt='5' and is_account='2' and accountdate < '" + getStartTime() + "'");
		if(null == orderList || orderList.isEmpty()){
			System.out.println("没有要结算的订单");
		}else{
			for(AbOrder o:orderList){
				String sn =  o.getStr("sn");
				System.out.println("本次结算的订单号为：" + sn);
				if(sn.startsWith("TC")){//同城物流订单结算
					AbTcExpressOrder ateo = AbTcExpressOrder.dao.findFirst("select * from ab_tc_express_order where sn = ?", sn);
					if("1".equals(o.getStr("psfs"))){//司机接单
						if(null!=sn && null != o.getStr("qdrid") && o.getStr("qdrid").length() > 0){
							SysUser sjtemp = SysUser.dao.findById(o.getStr("qdrid"));
							sjtemp.set("zhye", sjtemp.getBigDecimal("zhye").add(ateo.getBigDecimal("total_price").subtract(sjtemp.getBigDecimal("per"))));
							sjtemp.update();
						}
					}else{//商家接单
						if(null!=sn && null != o.getStr("mid") && o.getStr("mid").length() > 0){
							SysUser mertemp = SysUser.dao.findById(o.getStr("mid"));
							mertemp.set("zhye", mertemp.getBigDecimal("zhye").add(ateo.getBigDecimal("total_price").subtract(mertemp.getBigDecimal("per"))));
							mertemp.update();
						}
					}
				}else{//店铺订单结算
					List<AbOrderItem> itemList = AbOrderItem.dao.find("select * from ab_order_item where sn = ?", sn);
					//商家自送的情况，如果未设置佣金就将钱打到商家账户上。否则要扣除佣金比例部分。余下打到商家账户上。
					BigDecimal yj = new BigDecimal(0);
					for(AbOrderItem item : itemList){
						AbMerchantProduct temp = AbMerchantProduct.dao.findById(item.getStr("itemid"));
						yj.add(item.getBigDecimal("totalmoney").multiply(temp.getBigDecimal("yj")));//各产品佣金情况
					}
					SysUser user = SysUser.dao.findById(o.getStr("mid"));
					user.set("zhye", user.getBigDecimal("zhye").add(o.getBigDecimal("accountmoney").subtract(yj)));
					user.update();
					//外部派送情况，此时还有一部分资金是发放给司机，司机部分款项也要扣除相应的佣金
					if("1".equals(o.getStr("psfs"))){
						if(null != o.getStr("qdrid") && o.getStr("qdrid").length() > 0){
							SysUser sjtemp = SysUser.dao.findById(o.getStr("qdrid"));
							sjtemp.set("zhye", sjtemp.getBigDecimal("zhye").add(o.getBigDecimal("ptf").subtract(sjtemp.getBigDecimal("per"))));
							sjtemp.update();
						}
					}
				}
			}
		}
	}
	public String getStartTime() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DAY_OF_YEAR, DAYS);
		return DateUtil.format(c.getTime(), DateUtil.defDtPtn);
	}
	public static void main(String[] args) {
		OrderFinalJieSuanJob ofjsj = new OrderFinalJieSuanJob();
		System.out.println(ofjsj.getStartTime());
	}
}
