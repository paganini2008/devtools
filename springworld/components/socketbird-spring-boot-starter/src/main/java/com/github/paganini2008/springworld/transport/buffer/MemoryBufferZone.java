package com.github.paganini2008.springworld.transport.buffer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.github.paganini2008.devtools.collection.LruQueue;
import com.github.paganini2008.transport.Tuple;

/**
 * 
 * MemoryBufferZone
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
public class MemoryBufferZone implements BufferZone {

	private final ConcurrentMap<String, LruQueue<Tuple>> cache = new ConcurrentHashMap<String, LruQueue<Tuple>>();

	private final int maxSize;

	public MemoryBufferZone() {
		this(1024);
	}

	public MemoryBufferZone(int maxSize) {
		this.maxSize = maxSize;
	}

	@Override
	public void set(String name, Tuple tuple) {
		LruQueue<Tuple> q = cache.get(name);
		if (q == null) {
			cache.putIfAbsent(name, new LruQueue<Tuple>(maxSize));
			q = cache.get(name);
		}
		q.offer(tuple);
	}

	@Override
	public Tuple get(String name) {
		LruQueue<Tuple> q = cache.get(name);
		return q != null ? q.poll() : null;
	}

	@Override
	public int size(String name) {
		LruQueue<Tuple> q = cache.get(name);
		return q != null ? q.size() : 0;
	}

}
