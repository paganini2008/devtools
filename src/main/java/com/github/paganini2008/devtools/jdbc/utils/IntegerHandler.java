package com.github.paganini2008.devtools.jdbc.utils;

import com.github.paganini2008.devtools.jdbc.JdbcType;

public class IntegerHandler implements DataHandler<Integer> {

	public String getText(String columnName, JdbcType jdbcType, Integer value) {
		return value != null ? Integer.toString(value.intValue()) : null;
	}

	public Integer getValue(String str) {
		return str != null ? Integer.valueOf(str) : null;
	}

}
