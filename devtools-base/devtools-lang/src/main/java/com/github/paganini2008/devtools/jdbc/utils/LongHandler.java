package com.github.paganini2008.devtools.jdbc.utils;

import com.github.paganini2008.devtools.jdbc.JdbcType;

public class LongHandler implements DataHandler<Long> {

	public String getText(String columnName, JdbcType jdbcType, Long value) {
		return value != null ? Long.toString(value.longValue()) : null;
	}

	public Long getValue(String str) {
		return str != null ? Long.valueOf(str) : null;
	}

}
