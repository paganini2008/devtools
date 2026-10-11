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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

/**
 * MasterStandbyCacheTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class MasterStandbyCacheTest {

	@Test
	public void writesToBoth_readsFromMasterFirst() {
		LruCache master = new LruCache(2);
		HashCache backup = new HashCache();
		MasterStandbyCache cache = new MasterStandbyCache(master, backup);
		cache.putObject("a", 1);
		cache.putObject("b", 2);
		cache.putObject("c", 3);
		// 'a' was evicted from master, but still in backup
		assertFalse(master.hasKey("a"));
		assertTrue(cache.hasKey("a"));
		assertEquals(3, cache.getSize());
		assertEquals(new HashSet<Object>(Arrays.asList("b", "c")), cache.keys());

		// read from backup and put back into master
		assertEquals(1, cache.getObject("a"));
		assertTrue(master.hasKey("a"));
		assertNull(cache.getObject("zzz"));
		assertFalse(cache.hasKey("zzz"));
	}

	@Test
	public void putIfAbsent_removeAndClear() {
		HashCache master = new HashCache();
		HashCache backup = new HashCache();
		MasterStandbyCache cache = new MasterStandbyCache(master, backup);
		cache.putObject("a", 1, true);
		cache.putObject("a", 2, true);
		assertEquals(1, cache.getObject("a"));
		assertEquals(1, cache.removeObject("a"));
		assertFalse(backup.hasKey("a"));
		assertFalse(master.hasKey("a"));
		cache.putObject("b", 2);
		cache.clear();
		assertEquals(0, master.getSize());
		assertEquals(0, backup.getSize());
	}

	@Test
	public void multiLevel_withExpiration() {
		// L1: small lru cache, L2: expired cache
		LruCache l1 = new LruCache(1);
		ExpiredCache l2 = new HashCache().expiredCache();
		Cache cache = l1.masterStandbyCache(l2);
		cache.putObject("a", 1);
		cache.putObject("b", 2);
		assertEquals(1, cache.getObject("a"));
		assertEquals(2, cache.getObject("b"));
		assertEquals(2, l2.getSize());
		ExpiredCache expiring = ((AbstractCache) cache).expiredCache(1, TimeUnit.HOURS);
		try {
			expiring.putObject("c", 3, false, 60);
			assertEquals(3, expiring.getObject("c"));
		} finally {
			expiring.close();
		}
	}

}
