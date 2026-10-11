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
package com.github.paganini2008.devtools.time;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import com.github.paganini2008.devtools.collection.MutableMap;

/**
 * 
 * A {@link TimeSlotMap} whose value of each time slot is a list that values can be appended to.
 *
 * @author Fred Feng
 * @date 2022-03-22
 *
 * @since 2.0.4
 */
public class AppendableTimeSlotMap<V> extends MutableMap<Instant, List<V>> implements TimeSlotMap<List<V>> {

	private static final long serialVersionUID = 8180993603631297273L;

	private final int span;
	private final TimeSlot timeSlot;
	private final ZoneId zoneId;

	/**
	 * Creates a map backed by a {@code ConcurrentHashMap} using the system default time zone.
	 */
	public AppendableTimeSlotMap(int span, TimeSlot timeSlot) {
		this(new ConcurrentHashMap<>(), span, timeSlot);
	}

	/**
	 * Creates a map with the given delegate using the system default time zone.
	 */
	public AppendableTimeSlotMap(Map<Instant, List<V>> delegate, int span, TimeSlot timeSlot) {
		this(delegate, span, timeSlot, null);
	}

	/**
	 * Creates a map with the given delegate.
	 *
	 * @param zoneId the time zone used to align slots, null means the system default
	 */
	public AppendableTimeSlotMap(Map<Instant, List<V>> delegate, int span, TimeSlot timeSlot, ZoneId zoneId) {
		super(delegate);
		this.span = span;
		this.timeSlot = timeSlot;
		this.zoneId = zoneId != null ? zoneId : ZoneId.systemDefault();
	}

	/**
	 * Appends a value to the list of the slot containing the key, creating the list if absent.
	 *
	 * @return the list of that slot
	 */
	public List<V> append(Instant key, V value) {
		return append(key, value, () -> new ArrayList<V>());
	}

	/**
	 * Appends a value to the list of the slot containing the key, using the supplier to create the list if absent.
	 */
	public List<V> append(Instant key, V value, Supplier<List<V>> supplier) {
		List<V> values = getOrCreate(key, supplier);
		synchronized (values) {
			values.add(value);
		}
		return values;
	}

	/**
	 * Appends all values to the list of the slot containing the key.
	 */
	public List<V> appendAll(Instant key, List<V> list) {
		return appendAll(key, list, () -> new ArrayList<V>());
	}

	/**
	 * Appends all values to the list of the slot containing the key, using the supplier to create the list if absent.
	 */
	public List<V> appendAll(Instant key, List<V> list, Supplier<List<V>> supplier) {
		List<V> values = getOrCreate(key, supplier);
		synchronized (values) {
			values.addAll(list);
		}
		return values;
	}

	private List<V> getOrCreate(Instant key, Supplier<List<V>> supplier) {
		List<V> values = get(key);
		if (values == null) {
			List<V> newValues = supplier.get();
			values = putIfAbsent(key, newValues);
			if (values == null) {
				values = newValues;
			}
		}
		return values;
	}

	@Override
	public List<V> putIfAbsent(Instant key, List<V> value) {
		// atomic on the delegate instead of the non-atomic default implementation of Map
		return delegate.putIfAbsent(mutate(key), value);
	}

	@Override
	public Instant mutate(Object inputKey) {
		LocalDateTime ldt = timeSlot.locate((Instant) inputKey, span, zoneId);
		return ldt.atZone(zoneId).toInstant();
	}
	
	public TimeSlot getTimeSlot() {
		return timeSlot;
	}

	public int getSpan() {
		return span;
	}

	public ZoneId getZoneId() {
		return zoneId;
	}
}
