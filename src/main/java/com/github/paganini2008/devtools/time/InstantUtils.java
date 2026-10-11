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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * Conversions from other date types to {@link Instant}, null safe.
 *
 * @author Fred Feng
 * @date 2021-12-14
 * @since 2.0.4
 */
public abstract class InstantUtils {

	/**
	 * Converts milliseconds to an {@code Instant}, null returns null.
	 */
	public static Instant toInstant(Long timeInMs) {
		return toInstant(timeInMs, null);
	}

	/**
	 * Converts milliseconds to an {@code Instant}, returning the default value for null.
	 */
	public static Instant toInstant(Long timeInMs, Instant defaultValue) {
		if (timeInMs == null) {
			return defaultValue;
		}
		return Instant.ofEpochMilli(timeInMs);
	}

	/**
	 * Converts a date to an {@code Instant}, null returns null.
	 */
	public static Instant toInstant(Date date) {
		return toInstant(date, null);
	}

	/**
	 * Converts a date to an {@code Instant}, returning the default value for null.
	 */
	public static Instant toInstant(Date date, Instant defaultValue) {
		if (date == null) {
			return defaultValue;
		}
		return date.toInstant();
	}

	/**
	 * Converts a calendar to an {@code Instant}, null returns null.
	 */
	public static Instant toInstant(Calendar calendar) {
		return toInstant(calendar, null);
	}

	/**
	 * Converts a calendar to an {@code Instant}, returning the default value for null.
	 */
	public static Instant toInstant(Calendar calendar, Instant defaultValue) {
		if (calendar == null) {
			return defaultValue;
		}
		return calendar.toInstant();
	}

	/**
	 * Converts a local date time in the system default time zone to an {@code Instant}, null returns null.
	 */
	public static Instant toInstant(LocalDateTime ldt) {
		return toInstant(ldt, null);
	}

	/**
	 * Converts a local date time to an {@code Instant} using the offset of the zone at that date time, null returns null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static Instant toInstant(LocalDateTime ldt, ZoneId zoneId) {
		return toInstant(ldt, zoneId, null);
	}

	/**
	 * Converts a local date time to an {@code Instant} using the offset of the zone at that date time,
	 * returning the default value for null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static Instant toInstant(LocalDateTime ldt, ZoneId zoneId, Instant defaultValue) {
		if (ldt == null) {
			return defaultValue;
		}
		if (zoneId == null) {
			zoneId = ZoneId.systemDefault();
		}
		return ldt.atZone(zoneId).toInstant();
	}

	/**
	 * Converts the start of a local date in the system default time zone to an {@code Instant}, null returns null.
	 */
	public static Instant toInstant(LocalDate ld) {
		return toInstant(ld, null);
	}

	/**
	 * Converts the start of a local date to an {@code Instant}, null returns null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static Instant toInstant(LocalDate ld, ZoneId zoneId) {
		return toInstant(ld, zoneId, null);
	}

	/**
	 * Converts the start of a local date to an {@code Instant}, returning the default value for null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static Instant toInstant(LocalDate ld, ZoneId zoneId, Instant defaultValue) {
		if (ld == null) {
			return defaultValue;
		}
		if (zoneId == null) {
			zoneId = ZoneId.systemDefault();
		}
		return ld.atStartOfDay(zoneId).toInstant();
	}

}
