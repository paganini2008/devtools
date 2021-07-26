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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

import javax.sql.DataSource;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.ListUtils;
import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.multithreads.ExecutorUtils;
import com.github.paganini2008.devtools.primitives.Longs;

/**
 * 
 * JdbcDumpTemplate provide some jdbc operation that export data from one
 * ConnectionFactory and import into annother ConnectionFactory
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

	public long[] dump(String catalog, String schema, String[] excludedTables, JdbcDumpOptions dumpOptions, DumpErrorHandler errorHandler)
			throws SQLException {
		List<Long> rows = new ArrayList<Long>();
		Connection sourceConnection = null;
		try {
			sourceConnection = sourceConnectionFactory.getConnection(catalog, schema);
			Cursor<Tuple> cursor = JdbcUtils.describe(sourceConnection, catalog, schema);
			while (cursor.hasNext()) {
				Tuple tuple = cursor.next();
				String tableName = tuple.getProperty("tableName");
				if (StringUtils.isNotBlank(tableName)) {
					if (ArrayUtils.isEmpty(excludedTables) || ArrayUtils.notContains(excludedTables, tableName)) {
						rows.add(dump(catalog, schema, tableName, new TableJdbcDumpOptions(tableName, dumpOptions), errorHandler));
					}
				}
			}
			return Longs.toArray(rows);
		} finally {
			sourceConnectionFactory.close(sourceConnection);
		}

	}

	public long dump(String catalog, String schema, String tableName, JdbcDumpOptions dumpOptions, DumpErrorHandler errorHandler)
			throws SQLException {
		String sql = "select * from " + tableName;
		try {
			return dump(catalog, schema, sql, null, new TableJdbcDumpOptions(tableName, dumpOptions));
		} catch (Exception e) {
			if (errorHandler != null) {
				errorHandler.handleError(catalog, schema, sql, null, e);
				return 0;
			} else {
				throw e;
			}
		}
	}

	public long dump(String catalog, String schema, String sql, Object[] args, JdbcDumpOptions dumpOptions) throws SQLException {
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

			}, dumpOptions.getMaxRecords());
			return rows.get();
		} finally {
			sourceConnectionFactory.close(sourceConnection);
		}

	}

	public long[] dump(String catalog, String schema, String[] excludedTables, int batchSize, DumpProgress progress,
			JdbcDumpOptions dumpOptions, DumpErrorHandler errorHandler) throws SQLException {
		List<Long> rows = new ArrayList<Long>();
		Connection sourceConnection = null;
		try {
			sourceConnection = sourceConnectionFactory.getConnection(catalog, schema);
			Cursor<Tuple> cursor = JdbcUtils.describe(sourceConnection, catalog, schema);
			while (cursor.hasNext()) {
				Tuple tuple = cursor.next();
				String tableName = tuple.getProperty("tableName");
				if (StringUtils.isNotBlank(tableName)) {
					if (ArrayUtils.isEmpty(excludedTables) || ArrayUtils.notContains(excludedTables, tableName)) {
						rows.add(dump(catalog, schema, tableName, batchSize, progress, new TableJdbcDumpOptions(tableName, dumpOptions),
								errorHandler));
					}
				}
			}
			return Longs.toArray(rows);
		} finally {
			sourceConnectionFactory.close(sourceConnection);
		}

	}

	public long dump(String catalog, String schema, String tableName, int batchSize, DumpProgress progress, JdbcDumpOptions dumpOptions,
			DumpErrorHandler errorHandler) throws SQLException {
		String sql = "select * from " + tableName;
		try {
			return dump(catalog, schema, sql, null, batchSize, progress, new TableJdbcDumpOptions(tableName, dumpOptions));
		} catch (Exception e) {
			if (errorHandler != null) {
				errorHandler.handleError(catalog, schema, sql, null, e);
				return 0;
			} else {
				throw e;
			}
		}
	}

	private long getTotalRecords(ConnectionFactory connectionFactory, String sql, Object[] args) throws SQLException {
		Connection connection = connectionFactory.getConnection();
		try {
			return JdbcUtils.rowCount(connection, sql, args);
		} finally {
			connectionFactory.close(connection);
		}
	}

	public long dump(String catalog, String schema, String sql, Object[] args, int batchSize, DumpProgress progress,
			JdbcDumpOptions dumpOptions) throws SQLException {
		AtomicLong rows = new AtomicLong();
		if (progress != null) {
			progress.onStart(catalog, schema, sql, args, dumpOptions);
		}
		ConnectionFactory connectionFactory = new InternalConnectionFactory(sourceConnectionFactory, catalog, schema);
		long totalRecords = progress != null ? getTotalRecords(connectionFactory, sql, args) : 0;
		JdbcUtils.batchScan(connectionFactory, sql, args, 1, batchSize, list -> {
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
					long progressRecords = rows.addAndGet(effectedRows != null ? effectedRows.length : 0);
					if (progress != null) {
						progress.progress(catalog, schema, sql, args, progressRecords, totalRecords, dumpOptions);
					}
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

		}, dumpOptions.getMaxRecords());
		if (progress != null) {
			progress.onEnd(catalog, schema, sql, args, dumpOptions);
		}
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
		public Connection getConnection(String catalog, String schema) throws SQLException {
			throw new UnsupportedOperationException();
		}

		@Override
		public void close(Connection connection) throws SQLException {
			delegate.close(connection);
		}
	}

	static class TableJdbcDumpOptions implements JdbcDumpOptions {

		private static final String SQL_INSERTION = "insert into %s(%s) values (%s)";
		private final String tableName;
		private final JdbcDumpOptions dumpOptions;

		TableJdbcDumpOptions(String tableName, JdbcDumpOptions dumpOptions) {
			this.tableName = tableName;
			this.dumpOptions = dumpOptions;
		}

		@Override
		public String getCatalog() {
			return dumpOptions.getCatalog();
		}

		@Override
		public String getSchema() {
			return dumpOptions.getSchema();
		}

		@Override
		public String getTableName() {
			return tableName;
		}

		@Override
		public Executor getExecutor() {
			return dumpOptions.getExecutor();
		}

		@Override
		public long getMaxRecords() {
			return dumpOptions.getMaxRecords();
		}

		@Override
		public String getInsertionSql(Tuple t) {
			String sql = dumpOptions.getInsertionSql(t);
			if (StringUtils.isNotBlank(sql)) {
				return sql;
			}
			String[] columnNames = t.keys();
			return String.format(SQL_INSERTION, tableName, ArrayUtils.join(columnNames, ","),
					StringUtils.repeat("?", ",", columnNames.length));
		}

		@Override
		public Predicate<Tuple> getPredicate() {
			return dumpOptions.getPredicate();
		}

		@Override
		public Object[] getArgs(Tuple t) {
			return dumpOptions.getArgs(t);
		}
	}

}
