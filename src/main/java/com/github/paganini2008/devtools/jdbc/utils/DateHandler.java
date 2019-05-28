package com.github.paganini2008.devtools.jdbc.utils;

import java.sql.Date;

import com.github.paganini2008.devtools.date.DateUtils;
import com.github.paganini2008.devtools.jdbc.JdbcType;

public class DateHandler implements DataHandler<Date> {

	private final static String pattern = "yyyy-MM-dd";

	public String getText(String columnName, JdbcType jdbcType, Date value) {
		return value != null ? DateUtils.format(value, pattern) : null;
	}

	public Date getValue(String str) {
		if (str == null) {
			return null;
		}
		java.util.Date date = DateUtils.parse(str, pattern);
		return date != null ? new Date(date.getTime()) : null;
	}

}
