package com.github.paganini2008.devtools.db4j;

/**
 * SqlParameters
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface SqlParameters {

	int getSize();

	boolean hasValue(int index, String name);

	Object getValue(int index, String name);

	JdbcType getJdbcType(String name);

	void addJdbcType(String name, JdbcType jdbcType);

}
