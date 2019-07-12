package com.github.paganini2008.devtools.objectpool;

/**
 * Implement this interface to build your object factory. To manage the objects
 * in it.
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface ObjectFactory {

	/**
	 * Create object if unavaliable
	 * 
	 * @return
	 * @throws Exception
	 */
	Object createObject() throws Exception;

	/**
	 * Validate/Test a object if valid/actived/opened or not.
	 * 
	 * @param o
	 * @return
	 * @throws Exception
	 */
	default boolean testObject(Object o) throws Exception {
		return true;
	}

	/**
	 * Close/Release a object and remove it from the pool.
	 * 
	 * @param o
	 * @throws Exception
	 */
	void destroyObject(Object o) throws Exception;

}
