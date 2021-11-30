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
package com.github.paganini2008.devtools.date;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import com.github.paganini2008.devtools.collection.LruMap;
import com.github.paganini2008.devtools.collection.MutableMap;

/**
 * 
 * TimeSlotTable
 *
 * @author Fred Feng
 * @since 2.0.4
 */
public class TimeSlotTable extends MutableMap<Instant, Object> {

	private static final long serialVersionUID = -1609264341186593908L;

	private final TimeSlot timeSlot;
	private final int span;

	public TimeSlotTable(Map<Instant, Object> delegate, int span, TimeSlot timeSlot) {
		super(delegate);
		this.timeSlot = timeSlot;
		this.span = span;
	}

	@Override
	protected Instant mutate(Object inputKey) {
		LocalDateTime ldt = timeSlot.locate((Instant) inputKey, span);
		return ldt.atZone(ZoneId.systemDefault()).toInstant();
	}

	public static TimeSlotTable scroll(int span, TimeSlot timeSlot) {
		return new TimeSlotTable(new LruMap<>(timeSlot.sizeOf(span)), span, timeSlot);
	}

}
