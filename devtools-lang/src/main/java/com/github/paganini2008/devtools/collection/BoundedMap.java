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

import java.util.Map;

/**
 * 
 * BoundedMap
 *
 * @author Fred Feng
 * @since 2.0.1
 */
public interface BoundedMap<K, V> extends Map<K, V> {

	int getMaxSize();

	default void onEviction(K eldestKey, V eldestValue) {
		System.out.println("taotai: " + eldestKey + ", val: " + eldestValue);
	}

}
