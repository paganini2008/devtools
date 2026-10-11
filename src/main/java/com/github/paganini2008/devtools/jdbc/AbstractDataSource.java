/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

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

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.sql.DataSource;


/**
 * AbstractDataSource is a base {@link DriverManagerDataSource} which
 * implements the optional DataSource methods (log writer, login timeout,
 * unwrap etc.).
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class AbstractDataSource implements DriverManagerDataSource {

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
		if (iface == null) {
			throw new IllegalArgumentException("Interface argument must not be null");
		}
		if (!iface.isInstance(this)) {
			throw new SQLException("DataSource of type [" + getClass().getName()
					+ "] can only be unwrapped as [javax.sql.DataSource], not as [" + iface.getName());
		}
		return iface.cast(this);
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return iface != null && iface.isInstance(this);
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
