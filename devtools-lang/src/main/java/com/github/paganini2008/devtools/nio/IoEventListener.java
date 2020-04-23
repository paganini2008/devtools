package com.github.paganini2008.devtools.nio;

import com.github.paganini2008.devtools.nio.IoEvent.EventType;

/**
 * 
 * IoEventListener
 *
 * @author Fred Feng
 * @since 1.0
 */
public interface IoEventListener {

	void onEventFired(IoEvent event);

	EventType getEventType();

}
