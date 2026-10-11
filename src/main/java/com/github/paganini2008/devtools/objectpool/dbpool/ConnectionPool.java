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
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import com.github.paganini2008.devtools.logging.Log;
import com.github.paganini2008.devtools.logging.LogFactory;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.devtools.objectpool.GenericObjectPool;

/**
 * ConnectionPool is a JDBC connection pool built on {@link GenericObjectPool}.
 * Borrowed connections are proxies which are given back to the pool when
 * closed.
 * 
 * @author Fred Feng
 * @date 2019-07-29
 * @since 2.0.1
 */
public class ConnectionPool {

	private static final Log logger = LogFactory.getLog(ConnectionPool.class);

	public ConnectionPool() {
		connectionFactory = new ConnectionObjectFactory(this);
		objectPool = new GenericObjectPool(connectionFactory);
	}

	private final ConnectionObjectFactory connectionFactory;
	private final GenericObjectPool objectPool;

	private DailyQueryStatistics queryStatistics = new DailyQueryStatistics();
	private long maxWaitTime = 60L * 1000;
	private long connectionTimeout = 60L * 1000;

	public void setUser(String username) {
		this.connectionFactory.setUser(username);
	}

	public void setPassword(String password) {
		this.connectionFactory.setPassword(password);
	}

	public void setDriverClassName(String driverClassName) {
		this.connectionFactory.setDriverClassName(driverClassName);
	}

	public void setJdbcUrl(String url) {
		this.connectionFactory.setJdbcUrl(url);
	}

	public void setTestSql(String testSql) {
		this.connectionFactory.setTestSql(testSql);
	}

	public void setAutoCommit(boolean autoCommit) {
		this.connectionFactory.setAutoCommit(autoCommit);
	}

	public void setDefaultTransactionIsolationLevel(int defaultTransactionIsolationLevel) {
		this.connectionFactory.setDefaultTransactionIsolationLevel(defaultTransactionIsolationLevel);
	}

	public void setStatementCacheSize(int statementCacheSize) {
		this.connectionFactory.setStatementCacheSize(statementCacheSize);
	}

	public void setMaxIdleSize(int maxIdleSize) {
		this.objectPool.setMaxIdleSize(maxIdleSize);
	}

	public void setMaxSize(int maxSize) {
		this.objectPool.setMaxPoolSize(maxSize);
	}

	public void setMaxUsage(int usage) {
		this.objectPool.setMaxUsage(usage);
	}

	/**
	 * Set the max time(ms) to wait for an available connection in
	 * {@link #take()}.
	 * 
	 * @param maxWaitTime
	 */
	public void setMaxWaitTime(long maxWaitTime) {
		this.maxWaitTime = maxWaitTime;
	}

	public long getMaxWaitTime() {
		return maxWaitTime;
	}

	/**
	 * Set the max time(ms) {@link #close()} waits for the borrowed connections
	 * to be given back.
	 * 
	 * @param closeTimeout
	 */
	public void setCloseTimeout(long closeTimeout) {
		this.objectPool.setCloseTimeout(closeTimeout);
	}

	public void setTestWhileIdle(boolean testWhileIdle) {
		this.objectPool.setTestWhileIdle(testWhileIdle);
	}

	public void setTestWhileIdleInterval(long testWhileIdleInterval) {
		this.objectPool.setTestWhileIdleInterval(testWhileIdleInterval);
	}

	public void setCheckObjectExpired(boolean checkObjectExpired) {
		this.objectPool.setCheckObjectExpired(checkObjectExpired);
	}

	public void setCheckObjectExpiredInterval(long checkObjectExpiredInterval) {
		this.objectPool.setCheckObjectExpiredInterval(checkObjectExpiredInterval);
	}

	public void setMaxWaitTimeForExpiration(long maxWaitTimeForExpiration) {
		this.objectPool.setMaxWaitTimeForExpiration(maxWaitTimeForExpiration);
	}

	/**
	 * Set the max time(ms) to execute one connection operation when an executor
	 * is configured. A timed out connection is discarded.
	 * 
	 * @param connectionTimeout
	 */
	public void setConnectionTimeout(long connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public long getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setExecutor(ExecutorService executor) {
		this.connectionFactory.setExecutor(executor);
	}

	public Map<String, QuerySpan> getStatisticsResult(String daily) {
		return queryStatistics.getStatisticsResult(daily);
	}

	DailyQueryStatistics getQueryStatistics() {
		return queryStatistics;
	}

	GenericObjectPool getObjectPool() {
		return objectPool;
	}

	ConnectionObjectFactory getConnectionFactory() {
		return connectionFactory;
	}

	/**
	 * Take a connection from the pool, waiting at most
	 * {@link #getMaxWaitTime()} milliseconds.
	 * 
	 * @return a proxy connection which goes back to the pool when closed
	 * @throws SQLException
	 *             if no connection is available in time, the pool is closed
	 *             or a new connection can not be created
	 */
	public Connection take() throws SQLException {
		try {
			PooledConnection connection = (PooledConnection) objectPool.borrowObject(maxWaitTime);
			Connection proxy = connection.lease();
			if (logger.isDebugEnabled()) {
				logger.debug(ThreadUtils.currentThreadName() + " take connection " + connection.getRealConnection());
			}
			return proxy;
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw new SQLException(e.getMessage(), e);
		}
	}

	/**
	 * Give back a connection to the pool. Its state is restored first and it is
	 * discarded if the restoring fails. A connection which is no longer pooled
	 * (e.g. discarded as expired) is closed.
	 * 
	 * @param connection
	 * @throws SQLException
	 */
	public void giveback(PooledConnection connection) throws SQLException {
		connection.setValid(false);
		if (objectPool.getDetail(connection) == null) {
			connection.close();
			return;
		}
		try {
			connection.reset();
		} catch (SQLException e) {
			logger.warn("Discard connection because it can not be reset: " + e.getMessage(), e);
			discard(connection);
			return;
		}
		try {
			objectPool.givebackObject(connection);
			if (logger.isDebugEnabled()) {
				logger.debug(ThreadUtils.currentThreadName() + " giveback connection " + connection.getRealConnection());
			}
		} catch (Exception e) {
			throw new SQLException(e.getMessage(), e);
		}
	}

	/**
	 * Remove the connection from the pool and close it.
	 * 
	 * @param connection
	 */
	void discard(PooledConnection connection) {
		connection.setValid(false);
		try {
			objectPool.discardObject(connection);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			connection.close();
		}
	}

	/**
	 * Close the pool and all its connections.
	 * 
	 * @throws SQLException
	 */
	public void close() throws SQLException {
		try {
			objectPool.close();
		} catch (Exception e) {
			throw new SQLException(e.getMessage(), e);
		} finally {
			connectionFactory.shutdownExecutor();
		}
	}

}
