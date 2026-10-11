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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.LruMap;

/**
 * 
 * Utility methods for {@link LocalTime}, null safe where possible.
 *
 * @author Fred Feng
 * @date 2021-11-16
 *
 * @since 2.0.4
 */
public abstract class LocalTimeUtils {

	public static final LocalTime[] EMPTY_ARRAY = new LocalTime[0];
	private final static LruMap<String, DateTimeFormatter> dfCache = new LruMap<String, DateTimeFormatter>(16);

	/**
	 * Converts milliseconds to a {@code LocalTime} in the zone, null returns null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static LocalTime toLocalTime(Long ms, ZoneId zoneId) {
		return toLocalTime(ms, zoneId, null);
	}

	/**
	 * Converts milliseconds to a {@code LocalTime} in the zone, returning the default value for null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static LocalTime toLocalTime(Long ms, ZoneId zoneId, LocalTime defaultValue) {
		if (ms == null) {
			return defaultValue;
		}
		return toLocalTime(Instant.ofEpochMilli(ms), zoneId, defaultValue);
	}

	/**
	 * Converts an instant to a {@code LocalTime} in the zone, null returns null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static LocalTime toLocalTime(Instant instant, ZoneId zoneId) {
		return toLocalTime(instant, zoneId, null);
	}

	/**
	 * Converts an instant to a {@code LocalTime} in the zone, returning the default value for null
	 * or if the result is out of the supported range.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static LocalTime toLocalTime(Instant instant, ZoneId zoneId, LocalTime defaultValue) {
		if (zoneId == null) {
			zoneId = ZoneId.systemDefault();
		}
		try {
			return instant != null ? instant.atZone(zoneId).toLocalTime() : defaultValue;
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	/**
	 * Converts a date to a {@code LocalTime} in the zone, null returns null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static LocalTime toLocalTime(Date date, ZoneId zoneId) {
		return toLocalTime(date, zoneId, null);
	}

	/**
	 * Converts a date to a {@code LocalTime} in the zone, returning the default value for null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static LocalTime toLocalTime(Date date, ZoneId zoneId, LocalTime defaultValue) {
		if (date == null) {
			return defaultValue;
		}
		return toLocalTime(date.toInstant(), zoneId, defaultValue);
	}

	/**
	 * Converts a calendar to a {@code LocalTime} in the zone, null returns null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static LocalTime toLocalTime(Calendar calendar, ZoneId zoneId) {
		return toLocalTime(calendar, zoneId, null);
	}

	/**
	 * Converts a calendar to a {@code LocalTime} in the zone, returning the default value for null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static LocalTime toLocalTime(Calendar calendar, ZoneId zoneId, LocalTime defaultValue) {
		if (calendar == null) {
			return defaultValue;
		}
		return toLocalTime(calendar.toInstant(), zoneId, defaultValue);
	}

	/**
	 * Returns the time part of a local date time, returning the default value for null.
	 */
	public static LocalTime toLocalTime(LocalDateTime ldt, LocalTime defaultValue) {
		if (ldt == null) {
			return defaultValue;
		}
		return ldt.toLocalTime();
	}

	/**
	 * Parses a string with the ISO format, returning null if it is blank or can not be parsed.
	 */
	public static LocalTime parseLocalTime(String text) {
		return parseLocalTime(text, DateTimeFormatter.ISO_LOCAL_TIME);
	}

	/**
	 * Parses a string with the formatter (null means ISO), returning null if it is blank or can not be parsed.
	 */
	public static LocalTime parseLocalTime(String text, DateTimeFormatter formatter) {
		return parseLocalTime(text, formatter, null);
	}

	/**
	 * Parses a string with the formatter (null means ISO), returning the default value if it is blank or can not be parsed.
	 */
	public static LocalTime parseLocalTime(String text, DateTimeFormatter formatter, LocalTime defaultValue) {
		if (formatter == null) {
			formatter = DateTimeFormatter.ISO_LOCAL_TIME;
		}
		try {
			return StringUtils.isNotBlank(text) ? LocalTime.parse(text, formatter) : defaultValue;
		} catch (DateTimeParseException e) {
			return defaultValue;
		}
	}

	/**
	 * Parses a string with the pattern (English locale), returning null if it is blank or can not be parsed.
	 *
	 * @throws IllegalArgumentException if the pattern is blank
	 */
	public static LocalTime parseLocalTime(String text, String datePattern) {
		return parseLocalTime(text, datePattern, null);
	}

