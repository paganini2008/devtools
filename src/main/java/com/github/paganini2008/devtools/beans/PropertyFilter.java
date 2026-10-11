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
package com.github.paganini2008.devtools.beans;

import java.beans.PropertyDescriptor;

/**
 * Predicate deciding whether a bean property takes part in an operation (copy, toString, equals, ...).
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * @since 2.0.1
 */
public interface PropertyFilter {

	/**
	 * Returns true if the property of the given bean type should be included.
	 */
	boolean accept(Class<?> type, String name, PropertyDescriptor descriptor);

	/**
	 * Returns a filter rejecting every property.
	 */
	static PropertyFilter disjunction() {
		return (type, name, descriptor) -> {
			return false;
		};
	}

	/**
	 * Returns a filter accepting every property.
	 */
	static PropertyFilter conjunction() {
		return (type, name, descriptor) -> {
			return true;
		};
	}

	/**
	 * Returns a filter accepting a property only if both this filter and the given filter accept it.
	 */
	default PropertyFilter and(PropertyFilter filter) {
		return (type, name, descriptor) -> {
			return accept(type, name, descriptor) && filter.accept(type, name, descriptor);
		};
	}

	/**
	 * Returns a filter accepting a property if this filter or the given filter accepts it.
	 */
	default PropertyFilter or(PropertyFilter filter) {
		return (type, name, descriptor) -> {
			return accept(type, name, descriptor) || filter.accept(type, name, descriptor);
		};
	}

	/**
	 * Returns the negation of the given filter. Note that this filter itself is ignored.
	 */
	default PropertyFilter not(PropertyFilter filter) {
		return (type, name, descriptor) -> {
			return !filter.accept(type, name, descriptor);
		};
	}

}
