package net.yishenghuo.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CityVo implements Serializable{
	private String city;
	private String key;
	private String cate;
	private List<CityVo> subs = new ArrayList<CityVo>();
	
	public List<CityVo> getSubs() {
		return subs;
	}

	public void setSubs(List<CityVo> subs) {
		this.subs = subs;
	}

	public CityVo() {
	}

	public CityVo(String city, String key) {
		this.city = city;
		this.key = key;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getCate() {
		return cate;
	}

	public void setCate(String cate) {
		this.cate = cate;
	}
}
