package com.zp.entity;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jfinal.plugin.activerecord.Model;

public class AbSubject extends Model<AbSubject>{

    private static final long serialVersionUID = 1L;
    public static final AbSubject dao = new AbSubject();

    public static String TABLE = "ab_subject";

	/** 主键 **/
    public static final String ID = "id";

	/** 名称 **/
    public static final String MC = "mc";

	/** 层次 **/
    public static final String CCM = "ccm";

	/** 上级ID **/
    public static final String P_ID = "p_id";

	/** 是否禁用[0-禁用、1-可用] **/
    public static final String IS_ENABLE = "is_enable";

	/** 是否显示[0-隐藏、1-显示] **/
    public static final String IS_DISPLAY = "is_display";

	/** 类型[0-货物类、1-服务类] **/
    public static final String IS_TYPE = "is_type";

	/** 排序号 **/
    public static final String SEQ_NUM = "seq_num";

	/** 分类描述 **/
    public static final String DDESC = "ddesc";

	/** 分类地址 **/
    public static final String CLASS_PATH = "class_path";

	/** 备注 **/
    public static final String REMARK = "remark";
    
    
    public List<AbSubject> findByFields(Map<String, String> fields){
    	StringBuffer sql = new StringBuffer("select * from ab_subject where 1=1 ");
		
		if(fields.size() != 0){
			Set<String> set = fields.keySet();
			for (String key : set) {
				sql.append("and " + key + " = ? "); 
			}
		}
		
		sql.append("order by seq_num");
		
		Object[] params = fields.values().toArray();
		
		List<AbSubject> list = dao.find(sql.toString(), params);
		
		return list;
    }
 }