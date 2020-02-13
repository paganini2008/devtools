package com.github.paganini2008.devtools.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 
 * PreparedStatementSetter
 *
 * @author Fred Feng
 * @created 2016-02
 * @revised 2020-01
 * @version 1.0
 */
@FunctionalInterface
public interface PreparedStatementSetter {

	void setValues(PreparedStatement ps) throws SQLException;

}
