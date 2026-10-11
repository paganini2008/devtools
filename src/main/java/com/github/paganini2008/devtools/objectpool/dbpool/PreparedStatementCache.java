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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import com.github.paganini2008.devtools.collection.LruMap;

/**
 * PreparedStatementCache caches the {@link PreparedStatement}s of one pooled
 * connection, keyed by the sql and the other arguments of
 * {@code prepareStatement}. A cached statement is lent to only one user at a
 * time and returns to the cache when its proxy is closed.
 * 
 * @author Fred Feng
 * @date 2019-07-29
 * @since 2.0.1
 */
public class PreparedStatementCache {

	private final LruMap<String, PooledPreparedStatement> cache;
	private final Set<PooledPreparedStatement> leased = Collections.newSetFromMap(new IdentityHashMap<PooledPreparedStatement, Boolean>());
	private final DailyQueryStatistics queryStatistics;
	private volatile Connection proxyConnection;

	/**
	 * To create a PreparedStatementCache with a LruMap
	 * 
	 * @param maxSize
	 *            lruCache's size, a non-positive value disables caching
	 * @param queryStatistics
	 *            optional query statistics, may be null
	 */
	public PreparedStatementCache(int maxSize, DailyQueryStatistics queryStatistics) {
		this.cache = maxSize > 0 ? new LruMap<String, PooledPreparedStatement>(maxSize) {

			private static final long serialVersionUID = 847425849826360635L;

			public void onEviction(String eldestKey, PooledPreparedStatement ps) {
				if (ps != null) {
					ps.close();
				}
			}
		} : null;
		this.queryStatistics = queryStatistics;
	}

	void setProxyConnection(Connection proxyConnection) {
		this.proxyConnection = proxyConnection;
	}

	/**
	 * Get the proxy connection which statements of this cache belong to.
	 * 
	 * @return
	 */
	public Connection getProxyConnection() {
		return proxyConnection;
	}

	/**
	 * Close all cached and lent statements.
	 */
	public synchronized void destroy() {
		if (cache != null) {
			for (PooledPreparedStatement pps : new ArrayList<PooledPreparedStatement>(cache.values())) {
				pps.close();
			}
			cache.clear();
		}
		for (PooledPreparedStatement pps : new ArrayList<PooledPreparedStatement>(leased)) {
			pps.close();
		}
		leased.clear();
	}

	/**
	 * Get the count of cached (idle) statements.
	 * 
	 * @return
	 */
	public synchronized int size() {
		return cache != null ? cache.size() : 0;
	}

	/**
	 * Get the count of statements being lent.
	 * 
	 * @return
	 */
	public synchronized int leasedSize() {
		return leased.size();
	}

	/**
	 * Give back a lent statement to the cache. If another statement with the
	 * same key is already cached, the old one is closed.
	 * 
	 * @param key
	 *            cache key of the statement
	 * @param pps
	 * @throws SQLException
	 */
	public synchronized void giveback(String key, PooledPreparedStatement pps) throws SQLException {
		leased.remove(pps);
		if (cache != null && pps.isOpened()) {
			PooledPreparedStatement old = cache.put(key, pps);
			if (old != null && old != pps) {
				old.close();
			}
		} else {
			pps.close();
		}
	}

	/**
	 * Give back all the statements not closed by the last borrower.
	 */
	synchronized void reclaim() {
		for (PooledPreparedStatement pps : new ArrayList<PooledPreparedStatement>(leased)) {
			pps.release();
		}
		leased.clear();
	}

	/**
	 * Take a statement from cache or create a new one.
	 * 
	 * @param sql
	 * @param connection
	 *            the physical connection
	 * @param method
	 *            one of the {@code Connection.prepareStatement} methods
	 * @param args
	 *            arguments of the method
	 * @return
	 * @throws SQLException
	 */
	public synchronized PooledPreparedStatement take(String sql, Connection connection, Method method, Object[] args) throws SQLException {
		final String key = keyFor(args);
		PooledPreparedStatement instance = cache != null ? cache.remove(key) : null;
		if (instance != null) {
			try {
				if (instance.isOpened()) {
					PreparedStatement ps = instance.getRealStatement();
					ps.clearWarnings();
					ps.clearParameters();
					ps.clearBatch();
					instance.restoreState();
				} else {
					instance = null;
				}
			} catch (SQLException e) {
				instance.close();
				instance = null;
			}
		}
		if (instance == null) {
			instance = createObject(sql, connection, method, args);
			instance.setQueryStatistics(queryStatistics);
		}
		instance.lease();
		leased.add(instance);
		return instance;
	}

	static String keyFor(Object[] args) {
		if (args.length == 1) {
			return (String) args[0];
		}
		return args[0] + "#" + Arrays.deepToString(Arrays.copyOfRange(args, 1, args.length));
	}

	/**
	 * Create a PooledPreparedStatement object
	 * 
	 * @param sql
	 * @param connection
	 * @param method
	 * @param args
	 * @return
	 * @throws SQLException
	 */
	protected PooledPreparedStatement createObject(String sql, Connection connection, Method method, Object[] args) throws SQLException {
		try {
			return new PooledPreparedStatement(sql, keyFor(args), this, (PreparedStatement) method.invoke(connection, args));
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof SQLException) {
				throw (SQLException) e.getTargetException();
			} else {
				throw new IllegalStateException(e.getTargetException());
			}
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}
}
