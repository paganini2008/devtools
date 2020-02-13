package com.github.paganini2008.devtools.db4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * PreparedStatementCreator
 * 
 * @author Fred Feng
 * @created 2016-02
 * @version 1.0
 */
@FunctionalInterface
public interface PreparedStatementCreator {

	PreparedStatement createPreparedStatement(Connection connection) throws SQLException;

}
