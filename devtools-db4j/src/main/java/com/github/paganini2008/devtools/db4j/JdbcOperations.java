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

	int update(String sql, SqlParameter sqlParameter) throws SQLException;

	int update(String sql, SqlParameter sqlParameter, GeneratedKey generatedKey) throws SQLException;

	int[] batchUpdate(String sql, SqlParameters sqlParameters) throws SQLException;

	<T> T query(String sql, SqlParameter sqlParameter, ResultSetExtractor<T> extractor) throws SQLException;

	List<Tuple> queryForList(String sql, SqlParameter sqlParameter) throws SQLException;

	<T> T queryForObject(String sql, SqlParameter sqlParameter, RowMapper<T> rowMapper) throws SQLException;

	<T> List<T> queryForList(String sql, SqlParameter sqlParameter, RowMapper<T> rowMapper) throws SQLException;

	<T> Cursor<T> queryForCursor(String sql, SqlParameter sqlParameter, RowMapper<T> rowMapper) throws SQLException;

	<T> Cursor<T> queryForCachedCursor(String sql, SqlParameter sqlParameter, RowMapper<T> rowMapper) throws SQLException;

}
