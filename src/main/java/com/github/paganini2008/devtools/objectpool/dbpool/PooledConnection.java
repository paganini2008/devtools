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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.paganini2008.devtools.jdbc.JdbcUtils;
import com.github.paganini2008.devtools.logging.Log;
import com.github.paganini2008.devtools.logging.LogFactory;

/**
 * 
 * PooledConnection wraps a physical {@link Connection} managed by a
 * {@link ConnectionPool}. Every borrowing gets a fresh proxy connection whose
 * {@code close()} gives the physical connection back to the pool, so a stale
 * reference can never touch a connection lent to someone else.
 *
 * @author Fred Feng
 * @date 2019-07-29
 * @since 2.0.1
 */
public class PooledConnection implements InvocationHandler {

	private static final Log logger = LogFactory.getLog(PooledConnection.class);
	private static final String CLOSE_METHOD = "close";
	private static final Class<?>[] IFACES = new Class<?>[] { Connection.class };

	private final ConnectionPool connectionPool;
	private final Connection realConnection;
	private volatile Connection proxyConnection;
	private volatile boolean valid;
	private final PreparedStatementCache statementCache;
	private final Set<Statement> openStatements = Collections.newSetFromMap(new IdentityHashMap<Statement, Boolean>());
	private final ExecutorService executor;
	private final boolean defaultAutoCommit;
	private final boolean defaultReadOnly;
	private final int defaultTransactionIsolation;

	PooledConnection(Connection connection, int statementCacheSize, ExecutorService executor, ConnectionPool connectionPool)
			throws SQLException {
		this.realConnection = connection;
		this.executor = executor;
		this.connectionPool = connectionPool;
		this.defaultAutoCommit = connection.getAutoCommit();
		this.defaultReadOnly = connection.isReadOnly();
		this.defaultTransactionIsolation = connection.getTransactionIsolation();
		this.statementCache = new PreparedStatementCache(statementCacheSize, connectionPool.getQueryStatistics());
		this.proxyConnection = newProxyConnection();
	}

