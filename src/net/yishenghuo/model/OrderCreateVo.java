package net.yishenghuo.model;

import java.util.List;

public class OrderCreateVo {
	private List<Order> orderlist;
	private String openid;
	private String mobile;

	public OrderCreateVo() {
	}

	public OrderCreateVo(List<Order> orderlist, String openid, String mobile) {
		this.orderlist = orderlist;
		this.openid = openid;
		this.mobile = mobile;
	}

	public List<Order> getOrderlist() {
		return orderlist;
	}

	public void setOrderlist(List<Order> orderlist) {
		this.orderlist = orderlist;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
}
