package com.github.paganini2008.devtools.multithreads;

/**
 * 
 * ThreadPools
 * 
 * @author Fred Feng
 * @revised 2019-05
 * @version 1.0
 */
public abstract class ThreadPools {

	public static ThreadPool newSimplePool(int maxPoolSize) {
		return newSimplePool(maxPoolSize, 1000L, Integer.MAX_VALUE);
	}

	public static ThreadPool newSimplePool(int maxPoolSize, long timeout, int queueSize) {
		return new SimpleThreadPool(maxPoolSize, timeout, queueSize);
	}

	public static ThreadPool newCommonPool(int maxPoolSize) {
		return newCommonPool(maxPoolSize, 1000L, Integer.MAX_VALUE);
	}

	public static ThreadPool newCommonPool(int maxPoolSize, long timeout, int queueSize) {
		return new JdkExecutorThreadPool(maxPoolSize, timeout, queueSize);
	}

	public static <T> AsyncThreadPool<T> newAsyncPool(ThreadPool delegate) {
		return new AsyncThreadPoolImpl<T>(delegate);
	}

	public static <T> AsyncThreadPool<T> newAsyncPool(int maxPoolSize, long timeout, int queueSize) {
		return newAsyncPool(newCommonPool(maxPoolSize, timeout, queueSize));
	}

	public static <T> AsyncThreadPool<T> newAsyncPool(int maxPoolSize) {
		return newAsyncPool(maxPoolSize, 1000L, Integer.MAX_VALUE);
	}

}
