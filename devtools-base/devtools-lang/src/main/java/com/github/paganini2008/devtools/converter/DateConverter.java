package com.github.paganini2008.devtools.converter;

import java.util.Calendar;
import java.util.Date;

import com.github.paganini2008.devtools.date.DateUtils;

/**
 * DateConverter
 * 
 * @author Fred Feng
 * @version 1.0
 */
public class DateConverter extends BaseConverter<Date> {

	private final Converter<Long, Date> longConverter = new Converter<Long, Date>() {
		public Date getValue(Long source, Date defaultValue) {
			return DateUtils.valueOf(source, defaultValue);
		}
	};

	private final Converter<String, Date> stringConverter = new Converter<String, Date>() {
		public Date getValue(String source, Date defaultValue) {
			return DateUtils.parse(source, config.getDatePatterns(), defaultValue);
		}
	};

	private final Converter<Calendar, Date> calendarConverter = new Converter<Calendar, Date>() {
		public Date getValue(Calendar source, Date defaultValue) {
			return DateUtils.valueOf(source, defaultValue);
		}
	};

	private final Converter<int[], Date> arrayConverter = new Converter<int[], Date>() {
		public Date getValue(int[] source, Date defaultValue) {
			if (source == null) {
				return defaultValue;
			}
			if (source.length == 3) {
				return DateUtils.valueOf(source[0], source[1], source[2]);
			} else if (source.length == 6) {
				return DateUtils.valueOf(source[0], source[1], source[2], source[3], source[4], source[5]);
			}
			throw new IllegalArgumentException("Please define the array's length to 3 or 6.");
		}
	};

	public DateConverter() {
		put(Long.class, longConverter);
		put(String.class, stringConverter);
		put(Calendar.class, calendarConverter);
		put(int[].class, arrayConverter);
	}

}
