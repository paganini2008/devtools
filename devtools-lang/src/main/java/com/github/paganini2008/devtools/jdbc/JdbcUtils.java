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
 * JdbcUtils
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public abstract class JdbcUtils {

	public static final String SQL_ROW_COUNT = "select count(*) as rowCount from (%s) as t";

	public static void close(Connection connection) throws SQLException {
		if (connection != null) {
			connection.close();
		}
	}

	public static void close(ResultSet rs) throws SQLException {
		if (rs != null) {
			rs.close();
		}
	}

	public static void close(Statement stmt) throws SQLException {
		if (stmt != null) {
			stmt.close();
		}
	}

	public static void closeQuietly(Connection connection) {
		try {
			close(connection);
		} catch (SQLException e) {
		}
	}

	public static void closeQuietly(ResultSet rs) {
		try {
			close(rs);
		} catch (SQLException e) {
		}
	}

	public static void closeQuietly(Statement stmt) {
		try {
			close(stmt);
		} catch (SQLException e) {
		}
	}

	public static void commit(Connection connection) throws SQLException {
		if (connection != null) {
			connection.commit();
		}
	}

	public static void commitQuietly(Connection connection) {
		try {
			commit(connection);
		} catch (SQLException e) {
		}
	}

	public static void commitAndClose(Connection connection) throws SQLException {
		if (connection != null) {
			try {
				connection.commit();
			} finally {
				connection.close();
			}
		}
	}

	public static void commitAndCloseQuietly(Connection connection) {
		try {
			commitAndClose(connection);
		} catch (SQLException e) {
		}
	}

	public static void rollback(Connection connection) throws SQLException {
		if (connection != null) {
			connection.rollback();
		}
	}

	public static void rollbackQuietly(Connection connection) {
		try {
			rollback(connection);
		} catch (SQLException e) {
		}
	}

	public static void rollbackAndClose(Connection connection) throws SQLException {
		if (connection != null) {
			try {
				connection.rollback();
			} finally {
				connection.close();
			}
		}
	}

	public static void rollbackAndCloseQuietly(Connection connection) {
		try {
			rollbackAndClose(connection);
		} catch (SQLException e) {
		}
	}

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

	public static void printStackTrace(SQLException e) {
		printStackTrace(e, new PrintWriter(System.err));
	}

	public static void printStackTrace(SQLException e, PrintWriter pw) {
		SQLException next = e;
		while (next != null) {
			next.printStackTrace(pw);
			next = next.getNextException();
			if (next != null) {
				pw.println("Next SQLException:");
			}
		}
	}

	public static void printWarnings(Connection connection) {
		printWarnings(connection, new PrintWriter(System.err));
	}

	public static void printWarnings(Connection connection, PrintWriter pw) {
		if (connection != null) {
			try {
				printStackTrace(connection.getWarnings(), pw);
			} catch (SQLException e) {
				printStackTrace(e, pw);
			}
		}
	}

	public static Connection getConnection(String url, String user, String password) throws SQLException {
		return DriverManager.getConnection(url, user, password);
	}

	public static int update(Connection connection, String sql) throws SQLException {
		Statement stm = null;
		try {
			stm = connection.createStatement();
			return stm.executeUpdate(sql);
		} finally {
			closeQuietly(stm);
		}
	}

	public static int[] batchUpdate(Connection connection, String sql, List<Object[]> argsList) throws SQLException {
		return batchUpdate(connection, sql, setValues(argsList));
	}

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

	public static int insert(Connection connection, String sql, Object[] args) throws SQLException {
		return insert(connection, sql, setValues(args));
	}

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

	public static int update(Connection connection, String sql, Object[] args) throws SQLException {
		return update(connection, sql, setValues(args));
	}

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

	public static <T> T fetchOne(Connection connection, String sql, Class<T> requiredType) throws SQLException {
		Tuple tuple = fetchOne(connection, sql);
		if (tuple.isEmpty()) {
			return null;
		}
		return ConvertUtils.convertValue(tuple.toValues()[0], requiredType);
	}

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

	public static Cursor<Tuple> cursor(Connection connection, String sql) throws SQLException {
		Statement sm = null;
		ResultSet rs = null;
		Observable observable = Observable.unrepeatable();
		try {
			sm = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = sm.executeQuery(sql);
			return new CursorImpl(rs, CaseFormats.LOWER_CAMEL, observable);
		} finally {
			closeLazily(observable, rs, sm, connection);
		}
	}

	public static <T> T fetchOne(Connection connection, String sql, Object[] args, Class<T> requiredType) throws SQLException {
		return fetchOne(connection, sql, setValues(args), requiredType);
	}

	public static <T> T fetchOne(Connection connection, String sql, PreparedStatementCallback callback, Class<T> requiredType)
			throws SQLException {
		Tuple tuple = fetchOne(connection, sql, callback);
		if (tuple == null || tuple.isEmpty()) {
			return null;
		}
		return ConvertUtils.convertValue(tuple.toValues()[0], requiredType);
	}

	public static Tuple fetchOne(Connection connection, String sql, Object[] args) throws SQLException {
		return fetchOne(connection, sql, setValues(args));
	}

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

	public static List<Tuple> fetchAll(Connection connection, String sql, Object[] args) throws SQLException {
		return fetchAll(connection, sql, setValues(args));
	}

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

	public static Cursor<Tuple> cursor(Connection connection, String sql, Object[] args) throws SQLException {
		return cursor(connection, sql, setValues(args));
	}

	public static Cursor<Tuple> cursor(Connection connection, String sql, PreparedStatementCallback callback) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		final Observable observable = Observable.unrepeatable();
		try {
			ps = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			if (callback != null) {
				callback.setValues(ps);
			}
			rs = ps.executeQuery();
			return new CursorImpl(rs, CaseFormats.LOWER_CAMEL, observable);
		} finally {
			closeLazily(observable, rs, ps, connection);
		}
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

	public static Tuple toTuple(ResultSet rs) throws SQLException {
		return toTuple(rs, CaseFormats.LOWER_CAMEL);
	}

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

		CursorImpl(ResultSet rs, CaseFormat caseFormat, Observable observable) {
			this.rs = rs;
			this.caseFormat = caseFormat;
			this.observable = observable;
			this.opened = new AtomicBoolean(true);
		}

		public boolean hasNext() {
			try {
				opened.set(rs.next());
				return opened.get();
			} catch (SQLException e) {
				opened.set(false);
				throw new DetachedSqlException(e.getMessage(), e);
			} finally {
				if (!isOpened()) {
					close();
				}
			}
		}

		public Tuple next() {
			try {
				return toTuple(rs, caseFormat);
			} catch (SQLException e) {
				opened.set(false);
				throw new DetachedSqlException(e.getMessage(), e);
			} finally {
				if (!isOpened()) {
					close();
				}
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
			} catch (SQLException e) {
				throw new DetachedSqlException(e.getMessage(), e);
			}
		}

		public boolean isOpened() {
			return opened.get();
		}

		public void close() {
			observable.notifyObservers();
		}
	}

	public static void scan(Connection connection, String sql, Object[] args, Consumer<Tuple> consumer, long maxRecords)
			throws SQLException {
		scan(connection, sql, setValues(args), consumer, maxRecords);
	}

	public static void scan(Connection connection, String sql, PreparedStatementCallback callback, Consumer<Tuple> consumer,
			long maxRecords) throws SQLException {
		Cursor<Tuple> cursor = cursor(connection, sql, callback);
		while (cursor.hasNext()) {
			consumer.accept(cursor.next());
			if (maxRecords > 0 && cursor.getRownum() > maxRecords) {
				cursor.close();
				break;
			}
		}
	}

	public static void batchScan(DataSource dataSource, String sql, Object[] args, int page, int pageSize, Consumer<List<Tuple>> consumer,
			long maxRecords) throws SQLException {
		batchScan(new PooledConnectionFactory(dataSource), sql, args, page, pageSize, consumer, maxRecords);
	}

	public static void batchScan(DataSource dataSource, String sql, PreparedStatementCallback callback, int page, int pageSize,
			Consumer<List<Tuple>> consumer, long maxRecords) throws SQLException {
		batchScan(new PooledConnectionFactory(dataSource), sql, callback, page, pageSize, consumer, maxRecords);
	}

	public static void batchScan(ConnectionFactory connectionFactory, String sql, Object[] args, int page, int pageSize,
			Consumer<List<Tuple>> consumer, long maxRecords) throws SQLException {
		batchScan(connectionFactory, new DefaultPageableSql(sql), args, page, pageSize, consumer, maxRecords);
	}

	public static void batchScan(ConnectionFactory connectionFactory, PageableSql pageableSql, Object[] args, int page, int pageSize,
			Consumer<List<Tuple>> consumer, long maxRecords) throws SQLException {
		batchScan(connectionFactory, pageableSql, setValues(args), page, pageSize, consumer, maxRecords);
	}

	public static void batchScan(ConnectionFactory connectionFactory, String sql, PreparedStatementCallback callback, int page,
			int pageSize, Consumer<List<Tuple>> consumer, long maxRecords) throws SQLException {
		batchScan(connectionFactory, new DefaultPageableSql(sql), callback, page, pageSize, consumer, maxRecords);
	}

	public static void batchScan(ConnectionFactory connectionFactory, PageableSql pageableSql, PreparedStatementCallback callback, int page,
			int pageSize, Consumer<List<Tuple>> consumer, long maxRecords) throws SQLException {
		PageableQuery<Tuple> query = pageableQuery(connectionFactory, pageableSql, callback);
		for (PageResponse<Tuple> pageResponse : query.forEach(page, pageSize)) {
			consumer.accept(pageResponse.getContent());
			if (maxRecords > 0 && pageResponse.getPageSize() * pageResponse.getPageNumber() > maxRecords) {
				break;
			}
		}
	}

	public static PageableQuery<Tuple> pageableQuery(DataSource dataSource, String sql, Object[] args) {
		return pageableQuery(dataSource, sql, setValues(args));
	}

	public static PageableQuery<Tuple> pageableQuery(DataSource dataSource, String sql, PreparedStatementCallback callback) {
		return pageableQuery(dataSource, new DefaultPageableSql(sql), callback);
	}

	public static PageableQuery<Tuple> pageableQuery(DataSource dataSource, PageableSql pageableSql, Object[] args) {
		return new PageableQueryImpl(new PooledConnectionFactory(dataSource), pageableSql, setValues(args));
	}

	public static PageableQuery<Tuple> pageableQuery(DataSource dataSource, PageableSql pageableSql, PreparedStatementCallback callback) {
		return new PageableQueryImpl(new PooledConnectionFactory(dataSource), pageableSql, callback);
	}

	public static PageableQuery<Tuple> pageableQuery(ConnectionFactory connectionFactory, String sql, Object[] args) {
		return pageableQuery(connectionFactory, sql, setValues(args));
	}

	public static PageableQuery<Tuple> pageableQuery(ConnectionFactory connectionFactory, String sql, PreparedStatementCallback callback) {
		return pageableQuery(connectionFactory, new DefaultPageableSql(sql), callback);
	}

	public static PageableQuery<Tuple> pageableQuery(ConnectionFactory connectionFactory, PageableSql pageableSql, Object[] args) {
		return pageableQuery(connectionFactory, pageableSql, setValues(args));
	}

	public static PageableQuery<Tuple> pageableQuery(ConnectionFactory connectionFactory, PageableSql pageableSql,
			PreparedStatementCallback callback) {
		return new PageableQueryImpl(connectionFactory, pageableSql, callback);
	}

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

	public static long rowCount(Connection connection, String sql) throws SQLException {
		return rowCount(connection, sql, ArrayUtils.EMPTY_OBJECT_ARRAY);
	}

	public static long rowCount(Connection connection, String sql, Object[] args) throws SQLException {
		return rowCount(connection, sql, setValues(args));
	}

	public static long rowCount(Connection connection, String sql, PreparedStatementCallback callback) throws SQLException {
		Tuple tuple = fetchOne(connection, String.format(SQL_ROW_COUNT, sql), callback);
		return tuple != null ? ((Number) tuple.get("rowCount")).longValue() : 0L;
	}

	public static boolean existsTable(Connection connection, String schema, String tableName) throws SQLException {
		return existsTable(connection, null, schema, tableName);
	}

	public static boolean existsTable(Connection connection, String catalog, String schema, String tableName) throws SQLException {
		ResultSet rs = null;
		try {
			rs = connection.getMetaData().getTables(catalog, schema, tableName, new String[] { "TABLE" });
			if (rs != null && rs.next()) {
				return (tableName.equalsIgnoreCase(rs.getString("TABLE_NAME")));
			}
		} finally {
			closeQuietly(rs);
		}
		return false;
	}

	public static Cursor<Tuple> describe(Connection connection) throws SQLException {
		return describe(connection, null, null);
	}

	public static Cursor<Tuple> describe(Connection connection, String catalog, String schema) throws SQLException {
		ResultSet rs = null;
		Observable observable = Observable.unrepeatable();
		try {
			rs = connection.getMetaData().getTables(catalog, schema, "%", new String[] { "TABLE" });
			return new CursorImpl(rs, CaseFormats.LOWER_CAMEL, observable);
		} finally {
			closeLazily(observable, rs, null, null);
		}
	}

	public static Cursor<Tuple> getTableMetadata(Connection connection, String tableName) throws SQLException {
		return getTableMetadata(connection, null, null, tableName);
	}

	public static Cursor<Tuple> getTableMetadata(Connection connection, String catalog, String schema, String tableName)
			throws SQLException {
		ResultSet rs = null;
		Observable observable = Observable.unrepeatable();
		try {
			rs = connection.getMetaData().getColumns(catalog, schema, tableName, null);
			return new CursorImpl(rs, CaseFormats.LOWER_CAMEL, observable);
		} finally {
			closeLazily(observable, rs, null, null);
		}
	}

	public static Cursor<Tuple> getPkMetadata(Connection connection, String tableName) throws SQLException {
		return getPkMetadata(connection, null, null, tableName);
	}

	public static Cursor<Tuple> getPkMetadata(Connection connection, String catalog, String schema, String tableName) throws SQLException {
		ResultSet rs = null;
		Observable observable = Observable.unrepeatable();
		try {
			rs = connection.getMetaData().getPrimaryKeys(catalog, schema, tableName);
			return new CursorImpl(rs, CaseFormats.LOWER_CAMEL, observable);
		} finally {
			closeLazily(observable, rs, null, null);
		}
	}

	public static Cursor<Tuple> getFkMetadata(Connection connection, String tableName) throws SQLException {
		return getFkMetadata(connection, null, null, tableName);
	}

	public static Cursor<Tuple> getFkMetadata(Connection connection, String catalog, String schema, String tableName) throws SQLException {
		ResultSet rs = null;
		Observable observable = Observable.unrepeatable();
		try {
			rs = connection.getMetaData().getImportedKeys(catalog, schema, tableName);
			return new CursorImpl(rs, CaseFormats.LOWER_CAMEL, observable);
		} finally {
			closeLazily(observable, rs, null, null);
		}
	}

}
