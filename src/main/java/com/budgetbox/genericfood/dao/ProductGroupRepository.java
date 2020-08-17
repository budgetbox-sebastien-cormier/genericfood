package com.budgetbox.genericfood.dao;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.budgetbox.genericfood.dao.shared.NonTransactionalNonReadOnlyJpaRepository;
import com.budgetbox.genericfood.dao.shared.NonTransactionalReadOnly;

@Repository
public interface ProductGroupRepository  extends NonTransactionalNonReadOnlyJpaRepository<ProductGroup, Integer> {

	@NonTransactionalReadOnly
	public ProductGroup getByName(@Param("name") String name);
}
