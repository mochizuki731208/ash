package com.web.processor;


import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.web.util.DataElement;
import com.web.util.Dom4jXmlProduct;
import com.web.util.Utility;
import com.web.util.XmlConstants;
import com.web.util.XmlException;
import com.web.util.XmlUtil;
import com.zp.entity.AbAccessSystem;
import com.zp.entity.AbOrder;
import com.zp.entity.AbTcExpressOrder;
import com.zp.entity.SysUser;
import com.zp.tools.StringUtil;

public class AbOrderProcessorImpl implements AbOrderProcessor
{
  public void process(String reqAddr, InputStream is, OutputStream os) {
    Map rspMap = new HashMap();
    Dom4jXmlProduct xmlParse = new Dom4jXmlProduct();
    ObjectInputStream in=null;
    DataElement reqDe=null;
    try {
     // DataElement reqDe = Utility.retrieveReqDom(is);
       in = new ObjectInputStream(is);
	   String strObj = (String)in.readObject();
      System.out.println(("Receive req xml from clientproxy:\n" + strObj));
       reqDe=xmlParse.parse(strObj);
      rspMap = this.dealDate(reqDe);
      
    } catch (Exception e) {
    	e.printStackTrace();
      System.out.println("Exception receiver or handler error:"+e.getMessage());
      rspMap.put("RSP", reqDe);
      rspMap.put("resultCode", XmlConstants.S99999);
      rspMap.put("message", e.getMessage());
      
    }
    try {
      this.respond(os, rspMap);
    } catch (Exception e1) {
      System.out.println("Exception response error:"+ e1.getMessage());
     
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
          System.out.println("IOException close InputStream error:"+e.getMessage());
        }
      }
      if (os != null){
        try {
          os.close();
        } catch (IOException e) {
          System.out.println("IOException close OutputStream error:"+e.getMessage());
        }
      }
      if (in != null){
          try {
            in.close();
          } catch (IOException e) {
            System.out.println("IOException close ObjectInputStream error:"+e.getMessage());
          }
        }
    }
    finally
    {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
          System.out.println("IOException close InputStream error:"+e.getMessage());
        }
      }
      if (os != null){
        try {
          os.close();
        } catch (IOException e) {
          System.out.println("IOException close OutputStream error:"+e.getMessage());
        }
      }
      if (in != null){
          try {
            in.close();
          } catch (IOException e) {
            System.out.println("IOException close ObjectInputStream error:"+e.getMessage());
          }
        }
    }
  }

private void respond(OutputStream os, Map rspMap) throws Exception{
	DataElement rspDom=(DataElement) rspMap.get("RSP");
	String  resultCode=(String) rspMap.get("resultCode");
	String  message=(String) rspMap.get("message");
	DataElement repDe = rspDom.getDataElementAt(XmlConstants.body);
    DataElement resultCodeDe = new DataElement("result_code");
    DataElement resultDecDe = new DataElement("result_dec");
    resultCodeDe.setValue(resultCode);
    resultDecDe.setValue(message);
    repDe.addElement(resultCodeDe);
    repDe.addElement(resultDecDe);
    Utility.writeRspDom(os, rspDom);
    System.out.println("Send rsp xml to clientproxy:\n" + XmlUtil.outDataElement(rspDom, 0));
    
}

