package com.github.paganini2008.devtools.jdbc;

import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * DriverManagerDataSource
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface DriverManagerDataSource extends DataSource {

	/**
	 * Set username
	 * 
	 * @param user
	 */
	void setUser(String user);

	/**
	 * Set password
	 * 
	 * @param password
	 */
	void setPassword(String password);

	/**
	 * Set jdbc url
	 * 
	 * @param url
	 */
	void setJdbcUrl(String jdbcUrl);

	/**
	 * Set DriverClass Name
	 * 
	 * @param driverClassName
	 */
	void setDriverClassName(String driverClassName) throws SQLException;

	/**
	 * Close the DataSource
	 */
	void close() throws SQLException;

}
