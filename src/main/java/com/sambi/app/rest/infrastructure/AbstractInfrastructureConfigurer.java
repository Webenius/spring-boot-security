package com.sambi.app.rest.infrastructure;

import java.sql.SQLException;
import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

public abstract class AbstractInfrastructureConfigurer implements InfrastructureConfigurer {

    /**
     * Needed for resolving ${} of @Value in configuration classes.
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    @Override
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws SQLException {

        LocalContainerEntityManagerFactoryBean lcemfb = new LocalContainerEntityManagerFactoryBean();
        lcemfb.setPackagesToScan("com.sambi.app.domain");
        lcemfb.setDataSource(dataSource());
        lcemfb.setJpaVendorAdapter(jpaVendorAdapter());

        Properties properties = new Properties();
        properties.setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.EhCacheRegionFactory");
        properties.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.EhCacheProvider");
        properties.setProperty("hibernate.cache.use_second_level_cache", "false");
        properties.setProperty("hibernate.cache.use_query_cache", "false");
        properties.setProperty("hibernate.generate_statistics", "false");
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.jdbc.fetch_size", "100");
        properties.setProperty("hibernate.temp.use_jdbc_metadata_defaults", "false");

        lcemfb.setJpaProperties(properties);
        return lcemfb;
    }

	@Bean
	public PlatformTransactionManager transactionManager() throws Exception {
        return new JpaTransactionManager(entityManagerFactory().getObject());
	}
}