private Map dealDate(DataElement reqDe) throws Exception  {
	Map rspMap = new HashMap();
	rspMap.put("RSP", reqDe);
	String tradeSource=reqDe.getDataElementAt(XmlConstants.tradeSource).getValue();
	String tradeCode=reqDe.getDataElementAt(XmlConstants.tradeCode).getValue();
	//校验授权码
	AbAccessSystem accSys=this.getAccSysById(tradeSource);
		if(accSys==null){
			rspMap.put("resultCode",XmlConstants.S00001);
		    rspMap.put("message", "接入货的系统的授权码: "+tradeSource+ "不存在，请联系货的客服人员后台处理，谢谢。");
			return rspMap;
		}
	//校验接口
	if(!"create".equals(tradeCode)&&!"query".equals(tradeCode)&&!"delete".equals(tradeCode)){
			rspMap.put("resultCode",XmlConstants.S00011);
		    rspMap.put("message", "货的系统无此接口: "+tradeCode + "类型。");
		    return rspMap;
		}
	
	//提交订单接口
	if("create".equals(tradeCode)){
		rspMap=this.createData(reqDe);
	}
	//查询订单接口
	if("query".equals(tradeCode)){
		rspMap=this.queryData(reqDe);
	}
	//回调订单接口
	if("delete".equals(tradeCode)){
		rspMap=this.deleteData(reqDe);
	}
	
	return rspMap;
}
private Map deleteData(DataElement reqDe) throws Exception {
	Map rspMap = new HashMap();
	rspMap.put("RSP", reqDe);
	String tradeNo=reqDe.getDataElementAt(XmlConstants.tradeNo).getValue();
	AbTcExpressOrder order1=this.getAbTcExpressByTradeNo(tradeNo);
	if(order1==null){
		rspMap.put("resultCode",XmlConstants.S00012);
	    rspMap.put("message", "该订单数据不存在。");
		return rspMap;
	}
	AbOrder or=this.getAbOrderByTradeNo(tradeNo);
	String ddzt=or.get("ddzt");
	if("1".equals(ddzt)){
		Db.update("update  ab_order set ddzt='8' where sn='" + tradeNo + "'");
		Db.update("update ab_tc_express_order set data_status='1' where sn='" + tradeNo + "'");
		rspMap.put("resultCode",XmlConstants.S00000);
	    rspMap.put("message", "交易成功。");
		return rspMap;
	}else{
		rspMap.put("resultCode",XmlConstants.S00013);
	    rspMap.put("message", "该订单已发货，不能回调，请联系客户人员后台处理，谢谢。");
		return rspMap;
	}
}



private Map queryData(DataElement reqDe) throws Exception {
	Map rspMap = new HashMap();
	rspMap.put("RSP", reqDe);
	String tradeNo=reqDe.getDataElementAt(XmlConstants.tradeNo).getValue();
	AbTcExpressOrder order1=this.getAbTcExpressByTradeNo(tradeNo);
	if(order1==null){
		rspMap.put("resultCode",XmlConstants.S00012);
	    rspMap.put("message", "该订单数据不存在。");
		return rspMap;
	}
	AbOrder or=this.getAbOrderByTradeNo(tradeNo);
	String ddzt=or.get("ddzt");
	String qdrid=or.get("qdrid");
	SysUser u1 = this.getUserByMobile(qdrid,"id");
	String deliveryName="";
	String deliveryPhone="";
	String deliveryLng="";
	String deliveryLat="";
	if (u1 != null) {
		deliveryName=u1.get("mc");
		deliveryPhone=u1.get("mobile");
		deliveryLng=u1.get("lng");
		deliveryLat=u1.get("lat");
	}
	
	DataElement repDe = reqDe.getDataElementAt(XmlConstants.body);
    DataElement trade_status = new DataElement("trade_status");
    DataElement delivery_name = new DataElement("delivery_name");
    DataElement delivery_phone = new DataElement("delivery_phone");
    DataElement delivery_lng = new DataElement("delivery_lng");
    DataElement delivery_lat = new DataElement("delivery_lat");
    
    trade_status.setValue(ddzt);
    delivery_name.setValue(deliveryName);
    delivery_phone.setValue(deliveryPhone);
    delivery_lng.setValue(deliveryLng);
    delivery_lat.setValue(deliveryLat);
    repDe.addElement(trade_status);
    repDe.addElement(delivery_name);
    repDe.addElement(delivery_phone);
    repDe.addElement(delivery_lng);
    repDe.addElement(delivery_lat);
	
    rspMap.put("RSP", reqDe);
	rspMap.put("resultCode",XmlConstants.S00000);
    rspMap.put("message", "交易成功。");
	return rspMap;
}

