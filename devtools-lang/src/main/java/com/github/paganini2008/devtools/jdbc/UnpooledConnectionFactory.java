package com.github.paganini2008.devtools.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import com.github.paganini2008.devtools.beans.ToStringBuilder;

/**
 * 
 * UnpooledConnectionFactory
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class UnpooledConnectionFactory implements ConnectionFactory {

	private String driverClassName;
	private String jdbcUrl;
	private String user;
	private String password;
	private Boolean autoCommit;
	private TransactionIsolationLevel transactionIsolationLevel;

	public UnpooledConnectionFactory() {
	}

	public UnpooledConnectionFactory(String driverClassName, String jdbcUrl, String user, String password) {
		super();
		setDriverClassName(driverClassName);
		setJdbcUrl(jdbcUrl);
		setUser(user);
		setPassword(password);
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		try {
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(driverClassName);
		}
		this.driverClassName = driverClassName;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getAutoCommit() {
		return autoCommit;
	}

	public void setAutoCommit(Boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	public TransactionIsolationLevel getTransactionIsolationLevel() {
		return transactionIsolationLevel;
	}

	public void setTransactionIsolationLevel(TransactionIsolationLevel transactionIsolationLevel) {
		this.transactionIsolationLevel = transactionIsolationLevel;
	}

	public Connection getConnection() throws SQLException {
		Connection connection = JdbcUtils.getConnection(jdbcUrl, user, password);
		if (autoCommit != null) {
			connection.setAutoCommit(autoCommit);
		}
		if (transactionIsolationLevel != null) {
			connection.setTransactionIsolation(transactionIsolationLevel.getLevel());
		}
		return connection;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, new String[] { "connection" });
	}

}
