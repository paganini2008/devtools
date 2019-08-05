package com.github.paganini2008.devtools.jdbc.utils;

import java.sql.Time;

import com.github.paganini2008.devtools.date.DateUtils;
import com.github.paganini2008.devtools.jdbc.JdbcType;

public class TimeHandler implements DataHandler<Time> {

	private final static String pattern = "HH:mm:ss";

	public String getText(String columnName, JdbcType jdbcType, Time value) {
		return value != null ? DateUtils.format(value, pattern) : null;
	}

	public Time getValue(String str) {
		return str != null ? Time.valueOf(str) : null;
	}

}
