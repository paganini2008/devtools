package com.github.paganini2008.devtools.objectpool;

/**
 * Customized ObjectFactory interface to manage some reused objects.
 * 
 * @author Jimmy Hoff
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
	 * Verify the pooled object when it is idle or borrowed.
	 * 
	 * @param object
	 * @return
	 * @throws Exception
	 */
	default boolean testObject(Object object) throws Exception {
		return true;
	}

	/**
	 * Release the pooled object and remove it from the pool.
	 * 
	 * @param object
	 * @throws Exception
	 */
	void destroyObject(Object object) throws Exception;

}
