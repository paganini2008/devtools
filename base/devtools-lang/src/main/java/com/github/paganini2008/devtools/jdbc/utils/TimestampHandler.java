package com.github.paganini2008.devtools.jdbc.utils;

import java.sql.Timestamp;
import java.util.Date;

import com.github.paganini2008.devtools.date.DateUtils;
import com.github.paganini2008.devtools.jdbc.JdbcType;

public class TimestampHandler implements DataHandler<Timestamp> {

	private final static String pattern = "yyyy-MM-dd HH:mm:ss";

	public String getText(String columnName, JdbcType jdbcType, Timestamp value) {
		return value != null ? DateUtils.format(value, pattern) : null;
	}

	public Timestamp getValue(String str) {
		if (str == null) {
			return null;
		}
		Date date = DateUtils.parse(str, pattern);
		return date != null ? new Timestamp(date.getTime()) : null;
	}

}
