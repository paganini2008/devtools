package com.github.paganini2008.devtools.jdbc;

import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

/**
 * DataSourceFactory
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface DataSourceFactory {

	void setProperties(Properties config) throws SQLException;
	
	DataSource getDataSource() throws SQLException;

}
