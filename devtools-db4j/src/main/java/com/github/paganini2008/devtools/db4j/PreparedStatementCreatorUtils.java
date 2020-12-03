package com.github.paganini2008.devtools.db4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * PreparedStatementCreators
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public abstract class PreparedStatementCreatorUtils {

	public static PreparedStatementCreator forDefault(String sql) {
		return new DefaultPreparedStatementCreator(sql);
	}

	public static PreparedStatementCreator forColumnNames(String sql, String[] columnNames) {
		return new ColumnNamesPreparedStatementCreator(sql, columnNames);
	}

	public static PreparedStatementCreator forColumnIndexes(String sql, int[] columnIndexes) {
		return new ColumnIndexesPreparedStatementCreator(sql, columnIndexes);
	}

	public static PreparedStatementCreator forGeneratedKey(String sql) {
		return new GeneratedKeyPreparedStatementCreator(sql);
	}

	public static PreparedStatementCreator forQuery(String sql, int resultSetType, int resultSetConcurrency) {
		return new ResultSetPreparedStatementCreator(sql, resultSetType, resultSetConcurrency);
	}

	/**
	 * Default PreparedStatement
	 * 
	 * @author Jimmy Hoff
	 * @version 1.0
	 */
	private static class DefaultPreparedStatementCreator implements PreparedStatementCreator {

		private DefaultPreparedStatementCreator(String sql) {
			this.sql = sql;
		}

		private final String sql;

		public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
			return connection.prepareStatement(sql);
		}

	}

	/**
	 * PreparedStatement for columnNames
	 * 
	 * @author Jimmy Hoff
	 * @version 1.0
	 */
	private static class ColumnNamesPreparedStatementCreator implements PreparedStatementCreator {

		private ColumnNamesPreparedStatementCreator(String sql, String[] columnNames) {
			this.sql = sql;
			this.columnNames = columnNames;
		}

		private final String sql;

		private final String[] columnNames;

		public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
			return connection.prepareStatement(sql, columnNames);
		}

	}

	/**
	 * PreparedStatement for columnIndexes
	 * 
	 * @author Jimmy Hoff
	 * @version 1.0
	 */
	private static class ColumnIndexesPreparedStatementCreator implements PreparedStatementCreator {

		private ColumnIndexesPreparedStatementCreator(String sql, int[] columnIndexes) {
			this.sql = sql;
			this.columnIndexes = columnIndexes;
		}

		private final String sql;

		private final int[] columnIndexes;

		public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
			return connection.prepareStatement(sql, columnIndexes);
		}

	}

	/**
	 * PreparedStatement for GeneratedKeys
	 * 
	 * @author Jimmy Hoff
	 * @version 1.0
	 */
	private static class GeneratedKeyPreparedStatementCreator implements PreparedStatementCreator {

		private GeneratedKeyPreparedStatementCreator(String sql) {
			this.sql = sql;
		}

		private final String sql;

		public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
			return connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		}

	}

	/**
	 * PreparedStatement for ResultSet
	 * 
	 * @author Jimmy Hoff
	 * @version 1.0
	 */
	private static class ResultSetPreparedStatementCreator implements PreparedStatementCreator {

		private ResultSetPreparedStatementCreator(String sql, int resultSetType, int resultSetConcurrency) {
			this.sql = sql;
			this.resultSetType = resultSetType;
			this.resultSetConcurrency = resultSetConcurrency;
		}

		private final String sql;
		private final int resultSetType;
		private final int resultSetConcurrency;

		public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
			return connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
		}

	}
}