	private Connection newProxyConnection() {
		Connection proxy = (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), IFACES, this);
		statementCache.setProxyConnection(proxy);
		return proxy;
	}

	/**
	 * Start a new borrowing: create a new proxy connection and mark it valid.
	 * 
	 * @return the new proxy connection
	 */
	synchronized Connection lease() {
		this.proxyConnection = newProxyConnection();
		this.valid = true;
		return proxyConnection;
	}

	/**
	 * End current borrowing if the proxy is the current one. The check and the
	 * transition are atomic so that concurrent {@code close()} calls on one
	 * proxy give the connection back only once.
	 */
	private synchronized boolean endLease(Object proxy) {
		if (valid && proxy == proxyConnection) {
			valid = false;
			return true;
		}
		return false;
	}

	/**
	 * Remember a plain statement ({@code createStatement/prepareCall}) so that
	 * it can be closed when the connection is given back.
	 */
	private void track(Object result) {
		if (result instanceof Statement) {
			synchronized (openStatements) {
				if (openStatements.size() >= 16) {
					openStatements.removeIf(st -> {
						try {
							return st.isClosed();
						} catch (SQLException e) {
							return true;
						}
					});
				}
				openStatements.add((Statement) result);
			}
		}
	}

	private void closeOpenStatements() {
		List<Statement> statements;
		synchronized (openStatements) {
			statements = new ArrayList<Statement>(openStatements);
			openStatements.clear();
		}
		for (Statement statement : statements) {
			JdbcUtils.closeQuietly(statement);
		}
	}

	/**
	 * Restore the state changed by the last borrower: close the unclosed plain
	 * statements, reclaim the unclosed prepared statements, roll back the uncommitted transaction and reset auto-commit,
	 * read-only and transaction isolation to their initial values.
	 * 
	 * @throws SQLException
	 */
	void reset() throws SQLException {
		closeOpenStatements();
		statementCache.reclaim();
		if (!realConnection.getAutoCommit()) {
			realConnection.rollback();
		}
		if (realConnection.getAutoCommit() != defaultAutoCommit) {
			realConnection.setAutoCommit(defaultAutoCommit);
		}
		if (realConnection.isReadOnly() != defaultReadOnly) {
			realConnection.setReadOnly(defaultReadOnly);
		}
		if (realConnection.getTransactionIsolation() != defaultTransactionIsolation) {
			realConnection.setTransactionIsolation(defaultTransactionIsolation);
		}
		realConnection.clearWarnings();
	}

	/**
	 * Close the cached statements and the physical connection.
	 */
	public void close() {
		valid = false;
		closeOpenStatements();
		statementCache.destroy();
		JdbcUtils.closeQuietly(realConnection);
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	/**
	 * Check whether the physical connection is still open.
	 * 
	 * @return
	 */
	public boolean isOpened() {
		try {
			return !realConnection.isClosed();
		} catch (SQLException e) {
			return false;
		}
	}

	public Connection getRealConnection() {
		return realConnection;
	}

	/**
	 * Get the proxy connection of current borrowing.
	 * 
	 * @return
	 */
	public Connection getProxyConnection() {
		return proxyConnection;
	}

	PreparedStatementCache getStatementCache() {
		return statementCache;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		final String methodName = method.getName();
		if (methodName.equals("equals")) {
			return (proxy == args[0]);
		} else if (methodName.equals("hashCode")) {
			return System.identityHashCode(proxy);
		} else if (methodName.equals("toString")) {
			return realConnection.toString();
		}
		if (CLOSE_METHOD.equals(methodName)) {
			if (endLease(proxy)) {
				try {
					connectionPool.giveback(this);
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
			return null;
		}
		final boolean current = valid && proxy == proxyConnection;
		if (methodName.equals("isClosed") && !current) {
			return true;
		} else if (methodName.equals("isValid") && !current) {
			return false;
		}
		if (!current) {
			throw new SQLException("Connection is closed or unaccessable now.");
		}
		if (methodName.equals("prepareStatement")) {
			String sql = (String) args[0];
			if (logger.isDebugEnabled()) {
				logger.debug("[{}] Execute sql: {}", statementCache.size(), sql);
			}
			PooledPreparedStatement pps = statementCache.take(sql, realConnection, method, args);
			return pps.getProxyStatement();
		}
		final Object result;
		if (executor != null) {
			result = executeAsynchronously(method, args);
		} else {
			try {
				result = method.invoke(realConnection, args);
			} catch (InvocationTargetException e) {
				throw ExceptionUtils.unwrapThrowable(e);
			}
		}
		track(result);
		return result;
	}

	private Object executeAsynchronously(Method method, Object[] args) throws Throwable {
		// claimed by the executor thread when the operation starts, or by the caller to prevent it from starting
		final AtomicBoolean claimed = new AtomicBoolean();
		final Future<Object> future;
		try {
			future = executor.submit(() -> {
				return claimed.compareAndSet(false, true) ? method.invoke(realConnection, args) : null;
			});
		} catch (RejectedExecutionException e) {
			throw new SQLException("Executor rejected the connection operation: " + method.getName(), e);
		}
		final long timeout = connectionPool.getConnectionTimeout();
		try {
			return timeout > 0 ? future.get(timeout, TimeUnit.MILLISECONDS) : future.get();
		} catch (TimeoutException e) {
			future.cancel(true);
			valid = false;
			connectionPool.discard(this);
			throw new SQLException("Connection timeout!", e);
		} catch (ExecutionException e) {
			throw ExceptionUtils.unwrapThrowable(e.getCause());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			if (claimed.compareAndSet(false, true)) {
				// not started yet: it will never run
				future.cancel(false);
			} else if (!future.isDone()) {
				// the operation is running on the physical connection and its outcome is unknown
				future.cancel(true);
				valid = false;
				connectionPool.discard(this);
			}
			throw new SQLException("Interrupted while executing " + method.getName(), e);
		}
	}
}
