package com.github.paganini2008.transport.mina;

import java.net.SocketAddress;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.github.paganini2008.transport.ChannelContext;
import com.github.paganini2008.transport.ChannelEvent;
import com.github.paganini2008.transport.ChannelEvent.EventType;
import com.github.paganini2008.transport.ChannelEventListener;
import com.github.paganini2008.transport.ConnectionWatcher;

/**
 * 
 * MinaChannelContextAware
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public abstract class MinaChannelContextAware extends IoHandlerAdapter implements ChannelContext<IoSession> {

	private ConnectionWatcher connectionWatcher;
	private ChannelEventListener<IoSession> channelEventListener;

	public ConnectionWatcher getConnectionWatcher() {
		return connectionWatcher;
	}

	public void setConnectionWatcher(ConnectionWatcher connectionWatcher) {
		this.connectionWatcher = connectionWatcher;
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		addChannel(session);

		fireChannelEvent(session, EventType.CONNECTED, null);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		removeChannel(session.getRemoteAddress());

		fireReconnectionIfNecessary(session.getRemoteAddress());
		fireChannelEvent(session, EventType.CLOSED, null);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		session.closeNow();

		SocketAddress remoteAddress = session.getRemoteAddress();
		removeChannel(remoteAddress);

		fireReconnectionIfNecessary(session.getRemoteAddress());
		fireChannelEvent(session, EventType.FAULTY, cause);
	}

	public void setChannelEventListener(ChannelEventListener<IoSession> channelEventListener) {
		this.channelEventListener = channelEventListener;
	}

	public ChannelEventListener<IoSession> getChannelEventListener() {
		return channelEventListener;
	}

	private void fireChannelEvent(IoSession channel, EventType eventType, Throwable cause) {
		if (channelEventListener != null) {
			channelEventListener.fireChannelEvent(new ChannelEvent<IoSession>(channel, eventType, cause));
		}
	}

	private void fireReconnectionIfNecessary(SocketAddress remoteAddress) {
		if (connectionWatcher != null) {
			connectionWatcher.reconnect(remoteAddress);
		}
	}

}
