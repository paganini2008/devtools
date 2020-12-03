package com.github.paganini2008.devtools.multithreads;

/**
 * 
 * ThreadLocalLong
 *
 * @author Jimmy Hoff
 * @version 1.0
 */
public class ThreadLocalLong extends Number {

	private static final long serialVersionUID = 1107286858663072809L;
	private final ThreadLocal<Long> threadLocal;

	public ThreadLocalLong() {
		this(0);
	}

	public ThreadLocalLong(final long value) {
		threadLocal = new ThreadLocal<Long>() {
			protected Long initialValue() {
				return value;
			}
		};
	}

	public long getAndDecrement() {
		return getAndAdd(-1);
	}

	public long getAndIncrement() {
		return getAndAdd(1);
	}

	public long getAndAdd(long delta) {
		long prev = get();
		threadLocal.set(prev + delta);
		return prev;
	}

	public long decrementAndGet() {
		return addAndGet(-1);
	}

	public long incrementAndGet() {
		return addAndGet(1);
	}

	public long addAndGet(long delta) {
		long prev = get();
		threadLocal.set(prev + delta);
		return threadLocal.get();
	}
	
	public void reset() {
		threadLocal.remove();
	}

	public void set(long delta) {
		threadLocal.set(delta);
	}

	public long get() {
		return threadLocal.get().longValue();
	}

	@Override
	public int intValue() {
		return (int) get();
	}

	@Override
	public long longValue() {
		return get();
	}

	@Override
	public float floatValue() {
		return (float) get();
	}

	@Override
	public double doubleValue() {
		return (double) get();
	}

	@Override
	public String toString() {
		return String.valueOf(get());
	}

}
