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

import java.util.Iterator;

/**
 * StatefulIterator
 * <p>
 * An iterator with optional prepare and close hooks, whose remove() is not
 * supported.
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public abstract class StatefulIterator<T> implements Iterator<T> {

	/**
	 * Prepares resources before iteration. Does nothing by default.
	 */
	public void prepare() throws Exception {
	}

	/**
	 * Releases resources after iteration. Does nothing by default.
	 */
	public void close() throws Exception {
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Adapts an iterator to a StatefulIterator, returning it as it is if it already is one.
	 */
	public static <T> StatefulIterator<T> convertFrom(final Iterator<T> iterator) {
		if (iterator instanceof StatefulIterator) {
			return (StatefulIterator<T>) iterator;
		}
		return new StatefulIterator<T>() {

			public boolean hasNext() {
				return iterator.hasNext();
			}

			public T next() {
				return iterator.next();
			}
		};
	}

}
