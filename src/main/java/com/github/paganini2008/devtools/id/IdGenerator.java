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
package com.github.paganini2008.devtools.id;

/**
 * Generates identifiers of type {@code T}. Implementations document their own
 * uniqueness and thread-safety guarantees.
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
@FunctionalInterface
public interface IdGenerator<T> {

	/**
	 * Generates a new identifier.
	 */
	T generateId();

}
