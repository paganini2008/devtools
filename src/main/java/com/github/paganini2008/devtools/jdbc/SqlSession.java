package com.github.paganini2008.devtools.jdbc;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface SqlSession {

	List<Map<String, Object>> queryForList(String sql, Object[] parameters, JdbcType[] jdbcTypes) throws SQLException;

	<T> List<T> queryForList(String sql, Object[] parameters, JdbcType[] jdbcTypes, Class<T> objectClass) throws SQLException;

	Map<String, Object> queryForMap(String sql, Object[] parameters) throws SQLException;
	
	Map<String, Object> queryForMap(String sql, Object[] parameters, JdbcType[] jdbcTypes) throws SQLException;
	
	<T> T queryForObject(String sql, Object[] parameters, Class<T> requiredType) throws SQLException;

	<T> T queryForObject(String sql, Object[] parameters, JdbcType[] jdbcTypes, Class<T> requiredType) throws SQLException;

	Iterator<Map<String, Object>> iterator(String sql, Object[] parameters, JdbcType[] jdbcTypes) throws SQLException;

	Iterator<Map<String, Object>> iterator(String sql, Object[] parameters) throws SQLException;

	<T> Iterator<T> iterator(String sql, Object[] parameters, JdbcType[] jdbcTypes, Class<T> objectClass) throws SQLException;

	<T> Iterator<T> iterator(String sql, Object[] parameters, Class<T> objectClass) throws SQLException;

	void update(String sql, Object[] parameters) throws SQLException;

	void update(String sql, Object[] parameters, JdbcType[] jdbcTypes) throws SQLException;

	void update(String sql, Object[] parameters, KeyHolder keyHolder) throws SQLException;

	void update(String sql, Object[] parameters, JdbcType[] jdbcTypes, KeyHolder keyHolder) throws SQLException;

	int[] batch(String sql, List<Object[]> parameters) throws SQLException;

	int[] batch(String sql, List<Object[]> parameters, JdbcType[] jdbcTypes) throws SQLException;

	<T> T execute(SqlSessionCallback<T> callback);

}