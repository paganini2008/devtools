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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicStampedReference;

import com.github.paganini2008.devtools.collection.AtomicMutableMap;

/**
 * 
 * AggregationTimeSlotMap
 *
 * @author Fred Feng
 * @since 2.0.4
 */
public class AggregationTimeSlotMap<V> extends AtomicMutableMap<Instant, V> implements TimeSlotMap<V> {

	private static final long serialVersionUID = -1609264341186593908L;

	private final TimeSlot timeSlot;
	private final int span;

	public AggregationTimeSlotMap(int span, TimeSlot timeSlot) {
		this(new ConcurrentHashMap<>(), span, timeSlot);
	}

	public AggregationTimeSlotMap(Map<Instant, AtomicStampedReference<V>> delegate, int span, TimeSlot timeSlot) {
		super(delegate);
		this.timeSlot = timeSlot;
		this.span = span;
	}

	@Override
	public Instant mutate(Object inputKey) {
		LocalDateTime ldt = timeSlot.locate((Instant) inputKey, span);
		return ldt.atZone(ZoneId.systemDefault()).toInstant();
	}

}
