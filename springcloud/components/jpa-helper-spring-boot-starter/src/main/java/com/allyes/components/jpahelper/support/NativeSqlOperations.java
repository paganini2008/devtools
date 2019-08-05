package com.allyes.components.jpahelper.support;

import java.util.Map;

import com.allyes.components.jpahelper.ResultSetExtractor;

/**
 * 
 * NativeSqlOperations
 * 
 * @author Fred Feng
 * @created 2019-04
 */
public interface NativeSqlOperations<E> {

	ResultSetSlice<E> select(String sql, Object[] arguments);

	<T> T getSingleResult(String sql, Object[] arguments, Class<T> requiredType);

	default ResultSetSlice<Map<String, Object>> selectForMap(String sql, Object[] arguments) {
		return select(sql, arguments, tuple -> tuple);
	}

	default <T> ResultSetSlice<T> select(String sql, Object[] arguments, Class<T> resultClass) {
		return select(sql, arguments, new BeanPropertyRowMapper<T>(resultClass));
	}

	<T> ResultSetSlice<T> select(String sql, Object[] arguments, RowMapper<T> mapper);

	<T> T execute(String sql, Object[] arguments, ResultSetExtractor<T> extractor);

	int executeUpdate(String sql, Object[] arguments);

}
