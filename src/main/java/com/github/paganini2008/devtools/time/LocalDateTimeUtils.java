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
 * Utility methods for {@link LocalDateTime}, null safe where possible.
 *
 * @author Fred Feng
 * @date 2021-11-16
 *
 * @since 2.0.4
 */
public abstract class LocalDateTimeUtils {

	public static final LocalDateTime[] EMPTY_ARRAY = new LocalDateTime[0];
	private final static LruMap<String, DateTimeFormatter> dfCache = new LruMap<String, DateTimeFormatter>(16);

	/**
	 * Converts the date in the system default time zone and adds the amount, null means now.
	 */
	public static LocalDateTime addSeconds(Date date, int seconds) {
		return addSeconds(toLocalDateTime(date, null), seconds);
	}

	/**
	 * Adds the amount to now.
	 */
	public static LocalDateTime addSeconds(int seconds) {
		return addSeconds(LocalDateTime.now(), seconds);
	}

	/**
	 * Adds the amount to a local date time, null means now.
	 */
	public static LocalDateTime addSeconds(LocalDateTime localDateTime, int seconds) {
		return addAmount(localDateTime, seconds, ChronoUnit.SECONDS);
	}

	/**
	 * Converts the date in the system default time zone and adds the amount, null means now.
	 */
	public static LocalDateTime addMinutes(Date date, int minutes) {
		return addMinutes(toLocalDateTime(date, null), minutes);
	}

	/**
	 * Adds the amount to now.
	 */
	public static LocalDateTime addMinutes(int minutes) {
		return addMinutes(LocalDateTime.now(), minutes);
	}

	/**
	 * Adds the amount to a local date time, null means now.
	 */
	public static LocalDateTime addMinutes(LocalDateTime localDateTime, int minutes) {
		return addAmount(localDateTime, minutes, ChronoUnit.MINUTES);
	}

	/**
	 * Converts the date in the system default time zone and adds the amount, null means now.
	 */
	public static LocalDateTime addHours(Date date, int hours) {
		return addHours(toLocalDateTime(date, null), hours);
	}

	/**
	 * Adds the amount to now.
	 */
	public static LocalDateTime addHours(int hours) {
		return addHours(LocalDateTime.now(), hours);
	}

	/**
	 * Adds the amount to a local date time, null means now.
	 */
	public static LocalDateTime addHours(LocalDateTime localDateTime, int hours) {
		return addAmount(localDateTime, hours, ChronoUnit.HOURS);
	}

	/**
	 * Converts the date in the system default time zone and adds the amount, null means now.
	 */
	public static LocalDateTime addDays(Date date, int days) {
		return addDays(toLocalDateTime(date, null), days);
	}

	/**
	 * Adds the amount to now.
	 */
	public static LocalDateTime addDays(int days) {
		return addDays(LocalDateTime.now(), days);
	}

	/**
	 * Adds the amount to a local date time, null means now.
	 */
	public static LocalDateTime addDays(LocalDateTime localDateTime, int days) {
		return addAmount(localDateTime, days, ChronoUnit.DAYS);
	}

	/**
	 * Converts the date in the system default time zone and adds the amount, null means now.
	 */
	public static LocalDateTime addMonths(Date date, int months) {
		return addMonths(toLocalDateTime(date, null), months);
	}

	/**
	 * Adds the amount to now.
	 */
	public static LocalDateTime addMonths(int months) {
		return addMonths(LocalDateTime.now(), months);
	}

	/**
	 * Adds the amount to a local date time, null means now.
	 */
	public static LocalDateTime addMonths(LocalDateTime localDateTime, int months) {
		return addAmount(localDateTime, months, ChronoUnit.MONTHS);
	}

	/**
	 * Converts the date in the system default time zone and adds the amount, null means now.
	 */
	public static LocalDateTime addYears(Date date, int years) {
		return addYears(toLocalDateTime(date, null), years);
	}

	/**
	 * Adds the amount to now.
	 */
	public static LocalDateTime addYears(int years) {
		return addYears(LocalDateTime.now(), years);
	}

	/**
	 * Adds the amount to a local date time, null means now.
	 */
	public static LocalDateTime addYears(LocalDateTime localDateTime, int years) {
		return addAmount(localDateTime, years, ChronoUnit.YEARS);
	}

	/**
	 * Converts the date in the system default time zone and adds the amount, null means now.
	 */
	public static LocalDateTime addWeeks(Date date, int weeks) {
		return addWeeks(toLocalDateTime(date, null), weeks);
	}

