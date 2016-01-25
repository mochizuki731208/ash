package net.yishenghuo.model;

import java.util.ArrayList;
import java.util.List;

import com.zp.entity.AbMerchantYysj;

public class Merchant implements Comparable<Merchant>{
	private String id;
	private String iid;
	private String name;
	private String addr;
	private String tel;
	private String cate;
	private String tag;
	private String rate;
	private Integer ccost;
	private String ddesc;
	private String details;
	private Integer ddist;
	private String lng;
	private String lat;
	private String img_url;
	private Integer index_num;
	private Integer total;
	private Integer result_num;
	private String web_url;
	private String wap_url;
	private String subjectname;
	private String city;
	private Integer isbook;
	private Integer isupport;
	private Integer status;
	private Integer sort;
	private Integer isrecommend;
	private Integer commentcount;
	private Integer ordercount;
	private String loginnum;
	private String tgloginnum;
	private String webname;
	private String area_id;
	private String area;
	private String work_time;
	private String type;

	private String avgprice;
	private String avghour;
	private String avgptf;
	private String distance;
	private String zdpsje;
	
	private List<AbMerchantYysj> yysj = new ArrayList<AbMerchantYysj>();
	
	public List<AbMerchantYysj> getYysj() {
		return yysj;
	}

	public void setYysj(List<AbMerchantYysj> yysj) {
		this.yysj = yysj;
	}

	public String getZdpsje() {
		return zdpsje;
	}

	public void setZdpsje(String zdpsje) {
		this.zdpsje = zdpsje;
	}

    public int compareTo(Merchant o) {
        // 先按距离排序排序
		String dis = o.getDistance();
		if (dis == null || "".equals(dis) || distance == null || "".equals(distance))
		{
			return 0;
		}
		double d = Double.valueOf(distance);
		double d1 = Double.valueOf(dis);
        if (d > d1) {
            return 1;
        }
        if (d < d1) {
            return -1;
        }
        String price = o.getAvgprice();
        // 按价格排序排序
        double a = Double.valueOf(avgprice);
		double a1 = Double.valueOf(price);
        if (a > a1) {
            return 1;
        }
        if (a < a1) {
            return -1;
        }
        return 0;
    }
	
	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	private List<McCategory> mctype = new ArrayList<McCategory>();
	
	public List<McCategory> getMctype() {
		return mctype;
	}

	public void setMctype(List<McCategory> mctype) {
		this.mctype = mctype;
	}

	public String getAvgprice() {
		return avgprice;
	}

	public void setAvgprice(String avgprice) {
		this.avgprice = avgprice;
	}

	public String getAvghour() {
		return avghour;
	}

	public void setAvghour(String avghour) {
		this.avghour = avghour;
	}

	public String getAvgptf() {
		return avgptf;
	}

	public void setAvgptf(String avgptf) {
		this.avgptf = avgptf;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIid() {
		return iid;
	}

	public void setIid(String iid) {
		this.iid = iid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getCate() {
		return cate;
	}

	public void setCate(String cate) {
		this.cate = cate;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public Integer getCcost() {
		return ccost;
	}

	public void setCcost(Integer ccost) {
		this.ccost = ccost;
	}

	public String getDdesc() {
		return ddesc;
	}

	public void setDdesc(String ddesc) {
		this.ddesc = ddesc;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Integer getDdist() {
		return ddist;
	}

	public void setDdist(Integer ddist) {
		this.ddist = ddist;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getImg_url() {
		return img_url;
	}

	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}

	public Integer getIndex_num() {
		return index_num;
	}

	public void setIndex_num(Integer index_num) {
		this.index_num = index_num;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getResult_num() {
		return result_num;
	}

	public void setResult_num(Integer result_num) {
		this.result_num = result_num;
	}

	public String getWeb_url() {
		return web_url;
	}

	public void setWeb_url(String web_url) {
		this.web_url = web_url;
	}

	public String getWap_url() {
		return wap_url;
	}

	public void setWap_url(String wap_url) {
		this.wap_url = wap_url;
	}

	public String getSubjectname() {
		return subjectname;
	}

	public void setSubjectname(String subjectname) {
		this.subjectname = subjectname;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Integer getIsbook() {
		return isbook;
	}

	public void setIsbook(Integer isbook) {
		this.isbook = isbook;
	}

	public Integer getIsupport() {
		return isupport;
	}

	public void setIsupport(Integer isupport) {
		this.isupport = isupport;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getIsrecommend() {
		return isrecommend;
	}

	public void setIsrecommend(Integer isrecommend) {
		this.isrecommend = isrecommend;
	}

	public Integer getCommentcount() {
		return commentcount;
	}

	public void setCommentcount(Integer commentcount) {
		this.commentcount = commentcount;
	}

	public Integer getOrdercount() {
		return ordercount;
	}

	public void setOrdercount(Integer ordercount) {
		this.ordercount = ordercount;
	}

	public String getLoginnum() {
		return loginnum;
	}

	public void setLoginnum(String loginnum) {
		this.loginnum = loginnum;
	}

	public String getTgloginnum() {
		return tgloginnum;
	}

	public void setTgloginnum(String tgloginnum) {
		this.tgloginnum = tgloginnum;
	}

	public String getWebname() {
		return webname;
	}

	public void setWebname(String webname) {
		this.webname = webname;
	}

	public String getWork_time() {
		return work_time;
	}

	public void setWork_time(String work_time) {
		this.work_time = work_time;
	}

	public String getArea_id() {
		return area_id;
	}

	public void setArea_id(String area_id) {
		this.area_id = area_id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}
}