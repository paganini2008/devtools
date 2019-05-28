package com.github.paganini2008.devtools.beans;

/**
 * Getter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface Getter<E, T> {

	/**
	 * Get the value from a object or map.
	 * 
	 * @param entity
	 * @return
	 */
	T apply(E entity);

}
