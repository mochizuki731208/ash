package com.zp.entity;

import com.jfinal.plugin.activerecord.Model;

public class AbMerchantPrint  extends Model<AbMerchantPrint>{
	 private static final long serialVersionUID = 1L;
	 public static final AbMerchantPrint dao = new AbMerchantPrint();

	 public static String TABLE = "ab_merchant_print";

	 /** 主键 **/
	 public static final String ID = "id";
	 
	 /** 用户id **/
	 public static final String PARTNER = "partner";
	 
	 /** 打印机终端号**/
	 public static final String MACHINECODE = "machinecode";
	 
	 /** API密钥 **/
	 public static final String APIKEY = "apikey";
	 
	 /** 打印机密钥 **/
	 public static final String MKEY = "mkey";
	 
	 /** 手机号 **/
	 public static final String MOBILE = "mobile";
	 
	 /** 上架id **/
	 public static final String MID = "mid";
	 
	 /** 是否启用 **/
	 public static final String ISWORK = "iswork";
}
