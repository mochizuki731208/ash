package com.zp.controller.ab;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.zp.entity.AbCityarea;
import com.zp.entity.AbOrder;
import com.zp.entity.AbSubject;
import com.zp.entity.AbSubjectProduct;
import com.zp.entity.AbSubjectProductOrder;
import com.zp.entity.SysUser;
import com.zp.interceptor.AccessAbInterceptor;
import com.zp.tools.DateUtil;
import com.zp.tools.StringUtil;

public class AbKsContrller extends Controller {
	
	public void index(){
		fill();
	}
	
	public void fill(){
		//当前城市可用的分类
		AbCityarea city = this.getSessionAttr("currcity");
		if(city != null){
			String sql = "select s.* from ab_cityarea_subject cs left join ab_subject s on cs.subject_id=s.id and cs.city_id=? and s.id not in('1018') where s.is_type='1' and s.is_enable='1' and s.is_display='1' and s.ccm=1";
			String childSql = "select s.* from ab_cityarea_subject cs left join ab_subject s on cs.subject_id=s.id and cs.city_id=? where s.is_type='1' and s.is_enable='1' and s.is_display='1' and s.ccm=2 and s.p_id=?";
			List<AbSubject> subList = AbSubject.dao.find(sql, city.getStr("id"));
			Map<AbSubject, List<SubjectProject>> map = new TreeMap<AbSubject, List<SubjectProject>>(new Comparator<AbSubject>() {
				public int compare(AbSubject o1, AbSubject o2) {
					return o1.getInt("seq_num") - o2.getInt("seq_num");
				}
			});
			for(AbSubject topSub : subList){
				map.put(topSub, new ArrayList<SubjectProject>());
				List<AbSubject> childList = AbSubject.dao.find(childSql, city.getStr("id"), topSub.getStr("id"));
				if(childList != null && childList.size()>0){
					for(AbSubject child : childList){
						SubjectProject sp = new SubjectProject(child, AbSubjectProduct.dao.find("select * from ab_subject_product where subject_id=? order by order_num", child.getStr("id")));
						map.get(topSub).add(sp);
					}
				}
			}
			this.setAttr("subs", map);
		}
		
		render("/ab/ks/fill.html");
	}
	
	public static class SubjectProject{
		private AbSubject subject;
		private List<AbSubjectProduct> proList;
		
		public SubjectProject(AbSubject subject, List<AbSubjectProduct> proList) {
			super();
			this.subject = subject;
			this.proList = proList;
		}
		public AbSubject getSubject() {
			return subject;
		}
		public void setSubject(AbSubject subject) {
			this.subject = subject;
		}
		public List<AbSubjectProduct> getProList() {
			return proList;
		}
		public void setProList(List<AbSubjectProduct> proList) {
			this.proList = proList;
		}
	}
	
	@Before(AccessAbInterceptor.class)
	public void confirm(){
		Map map = this.getParaMap();
		String date1 = this.getPara("date1");
		this.setAttr("data", map);
		render("/ab/ks/confirm.html");
	}
	
	@Before(AccessAbInterceptor.class)
	public void complete(){
		String type1 = this.getPara("type1");
		String type2 = this.getPara("type2");
		String phone = this.getPara("phone");
		String mark = this.getPara("mark");
		String time = this.getPara("time");
		String address = this.getPara("address");
		String pros = this.getPara("pros");
		
		
		//生成订单数据
		String orderid = StringUtil.getUuid32();
		//保存购物车订单数据
		AbOrder order = new AbOrder();
		order.set("id", orderid);
		//生成订单号
		String sn = "";
		String sql = "select count(id) cnt from ab_order where sn like 'KS"+DateUtil.getCurrentDate("yyyyMMdd")+"%'";
		if(Db.findFirst(sql).getLong("cnt")<1){
			sn = "KS"+DateUtil.getCurrentDate("yyyyMMdd")+"000001";
			order.set("sn", sn);
		}else{
			//取最大值加1
			sn = StringUtil.convertStr(Db.findFirst("select max(sn) maxid from ab_order where sn like 'KS"+DateUtil.getCurrentDate("yyyyMMdd")+"%'").getStr("maxid"));
			sn = StringUtil.getStrSeqCode(false, sn, 6);
			order.set("sn", sn);
		}
		if(type2 != null){
			List<AbSubject> list = AbSubject.dao.find("select * from ab_subject where mc=?", type2);
			if(list != null && list.size() > 0){
				order.set("subject_id", list.get(0).get("id"));
				order.set("subject_name", list.get(0).get("mc"));
			}
		}else if(type1 != null){
			List<AbSubject> list = AbSubject.dao.find("select * from ab_subject where mc=?", type1);
			if(list != null && list.size() > 0){
				order.set("subject_id", list.get(0).get("id"));
				order.set("subject_name", list.get(0).get("mc"));
			}
		}
		order.set("is_type", "1");
		order.set("lxrdh", phone);//联系人电话
		order.set("shsj", time);//收货时间
		SysUser abuser = this.getSessionAttr("abuser");
		order.set("xdrid", abuser.getStr("id"));//下单人id
		order.set("xdrmc", abuser.getStr("mc"));//下单人名字
		order.set("xdrdh", abuser.getStr("mobile"));//下单人电话
		order.set("xdsj", DateUtil.getCurrentDate());//下单时间
		order.set("accountremark", mark);//备注
		order.set("shdz", address);//收货地址
		order.set("ddzt", '1');//受理订单
		order.save();
		
		if(pros!=null && pros.length()>0){
			for(String key : pros.split("#")){
				if(key.contains("_")){
					String id = key.split("_")[0];
					String num = key.split("_")[1];
					AbSubjectProductOrder po = new AbSubjectProductOrder();
					po.set("pid", id);
					po.set("oid", order.getStr("id"));
					po.set("num", Integer.parseInt(num));
					po.save();
				}
			}
		}
		
		render("/ab/ks/complete.html");
	}
	
	public void loadProduts(){
		String subId = this.getPara("id");
		List<AbSubjectProduct> proList = AbSubjectProduct.dao.find("select * from ab_subject_product where subject_id=? order by order_num", subId);
		renderJson(proList);
	}
	
	public void loadPro(){
		String subName = this.getPara("sub_name");
		String mc = this.getPara("mc");
		List<AbSubjectProduct> proList = AbSubjectProduct.dao.find("select p.* from ab_subject_product p left join ab_subject s on p.subject_id=s.id where s.mc=? and p.mc=? order by order_num", subName,mc);
		renderJson(proList);
	}
	
}