	/**
	 * Parses a string with the pattern (English locale), returning the default value if it is blank or can not be parsed.
	 *
	 * @throws IllegalArgumentException if the pattern is blank
	 */
	public static LocalTime parseLocalTime(String text, String datePattern, LocalTime defaultValue) {
		return parseLocalTime(text, getDateTimeFormatter(datePattern), defaultValue);
	}

	/**
	 * Obtains a local time after validating the hour and the minute.
	 *
	 * @throws IllegalArgumentException if any value is out of range
	 */
	public static LocalTime of(int hourOfDay, int minute) {
		return of(hourOfDay, minute, 0);
	}

	/**
	 * Obtains a local time after validating the hour, the minute and the second.
	 *
	 * @throws IllegalArgumentException if any value is out of range
	 */
	public static LocalTime of(int hourOfDay, int minute, int second) {
		TimeAssert.validateTime(hourOfDay, minute, second);
		return LocalTime.of(hourOfDay, minute, second);
	}

	private static DateTimeFormatter getDateTimeFormatter(String datePattern) {
		Assert.hasNoText(datePattern, "DatePattern can not be blank.");
		DateTimeFormatter sdf = dfCache.get(datePattern);
		if (sdf == null) {
			dfCache.putIfAbsent(datePattern, DateTimeFormatter.ofPattern(datePattern, Locale.ENGLISH));
			sdf = dfCache.get(datePattern);
		}
		return sdf;
	}

	/**
	 * Returns the given value, which is immutable, or the current time of the zone if it is null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static LocalTime copy(LocalTime lt, ZoneId zoneId) {
		if (zoneId == null) {
			zoneId = ZoneId.systemDefault();
		}
		// LocalTime is immutable, so it is safe to return the same instance
		return lt != null ? lt : LocalTime.now(zoneId);
	}

	/**
	 * Iterates from the parsed start (inclusive) to the parsed end (exclusive) by the interval.
	 *
	 * @param dtf the formatter, null means ISO
	 */
	public static Iterator<LocalTime> toIterator(String startTime, String endTime, DateTimeFormatter dtf, ZoneId zoneId, int interval,
			ChronoUnit chronoUnit) {
		return new LocalTimeIterator(parseLocalTime(startTime, dtf), parseLocalTime(endTime, dtf), zoneId, interval, chronoUnit);
	}

	/**
	 * Iterates from the start (inclusive) to the end (exclusive) by the interval, dates are converted in the zone.
	 *
	 */
	public static Iterator<LocalTime> toIterator(Date startTime, Date endTime, ZoneId zoneId, int interval, ChronoUnit chronoUnit) {
		return new LocalTimeIterator(toLocalTime(startTime, zoneId), toLocalTime(endTime, zoneId), zoneId, interval, chronoUnit);
	}

	/**
	 * Iterates from the start (inclusive) to the end (exclusive) by the interval. Times wrap around midnight;
	 * the iteration stops if the step cycles back to the start time.
	 *
	 * @param zoneId not used, kept for compatibility
	 */
	public static Iterator<LocalTime> toIterator(LocalTime startTime, LocalTime endTime, ZoneId zoneId, int interval,
			ChronoUnit chronoUnit) {
		return new LocalTimeIterator(startTime, endTime, zoneId, interval, chronoUnit);
	}

	static class LocalTimeIterator implements Iterator<LocalTime> {

		LocalTimeIterator(LocalTime startTime, LocalTime endTime, ZoneId zoneId, int interval, ChronoUnit chronoUnit) {
			this.first = startTime;
			this.current = startTime;
			this.endTime = endTime;
			this.interval = interval;
			this.chronoUnit = chronoUnit;
		}

		private final LocalTime first;
		private LocalTime current;
		private final LocalTime endTime;
		private final int interval;
		private final ChronoUnit chronoUnit;
		private boolean cycled;

		@Override
		public boolean hasNext() {
			return !cycled && current.isBefore(endTime);
		}

		@Override
		public LocalTime next() {
			LocalTime result = current;
			current = current.plus(interval, chronoUnit);
			// LocalTime wraps around midnight, so a fixed step may cycle back to the start forever
			if (current.equals(first)) {
				cycled = true;
			}
			return result;
		}
	}

}
