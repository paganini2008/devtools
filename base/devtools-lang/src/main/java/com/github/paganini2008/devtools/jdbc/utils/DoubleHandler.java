package com.github.paganini2008.devtools.jdbc.utils;

import java.math.BigDecimal;

import com.github.paganini2008.devtools.jdbc.JdbcType;

public class DoubleHandler implements DataHandler<Double> {

	public String getText(String columnName, JdbcType jdbcType, Double value) {
		return value != null ? BigDecimal.valueOf(value).toPlainString() : null;
	}

	public Double getValue(String str) {
		return str != null ? Double.valueOf(str) : null;
	}

}
