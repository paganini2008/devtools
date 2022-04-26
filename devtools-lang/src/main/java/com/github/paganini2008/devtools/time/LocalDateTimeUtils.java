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
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
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
 * LocalDateTimeUtils
 *
 * @author Fred Feng
 *
 * @since 2.0.4
 */
public abstract class LocalDateTimeUtils {

	public static final LocalDateTime[] EMPTY_ARRAY = new LocalDateTime[0];
	private final static LruMap<String, DateTimeFormatter> dfCache = new LruMap<String, DateTimeFormatter>(16);

	public static LocalDateTime addSeconds(Date date, int seconds) {
		return addSeconds(toLocalDateTime(date, null), seconds);
	}

	public static LocalDateTime addSeconds(int seconds) {
		return addSeconds(LocalDateTime.now(), seconds);
	}

	public static LocalDateTime addSeconds(LocalDateTime localDateTime, int seconds) {
		return addAmount(localDateTime, seconds, ChronoUnit.SECONDS);
	}

	public static LocalDateTime addMinutes(Date date, int minutes) {
		return addMinutes(toLocalDateTime(date, null), minutes);
	}

	public static LocalDateTime addMinutes(int minutes) {
		return addMinutes(LocalDateTime.now(), minutes);
	}

	public static LocalDateTime addMinutes(LocalDateTime localDateTime, int minutes) {
		return addAmount(localDateTime, minutes, ChronoUnit.MINUTES);
	}

	public static LocalDateTime addHours(Date date, int hours) {
		return addHours(toLocalDateTime(date, null), hours);
	}

	public static LocalDateTime addHours(int hours) {
		return addHours(LocalDateTime.now(), hours);
	}

	public static LocalDateTime addHours(LocalDateTime localDateTime, int hours) {
		return addAmount(localDateTime, hours, ChronoUnit.HOURS);
	}

	public static LocalDateTime addDays(Date date, int days) {
		return addDays(toLocalDateTime(date, null), days);
	}

	public static LocalDateTime addDays(int days) {
		return addDays(LocalDateTime.now(), days);
	}

	public static LocalDateTime addDays(LocalDateTime localDateTime, int days) {
		return addAmount(localDateTime, days, ChronoUnit.DAYS);
	}

	public static LocalDateTime addMonths(Date date, int months) {
		return addMonths(toLocalDateTime(date, null), months);
	}

	public static LocalDateTime addMonths(int months) {
		return addMonths(LocalDateTime.now(), months);
	}

	public static LocalDateTime addMonths(LocalDateTime localDateTime, int months) {
		return addAmount(localDateTime, months, ChronoUnit.MONTHS);
	}

	public static LocalDateTime addYears(Date date, int years) {
		return addDays(toLocalDateTime(date, null), years);
	}

	public static LocalDateTime addYears(int years) {
		return addYears(LocalDateTime.now(), years);
	}

	public static LocalDateTime addYears(LocalDateTime localDateTime, int years) {
		return addAmount(localDateTime, years, ChronoUnit.YEARS);
	}

	public static LocalDateTime addWeeks(Date date, int weeks) {
		return addWeeks(toLocalDateTime(date, null), weeks);
	}

	public static LocalDateTime addWeeks(int weeks) {
		return addWeeks(LocalDateTime.now(), weeks);
	}

	public static LocalDateTime addWeeks(LocalDateTime localDateTime, int weeks) {
		return addAmount(localDateTime, weeks, ChronoUnit.WEEKS);
	}

	public static LocalDateTime addAmount(LocalDateTime localDateTime, int amount, ChronoUnit chronoUnit) {
		if (localDateTime == null) {
			localDateTime = LocalDateTime.now();
		}
		return localDateTime.plus(amount, chronoUnit);
	}

	public static int getYear(LocalDateTime localDateTime) {
		return getField(localDateTime, ChronoField.YEAR);
	}

	public static int getMonth(LocalDateTime localDateTime) {
		return getField(localDateTime, ChronoField.MONTH_OF_YEAR);
	}

