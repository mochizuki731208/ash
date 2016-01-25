package net.yishenghuo.model;

import java.util.ArrayList;
import java.util.List;

public class MerchantDetail {
	private Merchant merchant;
	private List<Merchant> sames = new ArrayList<Merchant>();

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public List<Merchant> getSames() {
		return sames;
	}

	public void setSames(List<Merchant> sames) {
		this.sames = sames;
	}

}
