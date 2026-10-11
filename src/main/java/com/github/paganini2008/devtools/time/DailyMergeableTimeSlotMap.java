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

import java.time.ZoneId;

import com.github.paganini2008.devtools.collection.ConcurrentSortedBoundedMap;

/**
 * 
 * A bounded {@link MergeableTimeSlotMap} which only keeps the latest slots covering a number of days.
 *
 * @author Fred Feng
 * @date 2021-12-01
 * @since 2.0.4
 */
public class DailyMergeableTimeSlotMap<V extends MergeableFunction<V>> extends MergeableTimeSlotMap<V> {

	private static final long serialVersionUID = 1020741898314951406L;

	/**
	 * Creates a map which keeps the slots of one day.
	 */
	public DailyMergeableTimeSlotMap(int span, TimeSlot timeSlot) {
		this(span, timeSlot, 1);
	}

	/**
	 * Creates a map which keeps the slots of the given number of days, the oldest slots are evicted first.
	 */
	public DailyMergeableTimeSlotMap(int span, TimeSlot timeSlot, int days) {
		this(span, timeSlot, days, null);
	}

	/**
	 * Creates a map which keeps the slots of the given number of days.
	 *
	 * @param zoneId the time zone used to align slots, null means the system default
	 */
	public DailyMergeableTimeSlotMap(int span, TimeSlot timeSlot, int days, ZoneId zoneId) {
		super(new ConcurrentSortedBoundedMap<>(timeSlot.sizeOf(span, days)), span, timeSlot, zoneId);
	}

}
