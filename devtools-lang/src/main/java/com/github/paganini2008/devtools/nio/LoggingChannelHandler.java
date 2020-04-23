package com.github.paganini2008.devtools.nio;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * LoggingChannelHandler
 *
 * @author Fred Feng
 * @since 1.0
 */
public class LoggingChannelHandler implements ChannelHandler {

	@Override
	public void fireChannelActive(Channel channel) throws IOException {
		System.out.println("TestChannelHandler.fireChannelActive(): " + channel.toString());
	}

	@Override
	public void fireChannelInactive(Channel channel) throws IOException {
		System.out.println("TestChannelHandler.fireChannelInactive(): " + channel.toString());
	}

	private final AtomicInteger counter = new AtomicInteger();

	@Override
	public void fireChannelReadable(Channel channel, MessagePacket packet) throws IOException {
		System.out.println("TestChannelHandler.fireChannelReadable(): " + packet.getLength());
		for (Object object : packet.getMessages()) {
			System.out.println("[" + counter.incrementAndGet() + "] 接收： " + object);
		}
	}

	@Override
	public void fireChannelFatal(Channel channel, Throwable e) {
		System.out.println("TestChannelHandler.fireChannelFatal(): " + channel);
		e.printStackTrace();
	}
	
	public int count() {
		return counter.get();
	}

}
