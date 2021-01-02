package com.github.paganini2008.devtools.collection;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * CaseInsensitiveMap
 * 
 * @author Jimmy Hoff
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

	public static <V> CaseInsensitiveMap<V> treeMap() {
		return new CaseInsensitiveMap<V>(new TreeMap<String, V>());
	}

	public static <V> CaseInsensitiveMap<V> linkedHashMap() {
		return new CaseInsensitiveMap<V>(new LinkedHashMap<String, V>());
	}

	public static <V> CaseInsensitiveMap<V> concurrentHashMap() {
		return new CaseInsensitiveMap<V>(new ConcurrentHashMap<String, V>());
	}

}
