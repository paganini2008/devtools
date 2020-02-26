package com.github.paganini2008.devtools.db4j;

import java.sql.SQLException;
import java.util.List;

import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.db4j.mapper.RowMapper;
import com.github.paganini2008.devtools.jdbc.Cursor;

/**
 * 
 * JdbcOperations
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface JdbcOperations {

	default int update(String sql, Object[] args) throws SQLException {
		return update(sql, new ArraySqlParameter(args));
	}

	int update(String sql, SqlParameter sqlParameter) throws SQLException;

	default int update(String sql, Object[] args, GeneratedKey generatedKey) throws SQLException {
		return update(sql, new ArraySqlParameter(args), generatedKey);
	}

	int update(String sql, SqlParameter sqlParameter, GeneratedKey generatedKey) throws SQLException;

	default int[] batchUpdate(String sql, List<Object[]> argsList) throws SQLException {
		return batchUpdate(sql, new ArraySqlParameters(argsList));
	}

	int[] batchUpdate(String sql, SqlParameters sqlParameters) throws SQLException;

	default <T> T query(String sql, Object[] args, ResultSetExtractor<T> extractor) throws SQLException {
		return query(sql, new ArraySqlParameter(args), extractor);
	}

	<T> T query(String sql, SqlParameter sqlParameter, ResultSetExtractor<T> extractor) throws SQLException;

	default List<Tuple> queryForList(String sql, Object[] args) throws SQLException {
		return queryForList(sql, new ArraySqlParameter(args));
	}

	List<Tuple> queryForList(String sql, SqlParameter sqlParameter) throws SQLException;

	default <T> T queryForObject(String sql, Object[] args, RowMapper<T> rowMapper) throws SQLException {
		return queryForObject(sql, new ArraySqlParameter(args), rowMapper);
	}

	<T> T queryForObject(String sql, SqlParameter sqlParameter, RowMapper<T> rowMapper) throws SQLException;

	default <T> List<T> queryForList(String sql, Object[] args, RowMapper<T> rowMapper) throws SQLException {
		return queryForList(sql, new ArraySqlParameter(args), rowMapper);
	}

	<T> List<T> queryForList(String sql, SqlParameter sqlParameter, RowMapper<T> rowMapper) throws SQLException;

	default <T> Cursor<T> queryForCursor(String sql, Object[] args, RowMapper<T> rowMapper) throws SQLException {
		return queryForCursor(sql, new ArraySqlParameter(args), rowMapper);
	}

	<T> Cursor<T> queryForCursor(String sql, SqlParameter sqlParameter, RowMapper<T> rowMapper) throws SQLException;

	default <T> Cursor<T> queryForCachedCursor(String sql, Object[] args, RowMapper<T> rowMapper) throws SQLException {
		return queryForCachedCursor(sql, new ArraySqlParameter(args), rowMapper);
	}

	<T> Cursor<T> queryForCachedCursor(String sql, SqlParameter sqlParameter, RowMapper<T> rowMapper) throws SQLException;

	<T> T customize(Customizable<T> customizable) throws SQLException;

}
