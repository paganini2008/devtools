package com.github.paganini2008.devtools.date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.github.paganini2008.devtools.ArrayUtils;
import com.github.paganini2008.devtools.Assert;
import com.github.paganini2008.devtools.StringUtils;

/**
 * CalendarUtils
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class CalendarUtils {

	private CalendarUtils() {
	}

	public static final Calendar[] EMPTY_ARRAY = new Calendar[0];

	public static String[] format(Calendar[] dates, String pattern) {
		return format(dates, new SimpleDateFormat(pattern));
	}

	public static String[] format(Calendar[] dates, DateFormat df) {
		return format(dates, df, null);
	}

	public static String[] format(Calendar[] dates, DateFormat df, String defaultValue) {
		Assert.isNull(dates, "Date array must not be null.");
		String[] values = new String[dates.length];
		int i = 0;
		for (Calendar date : dates) {
			values[i++] = format(date, df, defaultValue);
		}
		return values;
	}

	public static String format(Calendar c, String pattern) {
		return format(c, pattern, "");
	}

	public static String format(Calendar c, String pattern, String defaultValue) {
		if (c == null) {
			return defaultValue;
		}
		return DateUtils.format(c.getTime(), pattern, defaultValue);
	}

	public static String format(Calendar cal) {
		return format(cal, DateUtils.DEFAULT_FORMAT);
	}

	public static String format(Calendar c, DateFormat df) {
		return format(c, df, null);
	}

	public static String format(Calendar c, DateFormat df, String defaultValue) {
		if (c == null) {
			return defaultValue;
		}
		return DateUtils.format(c.getTime(), df, defaultValue);
	}

	public static Calendar parse(String date, String[] patterns) {
		return parse(date, patterns, null);
	}

	public static Calendar parse(String date, String[] patterns, Calendar defaultValue) {
		if (StringUtils.isBlank(date)) {
			return defaultValue;
		}
		Date value = DateUtils.parse(date, patterns);
		return valueOf(value, defaultValue);
	}

	public static Calendar[] parses(String[] dates, String[] patterns) {
		return parses(dates, patterns, null);
	}

	public static Calendar[] parses(String[] dates, String[] patterns, Calendar defaultValue) {
		Calendar[] array = new Calendar[dates.length];
		int j = 0;
		for (String date : dates) {
			array[j++] = parse(date, patterns, defaultValue);
		}
		return array;
	}

	public static Calendar[] valuesOf(long[] dates) {
		Calendar[] array = new Calendar[dates.length];
		int i = 0;
		for (long date : dates) {
			array[i++] = valueOf(date);
		}
		return array;
	}

	public static Calendar valueOf(Long ms) {
		return valueOf(ms, null);
	}

	public static Calendar valueOf(Long ms, Calendar defaultValue) {
		if (ms == null) {
			return defaultValue;
		}
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(ms);
		return c;
	}

	public static Calendar valueOf(Date date) {
		return valueOf(date, null);
	}

	public static Calendar valueOf(Date date, Calendar defaultValue) {
		if (date == null) {
			return defaultValue;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c;
	}

	public static Calendar[] valuesOf(Date[] dates) {
		return valuesOf(dates, null);
	}

	public static Calendar[] valuesOf(Date[] dates, Calendar defaultValue) {
		Calendar[] array = new Calendar[dates.length];
		int j = 0;
		for (Date date : dates) {
			if (date != null) {
				array[j++] = valueOf(date, defaultValue);
			}
		}
		return ArrayUtils.ensureCapacity(array, j);
	}

	public static Calendar[] valuesOf(Long[] dates) {
		return valuesOf(dates, null);
	}

	public static Calendar[] valuesOf(Long[] dates, Calendar defaultValue) {
		Calendar[] array = new Calendar[dates.length];
		int j = 0;
		for (Long date : dates) {
			if (date != null) {
				array[j++] = valueOf(date, defaultValue);
			}
		}
		return ArrayUtils.ensureCapacity(array, j);
	}

	public static Long getTimeInMillis(Calendar date) {
		return getTimeInMillis(date, null);
	}

	public static Long getTimeInMillis(Calendar date, Long defaultValue) {
		if (date == null) {
			return defaultValue;
		}
		return date.getTimeInMillis();
	}

	public static Long[] getTimeInMillis(Calendar[] dates) {
		return getTimeInMillis(dates, null);
	}

	public static Long[] getTimeInMillis(Calendar[] dates, Long defaultValue) {
		Assert.isNull(dates, "Date string array must not be null.");
		Long[] values = new Long[dates.length];
		int j = 0;
		for (Calendar date : dates) {
			if (date != null) {
				values[j++] = getTimeInMillis(date, defaultValue);
			}
		}
		return ArrayUtils.ensureCapacity(values, j);
	}

	public static Calendar valueOf(int year, int month, int date) {
		return valueOf(year, month, date, 0, 0, 0);
	}

	public static Calendar valueOf(int year, int month, int date, int hour, int minute, int second) {
		Calendar c = Calendar.getInstance();
		c.set(year, month - 1, date, hour, minute, second);
		return c;
	}

	

}
