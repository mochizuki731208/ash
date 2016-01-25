package com.zp.dir;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zp.entity.AbSubject;
import com.zp.entity.CmsNrfl;
import com.zp.tools.StringUtil;

import freemarker.core.Environment;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;


public class CmsLbListDirective implements TemplateDirectiveModel {
    @SuppressWarnings("unchecked")
	public void execute(Environment env, Map params, TemplateModel[] loopVars,TemplateDirectiveBody body) throws TemplateException, IOException {
    	List<CmsNrfl> list = new ArrayList<CmsNrfl>();
    	String sjfl = StringUtil.toStr(params.get("sjfl"));
    	String lbmc = StringUtil.toStr(params.get("lbmc"));
    	
    	String sql = "select * from cms_nrfl where id is not null ";
    	
    	if(sjfl.length()>0){
	   		sql+=" and sjfl='"+sjfl+"'";
	   	}
    	
    	if(lbmc.length()>0){
	   		sql+=" and lbmc like '%"+lbmc+"%'";
	   	}
    	
    	sql +=" order by sxh";
       
    	list = CmsNrfl.dao.find(sql);
    	 
        env.setVariable("list", ObjectWrapper.DEFAULT_WRAPPER.wrap(list));
        body.render(env.getOut());
    }
 
}