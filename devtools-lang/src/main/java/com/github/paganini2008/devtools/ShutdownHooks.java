package com.github.paganini2008.devtools;

import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * ShutdownHooks
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public final class ShutdownHooks {

	private final static HookThread instance = new HookThread();

	static {
		Runtime.getRuntime().addShutdownHook(instance);
	}

	static class HookThread extends Thread {

		final Queue<ShutdownHook> queue;

		HookThread() {
			this.queue = new PriorityBlockingQueue<ShutdownHook>();
		}

		public void run() {
			ShutdownHook hook;
			while (!queue.isEmpty()) {
				hook = queue.poll();
				try {
					hook.process();
					System.out.println("Hook '" + hook.toString() + "'is executed.");
				} catch (Throwable e) {
					hook.ignoreException(e);
				}
			}
		}
	}

	public static void addHook(ShutdownHook hook) {
		if (hook != null) {
			instance.queue.add(hook);
		}
	}

	public static void removeHook(ShutdownHook hook) {
		if (hook != null) {
			instance.queue.remove(hook);
		}
	}

	public static void cleanHooks() {
		instance.queue.clear();
	}

	public static int countOfHooks() {
		return instance.queue.size();
	}

	private ShutdownHooks() {
	}

}
