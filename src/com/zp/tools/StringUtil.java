package com.zp.tools;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 李金伟
 * @版本：2015-1-14 10:42:31
 * @desc 字符串工具类
 */
@SuppressWarnings("unchecked")
public class StringUtil {
	
	/**
	 * @Author:LiJinwei
	 * @Email:lijinweiatzzhu@163.com
	 * @Date:Aug 15, 2010 9:35:59 AM
	 * @Desc:判断传入对象是否为空，如为空则返回true，或者返回false
	 * @param obj
	 * @return
	 */

	@SuppressWarnings("unchecked")
	public static boolean isNull(Collection obj) {
		if (obj == null || obj.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * @Author:LiJinwei
	 * @Email:lijinweiatzzhu@163.com
	 * @Date:Aug 15, 2010 9:36:52 AM
	 * @Desc:判断传入String是否为空，如为空则返回true，或者返回false
	 * @param obj
	 * @return
	 */
	public static boolean isNull(String str) {
		if (str == null || str.trim().equals("")) {
			return true;
		}
		return false;
	}

	/**
	 * @Author:LiJinwei
	 * @Email:lijinweiatzzhu@163.com
	 * @Date:Aug 15, 2010 9:37:00 AM
	 * @Desc:判断传入String数组是否为空，如为空则返回true，或者返回false
	 * @param obj
	 * @return
	 */
	public static boolean isNull(String[] str) {
		if (str == null || str.length <= 0) {
			return true;
		}
		return false;
	}

	/**
	 * @Author:LiJinwei
	 * @Email:lijinweiatzzhu@163.com
	 * @Date:Aug 15, 2010 8:46:01 AM
	 * @Desc:将数组转换为以符号进行分割的字符串
	 * @param strArray
	 * @param separator
	 * @return
	 */
	public static String toString(String[] strArray, String separator) {
		String strResult = "";
		if (strArray == null || separator == null) {
			strResult = null;
		} else {
			StringBuffer strBuffer = new StringBuffer();
			for (int i = 0; i < strArray.length; i++) {
				if (strArray[i] == null || strArray[i].length() == 0)
					continue;
				strBuffer.append(separator);
				strBuffer.append(strArray[i]);
			}
			if (strBuffer.length() > 0) {
				strBuffer.delete(0, separator.length());
			}
			strResult = strBuffer.toString();
		}
		return strResult;
	}

	/**
	 * @Author:LiJinwei
	 * @Email:lijinweiatzzhu@163.com
	 * @Date:Aug 15, 2010 8:47:00 AM
	 * @Desc:将阿拉伯数字转换为大写字中文汉字
	 * @param a
	 * @return
	 */
	public static String translateNumToChinese(int a) {
		String[] units = { "", "十", "百", "千", "万", "十", "百", "千", "亿" };
		String[] nums = { "一", "二", "三", "四", "五", "六", "七", "八", "九", "十" };
		String result = "";
		if (a < 0) {
			result = "负";
			a = Math.abs(a);
		}
		String t = String.valueOf(a);
		for (int i = t.length() - 1; i >= 0; i--) {
			int r = (int) (a / Math.pow(10, i));
			if (r % 10 != 0) {
				String s = String.valueOf(r);
				String l = s.substring(s.length() - 1, s.length());
				result += nums[Integer.parseInt(l) - 1];
				result += (units[i]);
			} else {
				if (!result.endsWith("零")) {
					result += "零";
				}
			}
		}
		return result;
	}

	/**
	 * @Author:LiJinwei
	 * @Email:lijinweiatzzhu@163.com
	 * @Date:Aug 15, 2010 8:48:31 AM
	 * @Desc:将对象转换为字符串，如果为空则返回null
	 * @param str
	 * @return
	 */
	public static String convertStr(Object str) {
		if (str == null || str.toString().equals(""))
			return null;
		return str.toString().trim();
	}

	public static String toStr(Object obj) {
		if (obj == null || obj.toString().equals("") || "null".equals(obj))
			return "";
		return obj.toString().trim();
	}

	public static String toHtmlStr(Object obj) {
		if (obj == null || obj.toString().equals("") || "null".equals(obj))
			return "&nbsp;";
		return obj.toString().trim();
	}

	/**
	 * @Author:LiJinwei
	 * @Email:lijinweiatzzhu@163.com
	 * @Date:Aug 15, 2010 8:49:18 AM
	 * @Desc:将对象转换为Integer数据，如果为空则返回null
	 * @param str
	 * @return
	 */
	public static Integer convertInt(Object str) {
		if (str == null || "".equals(str.toString()))
			return null;
		return Integer.valueOf(str.toString());
	}

	/**
	 * @Author:LiJinwei
	 * @Email:lijinweiatzzhu@163.com
	 * @Date:Aug 15, 2010 8:49:55 AM
	 * @Desc:将对象转换为Long型数据，如果为空则返回null
	 * @param str
	 * @return
	 */
	public static Long convertLong(Object str) {
		if (str == null || "".equals(str.toString()))
			return null;
		return Long.valueOf(str.toString());
	}

	/**
	 * @Author:LiJinwei
	 * @Email:lijinweiatzzhu@163.com
	 * @Date:Aug 15, 2010 8:50:45 AM
	 * @Desc:将对象转换为Double型数据，如果为空则返回null
	 * @param obj
	 * @return
	 */
	public static Double convertDouble(Object obj) {
		if (obj == null || "".equals(obj.toString()))
			return null;
		return Double.valueOf(obj.toString());
	}

	/**
	 * @Author:LiJinwei
	 * @Email:lijinweiatzzhu@163.com
	 * @Date:Aug 15, 2010 8:51:22 AM
	 * @Desc:将对象转换为BigDecimal型数据，如果为空则返回null
	 * @param obj
	 * @return
	 */
	public static BigDecimal convertBigDecimal(Object obj) {
		if (obj == null || "".equals(obj.toString()))
			return StringUtil.convertBigDecimal("0");
		return BigDecimal.valueOf(Double.valueOf(obj.toString()));
	}

	/**
	 * @Author:LiJinwei
	 * @Email:lijinweiatzzhu@163.com
	 * @Date:Aug 15, 2010 8:52:13 AM
	 * @Desc:将
	 * @param src
	 * @param separator
	 * @param quot
	 * @param defaultValue
	 * @return
	 */
	public static String join(Object[] src, String separator, String quot, String defaultValue) {
		StringBuffer sb = new StringBuffer();
		if (src == null || src.length == 0) {
			return defaultValue;
		} else {
			for (int i = 0; i < src.length; i++) {
				if (sb.length() > 0) {
					sb.append(separator);
				}
				if (quot != null) {
					sb.append(quot);
				}
				sb.append(src[i]);
				if (quot != null) {
					sb.append(quot);
				}
			}
			return sb.toString();
		}
	}

	public static String join(List<String> list, String separator, String quot, String defaultValue) {
		StringBuffer sb = new StringBuffer();
		if (list == null || list.size() == 0) {
			return defaultValue;
		} else {
			for (String value : list) {
				if (sb.length() > 0 && separator != null) {
					sb.append(separator);
				}
				if (quot != null) {
					sb.append(quot);
				}
				sb.append(value);
				if (quot != null) {
					sb.append(quot);
				}
			}
			return sb.toString();
		}
	}

	/**
	 * @Author:LiJinwei
	 * @Email:lijinweiatzzhu@163.com
	 * @Date:Aug 15, 2010 9:00:08 AM
	 * @Desc:将数字转换成制定格式的字符串，如：
	 * @format("￥#,##0.00元", 66778899),format("yyyy-MM-dd",new Date())
	 * @param pattern
	 * @param value
	 * @return
	 */
	public static String format(String pattern, Object value) {
		if (value == null) {
			return "0.00";
		} else if (value instanceof Number) {
			return new DecimalFormat(pattern).format(value);
		} else if (value instanceof Date) {
			return new SimpleDateFormat(pattern).format(value);
		} else if (value instanceof Calendar) {
			return new SimpleDateFormat(pattern).format(((Calendar) value).getTime());
		} else {
			return value.toString();
		}
	}

	/**
	 * @Author:LiJinwei
	 * @Email:lijinweiatzzhu@163.com
	 * @Date:Aug 15, 2010 9:11:18 AM
	 * @Desc:截取字符串，如果字符串长度小于要求的最大长度直接返回，否则截取相应的长度
	 * @Desc:一个汉字和和字符都按一个长度来计算
	 * @param str
	 * @param maxLength
	 * @return
	 */
	public static String subMaxStr(String str, int maxLength) {
		String result = null;
		if (str != null) {
			if (str.length() <= maxLength) {
				result = str;
			} else {
				result = str.substring(0, maxLength);
			}
		}
		return result;
	}

	/**
	 * @Author:LiJinwei
	 * @Email:lijinweiatzzhu@163.com
	 * @Date:Aug 15, 2010 9:12:58 AM
	 * @Desc:截取字符串，如果字符串长度小于要求的最大长度直接返回，否则截取相应的长度,剩余的又......代替，或者由flag里面的表示
	 * @param str
	 * @param maxLength
	 * @param flag
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String subMaxStrToEtc(String str, int maxLength, String flag) {
		String result = null;
		if (str != null) {
			if (strlen(str) <= maxLength) {
				result = str;
			} else {
				result = StringUtil.subString(str, maxLength) + flag;
			}
		} else {
			str = "";
		}
		return result;
	}

	/**
	 * @Author:LiJinwei
	 * @Email:lijinweiatzzhu@163.com
	 * @Date:Aug 15, 2010 9:14:00 AM
	 * @Desc:去除字符串中的回车符
	 * @param str
	 * @return
	 */
	public static String removeNewline(String str) {
		String result = null;
		if (str != null) {
			result = str.replaceAll("\\r\\n", "");
			result = result.replaceAll(new String(new char[] { 10 }), "");
		}
		return result;
	}

	/**
	 * @Author:LiJinwei
	 * @Email:lijinweiatzzhu@163.com
	 * @Date:Aug 15, 2010 9:14:44 AM
	 * @Desc:将字符串数组转换为数值字符串
	 * @param values
	 * @return
	 */
	public static Integer[] convertStringArray2IntegerArray(String[] values) {
		List<Integer> list = new ArrayList<Integer>();
		if (values != null) {
			for (String value : values) {
				list.add(Integer.valueOf(value));
			}
			return list.toArray(new Integer[] {});
		} else {
			return null;
		}
	}

	/**
	 * @Author:LiJinwei
	 * @Email:lijinweiatzzhu@163.com
	 * @Date:Aug 15, 2010 9:15:34 AM
	 * @Desc:将数字转换为汉文字符
	 * @param strNum
	 * @return
	 */
	public static String number2Cn(String strNum) {
		String result = null;
		if (strNum != null && strNum.length() > 0) {
			result = strNum;
			result = result.replaceAll("0", "零");
			result = result.replaceAll("1", "一");
			result = result.replaceAll("2", "二");
			result = result.replaceAll("3", "三");
			result = result.replaceAll("4", "四");
			result = result.replaceAll("5", "五");
			result = result.replaceAll("6", "六");
			result = result.replaceAll("7", "七");
			result = result.replaceAll("8", "八");
			result = result.replaceAll("9", "九");
		}
		return result;
	}

	/**
	 * @Author:LiJinwei
	 * @Email:lijinweiatzzhu@163.com
	 * @Date:Aug 15, 2010 9:17:18 AM
	 * @Desc:获取32位UUID编码
	 * @return
	 */
	public static String getRandString32() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString().replace("-", "").replace("{", "").replace("}", "");
	}

	/**
	 * @Author:LiJinwei
	 * @Email:lijinweiatzzhu@163.com
	 * @Date:Aug 15, 2010 9:17:42 AM
	 * @Desc:获取32位中前16位UUID编码
	 * @return
	 */
	public static String getRandString16() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString().replace("-", "").replace("{", "").replace("}", "").substring(0, 16);
	}

