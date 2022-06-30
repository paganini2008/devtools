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
package com.github.paganini2008.devtools.beans.streaming;

import java.util.Map;
import java.util.function.Function;

import com.github.paganini2008.devtools.converter.ConvertUtils;

/**
 * 
 * Mapping
 * 
 * @author Fred Feng
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
		try {
			return requiredType.cast(value);
		} catch (RuntimeException e) {
			return ConvertUtils.convertValue(value, requiredType);
		}
	}

	public static <T> Mapping<T> forName(String name, Class<T> requiredType) {
		return new Mapping<T>(name, requiredType);
	}

}
