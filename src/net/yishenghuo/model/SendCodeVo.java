package net.yishenghuo.model;

public class SendCodeVo extends BaseBean{
	private String phone;
	private String loginnum;
	private String type;

	private String password;
	private String code;

	public SendCodeVo() {
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getLoginnum() {
		return loginnum;
	}

	public void setLoginnum(String loginnum) {
		this.loginnum = loginnum;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
