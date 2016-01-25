package com.zp.controller.app;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.sql.dialect.db2.visitor.DB2ASTVisitor;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.zp.entity.AbFmcar;
import com.zp.entity.AbFmcarCity;
import com.zp.entity.AbFmcarGroup;
import com.zp.entity.AbFmcarUser;
import com.zp.entity.SysUser;
import com.zp.tools.SmsMessage;
import com.zp.tools.StringUtil;

public class AppFmcarController extends AbsAppController {

	final Logger logger = LoggerFactory.getLogger(AppFmcarController.class);

	public void hello() {
		try{
			String uid = getPara("uid");
			SysUser.dao.deleteById(uid);
			}catch(Exception e){
				renderJson(false);
				return ;
			}
		renderJson(true);
	}
	public void group() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try{
			String uid = getPara("uid");
			if(StringUtils.isEmpty(uid)){
				formatInvalidParamResponse(resultMap);
			}else{
				AbFmcarGroup fmcar = new AbFmcarGroup();
				List<AbFmcarGroup> list = fmcar.findByUserid(uid);
				resultMap.put("data", list);
				formatSuccessResponse(resultMap);
			}
		}catch(Exception e){
			formatInvokeFailResponse(e, resultMap);
		}
		renderJson(resultMap);
	}
	public void addgroup() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try{
			String uid = getPara("uid");
			String name = getPara("groupname");
			if(StringUtils.isEmpty(uid) || StringUtils.isEmpty(name)){
				formatInvalidParamResponse(resultMap);
			}else{
				AbFmcarGroup fmcar = new AbFmcarGroup();
				fmcar.set("groupname", name);
				fmcar.set("user_id", uid);
				fmcar.set("id", UUID.randomUUID().toString().replaceAll("-", ""));
				fmcar.save();
				formatSuccessResponse(resultMap);
			}
		}catch(Exception e){
			formatInvokeFailResponse(e, resultMap);
		}
		renderJson(resultMap);
	}
	public void deletegroup() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try{
			String uid = getPara("uid");
			String id = getPara("id");
			if(StringUtils.isEmpty(uid) ||  StringUtils.isEmpty(id)){
				formatInvalidParamResponse(resultMap);
			}else{
				AbFmcarGroup fmcar = AbFmcarGroup.dao.findById(id);
				if(fmcar!=null && StringUtils.equals(uid, fmcar.getStr("user_id"))){
					//判断分组下面是否有车辆
					Record r = Db.findFirst("select count(1) as con from ab_fmcar_user where group_id=?",id);
					if(r.getLong("con")>0){
						resultMap.put("result", RESULT_FAIL);
						resultMap.put("msg", "分组分组中还存在熟车，不能删除");
					}else{
						fmcar.delete();
						formatSuccessResponse(resultMap);
					}
				}else{
					resultMap.put("result", RESULT_FAIL);
					resultMap.put("msg", "分组不存在");
				}
				
			}
		}catch(Exception e){
			formatInvokeFailResponse(e, resultMap);
		}
		renderJson(resultMap);
	}
	public void updategroup() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try{
			String uid = getPara("uid");
			String id = getPara("id");
			String name = getPara("groupname");
			if(StringUtils.isEmpty(uid) || StringUtils.isEmpty(name) ||  StringUtils.isEmpty(id)){
				formatInvalidParamResponse(resultMap);
			}else{
				AbFmcarGroup fmcar = AbFmcarGroup.dao.findById(id);
				if(fmcar!=null && StringUtils.equals(uid, fmcar.getStr("user_id"))){
					fmcar.set("groupname", name);
					fmcar.update();
					formatSuccessResponse(resultMap);
				}else{
					resultMap.put("result", RESULT_FAIL);
					resultMap.put("msg", "分组不存在");
				}
			}
		}catch(Exception e){
			formatInvokeFailResponse(e, resultMap);
		}
		renderJson(resultMap);
	}
	/**
	 * del fmcar
	 */
	public void delFmcar() {
		System.out.println("delFmcar 1");
		String uid = this.getPara("uid");
		String carId = this.getPara("carid");
		SysUser user = SysUser.dao.findById(uid);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		AbFmcar car = AbFmcar.dao.findById(carId);
		if (!checkParam(uid, carId) || car == null) {
			formatInvalidParamResponse(resultMap);
			renderJson(resultMap);
		} else {
			try {
				AbFmcarUser.dao.delByUserIdAndCarId(uid, carId);
				AbFmcarCity.dao.delByCarId(carId);
				formatSuccessResponse(resultMap);
			} catch (Exception e) {
				System.out.println("exception addFmcar");
				e.printStackTrace();
				formatInvokeFailResponse(e, resultMap);
			}
		}
		renderJson(resultMap);
	}

	/**
	 * 添加熟车
	 */
	public void addFmcar() {
		System.out.println("addFmcar 1");
		String uid = this.getPara("uid");
		SysUser user = SysUser.dao.findById(uid);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (user == null) {
			logger.error("user is null");
			formatErrorMsgResponse("uid is not exsit", resultMap);
			renderJson(resultMap);
		} else {
			String userId = user.getStr("id");

			String runcity = this.getPara("runcity");
			String mobile = getPara("mobile"); // 必须
			String driver = getPara("driver"); // 必须
			String carNo = getPara("carNo");
			String length = getPara("length");
			String type = getPara("type");
			String isNet = getPara("isNet");

			if (!checkParam(mobile, driver, carNo, length, type )) {
				formatInvalidParamResponse(resultMap);
				System.out.println("addFmcar formatInvalidParamResponse 1");
			} else {
				try {
					//先判断我的熟车里面是否有当前司机
					String sql = "select count(1) as con from ab_fmcar a join ab_fmcar_user b on a.id = b.car_id where b.user_id = ? and a.mobile=?";
					Record r = Db.findFirst(sql,userId,mobile);
					if(r.getLong("con")>0){
						
						resultMap.put("result", RESULT_FAIL);
						resultMap.put("msg", "已存在此熟车，请勿重复添加");
						
					}else{
						AbFmcar car = getModel(AbFmcar.class);
						String carId = StringUtil.getRandString32();
						car.set(AbFmcar.ID, carId);
						car.set(AbFmcar.MOBILE, mobile);
						car.set(AbFmcar.DRIVER, driver);
						car.set(AbFmcar.CAR_NO, carNo);
						car.set(AbFmcar.LENGTH, length);
						car.set(AbFmcar.TYPE, type);
						car.set(AbFmcar.IS_NET, isNet);
						car.save();

						AbFmcarUser carUser = new AbFmcarUser();
						carUser.set("id", StringUtil.getRandString32());
						carUser.set("car_id", carId);
						carUser.set("user_id", userId);
						carUser.save();

						AbFmcarCity.dao.delByCarId(carId);

						if (!StringUtil.isNull(runcity)) {
							String[] citys = runcity.split(",", -1);
							for (String city : citys) {
								if (!StringUtil.isNull(city)) {
									String[] cityArr = city.split("#");
									if (cityArr.length != 2) {
										formatInvalidParamResponse(resultMap);
										System.out
												.println("exception addFmcar formatInvalidParamResponse");
										renderJson(resultMap);
										return;
									}

									AbFmcarCity fmcarCity = new AbFmcarCity();
									fmcarCity.set("id",
											StringUtil.getRandString32());
									fmcarCity.set("car_id", carId);

									fmcarCity.set("city_name", city);
									fmcarCity.save();
								}
							}
						}
					}
					
					
					// else {
					// AbFmcarCity fmcarCity = new AbFmcarCity();
					// List<AbFmcarCity> listTemp = AbFmcarCity.dao
					// .findByCarId(carId);
					// if (listTemp == null || listTemp.size() == 0) {
					// fmcarCity.set("id", StringUtil.getRandString32());
					// fmcarCity.set("car_id", carId);
					// fmcarCity.set("city_name", "");
					// fmcarCity.save();
					// }
					// }
					formatSuccessResponse(resultMap);
				} catch (Exception e) {
					System.out.println("exception addFmcar");
					e.printStackTrace();
					formatInvokeFailResponse(e, resultMap);
				}
			}
			renderJson(resultMap);
		}
	}

	/**
	 * 获取用户熟车列表：分页
	 */
	public void getUserFmCarList() {
		logger.info("getUserFmCarList");
		com.jfinal.log.Logger.getLogger("APP")
				.info(" jfinal getUserFmCarList ");
		org.apache.log4j.Logger.getLogger("APP").info("getUserFmCarList");
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			// 获取分页信息
			Integer pi = this.getParaToInt("pi");
			Integer ps = this.getParaToInt("ps");
			String uid = this.getPara("uid");

			// not necessary
			String usualCity = getPara("usualCity");
			String length = getPara("length");
			String sortDis = getPara("sortDis");
			String sortRate = getPara("sortRate");
			String groupid = getPara("groupid");
			String type = getPara("type");
			String nameOrMobile = this.getPara("nameOrMobile");
			if (pi == null || ps == null || pi < 1 || ps < 1
					|| StringUtils.isBlank(uid)) {
				formatInvalidParamResponse(resultMap);
			} else {
				List<Object> paramList = new ArrayList<Object>();
				String sql = "select c.id,c.status as state,c.mc as driver,c.cx as type,c.cc as length,c.cph as car_no,c.mobile,a.remark,"
						+ "c.xxdz, c.jz,c.sfrzzt,c.xsz, c.sfz,c.rate, c.credittype, c.sjzt, c.is_online, c.cpcs,c.logo as logo,"
						+ " c.lat as wd,c.lng as jd, a.location,a.lng,a.lat "
						+ " from ab_fmcar a JOIN (select d.*,b.car_id as car_id from ab_fmcar_user b join sys_user d on b.user_id = d.id where b.user_id=? ";
				
				
				paramList.add(uid);

				if (!StringUtils.isBlank(nameOrMobile)) {
					if (nameOrMobile.trim().startsWith("1")) { // 手机号码
						sql += "and a.mobile=? ";
						paramList.add(nameOrMobile);
					} else {
						sql += "and a.driver=? ";
						paramList.add(nameOrMobile);
					}
				}

				if (!StringUtils.isBlank(groupid)) {
					sql += "and b.groupid=? ";
					paramList.add(groupid);
				}
				if (!StringUtils.isBlank(type)) {
					sql += "and a.type=? ";
					paramList.add(type);
				}
				if (!StringUtils.isBlank(length)) {
					sql += "and a.length=? ";
					paramList.add(length);
				}
//				if (!StringUtils.isBlank(usualCity)) { // 常用城市
//					String arr[] = usualCity.split("#");
//					String sqlTemp = "";
//					int cityIndex = 0;
//					for (String city : arr) {
//						cityIndex++;
//						if (cityIndex == 1) {
//							sqlTemp += " and( d.city_name = ? ";
//
//						} else {
//							sqlTemp += " or d.city_name = ? ";
//						}
//
//						paramList.add(city);
//					}
//					sqlTemp += ")";
//					sql += sqlTemp;
//				}
				sql = sql+") c on a.id = c.car_id ";
				if (!StringUtils.isBlank(sortRate)) { // 评价排序
					if (sortRate.equals("0")) {
						sql += "order by c.rate desc ";
					} else if (sortRate.equals("1")) {
						sql += "order by c.rate asc ";
					}
				}

				sql += " limit ?,?";
				paramList.add(getStart(pi, ps));
				paramList.add(ps);
				System.out.println(sql);
				List<Record> fmCarList = Db.find(sql,paramList.toArray());
				if (fmCarList.size() == 1) {
					if (fmCarList.get(0).getStr("id") == null) {
						fmCarList.clear();
					}
				}
				//合作次数
				if(fmCarList!=null&& fmCarList.size()>0){
					for(int i=0;i<fmCarList.size(); i++){
						 Record hzcs = Db.findFirst("select count(1) as con from ab_order a where a.qdrid = ? and a.xdrid=? ", fmCarList.get(i).getStr("id"),uid); 
						fmCarList.get(i).set("hzcs", hzcs.getLong("con"));
					}
					
				}
				
				resultMap.put("data", fmCarList);
				formatSuccessResponse(resultMap);
			}
		} catch (Exception e) {
			formatInvokeFailResponse(e, resultMap);
		}

		renderJson(resultMap);
	}
	/**
	 * 获取用户熟车列表：分页
	 */
	public void getFmCarList() {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			// 获取分页信息
			Integer pi = this.getParaToInt("pi");
			Integer ps = this.getParaToInt("ps");
			String uid = this.getPara("uid");
			
			// not necessary
			String usualCity = getPara("usualCity");
			String length = getPara("length");
			String sortDis = getPara("sortDis");
			String sortRate = getPara("sortRate");
			String type = getPara("type");
			String nameOrMobile = this.getPara("nameOrMobile");
			if (pi == null || ps == null || pi < 1 || ps < 1
					|| StringUtils.isBlank(uid)) {
				formatInvalidParamResponse(resultMap);
			} else {
				List<Object> paramList = new ArrayList<Object>();
				String sql = "select a.id,c.id as uid,a.state,a.driver,a.type,a.length,a.car_no,a.mobile,a.remark,"
					+ "c.xxdz, c.jz,c.sfrzzt,c.xsz, c.sfz,c.rate, c.credittype, c.sjzt, c.is_online, "
					+ "a.location,a.lng,a.lat "
					+ " from  sys_user c left join ab_fmcar a on c.loginid=a.mobile " +
					"join ab_fmcar_user b on a.id= b.car_id "
					+ "where 1=1 and c.role_id=106 ";
				
				if (!StringUtils.isBlank(nameOrMobile)) {
					if (nameOrMobile.trim().startsWith("1")) { // 手机号码
						sql += "and a.mobile=? ";
						paramList.add(nameOrMobile);
					} else {
						sql += "and a.driver=? ";
						paramList.add(nameOrMobile);
					}
				}
				
				if (!StringUtils.isBlank(type)) {
					sql += "and a.type=? ";
					paramList.add(type);
				}
				if (!StringUtils.isBlank(length)) {
					sql += "and a.length=? ";
					paramList.add(length);
				}
				
				if (!StringUtils.isBlank(sortRate)) { // 评价排序
					if (sortRate.equals("0")) {
						sql += "order by c.rate desc ";
					} else if (sortRate.equals("1")) {
						sql += "order by c.rate asc ";
					}
				}
				
				sql += "limit ?,?";
				paramList.add(getStart(pi, ps));
				paramList.add(ps);
				System.out.println(sql);
				List<AbFmcar> fmCarList = AbFmcar.dao.find(sql,
						paramList.toArray());
				if (fmCarList.size() == 1) {
					if (fmCarList.get(0).getStr("id") == null) {
						fmCarList.clear();
					}
				}
				if(fmCarList!=null&& fmCarList.size()>0){
					for(int i=0;i<fmCarList.size(); i++){
						List<Record> list = Db.find("select city_name  from ab_fmcar_city where car_id=?", fmCarList.get(i).getStr("id")); 
						fmCarList.get(i).put("cpcs", list);
					}
					
				}
				
				resultMap.put("data", fmCarList);
				formatSuccessResponse(resultMap);
			}
		} catch (Exception e) {
			formatInvokeFailResponse(e, resultMap);
		}
		
		renderJson(resultMap);
	}

	/**
	 * 点击“我的熟车”列表记录进入详情页接口
	 */
	public void getUserFmCarDetail() {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			String id = this.getPara("id");
			if (StringUtils.isBlank(id)) {
				formatInvalidParamResponse(resultMap);
			} else {
				AbFmcar abFmCar = AbFmcar.dao.findById(id,
						"id,car_no,driver,mobile,length,type,location,state");
				resultMap.put("data", abFmCar);
				formatSuccessResponse(resultMap);
			}
		} catch (Exception e) {
			formatInvokeFailResponse(e, resultMap);
		}

		renderJson(resultMap);
	}

	//
	// public void addFmcarUser() {
	// SysUser user = getUser();
	// if (user == null) {
	// renderJson(false);
	// } else {
	// String userId = user.getStr("id");
	// String carId = this.getPara("carId");
	// String id = StringUtil.getRandString32();
	// AbFmcarUser fmcarUser = new AbFmcarUser();
	// fmcarUser.set("id", id);
	// fmcarUser.set("user_id", userId);
	// fmcarUser.set("car_id", carId);
	// fmcarUser.save();
	// renderJson(true);
	// }
	// }

	private String getCellValue(Cell cell) {
		String cellValue = "";
		if (cell != null) {
			int cellType = cell.getCellType();
			switch (cellType) {
			case Cell.CELL_TYPE_BOOLEAN:
				cellValue = String.valueOf(cell.getBooleanCellValue());
				break;
			case Cell.CELL_TYPE_NUMERIC:
				DecimalFormat df = new DecimalFormat("0");
				cellValue = df.format(cell.getNumericCellValue());
				System.err.println(cellValue);
				break;
			default:
				cellValue = String.valueOf(cell.getStringCellValue());

			}
		}
		return cellValue;
	}

	
	
	public void getByMobile() {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		try {
			String uid = this.getPara("uid");
			String mobile = this.getPara("mobile");
			if (StringUtils.isBlank(uid)||StringUtils.isBlank(mobile)) {
				formatInvalidParamResponse(resultMap);
			} else {
				Record r = Db.findFirst("select mobile,mc as driver,cc,cph,cx,cpcs from sys_user where role_id='106' and mobile=?",mobile);
				if(r==null){
					resultMap.put("result", RESULT_FAIL);
					resultMap.put("msg", "不存在手机号为"+mobile+"的司机");
				}else{
					resultMap.put("data", r);	
					formatSuccessResponse(resultMap);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			formatInvokeFailResponse(e, resultMap);
		}

		renderJson(resultMap);
	}

	public void showDetail() {
		String detailType = this.getPara("type");

		String id = this.getPara("id");
		AbFmcar car = AbFmcar.dao.findById(id);

		StringBuffer carcity = new StringBuffer("");
		List<AbFmcarCity> cityList = AbFmcarCity.dao.findByCarId(car
				.getStr("id"));
		if (cityList != null && cityList.size() > 0) {
			int len = cityList.size();
			for (int i = 0; i < (len - 1); i++) {
				carcity.append(cityList.get(i).getStr("city_name") + ",");
			}
			carcity.append(cityList.get(len - 1).getStr("city_name"));
		}

		String citys = carcity.toString().replaceAll("\\d+#", "");

		this.setAttr("carcity", citys);
		this.setAttr("car", car);
		this.setAttr("type", detailType);
		render("/ab/fmcar/my-trucks-detail.html");
	}

}
