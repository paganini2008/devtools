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
package com.github.paganini2008.devtools.collection;

import java.util.Iterator;

/**
 * StatefulIterator
 * 
 * @author Fred Feng
 * @version 1.0
 */
public abstract class StatefulIterator<T> implements Iterator<T> {

	public void prepare() throws Exception {
	}

	public void close() throws Exception {
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

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
