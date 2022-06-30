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
package com.github.paganini2008.devtools.mock;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.paganini2008.devtools.beans.BeanUtils;
import com.github.paganini2008.devtools.beans.PropertyUtils;
import com.github.paganini2008.devtools.reflection.FieldUtils;

/**
 * 
 * BeanMocker
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
public abstract class BeanMocker {

	public static <T> List<T> mockBeans(int count, Class<T> beanClass, MockContext context) {
		return mockBeans(count, beanClass, context, new RandomTemplate());
	}

	public static <T> List<T> mockBeans(int count, Class<T> beanClass, MockContext context, RandomOperations operations) {
		List<T> list = new ArrayList<T>(count);
		for (int i = 0; i < count; i++) {
			list.add(mockBean(beanClass, context, operations));
		}
		return list;
	}

	public static <T> T mockBean(Class<T> beanClass, MockContext context, RandomOperations operations) {
		T object = BeanUtils.instantiate(beanClass);
		Map<String, PropertyDescriptor> desc = PropertyUtils.getPropertyDescriptors(object.getClass());
		String propertyName;
		Object propertyValue;
		Field field;
		for (PropertyDescriptor pd : desc.values()) {
			context.reset();
			propertyName = pd.getName();
			field = FieldUtils.getFieldIfAbsent(beanClass, propertyName);
			if (field != null) {
				if (field.isAnnotationPresent(Recur.class)) {
					propertyValue = mockBean(pd.getPropertyType(), context, operations);
				} else {
					propertyValue = context.mock(field, operations);
				}
				if (propertyValue != null) {
					PropertyUtils.setProperty(object, pd.getName(), propertyValue);
				}
			}
		}
		return object;
	}

}
