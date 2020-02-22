package com.github.paganini2008.devtools.db4j;

/**
 * SqlParameter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface SqlParameter {

	/**
	 * Contains value by name
	 * 
	 * @param name
	 * @return
	 */
	boolean hasValue(String name);

	/**
	 * Get value by name
	 * 
	 * @param name
	 * @return
	 */
	Object getValue(String name);

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
