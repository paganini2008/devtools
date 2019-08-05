package com.github.paganini2008.devtools.event;

/**
 * 
 * EventPubSub
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public interface EventPubSub {

	void publish(Event event);

	<E extends Event> void subscribe(EventSubscriber<E> subscriber);

	<E extends Event> void unsubscribe(EventSubscriber<E> subscriber);

}