	public static int getDayOfMonth(LocalDateTime localDateTime) {
		return getField(localDateTime, ChronoField.DAY_OF_MONTH);
	}

	public static int getDayOfWeek(LocalDateTime localDateTime) {
		return getField(localDateTime, ChronoField.DAY_OF_WEEK);
	}

	public static int getDayOfYear(LocalDateTime localDateTime) {
		return getField(localDateTime, ChronoField.DAY_OF_YEAR);
	}

	public static int getHourOfDay(LocalDateTime localDateTime) {
		return getField(localDateTime, ChronoField.HOUR_OF_DAY);
	}

	public static int getMinute(LocalDateTime localDateTime) {
		return getField(localDateTime, ChronoField.MINUTE_OF_HOUR);
	}

	public static int getSecond(LocalDateTime localDateTime) {
		return getField(localDateTime, ChronoField.SECOND_OF_MINUTE);
	}

	public static int getField(LocalDateTime localDateTime, ChronoField field) {
		if (localDateTime == null) {
			localDateTime = LocalDateTime.now();
		}
		return localDateTime.get(field);
	}

	public static LocalDateTime setYear(int year) {
		return setYear(LocalDateTime.now(), year);
	}

	public static LocalDateTime setYear(LocalDateTime localDateTime, int year) {
		return setField(localDateTime, ChronoField.YEAR, year);
	}

	public static LocalDateTime setMonth(int month) {
		return setMonth(LocalDateTime.now(), month);
	}

	public static LocalDateTime setMonth(LocalDateTime localDateTime, int month) {
		return setField(localDateTime, ChronoField.MONTH_OF_YEAR, month);
	}

	public static LocalDateTime setDayOfMonth(int dayOfMonth) {
		return setDayOfMonth(LocalDateTime.now(), dayOfMonth);
	}

	public static LocalDateTime setDayOfMonth(LocalDateTime localDateTime, int dayOfMonth) {
		return setField(localDateTime, ChronoField.DAY_OF_MONTH, dayOfMonth);
	}

	public static LocalDateTime setDayOfWeek(int dayOfWeek) {
		return setDayOfWeek(LocalDateTime.now(), dayOfWeek);
	}

	public static LocalDateTime setDayOfWeek(LocalDateTime localDateTime, int dayOfWeek) {
		return setField(localDateTime, ChronoField.DAY_OF_WEEK, dayOfWeek);
	}

	public static LocalDateTime setDayOfYear(int dayOfYear) {
		return setDayOfYear(LocalDateTime.now(), dayOfYear);
	}

	public static LocalDateTime setDayOfYear(LocalDateTime localDateTime, int dayOfYear) {
		return setField(localDateTime, ChronoField.DAY_OF_YEAR, dayOfYear);
	}

	public static LocalDateTime setHourOfDay(int hourOfDay) {
		return setHourOfDay(LocalDateTime.now(), hourOfDay);
	}

	public static LocalDateTime setHourOfDay(LocalDateTime localDateTime, int hourOfDay) {
		return setField(localDateTime, ChronoField.HOUR_OF_DAY, hourOfDay);
	}

	public static LocalDateTime setMinute(int minute) {
		return setMinute(LocalDateTime.now(), minute);
	}

	public static LocalDateTime setMinute(LocalDateTime localDateTime, int minute) {
		return setField(localDateTime, ChronoField.MINUTE_OF_HOUR, minute);
	}

	public static LocalDateTime setSecond(int second) {
		return setSecond(LocalDateTime.now(), second);
	}

	public static LocalDateTime setSecond(LocalDateTime localDateTime, int second) {
		return setField(localDateTime, ChronoField.SECOND_OF_MINUTE, second);
	}

	public static LocalDateTime setField(LocalDateTime localDateTime, ChronoField field, int value) {
		if (localDateTime == null) {
			localDateTime = LocalDateTime.now();
		}
		return localDateTime.with(field, value);
	}

	public static LocalDateTime toLocalDateTime(Long ms, ZoneId zoneId) {
		return toLocalDateTime(ms, zoneId, null);
	}

