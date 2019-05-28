package com.github.paganini2008.devtools.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * PreparedStatementSetter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface PreparedStatementSetter {

	/**
	 * Set sql parameters
	 * 
	 * @param ps
	 * @throws SQLException
	 */
	void setValues(PreparedStatement ps) throws SQLException;

}
