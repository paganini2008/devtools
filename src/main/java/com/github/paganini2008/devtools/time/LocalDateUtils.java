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
 * Utility methods for {@link LocalDate}, null safe where possible.
 *
 * @author Fred Feng
 * @date 2020-02-20
 * @since 2.0.1
 */
public abstract class LocalDateUtils {

	public static final LocalDate[] EMPTY_ARRAY = new LocalDate[0];
	private final static LruMap<String, DateTimeFormatter> dfCache = new LruMap<String, DateTimeFormatter>(16);

	/**
	 * Converts the date in the system default time zone and adds the amount, null means today.
	 */
	public static LocalDate addYears(Date date, int years) {
		return addYears(toLocalDate(date, null), years);
	}

	/**
	 * Adds the amount to today.
	 */
	public static LocalDate addYears(int years) {
		return addYears(LocalDate.now(), years);
	}

	/**
	 * Adds the amount to a local date, null means today.
	 *
	 * <pre>
	 * LocalDateUtils.addMonths(LocalDate.of(2021, 1, 31), 1) = 2021-02-28
	 * </pre>
	 */
	public static LocalDate addYears(LocalDate localDate, int years) {
		return addAmount(localDate, years, ChronoUnit.YEARS);
	}

	/**
	 * Converts the date in the system default time zone and adds the amount, null means today.
	 */
	public static LocalDate addMonths(Date date, int months) {
		return addMonths(toLocalDate(date, null), months);
	}

	/**
	 * Adds the amount to today.
	 */
	public static LocalDate addMonths(int months) {
		return addMonths(LocalDate.now(), months);
	}

	/**
	 * Adds the amount to a local date, null means today.
	 *
	 * <pre>
	 * LocalDateUtils.addMonths(LocalDate.of(2021, 1, 31), 1) = 2021-02-28
	 * </pre>
	 */
	public static LocalDate addMonths(LocalDate localDate, int months) {
		return addAmount(localDate, months, ChronoUnit.MONTHS);
	}

	/**
	 * Converts the date in the system default time zone and adds the amount, null means today.
	 */
	public static LocalDate addDays(Date date, int days) {
		return addDays(toLocalDate(date, null), days);
	}

	/**
	 * Adds the amount to today.
	 */
	public static LocalDate addDays(int days) {
		return addDays(LocalDate.now(), days);
	}

	/**
	 * Adds the amount to a local date, null means today.
	 *
	 * <pre>
	 * LocalDateUtils.addMonths(LocalDate.of(2021, 1, 31), 1) = 2021-02-28
	 * </pre>
	 */
	public static LocalDate addDays(LocalDate localDate, int days) {
		return addAmount(localDate, days, ChronoUnit.DAYS);
	}

	/**
	 * Converts the date in the system default time zone and adds the amount, null means today.
	 */
	public static LocalDate addWeeks(Date date, int weeks) {
		return addWeeks(toLocalDate(date, null), weeks);
	}

	/**
	 * Adds the amount to today.
	 */
	public static LocalDate addWeeks(int weeks) {
		return addWeeks(LocalDate.now(), weeks);
	}

	/**
	 * Adds the amount to a local date, null means today.
	 *
	 * <pre>
	 * LocalDateUtils.addMonths(LocalDate.of(2021, 1, 31), 1) = 2021-02-28
	 * </pre>
	 */
	public static LocalDate addWeeks(LocalDate localDate, int weeks) {
		return addAmount(localDate, weeks, ChronoUnit.WEEKS);
	}

	/**
	 * Adds an amount of the unit to a local date, null means today.
	 */
	public static LocalDate addAmount(LocalDate localDate, int amount, ChronoUnit chronoUnit) {
		if (localDate == null) {
			localDate = LocalDate.now();
		}
		return localDate.plus(amount, chronoUnit);
	}

	/**
	 * Sets the field of today.
	 */
	public static LocalDate setYear(int year) {
		return setYear(LocalDate.now(), year);
	}

	/**
	 * Converts the date in the system default time zone and sets the field, null means today.
	 */
	public static LocalDate setYear(Date date, int year) {
		return setYear(toLocalDate(date, null), year);
	}

	/**
	 * Sets the field of a local date, null means today.
	 */
	public static LocalDate setYear(LocalDate localDate, int year) {
		return setField(localDate, ChronoField.YEAR, year);
	}

	/**
	 * Sets the field of today.
	 */
	public static LocalDate setMonth(int month) {
		return setMonth(LocalDate.now(), month);
	}

