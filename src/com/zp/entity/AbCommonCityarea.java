package com.zp.entity;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

public class AbCommonCityarea extends Model<AbCommonCityarea> {

	private static final long serialVersionUID = 1L;
	public static final AbCommonCityarea dao = new AbCommonCityarea();

	public static String TABLE = "ab_common_cityarea";

	/** 编码 * */
	public static final String ID = "id";

	/** 名称 * */
	public static final String MC = "mc";

	/** 上级ID * */
	public static final String P_ID = "p_id";

	/** 上级名称 * */
	public static final String P_NAME = "p_name";

	/** 层次码 * */
	public static final String CCM = "ccm";

	/** 排列顺序 * */
	public static final String SEQ_NUM = "seq_num";

	/** 是否显示 * */
	public static final String IS_DISPLAY = "is_display";

	public static List<AbCommonCityarea> queryLowerLevelArea(String p_id) {
		String param_p_id = null;
		if (p_id == null || p_id.equals("")) {
			param_p_id = "ROOT";
		} else {
			param_p_id = p_id;
		}
		return AbCommonCityarea.dao.find("select * from ab_common_cityarea where p_id=? and is_display='1' order by seq_num asc ", param_p_id);
	}
}