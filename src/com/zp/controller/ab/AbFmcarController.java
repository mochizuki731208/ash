package com.zp.controller.ab;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;
import com.zp.entity.AbFmcar;
import com.zp.entity.AbFmcarCity;
import com.zp.entity.AbFmcarGroup;
import com.zp.entity.AbFmcarUser;
import com.zp.entity.AbSjPosition;
import com.zp.entity.SysMobileBlank;
import com.zp.entity.SysUser;
import com.zp.handler.DbPage;
import com.zp.handler.PageUtil;
import com.zp.tools.SmsMessage;
import com.zp.tools.StringUtil;

public class AbFmcarController extends Controller {
	
	public void index(){
		SysUser user = (SysUser) this.getSessionAttr("abuser");
		if( user == null ){ //登录
			render("/ab/login.html");
		}else{
			List<AbFmcar> carlist = AbFmcar.dao.findByUserId(user.getStr("id"),this.getPara("group"));
			List<AbFmcarGroup> grouplist=AbFmcarGroup.dao.findByUserid(user.getStr("id"));
			this.setAttr("carlist", carlist);
			this.setAttr("grouplist", grouplist);
			render("/ab/fmcar/my-trucks.html");
		}
	}

	public void checkMobile(){
		Map<String, Object> result = new HashMap<String, Object>();
		SysUser user = getUser();
		if(user == null){
			result.put("result", "未登录");
			renderJson(result);
			return;
		}
		String userId = user.getStr("id");
		String mobile = this.getPara("mobile");
//		Map<String, String> fields = new HashMap<String, String>();
//		fields.put("mobile", mobile);
//		List<AbFmcar> list = AbFmcar.dao.findByFields(fields);
//		if (list != null && list.size() > 0) {
//			AbFmcar car = list.get(0);
//			result.put("car", car);
//			boolean isExist = AbFmcarUser.dao.isExistUserIdAndCarId(userId,
//					car.getStr("id"));
//			if (isExist) {
//				result.put("result", "mine");
//			} else {
//				result.put("result", "notMine");
//			}
//		} else {
//			result.put("result", "notExist");
//		}
		SysUser driver=SysUser.dao.findFirst("select role_id,role_name from sys_user where loginid=?",mobile);
		result.put("driver", driver);
		
		Map<String, String> fields = new HashMap<String, String>();
		fields.put("mobile", mobile);
		List<AbFmcar> list = AbFmcar.dao.findByFields(fields);
		if (list != null && list.size() > 0) {
			AbFmcar car = list.get(0);
			result.put("car", car);
			boolean isExist = AbFmcarUser.dao.isExistUserIdAndCarId(userId,
					car.getStr("id"));
			if (isExist) {
				result.put("car", "mine");
			} else {
				result.put("car", "notExist");
			}

		} else {
			result.put("car", "notExist");
		}
		
		renderJson(result);
		
	}
	
	public boolean isExistUserIdAndCarId(String userId,String mobile){
		Map<String, String> fields = new HashMap<String, String>();
		fields.put("mobile", mobile);
		List<AbFmcar> list = AbFmcar.dao.findByFields(fields);
		if(list != null && list.size() > 0){
			AbFmcar car = list.get(0);
			return AbFmcarUser.dao.isExistUserIdAndCarId(userId,car.getStr("id"));
		}else{
			return false;
		}
	}
	
	//取得我的熟车详细信息
	public void getCarUserInfo(){
		SysUser user=getUser();
		if(user==null){
			renderJson(false);
			return;
		}
		Map<String,Object>resultMap=new HashMap<String,Object>();
		String userId=user.getStr("id");
		String mobile=getPara("mobile");
		Map<String, String> fields = new HashMap<String, String>();
		fields.put("mobile", mobile);
		List<AbFmcar> list = AbFmcar.dao.findByFields(fields);
		
		if(list != null && list.size() > 0){
			AbFmcar car = list.get(0);
			if(car!=null){
				resultMap.put("carInfo", car);
			AbFmcarCity  afc = new AbFmcarCity();
			List<AbFmcarCity> cityList = afc.findByCarId(car.getStr(AbFmcar.ID));
			resultMap.put("cityList", cityList);			
			}			
		}
		 renderJson( resultMap);
		
	}
	
