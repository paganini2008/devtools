package com.github.paganini2008.springworld.scheduler;

/**
 * 
 * JobRunningControl
 * 
 * @author Fred Feng
 * @created 2019-11
 * @revised 2019-11
 * @version 1.0
 */
public interface JobRunningControl {

	boolean isRunning();

	void pause();

	void resume();

}
