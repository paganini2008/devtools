package com.github.paganini2008.devtools.collection;

import java.util.Map;

/**
 * 
 * KeyCaseInsensitiveMap
 * 
 * @author Fred Feng
 * @created 2018-03
 */
public abstract class KeyCaseInsensitiveMap<V> extends KeyInsensitiveMap<String, String, V> {

	private static final long serialVersionUID = 1L;

	protected KeyCaseInsensitiveMap() {
		super();
	}

	protected KeyCaseInsensitiveMap(Map<String, V> delegate) {
		super(delegate);
	}

	protected abstract String convertKey(Object key);

}
