package net.yishenghuo.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
	private int id;
	private String loginnum;
	private String iid;
	private String merchantname;
	private String imageid;
	private String imageurl;
	private String imagename;
	private Integer renshu;
	private Date starttime;
	private Date endtime;
	private Integer counts;
	private Double price;
	private Double totalmoney;
	private String phone;
	private String email;
	private String ddesc;
	private Integer status;
	private Date createtime;
	private String tgloginnum;
	private Integer serviceway;
	private String payway;
	private String recieveraddress;
	private String openid;
	private String time;
	private String linkman;
	private String remark;

	// 非映射字段
	private List<OrderCommodity> commoditys = new ArrayList<OrderCommodity>();

	public Order() {
	}

	public Order(String loginnum) {
		this.loginnum = loginnum;
	}

	public Order(String loginnum, String openid) {
		this.loginnum = loginnum;
		this.openid = openid;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLoginnum() {
		return this.loginnum;
	}

	public void setLoginnum(String loginnum) {
		this.loginnum = loginnum;
	}

	public String getIid() {
		return this.iid;
	}

	public void setIid(String iid) {
		this.iid = iid;
	}

	public String getMerchantname() {
		return this.merchantname;
	}

	public void setMerchantname(String merchantname) {
		this.merchantname = merchantname;
	}

	public String getImageid() {
		return this.imageid;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getLinkman() {
		return linkman;
	}

	public void setLinkman(String linkman) {
		this.linkman = linkman;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void setImageid(String imageid) {
		this.imageid = imageid;
	}

	public String getImageurl() {
		return this.imageurl;
	}

	public void setImageurl(String imageurl) {
		this.imageurl = imageurl;
	}

	public String getImagename() {
		return this.imagename;
	}

	public void setImagename(String imagename) {
		this.imagename = imagename;
	}

	public Integer getRenshu() {
		return this.renshu;
	}

	public void setRenshu(Integer renshu) {
		this.renshu = renshu;
	}

	public Date getStarttime() {
		return this.starttime;
	}

	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}

	public Date getEndtime() {
		return this.endtime;
	}

	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}

	public Integer getCounts() {
		return this.counts;
	}

	public void setCounts(Integer counts) {
		this.counts = counts;
	}

	public Double getPrice() {
		return this.price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getTotalmoney() {
		return this.totalmoney;
	}

	public void setTotalmoney(Double totalmoney) {
		this.totalmoney = totalmoney;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDdesc() {
		return this.ddesc;
	}

	public void setDdesc(String ddesc) {
		this.ddesc = ddesc;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCreatetime() {
		return this.createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public String getTgloginnum() {
		return this.tgloginnum;
	}

	public void setTgloginnum(String tgloginnum) {
		this.tgloginnum = tgloginnum;
	}

	public Integer getServiceway() {
		return this.serviceway;
	}

	public void setServiceway(Integer serviceway) {
		this.serviceway = serviceway;
	}

	public List<OrderCommodity> getCommoditys() {
		return commoditys;
	}

	public void setCommoditys(List<OrderCommodity> commoditys) {
		this.commoditys = commoditys;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getPayway() {
		return payway;
	}

	public void setPayway(String payway) {
		this.payway = payway;
	}

	public String getRecieveraddress() {
		return recieveraddress;
	}

	public void setRecieveraddress(String recieveraddress) {
		this.recieveraddress = recieveraddress;
	}
}