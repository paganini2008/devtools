package com.github.paganini2008.devtools.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public interface Transaction {

	Connection getConnection() throws SQLException;

	void commit() throws SQLException;

	void rollback() throws SQLException;

	void close() throws SQLException;
	
	long getElapsed();

}