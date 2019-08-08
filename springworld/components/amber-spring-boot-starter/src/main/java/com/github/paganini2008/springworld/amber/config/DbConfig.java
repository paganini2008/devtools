package com.github.paganini2008.springworld.amber.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * 
 * DbConfig
 * 
 * @author Fred Feng
 * @create 2018-03
 */
@Configuration
@ConditionalOnMissingBean(DataSource.class)
public class DbConfig {

	private static final Logger logger = LoggerFactory.getLogger(DbConfig.class);

	@Value("${amber.datasource.url}")
	private String jdbcUrl;
	@Value("${amber.datasource.username}")
	private String username;
	@Value("${amber.datasource.password}")
	private String password;
	@Value("${amber.datasource.driverClassName}")
	private String driverClassName;

	private HikariConfig getDbConfig() {
		logger.info("JdbcUrl: " + jdbcUrl);
		logger.info("Username: " + username);
		logger.info("Password: " + password);
		logger.info("DriverClassName: " + driverClassName);
		final HikariConfig config = new HikariConfig();
		config.setDriverClassName(driverClassName);
		config.setJdbcUrl(jdbcUrl);
		config.setUsername(username);
		config.setPassword(password);
		config.setMinimumIdle(5);
		config.setMaximumPoolSize(30);
		config.setMaxLifetime(60 * 1000);
		config.setIdleTimeout(60 * 1000);
		config.setValidationTimeout(3000);
		config.setReadOnly(false);
		config.setConnectionInitSql("SELECT UUID()");
		config.setConnectionTestQuery("SELECT 1");
		config.setConnectionTimeout(60 * 1000);
		config.setTransactionIsolation("TRANSACTION_READ_COMMITTED");

		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		return config;
	}

	@Bean
	public DataSource createDataSource() {
		HikariDataSource ds = new HikariDataSource(getDbConfig());
		logger.info("DataSource: " + ds);
		return ds;
	}

}
