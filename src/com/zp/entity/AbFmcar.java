package com.zp.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jfinal.plugin.activerecord.Model;

public class AbFmcar extends Model<AbFmcar>{

	private static final long serialVersionUID = 1L;
	public static final AbFmcar dao = new AbFmcar();
	
	public static String TABLE = "ab_fmcar";

	/** 主键 **/
    public static final String ID = "id";

	/** 车牌号 **/
    public static final String CAR_NO = "car_no";

	/** 司机姓名 **/
    public static final String DRIVER = "driver";

	/** 电话号码 **/
    public static final String MOBILE = "mobile";

	/** 车长 **/
    public static final String LENGTH = "length";

	/** 车型 **/
    public static final String TYPE = "type";

    /** 是否开通车辆定位 **/
    public static final String IS_LOCATE = "is_locate";
    
	/** 位置 **/
    public static final String LOCATION = "location";

	/** 定位时间 **/
    public static final String LOCATION_TIME = "location_time";

	/** 熟车状态 **/
    public static final String STATE = "state";
	
	/** 允许听全网订单? **/
    public static final String IS_NET = "is_net";
    
    /** 推荐号 **/
    public static final String RECOMMEND_NO = "recommend_no";
   
    /** 熟车保护? **/
    public static final String IS_PROTECT = "is_protect";
    
	/** 备注 **/
    public static final String REMARK = "remark";
    
    /** 真假司机 **/
    public static final String ISTRUE = "istrue";
    
    /** 车的体积 **/
    public static final String VV = "vv";

    public List<AbFmcar> findByFields(Map<String, String> fields){
    	StringBuffer sql = new StringBuffer("select * from ab_fmcar where 1=1 ");
		
		if(fields.size() != 0){
			Set<String> set = fields.keySet();
			for (String key : set) {
				sql.append("and " + key + " = ? "); 
			}
		}
		
		Object[] params = fields.values().toArray();
		
		List<AbFmcar> list = dao.find(sql.toString(), params);
		
		return list;
    }
    
    
    public List<AbFmcar> findByFields(Map<String, String> fields, String[] citys, String userId,String groupid){
    	ArrayList<Object> params = new ArrayList<Object>();
    	StringBuffer sql = new StringBuffer("");
    	
    	sql.append("select * from ab_fmcar where 1=1 ");
    	
    	if(fields.size() != 0){
			Set<String> set = fields.keySet();
			for (String key : set) {
				sql.append("and " + key + " = ? "); 
			}
		}
    	
    	params.addAll(fields.values());
    	
    	sql.append(" and id in ( ");
    	
    	if(citys != null && citys.length > 0){
    		sql.append("  select a.car_id from ( ");
        	sql.append(" 	select car_id from ab_fmcar_user  where user_id = ? ");
        	if(groupid!=null)
        		sql.append(" and group_id= ?");
        	sql.append("  ) a inner join ( ");
        	sql.append(" 	select car_id from ab_fmcar_city where  city_name in ( ");
        	for(int i=0;i<(citys.length - 1);i++){
    			sql.append("?,");
    		}
    		sql.append("?)");
        	sql.append("  ) b on a.car_id = b.car_id ");
        	
        	params.add(userId);
        	if(groupid!=null)
        		params.add(groupid);
        	params.addAll(Arrays.asList(citys));
    	}else{
    		sql.append(" select car_id from ab_fmcar_user  where user_id = ? ");
    		
    		params.add(userId);
    		if(groupid!=null){
    			sql.append(" and group_id= ?");
    			params.add(groupid);
    		}
    			
    	}
    	
    	sql.append(" )");
    	
		List<AbFmcar> list = dao.find(sql.toString(), params.toArray());
		
		return list;
    }
    
    //查询熟车，放然要把黑名单除外
    public List<AbFmcar> findByUserId(String userId,String groupId){
    	StringBuffer sql = new StringBuffer("select * from ab_fmcar where id in ( select car_id from ab_fmcar_user where user_id = ? %s) and mobile not in (select mobile from sys_mobile_blank where userid = ?)");
    	String abGroup="";
    	if(groupId==null||groupId.equals("")){
    		abGroup="";
    	}else{
    		abGroup=" and group_id='"+groupId+"' ";
    	}
    	List<AbFmcar> list = dao.find(String.format(sql.toString(),abGroup),userId,userId);
    	return list;
    }

	public AbFmcar findByMobileAndUserId(String userId, String mobile) {
		StringBuffer sql = new StringBuffer("select b.* from ( ");
    	sql.append(" select * from ab_fmcar_user where user_id = ? ");
    	sql.append(" ) a");
    	sql.append(" right join ( select * from ab_fmcar where mobile = ? ) b");
    	sql.append(" on b.id=a.car_id");
    	
    	AbFmcar car = dao.findFirst(sql.toString(),userId,mobile);
    	return car;
	}
	
	public AbFmcar findByMobile(String mobile){
		StringBuffer sql = new StringBuffer(" select * from ab_fmcar where mobile = ? ");
		AbFmcar car = dao.findFirst(sql.toString(), mobile);
	    return car;
	}
}