	public void addFmcar(){
		String mobile = "";
		SysUser user = getUser();
		if(user == null){
			renderJson(false);
		}else{
			String userId = user.getStr("id");
			
			AbFmcar car = getModel(AbFmcar.class);
			String carId = car.getStr("id");
			if(StringUtils.isBlank(car.getStr("id"))){
				if(!isExistUserIdAndCarId(userId,car.getStr("mobile"))){//防止客户一直点击，造成同号表里多条记录的行为
					carId = StringUtil.getRandString32();
					car.set("id", carId);
					car.save();
					
					AbFmcarUser carUser = new AbFmcarUser();
					carUser.set("id", StringUtil.getRandString32());
					carUser.set("car_id", carId);
					carUser.set("user_id", userId);
					carUser.set("group_id", this.getPara("groupid"));
					carUser.save();
				}
			}else{
				car.update();
			}
			
			AbFmcarCity.dao.delByCarId(carId);
			
			String runcity = this.getPara("runcity");
			if(!StringUtil.isNull(runcity)){
				String[] citys = runcity.split(",",-1);
				for (String city : citys) {
					if(!StringUtil.isNull(city)){
						AbFmcarCity fmcarCity = new AbFmcarCity();
						fmcarCity.set("id", StringUtil.getRandString32());
						fmcarCity.set("car_id", carId);
						fmcarCity.set("city_name", city);
						fmcarCity.save();
					}
				}
			}
			mobile = car.getStr("mobile");
			
			try {
				String msgContent = "您好，你熟悉的货主【" + user.getStr("mc") + "】正在使用易生活APP发货，为了方便给您下单，诚邀您来注册，注册链接地址http://www.yijuhome.cn/，谢谢";
				SysUser runUser = SysUser.dao.findFirst("select * from sys_user where loginid=? and role_id=?",mobile,"106");
				
				if(runUser == null){  //没有该跑腿人员，则需要发短信注册
					SmsMessage.SendMessage(mobile, msgContent);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			renderJson(true);
		}
	}
	
	public void searchFmcar(){
		SysUser user = getUser();
		if(user == null){
			index();
		}else{
			String userId = user.getStr("id");
			
			Map<String, String> fields = new HashMap<String, String>();
			
			String singleSearchField = this.getPara("nameOrMobile");
			if(!StringUtil.isNull(singleSearchField)){
				if(singleSearchField.trim().startsWith("1")){  //手机号码
					fields.put("mobile", singleSearchField);
				}else{
					fields.put("driver", singleSearchField);
				}
			}
			
			String carLength = this.getPara("car_length");
			if(!StringUtil.isNull(carLength)){
				fields.put("length", carLength);
			}
			
			String carType = this.getPara("car_type");
			if(!StringUtil.isNull(carType)){
				fields.put("type", carType);
				
			}
			
			String[] citys = null;
			String carCitys = this.getPara("carCitys"); 
			if(!StringUtil.isNull(carCitys)){
				citys = carCitys.split("[,|，]",-1);
			}

			String groupid=this.getPara("car_group");
			if(groupid!=null&&groupid.equals(""))
				groupid=null;
			List<AbFmcar> carlist = AbFmcar.dao.findByFields(fields,citys,userId,groupid);
			this.setAttr("carlist", carlist);
			this.setAttr("nameOrMobile", singleSearchField);
			
			List<AbFmcarGroup> grouplist=AbFmcarGroup.dao.findByUserid(user.getStr("id"));
			this.setAttr("grouplist", grouplist);

			render("/ab/fmcar/my-trucks.html");
		}
	}
	
	public void delFmcarUser(){
		SysUser user = getUser();
		if(user == null){
			renderJson(false);
		}else{
			String userId = user.getStr("id");
			String carId = this.getPara("carId");
			System.err.println(carId);
			AbFmcarUser.dao.delByUserIdAndCarId(userId, carId);
			renderJson(true);
		}
	}
	
	public void addFmcarUser(){
		SysUser user = getUser();
		if(user == null){
			renderJson(false);
		}else{
			String userId = user.getStr("id");
			String carId = this.getPara("carId");
			String groupId=this.getPara("groupid");
			String id = StringUtil.getRandString32();
			AbFmcarUser fmcarUser = new AbFmcarUser();
			fmcarUser.set("id", id);
			fmcarUser.set("user_id", userId);
			fmcarUser.set("car_id", carId);
			fmcarUser.set("group_id", groupId);
			fmcarUser.save();
			renderJson(true);
		}
	}
	
	public void uploadCars(){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		SysUser user = getUser();
		if(user == null){
			resultMap.put("totalNum", 0);
			resultMap.put("successNum", 0);
			resultMap.put("errorMsg", "用户未登录");
			resultMap.put("errorMobile", "");
			renderJson(false);
		}else{
			String userId = user.getStr("id");
			String recommendNo = this.getPara("recommendNo");
			
			Workbook wb = null;
			Sheet sheet = null;
			FileInputStream is = null;
			
			try {
				UploadFile f = this.getFile();
				File excel = f.getFile();
				is = new FileInputStream(excel);
				String fileName = excel.getName();
				if (StringUtils.isBlank(fileName)){
					renderJson("导入文档为空!");
					return;
				}else if(fileName.toLowerCase().endsWith("xls")){    
					wb = new HSSFWorkbook(is);    
		        }else if(fileName.toLowerCase().endsWith("xlsx")){  
		        	wb = new XSSFWorkbook(is);
		        }else{  
		        	renderJson("文档格式不正确!");
		        	return;
		        }  
				
				if (wb.getNumberOfSheets() < 0){
					renderJson("文档中没有工作表!");
					return;
				}
				
				sheet = wb.getSheetAt(0);
				
				Map<String, String> mobileMap = new HashMap<String, String>();
				
				AbFmcar car = null;
				AbFmcarUser carUser = null;
				AbFmcarCity carCity = null;
				int start = sheet.getFirstRowNum() + 1; //标题不计
				int end = sheet.getLastRowNum();
				int totalNum = 0;
				int successNum = 0;
				StringBuffer errorMsg = new StringBuffer("");
				StringBuffer errorMobile = new StringBuffer("");
				for (int i = start; i <= end; i++) {
					String mobile = "";
					try {
						Row row = sheet.getRow(i);
						int index = row.getFirstCellNum();
						String carId = StringUtil.getRandString32();
						String carNo = getCellValue(row.getCell(index+1));
						String driver = getCellValue(row.getCell(index+2));
						mobile = getCellValue(row.getCell(index));
						if(carNo.equals("")||mobile.equals("")||driver.equals(""))
							continue;
						
						if(StringUtils.isBlank(mobile) || mobileMap.containsKey(mobile)){ //有重复电话号码记录
							continue;
						}else{  //根据手机号码判断是否已经存在记录了，存在则不加
							totalNum ++;
							AbFmcar existCar = AbFmcar.dao.findByMobile(mobile);
							if(existCar != null){
								String existCarId = existCar.getStr("id");
								boolean flag = AbFmcarUser.dao.isExistUserIdAndCarId(userId,existCarId);
								if(!flag){
									carUser = new AbFmcarUser();
									carUser.set("id", StringUtil.getRandString32());
									carUser.set("car_id", existCarId);
									carUser.set("user_id", userId);
									carUser.save();
									successNum++;
								}else{
									errorMsg.append("该熟车已经存在;");
									errorMobile.append(mobile+";");
								}
								continue;
							}
						}
						
						mobileMap.put(mobile, "");
						
						String length = getCellValue(row.getCell(index+3));
						String type = getCellValue(row.getCell(index+4));
						String vv=getCellValue(row.getCell(index+5));
						//String is_net = getCellValue(row.getCell(index++));
//						if("是".equals(is_net)){
//							is_net = "1";
//						}else{
//							is_net = "0";
//						}
						
						car = new AbFmcar();
						car.set("id", carId);
						car.set("car_no", carNo);
						car.set("driver", driver);
						car.set("mobile", mobile);
						car.set("length", length);
						car.set("type", type);
						car.set("vv", vv);
						car.set("is_locate", "0");
						car.set("location", "");
						car.set("location_time", null);
						car.set("state", "");
						car.set("recommend_no", recommendNo);
						car.set("is_protect", "0");
						car.set("remark", "excel导入.");
						car.save();
						
						carUser = new AbFmcarUser();
						carUser.set("id", StringUtil.getRandString32());
						carUser.set("car_id", carId);
						carUser.set("user_id", userId);
						carUser.save();
						
						JSONArray cityList=JSONArray.parseArray(readCityList("/ab/fmcar/city2.html"));
						
						String citysStr = getCellValue(row.getCell(index+6));
						if(StringUtils.isNotBlank(citysStr)){
							String[] citys = citysStr.split("[,|，]",-1);
							for (String city : citys) {
								carCity = new AbFmcarCity();
								carCity.set("id", StringUtil.getRandString32());
								carCity.set("car_id", carId);
								
								JSONObject cityj=null;
								int z;
								for(z=0;z<cityList.size();z++){
									cityj=cityList.getJSONObject(z);
									if(cityj.getString("text").contains(city))
										break;
								}
								if(cityj!=null&&(z<cityList.size())){
									carCity.set("city_name", cityj.getString("id"));
									carCity.save();
								}
								
							}
						}
						
						successNum ++;
					} catch (Exception e) {
						e.printStackTrace();
						errorMsg.append(e.getMessage());
						errorMobile.append(mobile + ";");
					}
				}
				
				resultMap.put("totalNum", totalNum);
				resultMap.put("successNum", successNum);
				resultMap.put("errorMsg", errorMsg.toString());
				resultMap.put("errorMobile", errorMobile.toString());
			} catch (Exception e) {
				e.printStackTrace();
				renderJson(e.getMessage());
				return;
			}finally{
				try {
					if(is!=null){
						is.close();
					}
				} catch (Exception e2) {
				}
			}
			renderJson(resultMap);
		}
		
	}
	private String readCityList(String filename){
		//File file = new File(filename);
        Reader reader = null;
        String result="";
        try {
            //System.out.println("以字符为单位读取文件内容，一次读一个字节：");
            // 一次读一个字符
            reader = new InputStreamReader(this.getSession().getServletContext().getResourceAsStream(filename));
            int tempchar;
            while ((tempchar = reader.read()) != -1) {
                // 对于windows下，\r\n这两个字符在一起时，表示一个换行。
                // 但如果这两个字符分开显示时，会换两次行。
                // 因此，屏蔽掉\r，或者屏蔽\n。否则，将会多出很多空行。
                if (((char) tempchar) != '\r') {
                    //System.out.print((char) tempchar);
                	result+=(char) tempchar;
                }
            }
            reader.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
	}
	
	//下载批量添加熟车的模板文件
	public void dowloadTempletFile(){
		this.renderFile("/ab/fmcar/我的专属车.xls");
	}
	private String getCellValue(Cell cell){
		String cellValue = "";
		if(cell != null){
			int cellType = cell.getCellType();
			switch(cellType){
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
	
	public void edit(){
		this.setAttr("mobile", this.getPara("mobile"));
		render("/ab/fmcar/my-trucks-edit.html");
	}
	
	public void showDetail(){
		String detailType = this.getPara("type");
		
		String id = this.getPara("id");
		AbFmcar car = AbFmcar.dao.findById(id);
		
		StringBuffer carcity = new StringBuffer("");
		List<AbFmcarCity> cityList = AbFmcarCity.dao.findByCarId(car.getStr("id")); 
		if(cityList != null && cityList.size() > 0){
			int len = cityList.size();
			for(int i=0;i<(len-1);i++){
				carcity.append(cityList.get(i).getStr("city_name") + ",");
			}
			carcity.append(cityList.get(len-1).getStr("city_name"));
		}
		
		String citys = carcity.toString().replaceAll("\\d+#", "");
		
		this.setAttr("carcity", citys);
		this.setAttr("car", car);
		this.setAttr("type", detailType);
		render("/ab/fmcar/my-trucks-detail.html");
	}
	
	
	//----------------------------------------------------------------------------------
	//--------------------------跑腿人员车辆信息维护-------------------------------------
	//----------------------------------------------------------------------------------
	public void ywyIndex(){
		SysUser user = (SysUser) this.getSessionAttr("abuser");  //跑腿人员
		if( user == null ){ //登录
			render("/ab/login.html");
		}else{
			String mobile = user.getStr("loginid");//获取当前登录人的帐号 也就是电话号码
			AbFmcar car = AbFmcar.dao.findByMobile(mobile);//根据电话号码查询对应的熟车
			if (car == null) {
				car = new AbFmcar();
				car.set("mobile", mobile);
			}else{
				String id = car.getStr("id");
				
				StringBuffer carcity = new StringBuffer("");
				List<AbFmcarCity> cityList = AbFmcarCity.dao.findByCarId(id); 
				if(cityList != null && cityList.size() > 0){
					int len = cityList.size();
					for(int i=0;i<(len-1);i++){
						carcity.append(cityList.get(i).getStr("city_name") + ",");
					}
					carcity.append(cityList.get(len-1).getStr("city_name"));
				}
				this.setAttr("carcity", carcity.toString());
			}
			
			this.setAttr("car", car);
			this.setAttr("vo", user);
			
			render("/ab/fmcar/my-trucks-ywy.html");
		}
	}
	
	public void addFmcarywy(){
		SysUser user = getUser();
		if(user == null){
			renderJson(false);
		}else{
			AbFmcar car = getModel(AbFmcar.class);
			String carId = car.getStr("id");
			if(StringUtils.isBlank(carId)){
				carId = StringUtil.getRandString32();
				car.set("id", carId);
				car.save();
				
				/*AbFmcarUser carUser = new AbFmcarUser();
				carUser.set("id", StringUtil.getRandString32());
				carUser.set("car_id", carId);
				carUser.set("user_id", userId);
				carUser.save();*/
			}else{
				car.update();
			}
			
			AbFmcarCity.dao.delByCarId(carId);
			
			String runcity = this.getPara("runcity");
			if(!StringUtil.isNull(runcity)){
				String[] citys = runcity.split(",",-1);
				for (String city : citys) {
					if(!StringUtil.isNull(city)){
						AbFmcarCity fmcarCity = new AbFmcarCity();
						fmcarCity.set("id", StringUtil.getRandString32());
						fmcarCity.set("car_id", carId);
						fmcarCity.set("city_name", city);
						fmcarCity.save();
					}
				}
			}
			
			/*this.setAttr("carcity", runcity);
			this.setAttr("car", car);
			
			render("/ab/fmcar/my-trucks-ywy.html");*/
			
			renderJson(true);
		}
	}
	
	/*public void addYwyCar(){
		this.setAttr("mobile", this.getPara("mobile"));
		render("/ab/fmcar/my-trucks-edit.html");
	}
	
	public void editYwyCar(){
		String id = this.getPara("id");
		AbFmcar car = AbFmcar.dao.findById(id);
		this.setAttr("car", car);
		this.setAttr("mobile", car.getStr("mobile"));
		
		StringBuffer carcity = new StringBuffer("");
		List<AbFmcarCity> cityList = AbFmcarCity.dao.findByCarId(id); 
		if(cityList != null && cityList.size() > 0){
			int len = cityList.size();
			for(int i=0;i<(len-1);i++){
				carcity.append(cityList.get(i).getStr("city_name") + ",");
			}
			carcity.append(cityList.get(len-1).getStr("city_name"));
		}
		this.setAttr("carcity", carcity.toString());
		
		render("/ab/fmcar/my-trucks-edit.html");
	}*/
	
	private SysUser getUser() {
		SysUser user = (SysUser) this.getSessionAttr("abuser");
		
		if(user == null){
			user = (SysUser) this.getSessionAttr("user");
		}
		return user;
	}
	/**
	 * 进入黑名单页面
	 */
	public void initBlankMobile(){
		SysUser user = (SysUser) this.getSessionAttr("abuser");  //跑腿人员
		if( user == null ){ //登录
			this.render("/ab/login.html");
		}else{
			String sql = "select * from sys_mobile_blank where userid='" + user.getStr("id") + "'";
			PageUtil listpage = DbPage.paginate(this.getParaToInt("curPage", 1), 14,sql);
			this.setAttr("listpage", listpage);
			this.render("/ab/fmcar/myblankmobile.html");
		}
	}
	/**
	 * 增加黑名单
	 */
	public void addBlankMobile(){
		String balnkMobile = StringUtil.toStr(this.getPara("balnkMobile"));
		if(balnkMobile.length() == 0){
			this.render("/ab/fmcar/addblankmobile.html");
			return;
		}
		SysUser user = (SysUser) this.getSessionAttr("abuser");
		Map<String,Object> result =  new HashMap<String,Object>();
		if(balnkMobile.length() > 0){
			List<SysMobileBlank> smb_List = SysMobileBlank.dao.find("select * from sys_mobile_blank where userid=? and mobile=?", user.getStr("id"),balnkMobile);
			if(null!=smb_List && !smb_List.isEmpty()){
				result.put("result", "blankmobileisnotnull");
				renderJson(result);
			}else{
				SysMobileBlank smb = new SysMobileBlank();
				smb.set("id", StringUtil.getRandString32());
				smb.set("userid", user.getStr("id"));
				smb.set("mobile", balnkMobile);
				if(smb.save()){
					result.put("result", "addsuccess");
					renderJson(result);
				}else{
					result.put("result", "adderror");
					renderJson(result);
				}
			}
		}else{
			result.put("result", "mobileisnull");
			renderJson(result);//参数错误
		}
	}
	/**
	 * 删除黑名单
	 */
	public void deleteBlankMobile(){
		String balnkMobile = StringUtil.toStr(this.getPara("balnkMobile"));
		SysUser user = (SysUser) this.getSessionAttr("abuser");
		Map<String,Object> result =  new HashMap<String,Object>();
		if(balnkMobile.length() > 0){
			SysMobileBlank smb = SysMobileBlank.dao.findFirst("select * from sys_mobile_blank where userid=? and mobile=?", user.get("id"),balnkMobile);
			if(null!=smb){
				if(smb.delete()){
					result.put("result", "deletesuccess");
					renderJson(result);
				}else{
					result.put("result", "deleteeror");
					renderJson(result);
				}
			}else{
				result.put("result", "blankmobileisnull");
				renderJson(result);
			}
		}else{
			result.put("result", "mobileisnull");
			renderJson(result);//参数错误
		}
	}
	
	public void canXiaDan(){
		String mobile = StringUtil.toStr(this.getPara("mobile"));
		if(mobile.length() == 0){
			renderJson(false);
			return;
		}
		AbFmcar car = AbFmcar.dao.findFirst("select * from ab_fmcar where mobile=?", mobile);
		if(null==car || StringUtil.toStr(car.getStr("istrue")).equals("0")){
			renderJson(false);
			return;
		}
		
		SysUser user = SysUser.dao.findFirst("select * from sys_user where mobile=? and loginid=?", mobile,mobile);
		if(null==user){
			renderJson(false);
			return;
		}
		
		AbSjPosition po = AbSjPosition.dao.findFirst("select * from ab_sj_position where sjid=?", user.getStr("id"));
		if(null==po){
			renderJson(false);
			return;
		}
		renderJson(true);
	}
}
