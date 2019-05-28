package com.github.paganini2008.devtools.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

public class JdbcTransaction implements Transaction {

	private final DataSource dataSource;
	private Connection connection;
	private TransactionIsolationLevel level;
	private Boolean autoCommit;
	private long startTime;

	public JdbcTransaction(DataSource dataSource) {
		this.dataSource = dataSource;
		this.startTime = System.currentTimeMillis();
	}

	public Connection getConnection() throws SQLException {
		if (connection == null) {
			openConnection();
		}
		return connection;
	}

	public void commit() throws SQLException {
		if (connection != null && !connection.getAutoCommit()) {
			connection.commit();
		}
	}

	public void rollback() throws SQLException {
		if (connection != null && !connection.getAutoCommit()) {
			connection.rollback();
		}
	}

	public void close() throws SQLException {
		resetAutoCommit();
		DBUtils.closeQuietly(connection);
	}

	protected void resetAutoCommit() throws SQLException {
		if (connection != null && !connection.getAutoCommit()) {
			connection.setAutoCommit(true);
		}
	}

	protected void openConnection() throws SQLException {
		connection = dataSource.getConnection();
		if (level != null) {
			connection.setTransactionIsolation(level.getLevel());
		}
		if (autoCommit != null && connection.getAutoCommit() != autoCommit) {
			connection.setAutoCommit(autoCommit);
		}
	}

	public long getElapsed() {
		return System.currentTimeMillis() - startTime;
	}

}
