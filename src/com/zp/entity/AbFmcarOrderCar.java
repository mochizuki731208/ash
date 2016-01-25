package com.zp.entity;

import com.jfinal.plugin.activerecord.Model;

public class AbFmcarOrderCar extends Model<AbFmcarOrderCar>{
	
	private static final long serialVersionUID = 1L;
	public static final AbFmcarOrderCar dao = new AbFmcarOrderCar();
	
	public static String TABLE = "ab_fmcar_order_car";
	
	/** 主键 **/
    public static final String ID = "id";

	/** 熟车id **/
    public static final String CAR_ID = "car_id";
    
    /** 订单ID **/
    public static final String ORDER_ID = "order_id";
}
