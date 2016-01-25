package com.zp.controller.admin;


import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.zp.entity.AbCityarea;
import com.zp.entity.AbCityareaSubject;
import com.zp.entity.AbSubject;
import com.zp.entity.AbSubjectProduct;
import com.zp.entity.SysAreaSubjectPtf;
import com.zp.entity.SysUser;
import com.zp.interceptor.AccessAdminInterceptor;
import com.zp.tools.CityAttachAdminUtil;
import com.zp.tools.StringUtil;
import com.zp.tools.TreeNodeBean;
@SuppressWarnings("static-access")
public class AbSubjectController extends Controller {
	public void tree(){
		//构建树
		List<AbSubject> list = AbSubject.dao.find("select distinct t.* from ab_subject m,ab_subject t where m.id=t.p_id order by seq_num");
		TreeNodeBean treeNodeBean = new TreeNodeBean("TreeData");
		treeNodeBean.addRootNode("ROOT", "分类首页", false, false);
		String parentID;
		String curtreeID;
		String showText;
		String str_Script = "";

		for (int i = 0; i < list.size(); i++) {
			parentID = list.get(i).getStr("p_id");
			curtreeID = list.get(i).getStr("id");
			showText = list.get(i).getStr("mc");
			treeNodeBean.addTreeNode(parentID,curtreeID,showText,false,false);
		}
		str_Script = treeNodeBean.addTreeTail("div_left");
		this.setAttr("str_Script", str_Script);
		this.render("/admin/subject/tree.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void treeCity(){
		//构建树
				SysUser user = (SysUser)this.getSessionAttr("user");
				//首先判断该管理员是超级管理员还是省级管理员。还是市级管理员
				String SQL = "";
				String root = "";
				if("admin".equals(user.getStr("id"))){
					//超级管理员。操作
					SQL = "select distinct t.* from ab_cityarea m,ab_cityarea t where m.id=t.p_id  order by seq_num";
					root = "ROOT";
				}else{
					List<AbCityarea> tempList = AbCityarea.dao.find("select * from ab_cityarea where user_id= '" + user.getStr("id") + "' order by ccm");
					if(null != tempList && !tempList.isEmpty()){
						for(AbCityarea temp : tempList){
							if(SQL.length() > 0){
								SQL += " union ";	
							}
							SQL += "select * from ab_cityarea where id like '" + temp.getStr("id") + "%'";
							root = root.length() ==0 ? temp.getStr("p_id") :root;
						}
					}else{
						this.setAttr("str_Script", null);
						this.render("/admin/subject/treecity.html");
						return;
					}
				}
				
				
				String sql = "select distinct t.* from ab_cityarea m,ab_cityarea t where m.id=t.p_id";
				if(!"admin".equals(user.getStr("id"))){
					sql += " and t.user_id = '" + user.getStr("id") + "'";
				}
				sql += " order by seq_num";
				List<AbCityarea> list = AbCityarea.dao.find(SQL);
				TreeNodeBean treeNodeBean = new TreeNodeBean("TreeData");
				treeNodeBean.addRootNode(root, "区域分类", false, false);
		String parentID;
		String curtreeID;
		String showText;
		String str_Script = "";

		for (int i = 0; i < list.size(); i++) {
			parentID = list.get(i).getStr("p_id");
			curtreeID = list.get(i).getStr("id");
			showText = list.get(i).getStr("mc");
			treeNodeBean.addTreeNode(parentID,curtreeID,showText,false,false);
		}
		str_Script = treeNodeBean.addTreeTail("div_left");
		this.setAttr("str_Script", str_Script);
		this.render("/admin/subject/treecity.html");
	}
	
	/*
	 * 分页查询带搜索----- 修改过
	 */
	public void list(){
		String p_id = this.getPara("p_id");
		List<AbSubject> list = AbSubject.dao.find("select * from ab_subject where p_id='"+p_id+"' order by ccm,seq_num");
		this.setAttr("list", list);
		this.setAttr("p_id", p_id);
		
		//判断是否为服务类
		AbSubject pSub = AbSubject.dao.findById(p_id);
		if(pSub != null){
			this.setAttr("pSub", pSub);
			List<AbSubject> list2 = AbSubject.dao.find("select * from ab_subject_product where subject_id='"+p_id+"' order by order_num");
			this.setAttr("list2", list2);
		}
		
		this.render("/admin/subject/list.html");
	}
	
	public void listsubject(){
		List<AbSubject> list = AbSubject.dao.find("select * from ab_subject where p_id='ROOT' order by ccm,seq_num");
		this.setAttr("list", list);
		this.render("/admin/subject/listsubject.html");
	}
	
	public void subjectarea(){
		String cityid = this.getPara("cityid");
		String cityName = this.getPara("cityName");
		if(null == cityName || "区域分类".equals(cityName) || cityName.length() == 0){
			this.setAttr("str_Script", null);
			this.setAttr("cityid", null);
			this.setAttr("cityName", null);
			this.render("/admin/subject/subjectarea.html");
			return;
		}
		//构建树
		String sql = "select distinct t.id,t.p_id,t.mc,(SELECT COUNT(id) cnt FROM ab_cityarea_subject m WHERE m.subject_id =t.id AND m.city_id='"+cityid+"' ) cnt from ab_subject m,ab_subject t where m.id=t.p_id order by t.seq_num";
		System.out.println(sql);
		List<AbSubject> list = AbSubject.dao.find(sql);
		TreeNodeBean treeNodeBean = new TreeNodeBean("TreeData");
		treeNodeBean.addRootNode("ROOT", "区域分类", false, false);
		String parentID;
		String curtreeID;
		String showText;
		String str_Script = "";

		for (int i = 0; i < list.size(); i++) {
			parentID = list.get(i).getStr("p_id");
			curtreeID = list.get(i).getStr("id");
			showText = list.get(i).getStr("mc");
			if(curtreeID.startsWith("1017") || curtreeID.startsWith("1018")){
				continue;
			}
			if("1".equals(list.get(i).getLong("cnt").toString())){
				treeNodeBean.addTreeNode(parentID,curtreeID,showText,true,true);
			}else{
				treeNodeBean.addTreeNode(parentID,curtreeID,showText,true,false);
			}
		}
		str_Script = treeNodeBean.addTreeTail("div_left");
		this.setAttr("str_Script", str_Script);
		this.setAttr("cityid", cityid);
		this.render("/admin/subject/subjectarea.html");
	}
	/*
	 * 修改
	 * */
	public void edit(){
		
		String id = this.getPara("id");
		String m = this.getPara("m");
		AbSubject  vo = new AbSubject();
		
		if(StringUtil.toStr(id).length()>0){
			vo = vo.dao.findById(id);
		}else{
			vo.set("p_id", this.getPara("p_id"));
		}
		this.setAttr("method", m);
		this.setAttr("vo", vo);
		render("/admin/subject/edit.html");
	}
	/*
	 * 修改服务商品
	 * */
	public void editFw(){
		
		String id = this.getPara("id");
		String m = this.getPara("m");
		AbSubjectProduct  vo = new AbSubjectProduct();
		
		if(StringUtil.toStr(id).length()>0){
			vo = vo.dao.findById(id);
		}else{
			vo.set("subject_id", this.getPara("p_id"));
		}
		this.setAttr("method", m);
		this.setAttr("vo", vo);
		this.setAttr("p_id", this.getPara("p_id"));
		render("/admin/subject/editFw.html");
	}
	/*
	 * 保存
	 * */
	public void save(){
		
		boolean flag = false;
		
		AbSubject  subject = this.getModel(AbSubject.class,"tb");
		String method = this.getPara("m");
		
		if(subject!=null&&subject.get("p_id")!=null){
			//获取上级菜单的层次码，对层次码进行加1操作。
			subject.set("ccm", AbSubject.dao.findById(subject.getStr("p_id")).getInt("ccm")+1);
		}else{
			subject.set("ccm", 0);
		}
		
		if(!("add".equals(method))){
			subject.update();
		}else{
			//获取最大的ID号加1操作
			if(Db.findFirst("select count(id) cnt from ab_subject where p_id='"+subject.getStr("p_id")+"'").getLong("cnt").intValue()<1){
				subject.set("id",StringUtil.getStrSeqCode(true, subject.getStr("p_id"), 4));
			}else{
				String maxid = Db.findFirst("select id from ab_subject where p_id='"+subject.getStr("p_id")+"' order by id desc").getStr("id");
				subject.set("id",StringUtil.getStrSeqCode(false, maxid, 4));
			}
			
			subject.save();
		}
		flag = true;
		renderJson(flag);
	}
	/*
	 * 保存服务商品
	 * */
	public void saveFw(){
		boolean flag = false;
		AbSubjectProduct  pro = this.getModel(AbSubjectProduct.class,"pro");
		String method = this.getPara("m");
		if(!("add".equals(method))){
			pro.update();
		}else{
			pro.set("id", StringUtil.getUuid32());
			pro.save();
		}
		flag = true;
		renderJson(flag);
	}
	
	public void savesubjectarea(){
		boolean flag = false;
		String cityid = this.getPara("cityid");
		if(null==cityid || cityid.length() == 0){
			this.renderJson(flag);
			return ;
		}
		String subids = this.getPara("subids");
		Db.update("delete from ab_cityarea_subject where city_id='"+cityid+"'");
		//重新插入
		if(subids!=null&&subids.length()>0){
			String[] m = subids.split(",");
			for (int i = 0; i < m.length; i++) {
				AbCityareaSubject.dao
				.set("id", StringUtil.getUuid32())
				.set("subject_id", m[i])
				.set("city_id",cityid).save();
			}
		}
		flag = true;
		this.renderJson(flag);
	}
	
	public void initYuanGong(){
		String subjectid = StringUtil.toStr(this.getPara("subjectid"));
		String cityid = StringUtil.toStr(this.getPara("cityid"));
		AbCityareaSubject aca = AbCityareaSubject.dao.findFirst("select * from ab_cityarea_subject where city_id=? and subject_id=?",cityid,subjectid);
		this.setAttr("aca", aca);
		SysUser user = this.getSessionAttr("user");
		String sql = "select * from sys_user where role_id='104'";
		if(!"admin".equals(user.getStr("id"))){
			sql += " and (city_id in (" + CityAttachAdminUtil.getCityByAdmin(user.getStr("id")) + ")";
			sql += " or area_id in (" + CityAttachAdminUtil.getCityByAdmin(user.getStr("id")) + "))";
		}
		List<SysUser> YGList = SysUser.dao.find(sql);
		this.setAttr("YGList", YGList);
		this.render("/admin/subject/initYuanGong.html");
	}
	
	public void doAuthToYuanGong(){
		String userid = StringUtil.toStr(this.getPara("userid"));
		String subjectid = StringUtil.toStr(this.getPara("subjectid"));
		String cityid = StringUtil.toStr(this.getPara("cityid"));
		if(userid.length() ==0 || subjectid.length()==0 || cityid.length()==0){
			this.renderJson(false);
		}else{
			Db.update("delete from ab_cityarea_subject where city_id='"+cityid+"' and subject_id='" + subjectid + "'");
			AbCityareaSubject.dao
			.set("id", StringUtil.getUuid32())
			.set("subject_id",subjectid)
			.set("user_id",userid)
			.set("city_id",cityid).save();
			this.renderJson(true);
		}
	}
	
	public void delete(){
		String id = this.getPara("id");
		boolean flag = AbSubject.dao.deleteById(id);
		this.renderJson(flag);
	}
	public void deleteFw(){
		String id = this.getPara("id");
		boolean flag = AbSubjectProduct.dao.deleteById(id);
		this.renderJson(flag);
	}
	
	@Before(AccessAdminInterceptor.class)
	public void tree_sau(){
		SysUser user = (SysUser)this.getSessionAttr("user");
		String sql = "select distinct t.* from ab_cityarea m,ab_cityarea t where m.id=t.p_id";
		if(!"admin".equals(user.getStr("id"))){
			sql += " and t.user_id = '" + user.getStr("id") + "'";
		}
		sql += " order by seq_num";
		//构建树
		List<AbCityarea> list = AbCityarea.dao.find(sql);
		TreeNodeBean treeNodeBean = new TreeNodeBean("TreeData");
		if(!"admin".equals(user.getStr("id")) && null!= list && !list.isEmpty()){
			treeNodeBean.addRootNode(list.get(0).getStr("id").substring(0, 3), "区域分类", false, false);
		}else{
			treeNodeBean.addRootNode("ROOT", "区域分类", false, false);
		}
		String parentID;
		String curtreeID;
		String showText;
		String str_Script = "";

		for (int i = 0; i < list.size(); i++) {
			parentID = list.get(i).getStr("p_id");
			curtreeID = list.get(i).getStr("id");
			showText = list.get(i).getStr("mc");
			treeNodeBean.addTreeNode(parentID,curtreeID,showText,false,false);
		}
		str_Script = treeNodeBean.addTreeTail("div_left");
		this.setAttr("str_area", str_Script);
		
		//构建树
		List<AbSubject> list_subjet = AbSubject.dao.find("select distinct t.* from ab_subject m,ab_subject t where m.id=t.p_id order by seq_num");
		treeNodeBean = new TreeNodeBean("TreeData");
		treeNodeBean.addRootNode("ROOT", "分类首页", false, false);

		for (int i = 0; i < list_subjet.size(); i++) {
			parentID = list_subjet.get(i).getStr("p_id");
			curtreeID = list_subjet.get(i).getStr("id");
			showText = list_subjet.get(i).getStr("mc");
			treeNodeBean.addTreeNode(parentID,curtreeID,showText,false,false);
		}
		str_Script = treeNodeBean.addTreeTail("div_left");
		this.setAttr("str_subject", str_Script);
		
		str_Script = treeNodeBean.addTreeTail("div_left");
		this.setAttr("str_Script", str_Script);
		this.render("/admin/subject/tree_sau.html");
	}
	
	@Before(AccessAdminInterceptor.class)
	public void editptf(){
		
		String area_id = this.getPara("area_id");
		String subject_id = this.getPara("subject_id");
		
		String sql = "SELECT * FROM sys_area_subject_ptf WHERE area_id='"+area_id+"' AND subject_id='"+subject_id+"'";
		Record r = Db.findFirst(sql);
		if(r==null){
			r= new Record();
		}
		this.setAttr("area_id", area_id);
		this.setAttr("subject_id", subject_id);
		this.setAttr("r", r);
		this.render("/admin/subject/editptf.html");
	}
	
	/**
	 * 保存跑腿人员信息
	 */
	@Before(AccessAdminInterceptor.class)
	public void save_ptf(){
		boolean flag = false;
		String id = StringUtil.toStr(this.getPara("id"));
		String subject_id = StringUtil.toStr(this.getPara("subject_id"));
		String area_id = StringUtil.toStr(this.getPara("area_id"));
		String srvmoney = StringUtil.toStr(this.getPara("srvmoney"));
		if(id.length()>0){
			SysAreaSubjectPtf.dao.findById(id).set("srvmoney", srvmoney).update();
		}else{
			SysAreaSubjectPtf.dao
			.set("id", StringUtil.getRandString32())
			.set("subject_id", subject_id)
			.set("area_id", area_id)
			.set("srvmoney", srvmoney)
			.save();
		}
		flag = true;
		this.renderJson(flag);
	}
	
	/**
	 * 城市、分类树。
	 */
	@Before(AccessAdminInterceptor.class)
	public void list_citysubject(){
		//查询当前登录号负责的商圈
		SysUser user = this.getSessionAttr("user");
		List<AbCityarea> list_city = AbCityarea.dao.find("select * from ab_cityarea where user_id='"+user.getStr("id")+"'");
		//构建树
		List<AbSubject> list_subjet = AbSubject.dao.find("select distinct t.* from ab_subject m,ab_subject t where m.id=t.p_id order by seq_num");
		TreeNodeBean treeNodeBean = new TreeNodeBean("TreeData");
		treeNodeBean.addRootNode("ROOT", "分类首页", false, false);
		String parentID;
		String curtreeID;
		String showText;
		String str_Script = "";
		
		for (int i = 0; i < list_subjet.size(); i++) {
			parentID = list_subjet.get(i).getStr("p_id");
			curtreeID = list_subjet.get(i).getStr("id");
			showText = list_subjet.get(i).getStr("mc");
			treeNodeBean.addTreeNode(parentID,curtreeID,showText,false,false);
		}
		str_Script = treeNodeBean.addTreeTail("div_left");
		this.setAttr("list_city", list_city);
		this.setAttr("str_subject", str_Script);
		this.render("/admin/subject/list_areamng.html");
	}
}
