package com.github.paganini2008.springworld.support.logback;

import javax.sql.DataSource;

import com.github.paganini2008.devtools.Assert;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import ch.qos.logback.core.db.DataSourceConnectionSource;

/**
 * 
 * HikariCPConnectionSource
 * 
 * @author Fred Feng
 * @create 2019-03
 */
public class HikariCPConnectionSource extends DataSourceConnectionSource {

	private String username;
	private String driverClassName;
	private String url;

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void start() {
		Assert.hasNoText(driverClassName, "No selected driverClassName in HikariCPConnectionSource for logback.");
		Assert.hasNoText(url, "No selected jdbcUrl in HikariCPConnectionSource for logback.");
		DataSource dataSource = new HikariDataSource(getDbConfig());
		setDataSource(dataSource);
		super.start();
	}

	public void stop() {
		super.stop();
		HikariDataSource ds = (HikariDataSource) getDataSource();
		if (ds != null && !ds.isClosed()) {
			ds.close();
		}
	}

	protected HikariConfig getDbConfig() {
		final HikariConfig config = new HikariConfig();
		config.setDriverClassName(driverClassName);
		config.setJdbcUrl(url);
		config.setUsername(username);
		config.setPassword(getPassword());
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
		return config;
	}

}
