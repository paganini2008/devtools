package com.github.paganini2008.springworld.socketbird.transport;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.springworld.socketbird.buffer.BufferZone;
import com.github.paganini2008.transport.ChannelStateListener;
import com.github.paganini2008.transport.Tuple;

/**
 * 
 * MinaServerHandler
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-12
 * @version 1.0
 */
public class MinaServerHandler extends IoHandlerAdapter {

	@Autowired
	private BufferZone store;

	@Value("${socketbird.bufferzone.collectionName}")
	private String collectionName;

	@Autowired
	private ChannelStateListener channelStateListener;

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		super.sessionOpened(session);
		channelStateListener.onConnected(session.getRemoteAddress());
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		super.sessionClosed(session);
		channelStateListener.onClosed(session.getRemoteAddress());
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		super.exceptionCaught(session, cause);
		channelStateListener.onError(session.getRemoteAddress(), cause);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		store.set(collectionName, (Tuple) message);
	}

}