	public static LocalDateTime toLocalDateTime(Long ms, ZoneId zoneId, LocalDateTime defaultValue) {
		if (ms == null) {
			return defaultValue;
		}
		return toLocalDateTime(Instant.ofEpochMilli(ms), zoneId, defaultValue);
	}

	public static LocalDateTime toLocalDateTime(Instant instant, ZoneId zoneId) {
		return toLocalDateTime(instant, zoneId, null);
	}

	public static LocalDateTime toLocalDateTime(Instant instant, ZoneId zoneId, LocalDateTime defaultValue) {
		if (zoneId == null) {
			zoneId = ZoneId.systemDefault();
		}
		try {
			return instant != null ? instant.atZone(zoneId).toLocalDateTime() : defaultValue;
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	public static LocalDateTime toLocalDateTime(Date date, ZoneId zoneId) {
		return toLocalDateTime(date, zoneId, null);
	}

	public static LocalDateTime toLocalDateTime(Date date, ZoneId zoneId, LocalDateTime defaultValue) {
		if (date == null) {
			return defaultValue;
		}
		return toLocalDateTime(date.toInstant(), zoneId, defaultValue);
	}

	public static LocalDateTime toLocalDateTime(Calendar calendar, ZoneId zoneId) {
		return toLocalDateTime(calendar, zoneId, null);
	}

	public static LocalDateTime toLocalDateTime(Calendar calendar, ZoneId zoneId, LocalDateTime defaultValue) {
		if (calendar == null) {
			return defaultValue;
		}
		return toLocalDateTime(calendar.toInstant(), zoneId, defaultValue);
	}

	public static LocalDateTime toLocalDateTime(LocalDate ld) {
		return toLocalDateTime(ld, null);
	}

	public static LocalDateTime toLocalDateTime(LocalDate ld, LocalDateTime defaultValue) {
		if (ld == null) {
			return defaultValue;
		}
		try {
			return ld.atTime(0, 0, 0);
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	public static String format(LocalDateTime localDateTime, String datePattern) {
		return format(localDateTime, datePattern, "");
	}

	public static String format(LocalDateTime localDateTime, String datePattern, String defaultValue) {
		return localDateTime != null ? localDateTime.format(getDateTimeFormatter(datePattern)) : defaultValue;
	}

	public static String format(LocalDateTime localDateTime) {
		return format(localDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}

	public static String format(LocalDateTime localDateTime, DateTimeFormatter dateTimeFormatter) {
		return format(localDateTime, dateTimeFormatter, "");
	}

	public static String format(LocalDateTime localDateTime, DateTimeFormatter dateTimeFormatter, String defaultValue) {
		return localDateTime != null ? localDateTime.format(dateTimeFormatter) : defaultValue;
	}

	public static LocalDateTime parseLocalDateTime(String text) {
		return parseLocalDateTime(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}

	public static LocalDateTime parseLocalDateTime(String text, DateTimeFormatter formatter) {
		return parseLocalDateTime(text, formatter, null);
	}

	public static LocalDateTime parseLocalDateTime(String text, DateTimeFormatter formatter, LocalDateTime defaultValue) {
		if (formatter == null) {
			formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
		}
		try {
			return StringUtils.isNotBlank(text) ? LocalDateTime.parse(text, formatter) : defaultValue;
		} catch (DateTimeParseException e) {
			return defaultValue;
		}
	}

	public static LocalDateTime parseLocalDateTime(String text, String datePattern) {
		return parseLocalDateTime(text, datePattern, null);
	}

	public static LocalDateTime parseLocalDateTime(String text, String datePattern, LocalDateTime defaultValue) {
		return parseLocalDateTime(text, getDateTimeFormatter(datePattern), defaultValue);
	}

	public static LocalDateTime of(YearMonth yearMonth, int dayOfMonth, int hourOfDay, int minute, int second) {
		if (yearMonth == null) {
			yearMonth = YearMonth.now();
		}
		TimeAssert.validateTime(hourOfDay, minute, second);
		return yearMonth.atDay(dayOfMonth).atTime(hourOfDay, minute, second);
	}

	public static LocalDateTime of(Year year, Month month, int dayOfMonth, int hourOfDay, int minute, int second) {
		YearMonth yearMonth = YearMonthUtils.toYearMonth(year, month);
		return of(yearMonth, dayOfMonth, hourOfDay, minute, second);
	}

	public static LocalDateTime of(Date date, int hourOfDay, int minute, int second) {
		LocalDate localDate = LocalDateUtils.toLocalDate(date, null);
		return of(localDate, hourOfDay, minute, second);
	}

	public static LocalDateTime of(LocalDate localDate, int hourOfDay, int minute, int second) {
		if (localDate == null) {
			localDate = LocalDate.now();
		}
		TimeAssert.validateTime(hourOfDay, minute, second);
		return localDate.atTime(hourOfDay, minute, second);
	}

	public static LocalDateTime of(int year, int month, int dayOfMonth) {
		return of(year, month, dayOfMonth, 0, 0, 0);
	}

	public static LocalDateTime of(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
		TimeAssert.validateYear(year);
		TimeAssert.validateMonth(month);
		TimeAssert.validateDayOfMonth(year, month, dayOfMonth);
		TimeAssert.validateTime(hourOfDay, minute, second);
		return LocalDateTime.of(year, month, dayOfMonth, hourOfDay, minute, second);
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

	public static LocalDateTime copy(LocalDateTime ldt, ZoneId zoneId) {
		if (zoneId == null) {
			zoneId = ZoneId.systemDefault();
		}
		if (ldt == null) {
			return LocalDateTime.now(zoneId);
		}
		Instant ins = InstantUtils.toInstant(ldt, zoneId);
		return LocalDateTime.ofInstant(ins, zoneId);
	}

	public static long until(LocalDateTime endTime) {
		return until(null, endTime);
	}

	public static long until(LocalDateTime startTime, LocalDateTime endTime) {
		Assert.isNull(endTime, "EndTime must not be null");
		if (startTime == null) {
			startTime = LocalDateTime.now();
		}
		return startTime.until(endTime, ChronoUnit.MILLIS);
	}

	public static Iterator<LocalDateTime> toIterator(String startTime, String endTime, DateTimeFormatter dtf, ZoneId zoneId, int interval,
			ChronoUnit chronoUnit) {
		return new LocalDateTimeIterator(parseLocalDateTime(startTime, dtf), parseLocalDateTime(endTime, dtf), zoneId, interval,
				chronoUnit);
	}

	public static Iterator<LocalDateTime> toIterator(Date startTime, Date endTime, ZoneId zoneId, int interval, ChronoUnit chronoUnit) {
		return new LocalDateTimeIterator(toLocalDateTime(startTime, zoneId), toLocalDateTime(endTime, zoneId), zoneId, interval,
				chronoUnit);
	}

	public static Iterator<LocalDateTime> toIterator(LocalDateTime startTime, LocalDateTime endTime, ZoneId zoneId, int interval,
			ChronoUnit chronoUnit) {
		return new LocalDateTimeIterator(startTime, endTime, zoneId, interval, chronoUnit);
	}

	static class LocalDateTimeIterator implements Iterator<LocalDateTime> {

		LocalDateTimeIterator(LocalDateTime startTime, LocalDateTime endTime, ZoneId zoneId, int interval, ChronoUnit chronoUnit) {
			this.startTime = startTime;
			this.endTime = endTime;
			this.zoneId = zoneId;
			this.interval = interval;
			this.chronoUnit = chronoUnit;
		}

		private LocalDateTime startTime;
		private LocalDateTime endTime;
		private ZoneId zoneId;
		private int interval;
		private ChronoUnit chronoUnit;

		@Override
		public boolean hasNext() {
			return startTime.isBefore(endTime);
		}

		@Override
		public LocalDateTime next() {
			LocalDateTime copy = copy(startTime, zoneId);
			startTime = startTime.plus(interval, chronoUnit);
			return copy;
		}

	}

}
