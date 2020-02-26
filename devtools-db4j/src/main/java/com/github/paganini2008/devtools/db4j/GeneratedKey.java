package com.github.paganini2008.devtools.db4j;

import java.util.Map;

/**
 * KeyHolder
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface GeneratedKey {

	String[] getKeyNames();

	Number getKey();

	Object getKey(String keyName);

	void setKeys(Map<String, Object> map);

	static GeneratedKey forNames(String... columnNames) {
		return new GeneratedKeyImpl(columnNames);
	}

}
