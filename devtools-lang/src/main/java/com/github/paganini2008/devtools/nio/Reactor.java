package com.github.paganini2008.devtools.nio;

import java.io.IOError;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.concurrent.Executor;

/**
 * 
 * Reactor
 *
 * @author Fred Feng
 * @since 1.0
 */
public final class Reactor {

	private final Selector selector;
	private final IoEventPublisher ioEventPublisher;
	private final ChannelEventPublisher channelEventPublisher;

	public Reactor(Executor ioThreads, Executor channelThreads) {
		try {
			selector = Selector.open();
		} catch (IOException e) {
			throw new IOError(e);
		}
		ioEventPublisher = new DefaultIoEventPublisher(selector, ioThreads);
		channelEventPublisher = new DefaultChannelEventPublisher(channelThreads);
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
			ioEventPublisher.publishIoEvent(new IoEvent(this, selectionKey));
			it.remove();
		}
	}

	public void wakeup() {
		selector.wakeup();
	}

	public void close() throws IOException {
		try {
			selector.close();
		} finally {
			ioEventPublisher.destroy();
			channelEventPublisher.destroy();
		}
	}

	public IoEventPublisher getIoEventPublisher() {
		return ioEventPublisher;
	}

	public ChannelEventPublisher getChannelEventPublisher() {
		return channelEventPublisher;
	}

}
