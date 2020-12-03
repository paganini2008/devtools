package com.github.paganini2008.devtools.db4j;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * PreparedStatementExecutor
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface PreparedStatementExecutor<T> {

	T execute(PreparedStatement ps) throws SQLException;

	void close(PreparedStatement ps);

}
