/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools.db4j;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.github.paganini2008.devtools.db4j.type.TypeHandler;
import com.github.paganini2008.devtools.jdbc.PreparedStatementCallback;

/**
 * 
 * PreparedStatementCallbackUtils
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public abstract class PreparedStatementCallbackUtils {

	public static PreparedStatementCallback batchPrepare(List<Object[]> parametersList, JdbcType[] jdbcTypes,
			TypeHandlerRegistry typeHandlerRegistry) {
		return new BatchArgumentJdbcTypePreparedStatementCallback(parametersList, jdbcTypes, typeHandlerRegistry);
	}

	public static PreparedStatementCallback batchPrepare(List<Object[]> parametersList, TypeHandlerRegistry typeHandlerRegistry) {
		return new BatchArgumentPreparedStatementCallback(parametersList, typeHandlerRegistry);
	}

	public static PreparedStatementCallback prepare(Object[] parameters, JdbcType[] jdbcTypes, TypeHandlerRegistry typeHandlerRegistry) {
		return new ArgumentJdbcTypePreparedStatementCallback(parameters, jdbcTypes, typeHandlerRegistry);
	}

	public static PreparedStatementCallback prepare(Object[] parameters, TypeHandlerRegistry typeHandlerRegistry) {
		return new ArgumentPreparedStatementCallback(parameters, typeHandlerRegistry);
	}

	/**
	 * 
	 * BatchArgumentJdbcTypePreparedStatementCallback
	 *
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	private static class BatchArgumentJdbcTypePreparedStatementCallback implements PreparedStatementCallback {

		private BatchArgumentJdbcTypePreparedStatementCallback(List<Object[]> parametersList, JdbcType[] jdbcTypes,
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
	 * BatchArgumentPreparedStatementCallback
	 * 
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	private static class BatchArgumentPreparedStatementCallback implements PreparedStatementCallback {

		private BatchArgumentPreparedStatementCallback(List<Object[]> parametersList, TypeHandlerRegistry typeHandlerRegistry) {
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
	 * ArgumentJdbcTypePreparedStatementCallback
	 * 
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	private static class ArgumentJdbcTypePreparedStatementCallback implements PreparedStatementCallback {

		private ArgumentJdbcTypePreparedStatementCallback(Object[] parameters, JdbcType[] jdbcTypes, TypeHandlerRegistry typeHandlerRegistry) {
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
	 * ArgumentPreparedStatementCallback
	 * 
	 * @author Fred Feng
	 * @since 2.0.1
	 */
	private static class ArgumentPreparedStatementCallback implements PreparedStatementCallback {

		private ArgumentPreparedStatementCallback(Object[] parameters, TypeHandlerRegistry typeHandlerRegistry) {
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
