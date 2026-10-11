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
package com.github.paganini2008.devtools.cache;

/**
 * ExpiredCache
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * 
 * @since 2.0.1
 */
public interface ExpiredCache extends Cache {

	/**
	 * Puts a value which expires after the given seconds since now. A non-positive value means
	 * never expire.
	 */
	void putObject(Object key, Object value, boolean ifAbsent, int expired);

	/**
	 * Changes the time to live (seconds, counted from the creation) of a live entry. Missing or
	 * already expired entries are ignored.
	 */
	void setExpired(Object key, int expired);

	interface CachedObject {

		int getExpired();

		void setExpired(int expired);

		Object getValue();

		long getCreated();
	}
}
