package com.github.paganini2008.devtools.nio.examples;

import java.nio.channels.SelectionKey;
import java.util.concurrent.Executor;

import com.github.paganini2008.devtools.nio.Channel;

/**
 * 
 * NioReader
 *
 * @author Fred Feng
 * @since 1.0
 */
public class NioReader extends NioReactor {

	NioReader(Executor executor) {
		super(executor);
	}

	@Override
	protected boolean isSelectable(SelectionKey selectionKey) {
		return selectionKey.isReadable();
	}

	@Override
	protected void process(SelectionKey selectionKey) {
		Channel channel = (Channel) selectionKey.attachment();
		if (channel != null) {
			channel.read();
		}
	}

}
