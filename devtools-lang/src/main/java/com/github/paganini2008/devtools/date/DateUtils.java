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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.Console;
import com.github.paganini2008.devtools.collection.LruMap;

/**
 * DateUtils
 * 
 * @author Fred Feng
 * @since 2.0.1
 */
public abstract class DateUtils {

	public static final Date[] EMPTY_ARRAY = new Date[0];
	public final static String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public final static SimpleDateFormat DEFAULT_DATE_FORMATTER = new SimpleDateFormat(DEFAULT_DATE_PATTERN, Locale.ENGLISH);
	private final static LruMap<String, SimpleDateFormat> dateFormatterCache = new LruMap<String, SimpleDateFormat>(16);

	public static DateFormat getDateFormatter(String datePattern) {
		Assert.hasNoText(datePattern, "Date pattern can not be blank.");
		SimpleDateFormat sdf = dateFormatterCache.get(datePattern);
		if (sdf == null) {
			dateFormatterCache.put(datePattern, new SimpleDateFormat(datePattern, Locale.ENGLISH));
			sdf = dateFormatterCache.get(datePattern);
		}
		return sdf;
	}

	public static Date toDate(Long ms) {
		return toDate(ms, null);
	}

	public static Date toDate(Long ms, Date defaultValue) {
		return ms != null ? new Date(ms) : defaultValue;
	}

	public static Date toDate(Calendar calendar) {
		return toDate(calendar, null);
	}

	public static Date toDate(Calendar calendar, Date defaultValue) {
		return calendar != null ? calendar.getTime() : defaultValue;
	}

	public static Date toDate(LocalDate localDate, ZoneId zoneId) {
		return toDate(localDate, zoneId, null);
	}

	public static Date toDate(LocalDate localDate, ZoneId zoneId, Date defaultValue) {
		return localDate != null ? Date.from(localDate.atStartOfDay(zoneId).toInstant()) : defaultValue;
	}

	public static Date toDate(LocalDateTime localDateTime, ZoneId zoneId) {
		return toDate(localDateTime, zoneId, null);
	}

	public static Date toDate(LocalDateTime localDateTime, ZoneId zoneId, Date defaultValue) {
		return localDateTime != null ? Date.from(localDateTime.atZone(zoneId).toInstant()) : defaultValue;
	}

	public static Date[] toDateArray(long[] dates) {
		Date[] array = new Date[dates.length];
		int i = 0;
		for (long date : dates) {
			array[i++] = toDate(date);
		}
		return array;
	}

	public static Date[] toDateArray(Long[] mss) {
		return toDateArray(mss, null);
	}

	public static Date[] toDateArray(Long[] mss, Date defaultValue) {
		Date[] array = new Date[mss.length];
		int i = 0;
		for (Long ms : mss) {
			array[i++] = toDate(ms, defaultValue);
		}
		return array;
	}

	public static Date[] toDateArray(Calendar[] array) {
		return toDateArray(array, null);
	}

	public static Date[] toDateArray(Calendar[] array, Date defaultValue) {
		Date[] result = new Date[array.length];
		int i = 0;
		for (Calendar c : array) {
			result[i++] = toDate(c, defaultValue);
		}
		return result;
	}

	public static String format(Long ms, String datePattern) {
		return format(ms, datePattern, "");
	}

	public static String format(Long ms, String datePattern, String defaultValue) {
		return format(ms, getDateFormatter(datePattern), defaultValue);
	}

	public static String format(Long ms) {
		return format(ms, DEFAULT_DATE_FORMATTER);
	}

	public static String format(Long ms, DateFormat df) {
		return format(ms, df, "");
	}

	public static String format(Long ms, DateFormat df, String defaultValue) {
		if (ms == null) {
			return defaultValue;
		}
		Assert.isNull(df, "DateFormat can not be null.");
		synchronized (DateUtils.class) {
			try {
				return df.format(ms);
			} catch (RuntimeException e) {
				return defaultValue;
			}
		}
	}

	public static String format(Date date, String datePattern) {
		return format(date, datePattern, "");
	}

	public static String format(Date date, String datePattern, String defaultValue) {
		return format(date, getDateFormatter(datePattern), defaultValue);
	}

