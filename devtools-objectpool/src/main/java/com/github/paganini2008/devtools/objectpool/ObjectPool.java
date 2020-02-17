package com.github.paganini2008.devtools.objectpool;

/**
 * 
 * ObjectPool
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface ObjectPool {

	/**
	 * Get the pooled object's detail
	 * 
	 * @param object
	 * @return
	 */
	ObjectDetail getDetail(Object object);

	/**
	 * Borrow one from pool until it's available.
	 * 
	 * @return
	 * @throws Exception
	 */
	Object borrowObject() throws Exception;

	/**
	 * Borrow one from pool within given time(ms).
	 * 
	 * @param timeout
	 * @return
	 * @throws Exception
	 */
	Object borrowObject(long timeout) throws Exception;

	/**
	 * Give back the pooled object to pool.
	 * 
	 * @param object
	 * @throws Exception
	 */
	void givebackObject(Object object) throws Exception;

	/**
	 * Discard this one when it was invalid or expired.
	 * 
	 * @param object
	 * @throws Exception
	 */
	void discardObject(Object object) throws Exception;

	/**
	 * Close the pool.
	 * 
	 * @throws Exception
	 */
	void close() throws Exception;

}
