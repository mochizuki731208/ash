package com.zp.entity;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

public class AbFmcarCity extends Model<AbFmcarCity>{
	
	private static final long serialVersionUID = 1L;
	public static final AbFmcarCity dao = new AbFmcarCity();
	
	public static String TABLE = "ab_fmcar_city";

	/** 主键 **/
    public static final String ID = "id";

	/** 熟车id **/
    public static final String CAR_ID = "car_id";
    
    /** 常跑城市名 **/
    public static final String CITY_NAME = "city_name";
    
    public List<AbFmcarCity> findByCarId(String carId){
		StringBuffer sql = new StringBuffer(" select * from ab_fmcar_city where car_id = ? ");
		List<AbFmcarCity> cityList = dao.find(sql.toString(), carId);
	    return cityList;
	}
    
    public boolean delByCarId(String carId){
    	StringBuffer sql = new StringBuffer(" delete from ab_fmcar_city where car_id = ? ");
    	Db.update(sql.toString(),carId);
    	return true;
    }
}
