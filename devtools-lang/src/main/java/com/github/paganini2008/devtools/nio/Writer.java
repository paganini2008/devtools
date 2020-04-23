package com.github.paganini2008.devtools.nio;

/**
 * 
 * Writer
 *
 * @author Fred Feng
 * @since 1.0
 */
public class Writer implements Runnable {

	public Writer(NioChannel channel) {
		this.channel = channel;
	}

	private final NioChannel channel;

	public void run() {
		System.out.println("channel.isActive(): " + channel.isActive());
	}
}