	/**
	 * Adds the amount to now.
	 */
	public static LocalDateTime addWeeks(int weeks) {
		return addWeeks(LocalDateTime.now(), weeks);
	}

	/**
	 * Adds the amount to a local date time, null means now.
	 */
	public static LocalDateTime addWeeks(LocalDateTime localDateTime, int weeks) {
		return addAmount(localDateTime, weeks, ChronoUnit.WEEKS);
	}

	/**
	 * Adds the amount to a local date time, null means now.
	 */
	public static LocalDateTime addAmount(LocalDateTime localDateTime, int amount, ChronoUnit chronoUnit) {
		if (localDateTime == null) {
			localDateTime = LocalDateTime.now();
		}
		return localDateTime.plus(amount, chronoUnit);
	}

	/**
	 * Returns the field of a local date time, null means now.
	 */
	public static int getYear(LocalDateTime localDateTime) {
		return getField(localDateTime, ChronoField.YEAR);
	}

	/**
	 * Returns the field of a local date time, null means now.
	 */
	public static int getMonth(LocalDateTime localDateTime) {
		return getField(localDateTime, ChronoField.MONTH_OF_YEAR);
	}

	/**
	 * Returns the field of a local date time, null means now.
	 */
	public static int getDayOfMonth(LocalDateTime localDateTime) {
		return getField(localDateTime, ChronoField.DAY_OF_MONTH);
	}

	/**
	 * Returns the field of a local date time, null means now.
	 */
	public static int getDayOfWeek(LocalDateTime localDateTime) {
		return getField(localDateTime, ChronoField.DAY_OF_WEEK);
	}

	/**
	 * Returns the field of a local date time, null means now.
	 */
	public static int getDayOfYear(LocalDateTime localDateTime) {
		return getField(localDateTime, ChronoField.DAY_OF_YEAR);
	}

	/**
	 * Returns the field of a local date time, null means now.
	 */
	public static int getHourOfDay(LocalDateTime localDateTime) {
		return getField(localDateTime, ChronoField.HOUR_OF_DAY);
	}

	/**
	 * Returns the field of a local date time, null means now.
	 */
	public static int getMinute(LocalDateTime localDateTime) {
		return getField(localDateTime, ChronoField.MINUTE_OF_HOUR);
	}

	/**
	 * Returns the field of a local date time, null means now.
	 */
	public static int getSecond(LocalDateTime localDateTime) {
		return getField(localDateTime, ChronoField.SECOND_OF_MINUTE);
	}

	/**
	 * Returns the field of a local date time, null means now.
	 */
	public static int getField(LocalDateTime localDateTime, ChronoField field) {
		if (localDateTime == null) {
			localDateTime = LocalDateTime.now();
		}
		return localDateTime.get(field);
	}

	/**
	 * Sets the field of now.
	 */
	public static LocalDateTime setYear(int year) {
		return setYear(LocalDateTime.now(), year);
	}

	/**
	 * Sets the field of a local date time, null means now.
	 */
	public static LocalDateTime setYear(LocalDateTime localDateTime, int year) {
		return setField(localDateTime, ChronoField.YEAR, year);
	}

	/**
	 * Sets the field of now.
	 */
	public static LocalDateTime setMonth(int month) {
		return setMonth(LocalDateTime.now(), month);
	}

	/**
	 * Sets the field of a local date time, null means now.
	 */
	public static LocalDateTime setMonth(LocalDateTime localDateTime, int month) {
		return setField(localDateTime, ChronoField.MONTH_OF_YEAR, month);
	}

	/**
	 * Sets the field of now.
	 */
	public static LocalDateTime setDayOfMonth(int dayOfMonth) {
		return setDayOfMonth(LocalDateTime.now(), dayOfMonth);
	}

	/**
	 * Sets the field of a local date time, null means now.
	 */
	public static LocalDateTime setDayOfMonth(LocalDateTime localDateTime, int dayOfMonth) {
		return setField(localDateTime, ChronoField.DAY_OF_MONTH, dayOfMonth);
	}

	/**
	 * Sets the field of now.
	 */
	public static LocalDateTime setDayOfWeek(int dayOfWeek) {
		return setDayOfWeek(LocalDateTime.now(), dayOfWeek);
	}

	/**
	 * Sets the ISO day of week (1 = Monday) within the same week, null means now.
	 */
	public static LocalDateTime setDayOfWeek(LocalDateTime localDateTime, int dayOfWeek) {
		return setField(localDateTime, ChronoField.DAY_OF_WEEK, dayOfWeek);
	}

