package com.github.paganini2008.devtools.db4j;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 
 * ConnectionSettings
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface ConnectionSettings {

	void applySettings(Connection connection) throws SQLException;
	
}
