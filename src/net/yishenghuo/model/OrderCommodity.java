package net.yishenghuo.model;

public class OrderCommodity {
	private String id;
	private String loginnum;
	private String iid;
	private String price;
	private String realCharge;
	private String ddesc;
	private String status;
	private String createtime;
	private String imageId;
	private String imageName;
	private String imageImgurl;
	private String orderId;
	private String copies;

	public OrderCommodity() {
	}

	public OrderCommodity(String id) {
		this.id = id;
	}

	public OrderCommodity(String id, String loginnum, String iid, String price, String realCharge,
			String ddesc, String status, String createtime, String imageId, String imageName,
			String imageImgurl, String orderId) {
		this.id = id;
		this.loginnum = loginnum;
		this.iid = iid;
		this.price = price;
		this.realCharge = realCharge;
		this.ddesc = ddesc;
		this.status = status;
		this.createtime = createtime;
		this.imageId = imageId;
		this.imageName = imageName;
		this.imageImgurl = imageImgurl;
		this.orderId = orderId;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
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

	public String getPrice() {
		return this.price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getRealCharge() {
		return this.realCharge;
	}

	public void setRealCharge(String realCharge) {
		this.realCharge = realCharge;
	}

	public String getDdesc() {
		return this.ddesc;
	}

	public void setDdesc(String ddesc) {
		this.ddesc = ddesc;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreatetime() {
		return this.createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public String getImageId() {
		return this.imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getImageName() {
		return this.imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getImageImgurl() {
		return this.imageImgurl;
	}

	public void setImageImgurl(String imageImgurl) {
		this.imageImgurl = imageImgurl;
	}

	public String getOrderId() {
		return this.orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getCopies() {
		return copies;
	}

	public void setCopies(String copies) {
		this.copies = copies;
	}

}