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

import java.util.LinkedHashMap;
import java.util.Map;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.beans.BeanUtils;
import com.github.paganini2008.devtools.beans.LambdaUtils;
import com.github.paganini2008.devtools.beans.PropertyFilter;
import com.github.paganini2008.devtools.beans.PropertyFilters;
import com.github.paganini2008.devtools.beans.PropertyUtils;
import com.github.paganini2008.devtools.beans.SerializableFunction;

/**
 * 
 * Transformers
 * <p>
 * Factory of the common {@link Transformer transformers}, converting beans
 * into maps or other beans.
 * 
 * @author Fred Feng
 * @date 2019-07-11
 *
 * @since 2.0.1
 */
public abstract class Transformers {

	/**
	 * Converts a bean into a map of all its properties.
	 */
	public static <E> Transformer<E, Map<String, Object>> asMap() {
		return asMap((PropertyFilter) null);
	}

	/**
	 * Converts a bean into a map of the given properties only.
	 */
	public static <E> Transformer<E, Map<String, Object>> asMap(String[] propertyNames) {
		return asMap(PropertyFilters.includedProperties(propertyNames));
	}

	/**
	 * Converts a bean into a map of the properties accepted by the filter (null accepts all).
	 */
	public static <E> Transformer<E, Map<String, Object>> asMap(PropertyFilter propertyFilter) {
		return e -> PropertyUtils.convertToMap(e, null, propertyFilter);

	}

	/**
	 * Copies the given properties into a new bean of the required type.
	 */
	public static <E, T> Transformer<E, T> asBean(Class<T> requiredType, String[] propertyNames) {
		return asBean(requiredType, PropertyFilters.includedProperties(propertyNames));
	}

	/**
	 * Copies the properties into a new bean of the required type.
	 */
	public static <E, T> Transformer<E, T> asBean(Class<T> requiredType) {
		return asBean(requiredType, (PropertyFilter) null);
	}

	/**
	 * Copies the properties accepted by the filter (null accepts all) into a new bean of the
	 * required type.
	 */
	public static <E, T> Transformer<E, T> asBean(Class<T> requiredType, PropertyFilter propertyFilter) {
		return e -> BeanUtils.copy(e, requiredType, propertyFilter);

	}

	/**
	 * Converts a bean into a map of the values of the given getter method references, keyed by the resolved
	 * property names (see {@link LambdaUtils#getPropertyName(SerializableFunction)}) in the order of the arguments.
	 * The getters are invoked directly.
	 * 
	 * <pre>
	 * // select name, price from products
	 * Selector.from(products).setTransformer(Transformers.asMap(Product::getName, Product::getPrice)).list();
	 * // [{name=p1, price=100.0}, ...]
	 * </pre>
	 * 
	 * @throws IllegalArgumentException if any argument is not a getter method reference
	 * @since 3.0.0
	 */
	@SafeVarargs
	public static <E> Transformer<E, Map<String, Object>> asMap(SerializableFunction<E, ?>... getters) {
		final String[] names = getPropertyNames(getters);
		final SerializableFunction<E, ?>[] functions = getters.clone();
		return e -> {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			for (int i = 0; i < functions.length; i++) {
				map.put(names[i], functions[i].apply(e));
			}
			return map;
		};
	}

	/**
	 * Copies the properties referred by the given getter method references into a new bean of the required type,
	 * like {@link #asBean(Class, String[])}.
	 * 
	 * <pre>
	 * Selector.from(products).setTransformer(Transformers.asBean(ProductDto.class, Product::getName, Product::getPrice)).list();
	 * </pre>
	 * 
	 * @throws IllegalArgumentException if any argument is not a getter method reference
	 * @since 3.0.0
	 */
	@SafeVarargs
	public static <E, T> Transformer<E, T> asBean(Class<T> requiredType, SerializableFunction<E, ?>... getters) {
		return asBean(requiredType, getPropertyNames(getters));
	}

	private static String[] getPropertyNames(SerializableFunction<?, ?>[] getters) {
		Assert.isNull(getters, "Getters must not be null.");
		String[] names = new String[getters.length];
		for (int i = 0; i < getters.length; i++) {
			names[i] = LambdaUtils.getPropertyName(getters[i]);
		}
		return names;
	}

}
