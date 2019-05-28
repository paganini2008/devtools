package com.github.paganini2008.devtools.collection;

/**
 * 
 * PropertyChangeListener
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public interface PropertyChangeListener<T> {

	void onEventFired(PropertyChangeEvent<T> propertyChangeEvent);

}
