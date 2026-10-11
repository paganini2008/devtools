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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.paganini2008.devtools.jdbc.JdbcUtils;
import com.github.paganini2008.devtools.logging.Log;
import com.github.paganini2008.devtools.logging.LogFactory;

/**
 * PooledPreparedStatement wraps a physical {@link PreparedStatement} cached by
 * {@link PreparedStatementCache}. Every lending gets a fresh proxy whose
 * {@code close()} closes the result sets it opened and returns the statement
 * to the cache.
 * 
 * @author Fred Feng
 * @date 2019-07-29
 * @since 2.0.1
 */
public class PooledPreparedStatement implements InvocationHandler {

	private static final Log logger = LogFactory.getLog(PooledPreparedStatement.class);

	private static final String CLOSE_METHOD = "close";
	private static final Class<?>[] IFACES = new Class<?>[] { PreparedStatement.class };
	private static final Set<String> STATE_SETTERS = new HashSet<String>(
			Arrays.asList("setMaxRows", "setLargeMaxRows", "setQueryTimeout", "setFetchSize", "setMaxFieldSize", "setFetchDirection"));

	public PooledPreparedStatement(String sql, PreparedStatementCache statementCache, PreparedStatement ps) {
		this(sql, sql, statementCache, ps);
	}

	PooledPreparedStatement(String sql, String key, PreparedStatementCache statementCache, PreparedStatement ps) {
		this.sql = sql;
		this.key = key;
		this.statementCache = statementCache;
		this.realStatement = ps;
		this.proxyStatement = newProxyStatement();
	}

	private final String sql;
	private final String key;
	private final PreparedStatementCache statementCache;
	private final PreparedStatement realStatement;
	private volatile PreparedStatement proxyStatement;
	private final List<ResultSet> resultSets = new ArrayList<ResultSet>();
	private volatile DailyQueryStatistics queryStatistics;
	private volatile int[] defaultState;

	private PreparedStatement newProxyStatement() {
		return (PreparedStatement) Proxy.newProxyInstance(getClass().getClassLoader(), IFACES, this);
	}

	public void setQueryStatistics(DailyQueryStatistics queryStatistics) {
		this.queryStatistics = queryStatistics;
	}

	public String getSql() {
		return sql;
	}

	public PreparedStatement getRealStatement() {
		return realStatement;
	}

	/**
	 * Get the proxy statement of current lending, or null if it is not lent.
	 * 
	 * @return
	 */
	public PreparedStatement getProxyStatement() {
		return proxyStatement;
	}

	/**
	 * Check whether the physical statement is still open.
	 * 
	 * @return
	 */
	public boolean isOpened() {
		try {
			return !realStatement.isClosed();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}

	/**
	 * Close the physical statement.
	 */
	public void close() {
		proxyStatement = null;
		closeResultSets();
		JdbcUtils.closeQuietly(realStatement);
	}

	/**
	 * Restore the statement settings (max rows, query timeout, fetch size, max
	 * field size and fetch direction) changed by the last user, so that they
	 * never leak to the next user of the cached statement.
	 * 
	 * @throws SQLException
	 */
	void restoreState() throws SQLException {
		final int[] state = defaultState;
		if (state != null) {
			realStatement.setMaxRows(state[0]);
			realStatement.setQueryTimeout(state[1]);
			realStatement.setFetchSize(state[2]);
			realStatement.setMaxFieldSize(state[3]);
			realStatement.setFetchDirection(state[4]);
			defaultState = null;
		}
	}

	private void saveStateIfNecessary(String methodName) throws SQLException {
		if (defaultState == null && STATE_SETTERS.contains(methodName)) {
			defaultState = new int[] { realStatement.getMaxRows(), realStatement.getQueryTimeout(), realStatement.getFetchSize(),
					realStatement.getMaxFieldSize(), realStatement.getFetchDirection() };
		}
	}

	void lease() {
		this.proxyStatement = newProxyStatement();
	}

	/**
	 * End current lending and give the statement back to the cache.
	 */
	void release() {
		proxyStatement = null;
		closeResultSets();
		try {
			statementCache.giveback(key, this);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			close();
		}
	}

	private void closeResultSets() {
		synchronized (resultSets) {
			for (ResultSet rs : resultSets) {
				JdbcUtils.closeQuietly(rs);
			}
			resultSets.clear();
		}
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		final String methodName = method.getName();
		if (methodName.equals("equals")) {
			return (proxy == args[0]);
		} else if (methodName.equals("hashCode")) {
			return System.identityHashCode(proxy);
		} else if (methodName.equals("toString")) {
			return realStatement.toString();
		}
		final boolean current = proxy == proxyStatement;
		if (CLOSE_METHOD.equals(methodName)) {
			if (current) {
				release();
			}
			return null;
		} else if (methodName.equals("isClosed") && !current) {
			return true;
		}
		if (!current) {
			throw new SQLException("PreparedStatement is closed.");
		}
		if (methodName.equals("getConnection")) {
			Connection connection = statementCache.getProxyConnection();
			if (connection != null) {
				return connection;
			}
		} else if (methodName.startsWith("set") && args != null && args.length == 1) {
			saveStateIfNecessary(methodName);
		} else if (methodName.startsWith("set") && args != null && args.length >= 2 && args[0] instanceof Integer) {
			if (logger.isDebugEnabled()) {
				logger.debug("[{}] Invoke: {}, Parameter: {}", new Object[] { args[0], methodName, args[1] });
			}
		} else if (methodName.equals("executeQuery") && queryStatistics != null) {
			long start = System.currentTimeMillis();
			Object result = invokeReal(method, args);
			queryStatistics.executed(sql, args, start, System.currentTimeMillis());
			return result;
		}
		return invokeReal(method, args);
	}

	private Object invokeReal(Method method, Object[] args) throws Throwable {
		Object result;
		try {
			result = method.invoke(realStatement, args);
		} catch (InvocationTargetException e) {
			throw ExceptionUtils.unwrapThrowable(e);
		}
		if (result instanceof ResultSet) {
			synchronized (resultSets) {
				resultSets.removeIf(rs -> {
					try {
						return rs.isClosed();
					} catch (SQLException e) {
						return true;
					}
				});
				resultSets.add((ResultSet) result);
			}
		}
		return result;
	}
}
