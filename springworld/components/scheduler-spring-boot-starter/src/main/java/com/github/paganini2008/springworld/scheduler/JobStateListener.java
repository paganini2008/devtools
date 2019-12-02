package com.github.paganini2008.springworld.scheduler;

/**
 * 
 * JobStateListener
 * 
 * @author Fred Feng
 * @created 2019-11
 * @version 1.0
 */
public interface JobStateListener {

	void onStartEveryTime();

	void onEndEveryTime();

}
