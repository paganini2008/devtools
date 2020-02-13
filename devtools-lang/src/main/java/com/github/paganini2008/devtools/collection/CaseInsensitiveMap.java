package com.github.paganini2008.devtools.collection;

import java.util.LinkedHashMap;
import java.util.Locale;

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
		super(new LinkedHashMap<String, V>());
	}

	protected String convertKey(Object key) {
		if (key != null) {
			return key.toString().toLowerCase(Locale.ENGLISH);
		}
		return NULL;
	}

}
