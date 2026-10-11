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

import java.util.Set;

/**
 * 
 * MasterStandbyCache
 * 
 * @author Fred Feng
 * @date 2019-05-29
 * 
 * @since 2.0.1
 */
public class MasterStandbyCache extends AbstractCache {

	private final Cache master;
	private final Cache backup;

	public MasterStandbyCache(Cache master, Cache backup) {
		this.master = master;
		this.backup = backup;
	}

	/**
	 * Puts the value into both caches. If ifAbsent is true and the key exists in
	 * either cache, nothing is changed (otherwise the master could get the new
	 * value while the backup keeps the old one).
	 */
	public void putObject(Object key, Object value, boolean ifAbsent) {
		if (ifAbsent && getObject(key) != null) {
			return;
		}
		backup.putObject(key, value, ifAbsent);
		master.putObject(key, value, ifAbsent);
	}

	public boolean hasKey(Object key) {
		return master.hasKey(key) ? true : backup.hasKey(key);
	}

	public Object getObject(Object key) {
		Object result = master.getObject(key);
		if (result == null) {
			result = backup.getObject(key);
			if (result != null) {
				master.putObject(key, result);
			}
		}
		return result;
	}

	public Object removeObject(Object key) {
		backup.removeObject(key);
		return master.removeObject(key);
	}

	public Set<Object> keys() {
		Set<Object> keys = master.keys();
		if (keys == null) {
			keys = backup.keys();
		}
		return keys;
	}

	public void clear() {
		backup.clear();
		master.clear();
	}

	public int getSize() {
		return Math.max(master.getSize(), backup.getSize());
	}

	public void close() {
		master.close();
		backup.close();
	}

}
