package com.zp.job;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.zp.entity.AbOrderChargeback;
import com.zp.tools.CommonStaticData;
import com.zp.tools.DateUtil;

public class ChargebackJob implements Job {

	private static int MER_DAYS = -2;// 商户逾期无操作(48小时)
	private static int USER_DAYS = -2;//用户无操作的
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		//更新商户逾期无操作
		updateApplyChargeback();
		//商户申诉，用户无操作的情况
		updateNoJudgeChargeback();
	}

	private void updateNoJudgeChargeback() {
		StringBuffer sb = new StringBuffer();
		//查询状态为商户申诉的退单记录，申请时间为二天前
		sb.append("select * from ab_order_chargeback where apply_time <='"
				+ getStartTime(USER_DAYS)
				+ "' and cb_status = '"+CommonStaticData.CB_TYPE3+"' ");
		List<AbOrderChargeback> records = AbOrderChargeback.dao.find(sb.toString());
		for (AbOrderChargeback aoc : records) {
			//默认将退单记录状态，改为取消退单
			aoc.set(AbOrderChargeback.CB_STATUS, CommonStaticData.CB_TYPE4);
			aoc.set(AbOrderChargeback.RESULT_DESC, CommonStaticData.MSG_TWO);
			aoc.set(AbOrderChargeback.RESULT_TIME, DateUtil.getCurrentDate());
			aoc.update();
		}
	}

	private void updateApplyChargeback() {
		StringBuffer sb = new StringBuffer();
		//查询状态为用户申请的退单记录，申请时间为二天前
		sb.append("select * from ab_order_chargeback where apply_time <='"
				+ getStartTime(MER_DAYS)
				+ "' and cb_status = '"+CommonStaticData.CB_TYPE1+"' ");
		List<AbOrderChargeback> records = AbOrderChargeback.dao.find(sb.toString());
		for (AbOrderChargeback aoc : records) {
			//默认将退单记录状态，改为退单成功
			aoc.set(AbOrderChargeback.CB_STATUS, CommonStaticData.CB_TYPE6);
			aoc.set(AbOrderChargeback.REP_DESC, CommonStaticData.MSG_ONE);
			aoc.set(AbOrderChargeback.REP_TIME, DateUtil.getCurrentDate());
			aoc.update();
		}
	}

	public String getStartTime(int day) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DAY_OF_YEAR, day);
		return DateUtil.format(c.getTime(), DateUtil.defDtPtn);
	}

	public static void main(String[] args) {
		ChargebackJob aj = new ChargebackJob();
		System.out.println(aj.getStartTime(MER_DAYS));
	}

}
