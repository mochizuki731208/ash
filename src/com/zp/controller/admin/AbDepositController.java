package com.zp.controller.admin;


import java.io.UnsupportedEncodingException;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.zp.entity.LogUserDeposit;
@SuppressWarnings("static-access")
public class AbDepositController extends Controller {
	
	public static String decode(String ss){
		try {
			return java.net.URLDecoder.decode(ss, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	 * 分页查询带搜索----- 修改过
	 */
	public void list(){
		
		String fromsql = "from log_user_deposit  order by id asc";
		Object []arr = new String[0];
		String keyword = this.getPara(1, "1");
		if(!keyword.equals("1"))
		     keyword = decode(keyword);
		
		if(keyword.equals("1"))
			keyword = this.getPara("keyword", "1");
		
		if(!keyword.equals("1")){
			arr= new String[6];
			arr[0] = keyword;
			arr[1] = keyword;
			arr[2] = keyword;
			arr[3] = keyword;
			arr[4] = keyword;
			arr[5] = keyword;
			fromsql = "from log_user_deposit where instr(username,?)>0 or instr(createtime,?)>0 or instr(money,?)>0  or instr(money,?)>0 or instr(status,?)>0   or instr(remark,?)>0 order by id asc";
			this.setAttr("keyword", keyword);
		}
	
		Page<LogUserDeposit> loguserdepositpage = LogUserDeposit.dao.paginate(
				this.getParaToInt(0,1), 2,"select  * ", fromsql, arr);
		
		this.setAttr("loguserdepositpage", loguserdepositpage);
		this.render("/admin/log/loguserdeplist.html");
	}

	/*
	 * 删除
	 * */
	public void delete(){
		String id = this.getPara(0);
		LogUserDeposit.dao.deleteById(id);
		this.setAttr("msg", "alert('删除成功');");
		this.forwardAction("/admin/loguserdeposit/list/");
	}
}
