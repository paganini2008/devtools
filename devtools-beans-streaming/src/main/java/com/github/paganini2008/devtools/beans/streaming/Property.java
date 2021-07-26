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

import java.util.function.Function;

import com.github.paganini2008.devtools.beans.BeanUtils;

/**
 * 
 * Property
 * 
 * @author Fred Feng
 * 
 * 
 * @since 2.0.1
 */
public final class Property<E, T> implements Function<E, T> {

	private final String propertyName;
	private final Class<T> requiredType;

	Property(String propertyName, Class<T> requiredType) {
		this.propertyName = propertyName;
		this.requiredType = requiredType;
	}

	public T apply(E e) {
		return BeanUtils.getProperty(e, propertyName, requiredType);
	}

	public static <E, T> Property<E, T> forName(String propertyName, Class<T> requiredType) {
		return new Property<E, T>(propertyName, requiredType);
	}

}
