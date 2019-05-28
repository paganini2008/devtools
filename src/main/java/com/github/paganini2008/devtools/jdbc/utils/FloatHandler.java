package com.github.paganini2008.devtools.jdbc.utils;

import java.math.BigDecimal;

import com.github.paganini2008.devtools.jdbc.JdbcType;

public class FloatHandler implements DataHandler<Float> {

	public String getText(String columnName, JdbcType jdbcType, Float value) {
		return value != null ? BigDecimal.valueOf(value).toPlainString() : null;
	}

	public Float getValue(String str) {
		return str != null ? Float.valueOf(str) : null;
	}
}
