package com.github.paganini2008.devtools.db4j;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.github.paganini2008.devtools.StringUtils;

/**
 * GeneratedKeyHolder
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class GeneratedKeyHolder implements KeyHolder {

	public GeneratedKeyHolder() {
		keys = new LinkedHashMap<String, Object>();
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

	public KeyHolder addKeyName(String keyName) {
		if (StringUtils.isNotBlank(keyName)) {
			keys.put(keyName, null);
		}
		return this;
	}

	public String[] getKeyNames() {
		return keys.keySet().toArray(new String[keys.size()]);
	}

	public void load(Map<String, Object> map) {
		Object[] values = map.values().toArray();
		int index = 0;
		for (Map.Entry<String, Object> entry : keys.entrySet()) {
			if (index < values.length) {
				entry.setValue(values[index++]);
			}
		}
	}

}
