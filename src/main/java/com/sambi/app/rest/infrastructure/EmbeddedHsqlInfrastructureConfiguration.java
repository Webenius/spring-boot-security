package com.sambi.app.rest.infrastructure;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackages = {"com.sambi.app.repositories"})
@EnableTransactionManagement
@Profile("test")
public class EmbeddedHsqlInfrastructureConfiguration extends AbstractInfrastructureConfigurer {

	private static final Logger logger = LoggerFactory.getLogger(EmbeddedHsqlInfrastructureConfiguration.class);

	@Override
    @Bean
    @Primary
    public DataSource dataSource() {

        EmbeddedDatabaseBuilder embeddedDatabaseBuilder = new EmbeddedDatabaseBuilder();
        embeddedDatabaseBuilder.addScript("classpath:db.sql")
                               .setType(EmbeddedDatabaseType.HSQL);

        DataSource dataSource = embeddedDatabaseBuilder.build();
        logger.debug("Creating Data Source {}", dataSource.getClass().getCanonicalName());
        return dataSource;
    }
	@Bean
	public DatabaseConfigBean dbUnitDatabaseConfig() {
		final DatabaseConfigBean bean = new DatabaseConfigBean();
		bean.setDatatypeFactory(new HsqldbDataTypeFactory());
		return bean;
	}

	@Bean
	public DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection() {
		final DatabaseDataSourceConnectionFactoryBean bean = new DatabaseDataSourceConnectionFactoryBean(dataSource());
		bean.setDatabaseConfig(dbUnitDatabaseConfig());
		return bean;
	}

    @Override
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

        LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
        lef.setPackagesToScan("com.sambi.app.domain");
        lef.setDataSource(dataSource());
        lef.setJpaVendorAdapter(jpaVendorAdapter());

        Properties properties = new Properties();
        properties.setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.EhCacheRegionFactory");
        properties.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.EhCacheProvider");
        //properties.setProperty("net.sf.ehcache.configurationResourceName", "/app-ehcache.xml");
        properties.setProperty("hibernate.cache.use_second_level_cache", "true");
        properties.setProperty("hibernate.cache.use_query_cache", "true");
        properties.setProperty("hibernate.generate_statistics", "false");
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.jdbc.fetch_size", "100");

        lef.setJpaProperties(properties);
        return lef;
    }

    @Override
    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabase(Database.HSQL);
        jpaVendorAdapter.setGenerateDdl(false);
        jpaVendorAdapter.setShowSql(false);
        jpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.HSQLDialect");

        return jpaVendorAdapter;
    }
}
