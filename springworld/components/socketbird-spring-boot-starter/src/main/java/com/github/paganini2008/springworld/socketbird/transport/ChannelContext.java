package com.github.paganini2008.springworld.socketbird.transport;

import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.paganini2008.springworld.socketbird.Tuple;
import com.github.paganini2008.springworld.socketbird.utils.Partitioner;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ChannelContext
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@Slf4j
public final class ChannelContext {

	private final List<ChannelWrapper> channels = new CopyOnWriteArrayList<ChannelWrapper>();

	public void addChannel(ChannelWrapper channel) {
		addChannel(channel, 1);
	}

	public void addChannel(ChannelWrapper channel, int weight) {
		for (int i = 0; i < weight; i++) {
			channels.add(channel);
		}
		log.info("add channel " + channel);
	}

	public ChannelWrapper getChannel(SocketAddress address) {
		for (ChannelWrapper channel : channels) {
			if (channel.getRemoteAddr().equals(address)) {
				return channel;
			}
		}
		return null;
	}

	public void removeChannel(SocketAddress address) {
		for (ChannelWrapper channel : channels) {
			if (channel.getRemoteAddr().equals(address)) {
				channels.remove(channel);
				log.info("remove channel " + channel);
				break;
			}
		}
	}

	public int countOfChannels() {
		return channels.size();
	}

	public ChannelWrapper selectChannel(Tuple tuple, Partitioner partitioner) {
		return channels.isEmpty() ? null : partitioner.select(tuple, channels);
	}

	public List<ChannelWrapper> getChannels() {
		return channels;
	}

}
