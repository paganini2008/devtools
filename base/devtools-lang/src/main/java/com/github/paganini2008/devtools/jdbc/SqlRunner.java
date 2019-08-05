package com.github.paganini2008.devtools.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.github.paganini2008.devtools.Observable;
import com.github.paganini2008.devtools.Observer;
import com.github.paganini2008.devtools.converter.TypeConverter;
import com.github.paganini2008.devtools.jdbc.mapper.ColumnIndexRowMapper;
import com.github.paganini2008.devtools.jdbc.mapper.MapRowMapper;
import com.github.paganini2008.devtools.jdbc.mapper.ObjectRowMapper;
import com.github.paganini2008.devtools.jdbc.mapper.RowMapper;

/**
 * A template of jdbc operations
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class SqlRunner {

	private TypeConverter typeConverter;
	private TypeHandlerRegistry typeHandlerRegistry;

	public void setTypeConverter(TypeConverter typeConverter) {
		this.typeConverter = typeConverter;
	}

	public void setTypeHandlerRegistry(TypeHandlerRegistry typeHandlerRegistry) {
		this.typeHandlerRegistry = typeHandlerRegistry;
	}

	public TypeConverter getTypeConverter() {
		return typeConverter;
	}

	public TypeHandlerRegistry getTypeHandlerRegistry() {
		return typeHandlerRegistry;
	}

	// --------------------- Query ------------------------------
	public <T> T query(Connection connection, String sql, Object[] parameters, ResultSetExtractor<T> extractor) throws SQLException {
		return query(connection, sql, PreparedStatementSetterUtils.prepare(parameters, typeHandlerRegistry, typeConverter), extractor);
	}

	public <T> T query(Connection connection, String sql, Object[] parameters, JdbcType[] jdbcTypes, ResultSetExtractor<T> extractor)
			throws SQLException {
		return query(connection, sql, PreparedStatementSetterUtils.prepare(parameters, jdbcTypes, typeHandlerRegistry, typeConverter),
				extractor);
	}

	public <T> T query(Connection connection, String sql, PreparedStatementSetter statementSetter, ResultSetExtractor<T> extractor)
			throws SQLException {
		return query(connection, PreparedStatementCreatorUtils.create(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY),
				statementSetter, extractor);
	}

	public <T> T query(Connection connection, PreparedStatementCreator statementCreator, final PreparedStatementSetter statementSetter,
			final ResultSetExtractor<T> extractor) throws SQLException {
		return execute(connection, statementCreator, new PreparedStatementAction<T>() {
			public T execute(PreparedStatement ps) throws SQLException {
				if (statementSetter != null) {
					statementSetter.setValues(ps);
				}
				ResultSet rs = null;
				try {
					rs = ps.executeQuery();
					return extractor.extractData(rs);
				} finally {
					DBUtils.closeQuietly(rs);
				}
			}

			public void close(PreparedStatement ps) {
				DBUtils.closeQuietly(ps);
			}
		});
	}

	public <T> T queryForObject(Connection connection, String sql, Object[] parameters, Class<T> requiredType) throws SQLException {
		return queryForObject(connection, sql, PreparedStatementSetterUtils.prepare(parameters, typeHandlerRegistry, typeConverter),
				requiredType);
	}

	public <T> T queryForObject(Connection connection, String sql, Object[] parameters, JdbcType[] jdbcTypes, Class<T> requiredType)
			throws SQLException {
		return queryForObject(connection, sql,
				PreparedStatementSetterUtils.prepare(parameters, jdbcTypes, typeHandlerRegistry, typeConverter), requiredType);
	}

	public <T> T queryForObject(Connection connection, String sql, PreparedStatementSetter statementSetter, Class<T> requiredType)
			throws SQLException {
		return queryForObject(connection, PreparedStatementCreatorUtils.create(sql), statementSetter, requiredType);
	}

	public <T> T queryForObject(Connection connection, PreparedStatementCreator statementCreator, PreparedStatementSetter statementSetter,
			Class<T> requiredType) throws SQLException {
		return queryForObject(connection, statementCreator, statementSetter,
				new ColumnIndexRowMapper<T>(typeHandlerRegistry, requiredType, typeConverter));
	}

	public <T> T queryForObject(Connection connection, String sql, Object[] parameters, RowMapper<T> rowMapper) throws SQLException {
		return queryForObject(connection, sql, PreparedStatementSetterUtils.prepare(parameters, typeHandlerRegistry, typeConverter),
				rowMapper);
	}

	public <T> T queryForObject(Connection connection, String sql, Object[] parameters, JdbcType[] jdbcTypes, RowMapper<T> rowMapper)
			throws SQLException {
		return queryForObject(connection, sql,
				PreparedStatementSetterUtils.prepare(parameters, jdbcTypes, typeHandlerRegistry, typeConverter), rowMapper);
	}

	public <T> T queryForObject(Connection connection, String sql, PreparedStatementSetter statementSetter, RowMapper<T> rowMapper)
			throws SQLException {
		return queryForObject(connection, PreparedStatementCreatorUtils.create(sql), statementSetter, rowMapper);
	}

	public <T> T queryForObject(Connection connection, PreparedStatementCreator statementCreator, PreparedStatementSetter statementSetter,
			RowMapper<T> rowMapper) throws SQLException {
		return query(connection, statementCreator, statementSetter, new SingleRowResultSetExtractor<T>(rowMapper));
	}

	public Map<String, Object> queryForMap(Connection connection, String sql, Object[] parameters) throws SQLException {
		return queryForMap(connection, sql, PreparedStatementSetterUtils.prepare(parameters, typeHandlerRegistry, typeConverter));
	}

	public Map<String, Object> queryForMap(Connection connection, String sql, Object[] parameters, JdbcType[] jdbcTypes)
			throws SQLException {
		return queryForMap(connection, sql,
				PreparedStatementSetterUtils.prepare(parameters, jdbcTypes, typeHandlerRegistry, typeConverter));
	}

	public Map<String, Object> queryForMap(Connection connection, String sql, PreparedStatementSetter statementSetter) throws SQLException {
		return queryForMap(connection, PreparedStatementCreatorUtils.create(sql), statementSetter);
	}

	public Map<String, Object> queryForMap(Connection connection, PreparedStatementCreator statementCreator,
			PreparedStatementSetter statementSetter) throws SQLException {
		return queryForObject(connection, statementCreator, statementSetter, new MapRowMapper(typeHandlerRegistry));
	}

	public <T> List<T> queryForList(Connection connection, String sql, Object[] parameters, RowMapper<T> rowMapper) throws SQLException {
		return queryForList(connection, sql, PreparedStatementSetterUtils.prepare(parameters, typeHandlerRegistry, typeConverter),
				rowMapper);
	}

	public <T> List<T> queryForList(Connection connection, String sql, Object[] parameters, JdbcType[] jdbcTypes, RowMapper<T> rowMapper)
			throws SQLException {
		return queryForList(connection, sql,
				PreparedStatementSetterUtils.prepare(parameters, jdbcTypes, typeHandlerRegistry, typeConverter), rowMapper);
	}

	public <T> List<T> queryForList(Connection connection, String sql, PreparedStatementSetter statementSetter, RowMapper<T> rowMapper)
			throws SQLException {
		return queryForList(connection, PreparedStatementCreatorUtils.create(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY),
				statementSetter, rowMapper);
	}

	public <T> List<T> queryForList(Connection connection, PreparedStatementCreator statementCreator,
			PreparedStatementSetter statementSetter, RowMapper<T> rowMapper) throws SQLException {
		return query(connection, statementCreator, statementSetter, new RowMapperResultSetExtractor<T>(rowMapper));
	}

	public List<Map<String, Object>> queryForList(Connection connection, String sql, Object[] parameters) throws SQLException {
		return queryForList(connection, sql, PreparedStatementSetterUtils.prepare(parameters, typeHandlerRegistry, typeConverter),
				new MapRowMapper(typeHandlerRegistry));
	}

	public List<Map<String, Object>> queryForList(Connection connection, String sql, Object[] parameters, JdbcType[] jdbcTypes)
			throws SQLException {
		return queryForList(connection, sql,
				PreparedStatementSetterUtils.prepare(parameters, jdbcTypes, typeHandlerRegistry, typeConverter),
				new MapRowMapper(typeHandlerRegistry));
	}

	public List<Map<String, Object>> queryForList(Connection connection, String sql, PreparedStatementSetter statementSetter)
			throws SQLException {
		return queryForList(connection, PreparedStatementCreatorUtils.create(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY),
				statementSetter, new MapRowMapper(typeHandlerRegistry));
	}

	public List<Map<String, Object>> queryForList(Connection connection, PreparedStatementCreator statementCreator,
			PreparedStatementSetter statementSetter) throws SQLException {
		return queryForList(connection, statementCreator, statementSetter, new MapRowMapper(typeHandlerRegistry));
	}

	public <T> List<T> queryForList(Connection connection, String sql, Object[] parameters, Class<T> objectClass) throws SQLException {
		return queryForList(connection, sql, PreparedStatementSetterUtils.prepare(parameters, typeHandlerRegistry, typeConverter),
				objectClass);
	}

	public <T> List<T> queryForList(Connection connection, String sql, Object[] parameters, JdbcType[] jdbcTypes, Class<T> objectClass)
			throws SQLException {
		return queryForList(connection, sql,
				PreparedStatementSetterUtils.prepare(parameters, jdbcTypes, typeHandlerRegistry, typeConverter), objectClass);
	}

	public <T> List<T> queryForList(Connection connection, String sql, PreparedStatementSetter statementSetter, Class<T> objectClass)
			throws SQLException {
		return queryForList(connection, PreparedStatementCreatorUtils.create(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY),
				statementSetter, objectClass);
	}

	public <T> List<T> queryForList(Connection connection, PreparedStatementCreator statementCreator,
			PreparedStatementSetter statementSetter, Class<T> objectClass) throws SQLException {
		return queryForList(connection, statementCreator, statementSetter,
				new ObjectRowMapper<T>(typeHandlerRegistry, objectClass, typeConverter));
	}

	// ------------------------------------- iterator
	// -----------------------------------------

	/**
	 * Closeable
	 * 
	 * @author Fred Feng
	 * @version 1.0
	 */
	static class LazyCloseable extends Observable {

		LazyCloseable() {
			super(false);
		}

		public void finish() {
			super.notifyObservers();
		}

	}

	public Iterator<Map<String, Object>> iterator(Connection connection, String sql, Object[] parameters) throws SQLException {
		return iterator(connection, sql, PreparedStatementSetterUtils.prepare(parameters, typeHandlerRegistry, typeConverter));
	}

	public Iterator<Map<String, Object>> iterator(Connection connection, String sql, Object[] parameters, JdbcType[] jdbcTypes)
			throws SQLException {
		return iterator(connection, sql, PreparedStatementSetterUtils.prepare(parameters, jdbcTypes, typeHandlerRegistry, typeConverter));
	}

	public Iterator<Map<String, Object>> iterator(Connection connection, String sql, PreparedStatementSetter statementSetter)
			throws SQLException {
		return iterator(connection, PreparedStatementCreatorUtils.create(sql), statementSetter, new MapRowMapper(typeHandlerRegistry));
	}

	public <T> Iterator<T> iterator(Connection connection, String sql, Object[] parameters, Class<T> objectClass) throws SQLException {
		return iterator(connection, sql, PreparedStatementSetterUtils.prepare(parameters, typeHandlerRegistry, typeConverter),
				new ObjectRowMapper<T>(typeHandlerRegistry, objectClass, typeConverter));
	}

	public <T> Iterator<T> iterator(Connection connection, String sql, Object[] parameters, JdbcType[] jdbcTypes, Class<T> objectClass)
			throws SQLException {
		return iterator(connection, sql, PreparedStatementSetterUtils.prepare(parameters, jdbcTypes, typeHandlerRegistry, typeConverter),
				new ObjectRowMapper<T>(typeHandlerRegistry, objectClass, typeConverter));
	}

	public <T> Iterator<T> iterator(Connection connection, String sql, PreparedStatementSetter statementSetter, Class<T> objectClass)
			throws SQLException {
		return iterator(connection, sql, statementSetter, new ObjectRowMapper<T>(typeHandlerRegistry, objectClass, typeConverter));
	}

	public <T> Iterator<T> iterator(Connection connection, String sql, Object[] parameters, RowMapper<T> rowMapper) throws SQLException {
		return iterator(connection, sql, PreparedStatementSetterUtils.prepare(parameters, typeHandlerRegistry, typeConverter), rowMapper);
	}

	public <T> Iterator<T> iterator(Connection connection, String sql, Object[] parameters, JdbcType[] jdbcTypes, RowMapper<T> rowMapper)
			throws SQLException {
		return iterator(connection, sql, PreparedStatementSetterUtils.prepare(parameters, jdbcTypes, typeHandlerRegistry, typeConverter),
				rowMapper);
	}

	public <T> Iterator<T> iterator(Connection connection, String sql, PreparedStatementSetter statementSetter, RowMapper<T> rowMapper)
			throws SQLException {
		return iterator(connection, PreparedStatementCreatorUtils.create(sql), statementSetter, rowMapper);
	}

	public <T> Iterator<T> iterator(Connection connection, PreparedStatementCreator statementCreator,
			final PreparedStatementSetter statementSetter, final RowMapper<T> rowMapper) throws SQLException {
		final LazyCloseable closeable = new LazyCloseable();
		return execute(connection, statementCreator, new PreparedStatementAction<Iterator<T>>() {

			public Iterator<T> execute(PreparedStatement ps) throws SQLException {
				if (statementSetter != null) {
					statementSetter.setValues(ps);
				}
				ResultSet rs = null;
				try {
					rs = ps.executeQuery();
					ResultSetExtractor<Iterator<T>> extractor = new IteratorResultSetExtractor<T>(rowMapper, closeable);
					Iterator<T> result = extractor.extractData(rs);
					if (result != null) {
						final ResultSet delegate = rs;
						closeable.addObserver(new Observer() {
							public void update(Observable o, Object arg) {
								DBUtils.closeQuietly(delegate);
							}
						});
					}
					return result;
				} catch (SQLException e) {
					closeable.finish();
					throw e;
				}
			}

			public void close(final PreparedStatement ps) {
				closeable.addObserver(new Observer() {
					public void update(Observable o, Object arg) {
						DBUtils.closeQuietly(ps);
					}
				});
			}

		});
	}

	public Iterator<Map<String, Object>> detachedIterator(Connection connection, String sql, Object[] parameters) throws SQLException {
		return detachedIterator(connection, sql, PreparedStatementSetterUtils.prepare(parameters, typeHandlerRegistry, typeConverter));
	}

	public Iterator<Map<String, Object>> detachedIterator(Connection connection, String sql, Object[] parameters, JdbcType[] jdbcTypes)
			throws SQLException {
		return detachedIterator(connection, sql,
				PreparedStatementSetterUtils.prepare(parameters, jdbcTypes, typeHandlerRegistry, typeConverter));
	}

	public Iterator<Map<String, Object>> detachedIterator(Connection connection, String sql, PreparedStatementSetter statementSetter)
			throws SQLException {
		return detachedIterator(connection, PreparedStatementCreatorUtils.create(sql), statementSetter,
				new MapRowMapper(typeHandlerRegistry));
	}

	public <T> Iterator<T> detachedIterator(Connection connection, String sql, Object[] parameters, RowMapper<T> rowMapper)
			throws SQLException {
		return detachedIterator(connection, PreparedStatementCreatorUtils.create(sql),
				PreparedStatementSetterUtils.prepare(parameters, typeHandlerRegistry, typeConverter), rowMapper);
	}

	public <T> Iterator<T> detachedIterator(Connection connection, String sql, Object[] parameters, JdbcType[] jdbcTypes,
			RowMapper<T> rowMapper) throws SQLException {
		return detachedIterator(connection, PreparedStatementCreatorUtils.create(sql),
				PreparedStatementSetterUtils.prepare(parameters, jdbcTypes, typeHandlerRegistry, typeConverter), rowMapper);
	}

	public <T> Iterator<T> detachedIterator(Connection connection, PreparedStatementCreator statementCreator,
			final PreparedStatementSetter statementSetter, final RowMapper<T> rowMapper) throws SQLException {
		return execute(connection, statementCreator, new PreparedStatementAction<Iterator<T>>() {

			public Iterator<T> execute(PreparedStatement ps) throws SQLException {
				if (statementSetter != null) {
					statementSetter.setValues(ps);
				}
				ResultSet rs = null;
				try {
					rs = ps.executeQuery();
					ResultSetExtractor<Iterator<T>> extractor = new DetachedIteratorResultSetExtractor<T>(rowMapper);
					return extractor.extractData(rs);
				} finally {
					DBUtils.closeQuietly(rs);
				}
			}

			public void close(PreparedStatement ps) {
				DBUtils.closeQuietly(ps);
			}

		});
	}

	// ------------------------- Update ---------------------------

	public int update(Connection connection, String sql, Object[] parameters, KeyHolder keyHolder) throws SQLException {
		return update(connection, sql, PreparedStatementSetterUtils.prepare(parameters, typeHandlerRegistry, typeConverter), keyHolder);
	}

	public int update(Connection connection, String sql, Object[] parameters, JdbcType[] jdbcTypes, KeyHolder keyHolder)
			throws SQLException {
		return update(connection, sql, PreparedStatementSetterUtils.prepare(parameters, jdbcTypes, typeHandlerRegistry, typeConverter),
				keyHolder);
	}

	public int update(Connection connection, String sql, PreparedStatementSetter statementSetter, KeyHolder keyHolder) throws SQLException {
		return update(connection, PreparedStatementCreatorUtils.create(sql, keyHolder.getKeyNames()), statementSetter, keyHolder);
	}

	public int update(Connection connection, PreparedStatementCreator statementCreator, final PreparedStatementSetter statementSetter,
			final KeyHolder keyHolder) throws SQLException {
		return execute(connection, statementCreator, new PreparedStatementAction<Integer>() {
			public Integer execute(PreparedStatement ps) throws SQLException {
				if (statementSetter != null) {
					statementSetter.setValues(ps);
				}
				int effected = ps.executeUpdate();
				if (effected > 0) {
					ResultSet rs = ps.getGeneratedKeys();
					if (rs != null) {
						try {
							ResultSetExtractor<Map<String, Object>> extractor = new SingleRowResultSetExtractor<Map<String, Object>>(
									new MapRowMapper(typeHandlerRegistry));
							Map<String, Object> keys = extractor.extractData(rs);
							if (keys != null) {
								keyHolder.load(keys);
							}
						} finally {
							DBUtils.closeQuietly(rs);
						}
					}
				}
				return effected;
			}

			public void close(PreparedStatement ps) {
				DBUtils.closeQuietly(ps);
			}
		});
	}

	public int update(Connection connection, String sql, Object[] parameters) throws SQLException {
		return update(connection, sql, PreparedStatementSetterUtils.prepare(parameters, typeHandlerRegistry, typeConverter));
	}

	public int update(Connection connection, String sql, Object[] parameters, JdbcType[] jdbcTypes) throws SQLException {
		return update(connection, sql, PreparedStatementSetterUtils.prepare(parameters, jdbcTypes, typeHandlerRegistry, typeConverter));
	}

	public int update(Connection connection, String sql, PreparedStatementSetter statementSetter) throws SQLException {
		return update(connection, PreparedStatementCreatorUtils.create(sql), statementSetter);
	}

	public int update(Connection connection, PreparedStatementCreator statementCreator, final PreparedStatementSetter statementSetter)
			throws SQLException {
		return execute(connection, statementCreator, new PreparedStatementAction<Integer>() {
			public Integer execute(PreparedStatement ps) throws SQLException {
				if (statementSetter != null) {
					statementSetter.setValues(ps);
				}
				return ps.executeUpdate();
			}

			public void close(PreparedStatement ps) {
				DBUtils.closeQuietly(ps);
			}
		});
	}

	// ---------------------- Batch Update------------------------------

	public int[] batch(Connection connection, String sql, List<Object[]> parameters, JdbcType[] jdbcTypes) throws SQLException {
		return batch(connection, sql, PreparedStatementSetterUtils.batchPrepare(parameters, jdbcTypes, typeHandlerRegistry, typeConverter));
	}

	public int[] batch(Connection connection, String sql, List<Object[]> parameters) throws SQLException {
		return batch(connection, sql, PreparedStatementSetterUtils.batchPrepare(parameters, typeHandlerRegistry, typeConverter));
	}

	public int[] batch(Connection connection, String sql, PreparedStatementSetter statementSetter) throws SQLException {
		return batch(connection, PreparedStatementCreatorUtils.create(sql), statementSetter);
	}

	public int[] batch(Connection connection, PreparedStatementCreator statementCreator, final PreparedStatementSetter statementSetter)
			throws SQLException {
		return execute(connection, statementCreator, new PreparedStatementAction<int[]>() {
			public int[] execute(PreparedStatement ps) throws SQLException {
				if (statementSetter != null) {
					statementSetter.setValues(ps);
				}
				return ps.executeBatch();
			}

			public void close(PreparedStatement ps) {
				DBUtils.closeQuietly(ps);
			}
		});
	}

	public <T> T execute(Connection connection, PreparedStatementCreator statementCreator, PreparedStatementAction<T> statementAction)
			throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = statementCreator.createPreparedStatement(connection);
			return statementAction.execute(ps);
		} finally {
			statementAction.close(ps);
		}
	}

}
