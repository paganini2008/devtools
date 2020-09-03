package com.github.paganini2008.devtools.collection;

import java.util.EventObject;

/**
 * 
 * PropertyChangeEvent
 * 
 * @author Fred Feng
 * @create 2019-03
 */
public class PropertyChangeEvent<T> extends EventObject {

	private static final long serialVersionUID = -2624472937448392535L;

	public PropertyChangeEvent(T latest, T current) {
		super(latest);
		this.current = current;
	}

	private final T current;

	@SuppressWarnings("unchecked")
	public T getLatestVersion() {
		return (T) getSource();
	}

	public T getCurrentVersion() {
		return current;
	}

}
