package com.github.paganini2008.devtools.nio.examples;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

import com.github.paganini2008.devtools.nio.EmbedNioClient;
import com.github.paganini2008.devtools.nio.LoggingChannelHandler;

public class TestClient {

	public static void main(String[] args) throws IOException, InterruptedException {
		EmbedNioClient client = new EmbedNioClient();
		client.setWriterBatchSize(100);
		client.setWriterBufferSize(4 * 1024);
		client.addHandler(new LoggingChannelHandler());
		client.connect(new InetSocketAddress(8090));
		System.in.read();
		for (int i = 0; i < 50000; i++) {
			client.write(new Item("fengy_" + i, toFullString()));
		}
		Thread.sleep(60 * 60 * 1000L);
		client.close();
		System.out.println("TestClient.main()");
	}

	private static String toFullString() {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < 10; i++) {
			str.append(UUID.randomUUID().toString());
		}
		return str.toString();
	}

}
