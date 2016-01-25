package com.zp.dir;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zp.entity.CmsContent;
import com.zp.tools.StringUtil;

import freemarker.core.Environment;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;


public class CmsNewsListDirective implements TemplateDirectiveModel {
    @SuppressWarnings("unchecked")
	public void execute(Environment env, Map params, TemplateModel[] loopVars,TemplateDirectiveBody body) throws TemplateException, IOException {
    	List<CmsContent> list = new ArrayList<CmsContent>();
    	String lbid = StringUtil.toStr(params.get("lbid"));
    	String orderby = StringUtil.toStr(params.get("orderby"));
    	String pagenum = StringUtil.toStr(params.get("pagenum"));
    	
    	String sql = "select * from cms_content where id is not null ";
    	
    	if(lbid.length()>0){
	   		sql+=" and lbid='"+lbid+"'";
	   	}
    	
    	if(orderby.length()>0){
	   		sql+=" order by '"+orderby+"' ";
	   	}else{
	   		sql+=" order by release_date desc ";
	   	}
    	if(pagenum.length()>0){
    		sql +=" limit 0, "+pagenum;
	   	}
       
    	list = CmsContent.dao.find(sql);
    	 
        env.setVariable("list", ObjectWrapper.DEFAULT_WRAPPER.wrap(list));
        body.render(env.getOut());
    }
 
}