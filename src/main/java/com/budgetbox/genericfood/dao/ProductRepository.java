package com.budgetbox.genericfood.dao;

import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends NonTransactionalNonReadOnlyJpaRepository<Product, Integer>{

}
