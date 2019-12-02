package com.github.paganini2008.springworld.webcrawler.config;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * DbPoolConfig
 * 
 * @author Fred Feng
 * @revised 2019-06
 * @version 1.0
 */
@Slf4j
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "datasource")
public class DbPoolConfig {

	private String jdbcUrl;
	private String username;
	private String password;
	private String driverClassName;

	private HikariConfig getDbConfig() {
		log.info("JdbcUrl: " + jdbcUrl);
		log.info("Username: " + username);
		log.info("Password: " + password);
		log.info("DriverClassName: " + driverClassName);
		final HikariConfig config = new HikariConfig();
		config.setDriverClassName(driverClassName);
		config.setJdbcUrl(jdbcUrl);
		config.setUsername(username);
		config.setPassword(password);
		config.setMinimumIdle(5);
		config.setMaximumPoolSize(50);
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

	@Primary
	@Bean
	public DataSource createDataSource() {
		HikariDataSource ds = new HikariDataSource(getDbConfig());
		log.info("DbPool: " + ds);
		return ds;
	}

}