	/**
	 * Sets the field of now.
	 */
	public static LocalDateTime setDayOfYear(int dayOfYear) {
		return setDayOfYear(LocalDateTime.now(), dayOfYear);
	}

	/**
	 * Sets the field of a local date time, null means now.
	 */
	public static LocalDateTime setDayOfYear(LocalDateTime localDateTime, int dayOfYear) {
		return setField(localDateTime, ChronoField.DAY_OF_YEAR, dayOfYear);
	}

	/**
	 * Sets the field of now.
	 */
	public static LocalDateTime setHourOfDay(int hourOfDay) {
		return setHourOfDay(LocalDateTime.now(), hourOfDay);
	}

	/**
	 * Sets the field of a local date time, null means now.
	 */
	public static LocalDateTime setHourOfDay(LocalDateTime localDateTime, int hourOfDay) {
		return setField(localDateTime, ChronoField.HOUR_OF_DAY, hourOfDay);
	}

	/**
	 * Sets the field of now.
	 */
	public static LocalDateTime setMinute(int minute) {
		return setMinute(LocalDateTime.now(), minute);
	}

	/**
	 * Sets the field of a local date time, null means now.
	 */
	public static LocalDateTime setMinute(LocalDateTime localDateTime, int minute) {
		return setField(localDateTime, ChronoField.MINUTE_OF_HOUR, minute);
	}

	/**
	 * Sets the field of now.
	 */
	public static LocalDateTime setSecond(int second) {
		return setSecond(LocalDateTime.now(), second);
	}

	/**
	 * Sets the field of a local date time, null means now.
	 */
	public static LocalDateTime setSecond(LocalDateTime localDateTime, int second) {
		return setField(localDateTime, ChronoField.SECOND_OF_MINUTE, second);
	}

	/**
	 * Sets the field of a local date time, null means now.
	 */
	public static LocalDateTime setField(LocalDateTime localDateTime, ChronoField field, int value) {
		if (localDateTime == null) {
			localDateTime = LocalDateTime.now();
		}
		return localDateTime.with(field, value);
	}

	/**
	 * Converts milliseconds to a {@code LocalDateTime} in the zone, null returns null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static LocalDateTime toLocalDateTime(Long ms, ZoneId zoneId) {
		return toLocalDateTime(ms, zoneId, null);
	}

	/**
	 * Converts milliseconds to a {@code LocalDateTime} in the zone, returning the default value for null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static LocalDateTime toLocalDateTime(Long ms, ZoneId zoneId, LocalDateTime defaultValue) {
		if (ms == null) {
			return defaultValue;
		}
		return toLocalDateTime(Instant.ofEpochMilli(ms), zoneId, defaultValue);
	}

	/**
	 * Converts an instant to a {@code LocalDateTime} in the zone, null returns null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static LocalDateTime toLocalDateTime(Instant instant, ZoneId zoneId) {
		return toLocalDateTime(instant, zoneId, null);
	}

	/**
	 * Converts an instant to a {@code LocalDateTime} in the zone, returning the default value for null
	 * or if the result is out of the supported range.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
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

	/**
	 * Converts a date to a {@code LocalDateTime} in the zone, null returns null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static LocalDateTime toLocalDateTime(Date date, ZoneId zoneId) {
		return toLocalDateTime(date, zoneId, null);
	}

	/**
	 * Converts a date to a {@code LocalDateTime} in the zone, returning the default value for null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static LocalDateTime toLocalDateTime(Date date, ZoneId zoneId, LocalDateTime defaultValue) {
		if (date == null) {
			return defaultValue;
		}
		return toLocalDateTime(date.toInstant(), zoneId, defaultValue);
	}

	/**
	 * Converts a calendar to a {@code LocalDateTime} in the zone, null returns null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static LocalDateTime toLocalDateTime(Calendar calendar, ZoneId zoneId) {
		return toLocalDateTime(calendar, zoneId, null);
	}

	/**
	 * Converts a calendar to a {@code LocalDateTime} in the zone, returning the default value for null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static LocalDateTime toLocalDateTime(Calendar calendar, ZoneId zoneId, LocalDateTime defaultValue) {
		if (calendar == null) {
			return defaultValue;
		}
		return toLocalDateTime(calendar.toInstant(), zoneId, defaultValue);
	}

	/**
	 * Returns the start of a local date, null returns null.
	 */
	public static LocalDateTime toLocalDateTime(LocalDate ld) {
		return toLocalDateTime(ld, null);
	}

