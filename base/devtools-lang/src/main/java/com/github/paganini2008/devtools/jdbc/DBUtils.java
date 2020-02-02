package com.github.paganini2008.devtools.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import com.github.paganini2008.devtools.CaseFormats;
import com.github.paganini2008.devtools.Observable;
import com.github.paganini2008.devtools.Observer;
import com.github.paganini2008.devtools.collection.CollectionUtils;
import com.github.paganini2008.devtools.collection.Tuple;

/**
 * DBUtils
 * 
 * @author Fred Feng
 * @revised 2019-08
 * @created 2012-12
 * @version 1.0
 */
public abstract class DBUtils {

	public static void close(Connection conn) throws SQLException {
		if (conn != null) {
			conn.close();
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

	public static void closeQuietly(Connection conn) {
		try {
			close(conn);
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

	public static void commit(Connection conn) throws SQLException {
		if (conn != null) {
			conn.commit();
		}
	}

	public static void commitQuietly(Connection conn) {
		try {
			commit(conn);
		} catch (SQLException e) {
		}
	}

	public static void commitAndClose(Connection conn) throws SQLException {
		if (conn != null) {
			try {
				conn.commit();
			} finally {
				conn.close();
			}
		}
	}

	public static void commitAndCloseQuietly(Connection conn) {
		try {
			commitAndClose(conn);
		} catch (SQLException e) {
		}
	}

	public static void rollback(Connection conn) throws SQLException {
		if (conn != null) {
			conn.rollback();
		}
	}

	public static void rollbackQuietly(Connection conn) {
		try {
			rollback(conn);
		} catch (SQLException e) {
		}
	}

	public static void rollbackAndClose(Connection conn) throws SQLException {
		if (conn != null) {
			try {
				conn.rollback();
			} finally {
				conn.close();
			}
		}
	}

	public static void rollbackAndCloseQuietly(Connection conn) {
		try {
			rollbackAndClose(conn);
		} catch (SQLException e) {
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

	public static void printWarnings(Connection conn) {
		printWarnings(conn, new PrintWriter(System.err));
	}

	public static void printWarnings(Connection conn, PrintWriter pw) {
		if (conn != null) {
			try {
				printStackTrace(conn.getWarnings(), pw);
			} catch (SQLException e) {
				printStackTrace(e, pw);
			}
		}
	}

	public static Connection getConnection(String url, String user, String password) throws SQLException {
		return DriverManager.getConnection(url, user, password);
	}

	public static int executeUpdate(Connection connection, String sql) throws SQLException {
		Statement stm = null;
		try {
			stm = connection.createStatement();
			return stm.executeUpdate(sql);
		} finally {
			closeQuietly(stm);
		}
	}

	public static int[] executeBatch(Connection connection, String sql, List<Object[]> argsList) throws SQLException {
		return executeBatch(connection, sql, setParameters(argsList));
	}

	public static int[] executeBatch(Connection connection, String sql, PreparedStatementSetter callback) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(sql);
			callback.setValues(ps);
			return ps.executeBatch();
		} finally {
			closeQuietly(ps);
		}
	}

	public static int executeUpdate(Connection connection, String sql, Object[] args) throws SQLException {
		return executeUpdate(connection, sql, setParameters(args));
	}

	public static int executeUpdate(Connection connection, String sql, PreparedStatementSetter callback) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(sql);
			callback.setValues(ps);
			return ps.executeUpdate();
		} finally {
			closeQuietly(ps);
		}
	}

	public static Object executeOneResultQuery(Connection connection, String sql) throws SQLException {
		Iterator<Tuple> iterator = executeQuery(connection, sql);
		Tuple first = CollectionUtils.getFirst(iterator);
		return first != null && !first.isEmpty() ? first.valueArray()[0] : null;
	}

	public static Iterator<Tuple> executeQuery(Connection connection, String sql) throws SQLException {
		Statement sm = null;
		ResultSet rs = null;
		Observable observable = Observable.unrepeatable();
		try {
			sm = connection.createStatement();
			rs = sm.executeQuery(sql);
			return toIterator(rs, observable);
		} finally {
			closeAfterDone(observable, rs, sm);
		}
	}

	public static Object executeOneResultQuery(Connection connection, String sql, Object[] args) throws SQLException {
		Iterator<Tuple> iterator = executeQuery(connection, sql, args);
		Tuple first = CollectionUtils.getFirst(iterator);
		return first != null && !first.isEmpty() ? first.valueArray()[0] : null;
	}

	public static Iterator<Tuple> executeQuery(ConnectionFactory connectionFactory, String sql, Object[] args) throws SQLException {
		return executeQuery(connectionFactory.getConnection(), sql, args);
	}

	public static Iterator<Tuple> executeQuery(Connection connection, String sql, Object[] args) throws SQLException {
		return executeQuery(connection, sql, setParameters(args));
	}

	public static Iterator<Tuple> executeQuery(ConnectionFactory connectionFactory, String sql, PreparedStatementSetter callback)
			throws SQLException {
		return executeQuery(connectionFactory.getConnection(), sql, callback);
	}

	public static Iterator<Tuple> executeQuery(Connection connection, String sql, PreparedStatementSetter callback) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Observable observable = Observable.unrepeatable();
		try {
			ps = connection.prepareStatement(sql);
			callback.setValues(ps);
			rs = ps.executeQuery();
			return toIterator(rs, observable);
		} finally {
			closeAfterDone(observable, rs, ps);
		}
	}

	private static void closeAfterDone(Observable observable, final ResultSet rs, final Statement ps) {
		observable.addObserver(new Observer() {
			public void update(Observable ob, Object arg) {
				closeQuietly(rs);
				closeQuietly(ps);
			}
		});
	}

	private static Tuple toTuple(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		Tuple tuple = Tuple.newTuple(CaseFormats.LOWER_CAMEL);
		for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
			String columnName = rsmd.getColumnLabel(columnIndex);
			Object value = rs.getObject(columnIndex);
			tuple.set(columnName, value);
		}
		return tuple;
	}

