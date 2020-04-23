package com.github.paganini2008.devtools.nio;

import com.github.paganini2008.devtools.nio.ChannelEvent.EventType;

/**
 * 
 * ChannelEventListener
 *
 * @author Fred Feng
 * @since 1.0
 */
public interface ChannelEventListener {
	
	void onEventFired(ChannelEvent event);

	default EventType getEventType() {
		return EventType.ALL;
	}

}
