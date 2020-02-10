package com.github.paganini2008.springworld.transport.transport;

import org.apache.mina.core.session.IoSession;

import com.github.paganini2008.transport.ChannelEvent;
import com.github.paganini2008.transport.ChannelEvent.EventType;
import com.github.paganini2008.transport.ChannelEventListener;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * MinaChannelEventListener
 *
 * @author Fred Feng
 * @created 2020-01
 * @revised 2020-02
 * @version 1.0
 */
@Slf4j
public class MinaChannelEventListener implements ChannelEventListener<IoSession> {

	@Override
	public void fireChannelEvent(ChannelEvent<IoSession> channelEvent) {
		if (log.isTraceEnabled()) {
			IoSession session = channelEvent.getSource();
			EventType eventType = channelEvent.getEventType();
			switch (eventType) {
			case CONNECTED:
				log.trace(session.getRemoteAddress() + " has established connection.");
				break;
			case CLOSED:
				log.trace(session.getRemoteAddress() + " has loss connection.");
				break;
			case PING:
				log.trace(session.getRemoteAddress() + " send a ping.");
				break;
			case PONG:
				log.trace(session.getRemoteAddress() + " send a pong.");
				break;
			case FAULTY:
				log.trace(session.getRemoteAddress() + " has loss connection for fatal reason.", channelEvent.getCause());
				break;
			}
		}
	}

}
