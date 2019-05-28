package com.github.paganini2008.devtools.jdbc;

/**
 * ParameterSource
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface ParameterSource {

	/**
	 * Contains value by paramName
	 * 
	 * @param paramName
	 * @return
	 */
	boolean hasValue(String paramName);

	/**
	 * Get value by paramName
	 * 
	 * @param paramName
	 * @return
	 */
	Object getValue(String paramName);

	/**
	 * Get jdbcType by paramName
	 * 
	 * @param paramName
	 * @return
	 */
	JdbcType getJdbcType(String paramName);

	/**
	 * Add paramName and jdbcType
	 * 
	 * @param paramName
	 * @param jdbcType
	 */
	void addJdbcType(String paramName, JdbcType jdbcType);

}
