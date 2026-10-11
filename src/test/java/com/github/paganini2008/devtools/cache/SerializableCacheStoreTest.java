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

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.github.paganini2008.devtools.io.SerializationUtils;

/**
 * SerializableCacheStoreTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class SerializableCacheStoreTest {

	@TempDir
	File dir;

	private SerializableCacheStore newStore(int maxSize) {
		SerializableCacheStore store = new SerializableCacheStore(maxSize);
		store.setStoreDir(dir.getAbsolutePath());
		return store;
	}

	@Test
	public void writeReadAndRemove() {
		SerializableCacheStore store = newStore(10);
		store.writeObject("a", "Apple");
		store.writeObject(1, 100L);
		assertEquals(2, store.getSize());
		assertTrue(store.hasKey("a"));
		assertEquals(new HashSet<Object>(Arrays.asList("a", 1)), store.keys());
		assertEquals("Apple", store.readObject("a"));
		assertEquals(100L, store.readObject(1));
		File file = store.asFile("a");
		assertTrue(file.exists());
		assertTrue(file.getName().endsWith(SerializableCacheStore.fileExtension));

		assertEquals("Apple", store.removeObject("a"));
		assertFalse(file.exists());
		assertFalse(store.hasKey("a"));
		assertEquals(1, store.getSize());
		assertNull(store.removeObject("a"));
	}

	@Test
	public void readMissingKey_returnsNull() {
		SerializableCacheStore store = newStore(10);
		assertNull(store.readObject("missing"));
		// a stale file which was not written by this store is ignored
		SerializationUtils.writeObject("stale", store.asFile("stale"), false);
		assertNull(store.readObject("stale"));
		assertFalse(store.hasKey("stale"));
		// the file was deleted externally
		store.writeObject("b", "B");
		assertTrue(store.asFile("b").delete());
		assertNull(store.readObject("b"));
		assertNull(store.removeObject("b"));
		assertFalse(store.hasKey("b"));
	}

	@Test
	public void evictsLeastRecentlyUsedFile_whenFull() {
		SerializableCacheStore store = newStore(2);
		store.writeObject("a", "A");
		store.writeObject("b", "B");
		store.readObject("a");
		store.writeObject("c", "C");
		assertEquals(2, store.getSize());
		assertFalse(store.hasKey("b"));
		assertFalse(store.asFile("b").exists());
		assertEquals("A", store.readObject("a"));
		assertEquals("C", store.readObject("c"));
	}

	@Test
	public void asSecondLevelOfLruCache() {
		SerializableCacheStore store = newStore(10);
		LruCache cache = new LruCache(2);
		cache.setCacheStore(store);
		for (int i = 0; i < 5; i++) {
			cache.putObject("k" + i, "v" + i);
		}
		assertEquals(5, cache.getSize());
		assertEquals(3, store.getSize());
		for (int i = 0; i < 5; i++) {
			assertEquals("v" + i, cache.getObject("k" + i));
		}
		assertEquals(5, cache.getSize());
		assertNull(cache.getObject("k9"));
		cache.clear();
		assertEquals(0, cache.getSize());
		assertEquals(0, dir.listFiles().length);
	}

}