	/**
	 * Returns the start of a local date, returning the default value for null.
	 */
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

	/**
	 * Formats a local date time with the pattern (English locale), null returns "".
	 */
	public static String format(LocalDateTime localDateTime, String datePattern) {
		return format(localDateTime, datePattern, "");
	}

	/**
	 * Formats a local date time with the pattern (English locale), returning the default value for null.
	 */
	public static String format(LocalDateTime localDateTime, String datePattern, String defaultValue) {
		return localDateTime != null ? localDateTime.format(getDateTimeFormatter(datePattern)) : defaultValue;
	}

	/**
	 * Formats a local date time with the ISO format, null returns "".
	 *
	 * <pre>
	 * LocalDateTimeUtils.format(LocalDateTime.of(2021, 1, 31, 10, 20, 30)) = "2021-01-31T10:20:30"
	 * LocalDateTimeUtils.format(null)                                    = ""
	 * </pre>
	 */
	public static String format(LocalDateTime localDateTime) {
		return format(localDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}

	/**
	 * Formats a local date time with the formatter, null returns "".
	 */
	public static String format(LocalDateTime localDateTime, DateTimeFormatter dateTimeFormatter) {
		return format(localDateTime, dateTimeFormatter, "");
	}

	/**
	 * Formats a local date time with the formatter, returning the default value for null.
	 */
	public static String format(LocalDateTime localDateTime, DateTimeFormatter dateTimeFormatter, String defaultValue) {
		return localDateTime != null ? localDateTime.format(dateTimeFormatter) : defaultValue;
	}

	/**
	 * Parses a string with the ISO format, returning null if it is blank or can not be parsed.
	 */
	public static LocalDateTime parseLocalDateTime(String text) {
		return parseLocalDateTime(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}

	/**
	 * Parses a string with the formatter (null means ISO), returning null if it is blank or can not be parsed.
	 */
	public static LocalDateTime parseLocalDateTime(String text, DateTimeFormatter formatter) {
		return parseLocalDateTime(text, formatter, null);
	}

	/**
	 * Parses a string with the formatter (null means ISO), returning the default value if it is blank or can not be parsed.
	 */
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

	/**
	 * Parses a string with the pattern (English locale), returning null if it is blank or can not be parsed.
	 *
	 * @throws IllegalArgumentException if the pattern is blank
	 */
	public static LocalDateTime parseLocalDateTime(String text, String datePattern) {
		return parseLocalDateTime(text, datePattern, null);
	}

	/**
	 * Parses a string with the pattern (English locale), returning the default value if it is blank or can not be parsed.
	 *
	 * @throws IllegalArgumentException if the pattern is blank
	 */
	public static LocalDateTime parseLocalDateTime(String text, String datePattern, LocalDateTime defaultValue) {
		return parseLocalDateTime(text, getDateTimeFormatter(datePattern), defaultValue);
	}

	/**
	 * Obtains a local date time from a year-month (null means the current one), a day and a time.
	 *
	 * @throws IllegalArgumentException if any value is out of range
	 */
	public static LocalDateTime of(YearMonth yearMonth, int dayOfMonth, int hourOfDay, int minute, int second) {
		if (yearMonth == null) {
			yearMonth = YearMonth.now();
		}
		TimeAssert.validateDayOfMonth(yearMonth, dayOfMonth);
		TimeAssert.validateTime(hourOfDay, minute, second);
		return yearMonth.atDay(dayOfMonth).atTime(hourOfDay, minute, second);
	}

	/**
	 * Obtains a local date time from a year, a month, a day and a time.
	 *
	 * @throws IllegalArgumentException if any value is null or out of range
	 */
	public static LocalDateTime of(Year year, Month month, int dayOfMonth, int hourOfDay, int minute, int second) {
		YearMonth yearMonth = YearMonthUtils.toYearMonth(year, month);
		return of(yearMonth, dayOfMonth, hourOfDay, minute, second);
	}

	/**
	 * Obtains a local date time from the date part of a date (system default time zone, null means today) and a time.
	 *
	 * @throws IllegalArgumentException if the time is out of range
	 */
	public static LocalDateTime of(Date date, int hourOfDay, int minute, int second) {
		LocalDate localDate = LocalDateUtils.toLocalDate(date, null);
		return of(localDate, hourOfDay, minute, second);
	}

	/**
	 * Obtains a local date time from a local date (null means today) and a time.
	 *
	 * @throws IllegalArgumentException if the time is out of range
	 */
	public static LocalDateTime of(LocalDate localDate, int hourOfDay, int minute, int second) {
		if (localDate == null) {
			localDate = LocalDate.now();
		}
		TimeAssert.validateTime(hourOfDay, minute, second);
		return localDate.atTime(hourOfDay, minute, second);
	}

	/**
	 * Obtains the start of the given day.
	 *
	 * @throws IllegalArgumentException if any value is out of range
	 */
	public static LocalDateTime of(int year, int month, int dayOfMonth) {
		return of(year, month, dayOfMonth, 0, 0, 0);
	}

	/**
	 * Obtains a local date time after validating every field, e.g. {@code of(1900, 2, 28, 10, 20, 30)}.
	 *
	 * @throws IllegalArgumentException if any value is out of range
	 */
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

	/**
	 * Returns the given value, which is immutable, or the current date time of the zone if it is null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static LocalDateTime copy(LocalDateTime ldt, ZoneId zoneId) {
		if (zoneId == null) {
			zoneId = ZoneId.systemDefault();
		}
		// LocalDateTime is immutable, so it is safe to return the same instance
		return ldt != null ? ldt : LocalDateTime.now(zoneId);
	}

	/**
	 * Returns the milliseconds from now to the end time.
	 *
	 * @throws IllegalArgumentException if the end time is null
	 */
	public static long until(LocalDateTime endTime) {
		return until(null, endTime);
	}

	/**
	 * Returns the milliseconds from the start time (null means now) to the end time.
	 *
	 * @throws IllegalArgumentException if the end time is null
	 */
	public static long until(LocalDateTime startTime, LocalDateTime endTime) {
		Assert.isNull(endTime, "EndTime must not be null");
		if (startTime == null) {
			startTime = LocalDateTime.now();
		}
		return startTime.until(endTime, ChronoUnit.MILLIS);
	}

	/**
	 * Iterates from the parsed start (inclusive) to the parsed end (exclusive) by the interval.
	 *
	 * @param dtf the formatter, null means ISO
	 * @throws IllegalArgumentException if the start is before the end and the interval is not positive
	 */
	public static Iterator<LocalDateTime> toIterator(String startTime, String endTime, DateTimeFormatter dtf, ZoneId zoneId, int interval,
			ChronoUnit chronoUnit) {
		return new LocalDateTimeIterator(parseLocalDateTime(startTime, dtf), parseLocalDateTime(endTime, dtf), zoneId, interval,
				chronoUnit);
	}

	/**
	 * Iterates from the start (inclusive) to the end (exclusive) by the interval, dates are converted in the zone.
	 *
	 * @throws IllegalArgumentException if the start is before the end and the interval is not positive
	 */
	public static Iterator<LocalDateTime> toIterator(Date startTime, Date endTime, ZoneId zoneId, int interval, ChronoUnit chronoUnit) {
		return new LocalDateTimeIterator(toLocalDateTime(startTime, zoneId), toLocalDateTime(endTime, zoneId), zoneId, interval,
				chronoUnit);
	}

	/**
	 * Iterates from the start (inclusive) to the end (exclusive) by the interval.
	 *
	 * @param zoneId not used, kept for compatibility
	 * @throws IllegalArgumentException if the start is before the end and the interval is not positive
	 */
	public static Iterator<LocalDateTime> toIterator(LocalDateTime startTime, LocalDateTime endTime, ZoneId zoneId, int interval,
			ChronoUnit chronoUnit) {
		return new LocalDateTimeIterator(startTime, endTime, zoneId, interval, chronoUnit);
	}

	static class LocalDateTimeIterator implements Iterator<LocalDateTime> {

		LocalDateTimeIterator(LocalDateTime startTime, LocalDateTime endTime, ZoneId zoneId, int interval, ChronoUnit chronoUnit) {
			if (interval <= 0 && startTime != null && endTime != null && startTime.isBefore(endTime)) {
				throw new IllegalArgumentException("Interval must be positive, otherwise the iteration never ends.");
			}
			this.current = startTime;
			this.endTime = endTime;
			this.interval = interval;
			this.chronoUnit = chronoUnit;
		}

		private LocalDateTime current;
		private final LocalDateTime endTime;
		private final int interval;
		private final ChronoUnit chronoUnit;

		@Override
		public boolean hasNext() {
			return current.isBefore(endTime);
		}

		@Override
		public LocalDateTime next() {
			LocalDateTime result = current;
			current = current.plus(interval, chronoUnit);
			return result;
		}
	}

}
