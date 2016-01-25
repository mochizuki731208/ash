package com.zp.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;

import com.zp.entity.SysConfig;
public class SmsMessage {
	private static Properties property = null;
	/**
	 * @author lijinwei
	 * @Date 2010-9-27 14:24:06
	 * @param mobile
	 * @param content
	 */
	public static String SendMessage(String mobile,String content) throws Exception {
		
		HttpURLConnection urlcon = null;
		String messageSend = "";
		String str_rtn = "";
		try {
			if (property == null) {
				InputStream inputStream = SmsMessage.class.getClassLoader().getResourceAsStream("config.properties");
				property = new Properties();
				property.load(inputStream);
			}
			messageSend = property.getProperty("MessageSend");
			// 检查短信设置是否开通
			if ("on".equals(messageSend.toLowerCase())) {
				// 替换变量中的数据
				content =URLEncoder.encode(content,"GBK");
				// 调用URL发送短信
				StringBuffer sb = new StringBuffer();
				sb.append("http://dxhttp.c123.cn/tx/?");
				sb.append("uid=500952350001");
				sb.append("&pwd=ca74b5c8af25f923a6dc0e58166b3725");
				sb.append("&mobile="+mobile);
				sb.append("&content="+content);
				System.out.println(sb.toString());
				URL url = new URL(sb.toString());
				urlcon = (HttpURLConnection) url.openConnection();
				urlcon.setRequestMethod("POST");
				urlcon.connect();
				// 发送
				BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
				// 返回发送结果
				str_rtn = in.readLine();
			} else {
				System.out.println("短信发送暂时关闭！");
			}
		} catch (MalformedURLException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			if ("on".equals(messageSend.toLowerCase())) {
				urlcon.disconnect();
			}
		}
		return str_rtn;
	}
	
	public static String getDomain() throws Exception {
		
		String str_rtn = "";
		try {
			if (property == null) {
				InputStream inputStream = SmsMessage.class.getClassLoader().getResourceAsStream("config.properties");
				property = new Properties();
				property.load(inputStream);
			}
			str_rtn = property.getProperty("DomainName");
			
		} catch (MalformedURLException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		}
		return str_rtn;
	}
	
	public static boolean isSendSms(String c){
		SysConfig vo = SysConfig.dao.findById("01");
		if(null==vo){
			return true;
		}else{
			String value = vo.getStr("c");
			if(null!= value && value.equals("1")){
				return false;
			}
		}
		return true;
	}
	public static void main(String[] args) throws Exception {
		//wangsheng123
		String lxrdh = "18751986953";
		String yzm = RandomUtil.createRandomPwd(6);//获取刘伟短信验证
		String content = "尊敬的用户" + lxrdh + ",您好。您的订单已发货，您可凭借验证码【" + yzm + "】进行收货确认，谢谢。";
		System.out.println(SmsMessage.SendMessage(lxrdh,content));
	}
}
