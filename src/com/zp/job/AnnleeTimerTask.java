package com.zp.job;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

import javax.servlet.ServletContext;

import com.zp.entity.AbMerchantProduct;
import com.zp.entity.AbOrder;
import com.zp.entity.AbOrderItem;
import com.zp.entity.AbTcExpressOrder;
import com.zp.entity.SysUser;
import com.zp.tools.DateUtil;

public class AnnleeTimerTask extends TimerTask{
	private static boolean isInit = true;
	private ServletContext context;
	private static boolean isRunning = false;
	
	public AnnleeTimerTask(ServletContext context){
		this.context = context;
	}
	@Override
	public void run() {
		if(isInit){
			isInit=false;
			return;
		}
		if (!isRunning) {
			isRunning = true;
			if(doJieSuan()){
				isRunning = false; 
				context.log("本次任务执行结束：成功");
			}else{
				isRunning = false; 
				context.log("本次任务执行结束:失败");
			}
		}else{
			context.log("上一次任务执行还未结束");
		}
	}
	
	public static boolean doJieSuan(){
		//结算逻辑
		Calendar cad = Calendar.getInstance();
		cad.set(cad.get(Calendar.YEAR), cad.get(Calendar.MONTH), cad.get(Calendar.DAY_OF_MONTH) - 3, cad.get(Calendar.HOUR_OF_DAY), cad.get(Calendar.MINUTE), cad.get(Calendar.SECOND));
		String dataTime = DateUtil.format(cad.getTime(), DateUtil.defDtPtn);
		System.out.println("===============时间点" + dataTime + "===============");
		List<AbOrder> orderList = AbOrder.dao.find("select * from ab_order where ddzt='5' and is_account='2' and accountdate < '" + dataTime + "'");
		System.out.println("查询结束");
		if(null == orderList || orderList.isEmpty()){
			System.out.println("没有要结算的订单");
			return true;
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
			System.out.println("结算结束！");
			return true;
		}
	}
	public ServletContext getContext() {
		return context;
	}
	public void setContext(ServletContext context) {
		this.context = context;
	}

}
