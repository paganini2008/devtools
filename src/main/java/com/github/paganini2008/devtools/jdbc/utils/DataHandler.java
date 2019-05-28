package com.github.paganini2008.devtools.jdbc.utils;

import com.github.paganini2008.devtools.jdbc.JdbcType;

public interface DataHandler<T> {

	String getText(String columnName, JdbcType jdbcType, T value);

	T getValue(String str);

}
