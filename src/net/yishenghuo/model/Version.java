package net.yishenghuo.model;

public class Version extends BaseBean{

	private String state = "Y";
    private String code = "0";
    private String nVerAppUrl = "http://jdxt.lesonet.com/modules/versions/JDXT.apk";
    private String nVerDesc ="1.修改了注册流程和文字提示\n2.调整了目录\n3.简化了密码\n4.其他界面调整";
    private String nVerCode ="1.0";
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getnVerAppUrl() {
		return nVerAppUrl;
	}
	public void setnVerAppUrl(String nVerAppUrl) {
		this.nVerAppUrl = nVerAppUrl;
	}
	public String getnVerDesc() {
		return nVerDesc;
	}
	public void setnVerDesc(String nVerDesc) {
		this.nVerDesc = nVerDesc;
	}
	public String getnVerCode() {
		return nVerCode;
	}
	public void setnVerCode(String nVerCode) {
		this.nVerCode = nVerCode;
	}
}
