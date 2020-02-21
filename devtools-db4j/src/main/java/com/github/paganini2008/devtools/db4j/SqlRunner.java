package com.github.paganini2008.devtools.db4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.paganini2008.devtools.Observable;
import com.github.paganini2008.devtools.Observer;
import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.db4j.mapper.BeanPropertyRowMapper;
import com.github.paganini2008.devtools.db4j.mapper.ColumnIndexRowMapper;
import com.github.paganini2008.devtools.db4j.mapper.RowMapper;
import com.github.paganini2008.devtools.db4j.mapper.TupleRowMapper;
import com.github.paganini2008.devtools.jdbc.Cursor;
import com.github.paganini2008.devtools.jdbc.DefaultPageableSql;
import com.github.paganini2008.devtools.jdbc.JdbcUtils;
import com.github.paganini2008.devtools.jdbc.PageableException;
import com.github.paganini2008.devtools.jdbc.PageableQuery;
import com.github.paganini2008.devtools.jdbc.PageableSql;
import com.github.paganini2008.devtools.jdbc.PreparedStatementCallback;

/**
 * 
 * SqlRunner
 *
 * @author Fred Feng
 * @version 1.0
 */
public class SqlRunner {

	private TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistryImpl();

	public TypeHandlerRegistry getTypeHandlerRegistry() {
		return typeHandlerRegistry;
	}

	// --------------------- Query --------------------------
	public <T> T query(Connection connection, String sql, Object[] parameters, ResultSetExtractor<T> extractor) throws SQLException {
		return query(connection, sql, PreparedStatementCallbackUtils.prepare(parameters, typeHandlerRegistry), extractor);
	}

	public <T> T query(Connection connection, String sql, Object[] parameters, JdbcType[] jdbcTypes, ResultSetExtractor<T> extractor)
			throws SQLException {
		return query(connection, sql, PreparedStatementCallbackUtils.prepare(parameters, jdbcTypes, typeHandlerRegistry), extractor);
	}

	public <T> T query(Connection connection, String sql, PreparedStatementCallback callback, ResultSetExtractor<T> extractor)
			throws SQLException {
		return query(connection, PreparedStatementCreatorUtils.forQuery(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY),
				callback, extractor);
	}

	public <T> T query(Connection connection, PreparedStatementCreator statementCreator, final PreparedStatementCallback callback,
			final ResultSetExtractor<T> extractor) throws SQLException {
		return execute(connection, statementCreator, new PreparedStatementExecutor<T>() {
			public T execute(PreparedStatement ps) throws SQLException {
				if (callback != null) {
					callback.setValues(ps);
				}
				ResultSet rs = null;
				try {
					rs = ps.executeQuery();
					return extractor.extractData(rs);
				} finally {
					JdbcUtils.closeQuietly(rs);
				}
			}

			public void close(PreparedStatement ps) {
				JdbcUtils.closeQuietly(ps);
			}
		});
	}

	public <T> T queryForObject(Connection connection, String sql, Object[] parameters, Class<T> requiredType) throws SQLException {
		return queryForObject(connection, sql, PreparedStatementCallbackUtils.prepare(parameters, typeHandlerRegistry), requiredType);
	}

	public <T> T queryForObject(Connection connection, String sql, Object[] parameters, JdbcType[] jdbcTypes, Class<T> requiredType)
			throws SQLException {
		return queryForObject(connection, sql, PreparedStatementCallbackUtils.prepare(parameters, jdbcTypes, typeHandlerRegistry),
				requiredType);
	}

	public <T> T queryForObject(Connection connection, String sql, PreparedStatementCallback callback, Class<T> requiredType)
			throws SQLException {
		return queryForObject(connection, PreparedStatementCreatorUtils.forDefault(sql), callback, requiredType);
	}

	public <T> T queryForObject(Connection connection, PreparedStatementCreator statementCreator, PreparedStatementCallback callback,
			Class<T> requiredType) throws SQLException {
		return queryForObject(connection, statementCreator, callback, new ColumnIndexRowMapper<T>(typeHandlerRegistry, requiredType));
	}

	public <T> T queryForObject(Connection connection, String sql, Object[] parameters, RowMapper<T> rowMapper) throws SQLException {
		return queryForObject(connection, sql, PreparedStatementCallbackUtils.prepare(parameters, typeHandlerRegistry), rowMapper);
	}

