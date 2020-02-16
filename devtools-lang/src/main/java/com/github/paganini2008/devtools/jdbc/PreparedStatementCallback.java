package com.github.paganini2008.devtools.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 
 * PreparedStatementCallback
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
@FunctionalInterface
public interface PreparedStatementCallback {

	void setValues(PreparedStatement ps) throws SQLException;

}
