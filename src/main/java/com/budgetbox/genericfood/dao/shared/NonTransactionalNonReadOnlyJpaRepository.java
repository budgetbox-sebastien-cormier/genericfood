package com.budgetbox.genericfood.dao.shared;

import java.io.Serializable;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;


/**
 * <p>
 * How to use this interface:
 * </>
 * <p>
 * 1°/ Change your repository implementation to <i>NonTransactionalNonReadOnlyJpaRepository</i>:<br/>
 * 			> all connections will be <b>NON-TRANSACTIONAL</b> and <b>NEVER READONLY</b>, all will be executed ON THE MASTER with no transaction<br/>
 * 			> except special methods like save() deleteConnection() and flush().
 * </p>
 * <p>
 * 2°/ Remove @Transactional to your repository
 * </p>
 * <p>
 * 3°/ Add @NonTransactionalReadOnly to method you want to read on the slave with a ReadOnly connection and no transaction
 * </p>
 * <p>
 * 4°/ Add @Modifying and @Transactional to your save/update/deleteConnection special method to active Transaction when you change data in the DB
 * </p>
 * 
 * @author xfacq
 */
@NoRepositoryBean
public interface NonTransactionalNonReadOnlyJpaRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

	@Override
	@Transactional
	<S extends T> List<S> saveAll(Iterable<S> entities);

	@Override
	@Transactional
	void flush();

	@Override
	@Transactional
	<S extends T> S saveAndFlush(S entity);

	@Override
	@Transactional
	void deleteInBatch(Iterable<T> entities);

	@Override
	@Transactional
	void deleteAllInBatch();
	
	@Override
	@Transactional
	<S extends T> S save(S entity);

	@Override
	@Transactional
	void deleteById(ID id);

	@Override
	@Transactional
	void delete(T entity);

	@Transactional
	void deleteByIdIn(Iterable<ID> ids);

	@Override
	@Transactional
	void deleteAll(Iterable<? extends T> entities);

	@Override
	@Transactional
	void deleteAll();	

	public default T getById(ID id) {
		return findById(id).orElse(null);
	}

	@Override
	public default T getOne(ID id) {
		return findById(id).orElse(null);
	}
}
