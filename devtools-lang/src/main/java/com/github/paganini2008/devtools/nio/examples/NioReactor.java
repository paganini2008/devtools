package com.github.paganini2008.devtools.nio.examples;

import java.io.IOError;
import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.paganini2008.devtools.logging.Log;
import com.github.paganini2008.devtools.logging.LogFactory;

/**
 * 
 * NioReactor
 *
 * @author Fred Feng
 * @since 1.0
 */
public abstract class NioReactor implements Runnable {

	protected final Log logger = LogFactory.getLog(getClass());
	private final AtomicBoolean opened = new AtomicBoolean();
	protected final Executor executor;
	protected final Selector selector;

	protected NioReactor(Executor executor) {
		try {
			this.selector = Selector.open();
		} catch (IOException e) {
			throw new IOError(e);
		}
		this.executor = executor;
	}

	private long timeout = 1000L;

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	protected void register(SelectableChannel channel, int ops, Object attachment) throws IOException {
		selector.wakeup();
		channel.register(selector, ops, attachment);
		if (!isOpened()) {
			opened.set(true);
			executor.execute(this);
		}
	}
	
	public boolean isOpened() {
		return opened.get();
	}

	public void destroy() {
		opened.set(false);
		selector.wakeup();
	}

	@Override
	public void run() {
		int keys = 0;
		if (isOpened()) {
			try {
				keys = timeout > 0 ? selector.select(timeout) : selector.selectNow();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
			if (keys > 0) {
				Iterator<?> keyIterator = selector.selectedKeys().iterator();
				while (keyIterator.hasNext()) {
					SelectionKey selectionKey = (SelectionKey) keyIterator.next();
					if (selectionKey.isValid() && isSelectable(selectionKey)) {
						try {
							process(selectionKey);
						} catch (IOException e) {
							logger.error(e.getMessage(), e);
						}
					}
					keyIterator.remove();
				}
			}
		}
		if (isOpened()) {
			executor.execute(this);
		}
	}
	
	protected abstract boolean isSelectable(SelectionKey selectionKey);

	protected abstract void process(SelectionKey selectionKey) throws IOException;


}
