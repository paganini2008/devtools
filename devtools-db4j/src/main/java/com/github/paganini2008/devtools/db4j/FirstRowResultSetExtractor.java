package com.github.paganini2008.devtools.db4j;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.db4j.mapper.RowMapper;

/**
 * FirstRowResultSetExtractor
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class FirstRowResultSetExtractor<T> implements ResultSetExtractor<T> {

	private final RowMapper<T> rowMapper;

	public FirstRowResultSetExtractor(RowMapper<T> rowMapper) {
		if (rowMapper == null) {
			throw new IllegalArgumentException("RowMapper must not be null.");
		}
		this.rowMapper = rowMapper;
	}

	public T extractData(ResultSet rs) throws SQLException {
		return (rs != null) && (rs.next()) ? rowMapper.mapRow(1, rs) : null;
	}

}
