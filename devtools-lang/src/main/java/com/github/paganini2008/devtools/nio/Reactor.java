package com.github.paganini2008.devtools.nio;

import java.io.IOError;
import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

import com.github.paganini2008.devtools.event.EventBus;
import com.github.paganini2008.devtools.event.EventSubscriber;
import com.github.paganini2008.devtools.nio.ChannelEvent.EventType;

/**
 * 
 * Reactor
 *
 * @author Fred Feng
 * @since 1.0
 */
public final class Reactor {

	private final EventBus<IoEvent, Object> ioEventPublisher;
	private final EventBus<ChannelEvent, Object> channelEventPublisher;
	private final Selector selector;

	public Reactor(Executor ioThreads, Executor channelThreads) {
		try {
			selector = Selector.open();
		} catch (IOException e) {
			throw new IOError(e);
		}
		ioEventPublisher = new EventBus<>(ioThreads, true);
		channelEventPublisher = new EventBus<>(channelThreads, true);
	}

	public void select(long timeout) throws IOException {
		if (timeout > 0) {
			selector.select(timeout);
		} else {
			selector.select();
		}
		Iterator<?> it = selector.selectedKeys().iterator();
		while (it.hasNext()) {
			SelectionKey selectionKey = (SelectionKey) it.next();
			System.out.println("::: acceptable=" + selectionKey.isAcceptable() + ",readable=" + selectionKey.isReadable() + ",writable="
					+ selectionKey.isWritable() + ",connectable=" + selectionKey.isConnectable());
			IoEventHandler handler = (IoEventHandler) selectionKey.attachment();
			handler.onEventFired(new IoEvent(this, selectionKey));
			it.remove();
		}
	}

	public void publishChannelEvent(Channel channel, EventType eventType) {
		publishChannelEvent(channel, eventType, null, null);
	}

	public void publishChannelEvent(Channel channel, EventType eventType, List<Object> messages, Throwable cause) {
		channelEventPublisher.publish(new ChannelEvent(this, channel, eventType, messages, cause));
	}

	public void wakeup() {
		selector.wakeup();
	}

	public void close() throws IOException {
		selector.close();
	}

	public void subscribeChannelEvent(ChannelHandler channelHandler) {
		subscribeChannelEvent(new ChannelEventHandlerAdaptor(channelHandler));
	}

	public void subscribeChannelEvent(final ChannelEventHandler handler) {

		channelEventPublisher.subscribe(new EventSubscriber<ChannelEvent, Object>() {
			@Override
			public void onEventFired(ChannelEvent event) {
				if (handler.getEventType() == ChannelEvent.EventType.ALL || handler.getEventType() == event.getEventType()) {
					handler.handleEvent(event);
				}
			}
		});
	}

	void registerIoEvent(final SelectableChannel channel, final IoEventHandler handler) throws IOException {
		IoEvent.EventType type = handler.getEventType();
		channel.register(selector, type.getOperationCode(), handler);
	}

	/**
	 * 
	 * ChannelEventHandlerAdaptor
	 *
	 * @author Fred Feng
	 * @since 1.0
	 */
	private class ChannelEventHandlerAdaptor implements ChannelEventHandler {

		private final ChannelHandler handler;

		ChannelEventHandlerAdaptor(ChannelHandler handler) {
			this.handler = handler;
		}

		@Override
		public void handleEvent(ChannelEvent event) {
			Channel channel = event.getChannel();
			try {
				switch (event.getEventType()) {
				case ACTIVE:
					handler.fireChannelActive(channel);
					break;
				case INACTIVE:
					handler.fireChannelInactive(channel);
					break;
				case READABLE:
					handler.fireChannelReadable(channel, event.getMessages());
					break;
				case FATAL:
					handler.fireChannelFatal(channel, event.getCause());
					break;
				default:
					break;
				}
			} catch (Throwable e) {
				publishChannelEvent(channel, EventType.FATAL, null, e);
				channel.close();
			}
		}

	}

}
