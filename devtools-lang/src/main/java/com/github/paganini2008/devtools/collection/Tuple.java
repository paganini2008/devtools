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
package com.github.paganini2008.devtools.collection;

import java.util.Map;

import com.github.paganini2008.devtools.CaseFormat;

/**
 * 
 * Tuple
 *
 * @author Fred Feng
 * @version 1.0
 */
public interface Tuple {

	default boolean isEmpty() {
		return size() == 0;
	}

	int size();

	Object get(String key);

	void set(String key, Object value);

	String[] keys();

	Object[] toValues();

	String getProperty(String key);

	String getProperty(String key, String defaultValue);

	<T> T getProperty(String key, Class<T> requiredType);

	<T> T getProperty(String key, Class<T> requiredType, T defaultValue);

	<T> T getRequiredProperty(String key, Class<T> requiredType);

	void fill(Object object);

	<T> T toBean(Class<T> requiredType);

	Map<String, Object> toMap();

	void append(Map<String, Object> m);

	static Tuple newTuple() {
		return newTuple(CaseFormat.DEFAULT);
	}

	static Tuple newTuple(CaseFormat caseFormat) {
		return new TupleImpl(caseFormat);
	}

	static Tuple wrap(Map<String, Object> kwargs) {
		return wrap(kwargs, CaseFormat.DEFAULT);
	}

	static Tuple wrap(Map<String, Object> kwargs, CaseFormat caseFormat) {
		Tuple tuple = newTuple(caseFormat);
		tuple.append(kwargs);
		return tuple;
	}

}
