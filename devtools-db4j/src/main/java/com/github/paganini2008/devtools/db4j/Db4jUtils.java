package com.github.paganini2008.devtools.db4j;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.github.paganini2008.devtools.jdbc.PreparedStatementCallback;

/**
 * 
 * Db4jUtils
 *
 * @author Fred Feng
 * @version 1.0
 */
public abstract class Db4jUtils {

	public static PreparedStatementCallback batchPrepare(List<Object[]> parameterList, int[] jdbcTypes) {
		return new BatchArgumentJdbcTypePreparedStatementCallback(parameterList, jdbcTypes);
	}

	public static PreparedStatementCallback batchPrepare(List<Object[]> parameterList, JdbcType[] jdbcTypes) {
		return new BatchArgumentJdbcTypePreparedStatementCallback(parameterList, jdbcTypes);
	}

	public static PreparedStatementCallback batchPrepare(List<Object[]> parameterList) {
		return new BatchArgumentPreparedStatementCallback(parameterList);
	}

	public static PreparedStatementCallback prepare(Object[] parameters) {
		return new ArgumentPreparedStatementCallback(parameters);
	}

	public static PreparedStatementCallback prepare(Object[] parameters, int[] jdbcTypes) {
		return new ArgumentJdbcTypePreparedStatementCallback(parameters, jdbcTypes);
	}

	public static PreparedStatementCallback prepare(Object[] parameters, JdbcType[] jdbcTypes) {
		return new ArgumentJdbcTypePreparedStatementCallback(parameters, jdbcTypes);
	}

	private static int[] getSqlTypes(JdbcType[] jdbcTypes) {
		int[] sqlTypes = new int[jdbcTypes.length];
		for (int i = 0; i < sqlTypes.length; i++) {
			sqlTypes[i] = jdbcTypes[i].getTypeCode();
		}
		return sqlTypes;
	}

	private static class BatchArgumentJdbcTypePreparedStatementCallback implements PreparedStatementCallback {
		BatchArgumentJdbcTypePreparedStatementCallback(List<Object[]> parameterList, int[] sqlTypes) {
			this.parameterList = parameterList;
			this.sqlTypes = sqlTypes;
		}

		BatchArgumentJdbcTypePreparedStatementCallback(List<Object[]> parameterList, JdbcType[] jdbcTypes) {
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

	private static class BatchArgumentPreparedStatementCallback implements PreparedStatementCallback {
		BatchArgumentPreparedStatementCallback(List<Object[]> parameterList) {
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

	private static class ArgumentPreparedStatementCallback implements PreparedStatementCallback {

		ArgumentPreparedStatementCallback(Object[] parameters) {
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

	private static class ArgumentJdbcTypePreparedStatementCallback implements PreparedStatementCallback {

		ArgumentJdbcTypePreparedStatementCallback(Object[] parameters, int[] sqlTypes) {
			this.parameters = parameters;
			this.sqlTypes = sqlTypes;
		}

		ArgumentJdbcTypePreparedStatementCallback(Object[] parameters, JdbcType[] jdbcTypes) {
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
