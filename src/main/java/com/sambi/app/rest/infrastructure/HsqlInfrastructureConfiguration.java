package com.sambi.app.rest.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = {"com.sambi.app.repositories"})
@EnableTransactionManagement
@PropertySources(value = { @PropertySource("classpath:dev-application.properties") })
@Profile("dev")
public class HsqlInfrastructureConfiguration extends AbstractInfrastructureConfigurer {

	private static final Logger logger = LoggerFactory.getLogger(HsqlInfrastructureConfiguration.class);
	/**
     *Load the properties
     */
   // @Value("${spring.datasource.driver}")
   // private String databaseDriver;
    @Value("${spring.datasource.url}")
    private String databaseUrl;
    @Value("${spring.datasource.username}")
    private String databaseUsername;
    @Value("${spring.datasource.password}")
    private String databasePassword;

	@Override
    @Bean
    @Primary
    public DataSource dataSource() {
     	final SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriver(new org.hsqldb.jdbcDriver());
        dataSource.setUrl(databaseUrl); 
        dataSource.setUsername(databaseUsername);
        dataSource.setPassword(databasePassword);

        //  praktisch beim Testen.

        if(! "true".equals(System.getProperty("devNoInitDB")) ) {
            ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
            databasePopulator.setContinueOnError(false);
            databasePopulator.addScripts(new ClassPathResource("db.sql"));
            databasePopulator.execute(dataSource);
        }

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
        jpaVendorAdapter.setGenerateDdl(false);
        jpaVendorAdapter.setShowSql(false);
        jpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.HSQLDialect");

        return jpaVendorAdapter;
    }
}
