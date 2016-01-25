package com.zp.dir;

import java.io.IOException;
import java.util.Map;

import com.zp.entity.AbMerchantLogimg;
import com.zp.tools.StringUtil;

import freemarker.core.Environment;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;


public class AbMerLogimgDirective implements TemplateDirectiveModel {

	@SuppressWarnings("rawtypes")
	public void execute(Environment env, Map params, TemplateModel[] loopVars,TemplateDirectiveBody body) throws TemplateException, IOException {
    	String lid = StringUtil.toStr(params.get("lid"));
    	String desc = StringUtil.toStr(params.get("desc"));
    	String num = StringUtil.toStr(params.get("num"));
       
    	AbMerchantLogimg img = AbMerchantLogimg.dao.findByInfo(lid, num, desc);
    	 
        env.setVariable("img", ObjectWrapper.DEFAULT_WRAPPER.wrap(img));
        body.render(env.getOut());
    }
 
}