	public <T> T queryForObject(Connection connection, String sql, Object[] parameters, JdbcType[] jdbcTypes, RowMapper<T> rowMapper)
			throws SQLException {
		return queryForObject(connection, sql, PreparedStatementCallbackUtils.prepare(parameters, jdbcTypes, typeHandlerRegistry),
				rowMapper);
	}

	public <T> T queryForObject(Connection connection, String sql, PreparedStatementCallback callback, RowMapper<T> rowMapper)
			throws SQLException {
		return queryForObject(connection, PreparedStatementCreatorUtils.forDefault(sql), callback, rowMapper);
	}

	public <T> T queryForObject(Connection connection, PreparedStatementCreator statementCreator, PreparedStatementCallback callback,
			RowMapper<T> rowMapper) throws SQLException {
		return query(connection, statementCreator, callback, new FirstRowResultSetExtractor<T>(rowMapper));
	}

	public Tuple queryForTuple(Connection connection, String sql, Object[] parameters) throws SQLException {
		return queryForTuple(connection, sql, PreparedStatementCallbackUtils.prepare(parameters, typeHandlerRegistry));
	}

	public Tuple queryForTuple(Connection connection, String sql, Object[] parameters, JdbcType[] jdbcTypes) throws SQLException {
		return queryForTuple(connection, sql, PreparedStatementCallbackUtils.prepare(parameters, jdbcTypes, typeHandlerRegistry));
	}

	public Tuple queryForTuple(Connection connection, String sql, PreparedStatementCallback callback) throws SQLException {
		return queryForTuple(connection, PreparedStatementCreatorUtils.forDefault(sql), callback);
	}

	public Tuple queryForTuple(Connection connection, PreparedStatementCreator statementCreator, PreparedStatementCallback callback)
			throws SQLException {
		return queryForObject(connection, statementCreator, callback, new TupleRowMapper(typeHandlerRegistry));
	}

	public <T> List<T> queryForList(Connection connection, String sql, Object[] parameters, RowMapper<T> rowMapper) throws SQLException {
		return queryForList(connection, sql, PreparedStatementCallbackUtils.prepare(parameters, typeHandlerRegistry), rowMapper);
	}

	public <T> List<T> queryForList(Connection connection, String sql, Object[] parameters, JdbcType[] jdbcTypes, RowMapper<T> rowMapper)
			throws SQLException {
		return queryForList(connection, sql, PreparedStatementCallbackUtils.prepare(parameters, jdbcTypes, typeHandlerRegistry), rowMapper);
	}

	public <T> List<T> queryForList(Connection connection, String sql, PreparedStatementCallback callback, RowMapper<T> rowMapper)
			throws SQLException {
		return queryForList(connection,
				PreparedStatementCreatorUtils.forQuery(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY), callback, rowMapper);
	}

	public <T> List<T> queryForList(Connection connection, PreparedStatementCreator statementCreator, PreparedStatementCallback callback,
			RowMapper<T> rowMapper) throws SQLException {
		return query(connection, statementCreator, callback, new RowMapperResultSetExtractor<T>(rowMapper));
	}

	public List<Tuple> queryForList(Connection connection, String sql, Object[] parameters) throws SQLException {
		return queryForList(connection, sql, PreparedStatementCallbackUtils.prepare(parameters, typeHandlerRegistry),
				new TupleRowMapper(typeHandlerRegistry));
	}

	public List<Tuple> queryForList(Connection connection, String sql, Object[] parameters, JdbcType[] jdbcTypes) throws SQLException {
		return queryForList(connection, sql, PreparedStatementCallbackUtils.prepare(parameters, jdbcTypes, typeHandlerRegistry),
				new TupleRowMapper(typeHandlerRegistry));
	}

	public List<Tuple> queryForList(Connection connection, String sql, PreparedStatementCallback callback) throws SQLException {
		return queryForList(connection,
				PreparedStatementCreatorUtils.forQuery(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY), callback,
				new TupleRowMapper(typeHandlerRegistry));
	}

	public List<Tuple> queryForList(Connection connection, PreparedStatementCreator statementCreator, PreparedStatementCallback callback)
			throws SQLException {
		return queryForList(connection, statementCreator, callback, new TupleRowMapper(typeHandlerRegistry));
	}

