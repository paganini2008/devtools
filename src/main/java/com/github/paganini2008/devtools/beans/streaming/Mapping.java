/**
* Copyright 2017-2026 Fred Feng (paganini.fy@gmail.com)

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
package com.github.paganini2008.devtools.beans.streaming;

import java.util.Map;
import java.util.function.Function;

import com.github.paganini2008.devtools.converter.ConvertUtils;

/**
 * 
 * Mapping
 * <p>
 * A function reading a named value from a map, converting it to the required
 * type if necessary.
 * 
 * @author Fred Feng
 * @date 2019-07-11
 * 
 * @since 2.0.1
 */
public class Mapping<T> implements Function<Map<String, Object>, T> {

	private final String name;
	private final Class<T> requiredType;

	Mapping(String name, Class<T> requiredType) {
		this.name = name;
		this.requiredType = requiredType;
	}

	public T apply(Map<String, Object> m) {
		Object value = m.get(name);
		if (value == null || requiredType.isInstance(value)) {
			return requiredType.cast(value);
		}
		return ConvertUtils.convertValue(value, requiredType);
	}

	/**
	 * Creates a mapping reading the value of the given key.
	 */
	public static <T> Mapping<T> forName(String name, Class<T> requiredType) {
		return new Mapping<T>(name, requiredType);
	}

}
