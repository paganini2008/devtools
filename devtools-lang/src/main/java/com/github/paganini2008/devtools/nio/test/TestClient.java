package com.github.paganini2008.devtools.nio.test;

import java.net.InetSocketAddress;
import java.util.UUID;

import com.github.paganini2008.devtools.nio.ChannelHandler;
import com.github.paganini2008.devtools.nio.LoggingChannelHandler;
import com.github.paganini2008.devtools.nio.examples.Item;

public class TestClient {

	public static void main(String[] args) throws Exception {
		AioConnector client = new AioConnector();
		client.setWriterBufferSize(20 * 1024);
		client.setWriterBatchSize(10);
		ChannelHandler handler = new LoggingChannelHandler();
		client.addHandler(handler);
		try {
		client.connect(new InetSocketAddress("127.0.0.1",8090));
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		System.in.read();
		for (int i = 0; i < 10000; i++) {
			client.write(new Item("fengy_" + i, toFullString()));
		}
		Thread.sleep(60 * 60 * 1000L);
		client.close();
		System.out.println("TestClient.main()");
	}

	private static String toFullString() {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < 100; i++) {
			str.append(UUID.randomUUID().toString());
		}
		return str.toString();
	}

}
