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

	public PooledPreparedStatement take(String sql, Connection connection, Method method, Object[] args) throws SQLException {
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
	protected PooledPreparedStatement createObject(String sql, Connection connection, Method method, Object[] args) throws SQLException {
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
