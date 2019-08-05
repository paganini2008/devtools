package com.github.paganini2008.devtools.jdbc.utils;

import com.github.paganini2008.devtools.jdbc.JdbcType;

public class ByteHandler implements DataHandler<Byte> {

	public String getText(String columnName, JdbcType jdbcType, Byte value) {
		return value != null ? Byte.toString(value.byteValue()) : null;
	}

	public Byte getValue(String str) {
		return str != null ? Byte.valueOf(str) : null;
	}
}
