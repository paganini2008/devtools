package com.github.paganini2008.devtools.objectpool;

/**
 * A container of some object.
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface ObjectPool {

	/**
	 * Borrow it until it's available.
	 * 
	 * @return
	 * @throws Exception
	 */
	Object borrowObject() throws Exception;

	/**
	 * Borrow it if you can in some time.
	 * 
	 * @param time
	 * @return
	 * @throws Exception
	 */
	Object borrowObject(int time) throws Exception;

	/**
	 * Please give back the pooled object on each calling.
	 * 
	 * @param o
	 * @throws Exception
	 */
	void givebackObject(Object o) throws Exception;

	/**
	 * Do it when the pooled object was invalid or expired
	 * 
	 * @param o
	 * @throws Exception
	 */
	void discardObject(Object o) throws Exception;

	/**
	 * Close it
	 * 
	 * @throws Exception
	 */
	void close() throws Exception;

}
