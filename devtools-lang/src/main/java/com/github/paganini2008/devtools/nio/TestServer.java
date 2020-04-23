package com.github.paganini2008.devtools.nio;

public class TestServer {

	public static void main(String[] args) throws Exception {
		NioServer reactor = new NioServer();
		reactor.setReaderBufferSize(1024 * 1024);
		reactor.addHandler(new TestChannelHandler());
		reactor.start();
		System.in.read();
		reactor.stop();
		System.out.println("TestServer.main()");
	}

}
