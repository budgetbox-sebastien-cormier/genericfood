package com.budgetbox.genericfood.dao.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jdbc.pool.DataSource;

/**
 * Read more: https://my.oschina.net/shipley/blog/519449
 * 
 * 
 * @author xfacq
 *
 */
public class TomcatJdbcDataSourceBuilder {

	public static DataSource build(String host, String port, String user, String password) {
		DataSource dataSource = new DataSource();

	    dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
	    
	    String url = new StringBuilder("jdbc:mysql://")
	    		.append(host).append(':').append(port)
	    		.append("/products?zeroDateTimeBehavior=convertToNull&autoReconnect=true&characterEncoding=UTF-8&useSSL=false")
	    		.toString();
	    dataSource.setUrl(url); 
	    dataSource.setUsername(StringUtils.trimToNull(user));
	    dataSource.setPassword(StringUtils.trimToNull(password));

	    // Set Tomcat JDBC parameters
	    // https://tomcat.apache.org/tomcat-9.0-doc/jdbc-pool.html
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setValidationQueryTimeout(10);
        dataSource.setValidationInterval(30000);

        dataSource.setTestOnBorrow(true);
        dataSource.setTestOnReturn(false);
        dataSource.setTestWhileIdle(true);

        dataSource.setMaxWait(10000);

	    dataSource.setInitialSize(2);
	    dataSource.setMaxActive(10);
	    dataSource.setMinIdle(1);
	    dataSource.setMaxIdle(2);

	    // INFO: following line is commented because there is some strange stack about abandonned pooled connections
	    // dataSource.dataSource.setSuspectTimeout(60);
	    dataSource.setMinEvictableIdleTimeMillis(30000);
	    dataSource.setTimeBetweenEvictionRunsMillis(30000);

        dataSource.setLogAbandoned(true);
        dataSource.setRemoveAbandoned(true);
        dataSource.setRemoveAbandonedTimeout(30);
        dataSource.setAbandonWhenPercentageFull(50);

        dataSource.setJdbcInterceptors(
                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"
                + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer;"
                + "org.apache.tomcat.jdbc.pool.interceptor.ResetAbandonedTimer");

        return dataSource;
	}
}
