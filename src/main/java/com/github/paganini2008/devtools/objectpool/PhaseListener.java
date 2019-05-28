package com.github.paganini2008.devtools.objectpool;

/**
 * PhaseListener
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface PhaseListener {

	/**
	 * Do something after object creation
	 * 
	 * @param o
	 */
	void onCreateObject(Object o);

	/**
	 * Do something before object destroy
	 * 
	 * @param o
	 */
	void onDestroyObject(Object o);

}
