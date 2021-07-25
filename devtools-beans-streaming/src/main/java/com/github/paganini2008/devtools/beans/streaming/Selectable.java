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
import java.util.function.Predicate;

import com.github.paganini2008.devtools.jdbc.ResultSetSlice;

/**
 * 
 * Selectable
 * 
 * @author Fred Feng
 * 
 * @version 1.0
 */
public interface Selectable<E> extends ResultSetSlice<E> {

	Selectable<E> filter(Predicate<E> predicate);

	Selectable<E> orderBy(Sorter<E> sort);

	Selectable<E> distinct();

	<T> Groupable<E> groupBy(Function<E, T> function, String alias);

	default <T> Groupable<E> groupBy(String attributeName, Class<T> requiredType) {
		return groupBy(Property.forName(attributeName, requiredType), attributeName);
	}

	<T> ResultSetSlice<T> setTransformer(Transformer<E, T> transformer);

}
