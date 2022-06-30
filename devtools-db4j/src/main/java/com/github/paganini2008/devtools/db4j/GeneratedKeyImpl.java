/**
* Copyright 2017-2022 Fred Feng (paganini.fy@gmail.com)

* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.github.paganini2008.devtools.db4j;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.github.paganini2008.devtools.collection.MapUtils;

/**
 * GeneratedKeyImpl
 * 
 * @author Fred Feng
 * @since 2.0.1
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
