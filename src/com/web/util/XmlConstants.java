package com.web.util;

public class XmlConstants
{
  public static final String OPER_SUCCESS_RESULT = "0";
  public static final String OPER_MSGTYPE_SUCCESS = "N";
  public static final String OPER_MSGTYPE_EXCEPTION = "E";
  public static final int SYNREACHONE = 1;
  public static final int SYNREACHTWO = 2;
  public static final String TRANCMSGDCODE = "OperStatus";
  public static final String TRANCMSGDESC = "OperMsg";
  public static final String head = "head";
  public static final String body = "body";
  public static final String tradeTime = "head.trade_time";
  public static final String tradeSource = "head.trade_source";
  public static final String tradeCode = "head.trade_code";
  public static final String tradeType = "head.trade_type";
  public static final String tradeNo = "body.trade_no";
  public static final String carTime = "body.car_time";
  public static final String sendPlace = "body.send_place";
  public static final String sendName = "body.send_name";
  public static final String sendPhone = "body.send_phone";
  public static final String receivePlace = "body.receive_place";
  public static final String receiveName = "body.receive_name";
  public static final String receivePhone = "body.receive_phone";
  public static final String cargoType = "body.cargo_type";
  public static final String cargoWeight = "body.cargo_weight";
  public static final String cargoUnit = "body.cargo_unit";
  public static final String cargoVolum = "body.cargo_volum";
  public static final String cargoCount = "body.cargo_count";
  public static final String extraQeq = "body.extra_req";
  public static final String remark = "body.remark";
  public static final String payType = "body.pay_type";
  public static final String resultCode = "body.result_code";
  public static final String resultDec = "body.result_dec";
  /*交易成功*/
  public static final String S00000 = "S00000";
  /*接入货的系统的授权码不存在*/
  public static final String S00001 = "S00001";
  /*传入货的系统的订单号为空*/
  public static final String S00002 = "S00002";
  /*传入货的系统的订单号已存在*/
  public static final String S00003 = "S00003";
  /*始发地不能为空*/
  public static final String S00004 = "S00004";
  /*发件人姓名不能为空*/
  public static final String S00005 = "S00005";
  /*发件人电话不能为空*/
  public static final String S00006 = "S00006";
  /*货物到达地不能为空*/
  public static final String S00007 = "S00007";
  /*收件人姓名不能为空*/
  public static final String S00008 = "S00008";
  /*收件人电话不能为空*/
  public static final String S00009 = "S00009";
  /*订单支付方式不能为空*/
  public static final String S00010 = "S00010";
  /*货的系统无此接口类型*/
  public static final String S00011 = "S00011";
  /*该订单数据不存在*/
  public static final String S00012 = "S00012";
  /*该订单已发货，不能回调，请联系客户人员后台处理，谢谢*/
  public static final String S00013 = "S00013";
  /*其他错误*/
  public static final String S99999 = "S99999";
  
  
  
}
