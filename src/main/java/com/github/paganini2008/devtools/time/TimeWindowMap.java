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
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.collection.ConcurrentSortedBoundedMap;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * 
 * Buffers payloads per time window and passes them to a {@link TimeWindowListener} when the window is full
 * or a newer window begins.
 *
 * @author Fred Feng
 * @date 2021-12-12
 * @since 2.0.4
 */
public class TimeWindowMap<V> extends ConcurrentSortedBoundedMap<Instant, List<V>> {

	private static final long serialVersionUID = 2633466884704076710L;

	/**
	 * Creates a time window map using the system default time zone.
	 */
	public TimeWindowMap(int span, TimeSlot timeSlot, int batchSize, TimeWindowListener<V> timeWindowListener) {
		this(span, timeSlot, batchSize, timeWindowListener, null);
	}

	/**
	 * Creates a time window map.
	 *
	 * @param span the width of a window in units of the time slot
	 * @param batchSize values are flushed once a window holds this many values
	 * @param timeWindowListener receives the flushed values
	 * @param zoneId the time zone used to align windows, null means the system default
	 */
	public TimeWindowMap(int span, TimeSlot timeSlot, int batchSize, TimeWindowListener<V> timeWindowListener, ZoneId zoneId) {
		super(new ConcurrentHashMap<>(), 1);
		this.timeSlotMap = new AppendableTimeSlotMap<>(this, span, timeSlot, zoneId);
		this.batchSize = batchSize;
		this.timeWindowListener = timeWindowListener;
	}

	private final AppendableTimeSlotMap<V> timeSlotMap;
	private final int batchSize;
	private final TimeWindowListener<V> timeWindowListener;

	/**
	 * Same as {@link #offer(Instant, Object)} with a time in milliseconds.
	 */
	public List<V> offer(long timeInMs, V payload) {
		return offer(Instant.ofEpochMilli(timeInMs), payload);
	}

	/**
	 * Adds a payload to the window containing the time. Moving to a new window flushes the previous one,
	 * and a payload of an already flushed window is passed to the listener immediately.
	 *
	 * @return the values currently held by the window
	 */
	public List<V> offer(Instant time, V payload) {
		Assert.isNull(time, "Time must not be null.");
		final Instant slot = timeSlotMap.mutate(time);
		final List<V> values = isLate(slot) ? null : MapUtils.get(timeSlotMap, time, () -> new CopyOnWriteArrayList<V>());
		if (values == null) {
			// The payload belongs to an earlier time window which has been evicted already
			List<V> late = new ArrayList<V>(1);
			late.add(payload);
			timeWindowListener.saveCheckPoint(slot, late);
			return late;
		}
		values.add(payload);
		ThreadUtils.forUpdate(values, () -> values.size() >= batchSize, () -> {
			onEviction(slot, values);
		});
		return values;
	}

	private boolean isLate(Instant slot) {
		for (Instant current : keySet()) {
			if (current.isAfter(slot)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Flushes the values of the current window to the listener, does nothing if there is no window.
	 */
	public void flush() {
		Map.Entry<Instant, List<V>> entry = MapUtils.getLastEntry(timeSlotMap);
		if (entry != null) {
			onEviction(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public int getMaxSize() {
		return 1;
	}

	@Override
	public final void onEviction(Instant ins, List<V> values) {
		if (values == null) {
			return;
		}
		synchronized (values) {
			List<V> copy = new ArrayList<V>(values);
			timeWindowListener.saveCheckPoint(ins, copy);
			// Only remove the elements that have been saved, newer ones may be appended concurrently
			for (int i = 0, n = copy.size(); i < n; i++) {
				values.remove(0);
			}
		}
	}

}
