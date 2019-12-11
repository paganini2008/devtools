package com.github.paganini2008.devtools.collection;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 
 * CaseInsensitiveMap
 * 
 * @author Fred Feng
 * @created 2018-03
 * @revised 2019-07
 * @version 1.0
 */
public class CaseInsensitiveMap<V> extends KeyConversionMap<String, String, V> {

	private static final long serialVersionUID = -1990983691300106507L;
	private static final String NULL = "NULL";

	public CaseInsensitiveMap() {
		this(new LinkedHashMap<String, V>());
	}

	protected CaseInsensitiveMap(Map<String, V> delegate) {
		super(delegate);
	}

	protected String convertKey(Object key) {
		if (key != null) {
			return key.toString().toLowerCase(Locale.ENGLISH);
		}
		return NULL;
	}

	public static void main(String[] args) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("abc", 1);
		result.put("Ghj", 2);
		CaseInsensitiveMap<Object> data = new CaseInsensitiveMap<Object>();
		data.putAll(result);
		System.out.println(data);
		System.out.println(data.get("AbC"));
	}

}
