package com.zp.dir;

import java.io.IOException;
import java.util.Map;

import com.zp.entity.AbMerchantLogistics;
import com.zp.tools.StringUtil;

import freemarker.core.Environment;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;


public class AbMerLogisticsDirective implements TemplateDirectiveModel {

	@SuppressWarnings("rawtypes")
	public void execute(Environment env, Map params, TemplateModel[] loopVars,TemplateDirectiveBody body) throws TemplateException, IOException {
    	String lid = StringUtil.toStr(params.get("lid"));
       
    	AbMerchantLogistics lo = AbMerchantLogistics.dao.findById(lid);
    	 
        env.setVariable("lo", ObjectWrapper.DEFAULT_WRAPPER.wrap(lo));
        body.render(env.getOut());
    }
 
}