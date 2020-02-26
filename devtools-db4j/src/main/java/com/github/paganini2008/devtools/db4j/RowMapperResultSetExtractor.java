package com.github.paganini2008.devtools.db4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.github.paganini2008.devtools.db4j.mapper.RowMapper;

/**
 * RowMapperResultSetExtractor
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class RowMapperResultSetExtractor<T> implements ResultSetExtractor<List<T>> {

	private final RowMapper<T> rowMapper;

	public RowMapperResultSetExtractor(RowMapper<T> rowMapper) {
		if (rowMapper == null) {
			throw new IllegalArgumentException("RowMapper must not be null.");
		}
		this.rowMapper = rowMapper;
	}

	public List<T> extractData(ResultSet rs) throws SQLException {
		List<T> results = new ArrayList<T>();
		int rownum = 1;
		while (rs.next()) {
			results.add(this.rowMapper.mapRow(rownum++, rs));
		}
		return results;
	}

}
