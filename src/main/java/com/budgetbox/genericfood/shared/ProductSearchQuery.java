package com.budgetbox.genericfood.shared;

import org.apache.commons.lang3.StringUtils;

public class ProductSearchQuery {

	private int from = 0;

	private int size = 25;

	private String sort = "id";

	private String order = "asc";

	private int groupId = 0;

	private int subGroupId = 0;

	private String keywords = null;

	public int getFrom() {
		return from;
	}

	public boolean setFrom(int from) {
		if(from < 0) return false;
		this.from = from;
		return true;
	}

	public int getSize() {
		return size;
	}

	public boolean setSize(int size) {
		if(size < 0) return false;
		this.size = size;
		return true;
	}

	public String getSort() {
		return sort;
	}

	public boolean setSort(String sort) {
		if(!StringUtils.equalsAnyIgnoreCase(sort, "id", "name", "scientific_name", "group_id", "subgroup_id")) {
			return false;
		}
		this.sort = sort;
		return true;
	}

	public String getOrder() {
		return order;
	}

	public boolean setOrder(String order) {
		if(!StringUtils.equalsAnyIgnoreCase(order, "asc", "desc")) {
			return false;
		}
		this.order = order;
		return true;
	}

	public int getGroupId() {
		return groupId;
	}

	public boolean setGroupId(int groupId) {
		if(groupId < 0) return false;
		this.groupId = groupId;
		return true;
	}

	public int getSubGroupId() {
		return subGroupId;
	}

	public boolean setSubGroupId(int subGroupId) {
		if(subGroupId < 0) return false;
		this.subGroupId = subGroupId;
		return true;
	}

	public String getKeywords() {
		return keywords;
	}

	public boolean setKeywords(String keywords) {
		this.keywords = StringUtils.trimToNull(keywords);
		return true;
	}
}
