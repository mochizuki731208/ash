package net.yishenghuo.model;

import java.util.ArrayList;
import java.util.List;

public class Category {
	private String aibangurl;
	private String leve;
	private String menu;
	private String name;
	private String numid;
	private String parentid;
	private String sort;
	private List<CategorySub> subs = new ArrayList<CategorySub>();

	public String getAibangurl() {
		return aibangurl;
	}

	public void setAibangurl(String aibangurl) {
		this.aibangurl = aibangurl;
	}

	public String getLeve() {
		return leve;
	}

	public void setLeve(String leve) {
		this.leve = leve;
	}

	public String getMenu() {
		return menu;
	}

	public void setMenu(String menu) {
		this.menu = menu;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumid() {
		return numid;
	}

	public void setNumid(String numid) {
		this.numid = numid;
	}

	public String getParentid() {
		return parentid;
	}

	public void setParentid(String parentid) {
		this.parentid = parentid;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public List<CategorySub> getSubs() {
		return subs;
	}

	public void setSubs(List<CategorySub> subs) {
		this.subs = subs;
	}

}
