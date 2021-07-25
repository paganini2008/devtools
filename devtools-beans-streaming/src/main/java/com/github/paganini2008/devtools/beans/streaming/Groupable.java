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
 * Groupable
 * 
 * @author Fred Feng
 * 
 * @version 1.0
 */
public interface Groupable<E> {

	default <T> Groupable<E> groupBy(String attributeName, Class<T> requiredType) {
		return groupBy(Property.forName(attributeName, requiredType), attributeName);
	}

	<T> Groupable<E> groupBy(Function<E, T> function, String attributeName);

	Groupable<E> having(Predicate<Group<E>> predicate);

	Groupable<E> orderBy(Sorter<Group<E>> sort);

	<T> ResultSetSlice<T> setTransformer(Transformer<Group<E>, T> transformer);

}
