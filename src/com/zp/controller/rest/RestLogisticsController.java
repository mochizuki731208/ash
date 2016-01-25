package com.zp.controller.rest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.xvolks.jnative.misc.basicStructures.DOUBLE;

import com.alibaba.druid.sql.dialect.db2.visitor.DB2ASTVisitor;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.sun.corba.se.impl.encoding.TypeCodeOutputStream;
import com.zp.controller.ab.AbTcContrller;
import com.zp.entity.AbCityarea;
import com.zp.entity.AbDriverPosition;
import com.zp.entity.AbFmcar;
import com.zp.entity.AbFmcarOrder;
import com.zp.entity.AbMerchant;
import com.zp.entity.AbMerchantImg;
import com.zp.entity.AbMerchantLogimg;
import com.zp.entity.AbMerchantLogistics;
import com.zp.entity.AbOrder;
import com.zp.entity.AbOrderItemchargeoff;
import com.zp.entity.AbOrderPosition;
import com.zp.entity.AbOrderQuote;
import com.zp.entity.AbOrderSms;
import com.zp.entity.AbOrderStateImg;
import com.zp.entity.AbSjPosition;
import com.zp.entity.AbSubject;
import com.zp.entity.AbTcExpressOrder;
import com.zp.entity.AbTcExpressOrderBaojia;
import com.zp.entity.SysUser;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.tools.AutoQuoteTimer;
import com.zp.tools.DateUtil;
import com.zp.tools.RandomUtil;
import com.zp.tools.SmsMessage;
import com.zp.tools.StringUtil;

