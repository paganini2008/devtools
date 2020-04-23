package com.github.paganini2008.devtools.nio;

import com.github.paganini2008.devtools.nio.IoEvent.EventType;

/**
 * 
 * IoEventHandler
 *
 * @author Fred Feng
 * @since 1.0
 */
public interface IoEventHandler {

	void onEventFired(IoEvent event);

	EventType getEventType();

}
