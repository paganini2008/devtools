package com.github.paganini2008.devtools.jdbc;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JdbcSqlSession implements SqlSession {

	private final Transaction transaction;
	private final SqlRunner sqlRunner;

	public JdbcSqlSession(Transaction transaction, SqlRunner sqlRunner) {
		this.transaction = transaction;
		this.sqlRunner = sqlRunner;
	}

	public List<Map<String, Object>> queryForList(String sql, Object[] parameters, JdbcType[] jdbcTypes) throws SQLException {
		return sqlRunner.queryForList(transaction.getConnection(), sql, parameters, jdbcTypes);
	}

	public <T> List<T> queryForList(String sql, Object[] parameters, JdbcType[] jdbcTypes, Class<T> objectClass) throws SQLException {
		return sqlRunner.queryForList(transaction.getConnection(), sql, parameters, jdbcTypes, objectClass);
	}

	public Map<String, Object> queryForMap(String sql, Object[] parameters) throws SQLException {
		return sqlRunner.queryForMap(transaction.getConnection(), sql, parameters);
	}

	public Map<String, Object> queryForMap(String sql, Object[] parameters, JdbcType[] jdbcTypes) throws SQLException {
		return sqlRunner.queryForMap(transaction.getConnection(), sql, parameters, jdbcTypes);
	}

	public <T> T queryForObject(String sql, Object[] parameters, JdbcType[] jdbcTypes, Class<T> requiredType) throws SQLException {
		return sqlRunner.queryForObject(transaction.getConnection(), sql, parameters, jdbcTypes, requiredType);
	}

	public <T> T queryForObject(String sql, Object[] parameters, Class<T> requiredType) throws SQLException {
		return sqlRunner.queryForObject(transaction.getConnection(), sql, parameters, requiredType);
	}

	public Iterator<Map<String, Object>> iterator(String sql, Object[] parameters, JdbcType[] jdbcTypes) throws SQLException {
		return sqlRunner.iterator(transaction.getConnection(), sql, parameters, jdbcTypes);
	}

	public Iterator<Map<String, Object>> iterator(String sql, Object[] parameters) throws SQLException {
		return sqlRunner.iterator(transaction.getConnection(), sql, parameters);
	}

	public <T> Iterator<T> iterator(String sql, Object[] parameters, JdbcType[] jdbcTypes, Class<T> objectClass) throws SQLException {
		return sqlRunner.iterator(transaction.getConnection(), sql, parameters, jdbcTypes, objectClass);
	}

	public <T> Iterator<T> iterator(String sql, Object[] parameters, Class<T> objectClass) throws SQLException {
		return sqlRunner.iterator(transaction.getConnection(), sql, parameters, objectClass);
	}

	public void update(String sql, Object[] parameters) throws SQLException {
		sqlRunner.update(transaction.getConnection(), sql, parameters);
	}

	public void update(String sql, Object[] parameters, JdbcType[] jdbcTypes) throws SQLException {
		sqlRunner.update(transaction.getConnection(), sql, parameters);
	}

	public void update(String sql, Object[] parameters, KeyHolder keyHolder) throws SQLException {
		sqlRunner.update(transaction.getConnection(), sql, parameters, keyHolder);
	}

	public void update(String sql, Object[] parameters, JdbcType[] jdbcTypes, KeyHolder keyHolder) throws SQLException {
		sqlRunner.update(transaction.getConnection(), sql, parameters, keyHolder);
	}

	public int[] batch(String sql, List<Object[]> parameters) throws SQLException {
		return sqlRunner.batch(transaction.getConnection(), sql, parameters);
	}

	public int[] batch(String sql, List<Object[]> parameters, JdbcType[] jdbcTypes) throws SQLException {
		return sqlRunner.batch(transaction.getConnection(), sql, parameters, jdbcTypes);
	}

	public <T> T execute(SqlSessionCallback<T> callback) {
		return callback.execute(transaction, sqlRunner);
	}

}