	public <T> List<T> queryForList(Connection connection, String sql, Object[] parameters, Class<T> objectClass) throws SQLException {
		return queryForList(connection, sql, PreparedStatementCallbackUtils.prepare(parameters, typeHandlerRegistry), objectClass);
	}

	public <T> List<T> queryForList(Connection connection, String sql, Object[] parameters, JdbcType[] jdbcTypes, Class<T> objectClass)
			throws SQLException {
		return queryForList(connection, sql, PreparedStatementCallbackUtils.prepare(parameters, jdbcTypes, typeHandlerRegistry),
				objectClass);
	}

	public <T> List<T> queryForList(Connection connection, String sql, PreparedStatementCallback callback, Class<T> objectClass)
			throws SQLException {
		return queryForList(connection,
				PreparedStatementCreatorUtils.forQuery(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY), callback,
				objectClass);
	}

	public <T> List<T> queryForList(Connection connection, PreparedStatementCreator statementCreator, PreparedStatementCallback callback,
			Class<T> objectClass) throws SQLException {
		return queryForList(connection, statementCreator, callback, new BeanPropertyRowMapper<T>(typeHandlerRegistry, objectClass));
	}

	// ---------------------- Cursor ------------------------

	public Cursor<Tuple> iterator(Connection connection, String sql, Object[] parameters) throws SQLException {
		return iterator(connection, sql, PreparedStatementCallbackUtils.prepare(parameters, typeHandlerRegistry));
	}

	public Cursor<Tuple> iterator(Connection connection, String sql, Object[] parameters, JdbcType[] jdbcTypes) throws SQLException {
		return iterator(connection, sql, PreparedStatementCallbackUtils.prepare(parameters, jdbcTypes, typeHandlerRegistry));
	}

	public Cursor<Tuple> iterator(Connection connection, String sql, PreparedStatementCallback callback) throws SQLException {
		return iterator(connection, PreparedStatementCreatorUtils.forQuery(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY),
				callback, new TupleRowMapper(typeHandlerRegistry));
	}

	public <T> Cursor<T> iterator(Connection connection, String sql, Object[] parameters, Class<T> objectClass) throws SQLException {
		return iterator(connection, sql, PreparedStatementCallbackUtils.prepare(parameters, typeHandlerRegistry),
				new BeanPropertyRowMapper<T>(typeHandlerRegistry, objectClass));
	}

	public <T> Cursor<T> iterator(Connection connection, String sql, Object[] parameters, JdbcType[] jdbcTypes, Class<T> objectClass)
			throws SQLException {
		return iterator(connection, sql, PreparedStatementCallbackUtils.prepare(parameters, jdbcTypes, typeHandlerRegistry),
				new BeanPropertyRowMapper<T>(typeHandlerRegistry, objectClass));
	}

	public <T> Cursor<T> iterator(Connection connection, String sql, PreparedStatementCallback callback, Class<T> objectClass)
			throws SQLException {
		return iterator(connection, sql, callback, new BeanPropertyRowMapper<T>(typeHandlerRegistry, objectClass));
	}

	public <T> Cursor<T> iterator(Connection connection, String sql, Object[] parameters, RowMapper<T> rowMapper) throws SQLException {
		return iterator(connection, sql, PreparedStatementCallbackUtils.prepare(parameters, typeHandlerRegistry), rowMapper);
	}

	public <T> Cursor<T> iterator(Connection connection, String sql, Object[] parameters, JdbcType[] jdbcTypes, RowMapper<T> rowMapper)
			throws SQLException {
		return iterator(connection, sql, PreparedStatementCallbackUtils.prepare(parameters, jdbcTypes, typeHandlerRegistry), rowMapper);
	}

	public <T> Cursor<T> iterator(Connection connection, String sql, PreparedStatementCallback callback, RowMapper<T> rowMapper)
			throws SQLException {
		return iterator(connection, PreparedStatementCreatorUtils.forQuery(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY),
				callback, rowMapper);
	}

