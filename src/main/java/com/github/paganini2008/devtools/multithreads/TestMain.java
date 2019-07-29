package com.github.paganini2008.devtools.multithreads;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.paganini2008.devtools.Sequence;
import com.github.paganini2008.devtools.multithreads.latch.RecursiveLatch;

public class TestMain {

	public static ThreadPoolBuilder common(int maxPoolSize) {
		ThreadPoolBuilder builder = new ThreadPoolBuilder();
		return builder.setMaxPoolSize(maxPoolSize).setLatch(new RecursiveLatch(maxPoolSize/2)).setQueueSize(Integer.MAX_VALUE).setTimeout(-1L)
				.setThreadFactory(new PooledThreadFactory());
	}

	public static void main(String[] args) throws IOException {
		final ThreadPool threadPool = common(10).build();
		final AtomicInteger score = new AtomicInteger();
		for (int i : Sequence.forEach(0, 100)) {
			threadPool.execute(() -> {
				System.out.println(ThreadUtils.currentThreadName()+ " say: " + i);
				score.incrementAndGet();
			});
		}
		System.in.read();
		System.out.println(score);
		threadPool.shutdown();
		System.out.println("TestMain.main()");
	}

}
