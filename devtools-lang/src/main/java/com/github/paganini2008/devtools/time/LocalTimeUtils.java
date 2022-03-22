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
import java.time.LocalDate;
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
 * LocalTimeUtils
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
public abstract class LocalTimeUtils {

	public static final LocalTime[] EMPTY_ARRAY = new LocalTime[0];
	private final static LruMap<String, DateTimeFormatter> dfCache = new LruMap<String, DateTimeFormatter>(16);

	public static LocalTime toLocalTime(Long ms, ZoneId zoneId) {
		return toLocalTime(ms, zoneId, null);
	}

	public static LocalTime toLocalTime(Long ms, ZoneId zoneId, LocalTime defaultValue) {
		if (ms == null) {
			return defaultValue;
		}
		return toLocalTime(Instant.ofEpochMilli(ms), zoneId, defaultValue);
	}

	public static LocalTime toLocalTime(Instant instant, ZoneId zoneId) {
		return toLocalTime(instant, zoneId, null);
	}

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

	public static LocalTime toLocalTime(Date date, ZoneId zoneId) {
		return toLocalTime(date, zoneId, null);
	}

	public static LocalTime toLocalTime(Date date, ZoneId zoneId, LocalTime defaultValue) {
		if (date == null) {
			return defaultValue;
		}
		return toLocalTime(date.toInstant(), zoneId, defaultValue);
	}

	public static LocalTime toLocalTime(Calendar calendar, ZoneId zoneId) {
		return toLocalTime(calendar, zoneId, null);
	}

	public static LocalTime toLocalTime(Calendar calendar, ZoneId zoneId, LocalTime defaultValue) {
		if (calendar == null) {
			return defaultValue;
		}
		return toLocalTime(calendar.toInstant(), zoneId, defaultValue);
	}

	public static LocalTime toLocalTime(LocalDateTime ldt, LocalTime defaultValue) {
		if (ldt == null) {
			return defaultValue;
		}
		return ldt.toLocalTime();
	}

	public static LocalTime parseLocalTime(String text) {
		return parseLocalTime(text, DateTimeFormatter.ISO_LOCAL_TIME);
	}

	public static LocalTime parseLocalTime(String text, DateTimeFormatter formatter) {
		return parseLocalTime(text, formatter, null);
	}

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

	public static LocalTime parseLocalTime(String text, String datePattern) {
		return parseLocalTime(text, datePattern, null);
	}

	public static LocalTime parseLocalTime(String text, String datePattern, LocalTime defaultValue) {
		return parseLocalTime(text, getDateTimeFormatter(datePattern), defaultValue);
	}

	public static LocalTime of(int hourOfDay, int minute) {
		return of(hourOfDay, minute, 0);
	}

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

	public static LocalTime copy(LocalTime lt, ZoneId zoneId) {
		if (zoneId == null) {
			zoneId = ZoneId.systemDefault();
		}
		if (lt == null) {
			return LocalTime.now(zoneId);
		}
		LocalDateTime ldt = LocalDateTime.of(LocalDate.now(zoneId), lt);
		return LocalDateTimeUtils.copy(ldt, zoneId).toLocalTime();
	}

	public static Iterator<LocalTime> toIterator(String startTime, String endTime, DateTimeFormatter dtf, ZoneId zoneId, int interval,
			ChronoUnit chronoUnit) {
		return new LocalTimeIterator(parseLocalTime(startTime, dtf), parseLocalTime(endTime, dtf), zoneId, interval, chronoUnit);
	}

	public static Iterator<LocalTime> toIterator(Date startTime, Date endTime, ZoneId zoneId, int interval, ChronoUnit chronoUnit) {
		return new LocalTimeIterator(toLocalTime(startTime, zoneId), toLocalTime(endTime, zoneId), zoneId, interval, chronoUnit);
	}

	public static Iterator<LocalTime> toIterator(LocalTime startTime, LocalTime endTime, ZoneId zoneId, int interval,
			ChronoUnit chronoUnit) {
		return new LocalTimeIterator(startTime, endTime, zoneId, interval, chronoUnit);
	}

	static class LocalTimeIterator implements Iterator<LocalTime> {

		LocalTimeIterator(LocalTime startTime, LocalTime endTime, ZoneId zoneId, int interval, ChronoUnit chronoUnit) {
			this.startTime = startTime;
			this.endTime = endTime;
			this.zoneId = zoneId;
			this.interval = interval;
			this.chronoUnit = chronoUnit;
		}

		private LocalTime startTime;
		private LocalTime endTime;
		private ZoneId zoneId;
		private int interval;
		private ChronoUnit chronoUnit;

		@Override
		public boolean hasNext() {
			return startTime.isBefore(endTime);
		}

		@Override
		public LocalTime next() {
			LocalTime copy = copy(startTime, zoneId);
			startTime = startTime.plus(interval, chronoUnit);
			return copy;
		}

	}

}
