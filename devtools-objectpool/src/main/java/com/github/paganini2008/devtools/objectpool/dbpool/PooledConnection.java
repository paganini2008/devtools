package com.github.paganini2008.devtools.objectpool.dbpool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.github.paganini2008.devtools.jdbc.JdbcUtils;
import com.github.paganini2008.devtools.logging.Log;
import com.github.paganini2008.devtools.logging.LogFactory;

/**
 * 
 * PooledConnection
 *
 * @author Fred Feng
 * @version 1.0
 */
public class PooledConnection implements InvocationHandler {

	private static final Log logger = LogFactory.getLog(PooledConnection.class);
	private static final String CLOSE_METHOD = "close";
	private static final Class<?>[] IFACES = new Class<?>[] { Connection.class };

	private final ConnectionPool connectionPool;
	private final Connection realConnection;
	private final Connection proxyConnection;
	private volatile boolean valid;
	private final PreparedStatementCache statementCache;
	private final ExecutorService executor;

	PooledConnection(Connection connection, int statementCacheSize, ExecutorService executor, ConnectionPool connectionPool) {
		this.realConnection = connection;
		this.executor = executor;
		this.connectionPool = connectionPool;
		this.proxyConnection = (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), IFACES, this);
		this.statementCache = new PreparedStatementCache(statementCacheSize, connectionPool.getQueryStatistics());
	}

	public void close() {
		if (isOpened()) {
			statementCache.destroy();
			JdbcUtils.closeQuietly(realConnection);
		}
		valid = false;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public boolean isOpened() {
		try {
			return realConnection.isClosed() == false;
		} catch (SQLException e) {
			return false;
		}
	}

	public Connection getRealConnection() {
		return realConnection;
	}

	public Connection getProxyConnection() {
		return proxyConnection;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		String methodName = method.getName();
		if (methodName.equals("equals")) {
			return (realConnection == args[0]);
		} else if (methodName.equals("hashCode")) {
			return System.identityHashCode(realConnection);
		} else if (methodName.equals("toString")) {
			return realConnection.toString();
		} else {
			try {
				if (methodName.equals("prepareStatement")) {
					String sql = (String) args[0];
					if (logger.isDebugEnabled()) {
						logger.debug("[{}] Execute sql: {}", statementCache.size(), sql);
					}
					PooledPreparedStatement pps = statementCache.take(sql, realConnection, method, args);
					return pps.getProxyStatement();
				} else if (CLOSE_METHOD.equals(methodName)) {
					valid = false;
					try {
						connectionPool.giveback(this);
					} catch (SQLException e) {
						logger.error(e.getMessage(), e);
					}
					return null;
				} else {
					if (!valid) {
						throw new SQLException("Connection is closed or unaccessable now.");
					}
					try {
						if (executor != null) {
							return executeAsynchronously(method, args);
						}
						return method.invoke(realConnection, args);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						if (!(e instanceof SQLException)) {
							throw new SQLException(e);
						}
						throw e;
					}
				}
			} finally {
			}
		}
	}

	private Object executeAsynchronously(Method method, Object[] args) throws Throwable {
		Future<Object> future = executor.submit(() -> {
			return method.invoke(realConnection, args);
		});
		try {
			return future.get(connectionPool.getConnectionTimeout(), TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			if (e instanceof TimeoutException) {
				getProxyConnection().close();
				throw new SQLException("Connection timeout!", e);
			}
			if (!(e instanceof SQLException)) {
				throw new SQLException(e);
			}
			throw e;
		}
	}
}
