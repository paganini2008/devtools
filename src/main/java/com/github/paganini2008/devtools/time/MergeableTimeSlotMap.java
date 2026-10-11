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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.stream.Collectors;

import com.github.paganini2008.devtools.collection.AtomicMutableMap;

/**
 * 
 * A {@link TimeSlotMap} which atomically merges values falling into the same time slot.
 *
 * @author Fred Feng
 * @date 2021-11-30
 * @since 2.0.4
 */
public class MergeableTimeSlotMap<V extends MergeableFunction<V>> extends AtomicMutableMap<Instant, V> implements TimeSlotMap<V> {

	private static final long serialVersionUID = -1609264341186593908L;

	private final TimeSlot timeSlot;
	private final int span;
	private final ZoneId zoneId;

	/**
	 * Creates a map backed by a {@code ConcurrentHashMap} using the system default time zone.
	 */
	public MergeableTimeSlotMap(int span, TimeSlot timeSlot) {
		this(new ConcurrentHashMap<>(), span, timeSlot);
	}

	/**
	 * Creates a map with the given delegate using the system default time zone.
	 */
	public MergeableTimeSlotMap(Map<Instant, AtomicStampedReference<V>> delegate, int span, TimeSlot timeSlot) {
		this(delegate, span, timeSlot, null);
	}

	/**
	 * Creates a map with the given delegate.
	 *
	 * @param zoneId the time zone used to align slots, null means the system default
	 */
	public MergeableTimeSlotMap(Map<Instant, AtomicStampedReference<V>> delegate, int span, TimeSlot timeSlot, ZoneId zoneId) {
		super(delegate);
		this.timeSlot = timeSlot;
		this.span = span;
		this.zoneId = zoneId != null ? zoneId : ZoneId.systemDefault();
	}

	/**
	 * Atomically merges the value into the slot containing the instant.
	 *
	 * @return the merged value of that slot
	 */
	public V merge(Instant ins, V newValue) {
		return merge(ins, newValue, (current, update) -> (current != null) ? current.merge(update) : update);
	}

	@Override
	public Instant mutate(Object inputKey) {
		LocalDateTime ldt = timeSlot.locate((Instant) inputKey, span, zoneId);
		return ldt.atZone(zoneId).toInstant();
	}

	@Override
	public Map<Instant, V> toMap() {
		return delegate.entrySet().stream().sorted(Map.Entry.comparingByKey())
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getReference(), (o, n) -> o, LinkedHashMap::new));
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
