package com.github.paganini2008.devtools.nio;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.Selector;
import java.util.concurrent.Executor;

import com.github.paganini2008.devtools.event.EventBus;
import com.github.paganini2008.devtools.event.EventSubscriber;
import com.github.paganini2008.devtools.nio.IoEvent.EventType;

/**
 * 
 * DefaultIoEventPublisher
 *
 * @author Fred Feng
 * @since 1.0
 */
public class DefaultIoEventPublisher implements IoEventPublisher {

	private final EventBus<IoEvent, Object> delegate;
	private final Selector selector;

	public DefaultIoEventPublisher(Selector selector, Executor executor) {
		this.selector = selector;
		this.delegate = new EventBus<>(executor, true);
	}

	@Override
	public void publishIoEvent(IoEvent event) {
		EventType eventType = event.getEventType();
		if (eventType.isAsynchronous()) {
			delegate.publish(event);
		} else {
			IoEventListener listener = (IoEventListener) event.getSelectionKey().attachment();
			listener.onEventFired(event);
		}
	}

	@Override
	public void subscribeIoEvent(final SelectableChannel channel, final IoEventListener listener) throws IOException {
		EventType eventType = listener.getEventType();
		channel.register(selector, eventType.getOperationCode(), listener);
		if (eventType.isAsynchronous()) {
			delegate.subscribe(new EventSubscriber<IoEvent, Object>() {
				@Override
				public void onEventFired(IoEvent event) {
					listener.onEventFired(event);
				}
			});
		}
	}

	@Override
	public void destroy() {
		delegate.close();
	}

}
