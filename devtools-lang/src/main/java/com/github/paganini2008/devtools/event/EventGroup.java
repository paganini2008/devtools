package com.github.paganini2008.devtools.event;

/**
 * 
 * EventGroup
 *
 * @author Jimmy Hoff
 * @since 1.0
 */
public interface EventGroup<E extends Event<T>, T> extends EventSubscriber<E, T> {

	void subscribe(EventSubscriber<E, T> subscriber);

	void unsubscribe(EventSubscriber<E, T> subscriber);

}
