package com.github.paganini2008.devtools.jdbc;

import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Semaphore;

/**
 * Build a DataSource that contains unpooled connection.
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class UnpooledDataSource extends AbstractDataSource {

	private String driverClassName;
	private String url;
	private String user;
	private String password;
	private Boolean autoCommit;
	private TransactionIsolationLevel transactionIsolationLevel;
	private Integer maxSize = 16;
	private Semaphore semaphore;
	
	public UnpooledDataSource() {
	}

	public void setMaxSize(Integer maxSize) {
		this.maxSize = maxSize;
	}

	public Integer getMaxSize() {
		return maxSize;
	}

	public Boolean getAutoCommit() {
		return autoCommit;
	}

	public void setAutoCommit(Boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		try {
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Unknow driverClassName: " + driverClassName, e);
		}
		this.driverClassName = driverClassName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public TransactionIsolationLevel getTransactionIsolationLevel() {
		return transactionIsolationLevel;
	}

	public void setTransactionIsolationLevel(TransactionIsolationLevel transactionIsolationLevel) {
		this.transactionIsolationLevel = transactionIsolationLevel;
	}

	public PrintWriter getLogWriter() throws SQLException {
		return DriverManager.getLogWriter();
	}

	public void setLogWriter(PrintWriter logWriter) throws SQLException {
		DriverManager.setLogWriter(logWriter);
	}

	public void setLoginTimeout(int seconds) throws SQLException {
		DriverManager.setLoginTimeout(seconds);
	}

	public int getLoginTimeout() throws SQLException {
		return DriverManager.getLoginTimeout();
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new SQLException(getClass().getName() + " is not a wrapper.");
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	public Connection getConnection() throws SQLException {
		if (semaphore == null) {
			synchronized (this) {
				if (semaphore == null) {
					semaphore = new Semaphore(maxSize);
				}
			}
		}
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		Connection connection = DBUtils.getConnection(url, user, password);
		configureConnection(connection);
		return new ConnectionProxy(connection, semaphore).getProxyConnection();
	}

	protected void configureConnection(Connection connection) throws SQLException {
		if (autoCommit != null) {
			connection.setAutoCommit(autoCommit);
		}
		if (transactionIsolationLevel != null) {
			connection.setTransactionIsolation(transactionIsolationLevel.getLevel());
		}
	}

	static class ConnectionProxy implements InvocationHandler {

		private static final String CLOSE = "close";
		private static final Class<?>[] IFACES = new Class<?>[] { Connection.class };

		ConnectionProxy(Connection realConnection, Semaphore semaphore) {
			this.realConnection = realConnection;
			this.semaphore = semaphore;
			this.proxyConnection = (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), IFACES, this);
		}

		private final Connection realConnection;
		private final Connection proxyConnection;
		private final Semaphore semaphore;

		public Connection getRealConnection() {
			return realConnection;
		}

		public Connection getProxyConnection() {
			return proxyConnection;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			String methodName = method.getName();
			if (methodName.equals("equals")) {
				return (realConnection == args[0]);
			} else if (methodName.equals("hashCode")) {
				return System.identityHashCode(realConnection);
			} else if (methodName.equals("toString")) {
				return realConnection.toString();
			} else if (CLOSE.hashCode() == methodName.hashCode() && CLOSE.equals(methodName)) {
				DBUtils.closeQuietly(realConnection);
				semaphore.release();
				return null;
			} else {
				return method.invoke(realConnection, args);
			}
		}

	}

}
