/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools.jdbc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Build a DataSource that contains unpooled connection.
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class UnpooledDataSource extends AbstractDataSource {

	private final String driverClassName;
	private final String jdbcUrl;
	private final String user;
	private final String password;
	private final Semaphore semaphore;
	private Boolean autoCommit;
	private TransactionIsolationLevel transactionIsolationLevel;
	private int timeout = 60;

	public UnpooledDataSource(String driverClassName, String jdbcUrl, String user, String password, int maxSize) {
		try {
			Class.forName(driverClassName);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Invalid driverClassName: " + driverClassName, e);
		}
		this.driverClassName = driverClassName;
		this.jdbcUrl = jdbcUrl;
		this.user = user;
		this.password = password;
		this.semaphore = new Semaphore(maxSize);
	}

	@Override
	public void setUser(String user) {
		throw new UnsupportedOperationException("setUser");
	}

	@Override
	public void setPassword(String password) {
		throw new UnsupportedOperationException("setPassword");
	}

	@Override
	public void setJdbcUrl(String jdbcUrl) {
		throw new UnsupportedOperationException("setJdbcUrl");
	}

	@Override
	public void setDriverClassName(String driverClassName) throws SQLException {
		throw new UnsupportedOperationException("setDriverClassName");
	}

	public void setAutoCommit(Boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	public void setTransactionIsolationLevel(TransactionIsolationLevel transactionIsolationLevel) {
		this.transactionIsolationLevel = transactionIsolationLevel;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public Boolean getAutoCommit() {
		return autoCommit;
	}

	public TransactionIsolationLevel getTransactionIsolationLevel() {
		return transactionIsolationLevel;
	}

	/**
	 * Timeout in seconds
	 * 
	 * @param timeout
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public Connection getConnection() throws SQLException {
		try {
			if (timeout > 0) {
				if (semaphore.tryAcquire(timeout, TimeUnit.SECONDS) == false) {
					throw new SQLException("Acquiring connection timeout for " + timeout + " sec.");
				}
			} else {
				semaphore.acquire();
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		Connection connection = JdbcUtils.getConnection(jdbcUrl, user, password);
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
				JdbcUtils.closeQuietly(realConnection);
				semaphore.release();
				return null;
			} else {
				return method.invoke(realConnection, args);
			}
		}

	}

}
