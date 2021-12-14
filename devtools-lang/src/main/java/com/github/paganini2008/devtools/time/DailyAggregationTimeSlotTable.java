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

import com.github.paganini2008.devtools.collection.ConcurrentSortedBoundedMap;

/**
 * 
 * DailyAggregationTimeSlotTable
 *
 * @author Fred Feng
 * @since 2.0.4
 */
public class DailyAggregationTimeSlotTable<V> extends AggregationTimeSlotMap<V> {

	private static final long serialVersionUID = 1020741898314951406L;

	public DailyAggregationTimeSlotTable(int span, TimeSlot timeSlot) {
		this(span, timeSlot, 1);
	}

	public DailyAggregationTimeSlotTable(int span, TimeSlot timeSlot, int days) {
		super(new ConcurrentSortedBoundedMap<>(timeSlot.sizeOf(span, days)), span, timeSlot);
	}

}
