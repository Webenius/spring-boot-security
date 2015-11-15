package com.sambi.app.rest.infrastructure;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

public interface InfrastructureConfigurer {
		
    @Bean
    DataSource dataSource() throws SQLException;

    @Bean
    JpaVendorAdapter jpaVendorAdapter();

    @Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactory() throws SQLException;
}
