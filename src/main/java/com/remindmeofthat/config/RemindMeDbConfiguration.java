package com.remindmeofthat.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = "com.remindmeofthat.data.repository")
@EnableTransactionManagement
@EntityScan("com.remindmeofthat.data.model") //This is the package where the JPA entities live
public class RemindMeDbConfiguration {

    private static Logger logger = LoggerFactory.getLogger(RemindMeDbConfiguration.class);

    //These are set by environment variables
    @Value("${local.db.username}")
    private String dbUsername;

    @Value("${local.db.password}")
    private String dbPassword;

    @Value("${local.db.url}")
    private String dbUrl;

    /**
     * Class to configure the connection to the SQL Server DB
     * @return
     */
    @Bean
    @Primary
    public DataSource getClaimsDataSource() {

        logger.info("Configuring database access to [{}] with username [{}]", dbUrl, dbUsername);

        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.url(dbUrl);
        dataSourceBuilder.username(dbUsername);
        dataSourceBuilder.password(dbPassword);
        return dataSourceBuilder.build();
    }
}


