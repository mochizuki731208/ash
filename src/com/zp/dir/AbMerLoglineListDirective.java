package com.zp.dir;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.zp.entity.AbAppraise;
import com.zp.entity.AbMerchantLogline;
import com.zp.handler.DbPage;
import com.zp.tools.CommonSQL;
import com.zp.tools.StringUtil;

import freemarker.core.Environment;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;


public class AbMerLoglineListDirective implements TemplateDirectiveModel {

	@SuppressWarnings("rawtypes")
	public void execute(Environment env, Map params, TemplateModel[] loopVars,TemplateDirectiveBody body) throws TemplateException, IOException {
    	String lid = StringUtil.toStr(params.get("lid"));

    	List<AbMerchantLogline> list = AbMerchantLogline.dao.getListByInfoShow(lid);
    	 
        env.setVariable("list", ObjectWrapper.DEFAULT_WRAPPER.wrap(list));
        
        //获得商家评价信息
        String id = StringUtil.toStr(params.get("id"));
        if(StringUtils.isEmpty(id)){
        	Record merchant = DbPage.findFirst("select  m.* from  ab_merchant_logistics AS w  LEFT JOIN ab_merchant AS m ON m.id = w.mid where  w.id=?;", lid);
            id = merchant.getStr("id"); 
        }
      
        //
        List<Record> pjRecords = DbPage.find("select  count(1) count,type from ab_appraise aa where mer_id=? GROUP BY type",id);
        
        List<Record> list_comm = null;
        if(!StringUtils.isEmpty(StringUtil.toStr(params.get("list_comm")))){ 
        	list_comm = Db.find(CommonSQL.getAppraiseByMid(id));
        }
       /*List<AbAppraise> aprsone = AbAppraise.dao.find(CommonSQL.getAppraiseTypeByMid(id, "1"));
		List<AbAppraise> aprstwo = AbAppraise.dao.find(CommonSQL.getAppraiseTypeByMid(id, "2"));
		List<AbAppraise> aprsthree = AbAppraise.dao.find(CommonSQL.getAppraiseTypeByMid(id, "3"));
		*/
        //from ab_merchant t where check_status='1'  
        Map<String,Object> pj = new HashMap<String, Object>();
        
        
        //初始化为0
        int all = 0;
        pj.put("count_app1", 0);
        pj.put("count_app2", 0);
        pj.put("count_app3", 0);
        for (Record pjrecord : pjRecords) {
        	all += pjrecord.getLong("count"); 
        	pj.put("count_app"+pjrecord.getStr("type"), pjrecord.getLong("count"));
		}
        pj.put("count_appall", all);
        /*
        //pj.put("count_appall", list_comm == null ?0:list_comm.size());
        pj.put("count_appone", aprsone == null ?0:aprsone.size());
        pj.put("count_apptwo", aprstwo == null ?0:aprstwo.size());
        pj.put("count_appthree", aprsthree == null ?0:aprsthree.size());
        */
       // pj.put("merchant", merchant);
        //评价分数  
       // env.setVariable("merchant", ObjectWrapper.DEFAULT_WRAPPER.wrap(merchant));
        env.setVariable("merchantMap", ObjectWrapper.DEFAULT_WRAPPER.wrap(pj));
        env.setVariable("list_comm", ObjectWrapper.DEFAULT_WRAPPER.wrap(list_comm));
        body.render(env.getOut());
    }
 
}