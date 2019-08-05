package com.github.paganini2008.devtools.jdbc.utils;

import com.github.paganini2008.devtools.converter.ConvertUtils;
import com.github.paganini2008.devtools.jdbc.JdbcType;

public class BooleanHandler implements DataHandler<Boolean> {

	public String getText(String columnName, JdbcType jdbcType, Boolean value) {
		return value != null ? Boolean.toString(value.booleanValue()) : null;
	}

	public Boolean getValue(String str) {
		return ConvertUtils.convertValue(str, Boolean.class);
	}

}
