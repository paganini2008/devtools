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
package com.github.paganini2008.devtools.converter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Base class of converters targeting a single type {@code T}. Sub-converters are registered per source type and the most
 * specific registered type assignable from the actual source class is used.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class BasicConverter<T> implements Converter<Object, T> {

	private final Map<Class<?>, Converter<?, T>> converterRegistry = new ConcurrentHashMap<Class<?>, Converter<?, T>>();

	/**
	 * Registers (or replaces) the sub-converter used for source values of the given type (or its subtypes).
	 */
	public void registerType(Class<?> javaType, Converter<?, T> converter) {
		converterRegistry.put(javaType, converter);
	}

	/**
	 * Removes the sub-converter registered for the given source type.
	 */
	public void removeType(Class<?> javaType) {
		converterRegistry.remove(javaType);
	}

	/**
	 * Returns the sub-converter registered exactly for the given source type, or null if none.
	 */
	public Converter<?, T> lookupType(Class<?> javaType) {
		return converterRegistry.get(javaType);
	}

	/**
	 * Checks whether a sub-converter is registered exactly for the given source type.
	 */
	public boolean hasType(Class<?> javaType) {
		return converterRegistry.containsKey(javaType);
	}

	/**
	 * Converts the value with the sub-converter registered for the most specific type assignable from the value's class.
	 * 
	 * @param value the value to convert, may be null
	 * @param defaultValue returned when the value is null or no sub-converter matches
	 * @return the converted value or the default value
	 */
	@SuppressWarnings("unchecked")
	public T convertValue(Object value, T defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		final Class<?> matches = getAssignableClass(value.getClass());
		if (matches != null) {
			Converter<Object, T> converter = (Converter<Object, T>) lookupType(matches);
			if (converter != null) {
				return converter.convertValue(value, defaultValue);
			}
		}
		return defaultValue;
	}

	/**
	 * Find the registered type which is the most specific one assignable from the given class.
	 * 
	 * @param actual the actual class of source value
	 * @return the matched registered type or null
	 */
	protected Class<?> getAssignableClass(Class<?> actual) {
		if (converterRegistry.containsKey(actual)) {
			return actual;
		}
		Class<?> bestMatch = null;
		for (Class<?> type : converterRegistry.keySet()) {
			if (type.isAssignableFrom(actual)) {
				if (bestMatch == null || bestMatch.isAssignableFrom(type)) {
					bestMatch = type;
				}
			}
		}
		return bestMatch;
	}

}
