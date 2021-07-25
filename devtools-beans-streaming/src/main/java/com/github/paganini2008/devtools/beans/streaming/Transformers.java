/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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

import com.github.paganini2008.devtools.beans.BeanUtils;
import com.github.paganini2008.devtools.beans.PropertyFilter;
import com.github.paganini2008.devtools.beans.PropertyFilters;
import com.github.paganini2008.devtools.beans.PropertyUtils;

/**
 * 
 * Transformers
 * 
 * @author Fred Feng
 *
 * @version 1.0
 */
public abstract class Transformers {

	public static <E> Transformer<E, Map<String, Object>> asMap() {
		return asMap((PropertyFilter) null);
	}

	public static <E> Transformer<E, Map<String, Object>> asMap(String[] propertyNames) {
		return asMap(PropertyFilters.includedProperties(propertyNames));
	}

	public static <E> Transformer<E, Map<String, Object>> asMap(PropertyFilter propertyFilter) {
		return e -> {
			return PropertyUtils.convertToMap(e, null, propertyFilter);
		};
	}

	public static <E, T> Transformer<E, T> asBean(Class<T> requiredType, String[] propertyNames) {
		return asBean(requiredType, PropertyFilters.includedProperties(propertyNames));
	}

	public static <E, T> Transformer<E, T> asBean(Class<T> requiredType) {
		return asBean(requiredType, (PropertyFilter) null);
	}

	public static <E, T> Transformer<E, T> asBean(Class<T> requiredType, PropertyFilter propertyFilter) {
		return e -> {
			return BeanUtils.copy(e, requiredType, propertyFilter);
		};
	}

}
