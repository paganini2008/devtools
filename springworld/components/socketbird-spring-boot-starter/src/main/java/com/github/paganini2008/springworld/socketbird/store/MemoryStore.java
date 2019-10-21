package com.github.paganini2008.springworld.socketbird.store;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.github.paganini2008.devtools.collection.LruQueue;
import com.github.paganini2008.springworld.socketbird.Tuple;

/**
 * 
 * MemoryStore
 * 
 * @author Fred Feng
 * @created 2019-10
 * @revised 2019-10
 * @version 1.0
 */
@Component
@ConditionalOnProperty(name = "socketbird.store", havingValue = "memory", matchIfMissing = true)
public class MemoryStore implements Store {

	private final ConcurrentMap<String, LruQueue<Tuple>> cache = new ConcurrentHashMap<String, LruQueue<Tuple>>();

	private final int maxSize;

	public MemoryStore() {
		this(1024);
	}

	public MemoryStore(int maxSize) {
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

}
