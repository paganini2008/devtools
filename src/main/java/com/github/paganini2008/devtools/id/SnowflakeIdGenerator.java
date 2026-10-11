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

/**
 * Thread-safe generator of 64-bit, roughly time-ordered snowflake ids.
 *
 * <p>
 * Bit layout (most significant first):
 *
 * <pre>
 * | 1 bit sign (always 0) | 41 bits timestamp | 5 bits datacenterId | 5 bits workerId | 12 bits sequence |
 * </pre>
 *
 * The timestamp is the number of milliseconds since a custom epoch, which defaults to
 * {@link #DEFAULT_EPOCH} (2020-01-01T00:00:00Z); 41 bits cover about 69 years. Up to 4096 ids
 * can be generated per millisecond per worker; when the sequence is exhausted the generator
 * spins until the next millisecond.
 *
 * <p>
 * <b>Clock rollback policy:</b> if the clock moves backwards by at most
 * {@link #MAX_BACKWARD_MILLIS} (5 ms), the generator waits until the clock catches up with the
 * last timestamp; if it moves back further, {@link #generateId()} throws
 * {@link IllegalStateException} instead of risking duplicate ids.
 *
 * <p>
 * The time source can be replaced by overriding {@link #currentTimeMillis()}.
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public class SnowflakeIdGenerator implements IdGenerator<Long> {

	/**
	 * Default custom epoch: 2020-01-01T00:00:00Z (1577836800000 ms).
	 */
	public static final long DEFAULT_EPOCH = 1577836800000L;

	public static final int SEQUENCE_BITS = 12;
	public static final int WORKER_ID_BITS = 5;
	public static final int DATACENTER_ID_BITS = 5;
	public static final int TIMESTAMP_BITS = 41;

	public static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;
	public static final long MAX_WORKER_ID = (1L << WORKER_ID_BITS) - 1;
	public static final long MAX_DATACENTER_ID = (1L << DATACENTER_ID_BITS) - 1;
	public static final long MAX_TIMESTAMP = (1L << TIMESTAMP_BITS) - 1;

	/**
	 * Maximum tolerated clock rollback in milliseconds; within it the generator waits.
	 */
	public static final long MAX_BACKWARD_MILLIS = 5L;

	private static final int WORKER_ID_SHIFT = SEQUENCE_BITS;
	private static final int DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
	private static final int TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

	private final long workerId;
	private final long datacenterId;
	private final long epochMillis;

	private long lastTimestamp = -1L;
	private long sequence = 0L;

	/**
	 * Creates a generator using {@link #DEFAULT_EPOCH}.
	 *
	 * @throws IllegalArgumentException if workerId or datacenterId is outside [0, 31]
	 */
	public SnowflakeIdGenerator(long workerId, long datacenterId) {
		this(workerId, datacenterId, DEFAULT_EPOCH);
	}

	/**
	 * Creates a generator with a custom epoch.
	 *
	 * @throws IllegalArgumentException if workerId or datacenterId is outside [0, 31], or
	 *             epochMillis is negative
	 */
	public SnowflakeIdGenerator(long workerId, long datacenterId, long epochMillis) {
		if (workerId < 0 || workerId > MAX_WORKER_ID) {
			throw new IllegalArgumentException("workerId must be between 0 and " + MAX_WORKER_ID + ": " + workerId);
		}
		if (datacenterId < 0 || datacenterId > MAX_DATACENTER_ID) {
			throw new IllegalArgumentException("datacenterId must be between 0 and " + MAX_DATACENTER_ID + ": " + datacenterId);
		}
		if (epochMillis < 0) {
			throw new IllegalArgumentException("epochMillis must not be negative: " + epochMillis);
		}
		this.workerId = workerId;
		this.datacenterId = datacenterId;
		this.epochMillis = epochMillis;
	}

	/**
	 * Generates the next id; same as {@link #nextId()}.
	 *
	 * @throws IllegalStateException if the clock moved backwards by more than 5 ms, or the
	 *             current time is before the epoch or beyond the 41-bit range
	 */
	@Override
	public Long generateId() {
		return nextId();
	}

	/**
	 * Generates the next id as a primitive long. Ids from one generator are strictly increasing.
	 *
	 * @throws IllegalStateException if the clock moved backwards by more than 5 ms, or the
	 *             current time is before the epoch or beyond the 41-bit range
	 */
	public synchronized long nextId() {
		long timestamp = currentTimeMillis();
		if (timestamp < lastTimestamp) {
			checkBackward(timestamp);
			timestamp = waitUntil(lastTimestamp);
		}
		if (timestamp == lastTimestamp) {
			sequence = (sequence + 1) & MAX_SEQUENCE;
			if (sequence == 0) {
				timestamp = waitUntil(lastTimestamp + 1);
			}
		} else {
			sequence = 0L;
		}
		long delta = timestamp - epochMillis;
		if (delta < 0 || delta > MAX_TIMESTAMP) {
			throw new IllegalStateException("Current time " + timestamp + " is out of range for epoch " + epochMillis);
		}
		lastTimestamp = timestamp;
		return (delta << TIMESTAMP_SHIFT) | (datacenterId << DATACENTER_ID_SHIFT) | (workerId << WORKER_ID_SHIFT) | sequence;
	}

	private void checkBackward(long timestamp) {
		long offset = lastTimestamp - timestamp;
		if (offset > MAX_BACKWARD_MILLIS) {
			throw new IllegalStateException("Clock moved backwards by " + offset + " ms (tolerance " + MAX_BACKWARD_MILLIS
					+ " ms); refusing to generate id");
		}
	}

	private long waitUntil(long target) {
		long timestamp = currentTimeMillis();
		while (timestamp < target) {
			if (timestamp < lastTimestamp) {
				checkBackward(timestamp);
			}
			Thread.yield();
			timestamp = currentTimeMillis();
		}
		return timestamp;
	}

	/**
	 * Returns the current time in milliseconds; override to supply a custom clock.
	 */
	protected long currentTimeMillis() {
		return System.currentTimeMillis();
	}

	public long getWorkerId() {
		return workerId;
	}

	public long getDatacenterId() {
		return datacenterId;
	}

	public long getEpochMillis() {
		return epochMillis;
	}

	/**
	 * Decodes an id generated with {@link #DEFAULT_EPOCH}.
	 *
	 * @throws IllegalArgumentException if the id is negative
	 */
	public static SnowflakeId parse(long id) {
		return parse(id, DEFAULT_EPOCH);
	}

	/**
	 * Decodes an id generated with the given epoch.
	 *
	 * <pre>
	 * long id = new SnowflakeIdGenerator(3, 7).nextId();
	 * SnowflakeId sid = SnowflakeIdGenerator.parse(id);
	 * sid.getWorkerId()     // =&gt; 3
	 * sid.getDatacenterId() // =&gt; 7
	 * </pre>
	 *
	 * @throws IllegalArgumentException if the id is negative
	 */
	public static SnowflakeId parse(long id, long epochMillis) {
		if (id < 0) {
			throw new IllegalArgumentException("Snowflake id must not be negative: " + id);
		}
		long sequence = id & MAX_SEQUENCE;
		long workerId = (id >>> WORKER_ID_SHIFT) & MAX_WORKER_ID;
		long datacenterId = (id >>> DATACENTER_ID_SHIFT) & MAX_DATACENTER_ID;
		long timestamp = (id >>> TIMESTAMP_SHIFT) + epochMillis;
		return new SnowflakeId(id, timestamp, datacenterId, workerId, sequence);
	}

	@Override
	public String toString() {
		return "SnowflakeIdGenerator[workerId=" + workerId + ", datacenterId=" + datacenterId + ", epochMillis=" + epochMillis + "]";
	}

}