	public static String format(Date date) {
		return format(date, DEFAULT_DATE_FORMATTER);
	}

	public static String format(Date date, DateFormat df) {
		return format(date, df, "");
	}

	public static String format(Date date, DateFormat df, String defaultValue) {
		return format(date.getTime(), df, defaultValue);
	}

	public static String[] formatMany(Date[] dates) {
		return formatMany(dates, DEFAULT_DATE_FORMATTER);
	}

	public static String[] formatMany(Date[] dates, DateFormat df) {
		return formatMany(dates, df, "");
	}

	public static String[] formatMany(Date[] dates, DateFormat df, String defaultValue) {
		Assert.isNull(dates, "Date array can not be null.");
		String[] values = new String[dates.length];
		int i = 0;
		for (Date date : dates) {
			values[i++] = format(date, df, defaultValue);
		}
		return values;
	}

	public static String[] formatMany(Date[] dates, String datePattern) {
		return formatMany(dates, datePattern, "");
	}

	public static String[] formatMany(Date[] dates, String datePattern, String defaultValue) {
		return formatMany(dates, getDateFormatter(datePattern), defaultValue);
	}

	public static String reformat(String str, String srcFormat, String destFormat) {
		return reformat(str, srcFormat, destFormat, str);
	}

	public static String reformat(String str, String srcFormat, String destFormat, String defaultValue) {
		try {
			DateFormat leftSdf = getDateFormatter(srcFormat);
			Date date = leftSdf.parse(str);
			DateFormat rightSdf = getDateFormatter(destFormat);
			return rightSdf.format(date);
		} catch (ParseException e) {
			return defaultValue;
		}
	}

	public static Date parse(String str, String datePattern) {
		return parse(str, datePattern, null);
	}

	public static Date parse(String str, String datePattern, Date defaultValue) {
		DateFormat sdf = getDateFormatter(datePattern);
		synchronized (DateUtils.class) {
			try {
				return sdf.parse(str);
			} catch (ParseException e) {
				return defaultValue;
			}
		}
	}

	public static Date parse(String str, String[] datePatterns) {
		return parse(str, datePatterns, null);
	}

	public static Date parse(String str, String[] datePatterns, Date defaultValue) {
		Date date = null;
		for (String datePattern : datePatterns) {
			date = parse(str, datePattern, null);
			if (date != null) {
				return date;
			}
		}
		return defaultValue;
	}

	public static Date addYears(Date date, int amount) {
		return addField(date, Calendar.YEAR, amount);
	}

	public static Date addMonths(Date date, int amount) {
		return addField(date, Calendar.MONTH, amount);
	}

	public static Date addWeeks(Date date, int amount) {
		return addField(date, Calendar.WEEK_OF_YEAR, amount);
	}

	public static Date addDays(Date date, int amount) {
		return addField(date, Calendar.DAY_OF_MONTH, amount);
	}

	public static Date addHours(Date date, int amount) {
		return addField(date, Calendar.HOUR_OF_DAY, amount);
	}

	public static Date addMinutes(Date date, int amount) {
		return addField(date, Calendar.MINUTE, amount);
	}

	public static Date addSeconds(Date date, int amount) {
		return addField(date, Calendar.SECOND, amount);
	}

