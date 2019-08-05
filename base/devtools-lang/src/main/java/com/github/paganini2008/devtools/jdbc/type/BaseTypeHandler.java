package com.github.paganini2008.devtools.jdbc.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.converter.TypeConverter;
import com.github.paganini2008.devtools.jdbc.JdbcType;
import com.github.paganini2008.devtools.logging.Log;
import com.github.paganini2008.devtools.logging.LogFactory;

/**
 * BaseTypeHandler
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class BaseTypeHandler implements TypeHandler {

	private static final Log logger = LogFactory.getLog(TypeHandler.class);

	protected BaseTypeHandler() {
	}

	public void setValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType, TypeConverter typeConverter)
			throws SQLException {
		if (parameter != null) {
			if (typeConverter != null) {
				Object converted = typeConverter.convert(parameter, getJavaType(), null);
				if (converted != null) {
					parameter = converted;
				}
			}
			setNonNullValue(ps, parameterIndex, parameter, jdbcType);
		} else {
			setNullValue(ps, parameterIndex, parameter, jdbcType);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("SqlParameter[{}]: {}, {}", new Object[] { parameterIndex, parameter, jdbcType });
		}
	}

	protected void setNullValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType) throws SQLException {
		if (jdbcType != JdbcType.OTHER || jdbcType != JdbcType.OBJECT) {
			ps.setNull(parameterIndex, jdbcType.getTypeCode());
		} else {
			ps.setObject(parameterIndex, null);
		}
	}

	public Object getValue(ResultSet rs, String columnName) throws SQLException {
		Object result = getNullableValue(rs, columnName);
		if (rs.wasNull()) {
			return null;
		} else {
			return result;
		}
	}

	public Object getValue(ResultSet rs, int columnIndex) throws SQLException {
		Object result = getNullableValue(rs, columnIndex);
		if (rs.wasNull()) {
			return null;
		} else {
			return result;
		}
	}

	public Object getValue(CallableStatement cs, int columnIndex) throws SQLException {
		Object result = getNullableValue(cs, columnIndex);
		if (cs.wasNull()) {
			return null;
		} else {
			return result;
		}
	}

	protected abstract void setNonNullValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType)
			throws SQLException;

	protected abstract Object getNullableValue(ResultSet rs, String columnName) throws SQLException;

	protected abstract Object getNullableValue(ResultSet rs, int columnIndex) throws SQLException;

	protected abstract Object getNullableValue(CallableStatement cs, int columnIndex) throws SQLException;

	public abstract Class<?> getJavaType();

}
