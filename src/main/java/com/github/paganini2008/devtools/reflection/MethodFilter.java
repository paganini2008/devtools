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
package com.github.paganini2008.devtools.reflection;

import java.lang.reflect.Method;

/**
 * Predicate selecting methods by name and {@link java.lang.reflect.Method}.
 * 
 * @author Fred Feng
 * @date 2019-07-11
 * 
 * 
 * @since 2.0.1
 */
public interface MethodFilter {

	/**
	 * Returns true if the method should be selected.
	 */
	boolean accept(String name, Method method);

	/**
	 * Returns a filter selecting a method only if both this filter and the given filter select it.
	 */
	default MethodFilter and(MethodFilter filter) {
		return (name, method) -> {
			return accept(name, method) && filter.accept(name, method);
		};
	}

	/**
	 * Returns a filter selecting a method if this filter or the given filter selects it.
	 */
	default MethodFilter or(MethodFilter filter) {
		return (name, method) -> {
			return accept(name, method) || filter.accept(name, method);
		};
	}

	/**
	 * Returns the negation of the given filter. Note that this filter itself is ignored.
	 */
	default MethodFilter not(MethodFilter filter) {
		return (name, method) -> {
			return !filter.accept(name, method);
		};
	}

}
