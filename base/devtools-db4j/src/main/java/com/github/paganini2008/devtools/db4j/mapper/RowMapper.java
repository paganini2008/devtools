package com.github.paganini2008.devtools.db4j.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * RowMapper
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface RowMapper<T> {

	T mapRow(int rowIndex, ResultSet rs) throws SQLException;
	
}
