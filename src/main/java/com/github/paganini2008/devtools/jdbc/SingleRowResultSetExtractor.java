package com.github.paganini2008.devtools.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.jdbc.mapper.RowMapper;

/**
 * SingleRowResultSetExtractor
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class SingleRowResultSetExtractor<T> implements ResultSetExtractor<T> {

	private final RowMapper<T> rowMapper;

	public SingleRowResultSetExtractor(RowMapper<T> rowMapper) {
		if (rowMapper == null) {
			throw new IllegalArgumentException("RowMapper must not be null.");
		}
		this.rowMapper = rowMapper;
	}

	public T extractData(ResultSet rs) throws SQLException {
		return (rs != null) && (rs.next()) ? rowMapper.mapRow(1, rs) : null;
	}

}
