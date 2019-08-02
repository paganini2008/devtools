package com.github.paganini2008.devtools.jdbc.utils;

import java.util.Date;

import com.github.paganini2008.devtools.date.DateUtils;
import com.github.paganini2008.devtools.jdbc.JdbcType;

public class JavaDateHandler implements DataHandler<Date> {

	private final static String pattern = "yyyy-MM-dd HH:mm:ss";

	public String getText(String columnName, JdbcType jdbcType, Date value) {
		return value != null ? DateUtils.format(value, pattern) : null;
	}

	public Date getValue(String str) {
		if (str == null) {
			return null;
		}
		return DateUtils.parse(str, pattern);
	}

}
