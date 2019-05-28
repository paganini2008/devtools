package com.github.paganini2008.devtools.jdbc.type;

import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.paganini2008.devtools.jdbc.JdbcType;

/**
 * BlobTypeHandler
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class BlobTypeHandler extends BaseTypeHandler {

	public BlobTypeHandler() {
		super();
	}

	protected void setNonNullValue(PreparedStatement ps, int parameterIndex, Object parameter, JdbcType jdbcType) throws SQLException {
		byte[] bytes = getJavaType().cast(parameter);
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ps.setBinaryStream(parameterIndex, bis, bytes.length);
	}

	protected byte[] getNullableValue(ResultSet rs, String columnName) throws SQLException {
		Blob blob = rs.getBlob(columnName);
		byte[] returnValue = null;
		if (null != blob) {
			returnValue = blob.getBytes(1, (int) blob.length());
		}
		return returnValue;
	}

	protected byte[] getNullableValue(ResultSet rs, int columnIndex) throws SQLException {
		Blob blob = rs.getBlob(columnIndex);
		byte[] returnValue = null;
		if (null != blob) {
			returnValue = blob.getBytes(1, (int) blob.length());
		}
		return returnValue;
	}

	public Class<byte[]> getJavaType() {
		return byte[].class;
	}

	public Object getNullableValue(CallableStatement cs, int columnIndex) throws SQLException {
		Blob blob = cs.getBlob(columnIndex);
		byte[] returnValue = null;
		if (null != blob) {
			returnValue = blob.getBytes(1, (int) blob.length());
		}
		return returnValue;
	}

}
