package com.github.paganini2008.springboot.amber.config;

/**
 * 
 * SchedulerStateListener
 * 
 * @author Fred Feng
 * @created 2018-03
 */
public interface SchedulerStateListener {

	void onStart();

	void onShutingdown();

}
