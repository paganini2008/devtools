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
package com.github.paganini2008.devtools;

/**
 * 
 * Obtainable
 * 
 * Describes how to obtain a value, optionally with retries and a fallback value.
 *
 * @author Fred Feng
 * @date 2020-05-23
 * @since 2.0.1
 */
public interface Obtainable<T> {

	/**
	 * Obtains the value.
	 */
	T obtain();

	/**
	 * Called when {@link #obtain()} fails.
	 *
	 * @param e the failure
	 * @param retrying the current retry count
	 */
	default void exceptionCaught(Throwable e, int retrying) {
	}
	
	/**
	 * Returns the interval in milliseconds between retries. Defaults to 1000.
	 */
	default long retryingInterval() {
		return 1000L;
	}

	/**
	 * Returns the maximum number of retries. Defaults to 0.
	 */
	default int retries() {
		return 0;
	}

	/**
	 * Returns the value used when every attempt fails. Defaults to null.
	 */
	default T defaultValue() {
		return null;
	}

}
