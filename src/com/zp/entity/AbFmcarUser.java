package com.zp.entity;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

public class AbFmcarUser extends Model<AbFmcarUser>{
	
	private static final long serialVersionUID = 1L;
	public static final AbFmcarUser dao = new AbFmcarUser();
	
	public static String TABLE = "ab_fmcar_user";

	/** 主键 **/
    public static final String ID = "id";

	/** 熟车id **/
    public static final String CAR_ID = "car_id";
    
    /** 用户id **/
    public static final String USER_ID = "user_id";
    
    /** 分组id **/
    public static final String GROUP_ID = "group_id";
    
    public boolean isExistUserIdAndCarId(String userId,String carId){
    	StringBuffer sql = new StringBuffer(" select count(1) as count from ab_fmcar_user where car_id = ? and user_id = ? ");
    	Record record = Db.findFirst(sql.toString(),carId,userId);
    	return record.getLong("count") > 0;
    }
    
    public void delByUserIdAndCarId(String userId,String carId){
    	Db.update("delete from ab_fmcar_user where car_id = ? and user_id = ? ", carId,userId);
    }
    
    public AbFmcarUser findByUserIdCarId(String userId,String carId){
    	return dao.findFirst("SELECT cu.*,car.car_no,car.driver,car.length,car.mobile,car.type,car.vv FROM ash.ab_fmcar_user cu left join ab_fmcar car on cu.car_id=car.id  where cu.car_id = ? and cu.user_id = ?",carId,userId);
    }
}