	/**
	 * @Author:LiJinwei
	 * @Email:lijinweiatzzhu@163.com
	 * @Date:Aug 15, 2010 9:18:36 AM
	 * @Desc:将List转换为split分隔的字符串，默认以","进行分隔
	 * @param list
	 * @param split
	 * @return
	 */
	
	public static String listToString(List list, String split) {
		if (list == null || list.size() == 0) {
			return "";
		} else {
			String str = "";
			for (int i = 0; i < list.size(); i++) {
				if (split == null || split.length() < 1) {
					str = str + list.get(i) + ",";
				} else {
					str = str + list.get(i) + split;
				}
			}
			return str;
		}
	}

	/**
	 * @Author:LiJinwei
	 * @Email:lijinweiatzzhu@163.com
	 * @Date:Aug 15, 2010 9:21:50 AM
	 * @Desc:将字符串str转换成以split为分隔符的List数组
	 * @param str
	 * @param split
	 * @return
	 */
	public static List strToList(String str, String split) {
		if (str == null || str.equals("")) {
			return new ArrayList();
		} else {
			StringTokenizer st = new StringTokenizer(str, split);
			List re = new ArrayList();
			while (st.hasMoreElements()) {
				String s = (String) st.nextElement();
				if (s == null || s.equals("")) {
					continue;
				} else {
					re.add(s);
				}
			}

			return re;
		}

	}

