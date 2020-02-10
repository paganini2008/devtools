package com.github.paganini2008.springworld.transport.transport;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.github.paganini2008.springworld.transport.buffer.BufferZone;
import com.github.paganini2008.transport.ChannelEvent;
import com.github.paganini2008.transport.ChannelEvent.EventType;
import com.github.paganini2008.transport.ChannelEventListener;
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

	@Value("${spring.transport.bufferzone.collectionName:default}")
	private String collectionName;

	@Autowired(required = false)
	private ChannelEventListener<IoSession> channelEventListener;

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		fireChannelEvent(session, EventType.CONNECTED, null);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		fireChannelEvent(session, EventType.CLOSED, null);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		cause.printStackTrace();
		fireChannelEvent(session, EventType.FAULTY, cause);
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		store.set(collectionName, (Tuple) message);
	}

	private void fireChannelEvent(IoSession channel, EventType eventType, Throwable cause) {
		if (channelEventListener != null) {
			channelEventListener.fireChannelEvent(new ChannelEvent<IoSession>(channel, eventType, cause));
		}
	}

}
