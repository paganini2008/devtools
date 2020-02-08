package com.github.paganini2008.transport;

import java.net.SocketAddress;
import java.util.Collection;

/**
 * 
 * ChannelContext
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public interface ChannelContext<T> {

	default void addChannel(T channel) {
		addChannel(channel, 1);
	}

	void addChannel(T channel, int weight);

	T getChannel(SocketAddress address);

	void removeChannel(SocketAddress address);

	int countOfChannels();

	T selectChannel(Tuple tuple, Partitioner partitioner);

	Collection<T> getChannels();

	void setChannelEventListener(ChannelEventListener<T> channelEventListener);

}