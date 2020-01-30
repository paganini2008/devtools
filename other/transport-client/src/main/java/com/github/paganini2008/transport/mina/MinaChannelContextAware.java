package com.github.paganini2008.transport.mina;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import com.github.paganini2008.transport.ChannelContext;
import com.github.paganini2008.transport.ChannelStateListener;

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

	private ChannelStateListener channelStateListener;

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		addChannel(session);
		if (channelStateListener != null) {
			channelStateListener.onConnected(session.getRemoteAddress());
		}
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		removeChannel(session.getRemoteAddress());
		if (channelStateListener != null) {
			channelStateListener.onClosed(session.getRemoteAddress());
		}
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		cause.printStackTrace();
		session.closeNow();
		if (channelStateListener != null) {
			channelStateListener.onError(session.getRemoteAddress(), cause);
		}
	}

	public void setChannelStateListener(ChannelStateListener channelStateListener) {
		this.channelStateListener = channelStateListener;
	}

}
