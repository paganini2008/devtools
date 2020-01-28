package com.github.paganini2008.transport;

/**
 * 
 * LifeCycle
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public interface LifeCycle {

	void open();
	
	void close();
	
	boolean isOpened();
	
}
