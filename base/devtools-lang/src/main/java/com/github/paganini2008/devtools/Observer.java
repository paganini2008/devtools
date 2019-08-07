package com.github.paganini2008.devtools;

/**
 * 
 * Observer
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public interface Observer extends Comparable<Observer> {

	void update(Observable ob, Object arg);

	default int compareTo(Observer other) {
		return 0;
	}
}