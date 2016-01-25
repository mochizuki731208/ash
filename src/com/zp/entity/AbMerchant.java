package com.zp.entity;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jfinal.plugin.activerecord.Model; 

public class AbMerchant extends Model<AbMerchant>{

    private static final long serialVersionUID = 1L;
    public static final AbMerchant dao = new AbMerchant();

    public static String TABLE = "ab_merchant";

	/** 主键 **/
    public static final String ID = "id";

	/** 商家名称 **/
    public static final String MC = "mc";

	/** 一级分类ID **/
    public static final String SUBJECT_ID = "subject_id";

	/** 一级分类名称 **/
    public static final String SUBJECT_NAME = "subject_name";

	/** 类型[0-货物类、1-服务类] **/
    public static final String IS_TYPE = "is_type";

	/** 所属城市 **/
    public static final String CITY_ID = "city_id";

	/** 所属城市名称 **/
    public static final String CITY_NAME = "city_name";

	/** 所属商业区 **/
    public static final String AREA_ID = "area_id";

	/** 所属商业区名称 **/
    public static final String AREA_NAME = "area_name";

	/** 邮政编码 **/
    public static final String YZBM = "yzbm";

	/** 详细地址 **/
    public static final String XXDZ = "xxdz";

	/** 手机号 **/
    public static final String MOBILE = "mobile";

	/** QQ号码 **/
    public static final String QQ = "qq";

	/** 微博 **/
    public static final String WEIBO = "weibo";

	/** 微信 **/
    public static final String WEIXIN = "weixin";

	/** 客户评级 **/
    public static final String RATE = "rate";

	/** 服务评级 **/
    public static final String GRADE = "grade";

	/** 商户信用等级 **/
    public static final String CREDIT = "credit";

	/** 商家简介 **/
    public static final String BRIEF = "brief";

	/** 经度 **/
    public static final String LNG = "lng";

	/** 维度 **/
    public static final String LAT = "lat";

	/** 工作时间 **/
    public static final String WORKTIME = "worktime";

	/** 是否推荐[0-不推荐、1-推荐] **/
    public static final String IS_RECOMMEND = "is_recommend";

	/** 推荐开始时间 **/
    public static final String TJKSSJ = "tjkssj";

	/** 推荐截止时间 **/
    public static final String TJJZSJ = "tjjzsj";

	/** 效果图 **/
    public static final String IMG_URL = "img_url";

	/** 缩略图 **/
    public static final String THUMBNAIL_URL = "thumbnail_url";

	/** 企业LOGO **/
    public static final String LOGO = "logo";

	/** 是否自送[0-否、1-是] **/
    public static final String SFZS = "sfzs";

	/** 最低配送金额 **/
    public static final String ZDPSJE = "zdpsje";

	/** 非工作日是否允许下单 **/
    public static final String CHK_FGZRXD = "chk_fgzrxd";

	/** 是否选择提供发票 **/
    public static final String CHK_TGFP = "chk_tgfp";

	/** 提供发票[0-否、1-是] **/
    public static final String TGFP = "tgfp";

	/** 是否选择优惠活动 **/
    public static final String CHK_YHHD = "chk_yhhd";

	/** 买多少 **/
    public static final String MDS = "mds";

	/** 减多少 **/
    public static final String JDS = "jds";

	/** 是否选择送达时间 **/
    public static final String CHK_SDSJ = "chk_sdsj";

	/** 送达时间（分钟） **/
    public static final String SDSJ = "sdsj";

	/** 超时赔付 **/
    public static final String CSPF = "cspf";

	/** 推荐排序 **/
    public static final String SEQ_NUM = "seq_num";

	/** 营业状态[0-已打烊、1-营业中、2-休息中] **/
    public static final String BUSINESS_STATUS = "business_status";

	/** 审核状态[0-未审核、1-已审核] **/
    public static final String CHECK_STATUS = "check_status";

	/** 是否显示[0-不显示、1-显示] **/
    public static final String IS_DISPLAY = "is_display";

	/** 注册时间 **/
    public static final String CREATE_TIME = "create_time";

	/** 用户ID **/
    public static final String USER_ID = "user_id";

	/** 日访问数 **/
    public static final String VIEWS_DAY = "views_day";

	/** 日评论数 **/
    public static final String COMMENTS_DAY = "comments_day";

	/** 日下载数 **/
    public static final String VIEWS_MONTH = "views_month";

	/** 日顶数 **/
    public static final String COMMENTS_MONTH = "comments_month";

	/** 总评论数 **/
    public static final String COMMENTS_TOTAL = "comments_total";

	/** 点击量 **/
    public static final String VIEW_TOTAL = "view_total";
    
    /** 佣金比 **/
    public static final String YJ = "yj";

	/** 备注 **/
    public static final String REMARK = "remark";
    
    public List<AbMerchant> findByFields(Map<String, String> fields){
    	StringBuffer sql = new StringBuffer("select * from ab_merchant where 1=1 ");
		
		if(fields.size() != 0){
			Set<String> set = fields.keySet();
			for (String key : set) {
				sql.append("and " + key + " = ? "); 
			}
		}
		
		Object[] params = fields.values().toArray();
		
		List<AbMerchant> list = dao.find(sql.toString(), params);
		
		return list;
    }
 }