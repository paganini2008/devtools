package com.github.paganini2008.devtools.event;

/**
 * 
 * EventPubSub
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public interface EventPubSub<E extends Event<T>, T> {

	void publish(E event);

	void subscribe(EventSubscriber<E, T> subscriber);

	void unsubscribe(EventSubscriber<E, T> subscriber);

}
