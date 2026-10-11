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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

import javax.sql.DataSource;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.ListUtils;
import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.primitives.Longs;

/**
 * 
 * JdbcDumpTemplate provide some jdbc operation that export data from one
 * ConnectionFactory and import into annother ConnectionFactory
 *
 * @author Fred Feng
 * @date 2021-07-26
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

	/**
	 * Dumps all tables of the source catalog and schema row by row.
	 * 
	 * @param catalog the source catalog, null meaning any
	 * @param schema the source schema, null meaning any
	 * @param excludedTables the table names to skip, may be null
	 * @param progress the progress listener, may be null
	 * @param dumpOptions the write options
	 * @param errorHandler handles the failure of a table, null to rethrow it
	 * @return the count of rows written for each table
	 */
	public long[] dump(String catalog, String schema, String[] excludedTables, DumpProgress progress, JdbcDumpOptions dumpOptions,
			DumpErrorHandler errorHandler) throws SQLException {
		List<Long> rows = new ArrayList<Long>();
		Connection sourceConnection = null;
		try {
			sourceConnection = sourceConnectionFactory.getConnection(catalog, schema);
			for (String tableName : tableNames(sourceConnection, catalog, schema, excludedTables)) {
				rows.add(dump(catalog, schema, tableName, progress, dumpOptions, errorHandler));
			}
			return Longs.toArray(rows);
		} finally {
			sourceConnectionFactory.close(sourceConnection);
		}

	}

	private static List<String> tableNames(Connection connection, String catalog, String schema, String[] excludedTables)
			throws SQLException {
		List<String> tableNames = new ArrayList<String>();
		Cursor<Tuple> cursor = JdbcUtils.describe(connection, catalog, schema);
		try {
			while (cursor.hasNext()) {
				Tuple tuple = cursor.next();
				String tableName = tuple.getProperty("tableName");
				if (StringUtils.isNotBlank(tableName)) {
					if (ArrayUtils.isEmpty(excludedTables) || ArrayUtils.notContains(excludedTables, tableName)) {
						tableNames.add(tableName);
					}
				}
			}
		} finally {
			cursor.close();
		}
		return tableNames;
	}

	/**
	 * Dumps one table row by row.
	 * 
	 * @param errorHandler handles the failure, null to rethrow it
	 * @return the count of rows written, 0 if the failure was handled
	 */
	public long dump(String catalog, String schema, String tableName, DumpProgress progress, JdbcDumpOptions dumpOptions,
			DumpErrorHandler errorHandler) throws SQLException {
		final String sql = "select * from " + tableName;
		try {
			return dump(catalog, schema, sql, null, progress, new TableJdbcDumpOptions(tableName, dumpOptions));
		} catch (Exception e) {
			if (errorHandler != null) {
				errorHandler.handleError(catalog, schema, sql, null, e);
				return 0;
			} else {
				throw e;
			}
		}
	}

	/**
	 * Dumps the rows of a query one by one. Rows are written in the caller
	 * thread or on the executor of the options; this method returns after all
	 * writes have finished.
	 * 
	 * @return the count of rows written
	 * @throws SQLException if the query fails
	 * @throws JdbcDumpException if a write fails
	 */
	public long dump(String catalog, String schema, String sql, Object[] args, DumpProgress progress, JdbcDumpOptions dumpOptions)
			throws SQLException {
		AtomicLong rows = new AtomicLong();
		if (progress != null) {
			progress.onStart(catalog, schema, sql, args, dumpOptions);
		}
		Connection sourceConnection = null;
		try {
			sourceConnection = sourceConnectionFactory.getConnection(catalog, schema);
			long totalRecords = progress != null ? JdbcUtils.rowCount(sourceConnection, sql, args) : 0;
			final TaskTracker tracker = new TaskTracker(dumpOptions.getExecutor());
			final Predicate<Tuple> predicate = dumpOptions.getPredicate();
			JdbcUtils.scan(sourceConnection, sql, args, t -> {
				if (predicate != null && !predicate.test(t)) {
					return;
				}
				tracker.submit(() -> {
					Connection destinationConnection = null;
					try {
						destinationConnection = destinationConnectionFactory.getConnection(dumpOptions.getCatalog(),
								dumpOptions.getSchema());
						int effectedRow = JdbcUtils.update(destinationConnection, dumpOptions.getInsertionSql(t), ps -> {
							JdbcUtils.setValues(ps, dumpOptions.getArgs(t));
						});
						long progressRecords = rows.addAndGet(effectedRow);
						if (progress != null) {
							progress.progress(catalog, schema, sql, args, progressRecords, totalRecords, dumpOptions);
						}
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
			tracker.await();
			if (progress != null) {
				progress.onEnd(catalog, schema, sql, args, dumpOptions);
			}
			return rows.get();
		} finally {
			sourceConnectionFactory.close(sourceConnection);
		}

	}

	/**
	 * Dumps all tables of the source catalog and schema in batches.
	 * 
	 * @param batchSize the count of rows read and written per batch
	 * @return the count of rows written for each table
	 */
	public long[] dump(String catalog, String schema, String[] excludedTables, int batchSize, DumpProgress progress,
			JdbcDumpOptions dumpOptions, DumpErrorHandler errorHandler) throws SQLException {
		List<Long> rows = new ArrayList<Long>();
		Connection sourceConnection = null;
		try {
			sourceConnection = sourceConnectionFactory.getConnection(catalog, schema);
			for (String tableName : tableNames(sourceConnection, catalog, schema, excludedTables)) {
				rows.add(dump(catalog, schema, tableName, batchSize, progress, dumpOptions, errorHandler));
			}
			return Longs.toArray(rows);
		} finally {
			sourceConnectionFactory.close(sourceConnection);
		}

	}

	/**
	 * Dumps one table in batches.
	 * 
	 * @param errorHandler handles the failure, null to rethrow it
	 * @return the count of rows written, 0 if the failure was handled
	 */
	public long dump(String catalog, String schema, String tableName, int batchSize, DumpProgress progress, JdbcDumpOptions dumpOptions,
			DumpErrorHandler errorHandler) throws SQLException {
		final String sql = "select * from " + tableName;
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

	/**
	 * Dumps the rows of a query page by page, writing every page as one batch.
	 * This method returns after all writes have finished.
	 * 
	 * @param batchSize the page size
	 * @return the count of rows written
	 * @throws JdbcDumpException if a write fails
	 */
	public long dump(String catalog, String schema, String sql, Object[] args, int batchSize, DumpProgress progress,
			JdbcDumpOptions dumpOptions) throws SQLException {
		AtomicLong rows = new AtomicLong();
		if (progress != null) {
			progress.onStart(catalog, schema, sql, args, dumpOptions);
		}

		ConnectionFactory connectionFactory = new InternalConnectionFactory(sourceConnectionFactory, catalog, schema);
		Connection countConnection = null;
		long totalRecords;
		try {
			countConnection = connectionFactory.getConnection();
			totalRecords = progress != null ? JdbcUtils.rowCount(countConnection, sql, args) : 0;
		} finally {
			connectionFactory.close(countConnection);
		}

		final TaskTracker tracker = new TaskTracker(dumpOptions.getExecutor());
		final Predicate<Tuple> predicate = dumpOptions.getPredicate() != null ? dumpOptions.getPredicate() : t -> true;
		JdbcUtils.batchScan(connectionFactory, sql, args, 1, batchSize, list -> {
			if (list == null || list.isEmpty()) {
				return;
			}
			tracker.submit(() -> {
				Connection destinationConnection = null;
				try {
					destinationConnection = destinationConnectionFactory.getConnection(dumpOptions.getCatalog(), dumpOptions.getSchema());
					int[] effectedRows = JdbcUtils.batchUpdate(destinationConnection, dumpOptions.getInsertionSql(ListUtils.getFirst(list)),
							ps -> {
								list.stream().filter(predicate).forEach(t -> {
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
		tracker.await();
		if (progress != null) {
			progress.onEnd(catalog, schema, sql, args, dumpOptions);
		}
		return rows.get();
	}

	/**
	 * Run tasks on the executor (or in the caller thread if the executor is
	 * null) and wait for all of them.
	 */
	private static class TaskTracker {

		private final Executor executor;
		private final List<CompletableFuture<Void>> futures = new ArrayList<CompletableFuture<Void>>();

		TaskTracker(Executor executor) {
			this.executor = executor;
		}

		void submit(Runnable task) {
			if (executor == null) {
				task.run();
			} else {
				futures.add(CompletableFuture.runAsync(task, executor));
			}
		}

		void await() {
			if (futures.isEmpty()) {
				return;
			}
			try {
				CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0])).join();
			} catch (CompletionException e) {
				Throwable cause = e.getCause();
				if (cause instanceof RuntimeException) {
					throw (RuntimeException) cause;
				} else if (cause instanceof Error) {
					throw (Error) cause;
				}
				throw new JdbcDumpException(cause);
			}
		}
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

	private static class TableJdbcDumpOptions implements JdbcDumpOptions {

		private static final String SQL_INSERTION = "insert into %s(%s) values (%s)";
		private final String tableName;
		private final JdbcDumpOptions delegate;

		TableJdbcDumpOptions(String tableName, JdbcDumpOptions dumpOptions) {
			this.tableName = tableName;
			this.delegate = dumpOptions;
		}

		@Override
		public String getCatalog() {
			return delegate.getCatalog();
		}

		@Override
		public String getSchema() {
			return delegate.getSchema();
		}

		@Override
		public Executor getExecutor() {
			return delegate.getExecutor();
		}

		@Override
		public long getMaxRecords() {
			return delegate.getMaxRecords();
		}

		@Override
		public String getInsertionSql(Tuple t) {
			String sql = delegate.getInsertionSql(t);
			if (StringUtils.isNotBlank(sql)) {
				return sql;
			}
			String[] columnNames = t.keys();
			return String.format(SQL_INSERTION, tableName, ArrayUtils.join(columnNames, ","),
					StringUtils.repeat("?", ",", columnNames.length));
		}

		@Override
		public Predicate<Tuple> getPredicate() {
			return delegate.getPredicate();
		}

		@Override
		public Object[] getArgs(Tuple t) {
			return delegate.getArgs(t);
		}

	}

}
