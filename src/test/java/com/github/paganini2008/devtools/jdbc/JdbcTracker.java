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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * JdbcTracker wraps H2 connections with proxies which record every
 * Statement/ResultSet created, so tests can verify that all of them are
 * closed.
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class JdbcTracker {

	private final String url;
	private final List<Connection> connections = new ArrayList<Connection>();
	private final List<Statement> statements = new ArrayList<Statement>();
	private final List<ResultSet> resultSets = new ArrayList<ResultSet>();

	public JdbcTracker(String dbName) {
		this.url = "jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1";
	}

	public String getUrl() {
		return url;
	}

	public Connection rawConnection() throws SQLException {
		return DriverManager.getConnection(url, "sa", "");
	}

	public synchronized Connection connection() throws SQLException {
		Connection real = rawConnection();
		Connection proxy = wrap(real, Connection.class);
		connections.add(real);
		return proxy;
	}

	public ConnectionFactory connectionFactory() {
		return () -> connection();
	}

	@SuppressWarnings("unchecked")
	private <T> T wrap(final Object target, Class<T> iface) {
		return (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { iface }, (proxy, method, args) -> {
			Object result;
			try {
				result = method.invoke(target, args);
			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
			synchronized (JdbcTracker.this) {
				if (result instanceof java.sql.PreparedStatement) {
					statements.add((Statement) result);
					return wrap(result, java.sql.PreparedStatement.class);
				} else if (result instanceof Statement) {
					statements.add((Statement) result);
					return wrap(result, Statement.class);
				} else if (result instanceof ResultSet) {
					resultSets.add((ResultSet) result);
					return wrap(result, ResultSet.class);
				}
			}
			return result;
		});
	}

	public synchronized int openConnections() throws SQLException {
		int n = 0;
		for (Connection c : connections) {
			if (!c.isClosed()) {
				n++;
			}
		}
		return n;
	}

	public synchronized int openStatements() throws SQLException {
		int n = 0;
		for (Statement s : statements) {
			if (!s.isClosed()) {
				n++;
			}
		}
		return n;
	}

	public synchronized int openResultSets() throws SQLException {
		int n = 0;
		for (ResultSet rs : resultSets) {
			if (!rs.isClosed()) {
				n++;
			}
		}
		return n;
	}

	public synchronized int statementCount() {
		return statements.size();
	}

	/**
	 * Count of open statements and result sets (connections excluded).
	 */
	public int openResources() throws SQLException {
		return openStatements() + openResultSets();
	}

	public synchronized void closeAll() {
		for (Connection c : connections) {
			JdbcUtils.closeQuietly(c);
		}
	}

	public void execute(String... sqls) throws SQLException {
		try (Connection c = rawConnection(); Statement s = c.createStatement()) {
			for (String sql : sqls) {
				s.execute(sql);
			}
		}
	}

	public void drop() throws SQLException {
		closeAll();
		execute("DROP ALL OBJECTS");
	}
}
