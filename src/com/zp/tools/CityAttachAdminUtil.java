package com.zp.tools;

import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.zp.entity.AbCityarea;
import com.zp.entity.SysUser;

public class CityAttachAdminUtil {
	/**
	 * 根据管理员ID去查询城市ID集合
	 * @param id
	 * @return
	 */
	public static String getCityByAdmin(String id){
		String result = "";
		
		String SQL = "";
		if("admin".equals(id)){
			SQL = "select id from ab_cityarea where id != 'ROOT'";
		}else{
			List<AbCityarea> tempList = AbCityarea.dao.find("select * from ab_cityarea where user_id= '" + id+ "' order by ccm");
			if(null != tempList && !tempList.isEmpty()){
				for(AbCityarea temp : tempList){
					if(SQL.length() > 0){
						SQL += " union ";	
					}
					SQL += "select id from ab_cityarea where id like '" + temp.getStr("id") + "%'";
				}
			}else{
				SQL = "select id from ab_cityarea where id ='111111111111111111'";
			}
		}
		
		List<String> resultList = Db.query(SQL);
		if(null != resultList && !resultList.isEmpty()){
			for(String str : resultList){
				result += str + ",";
			}
			result = result.substring(0, result.length() - 1);
		}else{
			result = "1234567890";
		}
		return result;
	}
	//获取员工管辖的商家类型
	public static String getAuthSubjectIdsByUserId(String userid){
		String result = "";
		String sql = "select subject_id from ab_cityarea_subject where user_id='" + userid + "'";
		List<String> subjectIds = Db.query(sql);
		if(null != subjectIds && !subjectIds.isEmpty()){
			for(String str : subjectIds){
				result += str + ",";
			}
			result = result.substring(0, result.length() - 1);
		}else{
			result = "1234567890";
		}
		return result;
	}
	public static void main(String[] args) {
		System.out.println(getCityByAdmin("admin"));
	}
}