public class RestLogisticsController extends AbstractRestController {
	static Map<String,String> TYPE = new HashMap<String, String>();
	static{
		TYPE.put("10201000", "同城物流");
		TYPE.put("10201001", "物流专线");
		TYPE.put("10201002", "搬家公司");
		TYPE.put("10201003", "跑腿公司");
	}
	/**
	 * 
	 */
	public void searchLogistics() {
		keepPara();
		Map<String, Object> resultMap = new HashMap<String, Object>();		
		try {
			// 获取分页信息
			Integer pi = this.getParaToInt("pi");
			Integer ps = this.getParaToInt("ps");
			if(pi==null || ps==null) {
				//检查参数是否合法			
				resultMap.put("result", RESULT_FAIL);
				resultMap.put("msg", RESULT_INVALID_PARAM);
			} else {
				PageUtil listpage = DbPage.paginate(getStart(pi, ps), ps, getSQL());
				listpage.setCurPage(pi);
				resultMap.put("data", listpage);
	
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(resultMap);
	}
	public void getLogi() {
		Map<String, Object> resultMap = new HashMap<String, Object>();		
		try {
		List<AbSubject> list = AbSubject.dao.find("select mc from ab_subject where p_id=?", 1020);
		resultMap.put("data", list);
		}catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(resultMap);
	}
	private String getSQL() {
		String from = getParaTrans("from");
		String to = getParaTrans("to");
		String loginame = getParaTrans("loginame");
		String keywords = getParaTrans("keywords");
		//商圈
		String cityname = getPara("cityname");
		//服务分类
		String[] subject = getParaValues("subject");
		//默认排序
		String mrorder = getParaTrans("mrorder");
		//销量排序
		String zxlorder = getParaTrans("zxlorder");
		//价格排序
		String avgpriceorder = getParaTrans("avgpriceorder");
		//好评排序
		String hpsorder = getParaTrans("hpsorder");
		
		//搜索物流企业
		String sql = getSelect();
		String sqlfromto = "select g.lid from ab_merchant_logline as g where 1=1";
		boolean bfromto = false;
		if(from!=null && from.length() > 0) {
			sqlfromto += " and g.from='" + from + "'";
			bfromto = true;
		}
		if(to!=null && to.length() > 0) {
			sqlfromto += " and g.to='" + to + "'";
			bfromto = true;
		}
		if(bfromto) sql += " and w.id in (" + sqlfromto + ") ";
		if(loginame!=null && loginame.length() > 0) sql += " and m.mc like '%" + loginame.replace(" ", "%") + "%'";
		if(keywords!=null && keywords.length() > 0) sql += " and w.profile like '%" + keywords.replace(" ", "%") + "%'";
		if(cityname!=null && cityname.length() > 0) sql += " and m.city_name like '%" + cityname + "%' ";
		if(subject!=null && subject.length > 0) sql += " and m.subject_id IN (" + trans(subject) + ") ";
		sql += " order by ";
		if(mrorder!=null && mrorder.length() > 0)  sql += " m.seq_num, ";
		if(zxlorder!=null && zxlorder.length() > 0) sql += " m.zxl " + zxlorder + ", ";
		if(avgpriceorder!=null && avgpriceorder.length() > 0) sql += " m.avgprice " + avgpriceorder + ", ";
		if(hpsorder!=null && hpsorder.length() > 0) sql += " m.hps " + hpsorder + ", ";
		sql += " m.id ";
		return sql;
	}
	/**
	 * 防止参数的sql注入  
	 * @param name
	 * @return
	 */
	private String getParaTrans(String name) {
		String param  = this.getPara(name);
		if(param!=null && param.trim().length() > 0) {
			return TransactSQLInjection(param);
		}
		return null;
	}
	/**  
	* 防止sql注入  
	*  
	* @param sql  
	* @return  
	*/ 
	private String TransactSQLInjection(String sql) {  
		return sql.replaceAll(".*([';]+|(--)+).*", " ");  
	} 
	private String trans(String params[]) {
		String res = "";
		if(params==null) return res;
		for (String str : params) {
			res += "," + str;
		}
		return res.substring(1);
	}
	private String getSelect() {
		return "select m.mc, m.city_name, m.img_url, m.thumbnail_url, m.logo, m.lng, m.lat, " +
				" m.mobile, m.comments_total, m.hps, (m.hps/m.comments_total) as score, w.* " +
				" from ab_merchant AS m LEFT JOIN ab_merchant_logistics AS w ON m.id = w.mid " +
				" where m.check_status='1' and w.check_status='1' and m.is_display='1' " +
				" and m.subject_id like '1020%' ";
				//"and m.user_id IN (select userid from sys_user_type where usertype=3) ";
	}
	/***
	 * 物流公司列表
	 */
	public void companies() {
				keepPara();
				Map<String, Object> resultMap = new HashMap<String, Object>();		
				try {
					// 获取分页信息
					Integer pi = this.getParaToInt("pi");
					Integer ps = this.getParaToInt("ps");
					
					if(pi==null || ps==null) {
						//检查参数是否合法			
						resultMap.put("result", false);
						resultMap.put("info", RESULT_INVALID_PARAM);
					} else {
						String sql = "SELECT c.* ,l.servhotline as phone,l.id as lid FROM  ab_merchant c join ab_merchant_logistics l on l.mid = c.id where 1=1 and l.check_status ='1' ";
						
						if(StringUtils.isNotEmpty(getPara("companyName"))){
							sql = sql + " and c.mc like '%"+getPara("companyName")+"%'";
						}
						PageUtil listpage = DbPage.paginate(getStart(pi, ps), ps, sql);
						List<Record> result =  listpage.getList();
						List<Record> data =  new ArrayList<Record>();
						if(result!=null && result.size()>0){
							for(Record record : result){
								Record tmp = new Record();
								tmp.set("name", getStr("mc", record));
								tmp.set("id", getStr("lid", record));
								tmp.set("subjectType", TYPE.get(getStr("subject_id", record)));

								tmp.set("icon", getStr("logo", record).length()<1?"wlgs_default.jpg":getStr("logo", record));
								tmp.set("address", getStr("xxdz", record));
								
								tmp.set("phoneNum", getStr("phone", record));
								tmp.set("services", getStr("transtype", record));
								tmp.set("scope", getStr("mainline", record));
								
								
								List<Record> logline = Db.find("select a.* from ab_merchant_logline a join ab_merchant_logistics b on a.lid = b.id   where b.mid='"+record.getStr("id")+"'");
								
								Map<String,String> map = new HashMap<String, String>();
								List<Record> list = new ArrayList<Record>();
								if(logline!=null && logline.size()>0){
									for(Record r :logline){
										Record line = new Record();
										if(r.get("from")!=null){
											map.put(r.getStr("from"),r.getStr("from"));
											
										}
										if(r.get("to")!=null){
											map.put(r.getStr("to"),r.getStr("to"));
										}
										line.set("path",getStr("from",r)+"到"+getStr("to",r));
										line.set("hornours", getStr("from_time",r));
										line.set("comments", getStr("to_time",r));
										line.set("weight_price", r.getFloat("weight_price")==null?0.0: r.getFloat("weight_price"));
										line.set("volumn_price", r.getFloat("volumn_price")==null?0.0: r.getFloat("volumn_price"));
										list.add(line);
									}
								}
								StringBuffer sb = new StringBuffer();
								for(String str : map.keySet()){
									sb.append(",").append(str);
								}
								tmp.set("pathes", list);
								if(sb.length()>1){
									
									tmp.set("scope",sb.substring(1));
								}else{
									
									tmp.set("scope",sb.toString());
								}
								
								data.add(tmp);
							}
						}
						listpage.setCurPage(pi);
						resultMap.put("companies", data);
						resultMap.put("result", true);
						resultMap.put("info", "查询成功");
			
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
				renderJson(resultMap);
		
	}
	/***
	 * 物流公司详情
	 */
	public void companyInfo() {
		keepPara();
		Map<String, Object> resultMap = new HashMap<String, Object>();		
		try {
			String id = this.getPara("id");
			
			if(null==id) {
				//检查参数是否合法			
				resultMap.put("result", false);
				resultMap.put("info", RESULT_INVALID_PARAM);
			} else {
						AbMerchant	record = AbMerchant.dao.findById(id);
						Record tmp = new Record();
						tmp.set("icon", record.getStr("img_url") == null?"wlgs_default.jpg" : record.getStr("img_url"));
						tmp.set("name", record.getStr("mc")==null?"":record.getStr("mc"));
						tmp.set("id", record.getStr("id"));
						tmp.set("address", record.getStr("recaddress") ==null?"":record.getStr("recaddress"));
						tmp.set("services", record.getStr("transtype"));
						tmp.set("subjectType", TYPE.get(record.getStr("subject_id")));
						//涉及城市
						List<Record> logline = Db.find("select a.* from ab_merchant_logline a join ab_merchant_logistics b on a.lid = b.id   where b.mid='"+record.getStr("id")+"'");
						
						Map<String,String> map = new HashMap<String, String>();
						List<Record> list = new ArrayList<Record>();
						if(logline!=null && logline.size()>0){
							for(Record r :logline){
								Record line = new Record();
								if(r.get("from")!=null){
									map.put(getStr("from",r),getStr("from",r));
									
								}
								if(r.get("to")!=null){
									map.put(getStr("to",r),getStr("to",r));
								}
								line.set("path", getStr("from",r)+"到"+getStr("to",r));
								line.set("hornours",getStr("from_time",r));
								line.set("comments", getStr("to_time",r));
								list.add(line);
							}
						}
						StringBuffer sb = new StringBuffer();
						for(String str : map.keySet()){
							sb.append(",").append(str);
						}
						tmp.set("pathes", list);
						if(sb.length()>1){
							tmp.set("scope",sb.substring(1));
						}else{
							tmp.set("scope",sb.toString());
						}
						//主要路线
						tmp.set("path", record.getStr("mainline"));
						tmp.set("contactName", record.getStr("chief"));
						tmp.set("phoneNum", record.getStr("mobile"));
						//info 简介
						tmp.set("info", record.getStr("profile"));
						resultMap.put("companyInfo", tmp);
						resultMap.put("result", true);
						resultMap.put("info", "查询成功");
				
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(resultMap);
		
	}
	/***
	 * 
	 */
	public void services() {
		keepPara();
		Map<String, Object> resultMap = new HashMap<String, Object>();		
		try {
				//涉及城市
//				List<Record> logline = Db.find(;
				String sql = "SELECT *  FROM ab_merchant_logline a  JOIN ( SELECT b.id AS cid, c.mc AS NAME,b.recaddress as recaddress ,b.disaddress as disaddress, b.servhotline as servhotline FROM  ab_merchant_logistics b   JOIN ab_merchant c ON c.id = b.mid ";
				
				if(StringUtils.isNotEmpty(getPara("companyName"))){
					sql = sql + "c.mc like '%"+getPara("companyName")+"%'";
				}
				sql = sql + " ) d ON a.lid = d.cid  where 1=1 ";
				if(StringUtils.isNotEmpty(getPara("from_time"))){
					sql = sql + " and a.from_time >= '"+getPara("from_time")+"'";
				}
				if(StringUtils.isNotEmpty(getPara("from"))){
					List<Record> list = Db.find("SELECT id,mc from ab_cityarea where type_id=0 and id like '"+getPara("from")+"%'");
					StringBuffer cities = new StringBuffer();
					if(list!=null && list.size()>0){
						for(Record r : list){
							if(cities.length()>1){
								cities.append(",");
							}
							cities.append("'"+r.getStr("mc")+"'");
						}
					}
					
					if(cities.length()>1){
						sql = sql + " and a.from  in ("+cities.toString()+")";
					}
					
				}
				if(StringUtils.isNotEmpty(getPara("to"))){
					
					List<Record> list = Db.find("SELECT id,mc from ab_cityarea where type_id=0 and id like '"+getPara("to")+"%'");
					StringBuffer cities = new StringBuffer();
					if(list!=null && list.size()>0){
						for(Record r : list){
							if(cities.length()>1){
								cities.append(",");
							}
							cities.append("'"+r.getStr("mc")+"'");
						}
					}
					
					if(cities.length()>1){
				
						
						AbCityarea city = AbCityarea.dao.findById(getPara("to"));
						Record r = Db.findFirst("select count(1) as con from ("+sql+" and a.to in ("+cities.toString()+"))");
						if(r.getLong("con")<1 && city.getInt("ccm")==2){
							AbCityarea p = AbCityarea.dao.findById(city.getStr("p_id"));
							cities.append(",'"+p.getStr("mc")+"'");
							sql = sql + " and a.to in ("+cities.toString()+")";
						}else{
							sql = sql + " and a.to in ("+cities.toString()+")";
						}
					}
					
				}
				if(StringUtils.isNotEmpty(getPara("to_time"))){
					sql = sql + " and a.to_time >= '"+getPara("to_time")+"'";
				}
				
				if(StringUtils.isNotEmpty(getPara("order_by"))){
					String sort = getPara("sort");
					if(StringUtils.isEmpty(sort)){
						sort = " asc";
					}
					sql = sql + "order by "+getPara("order_by")+" "+ sort;
				}
				
				System.out.println(sql);
				List<Record> logline = Db.find(sql);
				List<Record> list = new ArrayList<Record>();
				if(logline!=null && logline.size()>0){
					for(Record r :logline){
						Record line = new Record();
						line.set("path", getStr("from", r)+"到"+getStr("to", r));
						line.set("departureTime", getStr("from_time", r));
						line.set("recaddress",getStr("recaddress", r));
						line.set("disaddress",getStr("disaddress", r));
						line.set("arrivalTime",getStr("to_time", r));
						line.set("xsts",r.get("xsts"));
						line.set("id",getStr("lid", r));
						if(StringUtils.isEmpty(r.getStr("NAME"))){
							line.set("companyName", getStr("name", r));
						}else{
							line.set("companyName", getStr("NAME", r));
						}
						line.set("minPrice", 0);
						line.set("phoneNum", getStr("servhotline", r));
						line.set("weight_price", r.getFloat("weight_price")==null?0.0: r.getFloat("weight_price"));
						line.set("volumn_price", r.getFloat("volumn_price")==null?0.0: r.getFloat("volumn_price"));
						list.add(line);
					}
				}
				//info 简介
				resultMap.put("services", list);
				resultMap.put("result", true);
				resultMap.put("info", "查询成功");
				
		}catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(resultMap);
		
	}
	public String getStr(String key, Record r){
		String result = "";
		if(StringUtils.isNotEmpty(key)){
			result = r.getStr(key);
		}
		if(result == null){
			result = "";
		}
		return result;
		
	}
	
	public void companyDetails(){
		keepPara();
		Map<String, Object> resultMap = new HashMap<String, Object>();		
		try {
			String id = this.getPara("id");
			
			if(null==id) {
				//检查参数是否合法			
				resultMap.put("result", false);
				resultMap.put("info", RESULT_INVALID_PARAM);
			} else {
						AbMerchantLogistics record = AbMerchantLogistics.dao.findById(id);
						Record tmp = new Record();
						//涉及城市
						List<Record> logline = Db.find("select a.* from ab_merchant_logline a join ab_merchant_logistics b on a.lid = b.id   where b.id='"+id+"'");
						Map<String,String> map = new HashMap<String, String>();
						List<Record> list = new ArrayList<Record>();
						if(logline!=null && logline.size()>0){
							for(Record r :logline){
								Record line = new Record();
								if(r.get("from")!=null){
									map.put(getStr("from",r),getStr("from",r));
									
								}
								if(r.get("to")!=null){
									map.put(getStr("to",r),getStr("to",r));
								}
								line.set("path", getStr("from",r)+"到"+getStr("to",r));
								line.set("from_time",getStr("from_time",r));
								line.set("to_time", getStr("to_time",r));
								line.set("weight_price", r.get("weight_price"));
								line.set("volumn_price", r.get("volumn_price"));
								list.add(line);
							}
						}
						StringBuffer sb = new StringBuffer();
						for(String str : map.keySet()){
							sb.append(",").append(str);
						}
						
							
						
						//主要路线
						List<AbMerchantLogimg> scene = AbMerchantLogimg.dao.getListByInfoShow(record.get("id"),"scene");
						List<AbMerchantLogimg> honor = AbMerchantLogimg.dao.getListByInfoShow(record.get("id"),"honor");
						List<AbMerchantLogimg> faith = AbMerchantLogimg.dao.getListByInfoShow(record.get("id"),"faith");
						
						record.put("scene", scene);
						record.put("honor", honor);
						record.put("faith", faith);
						List<AbMerchantImg> img = AbMerchantImg.dao.findByMidShow(record.getStr("mid"));
						record.put("pathes", list);
						record.put("banner", img);
						record.put("scope", sb.toString().substring(1));
						
						
						AbMerchant abMerchant = AbMerchant.dao.findById(record.getStr("mid"));
						record.put("name", abMerchant.get("mc"));
						record.put("address", abMerchant.get("xxdz"));
//						record.put("name", abMerchant.get("mc"));
//						record.put("sj", abMerchant);
						
				resultMap.put("companyInfo", record);
				resultMap.put("result", true);
				resultMap.put("info", "查询成功");
				
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		renderJson(resultMap);
	}
	}
