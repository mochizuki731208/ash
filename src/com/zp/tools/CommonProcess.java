package com.zp.tools;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.imageio.stream.FileImageOutputStream;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.zp.entity.AbAppraise;
import com.zp.entity.AbAppraiseImg;
import com.zp.entity.AbCard;
import com.zp.entity.AbCardExpense;
import com.zp.entity.AbMerchant;
import com.zp.entity.AbOrder;
import com.zp.entity.SysUser;
import com.zp.handler.PageUtil;

public class CommonProcess {
	private static String NUMBER_PATTERN = "#.0";

	public static void createImageFile(List<Record> records) {
		try {
			for (Record record : records) {
				createImage(record, "image", "img_url");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 参数1是record，2是图片字段名，3是图片路径字段名
	public static void createImage(Record record, String image, String imageurl)
			throws IOException {
		byte[] data = record.getBytes(image);
		File file = new File(PathKit.getWebRootPath() + "\\upload\\"
				+ record.getStr(imageurl));
		if (!file.exists() && data != null) {
			file.createNewFile();
			FileImageOutputStream imageOutput = new FileImageOutputStream(file);
			imageOutput.write(data, 0, data.length);
			imageOutput.close();
		}
	}

	/* 生成退单图片 */
	public static void createChargebackImageFile(List<Record> records) {
		try {
			for (Record record : records) {
				createImage(record, "apply_img", "apply_img_url");
				createImage(record, "rep_img", "rep_img_url");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* 创建单张图片 */
	public static void createSingleImage(byte[] data, String imgurl) {
		File file = new File(PathKit.getWebRootPath() + "\\upload\\" + imgurl);
		try {
			if (!file.exists() && data != null) {
				file.createNewFile();
				FileImageOutputStream imageOutput = new FileImageOutputStream(
						file);
				imageOutput.write(data, 0, data.length);
				imageOutput.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setCreditType(PageUtil listpage) {
		for (Record record : listpage.getList()) {
			setSingleCreditType(record);
		}
	}
	
	public static void setSingleCreditType(Record record){
		Integer mechantCredit = record.getInt("credit");
		int credit = (mechantCredit == null) ? 0 : mechantCredit.intValue();
		if (credit >= 1 && credit < 5) {
			record.set("credittype", "1");
		} else if (credit >= 5 && credit < 10) {
			record.set("credittype", "2");
		} else if (credit >= 10 && credit < 15) {
			record.set("credittype", "3");
		} else if (credit >= 15 && credit < 20) {
			record.set("credittype", "4");
		} else if (credit >= 20 && credit < 25) {
			record.set("credittype", "5");
		} else if (credit >= 25 && credit < 35) {
			record.set("credittype", "6");
		} else if (credit >= 35 && credit < 45) {
			record.set("credittype", "7");
		} else if (credit >= 45 && credit < 55) {
			record.set("credittype", "8");
		} else if (credit >= 55 && credit < 65) {
			record.set("credittype", "9");
		} else if (credit >= 65 && credit < 75) {
			record.set("credittype", "10");
		} else if (credit >= 75 && credit < 105) {
			record.set("credittype", "11");
		} else if (credit >= 105 && credit < 150) {
			record.set("credittype", "12");
		} else if (credit >= 150 && credit < 500) {
			record.set("credittype", "13");
		} else if (credit >= 500 && credit < 2000) {
			record.set("credittype", "14");
		} else if (credit >= 2000) {
			record.set("credittype", "15");
		} else {
			record.set("credittype", "0");
		}
	}

	/* 更新商户信用等级和客户评价，mid 商户ID, type 评价类型, isAdd 是否新增评价 */
	public static void updateMechantCreditAndRate(String mid, String type,
			boolean isAdd) {
		if (!StringUtil.isNull(mid)) {
			AbMerchant am = AbMerchant.dao.findById(mid);
			Integer mechantCredit = am.getInt(AbMerchant.CREDIT);
			int cv = (mechantCredit == null) ? 0 : mechantCredit.intValue();
			// 修改商户信用积分
			if (!"2".equals(type)) {
				if (isAdd && "1".equals(type)) {
					cv++;
				} else if (isAdd && "3".equals(type)) {
					cv--;
				} else if (!isAdd && "1".equals(type)) {
					cv--;
				} else if (!isAdd && "3".equals(type)) {
					cv++;
				}
				am.set(AbMerchant.CREDIT, cv);
			}
			float rate = 0;
			List<Record> aas = Db.find(CommonSQL.getAppraiseByMid(mid));
			if (aas != null && aas.size() > 0) {
				for (Record aa : aas) {
					rate += aa.getFloat(AbAppraise.DYNC_VAL);
				}
				rate = rate / aas.size();
			}
			DecimalFormat df = new DecimalFormat(NUMBER_PATTERN);
			am.set(AbMerchant.RATE, String.valueOf(df.format(rate)));
			am.update();
		}
	}

	/* 更新跑腿信用等级和客户评价，qdrId 业务员ID, mid 商户ID， type 评价类型, isAdd 是否新增评价 */
	public static void updateQDRCreditAndRate(String qdrId, String mid,
			String type, boolean isAdd) {
		if (!StringUtil.isNull(qdrId) && !qdrId.equals(mid)) {
			SysUser su = SysUser.dao.findById(qdrId);
			String roleId = su.getStr(SysUser.ROLE_ID);
			if (CommonStaticData.USER_TYPE_MERCHANT.equals(roleId)) {
				AbMerchant am = AbMerchant.dao.findById(mid);
				updateMechantAppraise(am, type, isAdd, qdrId);
			} else if (CommonStaticData.USER_TYPE_SERVICE.equals(roleId)) {
				updateServiceAppraise(su, type, isAdd, qdrId);
			}

		}
	}

	// 更新评价
	public static void updateMechantAppraise(AbMerchant am, String type,
			boolean isAdd, String qdrId) {
		Integer mechantCredit = am.getInt(SysUser.CREDIT);
		int cv = (mechantCredit == null) ? 0 : mechantCredit.intValue();
		// 修改商户信用积分
		if (!"2".equals(type)) {
			if (isAdd && "1".equals(type)) {
				cv++;
			} else if (isAdd && "3".equals(type)) {
				cv--;
			} else if (!isAdd && "1".equals(type)) {
				cv--;
			} else if (!isAdd && "3".equals(type)) {
				cv++;
			}
			am.set(SysUser.CREDIT, cv);
		}
		float rate = 0;
		List<Record> aas = Db.find(CommonSQL.getAppraiseByQdrid(qdrId));
		if (aas != null && aas.size() > 0) {
			for (Record aa : aas) {
				rate += aa.getFloat(AbAppraise.DYNC_VAL);
			}
			rate = rate / aas.size();
		}
		DecimalFormat df = new DecimalFormat(NUMBER_PATTERN);
		am.set(AbMerchant.RATE, String.valueOf(df.format(rate)));
		am.update();
	}

	// 更新评价
	public static void updateServiceAppraise(SysUser am, String type,
			boolean isAdd, String qdrId) {
		Integer mechantCredit = am.getInt(SysUser.CREDIT);
		int cv = (mechantCredit == null) ? 0 : mechantCredit.intValue();
		// 修改商户信用积分
		if (!"2".equals(type)) {
			if (isAdd && "1".equals(type)) {
				cv++;
			} else if (isAdd && "3".equals(type)) {
				cv--;
			} else if (!isAdd && "1".equals(type)) {
				cv--;
			} else if (!isAdd && "3".equals(type)) {
				cv++;
			}
			am.set(SysUser.CREDIT, cv);
		}
		float rate = 0;
		List<Record> aas = Db.find(CommonSQL.getAppraiseByQdrid(qdrId));
		if (aas != null && aas.size() > 0) {
			for (Record aa : aas) {
				rate += aa.getFloat(AbAppraise.DYNC_VAL);
			}
			rate = rate / aas.size();
		}
		DecimalFormat df = new DecimalFormat(NUMBER_PATTERN);
		am.set(AbMerchant.RATE, String.valueOf(df.format(rate)));
		am.update();
	}

	public static void deleteappraise(String appId) {
		String id = StringUtil.toStr(appId);
		AbAppraise app = AbAppraise.dao.findById(id);
		String mid = app.getStr(AbAppraise.MER_ID);
		AbOrder order = AbOrder.dao.findById(app.getStr(AbAppraise.ORDER_ID));
		List<AbAppraiseImg> listimgs = AbAppraiseImg.dao
				.find("select * from ab_appraise_img where app_id = '" + id
						+ "'");
		for (AbAppraiseImg aai : listimgs) {
			aai.delete();
		}
		String type = app.getStr(AbAppraise.TYPE);
		app.delete();
		// 更新商户的信用等级和客户评价
		updateMechantCreditAndRate(mid, type, false);
		updateQDRCreditAndRate(order.getStr(AbOrder.QDRID), mid, type, false);
	}

	/**
	 * 月份对照说明 一月 JA 二月 FE 三月 MH 四月 AP 五月 MR 六月 JE 七月 JY 八月 AG 九月 SP 十月 OT 十一月 NO
	 * 十二月 DE
	 * 
	 * @return
	 */

	public static String getMonthMapStr(int month) {
		switch (month) {
		case 1:
			return "JA";
		case 2:
			return "FE";
		case 3:
			return "MH";
		case 4:
			return "AP";
		case 5:
			return "MR";
		case 6:
			return "JE";
		case 7:
			return "JY";
		case 8:
			return "AG";
		case 9:
			return "SP";
		case 10:
			return "OT";
		case 11:
			return "NO";
		case 12:
			return "DE";
		}

		return null;
	}

	/**
	 * 会员卡规则 年份（2）+月份（2）+序号（6）+随机数3位 例如： 15JA000001ABC
	 **/
	public static String getCardNumber() {
		Calendar ca = Calendar.getInstance();
		ca.setTime(new Date());
		String yearstr = String.valueOf(ca.get(Calendar.YEAR)).substring(2);
		String monthstr = getMonthMapStr(ca.get(Calendar.MONTH) + 1);
		String condstr = yearstr + monthstr;
		List<Record> rs = Db
				.find("select MAX(substring(card_number, 5, 6)) max_number from ab_card where left(card_number, 4) = '"
						+ condstr + "'");
		return condstr + getCardSeqNumber(rs.get(0).getStr("max_number"))
				+ getRandomString(3);
	}

	/* 得到会员卡序列号 */
	public static String getCardSeqNumber(String maxstr) {
		if (StringUtil.isNull(maxstr)) {
			return "000001";
		}
		String number = String.valueOf(Integer.valueOf(maxstr) + 1);
		if (number.length() < 6) {
			String str = "";
			for (int i = 0; i < 6 - number.length(); i++) {
				str += "0";
			}
			number = str + number;
		}
		return number;
	}

	/* 随机生成字符串 */
	public static String getRandomString(int length) { // length表示生成字符串的长度
		String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		CommonProcess cp = new CommonProcess();
		Record record = new Record();
		record.set("sn", "TC20150324000001");
		record.set("qdrid", "");
		System.out.println(cp.equals(record));
	}

	// 有接单人
	public static boolean existQdr(Record record) {
		String sn = record.getStr("sn");
		if (!StringUtil.isNull(sn)
				&& CommonStaticData.ORDER_TYPE_TC.equals(sn.substring(0, 2))) {
			if (!StringUtil.isNull(record.getStr("qdrid"))) {
				return true;
			}
		}
		return false;
	}

	//会员卡抵扣
	public static BigDecimal cardDecution(List<Record> ecards, BigDecimal finalprice, String orderid){
		if(ecards != null && ecards.size()>0){
			for(Record r : ecards){
				if(finalprice.doubleValue() > 0){
					AbCard card = AbCard.dao.findById(r.getStr("id"));
					AbCardExpense ace = new AbCardExpense();
					ace.set(AbCardExpense.ID, StringUtil.getUuid32());
					ace.set(AbCardExpense.ORDER_ID, orderid);
					ace.set(AbCardExpense.CARD_ID, card.get("id"));
					ace.set(AbCardExpense.STATUS, CommonStaticData.CARD_EXPENSE_VALID);
					//总价金额大于单张会员卡余额
					BigDecimal rmoney = r.getBigDecimal(AbCard.RMONEY);
					if(finalprice.compareTo(r.getBigDecimal(AbCard.RMONEY))==1){
						//插入会员卡使用记录
						finalprice = finalprice.subtract(rmoney);
						ace.set(AbCardExpense.MONEY, rmoney);
						ace.save();
						//将会员卡，扣除余额，置为无效
						card.set(AbCard.STATUS, CommonStaticData.CARD_STATUS_INVALID);
						card.set(AbCard.RMONEY, new BigDecimal(0));
						card.update();
					}else{
						//插入会员卡使用记录
						ace.set(AbCardExpense.MONEY, finalprice);
						ace.save();
						//将会员卡，扣除余额
						card.set(AbCard.RMONEY, card.getBigDecimal(AbCard.RMONEY).subtract(finalprice));
						card.update();
						finalprice = new BigDecimal(0);
					}
				}
			}
		}
		return finalprice;
	}
}
