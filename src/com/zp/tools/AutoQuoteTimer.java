package com.zp.tools;

import java.util.Date;

import com.zp.entity.AbOrder;
import com.zp.entity.AbOrderQuote;

public class AutoQuoteTimer extends Thread{
	
	private int min = 5;//5分钟
	
	private String sn;
	private Date startTime;
	
	public AutoQuoteTimer(String sn, Date startTime) {
		super();
		this.sn = sn;
		this.startTime = startTime;
	}

	public void run(){
		while(true){
			try {
				Thread.sleep(60 * 1000);	//睡1分钟
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//判断是否满5分钟了
			long now = new Date().getTime();
			long start = startTime.getTime();
			if((now - start) < min * 60 * 1000){
				continue;
			}else{
				//如果已经选中标，则退出
				AbOrder order = AbOrder.dao.findFirst("select * from ab_order where sn=?" , sn);
				String uid = order.getStr(AbOrder.QDRID);
				if(order == null || order.getStr(AbOrder.DDZT).equals("2") || !StringUtil.isNull(uid)){
					break;
				}
				AbOrderQuote q = AbOrderQuote.dao.findFirst("select * from "+AbOrderQuote.TABLE+" where sn=? order by bj_time", sn);
				if(q == null){
					continue;
				}
				
				//自动选择第一个司机中标
				String driverId = q.getStr(AbOrderQuote.UID);
				order.set(AbOrder.QDRID, driverId);
				order.set(AbOrder.QDSJ, DateUtil.getAddDateStr(0));				
				order.set(AbOrder.DDZT, "2");
				order.update();
				break;
			}
		}
	}

}
