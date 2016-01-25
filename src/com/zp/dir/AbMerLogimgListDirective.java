package com.zp.dir;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.zp.entity.AbMerchantLogimg;
import com.zp.tools.StringUtil;

import freemarker.core.Environment;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;


public class AbMerLogimgListDirective implements TemplateDirectiveModel {

	@SuppressWarnings("rawtypes")
	public void execute(Environment env, Map params, TemplateModel[] loopVars,TemplateDirectiveBody body) throws TemplateException, IOException {
    	String lid = StringUtil.toStr(params.get("lid"));
    	String desc = StringUtil.toStr(params.get("desc"));

    	List<AbMerchantLogimg> list = AbMerchantLogimg.dao.getListByInfoShow(lid, desc);
    	 
        env.setVariable("list", ObjectWrapper.DEFAULT_WRAPPER.wrap(list));
        body.render(env.getOut());
    }
 
}