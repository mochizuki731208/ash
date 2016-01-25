package com.zp.controller.rest;

import java.text.Collator;
import java.util.Comparator;

import net.yishenghuo.model.CityVo;

public class CityComparator implements Comparator {
//	private Collator collator = Collator.getInstance();

	public CityComparator() {
	}

	/**
	 * compare 实现排序。
	 * 


	 * @param o1
	 *            Object
	 * @param o2
	 *            Object
	 * @return int
	 */
	public int compare(Object o1, Object o2) {
System.out.println("1111111111111----------------- "+((CityVo)o1).getCity()+"  ,  "+((CityVo)o2).getCity());
//		CollationKey key1 = collator.getCollationKey(((CityVo)o1).getCity());
//		CollationKey key2 = collator.getCollationKey(((CityVo)o2).getCity());
//		return key1.compareTo(key2);
		Comparator<Object> com=Collator.getInstance(java.util.Locale.CHINA);
		return com.compare(((CityVo)o1).getCity(), ((CityVo)o2).getCity());
	}
}
