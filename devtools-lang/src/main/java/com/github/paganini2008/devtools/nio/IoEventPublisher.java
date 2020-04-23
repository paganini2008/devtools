package com.github.paganini2008.devtools.nio;

import java.io.IOException;
import java.nio.channels.SelectableChannel;

/**
 * 
 * IoEventPublisher
 *
 * @author Fred Feng
 * @since 1.0
 */
public interface IoEventPublisher {

	void publishIoEvent(IoEvent event);

	void subscribeIoEvent(SelectableChannel channel, IoEventListener listener) throws IOException;
	
	void destroy();

}
