package com.zp.job;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.zp.entity.AbAppraise;
import com.zp.entity.AbOrder;
import com.zp.tools.CommonProcess;
import com.zp.tools.DateUtil;
import com.zp.tools.StringUtil;

public class AppraiseJob implements Job {

	private static int DAYS = -15;// 系统默认15天给于好评

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from ab_order where accountdate <='"
				+ getStartTime()
				+ "' and ddzt = '4' and id not in (select order_id from ab_appraise)");
		List<Record> records = Db.find(sb.toString());
		for (Record record : records) {
			// 默认好评，
			addappraise(record.getStr(AbOrder.XDRID), record.getStr(AbOrder.QDRID),
					record.getStr(AbOrder.MID), record.getStr(AbOrder.ID));
		}
	}

	public void addappraise(String userId, String mid, String orderId, String qdrid) {
		AbAppraise app = new AbAppraise();
		String appId = StringUtil.getUuid32();
		app.set(AbAppraise.ID, appId);
		app.set(AbAppraise.CONTENT, "");
		app.set(AbAppraise.MER_ID, mid);
		app.set(AbAppraise.USER_ID, userId);
		app.set(AbAppraise.ORDER_ID, orderId);
		app.set(AbAppraise.QDR_ID, qdrid);
		app.set(AbAppraise.TYPE, "1");// 默认好评
		app.set(AbAppraise.PRIVATE, "1");// 公开
		app.set(AbAppraise.DYNC_MAH, 5);
		app.set(AbAppraise.DYNC_SER, 5);
		app.set(AbAppraise.DYNC_SPD, 5);
		app.set(AbAppraise.DYNC_VAL, 5);
		app.set(AbAppraise.DATETIME, DateUtil.getCurrentDate());
		app.save();
		// 增加而修改商户信用等级和客户评价
		CommonProcess.updateMechantCreditAndRate(mid, "1", true);
		CommonProcess.updateQDRCreditAndRate(qdrid, mid, "1", true);
	}

	public String getStartTime() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DAY_OF_YEAR, DAYS);
		return DateUtil.format(c.getTime(), DateUtil.defDtPtn);
	}

	public static void main(String[] args) {
		AppraiseJob aj = new AppraiseJob();
		System.out.println(aj.getStartTime());
	}

}
