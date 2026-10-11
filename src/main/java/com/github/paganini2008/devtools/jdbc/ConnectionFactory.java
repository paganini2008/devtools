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

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 
 * ConnectionFactory abstracts how connections are obtained and released, so
 * callers can work with a DataSource, a pool or plain DriverManager
 * connections alike.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public interface ConnectionFactory {

	/**
	 * Obtains a connection.
	 * 
	 * @throws SQLException if no connection can be obtained
	 */
	Connection getConnection() throws SQLException;

	/**
	 * Obtains a connection and switches it to the catalog and schema; blank
	 * values are ignored.
	 */
	default Connection getConnection(String catalog, String schema) throws SQLException {
		Connection connection = getConnection();
		JdbcUtils.setPath(connection, catalog, schema);
		return connection;
	}

	/**
	 * Releases a connection obtained from this factory, ignoring null.
	 */
	default void close(Connection connection) throws SQLException {
		JdbcUtils.closeQuietly(connection);
	}

}
