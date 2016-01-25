package com.zp.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.google.gson.JsonObject;
public class MapUtils {
	private static Properties property = null;
	/**
	 * @author lijinwei
	 * @Date 2010-9-27 14:24:06
	 * @param mobile
	 * @param content
	 */
	public static String getMapZb(String city,String address) throws Exception {
		HttpURLConnection urlcon = null;
		String str_rtn = "";
		try {
			// 替换变量中的数据
			String str_url = "http://api.map.baidu.com/geocoder/v2/?ak=9eO2SGTDwVgdkUCPP0jluRol&output=json";
			str_url+="&address="+URLEncoder.encode(address,"GBK");
			str_url+="&city="+URLEncoder.encode(city,"GBK");
			
//			System.out.println(str_url.toString());
			URL url = new URL(str_url.toString());
			urlcon = (HttpURLConnection) url.openConnection();
			urlcon.setRequestMethod("POST");
			urlcon.connect();
			// 发送
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			// 返回发送结果
			str_rtn = in.readLine();
//			System.out.println(str_rtn);
		} catch (Exception e) {
			throw e;
		} finally {
			urlcon.disconnect();
		}
		return str_rtn;
	}
	public static Map getLngLat(String city,String address) throws Exception{
		try{
			String str_rtn = MapUtils.getMapZb(city,address);
			Map<String,String> result = new HashMap<String,String>();
			str_rtn = str_rtn.replaceAll("\"", "");
			if(str_rtn.indexOf("{lng") != -1){
				int i = str_rtn.indexOf("{lng:");
				int j = str_rtn.indexOf("},");
				str_rtn = str_rtn.substring(i + 1, j);
				String [] arr = str_rtn.split(",");
				result.put("lng", arr[0].split(":")[1]);
				result.put("lat", arr[1].split(":")[1]);
			}
			System.out.println(result);
			return result;
		}catch(Exception e){
			return null;
		}
		
	}
	public static void main(String[] args) throws Exception {
		//wangsheng123
		MapUtils.getLngLat("", "南京市新街口万达影城(万达广场店)");
	}
}
