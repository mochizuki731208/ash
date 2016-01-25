package net.yishenghuo.model;

import java.util.ArrayList;
import java.util.List;

import com.zp.entity.AbMerchantProduct;

public class McCategory {
	private String id;
	private String name;
	private List<AbMerchantProduct> subs = new ArrayList<AbMerchantProduct>();
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<AbMerchantProduct> getSubs() {
		return subs;
	}
	public void setSubs(List<AbMerchantProduct> subs) {
		this.subs = subs;
	}

}
