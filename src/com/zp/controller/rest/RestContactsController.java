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
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType;
import com.zp.entity.AbContacts;
import com.zp.entity.AbMerchant;
import com.zp.entity.SysUser;
import com.zp.entity.SysUserSubject;

/**
 * 人脉
 * 
 */
public class RestContactsController extends AbstractRestController {
	/**
	 * 人脉列表
	 */
	public void listFriends(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String uid = this.getPara("uid");
		Integer pi = this.getParaToInt("pi");
		Integer ps = this.getParaToInt("ps");
		if (pi == null || ps == null || pi < 1 || ps < 1 || StringUtils.isBlank(uid)) {
			formatInvalidParamResponse(resultMap);
		} else {
			List<Record> result = Db.find("select a.hy,a.gz,a.friendtype,a.friendid,c.mc as type from ab_contacts a " +
					" join  ab_subject  c on a.friendtype = c.id" +
					" where a.uid=?   limit ?,?",uid, getStart(pi, ps),ps);
			List<Record> data = new ArrayList<Record>();
			if(result!=null && result.size()>0){
				for(Record c : result){
					//1：商家，2：司机，3：用户
					if((c.get("friendtype")+"").startsWith("1020")){
						AbMerchant mer = AbMerchant.dao.findFirst("select * from ab_merchant where user_id=?",c.getStr("friendid"));
						if(mer!=null){
							c.set("name", mer.get("mc"));
							c.set("logo", mer.get("logo"));
							c.set("brief", mer.get("brief"));
							c.set("mobile", mer.get("mobile"));
							c.set("address", mer.get("xxdz"));
							c.set("city", mer.get("city_name"));
						}
					}else{
						SysUser mer = SysUser.dao.findById(c.getStr("friendid"));
						if(mer!=null){
							c.set("name", mer.get("mc"));
							c.set("logo", mer.get("logo"));
							c.set("mobile", mer.get("mobile"));
							c.set("city", mer.get("city_name"));
							c.set("brief", "");
					}
					}
						
					
					data.add(c);
				}
			}
			resultMap.put("data", data);
			resultMap.put("result", RESULT_SUCCESS);
		}
		renderJson(resultMap);
	}
	
