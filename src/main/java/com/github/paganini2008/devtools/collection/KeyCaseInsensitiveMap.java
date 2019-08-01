package com.github.paganini2008.devtools.collection;

import java.util.Map;

import com.github.paganini2008.devtools.Case;

/**
 * 
 * KeyCaseInsensitiveMap
 * 
 * @author Fred Feng
 * @created 2018-03
 */
public class KeyCaseInsensitiveMap<V> extends KeyConversionMap<String, String, V> {

	private static final long serialVersionUID = -1990983691300106507L;
	private static final String NULL = "NULL";

	public KeyCaseInsensitiveMap(Case format) {
		super();
		this.format = format;
	}

	public KeyCaseInsensitiveMap(Map<String, V> m, Case format) {
		super(m);
		this.format = format;
	}

	private final Case format;

	protected String convertKey(Object key) {
		if (key != null) {
			return format.toCase(key.toString());
		}
		return NULL;
	}

	public Case getCase() {
		return format;
	}

}
