package com.zp.tools.mail;

import java.util.Date;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.zp.entity.SysConfig;

/**
 * 简单邮件（不带附件的邮件）发送器 http://www.bt285.cn BT下载
 */
public class SimpleMailSender {
	/**
	 * 以文本格式发送邮件
	 * 
	 * @param mailInfo
	 *            待发送的邮件的信息
	 */
	public boolean sendTextMail(MailSenderInfo mailInfo) {
		// 判断是否需要身份认证
		MyAuthenticator authenticator = null;
		Properties pro = mailInfo.getProperties();
		if (mailInfo.isValidate()) {
			// 如果需要身份认证，则创建一个密码验证器
			authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
		}
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		Session sendMailSession = Session.getDefaultInstance(pro, authenticator);
		try {
			// 根据session创建一个邮件消息
			Message mailMessage = new MimeMessage(sendMailSession);
			// 创建邮件发送者地址
			Address from = new InternetAddress(mailInfo.getFromAddress());
			// 设置邮件消息的发送者
			mailMessage.setFrom(from);
			// 创建邮件的接收者地址，并设置到邮件消息中
			Address to = new InternetAddress(mailInfo.getToAddress());
			mailMessage.setRecipient(Message.RecipientType.TO, to);
			// 设置邮件消息的主题
			mailMessage.setSubject(mailInfo.getSubject());
			// 设置邮件消息发送的时间
			mailMessage.setSentDate(new Date());
			// 设置邮件消息的主要内容
			String mailContent = mailInfo.getContent();
			mailMessage.setText(mailContent);
			// 发送邮件
			Transport.send(mailMessage);
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * 以HTML格式发送邮件
	 * 
	 * @param mailInfo
	 *            待发送的邮件信息
	 */
	public static boolean sendHtmlMail(MailSenderInfo mailInfo) {
		// 判断是否需要身份认证
		MyAuthenticator authenticator = null;
		Properties pro = mailInfo.getProperties();
		// 如果需要身份认证，则创建一个密码验证器
		if (mailInfo.isValidate()) {
			authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
		}
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		Session sendMailSession = Session.getDefaultInstance(pro, authenticator);
		try {
			// 根据session创建一个邮件消息
			Message mailMessage = new MimeMessage(sendMailSession);
			// 创建邮件发送者地址
			Address from = new InternetAddress(mailInfo.getFromAddress());
			// 设置邮件消息的发送者
			mailMessage.setFrom(from);
			// 创建邮件的接收者地址，并设置到邮件消息中
			Address to = new InternetAddress(mailInfo.getToAddress());
			// Message.RecipientType.TO属性表示接收者的类型为TO
			mailMessage.setRecipient(Message.RecipientType.TO, to);
			// 设置邮件消息的主题
			mailMessage.setSubject(mailInfo.getSubject());
			// 设置邮件消息发送的时间
			mailMessage.setSentDate(new Date());
			// MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
			Multipart mainPart = new MimeMultipart();
			// 创建一个包含HTML内容的MimeBodyPart
			BodyPart html = new MimeBodyPart();
			// 设置HTML内容
			html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
			mainPart.addBodyPart(html);
			// 将MiniMultipart对象设置为邮件内容
			mailMessage.setContent(mainPart);
			// 发送邮件
			Transport.send(mailMessage);
			return true;
		} catch (MessagingException ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 发送邮件认证信息
	 * @param toaddress
	 * @param ctx
	 * @return
	 */
	public static boolean sendCertifiMail(String toaddress,String ctx){
		//獲取配置參數
		SysConfig config =SysConfig.dao.findById("04");
		String site = config.getStr("c1");
		String subject = config.getStr("c2");
		String content = site+ctx+"/ab/user/certiEmailUpdate/";
		return sendMail(toaddress, subject, content, true);
	}

	@SuppressWarnings("static-access")
	public static boolean sendMail(String toaddress,String subject,String content,boolean isText){
		SysConfig config =SysConfig.dao.findById("03");
		
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost(config.getStr("c1"));
		mailInfo.setMailServerPort(config.getStr("c2"));
		mailInfo.setValidate(true);
		mailInfo.setUserName(config.getStr("c3"));
		mailInfo.setPassword(config.getStr("c4"));// 您的邮箱密码
		mailInfo.setFromAddress(config.getStr("c3"));
		mailInfo.setToAddress(toaddress);
		mailInfo.setSubject(subject);
		mailInfo.setContent(content);
		
		// 这个类主要来发送邮件
		SimpleMailSender sms = new SimpleMailSender();
		boolean flag = false;
		if(isText)
			flag = sms.sendTextMail(mailInfo);// 发送文体格式
		else
			flag = sms.sendHtmlMail(mailInfo);// 发送html格式
		
		return flag;
	}
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) {
		// 这个类主要是设置邮件
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost("smtp.163.com");
		mailInfo.setMailServerPort("25");
		mailInfo.setValidate(true);
		mailInfo.setUserName("lijinweiatzzhu@163.com");
		mailInfo.setPassword("25wang");// 您的邮箱密码
		mailInfo.setFromAddress("lijinweiatzzhu@163.com");
		mailInfo.setToAddress("406091127@qq.com");
		mailInfo.setSubject("设置邮箱标题");
		mailInfo.setContent("http://localhost:8888/ash/ab/user/certiEmailUpdate/8d89f06162e04707b59e22fc6ecc6d67-a9e7be6acb454eb995c8398b6a8cf9db");
		// 这个类主要来发送邮件
		
		SimpleMailSender sms = new SimpleMailSender();
		sms.sendTextMail(mailInfo);// 发送文体格式
		//sms.sendHtmlMail(mailInfo);// 发送html格式
	}
}