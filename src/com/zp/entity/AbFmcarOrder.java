package com.zp.entity;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

public class AbFmcarOrder extends Model<AbFmcarOrder>{

    private static final long serialVersionUID = 1L;
    public static final AbFmcarOrder dao = new AbFmcarOrder();

    public static String TABLE = "ab_fmcar_order";

	/**  **/
    public static final String ID = "id";

	/**  **/
    public static final String START_PROVINCE_CODE = "start_province_code";

	/**  **/
    public static final String START_PROVINCE_NAME = "start_province_name";

	/**  **/
    public static final String START_CITY_CODE = "start_city_code";

	/**  **/
    public static final String START_CITY_NAME = "start_city_name";

	/**  **/
    public static final String START_COUNTY_CODE = "start_county_code";

	/**  **/
    public static final String START_COUNTY_NAME = "start_county_name";

	/**  **/
    public static final String START_ADDR = "start_addr";

	/**  **/
    public static final String ARR_PROVINCE_CODE = "arr_province_code";

	/**  **/
    public static final String ARR_PROVINCE_NAME = "arr_province_name";

	/**  **/
    public static final String ARR_CITY_CODE = "arr_city_code";

	/**  **/
    public static final String ARR_CITY_NAME = "arr_city_name";

	/**  **/
    public static final String ARR_COUNTY_CODE = "arr_county_code";

	/**  **/
    public static final String ARR_COUNTY_NAME = "arr_county_name";

	/**  **/
    public static final String ARR_ADDR = "arr_addr";

	/**  **/
    public static final String CAR_LENGTH = "car_length";

	/**  **/
    public static final String CAR_TYPE = "car_type";

	/**  **/
    public static final String LOAD_TIME = "load_time";

	/**  **/
    public static final String MOBILE = "mobile";

	/**  **/
    public static final String REMARK = "remark";

	/**  **/
    public static final String STATUS = "status";

	/**  **/
    public static final String CREATE_ID = "create_id";

	/**  **/
    public static final String CREATE_TIME = "create_time";

	/**  **/
    public static final String CAR_ID = "car_id";
    public static final String START_LNG = "start_lng";
    public static final String START_LAT = "start_lat";
    public static final String END_LNG = "end_lng";
    public static final String END_LAT = "end_lat";
    public static final String DDZT = "ddzt";
    public static final String ZFZT = "zfzt";
    public static final String ZFFS = "zffs";
    public static final String ZJE = "zje";
    
    
    public List<AbFmcarOrder> findByUserId(String userId){
    	StringBuffer sql = new StringBuffer();
    	sql.append(" select * from ab_fmcar_order where create_id = ? ");
    	
    	List<AbFmcarOrder> list = dao.find(sql.toString(),userId);
    	return list;
    }
    
    public int findCountByUserIdAndStatus(String userId , String status){
    	StringBuffer sql = new StringBuffer();
    	sql.append(" select count(*) count from ab_fmcar_order where create_id = ? and status = ? ");
    	
    	List list = Db.find(sql.toString() ,userId,status);
    	Record xx = (Record) list.get(0);
    	System.out.println("xxxx="  + xx);
    	return ((Long)xx.get("count")).intValue();
    }
    
    public List<AbFmcarOrder> findByFields(Map<String, String> fields){
    	StringBuffer sql = new StringBuffer("select * from ab_fmcar_order where 1=1 ");
		
		if(fields.size() != 0){
			Set<String> set = fields.keySet();
			for (String key : set) {
				sql.append("and " + key + " = ? "); 
			}
		}
		
		Object[] params = fields.values().toArray();
		
		List<AbFmcarOrder> list = dao.find(sql.toString(), params);
		
		return list;
    }
 }