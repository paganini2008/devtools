package com.github.paganini2008.devtools.nio;

import java.util.concurrent.Executor;

import com.github.paganini2008.devtools.event.EventBus;
import com.github.paganini2008.devtools.event.EventSubscriber;
import com.github.paganini2008.devtools.nio.ChannelEvent.EventType;

/**
 * 
 * DefaultChannelEventPublisher
 *
 * @author Fred Feng
 * @since 1.0
 */
public class DefaultChannelEventPublisher implements ChannelEventPublisher {

	private final EventBus<ChannelEvent, Object> delegate;

	public DefaultChannelEventPublisher(Executor executor) {
		this.delegate = new EventBus<>(executor, true);
	}

	public void publishChannelEvent(ChannelEvent event) {
		delegate.publish(event);
	}

	public void subscribeChannelEvent(ChannelHandler channelHandler) {
		subscribeChannelEvent(new ChannelEventListenerAdaptor(channelHandler));
	}

	public void subscribeChannelEvent(final ChannelEventListener listener) {

		delegate.subscribe(new EventSubscriber<ChannelEvent, Object>() {
			@Override
			public void onEventFired(ChannelEvent event) {
				if (listener.getEventType() == ChannelEvent.EventType.ALL || listener.getEventType() == event.getEventType()) {
					listener.onEventFired(event);
				}
			}
		});
	}
	
	@Override
	public void destroy() {
		delegate.close();
	}

	/**
	 * 
	 * ChannelEventListenerAdaptor
	 *
	 * @author Fred Feng
	 * @since 1.0
	 */
	private class ChannelEventListenerAdaptor implements ChannelEventListener {

		private final ChannelHandler handler;

		ChannelEventListenerAdaptor(ChannelHandler handler) {
			this.handler = handler;
		}

		@Override
		public void onEventFired(ChannelEvent event) {
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
					handler.fireChannelReadable(channel, event.getMessagePacket());
					break;
				case FATAL:
					handler.fireChannelFatal(channel, event.getCause());
					break;
				default:
					break;
				}
			} catch (Throwable e) {
				publishChannelEvent(new ChannelEvent(channel, EventType.FATAL, null, e));
				channel.close();
			}
		}

	}

}
