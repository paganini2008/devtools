/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools.objectpool.dbpool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.jdbc.JdbcUtils;
import com.github.paganini2008.devtools.objectpool.ObjectFactory;

/**
 * Build a java.sql.Connection object factory.
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class ConnectionObjectFactory implements ObjectFactory {

	private String user;
	private String password;
	private String driverClassName;
	private String jdbcUrl;
	private String testSql;
	private Boolean autoCommit;
	private Integer defaultTransactionIsolationLevel;
	private Integer statementCacheSize = 32;
	private ExecutorService executor = Executors.newFixedThreadPool(8);

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

	public void setAutoCommit(Boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	public Integer getDefaultTransactionIsolationLevel() {
		return defaultTransactionIsolationLevel;
	}

	/**
	 * Set default transaction level.
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
	 * Set a test sql like 'SELECT 1' to testify its availability when borrow a
	 * connection object.
	 * 
	 * @param testSql
	 */
	public void setTestSql(String testSql) {
		this.testSql = testSql;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public ExecutorService getExecutor() {
		return executor;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	/**
	 * Set the DriverClassName if the class exists.
	 * 
	 * @param driverClassName
	 * @throws SQLException
	 */
	public void setDriverClassName(String driverClassName) {
		try {
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Invalid DriverClassName: " + driverClassName, e);
		}
		this.driverClassName = driverClassName;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
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
		Connection connection = DriverManager.getConnection(jdbcUrl, user, password);
		configureConnection(connection);
		return new PooledConnection(connection, statementCacheSize, executor, connectionPool);
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
