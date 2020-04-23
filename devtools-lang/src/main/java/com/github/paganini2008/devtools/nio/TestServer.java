package com.github.paganini2008.devtools.nio;

public class TestServer {

	public static void main(String[] args) throws Exception {
		EmbedNioServer reactor = new EmbedNioServer();
		reactor.setReaderBufferSize(1024 * 1024);
		LoggingChannelHandler handler = new LoggingChannelHandler();
		reactor.addHandler(handler);
		reactor.start();
		System.in.read();
		System.out.println("Answer: " + handler.count());
		reactor.stop();
		System.out.println("TestServer.main()");
	}

}
