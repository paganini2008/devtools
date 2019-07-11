package com.github.paganini2008.devtools.collection;

import java.util.Map;

/**
 * 
 * KeyInsensitiveMap
 * 
 * @author Fred Feng
 * @created 2018-03
 */
public abstract class KeyInsensitiveMap<V> extends KeyConversionMap<String, String, V> {

	private static final long serialVersionUID = 1L;

	protected KeyInsensitiveMap() {
		super();
	}

	protected KeyInsensitiveMap(Map<String, V> delegate) {
		super(delegate);
	}

	protected abstract String convertKey(Object key);

}
