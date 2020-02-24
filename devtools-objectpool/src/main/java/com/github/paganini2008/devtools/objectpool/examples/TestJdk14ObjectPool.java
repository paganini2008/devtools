package com.github.paganini2008.devtools.objectpool.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.paganini2008.devtools.Sequence;
import com.github.paganini2008.devtools.multithreads.ExecutorUtils;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;
import com.github.paganini2008.devtools.objectpool.GenericObjectPool;
import com.github.paganini2008.devtools.objectpool.Jdk14ObjectPool;
import com.github.paganini2008.devtools.objectpool.ObjectFactory;

/**
 * 
 * TestJdk14ObjectPool
 *
 * @author Fred Feng
 * @version 1.0
 */
public class TestJdk14ObjectPool {

	public static class Resource {

		private final int id;

		public Resource(int id) {
			this.id = id;
		}

		public String doSomething(int i) {
			return ThreadUtils.currentThreadName() + " do something: " + i;
		}

		public String toString() {
			return "ID: " + id;
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
		GenericObjectPool objectPool = new GenericObjectPool(new ResourceFactory());
		objectPool.setMaxPoolSize(10);
		objectPool.setMaxIdleSize(3);
		Executor executor = Executors.newFixedThreadPool(10);
		AtomicInteger score = new AtomicInteger();
		List<Throwable> errors = new ArrayList<>();
		for (final int i : Sequence.forEach(0, 10000)) {
			executor.execute(() -> {
				score.incrementAndGet();
				Resource resource = null;
				try {
					resource = (Resource) objectPool.borrowObject();
					// ThreadUtils.randomSleep(1000L);
					System.out.println(
							resource.doSomething(i) + " :: busySize: " + objectPool.getBusySize() + ", idleSize: " + objectPool.getIdleSize());
				} catch (Exception e) {
					e.printStackTrace();
					errors.add(e);
				} finally {
					try {
						objectPool.givebackObject(resource);
						//objectPool.givebackObject(resource);
						//objectPool.givebackObject(resource);
						//objectPool.givebackObject(resource);
						//objectPool.givebackObject(resource);
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
		if (errors.size() > 0) {
			System.out.println(errors);
		}
		System.out.println("Done.");
	}

}
