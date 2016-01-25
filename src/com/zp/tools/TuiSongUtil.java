package com.zp.tools;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.audience.Audience.Builder;
import cn.jpush.api.push.model.notification.Notification;

import com.jfinal.plugin.activerecord.Db;
import com.mysql.jdbc.StringUtils;
import com.zp.entity.AbNotice;
import com.zp.entity.AbOrder;
import com.zp.entity.AbTcExpressOrder;
public class TuiSongUtil {
	
	public static String USER_APPKEY =     "0ed27bf8c4ec45216430bf13";
	public static String USER_SECRET =     "2299e58dbb613156b9154eb6";
	public static String DRIVER_APPKEY =   "469e1e136c984f33dc2a28e9";
	public static String DRIVER_SECRET =   "27001ef040594c440f79fcc1";
	public static void main(String[] args) {
		notice_user("1", new String[]{"3120e6fa726341aab9365a5e3175ddd1"},false);
	}
	
	
	public static void notice_all(String dataid,String []userId,boolean isall) {
		notice_user(dataid, userId, isall);
		notice_dirver(dataid, userId, isall);
	}
	public static PushPayload notice_test(String dataid,String []userId,boolean isall) {
		Map<String, String> extras = new HashMap<String, String>();
		extras.put("id",dataid);
		extras.put("from","JPush");
		extras.put("type","notice");
		
		PushPayload payload = null;
		String type = "系统消息";
		String title = "你有新的消息啦";
		//1、物流状态，2、系统通知，3、及时聊天，4、客服服务
		if(!isall){
			payload =	PushPayload.newBuilder()
	        .setPlatform(Platform.all())
	        .setAudience(Audience.newBuilder()
	        .addAudienceTarget(AudienceTarget.alias(userId)).build())
	        .setNotification(
							Notification.android(type+"-"+title, "货的用户版",
									extras))
	        .build();
		}else{
			payload =	PushPayload.newBuilder()
	        .setPlatform(Platform.all())
	        .setAudience(Audience.all())
	        .setNotification(
							Notification.android(type+"-"+title, "货的用户版",
									extras))
	        .build();
		}
		
		return payload;
	}
	public static PushPayload notice_common(String dataid,String []userId,boolean isall) {
		Map<String, String> extras = new HashMap<String, String>();
		extras.put("id",dataid);
		extras.put("from","JPush");
		extras.put("type","notice");
		
		PushPayload payload = null;
		AbNotice notice = AbNotice.dao.findById(dataid);
		String type = "系统消息";
		String title = "你有新的消息啦";
		//1、物流状态，2、系统通知，3、及时聊天，4、客服服务
		if(notice!=null){
			title = notice.getStr("ntitle");
			if("1".equals(notice.getStr("ngroup"))){
				type = "物流状态";
			}
			if("2".equals(notice.getStr("ngroup"))){
				type = "系统通知";
			}
			if("3".equals(notice.getStr("ngroup"))){
				type = "及时聊天";
			}
			if("4".equals(notice.getStr("ngroup"))){
				type = "客服服务";
			}
		}
		if(!isall){
			payload =	PushPayload.newBuilder()
	        .setPlatform(Platform.all())
	        .setAudience(Audience.newBuilder()
	        .addAudienceTarget(AudienceTarget.alias(userId)).build())
	        .setNotification(
							Notification.android(type+"-"+title, "货的用户版",
									extras))
	        .build();
		}else{
			payload =	PushPayload.newBuilder()
	        .setPlatform(Platform.all())
	        .setAudience(Audience.all())
	        .setNotification(
							Notification.android(type+"-"+title, "货的用户版",
									extras))
	        .build();
		}
		
		return payload;
	}
	public static void notice_user(String noticeid,String []userId,boolean isall) {
		JPushClient jpushClient = new JPushClient(USER_SECRET,
				USER_APPKEY, 5);
		try {
//			PushPayload payload =  notice_common(noticeid, userId,isall);
			PushPayload payload =  notice_test(noticeid, userId,isall);
			PushResult result = jpushClient.sendPush(payload);
			System.out.println("Got result - " + result);

		} catch (APIConnectionException e) {
			System.out.println(e);

		} catch (APIRequestException e) {
			System.out.println(e);
		}
	}
	public static void notice_dirver(String noticeid,String []userId,boolean isall) {
		JPushClient jpushClient = new JPushClient(DRIVER_APPKEY,
				DRIVER_SECRET, 5);
		try {
			PushPayload payload =  notice_common(noticeid, userId,isall);
			PushResult result = jpushClient.sendPush(payload);
			System.out.println("Got result - " + result);
			
		} catch (APIConnectionException e) {
			System.out.println(e);
			
		} catch (APIRequestException e) {
			System.out.println(e);
			
		}
	}
	/**@orderId  向附近司机端推送网站和app下的订单号或者id
	 * @userid   存放附近司机的userid 
	 * 该方法写在 网站下单和app下单成功后，，推送的参数为订单的号，司机端获取订单号后，点击通知栏，根据订单号获取订单详情
	 */
	public static void Tuisong(String orderId,String []userId) {
		
		JPushClient jpushClient = new JPushClient("303fca96708a795d97b13a59",
				"4c5109862d9ef519ff48d370", 3);
		
//		JPushClient jpushIos = new JPushClient("0ed27bf8c4ec45216430bf13",
//				"2299e58dbb613156b9154eb6", 3);

		if(StringUtils.isNullOrEmpty(orderId)|| null==userId || userId.length == 0){
			return;
		}
		// PushPayload payload = buildPushObject_by_notification("cc",null);
		PushPayload payload = buildPushObject_ios_audienceMore_messageWithExtras(orderId,userId);
//		PushPayload payload = PushPayload.alertAll("附近有新的订单，请点击查看！");
		try {
			PushResult result = jpushClient.sendPush(payload);
//			payload.
			System.out.println("Got result - " + result);

		} catch (APIConnectionException e) {
			// Connection error, should retry later
			//LOG.error("Connection error, should retry later", e);
			System.out.println(e);

		} catch (APIRequestException e) {
			System.out.println(e);

		}
	}

