package com.github.paganini2008.devtools.objectpool.dbpool;

import java.sql.Connection;
import java.sql.SQLException;

import com.github.paganini2008.devtools.objectpool.GenericObjectPool;

/**
 * ConnectionPool contains the objectPool as a connection provider.
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class ConnectionPool {

	public ConnectionPool() {
		connectionFactory = new ConnectionFactory(this);
		objectPool = new GenericObjectPool(connectionFactory);
	}

	private final ConnectionFactory connectionFactory;
	private final GenericObjectPool objectPool;

	private DailyQueryStatistics queryStatistics = new DailyQueryStatistics();
	private long maxWaitTime = 60L * 1000;

	public DailyQueryStatistics getQueryStatistics() {
		return queryStatistics;
	}

	public void setUser(String username) {
		this.connectionFactory.setUser(username);
	}

	public void setPassword(String password) {
		this.connectionFactory.setPassword(password);
	}

	public void setDriverClassName(String driverClassName) {
		this.connectionFactory.setDriverClassName(driverClassName);
	}

	public void setUrl(String url) {
		this.connectionFactory.setUrl(url);
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

	public void setMaxUses(int maxAge) {
		this.objectPool.setMaxUses(maxAge);
	}

	public void setMaxWaitTime(long maxWaitTime) {
		this.maxWaitTime = maxWaitTime;
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
	 * Take a Connection
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Connection take() throws SQLException {
		try {
			PooledConnection connection = (PooledConnection) objectPool.borrowObject(maxWaitTime);
			connection.setValid(true);
			return connection.getProxyConnection();
		} catch (Exception e) {
			if (e instanceof SQLException) {
				throw (SQLException) e;
			}
			throw new SQLException(e.getMessage(), e);
		}
	}

	/**
	 * Giveback connection after returning
	 * 
	 * @param connection
	 * @throws SQLException
	 */
	public void giveback(PooledConnection connection) throws SQLException {
		try {
			objectPool.givebackObject(connection);
		} catch (Exception e) {
			if (e instanceof SQLException) {
				throw (SQLException) e;
			}
			throw new SQLException(e.getMessage(), e);
		}
	}

	/**
	 * Close the objectPool
	 * 
	 * @throws SQLException
	 */
	public void close() throws SQLException {
		try {
			objectPool.close();
		} catch (Exception e) {
			throw new SQLException(e.getMessage(), e);
		}
	}

}
