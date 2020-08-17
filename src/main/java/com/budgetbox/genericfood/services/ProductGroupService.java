package com.budgetbox.genericfood.services;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.budgetbox.genericfood.dao.ProductGroup;
import com.budgetbox.genericfood.dao.ProductGroupRepository;

@Service
public class ProductGroupService {

	@Autowired
	private ProductGroupRepository productGroupRepository;

	private final Logger logger = Logger.getLogger(ProductGroupService.class.getName());

	public List<ProductGroup> getByIds(Collection<Integer> ids) {
		try {
			return productGroupRepository.findAllById(ids);
		} catch (Exception e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
		}
		return List.of();
	}
}