	/**
	 * Converts the date in the system default time zone and sets the field, null means today.
	 */
	public static LocalDate setMonth(Date date, int month) {
		return setMonth(toLocalDate(date, null), month);
	}

	/**
	 * Sets the field of a local date, null means today.
	 */
	public static LocalDate setMonth(LocalDate localDate, int month) {
		return setField(localDate, ChronoField.MONTH_OF_YEAR, month);
	}

	/**
	 * Sets the field of today.
	 */
	public static LocalDate setDayOfMonth(int dayOfMonth) {
		return setDayOfMonth(LocalDate.now(), dayOfMonth);
	}

	/**
	 * Converts the date in the system default time zone and sets the field, null means today.
	 */
	public static LocalDate setDayOfMonth(Date date, int dayOfMonth) {
		return setDayOfMonth(toLocalDate(date, null), dayOfMonth);
	}

	/**
	 * Sets the field of a local date, null means today.
	 */
	public static LocalDate setDayOfMonth(LocalDate localDate, int dayOfMonth) {
		return setField(localDate, ChronoField.DAY_OF_MONTH, dayOfMonth);
	}

	/**
	 * Sets the field of today.
	 */
	public static LocalDate setDayOfWeek(int dayOfWeek) {
		return setDayOfWeek(LocalDate.now(), dayOfWeek);
	}

	/**
	 * Converts the date in the system default time zone and sets the field, null means today.
	 */
	public static LocalDate setDayOfWeek(Date date, int dayOfWeek) {
		return setDayOfWeek(toLocalDate(date, null), dayOfWeek);
	}

	/**
	 * Sets the ISO day of week (1 = Monday) within the same week, null means today.
	 */
	public static LocalDate setDayOfWeek(LocalDate localDate, int dayOfWeek) {
		return setField(localDate, ChronoField.DAY_OF_WEEK, dayOfWeek);
	}

	/**
	 * Sets the field of today.
	 */
	public static LocalDate setDayOfYear(int dayOfYear) {
		return setDayOfYear(LocalDate.now(), dayOfYear);
	}

	/**
	 * Converts the date in the system default time zone and sets the field, null means today.
	 */
	public static LocalDate setDayOfYear(Date date, int dayOfYear) {
		return setDayOfYear(toLocalDate(date, null), dayOfYear);
	}

	/**
	 * Sets the field of a local date, null means today.
	 */
	public static LocalDate setDayOfYear(LocalDate localDate, int dayOfYear) {
		return setField(localDate, ChronoField.DAY_OF_YEAR, dayOfYear);
	}

	/**
	 * Sets the field of a local date, null means today.
	 */
	public static LocalDate setField(LocalDate localDate, ChronoField field, int value) {
		if (localDate == null) {
			localDate = LocalDate.now();
		}
		return localDate.with(field, value);
	}

	/**
	 * Converts milliseconds to a {@code LocalDate} in the zone, null returns null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static LocalDate toLocalDate(Long ms, ZoneId zoneId) {
		return toLocalDate(ms, zoneId, null);
	}

	/**
	 * Converts milliseconds to a {@code LocalDate} in the zone, returning the default value for null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static LocalDate toLocalDate(Long ms, ZoneId zoneId, LocalDate defaultValue) {
		if (ms == null) {
			return defaultValue;
		}
		return toLocalDate(Instant.ofEpochMilli(ms), zoneId, defaultValue);
	}

	/**
	 * Converts an instant to a {@code LocalDate} in the zone, null returns null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static LocalDate toLocalDate(Instant instant, ZoneId zoneId) {
		return toLocalDate(instant, zoneId, null);
	}

	/**
	 * Converts an instant to a {@code LocalDate} in the zone, returning the default value for null
	 * or if the result is out of the supported range.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static LocalDate toLocalDate(Instant instant, ZoneId zoneId, LocalDate defaultValue) {
		if (zoneId == null) {
			zoneId = ZoneId.systemDefault();
		}
		try {
			return instant != null ? instant.atZone(zoneId).toLocalDate() : defaultValue;
		} catch (RuntimeException e) {
			return defaultValue;
		}
	}

	/**
	 * Converts a date to a {@code LocalDate} in the zone, null returns null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static LocalDate toLocalDate(Date date, ZoneId zoneId) {
		return toLocalDate(date, zoneId, null);
	}

	/**
	 * Converts a date to a {@code LocalDate} in the zone, returning the default value for null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static LocalDate toLocalDate(Date date, ZoneId zoneId, LocalDate defaultValue) {
		if (date == null) {
			return defaultValue;
		}
		return toLocalDate(date.toInstant(), zoneId, defaultValue);
	}

	/**
	 * Converts a calendar to a {@code LocalDate} in the zone, null returns null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static LocalDate toLocalDate(Calendar calendar, ZoneId zoneId) {
		return toLocalDate(calendar, zoneId, null);
	}

	/**
	 * Converts a calendar to a {@code LocalDate} in the zone, returning the default value for null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static LocalDate toLocalDate(Calendar calendar, ZoneId zoneId, LocalDate defaultValue) {
		if (calendar == null) {
			return defaultValue;
		}
		return toLocalDate(calendar.toInstant(), zoneId, defaultValue);
	}

	/**
	 * Returns the date part of a local date time, returning the default value for null.
	 */
	public static LocalDate toLocalDate(LocalDateTime ldt, LocalDate defaultValue) {
		if (ldt == null) {
			return defaultValue;
		}
		return ldt.toLocalDate();
	}

