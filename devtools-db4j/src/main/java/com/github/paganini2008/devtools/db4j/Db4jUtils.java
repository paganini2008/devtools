/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.paganini2008.devtools.jdbc.PreparedStatementCallback;

/**
 * 
 * Db4jUtils
 *
 * @author Fred Feng
 * @version 1.0
 */
public abstract class Db4jUtils {

	private static final Map<String, Type> classNamesAndJavaTypes = new HashMap<String, Type>();

	static {
		classNamesAndJavaTypes.put(Byte.class.getName(), Byte.class);
		classNamesAndJavaTypes.put(Short.class.getName(), Short.class);
		classNamesAndJavaTypes.put(Integer.class.getName(), Integer.class);
		classNamesAndJavaTypes.put(Long.class.getName(), Long.class);
		classNamesAndJavaTypes.put(Float.class.getName(), Float.class);
		classNamesAndJavaTypes.put(Double.class.getName(), Double.class);
		classNamesAndJavaTypes.put(Character.class.getName(), Character.class);
		classNamesAndJavaTypes.put(Boolean.class.getName(), Boolean.class);

		classNamesAndJavaTypes.put(Byte.TYPE.getName(), Byte.TYPE);
		classNamesAndJavaTypes.put(Short.TYPE.getName(), Short.TYPE);
		classNamesAndJavaTypes.put(Integer.TYPE.getName(), Integer.TYPE);
		classNamesAndJavaTypes.put(Long.TYPE.getName(), Long.TYPE);
		classNamesAndJavaTypes.put(Float.TYPE.getName(), Float.TYPE);
		classNamesAndJavaTypes.put(Double.TYPE.getName(), Double.TYPE);
		classNamesAndJavaTypes.put(Character.TYPE.getName(), Character.TYPE);
		classNamesAndJavaTypes.put(Boolean.TYPE.getName(), Boolean.TYPE);

		classNamesAndJavaTypes.put(BigDecimal.class.getName(), BigDecimal.class);
		classNamesAndJavaTypes.put(BigInteger.class.getName(), BigInteger.class);
		classNamesAndJavaTypes.put(String.class.getName(), String.class);

		classNamesAndJavaTypes.put(Date.class.getName(), Date.class);
		classNamesAndJavaTypes.put(Time.class.getName(), Time.class);
		classNamesAndJavaTypes.put(Timestamp.class.getName(), Timestamp.class);

		classNamesAndJavaTypes.put(byte[].class.getName(), byte[].class);
	}

	public static void mappingClassNameAndJavaType(String className, Type javaType) {
		classNamesAndJavaTypes.put(className, javaType);
	}

	public static Map<String, Type> getClassNamesAndJavaTypes() {
		return classNamesAndJavaTypes;
	}

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
