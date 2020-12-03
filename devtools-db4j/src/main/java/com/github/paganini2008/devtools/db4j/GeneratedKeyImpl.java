package com.github.paganini2008.devtools.db4j;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.github.paganini2008.devtools.collection.MapUtils;

/**
 * GeneratedKeyImpl
 * 
 * @author Jimmy Hoff
 * @version 1.0
 */
public class GeneratedKeyImpl implements GeneratedKey {

	GeneratedKeyImpl(String... columnNames) {
		keys = new LinkedHashMap<String, Object>();
		if (columnNames != null) {
			for (String columnName : columnNames) {
				keys.put(columnName, null);
			}
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

	public Map<String, Object> getKeys() {
		return Collections.unmodifiableMap(keys);
	}

	public Object getKey(String keyName) {
		return keys.get(keyName);
	}

	public String[] getKeyNames() {
		return keys.keySet().toArray(new String[keys.size()]);
	}

	public void setKeys(Map<String, Object> map) {
		if (MapUtils.isNotEmpty(map)) {
			if (keys.isEmpty()) {
				keys.putAll(map);
			} else {
				Object[] values = map.values().toArray();
				if (values.length == keys.size()) {
					int i = 0;
					for (Map.Entry<String, Object> entry : keys.entrySet()) {
						entry.setValue(values[i++]);
					}
				}
			}
		}
	}

}
