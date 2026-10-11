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

import java.util.function.Function;

import com.github.paganini2008.devtools.beans.BeanUtils;
import com.github.paganini2008.devtools.beans.PropertyUtils;
import com.github.paganini2008.devtools.converter.ConvertUtils;

/**
 * 
 * Property
 * <p>
 * A function reading a (nested) bean property, converting it to the required
 * type if necessary.
 * 
 * @author Fred Feng
 * @date 2019-07-11
 * 
 * 
 * @since 2.0.1
 */
public class Property<E, T> implements Function<E, T> {

	private final String propertyName;
	private final Class<T> requiredType;

	Property(String propertyName, Class<T> requiredType) {
		this.propertyName = propertyName;
		this.requiredType = requiredType;
	}

	public T apply(E e) {
		return read(e, propertyName, requiredType);
	}

	/**
	 * Reads a simple or nested property. Unlike
	 * {@link PropertyUtils#getProperty(Object, String)}, a null intermediate bean
	 * (e.g. a null 'salesman' for "salesman.name") results in null, like a sql
	 * outer join does, instead of an exception.
	 * 
	 * @throws IllegalArgumentException if the bean is null or the property does
	 *             not exist
	 */
	static Object read(Object bean, String propertyName) {
		Object current = bean;
		String path = propertyName;
		int index;
		while (path != null && (index = path.indexOf('.')) > 0) {
			current = PropertyUtils.getProperty(current, path.substring(0, index));
			if (current == null) {
				return null;
			}
			path = path.substring(index + 1);
		}
		return PropertyUtils.getProperty(current, path);
	}

	/**
	 * Reads a simple or nested property converted to the required type like
	 * {@link BeanUtils#getProperty(Object, String, Class)}, see
	 * {@link #read(Object, String)} for the null intermediate beans. A value that
	 * cannot be converted results in null.
	 */
	@SuppressWarnings("unchecked")
	static <T> T read(Object bean, String propertyName, Class<T> requiredType) {
		Object value = read(bean, propertyName);
		if (value == null || requiredType == null) {
			return (T) value;
		}
		if (requiredType.isInstance(value)) {
			return requiredType.cast(value);
		}
		return ConvertUtils.convertValue(value, requiredType);
	}

	/**
	 * Creates a function reading the given (nested, e.g. "salesman.name") property.
	 */
	public static <E, T> Property<E, T> forName(String propertyName, Class<T> requiredType) {
		return new Property<E, T>(propertyName, requiredType);
	}

}
