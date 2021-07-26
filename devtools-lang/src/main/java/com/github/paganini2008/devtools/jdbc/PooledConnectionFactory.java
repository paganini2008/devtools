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

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * 
 * PooledConnectionFactory
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public class PooledConnectionFactory implements ConnectionFactory {

	private final DataSource dataSource;
	private final String catalog;
	private final String schema;

	public PooledConnectionFactory(DataSource dataSource) {
		this(dataSource, null, null);
	}

	public PooledConnectionFactory(DataSource dataSource, String catalog, String schema) {
		this.dataSource = dataSource;
		this.catalog = catalog;
		this.schema = schema;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	@Override
	public void close(Connection connection) throws SQLException {
		JdbcUtils.setPath(connection, catalog, schema);
		JdbcUtils.closeQuietly(connection);
	}

	@Override
	public String toString() {
		return dataSource.toString();
	}

}
