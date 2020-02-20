package com.github.paganini2008.devtools.db4j.mapper;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.db4j.JdbcType;
import com.github.paganini2008.devtools.db4j.TypeHandlerRegistry;
import com.github.paganini2008.devtools.db4j.TypeHandlerRegistryImpl;
import com.github.paganini2008.devtools.db4j.type.TypeHandler;

/**
 * 
 * AbstractRowMapper
 *
 * @author Fred Feng
 * 
 * 
 * @version 1.0
 */
public abstract class AbstractRowMapper<T> implements RowMapper<T> {

	private static final Map<String, Type> classNamesAndTypes = new HashMap<String, Type>();

	static {
		classNamesAndTypes.put(Byte.class.getName(), Byte.class);
		classNamesAndTypes.put(Short.class.getName(), Short.class);
		classNamesAndTypes.put(Integer.class.getName(), Integer.class);
		classNamesAndTypes.put(Long.class.getName(), Long.class);
		classNamesAndTypes.put(Float.class.getName(), Float.class);
		classNamesAndTypes.put(Double.class.getName(), Double.class);
		classNamesAndTypes.put(Character.class.getName(), Character.class);
		classNamesAndTypes.put(Boolean.class.getName(), Boolean.class);

		classNamesAndTypes.put(Byte.TYPE.getName(), Byte.TYPE);
		classNamesAndTypes.put(Short.TYPE.getName(), Short.TYPE);
		classNamesAndTypes.put(Integer.TYPE.getName(), Integer.TYPE);
		classNamesAndTypes.put(Long.TYPE.getName(), Long.TYPE);
		classNamesAndTypes.put(Float.TYPE.getName(), Float.TYPE);
		classNamesAndTypes.put(Double.TYPE.getName(), Double.TYPE);
		classNamesAndTypes.put(Character.TYPE.getName(), Character.TYPE);
		classNamesAndTypes.put(Boolean.TYPE.getName(), Boolean.TYPE);

		classNamesAndTypes.put(BigDecimal.class.getName(), BigDecimal.class);
		classNamesAndTypes.put(BigInteger.class.getName(), BigInteger.class);
		classNamesAndTypes.put(String.class.getName(), String.class);

		classNamesAndTypes.put(Date.class.getName(), Date.class);
		classNamesAndTypes.put(Time.class.getName(), Time.class);
		classNamesAndTypes.put(Timestamp.class.getName(), Timestamp.class);
	}

	public static void registerNameAndType(String className, Type javaType) {
		classNamesAndTypes.put(className, javaType);
	}

	protected AbstractRowMapper(TypeHandlerRegistry typeHandlerRegistry) {
		this.typeHandlerRegistry = typeHandlerRegistry;
	}

	public T mapRow(int rowIndex, ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		T object = createObject(columnCount);
		for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
			String columnName = getColumnName(rsmd, columnIndex);
			String columnDisplayName = getColumnDisplayName(rsmd, columnIndex);
			Type javaType = getJavaType(rsmd, columnIndex);
			JdbcType jdbcType = getJdbcType(rsmd, columnIndex);
			Object columnValue = getColumnValue(rs, columnName, columnIndex, javaType, jdbcType);
			setValue(object, columnIndex, columnName, columnDisplayName, jdbcType, columnValue);
		}
		return object;
	}

	private boolean useCamelCase = true;

	private final TypeHandlerRegistry typeHandlerRegistry;

	protected abstract T createObject(int columnCount);

	protected abstract void setValue(T object, int columnIndex, String columnName, String columnDisplayName, JdbcType jdbcType,
			Object columnValue);

	public void setUseCamelCase(boolean useCamelCase) {
		this.useCamelCase = useCamelCase;
	}

	protected Type getJavaType(ResultSetMetaData rsmd, int columnIndex) {
		String className;
		try {
			className = rsmd.getColumnClassName(columnIndex);
		} catch (SQLException e) {
			return null;
		}
		return classNamesAndTypes.get(className);
	}

	protected JdbcType getJdbcType(ResultSetMetaData rsmd, int columnIndex) {
		try {
			return JdbcType.find(rsmd.getColumnType(columnIndex));
		} catch (SQLException e) {
			return null;
		}
	}

	protected String getColumnDisplayName(ResultSetMetaData rsmd, int columnIndex) throws SQLException {
		String columnDisplayName = rsmd.getColumnLabel(columnIndex);
		return useCamelCase ? StringUtils.toCamelCase(columnDisplayName, "_") : columnDisplayName;
	}

	protected String getColumnName(ResultSetMetaData rsmd, int columnIndex) throws SQLException {
		return rsmd.getColumnName(columnIndex);
	}

	protected Object getColumnValue(ResultSet rs, String columnName, int columnIndex, Type javaType, JdbcType jdbcType)
			throws SQLException {
		TypeHandler typeHandler = typeHandlerRegistry != null ? typeHandlerRegistry.getTypeHandler(javaType, jdbcType)
				: TypeHandlerRegistryImpl.getDefault();
		return typeHandler.getValue(rs, columnIndex);
	}
}
