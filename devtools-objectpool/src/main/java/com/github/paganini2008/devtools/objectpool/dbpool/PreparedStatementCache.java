/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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
import java.util.Map;

import com.github.paganini2008.devtools.collection.LruMap;

/**
 * Build a PreparedStatementCache for each connection.
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class PreparedStatementCache {

	private final LruMap<String, PooledPreparedStatement> cache;
	private final DailyQueryStatistics queryStatistics;

	/**
	 * To create a PreparedStatementCache with a LruMap
	 * 
	 * @param maxSize
	 *            lruCache's size
	 */
	public PreparedStatementCache(int maxSize, DailyQueryStatistics queryStatistics) {
		this.cache = new LruMap<String, PooledPreparedStatement>(maxSize) {

			private static final long serialVersionUID = 847425849826360635L;

			public void onEviction(String eldestKey, PooledPreparedStatement ps) {
				ps.close();
			}
		};
		this.queryStatistics = queryStatistics;
	}

	/**
	 * Destroy the cache
	 */
	public void destroy() {
		for (Map.Entry<String, PooledPreparedStatement> entry : cache.entrySet()) {
			entry.getValue().close();
		}
	}

	public int size() {
		return cache.size();
	}

	public void giveback(String sql, PooledPreparedStatement pps) throws SQLException {
		if (pps.isOpened()) {
			cache.put(sql, pps);
		}
	}

	public PooledPreparedStatement take(String sql, Connection connection, Method method, Object[] args)
			throws SQLException {
		PooledPreparedStatement instance = cache.remove(sql);
		if (instance == null || !instance.isOpened()) {
			cache.put(sql, createObject(sql, connection, method, args));
			instance = cache.get(sql);
			instance.setQueryStatistics(queryStatistics);
		}
		instance.getRealStatement().clearWarnings();
		instance.getRealStatement().clearParameters();
		instance.getRealStatement().clearBatch();
		return instance;
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
	protected PooledPreparedStatement createObject(String sql, Connection connection, Method method, Object[] args)
			throws SQLException {
		try {
			return new PooledPreparedStatement(sql, this, (PreparedStatement) method.invoke(connection, args));
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
