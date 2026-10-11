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

import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Static helpers for generating common kinds of identifiers: UUIDs, short random ids,
 * snowflake ids and 12-byte object ids.
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public abstract class IdUtils {

	/**
	 * Length of the ids produced by {@link #shortId()}.
	 */
	public static final int SHORT_ID_LENGTH = 22;

	private static final char[] BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

	private static final char[] HEX = "0123456789abcdef".toCharArray();

	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	private static final byte[] OBJECT_ID_RANDOM = new byte[5];

	private static final AtomicInteger OBJECT_ID_COUNTER;

	static {
		SECURE_RANDOM.nextBytes(OBJECT_ID_RANDOM);
		OBJECT_ID_COUNTER = new AtomicInteger(SECURE_RANDOM.nextInt(0x1000000));
	}

	private static volatile SnowflakeIdGenerator defaultSnowflake = new SnowflakeIdGenerator(0, 0);

	/**
	 * Returns a random (version 4) UUID string with hyphens, 36 characters long, backed by
	 * {@link SecureRandom}.
	 *
	 * <pre>
	 * IdUtils.uuid() // =&gt; "3b241101-e2bb-4255-8caf-4136c566a962"
	 * </pre>
	 */
	public static String uuid() {
		return UUID.randomUUID().toString();
	}

	/**
	 * Returns a random (version 4) UUID string without hyphens, 32 lowercase hex characters.
	 *
	 * <pre>
	 * IdUtils.simpleUuid() // =&gt; "3b241101e2bb42558caf4136c566a962"
	 * </pre>
	 */
	public static String simpleUuid() {
		UUID uuid = UUID.randomUUID();
		char[] buf = new char[32];
		writeHex(uuid.getMostSignificantBits(), buf, 0);
		writeHex(uuid.getLeastSignificantBits(), buf, 16);
		return new String(buf);
	}

	/**
	 * Returns a version 4 UUID string with hyphens generated from {@link ThreadLocalRandom}.
	 * It is faster than {@link #uuid()} under contention but its values are predictable, so it
	 * must <b>not</b> be used for security-sensitive purposes such as tokens or session ids.
	 */
	public static String fastUuid() {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		long msb = (random.nextLong() & 0xFFFFFFFFFFFF0FFFL) | 0x0000000000004000L;
		long lsb = (random.nextLong() & 0x3FFFFFFFFFFFFFFFL) | 0x8000000000000000L;
		return new UUID(msb, lsb).toString();
	}

	/**
	 * Returns a 22-character, URL-safe Base62 ([0-9A-Za-z]) random id from {@link SecureRandom}
	 * (about 131 bits of entropy). Suitable as a compact unguessable token, e.g. for short links
	 * or resource keys exposed in URLs; it carries no ordering information.
	 *
	 * <pre>
	 * IdUtils.shortId() // =&gt; "4mXrT0qZ9bLk2VdHc8YwPe"
	 * </pre>
	 */
	public static String shortId() {
		char[] buf = new char[SHORT_ID_LENGTH];
		byte[] bytes = new byte[SHORT_ID_LENGTH + 8];
		int pos = 0;
		while (pos < SHORT_ID_LENGTH) {
			SECURE_RANDOM.nextBytes(bytes);
			for (int i = 0; i < bytes.length && pos < SHORT_ID_LENGTH; i++) {
				int v = bytes[i] & 0x3F;
				if (v < 62) { // rejection sampling keeps the distribution uniform
					buf[pos++] = BASE62[v];
				}
			}
		}
		return new String(buf);
	}

	/**
	 * Returns the next id of the default {@link SnowflakeIdGenerator} (workerId 0, datacenterId 0
	 * unless replaced via {@link #setDefaultSnowflake(SnowflakeIdGenerator)}).
	 *
	 * @throws IllegalStateException see {@link SnowflakeIdGenerator#nextId()}
	 */
	public static long snowflakeId() {
		return defaultSnowflake.nextId();
	}

	/**
	 * Returns the generator used by {@link #snowflakeId()}.
	 */
	public static SnowflakeIdGenerator getDefaultSnowflake() {
		return defaultSnowflake;
	}

	/**
	 * Replaces the generator used by {@link #snowflakeId()}, e.g. to configure the workerId and
	 * datacenterId of this process.
	 *
	 * @throws IllegalArgumentException if the generator is null
	 */
	public static void setDefaultSnowflake(SnowflakeIdGenerator generator) {
		if (generator == null) {
			throw new IllegalArgumentException("Default snowflake generator must not be null");
		}
		defaultSnowflake = generator;
	}

	/**
	 * Returns a 24-character lowercase hex object id made of 12 bytes: a 4-byte big-endian
	 * timestamp in seconds, a 5-byte random value generated once per JVM (class load), and a
	 * 3-byte counter starting at a random value. Ids are roughly ordered by creation second.
	 *
	 * <pre>
	 * IdUtils.objectId() // =&gt; "6706b6c0a1b2c3d4e5000001"
	 * </pre>
	 */
	public static String objectId() {
		int seconds = (int) (System.currentTimeMillis() / 1000L);
		int counter = OBJECT_ID_COUNTER.getAndIncrement() & 0xFFFFFF;
		char[] buf = new char[24];
		int p = 0;
		p = writeByte(seconds >>> 24, buf, p);
		p = writeByte(seconds >>> 16, buf, p);
		p = writeByte(seconds >>> 8, buf, p);
		p = writeByte(seconds, buf, p);
		for (byte b : OBJECT_ID_RANDOM) {
			p = writeByte(b, buf, p);
		}
		p = writeByte(counter >>> 16, buf, p);
		p = writeByte(counter >>> 8, buf, p);
		writeByte(counter, buf, p);
		return new String(buf);
	}

	private static int writeByte(int b, char[] buf, int pos) {
		buf[pos] = HEX[(b >>> 4) & 0xF];
		buf[pos + 1] = HEX[b & 0xF];
		return pos + 2;
	}

	private static void writeHex(long value, char[] buf, int offset) {
		for (int i = 15; i >= 0; i--) {
			buf[offset + i] = HEX[(int) (value & 0xF)];
			value >>>= 4;
		}
	}

}
