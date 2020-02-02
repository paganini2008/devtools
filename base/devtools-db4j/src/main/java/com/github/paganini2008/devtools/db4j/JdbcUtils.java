package com.github.paganini2008.devtools.db4j;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.github.paganini2008.devtools.jdbc.PreparedStatementSetter;

/**
 * 
 * JdbcUtils
 *
 * @author Fred Feng
 * @created 2016-02
 * @revised 2020-01
 * @version 1.0
 */
public abstract class JdbcUtils {

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
