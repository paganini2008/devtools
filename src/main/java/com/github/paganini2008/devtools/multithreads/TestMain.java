package com.github.paganini2008.devtools.multithreads;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.paganini2008.devtools.Sequence;

public class TestMain {

	static class Worker {
		public void test(int i) {
			System.out.println(ThreadUtils.currentThreadName()+ " TestMain.Worker.test(): " + i);
		}

		public void destroy() {
			System.out.println("TestMain.Worker.destroy()");
		}
	}

	static class PoolManager {

		final LinkedList<Worker> idleQueue = new LinkedList<Worker>();
		final LinkedList<Worker> busyQueue = new LinkedList<Worker>();
		final int maxPoolSize;
		volatile int poolSize = 0;

		PoolManager(int maxPoolSize) {
			this.maxPoolSize = maxPoolSize;
		}

		synchronized void giveback(Worker workerThread) {
			busyQueue.remove(workerThread);
			idleQueue.add(workerThread);
		}

		synchronized Worker borrow() {
			Worker workerThread = idleQueue.pollFirst();
			if (workerThread == null) {
				if (poolSize < maxPoolSize) {
					workerThread = new Worker();
					poolSize++;
				}
			}
			if (workerThread != null) {
				busyQueue.add(workerThread);
			}
			return workerThread;
		}

		synchronized void destroy() {
			while (!busyQueue.isEmpty()) {
				ThreadUtils.randomSleep(1000L);
			}
			while (!idleQueue.isEmpty()) {
				destroy(idleQueue.pollFirst());
			}
		}

		synchronized void retain(int n) {
			int l = idleQueue.size();
			if (l > n) {
				for (int i = n; i < l; i++) {
					destroy(idleQueue.pollFirst());
				}
			}
		}

		synchronized void destroy(Worker workerThread) {
			workerThread.destroy();
			poolSize--;
		}

	}

	public static void main(String[] args) throws IOException {
		Executor executor = Executors.newFixedThreadPool(50);
		PoolManager poolManager = new PoolManager(10);
		AtomicInteger score = new AtomicInteger();
		for (int i : Sequence.forEach(0, 100)) {
			executor.execute(() -> {
				Worker worker = poolManager.borrow();
				if (worker != null) {
					worker.test(i);
					//ThreadUtils.randomSleep(1000L);
					score.incrementAndGet();
					poolManager.giveback(worker);
				} else {
					System.out.println(ThreadUtils.currentThreadName() + " failed: " + i);
				}
			});
		}
		System.in.read();
		System.out.println(score);
		ExecutorUtils.gracefulShutdown(executor, 60000);
		System.out.println("TestMain.main()");
	}

}
