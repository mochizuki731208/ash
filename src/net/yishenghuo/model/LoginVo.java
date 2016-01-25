package net.yishenghuo.model;

public class LoginVo extends BaseBean{
	private String loginnum;
	private String phone;
	private String password;

	public LoginVo() {
	}

	public String getLoginnum() {
		return loginnum;
	}

	public void setLoginnum(String loginnum) {
		this.loginnum = loginnum;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
}