	public <T> Cursor<T> iterator(Connection connection, PreparedStatementCreator statementCreator,
			final PreparedStatementCallback callback, final RowMapper<T> rowMapper) throws SQLException {
		final Observable closeable = Observable.unrepeatable();
		final AtomicBoolean success = new AtomicBoolean(true);
		try {
			return execute(connection, statementCreator, new PreparedStatementExecutor<Cursor<T>>() {

				public Cursor<T> execute(PreparedStatement ps) throws SQLException {
					if (callback != null) {
						callback.setValues(ps);
					}
					ResultSet rs = null;
					try {
						rs = ps.executeQuery();
						ResultSetExtractor<Cursor<T>> extractor = new CursorResultSetExtractor<T>(rowMapper, closeable);
						return extractor.extractData(rs);
					} catch (SQLException e) {
						success.set(false);
						throw e;
					} finally {
						if (success.get()) {
							final ResultSet ref = rs;
							closeable.addObserver(new Observer() {
								public void update(Observable o, Object arg) {
									JdbcUtils.closeQuietly(ref);
								}
							});
						} else {
							JdbcUtils.closeQuietly(rs);
						}
					}
				}

				public void close(final PreparedStatement ps) {
					if (success.get()) {
						closeable.addObserver(new Observer() {
							public void update(Observable o, Object arg) {
								JdbcUtils.closeQuietly(ps);
							}
						});
					} else {
						JdbcUtils.closeQuietly(ps);
					}
				}

			});
		} finally {
			if (success.get()) {
				closeable.addObserver(new Observer() {
					public void update(Observable o, Object arg) {
						JdbcUtils.closeQuietly(connection);
					}
				});
			} else {
				JdbcUtils.closeQuietly(connection);
			}
		}
	}

	public Cursor<Tuple> cachedIterator(Connection connection, String sql, Object[] parameters) throws SQLException {
		return cachedIterator(connection, sql, PreparedStatementCallbackUtils.prepare(parameters, typeHandlerRegistry));
	}

	public Cursor<Tuple> cachedIterator(Connection connection, String sql, Object[] parameters, JdbcType[] jdbcTypes) throws SQLException {
		return cachedIterator(connection, sql, PreparedStatementCallbackUtils.prepare(parameters, jdbcTypes, typeHandlerRegistry));
	}

	public Cursor<Tuple> cachedIterator(Connection connection, String sql, PreparedStatementCallback callback) throws SQLException {
		return cachedIterator(connection, sql, callback, new TupleRowMapper(typeHandlerRegistry));
	}

	public <T> Cursor<T> cachedIterator(Connection connection, String sql, Object[] parameters, RowMapper<T> rowMapper)
			throws SQLException {
		return cachedIterator(connection, sql, PreparedStatementCallbackUtils.prepare(parameters, typeHandlerRegistry), rowMapper);
	}

	public <T> Cursor<T> cachedIterator(Connection connection, String sql, Object[] parameters, JdbcType[] jdbcTypes,
			RowMapper<T> rowMapper) throws SQLException {
		return cachedIterator(connection, sql, PreparedStatementCallbackUtils.prepare(parameters, jdbcTypes, typeHandlerRegistry),
				rowMapper);
	}

	public <T> Cursor<T> cachedIterator(Connection connection, String sql, final PreparedStatementCallback callback,
			final RowMapper<T> rowMapper) throws SQLException {
		return cachedIterator(connection,
				PreparedStatementCreatorUtils.forQuery(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY), callback, rowMapper);
	}

	public <T> Cursor<T> cachedIterator(Connection connection, PreparedStatementCreator statementCreator,
			final PreparedStatementCallback callback, final RowMapper<T> rowMapper) throws SQLException {
		try {
			return execute(connection, statementCreator, new PreparedStatementExecutor<Cursor<T>>() {

				public Cursor<T> execute(PreparedStatement ps) throws SQLException {
					if (callback != null) {
						callback.setValues(ps);
					}
					ResultSet rs = null;
					try {
						rs = ps.executeQuery();
						ResultSetExtractor<Cursor<T>> extractor = new CachedCursorResultSetExtractor<T>(rowMapper);
						return extractor.extractData(rs);
					} finally {
						JdbcUtils.closeQuietly(rs);
					}
				}

				public void close(PreparedStatement ps) {
					JdbcUtils.closeQuietly(ps);
				}

			});
		} finally {
			JdbcUtils.closeQuietly(connection);
		}
	}

	// ------------------------- Pageable Query -------------------

