/**
* Copyright 2021 Fred Feng (paganini.fy@gmail.com)

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
 * BasicConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class BasicConverter<T> implements Converter<Object, T> {

	private final Map<Class<?>, Converter<?, T>> converterRegistry = new ConcurrentHashMap<Class<?>, Converter<?, T>>();

	public void registerType(Class<?> javaType, Converter<?, T> converter) {
		converterRegistry.put(javaType, converter);
	}

	public void removeType(Class<?> javaType) {
		converterRegistry.remove(javaType);
	}

	public Converter<?, T> lookupType(Class<?> javaType) {
		return converterRegistry.get(javaType);
	}

	public boolean hasType(Class<?> javaType) {
		return converterRegistry.containsKey(javaType);
	}

	@SuppressWarnings("unchecked")
	public T convertValue(Object value, T defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		final Class<?> matches = getAssignableClass(value.getClass());
		if (matches != null) {
			Converter<Object, T> converter = (Converter<Object, T>) lookupType(matches);
			return converter.convertValue(value, defaultValue);
		}
		return defaultValue;
	}

	protected Class<?> getAssignableClass(Class<?> actual) {
		for (Class<?> type : converterRegistry.keySet()) {
			if (type.equals(actual) || type.isAssignableFrom(actual)) {
				return type;
			}
		}
		return null;
	}

}
