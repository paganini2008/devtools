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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

/**
 * ProviderTest
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class ProviderTest {

	@Test
	public void testApplyCachesCreatedObject() {
		AtomicInteger created = new AtomicInteger();
		Provider<String, StringBuilder> provider = new Provider<String, StringBuilder>() {
			@Override
			protected StringBuilder createObject(String key) {
				created.incrementAndGet();
				return new StringBuilder(key);
			}
		};
		StringBuilder first = provider.apply("a");
		assertEquals("a", first.toString());
		assertSame(first, provider.apply("a"));
		assertEquals(1, created.get());
		assertEquals("b", provider.apply("b").toString());
	}

	@Test
	public void testNullObjectIsNotCached() {
		AtomicInteger created = new AtomicInteger();
		Provider<String, String> provider = new Provider<String, String>() {
			@Override
			protected String createObject(String key) {
				created.incrementAndGet();
				return null;
			}
		};
		assertNull(provider.apply("a"));
		assertNull(provider.apply("a"));
		assertEquals(2, created.get());
	}

	@Test
	public void testConcurrentCallersGetSameInstance() throws Exception {
		Provider<Integer, Object> provider = new Provider<Integer, Object>() {
			@Override
			protected Object createObject(Integer key) {
				return new Object();
			}
		};
		ExecutorService executor = Executors.newFixedThreadPool(4);
		try {
			CountDownLatch start = new CountDownLatch(1);
			@SuppressWarnings("unchecked")
			Future<Object>[] futures = new Future[8];
			for (int i = 0; i < futures.length; i++) {
				futures[i] = executor.submit(() -> {
					start.await();
					return provider.apply(1);
				});
			}
			start.countDown();
			Object expected = futures[0].get(5, TimeUnit.SECONDS);
			for (Future<Object> f : futures) {
				assertSame(expected, f.get(5, TimeUnit.SECONDS));
			}
		} finally {
			executor.shutdownNow();
		}
	}

}
