package com.github.paganini2008.devtools.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 
 * UnpooledConnectionFactory
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class UnpooledConnectionFactory implements ConnectionFactory {

	private String driverClassName;
	private String url;
	private String user;
	private String password;
	private Boolean autoCommit;
	private TransactionIsolationLevel transactionIsolationLevel;

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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
		Connection connection = JdbcUtils.getConnection(url, user, password);
		if (autoCommit != null) {
			connection.setAutoCommit(autoCommit);
		}
		if (transactionIsolationLevel != null) {
			connection.setTransactionIsolation(transactionIsolationLevel.getLevel());
		}
		return connection;
	}

}