	public void addFriend(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String uid = this.getPara("uid");
		String friendid = this.getPara("friendid");
		String friendtype = this.getPara("friendtype");
		//关系类型，1关注，2为好友
		String rtype = this.getPara("rtype");
		String content = this.getPara("content");
		if (StringUtils.isBlank(friendid)|| StringUtils.isBlank(friendtype) || StringUtils.isBlank(uid)||StringUtils.isBlank(rtype)) {
			formatInvalidParamResponse(resultMap);
		} else {
			//判断是否已添加
			String sql = "SELECT COUNT(1) as cont  FROM ab_contacts o WHERE  o.friendid =? AND uid=? ";
			if(StringUtils.equals("1", rtype)){
				sql = sql +" and gz = 1";
			}else{
				sql = sql +" and hy = 1";
			}
			Record r = Db.findFirst(sql,friendid,uid);
			if(r.getLong("cont")>0){
				String tmp = "update ab_contacts set ";
				if(StringUtils.equals("1", rtype)){
					tmp = tmp +" gz = 1";
				}else{
					tmp = tmp +" hy = 1";
				}
				tmp = tmp + "  where  friendid =? AND uid=? ";
				Db.update(tmp,friendid,uid);
				resultMap.put("result", RESULT_SUCCESS);
				resultMap.put("msg", "操作成功");
			}else{
				AbContacts contacts = new AbContacts();
				contacts.set("friendid", friendid);
				contacts.set("friendtype", friendtype);
				contacts.set("uid", uid);
				if(StringUtils.equals("1", rtype)){
					contacts.set("gz", "1");
				}else{
					contacts.set("hy", "1");
				}
				contacts.set("add_date", new Date());
				contacts.set("id",UUID.randomUUID().toString().replaceAll("-", ""));
				contacts.save();
				resultMap.put("result", RESULT_SUCCESS);
				resultMap.put("msg", "添加成功");
			}
			
		}
		renderJson(resultMap);
	}
	public void deleteFriend(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String uid = this.getPara("uid");
		String friendid = this.getPara("friendid");
		String rtype = this.getPara("rtype");
		try{
		if (StringUtils.isBlank(friendid)|| StringUtils.isBlank(uid)||StringUtils.isBlank(rtype)) {
			formatInvalidParamResponse(resultMap);
		} else {
			if(StringUtils.equals("1", rtype)){
				 Db.update("update  ab_contacts set gz=0 where  friendid =? AND uid=?" ,friendid,uid);
			}else{
				 Db.update("update  ab_contacts set hy=0 where  friendid =? AND uid=?" ,friendid,uid);
			}
			 	Db.update("delete from ab_contacts where  friendid =? AND uid=? and gz=0 and hy=0",friendid,uid);
			
				resultMap.put("result", RESULT_SUCCESS);
				resultMap.put("msg", "删除成功");
			
		}
		}catch (Exception e) {
			e.printStackTrace();
			resultMap.put("result", RESULT_FAIL);
			resultMap.put("msg", "删除失败");
		}
		renderJson(resultMap);
	}
	/**
	 * 查询所有人脉
	 */
	public void all(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String uid = this.getPara("uid");
		String type = this.getPara("type");
		String city = this.getPara("city");
		String collect = this.getPara("collect");
		String phonenum = this.getPara("phonenum");
		String current_city = this.getPara("current_city");
		String company_name = this.getPara("company_name");
		String hzzt  = this.getPara("hzzt");
		
		Integer pi = this.getParaToInt("pi");
		Integer ps = this.getParaToInt("ps");
		
		if(StringUtils.isNotEmpty(city)){
			String[] arr = city.split(",");
			StringBuffer sb = new StringBuffer();
			for(String str : arr){
				if(sb.length()>1){
					sb.append(" or ");
				}
				sb.append(" a.city_name like '%").append(str).append("%'");
			}
			city = sb.toString();
		}
		if(StringUtils.isNotEmpty(type)){
			String[] arr = type.split(",");
			StringBuffer sb = new StringBuffer();
			for(String str : arr){
//				if(StringUtils.equals("1", str)){
//					str = "105";
//				}
//				if(StringUtils.equals("2", str)){
//					str = "106";
//				}
//				if(StringUtils.equals("3", str)){
//					str = "107";
//				}
				if(sb.length()>1){
					sb.append(",");
				}
				sb.append("'").append(str).append("'");
			}
			type = sb.toString();
		}
		
	
		
		//0未合作1已合作
		if (  StringUtils.isBlank(uid)||pi ==null || ps ==null) {
			formatInvalidParamResponse(resultMap);
		} else {
			//类型
			StringBuffer sqltmp = new StringBuffer("select a.id as id,a.mc as name,a.mobile as mobile,a.logo as logo,c.mc as type,d.xxdz as address," +
					"d.brief as brief,c.id as ftype,a.role_id as rid from sys_user a  " +
					"join ab_subject c on a.zhlx = c.id " +
					"left join ab_merchant d on a.id = d.user_id  where 1=1 ");
			
			if(StringUtils.isNotEmpty(type)){
				sqltmp.append(" and c.id  in("+type+") ");
			}
			if(StringUtils.isNotEmpty(company_name)){
				sqltmp.append(" and d.mc like '%").append(company_name).append("%' ");
			}
			//合作状态
			if(StringUtils.isNotEmpty(hzzt)){
				if(StringUtils.equals(hzzt,"1")){
					sqltmp.append(" and  (select count(1) from ab_order where (xdrid='"+uid+"' and qdrid=a.id ) or (qdrid='"+uid+"' and xdrid=a.id ) ) >0 ");
				}else{
					sqltmp.append(" and  (select count(1) from ab_order where (xdrid='"+uid+"' and qdrid=a.id ) or (qdrid='"+uid+"' and xdrid=a.id ) ) <1 ");
					
				}
			}
			//城市
			if(StringUtils.isNotEmpty(city)){
				sqltmp.append( " and (").append(city).append(" )");
			}else if(StringUtils.isNotEmpty(current_city)){
				sqltmp.append( " and a.city_name = '").append(current_city).append("' ");
			}
			//mobile
			if(StringUtils.isNotEmpty(phonenum)){
				sqltmp.append(" and a.mobile like '%"+phonenum+"%'");
				
			}
			System.out.println(collect);
			if(StringUtils.equals("收藏", collect)){
				sqltmp.append(" and a.id in ( select e.friendid from ab_contacts e where e.uid='"+uid+"')");
			}else{
				sqltmp.append(" and a.id not in(select e.friendid from ab_contacts e where e.uid='"+uid+"')");
			}
			
			sqltmp.append("order by create_date desc limit ?,? ");
			List<Record> mer = Db.find(sqltmp.toString(),getStart(pi, ps),ps);
			
			resultMap.put("data", mer);
			Object obj =  resultMap.get("data");
			if(obj!=null){
				List<Record> list = (List<Record>)obj;
				for(Record  r : list){
					//查询好友情况
					Record abContacts = Db.findFirst("select * from ab_contacts where uid=? and friendid=?",uid,r.get("id"));
					if(abContacts == null){
						r.set("gz", "0");
						r.set("hy", "0");
					}else{
						r.set("gz", r.get("gz"));
						r.set("hy",  r.get("hy"));
					}
//					String rid = r.get("rid");
//					String typestr = r.get("type");
//					if(StringUtils.isEmpty(typestr)){
//						if(StringUtils.equals("105", rid)){
//							 r.set("type","商家");
//							 r.set("ftype","1");
//						}
//						if(StringUtils.equals("106", rid)){
//							r.set("type","司机");
//							 r.set("ftype","2");
//						}
//						if(StringUtils.equals("107", rid)){
//							r.set("type","货主会员");
//							 r.set("ftype","3");
//						}
//					}
				}
			}
			resultMap.put("msg", "查询成功");
			resultMap.put("result", RESULT_SUCCESS);
		}
		renderJson(resultMap);
	}
	public void random(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String uid = this.getPara("uid");
		String type = this.getPara("type");
		String city = this.getPara("city");
		String collect = this.getPara("collect");
		String phonenum = this.getPara("phonenum");
		String current_city = this.getPara("current_city");
		String company_name = this.getPara("company_name");
		String hzzt  = this.getPara("hzzt");
		Integer pi = this.getParaToInt("pi");
		Integer ps = this.getParaToInt("ps");
		
		if(StringUtils.isNotEmpty(city)){
			String[] arr = city.split(",");
			StringBuffer sb = new StringBuffer();
			for(String str : arr){
				if(sb.length()>1){
					sb.append(" or ");
				}
				sb.append(" a.city_name like '%").append(str).append("%'");
			}
			city = sb.toString();
		}
		if(StringUtils.isNotEmpty(type)){
			String[] arr = type.split(",");
			StringBuffer sb = new StringBuffer();
			for(String str : arr){
//				if(StringUtils.equals("1", str)){
//					str = "105";
//				}
//				if(StringUtils.equals("2", str)){
//					str = "106";
//				}
//				if(StringUtils.equals("3", str)){
//					str = "107";
//				}
				if(sb.length()>1){
					sb.append(",");
				}
				sb.append("'").append(str).append("'");
			}
			type = sb.toString();
		}
		
	
		
		//0未合作1已合作
		if (StringUtils.isBlank(uid)||pi ==null || ps ==null) {
			formatInvalidParamResponse(resultMap);
		} else {
			//类型
			StringBuffer sqltmp = new StringBuffer("select a.id as id,a.mc as name,a.mobile as mobile,a.logo as logo,c.mc as type,d.xxdz as address," +
					"d.brief as brief,c.id as ftype,a.role_id as rid from sys_user a  " +
					"join ab_subject c on a.zhlx = c.id " +
					"left join ab_merchant d on a.id = d.user_id  where 1=1 ");
			
			if(StringUtils.isNotEmpty(type)){
				sqltmp.append(" and c.id  in("+type+") ");
			}
			if(StringUtils.isNotEmpty(company_name)){
				sqltmp.append(" and d.mc like '%").append(company_name).append("%' ");
			}
			//城市
			if(StringUtils.isNotEmpty(city)){
				sqltmp.append( " and (").append(city).append(" )");
			}else if(StringUtils.isNotEmpty(current_city)){
				sqltmp.append( " and a.city_name = '").append(current_city).append("' ");
			}
			
			//合作状态
			if(StringUtils.isNotEmpty(hzzt)){
				if(StringUtils.equals(hzzt,"1")){
					sqltmp.append(" and  (select count(1) from ab_order where (xdrid='"+uid+"' and qdrid=a.id ) or (qdrid='"+uid+"' and xdrid=a.id ) ) >0 ");
				}else{
					sqltmp.append(" and  (select count(1) from ab_order where (xdrid='"+uid+"' and qdrid=a.id ) or (qdrid='"+uid+"' and xdrid=a.id ) ) <1 ");
					
				}
			}
			
			
			//mobile
			if(StringUtils.isNotEmpty(phonenum)){
				sqltmp.append(" and a.mobile like '%"+phonenum+"%'");
				
			}
			if(StringUtils.equals("收藏", collect)){
				sqltmp.append(" and a.id in ( select e.friendid from ab_contacts e where e.uid='"+uid+"')");
			}else{
				sqltmp.append(" and a.id not in(select e.friendid from ab_contacts e where e.uid='"+uid+"')");
			}
			
		
			System.out.println(sqltmp);
			Record con = Db.findFirst("select count(1) as con from ("+sqltmp.toString()+") f");
			Long count = con.getLong("con");
			sqltmp.append("order by login_date desc limit ?,? ");
			if(count>ps){
				pi = (int)((count.intValue()/ps)*Math.random());
				if(getStart(pi, ps)+ps>count){
					pi = count.intValue()/ps-1;
					
				}
				if(pi<1){
					pi=1;
				}
			}
			List<Record> mer = Db.find(sqltmp.toString(),getStart(pi, ps),ps);
			
			resultMap.put("data", mer);
			Object obj =  resultMap.get("data");
			if(obj!=null){
				List<Record> list = (List<Record>)obj;
				for(Record  r : list){
					//查询好友情况
					Record abContacts = Db.findFirst("select * from ab_contacts where uid=? and friendid=?",uid,r.get("id"));
					if(abContacts == null){
						r.set("gz", "0");
						r.set("hy", "0");
					}else{
						r.set("gz", r.get("gz"));
						r.set("hy",  r.get("hy"));
					}
//					String rid = r.get("rid");
//					String typestr = r.get("type");
//					if(StringUtils.isEmpty(typestr)){
//						if(StringUtils.equals("105", rid)){
//							 r.set("type","商家");
//							 r.set("ftype","1");
//						}
//						if(StringUtils.equals("106", rid)){
//							r.set("type","司机");
//							 r.set("ftype","2");
//						}
//						if(StringUtils.equals("107", rid)){
//							r.set("type","货主会员");
//							 r.set("ftype","3");
//						}
//					}
				}
			}
			resultMap.put("msg", "查询成功");
			resultMap.put("result", RESULT_SUCCESS);
		}
		renderJson(resultMap);
	}
}
