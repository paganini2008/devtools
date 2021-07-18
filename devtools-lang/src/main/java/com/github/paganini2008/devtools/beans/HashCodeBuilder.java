/**
* Copyright 2018-2021 Fred Feng (paganini.fy@gmail.com)

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

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.ObjectUtils;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.reflection.MethodUtils;

/**
 * 
 * HashCodeBuilder
 *
 * @author Fred Feng
 * @version 1.1
 */
public abstract class HashCodeBuilder {

	public final static int initialNonZeroOddNumber = 17;

	public final static int multiplierNonZeroOddNumber = 31;

	public static int reflectionHashCode(Object bean) {
		return reflectionHashCode(bean, (PropertyFilter) null);
	}

	public static int reflectionHashCode(Object bean, PropertyFilter filter) {
		return reflectionHashCode(bean, null, filter);
	}

	public static int reflectionHashCode(Object bean, Class<?> stopClass, PropertyFilter filter) {
		Assert.isNull(bean, "Source bean must not be null.");
		Map<String, PropertyDescriptor> descriptors = PropertyUtils.getPropertyDescriptors(bean.getClass(), stopClass, filter);
		if (MapUtils.isEmpty(descriptors)) {
			return 0;
		}
		return reflectionHashCode(bean, descriptors.values());
	}

	private static int reflectionHashCode(Object bean, Collection<PropertyDescriptor> descriptors) {
		int hash = initialNonZeroOddNumber;
		for (PropertyDescriptor descriptor : descriptors) {
			hash += multiplierNonZeroOddNumber * hash + reflectionHashCode(bean, descriptor);
		}
		return hash;
	}

	private static int reflectionHashCode(Object bean, PropertyDescriptor descriptor) {
		Method method = descriptor.getReadMethod();
		if (method != null) {
			try {
				Object value = MethodUtils.invokeMethod(bean, method);
				return ObjectUtils.hashCode(value);
			} catch (Exception e) {
				throw new BeanPropertyAccessException(e.getMessage(), e);
			}
		}
		return 0;
	}

}