	private static Iterator<Tuple> toIterator(final ResultSet rs, final Observable observable) throws SQLException {

		return new Iterator<Tuple>() {

			public boolean hasNext() {
				boolean state = true;
				try {
					return (state = rs.next());
				} catch (SQLException e) {
					state = false;
					throw new IllegalStateException(e);
				} finally {
					if (!state) {
						observable.notifyObservers();
					}
				}
			}

			public Tuple next() {
				boolean state = true;
				try {
					return toTuple(rs);
				} catch (SQLException e) {
					state = false;
					throw new IllegalStateException(e.getMessage(), e);
				} finally {
					if (!state) {
						observable.notifyObservers();
					}
				}
			}
		};
	}

	public static void scan(ConnectionFactory connectionFactory, String sql, PreparedStatementSetter callback, Consumer<Tuple> consumer)
			throws SQLException {
		scan(connectionFactory.getConnection(), sql, callback, consumer);
	}

	public static void scan(ConnectionFactory connectionFactory, String sql, Object[] args, PreparedStatementSetter callback,
			Consumer<Tuple> consumer) throws SQLException {
		scan(connectionFactory.getConnection(), sql, args, consumer);
	}

	public static void scan(Connection connection, String sql, Object[] args, Consumer<Tuple> consumer) throws SQLException {
		scan(connection, sql, setParameters(args), consumer);
	}

	public static void scan(Connection connection, String sql, PreparedStatementSetter callback, Consumer<Tuple> consumer)
			throws SQLException {
		Iterator<Tuple> iterator = executeQuery(connection, sql, callback);
		CollectionUtils.forEach(iterator).forEach(consumer);
	}

	public static void scrollingScan(ConnectionFactory connectionFactory, String sql, Object[] args, int pageSize,
			Consumer<List<Tuple>> consumer) throws SQLException {
		scrollingScan(connectionFactory, sql, setParameters(args), pageSize, consumer);
	}

