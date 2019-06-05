package com.github.paganini2008.devtools;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TestMain {

	public static void main(String[] args) throws Exception {
		BlockingQueue<String> q = new LinkedBlockingQueue<String>(10);
		for(int i=0;i<12;i++) {
			q.add(UUID.randomUUID().toString());
		}
		System.out.println(q.size());
	}

}
