package com.github.paganini2008.devtools.db4j;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * PreparedStatementAction
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface PreparedStatementAction<T> {

	T execute(PreparedStatement ps) throws SQLException;

	void close(PreparedStatement ps);

}