	public static String replaceSQL(String sql) {
		sql = sql.replaceAll("'", "''");
		sql = sql.replaceAll("/", "//");
		return sql;
	}

	/**
	 * @Author:LiJinwei
	 * @Email:lijinweiatzzhu@163.com
	 * @Date:Aug 15, 2010 9:28:12 AM
	 * @Desc:过滤SQL语句，防止SQL语句字符注入
	 * @param inStr
	 * @return
	 */
	public static String doneSQL(String inStr) {
		StringBuffer sb = new StringBuffer("");
		char[] chStr = inStr.toCharArray();
		for (int j = 0; j < chStr.length; j++) {
			if (chStr[j] == '_' || chStr[j] == '%' || chStr[j] == '\\') {
				sb.append("\\");
			}
			sb.append(chStr[j]);
		}
		return sb.toString();
	}

	public static int strlen(String str) {
		if (str == null || str.length() <= 0) {
			return 0;
		}
		int len = 0;
		char c;
		for (int i = str.length() - 1; i >= 0; i--) {
			c = str.charAt(i);
			if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
				// 字母, 数字
				len++;
			} else {
				if (Character.isLetter(c)) { // 中文
					len += 2;
				} else { // 符号或控制字符
					len++;
				}
			}
		}
		return len;
	}

	/**
	 * @Author:LiJinwei
	 * @Email:lijinweiatzzhu@163.com
	 * @Date:Dec 9, 2010 3:28:17 PM
	 * @Desc:截取字符串，中文两个、英文一个
	 * @param str
	 * @param length
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String subString(String str, int length) {
		String str_rtn = "";
		if (length > 0) {
			// 将字符串按照"gbk"编码形式解码为字节序列，并保存在数组中
			// 当汉字采用GBK编码时占两个字节，而采用UTF-8编码时占3个字节，并且都为负整数

			try {
				byte[] bt = str.getBytes("gbk");
				if (length <= bt.length) {
					// 判断在要截取长度的数组中有多少个负数
					int count = 0;
					for (int i = 0; i < length; i++) {
						if (bt[i] < 0)
							count++;
					}
					if (count % 2 == 0) {
						// 如果刚好被2整除，说明截取不会出现乱码
						str_rtn = new String(bt, 0, length, "gbk");
					} else {
						// 不能整除则会出现乱码，则需要把最后一个字节去掉
						str_rtn = new String(bt, 0, --length, "gbk");
					}
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return str_rtn;
	}

	/**
	 * @Author:LiJinwei
	 * @Email:lijinweiatzzhu@163.com
	 * @Date:Dec 3, 2010 1:27:07 PM
	 * @Desc:序列化生成100010001000格式数据
	 * @param isAdd是否需要重新追加长度为len以1000开头的编码
	 * @param oldcode传入的编码
	 * @param len长度
	 * @return
	 */
	public static String getStrSeqCode(boolean isAdd, String oldcode, int len) {
		String str_seq = "";
		// 如果
		if (isAdd == true) {
			if (oldcode == null || oldcode.length() < 1) {
				str_seq = "1";
				for (int i = 0; i < len - 1; i++) {
					str_seq += "0";
				}
			} else {
				str_seq = oldcode + "1";
				for (int i = 0; i < len - 1; i++) {
					str_seq += "0";
				}
			}
		} else {
			if (oldcode != null && oldcode.length() > 0) {
				if (oldcode.length() < len) {
					System.out.println("getStrSeqCode()方法中oldcode长度不能小于给定的len长度！");
				} else if (oldcode.length() == len) {
					str_seq = (Long.parseLong(oldcode) + 1) + "";
				} else if (oldcode.length() > len) {
					String str_left = oldcode.substring(0, oldcode.length() - len);
					String str_right = oldcode.substring(oldcode.length() - len, oldcode.length());
					str_right = String.valueOf(Long.parseLong(str_right) + 1);
					if (str_right.length() < len) {
						String str_temp = "";
						for (int i = 0; i < len - str_right.length(); i++) {
							str_temp += "0";
						}
						str_right = str_temp + str_right;
					}
					str_seq = str_left + str_right;
				}
			} else {
				System.out.println("getStrSeqCode()方法中oldcode为空！");
			}
		}
		return str_seq;
	}
	
	public static String decode(String ss){
		try {
			return java.net.URLDecoder.decode(ss, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getFirstCat(String s){
		String[] sb = s.split(" ");
		StringBuffer ss = new StringBuffer("");
		for (int i = 0; i < sb.length; i++) {
			sb[i] = sb[i].substring(0, 1).toUpperCase() + sb[i].substring(1);
		}
		for (int i = 0; i < sb.length; i++) {
			ss.append(sb[i]);
			ss.append(" ");
		}
		return ss.toString();
	}
	
	public static String getClassName(String s){
		s = s.replace("_", " ");
		s = getFirstCat(s.toLowerCase());
		s = s.replace(" ", "");
		return s;
	}
	
	public static String getUuid32(){
		String uuid = java.util.UUID.randomUUID().toString();
		return uuid.replace("-","").toLowerCase();
	}
	
	public static String getExt(String filename){
		int index = filename.lastIndexOf(".");
		String ext = filename.substring(index).toLowerCase();
		return ext;
	}
	
	public static String getRemoteLoginUserIp(HttpServletRequest request){

	  String ip = request.getHeader("x-forwarded-for"); 
	  if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	  ip = request.getHeader("Proxy-Client-IP"); 
	  } 
	  if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	  ip = request.getHeader("WL-Proxy-Client-IP"); 
	  } 
	  if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	      ip = request.getRemoteAddr(); 
	  } 
	  return ip; 
	}
	
	public static String getThumbnailImg(){
		return null;
	}
	
	public static String getRandomDec(){
		Random ran=new Random();
		DecimalFormat df = new DecimalFormat(".00");
		double j = 0;
		do {
			j =ran.nextInt(50)/(100.0);
		} while (j<0.2);
		System.out.println(df.format(j));
		return df.format(j);
	}
	
	/**
	 * 计算地球上任意两点(经纬度)距离
	 * 
	 * @param long1
	 *            第一点经度
	 * @param lat1
	 *            第一点纬度
	 * @param long2
	 *            第二点经度
	 * @param lat2
	 *            第二点纬度
	 * @return 返回距离 单位：米
	 */
	public static String Distance(double long1, double lat1, double long2,double lat2) {
		double a, b, R;
		R = 6378137; // 地球半径
		lat1 = lat1 * Math.PI / 180.0;
		lat2 = lat2 * Math.PI / 180.0;
		a = lat1 - lat2;
		b = (long1 - long2) * Math.PI / 180.0;
		double d;
		double sa2, sb2;
		sa2 = Math.sin(a / 2.0);
		sb2 = Math.sin(b / 2.0);
		d = 2 * R * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1) * Math.cos(lat2) * sb2 * sb2));
		DecimalFormat    df   = new DecimalFormat("######0.00");
		return df.format(d);
	}
	
	/**
	 * @param filename
	 * @return
	 */
	public static String getThumbnail(String filename){
		String newfile = "";
		if(StringUtil.toStr(filename).length()>0){
			newfile = filename.substring(0,filename.indexOf("."))+"-thumbnail"+filename.substring(filename.indexOf("."));
		}
		return newfile;
	}

	
	public static void main(String[] args) {
		//System.out.println(getExt("xxx.jPG"));;
		//getRandomDec();
		//System.out.println("DD20150110000003".substring(10));
		//System.out.println(StringUtil.getStrSeqCode(false, "DD20150110000003", 6));
		System.out.println(Distance(Double.parseDouble("113.670916"), Double.parseDouble("34.81614"), Double.parseDouble("113.662867"), Double.parseDouble("34.706059")));
	}
}
