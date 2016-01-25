package com.zp.tools;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import com.mysql.jdbc.StringUtils;
import com.zp.entity.AbMerchantPrint;
import com.zp.entity.AbOrder;
import com.zp.entity.AbOrderItem;

public class PrintUtils {
	
	public  static String partner="868";//用户id
	public	static String machine_code="3400453686";//打印机终端号
	public	static String apiKey="30b5d06f11d821615be61cf4f87ec67c40d08ad8";//API密钥
	public	static String mKey="puv7yqtepemn";//打印机密钥
	
	public static void main(String[] args) {
		//pmRequest();//查询打印机状态
		sendRequest("测试打印123");//打印消息
	}
	//将订单信息打印出来
	public static String forMatterMsg(String orderId){
		if(StringUtils.isNullOrEmpty(orderId)){
			return "false";
		}else{
			AbOrder order = AbOrder.dao.findById(orderId);
			if(null == order || null == order.getStr("mid") || order.getStr("mid").length() == 0){
				return "";
			}else{
				AbMerchantPrint amp = AbMerchantPrint.dao.findFirst("select * from ab_merchant_print where mid=?", order.getStr("mid"));
				if(null == amp || "0".equals(amp.getStr("iswork"))){
					return "";
				}
			}
		}
		try{
			//总订单
			AbOrder order = AbOrder.dao.findById(orderId);
			//子订单。各商品详细
			List<AbOrderItem> items = AbOrderItem.dao.find("select * from ab_order_item where orderid=?", orderId);
			StringBuffer msg = new StringBuffer();
			msg.append("\n      " + order.getStr("mname"));//商家名称
			msg.append("      \n      \n");
			msg.append("订单号：").append(order.getStr("sn"));
			msg.append("      \n");
			msg.append("订购日期：").append(order.getStr("xdsj"));
			msg.append("      \n");
			msg.append("收货人：").append(order.getStr("lxr"));
			msg.append("      \n");
			msg.append("收货人电话：").append(order.getStr("lxrdh"));
			msg.append("      \n");
			msg.append("收货地址：").append(order.getStr("shdz"));
			msg.append("      \n");
			msg.append("名称         单价(元)      数量");
			msg.append("      \n");
			msg.append("--------------------------------");
			msg.append("      \n");
			int totalNums = 0;
			for(AbOrderItem item : items){
				totalNums += item.getInt("quantity");
				msg.append(generateFormat(item.getStr("itemname"), item.getBigDecimal("price") + "", item.getInt("quantity") + " "));
				msg.append("\n");
			}
			msg.append("--------------------------------");
			msg.append("      \n");
			msg.append("小计：").append(totalNums + "件");
			msg.append("      ");
			msg.append("总价：").append(order.getBigDecimal("spzj"));
			msg.append("      \n");
			msg.append("其他费用：").append(order.getBigDecimal("cjlfjf"));
			msg.append("      \n");
			msg.append("优惠金额：").append(order.getBigDecimal("yhje"));
			msg.append("      \n");
			msg.append("订单总金额：").append(order.getBigDecimal("ddzje"));
			msg.append("      \n");
			String zffs = order.getStr("zffs");
			System.out.println("支付方式："  + zffs);
			if("1".equals(zffs)){
				msg.append("支付方式：").append("充值直扣");
			}
			if("2".equals(zffs)){
				msg.append("支付方式：").append("支付宝");
			}
			if("3".equals(zffs)){
				msg.append("支付方式：").append("货到付款");
			}
			if("4".equals(zffs)){
				msg.append("支付方式：").append("无支付");
			}
			System.out.println("打印的内容：\t\n" + msg.toString());
			return msg.toString();
		}catch(Exception e){
			return "false";
		}
	}
	//打印机是否在线接口0是离线1是在线2是缺纸
	public static boolean pmRequest(){
		Map<String,String> params=new HashMap<String,String>();
		params.put("machine_code", machine_code);
		params.put("partner", partner);
		params.put("time", ((int)System.currentTimeMillis())+"");
		String sign=signRequest(params);
		params.put("sign", sign);
		HttpClient httpClient = new HttpClient();
		PostMethod post = new PostMethod("http://open.10ss.net:8888/getstatus.php"); 
		
		for(Map.Entry<String, String> entry : params.entrySet()){    
			post.addParameter(entry.getKey(),entry.getValue());
		}
		
		HttpMethodParams param = post.getParams();  
        param.setContentCharset("UTF-8");  
		try {
			httpClient.executeMethod(post);
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//如果返回200，表明成功
		if(post.getStatusCode()==200){
			try {
				String result;
				result=post.getResponseBodyAsString();
				System.out.println(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}
		else{
			System.out.println("打印失败！");
			return false;
		}

	}
	//打印机打印消息
	public static boolean sendRequest(String content){
		if(StringUtils.isNullOrEmpty(content)){
			return false;
		}
		Map<String,String> params=new HashMap<String,String>();
		params.put("partner", partner);
		params.put("machine_code", machine_code);
		Calendar ca = Calendar.getInstance();
		params.put("time", ca.getTimeInMillis()+"");
		String sign=signRequest(params);
		params.put("sign", sign);
		params.put("content",content);
		HttpClient httpClient = new HttpClient();
		PostMethod post = new PostMethod("http://open.10ss.net:8888"); 
		
		for(Map.Entry<String, String> entry : params.entrySet()){    
			post.addParameter(entry.getKey(),entry.getValue());
		}
		
		HttpMethodParams param = post.getParams();  
        param.setContentCharset("UTF-8");  
		
		try {
			httpClient.executeMethod(post);
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//如果返回200，表明成功
		if(post.getStatusCode()==200){
			try {
				String result;
				result=post.getResponseBodyAsString().replace("{", "").replace("}", "").replaceAll("\"", "");
				System.out.println(result);
				String[] strArr = result.split(",");
				Map<String,Object> map = new HashMap<String,Object>();
				for(String str : strArr){
					String[] temp  = str.split(":");
					map.put(temp[0], temp[1]);
				}
				System.out.println(map);
				if(map.get("state").equals("1")){//数据已经发送到客户端
					System.out.println("打印成功");
					return true;
				}else{
					System.out.println("打印失败,返回值："+result);
					return false;
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}
		else{
			System.out.println("打印失败！");
			return false;
		}

	}
	/**
	 * 打印签名
	 * @param params
	 * @return
	 */
	public static String signRequest(Map<String,String> params){
		Map<String,String> sortedParams=new TreeMap<String,String>();
		sortedParams.putAll(params);
		Set<Map.Entry<String,String>> paramSet=sortedParams.entrySet();
		StringBuilder query=new StringBuilder();
		query.append(apiKey);
		for (Map.Entry<String, String> param:paramSet) {
			query.append(param.getKey());
			query.append(param.getValue());
		}
		query.append(mKey);
		String encryptStr=MD5.GetMD5Code(query.toString()).toUpperCase();
		return encryptStr;
	}
	
	//生成位长度
	public static int getLength(String str){
		int t1 = 0;
		try{
			String strNew = new String(str.getBytes(), "UTF-8");
			for(int i = 0; i < strNew.length() - 1; i++){
				if(strNew.substring(i, i + 1).matches("[\u4e00-\u9fa5]")){
					t1 +=2;
				}else{
					t1 +=1;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return t1;
		}
		return t1;
	}
	
	public static String generateFormat(String p1,String p2,String p3){
		String msg = p1;
		int t1 = 14;
		int t2 = 18;
		t1 = t1 - getLength(p1);
		t2 = t2 - getLength(p2) - getLength(p3);
		for(int j = 0; j < t1; j++){
			msg += " ";
		}
		msg += p2;
		for(int j = 0; j < t2; j++){
			msg += " ";
		}
		msg += p3;
		return msg;
	}
}
