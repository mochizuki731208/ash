package com.zp.controller.ab;

import java.util.List;

import com.jfinal.core.Controller;
import com.zp.entity.AbFmcarGroup;
import com.zp.entity.SysUser;
import com.zp.tools.StringUtil;

public class AbFmcarGroupController extends Controller {
	public void index(){
		SysUser user = (SysUser) this.getSessionAttr("abuser");
		if( user == null ){ //登录
			render("/ab/login.html");
		}else{
			List<AbFmcarGroup> grouplist = AbFmcarGroup.dao.findByUserid(user.getStr("id"));
			this.setAttr("grouplist", grouplist);
			render("/ab/fmcar/truck-group-list.html");
		}

	}
	
	public void editgroup(){
		SysUser user = (SysUser) this.getSessionAttr("abuser");
		if( user == null ){ //登录
			render("/ab/login.html");
		}else{
			String groupid=this.getPara("id");
			if(groupid!=null){
				AbFmcarGroup group= AbFmcarGroup.dao.findById(groupid);
				this.setAttr("group", group);
				this.setAttr("method", "edit");
			}else{
				this.setAttr("method", "append");				
			}
				
			render("/ab/fmcar/truck-group-edit.html");
		}
		
	}
	
	public void save(){
		SysUser user = (SysUser) this.getSessionAttr("abuser");
		if( user == null ){ //登录
			render("/ab/login.html");
		}else{
			String method=this.getPara("method");
//			if(getPara("group.id")!=null){
			if(method.equalsIgnoreCase("edit")){
				AbFmcarGroup group=this.getModel(AbFmcarGroup.class);
				group.set("id", getPara("id"));
				group.set("groupname", getPara("groupname"));
				
				group.update();
			}else{
				AbFmcarGroup group=new AbFmcarGroup();
				group.set("id", StringUtil.getRandString32());
				group.set("groupname", getPara("groupname"));
				group.set("user_id", user.get("id"));
				group.save();
			}
				
			this.redirect("/ab/fmcargroup");
		}
		
	}
	public void deletebyid(){
		SysUser user = (SysUser) this.getSessionAttr("abuser");
		if( user == null ){ //登录
			render("/ab/login.html");
		}else{
			String id=getPara("id");
			if(id!=null){
				AbFmcarGroup.dao.deleteById(id);
			}
		}
		//redirect("/ab/fmcargroup");
		renderJson(true);
	}
}
