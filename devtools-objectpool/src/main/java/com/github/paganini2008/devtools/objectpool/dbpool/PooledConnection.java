package com.github.paganini2008.devtools.objectpool.dbpool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

import com.github.paganini2008.devtools.jdbc.JdbcUtils;
import com.github.paganini2008.devtools.logging.Log;
import com.github.paganini2008.devtools.logging.LogFactory;

/**
 * Pooled Sql Connection
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class PooledConnection implements InvocationHandler {

	private static final Log logger = LogFactory.getLog(PooledConnection.class);

	private static final String CLOSE = "close";
	private static final Class<?>[] IFACES = new Class<?>[] { Connection.class };

	private final ConnectionPool connectionPool;
	private final Connection realConnection;
	private final Connection proxyConnection;
	private volatile boolean valid;
	private final PreparedStatementCache statementCache;

	PooledConnection(Connection connection, int statementCacheSize, ConnectionPool connectionPool) {
		this.realConnection = connection;
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
						logger.debug("[{}]Execute sql: {}", statementCache.size(), sql);
					}
					PooledPreparedStatement pps = statementCache.take(sql, realConnection, method, args);
					return pps.getProxyStatement();
				} else if (CLOSE.equals(methodName)) {
					try {
						connectionPool.giveback(this);
					} catch (SQLException e) {
						logger.error(e.getMessage(), e);
					}
					valid = false;
					return null;
				} else {
					if (!valid) {
						throw new SQLException("Connection is closed or unavailable now.");
					}
					try {
						return method.invoke(realConnection, args);
					} catch (Throwable t) {
						throw ExceptionUtils.unwrapThrowable(t);
					}
				}
			} finally {
			}
		}
	}
}
