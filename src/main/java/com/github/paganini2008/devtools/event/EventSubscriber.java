package com.github.paganini2008.devtools.event;

/**
 * 
 * EventSubscriber
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public interface EventSubscriber<E extends Event> extends Comparable<EventSubscriber<E>>{ 

	void onEventFired(E event);

	default int compareTo(EventSubscriber<E> subscriber) {
		return 0;
	}

}
