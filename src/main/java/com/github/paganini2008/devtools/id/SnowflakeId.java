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

import java.io.Serializable;

/**
 * Immutable decoded form of a snowflake id, as returned by
 * {@link SnowflakeIdGenerator#parse(long)}.
 *
 * @author Fred Feng
 * @date 2026-10-11
 * @since 3.0.0
 */
public final class SnowflakeId implements Serializable {

	private static final long serialVersionUID = 1L;

	private final long id;
	private final long timestamp;
	private final long datacenterId;
	private final long workerId;
	private final long sequence;

	public SnowflakeId(long id, long timestamp, long datacenterId, long workerId, long sequence) {
		this.id = id;
		this.timestamp = timestamp;
		this.datacenterId = datacenterId;
		this.workerId = workerId;
		this.sequence = sequence;
	}

	/**
	 * Returns the raw 64-bit id.
	 */
	public long getId() {
		return id;
	}

	/**
	 * Returns the absolute generation time in milliseconds since the Unix epoch
	 * (the custom epoch has already been added back).
	 */
	public long getTimestamp() {
		return timestamp;
	}

	public long getDatacenterId() {
		return datacenterId;
	}

	public long getWorkerId() {
		return workerId;
	}

	public long getSequence() {
		return sequence;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof SnowflakeId)) {
			return false;
		}
		SnowflakeId other = (SnowflakeId) obj;
		return id == other.id && timestamp == other.timestamp && datacenterId == other.datacenterId && workerId == other.workerId
				&& sequence == other.sequence;
	}

	@Override
	public int hashCode() {
		int result = Long.hashCode(id);
		result = 31 * result + Long.hashCode(timestamp);
		return result;
	}

	@Override
	public String toString() {
		return "SnowflakeId[id=" + id + ", timestamp=" + timestamp + ", datacenterId=" + datacenterId + ", workerId=" + workerId
				+ ", sequence=" + sequence + "]";
	}

}
