package com.github.paganini2008.springworld.cluster.pool;

/**
 * 
 * Signature
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
public interface Signature {
	
	String getBeanName();

	String getBeanClassName();
	
	String getMethodName();
	
	Object[] getArguments();
	
	long getTimestamp();
	
}
