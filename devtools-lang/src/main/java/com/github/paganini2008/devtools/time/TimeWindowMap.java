/**
* Copyright 2017-2021 Fred Feng (paganini.fy@gmail.com)

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.paganini2008.devtools.collection.ConcurrentSortedBoundedMap;
import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.multithreads.ThreadUtils;

/**
 * 
 * TimeWindowMap
 *
 * @author Fred Feng
 * @since 2.0.4
 */
public class TimeWindowMap<V> extends ConcurrentSortedBoundedMap<Instant, List<V>> {

	private static final long serialVersionUID = 2633466884704076710L;

	public TimeWindowMap(int span, TimeSlot timeSlot, int batchSize, TimeWindowListener<V> timeWindowListener) {
		super(new ConcurrentHashMap<>(), 1);
		this.timeSlotMap = new ListableTimeSlotMap<>(this, span, timeSlot);
		this.batchSize = batchSize;
		this.timeWindowListener = timeWindowListener;
	}

	private final TimeSlotMap<List<V>> timeSlotMap;
	private final int batchSize;
	private final TimeWindowListener<V> timeWindowListener;

	public List<V> offer(long timeInMs, V payload) {
		return offer(Instant.ofEpochMilli(timeInMs), payload);
	}

	public List<V> offer(Instant time, V payload) {
		final List<V> values = MapUtils.get(timeSlotMap, time, () -> new CopyOnWriteArrayList<V>());
		values.add(payload);
		ThreadUtils.forUpdate(values, () -> values.size() >= batchSize, () -> {
			onEviction(timeSlotMap.mutate(time), values);
		});
		return values;
	}

	@Override
	public void clear() {
		timeSlotMap.clear();
	}

	@Override
	public int size() {
		return timeSlotMap.size();
	}

	@Override
	public int getMaxSize() {
		return 1;
	}

	public void flush() {
		Map.Entry<Instant, List<V>> entry = MapUtils.getLastEntry(timeSlotMap);
		onEviction(entry.getKey(), entry.getValue());
	}

	@Override
	public final void onEviction(Instant ins, List<V> values) {
		List<V> copy = new ArrayList<V>(values);
		timeWindowListener.saveCheckPoint(ins, copy);
		values.removeAll(copy);
	}

	public static void main(String[] args) throws Exception {
		final AtomicInteger counter = new AtomicInteger();
		TimeWindowMap<String> timeWindowMap = new TimeWindowMap<String>(1, TimeSlot.MINUTE, 10000, (time, values) -> {
			System.out.println("Time: " + time + "\t Size: " + counter.getAndAdd(values.size()));
		});
		ThreadUtils.benchmark(10, 10, 100000, i -> {
			// ThreadUtils.randomSleep(500, 5000);
			timeWindowMap.offer(System.currentTimeMillis(), UUID.randomUUID().toString());
		});
		System.out.println("Completed");
		System.in.read();
		timeWindowMap.flush();
		System.out.println(counter.get());

	}

}
