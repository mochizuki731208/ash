package com.zp.controller.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.yishenghuo.model.AbUser;

import org.apache.commons.lang.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.zp.entity.AbCityarea;
import com.zp.entity.AbFmcar;
import com.zp.entity.AbFmcarCity;
import com.zp.entity.AbFmcarUser;
import com.zp.entity.SysUser;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.tools.StringUtil;

public class RestFmCarController extends AbstractRestController {
	/**
	 * 获取用户熟车列表：分页
	 */
	public void getUserFmCarList() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			// 获取分页信息
			Integer pi = this.getParaToInt("pi");
			Integer ps = this.getParaToInt("ps");
			String uid = this.getPara("uid");
			String nameOrMobile = this.getPara("nameOrMobile");
			if (pi == null || ps == null || pi < 1 || ps < 1 || StringUtils.isBlank(uid)) {
				formatInvalidParamResponse(resultMap);
			} else {
				List<Object> paramList = new ArrayList<Object>();
				StringBuilder sqlBuf = new StringBuilder("SELECT g.*,u.xsz,u.sfz,u.rate,u.credittype,u.sjzt FROM (");
				sqlBuf.append(" SELECT t.*,c.id AS carCityId,c.city_name FROM (");
				sqlBuf.append(" SELECT b.user_id, a.mobile,a.id, a.location,a.location_time,a.recommend_no, ");
				sqlBuf.append(" a.lng,a.lat,a.is_protect,a.state,a.driver, a.type,a.length,a.car_no FROM ab_fmcar_user  ");
				sqlBuf.append("  b , ab_fmcar a WHERE a.id=b.car_id ");
				sqlBuf.append("  AND b.user_id =? ");
				paramList.add(uid);
				if(!StringUtils.isBlank(nameOrMobile)){
					if(nameOrMobile.trim().startsWith("1")){  //手机号码
						sqlBuf.append(" AND a.mobile like ?");
						paramList.add(nameOrMobile);
					}else{ 
						sqlBuf.append(" AND a.driver like ?");
						paramList.add(nameOrMobile);
					}
				}
				sqlBuf.append(" limit ?,?");
				paramList.add(getStart(pi, ps));
				paramList.add(ps);
				sqlBuf.append("  )AS t LEFT JOIN ab_fmcar_city  c ");
				sqlBuf.append("  ON t.id=c.car_id ) AS g LEFT JOIN sys_user AS u ON ");
				sqlBuf.append(" g.mobile=u.loginid ");
				List<AbFmcar> fmCarList = AbFmcar.dao.find(sqlBuf.toString(), paramList.toArray());
				resultMap.put("data", fmCarList);
				resultMap.put("result", RESULT_SUCCESS);
			}
		} catch (Exception e) {
			formatInvokeFailResponse(e, resultMap);
		}

		renderJson(resultMap);
	}

	public void delFmcarUser(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String userId = getPara("uid");// 用户id
		String carId = getPara("carId");// 用户id
		if(StringUtil.isNull(userId) || StringUtil.isNull(carId)) {
			resultMap.put("result", RESULT_FAIL);
			resultMap.put("msg", RESULT_INVALID_PARAM);
		} else {
			AbFmcarUser.dao.delByUserIdAndCarId(userId, carId);
			resultMap.put("result", RESULT_SUCCESS);
			resultMap.put("msg", "删除成功");
		}
		renderJson(resultMap);		
	}
	
	public void getCarInfoByMoblie() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String userId = getPara("uid");// 用户id
		String mobile = getPara("mobile");// 联系手机
		if(StringUtil.isNull(userId) || StringUtil.isNull(mobile)) {
			//检查参数是否合法			
			resultMap.put("result", RESULT_FAIL);
			resultMap.put("msg", RESULT_INVALID_PARAM);
		} else {
			AbFmcar car = AbFmcar.dao.findByMobile(mobile);
			if(null != car && !StringUtil.isNull(car.getStr(AbFmcar.ID))) {
				AbFmcarCity  afc = new AbFmcarCity();
				List<AbFmcarCity> cityList = afc.findByCarId(car.getStr(AbFmcar.ID));
				resultMap.put("cityList", cityList);
			}
			resultMap.put("result", RESULT_SUCCESS);
			resultMap.put("carInfo", car);
			resultMap.put("msg", "查询成功");
		}
		renderJson(resultMap);	
	}
	
	/**
	 * 添加“我的数车“ 关联已有账号
	 */
	public void addMobileCar() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String userId = getPara("uid");// 用户id
		String carId = getPara("carId");// carid
		if(StringUtil.isNull(userId) || StringUtil.isNull(carId)){
			//检查参数是否合法			
			resultMap.put("result", RESULT_FAIL);
			resultMap.put("msg", RESULT_INVALID_PARAM);
		} else {
			AbFmcarUser carUser = new AbFmcarUser();
			carUser.set(AbFmcarUser.ID, StringUtil.getRandString32());
			carUser.set(AbFmcarUser.CAR_ID, carId);
			carUser.set(AbFmcarUser.USER_ID, userId);
			carUser.save();
			resultMap.put("carId", carId);
			resultMap.put("result", RESULT_SUCCESS);
			resultMap.put("msg", "新增成功");
		}
		renderJson(resultMap);	
	}
	
	/**
	 * 添加“我的数车“
	 */
	public void addFmCar(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String userId = getPara("uid");// 用户id
		String mobile = getPara("mobile");// 联系手机
		String driver = getPara("driver");// 司机姓名
		String carNo = getPara("carNo");// 车牌号
		String length = getPara("length");// 车长
		String type = getPara("type");// 车型 
		String runCitys = getPara("runCitys");// 常用城市，多个以逗号隔开;每个项由cityId加“#”再加cityName组成，如：330300#大同，140900#漳州
		if(StringUtil.isNull(userId) || StringUtil.isNull(mobile) || StringUtil.isNull(driver) ||
				StringUtil.isNull(carNo) || StringUtil.isNull(length) || StringUtil.isNull(type) || StringUtil.isNull(runCitys)) {
			//检查参数是否合法			
			resultMap.put("result", RESULT_FAIL);
			resultMap.put("msg", RESULT_INVALID_PARAM);
		} else {
			AbFmcar car = new AbFmcar();			
			String carId = StringUtil.getRandString32();
			car.set("id", carId);
			car.set(AbFmcar.DRIVER, driver);
			car.set(AbFmcar.CAR_NO, carNo);
			car.set(AbFmcar.LENGTH, length);
			car.set(AbFmcar.TYPE, type);
			car.set(AbFmcar.MOBILE, mobile);
			car.save();				
			addFmCarCitys(runCitys, carId);
			
			AbFmcarUser carUser = new AbFmcarUser();
			carUser.set(AbFmcarUser.ID, StringUtil.getRandString32());
			carUser.set(AbFmcarUser.CAR_ID, carId);
			carUser.set(AbFmcarUser.USER_ID, userId);
			carUser.save();
			resultMap.put("carId", carId);
			resultMap.put("result", RESULT_SUCCESS);
			resultMap.put("msg", "新增成功");
		}
		renderJson(resultMap);	
	}

	/**
	 * 保存“我的数车” 对应的常跑城市
	 */
	private void addFmCarCitys(String runCitys, String carId) {		
		String citys[] = runCitys.split(";");
		int ciLength = citys.length;
		for (int i = 0; i < ciLength; i++) {
			AbFmcarCity  afc = new AbFmcarCity();
			afc.set(AbFmcarCity.ID, StringUtil.getRandString32());
			afc.set(AbFmcarCity.CAR_ID, carId);
			afc.set(AbFmcarCity.CITY_NAME, citys[i]);
			afc.save();
		}
	}
	
	/**
	 * 点击“我的数车”列表记录进入详情页接口
	 */
	public void getUserFmCarDetail() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			String id = this.getPara("id");
			if (StringUtils.isBlank(id)) {
				formatInvalidParamResponse(resultMap);
			} else {
				AbFmcar abFmCar = AbFmcar.dao.findById(id, "id,car_no,driver,mobile,length,type,location,state");
				resultMap.put("data", abFmCar);
				resultMap.put("result", RESULT_SUCCESS);
			}
		} catch (Exception e) {
			formatInvokeFailResponse(e, resultMap);
		}
		renderJson(resultMap);
	}
	
	/**
	 *得到城市列表
	 */
	public void getCityList() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			String id = this.getPara("uid");
			if (StringUtils.isBlank(id)) {
				formatInvalidParamResponse(resultMap);
			} else {			
				List<AbCityarea> abFmCar = AbCityarea.dao.find("SELECT a.mc,a.id,a.p_id FROM ab_cityarea AS a,ab_cityarea AS b WHERE a.p_id = b.id");
				resultMap.put("data", abFmCar);
				resultMap.put("result", RESULT_SUCCESS);
			}
		} catch (Exception e) {
			formatInvokeFailResponse(e, resultMap);
		}
		renderJson(resultMap);
	}
	
	/**
	 * 查询具体的熟车
	 */
	public void searchMyFmCar(){
		//查询当前城市的司机
		Map<String, Object> resultMap = new HashMap<String, Object>();
		//是否我的熟车
		String ismyfr = StringUtil.toStr(this.getPara("ismyfr"));
		
		//长跑城市
		String runcity = StringUtil.toStr(this.getPara("runcity"));
		//地图可见区域的最大最小进度
		String lngMin = StringUtil.toStr(this.getPara("lngMin"));
		String lngMax = StringUtil.toStr(this.getPara("lngMax"));
		//地图可见区域的最大最小维度
		String latMin = StringUtil.toStr(this.getPara("latMin"));
		String latMax = StringUtil.toStr(this.getPara("latMax"));
		//司机手机号码
		String mobile = StringUtil.toStr(this.getPara("mobile"));
		//车型
		String carType = StringUtil.toStr(this.getPara("carType"));
		//状态
		String carZt = StringUtil.toStr(this.getPara("carZt"));
		//车长
		String carLength = StringUtil.toStr(this.getPara("carLength"));
		//用户id
		String uid = StringUtil.toStr(this.getPara("uid"));
		if (StringUtils.isBlank(uid) || StringUtils.isBlank(lngMin)||StringUtils.isBlank(lngMax) || StringUtils.isBlank(latMin)|| StringUtils.isBlank(latMax)) {
			formatInvalidParamResponse(resultMap);
		} else {
			
	
		SysUser user = SysUser.dao.findById(uid);  //前台台用户
		List<AbFmcar> carlist = new ArrayList<AbFmcar>();
		String sql = "select a.*,b.* from ab_fmcar as a,(select * from(select c.id as shrid,c.loginid mobile1,d.jd,d.wd,c.sjzt zt,d.dqsj as dqsj,c.rate as rate,c.logo as logo from sys_user c left join ab_sj_position d on c.id= d.sjid and c.role_id='106' where 1 = 1";
		
				sql += " and d.jd <=" + lngMax + " and d.jd >=" + lngMin;
				sql += " and d.wd <=" + latMax + " and d.wd >=" + latMin;
		
				sql += " ORDER BY dqsj DESC) u WHERE 1 =1  GROUP BY shrid ) b where a.mobile = b.mobile1";
				
				if(ismyfr.length() > 0 && null != user){
					sql += " and a.id in (select car_id from ab_fmcar_user where user_id = '" + user.getStr("id") + "')";
				}
//				else{
//					sql += " and a.id in (select car_id from ab_fmcar_user)";
//				}
				 
				if(carType.length() > 0){
					sql += " and a.type = '" + carType + "'";
				}
				if(carZt.length() > 0){
					if("2".equals(carZt)){
						sql += " and b.zt = '" + carZt + "'";
					}else{
						sql += " and b.zt != '2'";
					}
				}
				if(carLength.length() > 0){
					sql += " and a.length = '" + carLength + "'";
				}
				if(runcity.length() > 0){
					String[] strArr = runcity.split(",");
					String msg = "";
					for(String str : strArr){
						msg += "'" + str + "',";
					}
					sql += " and a.id in (select car_id from ab_fmcar_city where city_name in (" + msg.substring(0, msg.length() - 1) +"))";
				}
				if(mobile.length() > 0){
					sql += " and a.mobile like'%" + mobile +"%'";
				}
				//黑名单的车子除外
				if(null!=user){
					sql += " and a.mobile not in (select mobile from sys_mobile_blank where userid= '" +  user.getStr("id") + "')";
				}
				
				
			carlist = AbFmcar.dao.find(sql);
			resultMap.put("data", carlist);
			resultMap.put("result", RESULT_SUCCESS);
		}
		renderJson(resultMap);
	}
	/**
	 * 查询具体的熟车
	 */
	public void searchMyFmCarList(){
		//查询当前城市的司机
		Map<String, Object> resultMap = new HashMap<String, Object>();
		//是否我的熟车
		String ismyfr = StringUtil.toStr(this.getPara("ismyfr"));
		
		//长跑城市
		String runcity = StringUtil.toStr(this.getPara("runcity"));
		//精度
		String lng = StringUtil.toStr(this.getPara("lng"));
		//维度
		String lat = StringUtil.toStr(this.getPara("lat"));
		//司机手机号码
		String mobile = StringUtil.toStr(this.getPara("mobile"));
		//车型
		String carType = StringUtil.toStr(this.getPara("carType"));
		//状态
		String carZt = StringUtil.toStr(this.getPara("carZt"));
		//车长
		String carLength = StringUtil.toStr(this.getPara("carLength"));
		//用户id
		String uid = StringUtil.toStr(this.getPara("uid"));
		
		Integer pi = this.getParaToInt("pi");
		Integer ps = this.getParaToInt("ps");
		
		if (StringUtils.isBlank(uid) || StringUtils.isBlank(lng)||StringUtils.isBlank(lat) || pi==null|| ps==null) {
			formatInvalidParamResponse(resultMap);
		} else {
			
	
		List<Record> carlist = new ArrayList<Record>();
		
		String sql = "select a.*,b.* from ab_fmcar as a,(select * from(select c.id as shrid,c.loginid mobile1,d.jd,d.wd,c.sjzt zt,d.dqsj as dqsj,c.rate as rate ,c.status as status from sys_user c left join ab_sj_position d on c.id= d.sjid and c.role_id='106' where 1 = 1";
		sql = "SELECT * FROM (" +
				"SELECT a.id,a.lat,a.lng,a.logo,a.mc,a.mobile,a.cc as length,a.cx as type,a.cpcs,a.cph as car_no,ROUND(6378.138*2*ASIN(SQRT(POW(SIN( ("+lat+"*PI()/180-a.lat*PI()/180)/2),2) +COS("+lng+"*PI()/180)*COS(a.lat*PI()/180)*  POW(SIN( ("+lng+" *PI()/180-a.lng*PI()/180)/2),2)))*1000) AS jl " +
				"FROM sys_user a LEFT JOIN " +
				"(select d.*,f.user_id as uid from ab_fmcar_user f join ab_fmcar d on f.car_id = d.id  ) c on a.id = c.uid where 1=1 and a.role_id=106 ";
				if(carType.length() > 0){
					sql += " and c.type = '" + carType + "'";
				}
				if(carZt.length() > 0){
					if("2".equals(carZt)){
						sql += " and a.zt = '" + carZt + "'";
					}else{
						sql += " and a.zt != '2'";
					}
				}
				if(carLength.length() > 0){
					sql += " and c.length like '%" + carLength + "%'";
				}
				if(runcity.length() > 0){
					String[] strArr = runcity.split(",");
					String msg = "";
					for(String str : strArr){
						msg += "'" + str + "',";
					}
					sql += " and a.id in (select car_id from ab_fmcar_city where city_name in (" + msg.substring(0, msg.length() - 1) +"))";
				}
				if(mobile.length() > 0){
					sql += " and a.mobile like'%" + mobile +"%'";
				}
				
				sql = sql +"  ) e   WHERE e.jl <=100000 ORDER BY e.jl ASC limit ?,?";
				System.out.println(sql);
			carlist = Db.find(sql,getStart(pi, ps),ps);
			resultMap.put("data", carlist);
			resultMap.put("result", RESULT_SUCCESS);
		}
		renderJson(resultMap);
	}
}
