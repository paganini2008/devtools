package com.github.paganini2008.devtools.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.Longs;
import com.github.paganini2008.devtools.StringUtils;
import com.github.paganini2008.devtools.collection.LruMap;

/**
 * DateUtils
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class DateUtils {

	private DateUtils() {
	}

	public static final Date[] EMPTY_ARRAY = new Date[0];

	public final static SimpleDateFormat DEFAULT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private final static LruMap<String, SimpleDateFormat> dateFormatCache = new LruMap<String, SimpleDateFormat>(16);

	private static SimpleDateFormat getDateFormat(String pattern) {
		SimpleDateFormat sdf = dateFormatCache.get(pattern);
		if (sdf == null) {
			dateFormatCache.put(pattern, new SimpleDateFormat(pattern, Locale.ENGLISH));
			sdf = dateFormatCache.get(pattern);
		}
		return sdf;
	}

	public static Date toDate(Long ms) {
		return toDate(ms, null);
	}

	public static Date toDate(Long ms, Date defaultValue) {
		return ms != null ? new Date(ms) : defaultValue;
	}

	public static Date[] toDates(long[] dates) {
		Date[] array = new Date[dates.length];
		int i = 0;
		for (long date : dates) {
			array[i++] = toDate(date);
		}
		return array;
	}

	public static Date[] toDates(Long[] mss) {
		return toDates(mss, null);
	}

	public static Date[] toDates(Long[] mss, Date defaultValue) {
		Date[] array = new Date[mss.length];
		int i = 0;
		for (Long ms : mss) {
			array[i++] = toDate(ms, defaultValue);
		}
		return array;
	}

	public static Date valueOf(Calendar c) {
		return valueOf(c, null);
	}

	public static Date valueOf(Calendar c, Date defaultValue) {
		return c != null ? c.getTime() : defaultValue;
	}

	public static Date[] valuesOf(Calendar[] cs) {
		return valuesOf(cs, null);
	}

	public static Date[] valuesOf(Calendar[] cs, Date defaultValue) {
		Date[] array = new Date[cs.length];
		int i = 0;
		for (Calendar c : cs) {
			array[i++] = valueOf(c, defaultValue);
		}
		return array;
	}

	public static String format(Long ms) {
		return format(ms, DEFAULT_FORMAT);
	}

	public static String format(Long ms, String pattern) {
		return format(ms, pattern, "");
	}

	public static String format(Long ms, String pattern, String defaultValue) {
		return format(ms, getDateFormat(pattern), defaultValue);
	}

	public static String format(Long ms, DateFormat df, String defaultValue) {
		if (ms == null) {
			return defaultValue;
		}
		synchronized (DateUtils.class) {
			return df != null ? df.format(ms) : DEFAULT_FORMAT.format(ms);
		}
	}

	public static String format(Long ms, DateFormat df) {
		return format(ms, df, "");
	}

	public static String format(Date date, String pattern) {
		return format(date, pattern, "");
	}

	public static String format(Date date, String pattern, String defaultValue) {
		return format(date, getDateFormat(pattern), defaultValue);
	}

	public static String format(Date date) {
		return format(date, DEFAULT_FORMAT);
	}

	public static String format(Date date, DateFormat df) {
		return format(date, df, "");
	}

	public static String format(Date date, DateFormat df, String defaultValue) {
		return format(date.getTime(), df, defaultValue);
	}

	public static String[] format(Date[] dates, DateFormat df) {
		return format(dates, df, "");
	}

	public static String[] format(Date[] dates, DateFormat df, String defaultValue) {
		Assert.isNull(dates, "Date string array must not be null.");
		String[] values = new String[dates.length];
		int i = 0;
		for (Date date : dates) {
			values[i++] = format(date, df, defaultValue);
		}
		return values;
	}

	public static String reformat(String str, String srcFormat, String destFormat) {
		return reformat(str, srcFormat, destFormat, str);
	}

	public static String reformat(String str, String srcFormat, String destFormat, String defaultValue) {
		try {
			SimpleDateFormat leftSdf = getDateFormat(srcFormat);
			Date date = leftSdf.parse(str);
			SimpleDateFormat rightSdf = getDateFormat(destFormat);
			return rightSdf.format(date);
		} catch (ParseException e) {
			return defaultValue;
		}
	}

	public static Date parse(String str, String pattern) {
		return parse(str, pattern, null);
	}

	public static Date parse(String str, String pattern, Date defaultValue) {
		SimpleDateFormat sdf = getDateFormat(pattern);
		synchronized (DateUtils.class) {
			try {
				return sdf.parse(str);
			} catch (ParseException e) {
				return defaultValue;
			}
		}
	}

	public static Date parse(String str, String[] patterns) {
		return parse(str, patterns, null);
	}

	public static Date parse(String str, String[] patterns, Date defaultValue) {
		Date date = null;
		for (String pattern : patterns) {
			date = parse(str, pattern, null);
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

	private static Date addField(Date date, int calendarField, int amount) {
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
		if (date == null) {
			return defaultValue;
		}
		return date.getTime();
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

	public static String formatDurationAsMinute(long ms) {
		return formatDurationAsMinute(ms, "#m:#s:#ms");
	}

	public static String formatDurationAsMinute(long ms, String pattern) {
		return formatDuration(ms, DurationType.MINUTE, pattern);
	}

	public static String formatDurationAsHour(long ms) {
		return formatDurationAsHour(ms, "#H:#m:#s:#ms");
	}

	public static String formatDurationAsHour(long ms, String pattern) {
		return formatDuration(ms, DurationType.HOUR, pattern);
	}

	public static String formatDurationAsDay(long ms) {
		return formatDurationAsDay(ms, "#D:#H:#m:#s:#ms");
	}

	public static String formatDurationAsDay(long ms, String pattern) {
		return formatDuration(ms, DurationType.DAY, pattern);
	}

	public static void main(String[] args) throws Exception {
		System.out.println(formatDurationAsDay(1234567781L));
	}

	public static String formatDuration(long ms, DurationType type, String pattern) {
		long[] args = type.toArray(ms);
		Long[] array = Longs.toWrappers(args);
		return StringUtils.parseText(pattern, "#", array);
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
		Calendar c = getCalendar(time, hour, minute, second);
		return c.getTime();
	}

	public static Calendar getCalendar(long time, int hour, int minute, int second) {
		Calendar c = getCalendar(time);
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);
		return c;
	}

	public static Calendar getCalendar(long time) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		return c;
	}

	public static Date setTime(int hour, int minute, int second) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, second);
		return c.getTime();
	}

	public static Date valueOf(Long ms) {
		return valueOf(ms, null);
	}

	public static Date valueOf(Long ms, Date defaultValue) {
		if (ms == null) {
			return defaultValue;
		}
		return new Date(ms);
	}

	public static Date[] parses(String[] strs, String[] patterns) {
		return parses(strs, patterns, true);
	}

	public static Date[] parses(String[] strs, String[] patterns, boolean thrown) {
		Date[] result = new Date[strs.length];
		int i = 0;
		for (String str : strs) {
			try {
				result[i++] = parse(str, patterns);
			} catch (RuntimeException e) {
				if (thrown) {
					throw e;
				}
			}
		}
		return ArrayUtils.ensureCapacity(result, i);
	}

}
