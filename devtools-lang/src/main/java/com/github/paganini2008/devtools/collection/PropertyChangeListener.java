package com.github.paganini2008.devtools.collection;

/**
 * 
 * PropertyChangeListener
 * 
 * @author Jimmy Hoff
 * 
 * @version 1.0
 */
public interface PropertyChangeListener<T> {

	void onEventFired(PropertyChangeEvent<T> propertyChangeEvent);

}
