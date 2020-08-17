package com.budgetbox.genericfood.config;

import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
		// default: disable all transactions, let NonTransactionalNonReadOnlyJpaRepository do the job
		enableDefaultTransactions = false,		
		basePackages = { "com.budgetbox.genericfood.dao" },
		excludeFilters = @Filter(type = FilterType.ANNOTATION, classes = Configuration.class),
		entityManagerFactoryRef = "productEntityManagerFactory",
		transactionManagerRef = "productTransactionManager")
public class ProductDatabaseConfiguration {

	private static final Logger logger = Logger.getLogger(ProductDatabaseConfiguration.class.getName());

	// -- DataSource methods ---------------------------------------------------------------

	@Bean(name = "productDataSource")
	protected DataSource dataSource() {
		try {
			return TomcatJdbcDataSourceBuilder.build();
		} catch (Exception e) {
			logger.severe(ExceptionUtils.getStackTrace(e));
		}

		return null;
    }

    // -- JPA Adapter / Entity manager / Transaction manager ---------------------------------------------------------

    @Bean(name = "productJpaVendorAdapter")
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
		hibernateJpaVendorAdapter.setShowSql(false);
		hibernateJpaVendorAdapter.setGenerateDdl(false);
		hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
		return hibernateJpaVendorAdapter;
	}

    @Bean(name = "productEntityManager")
    public EntityManager entityManager() {
        return entityManagerFactory().createEntityManager();
    }

    @Bean(name = "productTransactionManager")
    public JpaTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory());
        return transactionManager;
    }

	@Bean(name = "productEntityManagerFactory")
	public EntityManagerFactory entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
		lef.setDataSource(dataSource());
		lef.setJpaVendorAdapter(jpaVendorAdapter());
		lef.setPackagesToScan("com.budgetbox.genericfood.dao");
		lef.setPersistenceUnitName("productPersistenceUnit");
	    lef.afterPropertiesSet();
		return lef.getObject();
	}
}
