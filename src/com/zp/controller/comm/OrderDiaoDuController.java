package com.zp.controller.comm;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.zp.entity.AbFmcar;
import com.zp.entity.AbFmcarGroup;
import com.zp.entity.AbFmcarOrderBaoJia;
import com.zp.entity.AbOrder;
import com.zp.entity.AbTcExpressOrderDiaoDu;
import com.zp.entity.SysUser;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.tools.DateUtil;
import com.zp.tools.StringUtil;


/**
 * 车辆调度
 * @author gengsan
 * @classname:OrderDiaoDuController
 * @date:2015年9月27日下午10:29:28
 */
public class OrderDiaoDuController extends Controller {
	
	//下单人进行待调度订单查询
	public void showOrdersOwner(){
//		String sql="select * from ab_order where xdrid=? and ddzt=1 and id not in(select orderid from ab_tc_express_order_diaodu)";
		
	}
	//已经分派的订单
	public void showOrdersAlloc(){
		SysUser abuser = this.getSessionAttr("abuser");
		if(abuser==null){
			render("/ab/login.html");
			return;
		}
			String sql="SELECT a.*,o.sn,o.is_type,o.city_id,o.city_name,o.area_id,o.area_name,o.yt,o.mid,o.mname,o.sjjd,o.sjwd,o.mapbusiness,o.spzj,o.ddzje,o.yhje,o.cjlfjf,o.sdsj,o.ddzt,o.ddzje,o.shdz, o.lxr,o.lxrdh,o.xdrid,o.xdrmc";
			sql+=",car.car_no,car.driver,car.mobile,driver.id as driverid ";
			sql+=",baojia.createtime,baojia.price ";
			sql+=" FROM ab_tc_express_order_diaodu a ";
			sql+=" left join ab_order o on a.orderid =o.id left join ab_fmcar car on a.carid=car.id ";
			sql+=" left join sys_user driver on driver.loginid=car.mobile";
			sql+=" left join ab_fmcar_order_baojia baojia on (baojia.orderid=o.id and baojia.sjid=driver.id)";
			sql+=" where a.orderid in(select id from ab_order where xdrid=? )";
			
			List<Record> list=Db.find(sql,abuser.getStr("id"));
			this.setAttr("listordercar", list);
			render("/ab/diaodu/listtcordercar.html");
		
	}
	
	//查询我的车队。
	public void showMyFmCars(){
		SysUser user = (SysUser)this.getSessionAttr("abuser");
		if(user==null){
			this.renderJson(false);
		}
		String user_id = user.getStr("id");
		String sqlMyCar = "select a.*,GROUP_CONCAT(c.city_name) as city_name from ab_fmcar a,ab_fmcar_user b,ab_fmcar_city c where a.id=b.car_id and a.id=c.car_id and b.user_id='" + user_id +"'";
		String groupid=this.getPara("cargroup");
		if(groupid!=null&&!groupid.equals("")){
			sqlMyCar+=" and b.group_id='"+groupid+"' ";
			this.setAttr("currentgroup",groupid);			
		}
		sqlMyCar+=" group by a.id";
		renderJson(AbFmcar.dao.find(sqlMyCar));
//		this.setAttr("mycarList",AbFmcar.dao.find(sqlMyCar));
//		this.setAttr("id", id);
		
	}
	
	//下单人进行订单推送给相应的司机。此处金处理web端。处理后，对应司机短的菜单“我要抢单”中可以查看到订单
//	public void doZhiPai(){
//		
//	}
	
