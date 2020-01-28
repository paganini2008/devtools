package com.github.paganini2008.transport.netty;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.paganini2008.transport.ChannelContext;
import com.github.paganini2008.transport.Partitioner;
import com.github.paganini2008.transport.Tuple;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;

/**
 * 
 * NettyChannelContext
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
@Sharable
public class NettyChannelContext extends NettyChannelContextAware implements ChannelContext<Channel> {

	private final List<Channel> holder = new CopyOnWriteArrayList<Channel>();

	public void addChannel(Channel channel, int weight) {
		for (int i = 0; i < weight; i++) {
			holder.add(channel);
		}
	}

	public Channel getChannel(SocketAddress address) {
		for (Channel channel : holder) {
			if (channel.remoteAddress() != null && channel.remoteAddress().equals(address)) {
				return channel;
			}
		}
		return null;
	}

	public void removeChannel(SocketAddress address) {
		for (Channel channel : holder) {
			if (channel.remoteAddress() != null && channel.remoteAddress().equals(address)) {
				holder.remove(channel);
			}
		}
	}

	public int countOfChannels() {
		return holder.size();
	}

	public Channel selectChannel(Tuple tuple, Partitioner partitioner) {
		return holder.isEmpty() ? null : partitioner.selectChannel(tuple, holder);
	}

	public Collection<Channel> getChannels() {
		return holder;
	}

}
