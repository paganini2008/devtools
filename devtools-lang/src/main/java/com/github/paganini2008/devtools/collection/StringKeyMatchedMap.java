package com.github.paganini2008.devtools.collection;

import java.util.HashMap;

import com.github.paganini2008.devtools.MatchMode;

/**
 * 
 * StringKeyMatchedMap
 *
 * @author Fred Feng
 * 
 * @version 1.0
 */
public class StringKeyMatchedMap<V> extends KeyMatchedMap<String, V> {

	private static final long serialVersionUID = 7256088542898472026L;

	public StringKeyMatchedMap(MatchMode matchMode) {
		this(matchMode, true);
	}

	public StringKeyMatchedMap(MatchMode matchMode, boolean matchFirst) {
		super(new HashMap<String, V>(), matchFirst);
		this.matchMode = matchMode;
	}

	private final MatchMode matchMode;

	@Override
	protected boolean match(String key, Object inputKey) {
		return matchMode.matches(key, (String) inputKey);
	}

}
