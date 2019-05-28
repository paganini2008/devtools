package com.github.paganini2008.devtools.jdbc.utils;

import com.github.paganini2008.devtools.jdbc.JdbcType;

public class StringHandler implements DataHandler<String> {
	
	public String getText(String columnName, JdbcType jdbcType, String value) {
		return value;
	}

	public String getValue(String str) {
		return str;
	}

}
