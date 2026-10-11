/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

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
import com.github.paganini2008.devtools.multithreads.ExecutorUtils;
import com.github.paganini2008.devtools.objectpool.ObjectFactory;

/**
 * ConnectionObjectFactory is the {@link ObjectFactory} of
 * {@link ConnectionPool}, which creates, validates and destroys pooled
 * connections.
 * 
 * @author Fred Feng
 * @date 2019-07-29
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
	private ExecutorService executor;
	private boolean executorConfigured;
	private boolean defaultExecutor;

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
		return obtainExecutor();
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

	/**
	 * Set the executor which runs the connection operations so that they can be
	 * timed out, null to run them in the caller thread. If not set, a default
	 * executor with 8 daemon threads is created on demand.
	 * 
	 * @param executor
	 */
	public synchronized void setExecutor(ExecutorService executor) {
		if (this.defaultExecutor && this.executor != null && this.executor != executor) {
			this.executor.shutdown();
		}
		this.executor = executor;
		this.executorConfigured = true;
		this.defaultExecutor = false;
	}

	synchronized ExecutorService obtainExecutor() {
		if (executor == null && !executorConfigured) {
			executor = Executors.newFixedThreadPool(8, r -> {
				Thread thread = new Thread(r, "dbpool-executor");
				thread.setDaemon(true);
				return thread;
			});
			defaultExecutor = true;
		}
		return executor;
	}

	synchronized void shutdownExecutor() {
		if (executor != null) {
			ExecutorUtils.gracefulShutdown(executor, 60000L);
		}
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

	/**
	 * Create a new physical connection and wrap it as a {@link PooledConnection}.
	 */
	public PooledConnection createObject() throws SQLException {
		Connection connection = DriverManager.getConnection(jdbcUrl, user, password);
		try {
			configureConnection(connection);
			return new PooledConnection(connection, statementCacheSize != null ? statementCacheSize : 0, obtainExecutor(),
					connectionPool);
		} catch (SQLException | RuntimeException e) {
			JdbcUtils.closeQuietly(connection);
			throw e;
		}
	}

	/**
	 * Same as {@link #testObject(Object)}.
	 * 
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	public boolean validateObject(Object connection) throws SQLException {
		if (!(connection instanceof PooledConnection) || !((PooledConnection) connection).isOpened()) {
			return false;
		}
		if (StringUtils.isNotBlank(testSql)) {
			testSql(((PooledConnection) connection).getRealConnection(), testSql);
		}
		return true;
	}

	/**
	 * Check that the connection is open and, if a test sql is set, that the
	 * test sql can be executed.
	 */
	@Override
	public boolean testObject(Object connection) throws SQLException {
		return validateObject(connection);
	}

	public void destroyObject(Object connection) throws SQLException {
		if (connection != null) {
			((PooledConnection) connection).close();
		}
	}

}
