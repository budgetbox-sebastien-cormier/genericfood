package com.budgetbox.genericfood.dao;

import org.springframework.stereotype.Repository;

import com.budgetbox.genericfood.dao.shared.NonTransactionalNonReadOnlyJpaRepository;

@Repository
public interface ProductRepository extends NonTransactionalNonReadOnlyJpaRepository<Product, Integer>{

}
