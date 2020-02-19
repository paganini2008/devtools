package com.github.paganini2008.devtools.db4j;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * GeneratedKeyImpl
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class GeneratedKeyImpl implements GeneratedKey {

	GeneratedKeyImpl(String... columnNames) {
		keys = new LinkedHashMap<String, Object>();
		for (String columnName : columnNames) {
			keys.put(columnName, null);
		}
	}

	private final Map<String, Object> keys;

	public Number getKey() {
		Iterator<Map.Entry<String, Object>> it = keys.entrySet().iterator();
		if (it.hasNext()) {
			Map.Entry<String, Object> entry = it.next();
			if (entry.getValue() instanceof Number) {
				return ((Number) entry.getValue());
			}
		}
		return null;
	}

	public Object getKey(String keyName) {
		return keys.get(keyName);
	}

	public String[] getKeyNames() {
		return keys.keySet().toArray(new String[keys.size()]);
	}

	public void setValues(Map<String, Object> map) {
		if (map != null) {
			keys.putAll(map);
		}
	}

}
