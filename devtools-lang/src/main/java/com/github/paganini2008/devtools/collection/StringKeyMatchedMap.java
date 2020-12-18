package com.github.paganini2008.devtools.collection;

import java.util.HashMap;

import com.github.paganini2008.devtools.MatchMode;

/**
 * 
 * StringKeyMatchedMap
 *
 * @author Jimmy Hoff
 * 
 * @version 1.0
 */
public class StringKeyMatchedMap<V> extends KeyMatchedMap<String, V> {

	private static final long serialVersionUID = 7256088542898472026L;

	public StringKeyMatchedMap(MatchMode matchMode) {
		super(new HashMap<String, V>());
		this.matchMode = matchMode;
	}

	private final MatchMode matchMode;

	@Override
	protected boolean apply(String key, Object inputKey) {
		return matchMode.matches(key, (String) inputKey);
	}

}
