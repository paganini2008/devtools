package com.github.paganini2008.devtools.collection;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 
 * CaseInsensitiveMap
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class CaseInsensitiveMap<V> extends KeyConversionMap<String, String, V> {

	private static final long serialVersionUID = -1990983691300106507L;

	public CaseInsensitiveMap() {
		this(new HashMap<String, V>());
	}

	public CaseInsensitiveMap(Map<String, V> delegate) {
		super(delegate);
	}

	@Override
	protected String convertKey(Object key) {
		if (key != null) {
			return key.toString().toLowerCase(Locale.ENGLISH);
		}
		return "";
	}

}
