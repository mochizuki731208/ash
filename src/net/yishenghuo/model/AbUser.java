package net.yishenghuo.model;

import java.util.Date;

public class AbUser extends BaseBean{

	private String logid = ""; // token
	private String id;
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private String loginnum;// 账号
	private String password;
	private Integer type;// 账户类型1普通用户2商户
	private String qq;
	private String phone;
	private String email;
	private Integer pcate;// 父级商户分类
	private String cate;// 商户分类
	private String image;// 商户图片
	private Date registertime;// 注册时间
	private Date lastlogintime;// 上次登录
	private Integer jifen;// 账户积分
	private Double money;// 账户金额
	private Integer status;// 账户状态0未认证1已认证2未审核
	private Integer certificateperson;// 商户认证0未认证1已认证2未审核
	private Integer certificatecompany;// 公司认证0未认证1已认证2未审核
	private Integer certificatephone;// 手机号码认证0未认证1已认证2未审核
	private Integer certificateemail;// 电子邮箱认证0未认证1已认证2未审核
	private String safecode;// 安全码
	private String question;// 注册找回 问题
	private String answer;// 注册找回 答案
	private String certificatepersonpicy;
	private String certificatepersonpicx;
	private String certificatecompanypic;
	private Double per;
	private String tgloginnum;
	private String encrypt_type;

	// 非映射字段
	private String verify_code;// 校验码
	private String verify_type;// 校验校验类型  01注册,02找回

	public AbUser() {
	}

	public AbUser(String loginnum, String password) {
		this.loginnum = loginnum;
		this.password = password;
	}

	public AbUser(String logid, String loginnum, String password) {
		this.logid = logid;
		this.loginnum = loginnum;
		this.password = password;
	}

//	public Integer getId() {
//		return id;
//	}
//
//	public void setId(Integer id) {
//		this.id = id;
//	}

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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getPcate() {
		return pcate;
	}

	public void setPcate(Integer pcate) {
		this.pcate = pcate;
	}

	public String getCate() {
		return cate;
	}

	public void setCate(String cate) {
		this.cate = cate;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Date getRegistertime() {
		return registertime;
	}

	public void setRegistertime(Date registertime) {
		this.registertime = registertime;
	}

	public Date getLastlogintime() {
		return lastlogintime;
	}

	public void setLastlogintime(Date lastlogintime) {
		this.lastlogintime = lastlogintime;
	}

	public Integer getJifen() {
		return jifen;
	}

	public void setJifen(Integer jifen) {
		this.jifen = jifen;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getCertificateperson() {
		return certificateperson;
	}

	public void setCertificateperson(Integer certificateperson) {
		this.certificateperson = certificateperson;
	}

	public Integer getCertificatecompany() {
		return certificatecompany;
	}

	public void setCertificatecompany(Integer certificatecompany) {
		this.certificatecompany = certificatecompany;
	}

	public Integer getCertificatephone() {
		return certificatephone;
	}

	public void setCertificatephone(Integer certificatephone) {
		this.certificatephone = certificatephone;
	}

	public Integer getCertificateemail() {
		return certificateemail;
	}

	public void setCertificateemail(Integer certificateemail) {
		this.certificateemail = certificateemail;
	}

	public String getSafecode() {
		return safecode;
	}

	public void setSafecode(String safecode) {
		this.safecode = safecode;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getCertificatepersonpicy() {
		return certificatepersonpicy;
	}

	public void setCertificatepersonpicy(String certificatepersonpicy) {
		this.certificatepersonpicy = certificatepersonpicy;
	}

	public String getCertificatepersonpicx() {
		return certificatepersonpicx;
	}

	public void setCertificatepersonpicx(String certificatepersonpicx) {
		this.certificatepersonpicx = certificatepersonpicx;
	}

	public String getCertificatecompanypic() {
		return certificatecompanypic;
	}

	public void setCertificatecompanypic(String certificatecompanypic) {
		this.certificatecompanypic = certificatecompanypic;
	}

	public Double getPer() {
		return per;
	}

	public void setPer(Double per) {
		this.per = per;
	}

	public String getTgloginnum() {
		return tgloginnum;
	}

	public void setTgloginnum(String tgloginnum) {
		this.tgloginnum = tgloginnum;
	}

	public String getLogid() {
		return logid;
	}

	public void setLogid(String logid) {
		this.logid = logid;
	}

	public String getVerify_code() {
		return verify_code;
	}

	public void setVerify_code(String verify_code) {
		this.verify_code = verify_code;
	}

	public String getEncrypt_type() {
		return encrypt_type;
	}

	public void setEncrypt_type(String encrypt_type) {
		this.encrypt_type = encrypt_type;
	}

	public String getVerify_type() {
		return verify_type;
	}

	public void setVerify_type(String verify_type) {
		this.verify_type = verify_type;
	}
}