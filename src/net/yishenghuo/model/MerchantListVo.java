package net.yishenghuo.model;

import java.util.List;

public class MerchantListVo {
	private String count_no;
	private String size;
	private List<Merchant> merchant_list;
	
	public String getCount_no() {
		return count_no;
	}
	public void setCount_no(String count_no) {
		this.count_no = count_no;
	}
	public List<Merchant> getMerchant_list() {
		return merchant_list;
	}
	public void setMerchant_list(List<Merchant> merchant_list) {
		this.merchant_list = merchant_list;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
}
