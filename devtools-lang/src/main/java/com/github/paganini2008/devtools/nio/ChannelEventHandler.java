package com.github.paganini2008.devtools.nio;

import com.github.paganini2008.devtools.nio.ChannelEvent.EventType;

/**
 * 
 * ChannelEventHandler
 *
 * @author Fred Feng
 * @since 1.0
 */
public interface ChannelEventHandler {
	
	void handleEvent(ChannelEvent event);

	default EventType getEventType() {
		return EventType.ALL;
	}

}
