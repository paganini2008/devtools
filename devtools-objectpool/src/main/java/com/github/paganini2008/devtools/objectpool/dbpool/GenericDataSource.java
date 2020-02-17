package com.github.paganini2008.devtools.objectpool.dbpool;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import com.github.paganini2008.devtools.jdbc.AbstractDataSource;

/**
 * 
 * GenericDataSource
 *
 * @author Fred Feng
 * 
 * 
 */
public class GenericDataSource extends AbstractDataSource {

	public GenericDataSource(String driverClassName, String url, String username, String password) throws SQLException {
		this.setDriverClassName(driverClassName);
		this.setUrl(url);
		this.setUser(username);
		this.setPassword(password);
	}

	public GenericDataSource() {
	}

	private final ConnectionPool connectionPool = new ConnectionPool();

	public void setUser(String username) {
		this.connectionPool.setUser(username);
	}

	public void setPassword(String password) {
		this.connectionPool.setPassword(password);
	}

	public void setDriverClassName(String driverClassName) {
		this.connectionPool.setDriverClassName(driverClassName);
	}

	public void setUrl(String url) {
		this.connectionPool.setUrl(url);
	}

	public void setTestSql(String testSql) {
		this.connectionPool.setTestSql(testSql);
	}

	public void setAutoCommit(Boolean autoCommit) {
		this.connectionPool.setAutoCommit(autoCommit);
	}

	public void setDefaultTransactionIsolationLevel(Integer defaultTransactionIsolationLevel) {
		this.connectionPool.setDefaultTransactionIsolationLevel(defaultTransactionIsolationLevel);
	}

	public void setMaxIdleSize(int maxIdleSize) {
		this.connectionPool.setMaxIdleSize(maxIdleSize);
	}

	public void setMaxSize(int maxSize) {
		this.connectionPool.setMaxSize(maxSize);
	}

	public void setMaxUses(int maxUses) {
		this.connectionPool.setMaxUses(maxUses);
	}

	public void setTestWhileIdle(boolean testWhileIdle) {
		this.connectionPool.setTestWhileIdle(testWhileIdle);
	}

	public void setTestWhileIdleInterval(long testWhileIdleInterval) {
		this.connectionPool.setTestWhileIdleInterval(testWhileIdleInterval);
	}

	public void setCheckObjectExpired(boolean checkObjectExpired) {
		this.connectionPool.setCheckObjectExpired(checkObjectExpired);
	}

	public void setCheckObjectExpiredInterval(long checkObjectExpiredInterval) {
		this.connectionPool.setCheckObjectExpiredInterval(checkObjectExpiredInterval);
	}

	public void setMaxWaitTimeForExpiration(long maxWaitTimeForExpiration) {
		this.connectionPool.setMaxWaitTimeForExpiration(maxWaitTimeForExpiration);
	}

	public void setAcceptableExecutionTime(long acceptableExecutionTime) {
		this.connectionPool.getQueryStatistics().setAcceptableExecutionTime(acceptableExecutionTime);
	}

	public void setStatisticalSqlSampleCount(int statisticalSqlSampleCount) {
		this.connectionPool.getQueryStatistics().setStatisticalSqlSampleCount(statisticalSqlSampleCount);
	}

	public Map<String, QuerySpan> getStatisticsResult(String daily) {
		return connectionPool.getStatisticsResult(daily);
	}

	public PrintWriter getLogWriter() throws SQLException {
		return DriverManager.getLogWriter();
	}

	public void setLogWriter(PrintWriter logWriter) throws SQLException {
		DriverManager.setLogWriter(logWriter);
	}

	public void setLoginTimeout(int seconds) throws SQLException {
		DriverManager.setLoginTimeout(seconds);
	}

	public int getLoginTimeout() throws SQLException {
		return DriverManager.getLoginTimeout();
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new SQLException(getClass().getName() + " is not a wrapper.");
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	public Connection getConnection() throws SQLException {
		return connectionPool.take();
	}

	public void close() throws SQLException {
		connectionPool.close();
	}

}