	public static Date addField(Date date, int calendarField, int amount) {
		if (date == null) {
			throw new IllegalArgumentException("The date must not be null.");
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(calendarField, amount);
		return c.getTime();
	}

	public static Date setYear(Date date, int amount) {
		return setField(date, Calendar.YEAR, amount);
	}

	public static Date setMonth(Date date, int amount) {
		return setField(date, Calendar.MONTH, amount);
	}

	public static Date setWeekOfYear(Date date, int amount) {
		return setField(date, Calendar.WEEK_OF_YEAR, amount);
	}

	public static Date setWeekOfMonth(Date date, int amount) {
		return setField(date, Calendar.WEEK_OF_MONTH, amount);
	}

	public static Date setDayOfWeek(Date date, int amount) {
		return setField(date, Calendar.DAY_OF_WEEK, amount);
	}

	public static Date setDay(Date date, int amount) {
		return setField(date, Calendar.DAY_OF_MONTH, amount);
	}

	public static Date setDayOfYear(Date date, int amount) {
		return setField(date, Calendar.DAY_OF_YEAR, amount);
	}

	public static Date setAM(Date date, int hour, int minute, int second) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.AM_PM, Calendar.AM);
		c.set(Calendar.HOUR, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);
		return c.getTime();
	}

	public static Date setPM(Date date, int hour, int minute, int second) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.AM_PM, Calendar.PM);
		c.set(Calendar.HOUR, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);
		return c.getTime();
	}

	public static Date setTime(Date date, int hourOfDay, int minute, int second) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, hourOfDay);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);
		return c.getTime();
	}

	private static Date setField(Date date, int calendarField, int amount) {
		if (date == null) {
			throw new IllegalArgumentException("The date must not be null");
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(calendarField, amount);
		return c.getTime();
	}

	public static Long getTimeInMillis(Date date) {
		return getTimeInMillis(date, null);
	}

	public static Long getTimeInMillis(Date date, Long defaultValue) {
		return date != null ? date.getTime() : defaultValue;
	}

	public static Long[] getTimeInMillis(Date[] dates) {
		return getTimeInMillis(dates, null);
	}

	public static Long[] getTimeInMillis(Date[] dates, Long defaultValue) {
		Assert.isNull(dates, "Date string array must not be null.");
		Long[] values = new Long[dates.length];
		int i = 0;
		for (Date date : dates) {
			values[i++] = getTimeInMillis(date, defaultValue);
		}
		return values;
	}

	public static Date valueOf(int year, int month, int date) {
		return valueOf(year, month, date, 0, 0, 0);
	}

	public static Date valueOf(int year, int month, int date, int hour, int minute, int second) {
		Calendar c = Calendar.getInstance();
		c.set(year, month - 1, date, hour, minute, second);
		return c.getTime();
	}

	public static Date valueOf(long time, int hour, int minute, int second) {
		Calendar c = CalendarUtils.valueOf(time, hour, minute, second);
		return c.getTime();
	}

	public static Date setTime(int hour, int minute, int second) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);
		return c.getTime();
	}

	public static Date[] parseMany(String[] strings, String[] datePatterns) {
		return parseMany(strings, datePatterns, null);
	}

	public static Date[] parseMany(String[] strings, String[] datePatterns, Date defaultValue) {
		Assert.isNull(strings, "String array can not be null.");
		Date[] result = new Date[strings.length];
		int i = 0;
		for (String str : strings) {
			result[i++] = parse(str, datePatterns, defaultValue);
		}
		return result;
	}

	public static int getYear() {
		return getYear(new Date());
	}

	public static int getYear(Date date) {
		return getField(date, Calendar.YEAR);
	}

	public static int getMonth() {
		return getMonth(new Date());
	}

	public static int getMonth(Date date) {
		return getField(date, Calendar.MONTH);
	}

	public static int getDate() {
		return getDate(new Date());
	}

	public static int getDate(Date date) {
		return getField(date, Calendar.DAY_OF_MONTH);
	}

	public static int getWeekOfMonth() {
		return getWeekOfMonth(new Date());
	}

	public static int getWeekOfMonth(Date date) {
		return getField(date, Calendar.WEEK_OF_MONTH);
	}

	public static int getHourOfDay() {
		return getHourOfDay(new Date());
	}

	public static int getHourOfDay(Date date) {
		return getField(date, Calendar.HOUR_OF_DAY);
	}

	public static int getMinute() {
		return getMinute(new Date());
	}

	public static int getMinute(Date date) {
		return getField(date, Calendar.MINUTE);
	}

	public static int getSecond() {
		return getSecond(new Date());
	}

	public static int getSecond(Date date) {
		return getField(date, Calendar.SECOND);
	}

	public static int getField(Date date, int calendarField) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(calendarField);
	}

	public static int getLastDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	public static long converToSecond(long interval, TimeUnit timeUnit) {
		if (interval < 0) {
			throw new IllegalArgumentException("interval < 0");
		}
		return timeUnit != TimeUnit.SECONDS ? TimeUnit.SECONDS.convert(interval, timeUnit) : interval;
	}

	public static long convertToMillis(long interval, TimeUnit timeUnit) {
		if (interval < 0) {
			throw new IllegalArgumentException("interval < 0");
		}
		return timeUnit != TimeUnit.MILLISECONDS ? TimeUnit.MILLISECONDS.convert(interval, timeUnit) : interval;
	}

	public static long convertToNanos(long interval, TimeUnit timeUnit) {
		if (interval < 0) {
			throw new IllegalArgumentException("interval < 0");
		}
		return timeUnit != TimeUnit.NANOSECONDS ? TimeUnit.NANOSECONDS.convert(interval, timeUnit) : interval;
	}

	public static <R> Map<Date, R> populate(Date from, int days, int interval, int calendarField, Function<Calendar, R> valueHandler) {
		return populate(from, addDays(from, days), interval, calendarField, valueHandler);
	}

	public static <R> Map<Date, R> populate(Date from, Date to, int interval, int calendarField, Function<Calendar, R> valueHandler) {
		return populate(from, to, interval, calendarField, c -> c.getTime(), valueHandler);
	}

	public static <T, R> Map<T, R> populate(Date from, int days, int interval, int calendarField, Function<Calendar, T> keyHandler,
			Function<Calendar, R> valueHandler) {
		return populate(from, addDays(from, days), interval, calendarField, keyHandler, valueHandler);
	}

	public static <T, R> Map<T, R> populate(Date from, Date to, int interval, int calendarField, Function<Calendar, T> keyHandler,
			Function<Calendar, R> valueHandler) {
		Map<T, R> data = new LinkedHashMap<T, R>();
		Iterator<Calendar> dateIterator = toIterator(from, to, interval, calendarField);
		Calendar calendar;
		while (dateIterator.hasNext()) {
			calendar = dateIterator.next();
			data.put(keyHandler.apply(calendar), valueHandler.apply(calendar));
		}
		return data;
	}

	public static Iterator<Calendar> toIterator(Date from, int days, int interval, int calendarField) {
		return new DateIterator(from, addDays(from, days), interval, calendarField);
	}

	public static Iterator<Calendar> toIterator(Date from, Date to, int interval, int calendarField) {
		return from.before(to) ? new DateIterator(from, to, interval, calendarField)
				: new ReverseDateIterator(from, to, interval, calendarField);
	}

	/**
	 * 
	 * DateIterator
	 * 
	 * @author Fred Feng
	 *
	 * @since 2.0.2
	 */
	static class DateIterator implements Iterator<Calendar> {

		private final Date to;
		private final Calendar calendar;
		private final int interval;
		private final int calendarField;

		DateIterator(Date from, Date to, int interval, int calendarField) {
			this.calendar = CalendarUtils.toCalendar(from, null);
			this.to = to;
			this.interval = interval;
			this.calendarField = calendarField;
		}

		@Override
		public boolean hasNext() {
			return calendar.getTime().compareTo(to) < 0;
		}

		@Override
		public Calendar next() {
			Calendar copy = (Calendar) calendar.clone();
			calendar.add(calendarField, interval);
			return copy;
		}

	}

	/**
	 * 
	 * ReverseDateIterator
	 * 
	 * @author Fred Feng
	 *
	 * @since 2.0.2
	 */
	static class ReverseDateIterator implements Iterator<Calendar> {

		private final Date to;
		private final Calendar calendar;
		private final int interval;
		private final int calendarField;

		ReverseDateIterator(Date from, Date to, int interval, int calendarField) {
			this.calendar = CalendarUtils.toCalendar(from, null);
			this.to = to;
			this.interval = interval;
			this.calendarField = calendarField;
		}

		@Override
		public boolean hasNext() {
			return calendar.getTime().compareTo(to) > 0;
		}

		@Override
		public Calendar next() {
			Calendar copy = (Calendar) calendar.clone();
			calendar.add(calendarField, -1 * interval);
			return copy;
		}

	}

	public static void main(String[] args) throws Exception {
		Map<Date, Object> result = populate(addDays(new Date(), 10), new Date(), 1, Calendar.DAY_OF_MONTH, c -> new HashMap<>());
		Console.log(new TreeMap<Date, Object>(result));
	}

}
