package com.budgetbox.genericfood.dao.shared;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.budgetbox.genericfood.exceptions.IllegalPageRequestException;

public abstract class PageRequestBuilder {

	private static PageRequest from0To1000;

	static {
		try {
			from0To1000 = buildFromOffset(0, 1000);
		} catch (IllegalPageRequestException e) {
            throw new ExceptionInInitializerError(e);
		}
	}
	
	public static PageRequest from0To1000() {
		return from0To1000;
	}

	public static PageRequest buildFromOffset(int offset, int size) throws IllegalPageRequestException {
		if(offset > 0 && size > 0 && offset % size > 0) throw new IllegalPageRequestException("Pagination is not possible");
		return PageRequest.of((offset > 0 && size > 0) ? (offset / size) : 0, size > 0 ? size : 0);
	}

	public static PageRequest buildFromOffset(int offset, int size, Sort sort) throws IllegalPageRequestException {
		if(offset > 0 && size > 0 && offset % size > 0) throw new IllegalPageRequestException("Pagination is not possible");
		return PageRequest.of((offset > 0 && size > 0) ? (offset / size) : 0, size > 0 ? size : 0, sort);
	}
}
