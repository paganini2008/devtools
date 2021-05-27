package com.github.paganini2008.devtools.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.logging.Log;
import com.github.paganini2008.devtools.logging.LogFactory;

/**
 * AbstractDataSource
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class AbstractDataSource implements DriverManagerDataSource {

	protected final Log logger = LogFactory.getLog(getClass());

	public int getLoginTimeout() throws SQLException {
		throw new UnsupportedOperationException("Not supported by " + getClass().getName());
	}

	public void setLoginTimeout(int timeout) throws SQLException {
		throw new UnsupportedOperationException("Not supported by " + getClass().getName());
	}

	public PrintWriter getLogWriter() throws SQLException {
		throw new UnsupportedOperationException("Not supported by " + getClass().getName());
	}

	public void setLogWriter(PrintWriter pw) throws SQLException {
		throw new UnsupportedOperationException("Not supported by " + getClass().getName());
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		Assert.isNull(iface, "Interface argument must not be null");
		if (!DataSource.class.isAssignableFrom(iface)) {
			throw new SQLException("DataSource of type [" + getClass().getName()
					+ "] can only be unwrapped as [javax.sql.DataSource], not as [" + iface.getName());
		}
		return iface.cast(this);
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return DataSource.class.isAssignableFrom(iface);
	}

	public Connection getConnection(String username, String password) throws SQLException {
		throw new UnsupportedOperationException("Not supported by " + getClass().getName());
	}

	public Logger getParentLogger() {
		return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	}

	public void close() throws SQLException {
	}
}
