package com.github.paganini2008.devtools.event;

/**
 * 
 * EventSubscriber
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public interface EventSubscriber<E extends Event<T>, T> extends Comparable<EventSubscriber<E, T>> {

	void onEventFired(E event);

	default int compareTo(EventSubscriber<E, T> subscriber) {
		return 0;
	}

}