	public <T> PageableQuery<T> queryForPage(Connection connection, String sql, Object[] parameters, RowMapper<T> rowMapper) {
		return new PageableQueryImpl<T>(connection, new DefaultPageableSql(sql),
				PreparedStatementCallbackUtils.prepare(parameters, typeHandlerRegistry), rowMapper, this);
	}

	public <T> PageableQuery<T> queryForPage(Connection connection, String sql, Object[] parameters, JdbcType[] jdbcTypes,
			RowMapper<T> rowMapper) {
		return new PageableQueryImpl<T>(connection, new DefaultPageableSql(sql),
				PreparedStatementCallbackUtils.prepare(parameters, jdbcTypes, typeHandlerRegistry), rowMapper, this);
	}

	public <T> PageableQuery<T> queryForPage(Connection connection, String sql, PreparedStatementCallback callback,
			RowMapper<T> rowMapper) {
		return new PageableQueryImpl<T>(connection, new DefaultPageableSql(sql), callback, rowMapper, this);
	}

	public <T> PageableQuery<T> queryForPage(Connection connection, PageableSql pageableSql, PreparedStatementCallback callback,
			RowMapper<T> rowMapper) {
		return new PageableQueryImpl<T>(connection, pageableSql, callback, rowMapper, this);
	}

	public PageableQuery<Tuple> queryForPage(Connection connection, String sql, Object[] parameters, JdbcType[] jdbcTypes) {
		return queryForPage(connection, sql, PreparedStatementCallbackUtils.prepare(parameters, jdbcTypes, typeHandlerRegistry));
	}

	public PageableQuery<Tuple> queryForPage(Connection connection, String sql, Object[] parameters) {
		return queryForPage(connection, sql, PreparedStatementCallbackUtils.prepare(parameters, typeHandlerRegistry));
	}

	public PageableQuery<Tuple> queryForPage(Connection connection, String sql, PreparedStatementCallback callback) {
		return queryForPage(connection, new DefaultPageableSql(sql), callback);
	}

	public PageableQuery<Tuple> queryForPage(Connection connection, PageableSql pageableSql, PreparedStatementCallback callback) {
		return queryForPage(connection, pageableSql, callback, new TupleRowMapper(typeHandlerRegistry));
	}

	// ------------------------- Update ---------------------------

	public int update(Connection connection, String sql, Object[] parameters, GeneratedKey generatedKey) throws SQLException {
		return update(connection, sql, PreparedStatementCallbackUtils.prepare(parameters, typeHandlerRegistry), generatedKey);
	}

	public int update(Connection connection, String sql, Object[] parameters, JdbcType[] jdbcTypes, GeneratedKey generatedKey)
			throws SQLException {
		return update(connection, sql, PreparedStatementCallbackUtils.prepare(parameters, jdbcTypes, typeHandlerRegistry), generatedKey);
	}

	public int update(Connection connection, String sql, PreparedStatementCallback callback, GeneratedKey generatedKey)
			throws SQLException {
		return update(connection, PreparedStatementCreatorUtils.forColumnNames(sql, generatedKey.getKeyNames()), callback, generatedKey);
	}

	public int update(Connection connection, PreparedStatementCreator statementCreator, final PreparedStatementCallback callback,
			final GeneratedKey generatedKey) throws SQLException {
		return execute(connection, statementCreator, new PreparedStatementExecutor<Integer>() {
			@SuppressWarnings("unchecked")
			public Integer execute(PreparedStatement ps) throws SQLException {
				if (callback != null) {
					callback.setValues(ps);
				}
				int effected = ps.executeUpdate();
				if (effected > 0) {
					ResultSet rs = ps.getGeneratedKeys();
					if (rs != null) {
						try {
							ResultSetExtractor<Tuple> extractor = new FirstRowResultSetExtractor<Tuple>(
									new TupleRowMapper(typeHandlerRegistry));
							Tuple keys = extractor.extractData(rs);
							if (keys != null) {
								generatedKey.setValues((Map<String, Object>) keys);
							}
						} finally {
							JdbcUtils.closeQuietly(rs);
						}
					}
				}
				return effected;
			}

			public void close(PreparedStatement ps) {
				JdbcUtils.closeQuietly(ps);
			}
		});
	}

	public int update(Connection connection, String sql, Object[] parameters) throws SQLException {
		return update(connection, sql, PreparedStatementCallbackUtils.prepare(parameters, typeHandlerRegistry));
	}

