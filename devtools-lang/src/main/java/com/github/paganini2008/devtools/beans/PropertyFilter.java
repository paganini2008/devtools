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
package com.github.paganini2008.devtools.beans;

import java.beans.PropertyDescriptor;

/**
 * 
 * PropertyFilter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public interface PropertyFilter {

	boolean accept(Class<?> type, String name, PropertyDescriptor descriptor);

	static PropertyFilter disjunction() {
		return (type, name, descriptor) -> {
			return false;
		};
	}

	static PropertyFilter conjunction() {
		return (type, name, descriptor) -> {
			return true;
		};
	}

	default PropertyFilter and(PropertyFilter filter) {
		return (type, name, descriptor) -> {
			return accept(type, name, descriptor) && filter.accept(type, name, descriptor);
		};
	}

	default PropertyFilter or(PropertyFilter filter) {
		return (type, name, descriptor) -> {
			return accept(type, name, descriptor) || filter.accept(type, name, descriptor);
		};
	}

	default PropertyFilter not(PropertyFilter filter) {
		return (type, name, descriptor) -> {
			return !filter.accept(type, name, descriptor);
		};
	}

}
