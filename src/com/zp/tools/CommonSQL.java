package com.zp.tools;

public class CommonSQL {
	// 查询当前商户所有留言记录
	public static String getLeaveByMid(String id) {
		StringBuffer sb = new StringBuffer(
				"select aml.*, su.mc, su.mobile from ");
		sb.append("(select * from ab_merchant_leave where mer_id = '");
		sb.append(id)
				.append("') aml left join sys_user su on aml.user_id = su.id order by datetime desc ");
		return sb.toString();
	}

	// 查询当前商户所有评价记录
	public static String getAppraiseByMid(String id) {
		StringBuffer sb = new StringBuffer("select * from ( ");
		sb.append("select aa.*, su.mc, am.mc mmc, ao.sn from ");
		sb.append("(select * from ab_appraise where  mer_id = '").append(id).append("') aa ");
		sb.append("left join sys_user su on aa.user_id = su.id ");
		sb.append("left join ab_merchant am on aa.mer_id = am.id ");
		sb.append("left join ab_order ao on aa.order_id = ao.id ");
		sb.append(") t where 1=1 ");
		return sb.toString();
	}
	
	//查询跑腿人员关联评价记录,跑腿人员ID
	public static String getAppraiseByQdrid(String id){
		StringBuffer sb = new StringBuffer("select * from ( ");
		sb.append("select aa.*, su.mc, am.mc mmc, ao.sn from ");
		sb.append("(select * from ab_appraise where  qdr_id = '").append(id).append("') aa ");
		sb.append("left join sys_user su on aa.user_id = su.id ");
		sb.append("left join ab_merchant am on aa.mer_id = am.id ");
		sb.append("left join ab_order ao on aa.order_id = ao.id ");
		sb.append(") t where 1=1 ");
		return sb.toString();
	}

	// 查询当前商户所有评价记录
	public static String getAppraiseByMidAndType(String id, String type) {
		StringBuffer sb = new StringBuffer("select * from ( ");
		sb.append("select aa.*, su.mc, am.mc mmc, ao.sn from ");
		sb.append("(select * from ab_appraise where mer_id = '");
		sb.append(id).append("' and type = '").append(type).append("') aa ");
		sb.append("left join sys_user su on aa.user_id = su.id ");
		sb.append("left join ab_merchant am on aa.mer_id = am.id ");
		sb.append("left join ab_order ao on aa.order_id = ao.id ");
		sb.append(") t where 1=1  ");
		return sb.toString();
	}