	public int update(Connection connection, String sql, Object[] parameters, JdbcType[] jdbcTypes) throws SQLException {
		return update(connection, sql, PreparedStatementCallbackUtils.prepare(parameters, jdbcTypes, typeHandlerRegistry));
	}

	public int update(Connection connection, String sql, PreparedStatementCallback callback) throws SQLException {
		return update(connection, PreparedStatementCreatorUtils.forDefault(sql), callback);
	}

	public int update(Connection connection, PreparedStatementCreator statementCreator, final PreparedStatementCallback callback)
			throws SQLException {
		return execute(connection, statementCreator, new PreparedStatementExecutor<Integer>() {
			public Integer execute(PreparedStatement ps) throws SQLException {
				if (callback != null) {
					callback.setValues(ps);
				}
				return ps.executeUpdate();
			}

			public void close(PreparedStatement ps) {
				JdbcUtils.closeQuietly(ps);
			}
		});
	}

	// ---------------------- Batch Update------------------------------

	public int[] batch(Connection connection, String sql, List<Object[]> parameters, JdbcType[] jdbcTypes) throws SQLException {
		return batch(connection, sql, PreparedStatementCallbackUtils.batchPrepare(parameters, jdbcTypes, typeHandlerRegistry));
	}

	public int[] batch(Connection connection, String sql, List<Object[]> parameters) throws SQLException {
		return batch(connection, sql, PreparedStatementCallbackUtils.batchPrepare(parameters, typeHandlerRegistry));
	}

	public int[] batch(Connection connection, String sql, PreparedStatementCallback callback) throws SQLException {
		return batch(connection, PreparedStatementCreatorUtils.forDefault(sql), callback);
	}

	public int[] batch(Connection connection, PreparedStatementCreator statementCreator, final PreparedStatementCallback callback)
			throws SQLException {
		return execute(connection, statementCreator, new PreparedStatementExecutor<int[]>() {
			public int[] execute(PreparedStatement ps) throws SQLException {
				if (callback != null) {
					callback.setValues(ps);
				}
				return ps.executeBatch();
			}

			public void close(PreparedStatement ps) {
				JdbcUtils.closeQuietly(ps);
			}
		});
	}

	public <T> T execute(Connection connection, PreparedStatementCreator statementCreator, PreparedStatementExecutor<T> statementExecutor)
			throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = statementCreator.createPreparedStatement(connection);
			return statementExecutor.execute(ps);
		} finally {
			statementExecutor.close(ps);
		}
	}

	protected static boolean useCachedRowSet = true;

	public static void setUseCachedRowSet(boolean useCachedRowSet) {
		SqlRunner.useCachedRowSet = useCachedRowSet;
	}

	/**
	 * 
	 * PageableQueryImpl
	 *
	 * @author Fred Feng
	 * @version 1.0
	 */
	private static class PageableQueryImpl<T> implements PageableQuery<T> {

		private final Connection connection;
		private final PageableSql pageableSql;
		private final PreparedStatementCallback callback;
		private final RowMapper<T> rowMapper;
		private final SqlRunner sqlRunner;

		private PageableQueryImpl(Connection connection, PageableSql pageableSql, PreparedStatementCallback callback,
				RowMapper<T> rowMapper, SqlRunner sqlRunner) {
			this.connection = connection;
			this.pageableSql = pageableSql;
			this.callback = callback;
			this.rowMapper = rowMapper;
			this.sqlRunner = sqlRunner;
		}

		@Override
		public int totalCount() {
			final String sql = pageableSql.countableSql();
			try {
				return sqlRunner.queryForObject(connection, sql, callback, Integer.class);
			} catch (SQLException e) {
				throw new PageableException(e.getMessage(), e);
			}
		}

		@Override
		public Cursor<T> iterator(int maxResults, int firstResult) {
			final String sql = pageableSql.pageableSql(maxResults, firstResult);
			try {
				if (useCachedRowSet) {
					return sqlRunner.cachedIterator(connection, sql, callback, rowMapper);
				}
				return sqlRunner.iterator(connection, sql, callback, rowMapper);
			} catch (SQLException e) {
				throw new PageableException(e.getMessage(), e);
			}
		}

	}

}
