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

import com.github.paganini2008.devtools.collection.MutableMap;

/**
 * 
 * AccTimeSlotTable
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
public class AccTimeSlotTable<V> extends MutableMap<Instant, List<V>> implements TimeSlotTable<List<V>> {

	private static final long serialVersionUID = 8180993603631297273L;

	private final int span;
	private final TimeSlot timeSlot;

	public AccTimeSlotTable(int span, TimeSlot timeSlot) {
		this(new ConcurrentHashMap<>(), span, timeSlot);
	}

	public AccTimeSlotTable(Map<Instant, List<V>> delegate, int span, TimeSlot timeSlot) {
		super(delegate);
		this.span = span;
		this.timeSlot = timeSlot;
	}

	@Override
	protected Instant mutate(Object inputKey) {
		LocalDateTime ldt = timeSlot.locate((Instant) inputKey, span);
		return ldt.atZone(ZoneId.systemDefault()).toInstant();
	}

}
