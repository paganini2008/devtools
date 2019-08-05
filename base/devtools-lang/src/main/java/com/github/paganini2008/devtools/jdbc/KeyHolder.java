package com.github.paganini2008.devtools.jdbc;

import java.util.Map;

/**
 * KeyHolder
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface KeyHolder {

	KeyHolder addKeyName(String keyName);

	String[] getKeyNames();

	Number getKey();

	Object getKey(String keyName);

	void load(Map<String, Object> map);

}
