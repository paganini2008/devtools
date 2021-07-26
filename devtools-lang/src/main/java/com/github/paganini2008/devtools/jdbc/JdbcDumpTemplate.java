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
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

import javax.sql.DataSource;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.ListUtils;
import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.multithreads.ExecutorUtils;

/**
 * 
 * JdbcDumpTemplate
 *
 * @author Fred Feng
 *
 * @since 2.0.2
 */
public class JdbcDumpTemplate {

	private final ConnectionFactory sourceConnectionFactory;
	private final ConnectionFactory destinationConnectionFactory;

	public JdbcDumpTemplate(DataSource source, DataSource destination) {
		this(new PooledConnectionFactory(source), new PooledConnectionFactory(destination));
	}

	public JdbcDumpTemplate(ConnectionFactory sourceConnectionFactory, ConnectionFactory destinationConnectionFactory) {
		this.sourceConnectionFactory = sourceConnectionFactory;
		this.destinationConnectionFactory = destinationConnectionFactory;
	}

	public long dump(String catalog, String schema, DatabaseDumpOptions dumpOptions) throws SQLException {
		AtomicLong rows = new AtomicLong();
		Connection sourceConnection = null;
		try {
			sourceConnection = sourceConnectionFactory.getConnection(catalog, schema);
			Cursor<Tuple> cursor = JdbcUtils.describe(sourceConnection, catalog, schema);
			while (cursor.hasNext()) {
				Tuple tuple = cursor.next();
				String tableName = tuple.getProperty("tableName");
				if (StringUtils.isNotBlank(tableName)) {
					if (ArrayUtils.isEmpty(dumpOptions.excludeTables()) || ArrayUtils.notContains(dumpOptions.excludeTables(), tableName)) {
						rows.addAndGet(dump(catalog, schema, tableName, new QueryDumpOptionsWrapper(tableName, dumpOptions)));
					}
				}
			}
			return rows.get();
		} finally {
			sourceConnectionFactory.close(sourceConnection);
		}

	}

	public long dump(String catalog, String schema, String tableName, QueryDumpOptions dumpOptions) throws SQLException {
		return dump(catalog, schema, "select * from " + tableName, null, dumpOptions);
	}

	public long dump(String catalog, String schema, String sql, Object[] args, QueryDumpOptions dumpOptions) throws SQLException {
		AtomicLong rows = new AtomicLong();
		Connection sourceConnection = null;
		try {
			sourceConnection = sourceConnectionFactory.getConnection(catalog, schema);
			JdbcUtils.scan(sourceConnection, sql, args, t -> {
				ExecutorUtils.runInBackground(dumpOptions.getExecutor(), () -> {
					Connection destinationConnection = null;
					try {
						destinationConnection = destinationConnectionFactory.getConnection(dumpOptions.getCatalog(),
								dumpOptions.getSchema());
						int effectedRow = JdbcUtils.update(destinationConnection, dumpOptions.getInsertionSql(t), ps -> {
							JdbcUtils.setValues(ps, dumpOptions.getArgs(t));
						});
						rows.addAndGet(effectedRow);
					} catch (SQLException e) {
						throw new JdbcDumpException("Failed to execute writing operation", e);
					} finally {
						try {
							destinationConnectionFactory.close(destinationConnection);
						} catch (SQLException e) {
							throw new JdbcDumpException(e);
						}
					}
				});

			});
			return rows.get();
		} finally {
			sourceConnectionFactory.close(sourceConnection);
		}

	}

	public long dump(String catalog, String schema, int batchSize, DatabaseDumpOptions dumpOptions) throws SQLException {
		AtomicLong rows = new AtomicLong();
		Connection sourceConnection = null;
		try {
			sourceConnection = sourceConnectionFactory.getConnection(catalog, schema);
			Cursor<Tuple> cursor = JdbcUtils.describe(sourceConnection, catalog, schema);
			while (cursor.hasNext()) {
				Tuple tuple = cursor.next();
				String tableName = tuple.getProperty("tableName");
				if (StringUtils.isNotBlank(tableName)) {
					if (ArrayUtils.isEmpty(dumpOptions.excludeTables()) || ArrayUtils.notContains(dumpOptions.excludeTables(), tableName)) {
						rows.addAndGet(dump(catalog, schema, tableName, batchSize, new QueryDumpOptionsWrapper(tableName, dumpOptions)));
					}
				}
			}
			return rows.get();
		} finally {
			sourceConnectionFactory.close(sourceConnection);
		}

	}

	public long dump(String catalog, String schema, String tableName, int batchSize, QueryDumpOptions dumpOptions) throws SQLException {
		return dump(catalog, schema, "select * from " + tableName, null, batchSize, dumpOptions);
	}

	public long dump(String catalog, String schema, String sql, Object[] args, int batchSize, QueryDumpOptions dumpOptions)
			throws SQLException {
		AtomicLong rows = new AtomicLong();
		JdbcUtils.batchScan(new InternalConnectionFactory(sourceConnectionFactory, catalog, schema), sql, args, 1, batchSize, list -> {
			ExecutorUtils.runInBackground(dumpOptions.getExecutor(), () -> {
				Connection destinationConnection = null;
				try {
					destinationConnection = destinationConnectionFactory.getConnection(dumpOptions.getCatalog(), dumpOptions.getSchema());
					int[] effectedRows = JdbcUtils.batchUpdate(destinationConnection, dumpOptions.getInsertionSql(ListUtils.getFirst(list)),
							ps -> {
								list.stream().filter(dumpOptions.getPredicate()).forEach(t -> {
									try {
										JdbcUtils.setValues(ps, dumpOptions.getArgs(t));
										ps.addBatch();
									} catch (SQLException e) {
										throw new JdbcDumpException("Failed to set values into PreparedStatement", e);
									}
								});
							});
					rows.addAndGet(effectedRows != null ? effectedRows.length : 0);
				} catch (SQLException e) {
					throw new JdbcDumpException("Failed to execute batch writing operation", e);
				} finally {
					try {
						destinationConnectionFactory.close(destinationConnection);
					} catch (SQLException e) {
						throw new JdbcDumpException(e);
					}
				}
			});

		});
		return rows.get();
	}

	private static class InternalConnectionFactory implements ConnectionFactory {

		private final ConnectionFactory delegate;
		private final String catalog;
		private final String schema;

		InternalConnectionFactory(ConnectionFactory connectionFactory, String catalog, String schema) {
			this.delegate = connectionFactory;
			this.catalog = catalog;
			this.schema = schema;
		}

		@Override
		public Connection getConnection() throws SQLException {
			return delegate.getConnection(catalog, schema);
		}

		@Override
		public void close(Connection connection) throws SQLException {
			JdbcUtils.setPath(connection, catalog, schema);
			delegate.close(connection);
		}
	}

	private static class QueryDumpOptionsWrapper implements QueryDumpOptions {

		private final String tableName;
		private final DatabaseDumpOptions dumpOptions;

		QueryDumpOptionsWrapper(String tableName, DatabaseDumpOptions dumpOptions) {
			this.tableName = tableName;
			this.dumpOptions = dumpOptions;
		}

		@Override
		public String getInsertionSql(Tuple t) {
			return dumpOptions.getInsertionSql(tableName, t);
		}

		@Override
		public Predicate<Tuple> getPredicate() {
			return dumpOptions.getPredicate(tableName);
		}

		@Override
		public Object[] getArgs(Tuple t) {
			return dumpOptions.getArgs(tableName, t);
		}

		@Override
		public Executor getExecutor() {
			return dumpOptions.getExecutor();
		}
	}

}
