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

	public static interface PreparedStatementCallback {

		void setParameters(PreparedStatement ps) throws SQLException;

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

	public static int[] executeBatch(Connection connection, String sql, List<Object[]> argList) throws SQLException {
		return executeBatch(connection, sql, ps -> {
			for (Object[] args : argList) {
				setParameters(ps, args);
				ps.addBatch();
			}
		});
	}

	public static int[] executeBatch(Connection connection, String sql, PreparedStatementCallback callback) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(sql);
			callback.setParameters(ps);
			return ps.executeBatch();
		} finally {
			closeQuietly(ps);
		}
	}

	public static int executeUpdate(Connection connection, String sql, Object[] args) throws SQLException {
		return executeUpdate(connection, sql, ps -> {
			setParameters(ps, args);
		});
	}

	public static int executeUpdate(Connection connection, String sql, PreparedStatementCallback callback) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(sql);
			callback.setParameters(ps);
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

	public static Iterator<Tuple> executeQuery(Connection connection, String sql, Object[] args) throws SQLException {
		return executeQuery(connection, sql, ps -> {
			setParameters(ps, args);
		});
	}

	public static Iterator<Tuple> executeQuery(Connection connection, String sql, PreparedStatementCallback callback) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Observable observable = Observable.unrepeatable();
		try {
			ps = connection.prepareStatement(sql);
			callback.setParameters(ps);
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
		Tuple tuple = Tuple.newTuple();
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
					throw new IllegalStateException(e);
				} finally {
					if (!state) {
						observable.notifyObservers();
					}
				}
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public static void setParameters(PreparedStatement ps, Object[] args) throws SQLException {
		int parameterIndex = 1;
		for (Object arg : args) {
			ps.setObject(parameterIndex++, arg);
		}
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

	public static PreparedStatementSetter newBatchArgumentTypePrepareStatementSetter(List<Object[]> parameterList, int[] jdbcTypes) {
		return new BatchArgumentTypePrepareStatementSetter(parameterList, jdbcTypes);
	}

	public static PreparedStatementSetter newBatchArgumentTypePrepareStatementSetter(List<Object[]> parameterList, JdbcType[] jdbcTypes) {
		return new BatchArgumentTypePrepareStatementSetter(parameterList, jdbcTypes);
	}

	public static PreparedStatementSetter newBatchArgumentPrepareStatementSetter(List<Object[]> parameterList) {
		return new BatchArgumentPrepareStatementSetter(parameterList);
	}

	public static PreparedStatementSetter newArgumentPrepareStatementSetter(Object[] parameters) {
		return new ArgumentPrepareStatementSetter(parameters);
	}

	public static PreparedStatementSetter newArgumentTypePrepareStatementSetter(Object[] parameters, int[] jdbcTypes) {
		return new ArgumentTypePrepareStatementSetter(parameters, jdbcTypes);
	}

	public static PreparedStatementSetter newArgumentTypePrepareStatementSetter(Object[] parameters, JdbcType[] jdbcTypes) {
		return new ArgumentTypePrepareStatementSetter(parameters, jdbcTypes);
	}

	private static int[] getSqlTypes(JdbcType[] jdbcTypes) {
		int[] sqlTypes = new int[jdbcTypes.length];
		for (int i = 0; i < sqlTypes.length; i++) {
			sqlTypes[i] = jdbcTypes[i].getTypeCode();
		}
		return sqlTypes;
	}

	private static class BatchArgumentTypePrepareStatementSetter implements PreparedStatementSetter {
		BatchArgumentTypePrepareStatementSetter(List<Object[]> parameterList, int[] sqlTypes) {
			this.parameterList = parameterList;
			this.sqlTypes = sqlTypes;
		}

		BatchArgumentTypePrepareStatementSetter(List<Object[]> parameterList, JdbcType[] jdbcTypes) {
			this(parameterList, getSqlTypes(jdbcTypes));
		}

		private final List<Object[]> parameterList;
		private final int[] sqlTypes;

		public void setValues(PreparedStatement ps) throws SQLException {
			if (parameterList != null && parameterList.size() > 0) {
				for (Object[] parameters : parameterList) {
					int leftLength = parameters != null ? parameters.length : 0;
					int rightLength = sqlTypes != null ? sqlTypes.length : 0;
					if (leftLength != rightLength) {
						throw new IllegalArgumentException("JdbcTypes'length doesn't matches parameters'length length.");
					}
					if (parameters != null && parameters.length > 0) {
						for (int i = 0; i < parameters.length; i++) {
							ps.setObject(i + 1, parameters[i]);
						}
						ps.addBatch();
					}
				}
			}
		}
	}

	private static class BatchArgumentPrepareStatementSetter implements PreparedStatementSetter {
		BatchArgumentPrepareStatementSetter(List<Object[]> parameterList) {
			this.parameterList = parameterList;
		}

		private final List<Object[]> parameterList;

		public void setValues(PreparedStatement ps) throws SQLException {
			if (parameterList != null && parameterList.size() > 0) {
				for (Object[] parameters : parameterList) {
					if (parameters != null && parameters.length > 0) {
						for (int i = 0; i < parameters.length; i++) {
							ps.setObject(i + 1, parameters[i]);
						}
						ps.addBatch();
					}
				}
			}
		}
	}

	private static class ArgumentPrepareStatementSetter implements PreparedStatementSetter {

		ArgumentPrepareStatementSetter(Object[] parameters) {
			this.parameters = parameters;
		}

		private final Object[] parameters;

		public void setValues(PreparedStatement ps) throws SQLException {
			if (parameters != null && parameters.length > 0) {
				for (int i = 0; i < parameters.length; i++) {
					ps.setObject(i + 1, parameters[i]);
				}
			}
		}
	}

	private static class ArgumentTypePrepareStatementSetter implements PreparedStatementSetter {

		ArgumentTypePrepareStatementSetter(Object[] parameters, int[] sqlTypes) {
			this.parameters = parameters;
			this.sqlTypes = sqlTypes;
		}

		ArgumentTypePrepareStatementSetter(Object[] parameters, JdbcType[] jdbcTypes) {
			this(parameters, getSqlTypes(jdbcTypes));
		}

		private final Object[] parameters;
		private final int[] sqlTypes;

		public void setValues(PreparedStatement ps) throws SQLException {
			int leftLength = parameters != null ? parameters.length : 0;
			int rightLength = sqlTypes != null ? sqlTypes.length : 0;
			if (leftLength != rightLength) {
				throw new IllegalArgumentException("JdbcTypes'length doesn't matches parameters'length length.");
			}
			if (parameters != null && parameters.length > 0) {
				for (int i = 0; i < parameters.length; i++) {
					ps.setObject(i + 1, parameters[i]);
				}
			}
		}
	}

}
