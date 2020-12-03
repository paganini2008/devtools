package com.github.paganini2008.devtools.db4j;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.db4j.mapper.RowMapper;

/**
 * FirstRowResultSetExtractor
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class FirstRowResultSetExtractor<T> implements ResultSetExtractor<T> {

	private final RowMapper<T> rowMapper;
	private final TypeHandlerRegistry typeHandlerRegistry;

	public FirstRowResultSetExtractor(RowMapper<T> rowMapper, TypeHandlerRegistry typeHandlerRegistry) {
		if (rowMapper == null) {
			throw new IllegalArgumentException("RowMapper must not be null.");
		}
		this.rowMapper = rowMapper;
		this.typeHandlerRegistry = typeHandlerRegistry;
	}

	public T extractData(ResultSet rs) throws SQLException {
		return (rs != null) && (rs.next()) ? rowMapper.mapRow(1, rs, typeHandlerRegistry) : null;
	}

}
