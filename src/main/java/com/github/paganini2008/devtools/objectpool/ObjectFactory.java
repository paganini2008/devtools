package com.github.paganini2008.devtools.objectpool;

/**
 * Implement this interface to build your object factory. To manage the objects
 * on different stage.
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface ObjectFactory {

	/**
	 * Create some objects by parameter 'perNewSize' when the pool is
	 * initializing or the idleSize is 0.
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
	boolean validateObject(Object o) throws Exception;

	/**
	 * Close/Release a object and remove it from the pool.
	 * 
	 * @param o
	 * @throws Exception
	 */
	void destroyObject(Object o) throws Exception;

}
