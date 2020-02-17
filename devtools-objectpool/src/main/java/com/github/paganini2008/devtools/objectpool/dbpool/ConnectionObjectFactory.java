package com.github.paganini2008.devtools.objectpool.dbpool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.jdbc.JdbcUtils;
import com.github.paganini2008.devtools.objectpool.ObjectFactory;

/**
 * Build a java.sql.Connection object factory.
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ConnectionObjectFactory implements ObjectFactory {

	private String username;
	private String password;
	private String driverClassName;
	private String url;
	private String testSql;
	private Boolean autoCommit;
	private Integer defaultTransactionIsolationLevel;
	private Integer statementCacheSize = 32;

	private final ConnectionPool connectionPool;

	public ConnectionObjectFactory(ConnectionPool connectionPool) {
		this.connectionPool = connectionPool;
	}

	/**
	 * Set the pooled java.sql.PreparedStatement object cache's size.
	 * 
	 * @param statementCacheSize
	 */
	public void setStatementCacheSize(Integer statementCacheSize) {
		this.statementCacheSize = statementCacheSize;
	}

	public Integer getStatementCacheSize() {
		return statementCacheSize;
	}

	public Boolean getAutoCommit() {
		return autoCommit;
	}

	/**
	 * Set Connection object's attribute 'autoCommit'
	 * 
	 * @param autoCommit
	 */
	public void setAutoCommit(Boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	public Integer getDefaultTransactionIsolationLevel() {
		return defaultTransactionIsolationLevel;
	}

	/**
	 * Set Connection object's default transaction level.
	 * 
	 * @param defaultTransactionIsolationLevel
	 */
	public void setDefaultTransactionIsolationLevel(Integer defaultTransactionIsolationLevel) {
		this.defaultTransactionIsolationLevel = defaultTransactionIsolationLevel;
	}

	public String getTestSql() {
		return testSql;
	}

	/**
	 * Set a test sql like 'SELECT 1 FROM DUAL' when the borrowed object make sure
	 * its effectiveness.
	 * 
	 * @param testSql
	 */
	public void setTestSql(String testSql) {
		this.testSql = testSql;
	}

	public String getUser() {
		return username;
	}

	/**
	 * Set schema username
	 * 
	 * @param username
	 */
	public void setUser(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	/**
	 * Set schema password
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	/**
	 * Set the DriverClassName if its class existed.
	 * 
	 * @param driverClassName
	 * @throws SQLException
	 */
	public void setDriverClassName(String driverClassName) {
		try {
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Invalid jdbc driver class: " + driverClassName, e);
		}
		this.driverClassName = driverClassName;
	}

	public String getUrl() {
		return url;
	}

	/**
	 * Set jdbc url
	 * 
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	protected void configureConnection(Connection connection) throws SQLException {
		if (autoCommit != null && connection.getAutoCommit() != autoCommit) {
			connection.setAutoCommit(autoCommit);
		}
		if (defaultTransactionIsolationLevel != null) {
			connection.setTransactionIsolation(defaultTransactionIsolationLevel);
		}
	}

	/**
	 * Execute test sql
	 * 
	 * @param connection
	 * @param testSql
	 * @throws SQLException
	 */
	protected void testSql(Connection connection, String testSql) throws SQLException {
		Statement sm = null;
		try {
			sm = connection.createStatement();
			sm.executeQuery(testSql);
		} finally {
			JdbcUtils.closeQuietly(sm);
		}
	}

	public PooledConnection createObject() throws SQLException {
		Connection connection = DriverManager.getConnection(url, username, password);
		configureConnection(connection);
		return new PooledConnection(connection, statementCacheSize, connectionPool);
	}

	public boolean validateObject(Object connection) throws SQLException {
		boolean result = connection != null && ((PooledConnection) connection).isOpened();
		if (StringUtils.isNotBlank(testSql)) {
			testSql(((PooledConnection) connection).getRealConnection(), testSql);
		}
		return result;
	}

	public void destroyObject(Object connection) throws SQLException {
		((PooledConnection) connection).close();
	}

}
