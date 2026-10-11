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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * IdUtilsTest
 * 
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class IdUtilsTest {

	private static final Pattern UUID_V4 = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$");
	private static final Pattern SIMPLE_UUID_V4 = Pattern.compile("^[0-9a-f]{12}4[0-9a-f]{3}[89ab][0-9a-f]{15}$");
	private static final Pattern SHORT_ID = Pattern.compile("^[0-9A-Za-z]{22}$");
	private static final Pattern OBJECT_ID = Pattern.compile("^[0-9a-f]{24}$");

	@Test
	void uuid_isRandomVersion4WithHyphens() {
		Set<String> seen = new HashSet<String>();
		for (int i = 0; i < 1000; i++) {
			String id = IdUtils.uuid();
			assertTrue(UUID_V4.matcher(id).matches(), id);
			assertTrue(seen.add(id));
		}
	}

	@Test
	void simpleUuid_is32HexCharsMatchingUuidBits() {
		Set<String> seen = new HashSet<String>();
		for (int i = 0; i < 1000; i++) {
			String id = IdUtils.simpleUuid();
			assertEquals(32, id.length());
			assertTrue(SIMPLE_UUID_V4.matcher(id).matches(), id);
			assertTrue(seen.add(id));
			String dashed = id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-"
					+ id.substring(20);
			assertEquals(dashed, UUID.fromString(dashed).toString());
		}
	}

	@Test
	void fastUuid_isValidVersion4Uuid() {
		Set<String> seen = new HashSet<String>();
		for (int i = 0; i < 1000; i++) {
			String id = IdUtils.fastUuid();
			assertTrue(UUID_V4.matcher(id).matches(), id);
			UUID uuid = UUID.fromString(id);
			assertEquals(4, uuid.version());
			assertEquals(2, uuid.variant());
			assertTrue(seen.add(id));
		}
	}

	@Test
	void shortId_isBase62OfFixedLengthAndUsesWholeAlphabet() {
		assertEquals(22, IdUtils.SHORT_ID_LENGTH);
		Set<String> seen = new HashSet<String>();
		Set<Character> chars = new HashSet<Character>();
		for (int i = 0; i < 5000; i++) {
			String id = IdUtils.shortId();
			assertTrue(SHORT_ID.matcher(id).matches(), id);
			assertTrue(seen.add(id));
			for (char c : id.toCharArray()) {
				chars.add(c);
			}
		}
		assertEquals(62, chars.size());
	}

	@Test
	void snowflakeId_usesReplaceableDefaultGenerator() {
		SnowflakeIdGenerator original = IdUtils.getDefaultSnowflake();
		assertNotNull(original);
		assertEquals(0, original.getWorkerId());
		assertEquals(0, original.getDatacenterId());
		long a = IdUtils.snowflakeId();
		long b = IdUtils.snowflakeId();
		assertTrue(b > a);
		assertEquals(0, SnowflakeIdGenerator.parse(b).getWorkerId());
		try {
			SnowflakeIdGenerator custom = new SnowflakeIdGenerator(5, 6);
			IdUtils.setDefaultSnowflake(custom);
			assertSame(custom, IdUtils.getDefaultSnowflake());
			SnowflakeId sid = SnowflakeIdGenerator.parse(IdUtils.snowflakeId());
			assertEquals(5, sid.getWorkerId());
			assertEquals(6, sid.getDatacenterId());
			assertThrows(IllegalArgumentException.class, () -> IdUtils.setDefaultSnowflake(null));
			assertSame(custom, IdUtils.getDefaultSnowflake());
		} finally {
			IdUtils.setDefaultSnowflake(original);
		}
	}

	@Test
	void objectId_hasTimestampRandomAndCounterParts() {
		long before = System.currentTimeMillis() / 1000L;
		String a = IdUtils.objectId();
		String b = IdUtils.objectId();
		long after = System.currentTimeMillis() / 1000L;
		assertTrue(OBJECT_ID.matcher(a).matches(), a);
		assertTrue(OBJECT_ID.matcher(b).matches(), b);
		long seconds = Long.parseLong(a.substring(0, 8), 16);
		assertTrue(seconds >= before && seconds <= after, a);
		// 5-byte random part is fixed per JVM
		assertEquals(a.substring(8, 18), b.substring(8, 18));
		int ca = Integer.parseInt(a.substring(18), 16);
		int cb = Integer.parseInt(b.substring(18), 16);
		assertEquals((ca + 1) & 0xFFFFFF, cb);
	}

	@Test
	@Timeout(value = 20, unit = TimeUnit.SECONDS)
	void objectIdAndShortId_areUniqueUnderConcurrency() throws Exception {
		final int threads = 8;
		final int perThread = 20000;
		final Set<String> objectIds = ConcurrentHashMap.newKeySet();
		final Set<String> shortIds = ConcurrentHashMap.newKeySet();
		final CountDownLatch start = new CountDownLatch(1);
		final CountDownLatch done = new CountDownLatch(threads);
		for (int t = 0; t < threads; t++) {
			new Thread(() -> {
				try {
					start.await();
					for (int i = 0; i < perThread; i++) {
						objectIds.add(IdUtils.objectId());
						shortIds.add(IdUtils.shortId());
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} finally {
					done.countDown();
				}
			}).start();
		}
		start.countDown();
		assertTrue(done.await(15, TimeUnit.SECONDS));
		assertEquals(threads * perThread, objectIds.size());
		assertEquals(threads * perThread, shortIds.size());
	}

	@Test
	void utilityClass_canBeSubclassed() {
		assertNotNull(new IdUtils() {
		});
	}

}