	/**
	 * Formats a local date with the ISO format, null returns "".
	 *
	 * <pre>
	 * LocalDateUtils.format(LocalDate.of(2021, 1, 31)) = "2021-01-31"
	 * LocalDateUtils.format(null)                      = ""
	 * </pre>
	 */
	public static String format(LocalDate localDate) {
		return format(localDate, DateTimeFormatter.ISO_LOCAL_DATE);
	}

	/**
	 * Formats a local date with the formatter, null returns "".
	 */
	public static String format(LocalDate localDate, DateTimeFormatter dateTimeFormatter) {
		return format(localDate, dateTimeFormatter, "");
	}

	/**
	 * Formats a local date with the formatter, returning the default value for null.
	 */
	public static String format(LocalDate localDate, DateTimeFormatter dateTimeFormatter, String defaultValue) {
		return localDate != null ? localDate.format(dateTimeFormatter) : defaultValue;
	}

	/**
	 * Formats a local date with the pattern (English locale), null returns "".
	 *
	 * @throws IllegalArgumentException if the date is not null and the pattern is blank
	 */
	public static String format(LocalDate localDate, String datePattern) {
		return format(localDate, datePattern, "");
	}

	/**
	 * Formats a local date with the pattern (English locale), returning the default value for null.
	 */
	public static String format(LocalDate localDate, String datePattern, String defaultValue) {
		return localDate != null ? localDate.format(getDateTimeFormatter(datePattern)) : defaultValue;
	}

	/**
	 * Parses a string with the ISO format, returning null if it is blank or can not be parsed.
	 */
	public static LocalDate parseLocalDate(String text) {
		return parseLocalDate(text, DateTimeFormatter.ISO_LOCAL_DATE);
	}

	/**
	 * Parses a string with the formatter (null means ISO), returning null if it is blank or can not be parsed.
	 */
	public static LocalDate parseLocalDate(String text, DateTimeFormatter formatter) {
		return parseLocalDate(text, formatter, null);
	}

	/**
	 * Parses a string with the formatter (null means ISO), returning the default value if it is blank or can not be parsed.
	 */
	public static LocalDate parseLocalDate(String text, DateTimeFormatter formatter, LocalDate defaultValue) {
		if (formatter == null) {
			formatter = DateTimeFormatter.ISO_LOCAL_DATE;
		}
		try {
			return StringUtils.isNotBlank(text) ? LocalDate.parse(text, formatter) : defaultValue;
		} catch (DateTimeParseException e) {
			return defaultValue;
		}
	}

	/**
	 * Parses a string with the pattern (English locale), returning null if it is blank or can not be parsed.
	 *
	 * @throws IllegalArgumentException if the pattern is blank
	 */
	public static LocalDate parseLocalDate(String text, String datePattern) {
		return parseLocalDate(text, datePattern, null);
	}

	/**
	 * Parses a string with the pattern (English locale), returning the default value if it is blank or can not be parsed.
	 *
	 * @throws IllegalArgumentException if the pattern is blank
	 */
	public static LocalDate parseLocalDate(String text, String datePattern, LocalDate defaultValue) {
		return parseLocalDate(text, getDateTimeFormatter(datePattern), defaultValue);
	}

