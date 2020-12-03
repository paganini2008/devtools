package com.github.paganini2008.devtools.db4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.github.paganini2008.devtools.db4j.mapper.RowMapper;

/**
 * RowMapperResultSetExtractor
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class RowMapperResultSetExtractor<T> implements ResultSetExtractor<List<T>> {

	private final RowMapper<T> rowMapper;
	private final TypeHandlerRegistry typeHandlerRegistry;

	public RowMapperResultSetExtractor(RowMapper<T> rowMapper, TypeHandlerRegistry typeHandlerRegistry) {
		if (rowMapper == null) {
			throw new IllegalArgumentException("RowMapper must not be null.");
		}
		this.rowMapper = rowMapper;
		this.typeHandlerRegistry = typeHandlerRegistry;
	}

	public List<T> extractData(ResultSet rs) throws SQLException {
		List<T> results = new ArrayList<T>();
		int rownum = 1;
		while (rs.next()) {
			results.add(this.rowMapper.mapRow(rownum++, rs, typeHandlerRegistry));
		}
		return results;
	}

}
