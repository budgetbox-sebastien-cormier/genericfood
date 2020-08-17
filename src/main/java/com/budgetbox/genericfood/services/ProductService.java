package com.budgetbox.genericfood.services;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.budgetbox.genericfood.dao.Product;
import com.budgetbox.genericfood.dao.ProductRepository;
import com.budgetbox.genericfood.dao.shared.PageRequestBuilder;
import com.budgetbox.genericfood.shared.ProductSearchQuery;

@Service
public class ProductService {

	@Autowired
	ProductRepository productRepository;

	@PersistenceContext(unitName = "productEntityManagerFactory")
    private EntityManager entityManager;
	
	private final Logger logger = Logger.getLogger(ProductService.class.getName());

	public Page<Product> searchProducts(ProductSearchQuery query) {
		try {
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<Product> rowQuery = builder.createQuery(Product.class);
			Root<Product> root = rowQuery.from(Product.class);
			List<javax.persistence.criteria.Predicate> predicates = new ArrayList<>();

			if (query.getGroupId() > 0) {
				predicates.add(builder.equal(root.get("groupId"), query.getGroupId()));
			}
			if (query.getSubGroupId() > 0) {
				predicates.add(builder.equal(root.get("subGroupId"), query.getSubGroupId()));
			}
			if(StringUtils.isNotBlank(query.getKeywords())) {
				predicates.add(builder.or(builder.like(root.get("name"), "%" + query.getKeywords() + "%"), builder.like(root.get("scientificName"), "%" + query.getKeywords() + "%")));
			}

			javax.persistence.criteria.Predicate finalPredicate = builder.and(predicates.toArray(new javax.persistence.criteria.Predicate[predicates.size()]));
			javax.persistence.criteria.Order order = query.getOrder().equalsIgnoreCase("asc") ? builder.asc(root.get(query.getSort())) : builder.desc(root.get(query.getSort()));

			TypedQuery<Product> typedQuery = entityManager.createQuery(
					rowQuery.select(root).where(finalPredicate).orderBy(order));

			typedQuery.setFirstResult(query.getFrom());
			typedQuery.setMaxResults(query.getSize());

			Page<Product> matches = new PageImpl<Product>(
					typedQuery.getResultList(), 
					PageRequestBuilder.buildFromOffset(query.getFrom(), query.getSize(), Sort.by(Direction.fromString(query.getOrder()), query.getSort())), 
					findCountByCriteria(rowQuery));

			return matches;
        } catch (Exception e) {
            logger.severe(ExceptionUtils.getStackTrace(e));
        }

		return null;
	}

	private <T> Long findCountByCriteria(CriteriaQuery<?> criteria) {
	    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
	    CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
	    Root<?> entityRoot = countCriteria.from(criteria.getResultType());
	    countCriteria.select(builder.count(entityRoot));
	    countCriteria.where(criteria.getRestriction());
	    return entityManager.createQuery(countCriteria).getSingleResult();
	}

	public Product get(int id) {
		try {
			return productRepository.getById(id);
		} catch (Exception e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
		}
		return null;
	}

	public Product save(Product product) {
		try {
			return productRepository.save(product);
		} catch (Exception e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
		}
		return null;
	}

	public boolean delete(int id) {
		try {
			productRepository.deleteById(id);
			return true;
		} catch (Exception e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
		}
		return false;
	}
}
