package com.github.paganini2008.devtools.collection;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * WeakReferenceMap
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class WeakReferenceMap<K, V> extends ReferenceMap<K, V> implements Map<K, V> {

	private final ReferenceQueue<V> rq;

	public WeakReferenceMap() {
		super(new ConcurrentHashMap<K, Reference<V>>());
		this.rq = new ReferenceQueue<V>();
	}

	public V put(K key, V value) {
		V prev = super.put(key, value);
		enableCapacity();
		return prev;
	}

	@SuppressWarnings("unchecked")
	private void enableCapacity() {
		Ref ref;
		while ((ref = (Ref) rq.poll()) != null) {
			remove(ref.key);
			onEviction(ref.key, ref.get());
		}
	}

	protected void onEviction(K key, V value) {
	}

	class Ref extends WeakReference<V> {

		final K key;

		Ref(K key, V value, ReferenceQueue<V> rq) {
			super(value, rq);
			this.key = key;
		}

	}

	protected Reference<V> fold(K key, V value) {
		return new Ref(key, value, rq);
	}

}