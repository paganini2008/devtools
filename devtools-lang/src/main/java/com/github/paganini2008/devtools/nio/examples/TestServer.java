package com.github.paganini2008.devtools.nio.examples;

import com.github.paganini2008.devtools.nio.LoggingChannelHandler;

public class TestServer {

	public static void main(String[] args) throws Exception {
		NioAcceptor server = new NioAcceptor();
		//server.setReaderBufferSize(10 * 1024);
		LoggingChannelHandler handler = new LoggingChannelHandler();
		server.addHandler(handler);
		server.start();
		System.in.read();
		server.stop();
		System.out.println("TestServer.main()");
	}

}
