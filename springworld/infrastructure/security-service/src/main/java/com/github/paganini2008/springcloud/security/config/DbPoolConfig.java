package com.github.paganini2008.springcloud.security.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * 
 * DbPoolConfig
 * 
 * @author Fred Feng
 * @created 2019-07
 * @revised 2019-02
 * @version 1.0
 */
@Configuration
public class DbPoolConfig {

	private static final Logger logger = LoggerFactory.getLogger(DbPoolConfig.class);

	@Value("${spring.datasource.url}")
	private String jdbcUrl;
	@Value("${spring.datasource.username}")
	private String username;
	@Value("${spring.datasource.password}")
	private String password;
	@Value("${spring.datasource.driverClassName}")
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
		config.setMinimumIdle(1);
		config.setMaximumPoolSize(10);
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
		return new HikariDataSource(getDbConfig());
	}

}
