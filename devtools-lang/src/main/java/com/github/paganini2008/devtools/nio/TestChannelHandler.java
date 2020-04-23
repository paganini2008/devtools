package com.github.paganini2008.devtools.nio;

import java.io.IOException;
import java.util.List;

/**
 * 
 * TestChannelHandler
 *
 * @author Fred Feng
 * @since 1.0
 */
public class TestChannelHandler implements ChannelHandler {

	@Override
	public void fireChannelActive(Channel channel) throws IOException {
		System.out.println("TestChannelHandler.fireChannelActive(): " + channel.toString());
	}

	@Override
	public void fireChannelInactive(Channel channel) throws IOException {
		System.out.println("TestChannelHandler.fireChannelInactive(): " + channel.toString());
	}

	@Override
	public void fireChannelReadable(Channel channel, List<Object> messages) throws IOException {
		System.out.println("TestChannelHandler.fireChannelReadable(): " + messages.size());
		for (Object object : messages) {
			System.out.println("接收： " + object);
		}
	}

	@Override
	public void fireChannelFatal(Channel channel, Throwable e) {
		System.out.println("TestChannelHandler.fireChannelFatal(): " + channel);
		e.printStackTrace();
	}

}