private Map createData(DataElement reqDe) throws Exception {
	Map rspMap = new HashMap();
	rspMap.put("RSP", reqDe);
	//rspDom.getDataElementAt("ESBHead.RequesterId").getValue()
	DataElement  headDe=reqDe.getDataElementAt(XmlConstants.head);
	DataElement  bodyDe=reqDe.getDataElementAt(XmlConstants.body);
	String tradeTime=reqDe.getDataElementAt(XmlConstants.tradeTime).getValue();
	String tradeSource=reqDe.getDataElementAt(XmlConstants.tradeSource).getValue();
	String tradeCode=reqDe.getDataElementAt(XmlConstants.tradeCode).getValue();
	String tradeType=reqDe.getDataElementAt(XmlConstants.tradeType).getValue();
	String tradeNo=reqDe.getDataElementAt(XmlConstants.tradeNo).getValue();
	String carTime=reqDe.getDataElementAt(XmlConstants.carTime).getValue();
	String sendPlace=reqDe.getDataElementAt(XmlConstants.sendPlace).getValue();
	String sendName=reqDe.getDataElementAt(XmlConstants.sendName).getValue();
	String sendPhone=reqDe.getDataElementAt(XmlConstants.sendPhone).getValue();
	String receivePlace=reqDe.getDataElementAt(XmlConstants.receivePlace).getValue();
	String receiveName=reqDe.getDataElementAt(XmlConstants.receiveName).getValue();
	String receivePhone=reqDe.getDataElementAt(XmlConstants.receivePhone).getValue();
	String cargoType=reqDe.getDataElementAt(XmlConstants.cargoType).getValue();
	String cargoWeight=reqDe.getDataElementAt(XmlConstants.cargoWeight).getValue();
	String cargoUnit=reqDe.getDataElementAt(XmlConstants.cargoUnit).getValue();
	String cargoVolum=reqDe.getDataElementAt(XmlConstants.cargoVolum).getValue();
	String cargoCount=reqDe.getDataElementAt(XmlConstants.cargoCount).getValue();
	String extraQeq=reqDe.getDataElementAt(XmlConstants.extraQeq).getValue();
	String remark=reqDe.getDataElementAt(XmlConstants.remark).getValue();
	String payType=reqDe.getDataElementAt(XmlConstants.payType).getValue();
	AbTcExpressOrder order = new AbTcExpressOrder();
	//校验订单号
	if(tradeNo==null||"".equals(tradeNo)){
		rspMap.put("resultCode",XmlConstants.S00002);
	    rspMap.put("message", "传入货的系统的订单号为空。");
	    return rspMap;
	}else{
		AbTcExpressOrder order1=this.getAbTcExpressByTradeNo(tradeNo);
		if(order1!=null){
			rspMap.put("resultCode",XmlConstants.S00003);
		    rspMap.put("message", "传入货的系统的订单号已存在。");
			return rspMap;
		}
	}
	//校验发件人信息
	if(sendPlace==null||"".equals(sendPlace)){
		rspMap.put("resultCode",XmlConstants.S00004);
	    rspMap.put("message", "始发地不能为空。");
	    return rspMap;
	}
	if(sendName==null||"".equals(sendName)){
		rspMap.put("resultCode",XmlConstants.S00005);
	    rspMap.put("message", "发件人姓名不能为空。");
	    return rspMap;
	}
	if(sendPhone==null||"".equals(sendPhone)){
		rspMap.put("resultCode",XmlConstants.S00006);
	    rspMap.put("message", "发件人电话不能为空。");
	    return rspMap;
	}
	//校验收件人信息
		if(receivePlace==null||"".equals(receivePlace)){
			rspMap.put("resultCode",XmlConstants.S00007);
		    rspMap.put("message", "货物到达地不能为空。");
		    return rspMap;
		}
		if(receiveName==null||"".equals(receiveName)){
			rspMap.put("resultCode",XmlConstants.S00008);
		    rspMap.put("message", "收件人姓名不能为空。");
		    return rspMap;
		}
		if(receivePhone==null||"".equals(receivePhone)){
			rspMap.put("resultCode",XmlConstants.S00009);
		    rspMap.put("message", "收件人电话不能为空。");
		    return rspMap;
		}
		
		if(payType==null||"".equals(payType)){
			rspMap.put("resultCode",XmlConstants.S00010);
		    rspMap.put("message", "订单支付方式不能为空。");
		    return rspMap;
		}
	order.set("car", null);
	order.set("data_source", tradeSource);
	order.set("data_status", "0");
	order.set("start_price", 0);
	order.set("over_unit_price", 0);
	order.set("night_price", 0);
	order.set("send_name", sendName);
	order.set("send_phone", sendPhone);
	order.set("send_addr", sendPlace);
	order.set("rcv_name1", receiveName);
	order.set("rcv_phone1", receivePhone);
	order.set("rcv_addr1", receivePlace);
	if (StringUtil.isNull(cargoWeight)) {
		order.set("kilo", 0);
	} else {
		order.set("kilo", Integer.parseInt(cargoWeight));
	}
	order.set("total_price", 0);
	
	SysUser u1 = this.getUserByMobile(receivePhone,"mobile");
	if (u1 != null) {
		order.set("sh_user_id", u1.getStr("id"));
	}
	order.set("style", null);
	order.set("min_price", null);
	order.set("max_price", null);
	order.set("one_price", 0);
	order.set("more_money",null);
	order.set("cc", "0");
	
	order.set("send_time", carTime);
	order.set("lift", null);
	order.set("goods_type", cargoType);
	order.set("goods_mount", Integer.parseInt(cargoCount));
	order.set("goods_volumn", Integer.parseInt(cargoVolum));
	order.set("huidan_price", null);
	order.set("service_price", null);
	order.set("pay_type", payType);
	if (StringUtil.isNull(cargoWeight)) {
		cargoWeight = "0";
	}
	order.set("weight", Integer.parseInt(cargoWeight));
	
	order.set("people", null);
	order.set("cart", extraQeq);
	
	order.set("night_price", new BigDecimal(Double
			.parseDouble("0")));
	order.set("remark", remark);
	order.set("sn", tradeNo);
	
	String orderid = StringUtil.getUuid32();
	AbOrder o = new AbOrder();
	o.set("id", orderid);
	o.set("sn", tradeNo);
	o.set("spzj", new BigDecimal(0));
	
	o.set("is_type", "1");
	o.set("ddzje", new BigDecimal(0));
	o.set("ddzt", "1");
	o.set("lxr", order.get("rcv_name1"));
	o.set("lxrdh", order.get("rcv_phone1"));
	o.set("shdz", order.get("rcv_addr1"));

	o.set("xdrid", null);
	o.set("xdrmc", null);
	o.set("xdrdh", null);
	o.set("xdsj", null);
	o.set("city_id", null);
	o.set("city_name", null);
	o.set("area_id", null);
	o.set("area_name", null);
	// 同步到数据库
     order.save();
     o.save();
    rspMap.put("resultCode", XmlConstants.S00000);
    rspMap.put("message", "交易成功");
	return rspMap;
}

private SysUser getUserByMobile(String mobile,String columName) {
	String sql = "select * from sys_user where "+columName+"=?";
	SysUser u = SysUser.dao.findFirst(sql, mobile);
	if (u != null) {
		return u;
	}
	return null;
}
private AbAccessSystem getAccSysById(String id) {
	String sql = "select * from ab_access_system where id=?";
	AbAccessSystem accSys = AbAccessSystem.dao.findFirst(sql, id);
	if (accSys != null) {
		return accSys;
	}
	return null;
}

private AbTcExpressOrder getAbTcExpressByTradeNo(String tradeNo) {
	String sql = "select * from ab_tc_express_order where sn=? and data_status='0'";
	AbTcExpressOrder accSys = AbTcExpressOrder.dao.findFirst(sql, tradeNo);
	if (accSys != null) {
		return accSys;
	}
	return null;
}

private AbOrder getAbOrderByTradeNo(String tradeNo) {
	String sql = "select * from ab_order where sn=?";
	AbOrder or = AbOrder.dao.findFirst(sql, tradeNo);
	if (or != null) {
		return or;
	}
	return null;
}

}