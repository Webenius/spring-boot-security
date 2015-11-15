package com.sambi.app.rest.infrastructure;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = {"com.sambi.app.repositories"})
@EnableTransactionManagement
@EnableCaching
@Profile("production")
@PropertySources({
	@PropertySource(value = "classpath:${spring.profiles.active}-application.properties", ignoreResourceNotFound=false),
	@PropertySource(value = "file:${app.home}/application.properties", ignoreResourceNotFound=true),
	@PropertySource(value = "file:${user.home}/application.properties", ignoreResourceNotFound=true)
	})
public class ProductionInfrastructureConfiguration  extends AbstractInfrastructureConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(ProductionInfrastructureConfiguration.class);

    @Value("${spring.datasource.url}")
    private String databaseUrl;
    @Value("${spring.datasource.username}")
    private String databaseUsername;
    @Value("${spring.datasource.password}")
    private String databasePassword;

    @Override
    @Bean(destroyMethod="close")
    @Primary
    public DataSource dataSource() throws SQLException {

        final BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriver(new com.mysql.jdbc.Driver());
        dataSource.setUrl(databaseUrl);
        dataSource.setUsername(databaseUsername);
        dataSource.setPassword(databasePassword);

        dataSource.setInitialSize(1);
        dataSource.setMaxTotal(10);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxOpenPreparedStatements(100);

        logger.debug("databaseURL {}", databaseUrl);
        logger.debug("databaseUsername {}", databaseUsername);
        logger.debug("databasePassword {}", databasePassword);
        logger.debug("Creating Data Source {}", dataSource.getClass().getCanonicalName());

        return dataSource;
    }

    @Override
    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {

		HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
		jpaVendorAdapter.setDatabase(Database.MYSQL);
		jpaVendorAdapter.setGenerateDdl(false);
		jpaVendorAdapter.setShowSql(false);
		jpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQLInnoDBDialect");
		return jpaVendorAdapter;
	}
}
