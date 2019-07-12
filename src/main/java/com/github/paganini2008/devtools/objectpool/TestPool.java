package com.github.paganini2008.devtools.objectpool;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.paganini2008.devtools.Sequence;
import com.github.paganini2008.devtools.multithreads.ExecutorUtils;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

public class TestPool {

	public static class Resource {

		private final int id;

		public Resource(int id) {
			this.id = id;
		}

		public String say(int i) {
			return ThreadUtils.currentThreadName() + ", Hello: " + i;
		}

		public String toString() {
			return "Resource: " + id;
		}

	}

	public static class ResourceFactory implements ObjectFactory {

		private static final AtomicInteger seq = new AtomicInteger(0);

		public Object createObject() throws Exception {
			return new Resource(seq.incrementAndGet());
		}

		public void destroyObject(Object o) throws Exception {
			System.out.println("Destory: " + o);
		}

	}

	public static void main(String[] args) throws Exception {
		SimpleObjectPool objectPool = new SimpleObjectPool(10, new ResourceFactory());
		objectPool.setMaxIdleSize(3);
		Executor executor = Executors.newFixedThreadPool(50);
		AtomicInteger score = new AtomicInteger();
		for (final int i : Sequence.forEach(0, 10000)) {
			executor.execute(() -> {
				score.incrementAndGet();
				Resource resource = null;
				try {
					resource = (Resource) objectPool.borrowObject();
					ThreadUtils.randomSleep(1000L);
					System.out.println(
							resource.say(i) + " :: busySize: " + objectPool.getBusySize() + ", idleSize: " + objectPool.getIdleSize());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						objectPool.givebackObject(resource);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		System.in.read();
		System.out.println(score);
		objectPool.close();
		ExecutorUtils.gracefulShutdown(executor, 60000);
		System.out.println("TestMain.main()");
	}

}
