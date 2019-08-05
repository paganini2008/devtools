package com.github.paganini2008.devtools.jdbc;

/**
 * ParameterListSource
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface ParameterListSource {

	int getSize();

	boolean hasValue(int index, String paramName);

	Object getValue(int index, String paramName);

	JdbcType getJdbcType(String paramName);

	void addJdbcType(String paramName, JdbcType jdbcType);

}
