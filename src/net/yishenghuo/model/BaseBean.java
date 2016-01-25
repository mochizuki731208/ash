package net.yishenghuo.model;

import java.io.Serializable;

public class BaseBean implements Serializable{
	
	private boolean isSucess = true;
	private String msg;
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean isSucess() {
		return isSucess;
	}

	public void setSucess(boolean isSucess) {
		this.isSucess = isSucess;
	}
}
