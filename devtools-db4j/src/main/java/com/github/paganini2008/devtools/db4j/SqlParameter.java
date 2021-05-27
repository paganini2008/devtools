package com.github.paganini2008.devtools.db4j;

/**
 * SqlParameter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface SqlParameter extends SqlType {

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

}