	// 查询当前商户所有好评记录
	public static String getAppraiseTypeByMid(String id, String type) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from ab_appraise aa ");
		sb.append(" where 1=1 and mer_id = '").append(id).append("'");
		sb.append(" and type = '" + type + "'");
		return sb.toString();
	}
	
	// 查询跑腿人员评价记录
		public static String getAppraiseTypeByQdrid(String id, String type) {
			StringBuffer sb = new StringBuffer();
			sb.append("select * from ab_appraise aa ");
			sb.append(" where 1=1 and qdr_id = '").append(id).append("'");
			sb.append(" and type = '" + type + "'");
			return sb.toString();
		}

	public static String getAppraiseImgByAppId(String id) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from ab_appraise_img where app_id = '");
		sb.append(id).append("'");
		return sb.toString();
	}

	/* 查询当前用户积分变动记录, data为2是收入，3为支出 */
	public static String getScoreChangeByUserId(String id, String data) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from (");

		sb.append("select ais.*, aig.image, aig.img_url, aig.title, '' sn from ");
		sb.append(" (select * from ab_integral_score where user_id = '")
				.append(id).append("' and type = '07') ais ");
		sb.append(" left join ab_integral_exchange aie on ais.fid = aie.id ");
		sb.append(" left join ab_integral_goods aig on aig.id = aie.goods_id ");

		sb.append(" union ");
		sb.append(
				" select ais.*, '' image, '' img_url, '用户注册' title, '' sn from ab_integral_score ais where user_id = '")
				.append(id).append("' and type = '01' ");
		sb.append(" union ");
		sb.append(
				" select ais.*, '' image, '' img_url, '用户每日登陆' title, '' sn from ab_integral_score ais where user_id = '")
				.append(id).append("' and type = '02' ");
		sb.append(" union ");
		sb.append(
				" select ais.*, '' image, '' img_url, '邀请好友注册' title, '' sn from ab_integral_score ais where user_id = '")
				.append(id).append("' and type = '05' ");
		
		sb.append(" union ");
		sb.append(
				" select ais.*, '' image, '' img_url, '消费获得' title, ao.sn from (select * from ab_integral_score where user_id = '")
				.append(id).append("' and type = '06') ais left join ab_order ao on ais.fid = ao.id ");
		
		sb.append(" union ");
		sb.append(
				" select ais.*, '' image, '' img_url, '退单扣除' title, ao.sn from (select * from ab_integral_score where user_id = '")
				.append(id).append("' and type = '07') ais left join ab_order ao on ais.fid = ao.id ");
		
		sb.append(") t where 1=1 ");
		if ("2".equals(data)) {
			sb.append(" and t.value > 0");
		}
		if ("3".equals(data)) {
			sb.append(" and t.value < 0");
		}
		sb.append(" order by t.datetime desc");
		return sb.toString();
	}
	
	/* 查询所有的退单记录 */
	public static String findAllChargeBack(String sn, String mnname) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from (");
		sb.append("select su.mc, su.mobile, ao.mname, ao.subject_name, ao.ddzje, ao.xdrmc,ao.qdrname, ");
		sb.append("ao.sn, ss.mc ssmc, sy.mc symc, aoc.* from ab_order_chargeback aoc ");
		sb.append("left join sys_user su on aoc.apply_id = su.id ");
		sb.append("left join sys_user ss on aoc.service_id = ss.id ");
		sb.append("left join sys_user sy on aoc.judge_id = sy.id ");
		sb.append("left join ab_order ao on aoc.order_id = ao.id ");
		sb.append(" ) t where 1=1 ");
		if(!StringUtil.isNull(sn)){
			sb.append(" and t.sn like '%").append(sn).append("%'");
		}
		if(!StringUtil.isNull(mnname)){
			sb.append(" and t.mnname like '%").append(mnname).append("%'");
		}
		sb.append("order by t.apply_time desc");
		return sb.toString();
	}

	/*商户退单查询*/
	public static String findChargeBackForMerchant(String mid, String sn, String mc, String mobile, String status) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from (");
		sb.append("select su.mc, su.mobile, ao.mname, ao.qdrname, ao.xdrmc, ao.subject_name, ao.ddzje, ");
		sb.append("ao.sn, aoc.* from ");
		sb.append("(select * from ab_order_chargeback where mer_id = '").append(mid).append("') aoc ");
		sb.append("left join sys_user su on aoc.apply_id = su.id ");
		sb.append("left join ab_order ao on aoc.order_id = ao.id ");
		sb.append(" ) t where 1=1 ");
		if(!StringUtil.isNull(sn)){
			sb.append(" and t.sn like '%").append(sn).append("%'");
		}
		if(!StringUtil.isNull(mc)){
			sb.append(" and t.mc like '%").append(mc).append("%'");
		}
		if(!StringUtil.isNull(mobile)){
			sb.append(" and t.mobile like '%").append(mobile).append("%'");
		}
		if(!StringUtil.isNull(status)){
			sb.append(" and t.status = '").append(status).append("'");
		}
		sb.append(" order by t.apply_time desc");
		return sb.toString();
	}
	
	/*用户退单查询*/
	public static String findChargeBackForUser(String userid, String sn, String mnname, String status) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from (");
		sb.append("select su.mc, su.mobile, ao.mname, ao.subject_name, ao.ddzje, ao.xdrmc,ao.qdrname,");
		sb.append("ao.sn, aoc.* from ");
		sb.append("(select * from ab_order_chargeback where xdr_id = '").append(userid).append("') aoc ");
		sb.append("left join sys_user su on aoc.apply_id = su.id ");
		sb.append("left join ab_order ao on aoc.order_id = ao.id ");
		sb.append(" ) t where 1=1 ");
		if(!StringUtil.isNull(sn)){
			sb.append(" and t.sn like '%").append(sn).append("%'");
		}
		if(!StringUtil.isNull(mnname)){
			sb.append(" and t.mnname like '%").append(mnname).append("%'");
		}
		if(!StringUtil.isNull(status)){
			sb.append(" and t.status = '").append(status).append("'");
		}
		sb.append(" order by t.apply_time desc");
		return sb.toString();
	}
	
	/*业务员退单查询*/
	public static String findChargeBackForService(String sid, String sn, String mc, String mobile, String status) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from (");
		sb.append("select su.mc, su.mobile, ao.mname, ao.subject_name, ao.ddzje, ao.xdrmc,ao.qdrname,");
		sb.append("ao.sn, aoc.* from ");
		sb.append("(select * from ab_order_chargeback where qdr_id = '").append(sid).append("') aoc ");
		sb.append("left join sys_user su on aoc.apply_id = su.id ");
		sb.append("left join ab_order ao on aoc.order_id = ao.id ");
		sb.append(" ) t where 1=1 ");
		if(!StringUtil.isNull(sn)){
			sb.append(" and t.sn like '%").append(sn).append("%'");
		}
		if(!StringUtil.isNull(mc)){
			sb.append(" and t.mc like '%").append(mc).append("%'");
		}
		if(!StringUtil.isNull(mobile)){
			sb.append(" and t.mobile like '%").append(mobile).append("%'");
		}
		if(!StringUtil.isNull(status)){
			sb.append(" and t.status = '").append(status).append("'");
		}
		sb.append(" order by t.apply_time desc");
		return sb.toString();
	}
	

	public static String getChargebackById(String cbid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from (");
		sb.append("select su.mc, su.mobile, ao.mname, ao.subject_name, ao.ddzje, ao.xdrmc,ao.qdrname,  ");
		sb.append("ao.sn, ss.mc ssmc, aoc.* from ");
		sb.append("(select * from ab_order_chargeback where id = '").append(cbid).append("') aoc ");
		sb.append("left join sys_user su on aoc.apply_id = su.id ");
		sb.append("left join ab_order ao on aoc.order_id = ao.id ");
		sb.append("left join sys_user ss on aoc.service_id = ss.id ");
		sb.append(" ) t where 1=1 ");
		return sb.toString();
	}
	
	public static String getChargebackItemByCbid(String cbid) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from ab_order_chargeback_item where cbid = '").append(cbid).append("' ");
		sb.append("order by datetime desc");
		return sb.toString();
	}
	
	public static String getUserCards(String userid){
		StringBuffer sb = new StringBuffer("select * from ( ");
		sb.append("select acu.p_datetime, p_way, remark, ac.*, act.name, act.image, act.img_url, act.money, act.title, act.type, act.price, act.valid_days ");
		sb.append(" from (select * from ab_card_user where user_id = '").append(userid).append("') acu ");
		sb.append(" left join ab_card ac on acu.card_id = ac.id left join ab_card_type act on ac.type_id = act.id ) t where 1=1 ");
		return sb.toString();
	}
	
	//查询有效消费卡或会员卡
	public static String findUserCard(String userid, String type){
		StringBuffer sb = new StringBuffer("select * from (");
		sb.append("select ac.*, act.type from (");
		sb.append("select * from ab_card where status = '").append(CommonStaticData.CARD_STATUS_VALID).append("' ");
		sb.append(" and active = '").append(CommonStaticData.CARD_STATUS_VALID).append("' ");
		sb.append(" and purchase_status = '").append(CommonStaticData.CARD_PURCHASE_VALID).append("' ");
		sb.append(" and id in (select card_id from ab_card_user where user_id = '");
		sb.append(userid).append("')) ac left join ab_card_type act on ac.type_id = act.id ) t where 1=1 ");
		sb.append(" and type = '").append(type).append("' ");
		sb.append(" order by expire_time asc");
		return sb.toString();
	}
	
	//会员卡消费情况
	public static String findAllCardExpense(){
		StringBuffer sb = new StringBuffer();
		sb.append("select * from (");
		sb.append("select ace.*, ao.sn, ao.mname, ao.xdrmc, ao.qdrname, act.type, act.name, ac.card_number, ao.xdsj, acu.user_id from ab_card_expense ace ");
		sb.append("left join ab_order ao on ace.order_id = ao.id ");
		sb.append("left join ab_card ac on ace.card_id = ac.id ");
		sb.append("left join ab_card_type act on act.id = ac.type_id ");
		sb.append("left join ab_card_user acu on ace.card_id = acu.card_id ");
		sb.append(") t where 1=1 ");
		return sb.toString();
	}
	
	//会员卡补助
	public static String findAddExpense(){
		StringBuffer sb = new StringBuffer();
		sb.append("select * from (");
		sb.append("select a.card_number, a.create_time, a.rmoney, b.*, c.name, c.money, c.price, c.type, acu.user_id from ( ");
		sb.append("select * from ab_card where id in (select id from ab_card where status = '0') "); 
		sb.append(") a left join ( ");
		sb.append("select card_id, SUM(money) deduction from ab_card_expense where card_id in (select id from ab_card where status = '0') group by card_id ");
		sb.append(") b on a.id = b.card_id ");
		sb.append("left join ab_card_type c on a.type_id = c.id ");
		sb.append("left join ab_card_user acu on a.id = acu.card_id ");
		sb.append(") t where 1=1 ");
		return sb.toString();
	}	
	
	/* 查询所有的退单记录 */
	public static String findChargeBackCount(String cnt, String mnname) {
		StringBuffer sb = new StringBuffer();
		sb.append("select * from (");
		sb.append("select aly.*, su.mc from ( ");
		sb.append("select apply_id id, count(apply_id) cnt from ab_order_chargeback ");
//		sb.append("where apply_time > '"+startdate+"' and apply_time < '"+enddate+"' ");
		sb.append("group by apply_id ");
		sb.append(") aly left join sys_user su on aly.id = su.id ");
		sb.append(") t where 1=1 ");
		if(!StringUtil.isNull(cnt)){
			sb.append(" and t.cnt >= ").append(cnt).append("");
		}
		if(!StringUtil.isNull(mnname)){
			sb.append(" and t.mc like '%").append(mnname).append("%'");
		}
		return sb.toString();
	}
}
