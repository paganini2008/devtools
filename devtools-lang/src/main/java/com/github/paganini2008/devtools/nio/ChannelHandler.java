package com.github.paganini2008.devtools.nio;

import java.io.IOException;
import java.util.List;

/**
 * 
 * ChannelHandler
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface ChannelHandler {

	default void fireChannelActive(Channel channel) throws IOException {
	}

	default void fireChannelInactive(Channel channel) throws IOException {
	}

	default void fireChannelReadable(Channel channel, List<Object> messages) throws IOException {
	}

	default void fireChannelFatal(Channel channel, Throwable e) {
	}

}
