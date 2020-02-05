package com.github.paganini2008.springworld.cluster.pool;

/**
 * 
 * ProcessPool
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
public interface ProcessPool {

	static final String TOPIC_IDENTITY = "process-pool";

	void submit(String beanName, Class<?> beanClass, String methodName, Object... arguments);
	
	void shutdown();

}
