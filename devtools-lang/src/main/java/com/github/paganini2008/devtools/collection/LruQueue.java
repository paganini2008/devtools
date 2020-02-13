package com.github.paganini2008.devtools.collection;

import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * LruQueue
 * 
 * @author Fred Feng
 * @created 2016-08
 * @version 1.0
 */
public class LruQueue<E> extends AbstractQueue<E> implements Queue<E>, Serializable {

	private static final long serialVersionUID = 7243844578649162100L;

	public LruQueue() {
		this(128);
	}

	public LruQueue(int maxSize) {
		this(new ConcurrentLinkedQueue<E>(), maxSize);
	}

	public LruQueue(final Queue<E> delegate, final int maxSize) {
		this.delegate = delegate;
		this.maxSize = maxSize;
		this.keys = Collections.synchronizedMap(new LinkedHashMap<Integer, E>(16, 0.75F, true) {

			private static final long serialVersionUID = -4000636962958369285L;

			protected boolean removeEldestEntry(Map.Entry<Integer, E> eldest) {
				boolean result;
				if (result = size() > maxSize) {
					delegate.remove(eldest.getValue());
					onEviction(eldest.getValue());
				}
				return result;
			}
		});
	}

	private final Queue<E> delegate;
	private final int maxSize;
	private final AtomicInteger index = new AtomicInteger(0);
	private final Map<Integer, E> keys;

	public boolean offer(E e) {
		keys.put(index.incrementAndGet(), e);
		return delegate.offer(e);
	}

	public E poll() {
		keys.remove(index.get() - maxSize - 1);
		return delegate.poll();
	}

	public E peek() {
		keys.get(index.get() - maxSize - 1);
		return delegate.peek();
	}

	public Iterator<E> iterator() {
		return delegate.iterator();
	}

	public int size() {
		return delegate.size();
	}

	protected void onEviction(E e) {
	}

	public String toString() {
		return delegate.toString();
	}

	public static void main(String[] args) {
		LruQueue<String> q = new LruQueue<String>(1000);
		for (int i = 0; i < 1000000; i++) {
			q.add(String.valueOf(i));
		}
		System.out.println(q.poll());
		System.out.println(q.poll());
		System.out.println(q.poll());
		System.out.println(q.poll());
		System.out.println(q.poll());
	}

}
