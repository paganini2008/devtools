package com.github.paganini2008.devtools.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * 
 * PooledConnectionFactory
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class PooledConnectionFactory implements ConnectionFactory {

	private final DataSource dataSource;

	public PooledConnectionFactory(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
	
	public String toString() {
		return dataSource.toString();
	}

}
