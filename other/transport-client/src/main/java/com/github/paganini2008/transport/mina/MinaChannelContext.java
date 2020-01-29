package com.github.paganini2008.transport.mina;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.mina.core.session.IoSession;

import com.github.paganini2008.transport.ChannelContext;
import com.github.paganini2008.transport.Partitioner;
import com.github.paganini2008.transport.Tuple;

/**
 * 
 * MinaChannelContext
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public class MinaChannelContext extends MinaChannelContextAware implements ChannelContext<IoSession> {

	private final List<IoSession> holder = new CopyOnWriteArrayList<IoSession>();

	public void addChannel(IoSession channel, int weight) {
		for (int i = 0; i < weight; i++) {
			holder.add(channel);
		}
	}

	public IoSession getChannel(SocketAddress address) {
		for (IoSession channel : holder) {
			if (channel.getRemoteAddress() != null && channel.getRemoteAddress().equals(address)) {
				return channel;
			}
		}
		return null;
	}

	public void removeChannel(SocketAddress address) {
		for (IoSession channel : holder) {
			if (channel.getRemoteAddress() != null && channel.getRemoteAddress().equals(address)) {
				holder.remove(channel);
			}
		}
	}

	public int countOfChannels() {
		return holder.size();
	}

	public IoSession selectChannel(Tuple tuple, Partitioner partitioner) {
		return holder.isEmpty() ? null : partitioner.selectChannel(tuple, holder);
	}

	public Collection<IoSession> getChannels() {
		return holder;
	}

}
