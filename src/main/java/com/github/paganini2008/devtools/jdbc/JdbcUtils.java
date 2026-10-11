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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import javax.sql.DataSource;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.CaseFormat;
import com.github.paganini2008.devtools.CaseFormats;
import com.github.paganini2008.devtools.Observable;
import com.github.paganini2008.devtools.Observer;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.Tuple;
import com.github.paganini2008.devtools.converter.ConvertUtils;

/**
 * JdbcUtils provides null-safe helpers to close resources and manage
 * transactions, plus methods to execute updates and queries, iterate results
 * with cursors, page through results and read database metadata.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class JdbcUtils {

	public static final String SQL_ROW_COUNT = "select count(*) as row_count from (%s) t";
	public static final String DDL_TRUNCATE_TABLE = "truncate table %s";

	/**
	 * Closes the connection, doing nothing if it is null.
	 * 
	 * @param connection the connection to close, may be null
	 * @throws SQLException if closing fails
	 */
	public static void close(Connection connection) throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}

	/**
	 * Closes the result set, doing nothing if it is null.
	 * 
	 * @param rs the result set to close, may be null
	 * @throws SQLException if closing fails
	 */
	public static void close(ResultSet rs) throws SQLException {
		if (rs != null) {
			rs.close();
		}
	}

	/**
	 * Closes the statement, doing nothing if it is null.
	 * 
	 * @param stmt the statement to close, may be null
	 * @throws SQLException if closing fails
	 */
	public static void close(Statement stmt) throws SQLException {
		if (stmt != null) {
			stmt.close();
		}
	}

	/**
	 * Closes the connection, ignoring null and any SQLException.
	 * 
	 * <pre>
	 * JdbcUtils.closeQuietly((Connection) null)  = no-op
	 * JdbcUtils.closeQuietly(closedConnection)   = no-op
	 * </pre>
	 * 
	 * @param connection the connection to close, may be null
	 */
	public static void closeQuietly(Connection connection) {
		try {
			close(connection);
		} catch (SQLException e) {
		}
	}

	/**
	 * Closes the result set, ignoring null and any SQLException.
	 * 
	 * @param rs the result set to close, may be null
	 */
	public static void closeQuietly(ResultSet rs) {
		try {
			close(rs);
		} catch (SQLException e) {
		}
	}

	/**
	 * Closes the statement, ignoring null and any SQLException.
	 * 
	 * @param stmt the statement to close, may be null
	 */
	public static void closeQuietly(Statement stmt) {
		try {
			close(stmt);
		} catch (SQLException e) {
		}
	}

	/**
	 * Commits the current transaction, doing nothing if the connection is null.
	 * 
	 * @param connection the connection, may be null
	 * @throws SQLException if committing fails
	 */
	public static void commit(Connection connection) throws SQLException {
		if (connection != null) {
			connection.commit();
		}
	}

	/**
	 * Commits the current transaction, ignoring null and any SQLException.
	 * 
	 * @param connection the connection, may be null
	 */
	public static void commitQuietly(Connection connection) {
		try {
			commit(connection);
		} catch (SQLException e) {
		}
	}

	/**
	 * Commits the current transaction and always closes the connection, doing
	 * nothing if the connection is null.
	 * 
	 * @param connection the connection, may be null
	 * @throws SQLException if committing or closing fails
	 */
	public static void commitAndClose(Connection connection) throws SQLException {
		if (connection != null) {
			try {
				connection.commit();
			} finally {
				connection.close();
			}
		}
	}

	/**
	 * Commits the current transaction and closes the connection, ignoring null
	 * and any SQLException.
	 * 
	 * @param connection the connection, may be null
	 */
	public static void commitAndCloseQuietly(Connection connection) {
		try {
			commitAndClose(connection);
		} catch (SQLException e) {
		}
	}

	/**
	 * Rolls back the current transaction, doing nothing if the connection is
	 * null.
	 * 
	 * @param connection the connection, may be null
	 * @throws SQLException if rolling back fails
	 */
	public static void rollback(Connection connection) throws SQLException {
		if (connection != null) {
			connection.rollback();
		}
	}

	/**
	 * Rolls back the current transaction, ignoring null and any SQLException.
	 * 
	 * @param connection the connection, may be null
	 */
	public static void rollbackQuietly(Connection connection) {
		try {
			rollback(connection);
		} catch (SQLException e) {
		}
	}

	/**
	 * Rolls back the current transaction and always closes the connection, doing
	 * nothing if the connection is null.
	 * 
	 * @param connection the connection, may be null
	 * @throws SQLException if rolling back or closing fails
	 */
	public static void rollbackAndClose(Connection connection) throws SQLException {
		if (connection != null) {
			try {
				connection.rollback();
			} finally {
				connection.close();
			}
		}
	}

	/**
	 * Rolls back the current transaction and closes the connection, ignoring
	 * null and any SQLException.
	 * 
	 * @param connection the connection, may be null
	 */
	public static void rollbackAndCloseQuietly(Connection connection) {
		try {
			rollbackAndClose(connection);
		} catch (SQLException e) {
		}
	}

	/**
	 * Switches the catalog and the schema of the connection. A blank catalog or
	 * schema is ignored, and nothing is changed if it is already current.
	 * 
	 * @param connection the connection
	 * @param catalog the catalog, may be blank
	 * @param schema the schema, may be blank
	 * @throws SQLException if switching fails
	 */
	public static void setPath(Connection connection, String catalog, String schema) throws SQLException {
		if (StringUtils.isNotBlank(catalog)) {
			if (StringUtils.notEquals(connection.getCatalog(), catalog)) {
				connection.setCatalog(catalog);
			}
		}
		if (StringUtils.isNotBlank(schema)) {
			if (StringUtils.notEquals(connection.getSchema(), schema)) {
				connection.setSchema(schema);
			}
		}
	}

	/**
	 * Disables auto-commit mode, doing nothing if the connection is null.
	 * 
	 * @param connection the connection, may be null
	 * @throws SQLException if the mode can not be changed
	 */
	public static void disableCommit(Connection connection) throws SQLException {
		if (connection != null) {
			connection.setAutoCommit(false);
		}
	}

	/**
	 * Disables auto-commit mode, ignoring null and any SQLException.
	 * 
	 * @param connection the connection, may be null
	 */
	public static void disableCommitQuietly(Connection connection) {
		try {
			disableCommit(connection);
		} catch (SQLException ignored) {
		}
	}

	/**
	 * Prints the exception and all its chained next exceptions to
	 * {@code System.err}.
	 * 
	 * @param e the exception, may be null
	 */
	public static void printStackTrace(SQLException e) {
		printStackTrace(e, new PrintWriter(System.err));
	}

	/**
	 * Prints the exception and all its chained next exceptions to the writer,
	 * then flushes the writer.
	 * 
	 * @param e the exception, may be null
	 * @param pw the writer
	 */
	public static void printStackTrace(SQLException e, PrintWriter pw) {
		SQLException next = e;
		while (next != null) {
			next.printStackTrace(pw);
			next = next.getNextException();
			if (next != null) {
				pw.println("Next SQLException:");
			}
		}
		pw.flush();
	}

	/**
	 * Prints the warnings of the connection to {@code System.err}.
	 * 
	 * @param connection the connection, may be null
	 */
	public static void printWarnings(Connection connection) {
		printWarnings(connection, new PrintWriter(System.err));
	}

	/**
	 * Prints the warnings of the connection to the writer, doing nothing if the
	 * connection is null. A failure to read the warnings is printed instead.
	 * 
	 * @param connection the connection, may be null
	 * @param pw the writer
	 */
	public static void printWarnings(Connection connection, PrintWriter pw) {
		if (connection != null) {
			try {
				printStackTrace(connection.getWarnings(), pw);
			} catch (SQLException e) {
				printStackTrace(e, pw);
			}
		}
	}

	/**
	 * Opens a new connection by {@link DriverManager}.
	 */
	public static Connection getConnection(String url, String user, String password) throws SQLException {
		return DriverManager.getConnection(url, user, password);
	}

	/**
	 * Executes a static insert, update, delete or DDL statement.
	 * 
	 * @return the count of affected rows
	 */
	public static int update(Connection connection, String sql) throws SQLException {
		Statement stm = null;
		try {
			stm = connection.createStatement();
			return stm.executeUpdate(sql);
		} finally {
			closeQuietly(stm);
		}
	}

	/**
	 * Executes the statement in a batch, one batch entry per non-empty
	 * argument array.
	 * 
	 * @param argsList the parameters of each entry, null or empty entries are skipped
	 * @return the update counts of the batch entries
	 */
	public static int[] batchUpdate(Connection connection, String sql, List<Object[]> argsList) throws SQLException {
		return batchUpdate(connection, sql, setValues(argsList));
	}

	/**
	 * Executes the statement in a batch. The callback sets the parameters and
	 * calls {@code addBatch()} for each entry.
	 * 
	 * @return the update counts of the batch entries
	 */
	public static int[] batchUpdate(Connection connection, String sql, PreparedStatementCallback callback) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(sql);
			if (callback != null) {
				callback.setValues(ps);
			}
			return ps.executeBatch();
		} finally {
			closeQuietly(ps);
		}
	}

	/**
	 * Executes an insert statement with the given parameters.
	 * 
	 * @return the generated key if it is numeric, otherwise 0
	 */
	public static int insert(Connection connection, String sql, Object[] args) throws SQLException {
		return insert(connection, sql, setValues(args));
	}

	/**
	 * Executes an insert statement whose parameters are set by the callback.
	 * 
	 * @return the generated key if it is numeric, otherwise 0
	 */
	public static int insert(Connection connection, String sql, PreparedStatementCallback callback) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			if (callback != null) {
				callback.setValues(ps);
			}
			int effected = ps.executeUpdate();
			if (effected > 0) {
				rs = ps.getGeneratedKeys();
				if (rs != null && rs.next()) {
					Object result = rs.getObject(1);
					if (result instanceof Number) {
						return ((Number) result).intValue();
					}
				}
			}
			return 0;
		} finally {
			closeQuietly(rs);
			closeQuietly(ps);
		}
	}

	/**
	 * Executes an insert, update or delete statement with the given parameters.
	 * 
	 * @return the count of affected rows
	 */
	public static int update(Connection connection, String sql, Object[] args) throws SQLException {
		return update(connection, sql, setValues(args));
	}

	/**
	 * Executes an insert, update or delete statement whose parameters are set by
	 * the callback.
	 * 
	 * @return the count of affected rows
	 */
	public static int update(Connection connection, String sql, PreparedStatementCallback callback) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(sql);
			if (callback != null) {
				callback.setValues(ps);
			}
			return ps.executeUpdate();
		} finally {
			closeQuietly(ps);
		}
	}

	/**
	 * Returns the first column of the first row converted to the required type.
	 * 
	 * @return the value, or null if there is no row
	 */
	public static <T> T fetchOne(Connection connection, String sql, Class<T> requiredType) throws SQLException {
		Statement sm = null;
		ResultSet rs = null;
		try {
			sm = connection.createStatement();
			rs = sm.executeQuery(sql);
			return firstValue(rs, requiredType);
		} finally {
			closeQuietly(rs);
			closeQuietly(sm);
		}
	}

	private static <T> T firstValue(ResultSet rs, Class<T> requiredType) throws SQLException {
		if (rs != null && rs.next() && rs.getMetaData().getColumnCount() > 0) {
			return ConvertUtils.convertValue(rs.getObject(1), requiredType);
		}
		return null;
	}

	/**
	 * Returns the first row as a {@link Tuple} keyed by column labels.
	 * 
	 * @return the first row, or null if there is no row
	 */
	public static Tuple fetchOne(Connection connection, String sql) throws SQLException {
		Statement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.createStatement();
			rs = ps.executeQuery(sql);
			if (rs != null && rs.next()) {
				return toTuple(rs);
			}
			return null;
		} finally {
			closeQuietly(rs);
			closeQuietly(ps);
		}
	}

	/**
	 * Returns all rows as {@link Tuple}s.
	 * 
	 * @return the rows, never null
	 */
	public static List<Tuple> fetchAll(Connection connection, String sql) throws SQLException {
		List<Tuple> list = new ArrayList<Tuple>();
		Statement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.createStatement();
			rs = ps.executeQuery(sql);
			if (rs != null) {
				while (rs.next()) {
					list.add(toTuple(rs));
				}
			}
			return list;
		} finally {
			closeQuietly(rs);
			closeQuietly(ps);
		}
	}

	/**
	 * Opens a cursor over the rows of the query. The cursor owns the given
	 * connection: the result set, the statement and the connection are closed
	 * when the rows are exhausted or the cursor is closed. If the query fails,
	 * only the statement is closed.
	 * 
	 * @return the cursor
	 */
	public static Cursor<Tuple> cursor(Connection connection, String sql) throws SQLException {
		Statement sm = null;
		ResultSet rs = null;
		try {
			sm = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = sm.executeQuery(sql);
			return newCursor(rs, sm, connection);
		} catch (SQLException | RuntimeException e) {
			closeQuietly(rs);
			closeQuietly(sm);
			throw e;
		}
	}

	/**
	 * Returns the first column of the first row converted to the required type.
	 * 
	 * @return the value, or null if there is no row
	 */
	public static <T> T fetchOne(Connection connection, String sql, Object[] args, Class<T> requiredType) throws SQLException {
		return fetchOne(connection, sql, setValues(args), requiredType);
	}

	/**
	 * Returns the first column of the first row converted to the required type.
	 * 
	 * @return the value, or null if there is no row
	 */
	public static <T> T fetchOne(Connection connection, String sql, PreparedStatementCallback callback, Class<T> requiredType)
			throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(sql);
			if (callback != null) {
				callback.setValues(ps);
			}
			rs = ps.executeQuery();
			return firstValue(rs, requiredType);
		} finally {
			closeQuietly(rs);
			closeQuietly(ps);
		}
	}

	/**
	 * Returns the first row as a {@link Tuple}.
	 * 
	 * @return the first row, or null if there is no row
	 */
	public static Tuple fetchOne(Connection connection, String sql, Object[] args) throws SQLException {
		return fetchOne(connection, sql, setValues(args));
	}

	/**
	 * Returns the first row as a {@link Tuple}.
	 * 
	 * @return the first row, or null if there is no row
	 */
	public static Tuple fetchOne(Connection connection, String sql, PreparedStatementCallback callback) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(sql);
			if (callback != null) {
				callback.setValues(ps);
			}
			rs = ps.executeQuery();
			if (rs != null && rs.next()) {
				return toTuple(rs);
			}
			return null;
		} finally {
			closeQuietly(rs);
			closeQuietly(ps);
		}
	}

	/**
	 * Returns all rows as {@link Tuple}s.
	 * 
	 * @return the rows, never null
	 */
	public static List<Tuple> fetchAll(Connection connection, String sql, Object[] args) throws SQLException {
		return fetchAll(connection, sql, setValues(args));
	}

	/**
	 * Returns all rows as {@link Tuple}s.
	 * 
	 * @return the rows, never null
	 */
	public static List<Tuple> fetchAll(Connection connection, String sql, PreparedStatementCallback callback) throws SQLException {
		List<Tuple> list = new ArrayList<Tuple>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(sql);
			if (callback != null) {
				callback.setValues(ps);
			}
			rs = ps.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					list.add(toTuple(rs));
				}
			}
			return list;
		} finally {
			closeQuietly(rs);
			closeQuietly(ps);
		}
	}

	/**
	 * Opens a cursor over the rows of the query.
	 * 
	 * @see #cursor(Connection, String)
	 */
	public static Cursor<Tuple> cursor(Connection connection, String sql, Object[] args) throws SQLException {
		return cursor(connection, sql, setValues(args));
	}

	/**
	 * Opens a cursor over the rows of the query.
	 * 
	 * @see #cursor(Connection, String)
	 */
	public static Cursor<Tuple> cursor(Connection connection, String sql, PreparedStatementCallback callback) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			if (callback != null) {
				callback.setValues(ps);
			}
			rs = ps.executeQuery();
			return newCursor(rs, ps, connection);
		} catch (SQLException | RuntimeException e) {
			closeQuietly(rs);
			closeQuietly(ps);
			throw e;
		}
	}

	private static Cursor<Tuple> newCursor(ResultSet rs, Statement sm, Connection connection) {
		Observable observable = Observable.unrepeatable();
		closeLazily(observable, rs, sm, connection);
		return new CursorImpl(rs, CaseFormats.LOWER_CAMEL, observable);
	}

	private static void closeLazily(Observable observable, final ResultSet rs, final Statement sm, final Connection connection) {
		observable.addObserver(new Observer() {
			public void update(Observable ob, Object arg) {
				closeQuietly(rs);
				closeQuietly(sm);
				closeQuietly(connection);
			}
		});
	}

	/**
	 * Converts the current row to a {@link Tuple} with lower camel keys.
	 */
	public static Tuple toTuple(ResultSet rs) throws SQLException {
		return toTuple(rs, CaseFormats.LOWER_CAMEL);
	}

	/**
	 * Converts the current row to a {@link Tuple} keyed by the column labels
	 * converted with the case format.
	 */
	public static Tuple toTuple(ResultSet rs, CaseFormat caseFormat) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		Tuple tuple = Tuple.newTuple(caseFormat);
		for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
			String columnName = rsmd.getColumnLabel(columnIndex);
			Object value = rs.getObject(columnIndex);
			tuple.set(columnName, value);
		}
		return tuple;
	}

	/**
	 * 
	 * CursorImpl
	 *
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	static class CursorImpl implements Cursor<Tuple> {

		private final ResultSet rs;
		private final CaseFormat caseFormat;
		private final Observable observable;
		private final AtomicBoolean opened;
		private boolean fetched;
		private boolean hasRow;

		CursorImpl(ResultSet rs, CaseFormat caseFormat, Observable observable) {
			this.rs = rs;
			this.caseFormat = caseFormat;
			this.observable = observable;
			this.opened = new AtomicBoolean(true);
		}

		public boolean hasNext() {
			if (!isOpened()) {
				return false;
			}
			if (!fetched) {
				try {
					hasRow = rs.next();
					fetched = true;
				} catch (SQLException e) {
					close();
					throw new DetachedSqlException(e.getMessage(), e);
				}
				if (!hasRow) {
					close();
				}
			}
			return hasRow;
		}

		public Tuple next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			fetched = false;
			try {
				return toTuple(rs, caseFormat);
			} catch (SQLException e) {
				close();
				throw new DetachedSqlException(e.getMessage(), e);
			}
		}

		public int getRownum() {
			try {
				return rs.getRow();
			} catch (SQLException e) {
				throw new DetachedSqlException(e.getMessage(), e);
			}
		}

		public void mark(int rownum) {
			try {
				rs.absolute(rownum);
				fetched = false;
			} catch (SQLException e) {
				throw new DetachedSqlException(e.getMessage(), e);
			}
		}

		public boolean isOpened() {
			return opened.get();
		}

		public void close() {
			if (opened.compareAndSet(true, false)) {
				observable.notifyObservers();
			}
		}
	}

	/**
	 * Passes the rows to the consumer one by one. The connection is closed
	 * afterwards.
	 * 
	 * @param maxRecords the max count of rows to consume, non-positive means no limit
	 */
	public static void scan(Connection connection, String sql, Object[] args, Consumer<Tuple> consumer, long maxRecords)
			throws SQLException {
		scan(connection, sql, setValues(args), consumer, maxRecords);
	}

	/**
	 * Passes the rows to the consumer one by one. The connection is closed
	 * afterwards, even if the consumer fails.
	 * 
	 * @param maxRecords the max count of rows to consume, non-positive means no limit
	 */
	public static void scan(Connection connection, String sql, PreparedStatementCallback callback, Consumer<Tuple> consumer,
			long maxRecords) throws SQLException {
		Cursor<Tuple> cursor = cursor(connection, sql, callback);
		try {
			long count = 0;
			while (cursor.hasNext()) {
				consumer.accept(cursor.next());
				if (maxRecords > 0 && ++count >= maxRecords) {
					break;
				}
			}
		} finally {
			cursor.close();
		}
	}

	/**
	 * Passes the rows to the consumer page by page, starting from the given
	 * page.
	 * 
	 * @see #batchScan(ConnectionFactory, PageableSql, PreparedStatementCallback, int, int, Consumer, long)
	 */
	public static void batchScan(DataSource dataSource, String sql, Object[] args, int page, int pageSize, Consumer<List<Tuple>> consumer,
			long maxRecords) throws SQLException {
		batchScan(new PooledConnectionFactory(dataSource), sql, args, page, pageSize, consumer, maxRecords);
	}

	/**
	 * Passes the rows to the consumer page by page, starting from the given
	 * page.
	 * 
	 * @see #batchScan(ConnectionFactory, PageableSql, PreparedStatementCallback, int, int, Consumer, long)
	 */
	public static void batchScan(DataSource dataSource, String sql, PreparedStatementCallback callback, int page, int pageSize,
			Consumer<List<Tuple>> consumer, long maxRecords) throws SQLException {
		batchScan(new PooledConnectionFactory(dataSource), sql, callback, page, pageSize, consumer, maxRecords);
	}

	/**
	 * Passes the rows to the consumer page by page, starting from the given
	 * page.
	 * 
	 * @see #batchScan(ConnectionFactory, PageableSql, PreparedStatementCallback, int, int, Consumer, long)
	 */
	public static void batchScan(ConnectionFactory connectionFactory, String sql, Object[] args, int page, int pageSize,
			Consumer<List<Tuple>> consumer, long maxRecords) throws SQLException {
		batchScan(connectionFactory, new DefaultPageableSql(sql), args, page, pageSize, consumer, maxRecords);
	}

	/**
	 * Passes the rows to the consumer page by page, starting from the given
	 * page.
	 * 
	 * @see #batchScan(ConnectionFactory, PageableSql, PreparedStatementCallback, int, int, Consumer, long)
	 */
	public static void batchScan(ConnectionFactory connectionFactory, PageableSql pageableSql, Object[] args, int page, int pageSize,
			Consumer<List<Tuple>> consumer, long maxRecords) throws SQLException {
		batchScan(connectionFactory, pageableSql, setValues(args), page, pageSize, consumer, maxRecords);
	}

	/**
	 * Passes the rows to the consumer page by page, starting from the given
	 * page.
	 * 
	 * @see #batchScan(ConnectionFactory, PageableSql, PreparedStatementCallback, int, int, Consumer, long)
	 */
	public static void batchScan(ConnectionFactory connectionFactory, String sql, PreparedStatementCallback callback, int page,
			int pageSize, Consumer<List<Tuple>> consumer, long maxRecords) throws SQLException {
		batchScan(connectionFactory, new DefaultPageableSql(sql), callback, page, pageSize, consumer, maxRecords);
	}

	/**
	 * Passes the rows to the consumer page by page, starting from the given
	 * page. Every page is fetched with a new connection.
	 * 
	 * @param page the 1-based page to start from
	 * @param pageSize the page size
	 * @param maxRecords stops after the page which reaches this count of rows
	 *            (counted as {@code pageSize * pageNumber}), non-positive means
	 *            no limit
	 */
	public static void batchScan(ConnectionFactory connectionFactory, PageableSql pageableSql, PreparedStatementCallback callback, int page,
			int pageSize, Consumer<List<Tuple>> consumer, long maxRecords) throws SQLException {
		PageableQuery<Tuple> query = pageableQuery(connectionFactory, pageableSql, callback);
		for (PageResponse<Tuple> pageResponse : query.forEach(page, pageSize)) {
			consumer.accept(pageResponse.getContent());
			if (maxRecords > 0 && (long) pageResponse.getPageSize() * pageResponse.getPageNumber() >= maxRecords) {
				break;
			}
		}
	}

	/**
	 * Creates a {@link PageableQuery} using {@link DefaultPageableSql}.
	 */
	public static PageableQuery<Tuple> pageableQuery(DataSource dataSource, String sql, Object[] args) {
		return pageableQuery(dataSource, sql, setValues(args));
	}

	/**
	 * Creates a {@link PageableQuery} using {@link DefaultPageableSql}.
	 */
	public static PageableQuery<Tuple> pageableQuery(DataSource dataSource, String sql, PreparedStatementCallback callback) {
		return pageableQuery(dataSource, new DefaultPageableSql(sql), callback);
	}

	/**
	 * Creates a {@link PageableQuery} on connections of the data source.
	 */
	public static PageableQuery<Tuple> pageableQuery(DataSource dataSource, PageableSql pageableSql, Object[] args) {
		return new PageableQueryImpl(new PooledConnectionFactory(dataSource), pageableSql, setValues(args));
	}

	/**
	 * Creates a {@link PageableQuery} on connections of the data source.
	 */
	public static PageableQuery<Tuple> pageableQuery(DataSource dataSource, PageableSql pageableSql, PreparedStatementCallback callback) {
		return new PageableQueryImpl(new PooledConnectionFactory(dataSource), pageableSql, callback);
	}

	/**
	 * Creates a {@link PageableQuery} using {@link DefaultPageableSql}.
	 */
	public static PageableQuery<Tuple> pageableQuery(ConnectionFactory connectionFactory, String sql, Object[] args) {
		return pageableQuery(connectionFactory, sql, setValues(args));
	}

	/**
	 * Creates a {@link PageableQuery} using {@link DefaultPageableSql}.
	 */
	public static PageableQuery<Tuple> pageableQuery(ConnectionFactory connectionFactory, String sql, PreparedStatementCallback callback) {
		return pageableQuery(connectionFactory, new DefaultPageableSql(sql), callback);
	}

	/**
	 * Creates a {@link PageableQuery} on connections of the factory.
	 */
	public static PageableQuery<Tuple> pageableQuery(ConnectionFactory connectionFactory, PageableSql pageableSql, Object[] args) {
		return pageableQuery(connectionFactory, pageableSql, setValues(args));
	}

	/**
	 * Creates a {@link PageableQuery} on connections of the factory.
	 */
	public static PageableQuery<Tuple> pageableQuery(ConnectionFactory connectionFactory, PageableSql pageableSql,
			PreparedStatementCallback callback) {
		return new PageableQueryImpl(connectionFactory, pageableSql, callback);
	}

	/**
	 * Sets the arguments as the parameters of the statement in order, doing
	 * nothing if the arguments are null or empty.
	 */
	public static void setValues(PreparedStatement ps, Object[] args) throws SQLException {
		if (args != null && args.length > 0) {
			int parameterIndex = 1;
			for (Object arg : args) {
				ps.setObject(parameterIndex++, arg);
			}
		}
	}

	private static PreparedStatementCallback setValues(List<Object[]> argsList) {
		return ps -> {
			for (Object[] args : argsList) {
				if (args != null && args.length > 0) {
					setValues(ps, args);
					ps.addBatch();
				}
			}
		};
	}

	private static PreparedStatementCallback setValues(Object[] args) {
		return ps -> {
			setValues(ps, args);
		};
	}

	/**
	 * Counts the rows the query returns.
	 * 
	 * @return the count of rows
	 */
	public static long rowCount(Connection connection, String sql) throws SQLException {
		return rowCount(connection, sql, ArrayUtils.EMPTY_OBJECT_ARRAY);
	}

	/**
	 * Counts the rows the query returns.
	 * 
	 * @return the count of rows
	 */
	public static long rowCount(Connection connection, String sql, Object[] args) throws SQLException {
		return rowCount(connection, sql, setValues(args));
	}

	/**
	 * Counts the rows the query returns.
	 * 
	 * @return the count of rows
	 */
	public static long rowCount(Connection connection, String sql, PreparedStatementCallback callback) throws SQLException {
		Tuple tuple = fetchOne(connection, String.format(SQL_ROW_COUNT, sql), callback);
		return tuple != null ? ((Number) tuple.get("rowCount")).longValue() : 0L;
	}

	/**
	 * Truncates the table.
	 */
	public static void truncateTable(Connection connection, String tableName) throws SQLException {
		update(connection, String.format(DDL_TRUNCATE_TABLE, tableName));
	}

	/**
	 * Checks whether the table exists in the schema (the table name is
	 * compared ignoring case).
	 */
	public static boolean existsTable(Connection connection, String schema, String tableName) throws SQLException {
		return existsTable(connection, null, schema, tableName);
	}

	/**
	 * Checks whether the table exists in the catalog and schema (the table name
	 * is compared ignoring case).
	 */
	public static boolean existsTable(Connection connection, String catalog, String schema, String tableName) throws SQLException {
		ResultSet rs = null;
		try {
			rs = connection.getMetaData().getTables(catalog, schema, tableName, new String[] { "TABLE" });
			// the table name is a pattern ('_' and '%' are wildcards), so check every matched table
			while (rs != null && rs.next()) {
				if (tableName.equalsIgnoreCase(rs.getString("TABLE_NAME"))) {
					return true;
				}
			}
		} finally {
			closeQuietly(rs);
		}
		return false;
	}

	/**
	 * Opens a cursor over the metadata of all tables. Unlike
	 * {@link #cursor(Connection, String)}, the connection is not closed.
	 */
	public static Cursor<Tuple> describe(Connection connection) throws SQLException {
		return describe(connection, null, null);
	}

	/**
	 * Opens a cursor over the metadata of the tables in the catalog and schema,
	 * null meaning any. The connection is not closed.
	 */
	public static Cursor<Tuple> describe(Connection connection, String catalog, String schema) throws SQLException {
		ResultSet rs = connection.getMetaData().getTables(catalog, schema, "%", new String[] { "TABLE" });
		return newCursor(rs, null, null);
	}

	/**
	 * Opens a cursor over the column metadata of the table. The connection is
	 * not closed.
	 */
	public static Cursor<Tuple> getTableMetadata(Connection connection, String tableName) throws SQLException {
		return getTableMetadata(connection, null, null, tableName);
	}

	/**
	 * Opens a cursor over the column metadata of the table. The connection is
	 * not closed.
	 */
	public static Cursor<Tuple> getTableMetadata(Connection connection, String catalog, String schema, String tableName)
			throws SQLException {
		ResultSet rs = connection.getMetaData().getColumns(catalog, schema, tableName, null);
		return newCursor(rs, null, null);
	}

	/**
	 * Opens a cursor over the primary key columns of the table. The connection
	 * is not closed.
	 */
	public static Cursor<Tuple> getPkMetadata(Connection connection, String tableName) throws SQLException {
		return getPkMetadata(connection, null, null, tableName);
	}

	/**
	 * Opens a cursor over the primary key columns of the table. The connection
	 * is not closed.
	 */
	public static Cursor<Tuple> getPkMetadata(Connection connection, String catalog, String schema, String tableName) throws SQLException {
		ResultSet rs = connection.getMetaData().getPrimaryKeys(catalog, schema, tableName);
		return newCursor(rs, null, null);
	}

	/**
	 * Opens a cursor over the foreign key columns of the table (the imported
	 * keys). The connection is not closed.
	 */
	public static Cursor<Tuple> getFkMetadata(Connection connection, String tableName) throws SQLException {
		return getFkMetadata(connection, null, null, tableName);
	}

	/**
	 * Opens a cursor over the foreign key columns of the table (the imported
	 * keys). The connection is not closed.
	 */
	public static Cursor<Tuple> getFkMetadata(Connection connection, String catalog, String schema, String tableName) throws SQLException {
		ResultSet rs = connection.getMetaData().getImportedKeys(catalog, schema, tableName);
		return newCursor(rs, null, null);
	}

}
