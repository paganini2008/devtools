package com.github.paganini2008.devtools.jdbc.utils;

import com.github.paganini2008.devtools.jdbc.JdbcType;

public class ShortHandler implements DataHandler<Short> {

	public String getText(String columnName, JdbcType jdbcType, Short value) {
		return value != null ? Short.toString(value.shortValue()) : null;
	}

	public Short getValue(String str) {
		return str != null ? Short.valueOf(str) : null;
	}

}
