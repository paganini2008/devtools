package com.github.paganini2008.devtools.collection;

import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.paganini2008.devtools.ObjectUtils;

/**
 * 
 * LruQueue
 * 
 * @author Jimmy Hoff
 * 
 * @version 1.0
 */
public class LruQueue<E> extends AbstractQueue<E> implements Queue<E>, Serializable, BoundedCollection<E> {

	private static final long serialVersionUID = -6416314527516614700L;

	public LruQueue() {
		this(128);
	}

	public LruQueue(final int maxSize) {
		this(new ConcurrentLinkedQueue<E>(), maxSize);
	}

	public LruQueue(final int maxSize, final LruMapSupplier<Integer, E> supplier) {
		this(new ConcurrentLinkedQueue<E>(), maxSize, supplier);
	}

	public LruQueue(final Queue<E> delegate, final int maxSize) {
		this(delegate, maxSize,
				(size, listener) -> Collections.synchronizedMap(MapUtils.newLruMap(16, size, listener)));
	}

	public LruQueue(final Queue<E> delegate, final int maxSize, final LruMapSupplier<Integer, E> supplier) {
		this.delegate = delegate;
		this.maxSize = maxSize;
		this.keys = supplier.get(maxSize, (key, value) -> {
			delegate.remove(value);
			onEviction(value);
		});
	}

	private final Queue<E> delegate;
	private final int maxSize;
	private final AtomicInteger index = new AtomicInteger(0);
	private final Map<Integer, E> keys;

	@Override
	public boolean offer(E e) {
		keys.put(index.getAndIncrement(), e);
		return delegate.offer(e);
	}

	@Override
	public E poll() {
		keys.remove(index.get() - maxSize - 1);
		return delegate.poll();
	}

	@Override
	public E peek() {
		keys.get(index.get() - maxSize - 1);
		return delegate.peek();
	}

	@Override
	public boolean contains(Object o) {
		keys.get(indexFor(o));
		return delegate.contains(o);
	}

	@Override
	public boolean remove(Object o) {
		keys.remove(indexFor(o));
		return delegate.remove(o);
	}

	@Override
	public Iterator<E> iterator() {
		return delegate.iterator();
	}

	@Override
	public int size() {
		return delegate.size();
	}

	private int indexFor(Object o) {
		for (Map.Entry<Integer, E> entry : keys.entrySet()) {
			if (ObjectUtils.equals(o, entry.getValue())) {
				return entry.getKey();
			}
		}
		return -1;
	}

	@Override
	public String toString() {
		return delegate.toString();
	}

	public static void main(String[] args) {
	}

}