	//司机端有权查看的订单。只有下单人讲订单推送给该司机。该司机才能看到订单。才能接受订单。或者进行订单的加价
	public void showCanQiangDanOrders(){
		SysUser user = (SysUser)this.getSessionAttr("abuser");
		if(user==null){
			render("/ab/login.html");
			return;
		}
		String sql = "select a.*,c.id as driverid from(select a.*,b.carid from ab_order a,ab_tc_express_order_diaodu b where a.id = b.orderid)a";
				sql+="  left join ab_fmcar b on a.carid=b.id";
				sql+="  left join sys_user c on b.mobile=c.loginid";
		//sql += " where a.ddzt = 1 ";
		sql += " where a.carid in (select id from ab_fmcar where mobile='" + user.getStr("loginid") + "')";
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),14, sql);
		this.setAttr("listpage", listpage);
		this.render("/ab/diaodu/showCanQiangDanOrders.html");
	}
	
	//司机对订单进行报价
	public void addPriceForOder(){
		SysUser user= (SysUser)this.getSessionAttr("abuser");
		String orderid=this.getPara("orderid");
		String userid=user.getStr("id");
		String price=this.getPara("price");
		
		
		AbFmcarOrderBaoJia baojia=AbFmcarOrderBaoJia.dao.findByOrderDriver(orderid, userid);
		if(baojia==null){
			baojia=new AbFmcarOrderBaoJia();
			
			baojia.set("id", StringUtil.getRandString32());
			baojia.set("orderid", orderid);
			baojia.set("sjid", userid);
			baojia.set("price", new BigDecimal(price));
			baojia.set("createtime", DateUtil.getCurrentDate());
			baojia.save();
			this.renderJson(true);
		}else {
			baojia.set("orderid", orderid);
			baojia.set("price", new BigDecimal(price));
			baojia.set("createtime", DateUtil.getCurrentDate());
			baojia.updateByOrderDriver(orderid, userid, price);
		}
		this.renderJson(true);
	}
	
	//司机抢单/此处要避免多并发。
	public void acceptOrder(){
		String orderid = StringUtil.toStr(this.getPara("id"));
		SysUser user = (SysUser)this.getSessionAttr("abuser");
		if(orderid.length() == 0){
			this.renderJson(false);
			return;
		}
		AbFmcar af = AbFmcar.dao.findByMobile(user.getStr("loginid"));
		List<AbTcExpressOrderDiaoDu> atepodList = AbTcExpressOrderDiaoDu.dao.find("select * from ab_tc_express_order_diaodu where orderid=? and carid<>?", orderid,af.getStr("id"));
		for(AbTcExpressOrderDiaoDu obj : atepodList){
			obj.delete();
		}
		AbOrder order = AbOrder.dao.findById(orderid);
		if(null==order){
			this.renderJson(false);
			return;
		}
		order.set("ddzt", 2);
		order.set("qdrid", user.getStr("id"));
		order.set("qdsj", DateUtil.getCurrentDate());

		order.update();
		
//		String sn=Db.queryStr("select sn from ab_order where id=?",orderid);
//		String userid=order.getStr("xdrid");
//		String roleid=Db.queryStr("select role_id from sys_user where id=?",userid);
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		String str_date =df.format(new Date());
//		Record record=new Record();
//		record.set("id", StringUtil.getUuid32());
//		record.set("user_id", userid);
//		record.set("role_id", roleid);
//		record.set("mes_title","抢单消息");
//		record.set("mes_type", "0");
//		record.set("isread", "0");
//		record.set("text", "有人抢单！订单号："+sn);
//		record.set("send_date", str_date);
//		Db.save("message", record);
		
		this.renderJson(true);
	}
	
	//单个司机拒绝订单。此时应将他的抢单按钮隐藏，并显示已拒绝。不影响其他司机抢单
	public void rejectOrder(){
		String orderid = StringUtil.toStr(this.getPara("id"));
		SysUser user = (SysUser)this.getSessionAttr("abuser");
		if(orderid.length() == 0){
			this.renderJson(false);
			return;
		}
		AbFmcar af = AbFmcar.dao.findFirst("select * from ab_fmcar where mobile=?", user.getStr("loginid"));
		AbTcExpressOrderDiaoDu atepod = AbTcExpressOrderDiaoDu.dao.findFirst("select * from ab_tc_express_order_diaodu where orderid=? and carid=?", orderid,af.getStr("id"));
		if(null==atepod){
			this.renderJson(false);
		}else{
			atepod.delete();
			this.renderJson(true);
		}
//		String sn=Db.queryStr("select sn from ab_order where id=?",orderid);
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		String str_date =df.format(new Date());
//		Record record=new Record();
//		record.set("id", StringUtil.getUuid32());
//		record.set("user_id", user.get("id"));
//		record.set("role_id", user.get("role_id"));
//		record.set("mes_title","抢单消息");
//		record.set("mes_type", "0");
//		record.set("isread", "0");
//		record.set("text", "订单已拒绝！订单号："+sn);
//		record.set("send_date", str_date);
//		Db.save("message", record);
	}
	
	//下单人接受某个司机的报价处理。此时即将该司机指定为接单人。
	@Before(Tx.class)
	public void acceptAddPriceForOderByOwner() {
		SysUser user = (SysUser) this.getSessionAttr("abuser");
		if(user==null){
			renderJson(false);
			return;
		}
		String baojiaid = this.getPara("baojiaid");
		String orderid=this.getPara("orderid");
		
		AbFmcarOrderBaoJia bj=AbFmcarOrderBaoJia.dao.findById(baojiaid);
		if (bj == null) {
			renderJson(false);				
			return;
		}
		
		String driverid = bj.getStr("sjid");

		AbOrder order = AbOrder.dao.findById(orderid);
		if (order == null) {
				renderJson(false);				
				return;
			}
			SysUser driver = SysUser.dao.findById(driverid);
			order.set("ddzje",bj.getStr("price"));
			order.set("ddzt", 2);// 订单状态改为取货中
			order.set("qdsj", DateUtil.getCurrentDate());
			order.set("qdrid", driverid);

			order.set("qdrname", driver.getStr("mc"));
			
//			List<AbTcExpressOrderDiaoDu> atepodList = AbTcExpressOrderDiaoDu.dao.find("select * from ab_tc_express_order_diaodu where sn=? and car", sn);
//			for(AbTcExpressOrderDiaoDu obj : atepodList){
//				obj.delete();
//			}
			order.update();
			renderJson(true);
	}
	
	//初始化指派页面
	public void initZhiPai(){
		String id = StringUtil.toStr(this.getPara("id"));
		SysUser user = (SysUser)this.getSessionAttr("abuser");
		if(user==null){
			render("/ab/login.html");
			return;
		}
		
		//车辆列表
		String user_id = user.getStr("id");
		String sqlMyCar = "select a.group_id, b.*,c.city_name from ab_fmcar_user a "; 
				sqlMyCar+=" left join ab_fmcar b on a.car_id=b.id "; 
						sqlMyCar+="left join (select GROUP_CONCAT(city_name) as city_name,car_id from ab_fmcar_city group by car_id ) c on c.car_id=a.car_id"; 
						sqlMyCar+=" where user_id='" + user_id +"'";
		String groupid=this.getPara("cargroup");
		if(groupid!=null&&!groupid.equals("")){
			sqlMyCar+=" and a.group_id='"+groupid+"' ";
			this.setAttr("currentgroup",groupid);			
		}
		
		List<Record>list= Db.find(sqlMyCar);
		//城市名去掉前面的 nnnnn#(n为数字)
		for(Record r:list){
			String orgcitynam=r.getStr("city_name");
			if(orgcitynam==null)
				continue;
			String[] names=orgcitynam.split(",");
			String rname="";
			for (int i=0;i<names.length;i++){
				if(i>0)
					rname+=",";
				rname+=names[i].split("#")[1];
			}
			r.set("city_name", rname);
		}
		this.setAttr("mycarList",list);
		this.setAttr("id", id);
		
		//单列表
//		String sql = "select a.*,b.driver,c.id as flag from ab_fmcar_order a left join ab_fmcar b on a.car_id = b.id left join ab_fmcar_order_baojia c on a.id=c.orderid  where 1 = 1";
//		sql += " and a.ddzt = '1'";
//		sql += " and a.create_id = '" + user.getStr("id") + "'";
//		sql += " order by a.create_time desc";
//		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1), 1000,sql);
//		this.setAttr("listpage", listpage);
		// 添加按月、周、天查询
		SysUser abuser = this.getSessionAttr("abuser");
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from (");
		sb.append("select ao.*, aa.id appid, aoc.id cbid, aoc.cb_status,tc.style style,tc.min_price min_price,tc.max_price max_price,tc.pay_type,tc.sh_user_id,tc.sh_user_id2,tc.sh_user_id3,tc.sh_user_id4,tc.sh_user_id5,tc.send_time,tc.rcv_name1,tc.product_name,tc.product_sn,tc.producer,tc.cheliang,tc.transport,tc.cangku,tc.operator from ");
		sb.append(" (select * from ab_order where upper(left(sn,2))='TC') ao ");
		sb.append(" left join ab_appraise aa on ao.id = aa.order_id ");
		sb.append(" left join ab_tc_express_order tc on tc.sn = ao.sn ");
		sb.append(" left join ab_order_chargeback aoc on aoc.order_id = ao.id ) t where (xdrid='"	+ abuser.getStr("id") + "' or sh_user_id='"+abuser.getStr("id")+"'" +
				" or sh_user_id2='"+abuser.getStr("id")+"'" +
				" or sh_user_id3='"+abuser.getStr("id")+"'" +
				" or sh_user_id4='"+abuser.getStr("id")+"'" +
				" or sh_user_id5='"+abuser.getStr("id")+"'" +
				" or qdrid='"+abuser.getStr("id")+"'" +
				")  and id not in(select orderid from ab_tc_express_order_diaodu)");
		sb.append(" and t.ddzt='1'");
		sb.append(" and t.way=1");
		sb.append(" order by t.xdsj desc");
		PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1),30, sb.toString());
		
		this.setAttr("listpage", listpage);
		List<AbFmcarGroup> group=AbFmcarGroup.dao.findByUserid(user_id);
		this.setAttr("grouplist", group);
		this.render("/ab/diaodu/initZhiPai.html");
	}
	
	//司机报价页面初始
	public void initBaoJia(){
		SysUser user = this.getSessionAttr("abuser");
		if(user==null){
			this.render("/ab/login.html");
			return;
		}
		String orderid = StringUtil.toStr(this.getPara("id"));//订单id
		
		AbFmcarOrderBaoJia afobj = AbFmcarOrderBaoJia.dao.findByOrderDriver(orderid, user.getStr("id"));
		if(null==afobj){
			afobj = new AbFmcarOrderBaoJia();
			afobj.set("orderid", orderid);
		}
		this.setAttr("afobj", afobj);
		this.render("/ab/diaodu/showprice.html");
	}

	
	//进行订单指派--思路：讲订单和车id入库即可表示指派成功
	public void doZhiPai(){
		try{
			String[] car_id_Arr = this.getParaValues("car_id");
			String[] order_id_Arr = this.getParaValues("order_id");
			AbTcExpressOrderDiaoDu ateodu = null;
			
			if(car_id_Arr==null||car_id_Arr.length==0){
				List<AbFmcar> cars=AbFmcar.dao.find("select * from ab_fmcar");
				car_id_Arr =new String[cars.size()];
				for(int i=0;i<cars.size();i++){
					car_id_Arr[i]=cars.get(i).getStr("id");
				}
			}
			
			for(String car_id : car_id_Arr){
				for(String order_id:order_id_Arr){
					ateodu = AbTcExpressOrderDiaoDu.dao.findFirst("select * from ab_tc_express_order_diaodu where orderid=? and carid=?", order_id,car_id);
					if (null == ateodu) {
						ateodu = new AbTcExpressOrderDiaoDu();
						ateodu.set("id", StringUtil.getUuid32());
						ateodu.set("orderid", order_id);
						ateodu.set("carid", car_id);
						ateodu.set("cjsj", DateUtil.getCurrentDate());
						ateodu.save();
					} else {
						ateodu.set("orderid", order_id);
						ateodu.set("carid", car_id);
						ateodu.set("cjsj", DateUtil.getCurrentDate());
						ateodu.update();
					}
				}
			}
//			for(String str: car_id_Arr){
//				if(car_id.length() == 0){
//					car_id = str;
//				}else{
//					car_id += "," + str;
//				}
//			}
//			AbFmcarOrder afo = null;
//			for(String id : order_id_Arr){
//				afo = AbFmcarOrder.dao.findById(id);
//				if(car_id_Arr.length > 0 && null!=afo){
//					afo.set("car_id", car_id);
//					afo.set("ddzt", "1");
//					afo.update();
//				}
//			}
		}catch(Exception e){
			e.printStackTrace();
			this.renderJson(false);
			return;
		}
		this.renderJson(true);
	}

}
