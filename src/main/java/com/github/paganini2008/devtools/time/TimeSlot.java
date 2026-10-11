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
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * 
 * Granularity of a time slot (day, hour, minute or second) used to group timestamps into fixed-size buckets.
 *
 * @author Fred Feng
 * @date 2021-11-30
 * @since 2.0.4
 */
public enum TimeSlot {

	DAY {
		@Override
		public int sizeOf(int span, int days) {
			return span * days;
		}

		@Override
		public LocalDateTime locate(Instant timestamp, int span, ZoneId zoneId) {
			LocalDateTime ldt = timestamp.atZone(zoneId).toLocalDate().atTime(0, 0);
			return ldt;
		}
	},

	HOUR {

		@Override
		public LocalDateTime locate(Instant timestamp, int span, ZoneId zoneId) {
			final ZonedDateTime zdt = timestamp.atZone(zoneId);
			int hour = zdt.getHour();
			return LocalDateTime.of(zdt.toLocalDate(), LocalTime.of(hour - hour % span, 0, 0));
		}

		@Override
		public int sizeOf(int span, int days) {
			return (24 % span == 0 ? (24 / span) : (24 / span + 1)) * days;
		}

	},
	MINUTE {

		@Override
		public LocalDateTime locate(Instant timestamp, int span, ZoneId zoneId) {
			final ZonedDateTime zdt = timestamp.atZone(zoneId);
			int hour = zdt.getHour();
			int minute = zdt.getMinute();
			return LocalDateTime.of(zdt.toLocalDate(), LocalTime.of(hour, minute - minute % span, 0));
		}

		@Override
		public int sizeOf(int span, int days) {
			return (60 % span == 0 ? (60 / span) : (60 / span + 1)) * 24 * days;
		}

	},
	SECOND {

		@Override
		public LocalDateTime locate(Instant timestamp, int span, ZoneId zoneId) {
			final ZonedDateTime zdt = timestamp.atZone(zoneId);
			int hour = zdt.getHour();
			int minute = zdt.getMinute();
			int second = zdt.getSecond();
			return LocalDateTime.of(zdt.toLocalDate(), LocalTime.of(hour, minute, second - second % span));
		}

		@Override
		public int sizeOf(int span, int days) {
			return (60 % span == 0 ? (60 / span) : (60 / span + 1)) * 60 * 24 * days;
		}

	};

	/**
	 * Locates the start of the slot containing the timestamp in the system default time zone.
	 *
	 * @see #locate(Instant, int, ZoneId)
	 */
	public LocalDateTime locate(Instant timestamp, int span) {
		return locate(timestamp, span, ZoneId.systemDefault());
	}

	/**
	 * Locates the start of the slot containing the timestamp, slots are {@code span} units wide and aligned
	 * to the start of the day (or hour/minute).
	 *
	 * <pre>
	 * TimeSlot.MINUTE.locate(2021-01-31T10:47:38Z, 15, UTC) = 2021-01-31T10:45
	 * TimeSlot.HOUR.locate(2021-01-31T10:47:38Z, 3, UTC)    = 2021-01-31T09:00
	 * </pre>
	 */
	public abstract LocalDateTime locate(Instant timestamp, int span, ZoneId zoneId);

	/**
	 * Returns how many slots of the given span are needed to cover the given number of days.
	 */
	public abstract int sizeOf(int span, int days);

}
