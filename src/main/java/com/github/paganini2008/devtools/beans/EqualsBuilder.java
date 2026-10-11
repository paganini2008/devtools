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
package com.github.paganini2008.devtools.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import com.github.paganini2008.devtools.ObjectUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.reflection.MethodUtils;

/**
 * Compares two beans for equality by the values of their readable properties.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * 
 * 
 * @since 2.0.1
 */
public abstract class EqualsBuilder {

	/**
	 * Compares all readable properties of two beans of the same class.
	 * 
	 * <pre>
	 * reflectionEquals(null, null)        = true
	 * reflectionEquals(bean, null)        = false
	 * reflectionEquals(person, employee)  = false (different classes)
	 * </pre>
	 * If the class has no readable property, {@link Object#equals(Object)} is used instead.
	 */
	public static boolean reflectionEquals(Object source, Object target) {
		return reflectionEquals(source, target, (PropertyFilter) null);
	}

	/**
	 * Compares the readable properties accepted by the filter.
	 */
	public static boolean reflectionEquals(Object source, Object target, PropertyFilter filter) {
		return reflectionEquals(source, target, null, filter);
	}

	/**
	 * Compares the readable properties declared below the stop class and accepted by the filter.
	 * 
	 * @throws BeanPropertyAccessException if a getter fails
	 */
	public static boolean reflectionEquals(Object source, Object target, Class<?> stopClass, PropertyFilter filter) {
		if (source == target) {
			return true;
		}
		if (source == null || target == null) {
			return false;
		}
		if (source.getClass() != target.getClass()) {
			return false;
		}
		Map<String, PropertyDescriptor> descriptors = PropertyUtils.getPropertyDescriptors(source.getClass(), stopClass, filter);
		if (MapUtils.isEmpty(descriptors)) {
			return source.equals(target);
		}
		return reflectionEquals(source, target, descriptors.values());
	}

	private static boolean reflectionEquals(Object source, Object target, Collection<PropertyDescriptor> descriptors) {
		Object left, right;
		Method method;
		for (PropertyDescriptor descriptor : descriptors) {
			method = descriptor.getReadMethod();
			if (method != null) {
				left = invokeMethod(source, method);
				right = invokeMethod(target, method);
				if (ObjectUtils.notEquals(left, right)) {
					return false;
				}
			}
		}
		return true;
	}

	private static Object invokeMethod(Object object, Method method) {
		try {
			return MethodUtils.invokeMethod(object, method);
		} catch (Exception e) {
			throw new BeanPropertyAccessException(e.getMessage(), e);
		}
	}

}
