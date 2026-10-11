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
package com.github.paganini2008.devtools.collection;

import java.util.Map;

import com.github.paganini2008.devtools.CaseFormat;

/**
 * 
 * Tuple
 * <p>
 * An ordered set of named values, typically a row of a query result, whose keys
 * are looked up after being normalized by a {@link CaseFormat}.
 *
 * @author Fred Feng
 * @date 2019-08-05
 * @since 2.0.1
 */
public interface Tuple {

	default boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * Returns the number of values.
	 */
	int size();

	/**
	 * Returns the value of the key, or null if absent.
	 */
	Object get(String key);

	/**
	 * Sets the value of the key, replacing the value of any key with the same normalized form.
	 */
	void set(String key, Object value);

	/**
	 * Returns the keys in insertion order.
	 */
	String[] keys();

	/**
	 * Returns the values in insertion order.
	 */
	Object[] toValues();

	/**
	 * Returns the value converted to a String, or null if absent.
	 */
	String getProperty(String key);

	/**
	 * Returns the value converted to a String, or the default value if absent.
	 */
	String getProperty(String key, String defaultValue);

	/**
	 * Returns the value converted to the required type, or null if absent or not convertible.
	 */
	<T> T getProperty(String key, Class<T> requiredType);

	/**
	 * Returns the value converted to the required type, or the default value if absent or not convertible.
	 */
	<T> T getProperty(String key, Class<T> requiredType, T defaultValue);

	/**
	 * Returns the value converted to the required type.
	 *
	 * @throws com.github.paganini2008.devtools.MissingKeyException if the value is absent
	 */
	<T> T getRequiredProperty(String key, Class<T> requiredType);

	/**
	 * Copies the non-null values into the matching properties of the bean.
	 */
	void fill(Object object);

	/**
	 * Creates a bean of the required type and fills it with the values.
	 */
	<T> T toBean(Class<T> requiredType);

	/**
	 * Returns a copy of the values as an ordered map.
	 */
	Map<String, Object> toMap();

	/**
	 * Adds all entries of the map, ignoring null.
	 */
	void append(Map<String, Object> m);

	/**
	 * Creates an empty tuple whose keys are used as they are.
	 */
	static Tuple newTuple() {
		return newTuple(CaseFormat.DEFAULT);
	}

	/**
	 * Creates an empty tuple whose keys are normalized by the case format.
	 */
	static Tuple newTuple(CaseFormat caseFormat) {
		return new TupleImpl(caseFormat);
	}

	/**
	 * Creates a tuple holding the entries of the map.
	 */
	static Tuple wrap(Map<String, Object> kwargs) {
		return wrap(kwargs, CaseFormat.DEFAULT);
	}

	/**
	 * Creates a tuple holding the entries of the map, whose keys are normalized by the case format.
	 */
	static Tuple wrap(Map<String, Object> kwargs, CaseFormat caseFormat) {
		Tuple tuple = newTuple(caseFormat);
		tuple.append(kwargs);
		return tuple;
	}

}
