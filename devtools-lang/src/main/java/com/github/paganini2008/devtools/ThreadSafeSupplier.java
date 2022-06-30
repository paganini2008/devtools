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
package com.github.paganini2008.devtools;

import java.util.function.Supplier;

/**
 * 
 * ThreadSafeSupplier
 *
 * @author Fred Feng
 * @since 2.0.4
 */
public class ThreadSafeSupplier<T> implements Supplier<T> {

	private final Supplier<T> supplier;

	ThreadSafeSupplier(Supplier<T> supplier) {
		this.supplier = supplier;
	}

	private volatile T t;

	@Override
	public T get() {
		T result = t;
		if (result == null) {
			synchronized (this) {
				result = t;
				if (result == null) {
					result = supplier.get();
					t = result;
				}
			}
		}
		return result;
	}

	public static <T> ThreadSafeSupplier<T> of(Supplier<T> s) {
		return new ThreadSafeSupplier<T>(s);
	}

}
