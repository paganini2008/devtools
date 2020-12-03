package com.github.paganini2008.devtools.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 
 * ConnectionFactory
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface ConnectionFactory {

	Connection getConnection() throws SQLException;

	default void close(Connection connection) {
		JdbcUtils.closeQuietly(connection);
	}

}