	/**
	 * Obtains a local date from a year and a day of year.
	 *
	 * @throws IllegalArgumentException if the day is out of range
	 */
	public static LocalDate of(Year year, int dayOfYear) {
		TimeAssert.validateDayOfYear(year, dayOfYear);
		return year.atDay(dayOfYear);
	}

	/**
	 * Obtains a local date from a year-month and a day of month.
	 *
	 * @throws IllegalArgumentException if the day is out of range
	 */
	public static LocalDate of(YearMonth yearMonth, int dayOfMonth) {
		TimeAssert.validateDayOfMonth(yearMonth, dayOfMonth);
		return yearMonth.atDay(dayOfMonth);
	}

	/**
	 * Obtains a local date from a year, a month and a day of month.
	 *
	 * @throws IllegalArgumentException if any value is null or out of range
	 */
	public static LocalDate of(Year year, Month month, int dayOfMonth) {
		YearMonth yearMonth = YearMonthUtils.toYearMonth(year, month);
		return of(yearMonth, dayOfMonth);
	}

	/**
	 * Obtains a local date after validating the year, the month and the day, e.g. {@code of(1965, 7, 1)}.
	 *
	 * @throws IllegalArgumentException if any value is out of range
	 */
	public static LocalDate of(int year, int month, int dayOfMonth) {
		TimeAssert.validateYear(year);
		TimeAssert.validateMonth(month);
		TimeAssert.validateDayOfMonth(year, month, dayOfMonth);
		return LocalDate.of(year, month, dayOfMonth);
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
	 * Returns the given value, which is immutable, or the current date of the zone if it is null.
	 *
	 * @param zoneId the time zone, null means the system default
	 */
	public static LocalDate copy(LocalDate ld, ZoneId zoneId) {
		if (zoneId == null) {
			zoneId = ZoneId.systemDefault();
		}
		// LocalDate is immutable, so it is safe to return the same instance
		return ld != null ? ld : LocalDate.now(zoneId);
	}

	/**
	 * Iterates from the parsed start (inclusive) to the parsed end (exclusive) by the interval.
	 *
	 * @param dtf the formatter, null means ISO
	 * @throws IllegalArgumentException if the start is before the end and the interval is not positive
	 */
	public static Iterator<LocalDate> toIterator(String startTime, String endTime, DateTimeFormatter dtf, ZoneId zoneId, int interval,
			ChronoUnit chronoUnit) {
		return new LocalDateIterator(parseLocalDate(startTime, dtf), parseLocalDate(endTime, dtf), zoneId, interval, chronoUnit);
	}

	/**
	 * Iterates from the start (inclusive) to the end (exclusive) by the interval, dates are converted in the zone.
	 *
	 * @throws IllegalArgumentException if the start is before the end and the interval is not positive
	 */
	public static Iterator<LocalDate> toIterator(Date startTime, Date endTime, ZoneId zoneId, int interval, ChronoUnit chronoUnit) {
		return new LocalDateIterator(toLocalDate(startTime, zoneId), toLocalDate(endTime, zoneId), zoneId, interval, chronoUnit);
	}

	/**
	 * Iterates from the start (inclusive) to the end (exclusive) by the interval.
	 *
	 * @param zoneId not used, kept for compatibility
	 * @throws IllegalArgumentException if the start is before the end and the interval is not positive
	 */
	public static Iterator<LocalDate> toIterator(LocalDate startTime, LocalDate endTime, ZoneId zoneId, int interval,
			ChronoUnit chronoUnit) {
		return new LocalDateIterator(startTime, endTime, zoneId, interval, chronoUnit);
	}

	static class LocalDateIterator implements Iterator<LocalDate> {

		LocalDateIterator(LocalDate startTime, LocalDate endTime, ZoneId zoneId, int interval, ChronoUnit chronoUnit) {
			if (interval <= 0 && startTime != null && endTime != null && startTime.isBefore(endTime)) {
				throw new IllegalArgumentException("Interval must be positive, otherwise the iteration never ends.");
			}
			this.current = startTime;
			this.endTime = endTime;
			this.interval = interval;
			this.chronoUnit = chronoUnit;
		}

		private LocalDate current;
		private final LocalDate endTime;
		private final int interval;
		private final ChronoUnit chronoUnit;

		@Override
		public boolean hasNext() {
			return current.isBefore(endTime);
		}

		@Override
		public LocalDate next() {
			LocalDate result = current;
			current = current.plus(interval, chronoUnit);
			return result;
		}
	}

}
