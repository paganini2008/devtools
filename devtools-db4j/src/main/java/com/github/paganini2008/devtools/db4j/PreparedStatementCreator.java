package com.github.paganini2008.devtools.db4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * PreparedStatementCreator
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public interface PreparedStatementCreator {

	PreparedStatement createPreparedStatement(Connection connection) throws SQLException;

}
