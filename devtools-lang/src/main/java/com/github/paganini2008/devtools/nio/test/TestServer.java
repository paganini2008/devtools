package com.github.paganini2008.devtools.nio.test;

import com.github.paganini2008.devtools.nio.LoggingChannelHandler;

public class TestServer {

	public static void main(String[] args) throws Exception {
		AioAcceptor server = new AioAcceptor();
		server.setReaderBufferSize(10 * 1024);
		LoggingChannelHandler handler = new LoggingChannelHandler();
		server.addHandler(handler);
		server.start();
		System.in.read();
		server.stop();
		System.out.println("TestServer.main()");
	}

}
