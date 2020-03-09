package com.github.paganini2008.devtools.collection;

import java.util.HashMap;

import com.github.paganini2008.devtools.CaseFormats;

/**
 * 
 * CamelCaseInsensitiveMap
 *
 * @author Fred Feng
 * @version 1.0
 */
public class CamelCaseInsensitiveMap<V> extends KeyConversionMap<String, String, V> {

	private static final long serialVersionUID = 9123521793974589929L;

	public CamelCaseInsensitiveMap() {
		super(new HashMap<String, V>());
	}

	@Override
	protected String convertKey(Object key) {
		if (key != null) {
			return CaseFormats.LOWER_CAMEL.toCase(key.toString());
		}
		return "";
	}

}
