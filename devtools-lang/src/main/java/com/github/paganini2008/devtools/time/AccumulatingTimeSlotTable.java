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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.paganini2008.devtools.collection.MapUtils;
import com.github.paganini2008.devtools.collection.MutableMap;

/**
 * 
 * AccumulatingTimeSlotTable
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
public class AccumulatingTimeSlotTable<V> extends MutableMap<Instant, List<V>> implements MeasureTable<List<V>> {

	private static final long serialVersionUID = 8180993603631297273L;

	private final int span;
	private final TimeSlot timeSlot;

	public AccumulatingTimeSlotTable(int span, TimeSlot timeSlot) {
		this(new ConcurrentHashMap<>(), span, timeSlot);
	}

	public AccumulatingTimeSlotTable(Map<Instant, List<V>> delegate, int span, TimeSlot timeSlot) {
		super(delegate);
		this.span = span;
		this.timeSlot = timeSlot;
	}

	public List<V> put(Instant ins, V value) {
		List<V> list = MapUtils.get(this, ins, () -> new CopyOnWriteArrayList<>());
		list.add(value);
		return list;
	}

	@Override
	public List<V> put(Instant ins, List<V> values) {
		List<V> list = MapUtils.get(this, ins, () -> new CopyOnWriteArrayList<>());
		list.addAll(values);
		return list;
	}

	@Override
	protected Instant mutate(Object inputKey) {
		LocalDateTime ldt = timeSlot.locate((Instant) inputKey, span);
		return ldt.atZone(ZoneId.systemDefault()).toInstant();
	}

}
