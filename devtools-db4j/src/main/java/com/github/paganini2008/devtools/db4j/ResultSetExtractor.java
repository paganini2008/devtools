package com.github.paganini2008.devtools.db4j;

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
