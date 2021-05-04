package com.github.paganini2008.devtools;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 
 * Provider
 * 
 * @author Jimmy Hoff
 *
 * @version 1.0
 */
public abstract class Provider<T, R> implements Function<T, R> {

	private final Map<T, R> cache = new ConcurrentHashMap<T, R>();

	@Override
	public R apply(T key) {
		R result = cache.get(key);
		if (result == null) {
			cache.putIfAbsent(key, createObject(key));
			result = cache.get(key);
		}
		return null;
	}

	protected abstract R createObject(T key);

}
