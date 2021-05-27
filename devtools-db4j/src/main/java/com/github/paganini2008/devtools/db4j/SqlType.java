package com.github.paganini2008.devtools.db4j;

/**
 * 
 * SqlType
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface SqlType {

	/**
	 * Get jdbcType by name
	 * 
	 * @param name
	 * @return
	 */
	JdbcType getJdbcType(String name);

	/**
	 * Add name and jdbcType
	 * 
	 * @param name
	 * @param jdbcType
	 */
	void addJdbcType(String name, JdbcType jdbcType);

}
