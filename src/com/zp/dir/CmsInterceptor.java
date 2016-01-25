package com.zp.dir;

import java.util.List;
import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.core.Controller;
import com.zp.directive.StringKit;
import com.zp.entity.AbCityarea;
import com.zp.entity.AbFmcarGroup;
import com.zp.entity.AbMerchant;
import com.zp.entity.AbSubject;
import com.zp.entity.SysUser;
import com.zp.entity.SysUserType;

/**
 * 自定义标签拦截器
 */
public class CmsInterceptor implements Interceptor {
   
	public void intercept(ActionInvocation ai) {
		 Controller c = ai.getController();
		 c.setAttr("_cms_ctx_list", new CmsNewsListDirective());
		 c.setAttr("_cms_lb_list", new CmsLbListDirective());
		 
		 c.setAttr("_mer_logimg", new AbMerLogimgDirective());
		 c.setAttr("_mer_logimg_list", new AbMerLogimgListDirective());
		 c.setAttr("_mer_logline_list", new AbMerLoglineListDirective());
		 
		 c.setAttr("strkit", new StringKit());
		 
		 //当前城市问题
		 AbCityarea city = c.getSessionAttr("currcity");
		 c.setAttr("currcity", city);
		 //--------------
		 // 临时用于判断用户是否是物流企业
		 //--------------
		 SysUser abuser = c.getSessionAttr("abuser");
		 if(abuser==null||abuser.get("id")==null){
			 ai.invoke();
			 return;
		 }
		 //车辆分组标签
		 List<AbFmcarGroup> group=AbFmcarGroup.dao.findByUserid(abuser.getStr("id"));
		 c.setAttr("cargroup", group);
		 
//		 if(abuser!=null&&abuser.get("id")!=null){
//			 SysUserType usertype = SysUserType.dao.findModel(abuser.get("id"));
//			 c.setAttr("usertp", usertype);
//		 }
		 //这种判断取消
		 AbSubject as = AbSubject.dao.findFirst("select * from ab_subject where mc='物流企业'");
		 AbMerchant amc = AbMerchant.dao.findById(abuser.getStr("id"));
		 if(null!=amc && amc.getStr("subject_id").startsWith(as.getStr("id"))){
			 c.setAttr("iswl", "1");
		 }
		 ai.invoke();
	}
}