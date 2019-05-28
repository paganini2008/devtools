package com.github.paganini2008.devtools.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ResultSetExtractor
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface ResultSetExtractor<T> {

	T extractData(ResultSet rs) throws SQLException;

}
