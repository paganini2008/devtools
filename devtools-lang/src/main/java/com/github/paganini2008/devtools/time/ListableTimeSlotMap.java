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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import com.github.paganini2008.devtools.Console;
import com.github.paganini2008.devtools.RandomDateUtils;
import com.github.paganini2008.devtools.Sequence;
import com.github.paganini2008.devtools.collection.MutableMap;

/**
 * 
 * ListableTimeSlotMap
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
public class ListableTimeSlotMap<V> extends MutableMap<Instant, List<V>> implements TimeSlotMap<List<V>> {

	private static final long serialVersionUID = 8180993603631297273L;

	private final int span;
	private final TimeSlot timeSlot;

	public ListableTimeSlotMap(int span, TimeSlot timeSlot) {
		this(new ConcurrentHashMap<>(), span, timeSlot);
	}

	public ListableTimeSlotMap(Map<Instant, List<V>> delegate, int span, TimeSlot timeSlot) {
		super(delegate);
		this.span = span;
		this.timeSlot = timeSlot;
	}

	public List<V> append(Instant key, V value) {
		return append(key, value, () -> new ArrayList<V>());
	}

	public List<V> append(Instant key, V value, Supplier<List<V>> supplier) {
		List<V> values = get(key);
		if (values == null) {
			putIfAbsent(key, supplier.get());
			values = get(key);
		}
		values.add(value);
		return values;
	}

	public List<V> appendAll(Instant key, List<V> list) {
		return appendAll(key, list, () -> new ArrayList<V>());
	}

	public List<V> appendAll(Instant key, List<V> list, Supplier<List<V>> supplier) {
		List<V> values = get(key);
		if (values == null) {
			putIfAbsent(key, supplier.get());
			values = get(key);
		}
		values.addAll(list);
		return values;
	}

	@Override
	public Instant mutate(Object inputKey) {
		LocalDateTime ldt = timeSlot.locate((Instant) inputKey, span);
		return ldt.atZone(ZoneId.systemDefault()).toInstant();
	}

	public static void main(String[] args) {
		ListableTimeSlotMap<String> map = new ListableTimeSlotMap<>(5, TimeSlot.MINUTE);
		for (int i : Sequence.forEach(1, 10000)) {
			LocalDateTime ldt = RandomDateUtils.randomLocalDateTime("2022-03-22 00:00:00", "2022-03-22 15:59:59",
					DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			map.append(InstantUtils.toInstant(ldt, null, null), "Val_" + i);
		}
		Console.log(map);
	}

}
