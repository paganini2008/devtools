package com.github.paganini2008.devtools.event;

/**
 * 
 * EventSubscriber
 * 
 * @author Jimmy Hoff
 * 
 * @version 1.0
 */
public interface EventSubscriber<E extends Event<T>, T> extends Comparable<EventSubscriber<E, T>> {

	void onEventFired(E event);

	default boolean isPubSub() {
		return true;
	}

	default int compareTo(EventSubscriber<E, T> subscriber) {
		return 0;
	}

}
