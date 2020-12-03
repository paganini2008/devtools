package com.github.paganini2008.devtools.db4j.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.db4j.TypeHandlerRegistry;

/**
 * RowMapper
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface RowMapper<T> {

	T mapRow(int rowNum, ResultSet rs, TypeHandlerRegistry typeHandlerRegistry) throws SQLException;

}
