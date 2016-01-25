package com.zp.controller.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType;
import com.zp.entity.AbContacts;
import com.zp.entity.AbMerchant;
import com.zp.entity.AbNotice;
import com.zp.entity.SysUser;
import com.zp.entity.SysUserSubject;
import com.zp.tools.TuiSongUtil;

/**
 * 系统消息
 * 
 */
public class RestNoticeController extends AbstractRestController {
	/**
	 * 发送信息
	 */
	public void send(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		//发送人
		String uid = this.getPara("uid");
		//所属分组
		String ngroup = this.getPara("group");
		//是否需要极光推送
		String is_jpush = this.getPara("is_jpush");
		//接收人
		String revid = this.getPara("revid");
		//通知标题
		String title = this.getPara("title");
		//通知内容
		String content = this.getPara("content");
		//接收人
		String receive_name = this.getPara("receive_name");
		
		if (StringUtils.isBlank(uid) || StringUtils.isBlank(revid)|| StringUtils.isBlank(title) || StringUtils.isBlank(content)|| StringUtils.isBlank(ngroup)) {
			formatInvalidParamResponse(resultMap);
		} else {
			AbNotice notice = new AbNotice();
			notice.set("revid", revid);
			notice.set("ntitle", title);
			notice.set("id", UUID.randomUUID().toString().replaceAll("-",""));
			notice.set("ngroup", ngroup);
			notice.set("is_jpush", is_jpush);
			notice.set("ncontent", content);
			notice.set("status", "0");
			notice.set("uid", uid);
			notice.set("receive_name", receive_name);
			notice.set("ndate", new Date());
			
			if(notice.save()){
				if(StringUtils.equals("1", is_jpush)){
					//推送消息
					TuiSongUtil.notice_all(notice.getStr("id"), new String[]{revid}, false);
				}
				resultMap.put("result", RESULT_SUCCESS);
				resultMap.put("msg", "发送成功");
			}else{
				resultMap.put("result", RESULT_FAIL);
				resultMap.put("msg", "发送失败");
			}
		}
		
		renderJson(resultMap);
	}
	/**
	 * 读取消息列表
	 */
	public void list(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		//送发人
		String uid = this.getPara("uid");
		//所属分组
		String ngroup = this.getPara("group");
		//通知内容
		String content = this.getPara("content");
		String status = this.getPara("status");
		Integer pi = this.getParaToInt("pi");
		Integer ps = this.getParaToInt("ps");
		if (pi == null || ps == null || pi < 1 || ps < 1 || StringUtils.isBlank(uid)) {
			formatInvalidParamResponse(resultMap);
		} else {
			String sql = "select id,send_name,status,ngroup,ncontent,ntitle,ndate,nurl from ab_notice where   revid='"+uid+"'";
			
			if(StringUtils.isNotEmpty(status)){
				sql = sql +" and status='"+status+"'";
			}else{
				sql = sql +" and status !='-1'";
			}
			if(StringUtils.isNotEmpty(ngroup)){
				sql = sql +" and ngroup='"+ngroup+"'";
			}
			if(StringUtils.isNotEmpty(content)){
				sql = sql +" and ncontent like '%"+content+"%'";
			}
			sql = sql + " order by ndate desc limit ?,?";
			System.out.println(sql);
			List<Record> data = Db.find(sql,getStart(pi, ps),ps);
			resultMap.put("data", data);
			resultMap.put("result", RESULT_SUCCESS);
		}
		renderJson(resultMap);
	}
	/**
	 * 设置已读，删除消息
	 */
	public void update(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String uid = this.getPara("uid");
		String id = this.getPara("id");
		String status = this.getPara("status");
		if (StringUtils.isBlank(uid)||StringUtils.isBlank(id)||StringUtils.isBlank(status)) {
			formatInvalidParamResponse(resultMap);
		} else {
			String[] arr = id.split(",");
			for(String str : arr){
				String sql = "update ab_notice set status =? where id=? and revid=?";
				Db.update(sql, status,str,uid);
			}
			resultMap.put("result", RESULT_SUCCESS);
			resultMap.put("msg", "操作成功");
		}
		renderJson(resultMap);
	}
	public void details(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String uid = this.getPara("uid");
		String id = this.getPara("id");
		if (StringUtils.isBlank(uid)||StringUtils.isBlank(id)) {
			formatInvalidParamResponse(resultMap);
		} else {
			AbNotice notice = AbNotice.dao.findById(id);
			if(notice!=null && StringUtils.equals(uid, notice.getStr("revid"))){
				resultMap.put("notice", notice);
				resultMap.put("result", RESULT_SUCCESS);
			}else{
				resultMap.put("result", RESULT_FAIL);
				resultMap.put("msg", "数据获取失败");

			}
		}
		renderJson(resultMap);
	}
}
