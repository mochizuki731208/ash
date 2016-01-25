package com.zp.controller.ab;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.zp.entity.AbOrder;
import com.zp.entity.SysInvoice;
import com.zp.entity.SysInvoiceAddress;
import com.zp.entity.SysUser;
import com.zp.interceptor.AccessAbInterceptor;
import com.zp.tools.CityAttachAdminUtil;
import com.zp.tools.StringUtil;

public class AbInvoiceController extends Controller {

	/**
	 * 发票列表
	 */
	public void listInvoice() {

		SysUser user = (SysUser) this.getSessionAttr("user"); // 后台用户

		if (user == null) { // 登录
			forwardAction("/25console");
		} else {
			StringBuffer sbu = new StringBuffer();
			StringBuffer qryHistoryInvoice = new StringBuffer();
			StringBuffer tempStr = new StringBuffer();
			Map<String,Object> map = new HashMap<String,Object>();
			float money = 0 ;
			sbu.append("select DISTINCT applyInfor from sys_invoice t where t.area_id = ? ");
			List<SysInvoice> inforList = SysInvoice.dao.find(sbu.toString(), user.getStr("area_id"));
			List<SysInvoice> resultList = new ArrayList<SysInvoice>();
			if(inforList.size() > 0)
			{
				for(SysInvoice invoice : inforList)
				{
					String applyInfor = invoice.getStr("applyInfor");
					qryHistoryInvoice.append("select a.order_sn,a.money,a.type, a.content, a.have_done, b.mobile, b.name, b.address, a.applyInfor from sys_invoice a left join sys_invoice_address b on a.address_id = b.id ");
					qryHistoryInvoice.append(" where a.area_id = ? and a.applyInfor = ?");
					List<SysInvoice> invoicelist = SysInvoice.dao.find(qryHistoryInvoice.toString(),
							user.getStr("area_id"),applyInfor);
					String order_sn = "";
					String invoice_status = "";
					if(invoicelist.size() > 0)
					{
						for(SysInvoice temp : invoicelist)
						{
							order_sn = temp.getStr("order_sn");
							tempStr.append(order_sn);
							tempStr.append("(¥ ");
							tempStr.append(temp.getFloat("money"));
							tempStr.append("),");
							tempStr.append("<br/>");
							money +=temp.getFloat("money");
							
						}
						SysInvoice invoiceStatus = invoicelist.get(0);
						if(invoiceStatus.getInt("have_done")==0)
						{
							invoice_status = "未开票";
						}
						else if(invoiceStatus.getInt("have_done")==1)
						{
							invoice_status = "已驳回";
						}
						else if(invoiceStatus.getInt("have_done")==2)
						{
							invoice_status = "未签收";
						}
						else if(invoiceStatus.getInt("have_done")==3)
						{
							invoice_status = "已签收";
						}
						
						tempStr.delete(tempStr.length()-6, tempStr.length()-5);
						invoice.set("order_sn", tempStr.toString());
						invoice.set("money", money);
						invoice.set("type", invoicelist.get(0).get("type"));
						invoice.set("applyInfor", invoicelist.get(0).get("applyInfor"));
						invoice.set("have_done", invoice_status);
						invoice.set("content", invoicelist.get(0).get("content"));
						
						invoice.put("status", invoicelist.get(0).get("have_done"));
						invoice.put("name", invoicelist.get(0).get("name"));
						invoice.put("address", invoicelist.get(0).get("address"));
						invoice.put("mobile", invoicelist.get(0).get("mobile"));
						resultList.add(invoice);
					}
				}
				
			}
			this.setAttr("resultList", resultList);
			this.setAttr("usertype", "admin");
			render("/admin/invoice/my-trucks.html");
		}
	}

	
	/**
	 * 用户申请过的发票
	 */
	public void userListInvoice() {

		SysUser user = (SysUser) this.getSessionAttr("abuser"); // 用户

		if (user == null) { // 登录
			forwardAction("/ab/login");
		} 
		else 
		{
			StringBuffer sbu = new StringBuffer();
			StringBuffer qryHistoryInvoice = new StringBuffer();
			StringBuffer tempStr = new StringBuffer();
			Map<String,Object> map = new HashMap<String,Object>();
			float money = 0 ;
			sbu.append("select DISTINCT applyInfor from sys_invoice t where t.uid = ? ");
			List<SysInvoice> inforList = SysInvoice.dao.find(sbu.toString(), user.getStr("id"));
			List<SysInvoice> resultList = new ArrayList<SysInvoice>();
			if(inforList.size() > 0)
			{
				for(SysInvoice invoice : inforList)
				{
					String applyInfor = invoice.getStr("applyInfor");
					qryHistoryInvoice.append("select a.order_sn,a.money,a.type, a.content, a.have_done, b.mobile, b.name, b.address, a.applyInfor from sys_invoice a left join sys_invoice_address b on a.address_id = b.id ");
					qryHistoryInvoice.append(" where a.uid = ? and a.applyInfor = ?");
					List<SysInvoice> invoicelist = SysInvoice.dao.find(qryHistoryInvoice.toString(),
							user.getStr("id"),applyInfor);
					String order_sn = "";
					String invoice_status = "";
					for(SysInvoice temp : invoicelist)
					{
						order_sn = temp.getStr("order_sn");
						tempStr.append(order_sn);
						tempStr.append("(¥ ");
						tempStr.append(temp.getFloat("money"));
						tempStr.append("),");
						tempStr.append("<br/>");
						money +=temp.getFloat("money");
						
					}
					SysInvoice invoiceStatus = invoicelist.get(0);
					if(invoiceStatus.getInt("have_done")==0)
					{
						invoice_status = "未开票";
					}
					else if(invoiceStatus.getInt("have_done")==1)
					{
						invoice_status = "已驳回";
					}
					else if(invoiceStatus.getInt("have_done")==2)
					{
						invoice_status = "未签收";
					}
					else if(invoiceStatus.getInt("have_done")==3)
					{
						invoice_status = "已签收";
					}
					
					tempStr.delete(tempStr.length()-6, tempStr.length()-5);
					invoice.set("order_sn", tempStr.toString());
					invoice.set("money", money);
					invoice.set("type", invoicelist.get(0).get("type"));
					invoice.set("applyInfor", invoicelist.get(0).get("applyInfor"));
					invoice.set("have_done", invoice_status);
					invoice.set("content", invoicelist.get(0).get("content"));
					
					invoice.put("status", invoicelist.get(0).get("have_done"));
					invoice.put("name", invoicelist.get(0).get("name"));
					invoice.put("address", invoicelist.get(0).get("address"));
					invoice.put("mobile", invoicelist.get(0).get("mobile"));
					resultList.add(invoice);
				}
				
			}
			this.setAttr("resultList", resultList);
			this.setAttr("usertype", "admin");
			render("/admin/invoice/user-trucks.html");
		}
	}
	/**
	 * 更新发票状态，客户操作——签收发票
	 * @author changbaijian
	 * @see 我的发票——查询已申请发票记录
	 * 
	 */
	public void updateStatus()
	{
		SysUser user = (SysUser) this.getSessionAttr("abuser"); // 前台用户
		if (user == null) { // 登录
			forwardAction("/ab/login");
			return ;
		}
		String applyInfor = this.getPara("applyInfor");
		if(null == applyInfor || "".equals("applyInfor"))
		{
			renderJson("{\"result\":-1,\"msg\":\"fail\"}");
			return;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("update sys_invoice t set t.have_done = 3 where t.applyInfor = ? and t.uid  = ? ");
		int count = Db.update(sb.toString(),applyInfor,user.getStr("id"));
		if(count>0)
		{
			renderJson("{\"result\":0,\"msg\":\"success\"}");
		}
		else
		{
			renderJson("{\"result\":-1,\"msg\":\"fail\"}");
		}
		
	}
	/**
	 * 更新发票状态，管理员操作——发票推送
	 * @author changbaijian
	 * @see 后台--发票——发票列表
	 * 
	 */
	public void updateTrucksStatus()
	{
		SysUser user = (SysUser) this.getSessionAttr("user");  // 后台用户
		if (user == null) { // 登录
			forwardAction("/25console");
			return ;
		}
		String applyInfor = this.getPara("applyInfor");
		Integer hava_done = Integer.parseInt(this.getPara("hava_done"));
		if(null == applyInfor || "".equals("applyInfor"))
		{
			renderJson("{\"result\":-1,\"msg\":\"fail\"}");
			return;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("update sys_invoice t set t.have_done = ? where t.applyInfor = ? and t.area_id  = ? ");
		int count = Db.update(sb.toString(),hava_done, applyInfor,user.getStr("area_id"));
		if(count>0)
		{
			renderJson("{\"result\":0,\"msg\":\"success\"}");
		}
		else
		{
			renderJson("{\"result\":-1,\"msg\":\"fail\"}");
		}
	}
	
	public void reApplyInvoice()
	{
		SysUser user = (SysUser) this.getSessionAttr("abuser"); // 后台用户
		if (user == null) { // 登录
			forwardAction("/ab/login");
			return ;
		}
		String applyInfor = this.getPara("applyInfor");
		if(null == applyInfor || "".equals("applyInfor"))
		{
			renderJson("{\"result\":-1,\"msg\":\"fail\"}");
			return;
		}
		StringBuffer sb = new StringBuffer();
		StringBuffer sbAdress = new StringBuffer();
		sb.append("delete from sys_invoice  where applyInfor = ? and uid  = ? ");
		sbAdress.append("delete from sys_invoice_address  where id in (select address_id from sys_invoice t where t.applyInfor = ? and t.uid  = ?)");
		Db.update(sbAdress.toString(), applyInfor,user.getStr("id"));
		int count = Db.update(sb.toString(),applyInfor,user.getStr("id"));
		if(count>0)
		{
			renderJson("{\"result\":0,\"msg\":\"success\"}");
		}
		else
		{
			renderJson("{\"result\":-1,\"msg\":\"fail\"}");
		}
	}
	/**
	 * 提交发票
	 */
	public void submitInvoice(){
		SysUser user = (SysUser) this.getSessionAttr("abuser"); // 后台用户

		if (user == null) { // 登录
			forwardAction("/ab/login");
			return ;
		} 
		try{
			SysInvoice invoice = this.getModel(SysInvoice.class);
			SysInvoiceAddress address = this.getModel(SysInvoiceAddress.class);
			//获取本次发票申请金额
			Float invoiceMoney = Float.parseFloat(getPara("sys_invoice_money"));
			
			StringBuffer queryStr = new StringBuffer();
			//生成本次申请的唯一性标识，记录本次使用的订单SN
			String applyInfor = StringUtil.getUuid32();
			invoice.set("applyInfor", applyInfor);
			//记录操作人ID
			invoice.set("uid", user.getStr("id"));
			address.set("uid", user.getStr("id"));
			
			//设置发票申请类别 personal 个人，company公司
			if(!"personal".equals(getPara("sys_invoice_type"))){
				invoice.set("type", getPara("company_name"));
			}else{
				invoice.set("type", "个人");
			}
			//查询是否有申请记录，并且是否存有余额 数据库sys_invoice表使用applyMoney记录剩余未申请发票余额
			queryStr.append("select * from sys_invoice t where t.uid = ? and t.applyMoney != 0 ");
			AbOrder aborder = AbOrder.dao.findFirst(queryStr.toString(), user.getStr("id"));
			//如果有申请记录并且有余额
			if(null != aborder && aborder.getFloat("applyMoney") > 0)
			{
				//如果剩余余额足够本次申请的，直接使用余额申请发票，不添加新的订单
				if(aborder.getFloat("applyMoney") >= invoiceMoney)
				{
					//设置申请金额=客户申请的金额
					invoice.set("money",invoiceMoney);
					//添加订单编号
					invoice.set("order_id",aborder.getStr("order_id"));
					invoice.set(SysInvoice.ORDER_SN, aborder.getStr("sn"));
					//设置剩余金额为原有金额-本次使用金额
					invoice.set("applyMoney", (aborder.getFloat("applyMoney"))-invoiceMoney);
					//将保存数据库操作单独抽出方法，减少代码量和方法复杂度
					//保存发票申请到数据库
					saveInvoice(invoice,address);
					//将原来记录发票余额的订单余额清零，使用新生成的订单记录余额
					Db.update("update sys_invoice set applyMoney = 0 where order_id = ?",
							aborder.getStr("order_id"));
					//操作完成，直接返回页面
					renderJson("{\"result\":0,\"msg\":\"success\"}");
					return;
				}
				else//有申请记录并且有余额，但是余额不足够本次申请的
				{
					//设置申请金额
					invoice.set("money",aborder.getFloat("applyMoney"));
					//添加订单编号
					invoice.set("order_id",aborder.getStr("order_id"));
					invoice.set(SysInvoice.ORDER_SN, aborder.getStr("order_sn"));
					//将原来记录发票余额的订单余额清零，使用新生成的订单记录余额
					invoice.set("applyMoney", 0);
					//保存数据库操作
					saveInvoice(invoice,address);
					Db.update("update sys_invoice set applyMoney = 0 where order_id = ?",
							aborder.getStr("order_id"));
					
					//查询可以申请发票的订单记录
					StringBuffer sb = new StringBuffer();
				    sb.append("select * from ab_order t where t.xdrid = ? and t.ddzt='5' ");
				    sb.append(" and t.id not in (select order_id from sys_invoice t where t.uid = ? )");
				    sb.append(" order by t.ddzje desc");
				    List<AbOrder> list = AbOrder.dao.find(sb.toString(), user.getStr("id"),user.getStr("id"));
				    //记录申请发票总金额，起始金额为申请发票余额
				    Float orderMoney = aborder.getFloat("applyMoney");
				    //遍历所有订单
				    for(AbOrder abOrder : list)
				    {
				    	//获取订单金额
				    	BigDecimal ddzje = abOrder.getBigDecimal("ddzje");
				    	invoice.set("money",ddzje);
				    	//添加订单编号
				    	invoice.set("order_id",abOrder.getStr("id"));
				    	invoice.set(SysInvoice.ORDER_SN, abOrder.getStr("sn"));
				    	
				    	//保存数据库操作
				    	saveInvoice(invoice,address);
				    	//申请总金额循环累加
						orderMoney = orderMoney + ddzje.floatValue();
						//当总金额大于或等于本次申请金额时
				    	if(orderMoney >= invoiceMoney)
				    	{
				    		//如果获取到之前的申请记录，跳出for循环
				    		if(null != aborder)
				    		{
				    			//把金额剩余部分记录到applyMoney字段中
				    			invoice.set("applyMoney", orderMoney-invoiceMoney);
					    		float money = ddzje.floatValue()-(orderMoney-invoiceMoney);
					    		Db.update("update sys_invoice set money = ? , applyMoney = ? where order_id = ?", 
					    				money,orderMoney-invoiceMoney,abOrder.getStr("id"));
				    		}
				    		break;
				    	}
				    }
				    
				}
			}
			else//如果没有查到订单申请记录，或者没有剩余发票申请金额
			{
				//查询可申请发票的订单记录
				StringBuffer sb = new StringBuffer();
			    sb.append("select * from ab_order t where t.xdrid = ? and t.ddzt='5' ");
			    sb.append(" and t.id not in (select order_id from sys_invoice t where t.uid = ? )");
			    sb.append(" order by t.ddzje desc");
			    
			    List<AbOrder> list = AbOrder.dao.find(sb.toString(), user.getStr("id"),user.getStr("id"));
			   
			    //记录发票申请总金额，起始金额为0
			    Float orderMoney = new Float(0);
			    //循环遍历订单
			    for(AbOrder abOrder : list)
			    {
			    	//获取订单金额
			    	BigDecimal ddzje = abOrder.getBigDecimal("ddzje");
			    	invoice.set("money",ddzje);
			    	//添加订单编号
			    	invoice.set("order_id",abOrder.getStr("id"));
			    	invoice.set(SysInvoice.ORDER_SN, abOrder.getStr("sn"));
			    	
			    	//保存数据库操作
			    	saveInvoice(invoice,address);
			    	//累加申请总金额
					orderMoney = orderMoney + ddzje.floatValue();
					//当总金额大于申请金额时，更新申请余额并跳出循环
			    	if(orderMoney > invoiceMoney)
			    	{
			    		invoice.set("applyMoney", orderMoney-invoiceMoney);
			    		float money = ddzje.floatValue()-(orderMoney-invoiceMoney);
			    		Db.update("update sys_invoice set money = ? , applyMoney = ? where order_id = ?", 
			    				money,orderMoney-invoiceMoney,abOrder.getStr("id"));
			    		break;
			    	}
			    	else if(orderMoney == invoiceMoney)//如果申请金额正好等于总金额，直接跳出循环
			    	{
			    		break;
			    	}
			    }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			renderJson("{\"result\":-1,\"msg\":\"fail\"}");
			return ;
		}
		renderJson("{\"result\":0,\"msg\":\"success\"}");
	}
	
	/**
	 * 保存数据库操作的方法
	 * @author changbaijian/cWX290419
	 * @param invoice
	 * @param address
	 * @remark 参数为sys_invoice 和sys_invoice_address表的映射类
	 */
	private void saveInvoice(SysInvoice invoice,SysInvoiceAddress address)
	{
		String invoiceid = StringUtil.getUuid32();
		//主键
		invoice.set("id", invoiceid);
		//发票申请类别
		invoice.set("content",getPara("sys_invoice_content"));
		//地区编码
		invoice.set("area_id", getPara("area_id"));
		//余额记录，默认0
		invoice.set("applyMoney", 0);
		
		//主表记录外键ID
		String addressid = StringUtil.getUuid32();
		invoice.set(SysInvoice.ADDRESS_ID, addressid);  //发票收件人地址
		invoice.save();//insert
		
		address.set(SysInvoiceAddress.ID, addressid);
		//保存发票抬头和邮寄地址，联系电话....
		address.set("address",getPara("sys_invoice_address_address"));
		address.set("mobile",getPara("sys_invoice_address_mobile"));
		address.set("name",getPara("sys_invoice_address_name"));
		address.save();
	}
	
	/**
	 * 发票页面初始化和刷新
	 * @author changbaijian
	 * @see 数据更新性变化
	 */
	@Before(AccessAbInterceptor.class)
	public void invoice() {

		SysUser user = (SysUser) this.getSessionAttr("abuser"); // 用户

		if (user == null) { // 登录
			forwardAction("/ab/login");
			return;
		}
		//可申请的发票总金额，总金额包括可申请订单的总额和上次申请剩余金额
		StringBuffer sb = new StringBuffer();
		String userId = user.getStr("id");
		sb.append("select (inv.applyMoney+ord.ddzje) totalMoney from ");
		sb.append(" (select IFNULL(sum(applyMoney),0) applyMoney from sys_invoice t where t.uid = ? ) inv, ");
		sb.append(" (select sum(ddzje) ddzje from ab_order t where t.xdrid = ?  and t.ddzt='5' ");
		sb.append(" and t.id not in (select order_id from sys_invoice t where t.uid = ? )) ord");
	    Double totalMoney = Db.queryDouble(sb.toString(),userId,userId,userId);
	    if(null == totalMoney)
	    {
	    	totalMoney = new Double(0);
	    }
		
		//查询区域管理员
		StringBuffer area_sbu = new StringBuffer();
		area_sbu.append("select * from sys_user where role_id = 103  and area_id in (select t.area_id from ab_order t where t.xdrid = ?)");
		List<SysUser> receiverUser = SysUser.dao.find(area_sbu.toString(), userId);// 选择商圈 域

		if (receiverUser.size() > 0) 
		{
			SysUser mUser = receiverUser.get(0);
			this.setAttr("region_name_company", mUser.getStr(SysUser.MC));
			this.setAttr("area_id", mUser.getStr("area_id"));
			this.setAttr("invoice_available", "1"); // 可开发票
		}
		else 
		{
			this.setAttr("invoice_available", "0");
		}
		
		//已经申请的发票数量，申请的发票数量要根据发票申请提交时记录的标识来记录申请次数
		
		StringBuffer sbu = new StringBuffer();
		sbu.append("select count(DISTINCT applyInfor) count from sys_invoice t where t.uid = ? ");
		Record record = Db.findFirst(sbu.toString(),userId);
		this.setAttr("totalMoney", totalMoney.doubleValue());
		this.setAttr("record", record);
		render("/ab/dd/ddinvoice.html");
	}

}