	public static void Tuisong(String uid,String title,String id,String content) {
		JPushClient jpushClient = new JPushClient("303fca96708a795d97b13a59",
				"4c5109862d9ef519ff48d370", 3);
		Map<String, String> extras = new HashMap<String, String>();
		extras.put("id", id);
		 PushPayload payload = PushPayload.newBuilder()
         .setPlatform(Platform.all())
         .setNotification(
						Notification.android(title, "嘟嘟货的",
								extras))
         .setAudience(Audience.newBuilder()
         .addAudienceTarget(AudienceTarget.alias(new String[]{uid})).build())
         .setMessage(Message.newBuilder().setMsgContent(content).build())
         .build();
		try {
			PushResult result = jpushClient.sendPush(payload);
			System.out.println("Got result - " + result);
		} catch (APIConnectionException e) {
			e.printStackTrace();
		} catch (APIRequestException e) {
			e.printStackTrace();
		}
	}
	public static PushPayload buildPushObject_ios_audienceMore_messageWithExtras(String orderId,String []userId) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.newBuilder()
                .addAudienceTarget(AudienceTarget.alias(userId)).build())
                .setMessage(Message.newBuilder().setMsgContent(orderId).addExtra("from", "JPush").build())
                .build();
    }
	
	/**
	 * 查询20公里内的司机
	 * @param orderId
	 * @return
	 */
	public static String[] getSomeSjId(String orderId){
		try{
			String [] userIds = null;
			AbOrder order = AbOrder.dao.findById(orderId);
			BigDecimal lng;
			BigDecimal lat;
			if(order.getStr("sn").startsWith("TC")){
				AbTcExpressOrder ateo = AbTcExpressOrder.dao.findFirst("select * from ab_tc_express_order where sn = ?", order.getStr("sn"));
				lng = new BigDecimal(ateo.getStr("send_addr_jd"));
				lat = new BigDecimal(ateo.getStr("send_addr_wd"));
			}else{
				lng = new BigDecimal(order.getStr("sjjd"));
				lat = new BigDecimal(order.getStr("sjwd"));
			}
			
			BigDecimal jd_min = lng.subtract(new BigDecimal(0.2));
			BigDecimal jd_max = lng.add(new BigDecimal(0.2));
			BigDecimal wd_min = lat.subtract(new BigDecimal(0.18));
			BigDecimal wd_max = lat.add(new BigDecimal(0.18));
			String sql = "select a.id from sys_user a,(select b.* from (select * from ab_sj_position order by dqsj desc) b order by b.sjid ) c ";
			sql += "where a.id = c.sjid and c.zt=? and a.sjzt=?";
			sql += " and c.jd >= ?";
			sql += " and c.jd <= ?";
			sql += " and c.wd >= ?";
			sql += " and c.wd <= ?";
			List<String> userIdList = Db.query(sql, "2","2",jd_min.toString(),jd_max.toString(),wd_min.toString(),wd_max.toString());
			if(null!=userIdList && !userIdList.isEmpty()){
				userIds = new String[userIdList.size()];
				for(int i = 0; i < userIdList.size(); i++){
					userIds[i] = userIdList.get(i);
				}
			}
			return userIds;
		}catch(Exception e){
			System.out.println(e);
			return null;
		}
	}

}
