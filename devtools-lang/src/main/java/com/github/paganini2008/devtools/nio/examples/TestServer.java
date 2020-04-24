package com.github.paganini2008.devtools.nio.examples;

import com.github.paganini2008.devtools.nio.EmbedNioServer;
import com.github.paganini2008.devtools.nio.LoggingChannelHandler;

public class TestServer {

	public static void main(String[] args) throws Exception {
		EmbedNioServer server = new EmbedNioServer();
		LoggingChannelHandler handler = new LoggingChannelHandler();
		server.addHandler(handler);
		server.start();
		System.in.read();
		server.stop();
		System.out.println("TestServer.main()");
	}

}
