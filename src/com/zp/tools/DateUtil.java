package com.zp.tools;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {
	public static String defDtPtn = "yyyy-MM-dd HH:mm:ss";// 缺省日期格式

	/**
	 * @作者 李金伟 E-mail lijinweiatzzhu@163.com
	 * @日期 Jan 11, 2010 12:38:42 PM
	 * @描述 获取当前时间，格式：yyyy-MM-dd hh:mm:ss
	 * @return String
	 */
	public static String getCurrentDate() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(defDtPtn);
		return simpleDateFormat.format(new Date());
	}

	/**
	 * @作者 李金伟 E-mail lijinweiatzzhu@163.com
	 * @日期 Jan 11, 2010 12:38:08 PM
	 * @描述 根据传入的格式来格式化日期。
	 * @param format
	 * @return String
	 */
	public static String getCurrentDate(String format) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		return simpleDateFormat.format(new Date());
	}

	/**
	 * @作者 李金伟 E-mail lijinweiatzzhu@163.com
	 * @日期 Jan 25, 2010 4:46:53 PM
	 * @描述 按给定的格式将字符串转换为Date
	 * @param strdate
	 * @param format
	 * @return Date
	 */
	public static Date formatCalendar(String strdate, String format) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = simpleDateFormat.parse(strdate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 名称：long2StrDate 功能：该函数将8位或12位或14位的数值型日期时间转换为10位或16位或19带格式的日期时间 型如：
	 * 20040101 转换为 2004/01/01 或 200401010101 转换为 2004/01/01 01:01 或
	 * 20040101010101 转换为 2004/01/01 01:01:01 输入参数：Long date 十二位或十四位的数字日期时间
	 * 返回参数：String fmDate 16位或19位带格式的日期
	 */
	public static String long2StrDate(String date) {
		if (date == null || date.equals(""))
			return "";
		else
			return long2StrDate(Long.valueOf(date));
	}

	public static String int2StrDate(Integer date) {
		if (date == null)
			return "";
		return long2StrDate(Long.valueOf(date));
	}

	public static String long2StrDate(Number date) {
		String fmDate = new String();
		if (date == null) {
			fmDate = "";
		} else {
			fmDate = date.toString();
			String year = "";
			String month = "";
			String day = "";
			String hour = "";
			String minute = "";
			// 日期格式不合法则转化为空串
			if (fmDate.length() < 8) {
				fmDate = "";
			}
			if (fmDate.length() >= 8) {
				year = fmDate.substring(0, 4);
				month = fmDate.substring(4, 6);
				day = fmDate.substring(6, 8);
				fmDate = year + "/" + month + "/" + day;
			}
			if ((date.toString()).length() >= 12) {
				hour = (date.toString()).substring(8, 10);
				minute = (date.toString()).substring(10, 12);
				fmDate = fmDate + " " + hour + ":" + minute;
			}
			if ((date.toString()).length() == 14) {
				fmDate = fmDate + ":" + (date.toString()).substring(12, 14);
			}
		}
		return fmDate;
	}

	public static String int2StrOnlyDate(Number date) {
		String fmDate = new String();
		if (date == null) {
			fmDate = "";
		} else {
			String year = "";
			String month = "";
			String day = "";
			fmDate = date.toString();
			if (fmDate.length() < 8) {
				fmDate = "";
			}
			if (fmDate.length() >= 8) {
				year = fmDate.substring(0, 4);
				month = fmDate.substring(4, 6);
				day = fmDate.substring(6, 8);
				fmDate = year + "/" + month + "/" + day;
			}
		}
		return fmDate;
	}

	public static Date parse(String dateStr, String pattern)
			throws ParseException {
		DateFormat df = new SimpleDateFormat(pattern);
		return df.parse(dateStr);
	}

	public static Date parse(String dateStr) throws ParseException {
		if (dateStr.length() == "yyyy-MM-dd".length())
			return parse(dateStr, "yyyy-MM-dd");
		if (dateStr.length() == "yyyy-MM-dd HH:mm:ss".length()) {
			return parse(dateStr, "yyyy-MM-dd HH:mm:ss");
		}

		return parse(dateStr, "yyyy-MM-dd");
	}

	public static Timestamp parseTimestamp(String dateStr, String pattern)
			throws ParseException {
		return new Timestamp(parse(dateStr, pattern).getTime());
	}

	public static Timestamp parseTimestamp(String dateStr)
			throws ParseException {
		return new Timestamp(parse(dateStr).getTime());
	}

	public static String format(Date date, String pattern) {
		DateFormat df = new SimpleDateFormat(pattern);
		return df.format(date);
	}

	public static String int2StrCnDate(Integer date) {
		if (date == null)
			return "";
		return long2StrCnDate(Long.valueOf(date));
	}

	public static String long2StrCnDate(Long date) {
		String fmDate = new String();
		if (date == null) {
			fmDate = "";
		} else {
			fmDate = date.toString();
			String year = "";
			String month = "";
			String day = "";
			String hour = "";
			String minute = "";
			// 日期格式不合法则转化为空串
			if (fmDate.length() < 8) {
				fmDate = "";
			}
			if (fmDate.length() >= 8) {
				year = fmDate.substring(0, 4);
				month = fmDate.substring(4, 6);
				day = fmDate.substring(6, 8);
				fmDate = year + "年" + month + "月" + day + "日";
			}
			if ((date.toString()).length() >= 10) {
				hour = (date.toString()).substring(8, 10);
				fmDate = fmDate + hour + "时";
			}

			if ((date.toString()).length() >= 12) {
				minute = (date.toString()).substring(10, 12);
				fmDate = fmDate + minute + "分";
			}

			if ((date.toString()).length() == 14) {
				fmDate = fmDate + (date.toString()).substring(12, 14) + "秒";
			}
		}
		return fmDate;
	}

	public static String long2StrCnDate(Number date) {
		String fmDate = new String();
		if (date == null) {
			fmDate = "";
		} else {
			fmDate = String.valueOf(date.longValue());
			String year = "";
			String month = "";
			String day = "";
			String hour = "";
			String minute = "";
			// 日期格式不合法则转化为空串
			if (fmDate.length() < 8) {
				fmDate = "";
			}
			if (fmDate.length() >= 8) {
				year = fmDate.substring(0, 4);
				month = fmDate.substring(4, 6);
				day = fmDate.substring(6, 8);
				fmDate = year + "年" + month + "月" + day + "日";
			}
			if ((date.toString()).length() >= 12) {
				hour = (date.toString()).substring(8, 10);
				minute = (date.toString()).substring(10, 12);
				fmDate = fmDate + hour + "时" + minute + "分";
			}
			if ((date.toString()).length() == 14) {
				fmDate = fmDate + (date.toString()).substring(12, 14) + "秒";
			}
		}
		return fmDate;
	}

	public static String long2StrCnDate(Number date, int length) {
		if (date != null) {
			Long l = Long.valueOf(String.valueOf(date.longValue()).substring(0,
					length));
			return long2StrCnDate(l);
		}
		return null;
	}

	/**
	 * 名称：str2LongDate 功能：该函数将10位或16位或19位带格式的日期时间转换为8位或12位或14位的数值型日期时间
	 * 型如：2004/01/01 转换为 20040101 或 2004/01/01 01:01 转换为 200401010101 或
	 * 2004/01/01 01:01:01 转换为 20040101010101 如果 16位或19位带格式的日期时间 非法 ，则返回 0
	 * 输入参数：String strFmDate 16位或19位带格式的日期时间 返回参数：Long longDate 12位或14位的数值型日期时间
	 */
	public static Integer str2IntDate(String strFmDate) {
		Long date = str2LongDate(strFmDate);
		if (date == null)
			return null;
		return Integer.valueOf(date.intValue());
	}

	public static Long str2LongDate(String strFmDate) {
		if (strFmDate == null || strFmDate.length() == 0) {
			return null;
		}
		Long longDate = new Long(0);
		if (strFmDate.matches("^([12]\\d{3}/[01]\\d/[0123]\\d)$")) {
			longDate = Long.valueOf(strFmDate.replaceAll("/", ""));
		}
		if (strFmDate
				.matches("^([12]\\d{3}/[01]\\d/[0123]\\d\\s[012]\\d:[0-5]\\d)$")) {
			strFmDate = strFmDate.replaceAll("/", "").replaceAll(":", "")
					.replaceAll("\\s", "");
			longDate = Long.valueOf(strFmDate);
		}
		if (strFmDate
				.matches("^([12]\\d{3}/[01]\\d/[0123]\\d\\s[012]\\d:[0-5]\\d:[0-5]\\d)$")) {
			strFmDate = strFmDate.replaceAll("/", "").replaceAll(":", "")
					.replaceAll("\\s", "");
			longDate = Long.valueOf(strFmDate);
		}
		return longDate;
	}

	/**
	 * 编写人： 名称：getCurrentDateView 功能：得到系统当前时间显示，格式：yyyy/mm/dd主要是为页面显示用 输入参数：
	 * 返回参数：String:系统当前时间
	 */
	public static String getCurrentDateView() {
		// 获得当前日期
		Calendar cldCurrent = Calendar.getInstance();
		// 获得年月日
		String strYear = String.valueOf(cldCurrent.get(Calendar.YEAR));
		String strMonth = String.valueOf(cldCurrent.get(Calendar.MONTH) + 1);
		String strDate = String.valueOf(cldCurrent.get(Calendar.DATE));
		// 整理格式
		if (strMonth.length() < 2) {
			strMonth = "0" + strMonth;
		}
		if (strDate.length() < 2) {
			strDate = "0" + strDate;
		}
		// 得出当天日期的字符串
		String StrCurrentCalendar = strYear + "/" + strMonth + "/" + strDate;
		return StrCurrentCalendar;
	}

	/**
	 * 编写人： 名称：getCurrentDateTimeView 功能：得到系统当前时间显示，格式：yyyy/mm/dd hh:mm主要是为页面显示用
	 * 输入参数： 返回参数：String:系统当前时间
	 */
	public static String getCurrentDateTimeView() {
		// 获得当前日期
		Calendar cldCurrent = Calendar.getInstance();
		// 获得年月日
		String strYear = String.valueOf(cldCurrent.get(Calendar.YEAR));
		String strMonth = String.valueOf(cldCurrent.get(Calendar.MONTH) + 1);
		String strDate = String.valueOf(cldCurrent.get(Calendar.DATE));
		String srtHours = String.valueOf(cldCurrent.get(Calendar.HOUR_OF_DAY));
		String strMinute = String.valueOf(cldCurrent.get(Calendar.MINUTE));
		// 整理格式
		if (strMonth.length() < 2) {
			strMonth = "0" + strMonth;
		}
		if (strDate.length() < 2) {
			strDate = "0" + strDate;
		}
		if (srtHours.length() < 2) {
			srtHours = "0" + srtHours;
		}
		if (strMinute.length() < 2) {
			strMinute = "0" + strMinute;
		}
		// 得出当天日期时间的字符串
		String StrCurrentCalendar = strYear + "/" + strMonth + "/" + strDate
				+ " " + srtHours + ":" + strMinute;
		return StrCurrentCalendar;
	}

	public static Integer getIntCurrDate() {
		// 获得当前日期
		Calendar cldCurrent = Calendar.getInstance();
		// 获得年月日
		String strYear = String.valueOf(cldCurrent.get(Calendar.YEAR));
		String strMonth = String.valueOf(cldCurrent.get(Calendar.MONTH) + 1);
		String strDate = String.valueOf(cldCurrent.get(Calendar.DATE));
		// 整理格式
		if (strMonth.length() < 2) {
			strMonth = "0" + strMonth;
		}
		if (strDate.length() < 2) {
			strDate = "0" + strDate;
		}
		return Integer.valueOf(strYear + strMonth + strDate);
	}

	public static Integer getIntCurrYear() {
		// 获得当前日期
		Calendar cldCurrent = Calendar.getInstance();
		// 获得年月日
		String strYear = String.valueOf(cldCurrent.get(Calendar.YEAR));
		return Integer.valueOf(strYear);
	}

	public static Long getLongCurrDateTime() {
		// 获得当前日期
		Calendar cldCurrent = Calendar.getInstance();
		// 获得年月日
		String strYear = String.valueOf(cldCurrent.get(Calendar.YEAR));
		String strMonth = String.valueOf(cldCurrent.get(Calendar.MONTH) + 1);
		String strDate = String.valueOf(cldCurrent.get(Calendar.DATE));
		String srtHours = String.valueOf(cldCurrent.get(Calendar.HOUR_OF_DAY));
		String strMinute = String.valueOf(cldCurrent.get(Calendar.MINUTE));
		// 整理格式
		if (strMonth.length() < 2) {
			strMonth = "0" + strMonth;
		}
		if (strDate.length() < 2) {
			strDate = "0" + strDate;
		}
		if (srtHours.length() < 2) {
			srtHours = "0" + srtHours;
		}
		if (strMinute.length() < 2) {
			strMinute = "0" + strMinute;
		}
		return Long
				.valueOf(strYear + strMonth + strDate + srtHours + strMinute);
	}

	public static Long getLongCurrDateTime8() {
		// 获得当前日期
		Calendar cldCurrent = Calendar.getInstance();
		// 获得年月日
		String strYear = String.valueOf(cldCurrent.get(Calendar.YEAR));
		String strMonth = String.valueOf(cldCurrent.get(Calendar.MONTH) + 1);
		String strDate = String.valueOf(cldCurrent.get(Calendar.DATE));
		String srtHours = String.valueOf(cldCurrent.get(Calendar.HOUR_OF_DAY));
		String strMinute = String.valueOf(cldCurrent.get(Calendar.MINUTE));
		// 整理格式
		if (strMonth.length() < 2) {
			strMonth = "0" + strMonth;
		}
		if (strDate.length() < 2) {
			strDate = "0" + strDate;
		}
		if (srtHours.length() < 2) {
			srtHours = "0" + srtHours;
		}
		if (strMinute.length() < 2) {
			strMinute = "0" + strMinute;
		}
		return Long.valueOf(strYear + strMonth + strDate);
	}

	/**
	 * 作者：程伟平 创建时间：Feb 29, 2008
	 * <p>
	 * 
	 * 获得系统当前时间。
	 * 
	 * @return String 日期格式：yyyyMMddHHmm 示例：200802261310
	 */
	public static String getCurrentDateTime() {
		return getDateTime(0, 0);
	}

	/**
	 * 作者：程伟平 创建时间：Feb 26, 2008
	 * <p>
	 * 
	 * 获得相对于系统当前时间的前（后）month个月、前（后）date个日的时间。
	 * 说明：当month的值为负时，为当前时间的前month个月，当month的值为正时
	 * ，为当前时间的后month个月；当date的值为负时，为当前时间的前date个日，当date的值为正时，为当前时间的后date个日。
	 * 
	 * @param month
	 *            前（后）个月
	 * @param date
	 *            前（后）个日
	 * @return String 日期格式：yyyyMMddHHmm 示例：200802261310
	 */
	public static String getDateTime(int month, int date) {
		// 获得当前日期
		Calendar calendar = Calendar.getInstance();
		if (month != 0) {
			calendar.add(Calendar.MONTH, month);
		}
		if (date != 0) {
			calendar.add(Calendar.DATE, date);
		}
		// 获得年月日
		String strYear = String.valueOf(calendar.get(Calendar.YEAR));
		String strMonth = String.valueOf(calendar.get(Calendar.MONTH) + 1);
		String strDate = String.valueOf(calendar.get(Calendar.DATE));
		String srtHours = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
		String strMinute = String.valueOf(calendar.get(Calendar.MINUTE));
		// 整理格式
		if (strMonth.length() < 2) {
			strMonth = "0" + strMonth;
		}
		if (strDate.length() < 2) {
			strDate = "0" + strDate;
		}
		if (srtHours.length() < 2) {
			srtHours = "0" + srtHours;
		}
		if (strMinute.length() < 2) {
			strMinute = "0" + strMinute;
		}
		return strYear + strMonth + strDate + srtHours + strMinute;
	}

	/**
	 * 作者：程伟平 创建时间：Nov 8, 2008
	 * <p>
	 * 
	 * 获取当前年的第一天
	 * 
	 * @return String
	 */
	public static String getCurrentYearFirstDate() {

		// 获得当前日期
		Calendar calendar = Calendar.getInstance();

		String strYear = String.valueOf(calendar.get(Calendar.YEAR));

		return strYear + "01" + "01" + "00" + "00";

	}

	public static Long getLongCurrDateTime14() {
		return Long.valueOf(formateCalendar(Calendar.getInstance(),
				"yyyyMMddHHmmss"));
	}

	public static Long getLongCurrDateTime12() {
		return Long.valueOf(formateCalendar(Calendar.getInstance(),
				"yyyyMMddHHmm"));
	}

	public static String transferDate(Date date) {
		if (date == null)
			return null;
		SimpleDateFormat dateFormate = new SimpleDateFormat(
				"yyyy/MM/dd HH:mm:ss");
		return dateFormate.format(date);
	}

	public static Long transferDateToLong(Date date) {
		if (date == null)
			return null;
		SimpleDateFormat dateFormate = new SimpleDateFormat("yyyyMMddHHmmss");
		return Long.valueOf(dateFormate.format(date));
	}

	/**
	 * yyyy/MM/dd HH:mm:ss(yyyy年，MM月，dd日，HH时，mm分，ss秒)
	 * 
	 * @param calendar
	 * @param format
	 * @return
	 */
	public static String formateCalendar(Calendar calendar, String format) {
		if (calendar == null)
			return null;
		SimpleDateFormat dateFormate = new SimpleDateFormat(format);
		return dateFormate.format(calendar.getTime());
	}

	public static String formateCalendar(String strdate, String format) {
		if (strdate == null) {
			return null;
		} else {
			SimpleDateFormat dateFormate = new SimpleDateFormat(format);
			return dateFormate.format(strdate);
		}
	}

	/**
	 * 将形如20070730形式的日期格式，转换成2007-07-30
	 */
	public static String dateFormateStr(String strDate) {
		if (strDate == null)
			return "";
		String fmDate = "";
		String year = "";
		String month = "";
		String day = "";
		if (strDate.length() >= 8) {
			year = strDate.substring(0, 4);
			month = strDate.substring(4, 6);
			day = strDate.substring(6, 8);
			fmDate = year + "-" + month + "-" + day;
		}
		return fmDate;
	}

	public static Calendar long2Calendar(Long time) {
		Calendar calendar = null;
		if (time == null) {
			calendar = Calendar.getInstance();
		} else {
			String strTime = String.valueOf(time);
			int year = 0;
			int month = 0;
			int day = 0;
			int hour = 0;
			int minute = 0;
			int sec = 0;
			if (strTime.length() < 8) {
				throw new MisException("时间格式不合法，不能少与八位数字！");
			}
			if (strTime.length() >= 8) {
				year = Integer.valueOf(strTime.substring(0, 4));
				month = Integer.valueOf(strTime.substring(4, 6)) - 1;
				day = Integer.valueOf(strTime.substring(6, 8));
			}
			if (strTime.length() >= 10) {
				hour = Integer.valueOf(strTime.substring(8, 10));
			}
			if (strTime.length() >= 12) {
				minute = Integer.valueOf(strTime.substring(10, 12));
			}
			if (strTime.length() >= 14) {
				sec = Integer.valueOf(strTime.substring(12, 14));
			}
			calendar = new GregorianCalendar(year, month, day, hour, minute,
					sec);
		}
		return calendar;
	}

	/**
	 * 计算时间添加天数计算，给添加评标活动时使用，用于计算评标截止时间 问题编号：[fix-005]
	 * 
	 * @param time
	 *            原来的时间
	 * @param days
	 *            加的天加1天 1.5一天半
	 * @return
	 */
	public static long calcTimeAddOfDay(long time, double days) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");

		java.util.Date date1 = new Date();
		try {
			date1 = format.parse(String.valueOf(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int addDay = (int) (days * 24);
		long Time = (date1.getTime() / 1000) + (60 * 60 * addDay);

		date1.setTime(Time * 1000);

		String mydate = format.format(date1);

		return Long.parseLong(mydate);
	}

	public static String getMonth() {
		int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
		if (month < 10) {
			return "0" + StringUtil.convertStr(String.valueOf(month));
		}
		return StringUtil.convertStr(month);
	}

	/**
	 * 功能描述：得到给定时间减去给定天数后的时间<br>
	 * 
	 * @param time
	 *            时间
	 * @param days
	 *            天数
	 * @return long <BR>
	 *         作者：杨凯<BR>
	 *         时间：Jun 17, 2008 <br>
	 */

	public static long getTimeSubtractOfDay(long time, int days) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");

		java.util.Date date1 = new Date();
		try {
			date1 = format.parse(String.valueOf(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int subtractDay = (int) (days * 24);
		long Time = (date1.getTime() / 1000) - (60 * 60 * subtractDay);

		date1.setTime(Time * 1000);

		String mydate = format.format(date1);

		return Long.parseLong(mydate);
	}

	/**
	 * 功能描述：得到当前时间之前或之后的数据<br>
	 * 
	 * @param number
	 *            正数为以后时间 负数为之前时间 type 1 年 2 月 3 日 ws 返回的长度
	 * @return long <BR>
	 *         作者：陈伟<BR>
	 *         时间：Jun 17, 2008 <br>
	 */
	@SuppressWarnings("static-access")
	public static long getDateToNow(int number, int type, int ws) {
		Long resultLong = null;
		Calendar cal = Calendar.getInstance();
		Date date = new Date();
		cal.setTime(date);
		if (type == 1) {
			cal.add(cal.YEAR, number);
		} else if (type == 2) {
			cal.add(cal.MONTH, number);
		} else if (type == 3) {
			cal.add(cal.DAY_OF_MONTH, number);
		}
		resultLong = transferDateToLong(cal.getTime());
		if (ws == 8) {
			resultLong = resultLong / (1000000);
		}
		return resultLong;
	}

	/**
	 * 将字符串按指定格式解析成日期对象
	 * 
	 * @since 1.1
	 * @param dateStr
	 *            需要进行转换的日期字符串
	 * @param pattern
	 *            日期字符串的格式
	 * @return "yyyy-MM-dd HH:mm:ss"形式的日期对象
	 */
	public static java.util.Date parseDate(String dateStr, String pattern) {
		SimpleDateFormat DATEFORMAT = new SimpleDateFormat(defDtPtn);
		DATEFORMAT.applyPattern(pattern);
		java.util.Date ret = null;
		try {
			ret = DATEFORMAT.parse(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		DATEFORMAT.applyPattern(defDtPtn);
		return ret;
	}

	/**
	 * 得到指定年月的天数
	 * 
	 * @since 1.1
	 * @param month
	 *            指定月份
	 * @param year
	 *            指定的年份
	 * @return 天数
	 */
	public static int getDayOfMonth(int month, int year) {
		int ret = 0;
		boolean flag = false;

		if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
			flag = true;
		}

		switch (month) {
		case 1:
			ret = 31;
			break;
		case 2:
			if (flag) {
				ret = 29;
			} else {
				ret = 28;
			}
			break;
		case 3:
			ret = 31;
			break;
		case 4:
			ret = 30;
			break;
		case 5:
			ret = 31;
			break;
		case 6:
			ret = 30;
			break;
		case 7:
			ret = 31;
			break;
		case 8:
			ret = 31;
			break;
		case 9:
			ret = 30;
			break;
		case 10:
			ret = 31;
			break;
		case 11:
			ret = 30;
			break;
		case 12:
			ret = 31;
			break;
		default:
			break;
		}
		return ret;
	}

	/**
	 * 计算某天是星期几
	 * 
	 * @since 1.1
	 * @param p_date
	 *            日期字符串
	 * @return 星期
	 */
	public static int whatDayIsSpecifyDate(String p_date) {
		// 2002-2-22 is friday5
		long differenceDays = 0L;
		long m = 0L;
		differenceDays = signDaysBetweenTowDate(p_date, "2002-2-22");

		m = (differenceDays % 7);
		m = m + 5;
		m = m > 7 ? m - 7 : m;
		return Integer.parseInt(m + "");
	}

	/**
	 * 计算两日期间相差天数.
	 * 
	 * @since 1.1
	 * @param d1
	 *            日期字符串
	 * @param d2
	 *            日期字符串
	 * @return long 天数
	 */
	public static long signDaysBetweenTowDate(String d1, String d2) {
		java.sql.Date dd1 = null;
		java.sql.Date dd2 = null;
		long result = -1l;
		try {
			dd1 = java.sql.Date.valueOf(d1);
			dd2 = java.sql.Date.valueOf(d2);
			result = signDaysBetweenTowDate(dd1, dd2);
		} catch (Exception ex) {
			result = -1;
		}
		return result;
	}

	/**
	 * 计算两日期间相差天数.
	 * 
	 * @since 1.1
	 * @param d1
	 *            开始日期 日期型
	 * @param d2
	 *            结束日期 日期型
	 * @return long 天数
	 */
	public static long signDaysBetweenTowDate(java.sql.Date d1, java.sql.Date d2) {
		return (d1.getTime() - d2.getTime()) / (3600 * 24 * 1000);
	}

	/**
	 * @param datefrom
	 * @param dateto
	 * @param type
	 * @return
	 */
	public static long betweenTowDate(String datefrom, String dateto, int type) {
		long ls_rtn = 0;
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.Date now = df.parse(datefrom);
			java.util.Date date = df.parse(dateto);
			long l = now.getTime() - date.getTime();
			long day = l / (24 * 60 * 60 * 1000);
			long hour = (l / (60 * 60 * 1000) - day * 24);
			long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
			long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
			// 如果是小时
			if (type == 0) {
				ls_rtn = day;
			} else if (type == 1) {
				ls_rtn = day * 12 + hour;
			} else if (type == 2) {
				ls_rtn = day * 12 * 60 + hour * 60 + s;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ls_rtn;
	}

	public static String getDatePath() {
		String str_rtn = "";
		str_rtn = DateUtil.getCurrentDate("yyyy") + "/"
				+ DateUtil.getCurrentDate("MM") + "/"
				+ DateUtil.getCurrentDate("dd") + "/";
		return str_rtn;
	}

	/**
	 * 判断当前日期是星期几
	 * 
	 * @param pTime
	 *            修要判断的时间
	 * @return dayForWeek 判断结果
	 * @Exception 发生异常
	 */
	public static int dayForWeek(String pTime) {
		int dayForWeek = 0;
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Calendar c = Calendar.getInstance();
			c.setTime(format.parse(pTime));
			if (c.get(Calendar.DAY_OF_WEEK) == 1) {
				dayForWeek = 7;
			} else {
				dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dayForWeek;
	}

	public static void main1(String[] args) throws Throwable {
		// formatCalendar("2015-02-07 11:19:03", defDtPtn);
		// System.out.println(dayForWeek("2015-01-28"));
		getRandomDate();
	}

	public static String getRandomDate() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(defDtPtn);
		Calendar c = Calendar.getInstance();
		long timelong = c.getTimeInMillis();
		long newtimelong = timelong + (long) (Math.random() * 1000) * 1000000;
		c.setTimeInMillis(newtimelong);
		return simpleDateFormat.format(c.getTime());
	}

	/* 判断当前时间和参数时间是否是同一天 */
	public static boolean isSameDay(String dateStr) {
		Calendar currentdate = Calendar.getInstance();
		currentdate.setTime(new Date());
		Calendar logindate = Calendar.getInstance();
		logindate.setTime(formatCalendar(dateStr, defDtPtn));
		return currentdate.get(Calendar.YEAR) == logindate.get(Calendar.YEAR)
				&& currentdate.get(Calendar.MONTH) == logindate
						.get(Calendar.MONTH)
				&& currentdate.get(Calendar.DAY_OF_MONTH) == logindate
						.get(Calendar.DAY_OF_MONTH);
	}

	/* 判断是否可以进行退单操作，下单时间的1-24小时 */
	public static boolean isBackTime(String dateStr) {
		Calendar currentdate = Calendar.getInstance();
		currentdate.setTime(new Date());

		Calendar xddate = Calendar.getInstance();
		xddate.setTime(formatCalendar(dateStr, defDtPtn));

		long milliseconds1 = currentdate.getTimeInMillis();
		long milliseconds2 = xddate.getTimeInMillis();
		long diff = milliseconds1 - milliseconds2;
		long diffHours = diff / (60 * 60 * 1000);
		if (diffHours >= 1 && diffHours <= 24) {
			return true;
		}
		return false;
	}

	/* 得到当前日期+天数 */
	public static String getAddDateStr(int days) {
		Calendar currentdate = Calendar.getInstance();
		currentdate.setTime(new Date());
		currentdate.add(Calendar.DAY_OF_YEAR, days);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(defDtPtn);
		return simpleDateFormat.format(currentdate.getTime());
	}

	/**
	 * 获取当前时间所在周的周一日期
	 */
	public static Date getMondayOfWeek() {
		Calendar c = Calendar.getInstance();
		// 星期天为一周的第一天
		int day = c.get(Calendar.DAY_OF_WEEK);
		c.add(Calendar.DAY_OF_MONTH,
				Calendar.MONDAY - day <= 0 ? Calendar.MONDAY - day : -6);
		return c.getTime();
	}

	/**
	 * 获取当前时间所在周的周末日期
	 */
	public static Date getSundayOfWeek() {
		Calendar c = Calendar.getInstance();
		// 星期天为一周的第一天
		int day = c.get(Calendar.DAY_OF_WEEK);
		c.add(Calendar.DAY_OF_MONTH, day == Calendar.SUNDAY ? 0 : 8 - day);
		return c.getTime();
	}
	
	/**
	 * 获取一个月的最后一天
	 * @param args
	 */
	public static Date getLastDayOfMonth() {
		Date result = null;
		String ym = DateUtil.format(new Date(), "yyyyMM");
		try {
			Date _01 = DateUtil.parse(ym + "01", "yyyyMMdd");
			Calendar c = Calendar.getInstance();
			c.setTime(_01);
			c.add(Calendar.MONTH, 1);
			c.add(Calendar.DAY_OF_MONTH, -1);
			result = DateUtil.parse(DateUtil.format(c.getTime(),"yyyyMMdd") + " 23:59:59", "yyyyMMdd HH:mm:ss");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void main(String [] args) {
		System.out.println(getMondayOfWeek());
		System.out.println(getSundayOfWeek());
	}
}
