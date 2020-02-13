package com.github.paganini2008.devtools.db4j;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.github.paganini2008.devtools.db4j.type.TypeHandler;
import com.github.paganini2008.devtools.jdbc.PreparedStatementSetter;

/**
 * 
 * PreparedStatementSetterUtils
 *
 * @author Fred Feng
 * @created 2016-02
 * @revised 2020-01
 * @version 1.0
 */
public abstract class PreparedStatementSetterUtils {

	public static PreparedStatementSetter batchPrepare(List<Object[]> parametersList, JdbcType[] jdbcTypes,
			TypeHandlerRegistry typeHandlerRegistry) {
		return new BatchArgumentJdbcTypePrepareStatementSetter(parametersList, jdbcTypes, typeHandlerRegistry);
	}

	public static PreparedStatementSetter batchPrepare(List<Object[]> parametersList, TypeHandlerRegistry typeHandlerRegistry) {
		return new BatchArgumentPrepareStatementSetter(parametersList, typeHandlerRegistry);
	}

	public static PreparedStatementSetter prepare(Object[] parameters, JdbcType[] jdbcTypes, TypeHandlerRegistry typeHandlerRegistry) {
		return new ArgumentJdbcTypePrepareStatementSetter(parameters, jdbcTypes, typeHandlerRegistry);
	}

	public static PreparedStatementSetter prepare(Object[] parameters, TypeHandlerRegistry typeHandlerRegistry) {
		return new ArgumentPrepareStatementSetter(parameters, typeHandlerRegistry);
	}

	/**
	 * BatchJdbcTypePrepareStatementSetter
	 * 
	 * @author Fred Feng
	 * @version 1.0
	 */
	private static class BatchArgumentJdbcTypePrepareStatementSetter implements PreparedStatementSetter {

		private BatchArgumentJdbcTypePrepareStatementSetter(List<Object[]> parametersList, JdbcType[] jdbcTypes,
				TypeHandlerRegistry typeHandlerRegistry) {
			this.parametersList = parametersList;
			this.jdbcTypes = jdbcTypes;
			this.typeHandlerRegistry = typeHandlerRegistry;
		}

		private final List<Object[]> parametersList;
		private final JdbcType[] jdbcTypes;
		private final TypeHandlerRegistry typeHandlerRegistry;

		public void setValues(PreparedStatement ps) throws SQLException {
			if (parametersList != null && parametersList.size() > 0) {
				int right = jdbcTypes != null ? jdbcTypes.length : 0;
				for (int i = 0; i < parametersList.size(); i++) {
					Object[] parameters = parametersList.get(i);
					int left = parameters != null ? parameters.length : 0;
					if (left != right) {
						throw new IllegalArgumentException("Parameters and parameter types must match.");
					}
					TypeHandler typeHandler;
					for (int j = 0; j < right; j++) {
						typeHandler = typeHandlerRegistry != null
								? typeHandlerRegistry.getTypeHandler(parameters[j] != null ? parameters[j].getClass() : null, jdbcTypes[j])
								: TypeHandlerRegistryImpl.getDefault();
						typeHandler.setValue(ps, j + 1, parameters[j], jdbcTypes[j]);
					}
					ps.addBatch();
				}
			}
		}

	}

	/**
	 * BatchArgumentPrepareStatementSetter
	 * 
	 * @author Fred Feng
	 * @version 1.0
	 */
	private static class BatchArgumentPrepareStatementSetter implements PreparedStatementSetter {

		private BatchArgumentPrepareStatementSetter(List<Object[]> parametersList, TypeHandlerRegistry typeHandlerRegistry) {
			this.parametersList = parametersList;
			this.typeHandlerRegistry = typeHandlerRegistry;
		}

		private final List<Object[]> parametersList;
		private final TypeHandlerRegistry typeHandlerRegistry;

		public void setValues(PreparedStatement ps) throws SQLException {
			if (parametersList != null && parametersList.size() > 0) {
				for (int i = 0; i < parametersList.size(); i++) {
					Object[] parameters = parametersList.get(i);
					if (parameters != null && parameters.length > 0) {
						TypeHandler typeHandler;
						for (int j = 0; j < parameters.length; j++) {
							typeHandler = typeHandlerRegistry != null
									? typeHandlerRegistry.getTypeHandler(parameters[j] != null ? parameters[j].getClass() : null,
											JdbcType.OTHER)
									: TypeHandlerRegistryImpl.getDefault();
							typeHandler.setValue(ps, j + 1, parameters[j], JdbcType.OTHER);
						}
						ps.addBatch();
					}
				}
			}
		}
	}

	/**
	 * ArgumentJdbcTypePrepareStatementSetter
	 * 
	 * @author Fred Feng
	 * @version 1.0
	 */
	private static class ArgumentJdbcTypePrepareStatementSetter implements PreparedStatementSetter {

		private ArgumentJdbcTypePrepareStatementSetter(Object[] parameters, JdbcType[] jdbcTypes, TypeHandlerRegistry typeHandlerRegistry) {
			this.parameters = parameters;
			this.jdbcTypes = jdbcTypes;
			this.typeHandlerRegistry = typeHandlerRegistry;
		}

		private final Object[] parameters;
		private final JdbcType[] jdbcTypes;
		private final TypeHandlerRegistry typeHandlerRegistry;

		public void setValues(PreparedStatement ps) throws SQLException {
			int left = parameters != null ? parameters.length : 0;
			int right = jdbcTypes != null ? jdbcTypes.length : 0;
			if (left != right) {
				throw new IllegalArgumentException("Parameters and parameter types must match.");
			}
			TypeHandler typeHandler;
			for (int i = 0; i < right; i++) {
				typeHandler = typeHandlerRegistry != null
						? typeHandlerRegistry.getTypeHandler(parameters[i] != null ? parameters[i].getClass() : null, jdbcTypes[i])
						: TypeHandlerRegistryImpl.getDefault();
				typeHandler.setValue(ps, i + 1, parameters[i], jdbcTypes[i]);
			}
		}

	}

	/**
	 * ArgumentPrepareStatementSetter
	 * 
	 * @author Fred Feng
	 * @version 1.0
	 */
	private static class ArgumentPrepareStatementSetter implements PreparedStatementSetter {

		private ArgumentPrepareStatementSetter(Object[] parameters, TypeHandlerRegistry typeHandlerRegistry) {
			this.parameters = parameters;
			this.typeHandlerRegistry = typeHandlerRegistry;
		}

		private final Object[] parameters;
		private final TypeHandlerRegistry typeHandlerRegistry;

		public void setValues(PreparedStatement ps) throws SQLException {
			if (parameters != null && parameters.length > 0) {
				TypeHandler typeHandler;
				for (int i = 0; i < parameters.length; i++) {
					typeHandler = typeHandlerRegistry != null
							? typeHandlerRegistry.getTypeHandler(parameters[i] != null ? parameters[i].getClass() : null, JdbcType.OTHER)
							: TypeHandlerRegistryImpl.getDefault();
					typeHandler.setValue(ps, i + 1, parameters[i], JdbcType.OTHER);
				}
			}
		}
	}
}