	public static void scrollingScan(ConnectionFactory connectionFactory, String sql, PreparedStatementSetter callback, int pageSize,
			Consumer<List<Tuple>> consumer) throws SQLException {
		scrollingScan(connectionFactory.getConnection(), new DefaultPageableSql(sql), callback, pageSize, consumer);
	}

	public static void scrollingScan(Connection connection, PageableSql pageableSql, Object[] args, int pageSize,
			Consumer<List<Tuple>> consumer) throws SQLException {
		scrollingScan(connection, pageableSql, setParameters(args), pageSize, consumer);
	}

	public static void scrollingScan(Connection connection, PageableSql pageableSql, PreparedStatementSetter callback, int pageSize,
			Consumer<List<Tuple>> consumer) throws SQLException {
		PagingQuery<Tuple> query = pagingQuery(connection, pageableSql, callback);
		for (PageResponse<Tuple> pageResponse : query.forEachPage(1, pageSize)) {
			consumer.accept(pageResponse.getContent());
		}
	}

	public static PagingQuery<Tuple> pagingQuery(Connection connection, String sql, Object[] args) {
		return pagingQuery(connection, sql, setParameters(args));
	}

	public static PagingQuery<Tuple> pagingQuery(Connection connection, String sql, PreparedStatementSetter callback) {
		return pagingQuery(connection, new DefaultPageableSql(sql), callback);
	}

	public static PagingQuery<Tuple> pagingQuery(Connection connection, PageableSql pageableSql, Object[] args) {
		return pagingQuery(connection, pageableSql, setParameters(args));
	}

	public static PagingQuery<Tuple> pagingQuery(Connection connection, PageableSql pageableSql, PreparedStatementSetter callback) {
		return new PagingQueryImpl(connection, pageableSql, callback);
	}

	public static void setParameters(PreparedStatement ps, Object[] args) throws SQLException {
		if (args != null && args.length > 0) {
			int parameterIndex = 1;
			for (Object arg : args) {
				ps.setObject(parameterIndex++, arg);
			}
		}
	}

	private static PreparedStatementSetter setParameters(List<Object[]> argsList) {
		return ps -> {
			for (Object[] args : argsList) {
				if (args != null && args.length > 0) {
					setParameters(ps, args);
					ps.addBatch();
				}
			}
		};
	}

	private static PreparedStatementSetter setParameters(Object[] args) {
		return ps -> {
			setParameters(ps, args);
		};
	}

	public static boolean tableExists(DatabaseMetaData dbmd, String schema, String tableName) throws SQLException {
		ResultSet rs = dbmd.getTables(null, schema, tableName, new String[] { "TABLE" });
		if (rs != null && rs.next()) {
			return (tableName.equalsIgnoreCase(rs.getString("TABLE_NAME")));
		}
		return false;
	}

	public static boolean tableExists(Connection conn, String schema, String tableName) throws SQLException {
		return tableExists(conn.getMetaData(), schema, tableName);
	}

	public static void main(String[] args) throws SQLException {
		UnpooledConnectionFactory connectionFactory = new UnpooledConnectionFactory();
		connectionFactory.setDriverClassName("com.mysql.cj.jdbc.Driver");
		connectionFactory.setUser("fengy");
		connectionFactory.setPassword("123456");
		connectionFactory.setUrl(
				"jdbc:mysql://localhost:3306/db_mec_hlsh_v2?userUnicode=true&serverTimezone=GMT&characterEncoding=UTF8&useSSL=false&autoReconnect=true&zeroDateTimeBehavior=convertToNull");
		final AtomicInteger count = new AtomicInteger();
		DBUtils.scrollingScan(connectionFactory, "select * from mec_area", (Object[]) null, 100, list -> {
			for (Tuple tuple : list) {
				System.out.println(tuple);
				count.incrementAndGet();
			}
		});
		System.out.println("Rows: " + count);
	}

}
