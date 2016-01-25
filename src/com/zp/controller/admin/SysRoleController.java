package com.zp.controller.admin;

import java.util.List;

import com.jfinal.core.Controller;
import com.jfinal.kit.StringKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.zp.entity.SysMenu;
import com.zp.entity.SysRole;
import com.zp.entity.SysRoleMenu;
import com.zp.entity.SysUser;
import com.zp.tools.StringUtil;
import com.zp.tools.TreeNodeBean;
@SuppressWarnings("static-access")
public class SysRoleController extends Controller {
	/**
	 * 获取员工信息列表
	 */
	public void listcityuser(){
		//获取默认管理员
		List<SysUser> list = SysUser.dao.find("select * from sys_user where p_id=1 order by create_date");
		this.setAttr("list", list);
		render("/admin/city/listcityuser.html");
	}
	
	public void rolemenu(){
		String str_roleid = this.getPara("roleid");
		//构建树
		List<SysMenu> list = SysMenu.dao.find("SELECT DISTINCT t.*,(SELECT COUNT(id) cnt FROM sys_role_menu m WHERE m.menu_id =t.id AND role_id='"+str_roleid+"' ) cnt FROM sys_menu m,sys_menu t WHERE m.id=t.p_id ORDER BY t.seq_num");
		TreeNodeBean treeNodeBean = new TreeNodeBean("TreeData");
		treeNodeBean.addRootNode("ROOT", "系统功能", false, false);
		String parentID;
		String curtreeID;
		String showText;
		String str_Script = "";

		for (int i = 0; i < list.size(); i++) {
			parentID = list.get(i).getStr("p_id");
			curtreeID = list.get(i).getStr("id");
			showText = list.get(i).getStr("mc");
			if("1".equals(list.get(i).getLong("cnt").toString())){
				treeNodeBean.addTreeNode(parentID,curtreeID,showText,true,true);
			}else{
				treeNodeBean.addTreeNode(parentID,curtreeID,showText,true,false);
			}
			
		}
		str_Script = treeNodeBean.addTreeTail("div_left");
		this.setAttr("roleid", str_roleid);
		this.setAttr("str_Script", str_Script);
		this.render("/admin/role/rolemenu.html");
	}
	public void listrole(){
		List<SysRole> list = SysRole.dao.find("select * from sys_role order by sxh");
		this.setAttr("list", list);
		render("/admin/role/listrole.html");
	}
	
	public void list(){
		List<SysRole> list = SysRole.dao.find("select * from sys_role order by sxh");
		this.setAttr("list", list);
		render("/admin/role/list.html");
	}
	
	@SuppressWarnings("deprecation")
	public void edit(){
		String id = this.getPara("id");
		Record vo = new Record();
		if(!StringKit.isBlank(id)){
			vo = Db.findById("sys_role", id);
			//vo = Db.findById("sys_role", "jsbm",id,"jsbm,jsmc,sxh,ddesc");
			//vo = Db.findFirst("select * from sys_role where jsbm='"+id+"'");
		}
		this.setAttr("vo", vo);
		render("/admin/role/edit.html");
	}
	/*
	 * 保存
	 * */
	public void save(){
		SysRole role = this.getModel(SysRole.class,"role");
		if(role!=null&&role.getStr("id")!=null){
			role.update();
		}else{
			//获取最大的ID号加1操作
			if(Db.findFirst("select count(id) cnt from sys_role").getLong("cnt").intValue()<1){
				role.set("id","1000");
			}else{
				String maxid = Db.findFirst("select id from sys_role order by id desc").getStr("id");
				role.set("id",StringUtil.getStrSeqCode(false, maxid, 3));
			}
			role.save();
		}
		this.redirect("/admin/role/list");
	}
	
	public void saverolemenu(){
		boolean flag = false;
		//获取角色ID
		String roleid = this.getPara("roleid");
		String menuids = this.getPara("menuids");
		//删除之前的关联关系，重新插入所选的菜单数据
		Db.update("delete from sys_role_menu where role_id='"+roleid+"'");
		//重新插入
		if(menuids!=null&&menuids.length()>0){
			String[] m = menuids.split(",");
			for (int i = 0; i < m.length; i++) {
				SysRoleMenu.dao
				.set("id", StringUtil.getUuid32())
				.set("role_id", roleid)
				.set("menu_id",m[i]).save();
			}
		}
		flag = true;
		this.renderJson(flag);
	}
	
	/*
	 * 删除
	 * */
	public void delete(){
		String id = this.getPara("id");
		boolean flag = SysRole.dao.deleteById(id);
		this.renderJson(flag);
	}
	
}
