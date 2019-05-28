package com.github.paganini2008.devtools.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * PreparedStatementCreator
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface PreparedStatementCreator {

	PreparedStatement createPreparedStatement(Connection connection) throws SQLException;

}
