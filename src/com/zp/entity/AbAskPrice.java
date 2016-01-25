package com.zp.entity;

import com.jfinal.plugin.activerecord.Model;

public class AbAskPrice extends Model<AbAskPrice> {

	private static final long serialVersionUID = 1L;

	public static final AbAskPrice aap = new AbAskPrice();

	// 主键
	private String id;

	// 询价单类型
	private String ask_price_type;
	
	// 起始地省份
	private String source_province;

	// 起始地城市
	private String source_city;

	// 起始地地区
	private String source_area;

	// 目的地省份
	private String target_province;

	// 目的地城市
	private String target_city;

	// 目的地地区
	private String target_area;

	// 货物名称
	private String goods_name;

	// 货物类型[1:铺货 2:重货 3:泡货]
	private String goods_type;

	// 重量/体积
	private String goods_size;

	// 用车时间
	private String usecar_date;

	// 备注
	private String remark;

	// 联系人
	private String linkman;

	// 手机
	private String mobile;

	// 用户ID
	private String user_id;

	// 下单时间
	private String askprice_date;
}
